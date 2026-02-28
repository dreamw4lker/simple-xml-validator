package com.github.dreamw4lker.simplexvalfx.service;

import com.github.dreamw4lker.simplexvalfx.enums.ProtocolType;
import com.github.dreamw4lker.simplexvalfx.utils.TextAreaProgressMonitor;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class CDAFetcherService {
    private final TextArea logField;

    private final List<String> OUTDATED_PROTOCOLS = List.of("LAB_V4", "CITOL_V1");

    public CDAFetcherService(TextArea logField) {
        this.logField = logField;
    }

    private void appendLog(String text, Object... params) {
        if (!ObjectUtils.isEmpty(text)) {
            if (!text.endsWith("\n")) {
                text += "\n";
            }
        }

        String finalText = text;
        Platform.runLater(() -> {
            logField.appendText(String.format(finalText, params));
        });
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Загружает все протоколы согласно конфигурации
     *
     * @param username имя пользователя для аутентификации в Git
     * @param password пароль или токен для аутентификации в Git
     */
    public void downloadAllProtocols(String username, String password) {
        //Создаём папку для хранения протоколов
        try {
            Path targetDir = Paths.get(".", "protocols");
            // Если папка уже существует - копируем всё из неё в папку с постфиксом текущей даты/времени
            if (targetDir.toFile().exists() && targetDir.toFile().isDirectory()) {
                Path outdatedDirectory =
                        Paths.get(".","protocols_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
                PathUtils.copyDirectory(targetDir, outdatedDirectory);
                PathUtils.deleteDirectory(targetDir);
            }
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            appendLog("Не удалось создать папку для хранения протоколов: %s", getStackTrace(e));
        }

        //Обрабатываем все протоколы
        for (ProtocolType protocolType : ProtocolType.values()) {
            processProtocol(protocolType, username, password);
        }
    }

    /**
     * Загружает конкретный протокол
     *
     * @param protocolType тип протокола
     * @param username     имя пользователя для аутентификации в Git
     * @param password     пароль или токен для аутентификации в Git
     */
    public void processProtocol(ProtocolType protocolType, String username, String password) {
        appendLog("[ Downloading ] Протокол: %s, OID: %s", protocolType.getCode(), protocolType.getOid());
        try {
            String repoUrl = String.format("https://%s:%s@git.minzdrav.gov.ru/semd/%s.git", username, password, protocolType.getOid());
            try {
                File tempDir = File.createTempFile("jgit_clone_", "");
                //Удаляем временный файл, но сохраняем временную папку
                tempDir.delete();
                tempDir.mkdirs();

                Git git = Git.cloneRepository()
                        .setURI(repoUrl)
                        .setDirectory(tempDir)
                        .setProgressMonitor(new TextAreaProgressMonitor(logField))
                        .call();

                //Убираем autocrlf, чтобы репозиторий оставался "чистым"
                StoredConfig config = git.getRepository().getConfig();
                config.setString("core", null, "autocrlf", "false");
                config.save();

                // Обработка каждой версии протокола
                for (Integer version : protocolType.getVersions()) {
                    String versionName = protocolType.getCode() + "_V" + version;
                    appendLog("[ Checkout ] %s начато", versionName);

                    String branchName = protocolType.getOid() + "." + version;
                    git.checkout()
                            .setName("origin/" + branchName)
                            .setProgressMonitor(new TextAreaProgressMonitor(logField))
                            .call();

                    // Создание целевой директории для версии
                    Path targetDir = Paths.get(".", "protocols", versionName);
                    Files.createDirectories(targetDir);

                    processXsdFiles(tempDir.toPath(), targetDir, versionName);

                    if (!OUTDATED_PROTOCOLS.contains(versionName)) {
                        processSchematronFiles(tempDir.toPath(), targetDir, versionName);
                    }
                    appendLog("[ Checkout ] %s завершено", versionName);
                }
                git.close();
            } catch (IOException | GitAPIException e) {
                Thread.currentThread().interrupt();
                appendLog("Ошибка при клонировании репозитория: %s", getStackTrace(e));
            }
        } catch (Exception e) {
            appendLog("Ошибка при обработке протокола %s: %s", protocolType.getCode(), getStackTrace(e));
        }
    }

    /**
     * Обрабатывает XSD файлы из директории xsd
     */
    private void processXsdFiles(Path repoPath, Path targetDir, String versionName) throws IOException {
        Path xsdSourceDir = repoPath.resolve("xsd");
        Path xsdTargetDir = targetDir.resolve("xsd");
        Files.createDirectories(xsdTargetDir);

        if (!Files.exists(xsdSourceDir)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(xsdSourceDir)) {
            for (Path folder : stream) {
                if (!Files.isDirectory(folder)) {
                    continue;
                }

                String xsdFolder = folder.getFileName().toString();

                if (xsdFolder.equals("XSD CDA")) {
                    // Копируем XSD CDA
                    Path dest = xsdTargetDir.resolve("XSD_CDA");
                    PathUtils.copyDirectory(folder, dest);
                } else {
                    if (OUTDATED_PROTOCOLS.contains(versionName)) {
                        continue;
                    }

                    Path dest = xsdTargetDir.resolve("XSD_CDA_" + versionName);
                    PathUtils.copyDirectory(folder, dest);
                }
            }
        }
    }

    /**
     * Обрабатывает Schematron файлы
     */
    private void processSchematronFiles(Path repoPath, Path targetDir, String versionName) throws IOException {
        Path schematronSourceDir = repoPath.resolve("schematron");
        Path schematronTargetDir = targetDir.resolve("schematron");

        if (!Files.exists(schematronSourceDir)) {
            return;
        }

        Files.createDirectories(schematronTargetDir);

        // Поиск первого .sch файла
        Collection<File> files = FileUtils.listFiles(
                schematronSourceDir.toFile(),
                new SuffixFileFilter(".sch"),
                TrueFileFilter.INSTANCE
        );
        if (files.isEmpty()) {
            appendLog("Schematron-файл для %s не найден", versionName);
            return;
        }

        Path schFile = files.iterator().next().toPath();
        try {
            // Копирование как schematron.sch
            Path destSch = schematronTargetDir.resolve("schematron.sch");
            Files.copy(schFile, destSch, StandardCopyOption.REPLACE_EXISTING);

            // Создание .txt файла с оригинальным именем
            String originalFilename = schFile.getFileName().toString();
            Path txtFile = schematronTargetDir.resolve(originalFilename + ".txt");
            Files.createFile(txtFile);
        } catch (IOException e) {
            appendLog("Ошибка при копировании Schematron-файла для %s: %s", versionName, e.getStackTrace());
        }
    }
}

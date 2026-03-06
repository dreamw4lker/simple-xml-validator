package com.github.dreamw4lker.simplexvalfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class AboutController {
    @FXML
    private Label versionLabel;

    @FXML
    private TextArea licenseTextArea;

    public void initialize() {
        setVersionText();
        setLicenseText();
    }

    /**
     * Получение версии из ресурса custom.properties
     */
    private void setVersionText() {
        try {
            final Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("custom.properties"));
            versionLabel.setText("Версия: " + properties.getProperty("application.version"));
        } catch (IOException e) {
            log.error("Не удалось прочитать файл custom.properties", e);
        }
    }

    /**
     * Получение лицензии из файла LICENSE
     */
    private void setLicenseText() {
        try {
            Path licensePath = Paths.get(".", "LICENSE");
            String content = Files.readString(licensePath, StandardCharsets.UTF_8);
            licenseTextArea.setText(content);
        } catch (IOException e) {
            log.error("Не удалось прочитать файл LICENSE", e);
            licenseTextArea.setText("Не найден файл LICENSE");
        }
    }
}

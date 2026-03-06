package com.github.dreamw4lker.simplexvalfx.controllers;

import com.github.dreamw4lker.simplexvalfx.ValidatorApplication;
import com.github.dreamw4lker.simplexvalfx.beans.ProtocolTypeVersionConverter;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ProtocolType;
import com.github.dreamw4lker.simplexvalfx.beans.ProtocolTypeVersionBean;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidationResult;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidatorMode;
import com.github.dreamw4lker.simplexvalfx.utils.validator.schematron.SchematronValidator;
import com.github.dreamw4lker.simplexvalfx.utils.validator.xsd.XSDValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.github.dreamw4lker.simplexvalfx.utils.LoggingUtils.setupLogging;

@Slf4j
public class MainFormController {
    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem cdaFetchMenuItem;

    @FXML
    private MenuItem base64ToXmlMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Label noProtocolsLabel;

    @FXML
    private ComboBox<ProtocolTypeVersionBean> protocolTypeComboBox;

    @FXML
    private Button chooseFileBtn;

    @FXML
    private Label filePathLabel;

    @FXML
    private RadioButton checkTypeAllRadioBtn;

    @FXML
    private RadioButton checkTypeXsdRadioBtn;

    @FXML
    private RadioButton checkTypeSchematronRadioBtn;

    @FXML
    private CheckBox clearXMLNSCheckBox;

    @FXML
    private Button submitBtn;

    @FXML
    private Button clearLogBtn;

    @FXML
    private TextArea logTextArea;

    private File selectedXMLFile = null;
    private File lastSelectedDirectory = null;

    public void initialize() {
        setupLogging(logTextArea, MainFormController.class);
        setupLogging(logTextArea, XSDValidator.class);
        setupLogging(logTextArea, SchematronValidator.class);

        checkProtocolsPath();

        List<ProtocolTypeVersionBean> protocolTypeVersionBeans = ProtocolType.getProtocolTypeVersionBeans();
        protocolTypeComboBox.setConverter(new ProtocolTypeVersionConverter());
        protocolTypeComboBox.getItems().addAll(protocolTypeVersionBeans);
        protocolTypeComboBox.setValue(protocolTypeVersionBeans.getFirst());

        ToggleGroup toggleGroup = new ToggleGroup();
        checkTypeAllRadioBtn.setToggleGroup(toggleGroup);
        checkTypeXsdRadioBtn.setToggleGroup(toggleGroup);
        checkTypeSchematronRadioBtn.setToggleGroup(toggleGroup);

        exitMenuItem.setOnAction(this::onExit);
        cdaFetchMenuItem.setOnAction(this::onCdaFetch);
        base64ToXmlMenuItem.setOnAction(this::onBase64ToXml);
        aboutMenuItem.setOnAction(this::onAbout);

        chooseFileBtn.setOnAction(this::onChooseFileClick);
        submitBtn.setOnAction(this::onSubmit);
        clearLogBtn.setOnAction(this::onClearLogBtnClick);
    }

    /**
     * Проверка наличия (и непустоты) папки protocols
     */
    private void checkProtocolsPath() {
        Path protocolsPath = Paths.get("protocols");
        try {
            noProtocolsLabel.setVisible(
                    !Files.isDirectory(protocolsPath) || PathUtils.isEmptyDirectory(protocolsPath)
            );
        } catch (IOException e) {
            log.error("Ошибка при проверке папки «protocols»", e);
        }
    }

    /**
     * Действия при выборе пункта меню "Выход"
     */
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Действия при выборе пункта меню "CDA Fetcher"
     */
    private void onCdaFetch(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(ValidatorApplication.class.getResource("cda-fetch-form.fxml")));
        } catch (IOException e) {
            log.error("Ошибка при открытии формы «CDA Fetcher»", e);
            return;
        }

        Scene scene = new Scene(root, 800, 500);

        Stage stage = new Stage();
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.setScene(scene);
        stage.setTitle("CDA Fetcher");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(logTextArea.getScene().getWindow());
        stage.setOnHiding((closeEvent) -> {
            checkProtocolsPath();
        });
        stage.showAndWait();
    }

    /**
     * Действия при выборе пункта меню "Конвертер Base64 -> XML"
     */
    private void onBase64ToXml(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(ValidatorApplication.class.getResource("base64-to-xml-form.fxml")));
        } catch (IOException e) {
            log.error("Ошибка при открытии формы «Base64 to XML»", e);
            return;
        }

        Stage stage = new Stage();
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("Base64 to XML");
        stage.setMinWidth(800);
        stage.setMinHeight(500);

        Scene scene = new Scene(root, 800, 500);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Действия при выборе пункта меню "О программе"
     */
    private void onAbout(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(ValidatorApplication.class.getResource("about-form.fxml")));
        } catch (IOException e) {
            log.error("Ошибка при открытии формы «О программе»", e);
            return;
        }

        Scene scene = new Scene(root, 600, 400);

        Stage stage = new Stage();
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.setTitle("О программе");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(logTextArea.getScene().getWindow());
        stage.setOnHiding((closeEvent) -> {
            checkProtocolsPath();
        });
        stage.showAndWait();
    }

    /**
     * Действия при нажатии на кнопку "Выберите файл"
     */
    private void onChooseFileClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор XML-файла");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML-файлы", "*.xml"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        if (lastSelectedDirectory != null && lastSelectedDirectory.exists()) {
            fileChooser.setInitialDirectory(lastSelectedDirectory);
        }
        Window window = ((Node) event.getSource()).getScene().getWindow();
        selectedXMLFile = fileChooser.showOpenDialog(window);
        if (selectedXMLFile != null) {
            filePathLabel.setText(selectedXMLFile.getAbsolutePath());
            lastSelectedDirectory = selectedXMLFile.getParentFile();
        }
    }

    /**
     * Действия при нажатии на кнопку "Выполнить проверку"
     */
    private void onSubmit(ActionEvent event) {
        if (selectedXMLFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Выберите XML-файл для проверки");
            alert.showAndWait();
            return;
        }

        new Thread(() -> {
            ValidatorMode validatorMode = checkTypeAllRadioBtn.isSelected() ? ValidatorMode.ALL
                    : checkTypeXsdRadioBtn.isSelected() ? ValidatorMode.XSD : ValidatorMode.SCHEMATRON;
            ProtocolTypeVersionBean protocolTypeVersionBean = protocolTypeComboBox.getValue();

            //Чтение XML-файла в строку
            String content;
            try {
                content = IOUtils.toString(new FileInputStream(selectedXMLFile), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Не удалось прочитать файл «{}»", selectedXMLFile, e);
                return;
            }

            log.info("XML file: «{}»", selectedXMLFile);

            //Валидация по XSD
            List<ValidationResult> XSDResults = new ArrayList<>();
            for (Path xsdFilename : protocolTypeVersionBean.getXsdFilenames()) {
                XSDResults.add(new XSDValidator().validate(content, validatorMode, xsdFilename));
            }

            //Валидация по Schematron.
            //В некоторых Schematron-файлах не указан namespace по умолчанию.
            //Если установлен флаг, вырезаем его и из прочитанного документа
            if (clearXMLNSCheckBox.isSelected()) {
                content = content.replaceFirst("xmlns=\".*?\"", "xmlns=\"\"");
            }

            List<ValidationResult> schematronResults = new ArrayList<>();
            for (Path schematronFilename : protocolTypeVersionBean.getSchematronFilenames()) {
                schematronResults.add(new SchematronValidator().validate(content, validatorMode, schematronFilename));
            }

            log.info("Validation completed for file «{}»", selectedXMLFile);
            log.info("Results:");
            log.info("  XSD validation:");
            if (XSDResults.isEmpty()) {
                log.info("? No results");
            }
            for (int i = 0; i < XSDResults.size(); i++) {
                ValidationResult result = XSDResults.get(i);
                log.info("{} XSD.{}: {}", result.getIcon(), (i + 1), result);
            }

            log.info("  Schematron validation:");
            if (schematronResults.isEmpty()) {
                log.info("? No results");
            }
            for (int i = 0; i < schematronResults.size(); i++) {
                ValidationResult result = schematronResults.get(i);
                log.info("{} Schematron.{}: {}", result.getIcon(), (i + 1), result);
            }
        }).start();
    }

    /**
     * Действия при нажатии на кнопку "Очистить лог"
     */
    private void onClearLogBtnClick(ActionEvent event) {
        logTextArea.clear();
    }
}

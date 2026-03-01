package com.github.dreamw4lker.simplexvalfx.controllers;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import com.github.dreamw4lker.simplexvalfx.SimpleXMLValidatorApplication;
import com.github.dreamw4lker.simplexvalfx.beans.ProtocolTypeVersionConverter;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ProtocolType;
import com.github.dreamw4lker.simplexvalfx.beans.ProtocolTypeVersionBean;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidationResult;
import com.github.dreamw4lker.simplexvalfx.beans.enums.ValidatorMode;
import com.github.dreamw4lker.simplexvalfx.configuration.TextAreaLogbackAppender;
import com.github.dreamw4lker.simplexvalfx.utils.validator.schematron.SchematronValidator;
import com.github.dreamw4lker.simplexvalfx.utils.validator.xsd.XSDValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainFormController {
    private static final Logger log = (Logger) LoggerFactory.getLogger(MainFormController.class);

    @FXML
    private Button cdaFetchBtn;

    @FXML
    private ComboBox<ProtocolTypeVersionBean> protocolTypeComboBox;

    @FXML
    private Button chooseFileBtn;

    @FXML
    private Label filePathLabel;

    @FXML
    private CheckBox xsdCheckbox;

    @FXML
    private CheckBox schematronCheckbox;

    @FXML
    private Button submitBtn;

    @FXML
    private TextArea logTextArea;

    private File selectedXMLFile = null;

    public void setupLogging(TextArea logArea) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Настраиваем формат (Layout)
        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{HH:mm:ss} %-5level - %msg%n");
        layout.setContext(lc);
        layout.start();

        // Создаем и запускаем наш аппендер
        TextAreaLogbackAppender appender = new TextAreaLogbackAppender(logArea, layout);
        appender.setContext(lc);
        appender.start();

        // Добавляем аппендер к корневому (Root) логгеру
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(appender);
    }

    public void initialize() {
        setupLogging(logTextArea);

        protocolTypeComboBox.setConverter(new ProtocolTypeVersionConverter());
        protocolTypeComboBox.getItems().addAll(ProtocolType.getProtocolTypeVersionBeans());

        cdaFetchBtn.setOnAction((event) -> {
            // 1. Load the new FXML file
            Parent root = null;
            try {
                root = FXMLLoader.load(Objects.requireNonNull(SimpleXMLValidatorApplication.class.getResource("cda-fetch-form.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e); //todo
            }

            // 2. Get the current Stage object from the ActionEvent
            Stage stage = new Stage();

            // 3. Create a new Scene (or re-use an existing one) and set it on the stage
            Scene scene = new Scene(root, 1000, 500);
            stage.setScene(scene);
            stage.setTitle("CDA Fetcher");
            stage.show();
        });

        chooseFileBtn.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выбор XML-файла");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("XML-файлы", "*.xml"),
                    new FileChooser.ExtensionFilter("Все файлы", "*.*")
            );
            Window window = ((Node) event.getSource()).getScene().getWindow();
            selectedXMLFile = fileChooser.showOpenDialog(window);
            if (selectedXMLFile != null) {
                filePathLabel.setText(selectedXMLFile.getAbsolutePath());
            }
        });

        submitBtn.setOnAction((event) -> {
            //todo проверки перед запуском треда
            Thread thread = new Thread(() -> {
                ValidatorMode validatorMode = (xsdCheckbox.isSelected() && schematronCheckbox.isSelected())
                        ? ValidatorMode.ALL
                        : xsdCheckbox.isSelected() ? ValidatorMode.XSD : ValidatorMode.SCHEMATRON; //todo сделать выбор а не чекбоксы
                ProtocolTypeVersionBean protocolTypeVersionBean = protocolTypeComboBox.getValue(); //todo check selection


                //Чтение XML-файла в строку
                String content = null;
                try {
                    content = IOUtils.toString(new FileInputStream(selectedXMLFile), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e); //todo
                }

                log.info("XML file: «{}»", selectedXMLFile);

                //Валидация по XSD
                List<ValidationResult> XSDResults = new ArrayList<>();
                for (Path xsdFilename : protocolTypeVersionBean.getXsdFilenames()) {
                    try {
                        XSDResults.add(new XSDValidator().validate(content, validatorMode, xsdFilename.toString())); //todo tostring?
                    } catch (SAXException | IOException e) {
                        throw new RuntimeException(e);  //todo
                    }
                }

                //Валидация по Schematron.
                //В некоторых Schematron-файлах не указан namespace по умолчанию.
                //Если установлен флаг, вырезаем его и из прочитанного документа
                if (true/*properties.isClearXmlNamespaceOnSchematronValidation()*/) { //todo
                    content = content.replaceFirst("xmlns=\".*?\"", "xmlns=\"\"");
                }

                List<ValidationResult> schematronResults = new ArrayList<>();
                for (Path schematronFilename : protocolTypeVersionBean.getSchematronFilenames()) {
                    try {
                        schematronResults.add(new SchematronValidator().validate(content, validatorMode, schematronFilename.toString())); //todo tostring?
                    } catch (Exception e) {
                        throw new RuntimeException(e); //todo
                    }
                }

                log.info("Validation completed for file «{}»", selectedXMLFile);
                log.info("Results:");
                log.info("  XSD validation:");
                for (int i = 0; i < XSDResults.size(); i++) {
                    ValidationResult result = XSDResults.get(i);
                    log.info("{} XSD.{}: {}", result.getIcon(), (i + 1), result);
                }

                log.info("  Schematron validation:");
                for (int i = 0; i < schematronResults.size(); i++) {
                    ValidationResult result = schematronResults.get(i);
                    log.info("{} Schematron.{}: {}", result.getIcon(), (i + 1), result);
                }
            });
            thread.setDaemon(true);
            thread.start();
        });
    }
}

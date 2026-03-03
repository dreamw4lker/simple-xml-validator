package com.github.dreamw4lker.simplexvalfx.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

@Slf4j
public class Base64ToXmlController {
    @FXML
    private MenuItem saveAsMenuItem;

    @FXML
    private CheckBox formatXMLCheckbox;

    @FXML
    private TextArea base64TextArea;

    @FXML
    private TextArea xmlTextArea;

    public void initialize() {
        saveAsMenuItem.setOnAction(this::onSaveAsClick);

        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(this::onAutoConvert);
        base64TextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.playFromStart();
        });

        formatXMLCheckbox.setOnAction(this::onAutoConvert);
    }

    /**
     * Действия при выборе пункта меню "Сохранить XML как..."
     */
    private void onSaveAsClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить XML");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML-файлы", "*.xml"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        fileChooser.setInitialFileName("xml_output.xml");

        Window window = xmlTextArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            try {
                Files.write(file.toPath(), Collections.singleton(xmlTextArea.getText()));
            } catch (IOException e) {
                log.error("Ошибка ввода/вывода при сохранении файла", e);
            }
        }
    }

    /**
     * Форматирование XML-строки
     * @param xmlString XML-строка
     * todo: extract to class
     */
    private String formatXML(String xmlString) {
        if (ObjectUtils.isEmpty(xmlString)) {
            return "";
        }
        try {
            SAXReader reader = new SAXReader();
            reader.setValidation(false); //Не валидируем принятый XML

            Document document = reader.read(new StringReader(xmlString));

            StringWriter outputWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(outputWriter, OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();

            return outputWriter.toString();
        } catch (DocumentException e) {
            log.error("Не удалось отформатировать XML: ошибка при разборе", e);
            return "Не удалось отформатировать XML: ошибка при разборе. Подробности см. в логе приложения. Исходный текст:\n" + xmlString;
        } catch (IOException e) {
            log.error("Не удалось отформатировать XML: ошибка ввода/вывода", e);
            return "Не удалось отформатировать XML: ошибка ввода/вывода. Подробности см. в логе приложения. Исходный текст:\n" + xmlString;
        }
    }

    /**
     * Действие автоматического перевода Base64->XML.
     * Вызывается при клике на чекбокс "Форматировать XML" и в процессе ввода данных в поле Base64
     */
    private void onAutoConvert(ActionEvent event) {
        String base64String = base64TextArea.getText();
        byte[] decodedBytes = Base64.decodeBase64(base64String);

        String xmlString = new String(decodedBytes, StandardCharsets.UTF_8);

        if (formatXMLCheckbox.isSelected()) {
            xmlString = formatXML(xmlString);
        }
        xmlTextArea.setText(xmlString);
    }
}

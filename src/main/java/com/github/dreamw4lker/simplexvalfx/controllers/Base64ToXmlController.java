package com.github.dreamw4lker.simplexvalfx.controllers;

import com.github.dreamw4lker.simplexvalfx.service.Base64ToXmlService;
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

import java.io.File;
import java.io.IOException;
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
     * Действие автоматического перевода Base64->XML.
     * Вызывается при клике на чекбокс "Форматировать XML" и в процессе ввода данных в поле Base64
     */
    private void onAutoConvert(ActionEvent event) {
        xmlTextArea.setText(
                new Base64ToXmlService().convertBase64ToXml(
                        base64TextArea.getText(),
                        formatXMLCheckbox.isSelected()
                )
        );
    }
}

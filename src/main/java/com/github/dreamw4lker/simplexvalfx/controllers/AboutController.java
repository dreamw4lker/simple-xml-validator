package com.github.dreamw4lker.simplexvalfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
     * Получение лицензии из resources внутри jar
     */
    private void setLicenseText() {
        try {
            try (var inputStream = getClass().getClassLoader().getResourceAsStream("LICENSE")) {
                if (inputStream == null) {
                    throw new IOException("LICENSE not found in classpath");
                }
                String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                licenseTextArea.setText(content);
            }
        } catch (IOException e) {
            log.error("Не удалось прочитать LICENSE из ресурсов", e);
            licenseTextArea.setText("Не найден файл LICENSE");
        }
    }
}

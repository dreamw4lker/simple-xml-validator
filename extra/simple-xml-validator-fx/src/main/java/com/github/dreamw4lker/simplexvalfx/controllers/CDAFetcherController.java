package com.github.dreamw4lker.simplexvalfx.controllers;

import com.github.dreamw4lker.simplexvalfx.service.CDAFetcherService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.ObjectUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CDAFetcherController {
    @FXML
    private Hyperlink minzdravLink;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextArea logField;

    @FXML
    private Button submitBtn;

    public void initialize() {
        minzdravLink.setOnMouseClicked((event) -> {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                new Thread(() -> {
                    try {
                        desktop.browse(new URI("https://git.minzdrav.gov.ru"));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        });

        submitBtn.setOnMouseClicked(event -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            if (ObjectUtils.isEmpty(login) || ObjectUtils.isEmpty(password)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Необходимо указать логин и пароль");
                alert.showAndWait();
                return;
            }
            //todo спросить подтверждение
            changeFieldsActivity(true);

            Thread thread = new Thread(() -> {
                new CDAFetcherService(logField).downloadAllProtocols(login, password);
                changeFieldsActivity(false);
            });
            thread.setDaemon(true);
            thread.start();
        });
    }

    private void changeFieldsActivity(boolean isDisabled) {
        loginField.setDisable(isDisabled);
        passwordField.setDisable(isDisabled);
        submitBtn.setDisable(isDisabled);
    }
}

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
import java.util.Optional;

//todo: выключить debug-логирование гита
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
        minzdravLink.setOnAction((event) -> {
            this.onMinzdravLinkClicked();
        });

        submitBtn.setOnAction((event) -> {
            this.onSubmit();
        });
    }

    /**
     * Действия по нажатию на ссылку git.minzdrav.gov.ru
     */
    private void onMinzdravLinkClicked() {
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
    }

    /**
     * Действия по нажатию на кнопку скачивания
     */
    private void onSubmit() {
        String login = loginField.getText();
        String password = passwordField.getText();

        //Проверка формы ввода
        if (ObjectUtils.isEmpty(login) || ObjectUtils.isEmpty(password)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Укажите логин и пароль от git.minzdrav.gov.ru");
            alert.showAndWait();
            return;
        }

        //Отображение модального окна-подтверждения
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Вы действительно хотите обновить протоколы?");
        alert.setContentText("Все файлы, находящиеся в папке protocols, будут перезаписаны");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        changeFieldsActivity(true);

        //Запуск скачивания в отдельном потоке
        new Thread(() -> {
            new CDAFetcherService(logField).downloadAllProtocols(login, password);
            changeFieldsActivity(false);
        }).start();
    }
    /**
     * Блокировка/разблокировка полей на форме
     */
    private void changeFieldsActivity(boolean isDisabled) {
        loginField.setDisable(isDisabled);
        passwordField.setDisable(isDisabled);
        submitBtn.setDisable(isDisabled);
    }
}

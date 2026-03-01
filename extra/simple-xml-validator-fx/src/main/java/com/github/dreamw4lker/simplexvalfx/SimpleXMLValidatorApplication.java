package com.github.dreamw4lker.simplexvalfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleXMLValidatorApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-form.fxml"));

        primaryStage.setScene(new Scene(fxmlLoader.load(), 1000, 500));
        primaryStage.setTitle("XML Validator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

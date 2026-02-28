package com.github.dreamw4lker.simplexvalfx;

import com.github.dreamw4lker.simplexvalfx.controllers.CDAFetcherController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SimpleXMLValidatorApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        CDAFetcherController cdaFetcherController = new CDAFetcherController();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cda-fetch-form.fxml"));
        fxmlLoader.setController(cdaFetcherController);

        VBox root = fxmlLoader.load();

        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.setTitle("CDA Fetcher");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

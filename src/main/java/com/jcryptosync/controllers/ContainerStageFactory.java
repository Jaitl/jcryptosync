package com.jcryptosync.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ContainerStageFactory {
    public static Stage createContainerStage(ClassLoader loader) {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(loader.getResource("fxml/container.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Stage stage = new Stage();
        stage.setTitle("Управление контейнером");

        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add("styles/main.css");

        stage.setScene(scene);

        return stage;
    }
}
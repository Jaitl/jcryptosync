package com.jcryptosync.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;

public class LoginSceneFactory {

    private static HashMap<String, Scene> sceneMap = new HashMap<>();

    public static Scene createLoginScene(ClassLoader loader) {

        if(!sceneMap.containsKey("login")) {
            Scene loginScene = createScene(loader, new LoginController());
            sceneMap.put("login", loginScene);
        }

        return sceneMap.get("login");
    }

    public static Scene createNewPrimaryKeyScene(ClassLoader loader) {

        if(!sceneMap.containsKey("primaryKey")) {
            Scene primaryKeyScene = createScene(loader, new CreatePrimaryKeyController());
            sceneMap.put("primaryKey", primaryKeyScene);
        }

        return sceneMap.get("primaryKey");
    }

    private static Scene createScene(ClassLoader loader, BaseLoginController controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(loader.getResource("fxml/login.fxml"));

        fxmlLoader.setController(controller);

        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.prepareDialog();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/main.css");

        return scene;
    }
}

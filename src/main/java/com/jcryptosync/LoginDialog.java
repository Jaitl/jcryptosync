package com.jcryptosync;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginDialog  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/login.fxml"));
        Parent root = fxmlLoader.load();

        LoginController controller = fxmlLoader.getController();
        controller.enableLoginMode();

        Scene scene = new Scene(root, 500, 350);
        scene.getStylesheets().add("styles/main.css");

        primaryStage.setMinHeight(500);
        primaryStage.setMinHeight(350);
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}

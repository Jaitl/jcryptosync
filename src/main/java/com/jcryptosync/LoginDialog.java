package com.jcryptosync;

import com.jcryptosync.controllers.LoginSceneFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginDialog  extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        Scene scene = LoginSceneFactory.createLoginScene(getClass().getClassLoader());

        primaryStage.setMinHeight(500);
        primaryStage.setMinHeight(370);
        primaryStage.setTitle("Авторизация");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}

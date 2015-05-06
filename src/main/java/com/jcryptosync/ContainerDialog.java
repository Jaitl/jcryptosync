package com.jcryptosync;

import com.jcryptosync.controllers.StageFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class ContainerDialog extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage stage = StageFactory.createContainerStage(getClass().getClassLoader());

        primaryStage.onCloseRequestProperty().bindBidirectional(stage.onCloseRequestProperty());
        primaryStage.onHidingProperty().bindBidirectional(stage.onHidingProperty());

        primaryStage.setTitle("Управление контейнером");
        primaryStage.setScene(stage.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

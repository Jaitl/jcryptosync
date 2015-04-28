package com.jcryptosync.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class LoginController extends BaseLoginController {
    @Override
    protected void changeControllerAction(ActionEvent event) {
        Node node=(Node) event.getSource();
        Stage stage=(Stage) node.getScene().getWindow();

        Scene scene = LoginSceneFactory.createNewPrimaryKeyScene(getClass().getClassLoader());

        stage.setScene(scene);
    }

    @Override
    protected void selectKeyAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения ключа");
        File key = key = fileChooser.showOpenDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
        }
    }

    @Override
    protected void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");
        File container = fileChooser.showOpenDialog(null);

        if(container != null) {
            pathToContainer.setText(container.getPath());
        }
    }

    @Override
    public void executeAction() {

    }

    @Override
    public void prepareDialog() {
        super.prepareDialog();

        hideSecondPassword();
        createButton.setText("Создать контейнер");
        enterButton.setText("Войти");
    }


    private void hideSecondPassword() {
        secondPasswordContainer.setManaged(false);
        secondPassword.setManaged(false);
        secondPassword.setVisible(false);
        secondPasswordLabel.setManaged(false);
        secondPasswordLabel.setVisible(false);
    }
}

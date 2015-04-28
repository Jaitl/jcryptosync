package com.jcryptosync.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class LoginController {

    private enum LoginMode {
        Login,
        Create
    };

    private LoginMode loginMode = LoginMode.Login;

    @FXML
    private TextField firstPassword;
    @FXML
    private TextField secondPassword;
    @FXML
    private VBox secondPasswordContainer;
    @FXML
    private Label secondPasswordLabel;
    @FXML
    private Button createButton;
    @FXML
    private Button enterButton;
    @FXML
    private TextField pathToKey;
    @FXML
    private TextField pathToContainer;

    @FXML
    public void createAction() {
        if(loginMode == LoginMode.Login) {
            loginMode = LoginMode.Create;
            enableCreateMode();
        } else if (loginMode == LoginMode.Create) {
            loginMode = LoginMode.Login;
            enableLoginMode();
        }
    }

    @FXML
    private void selectKeyAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения ключа");
        File key = null;

        if(loginMode == LoginMode.Login) {
            key = fileChooser.showOpenDialog(null);
        } else if(loginMode == LoginMode.Create) {
            key = fileChooser.showSaveDialog(null);
        }

        if(key != null) {
            pathToKey.setText(key.getPath());
        }
    }

    @FXML
    private void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");
        File container = null;

        if(loginMode == LoginMode.Login) {
            container = fileChooser.showOpenDialog(null);
        } else if(loginMode == LoginMode.Create) {
            container = fileChooser.showSaveDialog(null);
        }

        if(container != null) {
            pathToContainer.setText(container.getPath());
        }
    }

    @FXML
    public void enterAction() {

    }

    public void enableLoginMode() {
        hideSecondPassword();
        createButton.setText("Создать контейнер");
        enterButton.setText("Войти");
    }

    private void enableCreateMode() {
        showSecondPassword();
        createButton.setText("Отменить");
        enterButton.setText("Создать");
    }

    private void showSecondPassword() {
        secondPasswordContainer.setManaged(true);
        secondPassword.setManaged(true);
        secondPassword.setVisible(true);
        secondPasswordLabel.setManaged(true);
        secondPasswordLabel.setVisible(true);
    }

    private void hideSecondPassword() {
        secondPasswordContainer.setManaged(false);
        secondPassword.setManaged(false);
        secondPassword.setVisible(false);
        secondPasswordLabel.setManaged(false);
        secondPasswordLabel.setVisible(false);
    }
}

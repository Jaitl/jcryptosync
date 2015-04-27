package com.jcryptosync;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    public void createAction() {
        if(loginMode == LoginMode.Login) {
            loginMode = LoginMode.Create;
            enableCreateMode();
        } else if (loginMode == LoginMode.Create) {
            loginMode = LoginMode.Login;
            enableLoginMode();
        }
    }

    public void enableLoginMode() {
        hideSecondPassword();
        createButton.setText("Новый контейнер");
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

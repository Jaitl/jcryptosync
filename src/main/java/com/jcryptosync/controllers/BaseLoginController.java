package com.jcryptosync.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


public abstract class BaseLoginController {
    @FXML
    protected PasswordField firstPassword;
    @FXML
    protected PasswordField secondPassword;
    @FXML
    protected VBox secondPasswordContainer;
    @FXML
    protected Label secondPasswordLabel;
    @FXML
    protected Button createButton;
    @FXML
    protected Button enterButton;
    @FXML
    protected TextField pathToKey;
    @FXML
    protected TextField pathToContainer;
    @FXML
    private TextArea errorArea;
    @FXML
    private Label errorTitleLabel;

    @FXML
    protected abstract void changeControllerAction(ActionEvent event);
    @FXML
    protected abstract void selectKeyAction();
    @FXML
    protected abstract void selectContainerAction();
    @FXML
    protected abstract void executeAction();

    private TextField lastField;

    public void prepareDialog() {
        clearErrors();
    }

    protected void setError(String textError, TextField field) {
        errorTitleLabel.setVisible(true);
        errorArea.setVisible(true);
        errorArea.setText(textError);

        field.getStyleClass().addAll("error");
        lastField = field;
    }

    protected void clearErrors() {
        errorArea.setVisible(false);
        errorTitleLabel.setVisible(false);

        if(lastField != null)
            lastField.getStyleClass().removeAll("error");
    }
}
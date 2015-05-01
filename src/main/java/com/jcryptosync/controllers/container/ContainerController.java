package com.jcryptosync.controllers.container;

import com.jcryptosync.container.ContainerManager;
import com.jcryptosync.exceptoins.ContainerMountException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerController {
    protected static Logger log = LoggerFactory.getLogger(ContainerController.class);

    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private VBox passwordVbox;
    @FXML
    private TextArea errorArea;
    @FXML
    private Label errorLabel;
    @FXML
    private VBox errorVbox;
    @FXML
    private Label statusLabel;
    @FXML
    protected Button executeButton;


    private enum ContainerStatus {
        Enabled,
        Disabled
    }

    private ContainerStatus containerStatus = ContainerStatus.Disabled;

    private ContainerManager containerManager = ContainerManager.createManager();

    @FXML
    private void executeAction() {
        if(containerStatus == ContainerStatus.Disabled) {
            boolean result = enableContainer();

            if(result) {
                containerStatus = ContainerStatus.Enabled;
                hidePasswordForm();
                statusEnabled();
            }
        } else if(containerStatus == ContainerStatus.Enabled) {
            boolean result = disableContainer();

            if(result) {
                containerStatus = ContainerStatus.Disabled;
                showPasswordForm();
                statusDisabled();
            }
        }
    }

    public void prepareDialog() {
        clearError();
        statusDisabled();
    }

    private boolean enableContainer() {
        try {
            containerManager.openContainer();
        } catch (ContainerMountException e) {
            log.error("open container error", e);
            setError(e.getMessage());
            return false;
        }

        containerManager.startFileWatcher();

        return true;
    }

    private boolean disableContainer() {
        try {
            containerManager.closeContainer();
        } catch (ContainerMountException e) {
            log.error("close container error", e);
            setError(e.getMessage());
            return false;
        }

        containerManager.stopFileWatcher();

        return true;
    }

    private void statusDisabled() {
        executeButton.setText("Подключить контейнер");
        statusLabel.setText("Отключен");
        statusLabel.getStyleClass().removeAll("green");
        statusLabel.getStyleClass().add("red");
    }

    private void statusEnabled() {
        executeButton.setText("Отключить контейнер");
        statusLabel.setText("Подключен");
        statusLabel.getStyleClass().removeAll("red");
        statusLabel.getStyleClass().add("green");

    }

    private void clearError() {
        hideErrorForm();
        passwordField.getStyleClass().removeAll("error");
    }
    private void setError(String error) {
        showErrorForm();
        passwordField.getStyleClass().addAll("error");
        errorArea.setText(error);
    }

    private void hideErrorForm() {
        errorVbox.setManaged(false);
        errorVbox.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorArea.setManaged(false);
        errorArea.setVisible(false);
    }

    private void showErrorForm() {
        errorVbox.setManaged(true);
        errorVbox.setVisible(true);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
        errorArea.setManaged(true);
        errorArea.setVisible(true);
    }

    private void hidePasswordForm() {
        passwordVbox.setManaged(false);
        passwordVbox.setVisible(false);
        passwordLabel.setManaged(false);
        passwordLabel.setVisible(false);
        passwordField.setManaged(false);
        passwordField.setVisible(false);
    }

    private void showPasswordForm() {
        passwordVbox.setManaged(true);
        passwordVbox.setVisible(true);
        passwordLabel.setManaged(true);
        passwordLabel.setVisible(true);
        passwordField.setManaged(true);
        passwordField.setVisible(true);
    }
}

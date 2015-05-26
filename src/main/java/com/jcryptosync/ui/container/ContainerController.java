package com.jcryptosync.ui.container;

import com.jcryptosync.Bootloader;
import com.jcryptosync.exceptoins.ContainerMountException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerController {
    protected static Logger log = LoggerFactory.getLogger(ContainerController.class);
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

    private Bootloader bootloader = new Bootloader();

    @FXML
    private void executeAction() {
        if(containerStatus == ContainerStatus.Disabled) {
            boolean result = enableContainer();

            if(result) {
                containerStatus = ContainerStatus.Enabled;
                statusEnabled();
            }
        } else if(containerStatus == ContainerStatus.Enabled) {
            boolean result = disableContainer();

            if(result) {
                containerStatus = ContainerStatus.Disabled;
                statusDisabled();
            }
        }
    }

    public void prepareDialog(Stage stage) {
        clearError();
        statusDisabled();
        bootloader.runApplication();

        stage.setOnCloseRequest(e -> bootloader.stopApplication());
        //stage.setOnHiding(e -> bootstrap.stopApplication());
    }

    private boolean enableContainer() {
        try {
            bootloader.openContainer();
        } catch (ContainerMountException e) {
            log.error("open container error", e);
            setError(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean disableContainer() {
        try {
            bootloader.closeContainer();
        } catch (ContainerMountException e) {
            log.error("close container error", e);
            setError(e.getMessage());
            return false;
        }

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
    }
    private void setError(String error) {
        showErrorForm();
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
}

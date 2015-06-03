package com.jcryptosync.ui.container;

import com.jcryptosync.Bootloader;
import com.jcryptosync.exceptoins.ContainerMountException;
import com.jcryptosync.ui.info.InfoController;
import com.jcryptosync.ui.settings.SettingsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerController implements ShowMessage {
    protected static Logger log = LoggerFactory.getLogger(ContainerController.class);

    @FXML
    private TextArea taMessage;

    private Bootloader bootloader = new Bootloader();



    public void prepareDialog(Stage stage) {
        MessageService.setMessageService(this);

        bootloader.runApplication();

        stage.setOnCloseRequest(e -> {
            try {
                bootloader.stopApplication();
            } catch (ContainerMountException e1) {
                e.consume();
                showMessage("ошибка при отключении диска, возможно один из файлов используется");
            }
        });
        //stage.setOnHiding(e -> bootstrap.stopApplication());
    }

    @Override
    public void showMessage(String message) {
        String oldTest = taMessage.getText();
        String newText;
        message = "* " + message;

        if(oldTest.trim().length() == 0)
            newText = message;
        else
            newText = oldTest + "\n" + message;

        taMessage.setText(newText);
    }

    @FXML
    public void openSettings() {
        SettingsController.openSettings(getClass().getClassLoader());
    }

    @FXML
    public void openInfo() {
        InfoController.openInfo(getClass().getClassLoader());
    }

    @FXML
    void closeDialog() {
        Platform.exit();
    }
}

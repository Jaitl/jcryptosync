package com.jcryptosync.ui.container;

import com.jcryptosync.Bootloader;
import com.jcryptosync.data.preferences.SyncPreferences;
import com.jcryptosync.exceptoins.ContainerMountException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
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

        stage.setOnCloseRequest(e -> bootloader.stopApplication());
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
}

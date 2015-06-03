package com.jcryptosync.ui.info;

import com.jcryptosync.preferences.ContainerPreferences;
import com.jcryptosync.preferences.SyncPreferences;
import com.jcryptosync.vfs.manager.VFSManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class InfoController {
    @FXML
    private TextField tfPort;
    @FXML
    private TextField tfIdClient;
    @FXML
    private TextField tfIdGroup;
    @FXML
    private TextField tfDiskState;

    public void loadInfo() {
        tfPort.setText(Integer.toString(ContainerPreferences.getInstance().getJettyPort()));
        tfIdClient.setText(ContainerPreferences.getInstance().getClientId());
        tfIdGroup.setText(SyncPreferences.getInstance().getGroupId());

        VFSManager vfsManager = SyncPreferences.getInstance().getVfsManager();
        if(vfsManager == null) {
            tfDiskState.setText("Отключен");
        } else {
            if(vfsManager.isMount())
                tfDiskState.setText("Подключен");
            else
                tfDiskState.setText("Отключен");
        }
    }

    @FXML
    void closeDialog(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public static void openInfo(ClassLoader classLoader) {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("fxml/info.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InfoController controller = fxmlLoader.getController();

        Stage stage = new Stage();
        stage.setTitle("Настройки");

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("styles/main.css");

        stage.setScene(scene);

        controller.loadInfo();
        stage.show();
    }
}

package com.jcryptosync.ui.settings;

import com.jcryptosync.data.preferences.UserPreferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    private TextField tfAddressClient;
    @FXML
    private TextField tfStartPort;
    @FXML
    private TextField tfEndPort;
    @FXML
    private CheckBox cbHardPassword;

    public void loadSettings() {
        tfStartPort.setText(Integer.toString(UserPreferences.getStartPort()));
        tfEndPort.setText(Integer.toString(UserPreferences.getEndPort()));
        cbHardPassword.setSelected(UserPreferences.isHardPassword());
        tfAddressClient.setText(UserPreferences.getClientAddress());
    }

    @FXML
    private void closeDialog(ActionEvent event) {
        if(checkSettings()) {
            UserPreferences.setStartPort(tfStartPort.getText());
            UserPreferences.setEndPort(tfEndPort.getText());
            UserPreferences.setHardPassword(cbHardPassword.isSelected());
            UserPreferences.setClientAddress(tfAddressClient.getText());

            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    private boolean checkSettings() {
        clearErrors();
        
        try {
            Integer.parseInt(tfStartPort.getText());
        } catch (NumberFormatException e) {
            setError(tfStartPort);
            return false;
        }

        try {
            Integer.parseInt(tfEndPort.getText());
        } catch (NumberFormatException e) {
            setError(tfEndPort);
            return false;
        }

        return true;
    }

    public static void openSettings(ClassLoader classLoader){
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(classLoader.getResource("fxml/settings.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SettingsController controller = fxmlLoader.getController();

        Stage stage = new Stage();
        stage.setTitle("Настройки");

        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().add("styles/main.css");

        stage.setScene(scene);

        controller.loadSettings();
        stage.show();
    }

    protected void setError(TextField field) {
        lastField = field;
        field.getStyleClass().addAll("error");
    }

    private TextField lastField;

    protected void clearErrors() {
         if(lastField != null)
            lastField.getStyleClass().removeAll("error");
    }
}

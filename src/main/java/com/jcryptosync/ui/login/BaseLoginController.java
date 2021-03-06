package com.jcryptosync.ui.login;

import com.jcryptosync.data.MainKeyManager;
import com.jcryptosync.exceptoins.NoCorrectCompositeKeyException;
import com.jcryptosync.exceptoins.NoCorrectMasterKeyException;
import com.jcryptosync.preferences.SyncPreferences;
import com.jcryptosync.preferences.UserPreferences;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.ui.container.ContainerController;
import com.jcryptosync.ui.settings.SettingsController;
import com.jcryptosync.utils.PasswordUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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
    protected CheckBox isNewContainer;

    protected enum Mode {
        Login,
        Create
    };

    protected Mode currentMode;

    @FXML
    protected abstract void changeControllerAction(ActionEvent event);
    protected abstract byte[] computeMainKey() throws IOException, NoCorrectPasswordException, NoCorrectCompositeKeyException, NoCorrectMasterKeyException;

    @FXML
    protected void selectKeyAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения ключа");

        Path initDir = UserPreferences.getPathToKey().getParent();

        if(initDir != null) {
            if (Files.exists(initDir)) {
                fileChooser.setInitialDirectory(initDir.toFile());
            }
        }

        File key = null;

        if(currentMode == Mode.Login)
            key = fileChooser.showOpenDialog(null);
        else
            key = fileChooser.showSaveDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
            UserPreferences.setPathToKey(key.getPath());
        }
    }

    @FXML
    protected void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");

        Path initDir = UserPreferences.getPathToContainer().getParent();

        if(initDir != null) {
            if (Files.exists(initDir)) {
                fileChooser.setInitialDirectory(initDir.toFile());
            }
        }

        File container;

        if(isNewContainer.isSelected())
            container = fileChooser.showSaveDialog(null);
        else
            container = fileChooser.showOpenDialog(null);

        if(container != null) {
            pathToContainer.setText(container.getPath());
            UserPreferences.setPathToContainer(container.getPath());
        }
    }

    @FXML
    public void executeAction(ActionEvent event) {
        clearErrors();

        if(checkFields()) {
            try {
                byte[] mainKey = computeMainKey();

                UserPreferences.setPathToContainer(pathToContainer.getText());
                UserPreferences.setPathToKey(pathToKey.getText());

                String groupId = MainKeyManager.computeGroupId(mainKey);
                SyncPreferences.getInstance().setGroupId(groupId);

                byte[] key = MainKeyManager.computeCompositeKey(firstPassword.getText(), mainKey);
                SyncPreferences.getInstance().setCompositeKey(key);

                showVFSDialog();
                ((Node)(event.getSource())).getScene().getWindow().hide();

            } catch (IOException e) {
                setError("Ошибка при работе с мастер-ключем.", pathToKey);
            } catch (NoCorrectPasswordException e) {
                setError("Неправильный пароль.", firstPassword);
            } catch (NoCorrectCompositeKeyException e) {
                setError("Пароль и мастер-ключ не похдодят к выбранному контейнеру.", pathToContainer);
            } catch (NoCorrectMasterKeyException e) {
                setError("Мастер-ключ поврежден.", pathToKey);
            }
        }
    }


    protected boolean checkFields() {
        if(firstPassword.getText().trim().length() == 0) {
            setError("Поле с паролем не заполнено", firstPassword);
            return false;
        }

        if(!checkPassword(firstPassword.getText())) {
            setError("Пароль слишком простой", firstPassword);
            return false;
        }

        if(pathToKey.getText().trim().length() == 0) {
            setError("Путь до ключа не выбран", pathToKey);
            return false;
        }

        if(pathToContainer.getText().trim().length() == 0) {
            setError("Путь до контейнера не выбран", pathToContainer);
            return false;
        }

        Path pathToFolder = Paths.get(pathToContainer.getText()).getParent();

        if (Files.notExists(pathToFolder)) {
            setError("Путь до контейнера не существует", pathToContainer);
            return false;
        }

        if(currentMode == Mode.Login) {

            Path pathKey = Paths.get(pathToKey.getText());

            if (Files.notExists(pathKey)) {
                setError("Масте-ключ по указанному пути не найден", pathToKey);
                return false;
            }
        }

        return true;
    }

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

    protected boolean checkPassword(String password) {
        if(UserPreferences.isHardPassword())
            return PasswordUtils.checkPassword(password);
        else if(password.trim().length() > 0)
            return true;
        else return false;
    }

    protected void showVFSDialog() {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/container.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContainerController controller = fxmlLoader.getController();

        Stage stage = new Stage();
        stage.setTitle("Список событий");

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add("styles/main.css");

        stage.setScene(scene);

        controller.prepareDialog(stage);

        stage.show();
    }

    @FXML
    public void openSettings() {
        SettingsController.openSettings(getClass().getClassLoader());
    }

    @FXML
    public void closeDialog() {
        Platform.exit();
    }
}

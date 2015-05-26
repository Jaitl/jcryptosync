package com.jcryptosync.ui.login;

import com.jcryptosync.data.MasterKeyManager;
import com.jcryptosync.data.SyncPreferences;
import com.jcryptosync.data.UserPreferences;
import com.jcryptosync.domain.MainKey;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.ui.LoginSceneFactory;
import com.jcryptosync.utils.HashUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginController extends BaseLoginController {
    @Override
    protected void changeControllerAction(ActionEvent event) {
        Node node=(Node) event.getSource();
        Stage stage=(Stage) node.getScene().getWindow();

        Scene scene = LoginSceneFactory.createNewKeyScene(getClass().getClassLoader());

        stage.setScene(scene);
    }

    @Override
    protected void selectKeyAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения ключа");

        Path initDir = UserPreferences.getPathToKey().getParent();

        if(initDir != null) {
            if (Files.exists(initDir)) {
                fileChooser.setInitialDirectory(initDir.toFile());
            }
        }

        File key = fileChooser.showOpenDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
            UserPreferences.setPathToKey(key.getPath());
        }
    }

    @Override
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

    @Override
    public void executeAction(ActionEvent event) {
        clearErrors();

        if(checkFields()) {
            try {

                MasterKeyManager keyManager = new MasterKeyManager();
                boolean passIsCorrect = keyManager.checkPassword(firstPassword.getText(), Paths.get(pathToKey.getText()));

                if(passIsCorrect) {
                    UserPreferences.setPathToContainer(pathToContainer.getText());
                    UserPreferences.setPathToKey(pathToKey.getText());

                    MainKey mainKey = keyManager.loadPrimaryKeyFromFile(firstPassword.getText(), Paths.get(pathToKey.getText()));

                    String groupId = HashUtils.computeGroupId(mainKey.getKey());
                    SyncPreferences.getInstance().setGroupId(groupId);

                    byte[] key = HashUtils.computeKey(firstPassword.getText(), mainKey.getKey());
                    SyncPreferences.getInstance().setKey(key);


                    showVFSDialog();
                    ((Node)(event.getSource())).getScene().getWindow().hide();
                }
            } catch (IOException e) {
                setError("Файл с ключем недоступен.", pathToKey);
            } catch (NoCorrectPasswordException e) {
                setError("Неправильный пароль.", firstPassword);
            }
        }
    }



    @Override
    public void prepareDialog() {
        super.prepareDialog();

        hideSecondPassword();
        createButton.setText("Создать контейнер");
        enterButton.setText("Войти");

        String pathKey = UserPreferences.getPathToKey().toString();

        if(pathToKey != null)
            pathToKey.setText(pathKey);

        String pathContainer = UserPreferences.getPathToContainer().toString();

        if(pathContainer != null)
            pathToContainer.setText(pathContainer);
    }


    private void hideSecondPassword() {
        secondPasswordContainer.setManaged(false);
        secondPassword.setManaged(false);
        secondPassword.setVisible(false);
        secondPasswordLabel.setManaged(false);
        secondPasswordLabel.setVisible(false);
    }
}

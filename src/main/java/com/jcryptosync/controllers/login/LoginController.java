package com.jcryptosync.controllers.login;

import com.jcryptosync.QuickPreferences;
import com.jcryptosync.container.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.container.primarykey.PrimaryKeyManager;
import com.jcryptosync.controllers.LoginSceneFactory;
import com.jcryptosync.controllers.StageFactory;
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

        Path initDir = QuickPreferences.getPathToKey().getParent();

        if(Files.exists(initDir)) {
            fileChooser.setInitialDirectory(initDir.toFile());
        }

        File key = fileChooser.showOpenDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
            QuickPreferences.setPathToKey(key.getPath());
        }
    }

    @Override
    protected void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");

        Path initDir = QuickPreferences.getPathToContainer().getParent();

        if(Files.exists(initDir)) {
            fileChooser.setInitialDirectory(initDir.toFile());
        }

        File container;

        if(isNewContainer.isSelected())
            container = fileChooser.showSaveDialog(null);
        else
            container = fileChooser.showOpenDialog(null);

        if(container != null) {
            pathToContainer.setText(container.getPath());
            QuickPreferences.setPathToContainer(container.getPath());
        }
    }

    @Override
    public void executeAction(ActionEvent event) {
        clearErrors();

        if(checkFields()) {
            try {

                PrimaryKeyManager keyManager = new PrimaryKeyManager();
                boolean passIsCorrect = keyManager.checkPassword(firstPassword.getText(), Paths.get(pathToKey.getText()));

                if(passIsCorrect) {
                    Stage stage = StageFactory.createContainerStage(getClass().getClassLoader());
                    stage.show();

                    QuickPreferences.setPathToContainer(pathToContainer.getText());
                    QuickPreferences.setPathToKey(pathToKey.getText());

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

        String pathKey = QuickPreferences.getPathToKey().toString();

        if(pathToKey != null)
            pathToKey.setText(pathKey);

        String pathContainer = QuickPreferences.getPathToContainer().toString();

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

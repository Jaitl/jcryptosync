package com.jcryptosync.controllers.login;

import com.jcryptosync.controllers.StageFactory;
import com.jcryptosync.controllers.LoginSceneFactory;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import com.jcryptosync.utils.PrimaryKeyUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
        File key = key = fileChooser.showOpenDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
        }
    }

    @Override
    protected void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");
        File container = fileChooser.showOpenDialog(null);

        if(container != null) {
            pathToContainer.setText(container.getPath());
        }
    }

    @Override
    public void executeAction(ActionEvent event) {
        clearErrors();

        if(checkFields()) {
            try {
                boolean passIsCorrect = PrimaryKeyUtils.checkPassword(firstPassword.getText(), Paths.get(pathToKey.getText()));

                if(passIsCorrect) {
                    Stage stage = StageFactory.createContainerStage(getClass().getClassLoader());
                    stage.show();

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
    }


    private void hideSecondPassword() {
        secondPasswordContainer.setManaged(false);
        secondPassword.setManaged(false);
        secondPassword.setVisible(false);
        secondPasswordLabel.setManaged(false);
        secondPasswordLabel.setVisible(false);
    }
}

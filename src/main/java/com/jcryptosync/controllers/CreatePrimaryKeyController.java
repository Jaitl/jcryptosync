package com.jcryptosync.controllers;

import com.jcryptosync.PrimaryKeyManager;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class CreatePrimaryKeyController extends BaseLoginController {


    @Override
    protected void changeControllerAction(ActionEvent event) {
        Node node=(Node) event.getSource();
        Stage stage=(Stage) node.getScene().getWindow();

        Scene scene = LoginSceneFactory.createLoginScene(getClass().getClassLoader());

        stage.setScene(scene);
    }

    @Override
    protected void selectKeyAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения ключа");
        File key = fileChooser.showSaveDialog(null);

        if(key != null) {
            pathToKey.setText(key.getPath());
        }
    }

    @Override
    protected void selectContainerAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор расположения контейнера");
        File container = fileChooser.showSaveDialog(null);

        if(container != null) {
            pathToContainer.setText(container.getPath());
        }
    }

    @Override
    protected void executeAction() {
        clearErrors();

        if(checkFields()) {
            try {
                PrimaryKeyManager.saveNewPrimaryKey(firstPassword.getText(), Paths.get(pathToKey.getText()));
            } catch (IOException e) {
                setError("Ошибка при сохранении ключа", pathToKey);
            }
        }
    }

    private boolean checkFields() {

        if(firstPassword.getText().trim().length() == 0) {
            setError("Поле с паролем не заполнено", firstPassword);
            return false;
        }

        if(!checkPassword(firstPassword.getText())) {
            setError("Пароль слишком простой", firstPassword);
            return false;
        }

        if(!firstPassword.getText().equals(secondPassword.getText())) {
            setError("Пароли не совпадают", secondPassword);
            return false;
        }

        if(pathToKey.getText().trim().length() == 0) {
            setError("Путь до ключа не выбран", pathToKey);
            return false;
        }

        if(pathToContainer.getText().trim().length() == 0) {
            setError("Путь контейнера не выбран", pathToContainer);
            return false;
        }

        return true;
    }

    private boolean checkPassword(String password) {
        return true;
    }

    @Override
    public void prepareDialog() {
        super.prepareDialog();

        showSecondPassword();
        createButton.setText("Отменить");
        enterButton.setText("Создать");
    }

    private void showSecondPassword() {
        secondPasswordContainer.setManaged(true);
        secondPassword.setManaged(true);
        secondPassword.setVisible(true);
        secondPasswordLabel.setManaged(true);
        secondPasswordLabel.setVisible(true);
    }
}

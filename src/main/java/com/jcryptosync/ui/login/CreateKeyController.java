package com.jcryptosync.ui.login;

import com.jcryptosync.data.MasterKeyManager;
import com.jcryptosync.domain.MainKey;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class CreateKeyController extends BaseLoginController {


    @Override
    protected void changeControllerAction(ActionEvent event) {
        Node node=(Node) event.getSource();
        Stage stage=(Stage) node.getScene().getWindow();

        Scene scene = LoginSceneFactory.createLoginScene(getClass().getClassLoader());

        stage.setScene(scene);
    }

    @Override
    protected MainKey computeMainKey() throws IOException, NoCorrectPasswordException {
        MasterKeyManager keyManager = new MasterKeyManager();
        keyManager.saveNewCryptKeyToFile(firstPassword.getText(), Paths.get(pathToKey.getText()));

        return keyManager.loadPrimaryKeyFromFile(firstPassword.getText(), Paths.get(pathToKey.getText()));
    }

    @Override
    protected boolean checkFields() {

        if(!super.checkFields())
            return false;

        if(!firstPassword.getText().equals(secondPassword.getText())) {
            setError("Пароли не совпадают", secondPassword);
            return false;
        }

        return true;
    }


    @Override
    public void prepareDialog() {
        super.prepareDialog();

        isNewContainer.setManaged(false);
        isNewContainer.setVisible(false);
        isNewContainer.setSelected(true);

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

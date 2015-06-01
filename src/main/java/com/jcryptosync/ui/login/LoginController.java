package com.jcryptosync.ui.login;

import com.jcryptosync.data.MasterKeyManager;
import com.jcryptosync.data.preferences.UserPreferences;
import com.jcryptosync.domain.MainKey;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    protected MainKey computeMainKey() throws IOException, NoCorrectPasswordException {
        MasterKeyManager keyManager = new MasterKeyManager();
        boolean passIsCorrect = keyManager.checkPassword(firstPassword.getText(), Paths.get(pathToKey.getText()));
        if(!passIsCorrect)
            throw new NoCorrectPasswordException("");

        return keyManager.loadPrimaryKeyFromFile(firstPassword.getText(), Paths.get(pathToKey.getText()));
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

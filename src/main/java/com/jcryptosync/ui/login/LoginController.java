package com.jcryptosync.ui.login;

import com.jcryptosync.data.MainKeyManager;
import com.jcryptosync.exceptoins.NoCorrectCompositeKeyException;
import com.jcryptosync.exceptoins.NoCorrectMasterKeyException;
import com.jcryptosync.preferences.UserPreferences;
import com.jcryptosync.exceptoins.NoCorrectPasswordException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mapdb.DB;
import org.mapdb.DBMaker;

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
    protected byte[] computeMainKey() throws IOException, NoCorrectPasswordException, NoCorrectCompositeKeyException, NoCorrectMasterKeyException {

        byte[] masterKey = new MainKeyManager().loadMainKey(firstPassword.getText(), Paths.get(pathToKey.getText()));

        byte[] compositeKey = MainKeyManager.computeCompositeKey(firstPassword.getText(), masterKey);

        try{
            DB db = DBMaker.newFileDB(new File(pathToContainer.getText())).encryptionEnable(compositeKey).make();
            db.close();
        } catch (Exception e) {
            throw new NoCorrectCompositeKeyException("");
        }

        return masterKey;
    }

    @Override
    public void prepareDialog() {
        super.prepareDialog();
        currentMode = Mode.Login;

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

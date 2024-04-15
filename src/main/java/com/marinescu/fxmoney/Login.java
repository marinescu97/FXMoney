package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * The class handles the logic behind the loginPage.fxml file.
 * It takes the data entered in the form.
 * If the entered data is correct, it will connect the user according to its type, otherwise an error message will be displayed.
 */

public class Login {
    /**
     * The TextField where the user enters their username or email.
     */
    @FXML
    private TextField userField;
    /**
     * The PasswordField where the user enters their password.
     */
    @FXML private PasswordField passwordField;
    /**
     * The Button that the user clicks to submit their login information.
     */
    @FXML private Button loginButton;
    /**
     * The Label used to display an error message.
     */
    @FXML private Text invalidMessage;

    /**
     * This method is called when the user clicks the “Login” button.
     * It checks the username (or email) and password and shows another window according to user type.
     */
    @FXML
    public void login() throws IOException {
        // Sets a style for the error message.
        String errorStyle = "-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;";

        // Checks if both fields or one of them is empty to show the error message.
        if (userField.getText().isEmpty() || passwordField.getText().isEmpty()){
            invalidMessage.setText("Fields required");
            if (userField.getText().isEmpty() && passwordField.getText().isEmpty()){
                userField.setStyle(errorStyle);
                passwordField.setStyle(errorStyle);
            } else {
                if (userField.getText().isEmpty()){
                    userField.setStyle(errorStyle);
                    passwordField.setStyle(null);
                }else if (passwordField.getText().isEmpty()){
                    passwordField.setStyle(errorStyle);
                    userField.setStyle(null);
                }
            }
        } else {
            // If data entered is correct, it closes the current window and open another window with user's information and rights.

            User user = DataSource.getInstance().queryUserLogin(userField.getText().trim(), passwordField.getText().trim());

            if (user!=null){
                Stage mainStage = (Stage) loginButton.getScene().getWindow();
                mainStage.close();

                DataHolder.getInstance().setMainUser(user);
                FXMLLoader fxmlLoader;
                Parent root;
                Scene scene;
                Stage stage;

                if (user.getType()==1 || user.getType()==2){
                    fxmlLoader = new FXMLLoader(getClass().getResource("bankerAdminPage.fxml"));
                } else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("atm.fxml"));
                }
                root = fxmlLoader.load();

                stage = new Stage();
                stage.initStyle(StageStyle.UNDECORATED);

                scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(false);

                stage.show();

                Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
            } else {
                // If data entered is invalid it will show an error message.

                invalidMessage.setText("Wrong username/email or password");
                userField.setStyle(errorStyle);
                passwordField.setStyle(errorStyle);
                userField.setText("");
                passwordField.setText("");
                userField.requestFocus();
            }
        }
    }
}

package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class handles the logic behind the userInfo.fxml file.
 */
public class UserInfo {
    /**
     * The labels that contain the information.
     */
    @FXML
    private Label usernameLbl, passwordLbl, accNameLbl, accIbanLbl, account, iban;
    /**
     * The button that closes the modal window.
     */
    @FXML
    private JFXButton okBtn;
    /**
     * The title of the modal window.
     */
    @FXML private Text title;
    /**
     * Subtitle of the modal window.
     */
    @FXML private Label subtitle;

    /**
     * This method is automatically called after the userInfo.fxml file has been loaded.
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param accName The name of the new account associated with the new user.
     * @param accIban The iban of the new account associated with the new user.
     * @param loan A boolean variable that checks if the modal window is for a loan request, or for adding a new user.
     */
    public void initialize(String username, String password, String accName, String accIban, boolean loan){
        usernameLbl.setText(username);
        passwordLbl.setText(password);

        if (loan){
            title.setText("Loan approved");
            subtitle.setText("Client information");
        } else if (accIban != null){
            title.setText("Client added successfully");
            subtitle.setText(null);
            accNameLbl.setText(accName);
            accIbanLbl.setText(accIban);
            account.setVisible(true);
            iban.setVisible(true);
        } else {
            title.setText("Banker added successfully");
            subtitle.setText(null);
        }

        // When the enter key, or the esc key, is pressed, the modal window will close.
        okBtn.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)){
                closeWindow();
            }
        });
    }

    /**
     * This closes the current window
     */
    @FXML
    public void closeWindow(){
        Stage stage = (Stage)usernameLbl.getScene().getWindow();
        stage.close();
    }
}

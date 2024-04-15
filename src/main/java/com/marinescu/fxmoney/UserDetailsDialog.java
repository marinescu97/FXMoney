package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

/**
 * This class handles the logic behind the userDetailsDialog.fxml file.
 * It displays information about a user.
 */
public class UserDetailsDialog {
    /**
     * Label containing information about the user.
     */
    @FXML
    private Label firstName, lastName, email, phoneNumber, pin, username, dateOfBirth, createdAt;
    /**
     * Label containing the title of the page.
     */
    @FXML
    private Label title;

    /**
     * This method is automatically called after the userDetailsDialog.fxml file has been loaded.
     * It sets the text of the labels with the user information.
     * @param user whose information is displayed.
     */
    public void initialize(User user){
        if (user.getType()==2){
            title.setText("Banker details");
        } else {
            title.setText("Client details");
        }

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        pin.setText(user.getPin());
        username.setText(user.getUsername());
        dateOfBirth.setText(new SimpleDateFormat("dd-MM-yyyy").format(user.getDateOfBirth()));
        createdAt.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(user.getCreatedAt()));
    }

    /**
     *  This method closes the current window
     * @param e The ActionEvent triggered by the button click.
     */
    @FXML public void closeStage(ActionEvent e){
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}

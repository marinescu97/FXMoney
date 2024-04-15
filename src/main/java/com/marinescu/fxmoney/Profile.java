package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

/**
 * This class handles the logic behind the profile.fxml file.
 */

public class Profile implements MyValidation, MyAlertInterface {
    /**
     * The layout container.
     */
    @FXML private VBox profile;

    /**
     * Ths button's container.
     */
    @FXML private HBox buttonsHBox;

    /**
     * Field corresponding to user information.
     */
    @FXML private TextField firstName, lastName, email, phoneNumber;

    /**
     * Field corresponding to user password.
     */
    @FXML private PasswordField password, confirmPassword;

    /**
     * The button that saves the user information.
     */
    @FXML private JFXButton saveButton;

    /**
     * The object that validates user information.
     */
    private final Validator validator = new Validator();

    /**
     * The current user.
     */
    private User currentUser;
    /**
     * This method is automatically called after the profile.fxml file has been loaded.
     * It initializes the container's elements and sets the fields with user's information.
     * @param user The user whose profile information needs to be changed
     */

    public void initialize(User user) {
        setCurrentUser(user);

        profile.setFocusTraversable(true);

        saveButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                saveProfile();
            }
        });

        for (Node node : firstName.getParent().getChildrenUnmodifiable()){
            if (node.isFocusTraversable()){
                node.setOnKeyPressed(e -> {
                    if (e.getCode().equals(KeyCode.ENTER)){
                        saveProfile();
                    }
                });
            }
        }

        firstName.textProperty().bindBidirectional(currentUser.firstNameProperty());
        lastName.textProperty().bindBidirectional(currentUser.lastNameProperty());

        email.setText(currentUser.getEmail());
        phoneNumber.setText(currentUser.getPhoneNumber());

        saveButton.setFocusTraversable(true);
    }


    /**
     * This method represents the functionality of the 'save' button.
     * This checks the validity of the entered information, and if they are valid, the new information is saved, otherwise an error message is displayed.
     */
    @FXML
    public void saveProfile(){
        validator.createCheck()
                .dependsOn("firstName", firstName.textProperty())
                .withMethod(c -> {
                    if (emptyField(firstName)) {
                        c.error("Required");
                    } else if (!validLength(firstName.getText(), 30)) {
                        c.error("Field should be maximum 30 length");
                    }
                })
                .decorates(firstName);

        validator.createCheck()
                .dependsOn("lastName", lastName.textProperty())
                .withMethod(c -> {
                    if (emptyField(lastName)) {
                        c.error("Required");
                    } else if (!validLength(lastName.getText(), 30)){
                        c.error("Field should be maximum 30 length");
                    }
                })
                .decorates(lastName);

        validator.createCheck()
                .dependsOn("email", email.textProperty())
                .withMethod(c -> {
                    if (!emptyField(email) && !validEmail(email.getText())) {
                        c.error("Invalid email");
                    } else if (!email.getText().trim().equals(currentUser.getEmail().trim()) && DataSource.getInstance().queryUserEmail(email.getText().trim()) > 0) {
                        c.error("Email already exists");
                    }
                })
                .decorates(email);

        validator.createCheck()
                .dependsOn("phoneNumber", phoneNumber.textProperty())
                .withMethod(c -> {
                    if (emptyField(phoneNumber)){
                        c.error("Required");
                    } else if (!validPhoneNumber(phoneNumber.getText())) {
                        c.error("Invalid phone number\n- it must contain a number of 10 digits");
                    } else if (!phoneNumber.getText().trim().equals(currentUser.getPhoneNumber()) && DataSource.getInstance().queryUserPhoneNumber(phoneNumber.getText()) > 0) {
                        c.error("Phone number already exists");
                    }
                })
                .decorates(phoneNumber);
        validator.createCheck()
                .dependsOn("password", password.textProperty())
                .withMethod(c -> {
                    if (!password.getText().trim().isEmpty() && !validPassword(password.getText().trim())) {
                        c.error("Please enter a strong password between [8-20] length");
                    }
                })
                .decorates(password);

        validator.createCheck()
                .dependsOn("confirmPassword", confirmPassword.textProperty())
                .withMethod(c -> {
                    if (!password.getText().trim().isEmpty() && !passwordMatch(password.getText().trim(), confirmPassword.getText().trim())) {
                        c.error("Passwords don't match");
                    }
                })
                .decorates(password)
                .decorates(confirmPassword);

        if (validator.validate()) {
            DataHolder.getInstance().getConfirmationDialog().showDialog();
            if (!emptyField(password)) {
                DataHolder.getInstance().getConfirmationDialog().okBtnHandler(event -> {
                    if (DataSource.getInstance().updateUser(currentUser.getId(), firstName.getText().trim(), lastName.getText().trim(), email.getText().trim(), phoneNumber.getText().trim(), password.getText().trim())) {
                        closeWindow();
                        DataHolder.getInstance().getConfirmationDialog().closeDialog();
                        showAlert("success", "Profile saved successfully");
                        password.clear();
                        confirmPassword.clear();
                    } else {
                        closeWindow();
                        DataHolder.getInstance().getConfirmationDialog().closeDialog();
                        showAlert("warning", "Something went wrong, please try again later!");
                    }
                });
            } else {
                DataHolder.getInstance().getConfirmationDialog().showDialog();
                DataHolder.getInstance().getConfirmationDialog().okBtnHandler(event -> {
                    if (DataSource.getInstance().updateUser(currentUser.getId(), firstName.getText().trim(), lastName.getText().trim(), email.getText().trim(), phoneNumber.getText().trim())) {
                        closeWindow();
                        DataHolder.getInstance().getConfirmationDialog().closeDialog();
                        showAlert("success", "Profile saved successfully");
                    } else {
                        closeWindow();
                        DataHolder.getInstance().getConfirmationDialog().closeDialog();
                        showAlert("warning", "Something went wrong, please try again later!");
                    }
                });
            }

            DataHolder.getInstance().getMainUser().setEmail(email.getText().trim());
            DataHolder.getInstance().getMainUser().setPhoneNumber(phoneNumber.getText().trim());

            email.setText(email.getText().trim());
            phoneNumber.setText(phoneNumber.getText().trim());
        }
    }

    /**
     * This method gets the first field from the page.
     * @return The field with first name.
     */
    public TextField getFirstName() {
        return firstName;
    }

    /**
     * This method sets the current user.
     * @param currentUser The current user.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * This method gets the button's container.
     * @return The button's container.
     */
    public HBox getButtonsHBox() {
        return buttonsHBox;
    }

    /**
     * This method closes the window if it is a modal window.
     */
    private void closeWindow(){
        Stage stage = (Stage) firstName.getScene().getWindow();
        if(stage.getModality() == Modality.APPLICATION_MODAL){
            stage.close();
        }
    }
}

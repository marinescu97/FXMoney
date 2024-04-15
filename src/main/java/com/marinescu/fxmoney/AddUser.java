package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Interfaces.MyRandomGenerator;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.sql.Date;

/**
 * This class handles the logic behind the addUser.fxml file.
 */

public class AddUser implements MyValidation, MyRandomGenerator, MyAlertInterface {
    /**
     * A container.
     */
    @FXML
    private VBox vBox;
    /**
     * Fields corresponding to user information.
     */
    @FXML
    private TextField firstName, lastName, email, phoneNumber, pin;
    @FXML private DatePicker dateOfBirth;

    /**
     * The button that adds the new user's information to the database.
     */
    @FXML private JFXButton addButton;

    /**
     * The object that validates user information.
     */
    private final Validator validator = new Validator();

    /**
     * This method is automatically called after the profile.fxml file has been loaded.
     * It initializes the container's elements.
     */
    public void initialize() {
        Label title = new Label();
        title.getStyleClass().add("title");
        if (DataHolder.getInstance().getMainUser().getId()==1 && DataHolder.getInstance().getUserType()==2){
            title.setText("Add a new banker");
        } else {
            title.setText("Add a new client");
        }

        VBox.setMargin(title, new Insets(20.0, 20.0, 30.0, 20.0));

        vBox.getChildren().add(0, title);

        firstName.requestFocus();

        addButton.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                addClient();
            }
        });

        // When the enter key is pressed, in any field, a new user is added.
        for (Node node : firstName.getParent().getChildrenUnmodifiable()){
            if (node.isFocusTraversable()){
                node.setOnKeyPressed(e -> {
                    if (e.getCode().equals(KeyCode.ENTER)){
                        addClient();
                    }
                });
            }
        }
    }

    /**
     * Handles the button click event.
     * Adds a new user's information and a new account attached to the user to the database.
     * If a new user was added successfully, it shows a modal with information about the user and the new account.
     */
    @FXML
    public void addClient(){
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
                    } else if (DataSource.getInstance().queryUserEmail(email.getText().trim()) > 0) {
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
                    } else if (!emptyField(phoneNumber) && DataSource.getInstance().queryUserPhoneNumber(phoneNumber.getText()) > 0) {
                        c.error("Phone number already exists");
                    }
                })
                .decorates(phoneNumber);

        validator.createCheck()
                .dependsOn("pin", pin.textProperty())
                .withMethod(c -> {
                    if (emptyField(pin)){
                        c.error("Required");
                    } else if (!validPin(pin.getText())) {
                        c.error("Please enter a valid pin");
                    } else if (!emptyField(pin) && DataSource.getInstance().queryUserPin(pin.getText()) > 0) {
                        c.error("Pin already exists");
                    }
                })
                .decorates(pin);

        validator.createCheck()
                .dependsOn("dateOfBirth", dateOfBirth.valueProperty())
                .withMethod(c -> {
                    if (emptyField(dateOfBirth)){
                        c.error("Required");
                    }else if (!validDateOfBirth(dateOfBirth.getValue())) {
                        c.error("""
                                    Invalid date of birth
                                    - minimum age requirement is 18
                                    """);
                    }
                })
                .decorates(dateOfBirth);

        if (validator.validate()){
            String username = randomUsername(firstName.getText().trim(), lastName.getText().trim(), pin.getText().trim());
            String password = randomPassword();
            String iban = null;
            String accName = null;
            int userId = 0;

            if (DataHolder.getInstance().getUserType()==3){
                iban = randomIban();
                accName = "Current";
                userId = DataSource.getInstance().insertUserAndAccount(firstName.getText().trim(), lastName.getText().trim(), email.getText().trim(), phoneNumber.getText().trim(),
                        pin.getText().trim(), Date.valueOf(dateOfBirth.getValue()), username, password, iban, DataHolder.getInstance().getUserType(), 1);
                if (DataHolder.getInstance().getMainUser().getId()==1){
                    DataHolder.getInstance().getUsersTableView().getItems().add(DataSource.getInstance().queryUserById(userId));
                }
            } else if (DataHolder.getInstance().getUserType()==2) {
                userId = DataSource.getInstance().insertUser(firstName.getText().trim(), lastName.getText().trim(), email.getText().trim(),
                        phoneNumber.getText().trim(), pin.getText().trim(), Date.valueOf(dateOfBirth.getValue()),
                        username, password, DataHolder.getInstance().getUserType(), 0);
                if (DataHolder.getInstance().getMainUser().getId()==1){
                    DataHolder.getInstance().getUsersTableView().getItems().add(DataSource.getInstance().queryUserById(userId));
                }
            }

            closeWindow();
            if (userId>0){
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userInfo.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    UserInfo userInfo = fxmlLoader.getController();
                    userInfo.initialize(username, password, accName, iban, false);

                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(
                            DataHolder.getInstance().getBorderPane().getScene().getWindow()

                    );
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.show();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                resetFields();
            } else {
                showAlert("Warning", "Something went wrong. Please try again later!");
            }
        }
    }

    /**
     * This clears all fields
     */
    private void resetFields(){
        firstName.clear();
        lastName.clear();
        email.clear();
        phoneNumber.clear();
        pin.clear();
        dateOfBirth.getEditor().clear();
    }

    /**
     * Gets the first field.
     * @return The first field.
     */
    public TextField getFirstName() {
        return firstName;
    }

    /**
     * This method closes the window if it is an application modal.
     */
    private void closeWindow(){
        Stage stage = (Stage) firstName.getScene().getWindow();
        if (stage.getModality() == Modality.APPLICATION_MODAL){
            stage.close();
        }
    }
}

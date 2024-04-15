package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Interfaces.ShowBalance;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class handles the logic behind the 'loanForm.fxml' file.
 */

public class LoanForm implements Initializable, MyValidation, ShowBalance {
    /**
     * The radio buttons' label.
     */
    @FXML
    private Label radioLabel;

    /**
     * Text field used for entering the required data.
     */
    @FXML private TextField desiredLoan, monthlyIncome, firstName, lastName, email, phoneNumber, pin;

    /**
     * Date picker used for entering the required data.
     */
    @FXML private DatePicker dateOfBirth;

    /**
     * A ToggleGroup used to group together a set of radio buttons.
     */
    @FXML private ToggleGroup used;

    /**
     * A button used to close the current window.
     */
    @FXML private JFXButton cancelBtn;

    /**
     * A ComboBox used to select an option from a list.
     */
    @FXML private ComboBox<String> months;

    /**
     * A list of numbers that represents the months numbers.
     */
    private final String[] monthsList = {"Months","6", "12", "18", "24", "36"};

    /**
     * An object that validates the entered data.
     */
    private final Validator validator = new Validator();

    /**
     * This method is automatically called after the loanForm.fxml file has been loaded.
     * If there is an authenticated client, it will automatically set some fields with the client information.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (DataHolder.getInstance().getUser()!=null){
            firstName.setText(DataHolder.getInstance().getUser().getFirstName());
            lastName.setText(DataHolder.getInstance().getUser().getLastName());
            email.setText(DataHolder.getInstance().getUser().getEmail());
            phoneNumber.setText(DataHolder.getInstance().getUser().getPhoneNumber());
            pin.setText(DataHolder.getInstance().getUser().getPin());
            dateOfBirth.setValue(DataHolder.getInstance().getUser().getDateOfBirth().toLocalDate());
        }

        months.getItems().addAll(FXCollections.observableArrayList(monthsList));
        months.getSelectionModel().selectFirst();

        desiredLoan.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.TAB)){
                monthlyIncome.requestFocus();
            }
        });
    }

    /**
     * This method closes the current window.
     */
    public void closeWindow(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * It sets an action for some keys.
     * @param event The event generated when a key is pressed.
     */
    public void keyCloseWindow(KeyEvent event){
        if (event.getCode().equals(KeyCode.ESCAPE)){
            closeWindow();
        } else if (event.getCode().equals(KeyCode.ENTER)) {
            submitBtnHandler();
        }
    }

    /**
     * This method handles the action of the 'Submit' button.
     * It checks the entered data, and if it is valid this will use the 'requestLoan' method, otherwise it will display an error message.
     */
    public void submitBtnHandler(){
        validator.createCheck()
                .dependsOn("desiredLoan", desiredLoan.textProperty())
                .withMethod(c -> {
                    if (emptyField(desiredLoan)) {
                        c.error("Required");
                    } else if (!validAmount(desiredLoan.getText().trim())) {
                        c.error("Please enter an amount");
                    }
                })
                .decorates(desiredLoan);
        validator.createCheck()
                .dependsOn("monthlyIncome", monthlyIncome.textProperty())
                .withMethod(c -> {
                    if (emptyField(monthlyIncome)) {
                        c.error("Required");
                    } else if (!validAmount(desiredLoan.getText().trim())) {
                        c.error("Please enter an amount");
                    }
                })
                .decorates(monthlyIncome);
        validator.createCheck()
                .dependsOn("months", months.valueProperty())
                .withMethod(c -> {
                    if (months.getSelectionModel().getSelectedItem()==null || Objects.equals(months.getSelectionModel().getSelectedItem(), "Months")) {
                        c.error("Required");
                    }
                })
                .decorates(months);
        validator.createCheck()
                .dependsOn("radioButtons", used.selectedToggleProperty())
                .withMethod(c -> {
                    if (used.getSelectedToggle() == null){
                        c.error("Required");
                    }
                })
                .decorates(radioLabel);
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
                    } else if (!emptyField(email) && DataSource.getInstance().queryUserEmail(email.getText()) != 0 && DataHolder.getInstance().getUser()==null) {
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
                    } else if (DataSource.getInstance().queryUserPhoneNumber(phoneNumber.getText()) != 0 & DataHolder.getInstance().getUser()==null) {
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
                    } else if (DataSource.getInstance().queryUserPin(pin.getText()) != 0 && DataHolder.getInstance().getUser()==null) {
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
            if (Double.parseDouble(used.getSelectedToggle().getUserData().toString()) < Double.parseDouble(desiredLoan.getText().trim())){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("The maximum loan amount is " + used.getSelectedToggle().getUserData().toString() + ". Are you agree?");
                alert.showAndWait()
                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> {
                            desiredLoan.setText(used.getSelectedToggle().getUserData().toString());
                            requestLoan(Double.parseDouble(used.getSelectedToggle().getUserData().toString()));
                        });
            } else {
                requestLoan(Double.parseDouble(desiredLoan.getText().trim()));
            }
        }
    }

    /**
     * This method calculates the monthly payment and the total payment for a given loan.
     * It checks if the client is eligible for loan.
     * Then redirects the user to the loan summary.
     * @param loan The loan amount.
     */
    private void requestLoan(double loan){
        double interestRatePerMonth = 0.03;
        double monthlyPayment = (loan * interestRatePerMonth * Math.pow(1 + interestRatePerMonth, Integer.parseInt(months.getSelectionModel().getSelectedItem()))) / (Math.pow(1 + interestRatePerMonth, Integer.parseInt(months.getSelectionModel().getSelectedItem())) - 1);
        double minSalary = round(0.43 * monthlyPayment, 2);
        if (DataHolder.getInstance().getUser()==null || DataHolder.getInstance().getUser().getLoanEligibility()==1){
            if (DataHolder.getInstance().getUser()==null){
                User newCustomer = new User();
                newCustomer.setFirstName(firstName.getText().trim());
                newCustomer.setLastName(lastName.getText().trim());
                newCustomer.setEmail(email.getText().trim());
                newCustomer.setPhoneNumber(phoneNumber.getText().trim());
                newCustomer.setPin(pin.getText().trim());
                newCustomer.setDateOfBirth(Date.valueOf(dateOfBirth.getValue()));
                DataHolder.getInstance().setUser(newCustomer);
            }
            if (Double.parseDouble(monthlyIncome.getText().trim())<minSalary){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Minimum salary should be $" + minSalary + "!");
                alert.show();
            } else {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loanSummary.fxml"));
                    Parent root = fxmlLoader.load();
                    LoanSummary loanSummary = fxmlLoader.getController();
                    loanSummary.initialize(Double.parseDouble(desiredLoan.getText().trim()), Integer.parseInt(months.getSelectionModel().getSelectedItem()), round(monthlyPayment, 2));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));

                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());
                    closeWindow();
                    stage.show();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Client is not eligible for any loan!");
            alert.show();
        }
    }
}

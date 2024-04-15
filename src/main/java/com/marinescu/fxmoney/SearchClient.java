package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import net.synedra.validatorfx.Validator;

import java.io.IOException;

/**
 * This class handles the logic behind the searchClient.fxml file.
 */
public class SearchClient implements MyAlertInterface, MyValidation {
    /**
     * The field for entering the client's PIN.
     * The only field in the form.
     */
    @FXML
    private TextField pinField;
    /**
     * The main window.
     */
    private final BorderPane bankerAdminBorderPane = DataHolder.getInstance().getBorderPane();
    /**
     * The object for validating the data from the field.
     */
    private final Validator validator = new Validator();

    /**
     * This method is automatically called after the searchClient.fxml file has been loaded.
     * It allows, when pressing the enter key on the field, to perform the search.
     */
    public void initialize() {
        DataHolder.getInstance().setCurrentContainerId("searchClient");
        pinField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){
                searchBtnHandler();
            }
        });
    }

    /**
     * This method performs the functionality of the search button.
     * It checks if the entered information is valid, and looks for the client and for his accounts in the database.
     * If they exist, the existing window is changed to the client's profile window.
     * If a client doesn't exist, it will show a message.
     */
    @FXML
    public void searchBtnHandler() {
        validator.createCheck()
                .dependsOn("pin", pinField.textProperty())
                .withMethod(c -> {
                    if (emptyField(pinField)){
                        c.error("Required");
                    } else if (!validPin(pinField.getText())) {
                        c.error("Please enter a valid pin");
                    }
                })
                .decorates(pinField);

        if (validator.validate()){
            User user = DataSource.getInstance().queryUserByPin(pinField.getText().trim());

            if (user!=null){
                DataHolder.getInstance().setUser(user);
                DataHolder.getInstance().setAccount(DataSource.getInstance().queryAccount(user.getId()));
                DataHolder.getInstance().setAccounts(user.getId());

                try {
                    FXMLLoader clientProfileLoader = new FXMLLoader(getClass().getResource("clientProfile.fxml"));
                    Parent root = clientProfileLoader.load();

                    bankerAdminBorderPane.setPrefHeight(550.0);
                    bankerAdminBorderPane.setPrefWidth(700.0);
                    bankerAdminBorderPane.setCenter(root);
                    DataHolder.getInstance().setCurrentContainerId("clientProfile");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.NONE, "Client not found", ButtonType.OK);
                customAlert(alert);
                alert.show();
                pinField.clear();
            }
        }
    }

    /**
     * It gets the field from the form.
     * @return The field.
     */
    public TextField getPinField() {
        return pinField;
    }
}

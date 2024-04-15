package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.LoanInterface;
import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Interfaces.ShowBalance;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * This class handles the logic behind the 'loanInfo.fxml' file.
 */
public class LoanInfo implements Initializable, ShowBalance, LoanInterface, MyValidation, MyAlertInterface {
    /**
     * A {@link Label} with loan information.
     */
    @FXML private Label startB, remainingB, monthlyPay, dueDate, penaltyLbl;
    /**
     * A {@link JFXButton} that closes the current page.
     */
    @FXML private JFXButton okBtn;

    /**
     * A {@link TextField} where the payment amount is entered.
     */
    @FXML private TextField amountField;
    /**
     * An {@link Validator} object for validating entered data.
     */
    private final Validator validator = new Validator();

    /**
     * This method is automatically called after the loanInfo.fxml file has been loaded.
     * It sets the labels with proper information.
     * It also sets the field with monthly payment amount.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startB.textProperty().bind(Bindings.concat("$", DataHolder.getInstance().getLoan().startBalanceProperty()));
        remainingB.textProperty().bind(Bindings.concat("$", DataHolder.getInstance().getLoan().remainingBalanceProperty()));
        monthlyPay.textProperty().bind(Bindings.concat("$", DataHolder.getInstance().getLoan().currentPaymentProperty()));
        penaltyLbl.textProperty().bind(Bindings.concat("$", DataHolder.getInstance().getLoan().penaltyProperty()));
        amountField.setText(String.valueOf(DataHolder.getInstance().getLoan().getCurrentPayment()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = sdf.parse(DataHolder.getInstance().getLoan().getDueDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = outputFormat.format(date);
            dueDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * The method performs a loan payment.
     */
    @FXML public void paymentHandler(){
        validator.createCheck()
                .dependsOn("amountField", amountField.textProperty())
                .withMethod(c -> {
                    if (emptyField(amountField)) {
                        c.error("Required");
                    } else if (!validAmount(amountField.getText())) {
                        c.error("Please enter a valid amount!");
                    } else if (Double.parseDouble(amountField.getText())<=0){
                        c.error("Please enter positive values!");
                    } else if (DataHolder.getInstance().getLoan().getRemainingBalance() < getEMI(DataHolder.getInstance().getLoan().getStartBalance(), DataHolder.getInstance().getLoan().getMonthsNumber()) && Double.parseDouble(amountField.getText()) > DataHolder.getInstance().getLoan().getCurrentPayment()) {
                        c.error("Maximum amount is $" + DataHolder.getInstance().getLoan().getCurrentPayment());
                    }
                })
                .decorates(amountField);

        if (validator.validate()){
            double amount = round(Double.parseDouble(amountField.getText().trim()), 2);
            double remainingBalance = round(getRemainingBalance(DataHolder.getInstance().getLoan().getRemainingBalance(), amount), 2);
            String currentPaymentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            double currentPayment;

            if (amount >= DataHolder.getInstance().getLoan().getCurrentPayment()){
                currentPayment = 0;
            } else {
                currentPayment = round(DataHolder.getInstance().getLoan().getCurrentPayment() - amount, 2);
            }

            if (DataSource.getInstance().loanTransaction(DataHolder.getInstance().getLoan().getClient(), remainingBalance, currentPayment, amount)){
                DataHolder.getInstance().getLoan().setRemainingBalance(remainingBalance);
                DataHolder.getInstance().getLoan().setCurrentPayment(currentPayment);
                DataHolder.getInstance().getLoan().setLastPaymentDate(currentPaymentDate);
                amountField.setText(String.valueOf(currentPayment));

                DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                DataHolder.getInstance().getTransactionsListView().setItems(DataHolder.getInstance().getTransactions());

                showAlert("success", "Transaction performed!");
            } else {
                showAlert("warning", "Something went wrong. Please try again later!");
            }
        }
    }

    /**
     * This method closes the current window.
     */
    @FXML public void closeWindow(){
        Stage stage = (Stage) okBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * This method handles a key press action.
     * @param e The generated event when a key is pressed.
     */
    @FXML public void keyPressHandle(KeyEvent e){
        switch (e.getCode()){
            case ESCAPE -> closeWindow();
            case ENTER -> paymentHandler();
        }
    }
}

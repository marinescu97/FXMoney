package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.LoanInterface;
import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Interfaces.MyRandomGenerator;
import com.marinescu.fxmoney.Interfaces.ShowBalance;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.Loan;
import com.marinescu.fxmoney.Model.PDFHolder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * This class handles the logic behind the 'loanSummary.fxml' file.
 */
public class LoanSummary implements ShowBalance, MyRandomGenerator, LoanInterface, MyAlertInterface {
    /**
     * Label for showing information about a loan.
     */
    @FXML
    private Label loanBalance, monthlyPayment, monthsNumber, totalAmount;
    /**
     * Button for closing the current window.
     */
    @FXML
    private JFXButton cancelBtn;

    /**
     * Button for opening a PDF with detailed information about the loan.
     */
    @FXML JFXButton printBtn;


    /**
     * This method is automatically called after the loanSummary.fxml file has been loaded.
     * It sets the labels with the given data.
     * It also sets the PDF data and sets the action for the 'Print' button.
     * @param loanBalance The loan amount.
     * @param monthsNumber The number of the months for repayment.
     * @param monthlyPayment Monthly payment amount.
     */
    public void initialize(Double loanBalance, int monthsNumber, Double monthlyPayment){
        setLabels(loanBalance, monthlyPayment, monthsNumber, round(monthlyPayment*monthsNumber, 2));
        PDFHolder.getInstance().setAmount(loanBalance);
        PDFHolder.getInstance().setMonths(monthsNumber);
        printBtn.setOnAction(e -> PDFHolder.getInstance().createPDF());
    }

    /**
     * This method performs the lending action.
     * After successfully granting the loan, it displays successfully message.
     * It also creates a new client account, if there is no registered client.
     */
    public void giveLoan(){
        closeWindow();
        if (DataSource.getInstance().queryAccountByIban("0406 4131 1928 1551").getBalance() >= round(PDFHolder.getInstance().getAmount(),2)){
            if (DataHolder.getInstance().getUser().getId()>0){
                if (DataSource.getInstance().insertLoan(DataHolder.getInstance().getUser().getId(), round(PDFHolder.getInstance().getAmount(), 2), PDFHolder.getInstance().getMonths(), round(getEMI(PDFHolder.getInstance().getAmount(), PDFHolder.getInstance().getMonths()), 2)) > 0){
                    showAlert("success", "Loan approved!");
                    Loan loan = DataSource.getInstance().queryLoan(DataHolder.getInstance().getUser().getId());
                    if (loan != null){
                        DataHolder.getInstance().setLoan(loan);
                        MenuItem item = new MenuItem("Loan info");
                        if (DataHolder.getInstance().getMenuButton()!=null){
                            DataHolder.getInstance().getMenuButton().getItems().add(1, item);

                            DataHolder.getInstance().getMenuButton().getItems().removeIf(menuItem -> menuItem.getText().equals("Request a loan"));

                            item.setOnAction(e -> {
                                try {
                                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loanInfo.fxml"));
                                    Parent root = fxmlLoader.load();
                                    Stage stage = new Stage();
                                    stage.setScene(new Scene(root));

                                    stage.initModality(Modality.APPLICATION_MODAL);
                                    stage.initStyle(StageStyle.UNDECORATED);
                                    stage.show();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                        }

                        double newBalance = DataSource.getInstance().queryBalanceByAccount(DataHolder.getInstance().getAccount().getId());
                        if (newBalance > 0){
                            DataHolder.getInstance().getAccount().setBalance(newBalance);
                        }

                        if (DataHolder.getInstance().getCurrentContainerId().equals("clientProfile")){
                            DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                            DataHolder.getInstance().getTransactionsListView().setItems(DataHolder.getInstance().getTransactions());
                        }
                    }
                } else {
                    showAlert("warning", "Something went wrong, please try again later");
                }
            } else {
                String username = randomUsername(DataHolder.getInstance().getUser().getFirstName(), DataHolder.getInstance().getUser().getLastName(), DataHolder.getInstance().getUser().getPin());
                String password = randomPassword();
                String iban = randomIban();
                if (DataSource.getInstance().insertLoan(round(PDFHolder.getInstance().getAmount(), 2),
                        PDFHolder.getInstance().getMonths(), DataHolder.getInstance().getUser().getFirstName(),
                        DataHolder.getInstance().getUser().getLastName(), DataHolder.getInstance().getUser().getEmail(),
                        DataHolder.getInstance().getUser().getPhoneNumber(), DataHolder.getInstance().getUser().getPin(),
                        DataHolder.getInstance().getUser().getDateOfBirth(), username, password, iban, round(getEMI(PDFHolder.getInstance().getAmount(), PDFHolder.getInstance().getMonths()), 2), 0) > 0){
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userInfo.fxml"));
                        Parent root = fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        UserInfo userInfo = fxmlLoader.getController();
                        userInfo.initialize(username, password, "Current", iban, true);

                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.show();
                        DataHolder.getInstance().setUser(null);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    showAlert("warning", "Something went wrong, please try again later");
                }
            }
        } else {
            showAlert("warning", "Something went wrong, please try again later!");
        }
    }

    /**
     * This method closes the current window.
     */
    public void closeWindow(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * It performs actions for some keys.
     * @param event The event generated when a key is pressed.
     */
    public void keyCloseWindow(KeyEvent event){
        if (event.getCode().equals(KeyCode.ESCAPE)){
            closeWindow();
        } else if (event.getCode().equals(KeyCode.P)) {
            System.out.println("Print");
        } else if (event.getCode().equals(KeyCode.ENTER)) {
            System.out.println("Give loan");
        }
    }

    /**
     * This method sets the labels with the given data.
     * @param loanBalance The loan amount.
     * @param monthlyPayment Monthly payment amount.
     * @param monthsNumber The number of months for repayment.
     * @param totalAmount The total repayment amount.
     */
    private void setLabels(Double loanBalance, Double monthlyPayment, int monthsNumber, Double totalAmount){
        this.loanBalance.setText(loanBalance.toString());
        this.monthlyPayment.setText(monthlyPayment.toString());
        this.monthsNumber.setText(String.valueOf(monthsNumber));
        this.totalAmount.setText(totalAmount.toString());
    }
}

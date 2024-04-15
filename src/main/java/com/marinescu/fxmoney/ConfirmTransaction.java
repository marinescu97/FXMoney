package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Model.Account;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class handles the logic behind the confirmTransaction.fxml file.
 */
public class ConfirmTransaction implements MyAlertInterface, Initializable {
    /**
     * The label containing data about the receiving client and his account.
     */
    @FXML
    private Label nameLbl, ibanLbl, amountLbl;
    /**
     * Button that closes the current window.
     */
    @FXML
    private JFXButton cancelBtn;
    /**
     * Button that performs the new transaction.
     */
    @FXML private JFXButton okBtn;

    /**
     * This method is automatically called after the confirmTransaction.fxml file has been loaded.
     * Sets the text of the labels with the corresponding data.
     * It also assigns the performing of the transaction to the "ok" button.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Account account = DataHolder.getInstance().getRecipientAccount();
        double amount = DataHolder.getInstance().getTransactionAmount();
        nameLbl.setText(DataSource.getInstance().queryUserNameByAccount(account.getClient()));
        ibanLbl.setText(account.getIban());
        amountLbl.setText("$"+amount);

        okBtn.setOnAction(e -> {
            if (DataSource.getInstance().performTransaction(DataHolder.getInstance().getAccount().getId(), account.getId(), amount)){
                DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                DataHolder.getInstance().getTransactionsListView().setItems(DataHolder.getInstance().getTransactions());
                closeWindow();
                showAlert("success", "Transaction performed");
                DataHolder.getInstance().getAccount().setBalance(DataHolder.getInstance().getAccount().getBalance()-amount);
                for (Account acc : DataHolder.getInstance().getAccounts()){
                    if (acc.getId() == account.getId()){
                        acc.setBalance(acc.getBalance()+amount);
                    }
                }
            } else {
                closeWindow();
                showAlert("warning", "Something went wrong, please try again later!");
            }
        });
    }

    /**
     * Closes the current window.
     */
    @FXML
    public void closeWindow(){
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}

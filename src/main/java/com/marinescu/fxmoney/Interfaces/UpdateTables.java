package com.marinescu.fxmoney.Interfaces;

import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.Loan;
import javafx.application.Platform;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This interface helps to update the database's tables when the application starts.
 */
public interface UpdateTables extends LoanInterface {
    /**
     * This method updates some tables from the database.
     */
    default void updateAllTables(){
        List<String> tables = DataSource.getInstance().queryTables();
        for (String table : tables){
            updateId(table);
            DataSource.getInstance().updateAutoIncrement(table);
        }

    }

    /**
     * This method renumbers the ID column of a table and updates all foreign keys associated with it.
     * @param table The table that has to be updated.
     */
    default void updateId(String table){
        List<Integer> id = DataSource.getInstance().queryId(table);
        int index = 1;
        for (int currentId : id){
            DataSource.getInstance().updateTableIds(table, index, currentId);
            index++;
        }
    }

    /**
     * This method updates the “loans” table from the database.
     * Every time the application is started, the payment information of each loan will be updated, if necessary.
     */
    default void updateLoans(){
        List<Loan> loans = DataSource.getInstance().queryLoans();
        if (loans==null){
            Platform.exit();
        } else {
            double remainingBalance;
            double currentPayment;
            double penalty;

            String loanDate;
            String lastPaymentDate;
            String dueDate;
            String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            for (Loan loan : loans){
                if (loan.getRemainingBalance() <= 0){
                    if (!DataSource.getInstance().deleteLoanAndSetUser(loan.getId())){
                        Platform.exit();
                    }
                } else {
                    remainingBalance = loan.getRemainingBalance();
                    currentPayment = loan.getCurrentPayment();
                    penalty = loan.getPenalty();

                    loanDate = loan.getDateApproved();
                    lastPaymentDate = loan.getLastPaymentDate();
                    dueDate = loan.getDueDate();
                    if (!dateBetween(loanDate, dueDate, now) && remainingBalance>0){
                        if (showMonthsDifference(dueDate, now) == 0 || now.equals(dueDate(dueDate, dueDate))){
                            if (currentPayment>0){
                                penalty += 50;
                                currentPayment+=50;
                                currentPayment+=getEMI(loan.getStartBalance(), loan.getMonthsNumber());
                                remainingBalance += 50;
                            } else if (currentPayment == 0) {
                                currentPayment+=getEMI(loan.getStartBalance(), loan.getMonthsNumber());
                            }
                            dueDate = dueDate(now, loanDate);
                        } else if (showMonthsDifference(dueDate, now) > 0){
                            double newCurrentPayment = currentPayment;
                            int monthsDiff = showMonthsDifference(lastPaymentDate, now);
                            if (!now.equals(dueDate(now, dueDate))){
                                monthsDiff++;
                            }
                            for (int i = 0; i<monthsDiff; i++){
                                penalty+=50;
                                newCurrentPayment+=50;
                                remainingBalance+=50;
                            }
                            if (currentPayment==0){
                                newCurrentPayment-=50;
                                remainingBalance-=50;
                            }
                            dueDate = dueDate(now, loanDate);
                            currentPayment = newCurrentPayment;
                        }
                    }
                    if (!DataSource.getInstance().updateLoan(round(remainingBalance,2), round(currentPayment,2), penalty, dueDate)){
                        Platform.exit();
                    }
                }
            }
        }
    }
}

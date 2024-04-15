package com.marinescu.fxmoney.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class represents a loan requested by a client.
 */
public class Loan {
    private SimpleIntegerProperty id, client, monthsNumber, penalty;
    private SimpleDoubleProperty startBalance, remainingBalance, currentPayment;
    private SimpleStringProperty dateApproved, lastPaymentDate, dueDate;

    /**
     * This method is the class' controller.
     * It creates a new loan with its attributes.
     * @param id The loan's id.
     * @param client The client who requested the loan.
     * @param startBalance The requested amount.
     * @param remainingBalance The amount left to pay.
     * @param currentPayment The payment amount in the current month.
     * @param penalty The sum of penalties.
     * @param monthsNumber Loan repayment period in months.
     * @param dateApproved The date the loan was accepted.
     * @param lastPaymentDate The date on which the last payment was made.
     * @param dueDate The date until which the loan can be returned.
     */

    public Loan(SimpleIntegerProperty id, SimpleIntegerProperty client, SimpleDoubleProperty startBalance, SimpleDoubleProperty remainingBalance, SimpleDoubleProperty currentPayment,
                SimpleIntegerProperty penalty, SimpleIntegerProperty monthsNumber, SimpleStringProperty dateApproved, SimpleStringProperty lastPaymentDate, SimpleStringProperty dueDate) {
        this.id = id;
        this.client = client;
        this.monthsNumber = monthsNumber;
        this.startBalance = startBalance;
        this.remainingBalance = remainingBalance;
        this.currentPayment = currentPayment;
        this.penalty = penalty;
        this.dateApproved = dateApproved;
        this.lastPaymentDate = lastPaymentDate;
        this.dueDate = dueDate;
    }

    /**
     * Gets the loan's id.
     * @return The loan's id.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Sets the loan's id.
     * @param id The new id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Gets the client associated with the loan.
     * @return The client.
     */
    public int getClient() {
        return client.get();
    }

    /**
     * Sets the loan's client's id.
     * @param client The new client's id.
     */
    public void setClient(int client) {
        this.client.set(client);
    }

    /**
     * Gets the loan repayment period in months.
     * @return The loan repayment period in months.
     */
    public int getMonthsNumber() {
        return monthsNumber.get();
    }

    /**
     * Gets the loan's start balance.
     * @return The start balance.
     */
    public double getStartBalance() {
        return startBalance.get();
    }

    /**
     * Gets the loan's start balance property.
     * @return The start balance property.
     */
    public SimpleDoubleProperty startBalanceProperty() {
        return startBalance;
    }

    /**
     * Gets the loan's remaining balance.
     * @return The loan's remaining balance.
     */
    public double getRemainingBalance() {
        return remainingBalance.get();
    }

    /**
     * Gets the loan's remaining balance property.
     * @return The loan's remaining balance property.
     */
    public SimpleDoubleProperty remainingBalanceProperty() {
        return remainingBalance;
    }

    /**
     * Sets the loan's remaining balance.
     * @param remainingBalance The new remaining balance.
     */
    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance.set(remainingBalance);
    }

    /**
     * Gets the payment amount in the current month.
     * @return The payment amount.
     */
    public double getCurrentPayment() {
        return currentPayment.get();
    }

    /**
     * Gets the payment amount property.
     * @return The payment property.
     */
    public SimpleDoubleProperty currentPaymentProperty() {
        return currentPayment;
    }

    /**
     * Sets the payment amount for the next month.
     * @param currentPayment The new payment amount.
     */
    public void setCurrentPayment(double currentPayment) {
        this.currentPayment.set(currentPayment);
    }

    /**
     * Gets the penalty.
     * @return penalty
     */
    public int getPenalty() {
        return penalty.get();
    }

    /**
     * Gets the penalty property.
     * @return The penalty property.
     */
    public SimpleIntegerProperty penaltyProperty() {
        return penalty;
    }

    /**
     * Gets the date when the loan was approved.
     * @return The date when the loan was approved.
     */
    public String getDateApproved() {
        return dateApproved.get();
    }

    /**
     * Gets the date when the last payment was made.
     * @return The last payment date.
     */
    public String getLastPaymentDate() {
        return lastPaymentDate.get();
    }

    /**
     * Sets the last payment's date.
     * @param lastPaymentDate The new date.
     */
    public void setLastPaymentDate(String lastPaymentDate) {
        this.lastPaymentDate.set(lastPaymentDate);
    }

    /**
     * Gets the date until which the loan can be returned.
     * @return The due date.
     */
    public String getDueDate() {
        return dueDate.get();
    }
}

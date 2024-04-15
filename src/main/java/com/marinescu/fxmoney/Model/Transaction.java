package com.marinescu.fxmoney.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Timestamp;

/**
 * This class represents a transaction.
 */
public class Transaction {
    private SimpleIntegerProperty id;
    private SimpleStringProperty iban, client, amount;
    private SimpleObjectProperty<Timestamp> transactionDate;

    /**
     * It creates a new transaction.
     */
    public Transaction() {
        this.id = new SimpleIntegerProperty();
        this.iban = new SimpleStringProperty();
        this.client = new SimpleStringProperty();
        this.amount = new SimpleStringProperty();
        this.transactionDate = new SimpleObjectProperty<>();
    }

    /**
     * Gets the transaction's id.
     * @return The transaction's id.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Sets the transaction's id.
     * @param id The new id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Gets the recipient or sender iban.
     * @return iban
     */
    public String getIban() {
        return iban.get();
    }

    /**
     * Sets the recipient or sender iban.
     * @param iban The new iban.
     */
    public void setIban(String iban) {
        this.iban.set(iban);
    }

    /**
     * Gets the recipient or sender client's name.
     * @return The client's name.
     */
    public String getClient() {
        return client.get();
    }

    /**
     * Sets the recipient or sender client's name.
     * @param client The new client's name.
     */
    public void setClient(String client) {
        this.client.set(client);
    }

    /**
     * Gets the transaction amount.
     * @return The transaction amount
     */
    public String getAmount() {
        return amount.get();
    }

    /**
     * Sets the transaction amount.
     * @param amount The new amount
     */
    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    /**
     * Gets the date when the transaction was performed.
     * @return The date.
     */
    public Timestamp getTransactionDate() {
        return transactionDate.get();
    }

    /**
     * Sets the date when the transaction was performed.
     * @param transactionDate The new transaction date.
     */
    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate.set(transactionDate);
    }

    /**
     * This method displays a transaction.
     * @return The way a transaction is displayed.
     */
    @Override
    public String toString() {
        return String.format("""
                %s
                %s      %s
                """, getClient(), getIban(), getAmount());
    }
}

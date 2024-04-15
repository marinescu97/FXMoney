package com.marinescu.fxmoney.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class represents an account owned by a user.
 */
public class Account {
    private final SimpleIntegerProperty id, client;
    private final SimpleStringProperty type, iban;
    private final SimpleDoubleProperty balance;

    /**
     * Creates a new account object with its attributes
     * @param id account's id
     * @param type account's type
     * @param iban account's iban
     * @param balance account's balance
     * @param client account's owner
     */
    public Account(int id, String type, String iban, double balance, int client) {
        this.id = new SimpleIntegerProperty(id);
        this.type = new SimpleStringProperty(type);
        this.iban = new SimpleStringProperty(iban);
        this.balance = new SimpleDoubleProperty(balance);
        this.client = new SimpleIntegerProperty(client);
    }

    /**
     * Gets the account's id.
     * @return The account's id.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Sets the account's id.
     * @param id the new id
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Gets the account's owner.
     * @return owner's id
     */
    public int getClient() {
        return client.get();
    }

    /**
     * Sets the account's owner.
     * @param client the owner's id
     */
    public void setClient(int client) {
        this.client.set(client);
    }

    /**
     * Gets the account's type.
     * @return account's type
     */
    public String getType() {
        return type.get();
    }

    /**
     * Sets the account's type.
     * @param type the new type
     */
    public void setType(String type) {
        this.type.set(type);
    }

    /**
     * Gets the account's iban.
     * @return The account's iban
     */
    public String getIban() {
        return iban.get();
    }

    /**
     * Gets the account's iban property.
     * @return iban property
     */
    public SimpleStringProperty ibanProperty() {
        return iban;
    }

    /**
     * Sets the account's iban.
     * @param iban the new iban
     */
    public void setIban(String iban) {
        this.iban.set(iban);
    }

    /**
     * Gets the account's balance.
     * @return The account's balance.
     */
    public double getBalance() {
        return balance.get();
    }

    /**
     * Gets the account's balance property.
     * @return The account's balance property
     */
    public SimpleDoubleProperty balanceProperty() {
        return balance;
    }

    /**
     * Sets the account's balance.
     * @param balance the new balance
     */
    public void setBalance(double balance) {
        this.balance.set(balance);
    }

    /**
     * Displays an account.
     * @return The way an account is displayed.
     */
    @Override
    public String toString() {
        if (getId()==0){
            return "Select account";
        }
        return getType();
    }
}

package com.marinescu.fxmoney.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Timestamp;
import java.sql.Date;

/**
 * This class represents a user with id, first name, last name, email, phone number,
 * date of birth, pin, username, password, type, loan eligibility and date it was created.
 */
public class User {
    private final SimpleIntegerProperty id, type, loanEligibility;
    private final SimpleStringProperty firstName, lastName, email, phoneNumber, pin, username, password;
    private final SimpleObjectProperty<Date> dateOfBirth;
    private final SimpleObjectProperty<Timestamp> createdAt;

    /**
     * Creates a new user object with its attributes.
     */
    public User() {
        this.id = new SimpleIntegerProperty();
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.pin = new SimpleStringProperty();
        this.dateOfBirth = new SimpleObjectProperty<>();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.type = new SimpleIntegerProperty();
        this.loanEligibility = new SimpleIntegerProperty();
        this.createdAt = new SimpleObjectProperty<>();
    }

    /**
     * Creates a new user object with its attributes.
     * @param id The id of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email of the user.
     * @param phoneNumber The phone number of the user.
     * @param pin The pin of the user.
     * @param dateOfBirth User's date of birth.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param type The type of the user.
     * @param loanEligibility User's loan eligibility.
     * @param createdAt The date and time when the user was created.
     */
    public User(int id, String firstName, String lastName, String email, String phoneNumber, String pin, Date dateOfBirth,
                String username, String password, int type, int loanEligibility, Timestamp createdAt) {
        this.id = new SimpleIntegerProperty(id);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.pin = new SimpleStringProperty(pin);
        this.dateOfBirth = new SimpleObjectProperty<>(dateOfBirth);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.type = new SimpleIntegerProperty(type);
        this.loanEligibility = new SimpleIntegerProperty(loanEligibility);
        this.createdAt = new SimpleObjectProperty<>(createdAt);
    }

    /**
     * Gets the id of the user.
     * @return The id of the user.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Sets the id of the user.
     * @param id The new id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Gets the type of the user.
     * @return The user's type.
     */
    public int getType() {
        return type.get();
    }

    /**
     * Sets the type of the user.
     * @param type The new type.
     */
    public void setType(int type) {
        this.type.set(type);
    }

    /**
     * Gets the first name of the user.
     * @return first name
     */
    public String getFirstName() {
        return firstName.get();
    }

    /**
     * Gets the first name property of the user.
     * @return first name property
     */
    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    /**
     * Sets the first name of the user.
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    /**
     * Gets the last name of the user.
     * @return last name
     */
    public String getLastName() {
        return lastName.get();
    }

    /**
     * Gets the last name property of the user.
     * @return last name property
     */
    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    /**
     * Sets the last name of the user.
     * @param lastName The new last name
     */
    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    /**
     * Gets the email of the user.
     * @return The user's email.
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Sets the email of the user.
     * @param email The new email.
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Gets the phone number of the user.
     * @return The user's phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    /**
     * Sets the phone number of the user.
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    /**
     * Gets the pin of the user.
     * @return The user's pin.
     */
    public String getPin() {
        return pin.get();
    }

    /**
     * Sets the pin of the user.
     * @param pin The new pin.
     */
    public void setPin(String pin) {
        this.pin.set(pin);
    }

    /**
     * Gets the username of the user.
     * @return The user's username.
     */
    public String getUsername() {
        return username.get();
    }

    /**
     * Sets the username of the user.
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username.set(username);
    }

    /**
     * Gets the password of the user.
     * @return The user's password.
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Sets the password of the user.
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password.set(password);
    }

    /**
     * Gets the date of birth of the user.
     * @return The user's date of birth.
     */
    public Date getDateOfBirth() {
        return dateOfBirth.get();
    }

    /**
     * Sets the date of birth of the user.
     * @param dateOfBirth The new date of birth.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    /**
     * Gets the date when the user was created.
     * @return The date when user was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt.get();
    }

    /**
     * Gets the number of loan eligibility of the user.
     * @return number of loan eligibility
     */
    public int getLoanEligibility() {
        return loanEligibility.get();
    }

    /**
     * Displays a user.
     * @return the way a user is displayed
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", type=" + type +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                ", phoneNumber=" + phoneNumber +
                ", pin=" + pin +
                ", username=" + username +
                ", password=" + password +
                ", dateOfBirth=" + dateOfBirth +
                ", created_at=" + createdAt +
                '}';
    }
}

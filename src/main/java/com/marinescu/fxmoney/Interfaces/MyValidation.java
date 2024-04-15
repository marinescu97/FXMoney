package com.marinescu.fxmoney.Interfaces;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This interface is used for validating user input in a JavaFX application.
 * It provides a set of default methods for validating various types of input fields, such as text fields, password fields,
 *  email addresses, phone numbers, and dates.
 * The interface also includes a default method for checking if one or more fields are empty, and a default method for
 *  checking if the user is not underage.
 */
public interface MyValidation {

    /**
     * Validates the length of a given text field.
     * @param field   the text field to validate
     * @param maxLength the maximum allowed length
     * @return true if the field has a valid length, false otherwise
     */
    default boolean validLength(String field, int maxLength){
        return field.length()<=maxLength;
    }

    /**
     * Validates an email address.
     * @param email the email address to validate
     * @return true if the email address is valid, false otherwise
     */
    default boolean validEmail(String email){
        String regex = "^[\\p{L}\\d!#$%&'*+/=?^_`{|}~-][\\p{L}\\d.!#$%&'*+/=?^_`{|}~-]{0,63}@[\\p{L}\\d-]+(?:\\.[\\p{L}\\d-]{2,7})*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches() && email.length()<=30;
    }

    /**
     * Validates a phone number.
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    default boolean validPhoneNumber(String phoneNumber){
        String regex = "^07\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * Validates a password.
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    default boolean validPassword(String password){
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (password.length()<8 || password.length()>30){
            return false;
        } else return matcher.matches();
    }

    /**
     * Validates if the password and the confirmation password match.
     * @param password the password to validate
     * @param confirmPassword the confirmation password to validate
     * @return true if the password and the confirmation password match, false otherwise.
     */
    default boolean passwordMatch(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    /**
     * Checks if one or more fields are empty.
     * @param nodes the nodes to check
     * @return true if one or more fields are empty, false otherwise
     */
    default boolean emptyField(Node... nodes){
        for (Node node : nodes){
            if (node instanceof TextField){
                if (((TextField)node).getText().isEmpty()){
                    return true;
                }
            } else if (node instanceof DatePicker) {
                if (((DatePicker)node).getValue()==null || ((DatePicker)node).getEditor().getText().isEmpty()){
                    return true;
                }
            } else if (node instanceof PasswordField){
                if (((PasswordField)node).getText().isEmpty()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates a personal identification number (PIN).
     * @param pin the PIN to validate
     * @return true if the PIN is valid, false otherwise
     */
    default boolean validPin(String pin){
        String regex = "\\d{6}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pin);
        return matcher.matches();
    }

    /**
     * Checks if the user is not underage.
     * @param dateOfBirth the user's date of birth
     * @return true if the user is not underage, false otherwise
     */
    default boolean validDateOfBirth(LocalDate dateOfBirth){
        try {
            LocalDate birthDay = LocalDate.of(dateOfBirth.getYear(), dateOfBirth.getMonthValue(), dateOfBirth.getDayOfMonth());
            LocalDate now = LocalDate.now();
            long years = ChronoUnit.YEARS.between(birthDay, now);
            return years>=18 && years<=90;
        } catch (NullPointerException e){
            return false;
        }
    }

    /**
     * Validates an amount.
     * @param amount the amount to validate
     * @return true if the amount is valid, false otherwise
     */
    default boolean validAmount(String amount){
        String regexDecimal = "^\\d*\\.\\d{0,2}$";
        String regexInteger = "^\\d+$";
        String regexDouble = regexDecimal + "|" + regexInteger;
        Pattern pattern = Pattern.compile(regexDouble);
        Matcher matcher = pattern.matcher(amount);
        return matcher.matches() && Double.parseDouble(amount)>0;
    }

    /**
     * Validates an International Bank Account Number (IBAN).
     * @param iban the IBAN to validate
     * @return true if the IBAN is valid, false otherwise
     */
    default boolean validIban(String iban){
        String regex = "\\d{16}";
        String regex1 = "\\d{4} \\d{4} \\d{4} \\d{4}";
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern1 = Pattern.compile(regex1);

        return pattern.matcher(iban).matches() || pattern1.matcher(iban).matches();
    }

    /**
     * Checks if the given account number exists in the list.
     * @param list the list of account numbers
     * @param account the account number to check
     * @return the account number if it exists in the list, null otherwise.
     */
    default String accountMatch(List<String> list, String account){
        if (list.contains(account)){
            return account;
        }
        return null;
    }
}

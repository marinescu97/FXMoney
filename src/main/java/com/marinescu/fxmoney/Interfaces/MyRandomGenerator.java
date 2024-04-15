package com.marinescu.fxmoney.Interfaces;

import com.marinescu.fxmoney.Model.DataSource;

import java.util.Locale;
import java.util.Random;

/**
 * This interface contains methods for generating random values.
 */
public interface MyRandomGenerator {
    /**
     * The method generates a random username, different from any username in the database.
     * The username consists of the first 3 letters of the first name, the first 3 letters of the second name,
     *  to which are added 3 digits taken randomly from his PIN.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param pin The user's pin.
     * @return The user's username.
     */
    default String randomUsername(String firstName, String lastName, String pin) {
        StringBuilder username = new StringBuilder(firstName.substring(0, 3) + lastName.substring(0, 3));
        Random random = new Random();
        String[] pinArray = pin.split("");
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(pin.length());
            username.append(pinArray[randomIndex]);
        }
        if (DataSource.getInstance().queryUserName(username.toString()) == 0) {
            return username.toString().toLowerCase(Locale.ROOT);
        }
        return randomUsername(firstName, lastName, pin);
    }

    /**
     * This method generates a random iban, different from any iban in the database.
     * @return The random iban.
     */
    default String randomIban(){
        char[] symbols = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        int length = 4;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i=0; i<4; i++){
            for (int j=0; j<length; j++){
                int randomIndex = random.nextInt(symbols.length);
                sb.append(symbols[randomIndex]);
            }
            sb.append(" ");
        }
        if (DataSource.getInstance().queryAccountIban(sb.toString().trim())==0){
            return sb.toString().trim();
        }
        return randomIban();
    }

    /**
     * This method generates a random password, different from any password in the database.
     * @return The random password.
     */
    default String randomPassword() {
        char[] symbols = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        int length = 8;
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(symbols.length);
            sb.append(symbols[randomIndex]);
        }
        String password = sb.toString();
        if (DataSource.getInstance().queryUserPassword(password)==0){
            return password;
        }
        return randomPassword();
    }
}

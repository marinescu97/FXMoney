package com.marinescu.fxmoney.Model;

/**
 * This class represents an account type with an id and a type.
 */
public class AccountType {
    private int id;
    private String type;

    /**
     * Creates a new account type object with its attributes.
     * @param id type's id
     * @param type type's name
     */
    public AccountType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Gets the type's id.
     * @return The type's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the type's id.
     * @param id The new id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the type's name.
     * @return The type's name.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type's name.
     * @param type The new name.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Display the type.
     * @return The way a type is displayed.
     */
    @Override
    public String toString() {
        return type;
    }
}

package com.marinescu.fxmoney.Model;

import java.time.Month;

/**
 * A class representing a value to be displayed in a line chart.
 */
public class ChartValue {
    // The month associated with the value
    private Month month;

    // The year associated with the value
    private String year;

    // The value to be displayed in the line chart
    private int value;

    /**
     * This method creates a new instance of the class.
     * @param month The month associated with the value.
     * @param year The year associated with the value
     * @param value The value to be displayed
     */
    public ChartValue(Month month, String year, int value) {
        this.month = month;
        this.year = year;
        this.value = value;
    }

    /**
     * Gets the month associated with the value.
     * @return The month associated with the value
     */
    public Month getMonth() {
        return month;
    }

    /**
     * Gets the year associated with the value.
     * @return The year associated with the value
     */
    public String getYear() {
        return year;
    }

    /**
     * Gets the value to be displayed in the line chart.
     * @return The value to be displayed in the line chart
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a string representation of the `ChartValue` object.
     * The string representation consists of the month and year associated with the value,
     * separated by a space.
     * @return A string representation of the `ChartValue` object
     */
    @Override
    public String toString() {
        return month.toString() + " " + year;
    }
}

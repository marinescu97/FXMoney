package com.marinescu.fxmoney.Interfaces;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This interface performs how an amount should be displayed.
 */
public interface ShowBalance {
    /**
     * This method rounds a number to a given number of decimal places.
     * @param value The number to be rounded.
     * @param places The number of decimal places.
     * @return The rounded number.
     */
    default double round(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

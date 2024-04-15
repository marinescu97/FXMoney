package com.marinescu.fxmoney.Interfaces;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * This interface contains methods for finding information about a loan.
 */
public interface LoanInterface extends ShowBalance {
    /**
     * The monthly interest rate.
     */
    double monthlyInterestRate = 0.03;

    /**
     * A date format.
     */
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * This method shows the number of months between two dates.
     * @param sDate The first date.
     * @param eDate The second date.
     * @return The number of months.
     */
    default int showMonthsDifference(String sDate, String eDate){
        LocalDate startDate = LocalDate.parse(sDate, formatter);
        LocalDate endDate = LocalDate.parse(eDate, formatter);

        Period period = Period.between(startDate, endDate);


        return period.getMonths() + (period.getYears() * 12);
    }

    /**
     * This method calculates the new remaining balance after a payment.
     * @param remainingBalance The old remaining balance.
     * @param payment The new payment.
     * @return The new remaining balance.
     */
    default double getRemainingBalance(double remainingBalance,double payment){
        double interestComponent = remainingBalance * monthlyInterestRate;
        if (payment >= interestComponent){
            double principalComponent = payment - interestComponent;
            remainingBalance = remainingBalance - principalComponent;
        } else {
            double unpaidInterest = interestComponent - payment;
            remainingBalance += unpaidInterest;
        }
        if (remainingBalance < 0){
            return 0;
        }
        return remainingBalance;
    }

    /**
     * This method calculates the EMI (Equated Monthly Instalment).
     * EMI represents a predetermined fixed payment that borrowers make to lenders on a specific date each month.
     * @param principal The loan amount.
     * @param loanTermMonths The number of months for repayment.
     * @return The EMI.
     */
    default double getEMI(double principal, int loanTermMonths) {
        return (principal * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTermMonths)) / (Math.pow(1 + monthlyInterestRate, loanTermMonths) - 1);
    }

    /**
     * This method calculates the next due date for payment.
     * @param date The current date.
     * @param loanDate The date when loan has approved.
     * @return The next due date.
     */
    default String dueDate(String date, String loanDate){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate d1 = LocalDate.parse(loanDate, f);
        LocalDate d2 = LocalDate.parse(date, f);
        if (showMonthsDifference(loanDate, date)>0 && d2.getDayOfMonth() <= d1.getDayOfMonth()){
            return d1.plusMonths(showMonthsDifference(loanDate, date)).format(f);
        }
        return d1.plusMonths(showMonthsDifference(loanDate, date)+1).format(f);
    }

    /**
     * The method returns a date in the specified format.
     * @param date The date to be formatted.
     * @return The formatted date.
     */
    default LocalDate formatDate(String date){
        return LocalDate.parse(date, formatter);
    }

    /**
     * This method determines if a date is between two dates.
     * @param startDate The start date.
     * @param endDate The end date.
     * @param targetDate The target date.
     * @return True if the target date is between the start date and the end date, otherwise false.
     */
    default boolean dateBetween(String startDate, String endDate, String targetDate){
        LocalDate sDate = formatDate(startDate);
        LocalDate eDate = formatDate(endDate);
        LocalDate tDate = formatDate(targetDate);
        return tDate.isAfter(sDate) && (tDate.isBefore(eDate) || tDate.equals(eDate));
    }
}

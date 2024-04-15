package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.ChartValue;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The class is responsible for managing and displaying a line chart.
 * This class supports the display of different types of data series, such as transactions and loans,
 *  over a specified time period.
 * The class allows users to select start and end dates to filter the data displayed on the chart.
 */
public class LineChart implements MyValidation {
    /**
     * The line chart.
     */
    @FXML private javafx.scene.chart.LineChart lineChart;

    /**
     * A date picker for selecting a date.
     */
    @FXML private DatePicker startDate, endDate;

    /**
     * The data series for the line chart.
     * This series contains the data points to be displayed on the chart.
     */
    private final XYChart.Series series = new XYChart.Series();

    /**
     * This method sets up the initial state of the line chart,
     *  including setting up the data series, configuring event listeners for date pickers, and initializing
     *  the chart with data for the last year up to the current date.
     */
    public void initialize(){
        String choice = DataHolder.getInstance().getChartsComboBox().getSelectionModel().getSelectedItem();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String end = df.format(LocalDate.now());
        showValues(setYearAgo(end), end, choice);
        lineChart.setAnimated(false);

        endDate.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER){
                if (emptyField(startDate) && emptyField(endDate)){
                    showValues(setYearAgo(end), end, choice);
                } else {
                    if (emptyField(startDate) && !emptyField(endDate)){
                        showValues(setYearAgo(endDate.getValue().toString()), endDate.getValue().toString(), choice);
                    } else if (!emptyField(startDate) && emptyField(endDate)){
                        showValues(startDate.getValue().toString(), setYearLater(startDate.getValue().toString()), choice);
                    } else {
                        showValues(startDate.getValue().toString(), endDate.getValue().toString(), choice);
                    }
                }
            }
        });

        startDate.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER){
                if (emptyField(startDate) && emptyField(endDate)){
                    showValues(setYearAgo(end), end, choice);
                } else {
                    if (emptyField(startDate) && !emptyField(endDate)){
                        showValues(setYearAgo(endDate.getValue().toString()), endDate.getValue().toString(), choice);
                    } else if (!emptyField(startDate) && emptyField(endDate)){
                        showValues(startDate.getValue().toString(), setYearLater(startDate.getValue().toString()), choice);
                    } else {
                        showValues(startDate.getValue().toString(), endDate.getValue().toString(), choice);
                    }
                }
            }
        });

        lineChart.getData().add(series);
    }

    /**
     * Displays values on the line chart based on the specified start date, end date, and choice of data type.
     * The method clears any existing data from the series and queries new data based on the parameters
     * provided to populate the chart.
     * @param start The start date for the data series in "yyyy-MM-dd" format.
     * @param end The end date for the data series in "yyyy-MM-dd" format.
     * @param choice The type of data to display, e.g., "Transactions" or "Loans".
     */
    public void showValues(String start, String end, String choice) {
        series.getData().clear();
        ObservableList<ChartValue> observableList = null;
        if (choice.equals("Transactions")) {
            lineChart.setTitle("Transactions");
            List<ChartValue> transactions = new ArrayList<>(DataSource.getInstance().queryTransactionsChart(start, end));
            observableList = FXCollections.observableList(transactions);
        } else if (choice.equals("Loans")) {
            lineChart.setTitle("Loans");
            List<ChartValue> loans = new ArrayList<>(DataSource.getInstance().queryLoansChart(start, end));
            observableList = FXCollections.observableArrayList(loans);
        }
        for (ChartValue value : observableList) {
            series.getData().add(new XYChart.Data(value.toString(), value.getValue()));
        }
    }

    /**
     * Calculates the date one year before the given date.
     * @param date The reference date in "yyyy-MM-dd" format.
     * @return A {@code String} representing the date one year before the given date.
     */
    public String setYearAgo(String date){
        return LocalDate.parse(date).minusYears(1).toString();
    }

    /**
     * Calculates the date one year after the given date.
     * @param date The reference date in "yyyy-MM-dd" format.
     * @return A {@code String} representing the date one year after the given date.
     */
    public String setYearLater(String date){
        return LocalDate.parse(date).plusYears(1).toString();
    }
}

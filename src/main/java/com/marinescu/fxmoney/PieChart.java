package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is responsible for creating and initializing the PieChart UI component.
 * It displays the chart based on the selected option from the {@link DataHolder#getInstance().getChartsComboBox()}.
 * If the user selects “Accounts”, the chart will display the number of accounts and their respective percentages.
 * If the user selects “Users”, the chart will display the number of users and their respective percentages.
 * Each slice of the pie chart is clickable and will display the corresponding number and percentage of the clicked slice.
 */
public class PieChart implements Initializable {
    /**
     * The pie chart.
     */
    @FXML
    private javafx.scene.chart.PieChart pieChart;

    /**
     * A label for displaying the number of objects and their respective percentages.
     */
    @FXML private Label percentLabel;

    /**
     * A list of pie chart data.
     */
    private final ObservableList<javafx.scene.chart.PieChart.Data> pieChartData = FXCollections.observableArrayList();

    /**
     * The colors that slices of the pie chart can be colored with.
     */
    private final String[] colors = {": #5F9C7D", ": #E6B188", ": #F2E6D7", ": #6F8EBC", ": #69608A", ": #EC7645", ": #914C45", ": #677400"};

    /**
     * This method initializes the PieChart UI component and sets its properties.
     * It also sets the event handler for each slice of the pie chart to display the corresponding number and percentage when clicked.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pieChart.setLabelLineLength(10);
        pieChart.setLegendSide(Side.RIGHT);
        percentLabel.setStyle("-fx-font: 40 arial;-fx-font-weight: bold");
        if (DataHolder.getInstance().getChartsComboBox().getSelectionModel().getSelectedItem().equals("Accounts")){
            pieChart.setTitle("Accounts");
            long totalAccounts=0;
            Map<String, Integer> accounts = new HashMap<>(DataSource.getInstance().queryAccountsChart());
            for (String account : accounts.keySet()){
                totalAccounts+=accounts.get(account);
                pieChartData.add(new javafx.scene.chart.PieChart.Data(account, accounts.get(account)));
            }

            pieChart.setData(pieChartData);
            pieChart.requestLayout();
            pieChart.applyCss();
            for (javafx.scene.chart.PieChart.Data data : pieChart.getData()) {
                data.getNode().setStyle(
                        "-fx-pie-color" + colors[(int) ((Math.random() * (colors.length)))] + ";"
                );

                long finalTotalClients = totalAccounts;
                data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                        e -> {
                            // show number of clients and percentage on mouse pressed
                            percentLabel.setTranslateX(e.getSceneX()-percentLabel.getLayoutX());
                            percentLabel.setTranslateY(e.getSceneY()-percentLabel.getLayoutY());
                            percentLabel.setText(Math.round(data.getPieValue()) + " - " + String.format("%.2f", (data.getPieValue()/finalTotalClients)*100) + "%");

                            // animation
                            Bounds b1 = data.getNode().getBoundsInLocal();
                            double newX = (b1.getWidth()) / 2.0 + b1.getMinX();
                            double newY = (b1.getHeight()) / 2.0 + b1.getMinY();

                            // reset pie wedge location
                            data.getNode().setTranslateX(0);
                            data.getNode().setTranslateY(0);

                            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), data.getNode());
                            tt.setByX(newX);
                            tt.setByY(newY);
                            tt.setAutoReverse(true);
                            tt.setCycleCount(2);
                            tt.play();
                        });
            }
        } else if (DataHolder.getInstance().getChartsComboBox().getSelectionModel().getSelectedItem().equals("Users")) {
            pieChart.setTitle("Users");
            long totalUsers=0;
            Map<String, Integer> users = new HashMap<>(DataSource.getInstance().queryUsersChart());
            ObservableList<javafx.scene.chart.PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (String user : users.keySet()){
                totalUsers+=users.get(user);
                pieChartData.add(new javafx.scene.chart.PieChart.Data(user, users.get(user)));
            }

            pieChart.setData(pieChartData);

            for (final javafx.scene.chart.PieChart.Data data : pieChart.getData()) {
                data.getNode().setStyle(
                        "-fx-pie-color " + colors[(int) ((Math.random() * (colors.length)))] + ";"
                );

                long finalTotalClients = totalUsers;
                data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                        e -> {
                            // show number of clients and percentage on mouse pressed
                            percentLabel.setTranslateX(e.getSceneX()-percentLabel.getLayoutX());
                            percentLabel.setTranslateY(e.getSceneY()-percentLabel.getLayoutY());
                            percentLabel.setText(Math.round(data.getPieValue()) + " - " + String.format("%.2f", (data.getPieValue()/finalTotalClients)*100) + "%");

                            // animation
                            Bounds b1 = data.getNode().getBoundsInLocal();
                            double newX = (b1.getWidth()) / 2.0 + b1.getMinX();
                            double newY = (b1.getHeight()) / 2.0 + b1.getMinY();

                            // reset pie wedge location
                            data.getNode().setTranslateX(0);
                            data.getNode().setTranslateY(0);

                            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), data.getNode());
                            tt.setByX(newX);
                            tt.setByY(newY);
                            tt.setAutoReverse(true);
                            tt.setCycleCount(2);
                            tt.play();
                        });
            }
        }
    }
}

package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Interfaces.MyAlertInterface;
import com.marinescu.fxmoney.Model.DataHolder;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * The class handles the logic behind the bankerAdminPage.fxml file.
 */
public class BankerAdmin implements MyAlertInterface {
    /**
     * The BorderPane, which is the root node of the window.
     */
    @FXML
    private BorderPane mainStage;
    /**
     * The Label which displays the date and time.
     */
    @FXML private Label labelDateTime;
    /**
     * The combo box from which the admin chooses a chart.
     */
    @FXML private ComboBox<String> chartsBox;

    /**
     * The KeyCombination object, representing a key press with the SHIFT modifier, and the '+' key.
     */
    final KeyCombination keyShiftPlus = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_ANY);
    /**
     * The list of chart titles for the ComboBox.
     */
    private final String[] chartsList = {"Accounts", "Users", "Transactions", "Loans"};

    /**
     * This method is automatically called after the FXML file has been loaded.
     *
     * It initializes the ComboBox and its list of chart titles.
     * Shows the menu from the menu.fxml file.
     * Sets the Label with the current date and time and updates it every minute.
     */
    public void initialize() {
        chartsBox.getItems().addAll(FXCollections.observableArrayList(chartsList));
        DataHolder.getInstance().setChartsComboBox(chartsBox);

        DataHolder.getInstance().setBorderPane(this.mainStage);

        mainStage.setOnMouseClicked(e -> mainStage.requestFocus());

        try {
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menu.fxml"));
            this.mainStage.setLeft(menuLoader.load());
            Menu menu = menuLoader.getController();
            mainStage.setOnKeyPressed(e -> keyPressHandler(DataHolder.getInstance().getMainUser().getType(), e, menu));

            chartsBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (t1.equals("Accounts") || t1.equals("Users")){
                        try {
                            DataHolder.getInstance().getBorderPane().setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pieChart.fxml"))));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (t1.equals("Transactions") || t1.equals("Loans")) {
                        try {
                            DataHolder.getInstance().getBorderPane().setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LineChart.fxml"))));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });

            chartsBox.getSelectionModel().selectFirst();

            menu.homeButtonHandler();
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                labelDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy")));
            }
        };
        timer.start();
    }

    /**
     * The method takes the type of the user, and the key which is pressed.
     * It shows a scene according to them.
     * @param userType the type of the user
     * @param e the event that is generated when a key is pressed
     * @param menu the menu showed in the window
     */
    public void keyPressHandler(int userType, KeyEvent e, Menu menu){
        if (userType == 2 || userType == 1){
            if (e.getCode().equals(KeyCode.H)){
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                menu.homeButtonHandler();
            } else if (e.getCode().equals(KeyCode.P)) {
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                menu.profileButtonHandler();
            } else if (e.getCode().equals(KeyCode.ESCAPE)) {
                DataHolder.getInstance().getConfirmationDialog().showDialog();
                DataHolder.getInstance().getConfirmationDialog().okBtnHandler(event -> {
                    DataHolder.getInstance().getConfirmationDialog().closeDialog();
                    menu.logoutButtonHandler();
                });
            }
        }
        if (userType == 2){
            if (e.getCode().equals(KeyCode.C)) {
                menu.clientButtonHandler();
            } else if (keyShiftPlus.match(e) || e.getCode().equals(KeyCode.ADD)) {
                menu.addClientButtonHandler();
            } else if (e.getCode().equals(KeyCode.L)) {
                menu.loanButtonHandler();
            }
        }

        if (userType == 1){
            if (e.getCode().equals(KeyCode.S)) {
                menu.statisticsButtonHandler();
            } else if (e.getCode().equals(KeyCode.B)) {
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                menu.bankersButtonHandler();
            } else if (e.getCode().equals(KeyCode.C)){
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                menu.clientsButtonHandler();
            } else if (e.getCode().equals(KeyCode.A)) {
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                menu.accountButtonHandler();
            }
        }
    }
}

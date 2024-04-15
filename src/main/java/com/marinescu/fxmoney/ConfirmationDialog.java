package com.marinescu.fxmoney;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

/**
 * This class represents a confirmation dialog.
 */
public class ConfirmationDialog {
    /**
     * The main container.
     */
    private final DialogPane dialogPane;

    /**
     * The main stage.
     */
    private final Stage stage;

    /**
     * A TranslateTransition used to move the dialog from one position to another over a specific duration.
     */
    private final TranslateTransition transition;

    /**
     * The dialog's button.
     */
    private final Button ok;

    /**
     * This method is the class' controller.
     * It creates and initializes the confirmation dialog.
     */
    public ConfirmationDialog() {
        stage = new Stage();

        dialogPane = new DialogPane();

        dialogPane.setStyle("-fx-background-radius: 10; -fx-border-radius: 10");
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style.css")).toExternalForm());

        dialogPane.getStyleClass().add("confirmDialog");

        dialogPane.setPrefWidth(250.0);
        dialogPane.setPrefHeight(50.0);

        dialogPane.setContentText("Are you sure?");

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ok = (Button) dialogPane.lookupButton(ButtonType.OK);

        Button cancel = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancel.addEventFilter(ActionEvent.ACTION, actionEvent -> fadeIN(stage));

        Scene scene = new Scene(dialogPane);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style.css")).toExternalForm());

        transition = new TranslateTransition(Duration.seconds(2), scene.getRoot());
        transition.setFromY(-20);
        transition.setToY(150);

        stage.initStyle(StageStyle.TRANSPARENT);

        stage.setScene(scene);
        stage.setHeight(500.0);
    }

    /**
     * This method displays the confirmation dialog.
     */
    public void showDialog(){
        stage.show();
        transition.play();
        fadeOUT();
    }

    /**
     * It closes the confirmation dialog.
     */
    public void closeDialog(){
        fadeIN(stage);
    }

    /**
     * This method adds an action to 'ok' button when it is clicked.
     * @param e The button's action event.
     */
    public void okBtnHandler(EventHandler e){
        ok.setOnAction(e);
    }

    /**
     * This method applies a fade effect to a stage.
     * This animates the stage's opacity property from a value of 1 to a value of 0.
     * It is used to close the confirmation dialog.
     * @param stage The given stage.
     */
    private void fadeIN(Stage stage){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), dialogPane);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> stage.close());
    }

    /**
     * This method applies a fade effect to a stage.
     * This animates the stage's opacity property from a value of 0 to a value of 1.
     * It is used to display the confirmation dialog.
     */
    private void fadeOUT(){
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), dialogPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }
}

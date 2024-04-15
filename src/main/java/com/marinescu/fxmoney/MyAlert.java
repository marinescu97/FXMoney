package com.marinescu.fxmoney;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * This class handles the logic behind the myAlert.fxml file.
 * It displays messages with different types, such as "success" or "warning".
 */
public class MyAlert {
    /**
     * The HBox container for the alert.
     */
    @FXML private HBox box;

    /**
     * The label for the alert message.
     */
    @FXML private Label message;

    /**
     * The button for closing the alert.
     */
    @FXML private Button btn;

    // Interpolators for animation curves
    /**
     * The ease-in interpolator for the alert animation.
     */
    private final Interpolator EXP_IN = new Interpolator() {
        @Override
        protected double curve(double t) {
            return (t == 1.0) ? 1.0 : 1 - Math.pow(2.0, -10 * t);
        }
    };

    /**
     * The ease-out interpolator for the alert animation.
     */
    private final Interpolator EXP_OUT = new Interpolator() {
        @Override
        protected double curve(double t) {
            return (t == 0.0) ? 0.0 : Math.pow(2.0, 10 * (t - 1));
        }
    };

    // Scale transitions and sequential animation
    /**
     * The first scale transition for the alert animation.
     */
    private final ScaleTransition scale1 = new ScaleTransition();

    /**
     * The second scale transition for the alert animation.
     */
    private final ScaleTransition scale2 = new ScaleTransition();

    /**
     * The sequential animation for the alert.
     */
    private final SequentialTransition anim = new SequentialTransition(scale1, scale2);

    /**
     * Initializes the alert with the given type and message.
     * @param type The type of the alert (e.g. "success", "warning")
     * @param text The message to display in the alert
     */
    public void initialize(String type,String text){
        // Configure the first scale transition.
        scale1.setFromX(0.01);
        scale1.setFromY(0.01);
        scale1.setToY(1.0);
        scale1.setDuration(Duration.seconds(0.33));
        scale1.setInterpolator(EXP_IN);
        scale1.setNode(box);

        // Configure the second scale transition.
        scale2.setFromX(0.01);
        scale2.setToX(1.0);
        scale2.setDuration(Duration.seconds(0.33));
        scale2.setInterpolator(EXP_OUT);
        scale2.setNode(box);

        // Set the message text and configure the appearance based on the type.
        message.setText(text);
        switch (type) {
            case "success" -> {
                String greenBg = "-fx-background-color: GREEN;";
                box.setStyle(greenBg);
                String whiteColor = "-fx-text-fill: WHITE;-fx-background-color: transparent;";
                message.setStyle(whiteColor);
                btn.setStyle(whiteColor);
            }
            case "warning" -> {
                box.setPrefWidth(500.0);
                message.setPrefWidth(400.0);
                String yellowBg = "-fx-background-color: YELLOW;";
                box.setStyle(yellowBg);
                String blackColor = "-fx-text-fill: BLACK;-fx-background-color: transparent;";
                message.setStyle(blackColor);
                btn.setStyle(blackColor);
            }
        }
    }

    /**
     * This method displays the alert.
     */
    public void show(){
        Stage stage = (Stage) box.getScene().getWindow();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        anim.play();
    }

    /**
     * This method closes the alert.
     */
    public void close(){
        Stage stage = (Stage) box.getScene().getWindow();
        anim.setOnFinished(e -> stage.close());
        anim.setAutoReverse(true);
        anim.setCycleCount(2);
        anim.playFrom(Duration.seconds(0.66));
    }
}

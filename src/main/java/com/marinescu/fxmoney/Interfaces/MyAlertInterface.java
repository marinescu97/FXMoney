package com.marinescu.fxmoney.Interfaces;

import com.marinescu.fxmoney.MyAlert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 * This interface contains custom alert functionalities.
 */
public interface MyAlertInterface {
    /**
     * Displays a custom alert with the given type and message.
     * @param type The type of the alert (e.g. "success", "warning").
     * @param message The message to display in the alert.
     */
    default void showAlert(String type, String message){
        try {
            FXMLLoader alertLoader = new FXMLLoader(getClass().getResource("myAlert.fxml"));
            Parent root = alertLoader.load();
            root.getStyleClass().removeAll(root.getStyleClass());
            root.getStyleClass().add("alertRoot");
            MyAlert myAlert = alertLoader.getController();
            myAlert.initialize(type,message);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style.css")).toExternalForm());

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            myAlert.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Customizes the appearance of an alert dialog.
     * @param alert The alert dialog to customize.
     */
    default void customAlert(Alert alert){
        alert.initStyle(StageStyle.UNDECORATED);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-radius: 10; -fx-border-radius: 10");
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style.css")).toExternalForm());
        dialogPane.getStyleClass().add("infoDialog");
        dialogPane.setPrefWidth(250.0);
        dialogPane.setPrefHeight(60.0);
    }
}

package com.marinescu.fxmoney;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * This class handles the logic behind the splashScreen.fxml file.
 * It loads the progress bar and displays the login window.
 */
public class SplashScreen {
    /**
     * reference to the 'loadProgress' ProgressBar from the FXML file.
     */
    @FXML private ProgressBar loadProgress;

    /**
     * The method initializes the controller.
     * It is automatically called after the FXML file has been loaded.
     */
    public void initialize() {
        Service service = createProcessBar();

        // Bind progress bar from the splashScreen.fxml file to task progress bar.
        loadProgress.progressProperty().bind(service.progressProperty());

        // When progress bar succeeded, a login page will be displayed.
        service.setOnSucceeded(e -> showLoginWindow(loadProgress.getScene().getWindow(), getClass()));

        // Start progress bar loading.
        service.start();
    }

    /**
     * This method creates a service with a background task with a progress indicator.
     * During loading, the progress bar will wait 3 seconds when loading at 30%, and 2 seconds when loading at 60%.
     * @return service
     */
    private static Service createProcessBar(){
        // Create a background task with progress indicator.
        return new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws InterruptedException {
                        for(int i = 0; i <=100; i++){
                            if (i == 30){
                                Thread.sleep(3000);
                            } else if (i == 60) {
                                Thread.sleep(2000);
                            }
                            updateProgress(i, 100);
                            Thread.sleep(50);
                        }

                        return null;
                    }
                };
            }
        };
    }

    /**
     * This method closes the current window and shows Login window.
     * @param window The current window.
     * @param c The current class.
     */

    private static void showLoginWindow(Window window, Class c){
        Stage mainStage = (Stage) window;
        mainStage.close();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(c.getResource("loginPage.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();

            stage.setTitle("FXMoney");
            stage.setScene(scene);
            stage.setResizable(false);

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - mainStage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - mainStage.getHeight()) / 2);

            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}

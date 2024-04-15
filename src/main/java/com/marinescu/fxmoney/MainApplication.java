package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Interfaces.UpdateTables;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * This is the main class of the Bank Application.
 * It extends the {@link Application} class and implements the {@link UpdateTables} interface.
 */
public class MainApplication extends Application implements UpdateTables {
    /**
     * Initializes the primary stage of the application.
     * @param stage The primary stage.
     * @throws IOException If the .fxml file is not found or cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("splashScreen.fxml"));
        MainApplication.setUserAgentStylesheet(STYLESHEET_CASPIAN);
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * The main method launches the application.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * This method initializes the application, checks if the database connection is successful
     *      using the {@link DataSource} class, and if not, it exits the platform.
     * If the connection is successful, it updates the loans and all tables using the updateLoans and updateAllTables methods.
     * @throws Exception If an error occurs while initializing the application.
     */
    @Override
    public void init() throws Exception {
        super.init();
        if (!DataSource.getInstance().connect()){
            Platform.exit();
        } else {
            updateLoans();
            updateAllTables();
        }
    }

    /**
     * This method stops the application and closes the database connection using the {@link DataSource} class.
     * @throws Exception If an error occurs while stopping the application.
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.getInstance().close();
    }
}
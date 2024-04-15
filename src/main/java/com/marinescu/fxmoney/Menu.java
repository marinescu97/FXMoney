package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * The class handles the logic behind the left menu (menu.fxml) from the banker or admin page.
 */

public class Menu {
    /**
     * The menu container.
     */
    @FXML
    private VBox menu;

    /**
     * Arrays of buttons ids, icons paths and buttons tooltips.
     * All as strings.
     */
    private String[] buttons, icons, tooltips;

    /**
     * The root node of the window.
     */

    private final BorderPane borderPane = DataHolder.getInstance().getBorderPane();

    /**
     * This method is automatically called after the menu.fxml file has been loaded.
     * The menu items (buttons, icons and tooltips) are displayed according to the type of user (banker or admin).
     * When a button is pressed, the method is called whose name is equal to the id of the button to which the word “Handler” is added.
     */
    public void initialize(){
        if (DataHolder.getInstance().getMainUser().getType()==2){
            buttons = new String[]{"homeButton", "profileButton", "clientButton", "addClientButton", "loanButton", "logoutButton"};
            icons = new String[]{"icons/home.png", "icons/profile.png", "icons/customer.png", "icons/new-user.png", "icons/loan.png","icons/logout.png"};
            tooltips = new String[]{"Home", "Profile", "Client details", "Add new client", "Loan request","Logout"};
        } else if (DataHolder.getInstance().getMainUser().getType()==1) {
            borderPane.setPrefHeight(550.0);
            buttons = new String[]{"homeButton", "profileButton", "accountButton", "statisticsButton", "bankersButton", "clientsButton", "logoutButton"};
            icons = new String[]{"icons/home.png", "icons/profile.png", "icons/account.png", "icons/bar-chart.png","icons/banker.png","icons/customer.png","icons/logout.png"};
            tooltips = new String[]{"Home", "Profile", "Account details","Statistics", "Bankers", "Clients", "Logout"};
        }

        for (int i=0; i< buttons.length; i++){
            JFXButton button = new JFXButton();
            button.setId(buttons[i]);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setFocusTraversable(false);

            Image image = new Image(Objects.requireNonNull(Menu.class.getResource(icons[i])).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(45.0);
            imageView.setFitWidth(45.0);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            button.setGraphic(imageView);

            button.setTooltip(new Tooltip(tooltips[i]));
            button.getTooltip().setStyle("-fx-background-color: #161616; -fx-text-fill: white; -fx-background-radius: 10px; -fx-font-size: 20px;");

            button.setOnAction(event -> {
                DataHolder.getInstance().getChartsComboBox().setVisible(false);
                if (button.getId().equals("logoutButton")){
                    logoutButtonHandler();
                } else {
                    try {
                        Method method = Menu.class.getMethod(button.getId()+"Handler");
                        method.invoke(new Menu());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
            menu.getChildren().add(button);
        }
    }

    /**
     * The method shows the home page in the main page.
     * This method is available for bankers and admin.
     */
    public void homeButtonHandler() {
        try {
            borderPane.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home.fxml"))));
            DataHolder.getInstance().setCurrentContainerId("home");
            if (DataHolder.getInstance().getMainUser().getType() == 1){
                borderPane.getCenter().setStyle("-fx-padding: 100 0 0 0");
            }
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    /**
     * The method shows the profile page in the main page.
     * In this page the user can see the profile information and can change them.
     * This method is available for bankers and admin.
     */
    public void profileButtonHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = fxmlLoader.load();
            Profile profile = fxmlLoader.getController();
            profile.initialize(DataHolder.getInstance().getMainUser());
            borderPane.setCenter(root);
            DataHolder.getInstance().setCurrentContainerId("profile");
            profile.getFirstName().requestFocus();
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    /**
     * The method shows the profile of a client in the main page, if a client was searched with his pin.
     * If he was not, it shows a search form instead.
     * It also resizes the main page according to size of client's profile page.
     * This method is available only for bankers.
     */
    public void clientButtonHandler() {
        try {
            if (DataHolder.getInstance().getUser() != null) {
                FXMLLoader clientDetailsLoader = new FXMLLoader(getClass().getResource("clientProfile.fxml"));
                Parent root = clientDetailsLoader.load();
                borderPane.setPrefHeight(550.0);
                borderPane.setPrefWidth(600.0);
                borderPane.setCenter(root);
                DataHolder.getInstance().setCurrentContainerId("clientProfile");
            } else {
                FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("searchClient.fxml"));
                borderPane.setCenter(searchLoader.load());
                SearchClient searchClient = searchLoader.getController();
                searchClient.getPinField().requestFocus();
                DataHolder.getInstance().setCurrentContainerId("searchClient");
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * The method shows a form in the main page, for adding a new client.
     * This method is available only for bankers.
     */
    public void addClientButtonHandler() {
        DataHolder.getInstance().setUserType(3);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addUser.fxml"));
            borderPane.setCenter(fxmlLoader.load());
            AddUser addUser = fxmlLoader.getController();
            addUser.getFirstName().requestFocus();
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    /**
     * The method logs out the user and shows login page instead of the current page.
     * This method is available for bankers and admin.
     */
    public void logoutButtonHandler() {
        try {
            DataHolder.getInstance().setMainUser(null);
            DataHolder.getInstance().setUser(null);
            DataHolder.getInstance().setAccount(null);

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginPage.fxml")));

            Stage mainStage = (Stage) borderPane.getScene().getWindow();
            mainStage.close();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.show();

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method shows a pie chart in the main page and a combo box with multiple choices of statistics.
     * It is available only for the admin.
     */
    public void statisticsButtonHandler() {
        try {
            borderPane.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("pieChart.fxml"))));
            DataHolder.getInstance().getChartsComboBox().setVisible(true);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method shows a table of all bankers in the main page.
     * It is available only for the admin.
     */
    public void bankersButtonHandler(){
        try {
            DataHolder.getInstance().setUserType(2);
            borderPane.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("usersData.fxml"))));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method shows a table of all clients in the main page.
     * This is available only for the admin.
     */
    public void clientsButtonHandler(){
        try {
            DataHolder.getInstance().setUserType(3);
            borderPane.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("usersData.fxml"))));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method shows a modal form with a loan request.
     * This method is available only for bankers.
     */
    public void loanButtonHandler(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scroll.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());
            stage.show();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method shows admin account information in the main page.
     * This method is available only for the admin.
     */
    public void accountButtonHandler(){
        try {
            DataHolder.getInstance().setUser(DataHolder.getInstance().getMainUser());
            DataHolder.getInstance().setAccount(DataSource.getInstance().queryAccount(DataHolder.getInstance().getMainUser().getId()));
            FXMLLoader clientDetailsLoader = new FXMLLoader(getClass().getResource("clientProfile.fxml"));
            Parent root = clientDetailsLoader.load();
            borderPane.setPrefHeight(550.0);
            borderPane.setPrefWidth(600.0);
            borderPane.setCenter(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

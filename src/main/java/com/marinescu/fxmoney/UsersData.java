package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Model.Account;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import com.marinescu.fxmoney.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This class handles the logic behind the usersData.fxml file.
 */
public class UsersData {
    /**
     * The layout container.
     */
    @FXML private VBox mainVBox;
    /**
     * The table with users.
     */
    @FXML private TableView<User> usersTableView;
    /**
     * A table column for users' ID.
     */
    @FXML private TableColumn<User, Integer> idCol;
    /**
     * A table column for users' first name.
     */
    @FXML private TableColumn<User, String> fNameCol;
    /**
     * A table column for users' last name.
     */
    @FXML private TableColumn<User, String> lNameCol;
    /**
     * A table column for users' email.
     */
    @FXML private TableColumn<User, String> emailCol;
    /**
     * A table column for users' phone number.
     */
    @FXML private TableColumn<User, String> pNumberCol;
    /**
     * A progress bar user for load users in the table.
     */
    @FXML private ProgressBar progressBar;
    /**
     * The title of the page based of users' type.
     */
    @FXML private Label title;
    /**
     * The list of users.
     */
    private final ObservableList<User> list = FXCollections.observableArrayList(DataSource.getInstance().queryUserByType(DataHolder.getInstance().getUserType()));

    /**
     * This method is automatically called after the usersData.fxml file has been loaded.
     * It sets the page title based on the users' type and loads the table content.
     */
    public void initialize(){
        usersTableView.getStyleClass().add("users-table");
        usersTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (DataHolder.getInstance().getUserType() == 2){
            title.setText("Bankers");
        } else {
            title.setText("Clients");
        }

        usersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        DataHolder.getInstance().setUsersTableView(usersTableView);
        mainVBox.setOnMouseClicked(e -> usersTableView.getSelectionModel().clearSelection());

        loadTable();
    }

    /**
     * This is a helper method to load the table content.
     */
    private void loadTable(){
        Task<ObservableList<User>> task = new GetAllUsers();
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        pNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        usersTableView.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);
        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));
        new Thread(task).start();
    }

    /**
     * This method display a window dialog with information about the selected user.
     */
    @FXML public void details(){
        if (usersTableView.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Please select a user");
            alert.show();
        } else {
            User user = usersTableView.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userDetailsDialog.fxml"));
                Parent root = fxmlLoader.load();
                UserDetailsDialog showUserDetails = fxmlLoader.getController();
                showUserDetails.initialize(user);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(usersTableView.getScene().getWindow());
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method displays a page for updating the user's information.
     */
    @FXML public void update(){
        if (usersTableView.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Please select a user");
            alert.show();
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
                Parent root = fxmlLoader.load();
                Profile profile = fxmlLoader.getController();
                profile.initialize(usersTableView.getSelectionModel().getSelectedItem());
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(usersTableView.getScene().getWindow());
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method shows a page for adding a new user to the table and to the database.
     */
    @FXML public void add(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addUser.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersTableView.getScene().getWindow());
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method deletes a user from the table and from the database.
     */
    @FXML public void delete(){
        if (usersTableView.getSelectionModel().getSelectedItem() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Please select a user");
            alert.show();
        } else {
            User user = usersTableView.getSelectionModel().getSelectedItem();
            List<Account> userAccounts = DataSource.getInstance().queryAccountsByUser(user.getId());
            double amount = 0;
            for (Account account : userAccounts){
                amount+=account.getBalance();
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Are you sure you want to delete user " + user.getFirstName() + " " + user.getLastName() + "?");
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent() && optional.get() == ButtonType.OK){

                alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("The user cannot be deleted!");

                if (DataSource.getInstance().queryLoan(user.getId())!=null){
                    alert.setContentText("He has an unpaid loan.");
                    alert.show();
                } else if (amount>0) {
                    alert.setContentText("He has an amount in his accounts worth $" + amount);
                    alert.show();
                } else {
                    if (DataSource.getInstance().deleteUser(user.getId())){
                        usersTableView.getItems().remove(user);
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setHeaderText("User successfully deleted");
                        alert1.show();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText("Something went wrong, please try again later");
                        error.show();
                    }
                }
            } else if (optional.isPresent() && optional.get() == ButtonType.CANCEL) {
                alert.close();
            }
        }
    }

    /**
     * This method refreshes the table.
     * It will empty the table and then will repopulate it with data from the database.
     */
    @FXML public void refresh(){
        list.removeAll();
        loadTable();
    }

    /**
     * A Task class that represents a background task.
     */
    static class GetAllUsers extends Task {
        /**
         * The actual work to be done in the background.
         * @return A list with users from the database base on their type.
         */
        @Override
        public ObservableList<User> call() {
            return FXCollections.observableArrayList(DataSource.getInstance().queryUserByType(DataHolder.getInstance().getUserType()));
        }
    }
}

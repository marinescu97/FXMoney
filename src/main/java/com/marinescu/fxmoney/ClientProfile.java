package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyRandomGenerator;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Optional;

/**
 * This class handles the logic behind the clientProfile.fxml file.
 * It performs operations on a client and account's information.
 */
public class ClientProfile implements MyRandomGenerator, MyValidation {
    /**
     * The current client
     */
    private User user;

    /**
     * The object that validates the entered data
     */
    private final Validator validator = new Validator();

    /**
     * A choice box of all the client's accounts
     */
    @FXML
    private ChoiceBox<Account> selectAccount;

    /**
     * Label that contain the account information
     */
    @FXML private Label accountNumber, balanceLbl;

    /**
     * Container for withdrawal and deposit operations
     */
    @FXML private HBox amountHbox;
    /**
     * The current container
     */
    @FXML private HBox currentContainer;

    /**
     * The button that performs withdrawal and deposit operations
     */
    @FXML private JFXButton amountBtn;

    /**
     * The amount field for withdrawal and deposit operations
     */
    @FXML private TextField amountField;

    /**
     * A menu that contains various operations on accounts and client information.
     */
    @FXML private MenuButton menuBtn;

    /**
     * A list of all account's transactions
     */
    @FXML private ListView<Transaction> transactionsListView;

    /**
     * This method is automatically called after the clientProfile.fxml file has been loaded.
     * It adds all the client's accounts to the choice box, selects the current account and displays account information.
     * It also shows the list of all account transactions.
     */
    public void initialize(){
            if (DataHolder.getInstance().getMainUser().getType()==1){
                user = DataHolder.getInstance().getMainUser();
                selectAccount.setVisible(false);
                menuBtn.setVisible(false);
                Account mainAccount = DataSource.getInstance().queryAccount(user.getId());
                accountNumber.setText(mainAccount.getIban());
                balanceLbl.setText(String.valueOf(mainAccount.getBalance()));

                DataHolder.getInstance().setTransactions(mainAccount.getId());
                if (DataHolder.getInstance().getTransactions().isEmpty()){
                    transactionsListView.setPlaceholder(new Label("No transactions"));
                } else {
                    transactionsListView.setItems(DataHolder.getInstance().getTransactions());
                    transactionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                }
            } else {
                user = DataHolder.getInstance().getUser();

                // Takes over the loan associated with the client from the database.
                Loan loan = DataSource.getInstance().queryLoan(user.getId());

                // Adds all the client's accounts to the choice box.
                selectAccount.getItems().addAll(DataHolder.getInstance().getAccounts());

                // Displays information based on the selected account.
                selectAccount.getSelectionModel().selectedItemProperty().addListener((observableValue, oldAcc, newAcc) -> {
                    DataHolder.getInstance().setAccount(newAcc);
                    accountNumber.textProperty().bind(Bindings.convert(DataHolder.getInstance().getAccount().ibanProperty()));
                    balanceLbl.textProperty().bind(Bindings.convert(DataHolder.getInstance().getAccount().balanceProperty()));
                    DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                    if (DataHolder.getInstance().getTransactions().isEmpty()){
                        transactionsListView.setPlaceholder(new Label("No transactions"));
                    } else {
                        transactionsListView.setItems(DataHolder.getInstance().getTransactions());
                        transactionsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                    }

                    if (newAcc.toString().equals("Deposit") && loan!=null){
                        if (menuBtn.getItems().get(1).getText().equals("Loan info")){
                            menuBtn.getItems().remove(1);
                        }
                    }
                });

                // Selects the first account.
                selectAccount.getSelectionModel().selectFirst();

                // Sets a singleton class variable with the current menu.
                DataHolder.getInstance().setMenuButton(menuBtn);

                // If there is an available loan, it adds to the menu an option to view the details of the loan.
                if (loan!=null){
                    DataHolder.getInstance().setLoan(loan);
                    MenuItem item = new MenuItem("Loan info");
                    DataHolder.getInstance().getMenuButton().getItems().add(1, item);

                    menuBtn.getItems().removeIf(menuItem -> menuItem.getText().equals("Request a loan"));

                    item.setOnAction(e -> {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loanInfo.fxml"));
                            Parent root = fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));

                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.show();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }

                // Creates a timeline to hide the container after 60 seconds of inactivity.
                Timeline hideTimeline = new Timeline(new KeyFrame(
                        Duration.seconds(60),
                        event -> hideAmountHbox()
                ));

                // Resets the inactivity timer on using the text field.
                amountField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.isEmpty()) {
                        hideTimeline.playFromStart();
                    }
                });

                // Starts the inactivity timer.
                hideTimeline.setCycleCount(Timeline.INDEFINITE);
                hideTimeline.play();

                // Creates a timeline to log out the client after 10 minutes of inactivity.
                Timeline logoutTimeline = new Timeline(new KeyFrame(
                        Duration.minutes(30),
                        event -> logout()
                ));

                currentContainer.setOnMouseMoved(e -> {
                    logoutTimeline.stop();
                    logoutTimeline.play();
                });
                logoutTimeline.setCycleCount(Timeline.INDEFINITE);
                logoutTimeline.play();
            }

            DataHolder.getInstance().setTransactionsListView(transactionsListView);

            // When accessing a transaction from the list, a window will be displayed with the details of the transaction.
            transactionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null){
                    DataHolder.getInstance().setTransaction(newValue);
                    showTransactionDetails(newValue.getAmount(), new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(newValue.getTransactionDate()), newValue.getClient(), newValue.getIban());
                }
            });

            currentContainer.setOnMouseClicked(e -> transactionsListView.getSelectionModel().clearSelection());
    }

    /**
     * This method displays a modal with a transaction information.
     * @param amount The transaction amount.
     * @param date The transaction date.
     * @param client The client with whom the transaction was made.
     * @param iban Iban of the account with which the transaction was made.
     */
    private void showTransactionDetails(String amount, String date, String client, String iban){
        Stage transactionDetails = new Stage();
        transactionDetails.initModality(Modality.APPLICATION_MODAL);
        transactionDetails.initStyle(StageStyle.UNDECORATED);

        Label amountLbl = new Label(amount);
        amountLbl.setStyle("-fx-font-size: 30px;");

        HBox amountBox = new HBox(amountLbl);
        amountBox.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(amountBox, new Insets(0, 20, 0, 0));

        Separator firstSeparator = new Separator();
        firstSeparator.setPrefWidth(200.0);

        Separator secondSeparator = new Separator();
        secondSeparator.setPrefWidth(200.0);

        Label dateLbl = new Label(date);
        Label clientLbl = new Label(client);
        clientLbl.setStyle("-fx-font-size: 30;");

        Label ibanLbl = new Label(iban);
        ibanLbl.setStyle("-fx-font-size: 30;");

        VBox detailsBox = new VBox(dateLbl, clientLbl, ibanLbl);
        detailsBox.setSpacing(20.0);
        VBox.setMargin(detailsBox, new Insets(0, 0, 0, 20.0));

        JFXButton closeBtn = new JFXButton("Close");
        closeBtn.setFocusTraversable(false);
        closeBtn.setOnAction(e -> {
            transactionsListView.getSelectionModel().clearSelection();
            transactionDetails.close();
        });

        HBox buttonsBox = new HBox();

        if (ibanLbl.getText().equals("\t".repeat(5) + " ".repeat(5)) || DataHolder.getInstance().getMainUser().getId()==1){
            buttonsBox.getChildren().add(closeBtn);
            HBox.setHgrow(closeBtn, javafx.scene.layout.Priority.ALWAYS);
        } else {
            JFXButton newPayment = new JFXButton("New payment");
            newPayment.setFocusTraversable(false);
            newPayment.setOnAction(e -> {
                transactionsListView.getSelectionModel().clearSelection();
                transactionDetails.close();
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newTransaction.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));

                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());
                    stage.show();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            buttonsBox.getChildren().addAll(closeBtn, newPayment);
        }

        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(30.0);
        VBox.setMargin(buttonsBox, new Insets(30.0, 0, 0, 0));

        Insets padding = new Insets(20.0, 0, 20.0, 0);

        VBox mainVbox = new VBox();
        mainVbox.setPadding(padding);
        mainVbox.setPrefHeight(286.0);
        mainVbox.setPrefWidth(380.0);
        mainVbox.getChildren().addAll(amountBox, firstSeparator, detailsBox, secondSeparator, buttonsBox);

        Scene modalScene = new Scene(mainVbox);
        modalScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/style.css")).toExternalForm());
        transactionDetails.setScene(modalScene);

        transactionDetails.show();
    }

    /**
     * It shows a modal window with the client's information.
     */
    @FXML public void showClientDetails(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("userDetailsDialog.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            UserDetailsDialog userDetailsDialog = fxmlLoader.getController();
            userDetailsDialog.initialize(user);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(accountNumber.getScene().getWindow());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows a modal window with a form for changing the client's profile information.
     */
    @FXML public void editClientProfile() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = fxmlLoader.load();
            Profile profile = fxmlLoader.getController();
            profile.initialize(DataHolder.getInstance().getUser());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            HBox buttonsHbox = profile.getButtonsHBox();
            JFXButton closeBtn = new JFXButton("Close");
            closeBtn.setOnAction(e -> stage.close());

            buttonsHbox.getChildren().add(0, closeBtn);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);

            stage.initOwner(DataHolder.getInstance().getBorderPane().getScene().getWindow());

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method adds a new account, different from the existing one.
     */
    @FXML public void addNewAccount(){
        String style = "-fx-font-family: 'Times New Roman'; -fx-font-size: 25; -fx-font-weight: bold";
        Stage newAccDialog = new Stage();
        Label label = new Label("Select account");
        label.setStyle(style);

        ChoiceBox<AccountType> choiceBox = new ChoiceBox<>();
        choiceBox.setMinWidth(200.0);
        choiceBox.prefWidth(200.0);
        choiceBox.setStyle(style);
        choiceBox.setStyle("-fx-font-size: 20;");

        ObservableList<AccountType> types = FXCollections.observableArrayList();
        types.addAll(DataSource.getInstance().queryAnotherAccounts(user.getId()));
        choiceBox.getItems().addAll(types);

        JFXButton okBtn = new JFXButton("Ok");
        okBtn.setStyle("""
                -fx-background-color: #077369;
                -fx-text-fill: #CACECDFF;
                -fx-font-family: "Times New Roman";
                -fx-font-size: 24px;
                -fx-font-weight: bold;
                -fx-border-radius: 10;""");

        VBox vBox = new VBox(label, choiceBox, okBtn);
        vBox.setPadding(new Insets(20, 20, 20, 20));
        vBox.setSpacing(50);
        vBox.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(vBox, 400, 250);
        newAccDialog.setScene(scene);
        newAccDialog.initModality(Modality.APPLICATION_MODAL);
        newAccDialog.initOwner(accountNumber.getScene().getWindow());
        newAccDialog.show();

        okBtn.setOnAction(e -> {
            if (choiceBox.getSelectionModel().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Select an account");
                alert.show();
            } else {
                int accId = DataSource.getInstance().insertAccount(choiceBox.getSelectionModel().getSelectedItem().getId(), randomIban(), user.getId());
                if (accId>0){
                    selectAccount.getItems().add(DataSource.getInstance().queryAccountById(accId));
                    DataHolder.getInstance().getAccounts().add(DataSource.getInstance().queryAccountById(accId));

                    newAccDialog.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Account created successfully");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Couldn't create account");
                    alert.show();
                }
            }
        });
    }

    /**
     * This method shows a modal window, which let the user performing a new transaction.
     */
    @FXML public void newTransaction(){
        DataHolder.getInstance().setTransaction(null);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("newTransaction.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method withdraws an amount from the account.
     * It shows the withdrawal container to allow the user to enter the withdrawal amount.
     * Checks the validity of the entered data.
     * If entered data is valid, will perform the withdrawal operation and will hide the withdrawal container, otherwise it will display a message.
     */
    @FXML public void withdraw(){
        Account account = DataHolder.getInstance().getAccount();

        amountHbox.setVisible(true);
        amountField.clear();

        amountBtn.setText("Withdraw");
        amountBtn.setOnAction(e -> {
            validator.createCheck()
                    .dependsOn("amountField", amountField.textProperty())
                    .withMethod(c -> {
                        if (emptyField(amountField)) {
                            c.error("Required");
                        } else if (!validAmount(amountField.getText())) {
                            c.error("Please enter a valid amount");
                        }else if (Double.parseDouble(amountField.getText())>5000) {
                            c.error("You can withdraw maximum $5000.00");
                        } else if (Double.parseDouble(amountField.getText()) > account.getBalance()) {
                            c.error("No funds");
                        }
                    })
                    .decorates(amountField);
            if (validator.validate()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Are you sure want to withdraw $" + amountField.getText() + "?");

                Optional<ButtonType> option = alert.showAndWait();

                if (option.isPresent() && option.get() == ButtonType.OK) {
                    if (DataSource.getInstance().performWithdrawTransaction(account.getId(), Double.parseDouble(amountField.getText()))){
                        DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                        transactionsListView.setItems(DataHolder.getInstance().getTransactions());
                        double balance = account.getBalance() - Double.parseDouble(amountField.getText());
                        account.setBalance(balance);
                        alert.close();
                        try {
                            Thread.sleep(500);
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Withdrawal request successful");
                            Optional<ButtonType> optionalButtonType = alert.showAndWait();
                            if (optionalButtonType.isPresent() && optionalButtonType.get().equals(ButtonType.OK)){
                                successAlert.close();
                                amountHbox.setVisible(false);
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } else if (option.isPresent() && option.get() == ButtonType.CANCEL) {
                    alert.close();
                }
            }
        });
    }

    /**
     * This method adds an amount to the account
     * It shows the deposit container to allow the user to enter the deposit amount.
     * Checks the validity of the entered data.
     * If entered data is valid, will perform the deposit operation and will hide the deposit container, otherwise it will display a message.
     */
    @FXML public void deposit(){
        Account account = DataHolder.getInstance().getAccount();

        amountHbox.setVisible(true);
        amountField.clear();

        amountBtn.setText("Deposit");
        amountBtn.setOnAction(e -> {
            validator.createCheck()
                    .dependsOn("amountField", amountField.textProperty())
                    .withMethod(c -> {
                        if (emptyField(amountField)) {
                            c.error("Required");
                        } else if (!validAmount(amountField.getText())) {
                            c.error("Please enter a valid amount");
                        }else if (Double.parseDouble(amountField.getText())>10000.0) {
                            c.error("You can deposit maximum $10,000.0");
                        }
                    })
                    .decorates(amountField);

            if (validator.validate()){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Are you sure want to deposit $" + amountField.getText() + "?");

                Optional<ButtonType> option = alert.showAndWait();

                if (option.isPresent() && option.get() == ButtonType.OK) {
                    if (DataSource.getInstance().performDepositTransaction(account.getId(), Double.parseDouble(amountField.getText()))){
                        DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
                        transactionsListView.setItems(DataHolder.getInstance().getTransactions());
                        double balance = Double.parseDouble(amountField.getText()) + account.getBalance();
                        account.setBalance(balance);
                        alert.close();
                        try {
                            Thread.sleep(500);
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Successfully deposit");
                            Optional<ButtonType> optionalButtonType = alert.showAndWait();
                            if (optionalButtonType.isPresent() && optionalButtonType.get().equals(ButtonType.OK)){
                                successAlert.close();
                                amountHbox.setVisible(false);
                            }
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } else if (option.isPresent() && option.get() == ButtonType.CANCEL) {
                    alert.close();
                }
            }
        });
    }

    /**
     * This method displays a modal window with a loan request form.
     */
    @FXML public void loanRequest(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("scroll.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());

            stage.show();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This disconnects the client's account and returns to the search client window.
     */
    @FXML public void logout() {
        DataHolder.getInstance().setUser(null);
        DataHolder.getInstance().setAccount(null);
        DataHolder.getInstance().getAccounts().clear();
        DataHolder.getInstance().setLoan(null);

        if (DataHolder.getInstance().getCurrentContainerId().equals("clientProfile")){
            Menu menu = new Menu();
            menu.clientButtonHandler();
        }
    }

    /**
     * This hides the container for withdrawal and deposit operations.
     */
    private void hideAmountHbox(){
        amountHbox.setVisible(false);
    }
}

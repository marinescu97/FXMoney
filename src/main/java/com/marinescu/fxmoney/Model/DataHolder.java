package com.marinescu.fxmoney.Model;

import com.marinescu.fxmoney.ConfirmationDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a singleton class that holds instances of various objects.
 * It ensures that only one instance of the class is created and accessible throughout the application.
 */
public class DataHolder {
    /**
     * The main user in the application.
     * It can be the admin or a banker.
     */
    private User mainUser;

    /**
     * The secondary user in the application.
     * It can be a banker, when the main user is the admin, or a client, when the main user is a banker.
     */
    private User user;

    /**
     * A user's type.
     */
    private int userType;

    /**
     * The main account.
     */
    private Account account;

    /**
     * The recipient account.
     */
    private Account recipientAccount;

    /**
     * The transaction selected from the transactions' list.
     */
    private Transaction transaction;

    /**
     * The user's loan.
     */
    private Loan loan;

    /**
     * A list of user's accounts.
     */
    private ObservableList<Account> accounts = FXCollections.observableArrayList();

    /**
     * A list of user's transactions.
     */
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    /**
     * The main container.
     */
    private BorderPane borderPane;

    /**
     * A {@link ComboBox} used for selecting a chart.
     */
    private ComboBox<String> chartsComboBox;

    /**
     * A confirmation dialog.
     */
    private final ConfirmationDialog confirmationDialog = new ConfirmationDialog();

    /**
     * A menu button.
     */
    private MenuButton menuButton;

    /**
     * A list of labels.
     */
    private List<Label> labels = new ArrayList<>();

    /**
     * A list of buttons.
     */
    private List<Button> buttons = new ArrayList<>();

    /**
     * The current container's id.
     */
    private String currentContainerId;

    /**
     * A {@link ListView} used for transactions' list.
     */
    private ListView<Transaction> transactionsListView;

    /**
     * The amount of a transaction.
     */
    private double transactionAmount;

    /**
     * A table of users.
     */
    private TableView<User> usersTableView;

    /**
     * The instance of the class.
     */
    private final static DataHolder INSTANCE = new DataHolder();

    /**
     * A private constructor to prevent instantiation from outside the class.
     */
    private DataHolder() {}

    /**
     * Gets the singleton instance of the class.
     * @return The instance of the class.
     */
    public static synchronized DataHolder getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the main user.
     * @return The main user.
     */
    public User getMainUser() {
        return mainUser;
    }

    /**
     * Sets the main user.
     * @param mainUser The new main user.
     */
    public void setMainUser(User mainUser) {
        this.mainUser = mainUser;
    }

    /**
     * Gets the secondary user.
     * @return The secondary user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the secondary user.
     * @param user The new user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the user's type.
     * @return The user's type.
     */
    public int getUserType() {
        return userType;
    }

    /**
     * Sets the user's type.
     * @param userType The new user's type.
     */
    public void setUserType(int userType) {
        this.userType = userType;
    }

    /**
     * Gets the user's account.
     * @return The user's account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the user's account.
     * @param account The new user's account.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the user's loan.
     * @return The user's loan.
     */
    public Loan getLoan() {
        return loan;
    }

    /**
     * Sets the user's loan.
     * @param loan The user's loan.
     */
    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    /**
     * Gets the user's accounts list.
     * @return The user's accounts list.
     */
    public ObservableList<Account> getAccounts() {
        return accounts;
    }

    /**
     * Sets the user's accounts list with values from the database.
     * @param id The user's id.
     */
    public void setAccounts(int id) {
        accounts.clear();
        accounts.addAll(DataSource.getInstance().queryAccountsByUser(id));
    }

    /**
     * Gets account's transactions list.
     * @return The account's transactions list.
     */
    public ObservableList<Transaction> getTransactions() {
        return this.transactions;
    }

    /**
     * Sets the account's transactions list.
     * It realizes the way the transactions are displayed, based on the values taken from the database.
     * @param accountId The account's id.
     */
    public void setTransactions(int accountId) {
        this.transactions.clear();
        this.transactions.addAll(DataSource.getInstance().queryTransactions(accountId));

        for (Transaction transaction : transactions){
            if (transaction.getIban().equals("0406 4131 1928 1551")){
                if (transaction.getAmount().charAt(0) == '+'){
                    transaction.setClient("Loan approved");
                } else if (transaction.getAmount().charAt(0) == '-') {
                    transaction.setClient("Loan installment");
                }
                transaction.setIban("\t".repeat(5) + " ".repeat(5));
            } else if (transaction.getIban().equals(DataSource.getInstance().queryAccountIbanById(accountId))){
                transaction.setIban("\t".repeat(5) + " ".repeat(5));
            }
        }
    }

    /**
     * Gets the main container.
     * @return The main container.
     */
    public BorderPane getBorderPane() {
        return borderPane;
    }

    /**
     * Sets the main container.
     * @param borderPane The main container.
     */
    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }

    /**
     * Gets the charts' box.
     * @return The charts' box.
     */
    public ComboBox<String> getChartsComboBox() {
        return chartsComboBox;
    }

    /**
     * Sets the charts' box.
     * @param chartsComboBox The charts' box.
     */
    public void setChartsComboBox(ComboBox<String> chartsComboBox) {
        this.chartsComboBox = chartsComboBox;
    }

    /**
     * Gets the confirmation dialog.
     * @return The confirmation dialog.
     */
    public ConfirmationDialog getConfirmationDialog() {
        return confirmationDialog;
    }

    /**
     * Gets the menu button.
     * @return The menu button.
     */
    public MenuButton getMenuButton() {
        return menuButton;
    }

    /**
     * Sets the menu button.
     * @param menuButton The menu button.
     */
    public void setMenuButton(MenuButton menuButton) {
        this.menuButton = menuButton;
    }

    /**
     * Gets the buttons' list.
     * @return The buttons' list.
     */
    public List<Button> getButtons() {
        return buttons;
    }

    /**
     * Adds buttons to the list.
     * @param buttons The buttons.
     */
    public void setButtons(Button... buttons) {
        this.buttons.clear();
        Collections.addAll(this.buttons, buttons);
    }

    /**
     * Gets the labels' list.
     * @return The labels' list.
     */
    public List<Label> getLabels() {
        return labels;
    }

    /**
     * Adds labels to the list.
     * @param labels The labels.
     */
    public void setLabels(Label... labels) {
        this.labels.clear();
        Collections.addAll(this.labels, labels);
    }

    /**
     * Gets the current container's id.
     * @return The current container's id.
     */
    public String getCurrentContainerId() {
        return currentContainerId;
    }

    /**
     * Sets the current container's id.
     * @param currentContainerId The current container's id.
     */
    public void setCurrentContainerId(String currentContainerId) {
        this.currentContainerId = currentContainerId;
    }

    /**
     * Gets the list view for transactions.
     * @return The list view.
     */
    public ListView<Transaction> getTransactionsListView() {
        return transactionsListView;
    }

    /**
     * Sets the transactions list view.
     * @param transactionsListView The list view.
     */
    public void setTransactionsListView(ListView<Transaction> transactionsListView) {
        this.transactionsListView = transactionsListView;
    }

    /**
     * Gets the transaction.
     * @return The transaction.
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Sets the transaction.
     * @param transaction A transaction.
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Gets the recipient account.
     * @return The recipient account.
     */
    public Account getRecipientAccount() {
        return recipientAccount;
    }

    /**
     * Sets the recipient account.
     * @param recipientAccount An account.
     */
    public void setRecipientAccount(Account recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    /**
     * Gets the transaction's amount.
     * @return The transaction's amount.
     */
    public double getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the transaction's amount.
     * @param transactionAmount An amount.
     */
    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * Gets the table view for users.
     * @return The table view.
     */
    public TableView<User> getUsersTableView() {
        return usersTableView;
    }

    /**
     * Sets the table view for users.
     * @param usersTableView A table view.
     */
    public void setUsersTableView(TableView<User> usersTableView) {
        this.usersTableView = usersTableView;
    }
}

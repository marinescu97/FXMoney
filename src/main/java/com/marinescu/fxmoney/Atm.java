package com.marinescu.fxmoney;

import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.synedra.validatorfx.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The class handles the logic behind the atm.fxml file.
 */
public class Atm implements MyValidation {
    /**
     * The main root of the window.
     */
    @FXML
    private BorderPane atmBorderPane;
    /**
     * A grid container which contains the user's information.
     */
    @FXML private GridPane infoGrid;
    /**
     * A grid container that contains a form for changing the user's password.
     */
    @FXML private GridPane changePasswordGrid;
    /**
     * A VBox container.
     */
    @FXML private VBox withdrawalBox, newTransBox, confirmTransaction;
    /**
     * A text field for entering the required information.
     */
    @FXML private TextField amountField, ibanField, tAmountField;
    /**
     * A password field for changing the user's password.
     */
    @FXML private PasswordField newPass, confirmPass;
    /**
     * An ImageView that performs operations on an image with a paper money.
     */
    @FXML private ImageView money;
    /**
     * An image with a cash slot with money.
     */
    private final Image moneyImg = new Image(Objects.requireNonNull(getClass().getResource("icons/atmMoney.png")).toExternalForm());
    /**
     * An image with an empty cash slot.
     */
    private final Image noMoneyImg = new Image(Objects.requireNonNull(getClass().getResource("icons/atmNoMoney.png")).toExternalForm());
    /**
     * A label attached to a button on one side or the other of the ATM screen.
     */
    @FXML private Label leftOneLbl, leftTwoLbl, leftThreeLbl, rightOneLbl, rightTwoLbl, rightThreeLbl;
    /**
     * A label for displaying the user's information.
     */
    @FXML private Label fName, lName, email, pNumber, username;
    /**
     * A label that displays the name of the recipient client in transactions.
     */
    @FXML private Label nameLbl;
    /**
     * A label that displays an account's information.
     */
    @FXML private Label accLbl, amountLbl;
    /**
     * The buttons on either side of the ATM screen.
     */
    @FXML private Button leftOne, leftTwo, leftThree, rightOne, rightTwo, rightThree;
    /**
     * The buttons at the bottom of the ATM.
     */
    @FXML private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnDoubleZero, btnDot, btnOk, btnCancel, btnDelete, btnClear;
    /**
     * A transactions' list.
     */
    @FXML private ListView<Transaction> transactionsListView;
    /**
     * The selected option from the menu.
     */
    private String prevLabel;
    /**
     * Main menu options.
     */
    private final String[] mainMenu = {"Show balance", "Withdrawal", "New transaction", "Transactions", "Details","Change password", "Exit"};
    /**
     * The main user.
     */
    private final User user = DataHolder.getInstance().getMainUser();
    /**
     * The main user's accounts.
     */
    private List<Account> accounts = DataSource.getInstance().queryAccountsByUser(user.getId());
    /**
     * An object that validates the entered data.
     */
    private final Validator validator = new Validator();
    /**
     * A text used for the amount.
     */
    private String amount="";
    /**
     * Indexes used to browse the menu.
     */
    private int fromIndex, toIndex;

    /**
     * This method is automatically called after the FXML file has been loaded.
     *
     * It Initializes the buttons on one side and the other of the atm's screen.
     * Sets the user's information.
     * Displays the main menu.
     */
    public void initialize() {
        DataHolder.getInstance().setButtons(rightOne, rightTwo, rightThree, leftOne, leftTwo, leftThree);
        DataHolder.getInstance().setLabels(rightOneLbl, rightTwoLbl, rightThreeLbl, leftOneLbl, leftTwoLbl, leftThreeLbl);
        setCurrentAccount();
        fName.setText(user.getFirstName());
        lName.setText(user.getLastName());
        email.setText(user.getEmail());
        pNumber.setText(user.getPhoneNumber());
        username.setText(user.getUsername());
        showMainMenu();
    }

    /**
     * This method is called after the “Show balance” option was chosen.
     * It sets the selected option from the main menu.
     * Then shows the user's accounts as options instead of the main menu's options.
     */
    public void balance(){
        prevLabel = "balance";
        String[] accountsList = new String[accounts.size()+1];
        for (int i=0; i<accounts.size(); i++){
            accountsList[i] = accounts.get(i).getType();
        }
        accountsList[accounts.size()] = "Back to menu";
        showMenu(0, accountsList);
    }

    /**
     * This method displays a user's account info based on selected option from the main menu.
     * If the user selects the “Balance” option, it will display the selected account's balance.
     * If the user selects the “Transactions” option, it will display the selected account's transactions.
     * @param acc The name of the selected account.
     */
    public void showAccInfo(String acc){
        String[] menu;
        DataHolder.getInstance().setAccount(DataSource.getInstance().queryAccount(user.getId(), acc));
        if (prevLabel.equals("balance")){
            menu = new String[]{"$ " + DataHolder.getInstance().getAccount().getBalance(), "" , "Back to menu"};
            showMenu(0, menu);
        } else if (prevLabel.equals("transactions")) {
            DataHolder.getInstance().setTransactions(DataHolder.getInstance().getAccount().getId());
            if (DataHolder.getInstance().getTransactions().isEmpty()){
                menu = new String[]{"", "", "Back to menu"};
                transactionsListView.setPlaceholder(new Label("No transactions"));
            } else {
                menu = new String[]{"▲", "▼", "Back to menu"};
                transactionsListView.setItems(DataHolder.getInstance().getTransactions());
                for (int i=0; i<transactionsListView.getItems().size(); i++){
                    if (transactionsListView.getItems().get(i).getIban().length()==10){
                        transactionsListView.getItems().get(i).setIban("\t".repeat(4) + " ".repeat(3));
                    }
                }
                fromIndex = 0;
                toIndex = 3;
            }
            showMenu(0, menu);
            transactionsListView.setVisible(true);
        }
    }

    /**
     * This method scrolls up the list of transactions.
     */
    public void scrollUp(){
        if (!DataHolder.getInstance().getTransactions().isEmpty() && fromIndex!=0){
            transactionsListView.scrollTo(fromIndex-1);
            fromIndex-=1;
            toIndex = fromIndex + 3;
        }
    }

    /**
     * This method scrolls down the list of transactions.
     */
    public void scrollDown(){
        if (!DataHolder.getInstance().getTransactions().isEmpty() && toIndex!=DataHolder.getInstance().getTransactions().size()-1){
            transactionsListView.scrollTo(fromIndex+1);
            fromIndex+=1;
            toIndex = fromIndex + 3;
        }
    }

    /**
     * This sets the functionality of the buttons at the bottom of the ATM.
     */
    public void setButtons(){
        btnOne.setOnAction(e -> insertNumber("1"));
        btnTwo.setOnAction(e -> insertNumber("2"));
        btnThree.setOnAction(e -> insertNumber("3"));
        btnFour.setOnAction(e -> insertNumber("4"));
        btnFive.setOnAction(e -> insertNumber("5"));
        btnSix.setOnAction(e -> insertNumber("6"));
        btnSeven.setOnAction(e -> insertNumber("7"));
        btnEight.setOnAction(e -> insertNumber("8"));
        btnNine.setOnAction(e -> insertNumber("9"));
        btnZero.setOnAction(e -> insertNumber("0"));
        btnDot.setOnAction(e -> insertNumber("."));
        btnDoubleZero.setOnAction(e -> insertNumber("00"));
        btnCancel.setOnAction(e -> showMenu(0, mainMenu));
        if (prevLabel.equals("withdrawal")){
            btnClear.setOnAction(e -> amountField.clear());
            btnDelete.setOnAction(e -> {
                amount = amount.substring(0, amount.length()-1);
                amountField.setText(amount);
            });
            btnOk.setOnAction(e -> {
                int accId = 0;
                double balance=0;
                for (Account account : accounts){
                    if (account.getType().equals("Current")){
                        accId = account.getId();
                        balance = account.getBalance();
                    }
                }

                int count = 0;
                for (int i=0; i<amount.length(); i++){
                    if (amount.charAt(i)=='.'){
                        count++;
                    }
                }
                if (count<=1) {
                    double am = Double.parseDouble(amount);
                    String[] menu = {"", "", "Back to menu"};
                    if (am > balance) {
                        menu[0] = "No funds";
                        showMenu(0, menu);
                    } else {
                        if (DataSource.getInstance().performWithdrawTransaction(accId, am)) {
                            accounts = DataSource.getInstance().queryAccountsByUser(user.getId());
                            menu[0] = "Transaction performed";
                            showMenu(0, menu);

                            Task<Void> sleeper = new Task<>() {
                                @Override
                                protected Void call() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException ignored) {
                                    }
                                    return null;
                                }
                            };

                            sleeper.setOnSucceeded(workerStateEvent -> money.setImage(moneyImg));

                            new Thread(sleeper).start();
                        } else {
                            menu[0] = "Something went wrong";
                            showMenu(0, menu);
                        }
                    }
                }
            });
        } else if (prevLabel.equals("newTransaction")) {
            setCurrentAccount();
            if (ibanField.isFocusTraversable()){
                btnClear.setOnAction(e -> {
                    amount = "";
                    ibanField.setText(amount);
                });
                btnDelete.setOnAction(e -> {
                    if (!ibanField.getText().isEmpty()){
                        amount = amount.substring(0, amount.length()-1);
                        ibanField.setText(amount);
                    }
                });
                btnOk.setOnAction(e -> {
                    if (!validIban(ibanField.getText().trim())){
                        StringBuilder iban = new StringBuilder();
                        for (int i=0; i<ibanField.getText().length(); i++){
                            if (i%4==0 && i!=0){
                                iban.append(" ");
                            }
                            iban.append(ibanField.getText().charAt(i));
                        }
                        ibanField.setText(iban.toString());
                    }

                    tAmountField.requestFocus();
                    ibanField.setFocusTraversable(false);
                    tAmountField.setFocusTraversable(true);
                    amount = "";
                    setButtons();
                });
            } else if (tAmountField.isFocusTraversable()) {
                btnClear.setOnAction(e -> {
                    amount = "";
                    tAmountField.setText(amount);
                });
                btnDelete.setOnAction(e -> {
                    amount = amount.substring(0, amount.length()-1);
                    tAmountField.setText(amount);
                });
                btnOk.setOnAction(e -> {
                    validator.createCheck()
                            .dependsOn("ibanField", ibanField.textProperty())
                            .withMethod(c -> {
                                if (emptyField(ibanField)){
                                    c.error("Required");
                                } else if (!validIban(ibanField.getText())) {
                                    c.error("Please enter a valid iban");
                                } else if (ibanField.getText().trim().equals(DataSource.getInstance().queryAccount(user.getId(), "Current").getIban())) {
                                    c.error("The iban should be a different one.");
                                } else if (accountMatch(DataSource.getInstance().queryAccounts(DataHolder.getInstance().getAccount().getIban()), formatIban(ibanField.getText().trim()))==null) {
                                    c.error("Account does not exist");
                                }
                            })
                            .decorates(ibanField);

                    validator.createCheck()
                            .dependsOn("tAmountField", tAmountField.textProperty())
                            .withMethod(c -> {
                                if (emptyField(tAmountField)) {
                                    c.error("Required");
                                } else if (!validAmount(tAmountField.getText())) {
                                    c.error("Please enter a valid amount");
                                }else if (Double.parseDouble(tAmountField.getText())>5000) {
                                    c.error("You can transfer maximum $5000.00");
                                } else if (Double.parseDouble(tAmountField.getText()) > DataHolder.getInstance().getAccount().getBalance()) {
                                    c.error("No funds");
                                }
                            })
                            .decorates(tAmountField);

                    if (validator.validate()) {
                        Account toAcc = DataSource.getInstance().queryAccountByIban(formatIban(ibanField.getText().trim()), DataHolder.getInstance().getAccount().getId());
                        System.out.println(toAcc.getIban());
                        if(toAcc!=null){
                            newTransBox.setVisible(false);
                            nameLbl.setText(DataSource.getInstance().queryUserNameByAccount(toAcc.getClient()));
                            accLbl.setText(toAcc.getIban());
                            amountLbl.setText("$ " + tAmountField.getText());
                            showMenu(0,new String[]{"", "", "Transfer", "", "", "Cancel"});
                            confirmTransaction.setVisible(true);

                            leftThree.setOnAction(event -> showMenu(0, mainMenu));
                            rightThree.setOnAction(event -> {
                                String[] confirmation = {"Transaction performed", "", "Back to menu"};
                                if (DataSource.getInstance().performTransaction(DataHolder.getInstance().getAccount().getId(), toAcc.getId(), Double.parseDouble(tAmountField.getText()))){
                                    showMenu(0, confirmation);
                                } else {
                                    confirmation[0] = "Something went wrong, try again later";
                                    showMenu(0, confirmation);
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /**
     * This method is called after the “Withdrawal” option was chosen.
     * It sets the selected option from the main menu.
     * Then displays a form for withdraw an amount from the “Current” account.
     */
    public void withdrawal(){
        prevLabel="withdrawal";
        showBackMenu();
        withdrawalBox.setVisible(true);
        setButtons();
    }

    /**
     * This method is called after the “New transaction” option was chosen.
     * It sets the selected option from the main menu.
     * Then displays a form for performing a new transaction.
     */
    public void newTransaction(){
        prevLabel = "newTransaction";
        showBackMenu();

        newTransBox.setVisible(true);
        amount = "";
        ibanField.setText(amount);
        tAmountField.setText(amount);
        ibanField.requestFocus();
        ibanField.setFocusTraversable(true);
        tAmountField.setFocusTraversable(false);
        setButtons();
    }

    /**
     * This method is called after the “Transactions” option was chosen.
     * It sets the selected option from the main menu.
     * Then shows the user's accounts as options instead of the main menu's options.
     */
    public void transactions(){
        prevLabel = "transactions";
        List<Account> accounts = DataSource.getInstance().queryAccountsByUser(user.getId());
        String[] accountsList = new String[accounts.size()+1];
        for (int i=0; i<accounts.size(); i++){
            accountsList[i] = accounts.get(i).getType();
        }
        accountsList[accounts.size()] = "Back to menu";
        showMenu(0, accountsList);
    }

    /**
     * This method displays the user's information.
     */
    public void details(){
        showBackMenu();
        infoGrid.setVisible(true);
    }

    /**
     * It displays the elements required to change the password and changes the user's password.
     */
    public void changePassword(){
        String[] menu = {"", "Ok", "Back to menu"};
        showMenu(0, menu);
        newPass.clear();
        confirmPass.clear();
        changePasswordGrid.setVisible(true);
        rightTwo.setOnAction(e -> performChangePassword());
        changePasswordGrid.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)){
                performChangePassword();
            }
        });
    }

    /**
     * This method changes the user's password.
     */
    public void performChangePassword(){
        validator.createCheck()
                .dependsOn("newPass", this.newPass.textProperty())
                .withMethod(c -> {
                    if (emptyField(this.newPass)){
                        c.error("Required");
                    } else if (!validPassword(this.newPass.getText().trim())) {
                        c.error("Please enter a strong password between [8-20] length");
                    }
                })
                .decorates(this.newPass);

        validator.createCheck()
                .dependsOn("confirmPass", confirmPass.textProperty())
                .withMethod(c -> {
                    if (!passwordMatch(newPass.getText().trim(), confirmPass.getText().trim())) {
                        c.error("Passwords don't match");
                    }
                })
                .decorates(confirmPass);

        if (validator.validate()){
            if (DataSource.getInstance().updateUserPassword(user.getId(), newPass.getText().trim())){
                newPass.setText("");
                confirmPass.setText("");
                changePasswordGrid.setVisible(false);
                String[] menu = {"Password changed successfully", "", "Back to menu"};
                showMenu(0, menu);
            } else {
                newPass.setText("");
                confirmPass.setText("");
                changePasswordGrid.setVisible(false);
                String[] menu = {"Something went wrong", "", "Back to menu"};
                showMenu(0, menu);
            }
        }
    }

    /**
     * It logs out the user.
     */
    public void exit() {
        try {
            DataHolder.getInstance().setMainUser(null);
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("loginPage.fxml")));
            Stage mainStage = (Stage) atmBorderPane.getScene().getWindow();
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
     * This method sets the functionality of the buttons on either side of the ATM screen, based on the options next to each button.
     * @param label The option next to the button.
     */
    public void setButtonAction(Label label){
        for (Button button : DataHolder.getInstance().getButtons()){
            if (label.getId().equals(button.getId()+"Lbl")){
                switch (label.getText()) {
                    case "Back to menu" ->
                            button.setOnAction(event -> showMenu(0, mainMenu));
                    case "Show balance" -> button.setOnAction(event -> balance());
                    case "Withdrawal" -> button.setOnAction(event -> withdrawal());
                    case "New transaction" -> button.setOnAction(event -> newTransaction());
                    case "Transactions" -> button.setOnAction(event -> transactions());
                    case "Details" -> button.setOnAction(event -> details());
                    case "Change password" -> button.setOnAction(event -> changePassword());
                    case "Current" -> button.setOnAction(event -> showAccInfo("Current"));
                    case "Deposit" -> button.setOnAction(event -> showAccInfo("Deposit"));
                    case "▲" -> button.setOnAction(event -> scrollUp());
                    case "▼" -> button.setOnAction(event -> scrollDown());
                    case "Exit" -> button.setOnAction(event -> exit());
                }
            }
        }
    }

    /**
     * This method displays a menu on the ATM screen based on the index at which the display starts and a number of option items.
     * @param currentIndex The index of the label from which the display of options starts.
     * @param array The options.
     */
    public void showMenu(int currentIndex,String[] array){
        confirmTransaction.setVisible(false);
        newTransBox.setVisible(false);
        transactionsListView.setVisible(false);
        money.setImage(noMoneyImg);
        clearLabels();
        disableButtons();
        infoGrid.setVisible(false);
        changePasswordGrid.setVisible(false);
        withdrawalBox.setVisible(false);
        List<Label> labels = DataHolder.getInstance().getLabels();
        int remainLength = array.length - currentIndex;
        if (remainLength<=labels.size() && array.length<=labels.size()){
            for (int i=0; i<labels.size(); i++){
                if (i<remainLength){
                    labels.get(i).setText(array[currentIndex]);
                    setButtonAction(labels.get(i));
                    currentIndex++;
                }
            }
        }
        else if (remainLength<labels.size()){
            int index = 0;
            for (int i=0; i<labels.size(); i++){
                if (labels.get(i).getId().equals("leftThreeLbl")){
                    labels.get(i).setText("◄");
                }else if (i<remainLength){
                    labels.get(i).setText(array[currentIndex]);
                    setButtonAction(labels.get(i));
                    currentIndex++;
                    index++;
                }
            }
            int finalIndex = currentIndex - index - 5;
            leftThree.setOnAction(event -> {
                if (finalIndex==0){
                    showMenu(finalIndex, array);
                } else {
                    showMenu(finalIndex+1, array);
                }
            });
        }
        else {
            int index = 0;
            for (Label label : labels) {
                if (label.getId().equals("rightThreeLbl")) {
                    label.setText("►");
                } else if (label.getId().equals("leftThreeLbl") && currentIndex >= 6) {
                    label.setText("◄");
                    int finalIndex = currentIndex - index - 5;
                    leftThree.setOnAction(event -> {
                        if (finalIndex==0){
                            showMenu(finalIndex, array);
                        } else {
                            showMenu(finalIndex+1, array);
                        }
                    });
                } else {
                    label.setText(array[currentIndex]);
                    setButtonAction(label);
                    currentIndex++;
                    index++;
                }
            }
            int finalCurrentIndex1 = currentIndex;
            rightThree.setOnAction(event -> showMenu(finalCurrentIndex1, array));
        }
    }

    /**
     * It deletes the events of the buttons on either side of the ATM screen.
     */
    public void disableButtons(){
        for (Button button : DataHolder.getInstance().getButtons()){
            button.setOnAction(null);
        }
    }

    /**
     * Clears all labels of the ATM screen.
     */
    public void clearLabels(){
        for (Label label : DataHolder.getInstance().getLabels()) {
            label.setText("");
        }
    }

    /**
     * Displays the main menu.
     */
    public void showMainMenu(){
        showMenu(0, mainMenu);
    }

    /**
     * Displays on the ATM screen the option to return to the main menu.
     * Assigns the functionality corresponding to the option.
     */
    public void showBackMenu(){
        String[] menu = {"", "", "Back to menu"};
        showMenu(0, menu);
    }

    /**
     * This method adds to a string of numbers, the number selected by the buttons at the bottom of the ATM.
     * @param num The selected number.
     */
    public void insertNumber(String num){
        amount+=num;
        switch (prevLabel) {
            case "withdrawal" -> amountField.setText(amount);
            case "newTransaction" ->
            {
                if (ibanField.isFocusTraversable()){
                    ibanField.setText(amount);
                } else if (tAmountField.isFocusTraversable()) {
                    tAmountField.setText(amount);
                }
            }
        }
    }

    /**
     * Sets the current account as the main account.
     */
    public void setCurrentAccount(){
        DataHolder.getInstance().setAccount(DataSource.getInstance().queryAccount(user.getId(), "Current"));
    }

    /**
     * Gets the formatted text of an iban.
     * @param iban The iban text.
     * @return The formatted iban.
     */
    private String formatIban(String iban){
        if (iban.length() == 16){
            return "%s %s %s %s".formatted(iban.substring(0,4), iban.substring(4,8),
                    iban.substring(8,12), iban.substring(12));
        }
        return null;
    }
}

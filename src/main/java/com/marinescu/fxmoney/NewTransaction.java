package com.marinescu.fxmoney;

import com.jfoenix.controls.JFXButton;
import com.marinescu.fxmoney.Interfaces.MyValidation;
import com.marinescu.fxmoney.Model.Account;
import com.marinescu.fxmoney.Model.DataHolder;
import com.marinescu.fxmoney.Model.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.synedra.validatorfx.Validator;

import java.io.IOException;

/**
 * This class handles the logic behind the newTransaction.fxml file.
 */
public class NewTransaction implements MyValidation {
    /**
     * Label that holds the current client's name.
     */
    @FXML private Label nameLbl;
    /**
     * Label that holds the current account's iban.
     */
    @FXML private Label ibanLbl;

    /**
     * Text field for entering the IBAN of the recipient account.
     */
    @FXML private TextField ibanField;
    /**
     * Text field for entering the desired amount.
     */
    @FXML private TextField amountField;
    /**
     * The button that performs the new transaction.
     */
    @FXML private JFXButton okBtn;
    /**
     * The object that validates the entered data.
     */
    private final Validator validator = new Validator();
    /**
     * A HashMap of all accounts that not the current account.
     */
    private ObservableMap<String, String> suggestionsMap;
    /**
     * A List of all possible accounts.
     */
    private ListView<String> suggestionsListView;
    /**
     * A popup with the list of all possible accounts.
     */
    private CustomPopup suggestionsPopup;

    /**
     * This method is automatically called after the newTransaction.fxml file has been loaded.
     * If a transaction has been selected from the transactions list, it will automatically set the recipient account.
     * This also allows auto-completion of the iban field in case of entering an existing iban.
     */
    public void initialize() {
        // Sets labels with the current account information.
        nameLbl.setText(DataSource.getInstance().queryUserNameByAccount(DataHolder.getInstance().getAccount().getClient()));
        ibanLbl.setText(DataHolder.getInstance().getAccount().getIban());

        // If a transaction has been selected from the transactions list, it will automatically set the iban field with the recipient account.
        if (DataHolder.getInstance().getTransaction()!=null){
            ibanField.setText(DataHolder.getInstance().getTransaction().getIban());
            ibanField.setEditable(false);
            ibanField.setFocusTraversable(false);
            amountField.requestFocus();
        } else {
            ibanField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.TAB)){
                    changeText();
                    amountField.requestFocus();
                }
            });
        }

        // Sets the autocomplete popup for the iban field.
        suggestionsMap = FXCollections.observableHashMap();
        suggestionsMap.putAll(DataSource.getInstance().queryAutocompleteAccounts(DataHolder.getInstance().getAccount().getIban()));

        suggestionsListView = new ListView<>();

        suggestionsPopup = new CustomPopup(suggestionsListView);

        ibanField.textProperty().addListener((observable, oldValue, newValue) -> {
            suggestionsListView.getItems().clear();
            suggestionsMap.forEach((key, value) -> {
                String firstName = value.split(" ")[0];
                String lastName = value.split(" ")[1];
                if (key.toLowerCase().startsWith(newValue.toLowerCase()) || firstName.toLowerCase().startsWith(newValue.toLowerCase()) || lastName.toLowerCase().startsWith(newValue.toLowerCase())) {
                    suggestionsListView.getItems().add(value + "\n" + key);
                }
            });
            suggestionsListView.getSelectionModel().selectFirst();

            if (!newValue.isEmpty() && !suggestionsListView.getItems().isEmpty()) {
                suggestionsPopup.show(ibanField);
            } else {
                suggestionsPopup.hide();
            }
        });

        suggestionsListView.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionsListView.getSelectionModel().getSelectedItem();
            if (selectedSuggestion != null) {
                ibanField.setText(getKeyFromText(selectedSuggestion));
                suggestionsPopup.hide();
            }
        });

        suggestionsListView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ibanField.setText(getKeyFromText(suggestionsListView.getSelectionModel().getSelectedItem()));
                suggestionsPopup.hide();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                selectNextElement();
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                selectPreviousElement();
                event.consume();
            }
        });

        // Centers items.
        suggestionsListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setStyle("-fx-line-spacing: 5;");
                            setAlignment(javafx.geometry.Pos.CENTER);
                        }
                    }
                };
            }
        });
    }

    /**
     * Helper method to select the next element or loop to the first.
     */
    private void selectNextElement() {
        int currentIndex = suggestionsListView.getSelectionModel().getSelectedIndex();
        if (currentIndex == -1 || currentIndex == suggestionsListView.getItems().size() - 1) {
            // If no item is selected, or the last item is selected, loops to the first.
            suggestionsListView.getSelectionModel().selectFirst();
        } else {
            // Selects the next element.
            suggestionsListView.getSelectionModel().selectNext();
        }
        scrollToSelected();
    }

    /**
     * Helper method to select the previous element or loop to the last.
     */
    private void selectPreviousElement() {
        int currentIndex = suggestionsListView.getSelectionModel().getSelectedIndex();
        if (currentIndex == -1 || currentIndex == 0) {
            // If no item is selected, or the first item is selected, loop to the last.
            suggestionsListView.getSelectionModel().selectLast();
        } else {
            // Select the previous element.
            suggestionsListView.getSelectionModel().selectPrevious();
        }
        scrollToSelected();
    }

    /**
     * Helper method to scroll to the selected item.
     */
    private void scrollToSelected() {
        int selectedIndex = suggestionsListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            suggestionsListView.scrollTo(selectedIndex);
        }
    }

    /**
     * Helper method to get the iban from a text consisting of a name and an iban.
     * @param text The selected text from the autocomplete list.
     * @return The iban.
     */
    public String getKeyFromText(String text){
        return text.split("\n")[1];
    }

    /**
     * Closes the current window.
     */
    public void closeWindow(){
        DataHolder.getInstance().setTransaction(null);
        Stage stage = (Stage) okBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Change the text of the iban field to the appropriate one for an iban.
     * If there is no space after every 4 digits, this will add them.
     */
    private void changeText(){
        String iban = ibanField.getText().trim();
        if (iban.length() == 16){
            ibanField.setText("%s %s %s %s".formatted(iban.substring(0,4), iban.substring(4,8),
                    iban.substring(8,12), iban.substring(12)));
        }
    }

    /**
     * This method displays a new modal window to confirm a new transaction.
     * First, it validates the data entered the fields, and if they are valid it will open the new window, otherwise it will display error messages.
     */
    @FXML
    private void performTransaction(){
        validator.createCheck()
                .dependsOn("ibanField", ibanField.textProperty())
                .withMethod(c -> {
                    if (emptyField(ibanField)) {
                        c.error("Required");
                    } else if (!validIban(ibanField.getText())) {
                        c.error("Please enter a valid iban");
                    } else if (ibanField.getText().trim().equals(DataHolder.getInstance().getAccount().getIban())) {
                        c.error("The iban should be a different one.");
                    }
                })
                .decorates(ibanField);

        validator.createCheck()
                .dependsOn("amountField", amountField.textProperty())
                .withMethod(c -> {
                    if (emptyField(amountField)) {
                        c.error("Required");
                    } else if (!validAmount(amountField.getText())) {
                        c.error("Please enter a valid amount");
                    }else if (Double.parseDouble(amountField.getText())>5000) {
                        c.error("You can transfer maximum $5000.00");
                    } else if (Double.parseDouble(amountField.getText()) > DataHolder.getInstance().getAccount().getBalance()) {
                        c.error("No funds");
                    }
                })
                .decorates(amountField);

        if (validator.validate()){
            changeText();
            Account account = DataSource.getInstance().queryAccountByIban(ibanField.getText(), DataHolder.getInstance().getAccount().getId());
            if (account!=null){
                DataHolder.getInstance().setRecipientAccount(account);
                DataHolder.getInstance().setTransactionAmount(Double.parseDouble(amountField.getText().trim()));
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("confirmTransaction.fxml"));
                    Parent root = fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));

                    closeWindow();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initOwner(DataHolder.getInstance().getBorderPane().getCenter().getScene().getWindow());
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Account does not exist");
                alert.show();
            }
        }
    }

    /**
     * A helper class to show an autocomplete popup list.
     */
    private static class CustomPopup {
        // The popup.
        private final Popup popup;
        // The list from the popup.
        private final ListView<String> content;

        /**
         * The class' controller.
         * It creates a new popup and adds the received list to it.
         * @param content The list from the popup.
         */
        public CustomPopup(ListView<String> content) {
            this.popup = new Popup();
            this.content = content;
            popup.getContent().add(content);
        }

        /**
         * This method shows the popup.
         * It also sets how the list is displayed.
         * @param textField The popup's field.
         */
        public void show(TextField textField) {
            Point2D pos = textField.localToScreen(0, textField.getHeight());
            popup.show(textField, pos.getX(), pos.getY());
            content.setPrefHeight(130);
            content.setPrefWidth(textField.getWidth());
            content.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            content.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-background-color: transparent;");
        }

        /**
         * It hides the popup.
         */
        public void hide() {
            popup.hide();
        }
    }
}

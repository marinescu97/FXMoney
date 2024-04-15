package com.marinescu.fxmoney.Model;

import com.marinescu.fxmoney.Interfaces.LoanInterface;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class provides a singleton instance of a database connection to the MySQL database.
 * It implements the {@code LoanInterface} interface for accessing loan-related data.
 */
public class DataSource implements LoanInterface {
    /**
     * The URL of the database.
     */
    public static final String url = "jdbc:mysql://localhost:3306/your_database_name";

    /**
     * The username used to connect to the database.
     */
    public static final String user = "your_username";

    /**
     * The password used to connect to the database.
     */
    public static final String password = "your_password";

    /**
     * The instance of the class.
     */
    private static final DataSource instance = new DataSource();

    /**
     * The constructor used to prevent instantiation outside the class.
     */
    private DataSource(){
    }

    /**
     * Gets the instance of the class.
     * @return The instance of the class.
     */
    public static DataSource getInstance(){
        return instance;
    }

    /**
     * A {@link Connection} object.
     */
    private Connection con;

    /**
     * This method connects the application to the database.
     * @return True if the database was connected, otherwise returns false.
     */
    public boolean connect(){
        try {
            con = DriverManager.getConnection(url, user, password);
            return true;
        } catch (Exception e){
            System.out.println("Couldn't connect to database: " + e.getMessage());
            return false;
        }
    }

    /**
     * This closes the connection to the database.
     */
    public void close(){
        try {
            if (con != null){
                con.close();
            }
        } catch (SQLException e){
            System.out.println("Couldn't close connection, " + e.getMessage());
        }
    }

    /**
     * This method adds all tables' name from the database, but the “user_types" and "account_types" tables, into a list.
     * @return The list of the tables.
     */
    public List<String> queryTables(){
        try(Statement stmt = con.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SHOW TABLES WHERE `Tables_in_bank` NOT LIKE ('%types')");
            List<String> tables = new ArrayList<>();
            while (resultSet.next()){
                tables.add(resultSet.getString(1));
            }
            return tables;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets a list of a table's ids.
     * @param table The table from which the ids are selected.
     * @return The list of ids.
     */
    public List<Integer> queryId(String table){
        try(Statement stmt = con.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT id FROM " + table);
            List<Integer> id = new ArrayList<>();
            while (resultSet.next()){
                id.add(resultSet.getInt(1));
            }
            return id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * It gets the last id from a table.
     * @param table The table from witch the last id is selected.
     * @return The last id of the table.
     */
    private int queryLastId(String table){
        try(Statement stmt = con.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT MAX(id)+1 FROM " + table);
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method updates a table and sets its auto_increment value to last id of the table + 1.
     * @param table The table that is updated.
     */
    public void updateAutoIncrement(String table){
        try {
            int lastId = queryLastId(table);
            if (lastId > 0){
                PreparedStatement updateAutoIncrement = con.prepareStatement("ALTER TABLE " + table + " AUTO_INCREMENT=?");
                updateAutoIncrement.setInt(1, lastId);
                updateAutoIncrement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method updates a table and sets an id with a new one.
     * @param table The table which is updated.
     * @param id The new id.
     * @param existingId The existing id.
     */
    public void updateTableIds(String table, int id, int existingId){
        try(PreparedStatement updateTableStmt = con.prepareStatement("UPDATE " + table + " SET id = ? WHERE id = ?")) {
            con.setAutoCommit(false);
            updateTableStmt.setInt(1, id);
            updateTableStmt.setInt(2, existingId);
            updateTableStmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ignored) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * It gets a user by its username (or email) and password.
     * @param username User's username or email.
     * @param password User's password.
     * @return The user.
     */
    public User queryUserLogin(String username, String password){
        try(PreparedStatement queryUserLogin = con.prepareStatement("SELECT * FROM users WHERE (username=? OR email=?) AND password=?")) {
            queryUserLogin.setString(1, username);
            queryUserLogin.setString(2, username);
            queryUserLogin.setString(3, encryptPassword(password));
            ResultSet resultSet = queryUserLogin.executeQuery();
            if (resultSet.next()){
                return new User(resultSet.getInt("id"), resultSet.getString("first_name"), resultSet.getString("last_name"),
                                    resultSet.getString("email"), resultSet.getString("phone_number"), resultSet.getString("pin"),
                                    resultSet.getDate("date_of_birth"), resultSet.getString("username"), resultSet.getString("password"),
                                    resultSet.getInt("type"), resultSet.getInt("loan_eligibility"), resultSet.getTimestamp("created_at"));
            }
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets a user by its id.
     * @param userId User's id.
     * @return The user.
     */
    public User queryUserById(int userId){
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE id=?")) {
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()){
                return new User(resultSet.getInt("id"), resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("email"), resultSet.getString("phone_number"), resultSet.getString("pin"),
                        resultSet.getDate("date_of_birth"), resultSet.getString("username"), resultSet.getString("password"),
                        resultSet.getInt("type"), resultSet.getInt("loan_eligibility"), resultSet.getTimestamp("created_at"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets the number of users who have a given email.
     * @param email The given email.
     * @return The number of users.
     */
    public int queryUserEmail(String email) {
        try(PreparedStatement queryUserEmail = con.prepareStatement("SELECT count(id) FROM users WHERE email=?")) {
            queryUserEmail.setString(1, email);
            ResultSet results = queryUserEmail.executeQuery();
            if (results.next()){
                return results.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * It gets the number of users who have a given phone number.
     * @param phoneNumber The given phone number.
     * @return The number of users.
     */
    public int queryUserPhoneNumber(String phoneNumber) {
        try(PreparedStatement queryUserPhoneNumber = con.prepareStatement("SELECT count(id) FROM users WHERE phone_number=?")) {
            queryUserPhoneNumber.setString(1, phoneNumber);
            ResultSet results = queryUserPhoneNumber.executeQuery();
            if (results.next()){
                return results.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This gets the number of users who have a given pin.
     * @param pin The given pin.
     * @return The number of users.
     */
    public int queryUserPin(String pin) {
        try(PreparedStatement queryUserPin = con.prepareStatement("SELECT count(id) FROM users WHERE pin=?")) {
            queryUserPin.setString(1, pin);
            ResultSet results = queryUserPin.executeQuery();
            if (results.next()){
                return results.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets the number of users who have a given username.
     * @param username The given username.
     * @return The number of users.
     */
    public int queryUserName(String username) {
        try(PreparedStatement queryUserName = con.prepareStatement("SELECT count(id) FROM users WHERE username=?")) {
            queryUserName.setString(1, username);
            ResultSet results = queryUserName.executeQuery();
            if (results.next()){
                return results.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets the number of users who have a given password.
     * @param password The given password.
     * @return The number of users.
     */
    public int queryUserPassword(String password) {
        try(PreparedStatement queryUserPassword = con.prepareStatement("SELECT count(id) FROM users WHERE password=?")) {
            queryUserPassword.setString(1, password);
            ResultSet results = queryUserPassword.executeQuery();
            if (results.next()){
                return results.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method searches for information about a specific client, through his pin.
     * @param pin The client's pin.
     * @return The client.
     */
    public User queryUserByPin(String pin){
        try(PreparedStatement query = con.prepareStatement("SELECT * FROM users WHERE pin=? AND type=3")){
            query.setString(1, pin);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new User(resultSet.getInt("id"), resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("email"), resultSet.getString("phone_number"), resultSet.getString("pin"),
                        resultSet.getDate("date_of_birth"), resultSet.getString("username"), resultSet.getString("password"),
                        resultSet.getInt("type"), resultSet.getInt("loan_eligibility"), resultSet.getTimestamp("created_at"));
            }
            return null;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets the user's full name from its account, by his id.
     * @param id The user's id.
     * @return The user's full name.
     */
    public String queryUserNameByAccount(int id){
        try(PreparedStatement query = con.prepareStatement("SELECT concat(u.first_name,' ', u.last_name) FROM users u JOIN accounts a ON u.id = a.client WHERE a.client=?")) {
            query.setInt(1, id);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return resultSet.getString(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method checks if there is a client in the loans table, based on loan's id.
     * @param loanId The loan's id.
     * @return 1 if there is a client for the loan, otherwise 0.
     */
    private int queryUserByLoan(int loanId){
        try(PreparedStatement query = con.prepareStatement("SELECT client FROM loans WHERE id=?")) {
            query.setInt(1, loanId);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets a list of users, by a given type.
     * @param type The given type.
     * @return The list of users.
     */
    public List<User> queryUserByType(int type){
        try(PreparedStatement query = con.prepareStatement("SELECT * FROM users WHERE type=?")) {
            query.setInt(1, type);
            ResultSet resultSet = query.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()){
                users.add(new User(resultSet.getInt("id"), resultSet.getString("first_name"), resultSet.getString("last_name"),
                        resultSet.getString("email"), resultSet.getString("phone_number"), resultSet.getString("pin"),
                        resultSet.getDate("date_of_birth"), resultSet.getString("username"), resultSet.getString("password"),
                        resultSet.getInt("type"), resultSet.getInt("loan_eligibility"), resultSet.getTimestamp("created_at")));
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method gets a map of users' count grouped by their types.
     * The users' type will be the key of the map.
     * The number of users grouped by type will be the value of the map.
     * @return A map of users' count.
     */
    public Map<String, Integer> queryUsersChart(){
        try(Statement query = con.createStatement()) {
            ResultSet resultSet = query.executeQuery("SELECT ut.type, count(u.type) FROM users u JOIN user_types ut ON ut.id = u.type WHERE ut.type!='Admin' GROUP BY ut.type");
            Map<String, Integer> users = new HashMap<>();
            while (resultSet.next()){
                users.put(resultSet.getString(1), resultSet.getInt(2));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method searches for information about a client's current account.
     * @param userId The client's id.
     * @return The account.
     */
    public Account queryAccount(int userId){
        try(PreparedStatement queryCurrentAccount = con.prepareStatement("select a.*, at.type as name from accounts as a inner join account_types as at on a.type = at.id where a.client=? and at.type='Current'")) {
            queryCurrentAccount.setInt(1,userId);
            ResultSet resultSet = queryCurrentAccount.executeQuery();
            if (resultSet.next()){
                return new Account(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("iban"),
                        resultSet.getDouble("balance"), userId);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an account by its client and type.
     * @param userId The client's id.
     * @param accName Account type.
     * @return The account.
     */
    public Account queryAccount(int userId, String accName){
        try(PreparedStatement query = con.prepareStatement("SELECT a.*, at.type AS name FROM accounts AS a INNER JOIN account_types AS at ON a.type = at.id WHERE a.client=? AND at.type=?")) {
            query.setInt(1, userId);
            query.setString(2, accName);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new Account(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("iban"),
                        resultSet.getDouble("balance"), userId);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the number of accounts that have a given iban.
     * @param iban The given iban.
     * @return The number of accounts.
     */
    public int queryAccountIban(String iban){
        try(PreparedStatement query = con.prepareStatement("SELECT count(iban) FROM accounts WHERE iban=?")) {
            query.setString(1, iban);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            return -1;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the account that has a given iban, but has not a given id.
     * @param iban The account's iban.
     * @param accountId The account's id.
     * @return The account.
     */
    public Account queryAccountByIban(String iban, int accountId){
        try(PreparedStatement query = con.prepareStatement("SELECT a.id, t.type, a.iban, a.balance, a.client FROM accounts AS a JOIN account_types AS t ON a.type=t.id WHERE a.iban=? AND a.id!=?")) {
            query.setString(1, iban);
            query.setInt(2, accountId);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new Account(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                                    resultSet.getDouble(4), resultSet.getInt(5));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an account by its iban.
     * @param iban The account's iban.
     * @return The account.
     */
    public Account queryAccountByIban(String iban){
        try(PreparedStatement query = con.prepareStatement("SELECT a.id, t.type, a.iban, a.balance, a.client FROM accounts AS a JOIN account_types AS t ON a.type=t.id WHERE a.iban=?")) {
            query.setString(1, iban);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new Account(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getDouble(4), resultSet.getInt(5));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an account bt its id.
     * @param id The account's id.
     * @return The account.
     */
    public Account queryAccountById(int id){
        try(PreparedStatement query = con.prepareStatement("SELECT a.id as id, at.type as name, a.iban as iban, a.balance as balance, a.client as client FROM accounts a JOIN account_types at ON at.id = a.type WHERE a.id=?")) {
            query.setInt(1, id);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new Account(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("iban"),
                        resultSet.getDouble("balance"), resultSet.getInt("client"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an account's iban by its id.
     * @param id The account's id.
     * @return The account's iban.
     */
    public String queryAccountIbanById(int id){
        try(PreparedStatement query = con.prepareStatement("SELECT iban FROM accounts WHERE id=?")) {
            query.setInt(1, id);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()){
                return resultSet.getString(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of all accounts' iban, different from the given one.
     * @param iban The given iban.
     * @return The list of accounts' iban.
     */
    public List<String> queryAccounts(String iban){
        try(PreparedStatement query = con.prepareStatement("SELECT iban FROM accounts WHERE iban!=?")) {
            query.setString(1, iban);
            List<String> accounts = new ArrayList<>();
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()){
                accounts.add(resultSet.getString(1));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of accounts, owned by a given user.
     * @param userId The user's id.
     * @return The list of accounts.
     */
    public ObservableList<Account> queryAccountsByUser(int userId){
        try(PreparedStatement queryAccountByUser = con.prepareStatement("SELECT a.*, at.type AS name FROM accounts AS a INNER JOIN account_types AS at ON a.type = at.id WHERE a.client=?")) {
            queryAccountByUser.setInt(1, userId);
            ResultSet resultSet = queryAccountByUser.executeQuery();
            ObservableList<Account> accounts = FXCollections.observableArrayList();
            while (resultSet.next()){
                accounts.add(new Account(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("iban"),
                        resultSet.getDouble("balance"), userId));
            }
            return accounts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Gets an account balance, by its id.
     * @param accountId The account's id.
     * @return The account's balance.
     */
    public double queryBalanceByAccount(int accountId){
        try(PreparedStatement query = con.prepareStatement("SELECT balance FROM accounts WHERE id=?")) {
            query.setInt(1, accountId);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets another account types, different from the one owned by a client.
     * @param clientId The client's id.
     * @return The list of accounts' types.
     */
    public List<AccountType> queryAnotherAccounts(int clientId){
        try(PreparedStatement query= con.prepareStatement("select id, type from account_types where id not in (select type from accounts where client=?)")) {
            query.setInt(1, clientId);
            ResultSet resultSet = query.executeQuery();
            List<AccountType> types = new ArrayList<>();
            while (resultSet.next()){
                AccountType type = new AccountType(resultSet.getInt(1), resultSet.getString(2));
                types.add(type);
            }
            return types;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a {@link HashMap} of accounts that have the iban different from the given one.
     * The key is the account's iban, and the value is the account's client's full name.
     * @param iban The given iban.
     * @return The account's HashMap.
     */
    public Map<String, String> queryAutocompleteAccounts(String iban){
        try(PreparedStatement query = con.prepareStatement("select concat(u.first_name, ' ', u.last_name), a.iban from accounts a join users u on a.client=u.id where a.iban!=?")) {
            query.setString(1, iban);
            ResultSet resultSet = query.executeQuery();
            Map<String, String> accounts = new HashMap<>();
            while (resultSet.next()){
                accounts.put(resultSet.getString(2), resultSet.getString(1));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a {@link HashMap} of accounts grouped by type.
     * The key is the number of accounts, and the value is the accounts' type.
     * @return The accounts' HashMap.
     */
    public Map<String, Integer> queryAccountsChart(){
        try(Statement query = con.createStatement()) {
            ResultSet resultSet = query.executeQuery("SELECT at.type, count(a.type) FROM accounts a JOIN account_types at on at.id = a.type GROUP BY at.type");
            Map<String, Integer> accounts = new HashMap<>();
            while (resultSet.next()){
                accounts.put(resultSet.getString(1), resultSet.getInt(2));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of an account's transactions.
     * @param account The given account.
     * @return The account's transactions.
     */
    public List<Transaction> queryTransactions(int account){
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction;
        try {
            PreparedStatement query = con.prepareStatement("""
                SELECT t.id, t.from_acc,a.iban as fromAcc, concat(u.first_name,' ', u.last_name) as fromClient, t.to_acc, a1.iban as toAcc, concat(u1.first_name,' ', u1.last_name) as toClient, t.amount as amount,t.created_at as transactionDate, t.withdrawal
                                FROM transactions t JOIN accounts a on t.from_acc = a.id
                                                    JOIN accounts a1 on t.to_acc = a1.id
                                                    JOIN users u on a.client = u.id
                                                    JOIN users u1 on a1.client = u1.id where from_acc=? or to_acc=? ORDER BY transactionDate DESC
                """);
            query.setInt(1,account);
            query.setInt(2, account);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()){
                transaction = new Transaction();
                transaction.setId(resultSet.getInt(1));
                if (resultSet.getInt(10) == 1 && resultSet.getInt(2) == account){
                    transaction.setIban(resultSet.getString(6));
                    transaction.setClient(resultSet.getString(7));
                    transaction.setAmount("-$" + resultSet.getDouble(8));
                } else {
                    transaction.setIban(resultSet.getString(3));
                    transaction.setClient(resultSet.getString(4));
                    transaction.setAmount("+$" + resultSet.getDouble(8));
                }
                transaction.setTransactionDate(resultSet.getTimestamp(9));
                transactions.add(transaction);
            }
            PreparedStatement withdrawalTransactions = con.prepareStatement("SELECT t.id,concat(u.first_name, ' ', u.last_name), t.amount, t.created_at FROM transactions t JOIN accounts a ON t.from_acc=a.id JOIN users u ON a.client=u.id WHERE t.from_acc=? AND t.to_acc IS NULL");
            withdrawalTransactions.setInt(1, account);
            resultSet = withdrawalTransactions.executeQuery();
            while (resultSet.next()){
                transaction = new Transaction();
                transaction.setId(resultSet.getInt(1));
                transaction.setIban("\t".repeat(5) + " ".repeat(5));
                transaction.setClient(resultSet.getString(2));
                transaction.setAmount("-$" + resultSet.getDouble(3));
                transaction.setTransactionDate(resultSet.getTimestamp(4));
                transactions.add(transaction);
            }

            PreparedStatement depositTransactions = con.prepareStatement("SELECT t.id,concat(u.first_name, ' ', u.last_name), t.amount, t.created_at FROM transactions t JOIN accounts a ON t.to_acc=a.id JOIN users u ON a.client=u.id WHERE t.to_acc=? AND t.from_acc IS NULL");
            depositTransactions.setInt(1, account);
            resultSet = depositTransactions.executeQuery();
            while (resultSet.next()){
                transaction = new Transaction();
                transaction.setId(resultSet.getInt(1));
                transaction.setIban("\t".repeat(5) + " ".repeat(5));
                transaction.setClient(resultSet.getString(2));
                transaction.setAmount("+$" + resultSet.getDouble(3));
                transaction.setTransactionDate(resultSet.getTimestamp(4));
                transactions.add(transaction);
            }
            Comparator<Transaction> comp = Comparator.comparing(Transaction::getTransactionDate);
            transactions.sort(comp.reversed());
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a client's loan.
     * @param clientId The client's id.
     * @return The client's loan.
     */
    public Loan queryLoan(int clientId){
        try(PreparedStatement query = con.prepareStatement("SELECT * FROM loans WHERE client=?")) {
            query.setInt(1, clientId);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                return new Loan(new SimpleIntegerProperty(resultSet.getInt("id")), new SimpleIntegerProperty(resultSet.getInt("client")),
                        new SimpleDoubleProperty(resultSet.getDouble("start_balance")), new SimpleDoubleProperty(resultSet.getDouble("remaining_balance")), new SimpleDoubleProperty(resultSet.getDouble("payment")),
                        new SimpleIntegerProperty(resultSet.getInt("penalty")), new SimpleIntegerProperty(resultSet.getInt("months_number")), new SimpleStringProperty(resultSet.getString("date_approved")), new SimpleStringProperty(resultSet.getString("last_payment_date")), new SimpleStringProperty(resultSet.getString("due_date")));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of all loans.
     * @return The list of loans.
     */
    public List<Loan> queryLoans(){
        try(PreparedStatement query = con.prepareStatement("SELECT id, client, start_balance, remaining_balance, months_number, payment, penalty, date_format(date_approved, '%Y-%m-%d'), date_format(due_date, '%Y-%m-%d'), date_format(last_payment_date, '%Y-%m-%d') FROM loans")) {
            List<Loan> loans = new ArrayList<>();
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()){
                Loan loan = new Loan(new SimpleIntegerProperty(resultSet.getInt(1)), new SimpleIntegerProperty(resultSet.getInt(2)),
                                    new SimpleDoubleProperty(resultSet.getDouble(3)), new SimpleDoubleProperty(resultSet.getDouble(4)),
                                    new SimpleDoubleProperty(resultSet.getDouble(6)), new SimpleIntegerProperty(resultSet.getInt(7)),
                                    new SimpleIntegerProperty(resultSet.getInt(5)), new SimpleStringProperty(resultSet.getString(8)), new SimpleStringProperty(resultSet.getString(10)),
                                    new SimpleStringProperty(resultSet.getString(9)));
                loans.add(loan);
            }
            return loans;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of chart values of transactions, starting with a given date and ending with a given date.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The list of chart values.
     */
    public List<ChartValue> queryTransactionsChart(String startDate, String endDate) {
        try (PreparedStatement query = con.prepareStatement("select count(*), year(created_at), month(created_at) from transactions where created_at between ? and date_add(?, interval +1 day ) group by year(created_at), month(created_at)")) {
            query.setString(1, startDate);
            query.setString(2, endDate);
            List<ChartValue> transactions = new ArrayList<>();
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                ChartValue chartValue = new ChartValue(Month.of(resultSet.getInt(3)), resultSet.getString(2), resultSet.getInt(1));
                transactions.add(chartValue);
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a list of chart values of loans, starting with a given date and ending with a given date.
     * @param startDate The start date.
     * @param endDate The end date.
     * @return The list of chart values.
     */
    public List<ChartValue> queryLoansChart(String startDate, String endDate){
        try(PreparedStatement query = con.prepareStatement("select count(*), year(date_approved), month(date_approved) from loans where date_approved between ? and date_add(?, interval +1 day ) group by year(date_approved), month(date_approved)")) {
            query.setString(1, startDate);
            query.setString(2, endDate);
            List<ChartValue> loans = new ArrayList<>();
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()){
                ChartValue chartValue = new ChartValue(Month.of(resultSet.getInt(3)), resultSet.getString(2), resultSet.getInt(1));
                loans.add(chartValue);
            }
            return loans;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method updates the first name, last name, email and phone number of a user.
     * @param id The user's id.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param email The user's email.
     * @param phoneNumber The user's phone number.
     * @return True if the user information was updated, otherwise false.
     */
    public boolean updateUser(int id, String firstName, String lastName, String email, String phoneNumber){
        try(PreparedStatement update = con.prepareStatement("UPDATE users SET first_name=?, last_name=?, email=?, phone_number=? WHERE id=?")) {
            update.setString(1, firstName);
            update.setString(2, lastName);
            update.setString(3, email);
            update.setString(4, phoneNumber);
            update.setInt(5, id);
            int affectedRows = update.executeUpdate();
            con.commit();
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    /**
     * This method updates the first name, last name, email, phone number and password of a user.
     * @param id The user's id.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param email The user's email.
     * @param phoneNumber The user's phone number.
     * @param password The user's password.
     * @return true if the user information was updated, otherwise false.
     */
    public boolean updateUser(int id, String firstName, String lastName, String email, String phoneNumber, String password){
        try(PreparedStatement updateUser = con.prepareStatement("UPDATE users SET first_name=?, last_name=?," +
                "email=?, phone_number=?, password=? WHERE id=?")) {
            updateUser.setString(1, firstName);
            updateUser.setString(2, lastName);
            updateUser.setString(3, email);
            updateUser.setString(4, phoneNumber);
            updateUser.setString(5, encryptPassword(password));
            updateUser.setInt(6, id);
            int affectedRows = updateUser.executeUpdate();
            con.commit();
            return affectedRows > 0;
        } catch (SQLException e){
            return false;
        }
    }

    /**
     * This method changes a user's password in the database.
     * @param id The ID of the user.
     * @param password The new entered password.
     * @return If the password was changed in the database or not.
     */
    public boolean updateUserPassword(int id, String password){
        try(PreparedStatement update = con.prepareStatement("UPDATE users SET password=? WHERE id=?")) {
            update.setString(1, encryptPassword(password));
            update.setInt(2, id);
            int affectedRows = update.executeUpdate();
            if (affectedRows > 0){
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the loan eligibility of a user.
     * @param clientId The user's id.
     * @param eligible The value with which the eligibility is replaced.
     * @return True if it was updated, otherwise false.
     */
    private boolean updateLoanEligibility(int clientId, int eligible){
        try(PreparedStatement update = con.prepareStatement("UPDATE users SET loan_eligibility=? WHERE id=?")) {
            update.setInt(1, eligible);
            update.setInt(2, clientId);
            int affectedRows = update.executeUpdate();
            return affectedRows>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replaces remaining balance, payment, penalty, due date for every loan, with the given values.
     * @param remainingBalance The remaining balance value.
     * @param currentPayment The payment value.
     * @param penalty The penalty value.
     * @param dueDate The due date value.
     * @return True if the table was updated, otherwise false.
     */
    public boolean updateLoan(double remainingBalance, double currentPayment, double penalty, String dueDate){
        try(PreparedStatement update = con.prepareStatement("UPDATE loans SET remaining_balance=?, payment=?, penalty=?, due_date=?")) {
            update.setDouble(1, remainingBalance);
            update.setDouble(2, currentPayment);
            update.setDouble(3, penalty);
            update.setString(4, dueDate);
            int affectedRows = update.executeUpdate();
            return affectedRows>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replaces remaining balance and payment for every loan, with the given values.
     * @param remainingBalance The remaining balance value.
     * @param currentPayment The payment value.
     * @return True if the table was updated, otherwise false.
     */
    private boolean updateLoan(double remainingBalance, double currentPayment){
        try(PreparedStatement update = con.prepareStatement("UPDATE loans SET remaining_balance=?, payment=?")) {
            update.setDouble(1, remainingBalance);
            update.setDouble(2, currentPayment);
            int affectedRows = update.executeUpdate();
            return affectedRows>0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a transaction between two accounts.
     * @param fromAccount The sender account.
     * @param toAccount The recipient account.
     * @param amount The amount.
     * @return True if transaction was performed, otherwise false.
     */
    public boolean performTransaction(int fromAccount, int toAccount, double amount) {
        try {
            con.setAutoCommit(false);
            if (withdrawAmount(fromAccount, amount) && depositAmount(toAccount, amount) && insertTransaction(fromAccount, toAccount, amount)) {
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            try {
                con.rollback();
                return false;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * It withdraws an amount from an account.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the table was updated, otherwise false.
     */
    public boolean performWithdrawTransaction(int accountId, double amount){
        try {
            con.setAutoCommit(false);
            if (withdrawAmount(accountId, amount) && insertWithdrawTransaction(accountId, amount)){
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            try {
                con.rollback();
                return false;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * It deposits an amount into an account.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the table was updated, otherwise false.
     */
    public boolean performDepositTransaction(int accountId, double amount){
        try {
            con.setAutoCommit(false);
            if (depositAmount(accountId, amount) && insertDepositTransaction(accountId, amount)){
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException ex){
            try {
                con.rollback();
                return false;
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This is a helper method for depositing an amount into an account.
     * It updates the accounts table, by adding the amount to the existing account's balance.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the table was updated, otherwise false.
     */
    private boolean depositAmount(int accountId, double amount){
        try(PreparedStatement update = con.prepareStatement("UPDATE accounts SET balance=balance+? WHERE id=?")) {
            update.setDouble(1, amount);
            update.setInt(2, accountId);
            int affectedRows = update.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a transaction into the database, after depositing an amount into an account.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the transaction was inserted, otherwise false.
     */
    private boolean insertDepositTransaction(int accountId, double amount){
        try(PreparedStatement insertTransaction = con.prepareStatement("INSERT INTO transactions(from_acc, to_acc, amount) VALUES (?, ?, ?)")) {
            insertTransaction.setInt(1, accountId);
            insertTransaction.setInt(2, accountId);
            insertTransaction.setDouble(3, amount);
            int insertedRows = insertTransaction.executeUpdate();
            return insertedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is a helper method for withdrawing an amount from an account.
     * It updates the accounts table.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the table was updated, otherwise false.
     */
    private boolean withdrawAmount(int accountId, double amount){
        try(PreparedStatement query = con.prepareStatement("SELECT balance FROM accounts WHERE id=?")) {
            query.setInt(1, accountId);
            ResultSet resultSet = query.executeQuery();
            if (resultSet.next()){
                double balance = resultSet.getDouble(1);
                try(PreparedStatement update = con.prepareStatement("UPDATE accounts SET balance=? WHERE id=?")) {
                    update.setDouble(1, Math.round((balance-amount) * Math.pow(10, 2)) / Math.pow(10, 2));
                    update.setInt(2, accountId);
                    int affectedRows = update.executeUpdate();
                    if (affectedRows==1){
                        return true;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a transaction into the database, after withdrawing an amount from an account.
     * @param accountId The account's id.
     * @param amount The amount.
     * @return True if the transaction was inserted, otherwise false.
     */
    public boolean insertWithdrawTransaction(int accountId, double amount){
        try(PreparedStatement insertTransaction = con.prepareStatement("INSERT INTO transactions(from_acc, to_acc, amount, withdrawal) VALUES (?, ?, ?, 1)")) {
            insertTransaction.setInt(1, accountId);
            insertTransaction.setInt(2, accountId);
            insertTransaction.setDouble(3, amount);
            int insertedRows = insertTransaction.executeUpdate();
            return insertedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new user to the database.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param email The user's email.
     * @param phoneNumber The user's phone number.
     * @param pin The user's pin.
     * @param dateOfBirth The user's date of birth.
     * @param username The user's username.
     * @param password The user's password.
     * @param type The user's type.
     * @param loanEligibility The user's loan eligibility.
     * @return The new user's id.
     */
    public int insertUser(String firstName, String lastName, String email, String phoneNumber, String pin, Date dateOfBirth, String username, String password, int type, int loanEligibility) {
        try(PreparedStatement insertUser = con.prepareStatement("INSERT INTO users(first_name, last_name, email, phone_number, pin, date_of_birth, username, password, type, loan_eligibility) VALUES (?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            insertUser.setString(1, firstName);
            insertUser.setString(2, lastName);
            insertUser.setString(3, email);
            insertUser.setString(4, phoneNumber);
            insertUser.setString(5, pin);
            insertUser.setDate(6, dateOfBirth);
            insertUser.setString(7, username);
            insertUser.setString(8, encryptPassword(password));
            insertUser.setInt(9, type);
            insertUser.setInt(10, loanEligibility);
            insertUser.executeUpdate();
            ResultSet resultSet = insertUser.getGeneratedKeys();
            if (resultSet.next()){
                con.commit();
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new account to the database.
     * @param type The account's type.
     * @param iban The account's iban.
     * @param client The account's client's id.
     * @return The new account's id.
     */
    public int insertAccount(int type, String iban, int client){
        try(PreparedStatement insertAccount = con.prepareStatement("INSERT INTO accounts(type, iban, client) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            insertAccount.setInt(1, type);
            insertAccount.setString(2, iban);
            insertAccount.setInt(3, client);
            insertAccount.executeUpdate();
            ResultSet resultSet = insertAccount.getGeneratedKeys();
            if (resultSet.next()){
                con.commit();
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new user and a current account to the database.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param email The user's email.
     * @param phoneNumber The user's phone number.
     * @param pin The user's pin.
     * @param dateOfBirth The user's date of birth.
     * @param username The user's username.
     * @param password The user's password.
     * @param iban The user's new account's iban.
     * @param type The user's type.
     * @param loanEligibility The user's loan eligibility.
     * @return The new user's id.
     */
    public int insertUserAndAccount(String firstName, String lastName, String email, String phoneNumber, String pin, Date dateOfBirth, String username, String password, String iban, int type, int loanEligibility) {
        try {
            con.setAutoCommit(false);
            int userId = insertUser(firstName, lastName, email, phoneNumber, pin, dateOfBirth, username, password, type, loanEligibility);
            if (userId>0 && insertAccount(1, iban, userId)>0){
                con.commit();
                return userId;
            }
            return 0;
        } catch (SQLException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Couldn't perform rollback");
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new loan into the database.
     * @param clientId The loan's client's id.
     * @param amount The loan's amount.
     * @param months The loan repayment months number.
     * @param currentPayment The repayment amount for the first month.
     * @return The loan's id, if the loan was inserted, otherwise 0.
     */
    public int insertLoan(int clientId, double amount, int months, double currentPayment) {
        try(PreparedStatement insertLoan = con.prepareStatement("INSERT INTO loans(client, start_balance, remaining_balance, payment,months_number, due_date) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);
            insertLoan.setInt(1, clientId);
            insertLoan.setDouble(2, amount);
            insertLoan.setDouble(3, amount);
            insertLoan.setDouble(4, currentPayment);
            insertLoan.setInt(5, months);
            insertLoan.setString(6, dueDate(getStringCurrentDate(), getStringCurrentDate()));
            insertLoan.executeUpdate();
            ResultSet resultSet = insertLoan.getGeneratedKeys();
            Account account = queryAccount(clientId);
            if (resultSet.next() && updateLoanEligibility(clientId, 0) && performTransaction(1, account.getId(), amount)){
                con.commit();
                return resultSet.getInt(1);
            }
            return 0;
        }catch (SQLException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Couldn't perform rollback");
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new client, a new account and a loan into the database.
     * @param amount The loan's amount.
     * @param months The loan repayment months number.
     * @param firstName The client's first name.
     * @param lastName The client's last name.
     * @param email The client's email.
     * @param phoneNumber The client's phone number.
     * @param pin The client's pin.
     * @param dateOfBirth The client's date of birth.
     * @param username The client's username.
     * @param password The client's password.
     * @param iban The account's iban.
     * @param emi The loan's repayment amount for the first month.
     * @param loanEligibility The client's loan eligibility.
     * @return The client's id, if the client, the account and the loan were inserted, otherwise 0.
     */
    public int insertLoan(double amount, int months, String firstName, String lastName, String email, String phoneNumber, String pin, Date dateOfBirth, String username, String password, String iban, double emi, int loanEligibility) {
        try {
            con.setAutoCommit(false);
            int clientId = insertUserAndAccount(firstName, lastName, email, phoneNumber, pin, dateOfBirth, username, password, iban, 3, loanEligibility);
            if (clientId > 0 && insertLoan(clientId, amount, months, emi) > 0 && updateLoanEligibility(clientId, 0)) {
                con.commit();
                return clientId;
            }
            return 0;
        } catch(SQLException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Couldn't perform rollback");
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a new transaction between two accounts.
     * @param fromAcc The sender account.
     * @param toAcc The recipient account.
     * @param amount The transaction's amount.
     * @return True if the transaction was inserted, otherwise false.
     */
    private boolean insertTransaction(int fromAcc, int toAcc, double amount){
        try(PreparedStatement insertTransaction = con.prepareStatement("INSERT INTO transactions(from_acc, to_acc, amount, withdrawal) VALUES(?,?,?, 1)")) {
            insertTransaction.setInt(1, fromAcc);
            insertTransaction.setInt(2, toAcc);
            insertTransaction.setDouble(3, amount);
            int affectedRows = insertTransaction.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a transaction for the loan's installment repayment.
     * It updates the loans table and performs a transaction between client and admin.
     * @param clientId The client's id.
     * @param remainingBalance The remaining balance.
     * @param currentPayment The next repayment amount.
     * @param amount The transaction's amount.
     * @return True if the transaction was performed, otherwise false.
     */
    public boolean loanTransaction(int clientId,double remainingBalance, double currentPayment, double amount){
        try {
            con.setAutoCommit(false);
            Account account = queryAccount(clientId);
            if (updateLoan(remainingBalance, currentPayment) && performTransaction(account.getId(), 1, amount)){
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Couldn't perform rollback");
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a loan by its id.
     * @param loanId The loan's id.
     * @return True is the loan was deleted, otherwise false.
     */
    private boolean deleteLoan(int loanId){
        try(PreparedStatement delete = con.prepareStatement("DELETE FROM loans WHERE id=?")) {
            delete.setInt(1, loanId);
            int affectedRows = delete.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a loan from the database and sets its client's loan eligibility.
     * @param loanId The loan's id.
     * @return True if the actions were performed, otherwise false.
     */
    public boolean deleteLoanAndSetUser(int loanId){
        try {
            con.setAutoCommit(false);
            int clientId = queryUserByLoan(loanId);
            if (clientId > 0 && deleteLoan(loanId) && updateLoanEligibility(clientId, 1)){
                con.commit();
                return true;
            }
            return false;
        } catch(SQLException e){
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Couldn't perform rollback");
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * This method deletes a user from database.
     * @param id The ID of the user
     * @return True if the user was deleted, otherwise returns false.
     */
    public boolean deleteUser(int id){
        try(PreparedStatement stmt = con.prepareStatement("DELETE FROM users WHERE id=?")) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0){
                con.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encrypts a password.
     * @param password The password to be encrypted.
     * @return The encrypted password.
     */
    private String encryptPassword(String password){
        String encryptedPass = null;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            encryptedPass=s.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedPass;
    }

    /**
     * Gets the current date in a specified format.
     * @return The current date.
     */
    private String getStringCurrentDate(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class SQL {

    private Connection connection;
    private Statement statement;
    private ResultSet rs;

    /**
     * This methode calls the DB and make the connection.
     * The Try-catch catch all exeptions (from DB conncetion) and print them out.
     */
    public SQL() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://davidemarcoli.de:3306/davidema_eBanking",
                    "davidema_ronnieha", "ebanking1234");
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * This methode is used to login.
     * First, data is taken from the DB via the connection.
     * Afterwards these are compared with the user inputs.
     * If they match, the user is logged in and receives his data.
     *
     * @param contractNumber
     * The contract number contains the number that the user has entered.
     * This number corresponds to his contract number in the Db.
     * @param password
     * The contract number contains a string that the user has entered.
     * This number corresponds to his password in the Db.
     *
     * @return
     * The userdata gets returned or null if the user log in failed.
     */
    public User login(String contractNumber, String password) {
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM users " +
                    "WHERE passwd='" + password + "' " +
                    "AND contract_number='" + contractNumber + "'");
            if (rs.next()) {
                return new User(rs.getString("contract_number"), rs.getString("first_name"),
                        rs.getString("last_name"), rs.getString("email"),
                        rs.getDate("birthdate"), rs.getString("address"),
                        rs.getString("passwd"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * This methode is used to register a new user.
     *
     * @param firstName
     * Contains the firstname that the user has entered.
     * @param lastName
     * Contains the lastname that the user has entered.
     * @param eMail
     * Contains the email that the user has entered.
     * @param address
     * Contains the address that the user has entered.
     * @param birthDate
     * Contains the birthdate that the user has entered.
     * @param password
     * Contains the password that the user has entered.
     *
     * @return
     * If everything is fine a new user gets returned.
     * If there are some complications null gets returned.
     */
    public User register(String firstName, String lastName, String eMail, String address, Date birthDate, String password) {
        if (!firstName.isBlank() && !lastName.isBlank() && !eMail.isBlank() && !address.isBlank() && !password.isBlank()) {
            try {
                String contractNumber = generateContractNumber();
                statement = connection.createStatement();
                statement.executeUpdate("INSERT INTO users " +
                        "VALUES ('" + contractNumber + "', '" + firstName + "', '" + lastName +"', '" + eMail +
                        "', '" + address + "', '" + birthDate + "', '" + password  + "')");
                addBankAccount(contractNumber);
                return new User(contractNumber, firstName, lastName, eMail, birthDate, address, password);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }

    /**
     * This method generates a new contract number.
     * Every user needs a unique number.
     * The contract numbers of all users are taken from the database.
     * After that a new contract number will be created and it will be checked that this number does not exist yet.
     *
     * @return
     * The contract number gets returned
     */
    private String generateContractNumber() {
        ArrayList<Integer> existingContractNumbers = new ArrayList<>();
        String contractNumber = "";
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT contract_number FROM users");
            while (rs.next()) {
                existingContractNumbers.add(Integer.parseInt(rs.getString("contract_number").substring(4)));
            }

            Random rand = new Random();
            int number = 0;
            boolean exists = true;
            while (exists) {
                number = rand.nextInt(10000000);
                if (!existingContractNumbers.contains(number)) {
                    exists = false;
                }
            }
            contractNumber = "700-" + String.format("%07d", number);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return contractNumber;
    }

    /**
     * Method to get all IBANs of the bank accounts from a specific user.
     *
     * @param contractNumber
     * Contains the contract number of the user.
     *
     * @return
     * A ObservableList of Strings containing the IBANs.
     */
    public ObservableList<String> getBankAccounts(String contractNumber) {
        ObservableList<String> bankAccounts = FXCollections.observableArrayList();
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT iban FROM bank_account " +
                    "WHERE id_user='" + contractNumber + "'");
            while (rs.next()) {
                bankAccounts.add(rs.getString("iban"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return bankAccounts;
    }

    /**
     * Searches the bank account with a specific iban and returns it.
     *
     * @param iban
     * IBAN to find bank account.
     *
     * @return
     * The bank account with the chosen iban.
     */
    public BankAccount getBankAccount(String iban) {
        BankAccount account = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM bank_account " +
                    "WHERE iban='" + iban + "'");
            if (rs.next()) {
                account = new BankAccount(iban, rs.getDouble("credit"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return account;
    }

    /**
     * This method is used to send money from one bank account to another bank account.
     *
     * @param senderIban
     * IBAN of the bank account that sends the money.
     * @param receiverIban
     * IBAN of the bank account that receives the money.
     * @param credit
     * Amount of money to send.
     *
     * @return
     */
    public boolean sendMoney(String senderIban, String receiverIban, double credit) {
        boolean sent = false;

        if (credit > 0 && ibanExists(senderIban) && ibanExists(receiverIban)) {
            double senderOldCredit = getBankAccount(senderIban).credit;
            double senderNewCredit = senderOldCredit - credit;
            double receiverOldCredit = getBankAccount(receiverIban).credit;
            double receiverNewCredit = receiverOldCredit + credit;

            try {
                statement = connection.createStatement();
                statement.executeUpdate("UPDATE bank_account" +
                        " SET credit = " + senderNewCredit +
                        " WHERE iban = '" + senderIban + "';");
                statement.executeUpdate("UPDATE bank_account" +
                        " SET credit = " + receiverNewCredit +
                        " WHERE iban = '" + receiverIban + "';");
                sent = true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return sent;
    }

    /**
     * Checks if a specific IBAN already exists.
     *
     * @param iban
     * IBAN to check.
     *
     * @return
     * Returns true if the IBAN exists and false if it doesn't.
     */
    public boolean ibanExists(String iban) {
        ArrayList<String> ibans = new ArrayList<>();
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT iban FROM bank_account");
            while (rs.next()) {
                ibans.add(rs.getString("iban"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (ibans.contains(iban)) {
            return true;
        }
        return false;
    }

    /**
     * Adds a bank account to a user.
     *
     * @param contractNumber
     * The user with this contract number gets a new bank accounts.
     */
    public void addBankAccount(String contractNumber) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO bank_account " +
                    "VALUES ('" + generateIban() + "', '0', '" + contractNumber + "')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Generates a new IBAN with random numbers. If the IBAN already exists, it generates a new one.
     *
     * @return
     * Returns the generated unique IBAN.
     */
    private String generateIban() {
        Random rand = new Random();
        String iban = "";
        do {
            iban = "CH";
            for (int i = 0; i < 19; i++) {
                iban += rand.nextInt(10);
            }
        } while (ibanExists(iban));
        return iban;
    }
}

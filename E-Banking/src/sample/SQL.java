package sample;

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
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "root", "eBankingPassWD22");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *This methode is used to login.
     * First, data is taken from the DB via the connection.
     * Afterwards these are compared with the user inputs.
     * If they match, the user is logged in and receives his data.
     *
     * @param contractNumber
     * The contractnumber contains the number that the user has entered.
     * This number corresponds to his contract number in the Db.
     * @param password
     * The contractnumber contains a string that the user has entered.
     * This number corresponds to his password in the Db.
     *
     * @return
     * The userdata gets returnt or null if the user logging failed.
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
     * If everything is fine a new user gets returnt.
     * If there are some complications null gets returnt.
     */
    public User register(String firstName, String lastName, String eMail, String address, Date birthDate, String password) {
        if (!firstName.isBlank() && !lastName.isBlank() && !eMail.isBlank() && !address.isBlank() && !password.isBlank()) {
            try {
                String contractNumber = createContractNumber();
                statement = connection.createStatement();
                statement.executeUpdate("INSERT INTO users " +
                        "VALUES ('" + contractNumber + "', '" + firstName + "', '" + lastName +"', '" + eMail +
                        "', '" + address + "', '" + birthDate + "', '" + password  + "')");
                return new User(contractNumber, firstName, lastName, eMail, birthDate, address, password);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }

    /**
     *In this methode the user gets his own conctractnumber.
     * Every user have a unique number.
     * The contractnumbers of all users are taken from the db.
     * After that a new contractnumber will be created and it will be checked that this number does not exist yet.
     *
     * @return
     * The contractnumber gets return
     */
    private String createContractNumber() {
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

}

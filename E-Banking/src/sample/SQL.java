package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class SQL {

    private Connection connection;
    private Statement statement;
    private ResultSet rs;

    public SQL() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "root", "eBankingPassWD22");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

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

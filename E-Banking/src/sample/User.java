package sample;

import java.sql.Date;

public class User {

    private String contractNumber;
    private String firstName;
    private String lastName;
    private String email;
    private Date birthdate;
    private String address;
    private String password;

    public User(String contractNumber, String firstName, String lastName, String email, Date birthdate, String address, String password) {
        this.contractNumber = contractNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.address = address;
        this.password = password;
    }

}

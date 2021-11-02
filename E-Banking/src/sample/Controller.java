package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.Date;

public class Controller {

    SQL database = new SQL();
    EBanking eBanking;

    // Login
    @FXML
    public AnchorPane loginPane;

    @FXML
    public TextField loginContractNumberInput;

    @FXML
    public PasswordField loginPasswordInput;

    @FXML
    public Button loginButton;

    @FXML
    public Label loginErrorLabel;

    // Register
    @FXML
    public AnchorPane registerPane;

    @FXML
    public TextField registerFirstNameInput;

    @FXML
    public TextField registerLastNameInput;

    @FXML
    public TextField registerEmailInput;

    @FXML
    public PasswordField registerPasswordInput;

    @FXML
    public TextField registerAddressInput;

    @FXML
    public DatePicker registerBirthDateInput;

    @FXML
    public Label registerErrorLabel;

    @FXML
    public void login() {
        User user = database.login(loginContractNumberInput.getText(), loginPasswordInput.getText());
        if (user == null) {
            loginErrorLabel.setText("Vertragsnummer oder Passwort ist falsch.");
        }
        else {
            loginPane.setVisible(false);
            eBanking = new EBanking(user);
        }
    }

    @FXML
    public void register() {
        User user = database.register(
                registerFirstNameInput.getText(),
                registerLastNameInput.getText(),
                registerEmailInput.getText(),
                registerAddressInput.getText(),
                Date.valueOf(registerBirthDateInput.getValue()),
                registerPasswordInput.getText());
        if (user == null) {
            registerErrorLabel.setText("Fehler");
        }
        else {
            registerPane.setVisible(false);
            eBanking = new EBanking(user);
        }
    }

    @FXML
    public void switchRegister() {
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }

    @FXML
    public void switchLogin() {
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }

}

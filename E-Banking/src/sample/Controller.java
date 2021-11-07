package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.Date;

public class Controller {

    private final SQL database = new SQL();
    private ObservableList<String> bankAccounts;

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

    // Bank Accounts
    @FXML
    public AnchorPane overviewPane;

    @FXML
    public ListView bankAccountTable;

    /**
     * The methode login gets called and a user gets returned.
     * If the user is null the user gets notified.
     * If the user is not null the view gets disabled and a new instance of eBanking(USER) gets created.
     */
    @FXML
    public void login() {
        User user = database.login(loginContractNumberInput.getText(), loginPasswordInput.getText());
        if (user == null) {
            loginErrorLabel.setText("wrong contract number or password");
        }
        else {
            loginPane.setVisible(false);
            overviewPane.setVisible(true);
            bankAccounts = database.getBankAccounts(user);
            bankAccountTable.setItems(bankAccounts);
        }
    }

    /**
     * Create a new user by the data from register.
     * The methode register gets called.
     * The user gets tested if its null.
     * If the user is not null, the register view is exited and a new instance of eBanking(USER) is created.
     */
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
            registerErrorLabel.setText("Error");
        }
        else {
            registerPane.setVisible(false);
            overviewPane.setVisible(true);
            bankAccounts = database.getBankAccounts(user);
            bankAccountTable.setItems(bankAccounts);
        }
    }

    /**
     * Switch the view from login to the view of register.
     */
    @FXML
    public void switchRegister() {
        loginPane.setVisible(false);
        registerPane.setVisible(true);
    }

    /**
     * Switch the view from register to the view of login.
     */
    @FXML
    public void switchLogin() {
        registerPane.setVisible(false);
        loginPane.setVisible(true);
    }

}

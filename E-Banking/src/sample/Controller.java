package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.sql.Date;
import java.text.DecimalFormat;

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

    @FXML
    public Label ibanLabel;

    @FXML
    public Label moneyLabel;

    @FXML
    public TextField transferIbanInput;

    @FXML
    public TextField transferMoneyInput;

    @FXML
    public Button transferSubmitButton;

    @FXML
    public Label bankAccountErrorLabel;

    public String currentIban;
    public User currentUser;

    // Profile
    @FXML
    public AnchorPane profilePane;

    @FXML
    public Label contractNumberLabel;

    @FXML Label firstNameLabel;

    @FXML Label lastNameLabel;

    @FXML Label emailLabel;

    // Menu
    @FXML
    public Menu profileMenu;

    @FXML
    public Menu bankAccountsMenu;

    /**
     * The methode login gets called and a user gets returned.
     * If the user is null, an error occurred and the user gets notified.
     * If the user is not null the view gets disabled and the bank accounts view gets enabled.
     */
    @FXML
    public void login() {
        User user = database.login(loginContractNumberInput.getText(), loginPasswordInput.getText());
        if (user == null) {
            loginErrorLabel.setText("wrong contract number or password");
        }
        else {
            switchBankAccounts();
            bankAccounts = database.getBankAccounts(user.getContractNumber());
            bankAccountTable.setItems(bankAccounts);
            currentUser = user;
            profileMenu.setDisable(false);
            bankAccountsMenu.setDisable(false);
            loginContractNumberInput.setText("");
            loginPasswordInput.setText("");
        }
    }

    /**
     * Create a new user by the data from register.
     * Calls the method database.register with the data from the input fields.
     * If the user is null, an error occurred and the user gets notified.
     * If the user is not null, the register view is exited and the bank accounts view gets activated.
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
            switchBankAccounts();
            bankAccounts = database.getBankAccounts(user.getContractNumber());
            bankAccountTable.setItems(bankAccounts);
            currentUser = user;
            registerFirstNameInput.setText("");
            registerLastNameInput.setText("");
            registerEmailInput.setText("");
            registerAddressInput.setText("");
            registerPasswordInput.setText("");
        }
    }

    /**
     * Logs the user out and opens the login view.
     */
    @FXML
    public void logout() {
        currentUser = null;
        currentIban = "";
        profileMenu.setDisable(true);
        bankAccountsMenu.setDisable(true);
        switchLogin();
    }

    /**
     * Switch the view to the view of register.
     */
    public void switchRegister() {
        setPanesInvisible();
        registerPane.setVisible(true);
    }

    /**
     * Switch the view to the view of login.
     */
    public void switchLogin() {
        setPanesInvisible();
        loginPane.setVisible(true);
    }

    /**
     * Switch the view to the profile view.
     */
    @FXML
    public void switchProfile() {
        setPanesInvisible();
        profilePane.setVisible(true);
        contractNumberLabel.setText("Contract Number: \t" + currentUser.getContractNumber());
        firstNameLabel.setText("First Name: \t\t" + currentUser.getFirstName());
        lastNameLabel.setText("Last Name: \t\t" + currentUser.getLastName());
        emailLabel.setText("E-Mail: \t\t\t" + currentUser.getEmail());
    }

    /**
     * Switch the view to the bank accounts view.
     */
    @FXML
    public void switchBankAccounts() {
        setPanesInvisible();
        overviewPane.setVisible(true);
    }

    /**
     * Sets all views to invisible.
     */
    private void setPanesInvisible() {
        loginPane.setVisible(false);
        registerPane.setVisible(false);
        overviewPane.setVisible(false);
        profilePane.setVisible(false);
    }

    /**
     * Gets called, when the user clicks on a bank account.
     * Shows the information of the bank account to the user.
     */
    @FXML
    public void selectBankAccount() {
        String iban = bankAccountTable.getSelectionModel().getSelectedItem().toString();
        BankAccount account = database.getBankAccount(iban);

        if (account == null) {
            bankAccountErrorLabel.setText("An unexpected error occurred.");
        }
        else {
            ibanLabel.setText("IBAN: " + account.iban);
            DecimalFormat df = new DecimalFormat("0.00");
            moneyLabel.setText("Credit: " + df.format(account.credit) + " CHF");
            currentIban = account.iban;
        }
    }

    /**
     * Get called, when the user clicks the send money button.
     * Validation of the entered data.
     * Calls the sendMoney method of the SQL class.
     */
    @FXML
    public void sendMoney() {
        String iban = transferIbanInput.getText();
        String money = transferMoneyInput.getText();

        if (currentIban != null) {
            if (money.matches("\\d+(.\\d{1,2})?")) {
                if (iban.matches("[A-Z]{2}\\d{19}")) {
                    if (!iban.equals(currentIban)) {
                        double senderCredit = database.getBankAccount(currentIban).credit;
                        if (senderCredit < Double.parseDouble(money)) {
                            bankAccountErrorLabel.setText("This bank account doesn't have enough money.");
                        } else {
                            if (database.sendMoney(currentIban, iban, Double.parseDouble(money))) {
                                transferIbanInput.setText("");
                                transferMoneyInput.setText("");
                                bankAccountErrorLabel.setText("");
                                bankAccounts = database.getBankAccounts(currentUser.getContractNumber());
                                bankAccountTable.setItems(bankAccounts);
                            } else {
                                bankAccountErrorLabel.setText("An unexpected error occurred.");
                            }
                        }
                    } else {
                        bankAccountErrorLabel.setText("You can't send money to the same bank account.");
                    }
                } else {
                    bankAccountErrorLabel.setText("IBAN is not in the correct format.");
                }
            } else {
                bankAccountErrorLabel.setText("Money is not in the correct format.");
            }
        } else {
            bankAccountErrorLabel.setText("Select a bank account.");
        }
    }

    /**
     * Calls the method addBankAccount from the SQL class to add a new bank account to the current user.
     */
    @FXML
    public void addBankAccount() {
        database.addBankAccount(currentUser.getContractNumber());
        bankAccounts = database.getBankAccounts(currentUser.getContractNumber());
        bankAccountTable.setItems(bankAccounts);
    }

}

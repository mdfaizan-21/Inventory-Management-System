package controllers;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Models.User;
import Services.OTPService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.EmailUtil;
import util.SceneNavigator;

/**
 * Controller for the Registration page
 */
public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAOImpl();
    private int generatedOTP;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Populate role combo box
        roleComboBox.getItems().addAll("USER", "ADMIN");
    }

    /**
     * Handle registration
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();

        // Validation
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || role == null) {
            showError("Please fill in all fields");
            return;
        }
        if(password.length()<6){
            showError("The length of Password Should be greater than 6");
            return;
        }
        // Check if username already exists
        if (userDAO.getUserByUserName(username) != null) {
            showError("Username already exists");
            return;
        }

        // Generate OTP and send it to user's email
        generatedOTP = OTPService.generateOTP();
        try {

            System.out.println("Sending OTP: " + generatedOTP + " to email: " + email);
            // Show OTP dialog
            EmailUtil.sendOTP(email, "OTP for registration", "Here is the OTP for signing Up in Inventory Management System\nOTP:- " + generatedOTP);
            showOTPDialog(username, password, email, role);
        } catch (Exception e) {
            showError("Failed to send verification code: " + e.getMessage());
        }
    }

    /**
     * Show OTP dialog for verification
     */
    private void showOTPDialog(String username, String password, String email, String role) {
        // Create a dialog for OTP entry
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Email Verification");
        dialog.setHeaderText("Please enter the verification code sent to " + email);

        // Set the button types
        ButtonType verifyButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(verifyButtonType, ButtonType.CANCEL);

        // Create the OTP field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField otpField = new TextField();
        otpField.setPromptText("Enter OTP");
        grid.add(new Label("Verification Code:"), 0, 0);
        grid.add(otpField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the verify button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == verifyButtonType) {
                return otpField.getText();
            }
            return null;
        });

        // Show the dialog and wait for user input
        dialog.showAndWait().ifPresent(otp -> {
            try {
                int enteredOTP = Integer.parseInt(otp);
                if (enteredOTP == generatedOTP) {
                    // Create new user with verified status
                    User newUser = new User(username, password, role, email, "verified");
                    userDAO.addUser(newUser);

                    // Show success message and return to login
                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                            "Your account has been created successfully! Please login.");
                    handleBackToLogin();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Verification Failed",
                            "Incorrect verification code. Please try registering again.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input",
                        "Please enter a valid numeric code.");
            }
        });
    }

    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigate back to login
     */
    @FXML
    private void handleBackToLogin() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneNavigator.loadScene(stage, "/fxml/Login.fxml", "Login - Inventory Management System");
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
}
package controllers;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Models.User;
import Services.OTPService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.SceneNavigator;

/**
 * Controller for the Email Verification dialog
 * Handles sending OTP and verifying user accounts
 */
public class VerificationController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField otpField;

    @FXML
    private Button sendOtpButton;

    @FXML
    private Button verifyButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAOImpl();
    private User userToVerify;
    private int generatedOTP;

    /**
     * Set the user that needs to be verified
     */
    public void setUserToVerify(User user) {
        this.userToVerify = user;
        showStatus("User needs verification: " + user.getUserName());
    }

    /**
     * Handle Send OTP button click
     */
    @FXML
    private void handleSendOTP() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter your email address");
            return;
        }

        // Validate email format (simple validation)
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            return;
        }

        // Generate OTP and send it
        generatedOTP = OTPService.generateOTP();

        try {
            // Send OTP to user's email
            // Note: We're using a simplified approach here - in a real app, you'd want to
            // implement a proper email service with error handling
            System.out.println("Sending OTP: " + generatedOTP + " to email: " + email);

            // Enable OTP field and verify button
            otpField.setDisable(false);
            verifyButton.setDisable(false);
            sendOtpButton.setText("Resend Code");

            showStatus("Verification code sent to " + email);
        } catch (Exception e) {
            showError("Failed to send verification code: " + e.getMessage());
        }
    }

    /**
     * Handle Verify button click
     */
    @FXML
    private void handleVerify() {
        String otpText = otpField.getText().trim();

        if (otpText.isEmpty()) {
            showError("Please enter the verification code");
            return;
        }

        try {
            int enteredOTP = Integer.parseInt(otpText);

            if (enteredOTP == generatedOTP) {
                // Update user verification status in database
                userDAO.addVerification(userToVerify.getUserName(), "verified", emailField.getText().trim());

                showStatus("Account verified successfully! Please log in again.");

                // Return to login screen after a short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::returnToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showError("Incorrect verification code. Please try again.");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric code");
        }
    }

    /**
     * Handle Cancel button click
     */
    @FXML
    private void handleCancel() {
        returnToLogin();
    }

    /**
     * Return to login screen
     */
    private void returnToLogin() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        SceneNavigator.loadScene(stage, "/fxml/Login.fxml", "Login - Inventory Management System");
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
        messageLabel.setVisible(false);
    }

    /**
     * Display status message
     */
    private void showStatus(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}

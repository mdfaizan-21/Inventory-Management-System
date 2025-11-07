package ui.controllers;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Models.User;
import Services.OTPService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class RegisterController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private Label messageLabel;
    
    private UserDAO userDAO = new UserDAOImpl();
    
    @FXML
    private void initialize() {
        // Set default role
        roleComboBox.setValue("USER");
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String role = roleComboBox.getValue();
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        if (role == null) {
            showError("Please select a role");
            return;
        }
        
        try {
            // Check if user already exists
            User existingUser = userDAO.getUserByUserName(username);
            if (existingUser != null) {
                showError("Username already exists");
                return;
            }
            
            // Create new user with UNVERIFIED status
            User newUser = new User(username, password, role, email, "UNVERIFIED");
            userDAO.addUser(newUser);
            
            // Generate and send OTP
            String otp = OTPService.generateOTP();
            OTPService.storeOTP(email, otp);
            
            showSuccess("Registration successful! Please verify your email.");
            
            // Show OTP verification dialog
            showOTPVerification(email, newUser);
            
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showOTPVerification(String email, User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("OTP Verification");
        dialog.setHeaderText("An OTP has been sent to: " + email);
        dialog.setContentText("Enter OTP:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(enteredOTP -> {
            if (OTPService.verifyOTP(email, enteredOTP)) {
                user.setStatus("VERIFIED");
                userDAO.updateUser(user);
                showSuccess("Account verified successfully! You can now login.");
                
                // Close registration window after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::handleCancel);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                
            } else {
                showError("Invalid OTP. Please contact support or register again.");
            }
        });
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
}

package ui.controllers;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Models.User;
import Services.OTPService;
import Services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.InventoryApp;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    private UserDAO userDAO = new UserDAOImpl();
    private UserService userService = new UserService();
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        try {
            User user = userDAO.getUserByUserName(username);
            
            if (user != null && user.getPassword().equals(password)) {
                // Check if user is verified
                if (!"VERIFIED".equalsIgnoreCase(user.getStatus())) {
                    showError("Please verify your account via OTP first");
                    // Show OTP verification dialog
                    showOTPVerification(user);
                    return;
                }
                
                // Successful login
                showSuccess("Login successful!");
                InventoryApp.showDashboardView(user);
                
            } else {
                showError("Invalid username or password");
            }
            
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Register New User");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 500, 600));
            stage.getScene().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.showAndWait();
            
        } catch (IOException e) {
            showError("Failed to open registration form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showOTPVerification(User user) {
        try {
            // Generate and send OTP
            String otp = OTPService.generateOTP();
            OTPService.storeOTP(user.getEmail(), otp);
            
            // Show OTP dialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("OTP Verification");
            dialog.setHeaderText("An OTP has been sent to: " + user.getEmail());
            dialog.setContentText("Enter OTP:");
            
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(enteredOTP -> {
                if (OTPService.verifyOTP(user.getEmail(), enteredOTP)) {
                    user.setStatus("VERIFIED");
                    userDAO.updateUser(user);
                    showSuccess("Account verified successfully! Please login again.");
                } else {
                    showError("Invalid OTP. Please try again.");
                }
            });
            
        } catch (Exception e) {
            showError("OTP verification failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: #e74c3c;");
        errorLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #27ae60;");
        errorLabel.setVisible(true);
    }
}

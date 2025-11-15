package controllers;

import DAO.Impl.UserDAOImpl;
import DAO.UserDAO;
import Models.User;
import Services.StockAlertService;
import Services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.SceneNavigator;
import util.SessionManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Login page
 * Handles user authentication and navigation to registration
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label statusLabel;

    private UserDAO userDAO = new UserDAOImpl();

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        // Attempt login using existing UserService
        User user = UserService.login(username, password);

        if (user != null) {
            // Check if user needs verification
            if (user.getStatus() == null || !user.getStatus().equalsIgnoreCase("verified")) {
                // Navigate to verification screen instead of showing error
                Stage stage = (Stage) usernameField.getScene().getWindow();
                VerificationController controller = (VerificationController) SceneNavigator.loadSceneWithController(
                        stage, "/fxml/Verification.fxml", "Email Verification");

                if (controller != null) {
                    controller.setUserToVerify(user);
                }
                return;
            }

            // Successful login - navigate to dashboard
            SessionManager.setCurrentUser(user);

            // If user is admin, start stock alert service
            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                User finalUser = user;
                new Thread(() -> {
                    StockAlertService alertService = new StockAlertService(finalUser);
                    // Send immediate alert on login
                    alertService.alertAdmin();
                    // Schedule periodic alerts every 10 minutes
                    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
                    scheduler.scheduleAtFixedRate(alertService::alertAdmin, 10, 10, TimeUnit.MINUTES);
                }).start();
                System.out.println("✅ Stock Alert service started for admin: " + user.getEmail());
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Load dashboard and pass user information
            DashboardController controller = (DashboardController) SceneNavigator.loadSceneWithController(
                    stage, "/fxml/Dashboard.fxml", "Dashboard - Inventory Management System");

            if (controller != null) {
                controller.setCurrentUser(user);
            }
        } else {
            showError("Invalid username or password");
        }
    }

    /**
     * Handle register button click
     */
    @FXML
    private void handleRegister() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        SceneNavigator.loadScene(stage, "/fxml/Register.fxml", "Register - Inventory Management System");
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }

    /**
     * Display status message
     */
    public void showStatus(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setVisible(true);
        errorLabel.setVisible(false);
    }
}

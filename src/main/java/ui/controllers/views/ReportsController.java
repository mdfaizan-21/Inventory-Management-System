package ui.controllers.views;

import Models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.controllers.BaseViewController;
import util.CSVHelper;
import util.EmailUtil;

public class ReportsController implements BaseViewController {
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private TableView<?> reportsTable;
    
    @FXML
    private TableColumn<?, ?> dateCol;
    
    @FXML
    private TableColumn<?, ?> userCol;
    
    @FXML
    private TableColumn<?, ?> emailCol;
    
    private User currentUser;
    private CSVHelper csvHelper = new CSVHelper();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void handleGenerateReport() {
        try {
            String fileName = csvHelper.generateReport();
            showSuccess("✅ Report generated successfully: " + fileName);
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEmailReport() {
        if (currentUser == null || currentUser.getEmail() == null) {
            showError("User email not available");
            return;
        }
        
        try {
            String fileName = csvHelper.generateReport();
            
            // Send email in background thread
            new Thread(() -> {
                try {
                    EmailUtil.sendReport(
                        currentUser.getEmail(),
                        "Inventory Report",
                        "Please find the attached inventory report.",
                        fileName
                    );
                    javafx.application.Platform.runLater(() -> 
                        showSuccess("✅ Report sent to " + currentUser.getEmail())
                    );
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> 
                        showError("Failed to send email: " + e.getMessage())
                    );
                }
            }).start();
            
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
}

package ui.controllers;

import DAO.Impl.ReportsTrackerImpl;
import DAO.ReportsTracker;
import Models.Product;
import Models.User;
import Services.InventoryManagement;
import Services.StockAlertService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.InventoryApp;
import util.CSVHelper;
import util.EmailUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DashboardController {
    
    @FXML
    private Label userLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label dateTimeLabel;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Button addProductBtn;
    
    @FXML
    private Button updateProductBtn;
    
    @FXML
    private Button deleteProductBtn;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    private CSVHelper csvHelper = new CSVHelper();
    private ReportsTracker reportsTracker = new ReportsTrackerImpl();
    
    @FXML
    private void initialize() {
        // Start clock
        startClock();
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        userLabel.setText("👤 " + user.getUserName() + " (" + user.getRole() + ")");
        
        // Configure UI based on role
        if ("USER".equalsIgnoreCase(user.getRole())) {
            // Hide admin-only buttons for regular users
            addProductBtn.setVisible(false);
            updateProductBtn.setVisible(false);
            deleteProductBtn.setVisible(false);
        }
        
        // Start stock alert service for admin
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            startStockAlertService();
        }
        
        // Show dashboard by default
        try {
            showDashboard();
        } catch (IOException e) {
            showError("Failed to load dashboard");
            e.printStackTrace();
        }
    }
    
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
    
    private void startStockAlertService() {
        new Thread(() -> {
            StockAlertService alertService = new StockAlertService(currentUser);
            ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
            scheduler.scheduleAtFixedRate(() -> {
                alertService.alertAdmin();
                Platform.runLater(() -> updateStatus("Stock alert check completed"));
            }, 0, 10, TimeUnit.MINUTES);
        }).start();
    }
    
    @FXML
    private void showDashboard() throws IOException {
        loadView("/fxml/views/dashboard_home.fxml");
        updateStatus("Dashboard loaded");
    }
    
    @FXML
    private void showViewProducts() throws IOException {
        loadView("/fxml/views/view_products.fxml");
        updateStatus("Viewing all products");
    }
    
    @FXML
    private void showAddProduct() throws IOException {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            showError("Only admins can add products");
            return;
        }
        loadView("/fxml/views/add_product.fxml");
        updateStatus("Add product view");
    }
    
    @FXML
    private void showUpdateProduct() throws IOException {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            showError("Only admins can update products");
            return;
        }
        loadView("/fxml/views/update_product.fxml");
        updateStatus("Update product view");
    }
    
    @FXML
    private void showDeleteProduct() throws IOException {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            showError("Only admins can delete products");
            return;
        }
        loadView("/fxml/views/delete_product.fxml");
        updateStatus("Delete product view");
    }
    
    @FXML
    private void showSearchProducts() throws IOException {
        loadView("/fxml/views/search_products.fxml");
        updateStatus("Search products view");
    }
    
    @FXML
    private void showReports() throws IOException {
        loadView("/fxml/views/reports.fxml");
        updateStatus("Reports view");
    }
    
    @FXML
    private void showStockAlerts() throws IOException {
        loadView("/fxml/views/stock_alerts.fxml");
        updateStatus("Stock alerts view");
    }
    
    @FXML
    private void handleImportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Products from CSV");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showOpenDialog(InventoryApp.getPrimaryStage());
        if (file != null) {
            try {
                int count = csvHelper.readDATFromCSV(file.getAbsolutePath());
                showSuccess("✅ " + count + " products imported successfully!");
                updateStatus("Imported " + count + " products from CSV");
            } catch (Exception e) {
                showError("Failed to import CSV: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleExportCSV() {
        try {
            String fileName = csvHelper.generateReport();
            showSuccess("✅ CSV report generated successfully: " + fileName);
            updateStatus("Exported products to CSV");
        } catch (Exception e) {
            showError("Failed to export CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will need to login again to access the system.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                InventoryApp.showLoginView();
            } catch (IOException e) {
                showError("Failed to logout: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("The application will close.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Inventory Management System");
        alert.setContentText("Version: 2.0\n" +
                            "A comprehensive inventory management solution\n" +
                            "Built with JavaFX\n\n" +
                            "© 2025 All Rights Reserved");
        alert.showAndWait();
    }
    
    private void loadView(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent view = loader.load();
        
        // Pass current user to the view controller if needed
        Object controller = loader.getController();
        if (controller instanceof BaseViewController) {
            ((BaseViewController) controller).setUser(currentUser);
        }
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package ui.controllers.views;

import Models.Product;
import Models.User;
import Services.InventoryManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.controllers.BaseViewController;
import util.CSVHelper;
import util.EmailUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardHomeController implements BaseViewController {
    
    @FXML
    private Label totalProductsLabel;
    
    @FXML
    private Label lowStockLabel;
    
    @FXML
    private Label categoriesLabel;
    
    @FXML
    private TableView<Product> recentActivityTable;
    
    @FXML
    private TableColumn<Product, Integer> productIdCol;
    
    @FXML
    private TableColumn<Product, String> productNameCol;
    
    @FXML
    private TableColumn<Product, String> categoryCol;
    
    @FXML
    private TableColumn<Product, Integer> stockCol;
    
    @FXML
    private TableColumn<Product, Double> priceCol;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    private CSVHelper csvHelper = new CSVHelper();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
        loadDashboardData();
    }
    
    @FXML
    private void initialize() {
        // Set up table columns
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("productType"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Format price column
        priceCol.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        // Highlight low stock items
        stockCol.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(stock.toString());
                    if (stock < 10) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
    
    private void loadDashboardData() {
        try {
            List<Product> products = inventoryManagement.Read();
            
            // Update statistics
            totalProductsLabel.setText(String.valueOf(products.size()));
            
            // Count low stock items (assuming threshold is 10)
            long lowStockCount = products.stream()
                .filter(p -> p.getAvailableQty() != null && p.getAvailableQty() < 10)
                .count();
            lowStockLabel.setText(String.valueOf(lowStockCount));
            
            // Count unique categories
            Set<String> categories = new HashSet<>();
            for (Product p : products) {
                if (p.getProductType() != null) {
                    categories.add(p.getProductType());
                }
            }
            categoriesLabel.setText(String.valueOf(categories.size()));
            
            // Load recent products (top 10)
            ObservableList<Product> recentProducts = FXCollections.observableArrayList(
                products.size() > 10 ? products.subList(0, 10) : products
            );
            recentActivityTable.setItems(recentProducts);
            
        } catch (Exception e) {
            showError("Failed to load dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboardData();
        showInfo("Dashboard data refreshed successfully!");
    }
    
    @FXML
    private void handleGenerateReport() {
        try {
            String fileName = csvHelper.generateReport();
            
            // Send report via email if user email is available
            if (currentUser != null && currentUser.getEmail() != null) {
                new Thread(() -> {
                    try {
                        EmailUtil.sendReport(
                            currentUser.getEmail(),
                            "Daily Inventory Report",
                            "Please find the attached inventory report.",
                            fileName
                        );
                        javafx.application.Platform.runLater(() -> 
                            showSuccess("Report generated and sent to " + currentUser.getEmail())
                        );
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> 
                            showWarning("Report generated but failed to send email: " + e.getMessage())
                        );
                    }
                }).start();
            } else {
                showSuccess("Report generated successfully: " + fileName);
            }
            
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

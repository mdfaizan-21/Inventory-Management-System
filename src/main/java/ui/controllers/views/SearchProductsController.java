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

import java.util.Collections;
import java.util.List;

public class SearchProductsController implements BaseViewController {
    
    @FXML
    private TextField idSearchField;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private TextField minPriceField;
    
    @FXML
    private TextField maxPriceField;
    
    @FXML
    private TableView<Product> resultsTable;
    
    @FXML
    private TableColumn<Product, Integer> idCol;
    
    @FXML
    private TableColumn<Product, String> nameCol;
    
    @FXML
    private TableColumn<Product, String> categoryCol;
    
    @FXML
    private TableColumn<Product, Integer> qtyCol;
    
    @FXML
    private TableColumn<Product, Double> priceCol;
    
    @FXML
    private Label countLabel;
    
    @FXML
    private Label messageLabel;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void initialize() {
        // Set up table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("productType"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
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
    }
    
    @FXML
    private void handleSearchById() {
        String idText = idSearchField.getText().trim();
        
        if (idText.isEmpty()) {
            showError("Please enter a product ID");
            return;
        }
        
        try {
            int productId = Integer.parseInt(idText);
            Product product = inventoryManagement.ReadById(productId);
            
            if (product == null) {
                showWarning("No product found with ID: " + productId);
                displayResults(Collections.emptyList());
            } else {
                showInfo("Product found!");
                displayResults(Collections.singletonList(product));
            }
            
        } catch (NumberFormatException e) {
            showError("Invalid product ID format");
        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearchByCategory() {
        String category = categoryComboBox.getValue();
        
        if (category == null || category.trim().isEmpty()) {
            showError("Please select a category");
            return;
        }
        
        try {
            List<Product> products = inventoryManagement.SearchProductsByCategory(category.trim());
            
            if (products.isEmpty()) {
                showWarning("No products found in category: " + category);
            } else {
                showInfo(products.size() + " product(s) found in category: " + category);
            }
            
            displayResults(products);
            
        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearchByPrice() {
        String minPriceText = minPriceField.getText().trim();
        String maxPriceText = maxPriceField.getText().trim();
        
        if (minPriceText.isEmpty() || maxPriceText.isEmpty()) {
            showError("Please enter both minimum and maximum prices");
            return;
        }
        
        try {
            double minPrice = Double.parseDouble(minPriceText);
            double maxPrice = Double.parseDouble(maxPriceText);
            
            if (minPrice < 0 || maxPrice < 0) {
                showError("Prices cannot be negative");
                return;
            }
            
            if (minPrice > maxPrice) {
                showError("Minimum price cannot be greater than maximum price");
                return;
            }
            
            List<Product> products = inventoryManagement.SearchProductsByPriceRange(minPrice, maxPrice);
            
            if (products.isEmpty()) {
                showWarning("No products found in price range: $" + minPrice + " - $" + maxPrice);
            } else {
                showInfo(products.size() + " product(s) found in price range");
            }
            
            displayResults(products);
            
        } catch (NumberFormatException e) {
            showError("Invalid price format");
        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleShowAll() {
        try {
            List<Product> products = inventoryManagement.Read();
            showInfo("Showing all products");
            displayResults(products);
            
            // Clear search fields
            idSearchField.clear();
            categoryComboBox.setValue(null);
            minPriceField.clear();
            maxPriceField.clear();
            
        } catch (Exception e) {
            showError("Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayResults(List<Product> products) {
        ObservableList<Product> observableProducts = FXCollections.observableArrayList(products);
        resultsTable.setItems(observableProducts);
        countLabel.setText(products.size() + " product(s) found");
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
    
    private void showWarning(String message) {
        messageLabel.setText("⚠️ " + message);
        messageLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
    
    private void showInfo(String message) {
        messageLabel.setText("ℹ️ " + message);
        messageLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");
        messageLabel.setVisible(true);
    }
}

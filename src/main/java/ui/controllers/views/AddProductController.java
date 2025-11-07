package ui.controllers.views;

import Exceptions.DuplicateProductException;
import Models.Product;
import Models.User;
import Services.InventoryManagement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ui.controllers.BaseViewController;

public class AddProductController implements BaseViewController {
    
    @FXML
    private TextField productIdField;
    
    @FXML
    private TextField productNameField;
    
    @FXML
    private ComboBox<String> categoryComboBox;
    
    @FXML
    private Spinner<Integer> quantitySpinner;
    
    @FXML
    private TextField priceField;
    
    @FXML
    private Spinner<Integer> thresholdSpinner;
    
    @FXML
    private Label messageLabel;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void handleAddProduct() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Parse inputs
            int productId = Integer.parseInt(productIdField.getText().trim());
            String productName = productNameField.getText().trim();
            String category = categoryComboBox.getValue();
            int quantity = quantitySpinner.getValue();
            double price = Double.parseDouble(priceField.getText().trim());
            int threshold = thresholdSpinner.getValue();
            
            // Create product
            Product newProduct = new Product(productId, productName, category, quantity, price);
            newProduct.setThresholdLimit(threshold);
            
            // Check for duplicate
            Product existingProduct = inventoryManagement.ReadById(productId);
            if (existingProduct != null) {
                showError("Product with ID " + productId + " already exists!");
                return;
            }
            
            // Add product
            inventoryManagement.addElementFromCSV(newProduct);
            
            showSuccess("✅ Product added successfully!");
            handleClear();
            
        } catch (NumberFormatException e) {
            showError("Invalid number format. Please check Product ID and Price fields.");
        } catch (DuplicateProductException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Failed to add product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClear() {
        productIdField.clear();
        productNameField.clear();
        categoryComboBox.setValue(null);
        quantitySpinner.getValueFactory().setValue(0);
        priceField.clear();
        thresholdSpinner.getValueFactory().setValue(10);
        messageLabel.setVisible(false);
    }
    
    private boolean validateInputs() {
        // Product ID
        if (productIdField.getText().trim().isEmpty()) {
            showError("Product ID is required");
            return false;
        }
        
        try {
            int id = Integer.parseInt(productIdField.getText().trim());
            if (id <= 0) {
                showError("Product ID must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Product ID must be a valid number");
            return false;
        }
        
        // Product Name
        if (productNameField.getText().trim().isEmpty()) {
            showError("Product Name is required");
            return false;
        }
        
        // Category
        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().trim().isEmpty()) {
            showError("Category is required");
            return false;
        }
        
        // Price
        if (priceField.getText().trim().isEmpty()) {
            showError("Price is required");
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) {
                showError("Price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Price must be a valid number");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }
}

package ui.controllers.views;

import Models.Product;
import Models.User;
import Services.InventoryManagement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ui.controllers.BaseViewController;

public class UpdateProductController implements BaseViewController {
    
    @FXML
    private TextField searchIdField;
    
    @FXML
    private GridPane updateForm;
    
    @FXML
    private HBox buttonBox;
    
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
    private Product currentProduct;
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    @FXML
    private void handleSearchProduct() {
        String idText = searchIdField.getText().trim();
        
        if (idText.isEmpty()) {
            showError("Please enter a product ID");
            return;
        }
        
        try {
            int productId = Integer.parseInt(idText);
            Product product = inventoryManagement.ReadById(productId);
            
            if (product == null) {
                showError("Product with ID " + productId + " not found");
                hideForm();
                return;
            }
            
            // Load product data into form
            currentProduct = product;
            loadProductData(product);
            showForm();
            showInfo("Product found! You can now update the details.");
            
        } catch (NumberFormatException e) {
            showError("Invalid product ID format");
        } catch (Exception e) {
            showError("Failed to search product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdateProduct() {
        if (currentProduct == null) {
            showError("No product selected for update");
            return;
        }
        
        if (!validateInputs()) {
            return;
        }
        
        try {
            // Update product fields
            currentProduct.setProductName(productNameField.getText().trim());
            currentProduct.setProductType(categoryComboBox.getValue());
            currentProduct.setAvailableQty(quantitySpinner.getValue());
            currentProduct.setPrice(Double.parseDouble(priceField.getText().trim()));
            currentProduct.setThresholdLimit(thresholdSpinner.getValue());
            
            // Perform update
            Product updatedProduct = inventoryManagement.update(currentProduct);
            
            if (updatedProduct != null) {
                showSuccess("✅ Product updated successfully!");
                // Keep the form visible with updated data
                loadProductData(updatedProduct);
            } else {
                showError("Failed to update product");
            }
            
        } catch (NumberFormatException e) {
            showError("Invalid number format in price field");
        } catch (Exception e) {
            showError("Failed to update product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleReset() {
        if (currentProduct != null) {
            loadProductData(currentProduct);
            showInfo("Form reset to original values");
        }
    }
    
    private void loadProductData(Product product) {
        productIdField.setText(String.valueOf(product.getProductId()));
        productNameField.setText(product.getProductName());
        categoryComboBox.setValue(product.getProductType());
        quantitySpinner.getValueFactory().setValue(product.getAvailableQty());
        priceField.setText(String.valueOf(product.getPrice()));
        
        if (product.getThresholdLimit() != null) {
            thresholdSpinner.getValueFactory().setValue(product.getThresholdLimit());
        } else {
            thresholdSpinner.getValueFactory().setValue(10);
        }
    }
    
    private void showForm() {
        updateForm.setVisible(true);
        updateForm.setManaged(true);
        buttonBox.setVisible(true);
        buttonBox.setManaged(true);
    }
    
    private void hideForm() {
        updateForm.setVisible(false);
        updateForm.setManaged(false);
        buttonBox.setVisible(false);
        buttonBox.setManaged(false);
        currentProduct = null;
    }
    
    private boolean validateInputs() {
        if (productNameField.getText().trim().isEmpty()) {
            showError("Product Name is required");
            return false;
        }
        
        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().trim().isEmpty()) {
            showError("Category is required");
            return false;
        }
        
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
    
    private void showInfo(String message) {
        messageLabel.setText("ℹ️ " + message);
        messageLabel.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setVisible(true);
    }
}

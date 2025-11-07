package ui.controllers.views;

import Exceptions.ProductNotFoundException;
import Models.Product;
import Models.User;
import Services.InventoryManagement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.controllers.BaseViewController;

import java.util.Optional;

public class DeleteProductController implements BaseViewController {
    
    @FXML
    private TextField productIdField;
    
    @FXML
    private VBox productDetailsBox;
    
    @FXML
    private HBox buttonBox;
    
    @FXML
    private Label idLabel;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label categoryLabel;
    
    @FXML
    private Label qtyLabel;
    
    @FXML
    private Label priceLabel;
    
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
        String idText = productIdField.getText().trim();
        
        if (idText.isEmpty()) {
            showError("Please enter a product ID");
            return;
        }
        
        try {
            int productId = Integer.parseInt(idText);
            Product product = inventoryManagement.ReadById(productId);
            
            if (product == null) {
                showError("Product with ID " + productId + " not found");
                hideDetails();
                return;
            }
            
            // Display product details
            currentProduct = product;
            displayProductDetails(product);
            showDetails();
            messageLabel.setVisible(false);
            
        } catch (NumberFormatException e) {
            showError("Invalid product ID format");
            hideDetails();
        } catch (Exception e) {
            showError("Failed to search product: " + e.getMessage());
            hideDetails();
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteProduct() {
        if (currentProduct == null) {
            showError("No product selected for deletion");
            return;
        }
        
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Product: " + currentProduct.getProductName());
        alert.setContentText("Are you sure you want to delete this product?\nThis action cannot be undone!");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                inventoryManagement.delete(currentProduct.getProductId());
                showSuccess("✅ Product deleted successfully!");
                
                // Clear the form
                handleCancel();
                productIdField.clear();
                
            } catch (ProductNotFoundException e) {
                showError(e.getMessage());
            } catch (Exception e) {
                showError("Failed to delete product: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        hideDetails();
        currentProduct = null;
        messageLabel.setVisible(false);
    }
    
    private void displayProductDetails(Product product) {
        idLabel.setText(String.valueOf(product.getProductId()));
        nameLabel.setText(product.getProductName());
        categoryLabel.setText(product.getProductType());
        qtyLabel.setText(String.valueOf(product.getAvailableQty()));
        priceLabel.setText(String.format("$%.2f", product.getPrice()));
    }
    
    private void showDetails() {
        productDetailsBox.setVisible(true);
        productDetailsBox.setManaged(true);
        buttonBox.setVisible(true);
        buttonBox.setManaged(true);
    }
    
    private void hideDetails() {
        productDetailsBox.setVisible(false);
        productDetailsBox.setManaged(false);
        buttonBox.setVisible(false);
        buttonBox.setManaged(false);
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

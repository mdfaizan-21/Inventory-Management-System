package controllers;

import Exceptions.DuplicateProductException;
import Models.Product;
import Services.InventoryManagement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.SceneNavigator;
import util.SessionManager;

/**
 * Controller for Add/Edit Product Form
 */
public class ProductFormController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField productIdField;

    @FXML
    private TextField productNameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField priceField;

    @FXML
    private Label errorLabel;

    private DashboardController dashboardController;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    private boolean isEditMode = false;
    private Product existingProduct;

    /**
     * Set reference to dashboard controller for refreshing data
     */
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    /**
     * Set whether this is edit mode or add mode
     */
    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;

        if (editMode) {
            titleLabel.setText("Edit Product");
            productIdField.setEditable(false); // Don't allow ID changes in edit mode
        } else {
            titleLabel.setText("Add Product");
        }
    }

    /**
     * Set product data for editing
     */
    public void setProduct(Product product) {
        this.existingProduct = product;

        // Populate fields
        productIdField.setText(String.valueOf(product.getProductId()));
        productNameField.setText(product.getProductName());
        categoryField.setText(product.getProductType());
        quantityField.setText(String.valueOf(product.getAvailableQty()));
        priceField.setText(String.valueOf(product.getPrice()));
    }

    /**
     * Handle Save button
     */
    @FXML
    private void handleSave() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            int id = Integer.parseInt(productIdField.getText().trim());
            String name = productNameField.getText().trim();
            String category = categoryField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            Product product = new Product(id, name, category, quantity, price);

            if (isEditMode) {
                // Update existing product
                Product updated = inventoryManagement.update(product);
                if (updated != null) {
                    returnToDashboard();
                } else {
                    showError("Failed to update product");
                }
            } else {
                // Add new product - using addElementFromCSV to check for duplicates
                try {
                    inventoryManagement.addElementFromCSV(product);
                    returnToDashboard();
                } catch (DuplicateProductException e) {
                    showError(e.getMessage());
                }
            }

        } catch (NumberFormatException e) {
            showError("Invalid number format for ID, Quantity, or Price");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    /**
     * Handle Cancel button
     */
    @FXML
    private void handleCancel() {
        returnToDashboard();
    }

    /**
     * Validate all input fields
     */
    private boolean validateInputs() {
        if (productIdField.getText().trim().isEmpty()) {
            showError("Product ID is required");
            return false;
        }

        if (productNameField.getText().trim().isEmpty()) {
            showError("Product Name is required");
            return false;
        }

        if (categoryField.getText().trim().isEmpty()) {
            showError("Category is required");
            return false;
        }

        if (quantityField.getText().trim().isEmpty()) {
            showError("Quantity is required");
            return false;
        }

        if (priceField.getText().trim().isEmpty()) {
            showError("Price is required");
            return false;
        }

        // Validate numeric fields
        try {
            Integer.parseInt(productIdField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Product ID must be a valid number");
            return false;
        }

        try {
            Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Quantity must be a valid number");
            return false;
        }

        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Price must be a valid number");
            return false;
        }

        return true;
    }

    /**
     * Return to dashboard
     */
    private void returnToDashboard() {
        Stage stage = (Stage) productIdField.getScene().getWindow();
        DashboardController controller = (DashboardController) SceneNavigator.loadSceneWithController(
                stage, "/fxml/Dashboard.fxml", "Dashboard - Inventory Management System");

        // Pass the current user back to dashboard
        if (controller != null) {
            if (SessionManager.getCurrentUser() != null) {
                controller.setCurrentUser(SessionManager.getCurrentUser());
            }
            // Reload products
            controller.reloadProducts();
        }
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }
}

package controllers;

import Exceptions.ProductNotFoundException;
import Models.Product;
import Models.User;
import Services.InventoryManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.CSVHelper;
import util.EmailUtil;
import util.SceneNavigator;
import util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the main Dashboard
 * Displays product inventory and handles all product operations
 */
public class DashboardController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, Integer> quantityColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private Label userLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField searchByIdField;

    @FXML
    private TextField searchByCategoryField;

    @FXML
    private Button addProductBtn;

    @FXML
    private Button updateProductBtn;

    @FXML
    private Button deleteProductBtn;

    @FXML
    private Button generateReportBtn;

    @FXML
    private Button importCsvBtn;

    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    private CSVHelper csvHelper = new CSVHelper();
    private ObservableList<Product> productList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productType"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Configure table to show all items with proper scrolling
        productTable.setItems(productList);
        productTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // Don't load products initially - user must click "Show All Products"
        productList.clear();
    }

    /**
     * Set the current logged-in user and adjust UI based on role
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        userLabel.setText("User: " + user.getUserName() + " (" + user.getRole() + ")");

        // Hide admin-only buttons for regular users
        if (user.getRole().equalsIgnoreCase("USER")) {
            addProductBtn.setVisible(false);
            updateProductBtn.setVisible(false);
            deleteProductBtn.setVisible(false);
            importCsvBtn.setVisible(false);
        }
    }

    /**
     * Load all products into the table
     */
    private void loadProducts() {
        List<Product> products = inventoryManagement.Read();
        productList.clear();
        productList.addAll(products);
        productTable.setItems(productList);
        productTable.refresh(); // Force table refresh to ensure all items are visible
    }

    /**
     * Handle Add Product button
     */
    @FXML
    private void handleAddProduct() {
        Stage stage = (Stage) productTable.getScene().getWindow();
        ProductFormController controller = (ProductFormController) SceneNavigator.loadSceneWithController(
                stage, "/fxml/ProductForm.fxml", "Add Product");

        if (controller != null) {
            controller.setDashboardController(this);
            controller.setEditMode(false);
        }
    }

    /**
     * Handle Update Product button
     */
    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("Please select a product to update");
            return;
        }

        Stage stage = (Stage) productTable.getScene().getWindow();
        ProductFormController controller = (ProductFormController) SceneNavigator.loadSceneWithController(
                stage, "/fxml/ProductForm.fxml", "Update Product");

        if (controller != null) {
            controller.setDashboardController(this);
            controller.setEditMode(true);
            controller.setProduct(selectedProduct);
        }
    }

    /**
     * Handle Delete Product button
     */
    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showError("Please select a product to delete");
            return;
        }

        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete: " + selectedProduct.getProductName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                inventoryManagement.delete(selectedProduct.getProductId());
                showStatus("Product deleted successfully!");
                loadProducts();
            } catch (ProductNotFoundException e) {
                showError(e.getMessage());
            }
        }
    }

    /**
     * Handle Show All Products button
     */
    @FXML
    private void handleShowAllProducts() {
        loadProducts();
        showStatus("All products loaded");
    }

    /**
     * Handle Refresh button
     */
    @FXML
    private void handleRefresh() {
        loadProducts();
        showStatus("Product list refreshed");
    }

    /**
     * Handle Search by ID
     */
    @FXML
    private void handleSearchById() {
        String idText = searchByIdField.getText().trim();

        if (idText.isEmpty()) {
            loadProducts();
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Product product = inventoryManagement.ReadById(id);

            if (product != null) {
                productList.clear();
                productList.add(product);
                productTable.setItems(productList);
                showStatus("Product found!");
            } else {
                showError("Product not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            showError("Invalid ID format");
        }
    }

    /**
     * Handle Search by Category
     */
    @FXML
    private void handleSearchByCategory() {
        String category = searchByCategoryField.getText().trim();

        if (category.isEmpty()) {
            loadProducts();
            return;
        }

        List<Product> products = inventoryManagement.SearchProductsByCategory(category);

        if (!products.isEmpty()) {
            productList.clear();
            productList.addAll(products);
            productTable.setItems(productList);
            showStatus("Found " + products.size() + " product(s) in category: " + category);
        } else {
            showError("No products found in category: " + category);
        }
    }

    /**
     * Handle Generate Report
     */
    @FXML
    private void handleGenerateReport() {
        try {
            String reportName = csvHelper.generateReport();

            if (currentUser != null && currentUser.getEmail() != null) {
                EmailUtil.sendReport(currentUser.getEmail(),
                        "Daily Report",
                        "The inventory report has been attached to this email",
                        reportName);
                showStatus("Report generated and sent to: " + currentUser.getEmail());
            } else {
                showStatus("Report generated: " + reportName);
            }
        } catch (Exception e) {
            showError("Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Handle Import CSV
     */
    @FXML
    private void handleImportCSV() {
        try {
            int count = csvHelper.readDATFromCSV();
            showStatus("Successfully imported " + count + " products from CSV");
            loadProducts();
        } catch (Exception e) {
            showError("Failed to import CSV: " + e.getMessage());
        }
    }

    /**
     * Handle Logout
     */
    @FXML
    private void handleLogout() {
        SessionManager.clearSession();
        Stage stage = (Stage) productTable.getScene().getWindow();
        SceneNavigator.loadScene(stage, "/fxml/Login.fxml", "Login - Inventory Management System");
    }

    /**
     * Public method to reload products (called from ProductForm after save)
     */
    public void reloadProducts() {
        loadProducts();
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Display status message
     */
    private void showStatus(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setVisible(true);

        // Auto-hide after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> statusLabel.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

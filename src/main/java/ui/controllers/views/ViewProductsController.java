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

import java.util.List;
import java.util.stream.Collectors;

public class ViewProductsController implements BaseViewController {
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<Product> productsTable;
    
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
    private TableColumn<Product, Integer> thresholdCol;
    
    @FXML
    private Label countLabel;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    private CSVHelper csvHelper = new CSVHelper();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
        loadProducts();
    }
    
    @FXML
    private void initialize() {
        // Set up table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("productType"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("thresholdLimit"));
        
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
        qtyCol.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    setText(stock.toString());
                    
                    // Check against threshold
                    Integer threshold = product.getThresholdLimit();
                    if (threshold != null && stock < threshold) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold;");
                    } else if (stock < 10) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
    
    private void loadProducts() {
        try {
            List<Product> products = inventoryManagement.Read();
            allProducts.clear();
            allProducts.addAll(products);
            productsTable.setItems(allProducts);
            updateCount();
        } catch (Exception e) {
            showError("Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            productsTable.setItems(allProducts);
        } else {
            ObservableList<Product> filtered = FXCollections.observableArrayList(
                allProducts.stream()
                    .filter(p -> 
                        (p.getProductName() != null && p.getProductName().toLowerCase().contains(searchText)) ||
                        (p.getProductId() != null && p.getProductId().toString().contains(searchText)) ||
                        (p.getProductType() != null && p.getProductType().toLowerCase().contains(searchText))
                    )
                    .collect(Collectors.toList())
            );
            productsTable.setItems(filtered);
        }
        updateCount();
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadProducts();
        showInfo("Products list refreshed");
    }
    
    @FXML
    private void handleViewDetails() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select a product to view details");
            return;
        }
        
        // Show product details in a dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText("Product Information");
        
        String details = String.format(
            "Product ID: %d\n" +
            "Name: %s\n" +
            "Category: %s\n" +
            "Available Quantity: %d\n" +
            "Price: $%.2f\n" +
            "Threshold Limit: %d",
            selected.getProductId(),
            selected.getProductName(),
            selected.getProductType(),
            selected.getAvailableQty(),
            selected.getPrice(),
            selected.getThresholdLimit() != null ? selected.getThresholdLimit() : 0
        );
        
        alert.setContentText(details);
        alert.showAndWait();
    }
    
    @FXML
    private void handleExport() {
        try {
            String fileName = csvHelper.generateReport();
            showSuccess("Products exported successfully to: " + fileName);
        } catch (Exception e) {
            showError("Failed to export products: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateCount() {
        countLabel.setText("Total: " + productsTable.getItems().size() + " products");
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

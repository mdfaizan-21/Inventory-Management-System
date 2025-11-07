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

import java.util.List;
import java.util.stream.Collectors;

public class StockAlertsController implements BaseViewController {
    
    @FXML
    private TableView<Product> alertsTable;
    
    @FXML
    private TableColumn<Product, Integer> idCol;
    
    @FXML
    private TableColumn<Product, String> nameCol;
    
    @FXML
    private TableColumn<Product, String> categoryCol;
    
    @FXML
    private TableColumn<Product, Integer> stockCol;
    
    @FXML
    private TableColumn<Product, Integer> thresholdCol;
    
    @FXML
    private TableColumn<Product, String> statusCol;
    
    @FXML
    private Label alertCountLabel;
    
    private User currentUser;
    private InventoryManagement inventoryManagement = new InventoryManagement();
    
    @Override
    public void setUser(User user) {
        this.currentUser = user;
        loadLowStockProducts();
    }
    
    @FXML
    private void initialize() {
        // Set up table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("productType"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("availableQty"));
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("thresholdLimit"));
        
        // Status column
        statusCol.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    int stock = product.getAvailableQty();
                    Integer threshold = product.getThresholdLimit() != null ? product.getThresholdLimit() : 10;
                    
                    if (stock == 0) {
                        setText("OUT OF STOCK");
                        setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else if (stock < threshold) {
                        setText("LOW STOCK");
                        setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else {
                        setText("NORMAL");
                        setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    }
                }
            }
        });
        
        // Highlight low stock in stock column
        stockCol.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(stock.toString());
                    if (stock == 0) {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold;");
                    } else if (stock < 5) {
                        setStyle("-fx-background-color: #ffe6cc; -fx-text-fill: #cc6600; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    }
                }
            }
        });
    }
    
    @FXML
    private void handleRefresh() {
        loadLowStockProducts();
    }
    
    private void loadLowStockProducts() {
        try {
            List<Product> allProducts = inventoryManagement.Read();
            
            // Filter products with stock below threshold
            List<Product> lowStockProducts = allProducts.stream()
                .filter(p -> {
                    int stock = p.getAvailableQty() != null ? p.getAvailableQty() : 0;
                    int threshold = p.getThresholdLimit() != null ? p.getThresholdLimit() : 10;
                    return stock < threshold;
                })
                .collect(Collectors.toList());
            
            ObservableList<Product> observableProducts = FXCollections.observableArrayList(lowStockProducts);
            alertsTable.setItems(observableProducts);
            
            alertCountLabel.setText(lowStockProducts.size() + " alert(s)");
            alertCountLabel.setStyle(lowStockProducts.size() > 0 ? 
                "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" : 
                "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to load stock alerts: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}

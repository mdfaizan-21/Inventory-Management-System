package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryApp extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Inventory Management System");
        
        // Load login view
        showLoginView();
    }
    
    public static void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(InventoryApp.class.getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(InventoryApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void showDashboardView(Models.User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(InventoryApp.class.getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load();
        
        // Pass user to dashboard controller
        ui.controllers.DashboardController controller = loader.getController();
        controller.setUser(user);
        
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(InventoryApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

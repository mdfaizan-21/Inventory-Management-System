package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for navigating between different scenes in the JavaFX
 * application
 */
public class SceneNavigator {

    /**
     * Loads a new scene from an FXML file
     * 
     * @param stage    The primary stage
     * @param fxmlPath Path to the FXML file (relative to resources)
     * @param title    Title for the window
     */
    public static void loadScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Set appropriate scene size based on FXML file
            Scene scene;
            if (fxmlPath.contains("Dashboard")) {
                // Dashboard: larger but constrained to fit screen
                double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
                double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                double maxHeight = screenHeight * 0.9; // 90% of screen height
                double maxWidth = screenWidth * 0.95; // 95% of screen width
                
                scene = new Scene(root, 1200, 700); // Default size
                stage.setMinWidth(1000);
                stage.setMinHeight(600);
                stage.setMaxWidth(maxWidth);
                stage.setMaxHeight(maxHeight);
                stage.setResizable(true);
            } else if (fxmlPath.contains("ProductForm")) {
                scene = new Scene(root, 550, 650);
                stage.setResizable(false);
            } else {
                // Login, Register, Verification: smaller fixed size
                scene = new Scene(root, 500, 600);
                stage.setResizable(false);
            }

            // Apply CSS
            scene.getStylesheets().add(SceneNavigator.class.getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen(); // Center window on screen
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
        }
    }

    /**
     * Loads a scene and returns the controller
     * 
     * @param stage    The primary stage
     * @param fxmlPath Path to the FXML file
     * @param title    Title for the window
     * @return The controller instance
     */
    public static Object loadSceneWithController(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Set appropriate scene size based on FXML file
            Scene scene;
            if (fxmlPath.contains("Dashboard")) {
                // Dashboard: larger but constrained to fit screen
                double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
                double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                double maxHeight = screenHeight * 0.9; // 90% of screen height
                double maxWidth = screenWidth * 0.95; // 95% of screen width
                
                scene = new Scene(root, 1200, 700); // Default size
                stage.setMinWidth(1000);
                stage.setMinHeight(600);
                stage.setMaxWidth(maxWidth);
                stage.setMaxHeight(maxHeight);
                stage.setResizable(true);
            } else if (fxmlPath.contains("ProductForm")) {
                scene = new Scene(root, 550, 650);
                stage.setResizable(false);
            } else {
                // Login, Register, Verification: smaller fixed size
                scene = new Scene(root, 500, 600);
                stage.setResizable(false);
            }

            // Apply CSS
            scene.getStylesheets().add(SceneNavigator.class.getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen(); // Center window on screen
            stage.show();

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
            return null;
        }
    }
}

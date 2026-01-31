package robotsimulator.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import robotsimulator.core.RobotManager;
import static robotsimulator.model.GridConstants.BACKGROUND_COLOR;
import static robotsimulator.model.GridConstants.CELL_SIZE;
import static robotsimulator.model.GridConstants.GRID_SIZE;

/**
 * Main coordinator that builds the complete scene.
 */
public class ScreenBuilder {
    
    public static Scene buildScene() {
        GridManager gridManager = new GridManager();
        
        // Create a pane for robots to move on top of the grid
        Pane robotLayer = new Pane();
        robotLayer.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        robotLayer.setMouseTransparent(true);
        
        RobotManager robotManager = new RobotManager(robotLayer, gridManager);
        ControlPanel controlPanel = new ControlPanel(gridManager, robotManager);
        
        gridManager.setListener(controlPanel::updateCounter);
        
        // Create the game grid area
        StackPane centerPane = new StackPane();
        centerPane.getChildren().addAll(gridManager.buildGrid(), robotLayer);
        
        // Style the game grid area
        centerPane.setStyle(
            "-fx-background-color: #1A1A2E;" +  // Dark blue background for game area
            "-fx-border-color: #3498DB;" +      // Blue border
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        // Create the control panel area
        VBox controlPanelBox = new VBox();
        controlPanelBox.getChildren().add(controlPanel.build());
        controlPanelBox.setStyle(
            "-fx-background-color: #2C3E50;" +  // Same as control panel background
            "-fx-padding: 10;" +
            "-fx-border-color: #4A6572;" +
            "-fx-border-width: 0 0 0 3;"  // Left border only
        );
        
        // Create a container for the game area
        VBox gameContainer = new VBox(10);
        gameContainer.setPadding(new Insets(20));
        gameContainer.setAlignment(javafx.geometry.Pos.CENTER);
        gameContainer.getChildren().add(centerPane);
        
        // Create main layout with game on left, controls on right
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
        
        // Set growth priorities - game area expands, controls have fixed width
        HBox.setHgrow(gameContainer, Priority.ALWAYS);
        controlPanelBox.setPrefWidth(500);  // Fixed width for control panel
        controlPanelBox.setMinWidth(450);
        controlPanelBox.setMaxWidth(550);
        
        mainLayout.getChildren().addAll(gameContainer, controlPanelBox);
        
        // Add some spacing between panels
        mainLayout.setSpacing(10);
        mainLayout.setPadding(new Insets(15));
        
        return new Scene(mainLayout, 1400, 900);  // Wider window for side-by-side layout
    }
    
    // Alternative layout with controls on left (some users prefer this)
    public static Scene buildSceneWithLeftControls() {
        GridManager gridManager = new GridManager();
        
        Pane robotLayer = new Pane();
        robotLayer.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        robotLayer.setMouseTransparent(true);
        
        RobotManager robotManager = new RobotManager(robotLayer, gridManager);
        ControlPanel controlPanel = new ControlPanel(gridManager, robotManager);
        
        gridManager.setListener(controlPanel::updateCounter);
        
        StackPane centerPane = new StackPane();
        centerPane.getChildren().addAll(gridManager.buildGrid(), robotLayer);
        
        // Style the game grid area
        centerPane.setStyle(
            "-fx-background-color: #1A1A2E;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        // Create the control panel area
        VBox controlPanelBox = new VBox();
        controlPanelBox.getChildren().add(controlPanel.build());
        controlPanelBox.setStyle(
            "-fx-background-color: #2C3E50;" +
            "-fx-padding: 10;" +
            "-fx-border-color: #4A6572;" +
            "-fx-border-width: 0 3 0 0;"  // Right border only
        );
        
        VBox gameContainer = new VBox(10);
        gameContainer.setPadding(new Insets(20));
        gameContainer.setAlignment(javafx.geometry.Pos.CENTER);
        gameContainer.getChildren().add(centerPane);
        
        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
        
        // Controls on left, game on right
        HBox.setHgrow(gameContainer, Priority.ALWAYS);
        controlPanelBox.setPrefWidth(500);
        controlPanelBox.setMinWidth(450);
        controlPanelBox.setMaxWidth(550);
        
        mainLayout.getChildren().addAll(controlPanelBox, gameContainer);
        mainLayout.setSpacing(10);
        mainLayout.setPadding(new Insets(15));
        
        return new Scene(mainLayout, 1400, 900);
    }
    
    // Responsive layout that adapts to window size
    public static Scene buildResponsiveScene() {
        GridManager gridManager = new GridManager();
        
        Pane robotLayer = new Pane();
        robotLayer.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        robotLayer.setMouseTransparent(true);
        
        RobotManager robotManager = new RobotManager(robotLayer, gridManager);
        ControlPanel controlPanel = new ControlPanel(gridManager, robotManager);
        
        gridManager.setListener(controlPanel::updateCounter);
        
        // Create the game area
        StackPane gameArea = new StackPane();
        gameArea.getChildren().addAll(gridManager.buildGrid(), robotLayer);
        gameArea.setStyle(
            "-fx-background-color: #1A1A2E;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        // Create the control panel area
        VBox controlPanelArea = new VBox();
        VBox controlContent = controlPanel.build();
        controlPanelArea.getChildren().add(controlContent);
        controlPanelArea.setStyle(
            "-fx-background-color: #2C3E50;" +
            "-fx-padding: 15;" +
            "-fx-border-color: #4A6572;" +
            "-fx-border-width: 3;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        // Use BorderPane for flexible layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
        root.setPadding(new Insets(20));
        
        // Center area for game
        VBox centerContainer = new VBox(gameArea);
        centerContainer.setAlignment(javafx.geometry.Pos.CENTER);
        VBox.setVgrow(gameArea, Priority.ALWAYS);
        
        // Right area for controls (will collapse on smaller screens)
        controlPanelArea.setPrefWidth(450);
        controlPanelArea.setMinWidth(400);
        
        root.setCenter(centerContainer);
        root.setRight(controlPanelArea);
        
        // Add some spacing
        BorderPane.setMargin(controlPanelArea, new Insets(0, 0, 0, 20));
        
        return new Scene(root, 1400, 900);
    }
    
    private static String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    // Simple side-by-side layout (recommended)
    public static Scene buildDefaultScene() {
        GridManager gridManager = new GridManager();
        
        Pane robotLayer = new Pane();
        robotLayer.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        robotLayer.setMouseTransparent(true);
        
        RobotManager robotManager = new RobotManager(robotLayer, gridManager);
        ControlPanel controlPanel = new ControlPanel(gridManager, robotManager);
        
        gridManager.setListener(controlPanel::updateCounter);
        
        // Game grid with styling
        StackPane gameGrid = new StackPane();
        gameGrid.getChildren().addAll(gridManager.buildGrid(), robotLayer);
        gameGrid.setStyle(
            "-fx-background-color: #1A1A2E;" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;"
        );
        
        // Control panel with styling
        VBox controls = controlPanel.build();
        controls.setStyle(
            "-fx-background-color: #2C3E50;" +
            "-fx-padding: 15;" +
            "-fx-border-color: #4A6572;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        
        // Main container
        HBox container = new HBox(20);
        container.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
        container.setPadding(new Insets(20));
        container.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Set sizes
        gameGrid.setPrefWidth(GRID_SIZE * CELL_SIZE + 40); // Grid + padding
        controls.setPrefWidth(550);
        controls.setMinWidth(450);
        
        // Make game area grow if there's extra space
        HBox.setHgrow(gameGrid, Priority.ALWAYS);
        
        container.getChildren().addAll(gameGrid, controls);
        
        return new Scene(container, 1400, 1000);
    }
}
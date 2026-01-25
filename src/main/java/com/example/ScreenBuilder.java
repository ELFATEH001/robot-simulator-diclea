package com.example;

import static com.example.GridConstants.BACKGROUND_COLOR;
import static com.example.GridConstants.CELL_SIZE;
import static com.example.GridConstants.GRID_SIZE;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

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
    
    // IMPORTANT: Add the same padding as the GridPane to align robots with cells
    // robotLayer.setStyle("-fx-padding: 20;");
    
    RobotManager robotManager = new RobotManager(robotLayer, gridManager);
    ControlPanel controlPanel = new ControlPanel(gridManager, robotManager);

    gridManager.setListener(controlPanel::updateCounter);

    StackPane centerPane = new StackPane();
    centerPane.getChildren().addAll(gridManager.buildGrid(), robotLayer);

    BorderPane root = new BorderPane();
    root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");
    root.setCenter(centerPane);
    root.setBottom(controlPanel.build());

    return new Scene(root, 800, 900);
}

    private static String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
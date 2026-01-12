package com.example;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import static com.example.GridConstants.BACKGROUND_COLOR;

/**
 * Main coordinator that builds the complete scene.
 */
public class ScreenBuilder {

    public static Scene buildScene() {
        GridManager gridManager = new GridManager();
        ControlPanel controlPanel = new ControlPanel(gridManager);

        // Set up listener to update counter when grid state changes
        gridManager.setListener(controlPanel::updateCounter);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + toHexString(BACKGROUND_COLOR) + ";");

        root.setCenter(gridManager.buildGrid());
        root.setBottom(controlPanel.build());

        return new Scene(root, 800, 850);
    }

    private static String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
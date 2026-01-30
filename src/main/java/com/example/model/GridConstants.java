package com.example.model;

import javafx.scene.paint.Color;

/**
 * Contains all constants used in the grid application.
 */
public class GridConstants {
    public static final int GRID_SIZE = 10;
    public static final int CELL_SIZE = 60;
    public static final int CELL_STROKE = 1;
    public static final Color DEFAULT_COLOR = Color.WHITE;
    public static final Color HOVER_COLOR = Color.LIGHTBLUE;
    public static final Color CLICKED_COLOR = Color.WHITE;
    public static final Color DIRTY_COLOR = Color.BROWN;
    public static final Color BACKGROUND_COLOR = Color.web("#2c3e50");
    public static final double GRID_PADDING = 20;

    private GridConstants() {
        // Prevent instantiation
    }
}
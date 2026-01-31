package robotsimulator.model;

import javafx.scene.paint.Color;

/**
 * Contains all constants used in the grid application.
 */
public class GridConstants {
    public static final int GRID_SIZE = 10;
    public static final int CELL_SIZE = 60;
    public static final int CELL_STROKE = 1;
    public static final int START_ROW = 0;
    public static final int START_COL = 0;
    public static final Color DEFAULT_COLOR = Color.WHITE;
    public static final Color HOVER_COLOR = Color.LIGHTBLUE;
    public static final Color CLICKED_COLOR = Color.WHITE;
    public static final Color DIRTY_COLOR = Color.BROWN;
    public static final Color BACKGROUND_COLOR = Color.web("#2c3e50");
    public static final Color WALL_COLOR = Color.DARKGRAY;
    public static final Color WALL_BORDER_COLOR = Color.BLACK;
    public static final double GRID_PADDING = 20;

    // Wall parameters
    public static final int NUM_WALLS = 3; // Number of walls to generate
    public static final int MIN_WALL_LENGTH = 2; // Minimum wall length
    public static final int MAX_WALL_LENGTH = 4; // Maximum wall length

    private GridConstants() {
        // Prevent instantiation
    }
}
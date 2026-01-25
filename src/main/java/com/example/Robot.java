package com.example;

import static com.example.GridConstants.CELL_SIZE;
import static com.example.GridConstants.CELL_STROKE;
import static com.example.GridConstants.GRID_PADDING;
import static com.example.GridConstants.GRID_SIZE;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Represents a robot that moves discretely between grid cells.
 */
public class Robot {
    // Grid position (0-based)
    private int gridRow;
    private int gridCol;
    
    // Target position for movement
    private int targetRow;
    private int targetCol;
    
    // Movement state
    private final BooleanProperty isMoving;
    private AnimationTimer moveTimer;
    private long lastMoveTime;
    private static final long MOVE_STEP_DELAY_NANOS = 500_000_000; // 50ms per step
    
    // Visual position properties (for binding to visual nodes)
    private final DoubleProperty x;
    private final DoubleProperty y;
    
    // Appearance
    private final double radius;
    private Color color;
    private Node visualNode;
    
    // Constructor with grid position (1-based)
    public Robot(int gridRow, int gridCol, double radius) {
        // Convert to 0-based
        this.gridRow = gridRow - 1;
        this.gridCol = gridCol - 1;
        this.targetRow = this.gridRow;
        this.targetCol = this.gridCol;
        this.radius = radius;
        this.color = Color.DODGERBLUE;
        this.isMoving = new SimpleBooleanProperty(false);
        
        // Initialize visual position
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        updateVisualPosition();
        
        // Initialize movement timer
        initializeMoveTimer();
    }
    
    /**
     * Initialize the movement animation timer
     */
    private void initializeMoveTimer() {
        moveTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastMoveTime >= MOVE_STEP_DELAY_NANOS) {
                    performMoveStep();
                    lastMoveTime = now;
                }
            }
        };
    }

    @Override
    public String toString() {
        return "Robot (" + getGridRowOneBased() + ", " + getGridColOneBased() + ")";
    }
    
    /**
     * Perform one step of movement towards target
     */
    private void performMoveStep() {
        // First move vertically (row), then horizontally (col)
        if (gridRow != targetRow) {
            // Move towards target row
            if (gridRow < targetRow) {
                gridRow++;
            } else {
                gridRow--;
            }
            updateVisualPosition();
        } else if (gridCol != targetCol) {
            // Move towards target column
            if (gridCol < targetCol) {
                gridCol++;
            } else {
                gridCol--;
            }
            updateVisualPosition();
        } else {
            // Reached target
            isMoving.set(false);
            moveTimer.stop();
        }
    }
    
    /**
     * Start moving to a target position with cardinal animation
     */
    public void moveToPosition(int targetGridRow, int targetGridCol) {
        // Convert from 1-based to 0-based
        this.targetRow = targetGridRow - 1;
        this.targetCol = targetGridCol - 1;
        
        // Don't start moving if already at target
        if (gridRow == targetRow && gridCol == targetCol) {
            return;
        }
        
        // Start movement animation
        isMoving.set(true);
        lastMoveTime = System.nanoTime();
        moveTimer.start();
    }
    
    /**
     * Check if robot is currently moving
     */
    public boolean isMoving() {
        return isMoving.get();
    }
    
    /**
     * Get the moving property for binding
     */
    public BooleanProperty movingProperty() {
        return isMoving;
    }
    
    /**
     * Update visual position based on grid position
     */
    private void updateVisualPosition() {
        // Calculate center of current cell
        // Add 20 pixels to account for GridPane padding
        double centerX = gridCol * CELL_SIZE + CELL_SIZE / 2 + CELL_STROKE * (gridCol + 1) + GRID_PADDING;
        double centerY = gridRow * CELL_SIZE + CELL_SIZE / 2 + CELL_STROKE * (gridRow + 1) + GRID_PADDING;
        
        setX(centerX);
        setY(centerY);
    }
    
    /**
     * Move up one cell
     */
    public void moveUp() {
        if (gridRow > 0) {
            gridRow--;
            updateVisualPosition();
        }
    }
    
    /**
     * Move down one cell
     */
    public void moveDown() {
        if (gridRow < GRID_SIZE - 1) {
            gridRow++;
            updateVisualPosition();
        }
    }
    
    /**
     * Move left one cell
     */
    public void moveLeft() {
        if (gridCol > 0) {
            gridCol--;
            updateVisualPosition();
        }
    }
    
    /**
     * Move right one cell
     */
    public void moveRight() {
        if (gridCol < GRID_SIZE - 1) {
            gridCol++;
            updateVisualPosition();
        }
    }
    
    /**
     * Set grid position directly (1-based indexing) - TELEPORT (no animation)
     */
    public void setGridPosition(int gridRow, int gridCol) {
        this.gridRow = gridRow - 1;
        this.gridCol = gridCol - 1;
        this.targetRow = this.gridRow;
        this.targetCol = this.gridCol;
        updateVisualPosition();
    }
    
    /**
     * Get current grid row (0-based)
     */
    public int getGridRow() {
        return gridRow;
    }
    
    /**
     * Get current grid column (0-based)
     */
    public int getGridCol() {
        return gridCol;
    }
    
    /**
     * Get current grid row (1-based for GridManager)
     */
    public int getGridRowOneBased() {
        return gridRow + 1;
    }
    
    /**
     * Get current grid column (1-based for GridManager)
     */
    public int getGridColOneBased() {
        return gridCol + 1;
    }
    
    // Property getters for binding
    public DoubleProperty xProperty() {
        return x;
    }
    
    public DoubleProperty yProperty() {
        return y;
    }
    
    // Standard getters and setters
    public double getX() {
        return x.get();
    }
    
    private void setX(double x) {
        this.x.set(x);
    }
    
    public double getY() {
        return y.get();
    }
    
    private void setY(double y) {
        this.y.set(y);
    }
    
    public double getRadius() {
        return radius;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Node getVisualNode() {
        return visualNode;
    }
    
    public void setVisualNode(Node visualNode) {
        this.visualNode = visualNode;
    }
}
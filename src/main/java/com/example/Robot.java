package com.example;

import static com.example.GridConstants.CELL_SIZE;
import static com.example.GridConstants.CELL_STROKE;
import static com.example.GridConstants.GRID_PADDING;
import static com.example.GridConstants.GRID_SIZE;

import javafx.beans.property.DoubleProperty;
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
        this.radius = radius;
        this.color = Color.DODGERBLUE;
        
        // Initialize visual position
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        updateVisualPosition();
    }
    
    /**
     * Update visual position based on grid position
     */
    private void updateVisualPosition() {
        // Calculate center of current cell
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
package robotsimulator.model;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import static robotsimulator.model.GridConstants.CELL_SIZE;
import static robotsimulator.model.GridConstants.CELL_STROKE;
import static robotsimulator.model.GridConstants.GRID_PADDING;
import robotsimulator.ui.GridManager;

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
    
    protected  GridManager gridManager;

    public void setGridManager(GridManager gridManager) {
        this.gridManager = gridManager;
    }

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
            int nextRow = (gridRow < targetRow) ? gridRow + 1 : gridRow - 1;
            
            // Check for wall
            if (gridManager != null && gridManager.isWallZeroBased(nextRow, gridCol)) {
                System.out.println("Path blocked by wall! Stopping movement.");
                isMoving.set(false);
                moveTimer.stop();
                return;
            }
            
            gridRow = nextRow;
            updateVisualPosition();
            
        } else if (gridCol != targetCol) {
            int nextCol = (gridCol < targetCol) ? gridCol + 1 : gridCol - 1;
            
            // Check for wall
            if (gridManager != null && gridManager.isWallZeroBased(gridRow, nextCol)) {
                System.out.println("Path blocked by wall! Stopping movement.");
                isMoving.set(false);
                moveTimer.stop();
                return;
            }
            
            gridCol = nextCol;
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
        int newTargetRow = targetGridRow - 1;
        int newTargetCol = targetGridCol - 1;
        
        // Check if target cell has a wall
        if (gridManager != null && gridManager.isWallZeroBased(newTargetRow, newTargetCol)) {
            System.out.println("Cannot move to wall cell!");
            return;
        }
        
        this.targetRow = newTargetRow;
        this.targetCol = newTargetCol;
        
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
            // Check if the cell above is a wall
            if (gridManager != null && gridManager.isWallZeroBased(gridRow - 1, gridCol)) {
                System.out.println("Hit a wall! Cannot move up.");
                return;
            }
            gridRow--;
            updateVisualPosition();
        }
    }
    
    /**
     * Move down one cell
     */
    public void moveDown() {
        if (gridRow < GridConstants.GRID_SIZE - 1) {
            if (gridManager != null && gridManager.isWallZeroBased(gridRow + 1, gridCol)) {
                System.out.println("Hit a wall! Cannot move down.");
                return;
            }
            gridRow++;
            updateVisualPosition();
        }
    }
    
    /**
     * Move left one cell
     */
    public void moveLeft() {
        if (gridCol > 0) {
            if (gridManager != null && gridManager.isWallZeroBased(gridRow, gridCol - 1)) {
                System.out.println("Hit a wall! Cannot move left.");
                return;
            }
            gridCol--;
            updateVisualPosition();
        }
    }
    
    /**
     * Move right one cell
     */
    public void moveRight() {
        if (gridCol < GridConstants.GRID_SIZE - 1) {
            if (gridManager != null && gridManager.isWallZeroBased(gridRow, gridCol + 1)) {
                System.out.println("Hit a wall! Cannot move right.");
                return;
            }
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
     * Set grid position with wall check (1-based indexing)
     * Returns false if target position has a wall
     */
    public boolean setGridPositionWithWallCheck(int gridRow, int gridCol) {
        // Convert from 1-based to 0-based
        int newRow = gridRow - 1;
        int newCol = gridCol - 1;
        
        // Check if target cell has a wall
        if (gridManager != null && gridManager.isWallZeroBased(newRow, newCol)) {
            System.out.println("Cannot teleport to wall cell at (" + gridRow + ", " + gridCol + ")");
            return false;
        }
        
        this.gridRow = newRow;
        this.gridCol = newCol;
        this.targetRow = this.gridRow;
        this.targetCol = this.gridCol;
        updateVisualPosition();
        return true;
    }

    /**
     * Check if a position has a wall (0-based)
     */
    protected boolean hasWallAt(int row, int col) {
        return gridManager != null && gridManager.isWallZeroBased(row, col);
    }
    
    /**
     * Check if a position has a wall (1-based)
     */
    protected boolean hasWallAtOneBased(int row, int col) {
        return hasWallAt(row - 1, col - 1);
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
    
    public final  void setColor(Color color) {
        this.color = color;
    }
    
    public Node getVisualNode() {
        return visualNode;
    }
    
    public void setVisualNode(Node visualNode) {
        this.visualNode = visualNode;
    }
}
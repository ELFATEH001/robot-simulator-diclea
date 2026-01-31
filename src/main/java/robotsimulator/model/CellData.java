package robotsimulator.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Represents a single cell in the grid with its visual and state data.
 */
public class CellData {
    private final Rectangle rectangle;
    private boolean isColored;
    private boolean isWall;

    public CellData(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.isColored = false;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
    
    public javafx.scene.paint.Paint getFill() {
        return rectangle.getFill();
    }

    public boolean isColored() {
        return isColored;
    }

    public void setColored(boolean colored) {
        // Don't allow coloring walls
        if (!isWall) {
            this.isColored = colored;
        }
    }

    public void setFill(Color color) {
        if (!isWall) {
            rectangle.setFill(color);
        }
        
    }

    public void setWall(boolean wall) {
        this.isWall = wall;
        if (wall) {
            rectangle.setFill(GridConstants.WALL_COLOR);
            rectangle.setStroke(GridConstants.WALL_BORDER_COLOR);
            rectangle.setStrokeWidth(2);
        }
    }
    public boolean isWall() {
        return isWall;
    }
}
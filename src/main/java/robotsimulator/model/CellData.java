package robotsimulator.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Represents a single cell in the grid with its visual and state data.
 */
public class CellData {
    private final Rectangle rectangle;
    private boolean isColored;

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
        isColored = colored;
    }

    public void setFill(Color color) {
        rectangle.setFill(color);
    }
}
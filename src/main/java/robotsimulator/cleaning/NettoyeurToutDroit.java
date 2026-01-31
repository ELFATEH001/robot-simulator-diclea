package robotsimulator.cleaning;

import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A cleaner robot that goes straight down a column, cleaning each cell
 */
public class NettoyeurToutDroit extends RobotCleaner {
    private int startCol;
    private int startRow;
    private int currentRow; // Track which row we've cleaned
    
    /**
     * Constructor with starting column
     */
    public NettoyeurToutDroit(int startCol, GridManager gridManager) {
        super(1, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
        this.startRow = 1;
        this.currentRow = 0; // Haven't started yet
        setColor(Color.LIGHTBLUE);
    }
    
    /**
     * Constructor with random starting column
     */
    public NettoyeurToutDroit(GridManager gridManager) {
        this((int) (Math.random() * GRID_SIZE) + 1, gridManager);
    }
    
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // Increment to next row
        currentRow++;
        
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            return true;
        }
        
        // Move to current row and clean
        setGridPosition(currentRow, startCol);
        cleanCurrentCell();
        
        // Check if we've reached the last row
        if (currentRow >= GRID_SIZE) {
            missionComplete = true;
        }
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        currentRow = startRow - 1; // Will be incremented to startRow on first step
        
        System.out.println("NettoyeurToutDroit mission reset - will clean down from (" + startRow + ", " + startCol + ") to (" + GRID_SIZE + ", " + startCol + ")");
    }
    
    /**
     * Clean the current cell
     */
    private void cleanCurrentCell() {
        if (gridManager != null) {
            gridManager.cleanCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
    public int getStartCol() {
        return startCol;
    }
}
package robotsimulator.cleaning;

import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A cleaner robot that systematically cleans the entire grid
 * from first cell (1,1) to last cell (GRID_SIZE, GRID_SIZE)
 * Using a ZIGZAG pattern:
 * - Row 1: left to right (1,1) -> (1,2) -> ... -> (1,GRID_SIZE)
 * - Row 2: right to left (2,GRID_SIZE) -> (2,GRID_SIZE-1) -> ... -> (2,1)
 * - Row 3: left to right (3,1) -> (3,2) -> ... -> (3,GRID_SIZE)
 * - And so on...
 */
public class NettoyeurComplet extends RobotCleaner {
    private int currentRow;
    private int currentCol;
    
    /**
     * Constructor - always starts at (1,1)
     */
    public NettoyeurComplet(GridManager gridManager) {
        super(1, 1, GridConstants.CELL_SIZE / 3, gridManager);
        this.currentRow = 1;
        this.currentCol = 1;
        setColor(Color.DODGERBLUE);
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // Move to current position
        setGridPosition(currentRow, currentCol);
        
        // Clean current cell
        cleanCurrentCell();
        
        // Determine direction based on row number (odd rows go left-to-right, even rows go right-to-left)
        boolean isOddRow = (currentRow % 2 == 1);
        
        if (isOddRow) {
            // Odd row: move left to right (1 -> GRID_SIZE)
            currentCol++;
            
            // If we've reached the end of the row, move to next row
            if (currentCol > GRID_SIZE) {
                currentRow++;
                currentCol = GRID_SIZE; // Start from right side for even row
            }
        } else {
            // Even row: move right to left (GRID_SIZE -> 1)
            currentCol--;
            
            // If we've reached the start of the row, move to next row
            if (currentCol < 1) {
                currentRow++;
                currentCol = 1; // Start from left side for odd row
            }
        }
        
        // Check if we've cleaned all cells
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("NettoyeurComplet finished cleaning entire grid in zigzag pattern!");
        }
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        currentRow = 1;
        currentCol = 1;
        setGridPosition(1, 1);
        
        System.out.println("NettoyeurComplet mission reset - will clean entire grid from (1,1) to (" + 
                         GRID_SIZE + "," + GRID_SIZE + ")");
    }
    
    /**
     * Clean the current cell
     */
    private void cleanCurrentCell() {
        if (gridManager != null) {
            gridManager.cleanCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
    /**
     * Get progress percentage
     */
    public double getProgress() {
        int totalCells = GRID_SIZE * GRID_SIZE;
        int cleanedCells = (currentRow - 1) * GRID_SIZE + (currentCol - 1);
        return (double) cleanedCells / totalCells * 100.0;
    }
    
    /**
     * Get number of cells remaining
     */
    public int getCellsRemaining() {
        int totalCells = GRID_SIZE * GRID_SIZE;
        int cleanedCells = (currentRow - 1) * GRID_SIZE + (currentCol - 1);
        return totalCells - cleanedCells;
    }
    
    @Override
    public String toString() {
        return "NettoyeurComplet at (" + currentRow + "," + currentCol + ") - " + 
               String.format("%.1f%%", getProgress()) + " complete";
    }
}
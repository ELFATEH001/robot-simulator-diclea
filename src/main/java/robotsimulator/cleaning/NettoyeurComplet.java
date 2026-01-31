package robotsimulator.cleaning;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

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
    private int consecutiveWallHits;
    
    /**
     * Constructor - always starts at (1,1)
     */
    public NettoyeurComplet(GridManager gridManager) {
        super(1, 1, GridConstants.CELL_SIZE / 3, gridManager);
        this.currentRow = 1;
        this.currentCol = 1;
        this.consecutiveWallHits = 0;
        setColor(Color.DODGERBLUE);
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // Move to current position with wall check
        boolean moved = setGridPositionWithWallCheck(currentRow, currentCol);
        if (!moved) {
            // Hit a wall, skip this cell
            consecutiveWallHits++;
            if (consecutiveWallHits > 5) {
                System.out.println("NettoyeurComplet: Too many consecutive wall hits, mission aborted!");
                missionComplete = true;
                return true;
            }
        } else {
            consecutiveWallHits = 0;
            // Clean current cell
            cleanCurrentCell();
        }
        
        // Determine direction and calculate next position
        boolean isOddRow = (currentRow % 2 == 1);
        int nextRow = currentRow;
        int nextCol = currentCol;
        
        if (isOddRow) {
            // Odd row: move left to right
            nextCol++;
            if (nextCol > GRID_SIZE) {
                nextRow++;
                nextCol = GRID_SIZE; // Start from right side for even row
            }
        } else {
            // Even row: move right to left
            nextCol--;
            if (nextCol < 1) {
                nextRow++;
                nextCol = 1; // Start from left side for odd row
            }
        }
        
        // Check if next position has a wall, try alternative
        if (hasWallAtOneBased(nextRow, nextCol)) {
            System.out.println("NettoyeurComplet: Wall at (" + nextRow + ", " + nextCol + "), trying alternative...");
            // Try to find alternative path
            int[] alternative = findAlternativePosition(currentRow, currentCol, nextRow, nextCol);
            nextRow = alternative[0];
            nextCol = alternative[1];
        }
        
        // Update position
        currentRow = nextRow;
        currentCol = nextCol;
        
        // Check if we've processed all cells
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("NettoyeurComplet finished cleaning accessible cells!");
        }
        
        return missionComplete;
    }

    private int[] findAlternativePosition(int currentRow, int currentCol, int targetRow, int targetCol) {
        // Try moving to adjacent non-wall cells        
        int[][] directions = {
            {0, 1},   // right
            {1, 0},   // down  
            {0, -1},  // left
            {-1, 0}   // up
        };
        
        for (int[] dir : directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];
            
            if (newRow >= 1 && newRow <= GRID_SIZE && 
                newCol >= 1 && newCol <= GRID_SIZE &&
                !hasWallAtOneBased(newRow, newCol)) {
                return new int[]{newRow, newCol};
            }
        }
        
        // If no alternative, continue original path
        return new int[]{targetRow, targetCol};
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
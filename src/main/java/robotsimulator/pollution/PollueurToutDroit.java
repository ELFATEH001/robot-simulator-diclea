package robotsimulator.pollution;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

/**
 * A polluter robot that goes straight down a column, polluting each cell
 */
public class PollueurToutDroit extends RobotPolluter {
    private int startCol;
    private int startRow;
    private int currentRow;
    private boolean wallHit;
    private int cellsPolluted;
    
    /**
     * Constructor with starting column
     */
    public PollueurToutDroit(int startCol, GridManager gridManager) {
        super(1, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
        this.startRow = 1;
        this.currentRow = 1; // Start at row 1
        this.wallHit = false;
        this.cellsPolluted = 0;
        setColor(Color.DARKRED);
    }
    
    /**
     * Constructor with random starting column
     */
    public PollueurToutDroit(GridManager gridManager) {
        this((int) (Math.random() * GRID_SIZE) + 1, gridManager);
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        // If mission already complete or wall was hit, return true
        if (missionComplete || wallHit) {
            return true;
        }
        
        // Check if we've already processed all rows
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("PollueurToutDroit: Completed polluting column " + startCol + 
                             ", polluted " + cellsPolluted + " cells");
            return true;
        }
        
        // Check if current position has wall
        if (hasWallAtOneBased(currentRow, startCol)) {
            System.out.println("PollueurToutDroit: Hit wall at (" + currentRow + ", " + startCol + ") !");
            wallHit = true;
            missionComplete = true; // Mission fails when hitting a wall
            return true;
        }
        
        // Move to current row
        setGridPositionWithWallCheck(currentRow, startCol);
        
        // Pollute current cell
        polluteCurrentCell();
        cellsPolluted++;
        
        System.out.println("PollueurToutDroit: Polluted (" + currentRow + ", " + startCol + ")");
        
        // Move to next row
        currentRow++;
        
        // Check if we've reached beyond the last row
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("PollueurToutDroit: Successfully finished column " + startCol + 
                             ", polluted " + cellsPolluted + "/" + GRID_SIZE + " cells");
        }
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        wallHit = false;
        cellsPolluted = 0;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        currentRow = startRow; // Start from current row
        
        System.out.println("PollueurToutDroit mission reset - will pollute down from (" + 
                         startRow + ", " + startCol + ")");
    }
    
    @Override
    public String toString() {
        if (wallHit) {
            return "PollueurToutDroit [col=" + startCol + ", FAILED - Hit wall at row " + currentRow + "]";
        } else if (missionComplete) {
            return "PollueurToutDroit [col=" + startCol + ", SUCCESS - polluted " + cellsPolluted + "/" + GRID_SIZE + " cells]";
        } else {
            return "PollueurToutDroit [col=" + startCol + ", row=" + currentRow + 
                   ", polluted=" + cellsPolluted + "/" + GRID_SIZE + "]";
        }
    }
    
    public int getStartCol() {
        return startCol;
    }
    
    public int getCellsPolluted() {
        return cellsPolluted;
    }
    
    public double getProgress() {
        return (double) cellsPolluted / GRID_SIZE * 100.0;
    }
    
    public boolean hitWall() {
        return wallHit;
    }
}
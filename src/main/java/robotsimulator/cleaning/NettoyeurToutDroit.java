package robotsimulator.cleaning;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

/**
 * A cleaner robot that goes straight down a column, cleaning each cell
 */
public class NettoyeurToutDroit extends RobotCleaner {
    private int startCol;
    private int startRow;
    private int currentRow; 
    private boolean wallHit;
    private int cellsCleaned;
    
    /**
     * Constructor with starting column
     */
    public NettoyeurToutDroit(int startCol, GridManager gridManager) {
        super(1, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
        this.startRow = 1;
        this.currentRow = 1; // Start at row 1
        this.wallHit = false;
        this.cellsCleaned = 0;
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
        // If mission already complete or wall was hit, return true
        if (missionComplete || wallHit) {
            return true;
        }
        
        // Check if we've already processed all rows
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("NettoyeurToutDroit: Completed cleaning column " + startCol + 
                             ", cleaned " + cellsCleaned + " cells");
            return true;
        }
        
        // Check if current position has wall
        if (hasWallAtOneBased(currentRow, startCol)) {
            System.out.println("NettoyeurToutDroit: Hit wall at (" + currentRow + ", " + startCol + ") - Mission ABORTED!");
            wallHit = true;
            missionComplete = true; // Mission fails when hitting a wall
            return true;
        }
        
        // Move to current row
        setGridPositionWithWallCheck(currentRow, startCol);
        
        // Clean current cell
        cleanCurrentCell();
        cellsCleaned++;
        
        System.out.println("NettoyeurToutDroit: Cleaned (" + currentRow + ", " + startCol + ")");
        
        // Move to next row
        currentRow++;
        
        // Check if we've reached beyond the last row
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            System.out.println("NettoyeurToutDroit: Successfully finished column " + startCol + 
                             ", cleaned " + cellsCleaned + "/" + GRID_SIZE + " cells");
        }
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        wallHit = false;
        cellsCleaned = 0;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        currentRow = startRow; // Start from current row
        
        System.out.println("NettoyeurToutDroit mission reset - will clean down from (" + 
                         startRow + ", " + startCol + ")");
    }
    
    @Override
    public String toString() {
        if (wallHit) {
            return "NettoyeurToutDroit [col=" + startCol + ", FAILED - Hit wall at row " + currentRow + "]";
        } else if (missionComplete) {
            return "NettoyeurToutDroit [col=" + startCol + ", SUCCESS - cleaned " + cellsCleaned + "/" + GRID_SIZE + " cells]";
        } else {
            return "NettoyeurToutDroit [col=" + startCol + ", row=" + currentRow + 
                   ", cleaned=" + cellsCleaned + "/" + GRID_SIZE + "]";
        }
    }
    
    public int getStartCol() {
        return startCol;
    }
    
    public int getCellsCleaned() {
        return cellsCleaned;
    }
    
    public double getProgress() {
        return (double) cellsCleaned / GRID_SIZE * 100.0;
    }
    
    public boolean hitWall() {
        return wallHit;
    }
}
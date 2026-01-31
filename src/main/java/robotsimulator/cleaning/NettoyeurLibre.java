package robotsimulator.cleaning;

import java.util.Random;

import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A cleaner robot that moves randomly around the grid, cleaning cells
 */
public class NettoyeurLibre extends RobotCleaner {
    private Random random;
    private int stepsTaken;
    private int maxSteps;
    
    /**
     * Constructor with starting position and maximum steps
     */
    public NettoyeurLibre(int startRow, int startCol, int maxSteps, GridManager gridManager) {
        super(startRow, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.random = new Random();
        this.stepsTaken = 0;
        this.maxSteps = maxSteps;
        setColor(Color.CYAN);
    }
    
    /**
     * Constructor with random starting position
     */
    public NettoyeurLibre(int maxSteps, GridManager gridManager) {
        this(
            (int) (Math.random() * GRID_SIZE) + 1,
            (int) (Math.random() * GRID_SIZE) + 1,
            maxSteps,
            gridManager
        );
    }
    
    /**
     * Constructor with default max steps (GRID_SIZE * GRID_SIZE)
     */
    public NettoyeurLibre(GridManager gridManager) {
        this(GRID_SIZE * GRID_SIZE, gridManager);
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // Clean current cell
        cleanCurrentCell();
        stepsTaken++;
        
        // Check if mission is complete
        if (stepsTaken >= maxSteps) {
            missionComplete = true;
            return true;
        }
        
        // Move randomly to adjacent cell
        int currentRow = getGridRowOneBased();
        int currentCol = getGridColOneBased();
        
        // Choose random direction (0=up, 1=right, 2=down, 3=left)
        int direction = random.nextInt(4);
        int newRow = currentRow;
        int newCol = currentCol;
        
        switch (direction) {
            case 0 -> // Up
                newRow = Math.max(1, currentRow - 1);
            case 1 -> // Right
                newCol = Math.min(GRID_SIZE, currentCol + 1);
            case 2 -> // Down
                newRow = Math.min(GRID_SIZE, currentRow + 1);
            case 3 -> // Left
                newCol = Math.max(1, currentCol - 1);
        }
        
        // Move to new position
        setGridPosition(newRow, newCol);
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        stepsTaken = 0;
        
        System.out.println("NettoyeurLibre mission reset - will clean randomly for " + maxSteps + " steps from (" + 
                         getGridRowOneBased() + ", " + getGridColOneBased() + ")");
    }
    
    /**
     * Clean the current cell
     */
    private void cleanCurrentCell() {
        if (gridManager != null) {
            gridManager.cleanCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
    public int getStepsTaken() {
        return stepsTaken;
    }
    
    public int getMaxSteps() {
        return maxSteps;
    }
}
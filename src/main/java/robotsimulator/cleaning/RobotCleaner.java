package robotsimulator.cleaning;

import javafx.scene.paint.Color;
import robotsimulator.model.Robot;
import robotsimulator.ui.GridManager;

/**
 * Abstract base class for cleaner robots
 */
public abstract class RobotCleaner extends Robot {
    protected boolean missionComplete;
    protected int wallHitCount;

    public RobotCleaner(int gridRow, int gridCol, double radius, GridManager gridManager) {
        super(gridRow, gridCol, radius);
        // Remove this line: this.gridManager = gridManager;
        // Instead, use the parent class's method
        setGridManager(gridManager);
        this.missionComplete = false;
        this.wallHitCount = 0;
        setColor(Color.BLUE); // Cleaners are blue
    }
    /**
     * Check if too many walls were hit
     */
    protected boolean isMissionBlocked() {
        return wallHitCount > 10; // Adjust threshold as needed
    }

    /**
     * Record a wall hit
     */
    protected void recordWallHit() {
        wallHitCount++;
        System.out.println(getClass().getSimpleName() + ": Wall hit #" + wallHitCount);
    }

    protected void resetWallHits() {
        wallHitCount = 0;
    }

    /**
     * Clean the current cell
     */
    protected void cleanCurrentCell() {
        if (gridManager != null) {
            gridManager.cleanCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
    /**
     * Abstract method to execute one step of the mission
     */
    public abstract boolean executeMissionStep(int stepCount);
    
    /**
     * Reset the mission so it can be executed again
     */
    public abstract void resetMission();
    
    /**
     * Check if mission is complete
     */
    public boolean isMissionComplete() {
        return missionComplete;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getGridRowOneBased() + ", " + getGridColOneBased() + ")";
    }
}
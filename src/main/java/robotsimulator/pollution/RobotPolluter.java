package robotsimulator.pollution;

import javafx.scene.paint.Color;
import robotsimulator.model.Robot;
import robotsimulator.ui.GridManager;

/**
 * Abstract base class for polluter robots
 */
public abstract class RobotPolluter extends Robot {
    protected boolean missionComplete;
    
    public RobotPolluter(int gridRow, int gridCol, double radius, GridManager gridManager) {
        super(gridRow, gridCol, radius);
        // Remove this line: this.gridManager = gridManager;
        // Instead, use the parent class's method
        setGridManager(gridManager);
        this.missionComplete = false;
        setColor(Color.RED); // Polluters are red
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
     * Pollute the current cell
     */
    protected void polluteCurrentCell() {
        if (gridManager != null) {
            gridManager.dirtyCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
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
package com.example.cleaning;

import com.example.model.Robot;
import com.example.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * Abstract base class for cleaner robots
 */
public abstract class RobotCleaner extends Robot {
    protected GridManager gridManager;
    protected boolean missionComplete;
    
    public RobotCleaner(int gridRow, int gridCol, double radius, GridManager gridManager) {
        super(gridRow, gridCol, radius);
        this.gridManager = gridManager;
        this.missionComplete = false;
        setColor(Color.BLUE); // Cleaners are blue
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
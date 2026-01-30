package com.example.pollution;

import com.example.model.GridConstants;
import static com.example.model.GridConstants.GRID_SIZE;
import com.example.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A polluter robot that goes straight down a column, polluting each cell
 */
public class PollueurToutDroit extends RobotPolluter {
    private int startCol;
    private int startRow;
    private int currentRow; // Track which row we've polluted
    
    /**
     * Constructor with starting column
     */
    public PollueurToutDroit(int startCol, GridManager gridManager) {
        super(1, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
        this.startRow = 1;
        this.currentRow = 0; // Haven't started yet
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
        if (missionComplete) {
            return true;
        }
        
        
        // Increment to next row
        currentRow++;
        
        if (currentRow > GRID_SIZE) {
            missionComplete = true;
            return true;
        }
        
        // Move to current row and pollute
        setGridPosition(currentRow, startCol);
        polluteCurrentCell();
        
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
        
        System.out.println("PollueurToutDroit mission reset - will go down from (" + startRow + ", " + startCol + ") to (" + GRID_SIZE + ", " + startCol + ")");
    }
    
    public int getStartCol() {
        return startCol;
    }
}
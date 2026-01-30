package com.example.pollution;

import com.example.model.GridConstants;
import static com.example.model.GridConstants.GRID_SIZE;
import com.example.ui.GridManager;

import javafx.scene.paint.Color;


/**
 * A polluter robot that jumps (skips cells) while polluting
 */
public class PollueurSauteurs extends RobotPolluter {
    private int jumpSize;
    private int startRow;
    private int startCol;
    private int jumpCount; // Track how many jumps we've made
    private int currentRow;
    private int currentCol;
    
    /**
     * Constructor with jump size
     */
    public PollueurSauteurs(int gridRow, int gridCol, int jumpSize, GridManager gridManager) {
        super(gridRow, gridCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.jumpSize = Math.max(1, jumpSize);
        this.startRow = gridRow;
        this.startCol = gridCol;
        this.jumpCount = 0;
        this.currentRow = gridRow;
        this.currentCol = gridCol;
        setColor(Color.ORANGERED);
    }
    
    /**
     * Constructor with random position and jump size
     */
    public PollueurSauteurs(GridManager gridManager) {
        this((int) (Math.random() * GRID_SIZE) + 1,
             (int) (Math.random() * GRID_SIZE) + 1,
             (int) (Math.random() * 3) + 2, // Jump 2-4 cells
             gridManager);
    }
    
    
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) return true;
        
        // Move to current position and pollute
        setGridPosition(currentRow, currentCol);
        polluteCurrentCell();
        
        jumpCount++;
        
        if (jumpCount >= 10) {
            missionComplete = true;
            return true;
        }
        
        // Calculate next position for next step
        currentRow += jumpSize;
        currentCol += jumpSize;
        
        // Wrap around if out of bounds
        if (currentRow > GRID_SIZE) currentRow = ((currentRow - 1) % GRID_SIZE) + 1;
        if (currentCol > GRID_SIZE) currentCol = ((currentCol - 1) % GRID_SIZE) + 1;
        
        return false;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        jumpCount = 0;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        currentRow = startRow;
        currentCol = startCol;
        
        System.out.println("PollueurSauteurs mission reset at (" + startRow + ", " + startCol + ") with jump size " + jumpSize);
    }
    
    public int getJumpSize() {
        return jumpSize;
    }
}
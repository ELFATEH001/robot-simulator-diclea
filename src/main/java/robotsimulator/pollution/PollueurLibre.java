package robotsimulator.pollution;

import java.util.Random;

import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A polluter robot that moves freely in random directions
 */
public class PollueurLibre extends RobotPolluter {
    private static final Random SHARED_RANDOM = new Random();
    private Random random;
    private int pollutionCount;
    private int maxPollutions;
    private int startRow;
    private int startCol;
    
    /**
     * Constructor with max pollutions
     */
    public PollueurLibre(int gridRow, int gridCol, int maxPollutions, GridManager gridManager) {
        super(gridRow, gridCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.random = new Random(System.nanoTime() + gridRow * 1000L + gridCol);
        this.pollutionCount = 0;
        this.maxPollutions = Math.max(1, maxPollutions);
        this.startRow = gridRow;
        this.startCol = gridCol;
        setColor(Color.CRIMSON);
    }
    
    /**
     * Constructor with random position
     */
    public PollueurLibre(GridManager gridManager) {
        this((int) (SHARED_RANDOM.nextDouble() * GRID_SIZE) + 1,
             (int) (SHARED_RANDOM.nextDouble() * GRID_SIZE) + 1,
             20, // Default: pollute 20 cells
             gridManager);
    }
    
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) return true;
        
        // Pollute current cell
        polluteCurrentCell();
        pollutionCount++;
        
        if (pollutionCount >= maxPollutions) {
            missionComplete = true;
            return true;
        }
        
        // Move randomly for next step
        moveRandomly();
        
        return false;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        pollutionCount = 0;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        
        // Reset random with new seed for different behavior
        this.random = new Random(System.nanoTime() + startRow * 1000L + startCol);
        System.out.println("PollueurLibre mission reset at (" + startRow + ", " + startCol + ")");
    }
    
    /**
     * Move to a random adjacent cell
     */
    private void moveRandomly() {
        int direction = random.nextInt(4); // 0: up, 1: down, 2: left, 3: right
        int currentRow = getGridRowOneBased();
        int currentCol = getGridColOneBased();
        
        switch (direction) {
            case 0 -> {
                // Up
                if (currentRow > 1) {
                    setGridPosition(currentRow - 1, currentCol);
                }
            }
            case 1 -> {
                // Down
                if (currentRow < GRID_SIZE) {
                    setGridPosition(currentRow + 1, currentCol);
                }
            }
            case 2 -> {
                // Left
                if (currentCol > 1) {
                    setGridPosition(currentRow, currentCol - 1);
                }
            }
            case 3 -> {
                // Right
                if (currentCol < GRID_SIZE) {
                    setGridPosition(currentRow, currentCol + 1);
                }
            }
        }
    }
    
    public int getPollutionCount() {
        return pollutionCount;
    }
    
    public int getMaxPollutions() {
        return maxPollutions;
    }
}
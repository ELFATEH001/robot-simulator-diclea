package robotsimulator.pollution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

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
    private boolean wallHit;
    private int consecutiveWallHits;
    
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
        this.wallHit = false;
        this.consecutiveWallHits = 0;
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
        // If mission already complete or wall was hit, return true
        if (missionComplete || wallHit) {
            return true;
        }
        
        // Check if starting position has wall
        if (hasWallAtOneBased(getGridRowOneBased(), getGridColOneBased())) {
            System.out.println("PollueurLibre: Started on wall cell at (" + 
                             getGridRowOneBased() + ", " + getGridColOneBased() + 
                             ") - Mission ABORTED!");
            wallHit = true;
            missionComplete = true;
            return true;
        }
        
        // Pollute current cell
        polluteCurrentCell();
        pollutionCount++;
        System.out.println("PollueurLibre: Polluted (" + getGridRowOneBased() + ", " + 
                         getGridColOneBased() + ") - " + pollutionCount + "/" + maxPollutions);
        
        // Check if mission is complete
        if (pollutionCount >= maxPollutions) {
            missionComplete = true;
            System.out.println("PollueurLibre: Successfully completed " + maxPollutions + " pollutions!");
            return true;
        }
        
        // Move randomly for next step (avoiding walls)
        if (!moveRandomlyAvoidingWalls()) {
            // Failed to find a valid move
            System.out.println("PollueurLibre: Cannot find valid move - Mission ABORTED!");
            wallHit = true;
            missionComplete = true;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        wallHit = false;
        pollutionCount = 0;
        consecutiveWallHits = 0;
        
        // Update starting position based on current position
        startRow = getGridRowOneBased();
        startCol = getGridColOneBased();
        
        // Reset random with new seed for different behavior
        this.random = new Random(System.nanoTime() + startRow * 1000L + startCol);
        System.out.println("PollueurLibre mission reset at (" + startRow + ", " + startCol + ")");
    }
    
    /**
     * Move to a random adjacent non-wall cell
     * Returns true if moved successfully, false if surrounded by walls
     */
    private boolean moveRandomlyAvoidingWalls() {
        int currentRow = getGridRowOneBased();
        int currentCol = getGridColOneBased();
        
        // Get all valid adjacent positions (not walls and within bounds)
        List<int[]> validMoves = new ArrayList<>();
        int[][] directions = {
            {-1, 0},  // up
            {1, 0},   // down
            {0, -1},  // left
            {0, 1}    // right
        };
        
        for (int[] dir : directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];
            
            // Check bounds
            if (newRow >= 1 && newRow <= GRID_SIZE && 
                newCol >= 1 && newCol <= GRID_SIZE) {
                
                // Check if not a wall
                if (!hasWallAtOneBased(newRow, newCol)) {
                    validMoves.add(new int[]{newRow, newCol});
                }
            }
        }
        
        // If no valid moves, we're trapped
        if (validMoves.isEmpty()) {
            System.out.println("PollueurLibre: Trapped at (" + currentRow + ", " + currentCol + ")");
            consecutiveWallHits++;
            return false;
        }
        
        // Choose random valid move
        int[] chosenMove = validMoves.get(random.nextInt(validMoves.size()));
        int targetRow = chosenMove[0];
        int targetCol = chosenMove[1];
        
        // Double-check this position doesn't have a wall (shouldn't happen but just in case)
        if (hasWallAtOneBased(targetRow, targetCol)) {
            System.out.println("PollueurLibre: ERROR - Chosen move has wall at (" + 
                             targetRow + ", " + targetCol + ")");
            consecutiveWallHits++;
            
            if (consecutiveWallHits >= 3) {
                System.out.println("PollueurLibre: Too many consecutive wall detection errors");
                return false;
            }
            
            // Try another move recursively (with safety limit)
            return moveRandomlyAvoidingWalls();
        }
        
        // Move to the chosen position
        boolean moved = setGridPositionWithWallCheck(targetRow, targetCol);
        if (!moved) {
            System.out.println("PollueurLibre: Failed to move to (" + targetRow + ", " + targetCol + ")");
            consecutiveWallHits++;
            
            if (consecutiveWallHits >= 3) {
                return false;
            }
            
            // Try another move
            return moveRandomlyAvoidingWalls();
        }
        
        // Reset consecutive wall hits counter on successful move
        consecutiveWallHits = 0;
        System.out.println("PollueurLibre: Moved to (" + targetRow + ", " + targetCol + ")");
        
        return true;
    }
    
    @Override
    public String toString() {
        if (wallHit) {
            return "PollueurLibre [FAILED - Hit wall or trapped, polluted " + 
                   pollutionCount + "/" + maxPollutions + " cells]";
        } else if (missionComplete) {
            return "PollueurLibre [SUCCESS - polluted " + pollutionCount + "/" + 
                   maxPollutions + " cells]";
        } else {
            return "PollueurLibre [polluted=" + pollutionCount + "/" + maxPollutions + 
                   ", at (" + getGridRowOneBased() + ", " + getGridColOneBased() + ")]";
        }
    }
    
    public int getPollutionCount() {
        return pollutionCount;
    }
    
    public int getMaxPollutions() {
        return maxPollutions;
    }
    
    public boolean hitWall() {
        return wallHit;
    }
    
    public double getProgress() {
        return (double) pollutionCount / maxPollutions * 100.0;
    }
}
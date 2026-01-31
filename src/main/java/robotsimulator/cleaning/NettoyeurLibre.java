package robotsimulator.cleaning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

/**
 * A cleaner robot that moves randomly around the grid, cleaning cells
 */
public class NettoyeurLibre extends RobotCleaner {
    private Random random;
    private int stepsTaken;
    private int maxSteps;
    // private int consecutiveWallHits;
    
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
        
        // Try to move to a random non-wall adjacent cell
        int currentRow = getGridRowOneBased();
        int currentCol = getGridColOneBased();
        
        // Generate a list of possible moves
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] directions = {
            {-1, 0},  // up
            {1, 0},   // down
            {0, -1},  // left
            {0, 1}    // right
        };
        
        for (int[] dir : directions) {
            int newRow = currentRow + dir[0];
            int newCol = currentCol + dir[1];
            
            if (newRow >= 1 && newRow <= GRID_SIZE && 
                newCol >= 1 && newCol <= GRID_SIZE &&
                !hasWallAtOneBased(newRow, newCol)) {
                possibleMoves.add(new int[]{newRow, newCol});
            }
        }
        
        // If no moves possible (surrounded by walls), mission fails
        if (possibleMoves.isEmpty()) {
            System.out.println("NettoyeurLibre: Surrounded by walls, mission aborted!");
            missionComplete = true;
            return true;
        }
        
        // Choose random valid move
        int[] move = possibleMoves.get(random.nextInt(possibleMoves.size()));
        setGridPositionWithWallCheck(move[0], move[1]);
        
        return missionComplete;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        stepsTaken = 0;
        
        System.out.println("NettoyeurLibre mission reset - will clean randomly for " + maxSteps + " steps from (" + 
                         getGridRowOneBased() + ", " + getGridColOneBased() + ")");
    }
    

    
    public int getStepsTaken() {
        return stepsTaken;
    }
    
    public int getMaxSteps() {
        return maxSteps;
    }
}
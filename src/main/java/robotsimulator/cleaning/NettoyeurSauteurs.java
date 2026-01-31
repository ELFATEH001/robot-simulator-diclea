package robotsimulator.cleaning;

import robotsimulator.model.GridConstants;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.ui.GridManager;

import javafx.scene.paint.Color;

/**
 * A cleaner robot that jumps around the grid in a pattern, cleaning cells
 * It moves in a knight's move pattern (like chess) or diagonal jumps
 */
public class NettoyeurSauteurs extends RobotCleaner {
    private int jumpDistance;
    private int startRow;
    private int startCol;
    private int currentIndex;
    private int[][] visitPattern;
    
    /**
     * Constructor with starting position and jump distance
     */
    public NettoyeurSauteurs(int startRow, int startCol, int jumpDistance, GridManager gridManager) {
        super(startRow, startCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.jumpDistance = Math.max(1, Math.min(jumpDistance, GRID_SIZE / 2));
        this.startRow = startRow;
        this.startCol = startCol;
        this.currentIndex = 0;
        setColor(Color.DEEPSKYBLUE);
        generateVisitPattern();
    }
    
    /**
     * Constructor with default jump distance of 2
     */
    public NettoyeurSauteurs(int startRow, int startCol, GridManager gridManager) {
        this(startRow, startCol, 2, gridManager);
    }
    
    /**
     * Constructor with random starting position
     */
    public NettoyeurSauteurs(GridManager gridManager) {
        this(
            (int) (Math.random() * GRID_SIZE) + 1,
            (int) (Math.random() * GRID_SIZE) + 1,
            2,
            gridManager
        );
    }
    
    /**
     * Generate a pattern of positions to visit by jumping
     */
    private void generateVisitPattern() {
        // Create a pattern that covers the grid with diagonal jumps
        int estimatedSteps = (GRID_SIZE * GRID_SIZE) / (jumpDistance * jumpDistance) + GRID_SIZE;
        visitPattern = new int[estimatedSteps][2];
        int index = 0;
        
        // Start from initial position
        int row;
        int col;
        
        // Pattern: diagonal jumps, wrapping around when hitting edges
        for (int i = 0; i < GRID_SIZE && index < estimatedSteps; i++) {
            for (int j = 0; j < GRID_SIZE && index < estimatedSteps; j += jumpDistance) {
                row = ((i * jumpDistance) % GRID_SIZE) + 1;
                col = ((j + startCol - 1) % GRID_SIZE) + 1;
                
                visitPattern[index][0] = row;
                visitPattern[index][1] = col;
                index++;
            }
        }
        
        // Trim the array to actual size
        int[][] trimmed = new int[index][2];
        System.arraycopy(visitPattern, 0, trimmed, 0, index);
        visitPattern = trimmed;
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // Check if we've visited all positions in the pattern
        if (currentIndex >= visitPattern.length) {
            missionComplete = true;
            return true;
        }
        
        // Move to next position in pattern
        int targetRow = visitPattern[currentIndex][0];
        int targetCol = visitPattern[currentIndex][1];
        setGridPosition(targetRow, targetCol);
        
        // Clean current cell
        cleanCurrentCell();
        
        // Move to next position
        currentIndex++;
        
        // Check if mission is complete
        if (currentIndex >= visitPattern.length) {
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
        currentIndex = 0;
        
        // Regenerate pattern from new starting position
        generateVisitPattern();
        
        System.out.println("NettoyeurSauteurs mission reset - will clean by jumping (distance=" + 
                         jumpDistance + ") from (" + startRow + ", " + startCol + ")");
    }
    
    /**
     * Clean the current cell
     */
    private void cleanCurrentCell() {
        if (gridManager != null) {
            gridManager.cleanCell(getGridRowOneBased(), getGridColOneBased());
        }
    }
    
    public int getJumpDistance() {
        return jumpDistance;
    }
    
    public int getStepsRemaining() {
        return visitPattern.length - currentIndex;
    }
}
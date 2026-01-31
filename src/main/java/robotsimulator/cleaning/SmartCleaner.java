package robotsimulator.cleaning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javafx.scene.paint.Color;
import robotsimulator.model.GridConstants;
import robotsimulator.ui.GridManager;

/**
 * Smart cleaner robot that uses pathfinding (A* algorithm) to find dirty cells
 */
public class SmartCleaner extends RobotCleaner {
    private final  List<Cell> dirtyCells;
    private int currentTargetIndex;
    private List<Cell> currentPath;
    private int pathStep;
    private final int maxCleaningSteps;
    private int stepsTaken;
    
    public SmartCleaner(int gridRow, int gridCol, int maxCleaningSteps, GridManager gridManager) {
        super(gridRow, gridCol, GridConstants.CELL_SIZE / 3, gridManager);
        this.dirtyCells = new ArrayList<>();
        this.currentTargetIndex = 0;
        this.currentPath = new ArrayList<>();
        this.pathStep = 0;
        this.maxCleaningSteps = maxCleaningSteps;
        this.stepsTaken = 0;
        setColor(Color.CYAN); // Smart cleaners are cyan
    }
    
    @Override
    public boolean executeMissionStep(int stepCount) {
        if (missionComplete) {
            return true;
        }
        
        // First, scan for dirty cells
        updateDirtyCells();
        
        // If no dirty cells left, mission is complete
        if (dirtyCells.isEmpty()) {
            missionComplete = true;
            return true;
        }
        
        // If we've exceeded max steps, mission is complete
        if (stepsTaken >= maxCleaningSteps) {
            missionComplete = true;
            return true;
        }
        
        // If we don't have a current target or reached current target, find new target
        if (currentPath.isEmpty() || pathStep >= currentPath.size()) {
            if (!findNextTarget()) {
                missionComplete = true;
                return true;
            }
        }
        
        // Move along the path
        if (!currentPath.isEmpty() && pathStep < currentPath.size()) {
            Cell nextCell = currentPath.get(pathStep);
            setGridPosition(nextCell.row + 1, nextCell.col + 1); // Convert to 1-based
            pathStep++;
            stepsTaken++;
            
            // Clean the cell if we reached it
            if (isAtCell(nextCell)) {
                gridManager.cleanCell(nextCell.row + 1, nextCell.col + 1);
                dirtyCells.remove(nextCell);
                
                // If we reached a target, find new target
                if (currentTargetIndex < dirtyCells.size() && 
                    isAtCell(dirtyCells.get(currentTargetIndex))) {
                    // Find new target
                    findNextTarget();
                }
            }
        }
        
        return false;
    }
    
    /**
     * Update the list of dirty cells from the grid
     */
    private void updateDirtyCells() {
        dirtyCells.clear();
        
        // Scan the entire grid for dirty cells (1-based indexing)
        for (int row = 1; row <= GridConstants.GRID_SIZE; row++) {
            for (int col = 1; col <= GridConstants.GRID_SIZE; col++) {
                if (gridManager.isCellDirty(row, col)) {
                    // Convert to 0-based for internal storage
                    dirtyCells.add(new Cell(row - 1, col - 1));
                }
            }
        }
    }
    
    /**
     * Find the next target cell to clean
     * Returns true if a target was found, false otherwise
     */
    private boolean findNextTarget() {
        if (dirtyCells.isEmpty()) {
            return false;
        }
        
        // Find the nearest dirty cell
        Cell currentPos = new Cell(getGridRow(), getGridCol());
        Cell nearestCell = null;
        double minDistance = Double.MAX_VALUE;
        int nearestIndex = -1;
        
        for (int i = 0; i < dirtyCells.size(); i++) {
            Cell dirtyCell = dirtyCells.get(i);
            double distance = calculateManhattanDistance(currentPos, dirtyCell);
            
            if (distance < minDistance) {
                minDistance = distance;
                nearestCell = dirtyCell;
                nearestIndex = i;
            }
        }
        
        if (nearestCell != null) {
            currentTargetIndex = nearestIndex;
            // Find path to the nearest dirty cell
            currentPath = findPath(currentPos, nearestCell);
            pathStep = 0;
            return true;
        }
        
        return false;
    }
    
    /**
     * Find path from start to target using A* algorithm
     */
    private List<Cell> findPath(Cell start, Cell target) {
        List<Cell> path = new ArrayList<>();
        
        // If start is target, return empty path
        if (start.equals(target)) {
            return path;
        }
        
        // Create open and closed sets
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        boolean[][] closedSet = new boolean[GridConstants.GRID_SIZE][GridConstants.GRID_SIZE];
        
        // Create start node
        Node startNode = new Node(start.row, start.col);
        startNode.gCost = 0;
        startNode.hCost = calculateManhattanDistance(start, target);
        startNode.fCost = startNode.hCost;
        openSet.add(startNode);
        
        Node[][] nodeGrid = new Node[GridConstants.GRID_SIZE][GridConstants.GRID_SIZE];
        nodeGrid[start.row][start.col] = startNode;
        
        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            
            // If we reached the target, reconstruct path
            if (currentNode.row == target.row && currentNode.col == target.col) {
                return reconstructPath(currentNode);
            }
            
            closedSet[currentNode.row][currentNode.col] = true;
            
            // Check all 4 neighbors
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right
            
            for (int[] dir : directions) {
                int newRow = currentNode.row + dir[0];
                int newCol = currentNode.col + dir[1];
                
                // Check if neighbor is within grid bounds
                if (newRow >= 0 && newRow < GridConstants.GRID_SIZE && 
                    newCol >= 0 && newCol < GridConstants.GRID_SIZE) {
                    
                    if (gridManager.isWallZeroBased(newRow, newCol)) {
                        continue;
                    }

                    // Skip if already processed
                    if (closedSet[newRow][newCol]) {
                        continue;
                    }
                    
                    // Calculate new movement cost
                    double newGCost = currentNode.gCost + 1;
                    
                    Node neighbor = nodeGrid[newRow][newCol];
                    if (neighbor == null) {
                        neighbor = new Node(newRow, newCol);
                        nodeGrid[newRow][newCol] = neighbor;
                    }
                    
                    // Check if this path is better
                    if (newGCost < neighbor.gCost) {
                        neighbor.parent = currentNode;
                        neighbor.gCost = newGCost;
                        neighbor.hCost = calculateManhattanDistance(
                            new Cell(newRow, newCol), target);
                        neighbor.fCost = neighbor.gCost + neighbor.hCost;
                        
                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }
        
        // No path found, return direct movement (fallback)
        return getDirectMovementPath(start, target);
    }
    
    /**
     * Reconstruct path from end node to start node
     */
    private List<Cell> reconstructPath(Node endNode) {
        List<Cell> path = new ArrayList<>();
        Node current = endNode;
        
        while (current.parent != null) {
            path.add(0, new Cell(current.row, current.col));
            current = current.parent;
        }
        
        return path;
    }
    
    /**
     * Fallback: Get direct movement path (simple, no obstacles considered)
     */
    private List<Cell> getDirectMovementPath(Cell start, Cell target) {
        List<Cell> path = new ArrayList<>();
        int currentRow = start.row;
        int currentCol = start.col;
        
        // Move vertically first, then horizontally
        while (currentRow != target.row) {
            if (currentRow < target.row) {
                currentRow++;
            } else {
                currentRow--;
            }
            path.add(new Cell(currentRow, currentCol));
        }
        
        while (currentCol != target.col) {
            if (currentCol < target.col) {
                currentCol++;
            } else {
                currentCol--;
            }
            path.add(new Cell(currentRow, currentCol));
        }
        
        return path;
    }
    
    /**
     * Calculate Manhattan distance between two cells
     */
    private double calculateManhattanDistance(Cell a, Cell b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }
    
    /**
     * Check if robot is at a specific cell
     */
    private boolean isAtCell(Cell cell) {
        return getGridRow() == cell.row && getGridCol() == cell.col;
    }
    
    @Override
    public void resetMission() {
        missionComplete = false;
        dirtyCells.clear();
        currentPath.clear();
        currentTargetIndex = 0;
        pathStep = 0;
        stepsTaken = 0;
    }
    
    @Override
    public String toString() {
        return "SmartCleaner (" + getGridRowOneBased() + ", " + getGridColOneBased() + 
               ") Steps: " + stepsTaken + "/" + maxCleaningSteps;
    }
    
    /**
     * Helper class to represent a cell
     */
    private static class Cell {
        int row;
        int col;
        
        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Cell cell = (Cell) obj;
            return row == cell.row && col == cell.col;
        }
        
        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }
    
    /**
     * Helper class for A* pathfinding nodes
     */
    private static class Node {
        int row;
        int col;
        double gCost; // Distance from start
        double hCost; // Heuristic distance to target
        double fCost; // gCost + hCost
        Node parent;
        
        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.gCost = Double.MAX_VALUE;
            this.hCost = 0;
            this.fCost = Double.MAX_VALUE;
            this.parent = null;
        }
    }
}
package com.example;

import java.util.ArrayList;
import java.util.List;

import static com.example.GridConstants.CELL_SIZE;
import static com.example.GridConstants.GRID_SIZE;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * Manages all robots in the simulation with discrete cell-to-cell movement.
 */
public class RobotManager {
    private final List<Robot> robots;
    private final Pane robotLayer;
    private final GridManager gridManager;
    private AnimationTimer gameLoop;
    private boolean isRunning;
    
    // Movement control
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY_NANOS = 200_000_000; // 200ms between moves
    
    public RobotManager(Pane robotLayer, GridManager gridManager) {
        this.robots = new ArrayList<>();
        this.robotLayer = robotLayer;
        this.gridManager = gridManager;
        this.isRunning = false;
    }
    
    /**
     * Create a robot at the specified grid position (1-based indexing)
     */
    public Robot createRobot(int gridRow, int gridCol) {
        if (gridRow < 1 || gridRow > GRID_SIZE || gridCol < 1 || gridCol > GRID_SIZE) {
            System.out.println("Invalid robot position!");
            return null;
        }
        
        Robot robot = new Robot(gridRow, gridCol, CELL_SIZE / 3);
        robots.add(robot);
        
        // Add visual representation to the pane
        Circle circle = new Circle(robot.getRadius());
        circle.setFill(robot.getColor());
        circle.setStroke(Color.DARKBLUE);
        circle.setStrokeWidth(2);
        
        // Bind circle position to robot position
        circle.centerXProperty().bind(robot.xProperty());
        circle.centerYProperty().bind(robot.yProperty());
        
        robotLayer.getChildren().add(circle);
        robot.setVisualNode(circle);
        
        return robot;
    }
    
    /**
     * Remove a specific robot
     */
    public void removeRobot(Robot robot) {
        robots.remove(robot);
        if (robot.getVisualNode() != null) {
            robotLayer.getChildren().remove(robot.getVisualNode());
        }
    }
    
    /**
     * Remove all robots
     */
    public void clearAllRobots() {
        robotLayer.getChildren().clear();
        robots.clear();
    }
    
    /**
     * Start the animation loop for robot movement and cleaning
     */
    public void startSimulation() {
        if (isRunning) return;
        
        isRunning = true;
        lastMoveTime = System.nanoTime();
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Only move at fixed intervals
                if (now - lastMoveTime >= MOVE_DELAY_NANOS) {
                    updateRobots();
                    lastMoveTime = now;
                }
            }
        };
        gameLoop.start();
    }
    
    /**
     * Stop the animation loop
     */
    public void stopSimulation() {
        if (gameLoop != null) {
            gameLoop.stop();
            isRunning = false;
        }
    }
    
    /**
     * Check if simulation is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Update all robots - they move and clean cells
     */
    private void updateRobots() {
        for (Robot robot : robots) {
            // Clean the cell the robot is currently on
            cleanCurrentCell(robot);
        }
    }
    
    /**
     * Clean the cell where the robot currently is
     */
    private void cleanCurrentCell(Robot robot) {
        gridManager.cleanCell(robot.getGridRowOneBased(), robot.getGridColOneBased());
    }
    
    /**
     * Get current robot count
     */
    public int getRobotCount() {
        return robots.size();
    }
    
    /**
     * Get all robots (for external manipulation if needed)
     */
    public List<Robot> getRobots() {
        return new ArrayList<>(robots);
    }
    
    /**
     * Get a specific robot by index
     */
    public Robot getRobot(int index) {
        if (index >= 0 && index < robots.size()) {
            return robots.get(index);
        }
        return null;
    }
}
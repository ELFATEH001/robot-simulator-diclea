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
    private final List<RobotPolluter> polluters;
    private final Pane robotLayer;
    private final GridManager gridManager;
    private AnimationTimer gameLoop;
    private AnimationTimer polluterMissionTimer;
    private boolean isRunning;
    private boolean missionsRunning;
    
    // Movement control
    private long lastMoveTime = 0;
    private long lastMissionTime = 0;
    private static final long MOVE_DELAY_NANOS = 200_000_000; // 200ms between moves
    private static final long MISSION_STEP_DELAY_NANOS = 500_000_000; // 500ms between mission steps
    
    public RobotManager(Pane robotLayer, GridManager gridManager) {
        this.robots = new ArrayList<>();
        this.polluters = new ArrayList<>();
        this.robotLayer = robotLayer;
        this.gridManager = gridManager;
        this.isRunning = false;
        this.missionsRunning = false;
    }
    
    /**
     * Create a standard robot at the specified grid position (1-based indexing)
     */
    public Robot createRobot(int gridRow, int gridCol) {
        if (gridRow < 1 || gridRow > GRID_SIZE || gridCol < 1 || gridCol > GRID_SIZE) {
            System.out.println("Invalid robot position!");
            return null;
        }
        
        Robot robot = new Robot(gridRow, gridCol, CELL_SIZE / 3);
        robots.add(robot);
        addVisualRepresentation(robot);
        
        return robot;
    }
    
    /**
     * Create a straight line polluter
     */
    public PollueurToutDroit createStraightPolluter(int startCol) {
        PollueurToutDroit polluter = new PollueurToutDroit(startCol, gridManager);
        robots.add(polluter);
        polluters.add(polluter);
        addVisualRepresentation(polluter);
        
        return polluter;
    }
    
    /**
     * Create a jumping polluter
     */
    public PollueurSauteurs createJumpingPolluter(int row, int col, int jumpSize) {
        PollueurSauteurs polluter = new PollueurSauteurs(row, col, jumpSize, gridManager);
        robots.add(polluter);
        polluters.add(polluter);
        addVisualRepresentation(polluter);
        
        return polluter;
    }
    
    /**
     * Create a free movement polluter
     */
    public PollueurLibre createFreePolluter(int row, int col, int maxPollutions) {
        PollueurLibre polluter = new PollueurLibre(row, col, maxPollutions, gridManager);
        robots.add(polluter);
        polluters.add(polluter);
        addVisualRepresentation(polluter);
        
        return polluter;
    }
    
    /**
     * Add visual representation for a robot
     */
    private void addVisualRepresentation(Robot robot) {
        Circle circle = new Circle(robot.getRadius());
        circle.setFill(robot.getColor());
        circle.setStroke(Color.DARKBLUE);
        circle.setStrokeWidth(2);
        
        // Bind circle position to robot position
        circle.centerXProperty().bind(robot.xProperty());
        circle.centerYProperty().bind(robot.yProperty());
        
        robotLayer.getChildren().add(circle);
        robot.setVisualNode(circle);
    }
    
    /**
     * Start polluter missions
     */
    public void startMissions() {
        if (missionsRunning) return;
        
        missionsRunning = true;
        lastMissionTime = System.nanoTime();
        
        polluterMissionTimer = new AnimationTimer() {
            private int stepCount = 0;
            
            @Override
            public void handle(long now) {
                if (now - lastMissionTime >= MISSION_STEP_DELAY_NANOS) {
                    boolean allComplete = executeMissionStep(stepCount);
                    stepCount++;
                    lastMissionTime = now;
                    
                    if (allComplete) {
                        stopMissions();
                    }
                }
            }
        };
        polluterMissionTimer.start();
    }
    
    /**
     * Execute one step of all polluter missions
     */
    private boolean executeMissionStep(int stepCount) {
        boolean allComplete = true;
        
        for (RobotPolluter polluter : polluters) {
            if (!polluter.isMissionComplete()) {
                // This will call the appropriate implementation for each polluter type
                polluter.executeMissionStep(stepCount);
                
                if (!polluter.isMissionComplete()) {
                    allComplete = false;
                }
            }
        }
        
        return allComplete;
    }
    
    /**
     * Stop polluter missions
     */
    public void stopMissions() {
        if (polluterMissionTimer != null) {
            polluterMissionTimer.stop();
            missionsRunning = false;
        }
    }
    
    /**
     * Check if missions are running
     */
    public boolean areMissionsRunning() {
        return missionsRunning;
    }
    
    /**
     * Remove a specific robot
     */
    public void removeRobot(Robot robot) {
        if (robot == null) {
            return;
        }
        robots.remove(robot);
        if (robot instanceof RobotPolluter robotPolluter) {
            polluters.remove(robotPolluter);
        }
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
        polluters.clear();
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
            // Only clean if robot is a standard robot and not moving
            if (robot != null && !(robot instanceof RobotPolluter) && !robot.isMoving()) {
                cleanCurrentCell(robot);
            }
        }
    }
    
    /**
     * Clean the cell where the robot currently is
     */
    private void cleanCurrentCell(Robot robot) {
        gridManager.cleanCell(robot.getGridRowOneBased(), robot.getGridColOneBased());
    }
    
    /**
     * Move a specific robot to a grid position with cardinal animation (1-based)
     */
    public void moveRobotToPosition(Robot robot, int gridRow, int gridCol) {
        if (gridRow < 1 || gridRow > GRID_SIZE || gridCol < 1 || gridCol > GRID_SIZE) {
            System.out.println("Invalid target position!");
            return;
        }
        
        robot.moveToPosition(gridRow, gridCol);
    }

    /**
     * Reset a polluter's mission so it can run again
     */
    public void resetPolluterMission(RobotPolluter polluter) {
        if (polluter != null) {
            polluter.resetMission();
        }
    }

    /**
     * Start mission for a single polluter
     */
    public void startSinglePolluterMission(RobotPolluter polluter) {
        if (polluter == null) return;
        
        // Stop any currently running missions first
        stopMissions();
        
        // Start a timer for this specific polluter
        final int[] stepCount = {0};
        
        AnimationTimer missionTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            private static final long STEP_DELAY = 500_000_000; // 500ms between steps
            
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= STEP_DELAY) {
                    boolean completed = polluter.executeMissionStep(stepCount[0]);
                    stepCount[0]++;
                    
                    if (completed) {
                        this.stop();
                        System.out.println(polluter + " mission completed!");
                    }
                    
                    lastUpdate = now;
                }
            }
        };
        
        // Store the timer so it can be stopped later
        missionTimer.start();
    }
    
    /**
     * Get current robot count
     */
    public int getRobotCount() {
        return robots.size();
    }
    
    /**
     * Get polluter count
     */
    public int getPolluterCount() {
        return polluters.size();
    }
    
    /**
     * Get all robots (for external manipulation if needed)
     */
    public List<Robot> getRobots() {
        return new ArrayList<>(robots);
    }
    
    /**
     * Get all polluters
     */
    public List<RobotPolluter> getPolluters() {
        return new ArrayList<>(polluters);
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
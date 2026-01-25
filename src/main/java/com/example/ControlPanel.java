package com.example;

import static com.example.GridConstants.GRID_SIZE;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Builds and manages the control panel UI with grid and robot controls.
 */
public class ControlPanel {
    private final GridManager gridManager;
    private final RobotManager robotManager;
    private final Label counterLabel;
    private final Label robotCountLabel;
    private final TextField rowInput;
    private final TextField colInput;

    public ControlPanel(GridManager gridManager, RobotManager robotManager) {
        this.gridManager = gridManager;
        this.robotManager = robotManager;
        this.counterLabel = new Label("Colored cells: 0");
        this.robotCountLabel = new Label("Robots: 0");
        this.rowInput = new TextField();
        this.colInput = new TextField();
    }

    public VBox build() {
        // Row 1: Grid status and reset
        configureCounterLabel();
        configureRobotCountLabel();
        Button resetButton = createResetButton();
        
        HBox controlRow1 = new HBox(20, counterLabel, robotCountLabel, resetButton);
        controlRow1.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 2: Cell operations
        configureInputFields();
        Button dirtyButton = createDirtyButton();
        Button cleanButton = createCleanButton();
        
        HBox controlRow2 = new HBox(10, rowInput, colInput, dirtyButton, cleanButton);
        controlRow2.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 3: Robot operations
        Button addRobotButton = createAddRobotButton();
        Button removeLastRobotButton = createRemoveLastRobotButton();
        Button clearRobotsButton = createClearRobotsButton();
        
        HBox controlRow3 = new HBox(10, addRobotButton, removeLastRobotButton, clearRobotsButton);
        controlRow3.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 4: Robot movement controls
        Button startButton = createStartButton();
        Button stopButton = createStopButton();
        Button upButton = createDirectionButton("▲", () -> moveAllRobots("UP"));
        Button downButton = createDirectionButton("▼", () -> moveAllRobots("DOWN"));
        Button leftButton = createDirectionButton("◄", () -> moveAllRobots("LEFT"));
        Button rightButton = createDirectionButton("►", () -> moveAllRobots("RIGHT"));
        
        HBox controlRow4 = new HBox(10, startButton, stopButton, upButton, downButton, 
                                     leftButton, rightButton);
        controlRow4.setStyle("-fx-alignment: center; -fx-padding: 10;");

        return new VBox(5, controlRow1, controlRow2, controlRow3, controlRow4);
    }

    private void configureCounterLabel() {
        counterLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    }

    private void configureRobotCountLabel() {
        robotCountLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset Grid");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setOnAction(e -> {
            gridManager.resetGrid();
            robotManager.stopSimulation();
            robotManager.clearAllRobots();
            updateRobotCount();
        });
        return resetButton;
    }

    private void configureInputFields() {
        rowInput.setPromptText("Row (1-" + GRID_SIZE + ")");
        rowInput.setPrefWidth(80);

        colInput.setPromptText("Col (1-" + GRID_SIZE + ")");
        colInput.setPrefWidth(80);
    }

    private Button createDirtyButton() {
        Button dirtyButton = new Button("Dirty Cell");
        dirtyButton.setStyle("-fx-font-size: 16px;");
        dirtyButton.setOnAction(e -> handleDirtyAction());
        return dirtyButton;
    }

    private Button createCleanButton() {
        Button cleanButton = new Button("Clean Cell");
        cleanButton.setStyle("-fx-font-size: 16px;");
        cleanButton.setOnAction(e -> handleCleanAction());
        return cleanButton;
    }

    private Button createAddRobotButton() {
        Button addRobotButton = new Button("Add Robot");
        addRobotButton.setStyle("-fx-font-size: 16px;");
        addRobotButton.setOnAction(e -> handleAddRobot());
        return addRobotButton;
    }

    private Button createRemoveLastRobotButton() {
        Button removeButton = new Button("Remove Last");
        removeButton.setStyle("-fx-font-size: 16px;");
        removeButton.setOnAction(e -> handleRemoveLastRobot());
        return removeButton;
    }

    private Button createClearRobotsButton() {
        Button clearButton = new Button("Clear Robots");
        clearButton.setStyle("-fx-font-size: 16px;");
        clearButton.setOnAction(e -> {
            robotManager.clearAllRobots();
            updateRobotCount();
        });
        return clearButton;
    }

    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startButton.setOnAction(e -> robotManager.startSimulation());
        return startButton;
    }

    private Button createStopButton() {
        Button stopButton = new Button("Pause");
        stopButton.setStyle("-fx-font-size: 16px; -fx-background-color: #f44336; -fx-text-fill: white;");
        stopButton.setOnAction(e -> robotManager.stopSimulation());
        return stopButton;
    }

    private Button createDirectionButton(String symbol, Runnable action) {
        Button button = new Button(symbol);
        button.setStyle("-fx-font-size: 16px; -fx-min-width: 40px;");
        button.setOnAction(e -> action.run());
        return button;
    }

    private void handleDirtyAction() {
        try {
            int row = Integer.parseInt(rowInput.getText());
            int col = Integer.parseInt(colInput.getText());
            gridManager.dirtyCell(row, col);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input!");
        }
    }

    private void handleCleanAction() {
        try {
            int row = Integer.parseInt(rowInput.getText());
            int col = Integer.parseInt(colInput.getText());
            gridManager.cleanCell(row, col);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input!");
        }
    }

    private void handleAddRobot() {
        try {
            int row = Integer.parseInt(rowInput.getText());
            int col = Integer.parseInt(colInput.getText());
            Robot robot = robotManager.createRobot(row, col);
            if (robot != null) {
                updateRobotCount();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input! Using default position (1, 1)");
            robotManager.createRobot(1, 1);
            updateRobotCount();
        }
    }

    private void handleRemoveLastRobot() {
        if (robotManager.getRobotCount() > 0) {
            Robot lastRobot = robotManager.getRobot(robotManager.getRobotCount() - 1);
            if (lastRobot != null) {
                robotManager.removeRobot(lastRobot);
                updateRobotCount();
            }
        }
    }

    private void moveAllRobots(String direction) {
        for (Robot robot : robotManager.getRobots()) {
            switch (direction) {
                case "UP" -> robot.moveUp();
                case "DOWN" -> robot.moveDown();
                case "LEFT" -> robot.moveLeft();
                case "RIGHT" -> robot.moveRight();
            }
        }
    }

    public void updateCounter(int count) {
        counterLabel.setText("Colored cells: " + count);
    }

    private void updateRobotCount() {
        robotCountLabel.setText("Robots: " + robotManager.getRobotCount());
    }
}
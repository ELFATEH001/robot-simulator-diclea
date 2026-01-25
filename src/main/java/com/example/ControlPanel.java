package com.example;

import static com.example.GridConstants.GRID_SIZE;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    
    // Cell operation fields
    private final TextField cellRowInput;
    private final TextField cellColInput;
    
    // Robot operation fields
    private final TextField robotRowInput;
    private final TextField robotColInput;
    private final ComboBox<Robot> robotSelector;    

    public ControlPanel(GridManager gridManager, RobotManager robotManager) {
        this.gridManager = gridManager;
        this.robotManager = robotManager;
        this.counterLabel = new Label("Colored cells: 0");
        this.robotCountLabel = new Label("Robots: 0");
        
        // Cell operation inputs
        this.cellRowInput = new TextField();
        this.cellColInput = new TextField();
        
        // Robot operation inputs
        this.robotRowInput = new TextField();
        this.robotColInput = new TextField();
        this.robotSelector = new ComboBox<>();
    }

    public VBox build() {
        // Row 1: Grid status and reset
        configureCounterLabel();
        configureRobotCountLabel();
        Button resetButton = createResetButton();
        
        HBox controlRow1 = new HBox(20, counterLabel, robotCountLabel, resetButton);
        controlRow1.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 2: Cell operations (dirty/clean)
        configureCellInputFields();
        Button dirtyButton = createDirtyButton();
        Button cleanButton = createCleanButton();
        
        HBox controlRow2 = new HBox(10, 
            new Label("Cell:"), cellRowInput, cellColInput, dirtyButton, cleanButton);
        controlRow2.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 3: Robot creation
        configureRobotInputFields();
        Button createRobotButton = createRobotAtPositionButton();
        Button removeSelectedRobotButton = createRemoveSelectedRobotButton();
        Button clearRobotsButton = createClearRobotsButton();
        
        HBox controlRow3 = new HBox(10, 
            new Label("Robot pos:"), robotRowInput, robotColInput, 
            createRobotButton, removeSelectedRobotButton, clearRobotsButton);
        controlRow3.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 4: Robot selection and movement
        configureRobotSelector();
        Button moveToButton = createMoveToButton();
        Button startButton = createStartButton();
        Button stopButton = createStopButton();
        
        HBox controlRow4 = new HBox(10, 
            new Label("Select:"), robotSelector, moveToButton, startButton, stopButton);
        controlRow4.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 5: Manual robot movement controls
        Button upButton = createDirectionButton("▲", () -> moveSelectedRobot("UP"));
        Button downButton = createDirectionButton("▼", () -> moveSelectedRobot("DOWN"));
        Button leftButton = createDirectionButton("◄", () -> moveSelectedRobot("LEFT"));
        Button rightButton = createDirectionButton("►", () -> moveSelectedRobot("RIGHT"));
        
        HBox controlRow5 = new HBox(10, 
            new Label("Move:"), upButton, downButton, leftButton, rightButton);
        controlRow5.setStyle("-fx-alignment: center; -fx-padding: 10;");

        return new VBox(5, controlRow1, controlRow2, controlRow3, controlRow4, controlRow5);
    }

    private void configureCounterLabel() {
        counterLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    }

    private void configureRobotCountLabel() {
        robotCountLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    }

    private Button createRemoveSelectedRobotButton() {
        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle("-fx-font-size: 14px;");
        removeButton.setOnAction(e -> handleRemoveSelectedRobot());
        return removeButton;
    }

    private void handleRemoveSelectedRobot() {
        Robot selectedRobot = robotSelector.getSelectionModel().getSelectedItem();

        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }

        robotManager.removeRobot(selectedRobot);
        updateRobotCount();
        updateRobotSelector();
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset Grid");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setOnAction(e -> {
            gridManager.resetGrid();
            robotManager.stopSimulation();
            robotManager.clearAllRobots();
            updateRobotCount();
            updateRobotSelector();
        });
        return resetButton;
    }

    private void configureCellInputFields() {
        cellRowInput.setPromptText("Row (1-" + GRID_SIZE + ")");
        cellRowInput.setPrefWidth(80);

        cellColInput.setPromptText("Col (1-" + GRID_SIZE + ")");
        cellColInput.setPrefWidth(80);
    }

    private void configureRobotInputFields() {
        robotRowInput.setPromptText("Row (1-" + GRID_SIZE + ")");
        robotRowInput.setPrefWidth(80);

        robotColInput.setPromptText("Col (1-" + GRID_SIZE + ")");
        robotColInput.setPrefWidth(80);
    }

    private void configureRobotSelector() {
        robotSelector.setPromptText("Select Robot");
        robotSelector.setPrefWidth(120);
    }

    private Button createDirtyButton() {
        Button dirtyButton = new Button("Dirty");
        dirtyButton.setStyle("-fx-font-size: 14px;");
        dirtyButton.setOnAction(e -> handleDirtyAction());
        return dirtyButton;
    }

    private Button createCleanButton() {
        Button cleanButton = new Button("Clean");
        cleanButton.setStyle("-fx-font-size: 14px;");
        cleanButton.setOnAction(e -> handleCleanAction());
        return cleanButton;
    }

    private Button createRobotAtPositionButton() {
        Button createButton = new Button("Create Robot");
        createButton.setStyle("-fx-font-size: 14px;");
        createButton.setOnAction(e -> handleCreateRobotAtPosition());
        return createButton;
    }

    

    private Button createClearRobotsButton() {
        Button clearButton = new Button("Clear All");
        clearButton.setStyle("-fx-font-size: 14px;");
        clearButton.setOnAction(e -> {
            robotManager.clearAllRobots();
            updateRobotCount();
            updateRobotSelector();
        });
        return clearButton;
    }

    private Button createMoveToButton() {
        Button moveToButton = new Button("Move To Position");
        moveToButton.setStyle("-fx-font-size: 14px;");
        moveToButton.setOnAction(e -> handleMoveToPosition());
        return moveToButton;
    }

    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startButton.setOnAction(e -> robotManager.startSimulation());
        return startButton;
    }

    private Button createStopButton() {
        Button stopButton = new Button("Pause");
        stopButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white;");
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
            int row = Integer.parseInt(cellRowInput.getText());
            int col = Integer.parseInt(cellColInput.getText());
            gridManager.dirtyCell(row, col);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid cell input!");
        }
    }

    private void handleCleanAction() {
        try {
            int row = Integer.parseInt(cellRowInput.getText());
            int col = Integer.parseInt(cellColInput.getText());
            gridManager.cleanCell(row, col);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid cell input!");
        }
    }

    private void handleCreateRobotAtPosition() {
        try {
            int row = Integer.parseInt(robotRowInput.getText());
            int col = Integer.parseInt(robotColInput.getText());
            Robot robot = robotManager.createRobot(row, col);
            if (robot != null) {
                updateRobotCount();
                updateRobotSelector();
                robotSelector.getSelectionModel().select(robot);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid robot position input!");
        }
    }


    private void handleMoveToPosition() {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }

        try {
            int row = Integer.parseInt(robotRowInput.getText());
            int col = Integer.parseInt(robotColInput.getText());
            
            if (row >= 1 && row <= GRID_SIZE && col >= 1 && col <= GRID_SIZE) {
                robotManager.moveRobotToPosition(selectedRobot, row, col);
            } else {
                System.out.println("Position out of bounds!");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid position input!");
        }
    }

    private void moveSelectedRobot(String direction) {
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }

        switch (direction) {
            case "UP" -> selectedRobot.moveUp();
            case "DOWN" -> selectedRobot.moveDown();
            case "LEFT" -> selectedRobot.moveLeft();
            case "RIGHT" -> selectedRobot.moveRight();
        }
    }

    private Robot getSelectedRobot() {
        return robotSelector.getSelectionModel().getSelectedItem();
    }

    private void updateRobotSelector() {
    robotSelector.getItems().setAll(robotManager.getRobots());

        if (!robotSelector.getItems().isEmpty()) {
            robotSelector.getSelectionModel().selectFirst();
        }
    }

    public void updateCounter(int count) {
        counterLabel.setText("Colored cells: " + count);
    }

    private void updateRobotCount() {
        robotCountLabel.setText("Robots: " + robotManager.getRobotCount());
    }
}
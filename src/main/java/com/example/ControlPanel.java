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
    private final Label polluterCountLabel;
    
    // Cell operation fields
    private final TextField cellRowInput;
    private final TextField cellColInput;
    
    // Robot operation fields
    private final TextField robotRowInput;
    private final TextField robotColInput;
    private final ComboBox<Robot> robotSelector;
    
    // Polluter creation fields
    private final ComboBox<String> polluterTypeSelector;
    private final TextField polluterParam1Input;
    private final TextField polluterParam2Input;

    public ControlPanel(GridManager gridManager, RobotManager robotManager) {
        this.gridManager = gridManager;
        this.robotManager = robotManager;
        this.counterLabel = new Label("Colored cells: 0");
        this.robotCountLabel = new Label("Robots: 0");
        this.polluterCountLabel = new Label("Polluters: 0");
        
        // Cell operation inputs
        this.cellRowInput = new TextField();
        this.cellColInput = new TextField();
        
        // Robot operation inputs
        this.robotRowInput = new TextField();
        this.robotColInput = new TextField();
        this.robotSelector = new ComboBox<>();
        
        // Polluter creation inputs
        this.polluterTypeSelector = new ComboBox<>();
        this.polluterParam1Input = new TextField();
        this.polluterParam2Input = new TextField();
    }

    public VBox build() {
        // Row 1: Grid status and reset
        configureCounterLabel();
        configureRobotCountLabel();
        configurePolluterCountLabel();
        Button resetButton = createResetButton();
        
        HBox controlRow1 = new HBox(15, counterLabel, robotCountLabel, polluterCountLabel, resetButton);
        controlRow1.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 2: Cell operations (dirty/clean)
        configureCellInputFields();
        Button dirtyButton = createDirtyButton();
        Button cleanButton = createCleanButton();
        
        HBox controlRow2 = new HBox(10, 
            new Label("Cell:"), cellRowInput, cellColInput, dirtyButton, cleanButton);
        controlRow2.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 3: Standard robot creation
        configureRobotInputFields();
        Button createRobotButton = createRobotAtPositionButton();
        Button removeSelectedRobotButton = createRemoveSelectedRobotButton();
        Button clearRobotsButton = createClearRobotsButton();
        
        HBox controlRow3 = new HBox(10, 
            new Label("Robot pos:"), robotRowInput, robotColInput, 
            createRobotButton, removeSelectedRobotButton, clearRobotsButton);
        controlRow3.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 4: Polluter creation
        configurePolluterControls();
        Button createPolluterButton = createPolluterButton();
        Button startSelectedMissionButton = createStartSelectedMissionButton();
        Button stopMissionsButton = createStopMissionsButton();
        
        HBox controlRow4 = new HBox(10,
            new Label("Polluter:"), polluterTypeSelector, polluterParam1Input, polluterParam2Input,
            createPolluterButton, startSelectedMissionButton, stopMissionsButton);
        controlRow4.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 5: Robot selection and movement
        configureRobotSelector();
        Button moveToButton = createMoveToButton();
        Button startButton = createStartButton();
        Button stopButton = createStopButton();
        
        HBox controlRow5 = new HBox(10, 
            new Label("Select:"), robotSelector, moveToButton, startButton, stopButton);
        controlRow5.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 6: Manual robot movement controls
        Button upButton = createDirectionButton("▲", () -> moveSelectedRobot("UP"));
        Button downButton = createDirectionButton("▼", () -> moveSelectedRobot("DOWN"));
        Button leftButton = createDirectionButton("◄", () -> moveSelectedRobot("LEFT"));
        Button rightButton = createDirectionButton("►", () -> moveSelectedRobot("RIGHT"));
        
        HBox controlRow6 = new HBox(10, 
            new Label("Move:"), upButton, downButton, leftButton, rightButton);
        controlRow6.setStyle("-fx-alignment: center; -fx-padding: 10;");

        return new VBox(5, controlRow1, controlRow2, controlRow3, controlRow4, controlRow5, controlRow6);
    }

    private void configureCounterLabel() {
        counterLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
    }

    private void configureRobotCountLabel() {
        robotCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
    }

    private void configurePolluterCountLabel() {
        polluterCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ff4444;");
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
        updatePolluterCount();
        updateRobotSelector();
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset Grid");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setOnAction(e -> {
            gridManager.resetGrid();
            robotManager.stopSimulation();
            robotManager.stopMissions();
            robotManager.clearAllRobots();
            updateRobotCount();
            updatePolluterCount();
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

    private void configurePolluterControls() {
        polluterTypeSelector.getItems().addAll("Straight Line", "Jumping", "Free Movement");
        polluterTypeSelector.setPromptText("Type");
        polluterTypeSelector.setPrefWidth(120);
        
        polluterParam1Input.setPromptText("Param 1");
        polluterParam1Input.setPrefWidth(80);
        
        polluterParam2Input.setPromptText("Param 2");
        polluterParam2Input.setPrefWidth(80);
        
        // Set default values based on polluter type selection
        polluterTypeSelector.setOnAction(e -> updatePolluterParameterHints());
    }

    private void updatePolluterParameterHints() {
        String selectedType = polluterTypeSelector.getValue();
        if (selectedType == null) return;
        
        switch (selectedType) {
            case "Straight Line" -> {
                polluterParam1Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                polluterParam2Input.setPromptText("(not used)");
                polluterParam2Input.setDisable(true);
            }
            case "Jumping" -> {
                polluterParam1Input.setPromptText("Start Row (1-" + GRID_SIZE + ")");
                polluterParam2Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                polluterParam2Input.setDisable(false);
            }
            case "Free Movement" -> {
                polluterParam1Input.setPromptText("Start Row (1-" + GRID_SIZE + ")");
                polluterParam2Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                polluterParam2Input.setDisable(false);
            }
        }
    }

    private void configureRobotSelector() {
        robotSelector.setPromptText("Select Robot");
        robotSelector.setPrefWidth(150);
        // Set cell factory to display custom text for each robot
        robotSelector.setCellFactory(lv -> new javafx.scene.control.ListCell<Robot>() {
            @Override
            protected void updateItem(Robot robot, boolean empty) {
                super.updateItem(robot, empty);
                setText(empty ? null : robot.toString());
            }
        });
        // Set button cell for display
        robotSelector.setButtonCell(new javafx.scene.control.ListCell<Robot>() {
            @Override
            protected void updateItem(Robot robot, boolean empty) {
                super.updateItem(robot, empty);
                setText(empty ? null : robot.toString());
            }
        });
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
        createButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white;");
        createButton.setOnAction(e -> handleCreateRobotAtPosition());
        return createButton;
    }

    private Button createPolluterButton() {
        Button createButton = new Button("Create Polluter");
        createButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ff4444; -fx-text-fill: white;");
        createButton.setOnAction(e -> handleCreatePolluter());
        return createButton;
    }

    private Button createStartSelectedMissionButton() {
        Button button = new Button("Start Selected Mission");
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #ff8800; -fx-text-fill: white;");
        button.setOnAction(e -> handleStartSelectedMission());
        return button;
    }

    private void handleStartSelectedMission() {
        Robot selectedRobot = robotSelector.getSelectionModel().getSelectedItem();
        
        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }
        
        // Check if the selected robot is a polluter
        if (!(selectedRobot instanceof RobotPolluter)) {
            System.out.println("Selected robot is not a polluter!");
            return;
        }
        
        RobotPolluter polluter = (RobotPolluter) selectedRobot;
        
        // Reset the polluter's mission if it was already completed
        if (polluter.isMissionComplete()) {
            System.out.println("Resetting mission for: " + polluter);
            robotManager.resetPolluterMission(polluter);
        }
        
        // Start the specific polluter's mission
        robotManager.startSinglePolluterMission(polluter);
        System.out.println("Started mission for: " + polluter);
    }

    private Button createStopMissionsButton() {
        Button button = new Button("Stop All Missions");
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #ff4444; -fx-text-fill: white;");
        button.setOnAction(e -> {
            robotManager.stopMissions();
            System.out.println("All polluter missions stopped!");
        });
        return button;
    }

    private Button createClearRobotsButton() {
        Button clearButton = new Button("Clear All");
        clearButton.setStyle("-fx-font-size: 14px;");
        clearButton.setOnAction(e -> {
            robotManager.clearAllRobots();
            updateRobotCount();
            updatePolluterCount();
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
        Button startButton = new Button("Start Cleaning");
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startButton.setOnAction(e -> {
            robotManager.startSimulation();
            System.out.println("Cleaning simulation started!");
        });
        return startButton;
    }

    private Button createStopButton() {
        Button stopButton = new Button("Pause Cleaning");
        stopButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white;");
        stopButton.setOnAction(e -> {
            robotManager.stopSimulation();
            System.out.println("Cleaning simulation paused!");
        });
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

    private void handleCreatePolluter() {
        String polluterType = polluterTypeSelector.getValue();
        if (polluterType == null || polluterType.isEmpty()) {
            System.out.println("Please select a polluter type first!");
            return;
        }

        try {
            RobotPolluter createdPolluter = null;
            
            switch (polluterType) {
                case "Straight Line" -> {
                    int startCol = polluterParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(polluterParam1Input.getText());
                    startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
                    createdPolluter = robotManager.createStraightPolluter(startCol);
                }
                    
                case "Jumping" -> {
                    int jumpRow = polluterParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(polluterParam1Input.getText());
                    int jumpCol = polluterParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(polluterParam2Input.getText());
                    jumpRow = Math.max(1, Math.min(jumpRow, GRID_SIZE));
                    jumpCol = Math.max(1, Math.min(jumpCol, GRID_SIZE));
                    int jumpSize = 2; // Default jump size
                    createdPolluter = robotManager.createJumpingPolluter(jumpRow, jumpCol, jumpSize);
                }
                    
                case "Free Movement" -> {
                    int freeRow = polluterParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(polluterParam1Input.getText());
                    int freeCol = polluterParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(polluterParam2Input.getText());
                    freeRow = Math.max(1, Math.min(freeRow, GRID_SIZE));
                    freeCol = Math.max(1, Math.min(freeCol, GRID_SIZE));
                    int maxPollutions = 15; // Default
                    createdPolluter = robotManager.createFreePolluter(freeRow, freeCol, maxPollutions);
                }
            }
            
            updateRobotCount();
            updatePolluterCount();
            updateRobotSelector();
            
            // Select the newly created polluter
            if (createdPolluter != null) {
                robotSelector.getSelectionModel().select(createdPolluter);
            }
            
            // Clear input fields
            polluterParam1Input.clear();
            polluterParam2Input.clear();
            
        } catch (NumberFormatException ex) {
            System.out.println("Invalid polluter parameters! Using random values.");
            // Create with random parameters
            if (polluterType.equals("Straight Line")) {
                robotManager.createStraightPolluter((int) (Math.random() * GRID_SIZE) + 1);
            } else {
                robotManager.createJumpingPolluter(
                    (int) (Math.random() * GRID_SIZE) + 1,
                    (int) (Math.random() * GRID_SIZE) + 1,
                    2);
            }
            updateRobotCount();
            updatePolluterCount();
            updateRobotSelector();
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
    
    private void updatePolluterCount() {
        polluterCountLabel.setText("Polluters: " + robotManager.getPolluterCount());
    }
}
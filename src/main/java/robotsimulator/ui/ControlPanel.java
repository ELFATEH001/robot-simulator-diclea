package robotsimulator.ui;

import static robotsimulator.model.GridConstants.GRID_SIZE;

import robotsimulator.cleaning.RobotCleaner;
import robotsimulator.core.RobotManager;
import robotsimulator.model.Robot;
import robotsimulator.pollution.RobotPolluter;

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
    private final Label cleanerCountLabel;
    
    // Cell operation fields
    private final TextField cellRowInput;
    private final TextField cellColInput;
    
    // Robot operation fields
    private final TextField robotRowInput;
    private final TextField robotColInput;
    private final ComboBox<Robot> robotSelector;
    
    // Polluter creation fields
    private final ComboBox<String> polluterTypeSelector;
    private final ComboBox<String> cleanerTypeSelector;
    private final TextField polluterParam1Input;
    private final TextField polluterParam2Input;
    private final TextField cleanerParam1Input;
    private final TextField cleanerParam2Input;

    public ControlPanel(GridManager gridManager, RobotManager robotManager) {
        this.gridManager = gridManager;
        this.robotManager = robotManager;
        this.counterLabel = new Label("Colored cells: 0");
        this.robotCountLabel = new Label("Robots: 0");
        this.polluterCountLabel = new Label("Polluters: 0");
        this.cleanerCountLabel = new Label("Cleaners: 0");
        
        // Cell operation inputs
        this.cellRowInput = new TextField();
        this.cellColInput = new TextField();
        
        // Robot operation inputs
        this.robotRowInput = new TextField();
        this.robotColInput = new TextField();
        this.robotSelector = new ComboBox<>();
        
        // Polluter creation inputs
        this.polluterTypeSelector = new ComboBox<>();
        this.cleanerTypeSelector = new ComboBox<>();
        this.polluterParam1Input = new TextField();
        this.polluterParam2Input = new TextField();
        this.cleanerParam1Input = new TextField();
        this.cleanerParam2Input = new TextField();
    }

    private Label whiteLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        return label;
    }

    public VBox build() {
        // Row 1: Grid status and reset
        configureCounterLabel();
        configureRobotCountLabel();
        configurePolluterCountLabel();
        configureCleanerCountLabel();
        Button resetButton = createResetButton();
        
        HBox controlRow1 = new HBox(15, counterLabel, robotCountLabel, polluterCountLabel, cleanerCountLabel, resetButton);
        controlRow1.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 2: Cell operations (dirty/clean)
        configureCellInputFields();
        Button dirtyButton = createDirtyButton();
        Button cleanButton = createCleanButton();
        
        HBox controlRow2 = new HBox(10, 
            whiteLabel("Cell:"), cellRowInput, cellColInput, dirtyButton, cleanButton);
        controlRow2.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 3: Standard robot creation
        configureRobotInputFields();
        Button createRobotButton = createRobotAtPositionButton();
        Button removeSelectedRobotButton = createRemoveSelectedRobotButton();
        Button clearRobotsButton = createClearRobotsButton();
        
        HBox controlRow3 = new HBox(10, 
            whiteLabel("Robot pos:"), robotRowInput, robotColInput,
            createRobotButton, removeSelectedRobotButton, clearRobotsButton);
        controlRow3.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 4: Polluter creation
        configurePolluterControls();
        Button createPolluterButton = createPolluterButton();
        Button startSelectedMissionButton = createStartSelectedMissionButton();
        
        HBox controlRow4 = new HBox(10,
            whiteLabel("Polluter:"), polluterTypeSelector, polluterParam1Input, polluterParam2Input,
            createPolluterButton, startSelectedMissionButton);
        controlRow4.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 5: Cleaner creation
        configureCleanerControls();
        Button createCleanerButton = createCleanerButton();
        Button startSelectedCleaningButton = createStartSelectedCleaningButton();
        
        HBox controlRow5 = new HBox(10,
            whiteLabel("Cleaner:"), cleanerTypeSelector, cleanerParam1Input, cleanerParam2Input,
            createCleanerButton, startSelectedCleaningButton);
        controlRow5.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 6: Robot selection and movement
        configureRobotSelector();
        Button moveToButton = createMoveToButton();
        Button startAllCleanersButton = createStartAllCleanersButton();
        Button stopAllCleanersButton = createStopAllCleanersButton();
        
        HBox controlRow6 = new HBox(10, 
            whiteLabel("Select:"), robotSelector, moveToButton, startAllCleanersButton, stopAllCleanersButton);
        controlRow6.setStyle("-fx-alignment: center; -fx-padding: 10;");

        // Row 7: Manual robot movement controls
        Button upButton = createDirectionButton("▲", () -> moveSelectedRobot("UP"));
        Button downButton = createDirectionButton("▼", () -> moveSelectedRobot("DOWN"));
        Button leftButton = createDirectionButton("◄", () -> moveSelectedRobot("LEFT"));
        Button rightButton = createDirectionButton("►", () -> moveSelectedRobot("RIGHT"));
        
        HBox controlRow7 = new HBox(10, 
            whiteLabel("Move:"), upButton, downButton, leftButton, rightButton);
        controlRow7.setStyle("-fx-alignment: center; -fx-padding: 10;");

        return new VBox(5, controlRow1, controlRow2, controlRow3, controlRow4, controlRow5, controlRow6, controlRow7);
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

    private void configureCleanerCountLabel() {
        cleanerCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50;");
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
        updateCleanerCount();
        updateRobotSelector();
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset Grid");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setOnAction(e -> {
            gridManager.resetGrid();
            robotManager.stopSimulation();
            robotManager.stopMissions();
            robotManager.stopCleaners();
            robotManager.clearAllRobots();
            updateRobotCount();
            updatePolluterCount();
            updateCleanerCount();
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
        
        polluterTypeSelector.setOnAction(e -> updatePolluterParameterHints());
    }

    private void configureCleanerControls() {
        cleanerTypeSelector.getItems().addAll("Straight Line", "Jumping", "Free Movement", "Complete Grid", "Smart Cleaner");
        cleanerTypeSelector.setPromptText("Type");
        cleanerTypeSelector.setPrefWidth(120);
        
        cleanerParam1Input.setPromptText("Param 1");
        cleanerParam1Input.setPrefWidth(80);
        
        cleanerParam2Input.setPromptText("Param 2");
        cleanerParam2Input.setPrefWidth(80);
        
        cleanerTypeSelector.setOnAction(e -> updateCleanerParameterHints());
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

    private void updateCleanerParameterHints() {
        String selectedType = cleanerTypeSelector.getValue();
        if (selectedType == null) return;
        
        switch (selectedType) {
            case "Straight Line" -> {
                cleanerParam1Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setPromptText("(not used)");
                cleanerParam2Input.setDisable(true);
            }
            case "Jumping" -> {
                cleanerParam1Input.setPromptText("Start Row (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setDisable(false);
            }
            case "Free Movement" -> {
                cleanerParam1Input.setPromptText("Start Row (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setDisable(false);
            }
            case "Complete Grid" -> {
                cleanerParam1Input.setPromptText("(not used)");
                cleanerParam2Input.setPromptText("(not used)");
                cleanerParam1Input.setDisable(true);
                cleanerParam2Input.setDisable(true);
            }
            case "Smart Cleaner" -> {
                cleanerParam1Input.setPromptText("Start Row (1-" + GRID_SIZE + ")");
                cleanerParam2Input.setPromptText("Start Col (1-" + GRID_SIZE + ")");
                cleanerParam1Input.setDisable(false);
                cleanerParam2Input.setDisable(false);
            }
        }
    }

    private void configureRobotSelector() {
        robotSelector.setPromptText("Select Robot");
        robotSelector.setPrefWidth(150);
        robotSelector.setCellFactory(lv -> new javafx.scene.control.ListCell<Robot>() {
            @Override
            protected void updateItem(Robot robot, boolean empty) {
                super.updateItem(robot, empty);
                setText(empty ? null : robot.toString());
            }
        });
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

    private Button createCleanerButton() {
        Button createButton = new Button("Create Cleaner");
        createButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        createButton.setOnAction(e -> handleCreateCleaner());
        return createButton;
    }

    private Button createStartSelectedMissionButton() {
        Button button = new Button("Start Selected Mission");
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #ff8800; -fx-text-fill: white;");
        button.setOnAction(e -> handleStartSelectedMission());
        return button;
    }

    private Button createStartSelectedCleaningButton() {
        Button button = new Button("Start Selected Cleaning");
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        button.setOnAction(e -> handleStartSelectedCleaning());
        return button;
    }

    private void handleStartSelectedMission() {
        Robot selectedRobot = robotSelector.getSelectionModel().getSelectedItem();
        
        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }
        
        if (!(selectedRobot instanceof RobotPolluter)) {
            System.out.println("Selected robot is not a polluter!");
            return;
        }
        
        RobotPolluter polluter = (RobotPolluter) selectedRobot;
        robotManager.resetPolluterMission(polluter);
        robotManager.startSinglePolluterMission(polluter);
        System.out.println("Started mission for: " + polluter);
    }

    private void handleStartSelectedCleaning() {
        Robot selectedRobot = robotSelector.getSelectionModel().getSelectedItem();
        
        if (selectedRobot == null) {
            System.out.println("Please select a robot first!");
            return;
        }
        
        if (!(selectedRobot instanceof RobotCleaner)) {
            System.out.println("Selected robot is not a cleaner!");
            return;
        }
        
        RobotCleaner cleaner = (RobotCleaner) selectedRobot;
        robotManager.resetCleanerMission(cleaner);
        robotManager.startSingleCleanerMission(cleaner);
        System.out.println("Started cleaning mission for: " + cleaner);
    }

    private Button createClearRobotsButton() {
        Button clearButton = new Button("Clear All");
        clearButton.setStyle("-fx-font-size: 14px;");
        clearButton.setOnAction(e -> {
            robotManager.clearAllRobots();
            updateRobotCount();
            updatePolluterCount();
            updateCleanerCount();
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

    private Button createStartAllCleanersButton() {
        Button startButton = new Button("Start All Cleaners");
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        startButton.setOnAction(e -> {
            robotManager.startAllCleaners();
            System.out.println("All cleaners started!");
        });
        return startButton;
    }

    private Button createStopAllCleanersButton() {
        Button stopButton = new Button("Stop All Cleaners");
        stopButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white;");
        stopButton.setOnAction(e -> {
            robotManager.stopAllCleaners();
            System.out.println("All cleaners stopped!");
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
                    int jumpSize = 2;
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
                    int maxPollutions = 15;
                    createdPolluter = robotManager.createFreePolluter(freeRow, freeCol, maxPollutions);
                }
            }
            
            updateRobotCount();
            updatePolluterCount();
            updateRobotSelector();
            
            if (createdPolluter != null) {
                robotSelector.getSelectionModel().select(createdPolluter);
            }
            
            polluterParam1Input.clear();
            polluterParam2Input.clear();
            
        } catch (NumberFormatException ex) {
            System.out.println("Invalid polluter parameters! Using random values.");
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

    private void handleCreateCleaner() {
        String cleanerType = cleanerTypeSelector.getValue();
        if (cleanerType == null || cleanerType.isEmpty()) {
            System.out.println("Please select a cleaner type first!");
            return;
        }

        try {
            RobotCleaner createdCleaner = null;
            
            switch (cleanerType) {
                case "Straight Line" -> {
                    int startCol = cleanerParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam1Input.getText());
                    startCol = Math.max(1, Math.min(startCol, GRID_SIZE));
                    createdCleaner = robotManager.createStraightCleaner(startCol);
                }
                    
                case "Jumping" -> {
                    int jumpRow = cleanerParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam1Input.getText());
                    int jumpCol = cleanerParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam2Input.getText());
                    jumpRow = Math.max(1, Math.min(jumpRow, GRID_SIZE));
                    jumpCol = Math.max(1, Math.min(jumpCol, GRID_SIZE));
                    int jumpSize = 2;
                    createdCleaner = robotManager.createJumpingCleaner(jumpRow, jumpCol, jumpSize);
                }
                    
                case "Free Movement" -> {
                    int freeRow = cleanerParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam1Input.getText());
                    int freeCol = cleanerParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam2Input.getText());
                    freeRow = Math.max(1, Math.min(freeRow, GRID_SIZE));
                    freeCol = Math.max(1, Math.min(freeCol, GRID_SIZE));
                    int maxSteps = GRID_SIZE * GRID_SIZE;
                    createdCleaner = robotManager.createFreeCleaner(freeRow, freeCol, maxSteps);
                }
                    
                case "Complete Grid" -> {
                    createdCleaner = robotManager.createCompleteCleaner();
                }
                    
                case "Smart Cleaner" -> {
                    int smartRow = cleanerParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam1Input.getText());
                    int smartCol = cleanerParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam2Input.getText());
                    smartRow = Math.max(1, Math.min(smartRow, GRID_SIZE));
                    smartCol = Math.max(1, Math.min(smartCol, GRID_SIZE));
                    int maxCleaningSteps = GRID_SIZE * GRID_SIZE; // Default to clean entire grid
                    createdCleaner = robotManager.createSmartCleaner(smartRow, smartCol, maxCleaningSteps);
                }
            }
            
            updateRobotCount();
            updateCleanerCount();
            updateRobotSelector();
            
            if (createdCleaner != null) {
                robotSelector.getSelectionModel().select(createdCleaner);
            }
            
            cleanerParam1Input.clear();
            cleanerParam2Input.clear();
            
        } catch (NumberFormatException ex) {
            System.out.println("Invalid cleaner parameters! Using random values.");
            switch (cleanerType) {
                case "Straight Line" -> 
                    robotManager.createStraightCleaner((int) (Math.random() * GRID_SIZE) + 1);
                case "Jumping" -> 
                    robotManager.createJumpingCleaner(
                        (int) (Math.random() * GRID_SIZE) + 1,
                        (int) (Math.random() * GRID_SIZE) + 1,
                        2);
                case "Free Movement" -> 
                    robotManager.createFreeCleaner(
                        (int) (Math.random() * GRID_SIZE) + 1,
                        (int) (Math.random() * GRID_SIZE) + 1,
                        GRID_SIZE * GRID_SIZE);
                case "Complete Grid" -> 
                    robotManager.createCompleteCleaner();
                case "Smart Cleaner" -> 
                    robotManager.createSmartCleaner(
                        (int) (Math.random() * GRID_SIZE) + 1,
                        (int) (Math.random() * GRID_SIZE) + 1,
                        GRID_SIZE * GRID_SIZE);
            }
            updateRobotCount();
            updateCleanerCount();
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
    
    private void updateCleanerCount() {
        cleanerCountLabel.setText("Cleaners: " + robotManager.getCleanerCount());
    }
}
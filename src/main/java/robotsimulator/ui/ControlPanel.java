package robotsimulator.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import robotsimulator.cleaning.RobotCleaner;
import robotsimulator.core.RobotManager;
import static robotsimulator.model.GridConstants.GRID_SIZE;
import robotsimulator.model.Robot;
import robotsimulator.pollution.RobotPolluter;

/**
 * Builds and manages the control panel UI with grid and robot controls.
 */
public class ControlPanel {
    private final GridManager gridManager;
    private final RobotManager robotManager;
    
    // Status labels with vibrant colors
    private final Label counterLabel;
    private final Label robotCountLabel;
    private final Label polluterCountLabel;
    private final Label cleanerCountLabel;
    
    // Input fields
    private final TextField cellRowInput;
    private final TextField cellColInput;
    private final TextField robotRowInput;
    private final TextField robotColInput;
    
    // Selectors
    private final ComboBox<Robot> robotSelector;
    private final ComboBox<String> polluterTypeSelector;
    private final ComboBox<String> cleanerTypeSelector;
    
    // Parameter inputs
    private final TextField polluterParam1Input;
    private final TextField polluterParam2Input;
    private final TextField cleanerParam1Input;
    private final TextField cleanerParam2Input;

    // Color constants
    private static final String BG_COLOR = "#2C3E50"; // Dark blue-gray
    private static final String PANEL_BG = "#34495E"; // Slightly lighter blue-gray
    private static final String ACCENT_COLOR = "#3498DB"; // Bright blue
    private static final String DANGER_COLOR = "#E74C3C"; // Red
    private static final String SUCCESS_COLOR = "#2ECC71"; // Green
    private static final String WARNING_COLOR = "#F39C12"; // Orange
    private static final String TEXT_COLOR = "#ECF0F1"; // Light gray
    private static final String WH_COLOR = "#ffffff"; // Light gray
    // Font sizes
    private static final String TITLE_FONT = "-fx-font-size: 18px; -fx-font-weight: bold;";
    private static final String HEADER_FONT = "-fx-font-size: 14px; -fx-font-weight: bold;";
    private static final String NORMAL_FONT = "-fx-font-size: 13px;";
    private static final String IND = "-fx-font-size: 18px; ; -fx-font-weight: bold;";
    
    public ControlPanel(GridManager gridManager, RobotManager robotManager) {
        this.gridManager = gridManager;
        this.robotManager = robotManager;
        
        // Initialize labels with better styling
        this.counterLabel = createStyledLabel_StateIND("Dirty Cells: 0", WARNING_COLOR);
        this.robotCountLabel = createStyledLabel_StateIND("Robots: 0", ACCENT_COLOR);
        this.polluterCountLabel = createStyledLabel_StateIND("Polluters: 0", DANGER_COLOR);
        this.cleanerCountLabel = createStyledLabel_StateIND("Cleaners: 0", SUCCESS_COLOR);
        
        // Initialize input fields
        this.cellRowInput = createStyledTextField();
        this.cellColInput = createStyledTextField();
        this.robotRowInput = createStyledTextField();
        this.robotColInput = createStyledTextField();
        
        // Initialize selectors
        this.robotSelector = new ComboBox<>();
        this.polluterTypeSelector = new ComboBox<>();
        this.cleanerTypeSelector = new ComboBox<>();
        
        // Initialize parameter inputs
        this.polluterParam1Input = createStyledTextField();
        this.polluterParam2Input = createStyledTextField();
        this.cleanerParam1Input = createStyledTextField();
        this.cleanerParam2Input = createStyledTextField();
    }
    
    // private Label createStyledLabel(String text, String color) {
    //     Label label = new Label(text);
    //     label.setStyle(NORMAL_FONT + " -fx-text-fill: " + color + ";");
    //     return label;
    // }

    private Label createStyledLabel_StateIND(String text, String color) {
        Label label = new Label(text);
        label.setStyle(IND + " -fx-text-fill: " + color + ";");
        return label;
    }
    
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setStyle(HEADER_FONT + " -fx-text-fill: " + TEXT_COLOR + ";");
        return label;
    }
    
    private TextField createStyledTextField() {
        TextField field = new TextField();
        field.setStyle(NORMAL_FONT + 
            " -fx-background-color: #4A6572;" +
            " -fx-text-fill: " + TEXT_COLOR + ";" +
            " -fx-prompt-text-fill: #95A5A6;" +
            " -fx-border-color: #5D6D7E;" +
            " -fx-border-radius: 4;" +
            " -fx-background-radius: 4;");
        field.setPrefWidth(100);
        field.setMaxWidth(100);
        return field;
    }
    
    private Button createStyledButton(String text, String bgColor) {
        Button button = new Button(text);
        button.setStyle(NORMAL_FONT + 
            " -fx-background-color: " + bgColor + ";" +
            " -fx-text-fill: white;" +
            " -fx-font-weight: bold;" +
            " -fx-background-radius: 6;" +
            " -fx-padding: 8 15;" +
            " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + " -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle() + " -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
        return button;
    }
    
    private VBox createPanel(String title) {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: " + PANEL_BG + ";" +
                      " -fx-padding: 15;" +
                      " -fx-border-radius: 8;" +
                      " -fx-background-radius: 8;" +
                      " -fx-border-color: #4A6572;" +
                      " -fx-border-width: 2;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(TITLE_FONT + " -fx-text-fill: " + TEXT_COLOR + ";");
        
        panel.getChildren().add(titleLabel);
        return panel;
    }
    
    public VBox build() {
        // Main container
        VBox mainContainer = new VBox(15);
        mainContainer.setStyle("-fx-background-color: " + BG_COLOR + ";" +
                             " -fx-padding: 20;" +
                             " -fx-spacing: 15;");
        mainContainer.setAlignment(Pos.TOP_CENTER);
        
        // ========== STATUS PANEL ==========
        VBox statusPanel = createPanel("GAME STATUS");
        
        // Status grid
        GridPane statusGrid = new GridPane();
        statusGrid.setHgap(20);
        statusGrid.setVgap(10);
        statusGrid.setPadding(new Insets(10, 0, 0, 0));
        
        statusGrid.add(counterLabel, 0, 0);
        statusGrid.add(robotCountLabel, 1, 0);
        statusGrid.add(polluterCountLabel, 0, 1);
        statusGrid.add(cleanerCountLabel, 1, 1);
        
        // Reset button
        Button resetButton = createStyledButton("RESET ALL", DANGER_COLOR);
        resetButton.setStyle(resetButton.getStyle() + " -fx-font-size: 14px;");
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
        
        HBox resetBox = new HBox(resetButton);
        resetBox.setAlignment(Pos.CENTER);
        
        statusPanel.getChildren().addAll(statusGrid, resetBox);
        
        // ========== CELL CONTROL PANEL ==========
        VBox cellPanel = createPanel("CELL CONTROLS");
        
        HBox cellInputs = new HBox(10);
        cellInputs.setAlignment(Pos.CENTER_LEFT);
        
        configureCellInputFields();
        Button dirtyButton = createStyledButton("DIRTY", DANGER_COLOR);
        Button cleanButton = createStyledButton("CLEAN", SUCCESS_COLOR);
        
        dirtyButton.setOnAction(e -> handleDirtyAction());
        cleanButton.setOnAction(e -> handleCleanAction());
        
        cellInputs.getChildren().addAll(
            createHeaderLabel("Cell:"),
            cellRowInput,
            cellColInput,
            dirtyButton,
            cleanButton
        );
        
        cellPanel.getChildren().add(cellInputs);
        
        // ========== ROBOT CREATION PANEL ==========
        VBox robotCreationPanel = createPanel("ROBOT CREATION");
        
        // Standard Robot
        VBox standardRobotBox = new VBox(10);
        standardRobotBox.setPadding(new Insets(0, 0, 15, 0));
        
        HBox standardRobotInputs = new HBox(10);
        standardRobotInputs.setAlignment(Pos.CENTER_LEFT);
        
        configureRobotInputFields();
        Button clearRobotsButton = createStyledButton("🗑️ CLEAR ALL ROBOTS", "#95A5A6");
        
        clearRobotsButton.setOnAction(e -> {
            robotManager.clearAllRobots();
            updateRobotCount();
            updatePolluterCount();
            updateCleanerCount();
            updateRobotSelector();
        });
        
        standardRobotInputs.getChildren().addAll(
            robotRowInput,
            robotColInput,
            clearRobotsButton
        );
        
        standardRobotBox.getChildren().add(standardRobotInputs);
        
        // Polluter Creation
        VBox polluterBox = new VBox(10);
        polluterBox.setPadding(new Insets(0, 0, 15, 0));
        
        configurePolluterControls();
        Button createPolluterButton = createStyledButton("CREATE POLLUTER", DANGER_COLOR);
        Button startPolluterButton = createStyledButton("▶️ START POLLUTER", WARNING_COLOR);
        
        createPolluterButton.setOnAction(e -> handleCreatePolluter());
        startPolluterButton.setOnAction(e -> handleStartSelectedMission());
        
        HBox polluterTypeBox = new HBox(10);
        polluterTypeBox.setAlignment(Pos.CENTER_LEFT);
        polluterTypeBox.getChildren().addAll(
            createHeaderLabel("Polluter Type:"),
            polluterTypeSelector
        );
        
        HBox polluterParamBox = new HBox(10);
        polluterParamBox.setAlignment(Pos.CENTER_LEFT);
        polluterParamBox.getChildren().addAll(
            createHeaderLabel("Params:"),
            polluterParam1Input,
            polluterParam2Input
        );
        
        HBox polluterButtonBox = new HBox(10);
        polluterButtonBox.setAlignment(Pos.CENTER_LEFT);
        polluterButtonBox.getChildren().addAll(
            createPolluterButton,
            startPolluterButton
        );
        
        polluterBox.getChildren().addAll(polluterTypeBox, polluterParamBox, polluterButtonBox);
        
        // Cleaner Creation
        VBox cleanerBox = new VBox(10);
        
        configureCleanerControls();
        Button createCleanerButton = createStyledButton("CREATE CLEANER", SUCCESS_COLOR);
        Button startCleanerButton = createStyledButton("▶️ START CLEANER", SUCCESS_COLOR);
        
        createCleanerButton.setOnAction(e -> handleCreateCleaner());
        startCleanerButton.setOnAction(e -> handleStartSelectedCleaning());
        
        HBox cleanerTypeBox = new HBox(10);
        cleanerTypeBox.setAlignment(Pos.CENTER_LEFT);
        cleanerTypeBox.getChildren().addAll(
            createHeaderLabel("Cleaner Type:"),
            cleanerTypeSelector
        );
        
        HBox cleanerParamBox = new HBox(10);
        cleanerParamBox.setAlignment(Pos.CENTER_LEFT);
        cleanerParamBox.getChildren().addAll(
            createHeaderLabel("Params:"),
            cleanerParam1Input,
            cleanerParam2Input
        );
        
        HBox cleanerButtonBox = new HBox(10);
        cleanerButtonBox.setAlignment(Pos.CENTER_LEFT);
        cleanerButtonBox.getChildren().addAll(
            createCleanerButton,
            startCleanerButton
        );
        
        cleanerBox.getChildren().addAll(cleanerTypeBox, cleanerParamBox, cleanerButtonBox);
        
        robotCreationPanel.getChildren().addAll(standardRobotBox, polluterBox, cleanerBox);
        
        // ========== ROBOT CONTROL PANEL ==========
        VBox robotControlPanel = createPanel("ROBOT CONTROLS");
        
        // Robot Selection
        VBox selectionBox = new VBox(5);
        selectionBox.setPadding(new Insets(0, 0, 15, 0));
        
        configureRobotSelector();
        Button moveToButton = createStyledButton("Go TO", ACCENT_COLOR);
        Button removeRobotButton = createStyledButton("DEL robot", DANGER_COLOR);
        
        moveToButton.setOnAction(e -> handleMoveToPosition());
        removeRobotButton.setOnAction(e -> handleRemoveSelectedRobot());
        
        HBox robotSelectBox = new HBox(10);
        robotSelectBox.setAlignment(Pos.CENTER_LEFT);
        robotSelectBox.getChildren().addAll(
            createHeaderLabel("Select Robot:"),
            robotSelector
        );
        
        HBox robotActionBox = new HBox(10);
        robotActionBox.setAlignment(Pos.CENTER_LEFT);
        robotActionBox.getChildren().addAll(
            createHeaderLabel("Pose:"),
            robotRowInput,
            robotColInput,
            moveToButton,
            removeRobotButton
        );
        
        selectionBox.getChildren().addAll(robotSelectBox, robotActionBox);
        
        // Direction Controls
        VBox directionBox = new VBox(3);
        VBox directionBox_wout_exp = new VBox(3);
        Button upButton = createStyledButton("▲", ACCENT_COLOR);
        Button downButton = createStyledButton("▼", ACCENT_COLOR);
        Button leftButton = createStyledButton("◀", ACCENT_COLOR);
        Button rightButton = createStyledButton("▶", ACCENT_COLOR);
        
        upButton.setStyle(upButton.getStyle() + " -fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 50px;");
        downButton.setStyle(downButton.getStyle() + " -fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 50px;");
        leftButton.setStyle(leftButton.getStyle() + " -fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 50px;");
        rightButton.setStyle(rightButton.getStyle() + " -fx-font-size: 16px; -fx-min-width: 50px; -fx-min-height: 50px;");
        
        upButton.setOnAction(e -> moveSelectedRobot("UP"));
        downButton.setOnAction(e -> moveSelectedRobot("DOWN"));
        leftButton.setOnAction(e -> moveSelectedRobot("LEFT"));
        rightButton.setOnAction(e -> moveSelectedRobot("RIGHT"));
        
        HBox directionRow1 = new HBox(10);
        directionRow1.setAlignment(Pos.CENTER);
        directionRow1.getChildren().add(upButton);
        
        HBox directionRow2 = new HBox(10);
        directionRow2.setAlignment(Pos.CENTER);
        directionRow2.getChildren().addAll(leftButton, downButton, rightButton);
        
        directionBox_wout_exp.getChildren().addAll(
            directionRow1,
            directionRow2
        );

        directionBox.getChildren().addAll(
            createHeaderLabel("Move Robot:"),
            directionBox_wout_exp
        );
        
        

        robotControlPanel.getChildren().addAll(selectionBox, directionBox);
        
        // ========== ASSEMBLE MAIN CONTAINER ==========
        mainContainer.getChildren().addAll(
            statusPanel,
            cellPanel,
            robotCreationPanel,
            robotControlPanel
        );
        
        return mainContainer;
    }
    
    private void configureCellInputFields() {
        cellRowInput.setPromptText("Row (1-" + GRID_SIZE + ")");
        cellColInput.setPromptText("Col (1-" + GRID_SIZE + ")");
    }
    
    private void configureRobotInputFields() {
        robotRowInput.setPromptText("Row (1-" + GRID_SIZE + ")");
        robotColInput.setPromptText("Col (1-" + GRID_SIZE + ")");
    }
    
    private void configurePolluterControls() {
        polluterTypeSelector.getItems().addAll("Straight Line", "Jumping", "Free Movement");
        polluterTypeSelector.setPromptText("Select Type");
        polluterTypeSelector.setPrefWidth(150);
        polluterTypeSelector.setStyle(NORMAL_FONT + 
            " -fx-background-color: #ffffff;" +
            " -fx-text-fill: " + WH_COLOR + ";" +
            " -fx-prompt-text-fill: #ffffff;");
        
        polluterTypeSelector.setOnAction(e -> updatePolluterParameterHints());
    }
    
    private void configureCleanerControls() {
        cleanerTypeSelector.getItems().addAll("Straight Line", "Jumping", "Free Movement", "Complete Grid", "Smart Cleaner");
        cleanerTypeSelector.setPromptText("Select Type");
        cleanerTypeSelector.setPrefWidth(150);
        cleanerTypeSelector.setStyle(NORMAL_FONT + 
            " -fx-background-color: #ffffff;" +
            " -fx-text-fill: #ffffff;" +
            " -fx-prompt-text-fill: #ffffff;");
        
        cleanerTypeSelector.setOnAction(e -> updateCleanerParameterHints());
    }
    
    private void configureRobotSelector() {
        robotSelector.setPromptText("Select a Robot");
        robotSelector.setPrefWidth(250);
        robotSelector.setStyle(NORMAL_FONT + 
            " -fx-background-color: #ffffff;" +
            " -fx-text-fill: " + TEXT_COLOR + ";" +
            " -fx-prompt-text-fill: #000000;");
        
        robotSelector.setCellFactory(lv -> new javafx.scene.control.ListCell<Robot>() {
            @Override
            protected void updateItem(Robot robot, boolean empty) {
                super.updateItem(robot, empty);
                if (empty || robot == null) {
                    setText(null);
                } else {
                    setText(robot.toString());
                    if (robot instanceof RobotPolluter) {
                        setTextFill(javafx.scene.paint.Color.web(DANGER_COLOR));
                    } else if (robot instanceof RobotCleaner) {
                        setTextFill(javafx.scene.paint.Color.web(SUCCESS_COLOR));
                    } else {
                        setTextFill(javafx.scene.paint.Color.web(ACCENT_COLOR));
                    }
                }
            }
        });
        
        robotSelector.setButtonCell(new javafx.scene.control.ListCell<Robot>() {
            @Override
            protected void updateItem(Robot robot, boolean empty) {
                super.updateItem(robot, empty);
                if (empty || robot == null) {
                    setText(null);
                } else {
                    setText(robot.toString());
                }
            }
        });
    }
    
    private void updatePolluterParameterHints() {
        String selectedType = polluterTypeSelector.getValue();
        if (selectedType == null) return;
        
        switch (selectedType) {
            case "Straight Line" -> {
                polluterParam1Input.setPromptText("Start Col");
                polluterParam2Input.setPromptText("(not used)");
                polluterParam2Input.setDisable(true);
            }
            case "Jumping" -> {
                polluterParam1Input.setPromptText("Start Row");
                polluterParam2Input.setPromptText("Start Col");
                polluterParam2Input.setDisable(false);
            }
            case "Free Movement" -> {
                polluterParam1Input.setPromptText("Start Row");
                polluterParam2Input.setPromptText("Start Col");
                polluterParam2Input.setDisable(false);
            }
        }
    }
    
    private void updateCleanerParameterHints() {
        String selectedType = cleanerTypeSelector.getValue();
        if (selectedType == null) return;
        
        switch (selectedType) {
            case "Straight Line" -> {
                cleanerParam1Input.setPromptText("Start Col");
                cleanerParam2Input.setPromptText("(not used)");
                cleanerParam2Input.setDisable(true);
            }
            case "Jumping" -> {
                cleanerParam1Input.setPromptText("Start Row");
                cleanerParam2Input.setPromptText("Start Col");
                cleanerParam2Input.setDisable(false);
            }
            case "Free Movement" -> {
                cleanerParam1Input.setPromptText("Start Row");
                cleanerParam2Input.setPromptText("Start Col");
                cleanerParam2Input.setDisable(false);
            }
            case "Complete Grid" -> {
                cleanerParam1Input.setPromptText("(auto)");
                cleanerParam2Input.setPromptText("(auto)");
                cleanerParam1Input.setDisable(true);
                cleanerParam2Input.setDisable(true);
            }
            case "Smart Cleaner" -> {
                cleanerParam1Input.setPromptText("Start Row");
                cleanerParam2Input.setPromptText("Start Col");
                cleanerParam1Input.setDisable(false);
                cleanerParam2Input.setDisable(false);
            }
        }
    }
    
    // All the handler methods remain the same as before...
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
                    int maxSteps = 10;
                    createdCleaner = robotManager.createFreeCleaner(freeRow, freeCol, maxSteps);
                }
                case "Complete Grid" -> createdCleaner = robotManager.createCompleteCleaner();
                case "Smart Cleaner" -> {
                    int smartRow = cleanerParam1Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam1Input.getText());
                    int smartCol = cleanerParam2Input.getText().isEmpty() ?
                            (int) (Math.random() * GRID_SIZE) + 1 :
                            Integer.parseInt(cleanerParam2Input.getText());
                    smartRow = Math.max(1, Math.min(smartRow, GRID_SIZE));
                    smartCol = Math.max(1, Math.min(smartCol, GRID_SIZE));
                    int maxCleaningSteps = GRID_SIZE * GRID_SIZE;
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
                case "Straight Line" -> robotManager.createStraightCleaner((int) (Math.random() * GRID_SIZE) + 1);
                case "Jumping" -> robotManager.createJumpingCleaner((int) (Math.random() * GRID_SIZE) + 1,
                    (int) (Math.random() * GRID_SIZE) + 1, 2);
                case "Free Movement" -> robotManager.createFreeCleaner((int) (Math.random() * GRID_SIZE) + 1,
                    (int) (Math.random() * GRID_SIZE) + 1, GRID_SIZE * GRID_SIZE);
                case "Complete Grid" -> robotManager.createCompleteCleaner();
                case "Smart Cleaner" -> robotManager.createSmartCleaner((int) (Math.random() * GRID_SIZE) + 1,
                    (int) (Math.random() * GRID_SIZE) + 1, GRID_SIZE * GRID_SIZE);
            }
            updateRobotCount();
            updateCleanerCount();
            updateRobotSelector();
        }
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
        counterLabel.setText("Dirty Cells: " + count);
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
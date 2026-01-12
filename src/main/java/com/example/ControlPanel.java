package com.example;

import static com.example.GridConstants.GRID_SIZE;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Builds and manages the control panel UI.
 */
public class ControlPanel {
    private final GridManager gridManager;
    private final Label counterLabel;
    private final TextField rowInput;
    private final TextField colInput;

    public ControlPanel(GridManager gridManager) {
        this.gridManager = gridManager;
        this.counterLabel = new Label("Colored cells: 0");
        this.rowInput = new TextField();
        this.colInput = new TextField();
    }

    public VBox build() {
        configureCounterLabel();
        Button resetButton = createResetButton();
        configureInputFields();
        Button dirtyButton = createDirtyButton();
        Button cleanButton = createCleanButton();

        HBox controlRow1 = new HBox(20, counterLabel, resetButton);
        controlRow1.setStyle("-fx-alignment: center; -fx-padding: 10;");

        HBox controlRow2 = new HBox(10, rowInput, colInput, dirtyButton, cleanButton);
        controlRow2.setStyle("-fx-alignment: center; -fx-padding: 10;");

        return new VBox(5, controlRow1, controlRow2);
    }

    private void configureCounterLabel() {
        counterLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
    }

    private Button createResetButton() {
        Button resetButton = new Button("Reset Grid");
        resetButton.setStyle("-fx-font-size: 16px;");
        resetButton.setOnAction(e -> gridManager.resetGrid());
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

    public void updateCounter(int count) {
        counterLabel.setText("Colored cells: " + count);
    }
}
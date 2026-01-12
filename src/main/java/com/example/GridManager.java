package com.example;

import static com.example.GridConstants.CELL_SIZE;
import static com.example.GridConstants.CLICKED_COLOR;
import static com.example.GridConstants.DEFAULT_COLOR;
import static com.example.GridConstants.DIRTY_COLOR;
import static com.example.GridConstants.GRID_SIZE;
import static com.example.GridConstants.HOVER_COLOR;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Manages the grid state and operations.
 */
public class GridManager {
    private final CellData[][] cells;
    private int coloredCount;
    private GridStateListener listener;

    public GridManager() {
        this.cells = new CellData[GRID_SIZE][GRID_SIZE];
        this.coloredCount = 0;
    }

    public void setListener(GridStateListener listener) {
        this.listener = listener;
    }

    public int getColoredCount() {
        return coloredCount;
    }

    public GridPane buildGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-padding: 20;");

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane cell = createCell(row, col);
                gridPane.add(cell, col, row);
            }
        }

        return gridPane;
    }

    private StackPane createCell(int row, int col) {
        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
        rect.setFill(DEFAULT_COLOR);
        rect.setStroke(Color.GRAY);
        rect.setStrokeWidth(1);

        CellData cellData = new CellData(rect);
        cells[row][col] = cellData;

        StackPane cell = new StackPane(rect);

        cell.setOnMouseClicked(e -> handleCellClick(cellData));
        cell.setOnMouseEntered(e -> handleCellHover(cellData, true));
        cell.setOnMouseExited(e -> handleCellHover(cellData, false));

        return cell;
    }

    private void handleCellClick(CellData cellData) {
        if (!cellData.isColored()) {
            cellData.setFill(CLICKED_COLOR);
            cellData.setColored(true);
            coloredCount++;
            notifyStateChanged();
        }
    }

    private void handleCellHover(CellData cellData, boolean entering) {
        if (!cellData.isColored()) {
            cellData.setFill(entering ? HOVER_COLOR : DEFAULT_COLOR);
        }
    }

    public void resetGrid() {
        coloredCount = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                CellData cellData = cells[row][col];
                cellData.setFill(DEFAULT_COLOR);
                cellData.setColored(false);
            }
        }
        notifyStateChanged();
    }

    public void dirtyCell(int row, int col) {
        row--;
        col--;
        if (!isValidPosition(row, col)) return;

        CellData cellData = cells[row][col];
        cellData.setFill(DIRTY_COLOR);
        if (!cellData.isColored()) {
            cellData.setColored(true);
            coloredCount++;
        }
        notifyStateChanged();
    }

    public void cleanCell(int row, int col) {
        row--;
        col--;
        if (!isValidPosition(row, col)) return;

        CellData cellData = cells[row][col];
        if (cellData.isColored()) {
            cellData.setFill(DEFAULT_COLOR);
            cellData.setColored(false);
            coloredCount--;
            notifyStateChanged();
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    private void notifyStateChanged() {
        if (listener != null) {
            listener.onStateChanged(coloredCount);
        }
    }

    public interface GridStateListener {
        void onStateChanged(int coloredCount);
    }
}
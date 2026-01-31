package com.example.ui;

import com.example.model.CellData;
import static com.example.model.GridConstants.CELL_SIZE;
import static com.example.model.GridConstants.CELL_STROKE;
import static com.example.model.GridConstants.CLICKED_COLOR;
import static com.example.model.GridConstants.DEFAULT_COLOR;
import static com.example.model.GridConstants.DIRTY_COLOR;
import static com.example.model.GridConstants.GRID_PADDING;
import static com.example.model.GridConstants.GRID_SIZE;
import static com.example.model.GridConstants.HOVER_COLOR;

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
        gridPane.setStyle("-fx-padding: " + GRID_PADDING + ";");
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
        rect.setStrokeWidth(CELL_STROKE);

        CellData cellData = new CellData(rect);
        cells[row][col] = cellData;

        StackPane cell = new StackPane(rect);

        cell.setOnMouseClicked(e -> handleCellClick(cellData));
        cell.setOnMouseEntered(e -> handleCellHover(cellData, true));
        cell.setOnMouseExited(e -> handleCellHover(cellData, false));

        return cell;
    }

    private void handleCellClick(CellData cellData) {
        if (cellData.isColored()) {
            cellData.setFill(CLICKED_COLOR);
            if (coloredCount != 0){
                coloredCount--;
                notifyStateChanged();
            }    
            cellData.setColored(false);      
        } 
        else {
            cellData.setFill(CLICKED_COLOR);
            if (cellData.getFill() != CLICKED_COLOR){
                coloredCount--;
            }
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
    
    /**
     * Check if a cell is dirty/colored
     * @param row 1-based row index
     * @param col 1-based column index
     * @return true if the cell is dirty/colored, false otherwise
     */
    public boolean isCellDirty(int row, int col) {
        row--;
        col--;
        if (!isValidPosition(row, col)) {
            return false;
        }
        return cells[row][col].isColored();
    }
    
    /**
     * Check if a cell is dirty/colored (0-based version)
     * @param row 0-based row index
     * @param col 0-based column index
     * @return true if the cell is dirty/colored, false otherwise
     */
    public boolean isCellDirtyZeroBased(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }
        return cells[row][col].isColored();
    }
    
    /**
     * Get the color of a specific cell
     * @param row 1-based row index
     * @param col 1-based column index
     * @return the color of the cell, or null if position is invalid
     */
    public Color getCellColor(int row, int col) {
        row--;
        col--;
        if (!isValidPosition(row, col)) {
            return null;
        }
        return (Color) cells[row][col].getFill();
    }
    
    /**
     * Get all dirty cells positions (0-based)
     * @return list of [row, col] arrays for dirty cells
     */
    public int[][] getDirtyCells() {
        int[][] dirtyCells = new int[coloredCount][2];
        int index = 0;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (cells[row][col].isColored()) {
                    dirtyCells[index][0] = row;
                    dirtyCells[index][1] = col;
                    index++;
                }
            }
        }
        
        return dirtyCells;
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
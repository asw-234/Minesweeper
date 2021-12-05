package uni.minesweeper.model;

import android.util.Log;
import java.util.ArrayList;

public class MinesweeperModel {
    private static MinesweeperModel singletonInstance = null;
    private int boardSize;
    private int totalMines;
    private boolean isFlagMode = false;

    public enum ETileType {SAFE, SAFE_CHECKED, BOMB, FLAG, BOMB_LOSS, FLAG_LOSS}

    private ArrayList<ArrayList<ETileType>> model = null;
    private ArrayList<ArrayList<Integer>> modelBombIndicators = null;


    private MinesweeperModel() {}

    public static MinesweeperModel getSingletonInstance() {
        if (singletonInstance == null)
            singletonInstance = new MinesweeperModel();

        return singletonInstance;
    }

    public void resetModel() {
        if (boardSize == 0 || totalMines == 0) {
            Log.w("DBG_WARNING", "Tried to create model with " +
                ((boardSize == 0) ? "boardSize == 0" : "totalMines == 0")
            );

            return;
        }

        isFlagMode = false;
        model = new ArrayList<>();
        modelBombIndicators = new ArrayList<>();

        // Create board
        for (int i = 0; i < boardSize; ++i) {
            ArrayList<ETileType> row = new ArrayList<>();
            ArrayList<Integer> bombRow = new ArrayList<>();

            for (int j = 0; j < boardSize; ++j) {
                row.add(ETileType.SAFE);
                bombRow.add(0);
            }

            model.add(row);
            modelBombIndicators.add(bombRow);
        }

        // Add mines
        int mines = totalMines;

        while (mines > 0) {
            int randomRow = (int) (model.size() * Math.random());
            int randomCol = (int) (model.size() * Math.random());

            if (model.get(randomRow).get(randomCol) != ETileType.BOMB) {
                model.get(randomRow).set(randomCol, ETileType.BOMB);
                --mines;
            }
        }

        for (int i = 0; i < modelBombIndicators.size(); ++i) {
            for (int j = 0; j < modelBombIndicators.size(); ++j) {
                calculateBombIndicators(i, j);
            }
        }
    }

    private void calculateBombIndicators(int row, int col) {
        Integer bombIndicator = 0;

        for (int i = row - 1; i <= row + 1; ++i) {
            if (i < 0 || i >= modelBombIndicators.size())
                continue;

            for (int j = col - 1; j <= col + 1; ++j) {
                if (j < 0 || j >= modelBombIndicators.size())
                    continue;

                if (model.get(i).get(j) == ETileType.BOMB)
                    bombIndicator++;
            }
        }

        modelBombIndicators.get(row).set(col, bombIndicator);
    }

    public int getNearbyBombs(int row, int col) {
        return modelBombIndicators.get(row).get(col);
    }


    public void setTile(int row, int col, ETileType type) {
        model.get(row).set(col, type);
    }

    public ETileType getTile(int row, int col) {
        return model.get(row).get(col);
    }

    public void setSize(int size) {
        boardSize = size;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setTotalMines(int mines) {
        totalMines = mines;
    }

    public int getTotalMines() {
        return totalMines;
    }

    public boolean isFlagMode() {
        return isFlagMode;
    }

    public void setFlagMode(boolean isFlag) {
        isFlagMode = isFlag;
    }
}

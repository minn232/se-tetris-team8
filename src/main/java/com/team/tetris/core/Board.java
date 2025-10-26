package com.team.tetris.core;

import java.util.List;

public class Board {
    public static final int ROWS = 20;
    public static final int COLS = 10;
    private static final char EMPTY = ' ';

    private final char[][] grid = new char[ROWS][COLS];

    public Board() {
        initClear(); // 생성자에서는 private 메서드만 호출(경고 방지)
    }

    private void initClear() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = EMPTY;
            }
        }
    }

    public void clear() {
        initClear();
    }

    public boolean inBounds(int r, int c) {
        return 0 <= r && r < ROWS && 0 <= c && c < COLS;
    }

    public boolean isEmpty(int r, int c) {
        return inBounds(r, c) && grid[r][c] == EMPTY;
    }

    public void setCell(int r, int c, char ch) {
        if (inBounds(r, c)) grid[r][c] = ch;
    }

    public void clearCell(int r, int c) {
        if (inBounds(r, c)) grid[r][c] = EMPTY;
    }

    public char getCell(int r, int c) {
        if (!inBounds(r, c)) return 'X';
        return grid[r][c];
    }

    public boolean canPlace(List<Position> cells) {
        for (Position p : cells) {
            if (!inBounds(p.y, p.x) || !isEmpty(p.y, p.x)) return false;
        }
        return true;
    }

    public void place(List<Position> cells, char ch) {
        for (Position p : cells) setCell(p.y, p.x, ch);
    }

    public int clearFullLines() {
        int cleared = 0;
        for (int r = ROWS - 1; r >= 0; r--) {
            if (isFullRow(r)) {
                dropAbove(r);
                cleared++;
                r++;
            }
        }
        return cleared;
    }

    private boolean isFullRow(int r) {
        for (int c = 0; c < COLS; c++) {
            if (grid[r][c] == EMPTY) return false;
        }
        return true;
    }

    private void dropAbove(int row) {
        for (int r = row; r > 0; r--) {
            System.arraycopy(grid[r - 1], 0, grid[r], 0, COLS); // 수동 복사 경고 제거
        }
        for (int c = 0; c < COLS; c++) grid[0][c] = EMPTY;
    }
}
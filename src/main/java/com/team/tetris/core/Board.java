package com.team.tetris.core;

import java.util.Random;

public class Board {
    public static final int ROWS = 20;
    public static final int COLS = 10;

    private final ShapeType[][] grid;
    private final Random random = new Random();
    private final double[] weights = new double[ShapeType.values().length];

    private final Difficulty difficulty;

    private Tetromino current;
    private ShapeType nextShape;

    private boolean gameOver = false;

    // 점수/가속
    private int score = 0;
    private double scoreMultiplier = 1.0; // 난이도 보너스(클리어/보너스에만 적용)
    private int totalLinesCleared = 0;    // 누적 삭제 줄 수(속도 가속에 사용)

    private final boolean isItemMode;

    // 애니메이션용: 이번에 삭제할 줄
    private int[] pendingClearRows = null;

    public Board(Difficulty difficulty, boolean isItemMode) {
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        this.grid = new ShapeType[ROWS][COLS];
        setWeightsByDifficulty();
        setScoreMultiplier();
        spawnNewTetromino();
    }

    // ===== 난이도별 블록 가중치 =====
    private void setWeightsByDifficulty() {
        for (int i = 0; i < weights.length; i++) weights[i] = 1.0;
        switch (difficulty) {
            case EASY   -> weights[ShapeType.I.ordinal()] = 1.2; // +20%
            case NORMAL -> weights[ShapeType.I.ordinal()] = 1.0;
            case HARD   -> weights[ShapeType.I.ordinal()] = 0.8; // -20%
        }
    }

    // ===== 난이도별 점수 배율 =====
    private void setScoreMultiplier() {
        switch (difficulty) {
            case EASY -> scoreMultiplier = 0.9;
            case NORMAL -> scoreMultiplier = 1.0;
            case HARD   -> scoreMultiplier = 1.1;
        }
    }

    // ===== RWS (stochastic acceptance) =====
    private ShapeType pickByRoulette() {
        double max = 0;
        for (double w : weights) if (w > max) max = w;
        if (max <= 0) max = 1.0;

        while (true) {
            int i = random.nextInt(weights.length);
            if (random.nextDouble() < (weights[i] / max))
                return ShapeType.values()[i];
        }
    }

    // ===== 스폰/오버 =====
    private void spawnNewTetromino() {
        if (gameOver) return;
        ShapeType shape = (nextShape != null) ? nextShape : pickByRoulette();
        current = new Tetromino(shape, COLS / 2 - 2, 0);
        nextShape = pickByRoulette();

        if (!canMove(current, 0, 0)) {
            gameOver = true;
        }
    }

    // ===== 이동/회전 =====
    public void moveLeft()  { if (!gameOver && canMove(current, -1, 0)) current.move(-1, 0); }
    public void moveRight() { if (!gameOver && canMove(current,  1, 0)) current.move( 1, 0); }

    /** 한 칸 하강 (자동/수동 동일) */
    public boolean moveDown() {
        if (gameOver) return false;

        if (canMove(current, 0, 1)) {
            current.move(0, 1);
            addBaseScore(10); // 한 칸 떨어질 때마다 +10 (난이도 무관)
            return true;
        } else {
            // 고정 → 삭제 줄 예약 (애니메이션용)
            fixToBoard();
            pendingClearRows = findFullRows();
            if (pendingClearRows == null || pendingClearRows.length == 0) {
                spawnNewTetromino();
            }
            return false;
        }
    }

    /** 즉시 낙하 */
    public void hardDrop() {
        if (gameOver) return;
        int dropDist = 0;
        while (canMove(current, 0, 1)) {
            current.move(0, 1);
            dropDist++;
        }
        addBaseScore(dropDist * 10);
        moveDown();
    }

    public void rotate() {
        if (gameOver) return;
        Tetromino r = current.getRotatedCopy();
        if (canMove(r, 0, 0)) current.rotate();
    }

    // ===== 점수 =====
    private void addBaseScore(int base) { score += base; }
    private void addBonusScore(int base) { score += Math.round(base * scoreMultiplier); }

    // ===== 충돌/고정/라인 =====
    private boolean canMove(Tetromino t, int dx, int dy) {
        for (Position p : t.getBlocks()) {
            int x = t.getX() + p.x + dx;
            int y = t.getY() + p.y + dy;
            if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return false;
            if (grid[y][x] != null) return false;
        }
        return true;
    }

    private void fixToBoard() {
        for (Position p : current.getBlocks()) {
            int x = current.getX() + p.x;
            int y = current.getY() + p.y;
            if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                grid[y][x] = current.getShape();
            }
        }
    }

    // (1단계) 가득 찬 줄 모두 찾기
    private int[] findFullRows() {
        java.util.ArrayList<Integer> rows = new java.util.ArrayList<>();
        for (int y = 0; y < ROWS; y++) {
            boolean full = true;
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == null) { full = false; break; }
            }
            if (full) rows.add(y);
        }
        if (rows.isEmpty()) return null;
        return rows.stream().mapToInt(i -> i).toArray();
    }

    // (2단계) 실제 삭제 — ★ 재구성 방식으로 한 번에 처리 (4줄도 OK)
    public void clearRows(int[] rows) {
        if (rows == null || rows.length == 0) return;

        // 지울 줄 마스크
        boolean[] clear = new boolean[ROWS];
        for (int r : rows) {
            if (r >= 0 && r < ROWS) clear[r] = true;
        }

        // 아래에서 위로 읽으며, 살아있는 줄을 아래쪽으로 쌓기
        int write = ROWS - 1;
        for (int read = ROWS - 1; read >= 0; read--) {
            if (clear[read]) continue; // 지울 줄이면 건너뜀
            if (write != read) {
                System.arraycopy(grid[read], 0, grid[write], 0, COLS);
            }
            write--;
        }

        // 위쪽 남은 칸들 전부 비우기
        for (int y = write; y >= 0; y--) {
            for (int x = 0; x < COLS; x++) grid[y][x] = null;
        }

        addBonusScore(1000 * rows.length);
        totalLinesCleared += rows.length;
        pendingClearRows = null;
        spawnNewTetromino();
    }

    // GamePanel이 1회용으로 읽는 삭제 예정 줄
    public int[] pollClearingRows() {
        int[] out = pendingClearRows;
        pendingClearRows = null;
        return out;
    }

    // ===== 게터/리셋 =====
    public boolean isGameOver()          { return gameOver; }
    public int getScore()                { return score; }
    public int getTotalLinesCleared()    { return totalLinesCleared; }
    public Difficulty getDifficulty()    { return difficulty; }
    public ShapeType[][] getGrid()       { return grid; }
    public Tetromino getCurrent()        { return current; }
    public ShapeType getNextShape()      { return nextShape; }

    public void reset() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) grid[y][x] = null;
        }
        score = 0;
        totalLinesCleared = 0;
        gameOver = false;
        pendingClearRows = null;
        nextShape = pickByRoulette();
        spawnNewTetromino();
    }
}
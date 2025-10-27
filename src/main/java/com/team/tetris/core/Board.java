package com.team.tetris.core;

import java.util.Random;

public class Board {
    public static final int ROWS = 20;
    public static final int COLS = 10;

    private final ShapeType[][] grid = new ShapeType[ROWS][COLS];
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

    public Board(Difficulty difficulty) {
        this.difficulty = difficulty;
        setWeightsByDifficulty();
        setScoreMultiplier();
        this.nextShape = pickByRoulette();
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

    // ===== 난이도별 점수 배율(클리어/보너스 전용) =====
    private void setScoreMultiplier() {
        switch (difficulty) {
            case EASY, NORMAL -> scoreMultiplier = 1.0;
            case HARD         -> scoreMultiplier = 1.1;
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
            addBaseScore(10);     // 한 칸 떨어질 때마다 +10 (난이도 무관)
            return true;
        } else {
            // 고정 → 라인 삭제 → 점수/가속 반영 → 다음 스폰
            fixToBoard();
            int lines = clearFullLines();
            if (lines > 0) {
                addBonusScore(1000 * lines); // n줄 동시 삭제 시 1000*n, 난이도 배율 적용
                totalLinesCleared += lines;   // 누적 카운트 (속도 가속용)
            }
            spawnNewTetromino();
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
        addBaseScore(dropDist * 10); // 떨어진 칸 수 × 10 (난이도 배율 미적용)
        moveDown();                  // 고정/클리어/스폰 처리
    }

    public void rotate() {
        if (gameOver) return;
        Tetromino r = current.getRotatedCopy();
        if (canMove(r, 0, 0)) current.rotate();
    }

    // ===== 점수 =====
    private void addBaseScore(int base) {
        score += base; // 낙하 기본점은 난이도 배율 미적용
    }

    private void addBonusScore(int base) {
        score += Math.round(base * scoreMultiplier); // 클리어/보너스만 배율
    }

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

    private int clearFullLines() {
        int cleared = 0;
        for (int y = ROWS - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == null) { full = false; break; }
            }
            if (full) {
                cleared++;
                removeLine(y);
                y++; // 위에서 내려온 줄 재검사
            }
        }
        return cleared;
    }

    private void removeLine(int line) {
        for (int y = line; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, COLS);
        }
        for (int x = 0; x < COLS; x++) grid[0][x] = null;
    }

    // ===== 게임 상태/게터 =====
    public boolean isGameOver()          { return gameOver; }
    public int getScore()                { return score; }
    public int getTotalLinesCleared()    { return totalLinesCleared; }
    public Difficulty getDifficulty()    { return difficulty; }
    public ShapeType[][] getGrid()       { return grid; }
    public Tetromino getCurrent()        { return current; }
    public ShapeType getNextShape()      { return nextShape; }

    // 리셋(재시작용)
    public void reset() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) grid[y][x] = null;
        }
        score = 0;
        totalLinesCleared = 0;
        gameOver = false;
        nextShape = pickByRoulette();
        spawnNewTetromino();
    }
}

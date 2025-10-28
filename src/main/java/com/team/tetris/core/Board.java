package com.team.tetris.core;

import java.util.Random;

public class Board {
    public static final int ROWS = 20;
    public static final int COLS = 10;

    private final ShapeType[][] grid = new ShapeType[ROWS][COLS];
    private final Random random = new Random();
    private final double[] weights = new double[ShapeType.values().length];

    private final Difficulty difficulty;

    private Object current; // Tetromino 또는 WeightBlock
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
        
        if (!canMoveCurrent(0, 0)) {
            gameOver = true;
        }
    }

    // current가 Tetromino, WeightBlock, BombBlock일 때 이동 가능 여부 체크
    private boolean canMoveCurrent(int dx, int dy) {
        if (current instanceof Tetromino t) {
            return canMove(t, dx, dy);
        } else if (current instanceof ItemBlock item) {
            // WeightBlock과 BombBlock 모두 처리
            for (Position p : item.getBlocks()) {
                int x = item.getX() + p.x + dx;
                int y = item.getY() + p.y + dy;
                if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return false;
                if (grid[y][x] != null) return false;
            }
            return true;
        }
        return false;
    }

    // ===== 이동/회전 =====
    public void moveLeft() {
        if (gameOver) return;
        if (current instanceof ItemBlock item) {
            item.moveLeft(this);
        } else if (current instanceof Tetromino t) {
            if (canMove(t, -1, 0)) t.move(-1, 0);
        }
    }

    public void moveRight() {
        if (gameOver) return;
        if (current instanceof ItemBlock item) {
            item.moveRight(this);
        } else if (current instanceof Tetromino t) {
            if (canMove(t, 1, 0)) t.move(1, 0);
        }
    }

    /** 한 칸 하강 (자동/수동 동일) */
    public boolean moveDown() {
        if (gameOver) return false;
        
        if (current instanceof WeightBlock w) {
            boolean reachedBottom = w.moveDown(this);
            if (reachedBottom) {
                spawnNewTetromino();
            }
            return !reachedBottom;
        } else if (current instanceof BombBlock b) {
            boolean reachedBottom = b.moveDown(this);
            if (reachedBottom) {
                // 폭탄 폭발 후 새 블록 스폰
                b.explode(this);
                spawnNewTetromino();
            }
            return !reachedBottom;
        } else if (current instanceof Tetromino t) {
            if (canMove(t, 0, 1)) {
                t.move(0, 1);
                addBaseScore(10);
                return true;
            } else {
                fixToBoard();
                int lines = clearFullLines();
                if (lines > 0) {
                    addBonusScore(1000 * lines);
                    totalLinesCleared += lines;
                }
                spawnNewTetromino();
                return false;
            }
        }
        return false;
    }

    /** 즉시 낙하 */
    public void hardDrop() {
        if (gameOver) return;
        
        if (current instanceof WeightBlock w) {
            boolean reachedBottom = false;
            while (!reachedBottom) {
                reachedBottom = w.moveDown(this);
            }
            spawnNewTetromino();
        } else if (current instanceof BombBlock b) {
            boolean reachedBottom = false;
            while (!reachedBottom) {
                reachedBottom = b.moveDown(this);
            }
            b.explode(this);
            spawnNewTetromino();
        } else if (current instanceof Tetromino t) {
            int dropDist = 0;
            while (canMove(t, 0, 1)) {
                t.move(0, 1);
                dropDist++;
            }
            addBaseScore(dropDist * 10);
            moveDown();
        }
    }

    public void rotate() {
        if (gameOver || current == null) return;
        
        if (current instanceof BombBlock b) {
            // BombBlock은 회전 가능 - 회전 후 충돌 체크
            b.rotate();
            
            // 회전 후 충돌 체크
            boolean canRotate = true;
            for (Position p : b.getBlocks()) {
                int px = b.getX() + p.x;
                int py = b.getY() + p.y;
                if (px < 0 || px >= COLS || py < 0 || py >= ROWS || grid[py][px] != null) {
                    canRotate = false;
                    break;
                }
            }
            
            // 회전 불가능하면 되돌림 (3번 더 회전)
            if (!canRotate) {
                b.rotate();
                b.rotate();
                b.rotate();
            }
        } else if (current instanceof WeightBlock) {
            // WeightBlock은 회전 불가
            return;
        } else if (current instanceof Tetromino t) {
            Tetromino r = t.getRotatedCopy();
            if (canMove(r, 0, 0)) t.rotate();
        }
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
        if (current instanceof ItemBlock) {
            // ItemBlock(WeightBlock, BombBlock 등)은 고정하지 않음
            return;
        }
        if (current instanceof Tetromino t) {
            for (Position p : t.getBlocks()) {
                int x = t.getX() + p.x;
                int y = t.getY() + p.y;
                if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                    grid[y][x] = t.getShape();
                }
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
    public Object getCurrent()        { return current; }
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

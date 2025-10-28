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
    private final com.team.tetris.items.ItemManager itemManager;
    private com.team.tetris.items.ItemBlock nextItemBlock;
    private com.team.tetris.items.ItemBlock currentItemBlock;
    
    // 슬로우 효과 관련
    private boolean slowEffectActive = false;
    private long slowEffectStartTime = 0;
    private static final long SLOW_EFFECT_DURATION = 10000; // 10초

    public Board(Difficulty difficulty, boolean isItemMode) {
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        this.grid = new ShapeType[ROWS][COLS];
        this.itemManager = new com.team.tetris.items.ItemManager(isItemMode);
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

    // ===== 난이도별 점수 배율(클리어/보너스 전용) =====
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
        
        ShapeType shape;
        if (nextItemBlock != null) {
            // 아이템 블록이 있으면 해당 블록의 기본 형태 사용
            shape = nextItemBlock.getBaseShape();
            currentItemBlock = nextItemBlock;  // 현재 아이템 블록 설정
            System.out.println("아이템 블록 스폰: " + nextItemBlock.getName());
        } else {
            shape = (nextShape != null) ? nextShape : pickByRoulette();
            currentItemBlock = null;  // 일반 블록
        }
        
        current = new Tetromino(shape, COLS / 2 - 2, 0);
        
        // 다음 블록 설정 (아이템 블록은 한 번만 사용)
        if (nextItemBlock != null) {
            nextShape = pickByRoulette();  // 아이템 블록 사용 후 일반 블록으로 복귀
            nextItemBlock = null;
        } else {
            nextShape = pickByRoulette();
        }

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
        // 현재 테트리미노가 방금 생성된 아이템 블록인지 확인
        boolean isCurrentItemBlock = (currentItemBlock != null);
        
        Position[] blocks = current.getBlocks();
        for (int i = 0; i < blocks.length; i++) {
            Position p = blocks[i];
            int x = current.getX() + p.x;
            int y = current.getY() + p.y;
            if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                grid[y][x] = current.getShape();
                
                // 아이템 블록이라면 효과 발동
                if (isCurrentItemBlock) {
                    if (currentItemBlock instanceof com.team.tetris.items.LineBlock lineBlock) {
                        // LineBlock은 특별한 처리
                        lineBlock.activateEffectAtPosition(this, x, y, i);
                    } else {
                        // 다른 아이템 블록들은 기본 처리
                        currentItemBlock.activateEffect(this, x, y);
                    }
                }
            }
        }
        
        // 아이템 블록 효과 발동 후 초기화
        if (isCurrentItemBlock) {
            currentItemBlock = null;
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
        
        if (cleared > 0) {
            System.out.println(cleared + "줄 삭제됨. 아이템 모드: " + isItemMode);
        }
        
        // 줄이 지워졌다면 아이템 매니저에게 알림 (아이템 모드에서만)
        if (cleared > 0 && isItemMode && itemManager != null) {
            com.team.tetris.items.ItemBlock newItem = itemManager.onLinesCleared(cleared);
            if (newItem != null) {
                nextItemBlock = newItem;
                System.out.println("아이템 블록 생성: " + newItem.getName());
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
    public boolean isItemMode()          { return isItemMode; }
    public com.team.tetris.items.ItemBlock getNextItemBlock() { return nextItemBlock; }
    public com.team.tetris.items.ItemBlock getCurrentItemBlock() { return currentItemBlock; }
    public com.team.tetris.items.ItemManager getItemManager() { return itemManager; }
    
    // 슬로우 효과 관련 메서드들
    public boolean isSlowEffectActive() { return slowEffectActive; }
    public long getSlowEffectRemainingTime() {
        if (!slowEffectActive) return 0;
        long elapsed = System.currentTimeMillis() - slowEffectStartTime;
        return Math.max(0, SLOW_EFFECT_DURATION - elapsed);
    }
    
    public void activateSlowEffect() {
        slowEffectActive = true;
        slowEffectStartTime = System.currentTimeMillis();
        System.out.println("슬로우 효과 활성화! 10초간 속도 감소");
    }
    
    public void updateSlowEffect() {
        if (slowEffectActive) {
            long elapsed = System.currentTimeMillis() - slowEffectStartTime;
            if (elapsed >= SLOW_EFFECT_DURATION) {
                slowEffectActive = false;
                System.out.println("슬로우 효과 종료");
            }
        }
    }

    // 리셋(재시작용)
    public void reset() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) grid[y][x] = null;
        }
        score = 0;
        totalLinesCleared = 0;
        gameOver = false;
        
        // 슬로우 효과 초기화
        slowEffectActive = false;
        slowEffectStartTime = 0;
        
        // 아이템 관련 초기화
        nextItemBlock = null;
        currentItemBlock = null;
        if (itemManager != null) {
            // ItemManager의 totalLinesCleared도 초기화해야 함
            itemManager.reset();
        }
        
        nextShape = pickByRoulette();
        spawnNewTetromino();
    }

    // ===== 아이템 블록용 메서드들 =====
    
    /**
     * 특정 줄 전체를 제거 (라인 아이템용)
     */
    public void clearLine(int line) {
        if (line >= 0 && line < ROWS) {
            for (int x = 0; x < COLS; x++) {
                grid[line][x] = null;
            }
            // 위쪽 블록들을 아래로 이동
            for (int y = line; y > 0; y--) {
                System.arraycopy(grid[y - 1], 0, grid[y], 0, COLS);
            }
            // 맨 위 줄 비우기
            for (int x = 0; x < COLS; x++) {
                grid[0][x] = null;
            }
        }
    }
}

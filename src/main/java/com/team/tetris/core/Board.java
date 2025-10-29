package com.team.tetris.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.team.tetris.items.ItemManager;

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

    // 아이템 모드
    private final boolean isItemMode;
    private final ItemManager itemManager;
    private com.team.tetris.items.ItemBlock nextItemBlock;
    private com.team.tetris.items.ItemBlock currentItemBlock;
    private boolean shouldGenerateItem = false;
    
    // 슬로우 효과 관련
    private boolean slowEffectActive = false;
    private long slowEffectStartTime = 0;
    private static final long SLOW_EFFECT_DURATION = 10000; // 10초
    
    // Transform 효과 관련
    private int transformRemainingBlocks = 0; // 남은 Transform 효과 블록 수
    
    // 줄 삭제 애니메이션 관련
    private int[] pendingClearRows = null;

    public Board(Difficulty difficulty) {
        this(difficulty, false); // 기본값: 아이템 모드 비활성화
    }

    public Board(Difficulty difficulty, boolean isItemMode) {
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        this.itemManager = new ItemManager(isItemMode);
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
        // Transform 효과가 활성화되어 있으면 I 블록 반환
        if (transformRemainingBlocks > 0) {
            transformRemainingBlocks--;
            return ShapeType.I;
        }
        
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
        
        // 1. 먼저 현재 미리보기 블록을 스폰
        ShapeType shape;
        if (nextItemBlock != null) {
            shape = nextItemBlock.getBaseShape();
            currentItemBlock = nextItemBlock;
            nextItemBlock = null;
        } else {
            shape = (nextShape != null) ? nextShape : pickByRoulette();
            currentItemBlock = null;
        }
        
        if (shape == null) {
            shape = ShapeType.I;
        }
        
        current = new Tetromino(shape, COLS / 2 - 2, 0);
        
        // WeightBlock의 경우 커스텀 블록 설정
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock weightBlock) {
            current.setBlocks(weightBlock.getCustomBlocks());
        }
        
        // 2. 다음 블록 준비
        nextShape = pickByRoulette();
        
        // 3. 아이템 생성 플래그가 설정되어 있으면 이제 아이템 생성
        if (shouldGenerateItem && isItemMode && itemManager != null) {
            com.team.tetris.items.ItemBlock item = itemManager.generateItem();
            if (item != null) {
                nextItemBlock = item; // 다음 블록을 아이템으로 대체
            }
            shouldGenerateItem = false; // 플래그 리셋
        }

        if (!canMove(current, 0, 0)) {
            gameOver = true;
        }
    }

    // ===== 이동/회전 =====
    public void moveLeft() {
        if (gameOver) return;
        
        // WeightBlock 좌우 이동 잠금 체크
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock weightBlock) {
            if (weightBlock.isLockedHorizontal()) {
                System.out.println("WeightBlock: 좌우 이동 잠김 (moveLeft 무시)");
                return;
            }
        }
        
        if (canMove(current, -1, 0)) current.move(-1, 0);
    }
    
    public void moveRight() {
        if (gameOver) return;
        
        // WeightBlock 좌우 이동 잠금 체크
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock weightBlock) {
            if (weightBlock.isLockedHorizontal()) {
                System.out.println("WeightBlock: 좌우 이동 잠김 (moveRight 무시)");
                return;
            }
        }
        
        if (canMove(current, 1, 0)) current.move(1, 0);
    }

    /** 한 칸 하강 (자동/수동 동일) */
    public boolean moveDown() {
        if (gameOver) return false;
        
        // WeightBlock 특수 처리: 낙하 중 아래 블록 삭제
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock weightBlock) {
            weightBlock.eraseBelowBlocks(this, current.getX(), current.getY());
        }

        if (canMove(current, 0, 1)) {
            current.move(0, 1);
            addBaseScore(10);     // 한 칸 떨어질 때마다 +10 (난이도 무관)
            
            // WeightBlock이 블록에 닿으면 좌우 이동 잠금
            if (currentItemBlock instanceof com.team.tetris.items.WeightBlock weightBlock) {
                if (hasBlockBelow()) {
                    weightBlock.setLockedHorizontal(true);
                    System.out.println("WeightBlock: 블록 접촉, 좌우 이동 잠금");
                }
            }
            
            return true;
        } else {
            // WeightBlock은 바닥에 도달하면 보드에 고정하지 않고 사라짐
            if (currentItemBlock instanceof com.team.tetris.items.WeightBlock) {
                System.out.println("WeightBlock: 바닥 도달, 사라짐");
                spawnNewTetromino();
                return false;
            }
            
            // 고정 → 라인 삭제 애니메이션 준비
            fixToBoard();
            List<Integer> full = scanFullLines();
            if (!full.isEmpty()) {
                // 삭제할 줄을 pendingClearRows에 저장 (GamePanel에서 애니메이션 후 clearRows 호출)
                pendingClearRows = full.stream().mapToInt(Integer::intValue).toArray();
            } else {
                spawnNewTetromino();
            }
            return false;
        }
    }

    /** 즉시 낙하 */
    public void hardDrop() {
        if (gameOver) return;
        int dropDist = 0;
        
        // WeightBlock 특수 처리: 하드드롭 시 경로상의 모든 블록 삭제
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock) {
            // 1단계: 아래 방향으로 모든 블록 삭제 (바닥까지)
            for (Position block : current.getBlocks()) {
                int blockX = current.getX() + block.x;
                
                // 현재 블록 아래부터 바닥까지 모든 블록 삭제
                for (int y = current.getY() + block.y + 1; y < ROWS; y++) {
                    if (blockX >= 0 && blockX < COLS) {
                        if (grid[y][blockX] != null) {
                            grid[y][blockX] = null;
                        }
                    }
                }
            }
            
            // 2단계: 블록 삭제 후 떨어질 수 있는 만큼 낙하
            int totalDropDist = 0;
            while (canMove(current, 0, 1)) {
                current.move(0, 1);
                totalDropDist++;
            }
            
            System.out.println("WeightBlock: 하드드롭으로 경로상 모든 블록 삭제 후 " + totalDropDist + "칸 낙하");
            addBaseScore(totalDropDist * 10);
            spawnNewTetromino(); // WeightBlock은 고정하지 않고 사라짐
            return;
        }
        
        // 일반 블록 하드드롭
        while (canMove(current, 0, 1)) {
            current.move(0, 1);
            dropDist++;
        }
        addBaseScore(dropDist * 10); // 떨어진 칸 수 × 10 (난이도 배율 미적용)
        moveDown();                  // 고정/클리어/스폰 처리
    }

    public void rotate() {
        if (gameOver) return;
        
        // WeightBlock은 회전 불가
        if (currentItemBlock instanceof com.team.tetris.items.WeightBlock) {
            System.out.println("WeightBlock: 회전 불가");
            return;
        }
        
        Tetromino r = current.getRotatedCopy();
        if (canMove(r, 0, 0)) {
            current.rotate();
            
            // LineBlock이나 BombBlock인 경우 회전 상태 업데이트
            if (currentItemBlock instanceof com.team.tetris.items.LineBlock lineBlock) {
                lineBlock.setRotation(current.getRotation());
            } else if (currentItemBlock instanceof com.team.tetris.items.BombBlock bombBlock) {
                bombBlock.setRotation(current.getRotation());
            }
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
        if (t == null || t.getBlocks() == null) {
            System.err.println("canMove: Tetromino 또는 blocks가 null입니다!");
            return false;
        }
        
        for (Position p : t.getBlocks()) {
            int x = t.getX() + p.x + dx;
            int y = t.getY() + p.y + dy;
            if (x < 0 || x >= COLS || y < 0 || y >= ROWS) return false;
            if (grid[y][x] != null) return false;
        }
        return true;
    }

    private void fixToBoard() {
        // 현재 테트리미노가 아이템 블록인지 확인
        boolean isCurrentItemBlock = (currentItemBlock != null);
        boolean isBombBlock = (currentItemBlock instanceof com.team.tetris.items.BombBlock);
        
        if (isCurrentItemBlock) {
            System.out.println("아이템 블록 고정: " + currentItemBlock.getName());
        }
        
        // BombBlock이 아닌 경우에만 보드에 고정
        if (!isBombBlock) {
            for (int i = 0; i < current.getBlocks().length; i++) {
                Position p = current.getBlocks()[i];
                int x = current.getX() + p.x;
                int y = current.getY() + p.y;
                if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                    grid[y][x] = current.getShape();
                }
            }
        }
        
        // 아이템 효과 발동
        if (isCurrentItemBlock) {
            for (int i = 0; i < current.getBlocks().length; i++) {
                Position p = current.getBlocks()[i];
                int x = current.getX() + p.x;
                int y = current.getY() + p.y;
                if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
                    if (currentItemBlock instanceof com.team.tetris.items.LineBlock lineBlock) {
                        // LineBlock은 특별한 처리
                        System.out.println("LineBlock 효과 발동 위치: (" + x + ", " + y + "), 블록 인덱스: " + i);
                        lineBlock.activateEffectAtPosition(this, x, y, i);
                    } else if (currentItemBlock instanceof com.team.tetris.items.SlowBlock) {
                        // SlowBlock은 첫 번째 블록에서만 효과 발동
                        if (i == 0) {
                            System.out.println("SlowBlock 효과 발동");
                            currentItemBlock.activateEffect(this, x, y);
                        }
                    } else if (currentItemBlock instanceof com.team.tetris.items.BombBlock bombBlock) {
                        // BombBlock은 폭탄 블록(B 표시된 블록)에서만 효과 발동 후 사라짐
                        if (i == bombBlock.getBombIndex()) {
                            System.out.println("BombBlock 효과 발동 - 3x3 폭발! 폭탄 위치: (" + x + ", " + y + ")");
                            currentItemBlock.activateEffect(this, x, y);
                            System.out.println("BombBlock 폭발 완료 - 블록 사라짐");
                        }
                    } else if (currentItemBlock instanceof com.team.tetris.items.WeightBlock) {
                        // WeightBlock은 첫 번째 블록에서만 효과 발동
                        if (i == 0) {
                            System.out.println("WeightBlock 효과 발동");
                            currentItemBlock.activateEffect(this, x, y);
                        }
                    } else {
                        // 다른 아이템들은 첫 번째 블록에서만 효과 발동
                        if (i == 0) {
                            System.out.println(currentItemBlock.getName() + " 효과 발동");
                            currentItemBlock.activateEffect(this, x, y);
                        }
                    }
                }
            }
        }
        
        // 아이템 효과 발동 후 초기화
        if (isCurrentItemBlock) {
            System.out.println("아이템 블록 효과 발동 완료, currentItemBlock 초기화");
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
        
        if (cleared > 0 && isItemMode && itemManager != null) {
            itemManager.onLinesCleared(cleared);
            if (itemManager.shouldCreateItem()) {
                shouldGenerateItem = true;
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
    
    // ===== 줄 삭제 애니메이션 관련 메서드 =====
    
    /**
     * 삭제 대상 줄만 스캔 (삭제는 하지 않음)
     */
    private List<Integer> scanFullLines() {
        List<Integer> full = new ArrayList<>();
        for (int y = ROWS - 1; y >= 0; y--) {
            boolean isFull = true;
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == null) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                full.add(y);
            }
        }
        return full;
    }
    
    /**
     * GamePanel에서 애니메이션 후 호출: 실제 줄 삭제 + 점수/아이템 후처리 + 다음 블록 스폰
     */
    public void clearRows(int[] rows) {
        if (rows == null || rows.length == 0) {
            return;
        }

        boolean[] clear = new boolean[ROWS];
        int cleared = 0;
        for (int r : rows) {
            if (r >= 0 && r < ROWS && !clear[r]) {
                clear[r] = true;
                cleared++;
            }
        }
        if (cleared == 0) {
            return;
        }

        int write = ROWS - 1;
        for (int read = ROWS - 1; read >= 0; read--) {
            if (clear[read]) {
                continue;
            }
            if (write != read) {
                System.arraycopy(grid[read], 0, grid[write], 0, COLS);
            }
            write--;
        }
        // 위쪽 빈 영역 비우기
        for (int y = write; y >= 0; y--) {
            for (int x = 0; x < COLS; x++) {
                grid[y][x] = null;
            }
        }

        // 점수/누적
        addBonusScore(1000 * cleared);
        totalLinesCleared += cleared;

        // 아이템 후처리
        if (isItemMode && itemManager != null) {
            itemManager.onLinesCleared(cleared);
            if (itemManager.shouldCreateItem()) {
                shouldGenerateItem = true;
            }
        }

        // 애니 상태 정리 + 다음 블록 스폰
        pendingClearRows = null;
        spawnNewTetromino();
    }
    
    /**
     * GamePanel이 1회용으로 읽는 삭제 예정 줄
     */
    public int[] pollClearingRows() {
        int[] out = pendingClearRows;
        pendingClearRows = null;
        return out;
    }
    
    // ===== 아이템 효과 메서드 =====
    
    /**
     * LineBlock이 특정 줄을 삭제할 때 사용
     */
    public void clearLine(int y) {
        if (y >= 0 && y < ROWS) {
            removeLine(y);
        }
    }
    
    /**
     * SlowBlock이 슬로우 효과를 활성화할 때 사용
     */
    public void activateSlowEffect() {
        slowEffectActive = true;
        slowEffectStartTime = System.currentTimeMillis();
        System.out.println("슬로우 효과 활성화!");
    }
    
    /**
     * 슬로우 효과 상태 업데이트 (매 프레임마다 호출)
     */
    public void updateSlowEffect() {
        if (slowEffectActive) {
            long elapsed = System.currentTimeMillis() - slowEffectStartTime;
            if (elapsed >= SLOW_EFFECT_DURATION) {
                slowEffectActive = false;
                System.out.println("슬로우 효과 종료!");
            }
        }
    }
    
    /**
     * 슬로우 효과가 활성화되어 있는지 확인
     */
    public boolean isSlowEffectActive() {
        return slowEffectActive;
    }
    
    /**
     * 슬로우 효과 남은 시간 (밀리초)
     */
    public long getSlowEffectRemainingTime() {
        if (!slowEffectActive) return 0;
        long elapsed = System.currentTimeMillis() - slowEffectStartTime;
        return Math.max(0, SLOW_EFFECT_DURATION - elapsed);
    }
    
    // ===== Transform 효과 메서드 =====
    
    /**
     * Transform 효과 활성화 - 다음 5개의 블록을 I 블록으로 변환
     */
    public void activateTransformEffect() {
        // nextShape를 I로 설정하고, 전체 5개 블록을 카운트
        // nextShape 사용 시와 pickByRoulette() 호출 시 각각 카운터 감소
        transformRemainingBlocks = 5;
        // 이미 준비된 nextShape도 I 블록으로 변경 (첫 번째 블록)
        nextShape = ShapeType.I;
        System.out.println("Transform 효과 활성화: 다음 5개의 블록이 I 블록으로 변환됩니다.");
    }
    
    /**
     * Transform 효과 남은 블록 수
     */
    public int getTransformRemainingBlocks() {
        return transformRemainingBlocks;
    }
    
    /**
     * 현재 테트로미노(WeightBlock) 아래에 블록이 있는지 확인
     */
    private boolean hasBlockBelow() {
        if (current == null || current.getBlocks() == null) return false;
        
        Position[] blocks = current.getBlocks();
        for (Position block : blocks) {
            int boardX = current.getX() + block.x;
            int boardY = current.getY() + block.y + 1; // 바로 아래
            
            if (boardY >= ROWS) return true; // 바닥에 도달
            if (boardY >= 0 && boardX >= 0 && boardX < COLS) {
                if (grid[boardY][boardX] != null) {
                    return true; // 블록 발견
                }
            }
        }
        return false;
    }

    // ===== 게임 상태/게터 =====
    public boolean isGameOver()          { return gameOver; }
    public int getScore()                { return score; }
    public int getTotalLinesCleared()    { return totalLinesCleared; }
    public Difficulty getDifficulty()    { return difficulty; }
    public ShapeType[][] getGrid()       { return grid; }
    public Tetromino getCurrent()        { return current; }
    public ShapeType getNextShape()      { return nextShape; }
    
    // 아이템 모드 관련 게터
    public boolean isItemMode()                                  { return isItemMode; }
    public ItemManager getItemManager()                          { return itemManager; }
    public com.team.tetris.items.ItemBlock getNextItemBlock()    { return nextItemBlock; }
    public com.team.tetris.items.ItemBlock getCurrentItemBlock() { return currentItemBlock; }

    // 리셋(재시작용)
    public void reset() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) grid[y][x] = null;
        }
        score = 0;
        totalLinesCleared = 0;
        gameOver = false;
        nextShape = pickByRoulette();
        nextItemBlock = null; // 아이템 블록 초기화
        spawnNewTetromino();
    }
}

package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.Position;
import com.team.tetris.core.ShapeType;

/**
 * 폭탄 블록 - 일반 테트로미노 형태지만 한 칸이 폭탄(B)으로 표시되고,
 * 설치되면 폭탄을 중심으로 3x3 영역의 블록을 파괴
 */
public class BombBlock implements ItemBlock {
    
    private ShapeType baseShape; // 기본 테트로미노 형태
    private int rotation; // 현재 회전 상태 (0, 1, 2, 3)
    private int bombIndexInRotation0; // rotation=0일 때 폭탄의 인덱스
    private Position[] blocks; // 현재 블록 위치들
    private int x, y; // 현재 좌표
    private boolean lockedHorizontal = false; // 좌우 이동 잠금
    
    public BombBlock() {
        // 랜덤한 테트로미노 선택
        ShapeType[] shapes = ShapeType.values();
        Random random = new Random();
        this.baseShape = shapes[random.nextInt(shapes.length)];
        this.rotation = 0;
        
        // 해당 shape의 블록 위치 복사
        Position[] shapeBlocks = baseShape.getOffsets(rotation);
        this.blocks = new Position[shapeBlocks.length];
        for (int i = 0; i < shapeBlocks.length; i++) {
            this.blocks[i] = new Position(shapeBlocks[i].x, shapeBlocks[i].y);
        }
        
        // 랜덤하게 한 칸을 폭탄으로 지정 (rotation 0 기준 인덱스 저장)
        bombIndexInRotation0 = random.nextInt(this.blocks.length);
    }
    
    @Override
    public String getName() {
        return "Bomb Block";
    }
    
    @Override
    public Color getColor() {
        return baseShape.getColor(); // 원래 테트로미노 색상 사용
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape;
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        if (boardObj instanceof Board board) {
            System.out.println("BombBlock.activateEffect 호출 - 폭발 중심: (" + x + ", " + y + ")");
            explode(board, x, y);
        }
    }
    
    @Override
    public String getDescription() {
        return "Explodes in a 3x3 area when placed";
    }
    
    @Override
    public char getSymbol() {
        return 'B'; // Bomb
    }
    
    /**
     * 특정 블록 인덱스에 대한 심볼을 반환
     * 폭탄 블록 위치에만 'B'를 표시하고, 나머지는 공백
     */
    public char getBlockSymbol(int blockIndex) {
        return (blockIndex == getBombIndex()) ? 'B' : ' ';
    }
    
    // === 추가 메서드들 ===
    
    /**
     * 폭탄 설치 시 3x3 영역 파괴
     * @param board 게임 보드
     * @param centerX 폭발 중심 X 좌표
     * @param centerY 폭발 중심 Y 좌표
     */
    private void explode(Board board, int centerX, int centerY) {
        System.out.println("3x3 폭발 시작! 중심: (" + centerX + ", " + centerY + ")");
        
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int targetX = centerX + dx;
                int targetY = centerY + dy;
                
                if (targetX >= 0 && targetX < Board.COLS && 
                    targetY >= 0 && targetY < Board.ROWS) {
                    System.out.println("  폭발 위치: (" + targetX + ", " + targetY + ")");
                    board.getGrid()[targetY][targetX] = null;
                }
            }
        }
        
        System.out.println("폭발 완료!");
    }
    
    public void initialize(int x, int y) {
        this.x = x;
        this.y = y;
        this.lockedHorizontal = false;
        this.rotation = 0;
        updateBlocks();
    }
    
    private void updateBlocks() {
        Position[] shapeBlocks = baseShape.getOffsets(rotation);
        this.blocks = new Position[shapeBlocks.length];
        for (int i = 0; i < shapeBlocks.length; i++) {
            this.blocks[i] = new Position(shapeBlocks[i].x, shapeBlocks[i].y);
        }
    }
    
    public Position[] getBlocks() {
        return blocks;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public ShapeType getShape() { return baseShape; }
    
    public void rotate() {
        rotation = (rotation + 1) % 4;
        Position[] newBlocks = baseShape.getOffsets(rotation);
        this.blocks = new Position[newBlocks.length];
        for (int i = 0; i < newBlocks.length; i++) {
            this.blocks[i] = new Position(newBlocks[i].x, newBlocks[i].y);
        }
    }
    
    public void moveLeft(Board board) {
        if (!lockedHorizontal && canMoveHorizontal(board, -1)) {
            x -= 1;
        }
    }
    
    public void moveRight(Board board) {
        if (!lockedHorizontal && canMoveHorizontal(board, 1)) {
            x += 1;
        }
    }
    
    private boolean canMoveHorizontal(Board board, int dx) {
        for (Position p : blocks) {
            int bx = x + p.x + dx;
            int by = y + p.y;
            if (bx < 0 || bx >= Board.COLS || by < 0 || by >= Board.ROWS) return false;
            if (board.getGrid()[by][bx] != null) return false;
        }
        return true;
    }
    
    public boolean moveDown(Board board) {
        for (Position p : blocks) {
            int by = y + p.y + 1;
            if (by >= Board.ROWS) {
                return true;
            }
        }
        
        for (Position p : blocks) {
            int bx = x + p.x;
            int by = y + p.y + 1;
            if (board.getGrid()[by][bx] != null) {
                lockedHorizontal = true;
                return true;
            }
        }
        
        y += 1;
        return false;
    }
    
    /**
     * 현재 회전 상태에서 폭탄의 상대 위치를 반환
     */
    private Position getCurrentBombPosition() {
        int currentIndex = baseShape.getMappedIndex(rotation, bombIndexInRotation0);
        return blocks[currentIndex];
    }
    
    /**
     * 폭탄의 상대 위치 반환 (렌더링용)
     */
    public Position getBombPosition() {
        return getCurrentBombPosition();
    }
    
    /**
     * 폭탄 블록의 인덱스 반환 (현재 회전 상태에서)
     */
    public int getBombIndex() {
        return baseShape.getMappedIndex(rotation, bombIndexInRotation0);
    }
    
    public int getRotation() {
        return rotation;
    }
    
    /**
     * 회전 상태 설정 (Board에서 Tetromino 회전 시 호출)
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}

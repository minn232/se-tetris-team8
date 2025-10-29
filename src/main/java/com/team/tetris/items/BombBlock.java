package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ItemBlock;
import com.team.tetris.core.Position;
import com.team.tetris.core.ShapeType;

/**
 * 폭탄 블록 - 일반 테트로미노 형태지만 한 칸이 폭탄(B)으로 표시되고,
 * 설치되면 폭탄을 중심으로 3x3 영역의 블록을 파괴
 */
public class BombBlock extends ItemBlock {
    
    private ShapeType shape; // 테트로미노 형태
    private int rotation; // 현재 회전 상태 (0, 1, 2, 3)
    private int bombIndexInRotation0; // rotation=0일 때 폭탄의 인덱스 (변경되지 않음)
    
    public BombBlock(int x, int y) {
        super(x, y, null); // 일단 null로 초기화
        
        // 랜덤한 테트로미노 선택
        ShapeType[] shapes = ShapeType.values();
        Random random = new Random();
        this.shape = shapes[random.nextInt(shapes.length)];
        this.rotation = 0;
        
        // 해당 shape의 블록 위치 복사
        Position[] shapeBlocks = shape.getOffsets(rotation);
        this.blocks = new Position[shapeBlocks.length];
        for (int i = 0; i < shapeBlocks.length; i++) {
            this.blocks[i] = new Position(shapeBlocks[i].x, shapeBlocks[i].y);
        }
        
        // 랜덤하게 한 칸을 폭탄으로 지정 (rotation 0 기준 인덱스 저장)
        bombIndexInRotation0 = random.nextInt(this.blocks.length);
    }
    
    @Override
    public Color getColor() {
        return shape.getColor(); // 원래 테트로미노 색상 사용
    }
    
    /**
     * ShapeType 반환 (렌더링용)
     */
    public ShapeType getShape() {
        return shape;
    }
    
    @Override
    public void rotate() {
        // BombBlock은 회전 가능
        rotation = (rotation + 1) % 4;
        Position[] newBlocks = shape.getOffsets(rotation);
        this.blocks = new Position[newBlocks.length];
        for (int i = 0; i < newBlocks.length; i++) {
            this.blocks[i] = new Position(newBlocks[i].x, newBlocks[i].y);
        }
    }
    
    @Override
    public void moveLeft(Board board) {
        if (!lockedHorizontal && canMoveHorizontal(board, -1)) {
            x -= 1;
        }
    }
    
    @Override
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
    
    @Override
    public boolean moveDown(Board board) {
        // 일반 테트로미노처럼 움직임
        for (Position p : blocks) {
            int by = y + p.y + 1;
            if (by >= Board.ROWS) {
                return true; // 바닥에 닿음
            }
        }
        
        // 아래에 블록이 있는지 확인
        for (Position p : blocks) {
            int bx = x + p.x;
            int by = y + p.y + 1;
            if (board.getGrid()[by][bx] != null) {
                lockedHorizontal = true;
                return true; // 블록에 닿음
            }
        }
        
        y += 1;
        return false; // 계속 내려갈 수 있음
    }
    
    /**
     * 폭탄이 설치될 때 호출 - 폭탄 위치 기준 3x3 영역 파괴
     */
    public void explode(Board board) {
        // 현재 회전 상태에서 폭탄의 절대 위치 계산
        Position currentBombPos = getCurrentBombPosition();
        int bombX = x + currentBombPos.x;
        int bombY = y + currentBombPos.y;
        
        // 3x3 영역 파괴 (폭탄 중심 기준 상하좌우 1칸씩)
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int targetX = bombX + dx;
                int targetY = bombY + dy;
                
                // 범위 체크
                if (targetX >= 0 && targetX < Board.COLS && 
                    targetY >= 0 && targetY < Board.ROWS) {
                    board.getGrid()[targetY][targetX] = null;
                }
            }
        }
    }
    
    /**
     * 현재 회전 상태에서 폭탄의 상대 위치를 반환
     * ShapeType의 blockMapping을 사용하여 정확한 인덱스 변환
     */
    private Position getCurrentBombPosition() {
        // ShapeType의 매핑 테이블을 사용하여 현재 rotation에서의 인덱스 찾기
        int currentIndex = shape.getMappedIndex(rotation, bombIndexInRotation0);
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
        return shape.getMappedIndex(rotation, bombIndexInRotation0);
    }
}

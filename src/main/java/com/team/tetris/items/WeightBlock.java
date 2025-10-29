package com.team.tetris.items;

import java.awt.Color;

import com.team.tetris.core.Board;
import com.team.tetris.core.Position;
import com.team.tetris.core.ShapeType;

/**
 * 무게 블록 - 내려가면서 아래 블록을 삭제하는 아이템
 */
public class WeightBlock implements ItemBlock {
    
    private Position[] blocks;
    private int x, y;
    private boolean lockedHorizontal = false;
    
    public WeightBlock() {
        this.blocks = new Position[] {
            new Position(1, 0), new Position(2, 0),  // 위쪽 2개
            new Position(0, 1), new Position(1, 1), new Position(2, 1), new Position(3, 1)  // 아래쪽 4개
        };
    }
    
    @Override
    public String getName() {
        return "Weight Block";
    }
    
    @Override
    public Color getColor() {
        return Color.WHITE;
    }
    
    @Override
    public ShapeType getBaseShape() {
        // WeightBlock은 커스텀 형태이지만, Board에서 Tetromino 생성을 위해
        // 임의의 ShapeType을 반환 (실제로는 사용되지 않음)
        return ShapeType.I; // 아무 ShapeType이나 사용 (렌더링은 커스텀 블록으로 됨)
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        // WeightBlock은 설치 시가 아닌 낙하 중 효과 발동
        // 별도 효과 없음
    }
    
    @Override
    public String getDescription() {
        return "Erases blocks below as it falls";
    }
    
    @Override
    public char getSymbol() {
        return 'W'; // Weight
    }
    
    /**
     * 특정 블록 인덱스에 대한 심볼을 반환
     * 모든 블록에 'W'를 표시
     */
    public char getBlockSymbol(int blockIndex) {
        return 'W';
    }
    
    // === 추가 메서드들 ===
    
    public void initialize(int x, int y) {
        this.x = x;
        this.y = y;
        this.lockedHorizontal = false;
    }
    
    public Position[] getBlocks() {
        return blocks;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
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
            if (bx < 0 || bx >= Board.COLS || board.getGrid()[by][bx] != null) {
                return false;
            }
        }
        return true;
    }
    
    public boolean moveDown(Board board) {
        // 바닥에 닿았는지 확인
        boolean hitBottom = false;
        for (Position p : blocks) {
            int by = y + p.y + 1;
            if (by >= Board.ROWS) {
                hitBottom = true;
                break;
            }
        }
        
        if (hitBottom) {
            return true;
        }
        
        // 아래에 블록이 있으면 좌우 이동 불가
        boolean hasBlockBelow = false;
        for (Position p : blocks) {
            int bx = x + p.x;
            int by = y + p.y + 1;
            if (board.getGrid()[by][bx] != null) {
                hasBlockBelow = true;
                break;
            }
        }
        
        if (hasBlockBelow) {
            lockedHorizontal = true;
        }
        
        // 아래 블록 삭제 후 한 칸 이동
        eraseBelowOne(board);
        y += 1;
        return false;
    }
    
    private void eraseBelowOne(Board board) {
        for (Position p : blocks) {
            int bx = x + p.x;
            int by = y + p.y + 1;
            if (by < Board.ROWS && board.getGrid()[by][bx] != null) {
                board.getGrid()[by][bx] = null;
            }
        }
    }
    
    public boolean isLockedHorizontal() {
        return lockedHorizontal;
    }
    
    public void setLockedHorizontal(boolean locked) {
        this.lockedHorizontal = locked;
    }
    
    /**
     * 현재 위치 기준으로 바로 아래의 블록들을 삭제 (일반 낙하용)
     */
    public void eraseBelowBlocks(Board board, int currentX, int currentY) {
        for (Position p : blocks) {
            int bx = currentX + p.x;
            int by = currentY + p.y + 1; // 바로 아래
            
            if (by >= 0 && by < Board.ROWS && bx >= 0 && bx < Board.COLS) {
                if (board.getGrid()[by][bx] != null) {
                    board.getGrid()[by][bx] = null;
                    System.out.println("WeightBlock: (" + bx + ", " + by + ") 블록 삭제");
                }
            }
        }
    }
    
    /**
     * 하드드롭 시 경로상의 모든 블록 삭제
     */
    public int eraseAllBlocksInPath(Board board, int startX, int startY, int endY) {
        int erasedCount = 0;
        
        // WeightBlock의 각 블록 위치에서 수직으로 경로상의 모든 블록 삭제
        for (Position block : blocks) {
            int blockX = startX + block.x;
            
            // 현재 Y 위치부터 최종 Y 위치까지 수직으로 삭제
            for (int y = startY + block.y; y <= endY + block.y; y++) {
                if (y >= 0 && y < Board.ROWS && blockX >= 0 && blockX < Board.COLS) {
                    if (board.getGrid()[y][blockX] != null) {
                        board.getGrid()[y][blockX] = null;
                        erasedCount++;
                    }
                }
            }
        }
        
        return erasedCount;
    }
}
package items;

import java.awt.Color;

import core.Board;
import core.Position;
import core.ShapeType;

/**
 * 무게 아이템 블록 - 낙하하면서 아래 블록을 삭제
 * 커스텀 6블록 형태 (ㅁㅁ / ㅁㅁㅁㅁ), 회전 불가, 블록 접촉 시 좌우 이동 잠김
 */
public class WeightBlock implements ItemBlock {
    
    private static final Position[] CUSTOM_BLOCKS = {
        new Position(1, 0), new Position(2, 0),
        new Position(0, 1), new Position(1, 1), new Position(2, 1), new Position(3, 1)
    };
    
    private boolean lockedHorizontal = false;
    
    @Override
    public String getName() {
        return "Weight";
    }
    
    @Override
    public Color getColor() {
        return Color.WHITE;
    }
    
    @Override
    public ShapeType getBaseShape() {
        return ShapeType.I;
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        // WeightBlock은 낙하 중 효과를 발동하므로 배치 시 별도 효과 없음
    }
    
    @Override
    public String getDescription() {
        return "낙하하면서 아래 블록을 삭제합니다";
    }
    
    @Override
    public char getSymbol() {
        return 'W';
    }
    
    public char getBlockSymbol(int blockIndex) {
        return 'W';
    }
    
    public Position[] getCustomBlocks() {
        return CUSTOM_BLOCKS;
    }
    
    public Position[] getBlocks() {
        return CUSTOM_BLOCKS;
    }
    
    public boolean isLockedHorizontal() {
        return lockedHorizontal;
    }
    
    public void setLockedHorizontal(boolean locked) {
        this.lockedHorizontal = locked;
    }
    
    public void eraseBelowBlocks(Board board, int currentX, int currentY) {
        for (Position p : CUSTOM_BLOCKS) {
            int bx = currentX + p.x;
            int by = currentY + p.y + 1;
            
            if (by >= 0 && by < Board.ROWS && bx >= 0 && bx < Board.COLS) {
                board.getGrid()[by][bx] = null;
            }
        }
    }
    
    public void eraseAllBlocksInPath(Board board, int startX, int startY, int endY) {
        for (Position block : CUSTOM_BLOCKS) {
            int blockX = startX + block.x;
            
            for (int y = startY + block.y; y <= endY + block.y; y++) {
                if (y >= 0 && y < Board.ROWS && blockX >= 0 && blockX < Board.COLS) {
                    board.getGrid()[y][blockX] = null;
                }
            }
        }
    }
}
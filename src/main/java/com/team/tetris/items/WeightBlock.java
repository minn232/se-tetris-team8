package com.team.tetris.items;

import java.awt.Color;

import com.team.tetris.core.Board;
import com.team.tetris.core.ItemBlock;
import com.team.tetris.core.Position;

/**
 * 무게 블록 - 내려가면서 아래 블록을 삭제하는 아이템
 */
public class WeightBlock extends ItemBlock {

    public WeightBlock(int x, int y) {
        super(x, y, new Position[] {
            new Position(1,0), new Position(2,0),
            new Position(0,1), new Position(1,1), new Position(2,1), new Position(3,1)
        });
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
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
            if (bx < 0 || bx >= Board.COLS || board.getGrid()[by][bx] != null) {
                return false;
            }
        }
        return true;
    }

    @Override
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
            return true; // 바닥에 닿으면 true 반환 (사라짐)
        }
        
        // 블록을 만나도 계속 내려가되, 만난 블록은 삭제
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
        return false; // 계속 내려갈 수 있음
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
}
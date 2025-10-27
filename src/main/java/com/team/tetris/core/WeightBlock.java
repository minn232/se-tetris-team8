package com.team.tetris.core;

public class WeightBlock {
    private int x, y;
    private final Position[] blocks;
    private boolean lockedHorizontal = false;

    public WeightBlock(int x, int y) {
        this.x = x;
        this.y = y;
        this.blocks = new Position[] {
            new Position(1,0), new Position(2,0),
            new Position(0,1), new Position(1,1), new Position(2,1), new Position(3,1)
        };
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Position[] getBlocks() { return blocks; }

    // 회전 불가
    public void rotate() {
        // 아무 동작도 하지 않음
    }

    // 좌우 이동: lockedHorizontal이 false일 때만 이동
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

    // 아래로 이동하며 한 칸씩 내려갈 때마다 바로 아래 블록만 삭제
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

    // 한 칸 내릴 때마다 바로 아래 블록만 삭제
    private void eraseBelowOne(Board board) {
        for (Position p : blocks) {
            int bx = x + p.x;
            int by = y + p.y + 1;
            if (by < Board.ROWS && board.getGrid()[by][bx] != null) {
                board.getGrid()[by][bx] = null;
            }
        }
    }


    // 테스트용: 좌우 이동 잠금 상태 반환
    public boolean isLockedHorizontal() {
        return lockedHorizontal;
    }
}
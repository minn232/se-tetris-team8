package com.team.tetris.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeightBlockTest {
    @Test
    void testHorizontalLockAfterCollision() {
        Board board = new Board(Difficulty.NORMAL);
        WeightBlock block = new WeightBlock(3, 0);
        // 아래에 블록이 없는 상태에서 좌우 이동 가능
        assertFalse(getLocked(block));
        block.moveLeft(board);
        assertEquals(2, block.getX());
        block.moveRight(board);
        assertEquals(3, block.getX());
        // 아래에 블록을 직접 배치
        for (Position p : block.getBlocks()) {
            int bx = block.getX() + p.x;
            int by = block.getY() + p.y + 1;
            if (by < Board.ROWS) {
                board.getGrid()[by][bx] = ShapeType.I;
            }
        }
        block.moveDown(board);
        assertTrue(getLocked(block));
        int beforeX = block.getX();
        block.moveLeft(board);
        assertEquals(beforeX, block.getX());
        block.moveRight(board);
        assertEquals(beforeX, block.getX());
    }

    // WeightBlock의 lockedHorizontal 상태를 리플렉션으로 확인
    private boolean getLocked(WeightBlock block) {
        try {
            java.lang.reflect.Field f = WeightBlock.class.getDeclaredField("lockedHorizontal");
            f.setAccessible(true);
            return f.getBoolean(block);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

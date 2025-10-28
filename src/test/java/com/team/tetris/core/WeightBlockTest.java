package com.team.tetris.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

class WeightBlockTest {
    
    private Board board;
    
    @BeforeEach
    void setUp() {
        board = new Board(Difficulty.NORMAL);
    }
    
    @Test
    void testWeightBlockCreation() {
        // 생성자 테스트
        WeightBlock block = new WeightBlock(3, 0);
        assertEquals(3, block.getX());
        assertEquals(0, block.getY());
        assertNotNull(block.getBlocks());
        assertEquals(6, block.getBlocks().length); // 2x4 형태의 6개 블록
        assertFalse(isLockedHorizontal(block));
    }
    
    @Test
    void testGetColor() {
        // getColor() 메서드 테스트
        WeightBlock block = new WeightBlock(3, 0);
        assertEquals(Color.WHITE, block.getColor());
    }
    
    @Test
    void testRotate() {
        // rotate() 메서드 테스트 (ItemBlock에서 상속, 회전 불가)
        WeightBlock block = new WeightBlock(3, 0);
        Position[] blocksBefore = block.getBlocks();
        block.rotate();
        Position[] blocksAfter = block.getBlocks();
        // 회전해도 블록 위치 변경 없음
        assertArrayEquals(blocksBefore, blocksAfter);
    }
    
    @Test
    void testMoveLeftWhenPossible() {
        // 좌측 이동 가능한 경우
        WeightBlock block = new WeightBlock(3, 0);
        assertFalse(isLockedHorizontal(block));
        block.moveLeft(board);
        assertEquals(2, block.getX());
        assertEquals(0, block.getY());
    }
    
    @Test
    void testMoveRightWhenPossible() {
        // 우측 이동 가능한 경우
        WeightBlock block = new WeightBlock(3, 0);
        assertFalse(isLockedHorizontal(block));
        block.moveRight(board);
        assertEquals(4, block.getX());
        assertEquals(0, block.getY());
    }
    
    @Test
    void testMoveLeftBlockedByWall() {
        // 왼쪽 벽에 막혀서 이동 불가
        WeightBlock block = new WeightBlock(0, 0);
        block.moveLeft(board);
        assertEquals(0, block.getX()); // 이동 안됨
    }
    
    @Test
    void testMoveRightBlockedByWall() {
        // 오른쪽 벽에 막혀서 이동 불가
        WeightBlock block = new WeightBlock(6, 0);
        block.moveRight(board);
        assertEquals(6, block.getX()); // 이동 안됨
    }
    
    @Test
    void testMoveLeftBlockedByBlock() {
        // 왼쪽에 블록이 있어서 이동 불가
        // WeightBlock 형태: (1,0),(2,0) / (0,1),(1,1),(2,1),(3,1)
        WeightBlock block = new WeightBlock(3, 5);
        // x=3일 때 왼쪽 끝은 x+0 = 3 (y=6), 왼쪽으로 이동하면 x=2
        board.getGrid()[6][2] = ShapeType.I; // (x=2, y=6)에 블록 배치
        block.moveLeft(board);
        assertEquals(3, block.getX()); // 이동 안됨
    }
    
    @Test
    void testMoveRightBlockedByBlock() {
        // 오른쪽에 블록이 있어서 이동 불가
        // WeightBlock 형태: (1,0),(2,0) / (0,1),(1,1),(2,1),(3,1)
        WeightBlock block = new WeightBlock(3, 5);
        // x=3일 때 오른쪽 끝은 x+3 = 6 (y=6), 오른쪽으로 이동하면 x+3 = 7
        board.getGrid()[6][7] = ShapeType.I; // (x=7, y=6)에 블록 배치
        block.moveRight(board);
        assertEquals(3, block.getX()); // 이동 안됨
    }
    
    @Test
    void testHorizontalLockAfterCollision() {
        // 아래에 블록이 있으면 좌우 이동 잠금
        WeightBlock block = new WeightBlock(3, 5);
        assertFalse(isLockedHorizontal(block));
        
        // 초기에는 좌우 이동 가능
        block.moveLeft(board);
        assertEquals(2, block.getX());
        block.moveRight(board);
        assertEquals(3, block.getX());
        
        // 아래에 블록을 배치
        for (Position p : block.getBlocks()) {
            int bx = block.getX() + p.x;
            int by = block.getY() + p.y + 1;
            if (by < Board.ROWS) {
                board.getGrid()[by][bx] = ShapeType.I;
            }
        }
        
        // moveDown 호출 후 좌우 이동 잠김
        block.moveDown(board);
        assertTrue(isLockedHorizontal(block));
        
        int beforeX = block.getX();
        block.moveLeft(board);
        assertEquals(beforeX, block.getX()); // 이동 안됨
        block.moveRight(board);
        assertEquals(beforeX, block.getX()); // 이동 안됨
    }
    
    @Test
    void testMoveDownNormal() {
        // 정상적으로 아래로 이동
        WeightBlock block = new WeightBlock(3, 5);
        int beforeY = block.getY();
        boolean result = block.moveDown(board);
        
        assertFalse(result); // 바닥에 닿지 않음
        assertEquals(beforeY + 1, block.getY());
    }
    
    @Test
    void testMoveDownHitsBottom() {
        // 바닥에 닿으면 true 반환
        // WeightBlock 형태에서 y+1 = ROWS(20)이 되면 바닥
        WeightBlock block = new WeightBlock(3, 19); // y=19, 아래로 이동하면 y+1+1=21 >= 20
        assertTrue(block.moveDown(board)); // 바로 바닥에 닿음
    }
    
    @Test
    void testMoveDownErasesBlocks() {
        // 아래로 내려가면서 블록 삭제
        WeightBlock block = new WeightBlock(3, 5);
        
        // WeightBlock 아래에 블록들을 배치
        board.getGrid()[7][4] = ShapeType.I;
        board.getGrid()[7][5] = ShapeType.O;
        board.getGrid()[7][6] = ShapeType.T;
        
        // moveDown 호출
        block.moveDown(board);
        
        // 아래 블록들이 삭제되었는지 확인
        assertNull(board.getGrid()[7][4]);
        assertNull(board.getGrid()[7][5]);
        assertNull(board.getGrid()[7][6]);
        
        // y 좌표가 증가했는지 확인
        assertEquals(6, block.getY());
    }
    
    @Test
    void testMoveDownErasesOnlyBelowBlocks() {
        // WeightBlock 바로 아래 블록만 삭제 (다른 위치는 유지)
        // x=3, y=5일 때 블록 위치: (4,5),(5,5) / (3,6),(4,6),(5,6),(6,6)
        WeightBlock block = new WeightBlock(3, 5);
        
        // 여러 위치에 블록 배치
        // y=6 아래인 y=7 위치의 블록들: (3,7),(4,7),(5,7),(6,7)
        board.getGrid()[7][3] = ShapeType.I; // 아래 (삭제됨)
        board.getGrid()[7][4] = ShapeType.O; // 아래 (삭제됨)
        board.getGrid()[8][4] = ShapeType.T; // 더 아래 (삭제 안됨)
        board.getGrid()[5][4] = ShapeType.S; // 위 (삭제 안됨)
        board.getGrid()[7][8] = ShapeType.Z; // 옆 (삭제 안됨)
        
        block.moveDown(board);
        
        // 바로 아래만 삭제됨
        assertNull(board.getGrid()[7][3]);
        assertNull(board.getGrid()[7][4]);
        
        // 다른 위치는 유지됨
        assertNotNull(board.getGrid()[8][4]);
        assertNotNull(board.getGrid()[5][4]);
        assertNotNull(board.getGrid()[7][8]);
    }
    
    @Test
    void testMoveDownMultipleTimes() {
        // 여러 번 내려가면서 각 단계의 블록 삭제
        WeightBlock block = new WeightBlock(3, 5);
        
        // 여러 높이에 블록 배치
        board.getGrid()[7][4] = ShapeType.I;
        board.getGrid()[8][4] = ShapeType.O;
        board.getGrid()[9][4] = ShapeType.T;
        
        // 첫 번째 moveDown
        block.moveDown(board);
        assertEquals(6, block.getY());
        assertNull(board.getGrid()[7][4]); // 첫 번째 블록 삭제됨
        
        // 두 번째 moveDown
        block.moveDown(board);
        assertEquals(7, block.getY());
        assertNull(board.getGrid()[8][4]); // 두 번째 블록 삭제됨
        
        // 세 번째 moveDown
        block.moveDown(board);
        assertEquals(8, block.getY());
        assertNull(board.getGrid()[9][4]); // 세 번째 블록 삭제됨
    }
    
    @Test
    void testEraseBlocksAtBoundary() {
        // 경계 조건: 바닥 근처에서 블록 삭제
        WeightBlock block = new WeightBlock(3, 17);
        
        // 바닥 근처에 블록 배치
        board.getGrid()[19][4] = ShapeType.I;
        board.getGrid()[19][5] = ShapeType.O;
        
        block.moveDown(board);
        
        // 블록이 삭제됨
        assertNull(board.getGrid()[19][4]);
        assertNull(board.getGrid()[19][5]);
    }
    
    @Test
    void testCanMoveHorizontalVariousCases() {
        // canMoveHorizontal의 다양한 경우 테스트
        WeightBlock block = new WeightBlock(5, 10);
        
        // 왼쪽으로 이동 가능
        block.moveLeft(board);
        assertEquals(4, block.getX());
        
        // 오른쪽으로 이동 가능 (x=4에서 x=5로)
        block.moveRight(board);
        assertEquals(5, block.getX());
        
        // 블록 배치 후 이동 불가 (x=5일 때 y=11의 x=5 위치에 블록)
        board.getGrid()[11][5] = ShapeType.I;
        int beforeX = block.getX();
        block.moveLeft(board);
        assertEquals(beforeX, block.getX()); // 이동 안됨
    }
    
    @Test
    void testNoRotation() {
        // ItemBlock의 rotate() 메서드가 실제로 아무것도 하지 않는지 확인
        WeightBlock block = new WeightBlock(3, 5);
        int xBefore = block.getX();
        int yBefore = block.getY();
        Position[] blocksBefore = block.getBlocks();
        
        block.rotate();
        
        assertEquals(xBefore, block.getX());
        assertEquals(yBefore, block.getY());
        assertArrayEquals(blocksBefore, block.getBlocks());
    }
    
    @Test
    void testIsLockedHorizontalInitiallyFalse() {
        // 초기 상태에서 lockedHorizontal은 false
        WeightBlock block = new WeightBlock(3, 5);
        assertFalse(isLockedHorizontal(block));
    }
    
    @Test
    void testEraseAtBottomBoundary() {
        // 경계 조건: by >= Board.ROWS인 경우 (삭제하지 않음)
        // y=19일 때 y+1=20이므로 블록 삭제를 시도하지 않음
        WeightBlock block = new WeightBlock(3, 19);
        
        // moveDown 호출 시 eraseBelowOne이 호출되지만 by >= ROWS이므로 아무것도 삭제 안됨
        // 이 케이스로 eraseBelowOne의 by < Board.ROWS 브랜치를 커버
        assertTrue(block.moveDown(board)); // 바닥에 닿아 true 반환
    }
    
    // WeightBlock의 lockedHorizontal 상태를 리플렉션으로 확인
    private boolean isLockedHorizontal(WeightBlock block) {
        try {
            java.lang.reflect.Field f = ItemBlock.class.getDeclaredField("lockedHorizontal");
            f.setAccessible(true);
            return f.getBoolean(block);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

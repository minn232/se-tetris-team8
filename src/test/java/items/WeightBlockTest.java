package items;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;
import core.Position;
import core.ShapeType;

@DisplayName("WeightBlock 테스트")
public class WeightBlockTest {

    private WeightBlock weightBlock;

    @BeforeEach
    void setUp() {
        weightBlock = new WeightBlock();
    }

    @Test
    @DisplayName("기본 속성 테스트")
    void testBasicProperties() {
        assertEquals("Weight", weightBlock.getName());
        assertEquals(Color.WHITE, weightBlock.getColor());
        assertEquals(ShapeType.I, weightBlock.getBaseShape());
        assertEquals("낙하하면서 아래 블록을 삭제합니다", weightBlock.getDescription());
        assertEquals('W', weightBlock.getSymbol());
    }

    @Test
    @DisplayName("getCustomBlocks - 6개 블록 반환")
    void testGetCustomBlocks() {
        Position[] blocks = weightBlock.getCustomBlocks();
        assertNotNull(blocks);
        assertEquals(6, blocks.length);
    }

    @Test
    @DisplayName("getBlocks - getCustomBlocks와 동일")
    void testGetBlocks() {
        Position[] customBlocks = weightBlock.getCustomBlocks();
        Position[] blocks = weightBlock.getBlocks();
        assertSame(customBlocks, blocks);
    }

    @Test
    @DisplayName("getBlockSymbol - 모든 인덱스에서 'W' 반환")
    void testGetBlockSymbol() {
        assertEquals('W', weightBlock.getBlockSymbol(0));
        assertEquals('W', weightBlock.getBlockSymbol(1));
        assertEquals('W', weightBlock.getBlockSymbol(5));
    }

    @Test
    @DisplayName("lockedHorizontal - 초기값 false")
    void testLockedHorizontalInitial() {
        assertFalse(weightBlock.isLockedHorizontal());
    }

    @Test
    @DisplayName("setLockedHorizontal - 값 설정")
    void testSetLockedHorizontal() {
        weightBlock.setLockedHorizontal(true);
        assertTrue(weightBlock.isLockedHorizontal());
        
        weightBlock.setLockedHorizontal(false);
        assertFalse(weightBlock.isLockedHorizontal());
    }

    @Test
    @DisplayName("activateEffect - 배치 시 효과 없음")
    void testActivateEffect() {
        Board board = new Board(Difficulty.EASY);
        assertDoesNotThrow(() -> weightBlock.activateEffect(board, 0, 0));
    }

    @Test
    @DisplayName("eraseBelowBlocks - 아래 블록 삭제")
    void testEraseBelowBlocks() {
        Board board = new Board(Difficulty.EASY);
        ShapeType[][] grid = board.getGrid();
        
        // CUSTOM_BLOCKS: (1,0), (2,0), (0,1), (1,1), (2,1), (3,1)
        // currentX=0, currentY=9일 때:
        // - p.y=0 블록들(1,0),(2,0): by=9+0+1=10 → grid[10][1], grid[10][2] 삭제
        // - p.y=1 블록들(0,1),(1,1),(2,1),(3,1): by=9+1+1=11 → grid[11][0,1,2,3] 삭제
        grid[10][1] = ShapeType.I;
        grid[10][2] = ShapeType.I;
        grid[11][0] = ShapeType.I;
        grid[11][1] = ShapeType.I;
        grid[11][2] = ShapeType.I;
        grid[11][3] = ShapeType.I;
        
        weightBlock.eraseBelowBlocks(board, 0, 9);
        
        assertNull(grid[10][1]);
        assertNull(grid[10][2]);
        assertNull(grid[11][0]);
        assertNull(grid[11][1]);
        assertNull(grid[11][2]);
        assertNull(grid[11][3]);
    }

    @Test
    @DisplayName("eraseBelowBlocks - 경계 처리")
    void testEraseBelowBlocksBoundary() {
        Board board = new Board(Difficulty.EASY);
        assertDoesNotThrow(() -> weightBlock.eraseBelowBlocks(board, -1, 0));
        assertDoesNotThrow(() -> weightBlock.eraseBelowBlocks(board, 8, 19));
        assertDoesNotThrow(() -> weightBlock.eraseBelowBlocks(board, 0, -1));
    }

    @Test
    @DisplayName("eraseAllBlocksInPath - 경로상 모든 블록 삭제")
    void testEraseAllBlocksInPath() {
        Board board = new Board(Difficulty.EASY);
        ShapeType[][] grid = board.getGrid();
        
        // CUSTOM_BLOCKS: (1,0), (2,0), (0,1), (1,1), (2,1), (3,1)
        // startX=0, startY=5, endY=14일 때:
        // - block.y=0 블록들: y=5~14, x=1,2
        // - block.y=1 블록들: y=6~15, x=0,1,2,3
        // 경로에 블록 채우기
        for (int y = 5; y <= 15; y++) {
            for (int x = 0; x < 4; x++) {
                grid[y][x] = ShapeType.I;
            }
        }
        
        weightBlock.eraseAllBlocksInPath(board, 0, 5, 14);
        
        // y=5: x=1,2 삭제 (block.y=0)
        assertNull(grid[5][1]);
        assertNull(grid[5][2]);
        // y=6~14: x=0,1,2,3 모두 삭제 (block.y=0,1 모두)
        for (int y = 6; y <= 14; y++) {
            assertNull(grid[y][0]);
            assertNull(grid[y][1]);
            assertNull(grid[y][2]);
            assertNull(grid[y][3]);
        }
        // y=15: x=0,1,2,3 삭제 (block.y=1)
        assertNull(grid[15][0]);
        assertNull(grid[15][1]);
        assertNull(grid[15][2]);
        assertNull(grid[15][3]);
    }

    @Test
    @DisplayName("eraseAllBlocksInPath - 경계 처리")
    void testEraseAllBlocksInPathBoundary() {
        Board board = new Board(Difficulty.EASY);
        assertDoesNotThrow(() -> weightBlock.eraseAllBlocksInPath(board, -1, 0, 10));
        assertDoesNotThrow(() -> weightBlock.eraseAllBlocksInPath(board, 8, -1, 5));
        assertDoesNotThrow(() -> weightBlock.eraseAllBlocksInPath(board, 0, 0, 25));
    }
}

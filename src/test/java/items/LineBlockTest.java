package items;
import java.awt.Color;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;
import core.ShapeType;

/**
 * LineBlock 클래스에 대한 간결한 테스트
 */
public class LineBlockTest {
    
    private LineBlock lineBlock;
    private Board board;
    
    @BeforeEach
    void setUp() {
        lineBlock = new LineBlock();
        board = new Board(Difficulty.NORMAL, false);
    }
    
    // ===== 기본 메서드 테스트 =====
    
    @Test
    @DisplayName("기본 속성 테스트")
    void testBasicProperties() {
        assertEquals("Line", lineBlock.getName());
        assertEquals(new Color(100, 255, 100), lineBlock.getColor());
        assertEquals("전체 가로줄을 제거합니다", lineBlock.getDescription());
        assertEquals('L', lineBlock.getSymbol());
        assertNotNull(lineBlock.getBaseShape());
    }
    
    // ===== getBaseShape 테스트 =====
    
    @Test
    @DisplayName("getBaseShape - SPAWNABLE_SHAPES 확인")
    void testGetBaseShape() {
        for (int i = 0; i < 20; i++) {
            LineBlock block = new LineBlock();
            ShapeType shape = block.getBaseShape();
            assertNotEquals(ShapeType.GRAY, shape);
        }
    }
    
    // ===== rotation 테스트 =====
    
    @Test
    @DisplayName("rotation - get/set")
    void testRotation() {
        assertEquals(0, lineBlock.getRotation());
        
        lineBlock.setRotation(1);
        assertEquals(1, lineBlock.getRotation());
        
        lineBlock.setRotation(3);
        assertEquals(3, lineBlock.getRotation());
    }
    
    // ===== getLineBlockIndex 테스트 =====
    
    @Test
    @DisplayName("getLineBlockIndex - rotation 변경")
    void testGetLineBlockIndex() {
        for (int r = 0; r < 4; r++) {
            lineBlock.setRotation(r);
            int index = lineBlock.getLineBlockIndex();
            assertTrue(index >= 0);
        }
    }
    
    // ===== getBlockSymbol 테스트 =====
    
    @Test
    @DisplayName("getBlockSymbol - L 위치")
    void testGetBlockSymbol() {
        int lineIndex = lineBlock.getLineBlockIndex();
        assertEquals('L', lineBlock.getBlockSymbol(lineIndex));
        
        // 다른 위치는 ' '
        ShapeType shape = lineBlock.getBaseShape();
        for (int i = 0; i < shape.getOffsets(0).length; i++) {
            if (i != lineIndex) {
                assertEquals(' ', lineBlock.getBlockSymbol(i));
            }
        }
    }
    
    // ===== activateEffect 테스트 =====
    
    @Test
    @DisplayName("activateEffect - null/비Board 처리")
    void testActivateEffect() {
        assertDoesNotThrow(() -> lineBlock.activateEffect(null, 5, 5));
        assertDoesNotThrow(() -> lineBlock.activateEffect("not a board", 5, 5));
    }
    
    // ===== activateEffectAtPosition 테스트 =====
    
    @Test
    @DisplayName("activateEffectAtPosition - null 처리")
    void testActivateEffectAtPositionNull() {
        assertDoesNotThrow(() -> lineBlock.activateEffectAtPosition(null, 5, 5, 0));
    }
    
    @Test
    @DisplayName("activateEffectAtPosition - 라인 삭제")
    void testActivateEffectAtPositionClearLine() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 10번째 줄 채우기
        for (int x = 0; x < Board.COLS; x++) {
            grid[10][x] = ShapeType.I;
        }
        
        // L 블록 위치에서 효과 발동
        int lineIndex = lineBlock.getLineBlockIndex();
        lineBlock.activateEffectAtPosition(board, 5, 10, lineIndex);
        
        // 10번째 줄이 null로 변경되었는지 확인
        for (int x = 0; x < Board.COLS; x++) {
            assertNull(grid[10][x]);
        }
    }
    
    @Test
    @DisplayName("activateEffectAtPosition - 비L 위치에서는 무시")
    void testActivateEffectAtPositionNonLineBlock() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 10번째 줄 채우기
        for (int x = 0; x < Board.COLS; x++) {
            grid[10][x] = ShapeType.T;
        }
        
        // L이 아닌 위치에서 효과 발동 시도
        int lineIndex = lineBlock.getLineBlockIndex();
        int nonLineIndex = (lineIndex + 1) % 4;
        lineBlock.activateEffectAtPosition(board, 5, 10, nonLineIndex);
        
        // 줄이 그대로 유지되어야 함
        for (int x = 0; x < Board.COLS; x++) {
            assertEquals(ShapeType.T, grid[10][x]);
        }
    }
    
    // ===== 통합 테스트 =====
    
    @Test
    @DisplayName("통합 - rotation과 LineBlockIndex 일관성")
    void testIntegrationRotationConsistency() {
        for (int r = 0; r < 4; r++) {
            lineBlock.setRotation(r);
            int index = lineBlock.getLineBlockIndex();
            
            assertEquals('L', lineBlock.getBlockSymbol(index));
            assertTrue(index >= 0);
            assertTrue(index < lineBlock.getBaseShape().getOffsets(r).length);
        }
    }
}

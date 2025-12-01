package items;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;
import core.ShapeType;

/**
 * SlowBlock 클래스에 대한 간결한 테스트
 */
public class SlowBlockTest {
    
    private SlowBlock slowBlock;
    private Board board;
    
    @BeforeEach
    void setUp() {
        slowBlock = new SlowBlock();
        board = new Board(Difficulty.NORMAL, false);
    }
    
    // ===== 기본 속성 테스트 =====
    
    @Test
    @DisplayName("기본 속성 테스트")
    void testBasicProperties() {
        assertEquals("Slow", slowBlock.getName());
        assertEquals(new Color(173, 216, 230), slowBlock.getColor());
        assertEquals("10초간 게임 속도를 0.5배로 감소시킵니다", slowBlock.getDescription());
        assertEquals('S', slowBlock.getSymbol());
        assertNotNull(slowBlock.getBaseShape());
    }
    
    // ===== getBaseShape 테스트 =====
    
    @Test
    @DisplayName("getBaseShape - GRAY 제외")
    void testGetBaseShape() {
        for (int i = 0; i < 20; i++) {
            SlowBlock block = new SlowBlock();
            assertNotEquals(ShapeType.GRAY, block.getBaseShape());
        }
    }
    
    // ===== getBlockSymbol 테스트 =====
    
    @Test
    @DisplayName("getBlockSymbol - 0번 인덱스만 S")
    void testGetBlockSymbol() {
        assertEquals('S', slowBlock.getBlockSymbol(0));
        assertEquals(' ', slowBlock.getBlockSymbol(1));
        assertEquals(' ', slowBlock.getBlockSymbol(2));
        assertEquals(' ', slowBlock.getBlockSymbol(3));
    }
    
    // ===== activateEffect 테스트 =====
    
    @Test
    @DisplayName("activateEffect - null 처리")
    void testActivateEffectNull() {
        assertDoesNotThrow(() -> slowBlock.activateEffect(null, 5, 5));
    }
    
    @Test
    @DisplayName("activateEffect - 비Board 처리")
    void testActivateEffectNonBoard() {
        assertDoesNotThrow(() -> slowBlock.activateEffect("not a board", 5, 5));
    }
    
    @Test
    @DisplayName("activateEffect - Board에 슬로우 효과 발동")
    void testActivateEffectOnBoard() {
        assertDoesNotThrow(() -> slowBlock.activateEffect(board, 5, 10));
    }
}

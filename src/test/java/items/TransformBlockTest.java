package items;
import java.awt.Color;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Board;
import core.ShapeType;

@DisplayName("TransformBlock 테스트")
class TransformBlockTest {

    private TransformBlock transformBlock;

    @BeforeEach
    void setUp() {
        transformBlock = new TransformBlock();
    }

    @Test
    @DisplayName("기본 속성 테스트")
    void testBasicProperties() {
        assertEquals("TransformToI", transformBlock.getName());
        assertEquals(new Color(240, 240, 240), transformBlock.getColor());
        assertEquals("다음 5개의 블록을 I 블록으로 변환합니다.", transformBlock.getDescription());
        assertEquals('T', transformBlock.getSymbol());
    }

    @Test
    @DisplayName("getBaseShape - SPAWNABLE_SHAPES 중 하나 반환")
    void testGetBaseShape() throws Exception {
        Field spawnableShapesField = TransformBlock.class.getDeclaredField("SPAWNABLE_SHAPES");
        spawnableShapesField.setAccessible(true);
        ShapeType[] spawnableShapes = (ShapeType[]) spawnableShapesField.get(null);

        ShapeType baseShape = transformBlock.getBaseShape();
        assertNotNull(baseShape);
        
        boolean isSpawnable = false;
        for (ShapeType shape : spawnableShapes) {
            if (shape == baseShape) {
                isSpawnable = true;
                break;
            }
        }
        assertTrue(isSpawnable);
        assertNotEquals(ShapeType.GRAY, baseShape);
    }

    @Test
    @DisplayName("getBaseShape - 랜덤성 검증")
    void testGetBaseShapeRandomness() {
        boolean foundDifferent = false;
        ShapeType firstShape = new TransformBlock().getBaseShape();
        
        for (int i = 0; i < 50; i++) {
            TransformBlock block = new TransformBlock();
            if (block.getBaseShape() != firstShape) {
                foundDifferent = true;
                break;
            }
        }
        
        assertTrue(foundDifferent, "50번 생성 중 다른 ShapeType이 하나도 나오지 않음");
    }

    @Test
    @DisplayName("getBlockSymbol - 첫 블록은 'T', 나머지는 공백")
    void testGetBlockSymbol() {
        assertEquals('T', transformBlock.getBlockSymbol(0));
        assertEquals(' ', transformBlock.getBlockSymbol(1));
        assertEquals(' ', transformBlock.getBlockSymbol(2));
        assertEquals(' ', transformBlock.getBlockSymbol(3));
    }

    @Test
    @DisplayName("activateEffect - null 처리")
    void testActivateEffectNull() {
        assertDoesNotThrow(() -> transformBlock.activateEffect(null, 0, 0));
    }

    @Test
    @DisplayName("activateEffect - Board 아닌 객체 처리")
    void testActivateEffectNonBoard() {
        Object notBoard = new Object();
        assertDoesNotThrow(() -> transformBlock.activateEffect(notBoard, 0, 0));
    }

    @Test
    @DisplayName("activateEffect - Board에 변환 효과 활성화")
    void testActivateEffectOnBoard() throws Exception {
        Board board = new Board(core.Difficulty.EASY);
        
        transformBlock.activateEffect(board, 0, 0);
        
        Field transformRemainingBlocksField = Board.class.getDeclaredField("transformRemainingBlocks");
        transformRemainingBlocksField.setAccessible(true);
        int transformRemainingBlocks = (int) transformRemainingBlocksField.get(board);
        
        assertEquals(5, transformRemainingBlocks);
    }
}

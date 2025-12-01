package items;
import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ItemShapeType enum에 대한 간결한 테스트
 */
public class ItemShapeTypeTest {
    
    // ===== getColor 테스트 =====
    
    @Test
    @DisplayName("getColor - 모든 타입")
    void testGetColor() {
        assertEquals(new Color(100, 255, 100), ItemShapeType.LINE.getColor());
        assertEquals(new Color(173, 216, 230), ItemShapeType.SLOW.getColor());
        assertEquals(new Color(255, 100, 100), ItemShapeType.BOMB.getColor());
        assertEquals(Color.WHITE, ItemShapeType.WEIGHT.getColor());
    }
    
    // ===== getSymbol 테스트 =====
    
    @Test
    @DisplayName("getSymbol - 모든 타입")
    void testGetSymbol() {
        assertEquals('L', ItemShapeType.LINE.getSymbol());
        assertEquals('S', ItemShapeType.SLOW.getSymbol());
        assertEquals('B', ItemShapeType.BOMB.getSymbol());
        assertEquals('W', ItemShapeType.WEIGHT.getSymbol());
    }
    
    // ===== fromItemBlock 테스트 =====
    
    @Test
    @DisplayName("fromItemBlock - LineBlock")
    void testFromItemBlockLine() {
        ItemBlock item = new LineBlock();
        assertEquals(ItemShapeType.LINE, ItemShapeType.fromItemBlock(item));
    }
    
    @Test
    @DisplayName("fromItemBlock - SlowBlock")
    void testFromItemBlockSlow() {
        ItemBlock item = new SlowBlock();
        assertEquals(ItemShapeType.SLOW, ItemShapeType.fromItemBlock(item));
    }
    
    @Test
    @DisplayName("fromItemBlock - BombBlock")
    void testFromItemBlockBomb() {
        ItemBlock item = new BombBlock();
        assertEquals(ItemShapeType.BOMB, ItemShapeType.fromItemBlock(item));
    }
    
    @Test
    @DisplayName("fromItemBlock - WeightBlock")
    void testFromItemBlockWeight() {
        ItemBlock item = new WeightBlock();
        assertEquals(ItemShapeType.WEIGHT, ItemShapeType.fromItemBlock(item));
    }
    
    @Test
    @DisplayName("fromItemBlock - default case")
    void testFromItemBlockDefault() {
        // 알 수 없는 symbol은 LINE을 반환
        ItemBlock unknownItem = new ItemBlock() {
            public String getName() { return "Unknown"; }
            public Color getColor() { return Color.BLACK; }
            public core.ShapeType getBaseShape() { return core.ShapeType.I; }
            public void activateEffect(Object board, int x, int y) {}
            public String getDescription() { return "Unknown"; }
            public char getSymbol() { return 'X'; }
        };
        
        assertEquals(ItemShapeType.LINE, ItemShapeType.fromItemBlock(unknownItem));
    }
    
    // ===== enum values 테스트 =====
    
    @Test
    @DisplayName("values - 4개 enum 상수")
    void testValues() {
        ItemShapeType[] values = ItemShapeType.values();
        assertEquals(4, values.length);
    }
    
    @Test
    @DisplayName("valueOf - 이름으로 조회")
    void testValueOf() {
        assertEquals(ItemShapeType.LINE, ItemShapeType.valueOf("LINE"));
        assertEquals(ItemShapeType.SLOW, ItemShapeType.valueOf("SLOW"));
        assertEquals(ItemShapeType.BOMB, ItemShapeType.valueOf("BOMB"));
        assertEquals(ItemShapeType.WEIGHT, ItemShapeType.valueOf("WEIGHT"));
    }
}

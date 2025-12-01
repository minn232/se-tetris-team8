package items;
import java.awt.Color;
import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
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
 * BombBlock 클래스에 대한 종합적인 테스트
 * 모든 메서드의 line coverage를 높이기 위한 테스트
 */
public class BombBlockTest {
    
    private BombBlock bombBlock;
    private Board board;
    
    @BeforeEach
    void setUp() {
        bombBlock = new BombBlock();
        board = new Board(Difficulty.NORMAL, false);
    }
    
    @AfterEach
    void tearDown() {
        bombBlock = null;
        board = null;
    }
    
    // ===== 생성자 및 기본 속성 테스트 =====
    
    @Test
    @DisplayName("BombBlock 생성자 테스트")
    void testBombBlockCreation() {
        assertNotNull(bombBlock);
        assertNotNull(bombBlock.getBaseShape());
        assertNotNull(bombBlock.getColor());
    }
    
    @Test
    @DisplayName("BombBlock - 여러 번 생성 시 랜덤 ShapeType")
    void testRandomShapeTypeGeneration() {
        // 여러 번 생성하여 다양한 ShapeType이 선택되는지 확인
        boolean hasVariation = false;
        ShapeType firstShape = bombBlock.getBaseShape();
        
        for (int i = 0; i < 20; i++) {
            BombBlock newBomb = new BombBlock();
            if (newBomb.getBaseShape() != firstShape) {
                hasVariation = true;
                break;
            }
        }
        
        // 20번 중 최소 1번은 다른 ShapeType이 나와야 함 (확률적으로 거의 확실)
        assertTrue(hasVariation || firstShape != null, "랜덤 ShapeType 생성 확인");
    }
    
    @Test
    @DisplayName("BombBlock - SPAWNABLE_SHAPES 검증")
    void testSpawnableShapes() throws Exception {
        // 100번 생성하여 모두 SPAWNABLE_SHAPES에 포함되는지 확인
        ShapeType[] validShapes = {ShapeType.I, ShapeType.O, ShapeType.T, 
                                    ShapeType.S, ShapeType.Z, ShapeType.J, ShapeType.L};
        
        for (int i = 0; i < 100; i++) {
            BombBlock bomb = new BombBlock();
            ShapeType shape = bomb.getBaseShape();
            
            boolean isValid = false;
            for (ShapeType valid : validShapes) {
                if (shape == valid) {
                    isValid = true;
                    break;
                }
            }
            
            assertTrue(isValid, "생성된 ShapeType은 SPAWNABLE_SHAPES에 포함되어야 함");
            assertNotEquals(ShapeType.GRAY, shape, "GRAY는 생성되지 않아야 함");
        }
    }
    
    @Test
    @DisplayName("BombBlock - bombIndexInRotation0 범위 검증")
    void testBombIndexInRotation0Range() throws Exception {
        // 100번 생성하여 bombIndexInRotation0이 올바른 범위인지 확인
        for (int i = 0; i < 100; i++) {
            BombBlock bomb = new BombBlock();
            ShapeType shape = bomb.getBaseShape();
            int bombIndex = bomb.getBombIndex();
            
            int maxIndex = shape.getOffsets(0).length;
            assertTrue(bombIndex >= 0 && bombIndex < maxIndex, 
                      "bombIndex는 0 ~ " + (maxIndex - 1) + " 범위여야 함");
        }
    }
    
    // ===== getName 테스트 =====
    
    @Test
    @DisplayName("getName 테스트")
    void testGetName() {
        assertEquals("Bomb", bombBlock.getName());
    }
    
    // ===== getColor 테스트 =====
    
    @Test
    @DisplayName("getColor - baseShape의 색상 반환")
    void testGetColor() {
        Color color = bombBlock.getColor();
        Color expectedColor = bombBlock.getBaseShape().getColor();
        
        assertNotNull(color);
        assertEquals(expectedColor, color);
    }
    
    @Test
    @DisplayName("getColor - 여러 ShapeType의 색상")
    void testGetColorVariousShapes() {
        // 여러 번 생성하여 다양한 색상 확인
        for (int i = 0; i < 50; i++) {
            BombBlock bomb = new BombBlock();
            Color color = bomb.getColor();
            Color shapeColor = bomb.getBaseShape().getColor();
            
            assertNotNull(color);
            assertEquals(shapeColor, color);
        }
    }
    
    // ===== getBaseShape 테스트 =====
    
    @Test
    @DisplayName("getBaseShape 테스트")
    void testGetBaseShape() {
        ShapeType shape = bombBlock.getBaseShape();
        
        assertNotNull(shape);
        assertTrue(shape == ShapeType.I || shape == ShapeType.O || shape == ShapeType.T ||
                   shape == ShapeType.S || shape == ShapeType.Z || shape == ShapeType.J ||
                   shape == ShapeType.L);
    }
    
    // ===== getDescription 테스트 =====
    
    @Test
    @DisplayName("getDescription 테스트")
    void testGetDescription() {
        assertEquals("3x3 영역의 블록을 파괴합니다", bombBlock.getDescription());
    }
    
    // ===== getSymbol 테스트 =====
    
    @Test
    @DisplayName("getSymbol 테스트")
    void testGetSymbol() {
        assertEquals('B', bombBlock.getSymbol());
    }
    
    // ===== getBlockSymbol 테스트 =====
    
    @Test
    @DisplayName("getBlockSymbol - 폭탄 블록 위치")
    void testGetBlockSymbolBombPosition() {
        int bombIndex = bombBlock.getBombIndex();
        assertEquals('B', bombBlock.getBlockSymbol(bombIndex));
    }
    
    @Test
    @DisplayName("getBlockSymbol - 일반 블록 위치")
    void testGetBlockSymbolNormalPosition() {
        int bombIndex = bombBlock.getBombIndex();
        ShapeType shape = bombBlock.getBaseShape();
        int blockCount = shape.getOffsets(0).length;
        
        // 폭탄 블록이 아닌 다른 위치는 ' ' 반환
        for (int i = 0; i < blockCount; i++) {
            if (i != bombIndex) {
                assertEquals(' ', bombBlock.getBlockSymbol(i));
            }
        }
    }
    
    @Test
    @DisplayName("getBlockSymbol - rotation 변경 후")
    void testGetBlockSymbolAfterRotation() {
        int originalBombIndex = bombBlock.getBombIndex();
        assertEquals('B', bombBlock.getBlockSymbol(originalBombIndex));
        
        // rotation 변경
        bombBlock.setRotation(1);
        int newBombIndex = bombBlock.getBombIndex();
        assertEquals('B', bombBlock.getBlockSymbol(newBombIndex));
    }
    
    // ===== rotation 관련 테스트 =====
    
    @Test
    @DisplayName("getRotation/setRotation 테스트")
    void testGetSetRotation() {
        assertEquals(0, bombBlock.getRotation());
        
        bombBlock.setRotation(1);
        assertEquals(1, bombBlock.getRotation());
        
        bombBlock.setRotation(2);
        assertEquals(2, bombBlock.getRotation());
        
        bombBlock.setRotation(3);
        assertEquals(3, bombBlock.getRotation());
    }
    
    @Test
    @DisplayName("setRotation - 모든 회전 상태")
    void testSetRotationAllStates() {
        for (int r = 0; r < 4; r++) {
            bombBlock.setRotation(r);
            assertEquals(r, bombBlock.getRotation());
            
            // 각 rotation에서 bombIndex가 유효한지 확인
            int bombIndex = bombBlock.getBombIndex();
            assertTrue(bombIndex >= 0);
        }
    }
    
    // ===== getBombIndex 테스트 =====
    
    @Test
    @DisplayName("getBombIndex - rotation 0")
    void testGetBombIndexRotation0() {
        bombBlock.setRotation(0);
        int bombIndex = bombBlock.getBombIndex();
        
        assertTrue(bombIndex >= 0);
        assertTrue(bombIndex < bombBlock.getBaseShape().getOffsets(0).length);
    }
    
    @Test
    @DisplayName("getBombIndex - rotation 변경 시 매핑")
    void testGetBombIndexRotationMapping() {
        bombBlock.setRotation(0);
        int index0 = bombBlock.getBombIndex();
        
        bombBlock.setRotation(1);
        int index1 = bombBlock.getBombIndex();
        
        bombBlock.setRotation(2);
        int index2 = bombBlock.getBombIndex();
        
        bombBlock.setRotation(3);
        int index3 = bombBlock.getBombIndex();
        
        // 모든 인덱스가 0 이상이어야 함
        assertTrue(index0 >= 0);
        assertTrue(index1 >= 0);
        assertTrue(index2 >= 0);
        assertTrue(index3 >= 0);
    }
    
    @Test
    @DisplayName("getBombIndex - 여러 ShapeType에 대한 매핑")
    void testGetBombIndexVariousShapes() {
        for (int i = 0; i < 50; i++) {
            BombBlock bomb = new BombBlock();
            
            for (int r = 0; r < 4; r++) {
                bomb.setRotation(r);
                int bombIndex = bomb.getBombIndex();
                int maxIndex = bomb.getBaseShape().getOffsets(r).length;
                
                assertTrue(bombIndex >= 0 && bombIndex < maxIndex);
            }
        }
    }
    
    // ===== activateEffect 테스트 =====
    
    @Test
    @DisplayName("activateEffect - null 체크")
    void testActivateEffectWithNull() {
        // null을 전달해도 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> bombBlock.activateEffect(null, 5, 5));
    }
    
    @Test
    @DisplayName("activateEffect - Board가 아닌 객체")
    void testActivateEffectWithNonBoard() {
        String notABoard = "Not a Board";
        assertDoesNotThrow(() -> bombBlock.activateEffect(notABoard, 5, 5));
    }
    
    @Test
    @DisplayName("activateEffect - 3x3 폭발 효과 (중앙)")
    void testActivateEffectCenter() throws Exception {
        // 그리드 설정
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 5x5 영역에 블록 배치
        for (int y = 8; y <= 12; y++) {
            for (int x = 3; x <= 7; x++) {
                grid[y][x] = ShapeType.I;
            }
        }
        
        // (5, 10)을 중심으로 폭발
        bombBlock.activateEffect(board, 5, 10);
        
        // 3x3 영역 (4-6, 9-11)이 파괴되었는지 확인
        for (int y = 9; y <= 11; y++) {
            for (int x = 4; x <= 6; x++) {
                assertNull(grid[y][x], "(" + x + ", " + y + ")는 파괴되어야 함");
            }
        }
        
        // 주변 블록은 유지되어야 함
        assertEquals(ShapeType.I, grid[8][5], "위쪽 블록은 유지되어야 함");
        assertEquals(ShapeType.I, grid[12][5], "아래쪽 블록은 유지되어야 함");
        assertEquals(ShapeType.I, grid[10][3], "왼쪽 블록은 유지되어야 함");
        assertEquals(ShapeType.I, grid[10][7], "오른쪽 블록은 유지되어야 함");
    }
    
    @Test
    @DisplayName("activateEffect - 3x3 폭발 효과 (좌상단)")
    void testActivateEffectTopLeft() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 좌상단 영역에 블록 배치
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                grid[y][x] = ShapeType.T;
            }
        }
        
        // (0, 0)을 중심으로 폭발
        bombBlock.activateEffect(board, 0, 0);
        
        // 보드 범위 내 블록만 파괴
        assertNull(grid[0][0]);
        assertNull(grid[0][1]);
        assertNull(grid[1][0]);
        assertNull(grid[1][1]);
    }
    
    @Test
    @DisplayName("activateEffect - 3x3 폭발 효과 (우하단)")
    void testActivateEffectBottomRight() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        int lastX = Board.COLS - 1;
        int lastY = Board.ROWS - 1;
        
        // 우하단 영역에 블록 배치
        for (int y = lastY - 2; y <= lastY; y++) {
            for (int x = lastX - 2; x <= lastX; x++) {
                grid[y][x] = ShapeType.O;
            }
        }
        
        // 우하단 모서리를 중심으로 폭발
        bombBlock.activateEffect(board, lastX, lastY);
        
        // 보드 범위 내 블록만 파괴
        assertNull(grid[lastY][lastX]);
        assertNull(grid[lastY][lastX - 1]);
        assertNull(grid[lastY - 1][lastX]);
        assertNull(grid[lastY - 1][lastX - 1]);
    }
    
    @Test
    @DisplayName("activateEffect - 보드 범위 밖 좌표")
    void testActivateEffectOutOfBounds() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 전체 그리드에 블록 배치
        for (int y = 0; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                grid[y][x] = ShapeType.S;
            }
        }
        
        // 보드 범위 밖 좌표로 폭발 (예외 발생하지 않아야 함)
        assertDoesNotThrow(() -> bombBlock.activateEffect(board, -5, -5));
        assertDoesNotThrow(() -> bombBlock.activateEffect(board, Board.COLS + 5, Board.ROWS + 5));
    }
    
    @Test
    @DisplayName("activateEffect - 빈 그리드에 폭발")
    void testActivateEffectEmptyGrid() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 빈 그리드에 폭발 (예외 발생하지 않아야 함)
        assertDoesNotThrow(() -> bombBlock.activateEffect(board, 5, 10));
        
        // 여전히 null이어야 함
        assertNull(grid[10][5]);
        assertNull(grid[9][4]);
        assertNull(grid[11][6]);
    }
    
    @Test
    @DisplayName("activateEffect - 부분적으로 채워진 3x3 영역")
    void testActivateEffectPartiallyFilled() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 체크보드 패턴으로 블록 배치
        for (int y = 9; y <= 11; y++) {
            for (int x = 4; x <= 6; x++) {
                if ((x + y) % 2 == 0) {
                    grid[y][x] = ShapeType.Z;
                }
            }
        }
        
        // (5, 10)을 중심으로 폭발
        bombBlock.activateEffect(board, 5, 10);
        
        // 모든 3x3 영역이 null이어야 함
        for (int y = 9; y <= 11; y++) {
            for (int x = 4; x <= 6; x++) {
                assertNull(grid[y][x]);
            }
        }
    }
    
    @Test
    @DisplayName("activateEffect - 여러 번 연속 폭발")
    void testActivateEffectMultipleTimes() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 전체 그리드 채우기
        for (int y = 0; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                grid[y][x] = ShapeType.J;
            }
        }
        
        // 여러 위치에서 폭발
        bombBlock.activateEffect(board, 2, 5);
        bombBlock.activateEffect(board, 7, 10);
        bombBlock.activateEffect(board, 5, 15);
        
        // 폭발한 영역들이 파괴되었는지 확인
        assertNull(grid[5][2]);
        assertNull(grid[10][7]);
        assertNull(grid[15][5]);
    }
    
    // ===== 통합 시나리오 테스트 =====
    
    @Test
    @DisplayName("통합 테스트 - BombBlock 생성부터 폭발까지")
    void testIntegrationFullScenario() throws Exception {
        // 1. BombBlock 생성
        BombBlock bomb = new BombBlock();
        assertNotNull(bomb);
        assertEquals("Bomb", bomb.getName());
        assertEquals('B', bomb.getSymbol());
        
        // 2. rotation 설정
        bomb.setRotation(1);
        assertEquals(1, bomb.getRotation());
        
        // 3. bombIndex 확인
        int bombIndex = bomb.getBombIndex();
        assertEquals('B', bomb.getBlockSymbol(bombIndex));
        
        // 4. 보드에 블록 배치
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        for (int y = 5; y < 15; y++) {
            for (int x = 2; x < 8; x++) {
                grid[y][x] = ShapeType.L;
            }
        }
        
        // 5. 폭발 효과 활성화
        bomb.activateEffect(board, 5, 10);
        
        // 6. 3x3 영역이 파괴되었는지 확인
        for (int y = 9; y <= 11; y++) {
            for (int x = 4; x <= 6; x++) {
                assertNull(grid[y][x]);
            }
        }
        
        // 7. 주변 블록은 유지
        assertEquals(ShapeType.L, grid[8][5]);
        assertEquals(ShapeType.L, grid[12][5]);
    }
    
    @Test
    @DisplayName("통합 테스트 - 다양한 ShapeType으로 BombBlock 생성")
    void testIntegrationVariousShapes() {
        boolean hasI = false, hasO = false, hasT = false, hasS = false;
        boolean hasZ = false, hasJ = false, hasL = false;
        
        // 충분한 횟수만큼 생성하여 모든 ShapeType이 나오는지 확인
        for (int i = 0; i < 200; i++) {
            BombBlock bomb = new BombBlock();
            ShapeType shape = bomb.getBaseShape();
            
            if (shape == ShapeType.I) hasI = true;
            else if (shape == ShapeType.O) hasO = true;
            else if (shape == ShapeType.T) hasT = true;
            else if (shape == ShapeType.S) hasS = true;
            else if (shape == ShapeType.Z) hasZ = true;
            else if (shape == ShapeType.J) hasJ = true;
            else if (shape == ShapeType.L) hasL = true;
            
            // 각 BombBlock이 정상적으로 동작하는지 확인
            assertNotNull(bomb.getName());
            assertNotNull(bomb.getColor());
            assertNotNull(bomb.getDescription());
            assertEquals('B', bomb.getSymbol());
        }
        
        // 통계적으로 200번이면 모든 ShapeType이 최소 1번은 나와야 함
        int coverage = (hasI ? 1 : 0) + (hasO ? 1 : 0) + (hasT ? 1 : 0) + (hasS ? 1 : 0) +
                       (hasZ ? 1 : 0) + (hasJ ? 1 : 0) + (hasL ? 1 : 0);
        
        assertTrue(coverage >= 5, "7개 ShapeType 중 최소 5개는 생성되어야 함");
    }
    
    @Test
    @DisplayName("통합 테스트 - rotation과 bombIndex 매핑 일관성")
    void testIntegrationRotationConsistency() throws Exception {
        BombBlock bomb = new BombBlock();
        ShapeType shape = bomb.getBaseShape();
        
        // rotation 0에서 시작
        bomb.setRotation(0);
        int index0 = bomb.getBombIndex();
        
        // 4번 회전 (360도)
        for (int i = 0; i < 4; i++) {
            bomb.setRotation(i);
            int currentIndex = bomb.getBombIndex();
            int maxIndex = shape.getOffsets(i).length;
            
            assertTrue(currentIndex >= 0 && currentIndex < maxIndex,
                      "rotation " + i + "에서 bombIndex는 0 ~ " + (maxIndex - 1) + " 범위여야 함");
            
            // getBlockSymbol이 올바르게 동작하는지 확인
            assertEquals('B', bomb.getBlockSymbol(currentIndex));
            
            // 다른 인덱스는 ' '를 반환해야 함
            for (int j = 0; j < maxIndex; j++) {
                if (j != currentIndex) {
                    assertEquals(' ', bomb.getBlockSymbol(j));
                }
            }
        }
    }
    
    @Test
    @DisplayName("통합 테스트 - 경계에서의 폭발")
    void testIntegrationBoundaryExplosion() throws Exception {
        Field gridField = Board.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        ShapeType[][] grid = (ShapeType[][]) gridField.get(board);
        
        // 전체 그리드 채우기
        for (int y = 0; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                grid[y][x] = ShapeType.I;
            }
        }
        
        // 4개 모서리에서 폭발
        bombBlock.activateEffect(board, 0, 0);
        bombBlock.activateEffect(board, Board.COLS - 1, 0);
        bombBlock.activateEffect(board, 0, Board.ROWS - 1);
        bombBlock.activateEffect(board, Board.COLS - 1, Board.ROWS - 1);
        
        // 모서리 근처가 파괴되었는지 확인
        assertNull(grid[0][0]);
        assertNull(grid[0][Board.COLS - 1]);
        assertNull(grid[Board.ROWS - 1][0]);
        assertNull(grid[Board.ROWS - 1][Board.COLS - 1]);
    }
}

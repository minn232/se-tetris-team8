package core;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Position 클래스에 대한 종합적인 테스트
 * 모든 메서드의 line coverage를 높이기 위한 테스트
 */
public class PositionTest {
    
    private Position position;
    
    @BeforeEach
    void setUp() {
        position = new Position(5, 10);
    }
    
    @AfterEach
    void tearDown() {
        position = null;
    }
    
    // ===== 생성자 테스트 =====
    
    @Test
    @DisplayName("Position 생성자 테스트 - 양수 좌표")
    void testConstructorWithPositiveCoordinates() {
        Position pos = new Position(3, 7);
        
        assertNotNull(pos);
        assertEquals(3, pos.x);
        assertEquals(7, pos.y);
    }
    
    @Test
    @DisplayName("Position 생성자 테스트 - 음수 좌표")
    void testConstructorWithNegativeCoordinates() {
        Position pos = new Position(-5, -10);
        
        assertNotNull(pos);
        assertEquals(-5, pos.x);
        assertEquals(-10, pos.y);
    }
    
    @Test
    @DisplayName("Position 생성자 테스트 - 0 좌표")
    void testConstructorWithZeroCoordinates() {
        Position pos = new Position(0, 0);
        
        assertNotNull(pos);
        assertEquals(0, pos.x);
        assertEquals(0, pos.y);
    }
    
    @Test
    @DisplayName("Position 생성자 테스트 - 혼합 좌표")
    void testConstructorWithMixedCoordinates() {
        Position pos1 = new Position(-3, 5);
        Position pos2 = new Position(8, -2);
        
        assertEquals(-3, pos1.x);
        assertEquals(5, pos1.y);
        assertEquals(8, pos2.x);
        assertEquals(-2, pos2.y);
    }
    
    @Test
    @DisplayName("Position 생성자 테스트 - 큰 값")
    void testConstructorWithLargeValues() {
        Position pos = new Position(1000, 2000);
        
        assertEquals(1000, pos.x);
        assertEquals(2000, pos.y);
    }
    
    // ===== add 메서드 테스트 =====
    
    @Test
    @DisplayName("add 메서드 테스트 - 양수 증가")
    void testAddWithPositiveValues() {
        Position result = position.add(3, 5);
        
        assertNotNull(result);
        assertEquals(8, result.x, "x는 5 + 3 = 8");
        assertEquals(15, result.y, "y는 10 + 5 = 15");
        
        // 원본은 변경되지 않음
        assertEquals(5, position.x);
        assertEquals(10, position.y);
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 음수 증가 (감소)")
    void testAddWithNegativeValues() {
        Position result = position.add(-2, -7);
        
        assertNotNull(result);
        assertEquals(3, result.x, "x는 5 - 2 = 3");
        assertEquals(3, result.y, "y는 10 - 7 = 3");
        
        // 원본은 변경되지 않음
        assertEquals(5, position.x);
        assertEquals(10, position.y);
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 0 추가")
    void testAddWithZeroValues() {
        Position result = position.add(0, 0);
        
        assertNotNull(result);
        assertEquals(5, result.x);
        assertEquals(10, result.y);
        
        // 새 객체가 생성됨
        assertNotSame(position, result);
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 혼합 값")
    void testAddWithMixedValues() {
        Position result1 = position.add(3, -5);
        Position result2 = position.add(-4, 2);
        
        assertEquals(8, result1.x, "x는 5 + 3 = 8");
        assertEquals(5, result1.y, "y는 10 - 5 = 5");
        
        assertEquals(1, result2.x, "x는 5 - 4 = 1");
        assertEquals(12, result2.y, "y는 10 + 2 = 12");
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 연속 호출")
    void testAddChainedCalls() {
        Position result = position.add(1, 2).add(3, 4).add(5, 6);
        
        assertEquals(14, result.x, "x는 5 + 1 + 3 + 5 = 14");
        assertEquals(22, result.y, "y는 10 + 2 + 4 + 6 = 22");
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 불변성 검증")
    void testAddImmutability() {
        Position original = new Position(5, 10);
        Position result1 = original.add(1, 1);
        Position result2 = original.add(2, 2);
        
        // 원본은 변경되지 않음
        assertEquals(5, original.x);
        assertEquals(10, original.y);
        
        // 각 호출은 독립적인 새 객체 생성
        assertEquals(6, result1.x);
        assertEquals(11, result1.y);
        assertEquals(7, result2.x);
        assertEquals(12, result2.y);
        
        assertNotSame(original, result1);
        assertNotSame(original, result2);
        assertNotSame(result1, result2);
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 음수 결과")
    void testAddResultingInNegative() {
        Position pos = new Position(3, 5);
        Position result = pos.add(-10, -8);
        
        assertEquals(-7, result.x);
        assertEquals(-3, result.y);
    }
    
    @Test
    @DisplayName("add 메서드 테스트 - 큰 값")
    void testAddWithLargeValues() {
        Position result = position.add(1000, 2000);
        
        assertEquals(1005, result.x);
        assertEquals(2010, result.y);
    }
    
    // ===== toString 메서드 테스트 =====
    
    @Test
    @DisplayName("toString 메서드 테스트 - 기본")
    void testToString() {
        String result = position.toString();
        
        assertNotNull(result);
        assertEquals("(5,10)", result);
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 0 좌표")
    void testToStringWithZero() {
        Position pos = new Position(0, 0);
        String result = pos.toString();
        
        assertEquals("(0,0)", result);
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 음수 좌표")
    void testToStringWithNegative() {
        Position pos = new Position(-5, -10);
        String result = pos.toString();
        
        assertEquals("(-5,-10)", result);
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 혼합 좌표")
    void testToStringWithMixed() {
        Position pos1 = new Position(3, -7);
        Position pos2 = new Position(-4, 8);
        
        assertEquals("(3,-7)", pos1.toString());
        assertEquals("(-4,8)", pos2.toString());
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 한 자리 수")
    void testToStringWithSingleDigit() {
        Position pos = new Position(1, 2);
        String result = pos.toString();
        
        assertEquals("(1,2)", result);
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 여러 자리 수")
    void testToStringWithMultipleDigits() {
        Position pos = new Position(123, 456);
        String result = pos.toString();
        
        assertEquals("(123,456)", result);
    }
    
    @Test
    @DisplayName("toString 메서드 테스트 - 큰 음수")
    void testToStringWithLargeNegative() {
        Position pos = new Position(-999, -888);
        String result = pos.toString();
        
        assertEquals("(-999,-888)", result);
    }
    
    // ===== 필드 접근 테스트 =====
    
    @Test
    @DisplayName("x 필드 직접 접근 테스트")
    void testXFieldAccess() {
        position.x = 100;
        assertEquals(100, position.x);
        
        position.x = -50;
        assertEquals(-50, position.x);
        
        position.x = 0;
        assertEquals(0, position.x);
    }
    
    @Test
    @DisplayName("y 필드 직접 접근 테스트")
    void testYFieldAccess() {
        position.y = 200;
        assertEquals(200, position.y);
        
        position.y = -75;
        assertEquals(-75, position.y);
        
        position.y = 0;
        assertEquals(0, position.y);
    }
    
    @Test
    @DisplayName("x, y 필드 동시 수정 테스트")
    void testBothFieldsModification() {
        position.x = 15;
        position.y = 25;
        
        assertEquals(15, position.x);
        assertEquals(25, position.y);
        assertEquals("(15,25)", position.toString());
    }
    
    // ===== 통합 시나리오 테스트 =====
    
    @Test
    @DisplayName("통합 테스트 - 이동 시뮬레이션 (오른쪽)")
    void testIntegrationMoveRight() {
        Position start = new Position(0, 10);
        Position moved = start.add(1, 0); // 오른쪽 이동
        
        assertEquals(1, moved.x);
        assertEquals(10, moved.y);
        assertEquals("(1,10)", moved.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 이동 시뮬레이션 (왼쪽)")
    void testIntegrationMoveLeft() {
        Position start = new Position(5, 10);
        Position moved = start.add(-1, 0); // 왼쪽 이동
        
        assertEquals(4, moved.x);
        assertEquals(10, moved.y);
        assertEquals("(4,10)", moved.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 이동 시뮬레이션 (아래)")
    void testIntegrationMoveDown() {
        Position start = new Position(5, 0);
        Position moved = start.add(0, 1); // 아래 이동
        
        assertEquals(5, moved.x);
        assertEquals(1, moved.y);
        assertEquals("(5,1)", moved.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 이동 시뮬레이션 (위)")
    void testIntegrationMoveUp() {
        Position start = new Position(5, 10);
        Position moved = start.add(0, -1); // 위 이동
        
        assertEquals(5, moved.x);
        assertEquals(9, moved.y);
        assertEquals("(5,9)", moved.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 대각선 이동")
    void testIntegrationMoveDiagonal() {
        Position start = new Position(0, 0);
        Position moved = start.add(1, 1); // 오른쪽 아래 대각선
        
        assertEquals(1, moved.x);
        assertEquals(1, moved.y);
        assertEquals("(1,1)", moved.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 여러 번 이동")
    void testIntegrationMultipleMoves() {
        Position start = new Position(5, 10);
        
        // 오른쪽 3칸
        Position pos1 = start.add(1, 0);
        Position pos2 = pos1.add(1, 0);
        Position pos3 = pos2.add(1, 0);
        
        assertEquals(8, pos3.x);
        assertEquals(10, pos3.y);
        
        // 아래 2칸
        Position pos4 = pos3.add(0, 1);
        Position pos5 = pos4.add(0, 1);
        
        assertEquals(8, pos5.x);
        assertEquals(12, pos5.y);
        assertEquals("(8,12)", pos5.toString());
    }
    
    @Test
    @DisplayName("통합 테스트 - 회전 시뮬레이션")
    void testIntegrationRotationSimulation() {
        // 원점을 중심으로 한 상대 위치들
        Position[] relativePositions = {
            new Position(0, 0),
            new Position(1, 0),
            new Position(0, 1),
            new Position(1, 1)
        };
        
        // 기준점 (3, 5)에 배치
        int baseX = 3;
        int baseY = 5;
        
        for (Position relative : relativePositions) {
            Position absolute = relative.add(baseX, baseY);
            assertEquals(relative.x + baseX, absolute.x);
            assertEquals(relative.y + baseY, absolute.y);
        }
    }
    
    @Test
    @DisplayName("통합 테스트 - 경계 테스트")
    void testIntegrationBoundaryCheck() {
        Position pos = new Position(9, 19); // 보드 오른쪽 하단 근처
        
        // 오른쪽으로 이동 시도
        Position rightMove = pos.add(1, 0);
        assertEquals(10, rightMove.x);
        
        // 아래로 이동 시도
        Position downMove = pos.add(0, 1);
        assertEquals(20, downMove.y);
        
        // 경계를 벗어난 값도 Position은 저장 가능
        Position outOfBounds = pos.add(100, 100);
        assertEquals(109, outOfBounds.x);
        assertEquals(119, outOfBounds.y);
    }
}

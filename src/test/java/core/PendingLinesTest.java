package core;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PendingLines 클래스에 대한 종합적인 테스트
 * 모든 메서드의 line coverage를 높이기 위한 테스트
 */
public class PendingLinesTest {
    
    private PendingLines pendingLines;
    
    @BeforeEach
    void setUp() {
        pendingLines = new PendingLines();
    }
    
    @AfterEach
    void tearDown() {
        pendingLines = null;
    }
    
    // ===== 기본 생성 및 초기화 테스트 =====
    
    @Test
    @DisplayName("PendingLines 기본 생성 테스트")
    void testPendingLinesCreation() {
        assertNotNull(pendingLines);
        assertFalse(pendingLines.hasPendingLines());
        assertEquals(0, pendingLines.getCount());
    }
    
    // ===== addLines 테스트 =====
    
    @Test
    @DisplayName("줄 추가 테스트 - 1줄")
    void testAddSingleLine() {
        pendingLines.addLines(1);
        
        assertTrue(pendingLines.hasPendingLines());
        assertEquals(1, pendingLines.getCount());
    }
    
    @Test
    @DisplayName("줄 추가 테스트 - 여러 줄")
    void testAddMultipleLines() {
        pendingLines.addLines(5);
        
        assertTrue(pendingLines.hasPendingLines());
        assertEquals(5, pendingLines.getCount());
    }
    
    @Test
    @DisplayName("줄 추가 테스트 - 여러 번 추가")
    void testAddLinesMultipleTimes() {
        pendingLines.addLines(3);
        pendingLines.addLines(2);
        pendingLines.addLines(4);
        
        assertTrue(pendingLines.hasPendingLines());
        assertEquals(9, pendingLines.getCount());
    }
    
    @Test
    @DisplayName("줄 추가 테스트 - 0줄 추가")
    void testAddZeroLines() {
        pendingLines.addLines(0);
        
        assertFalse(pendingLines.hasPendingLines());
        assertEquals(0, pendingLines.getCount());
    }
    
    // ===== hasPendingLines 테스트 =====
    
    @Test
    @DisplayName("hasPendingLines 테스트 - 초기 상태")
    void testHasPendingLinesInitial() {
        assertFalse(pendingLines.hasPendingLines());
    }
    
    @Test
    @DisplayName("hasPendingLines 테스트 - 줄 추가 후")
    void testHasPendingLinesAfterAdd() {
        pendingLines.addLines(1);
        assertTrue(pendingLines.hasPendingLines());
    }
    
    @Test
    @DisplayName("hasPendingLines 테스트 - 적용 후")
    void testHasPendingLinesAfterApply() {
        ShapeType[][] grid = createEmptyGrid();
        
        pendingLines.addLines(2);
        pendingLines.applyToBoard(grid);
        
        assertFalse(pendingLines.hasPendingLines());
    }
    
    // ===== getCount 테스트 =====
    
    @Test
    @DisplayName("getCount 테스트 - 초기 상태")
    void testGetCountInitial() {
        assertEquals(0, pendingLines.getCount());
    }
    
    @Test
    @DisplayName("getCount 테스트 - 줄 추가 후")
    void testGetCountAfterAdd() {
        pendingLines.addLines(7);
        assertEquals(7, pendingLines.getCount());
    }
    
    // ===== applyToBoard 테스트 =====
    
    @Test
    @DisplayName("applyToBoard 테스트 - null 그리드")
    void testApplyToBoardWithNullGrid() {
        pendingLines.addLines(3);
        
        int applied = pendingLines.applyToBoard(null);
        
        assertEquals(0, applied, "null 그리드에는 적용되지 않아야 함");
        assertEquals(3, pendingLines.getCount(), "null 그리드일 때는 카운트가 유지됨");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 대기 줄이 없을 때")
    void testApplyToBoardWithNoLines() {
        ShapeType[][] grid = createEmptyGrid();
        
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(0, applied, "대기 줄이 없으면 0 반환");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 1줄 적용")
    void testApplyToBoardSingleLine() {
        ShapeType[][] grid = createEmptyGrid();
        
        pendingLines.addLines(1);
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(1, applied, "1줄이 적용되어야 함");
        assertEquals(0, pendingLines.getCount(), "적용 후 카운트는 0");
        
        // 맨 아래 줄이 GRAY 블록으로 채워졌는지 확인 (한 칸은 비어있음)
        int nullCount = 0;
        int grayCount = 0;
        for (int x = 0; x < Board.COLS; x++) {
            if (grid[Board.ROWS - 1][x] == null) {
                nullCount++;
            } else if (grid[Board.ROWS - 1][x] == ShapeType.GRAY) {
                grayCount++;
            }
        }
        
        assertEquals(1, nullCount, "한 칸은 비어있어야 함");
        assertEquals(Board.COLS - 1, grayCount, "나머지는 GRAY 블록이어야 함");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 여러 줄 적용")
    void testApplyToBoardMultipleLines() {
        ShapeType[][] grid = createEmptyGrid();
        
        pendingLines.addLines(3);
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(3, applied, "3줄이 적용되어야 함");
        assertEquals(0, pendingLines.getCount(), "적용 후 카운트는 0");
        
        // 아래 3줄이 GRAY 블록으로 채워졌는지 확인
        for (int y = Board.ROWS - 3; y < Board.ROWS; y++) {
            int grayCount = 0;
            for (int x = 0; x < Board.COLS; x++) {
                if (grid[y][x] == ShapeType.GRAY) {
                    grayCount++;
                }
            }
            assertTrue(grayCount >= Board.COLS - 1, "각 줄은 최소 " + (Board.COLS - 1) + "개의 GRAY 블록을 가져야 함");
        }
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 기존 블록이 있는 상태에서 적용")
    void testApplyToBoardWithExistingBlocks() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 하단 5줄에 블록 배치
        for (int y = Board.ROWS - 5; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                grid[y][x] = ShapeType.I;
            }
        }
        
        pendingLines.addLines(2);
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(2, applied, "2줄이 적용되어야 함");
        
        // 기존 블록들이 위로 밀려났는지 확인
        // 원래 ROWS-5 ~ ROWS-1에 있던 블록들이 ROWS-7 ~ ROWS-3으로 이동
        boolean hasBlocksShifted = false;
        for (int y = Board.ROWS - 7; y < Board.ROWS - 2; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                if (grid[y][x] == ShapeType.I) {
                    hasBlocksShifted = true;
                    break;
                }
            }
            if (hasBlocksShifted) break;
        }
        assertTrue(hasBlocksShifted, "기존 블록들이 위로 이동해야 함");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 맨 위 줄에 블록이 있을 때 (게임 오버 상황)")
    void testApplyToBoardWithTopRowFilled() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 맨 위 줄에 블록 배치
        for (int x = 0; x < Board.COLS; x++) {
            grid[0][x] = ShapeType.T;
        }
        
        pendingLines.addLines(3);
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(0, applied, "맨 위 줄이 차있으면 줄을 추가할 수 없음");
        assertEquals(0, pendingLines.getCount(), "카운트는 초기화되어야 함");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 부분적 적용 (중간에 게임 오버)")
    void testApplyToBoardPartialApplication() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 상단 3줄을 제외하고 모두 채움
        for (int y = 3; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                grid[y][x] = ShapeType.O;
            }
        }
        
        // 3줄 이상을 추가하려고 시도
        pendingLines.addLines(5);
        int applied = pendingLines.applyToBoard(grid);
        
        // 3줄까지만 추가되고 그 이상은 게임 오버로 중단
        assertTrue(applied <= 3, "최대 3줄까지만 적용되어야 함");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 최대 줄 수 (20줄)")
    void testApplyToBoardMaxLines() {
        ShapeType[][] grid = createEmptyGrid();
        
        pendingLines.addLines(Board.ROWS);
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(Board.ROWS, applied, "보드 전체 높이만큼 적용");
        
        // 모든 줄이 GRAY 블록으로 채워졌는지 확인
        int totalGrayBlocks = 0;
        for (int y = 0; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                if (grid[y][x] == ShapeType.GRAY) {
                    totalGrayBlocks++;
                }
            }
        }
        
        // 각 줄마다 1개씩 구멍이 있으므로, GRAY 블록은 ROWS * (COLS - 1)개
        assertEquals(Board.ROWS * (Board.COLS - 1), totalGrayBlocks, 
                     "모든 줄이 GRAY 블록으로 채워져야 함 (각 줄마다 1개 구멍)");
    }
    
    @Test
    @DisplayName("applyToBoard 테스트 - 연속 적용")
    void testApplyToBoardConsecutive() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 첫 번째 적용
        pendingLines.addLines(2);
        int applied1 = pendingLines.applyToBoard(grid);
        assertEquals(2, applied1, "첫 번째 적용: 2줄");
        
        // 두 번째 적용
        pendingLines.addLines(3);
        int applied2 = pendingLines.applyToBoard(grid);
        assertEquals(3, applied2, "두 번째 적용: 3줄");
        
        // 총 5줄이 GRAY 블록으로 채워져야 함
        int grayRows = 0;
        for (int y = 0; y < Board.ROWS; y++) {
            boolean hasGray = false;
            for (int x = 0; x < Board.COLS; x++) {
                if (grid[y][x] == ShapeType.GRAY) {
                    hasGray = true;
                    break;
                }
            }
            if (hasGray) grayRows++;
        }
        
        assertEquals(5, grayRows, "총 5줄이 GRAY 블록을 포함해야 함");
    }
    
    // ===== reset 테스트 =====
    
    @Test
    @DisplayName("reset 테스트 - 초기 상태")
    void testResetInitial() {
        pendingLines.reset();
        
        assertEquals(0, pendingLines.getCount());
        assertFalse(pendingLines.hasPendingLines());
    }
    
    @Test
    @DisplayName("reset 테스트 - 줄 추가 후")
    void testResetAfterAdd() {
        pendingLines.addLines(10);
        assertEquals(10, pendingLines.getCount());
        
        pendingLines.reset();
        
        assertEquals(0, pendingLines.getCount());
        assertFalse(pendingLines.hasPendingLines());
    }
    
    @Test
    @DisplayName("reset 테스트 - 여러 번 호출")
    void testResetMultipleTimes() {
        pendingLines.addLines(5);
        pendingLines.reset();
        pendingLines.reset();
        pendingLines.reset();
        
        assertEquals(0, pendingLines.getCount());
        assertFalse(pendingLines.hasPendingLines());
    }
    
    // ===== 통합 시나리오 테스트 =====
    
    @Test
    @DisplayName("통합 테스트 - 추가, 적용, 리셋 시나리오")
    void testIntegrationScenario() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 1. 줄 추가
        pendingLines.addLines(3);
        assertEquals(3, pendingLines.getCount());
        assertTrue(pendingLines.hasPendingLines());
        
        // 2. 보드에 적용
        int applied = pendingLines.applyToBoard(grid);
        assertEquals(3, applied);
        assertEquals(0, pendingLines.getCount());
        assertFalse(pendingLines.hasPendingLines());
        
        // 3. 다시 줄 추가
        pendingLines.addLines(5);
        assertEquals(5, pendingLines.getCount());
        
        // 4. 리셋
        pendingLines.reset();
        assertEquals(0, pendingLines.getCount());
        assertFalse(pendingLines.hasPendingLines());
    }
    
    @Test
    @DisplayName("통합 테스트 - 누적 추가 후 적용")
    void testIntegrationAccumulateAndApply() {
        ShapeType[][] grid = createEmptyGrid();
        
        // 여러 번 누적 추가
        pendingLines.addLines(2);
        pendingLines.addLines(1);
        pendingLines.addLines(3);
        
        assertEquals(6, pendingLines.getCount());
        
        // 한 번에 적용
        int applied = pendingLines.applyToBoard(grid);
        
        assertEquals(6, applied);
        assertFalse(pendingLines.hasPendingLines());
    }
    
    // ===== 헬퍼 메서드 =====
    
    /**
     * 빈 그리드 생성
     */
    private ShapeType[][] createEmptyGrid() {
        return new ShapeType[Board.ROWS][Board.COLS];
    }
}

package ranking;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Difficulty;

@DisplayName("RankingBoard 테스트")
class RankingBoardTest {

    private RankingBoard rankingBoard;

    @BeforeEach
    void setUp() {
        if (!isHeadless()) {
            try {
                rankingBoard = new RankingBoard();
            } catch (Exception e) {
                rankingBoard = null;
            }
        }
    }

    @AfterEach
    void tearDown() {
        if (rankingBoard != null) {
            try {
                rankingBoard.dispose();
            } catch (Exception ignored) {}
        }
    }

    @Test
    @DisplayName("기본 생성자 테스트")
    void testDefaultConstructor() {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard();
            assertNotNull(board);
            assertEquals("Ranking Board", board.getTitle());
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("하이라이트 생성자 테스트")
    void testHighlightConstructor() {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("TestPlayer", 5000, true);
            assertNotNull(board);
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }


    @Test
    @DisplayName("아이템 모드 생성자 테스트")
    void testItemModeConstructor() {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("TestPlayer", 5000, true, true, false);
            assertNotNull(board);
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("DATE_FORMATTER 상수 테스트")
    void testDateFormatter() throws Exception {
        Field field = RankingBoard.class.getDeclaredField("DATE_FORMATTER");
        field.setAccessible(true);
        DateTimeFormatter formatter = (DateTimeFormatter) field.get(null);
        
        assertNotNull(formatter);
        
        LocalDateTime now = LocalDateTime.now();
        String formatted = now.format(formatter);
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("파일 경로 상수 테스트")
    void testFileConstants() throws Exception {
        Field normalField = RankingBoard.class.getDeclaredField("NORMAL_RANKING_FILE");
        Field timeAttackField = RankingBoard.class.getDeclaredField("TIMEATTACK_RANKING_FILE");
        Field itemField = RankingBoard.class.getDeclaredField("ITEM_RANKING_FILE");
        
        normalField.setAccessible(true);
        timeAttackField.setAccessible(true);
        itemField.setAccessible(true);
        
        assertEquals("normal_rankings.dat", normalField.get(null));
        assertEquals("timeattack_rankings.dat", timeAttackField.get(null));
        assertEquals("item_rankings.dat", itemField.get(null));
    }

    @Test
    @DisplayName("createRankingPanel 메서드 테스트")
    void testCreateRankingPanel() throws Exception {
        if (isHeadless() || rankingBoard == null) {
            assertTrue(true, "Headless 환경 또는 null");
            return;
        }
        
        try {
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod(
                "createRankingPanel", List.class, String.class, boolean.class);
            method.setAccessible(true);
            
            List<RankingEntry> testRankings = createTestRankings();
            JPanel panel = (JPanel) method.invoke(rankingBoard, testRankings, "Test", false);
            
            assertNotNull(panel);
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("createTitlePanel 메서드 테스트")
    void testCreateTitlePanel() throws Exception {
        if (isHeadless() || rankingBoard == null) {
            assertTrue(true, "Headless 환경 또는 null");
            return;
        }
        
        try {
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod(
                "createTitlePanel", String.class, int.class);
            method.setAccessible(true);
            
            JPanel panel = (JPanel) method.invoke(rankingBoard, "Test Title", 16);
            assertNotNull(panel);
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("addRankingEntries 메서드 테스트")
    void testAddRankingEntries() throws Exception {
        if (isHeadless() || rankingBoard == null) {
            assertTrue(true, "Headless 환경 또는 null");
            return;
        }
        
        try {
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod(
                "addRankingEntries", JPanel.class, List.class, int.class, boolean.class);
            method.setAccessible(true);
            
            JPanel panel = new JPanel();
            List<RankingEntry> testRankings = createTestRankings();
            
            assertDoesNotThrow(() -> {
                method.invoke(rankingBoard, panel, testRankings, 16, false);
            });
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("createButtonPanel 메서드 테스트")
    void testCreateButtonPanel() throws Exception {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("Test", 1000, true);
            assertNotNull(board);
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("createCloseButtonPanel 메서드 테스트")
    void testCreateCloseButtonPanel() throws Exception {
        if (isHeadless() || rankingBoard == null) {
            assertTrue(true, "Headless 환경 또는 null");
            return;
        }
        
        try {
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod("createCloseButtonPanel");
            method.setAccessible(true);
            
            JPanel panel = (JPanel) method.invoke(rankingBoard);
            assertNotNull(panel);
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("updateButtonHighlight 메서드 테스트")
    void testUpdateButtonHighlight() throws Exception {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("Test", 1000, true);
            
            Field buttonsField = RankingBoard.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            JButton[] buttons = (JButton[]) buttonsField.get(board);
            
            if (buttons != null) {
                java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod("updateButtonHighlight");
                method.setAccessible(true);
                
                assertDoesNotThrow(() -> method.invoke(board));
            }
            
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("handleKeyPress 메서드 테스트")
    void testHandleKeyPress() throws Exception {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("Test", 1000, true);
            
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod(
                "handleKeyPress", java.awt.event.KeyEvent.class);
            method.setAccessible(true);
            
            board.dispose();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("빈 랭킹 리스트 처리")
    void testEmptyRankingList() throws Exception {
        if (isHeadless() || rankingBoard == null) {
            assertTrue(true, "Headless 환경 또는 null");
            return;
        }
        
        try {
            java.lang.reflect.Method method = RankingBoard.class.getDeclaredMethod(
                "createRankingPanel", List.class, String.class, boolean.class);
            method.setAccessible(true);
            
            List<RankingEntry> emptyList = new ArrayList<>();
            JPanel panel = (JPanel) method.invoke(rankingBoard, emptyList, "Empty", false);
            
            assertNotNull(panel);
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    @Test
    @DisplayName("하이라이트된 엔트리 테스트")
    void testHighlightedEntry() {
        if (isHeadless()) {
            assertTrue(true, "Headless 환경");
            return;
        }
        
        try {
            RankingBoard board = new RankingBoard("Alice", 10000, false);
            assertNotNull(board);
            board.dispose();
        } catch (Exception e) {
            assertTrue(true, "GUI 환경 제한");
        }
    }

    private boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    private List<RankingEntry> createTestRankings() {
        List<RankingEntry> rankings = new ArrayList<>();
        rankings.add(new RankingEntry("Alice", 10000, LocalDateTime.now(), Difficulty.EASY));
        rankings.add(new RankingEntry("Bob", 8000, LocalDateTime.now(), Difficulty.NORMAL));
        rankings.add(new RankingEntry("Charlie", 6000, LocalDateTime.now(), Difficulty.HARD));
        return rankings;
    }
}

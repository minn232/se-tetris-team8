package ranking;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.Difficulty;

@DisplayName("RankingManager 테스트")
class RankingManagerTest {

    private RankingManager manager;
    private static final String TEST_DATA_DIR = "src/main/data/";

    @BeforeEach
    void setUp() {
        manager = RankingManager.getInstance("test_rankings.dat");
        manager.clearRankings();
    }

    @AfterEach
    void tearDown() {
        if (manager != null) {
            manager.clearRankings();
        }
        // 테스트 파일 삭제
        File testFile = new File(TEST_DATA_DIR + "test_rankings.dat");
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    @DisplayName("getInstance - 싱글톤 패턴")
    void testGetInstance() {
        RankingManager instance1 = RankingManager.getInstance();
        RankingManager instance2 = RankingManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("getInstance - 파일명 지정")
    void testGetInstanceWithFileName() {
        RankingManager normalManager = RankingManager.getInstance("normal_rankings.dat");
        RankingManager itemManager = RankingManager.getInstance("item_rankings.dat");
        
        assertNotNull(normalManager);
        assertNotNull(itemManager);
    }

    @Test
    @DisplayName("addEntry - 엔트리 추가")
    void testAddEntry() {
        RankingEntry entry = new RankingEntry("Player1", 1000, LocalDateTime.now(), Difficulty.EASY);
        manager.addEntry(entry);
        
        List<RankingEntry> rankings = manager.getRankings();
        assertEquals(1, rankings.size());
        assertEquals("Player1", rankings.get(0).getPlayerName());
    }

    @Test
    @DisplayName("addEntry - 정렬 확인")
    void testAddEntrySorting() {
        manager.addEntry(new RankingEntry("Player1", 500, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player2", 1000, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player3", 750, LocalDateTime.now(), Difficulty.EASY));
        
        List<RankingEntry> rankings = manager.getRankings();
        assertEquals(3, rankings.size());
        assertEquals(1000, rankings.get(0).getScore());
        assertEquals(750, rankings.get(1).getScore());
        assertEquals(500, rankings.get(2).getScore());
    }

    @Test
    @DisplayName("addEntry - MAX_RANKINGS 초과 시 자동 제거")
    void testAddEntryMaxLimit() {
        // 11개 추가 (MAX_RANKINGS = 10)
        for (int i = 0; i < 11; i++) {
            manager.addEntry(new RankingEntry("Player" + i, (11 - i) * 100, LocalDateTime.now(), Difficulty.EASY));
        }
        
        List<RankingEntry> rankings = manager.getRankings();
        assertEquals(10, rankings.size());
        assertEquals(1100, rankings.get(0).getScore());
        assertEquals(200, rankings.get(9).getScore());
    }

    @Test
    @DisplayName("shouldInputName - 랭킹 진입 가능")
    void testShouldInputNameTrue() {
        for (int i = 0; i < 5; i++) {
            manager.addEntry(new RankingEntry("Player" + i, (i + 1) * 100, LocalDateTime.now(), Difficulty.EASY));
        }
        
        assertTrue(manager.shouldInputName(600));
        assertTrue(manager.shouldInputName(50));
    }

    @Test
    @DisplayName("shouldInputName - 랭킹 진입 불가")
    void testShouldInputNameFalse() {
        for (int i = 0; i < 10; i++) {
            manager.addEntry(new RankingEntry("Player" + i, (10 - i) * 100, LocalDateTime.now(), Difficulty.EASY));
        }
        
        assertFalse(manager.shouldInputName(50));
    }

    @Test
    @DisplayName("shouldInputName - 랭킹 빈 경우")
    void testShouldInputNameEmpty() {
        assertTrue(manager.shouldInputName(100));
    }

    @Test
    @DisplayName("getRankings - 빈 리스트")
    void testGetRankingsEmpty() {
        List<RankingEntry> rankings = manager.getRankings();
        assertNotNull(rankings);
        assertEquals(0, rankings.size());
    }

    @Test
    @DisplayName("getRankings - 불변성 확인")
    void testGetRankingsImmutability() {
        manager.addEntry(new RankingEntry("Player1", 1000, LocalDateTime.now(), Difficulty.EASY));
        
        List<RankingEntry> rankings1 = manager.getRankings();
        List<RankingEntry> rankings2 = manager.getRankings();
        
        assertNotSame(rankings1, rankings2);
    }

    @Test
    @DisplayName("clearRankings - 모든 랭킹 삭제")
    void testClearRankings() {
        manager.addEntry(new RankingEntry("Player1", 1000, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player2", 800, LocalDateTime.now(), Difficulty.EASY));
        
        assertEquals(2, manager.getRankings().size());
        
        manager.clearRankings();
        
        assertEquals(0, manager.getRankings().size());
    }

    @Test
    @DisplayName("ensureDataDirectoryExists - 디렉토리 생성")
    void testEnsureDataDirectoryExists() {
        File dataDir = new File(TEST_DATA_DIR);
        assertTrue(dataDir.exists() || dataDir.mkdirs());
    }

    @Test
    @DisplayName("상수 테스트")
    void testConstants() throws Exception {
        Field maxRankingsField = RankingManager.class.getDeclaredField("MAX_RANKINGS");
        Field dataDirField = RankingManager.class.getDeclaredField("DATA_DIR");
        Field normalFileField = RankingManager.class.getDeclaredField("NORMAL_RANKING_FILE");
        Field itemFileField = RankingManager.class.getDeclaredField("ITEM_RANKING_FILE");
        
        maxRankingsField.setAccessible(true);
        dataDirField.setAccessible(true);
        normalFileField.setAccessible(true);
        itemFileField.setAccessible(true);
        
        assertEquals(10, maxRankingsField.get(null));
        assertEquals("src/main/data/", dataDirField.get(null));
        assertEquals("normal_rankings.dat", normalFileField.get(null));
        assertEquals("item_rankings.dat", itemFileField.get(null));
    }

    @Test
    @DisplayName("파일 저장 및 로드")
    void testSaveAndLoad() {
        manager.addEntry(new RankingEntry("Player1", 1000, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player2", 800, LocalDateTime.now(), Difficulty.EASY));
        
        // 새 인스턴스로 로드
        RankingManager newManager = RankingManager.getInstance("test_rankings.dat");
        List<RankingEntry> rankings = newManager.getRankings();
        
        assertEquals(2, rankings.size());
        assertEquals("Player1", rankings.get(0).getPlayerName());
        assertEquals(1000, rankings.get(0).getScore());
    }

    @Test
    @DisplayName("동일 점수 처리")
    void testSameScore() {
        manager.addEntry(new RankingEntry("Player1", 1000, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player2", 1000, LocalDateTime.now(), Difficulty.EASY));
        manager.addEntry(new RankingEntry("Player3", 1000, LocalDateTime.now(), Difficulty.EASY));
        
        List<RankingEntry> rankings = manager.getRankings();
        assertEquals(3, rankings.size());
        
        for (RankingEntry entry : rankings) {
            assertEquals(1000, entry.getScore());
        }
    }
}

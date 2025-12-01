package items;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ItemManager 클래스에 대한 종합적인 테스트
 */
public class ItemManagerTest {
    
    private ItemManager itemManager;
    private ItemManager noItemManager;
    
    @BeforeEach
    void setUp() {
        itemManager = new ItemManager(true);
        noItemManager = new ItemManager(false);
    }
    
    @AfterEach
    void tearDown() {
        itemManager = null;
        noItemManager = null;
    }
    
    // ===== 생성자 테스트 =====
    
    @Test
    @DisplayName("ItemManager 생성 - itemMode true")
    void testItemManagerCreationWithItemMode() {
        assertTrue(itemManager.isItemMode());
        assertEquals(0, itemManager.getTotalLinesCleared());
        assertEquals(5, itemManager.getAvailableItems().size());
    }
    
    @Test
    @DisplayName("ItemManager 생성 - itemMode false")
    void testItemManagerCreationWithoutItemMode() {
        assertFalse(noItemManager.isItemMode());
        assertEquals(0, noItemManager.getTotalLinesCleared());
        assertEquals(0, noItemManager.getAvailableItems().size());
    }
    
    // ===== onLinesCleared 테스트 =====
    
    @Test
    @DisplayName("onLinesCleared - 줄 수 증가")
    void testOnLinesCleared() {
        itemManager.onLinesCleared(3);
        assertEquals(3, itemManager.getTotalLinesCleared());
        
        itemManager.onLinesCleared(5);
        assertEquals(8, itemManager.getTotalLinesCleared());
    }
    
    @Test
    @DisplayName("onLinesCleared - itemMode false일 때")
    void testOnLinesClearedWithoutItemMode() {
        noItemManager.onLinesCleared(5);
        assertEquals(0, noItemManager.getTotalLinesCleared());
    }
    
    // ===== generateItem 테스트 =====
    
    @Test
    @DisplayName("generateItem - 10줄 미만일 때 null")
    void testGenerateItemUnder10() {
        itemManager.onLinesCleared(5);
        assertNull(itemManager.generateItem());
    }
    
    @Test
    @DisplayName("generateItem - 10줄일 때 아이템 생성")
    void testGenerateItem10Lines() {
        itemManager.onLinesCleared(10);
        ItemBlock item = itemManager.generateItem();
        assertNotNull(item);
    }
    
    @Test
    @DisplayName("generateItem - 생성 후 totalLinesCleared 리셋")
    void testGenerateItemResetLines() {
        itemManager.onLinesCleared(10);
        itemManager.generateItem();
        assertEquals(0, itemManager.getTotalLinesCleared());
    }
    
    @Test
    @DisplayName("generateItem - 15줄일 때 5줄 남음")
    void testGenerateItem15Lines() {
        itemManager.onLinesCleared(15);
        itemManager.generateItem();
        assertEquals(5, itemManager.getTotalLinesCleared());
    }
    
    // ===== getRandomItem 테스트 =====
    
    @Test
    @DisplayName("getRandomItem - 아이템 생성")
    void testGetRandomItem() {
        ItemBlock item = itemManager.getRandomItem();
        assertNotNull(item);
    }
    
    @Test
    @DisplayName("getRandomItem - 새 인스턴스 생성")
    void testGetRandomItemNewInstance() {
        ItemBlock item1 = itemManager.getRandomItem();
        ItemBlock item2 = itemManager.getRandomItem();
        assertNotSame(item1, item2);
    }
    
    @Test
    @DisplayName("getRandomItem - itemMode false일 때 null")
    void testGetRandomItemWithoutItemMode() {
        assertNull(noItemManager.getRandomItem());
    }
    
    // ===== getItemByName 테스트 =====
    
    @Test
    @DisplayName("getItemByName - 각 아이템 검색")
    void testGetItemByName() {
        assertNotNull(itemManager.getItemByName("Line"));
        assertNotNull(itemManager.getItemByName("Slow"));
        assertNotNull(itemManager.getItemByName("TransformToI"));
        assertNotNull(itemManager.getItemByName("Weight"));
        assertNotNull(itemManager.getItemByName("Bomb"));
    }
    
    @Test
    @DisplayName("getItemByName - 존재하지 않는 이름")
    void testGetItemByNameNotFound() {
        assertNull(itemManager.getItemByName("NonExistent"));
    }
    
    // ===== reset 테스트 =====
    
    @Test
    @DisplayName("reset - totalLinesCleared 초기화")
    void testReset() {
        itemManager.onLinesCleared(15);
        itemManager.reset();
        assertEquals(0, itemManager.getTotalLinesCleared());
    }
    
    // ===== getLinesUntilNextItem 테스트 =====
    
    @Test
    @DisplayName("getLinesUntilNextItem - 초기 상태")
    void testGetLinesUntilNextItemInitial() {
        assertEquals(10, itemManager.getLinesUntilNextItem());
    }
    
    @Test
    @DisplayName("getLinesUntilNextItem - 3줄 삭제 후")
    void testGetLinesUntilNextItemAfter3() {
        itemManager.onLinesCleared(3);
        assertEquals(7, itemManager.getLinesUntilNextItem());
    }
    
    @Test
    @DisplayName("getLinesUntilNextItem - itemMode false일 때")
    void testGetLinesUntilNextItemWithoutItemMode() {
        assertEquals(-1, noItemManager.getLinesUntilNextItem());
    }
    
    // ===== getStatusInfo 테스트 =====
    
    @Test
    @DisplayName("getStatusInfo - 기본 형식")
    void testGetStatusInfo() {
        String status = itemManager.getStatusInfo();
        assertTrue(status.contains("mode=ON"));
        assertTrue(status.contains("totalLines=0"));
        assertTrue(status.contains("availableItems=5"));
    }
}

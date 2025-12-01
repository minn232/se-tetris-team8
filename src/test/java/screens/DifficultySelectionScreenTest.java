package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Difficulty;

class DifficultySelectionScreenTest {
    
    private DifficultySelectionScreen screen;
    
    @BeforeEach
    void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            screen = new DifficultySelectionScreen(false);
        }
    }
    
    @AfterEach
    void tearDown() {
        if (screen != null) {
            screen.dispose();
        }
    }
    
    @Test
    void testConstructorDefault() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertNotNull(screen);
        assertEquals("Select Difficulty", screen.getTitle());
        assertFalse(screen.isResizable());
        try {
            Field isItemModeField = DifficultySelectionScreen.class.getDeclaredField("isItemMode");
            isItemModeField.setAccessible(true);
            assertFalse((boolean) isItemModeField.get(screen));
            
            Field isBattleModeField = DifficultySelectionScreen.class.getDeclaredField("isBattleMode");
            isBattleModeField.setAccessible(true);
            assertFalse((boolean) isBattleModeField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testConstructorWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen itemScreen = new DifficultySelectionScreen(true);
        assertNotNull(itemScreen);
        try {
            Field isItemModeField = DifficultySelectionScreen.class.getDeclaredField("isItemMode");
            isItemModeField.setAccessible(true);
            assertTrue((boolean) isItemModeField.get(itemScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            itemScreen.dispose();
        }
    }
    
    @Test
    void testConstructorWithBattleMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen battleScreen = new DifficultySelectionScreen(false, true);
        assertNotNull(battleScreen);
        try {
            Field isBattleModeField = DifficultySelectionScreen.class.getDeclaredField("isBattleMode");
            isBattleModeField.setAccessible(true);
            assertTrue((boolean) isBattleModeField.get(battleScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            battleScreen.dispose();
        }
    }
    
    @Test
    void testConstructorWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen timeScreen = new DifficultySelectionScreen(false, false, true);
        assertNotNull(timeScreen);
        try {
            Field isTimeAttackField = DifficultySelectionScreen.class.getDeclaredField("isTimeAttack");
            isTimeAttackField.setAccessible(true);
            assertTrue((boolean) isTimeAttackField.get(timeScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            timeScreen.dispose();
        }
    }
    
    @Test
    void testHandleKeyPressLeft() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 2);
            
            handleKeyPressMethod.invoke(screen, KeyEvent.VK_LEFT);
            
            int newIndex = (int) selectedIndexField.get(screen);
            assertEquals(1, newIndex);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressRight() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 0);
            
            handleKeyPressMethod.invoke(screen, KeyEvent.VK_RIGHT);
            
            int newIndex = (int) selectedIndexField.get(screen);
            assertEquals(1, newIndex);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressLeftBoundary() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 0);
            
            handleKeyPressMethod.invoke(screen, KeyEvent.VK_LEFT);
            
            int newIndex = (int) selectedIndexField.get(screen);
            assertEquals(0, newIndex);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressRightBoundary() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 2);
            
            handleKeyPressMethod.invoke(screen, KeyEvent.VK_RIGHT);
            
            int newIndex = (int) selectedIndexField.get(screen);
            assertEquals(2, newIndex);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressSpace() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(screen, KeyEvent.VK_SPACE));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressEnter() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(screen, KeyEvent.VK_ENTER));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressEscape() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = DifficultySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
            handleKeyPressMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(screen, KeyEvent.VK_ESCAPE));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateButtonFocus() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method updateButtonFocusMethod = DifficultySelectionScreen.class.getDeclaredMethod("updateButtonFocus");
            updateButtonFocusMethod.setAccessible(true);
            
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 1);
            
            assertDoesNotThrow(() -> updateButtonFocusMethod.invoke(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testStartGameEasy() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(screen, Difficulty.EASY));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testStartGameNormal() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(screen, Difficulty.NORMAL));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testStartGameHard() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(screen, Difficulty.HARD));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testStartGameBattleMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen battleScreen = new DifficultySelectionScreen(false, true);
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(battleScreen, Difficulty.EASY));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            battleScreen.dispose();
        }
    }
    
    @Test
    void testStartGameBattleModeWithItemAndTime() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen battleScreen = new DifficultySelectionScreen(true, true, true);
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(battleScreen, Difficulty.NORMAL));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            battleScreen.dispose();
        }
    }
    
    @Test
    void testStartGameWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen itemScreen = new DifficultySelectionScreen(true);
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(itemScreen, Difficulty.EASY));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            itemScreen.dispose();
        }
    }
    
    @Test
    void testStartGameWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        DifficultySelectionScreen timeScreen = new DifficultySelectionScreen(false, false, true);
        try {
            Method startGameMethod = DifficultySelectionScreen.class.getDeclaredMethod("startGame", Difficulty.class);
            startGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> startGameMethod.invoke(timeScreen, Difficulty.HARD));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            timeScreen.dispose();
        }
    }
    
    @Test
    void testAddButton() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method addButtonMethod = DifficultySelectionScreen.class.getDeclaredMethod(
                "addButton", javax.swing.JPanel.class, String.class, int.class, int.class, int.class, int.class);
            addButtonMethod.setAccessible(true);
            
            javax.swing.JPanel panel = new javax.swing.JPanel();
            JButton button = (JButton) addButtonMethod.invoke(screen, panel, "/images/EasyModeButton.png", 80, 120, 100, 100);
            
            assertNotNull(button);
            assertEquals(1, panel.getComponentCount());
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testButtonsArray() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field buttonsField = DifficultySelectionScreen.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            JButton[] buttons = (JButton[]) buttonsField.get(screen);
            
            assertNotNull(buttons);
            assertEquals(3, buttons.length);
            
            for (JButton btn : buttons) {
                assertNotNull(btn);
                assertFalse(btn.isFocusable());
            }
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testSelectedIndexInitial() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field selectedIndexField = DifficultySelectionScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            int selectedIndex = (int) selectedIndexField.get(screen);
            
            assertEquals(0, selectedIndex);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testScreenProperties() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertTrue(screen.isFocusable());
        assertEquals(JFrame.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
        assertFalse(screen.isResizable());
    }
}

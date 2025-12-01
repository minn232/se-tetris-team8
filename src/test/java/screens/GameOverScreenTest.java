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

class GameOverScreenTest {
    
    private GameOverScreen screen;
    
    @BeforeEach
    void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            screen = new GameOverScreen(1000, Difficulty.EASY, false);
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
        assertEquals("Game Over", screen.getTitle());
        assertEquals(JFrame.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
        
        try {
            Field finalScoreField = GameOverScreen.class.getDeclaredField("finalScore");
            finalScoreField.setAccessible(true);
            assertEquals(1000, (int) finalScoreField.get(screen));
            
            Field difficultyField = GameOverScreen.class.getDeclaredField("difficulty");
            difficultyField.setAccessible(true);
            assertEquals(Difficulty.EASY, difficultyField.get(screen));
            
            Field isItemModeField = GameOverScreen.class.getDeclaredField("isItemMode");
            isItemModeField.setAccessible(true);
            assertFalse((boolean) isItemModeField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testConstructorWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen itemScreen = new GameOverScreen(2000, Difficulty.NORMAL, true);
        assertNotNull(itemScreen);
        
        try {
            Field isItemModeField = GameOverScreen.class.getDeclaredField("isItemMode");
            isItemModeField.setAccessible(true);
            assertTrue((boolean) isItemModeField.get(itemScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            itemScreen.dispose();
        }
    }
    
    @Test
    void testConstructorWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen timeScreen = new GameOverScreen(3000, Difficulty.HARD, false, true);
        assertNotNull(timeScreen);
        
        try {
            Field isTimeAttackModeField = GameOverScreen.class.getDeclaredField("isTimeAttackMode");
            isTimeAttackModeField.setAccessible(true);
            assertTrue((boolean) isTimeAttackModeField.get(timeScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            timeScreen.dispose();
        }
    }
    
    @Test
    void testInitializeUI() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertTrue(screen.isFocusable());
        
        try {
            Field buttonsField = GameOverScreen.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            JButton[] buttons = (JButton[]) buttonsField.get(screen);
            
            assertNotNull(buttons);
            assertEquals(3, buttons.length);
            
            for (JButton btn : buttons) {
                assertFalse(btn.isFocusable());
            }
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressUp() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 1);
            
            KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, ' ');
            handleKeyPressMethod.invoke(screen, upEvent);
            
            assertEquals(0, (int) selectedIndexField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressDown() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 0);
            
            KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, ' ');
            handleKeyPressMethod.invoke(screen, downEvent);
            
            assertEquals(1, (int) selectedIndexField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressUpWrap() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 0);
            
            KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, ' ');
            handleKeyPressMethod.invoke(screen, upEvent);
            
            assertEquals(2, (int) selectedIndexField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressDownWrap() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 2);
            
            KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, ' ');
            handleKeyPressMethod.invoke(screen, downEvent);
            
            assertEquals(0, (int) selectedIndexField.get(screen));
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
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            KeyEvent enterEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\n');
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(screen, enterEvent));
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
            Method handleKeyPressMethod = GameOverScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            KeyEvent spaceEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(screen, spaceEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testUpdateButtonHighlight() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method updateButtonHighlightMethod = GameOverScreen.class.getDeclaredMethod("updateButtonHighlight");
            updateButtonHighlightMethod.setAccessible(true);
            
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            selectedIndexField.set(screen, 1);
            
            assertDoesNotThrow(() -> updateButtonHighlightMethod.invoke(screen));
            
            Field buttonsField = GameOverScreen.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            JButton[] buttons = (JButton[]) buttonsField.get(screen);
            
            assertNotNull(buttons[1].getBackground());
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCreateButton() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method createButtonMethod = GameOverScreen.class.getDeclaredMethod("createButton", String.class);
            createButtonMethod.setAccessible(true);
            
            JButton button = (JButton) createButtonMethod.invoke(screen, "Test Button");
            
            assertNotNull(button);
            assertEquals("Test Button", button.getText());
            assertNotNull(button.getMaximumSize());
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testRestartGame() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method restartGameMethod = GameOverScreen.class.getDeclaredMethod("restartGame");
            restartGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> restartGameMethod.invoke(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testRestartGameWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen itemScreen = new GameOverScreen(5000, Difficulty.NORMAL, true);
        try {
            Method restartGameMethod = GameOverScreen.class.getDeclaredMethod("restartGame");
            restartGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> restartGameMethod.invoke(itemScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            itemScreen.dispose();
        }
    }
    
    @Test
    void testRestartGameWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen timeScreen = new GameOverScreen(7000, Difficulty.HARD, false, true);
        try {
            Method restartGameMethod = GameOverScreen.class.getDeclaredMethod("restartGame");
            restartGameMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> restartGameMethod.invoke(timeScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            timeScreen.dispose();
        }
    }
    
    @Test
    void testSelectedIndexInitial() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field selectedIndexField = GameOverScreen.class.getDeclaredField("selectedIndex");
            selectedIndexField.setAccessible(true);
            
            assertEquals(0, (int) selectedIndexField.get(screen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHighScores() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen highScoreScreen1 = new GameOverScreen(10000, Difficulty.EASY, false);
        GameOverScreen highScoreScreen2 = new GameOverScreen(50000, Difficulty.NORMAL, true);
        GameOverScreen highScoreScreen3 = new GameOverScreen(100000, Difficulty.HARD, true, true);
        
        try {
            Field finalScoreField = GameOverScreen.class.getDeclaredField("finalScore");
            finalScoreField.setAccessible(true);
            
            assertEquals(10000, (int) finalScoreField.get(highScoreScreen1));
            assertEquals(50000, (int) finalScoreField.get(highScoreScreen2));
            assertEquals(100000, (int) finalScoreField.get(highScoreScreen3));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            highScoreScreen1.dispose();
            highScoreScreen2.dispose();
            highScoreScreen3.dispose();
        }
    }
    
    @Test
    void testButtonCount() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field buttonsField = GameOverScreen.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            JButton[] buttons = (JButton[]) buttonsField.get(screen);
            
            assertEquals(3, buttons.length);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testAllDifficulties() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        GameOverScreen easyScreen = new GameOverScreen(1000, Difficulty.EASY, false);
        GameOverScreen normalScreen = new GameOverScreen(2000, Difficulty.NORMAL, false);
        GameOverScreen hardScreen = new GameOverScreen(3000, Difficulty.HARD, false);
        
        try {
            Field difficultyField = GameOverScreen.class.getDeclaredField("difficulty");
            difficultyField.setAccessible(true);
            
            assertEquals(Difficulty.EASY, difficultyField.get(easyScreen));
            assertEquals(Difficulty.NORMAL, difficultyField.get(normalScreen));
            assertEquals(Difficulty.HARD, difficultyField.get(hardScreen));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            easyScreen.dispose();
            normalScreen.dispose();
            hardScreen.dispose();
        }
    }
}

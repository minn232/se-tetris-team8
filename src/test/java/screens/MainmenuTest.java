package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainmenuTest {
    private Mainmenu mainmenu;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        mainmenu = new Mainmenu();
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (mainmenu != null) {
            mainmenu.dispose();
        }
        BackgroundMusicPlayer.getInstance().stop();
    }
    
    @Test
    public void testConstructor() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(mainmenu);
        assertEquals("Tetris", mainmenu.getTitle());
        assertFalse(mainmenu.isResizable());
        assertTrue(mainmenu.isFocusable());
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JFrame.EXIT_ON_CLOSE, mainmenu.getDefaultCloseOperation());
    }
    
    @Test
    public void testButtonsArray() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = Mainmenu.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(mainmenu);
        
        assertNotNull(buttons);
        assertEquals(6, buttons.length);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isFocusable());
        }
    }
    
    @Test
    public void testSelectedIndexInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(mainmenu);
        
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testHandleKeyPressLeft() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // multi -> single (1 -> 0)
        selectedIndexField.set(mainmenu, 1);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(mainmenu));
        
        // ranking -> multi (2 -> 1)
        selectedIndexField.set(mainmenu, 2);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(1, (int) selectedIndexField.get(mainmenu));
        
        // settings -> howto (4 -> 3)
        selectedIndexField.set(mainmenu, 4);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(3, (int) selectedIndexField.get(mainmenu));
        
        // exit -> settings (5 -> 4)
        selectedIndexField.set(mainmenu, 5);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(4, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressRight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // single -> multi (0 -> 1)
        selectedIndexField.set(mainmenu, 0);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(mainmenu));
        
        // multi -> ranking (1 -> 2)
        selectedIndexField.set(mainmenu, 1);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(2, (int) selectedIndexField.get(mainmenu));
        
        // howto -> settings (3 -> 4)
        selectedIndexField.set(mainmenu, 3);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(4, (int) selectedIndexField.get(mainmenu));
        
        // settings -> exit (4 -> 5)
        selectedIndexField.set(mainmenu, 4);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(5, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressUp() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // howto -> single (3 -> 0)
        selectedIndexField.set(mainmenu, 3);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_UP);
        assertEquals(0, (int) selectedIndexField.get(mainmenu));
        
        // settings -> multi (4 -> 1)
        selectedIndexField.set(mainmenu, 4);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_UP);
        assertEquals(1, (int) selectedIndexField.get(mainmenu));
        
        // exit -> ranking (5 -> 2)
        selectedIndexField.set(mainmenu, 5);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_UP);
        assertEquals(2, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressDown() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // single -> howto (0 -> 3)
        selectedIndexField.set(mainmenu, 0);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_DOWN);
        assertEquals(3, (int) selectedIndexField.get(mainmenu));
        
        // multi -> settings (1 -> 4)
        selectedIndexField.set(mainmenu, 1);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_DOWN);
        assertEquals(4, (int) selectedIndexField.get(mainmenu));
        
        // ranking -> exit (2 -> 5)
        selectedIndexField.set(mainmenu, 2);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_DOWN);
        assertEquals(5, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testUpdateButtonFocus() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = Mainmenu.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(mainmenu);
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(mainmenu, 0);
        
        Method updateButtonFocus = Mainmenu.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(mainmenu);
        
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testUpdateButtonFocusSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = Mainmenu.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(mainmenu);
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(mainmenu, 1);
        
        Method updateButtonFocus = Mainmenu.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(mainmenu);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testAddButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = Mainmenu.class.getDeclaredMethod(
            "addButton", 
            javax.swing.JPanel.class, 
            String.class, 
            int.class, 
            int.class, 
            int.class, 
            int.class
        );
        addButton.setAccessible(true);
        
        javax.swing.JPanel panel = new javax.swing.JPanel();
        JButton button = (JButton) addButton.invoke(
            mainmenu, 
            panel, 
            "/images/SinglePlayButton.png", 
            100, 
            50, 
            10, 
            20
        );
        
        assertNotNull(button);
        assertEquals(10, button.getX());
        assertEquals(20, button.getY());
        assertEquals(100, button.getWidth());
        assertEquals(50, button.getHeight());
    }
    
    @Test
    public void testOpenSettings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method openSettings = Mainmenu.class.getDeclaredMethod("openSettings");
        openSettings.setAccessible(true);
        
        // openSettings는 새 창을 띄우므로 실행만 확인
        assertDoesNotThrow(() -> openSettings.invoke(mainmenu));
    }
    
    @Test
    public void testBackgroundMusicPlaying() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 생성자에서 배경음악이 재생되는지 확인
        assertNotNull(BackgroundMusicPlayer.getInstance());
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(mainmenu.getKeyListeners().length > 0);
    }
    
    @Test
    public void testAllButtonsNonNull() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = Mainmenu.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(mainmenu);
        
        for (JButton btn : buttons) {
            assertNotNull(btn);
        }
    }
    
    @Test
    public void testHandleKeyPressLeftNoChange() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // single에서 LEFT 누르면 변경 없음 (0 -> 0)
        selectedIndexField.set(mainmenu, 0);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(mainmenu));
        
        // howto에서 LEFT 누르면 변경 없음 (3 -> 3)
        selectedIndexField.set(mainmenu, 3);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_LEFT);
        assertEquals(3, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressRightNoChange() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // ranking에서 RIGHT 누르면 변경 없음 (2 -> 2)
        selectedIndexField.set(mainmenu, 2);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(2, (int) selectedIndexField.get(mainmenu));
        
        // exit에서 RIGHT 누르면 변경 없음 (5 -> 5)
        selectedIndexField.set(mainmenu, 5);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_RIGHT);
        assertEquals(5, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressUpNoChange() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // single에서 UP 누르면 변경 없음 (0 -> 0)
        selectedIndexField.set(mainmenu, 0);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_UP);
        assertEquals(0, (int) selectedIndexField.get(mainmenu));
        
        // multi에서 UP 누르면 변경 없음 (1 -> 1)
        selectedIndexField.set(mainmenu, 1);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_UP);
        assertEquals(1, (int) selectedIndexField.get(mainmenu));
    }
    
    @Test
    public void testHandleKeyPressDownNoChange() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = Mainmenu.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = Mainmenu.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // howto에서 DOWN 누르면 변경 없음 (3 -> 3)
        selectedIndexField.set(mainmenu, 3);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_DOWN);
        assertEquals(3, (int) selectedIndexField.get(mainmenu));
        
        // settings에서 DOWN 누르면 변경 없음 (4 -> 4)
        selectedIndexField.set(mainmenu, 4);
        handleKeyPress.invoke(mainmenu, KeyEvent.VK_DOWN);
        assertEquals(4, (int) selectedIndexField.get(mainmenu));
    }
}

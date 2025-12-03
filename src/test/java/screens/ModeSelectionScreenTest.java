package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JButton;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModeSelectionScreenTest {
    private ModeSelectionScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new ModeSelectionScreen();
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (screen != null) {
            screen.dispose();
        }
    }
    
    @Test
    public void testConstructorDefault() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(screen);
        assertEquals("Select Game Mode", screen.getTitle());
        assertFalse(screen.isResizable());
        assertTrue(screen.isFocusable());
    }
    
    @Test
    public void testConstructorWithBattleMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        ModeSelectionScreen battleScreen = new ModeSelectionScreen(true);
        
        Field isBattleModeField = ModeSelectionScreen.class.getDeclaredField("isBattleMode");
        isBattleModeField.setAccessible(true);
        assertTrue((boolean) isBattleModeField.get(battleScreen));
        
        // 배틀 모드는 3개 버튼 (Soft, TimeAttack, Item)
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(battleScreen);
        assertEquals(3, buttons.length);
        
        battleScreen.dispose();
    }
    
    @Test
    public void testConstructorSingleplayMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field isBattleModeField = ModeSelectionScreen.class.getDeclaredField("isBattleMode");
        isBattleModeField.setAccessible(true);
        assertFalse((boolean) isBattleModeField.get(screen));
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
    }
    
    @Test
    public void testButtonsArray() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertNotNull(buttons);
        // 싱글 플레이 모드는 2개 버튼 (Soft, Item)
        assertEquals(2, buttons.length);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isFocusable());
        }
    }
    
    @Test
    public void testSelectedIndexInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(screen);
        
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testHandleKeyPressLeft() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressLeftBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRightBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        // 싱글 플레이 모드는 최대 인덱스가 1
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testUpdateButtonFocus() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method updateButtonFocus = ModeSelectionScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
        // 싱글 플레이는 2개 버튼만 있음
    }
    
    @Test
    public void testUpdateButtonFocusSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method updateButtonFocus = ModeSelectionScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
        // 싱글 플레이는 2개 버튼만 있음
    }
    
    @Test
    public void testUpdateButtonFocusThirdButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 배틀 모드에서만 3개 버튼이 있음
        ModeSelectionScreen battleScreen = new ModeSelectionScreen(true);
        
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(battleScreen);
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(battleScreen, 2);
        
        Method updateButtonFocus = ModeSelectionScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(battleScreen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[2].getClientProperty("opacity"));
        
        battleScreen.dispose();
    }
    
    @Test
    public void testAddButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = ModeSelectionScreen.class.getDeclaredMethod(
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
            screen, 
            panel, 
            "/images/SoftModeButton.png", 
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
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getKeyListeners().length > 0);
    }
    
    @Test
    public void testAllButtonsNonNull() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = ModeSelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton btn : buttons) {
            assertNotNull(btn);
        }
    }
    
    @Test
    public void testHandleKeyPressLeftFromSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        // 싱글 플레이는 인덱스 1이 최대
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRightFromFirstButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = ModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = ModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testScreenNavigatorPush() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Constructor에서 ScreenNavigator.getInstance().push() 호출됨
        assertNotNull(screen);
    }
    
    @Test
    public void testScreenNavigatorPushWithBattleMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        ModeSelectionScreen battleScreen = new ModeSelectionScreen(true);
        assertNotNull(battleScreen);
        battleScreen.dispose();
    }
}

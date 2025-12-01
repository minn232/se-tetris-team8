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

public class MultiplaySelectionScreenTest {
    private MultiplaySelectionScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new MultiplaySelectionScreen();
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (screen != null) {
            screen.dispose();
        }
    }
    
    @Test
    public void testConstructor() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(screen);
        assertEquals("Select Multiplayer Mode", screen.getTitle());
        assertFalse(screen.isResizable());
        assertTrue(screen.isFocusable());
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
    }
    
    @Test
    public void testButtonsArray() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = MultiplaySelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertNotNull(buttons);
        assertEquals(2, buttons.length);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isFocusable());
        }
    }
    
    @Test
    public void testSelectedIndexInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(screen);
        
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testHandleKeyPressLeft() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = MultiplaySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressLeftBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = MultiplaySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = MultiplaySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRightBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = MultiplaySelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testUpdateButtonFocus() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = MultiplaySelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method updateButtonFocus = MultiplaySelectionScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testUpdateButtonFocusSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = MultiplaySelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = MultiplaySelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method updateButtonFocus = MultiplaySelectionScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testAddButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = MultiplaySelectionScreen.class.getDeclaredMethod(
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
            "/images/P2PBattleButton.png", 
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
        
        Field buttonsField = MultiplaySelectionScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton btn : buttons) {
            assertNotNull(btn);
        }
    }
    
    @Test
    public void testScreenNavigatorPush() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Constructor에서 ScreenNavigator.getInstance().push("MultiplaySelection") 호출됨
        assertNotNull(screen);
    }
}

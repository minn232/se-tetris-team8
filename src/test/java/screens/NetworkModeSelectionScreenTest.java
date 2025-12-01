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

public class NetworkModeSelectionScreenTest {
    private NetworkModeSelectionScreen hostScreen;
    private NetworkModeSelectionScreen clientScreen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        hostScreen = new NetworkModeSelectionScreen(true);
        clientScreen = new NetworkModeSelectionScreen(false);
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (hostScreen != null) {
            hostScreen.dispose();
        }
        if (clientScreen != null) {
            clientScreen.dispose();
        }
    }
    
    @Test
    public void testConstructorHost() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(hostScreen);
        assertEquals("Select Battle Mode", hostScreen.getTitle());
        assertFalse(hostScreen.isResizable());
        assertTrue(hostScreen.isFocusable());
        
        Field isHostField = NetworkModeSelectionScreen.class.getDeclaredField("isHost");
        isHostField.setAccessible(true);
        assertTrue((boolean) isHostField.get(hostScreen));
    }
    
    @Test
    public void testConstructorClient() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(clientScreen);
        
        Field isHostField = NetworkModeSelectionScreen.class.getDeclaredField("isHost");
        isHostField.setAccessible(true);
        assertFalse((boolean) isHostField.get(clientScreen));
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, hostScreen.getDefaultCloseOperation());
    }
    
    @Test
    public void testModeButtonsArray() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(hostScreen);
        
        assertNotNull(buttons);
        assertEquals(3, buttons.length);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isFocusable());
        }
    }
    
    @Test
    public void testSelectedIndexInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(hostScreen);
        
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testSelectedModeDefault() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedModeField = NetworkModeSelectionScreen.class.getDeclaredField("selectedMode");
        selectedModeField.setAccessible(true);
        String selectedMode = (String) selectedModeField.get(hostScreen);
        
        assertEquals("SOFT", selectedMode);
    }
    
    @Test
    public void testClientModeButtonsDisabled() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(clientScreen);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isEnabled());
        }
    }
    
    @Test
    public void testHostModeButtonsEnabled() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(hostScreen);
        
        for (JButton btn : buttons) {
            assertTrue(btn.isEnabled());
        }
    }
    
    @Test
    public void testHandleKeyPressLeft() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 1);
        
        Method handleKeyPress = NetworkModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(hostScreen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(hostScreen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressLeftBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 0);
        
        Method handleKeyPress = NetworkModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(hostScreen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(hostScreen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 0);
        
        Method handleKeyPress = NetworkModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(hostScreen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(hostScreen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRightBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 2);
        
        Method handleKeyPress = NetworkModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(hostScreen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(hostScreen);
        assertEquals(2, newIndex);
    }
    
    @Test
    public void testHandleKeyPressClientNoEffect() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(clientScreen, 0);
        
        Method handleKeyPress = NetworkModeSelectionScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(clientScreen, KeyEvent.VK_RIGHT);
        
        // Client는 키 입력 무시
        int newIndex = (int) selectedIndexField.get(clientScreen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testUpdateFocus() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(hostScreen);
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 0);
        
        Method updateFocus = NetworkModeSelectionScreen.class.getDeclaredMethod("updateFocus");
        updateFocus.setAccessible(true);
        updateFocus.invoke(hostScreen);
        
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[2].getClientProperty("opacity"));
    }
    
    @Test
    public void testUpdateFocusSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(hostScreen);
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 1);
        
        Method updateFocus = NetworkModeSelectionScreen.class.getDeclaredMethod("updateFocus");
        updateFocus.setAccessible(true);
        updateFocus.invoke(hostScreen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[2].getClientProperty("opacity"));
    }
    
    @Test
    public void testUpdateFocusThirdButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field modeButtonsField = NetworkModeSelectionScreen.class.getDeclaredField("modeButtons");
        modeButtonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) modeButtonsField.get(hostScreen);
        
        Field selectedIndexField = NetworkModeSelectionScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(hostScreen, 2);
        
        Method updateFocus = NetworkModeSelectionScreen.class.getDeclaredMethod("updateFocus");
        updateFocus.setAccessible(true);
        updateFocus.invoke(hostScreen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[2].getClientProperty("opacity"));
    }
    
    @Test
    public void testAddButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = NetworkModeSelectionScreen.class.getDeclaredMethod(
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
            hostScreen, 
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
        
        assertTrue(hostScreen.getKeyListeners().length > 0);
    }
    
    @Test
    public void testScreenNavigatorPush() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Constructor에서 ScreenNavigator.getInstance().push() 호출됨
        assertNotNull(hostScreen);
        assertNotNull(clientScreen);
    }
    
    @Test
    public void testSelectModeMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method selectMode = NetworkModeSelectionScreen.class.getDeclaredMethod("selectMode", String.class);
        selectMode.setAccessible(true);
        assertNotNull(selectMode);
    }
    
    @Test
    public void testOpenBattlePanelMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method openBattlePanel = NetworkModeSelectionScreen.class.getDeclaredMethod("openBattlePanel");
        openBattlePanel.setAccessible(true);
        assertNotNull(openBattlePanel);
    }
}

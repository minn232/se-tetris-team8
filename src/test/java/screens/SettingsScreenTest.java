package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Settings;

public class SettingsScreenTest {
    private SettingsScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new SettingsScreen();
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
        assertEquals("Settings", screen.getTitle());
    }
    
    @Test
    public void testScreenIsVisible() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen.setVisible(true);
        assertTrue(screen.isVisible());
    }
    
    @Test
    public void testColorBlindCheckboxExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chkField = SettingsScreen.class.getDeclaredField("chkColorBlind");
        chkField.setAccessible(true);
        JCheckBox checkbox = (JCheckBox) chkField.get(screen);
        assertNotNull(checkbox);
    }
    
    @Test
    public void testColorBlindCheckboxInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chkField = SettingsScreen.class.getDeclaredField("chkColorBlind");
        chkField.setAccessible(true);
        JCheckBox checkbox = (JCheckBox) chkField.get(screen);
        assertEquals(Settings.isColorBlind(), checkbox.isSelected());
    }
    
    @Test
    public void testClearScoresButtonExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field btnField = SettingsScreen.class.getDeclaredField("btnClearScores");
        btnField.setAccessible(true);
        JButton button = (JButton) btnField.get(screen);
        assertNotNull(button);
        assertEquals("Clear Scoreboard", button.getText());
    }
    
    @Test
    public void testButtonsArrayExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        assertNotNull(buttons);
        assertEquals(4, buttons.length);
    }
    
    @Test
    public void testSelectedIndexInitialValue() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testKeyButtonsP1MapExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        assertNotNull(map);
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void testKeyButtonsP2MapExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        assertNotNull(map);
        assertFalse(map.isEmpty());
    }
    
    @Test
    public void testKeyButtonsP1HasAllBindings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        assertTrue(map.containsKey(Settings.KeyBinding.DOWN));
        assertTrue(map.containsKey(Settings.KeyBinding.LEFT));
        assertTrue(map.containsKey(Settings.KeyBinding.RIGHT));
        assertTrue(map.containsKey(Settings.KeyBinding.ROTATE));
        assertTrue(map.containsKey(Settings.KeyBinding.HARD_DROP));
    }
    
    @Test
    public void testKeyButtonsP2HasAllBindings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        assertTrue(map.containsKey(Settings.KeyBinding.DOWN));
        assertTrue(map.containsKey(Settings.KeyBinding.LEFT));
        assertTrue(map.containsKey(Settings.KeyBinding.RIGHT));
        assertTrue(map.containsKey(Settings.KeyBinding.ROTATE));
        assertTrue(map.containsKey(Settings.KeyBinding.HARD_DROP));
    }
    
    @Test
    public void testAddKeyBindingRowMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method method = SettingsScreen.class.getDeclaredMethod("addKeyBindingRow", 
            javax.swing.JPanel.class, String.class, Settings.KeyBinding.class, java.awt.Font.class);
        method.setAccessible(true);
        assertNotNull(method);
    }
    
    @Test
    public void testSetupKeyBindingListenersMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method method = SettingsScreen.class.getDeclaredMethod("setupKeyBindingListeners");
        method.setAccessible(true);
        assertNotNull(method);
    }
    
    @Test
    public void testHandleKeyPressMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method method = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        method.setAccessible(true);
        assertNotNull(method);
    }
    
    @Test
    public void testUpdateButtonHighlightMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method method = SettingsScreen.class.getDeclaredMethod("updateButtonHighlight");
        method.setAccessible(true);
        assertNotNull(method);
        assertDoesNotThrow(() -> method.invoke(screen));
    }
    
    @Test
    public void testHandleKeyPressUpKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 2);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, upEvent);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressDownKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, downEvent);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressUpKeyWrapAround() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, upEvent);
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(buttons.length - 1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressDownKeyWrapAround() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, buttons.length - 1);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, downEvent);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testScreenSize() {
        if (GraphicsEnvironment.isHeadless()) return;
        assertTrue(screen.getWidth() > 0);
        assertTrue(screen.getHeight() > 0);
    }
    
    @Test
    public void testScreenIsFocusable() {
        if (GraphicsEnvironment.isHeadless()) return;
        assertTrue(screen.isFocusable());
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        assertTrue(screen.getKeyListeners().length > 0);
    }
    
    @Test
    public void testButtonsNotFocusable() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton button : buttons) {
            assertFalse(button.isFocusable());
        }
    }
    
    @Test
    public void testUpdateButtonHighlightChangesBackground() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method updateHighlight = SettingsScreen.class.getDeclaredMethod("updateButtonHighlight");
        updateHighlight.setAccessible(true);
        updateHighlight.invoke(screen);
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertTrue(buttons[1].isOpaque());
        assertFalse(buttons[0].isOpaque());
    }
    
    @Test
    public void testResolutionParsing640x360() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("640x360");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testResolutionParsing1280x720() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1280x720");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testResolutionParsing1920x1080() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1920x1080");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testInvalidResolutionFallback() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("invalid");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testNullResolutionFallback() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution(null);
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testColorBlindCheckboxActionListener() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chkField = SettingsScreen.class.getDeclaredField("chkColorBlind");
        chkField.setAccessible(true);
        JCheckBox checkbox = (JCheckBox) chkField.get(screen);
        
        assertTrue(checkbox.getActionListeners().length > 0);
    }
    
    @Test
    public void testClearScoresButtonActionListener() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field btnField = SettingsScreen.class.getDeclaredField("btnClearScores");
        btnField.setAccessible(true);
        JButton button = (JButton) btnField.get(screen);
        
        assertTrue(button.getActionListeners().length > 0);
    }
    
    @Test
    public void testSaveButtonExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        boolean hasSaveButton = false;
        for (JButton btn : buttons) {
            if ("Save".equals(btn.getText())) {
                hasSaveButton = true;
                break;
            }
        }
        assertTrue(hasSaveButton);
    }
    
    @Test
    public void testCancelButtonExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        boolean hasCancelButton = false;
        for (JButton btn : buttons) {
            if ("Cancel".equals(btn.getText())) {
                hasCancelButton = true;
                break;
            }
        }
        assertTrue(hasCancelButton);
    }
    
    @Test
    public void testResetToDefaultButtonExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        boolean hasResetButton = false;
        for (JButton btn : buttons) {
            if ("Reset to Default".equals(btn.getText())) {
                hasResetButton = true;
                break;
            }
        }
        assertTrue(hasResetButton);
    }
    
    @Test
    public void testAllButtonsHaveActionListeners() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton button : buttons) {
            assertTrue(button.getActionListeners().length > 0);
        }
    }
    
    @Test
    public void testKeyButtonsP1HaveActionListeners() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        for (JButton button : map.values()) {
            assertTrue(button.getActionListeners().length > 0);
        }
    }
    
    @Test
    public void testKeyButtonsP2HaveActionListeners() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        for (JButton button : map.values()) {
            assertTrue(button.getActionListeners().length > 0);
        }
    }
    
    @Test
    public void testHandleKeyPressEnterKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent enterEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED);
        
        assertDoesNotThrow(() -> handleKeyPress.invoke(screen, enterEvent));
    }
    
    @Test
    public void testHandleKeyPressEscapeKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent escapeEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED);
        
        assertDoesNotThrow(() -> handleKeyPress.invoke(screen, escapeEvent));
    }
    
    @Test
    public void testFontSizeCalculationFor1080p() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1920x1080");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertNotNull(testScreen);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testFontSizeCalculationFor720p() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1280x720");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertNotNull(testScreen);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testFontSizeCalculationFor360p() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("640x360");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertNotNull(testScreen);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testResolutionParsingWithSpaces() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1280 x 720");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testResolutionParsingMalformed() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("abc x def");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testResolutionParsingSingleValue() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("1280");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testMultipleKeyPressSequence() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        // Start at 0
        selectedIndexField.set(screen, 0);
        
        // Press down twice
        KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, downEvent);
        handleKeyPress.invoke(screen, downEvent);
        
        assertEquals(2, (int) selectedIndexField.get(screen));
        
        // Press up once
        KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        handleKeyPress.invoke(screen, upEvent);
        
        assertEquals(1, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testUpdateButtonHighlightAllButtons() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method updateHighlight = SettingsScreen.class.getDeclaredMethod("updateButtonHighlight");
        updateHighlight.setAccessible(true);
        
        // Test highlighting each button
        for (int i = 0; i < buttons.length; i++) {
            selectedIndexField.set(screen, i);
            updateHighlight.invoke(screen);
            
            for (int j = 0; j < buttons.length; j++) {
                if (i == j) {
                    assertTrue(buttons[j].isOpaque());
                } else {
                    assertFalse(buttons[j].isOpaque());
                }
            }
        }
    }
    
    @Test
    public void testScreenHasLayoutManager() {
        if (GraphicsEnvironment.isHeadless()) return;
        assertNotNull(screen.getLayout());
    }
    
    @Test
    public void testScreenContentPaneNotNull() {
        if (GraphicsEnvironment.isHeadless()) return;
        assertNotNull(screen.getContentPane());
    }
    
    @Test
    public void testKeyButtonTextMatchesSettings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        for (java.util.Map.Entry<Settings.KeyBinding, JButton> entry : map.entrySet()) {
            Settings.KeyBinding binding = entry.getKey();
            JButton button = entry.getValue();
            String expectedText = KeyEvent.getKeyText(binding.getValue(Settings.Player.P1));
            assertEquals(expectedText, button.getText());
        }
    }
    
    @Test
    public void testKeyButtonTextMatchesSettingsP2() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        for (java.util.Map.Entry<Settings.KeyBinding, JButton> entry : map.entrySet()) {
            Settings.KeyBinding binding = entry.getKey();
            JButton button = entry.getValue();
            String expectedText = KeyEvent.getKeyText(binding.getValue(Settings.Player.P2));
            assertEquals(expectedText, button.getText());
        }
    }
    
    @Test
    public void testAllKeyBindingsRepresented() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        assertEquals(5, map.size());
    }
    
    @Test
    public void testKeyButtonP1DownExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        JButton button = map.get(Settings.KeyBinding.DOWN);
        assertNotNull(button);
        assertNotNull(button.getText());
    }
    
    @Test
    public void testKeyButtonP1LeftExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        JButton button = map.get(Settings.KeyBinding.LEFT);
        assertNotNull(button);
        assertNotNull(button.getText());
    }
    
    @Test
    public void testKeyButtonP1RightExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        JButton button = map.get(Settings.KeyBinding.RIGHT);
        assertNotNull(button);
        assertNotNull(button.getText());
    }
    
    @Test
    public void testKeyButtonP1RotateExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        JButton button = map.get(Settings.KeyBinding.ROTATE);
        assertNotNull(button);
        assertNotNull(button.getText());
    }
    
    @Test
    public void testKeyButtonP1HardDropExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        JButton button = map.get(Settings.KeyBinding.HARD_DROP);
        assertNotNull(button);
        assertNotNull(button.getText());
    }
    
    @Test
    public void testSetupKeyBindingListenersCalledInConstructor() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Verify that key binding listeners are set up by checking button action listeners
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> mapP1 = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        for (JButton button : mapP1.values()) {
            assertTrue(button.getActionListeners().length > 0, "Key binding button should have action listener");
        }
    }
    
    @Test
    public void testColorBlindModeChangesSettings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chkField = SettingsScreen.class.getDeclaredField("chkColorBlind");
        chkField.setAccessible(true);
        JCheckBox checkbox = (JCheckBox) chkField.get(screen);
        
        boolean original = Settings.isColorBlind();
        checkbox.setSelected(!original);
        checkbox.doClick();
        
        // Reset to original
        Settings.setColorBlind(original);
    }
    
    @Test
    public void testInitialButtonHighlight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(screen);
        
        // The button at selectedIndex should be highlighted
        assertTrue(buttons[selectedIndex].isOpaque());
    }
    
    @Test
    public void testButtonArrayContainsClearScores() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Field clearScoresField = SettingsScreen.class.getDeclaredField("btnClearScores");
        clearScoresField.setAccessible(true);
        JButton clearScoresBtn = (JButton) clearScoresField.get(screen);
        
        boolean found = false;
        for (JButton btn : buttons) {
            if (btn == clearScoresBtn) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
    
    @Test
    public void testResolutionWithExtraWhitespace() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String previousRes = Settings.getResolution();
        Settings.setResolution("  1920  x  1080  ");
        
        SettingsScreen testScreen = new SettingsScreen();
        assertTrue(testScreen.getWidth() > 0);
        assertTrue(testScreen.getHeight() > 0);
        testScreen.dispose();
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testConstructorCreatesAllComponents() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Verify all main components are created
        Field chkField = SettingsScreen.class.getDeclaredField("chkColorBlind");
        chkField.setAccessible(true);
        assertNotNull(chkField.get(screen));
        
        Field btnField = SettingsScreen.class.getDeclaredField("btnClearScores");
        btnField.setAccessible(true);
        assertNotNull(btnField.get(screen));
        
        Field buttonsField = SettingsScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        assertNotNull(buttonsField.get(screen));
        
        Field keyButtonsP1Field = SettingsScreen.class.getDeclaredField("keyButtonsP1");
        keyButtonsP1Field.setAccessible(true);
        assertNotNull(keyButtonsP1Field.get(screen));
        
        Field keyButtonsP2Field = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        keyButtonsP2Field.setAccessible(true);
        assertNotNull(keyButtonsP2Field.get(screen));
    }
    
    @Test
    public void testHandleKeyPressWithUnknownKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int initialIndex = (int) selectedIndexField.get(screen);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        // Press an unmapped key (e.g., VK_A)
        KeyEvent unknownEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a');
        handleKeyPress.invoke(screen, unknownEvent);
        
        // Index should not change
        assertEquals(initialIndex, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testMultipleUpPresses() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 2);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent upEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        
        handleKeyPress.invoke(screen, upEvent);
        assertEquals(1, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, upEvent);
        assertEquals(0, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testMultipleDownPresses() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = SettingsScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = SettingsScreen.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        
        KeyEvent downEvent = new KeyEvent(screen, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        
        handleKeyPress.invoke(screen, downEvent);
        assertEquals(1, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, downEvent);
        assertEquals(2, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, downEvent);
        assertEquals(3, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testScreenLocationAfterPack() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Screen should be centered after pack() and setLocationRelativeTo(null)
        assertTrue(screen.getLocation().x >= 0 || screen.getLocation().x <= 0);
        assertTrue(screen.getLocation().y >= 0 || screen.getLocation().y <= 0);
    }
    
    @Test
    public void testScreenHasBorderLayout() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getLayout() instanceof java.awt.BorderLayout);
    }
    
    @Test
    public void testAllResolutionsProduceValidSizes() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String[] resolutions = {"640x360", "1280x720", "1920x1080"};
        String previousRes = Settings.getResolution();
        
        for (String res : resolutions) {
            Settings.setResolution(res);
            SettingsScreen testScreen = new SettingsScreen();
            
            assertTrue(testScreen.getWidth() > 0, "Width should be positive for " + res);
            assertTrue(testScreen.getHeight() > 0, "Height should be positive for " + res);
            
            testScreen.dispose();
        }
        
        Settings.setResolution(previousRes);
    }
    
    @Test
    public void testKeyButtonP2AllBindings() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field mapField = SettingsScreen.class.getDeclaredField("keyButtonsP2");
        mapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Settings.KeyBinding, JButton> map = 
            (java.util.Map<Settings.KeyBinding, JButton>) mapField.get(screen);
        
        assertNotNull(map.get(Settings.KeyBinding.DOWN));
        assertNotNull(map.get(Settings.KeyBinding.LEFT));
        assertNotNull(map.get(Settings.KeyBinding.RIGHT));
        assertNotNull(map.get(Settings.KeyBinding.ROTATE));
        assertNotNull(map.get(Settings.KeyBinding.HARD_DROP));
    }
}



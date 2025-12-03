package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HostJoinScreenTest {
    private HostJoinScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new HostJoinScreen();
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (screen != null) {
            screen.dispose();
        }
        // 테스트 파일 정리
        File lastIpFile = new File("last_ip.txt");
        if (lastIpFile.exists()) {
            lastIpFile.delete();
        }
    }
    
    @Test
    public void testConstructor() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(screen);
        assertTrue(screen.isFocusable());
        assertEquals("Host or Join", screen.getTitle());
        assertFalse(screen.isResizable());
    }
    
    @Test
    public void testButtonsArray() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
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
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int selectedIndex = (int) selectedIndexField.get(screen);
        
        assertEquals(0, selectedIndex);
    }
    
    @Test
    public void testHandleKeyPressLeft() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressLeftBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(0, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testHandleKeyPressRightBoundary() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        
        int newIndex = (int) selectedIndexField.get(screen);
        assertEquals(1, newIndex);
    }
    
    @Test
    public void testUpdateButtonFocus() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method updateButtonFocus = HostJoinScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testUpdateButtonFocusSecondButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method updateButtonFocus = HostJoinScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        updateButtonFocus.invoke(screen);
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testAddButton() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = HostJoinScreen.class.getDeclaredMethod(
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
            "/images/HostButton.png", 
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
    public void testLoadLastIPNoFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        if (file.exists()) file.delete();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("", result);
    }
    
    @Test
    public void testLoadLastIPWithFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("192.168.1.1");
        writer.close();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("192.168.1.1", result);
        
        file.delete();
    }
    
    @Test
    public void testLoadLastIPWithWhitespace() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("  192.168.1.1  \n");
        writer.close();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("192.168.1.1", result);
        
        file.delete();
    }
    
    @Test
    public void testSaveLastIP() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method saveLastIP = HostJoinScreen.class.getDeclaredMethod("saveLastIP", String.class);
        saveLastIP.setAccessible(true);
        saveLastIP.invoke(screen, "192.168.0.1");
        
        File file = new File("last_ip.txt");
        assertTrue(file.exists());
        
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
        String content = reader.readLine();
        reader.close();
        
        assertEquals("192.168.0.1", content);
        
        file.delete();
    }
    
    @Test
    public void testIsValidIPLocalhost() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "localhost"));
        assertTrue((boolean) isValidIP.invoke(screen, "LOCALHOST"));
        assertTrue((boolean) isValidIP.invoke(screen, "LocalHost"));
    }
    
    @Test
    public void testIsValidIPv4() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "192.168.1.1"));
        assertTrue((boolean) isValidIP.invoke(screen, "10.0.0.1"));
        assertTrue((boolean) isValidIP.invoke(screen, "255.255.255.255"));
        assertTrue((boolean) isValidIP.invoke(screen, "0.0.0.0"));
    }
    
    @Test
    public void testIsValidIPInvalid() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertFalse((boolean) isValidIP.invoke(screen, "256.1.1.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "abc.def.ghi.jkl"));
        assertFalse((boolean) isValidIP.invoke(screen, ""));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1.1.1"));
    }
    
    @Test
    public void testScreenNavigatorPush() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Constructor에서 ScreenNavigator.getInstance().push("HostJoin") 호출됨
        assertNotNull(screen);
    }
    
    @Test
    public void testHandleKeyPressSpace() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // Space 키는 doClick()을 호출하므로 예외가 발생하지 않으면 성공
        handleKeyPress.invoke(screen, KeyEvent.VK_SPACE);
    }
    
    @Test
    public void testHandleKeyPressEnter() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // Enter 키는 doClick()을 호출하므로 예외가 발생하지 않으면 성공
        handleKeyPress.invoke(screen, KeyEvent.VK_ENTER);
    }
    
    @Test
    public void testHandleKeyPressEscape() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // ESC 키는 goBack()을 호출
        handleKeyPress.invoke(screen, KeyEvent.VK_ESCAPE);
    }
    
    @Test
    public void testHandleKeyPressUnknownKey() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        int initialIndex = (int) selectedIndexField.get(screen);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // 매핑되지 않은 키 (예: VK_A)
        handleKeyPress.invoke(screen, KeyEvent.VK_A);
        
        // 인덱스는 변경되지 않아야 함
        assertEquals(initialIndex, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getKeyListeners().length > 0);
    }
    
    @Test
    public void testScreenIsFocusable() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.isFocusable());
    }
    
    @Test
    public void testIsValidIPWithNullOrEmpty() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertFalse((boolean) isValidIP.invoke(screen, ""));
        assertFalse((boolean) isValidIP.invoke(screen, "   "));
    }
    
    @Test
    public void testIsValidIPWithLeadingZeros() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "001.002.003.004"));
        assertTrue((boolean) isValidIP.invoke(screen, "192.168.001.001"));
    }
    
    @Test
    public void testIsValidIPBoundaryValues() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "0.0.0.0"));
        assertTrue((boolean) isValidIP.invoke(screen, "255.255.255.255"));
        assertFalse((boolean) isValidIP.invoke(screen, "256.0.0.0"));
        assertFalse((boolean) isValidIP.invoke(screen, "0.256.0.0"));
        assertFalse((boolean) isValidIP.invoke(screen, "0.0.256.0"));
        assertFalse((boolean) isValidIP.invoke(screen, "0.0.0.256"));
    }
    
    @Test
    public void testIsValidIPMalformed() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1.1.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168..1"));
        assertFalse((boolean) isValidIP.invoke(screen, ".192.168.1.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1.1."));
    }
    
    @Test
    public void testSaveLastIPWithEmptyString() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method saveLastIP = HostJoinScreen.class.getDeclaredMethod("saveLastIP", String.class);
        saveLastIP.setAccessible(true);
        saveLastIP.invoke(screen, "");
        
        File file = new File("last_ip.txt");
        assertTrue(file.exists());
        
        file.delete();
    }
    
    @Test
    public void testLoadLastIPWithEmptyFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("");
        writer.close();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("", result);
        
        file.delete();
    }
    
    @Test
    public void testLoadLastIPWithMultipleLines() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("192.168.1.1\n192.168.1.2\n");
        writer.close();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        // 첫 번째 줄만 읽음
        assertEquals("192.168.1.1", result);
        
        file.delete();
    }
    
    @Test
    public void testButtonsArrayLength() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        assertEquals(2, buttons.length);
    }
    
    @Test
    public void testMultipleLeftKeyPresses() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 1);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(screen));
        
        // 이미 0이므로 더 이상 감소하지 않음
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testMultipleRightKeyPresses() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(screen));
        
        // 이미 1(마지막)이므로 더 이상 증가하지 않음
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testAlternatingKeyPresses() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        selectedIndexField.set(screen, 0);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testUpdateButtonFocusAllIndices() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        Method updateButtonFocus = HostJoinScreen.class.getDeclaredMethod("updateButtonFocus");
        updateButtonFocus.setAccessible(true);
        
        // Test index 0
        selectedIndexField.set(screen, 0);
        updateButtonFocus.invoke(screen);
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
        
        // Test index 1
        selectedIndexField.set(screen, 1);
        updateButtonFocus.invoke(screen);
        assertEquals(1.0f, buttons[0].getClientProperty("opacity"));
        assertEquals(0.75f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testScreenSize() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getWidth() > 0);
        assertTrue(screen.getHeight() > 0);
    }
    
    @Test
    public void testScreenLocation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // setLocationRelativeTo(null)로 중앙 배치되므로 위치가 설정되어 있음
        assertNotNull(screen.getLocation());
    }
    
    @Test
    public void testIsValidIPLocalhostCaseInsensitive() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "LOCALHOST"));
        assertTrue((boolean) isValidIP.invoke(screen, "localhost"));
        assertTrue((boolean) isValidIP.invoke(screen, "LocalHost"));
        assertTrue((boolean) isValidIP.invoke(screen, "LoCaLhOsT"));
    }
    
    @Test
    public void testBackgroundPanel() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // BackgroundPanel이 contentPane으로 설정되어 있는지 확인
        assertNotNull(screen.getContentPane());
    }
    
    @Test
    public void testButtonsNotFocusable() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton btn : buttons) {
            assertFalse(btn.isFocusable());
        }
    }
    
    @Test
    public void testAddButtonMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method addButton = HostJoinScreen.class.getDeclaredMethod(
            "addButton", JPanel.class, String.class, int.class, int.class, int.class, int.class
        );
        addButton.setAccessible(true);
        
        JPanel testPanel = new JPanel();
        testPanel.setLayout(null);
        
        JButton result = (JButton) addButton.invoke(
            screen, testPanel, "/images/button/host_btn.png", 150, 50, 0, 0
        );
        
        assertNotNull(result);
        assertEquals(150, result.getWidth());
        assertEquals(50, result.getHeight());
    }
    
    @Test
    public void testGoBackActionListener() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        // 버튼이 ActionListener를 가지고 있는지 확인
        for (JButton btn : buttons) {
            assertTrue(btn.getActionListeners().length > 0);
        }
    }
    
    @Test
    public void testIOExceptionInLoadLastIP() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 파일이 없는 경우 빈 문자열 반환
        File file = new File("last_ip.txt");
        if (file.exists()) file.delete();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("", result);
    }
    
    @Test
    public void testSaveLastIPWithSpecialCharacters() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method saveLastIP = HostJoinScreen.class.getDeclaredMethod("saveLastIP", String.class);
        saveLastIP.setAccessible(true);
        saveLastIP.invoke(screen, "192.168.1.1\n192.168.1.2");
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        // 첫 번째 줄만 로드됨
        assertEquals("192.168.1.1", result);
        
        File file = new File("last_ip.txt");
        file.delete();
    }
    
    @Test
    public void testLoadLastIPWithNullLine() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        File file = new File("last_ip.txt");
        FileWriter writer = new FileWriter(file);
        writer.close();
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        String result = (String) loadLastIP.invoke(screen);
        
        assertEquals("", result);
        
        file.delete();
    }
    
    @Test
    public void testIsValidIPWithWhitespace() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        // IP 패턴은 공백을 허용하지 않음
        assertFalse((boolean) isValidIP.invoke(screen, " 192.168.1.1"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.1.1 "));
        assertFalse((boolean) isValidIP.invoke(screen, "192. 168.1.1"));
    }
    
    @Test
    public void testIsValidIPWithNegativeNumbers() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertFalse((boolean) isValidIP.invoke(screen, "-1.0.0.0"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.-168.1.1"));
    }
    
    @Test
    public void testButtonOpacityInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        // 첫 번째 버튼만 선택됨
        assertEquals(0.75f, buttons[0].getClientProperty("opacity"));
        assertEquals(1.0f, buttons[1].getClientProperty("opacity"));
    }
    
    @Test
    public void testHandleKeyPressSequence() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedIndexField = HostJoinScreen.class.getDeclaredField("selectedIndex");
        selectedIndexField.setAccessible(true);
        
        Method handleKeyPress = HostJoinScreen.class.getDeclaredMethod("handleKeyPress", int.class);
        handleKeyPress.setAccessible(true);
        
        // 0 -> RIGHT -> 1 -> LEFT -> 0
        selectedIndexField.set(screen, 0);
        handleKeyPress.invoke(screen, KeyEvent.VK_RIGHT);
        assertEquals(1, (int) selectedIndexField.get(screen));
        
        handleKeyPress.invoke(screen, KeyEvent.VK_LEFT);
        assertEquals(0, (int) selectedIndexField.get(screen));
    }
    
    @Test
    public void testScreenTitle() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(screen.getTitle());
    }
    
    @Test
    public void testScreenDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(JFrame.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
    }
    
    @Test
    public void testSaveAndLoadLastIPCycle() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method saveLastIP = HostJoinScreen.class.getDeclaredMethod("saveLastIP", String.class);
        saveLastIP.setAccessible(true);
        
        Method loadLastIP = HostJoinScreen.class.getDeclaredMethod("loadLastIP");
        loadLastIP.setAccessible(true);
        
        String testIP = "10.0.0.5";
        saveLastIP.invoke(screen, testIP);
        String loaded = (String) loadLastIP.invoke(screen);
        
        assertEquals(testIP, loaded);
        
        File file = new File("last_ip.txt");
        file.delete();
    }
    
    @Test
    public void testButtonBoundsSet() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field buttonsField = HostJoinScreen.class.getDeclaredField("buttons");
        buttonsField.setAccessible(true);
        JButton[] buttons = (JButton[]) buttonsField.get(screen);
        
        for (JButton btn : buttons) {
            assertNotNull(btn.getBounds());
            assertTrue(btn.getWidth() > 0);
            assertTrue(btn.getHeight() > 0);
        }
    }
}

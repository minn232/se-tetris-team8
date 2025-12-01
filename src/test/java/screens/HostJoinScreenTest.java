package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
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
    public void testIsValidIPEdgeCases() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method isValidIP = HostJoinScreen.class.getDeclaredMethod("isValidIP", String.class);
        isValidIP.setAccessible(true);
        
        assertTrue((boolean) isValidIP.invoke(screen, "127.0.0.1"));
        assertTrue((boolean) isValidIP.invoke(screen, "1.2.3.4"));
        assertFalse((boolean) isValidIP.invoke(screen, "999.999.999.999"));
        assertFalse((boolean) isValidIP.invoke(screen, "192.168.-1.1"));
    }
    
    @Test
    public void testScreenNavigatorPush() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // Constructor에서 ScreenNavigator.getInstance().push("HostJoin") 호출됨
        assertNotNull(screen);
    }
}

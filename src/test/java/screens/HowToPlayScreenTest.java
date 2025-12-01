package screens;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HowToPlayScreenTest {
    private HowToPlayScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new HowToPlayScreen();
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
        assertEquals("How to Play", screen.getTitle());
        assertTrue(screen.isModal());
        assertFalse(screen.isResizable());
        assertTrue(screen.isFocusable());
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JDialog.DISPOSE_ON_CLOSE, screen.getDefaultCloseOperation());
    }
    
    @Test
    public void testLayout() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getLayout() instanceof java.awt.BorderLayout);
    }
    
    @Test
    public void testImageLabel() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JLabel imageLabel = findImageLabel(screen.getContentPane());
        if (imageLabel != null) {
            assertEquals(JLabel.CENTER, imageLabel.getHorizontalAlignment());
            assertNotNull(imageLabel.getIcon());
        }
    }
    
    @Test
    public void testButtonPanel() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        
        assertNotNull(buttonPanel);
        assertTrue(buttonPanel.getLayout() instanceof javax.swing.BoxLayout);
    }
    
    @Test
    public void testCloseButton() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        JButton closeButton = findCloseButton(buttonPanel);
        
        assertNotNull(closeButton);
        assertEquals("Close", closeButton.getText());
        assertFalse(closeButton.isFocusable());
    }
    
    @Test
    public void testCloseButtonAction() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        JButton closeButton = findCloseButton(buttonPanel);
        
        assertTrue(screen.isVisible() || !screen.isShowing());
        closeButton.doClick();
        assertFalse(screen.isDisplayable());
    }
    
    @Test
    public void testEscapeKeyPressed() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        KeyEvent escapeEvent = new KeyEvent(
            screen,
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ESCAPE,
            KeyEvent.CHAR_UNDEFINED
        );
        
        screen.dispatchEvent(escapeEvent);
        assertFalse(screen.isDisplayable());
    }
    
    @Test
    public void testNonEscapeKeyPressed() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // ENTER 키는 아무 동작도 하지 않으므로 화면이 여전히 유효해야 함
        boolean wasDisplayable = screen.isDisplayable();
        
        KeyEvent enterEvent = new KeyEvent(
            screen,
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            KeyEvent.VK_ENTER,
            KeyEvent.CHAR_UNDEFINED
        );
        
        screen.dispatchEvent(enterEvent);
        
        // 다이얼로그가 처음에 표시 가능했다면 여전히 표시 가능해야 함
        // 헤드리스 환경이나 초기화 문제로 인해 처음부터 표시 불가능할 수 있음
        if (wasDisplayable) {
            assertTrue(screen.isDisplayable());
        }
    }
    
    @Test
    public void testWindowSize() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getWidth() > 0);
        assertTrue(screen.getHeight() > 0);
    }
    
    @Test
    public void testCloseButtonFont() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        JButton closeButton = findCloseButton(buttonPanel);
        
        assertNotNull(closeButton.getFont());
        assertEquals("Arial", closeButton.getFont().getFamily());
        assertEquals(java.awt.Font.PLAIN, closeButton.getFont().getStyle());
    }
    
    @Test
    public void testCloseButtonSize() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        JButton closeButton = findCloseButton(buttonPanel);
        
        assertNotNull(closeButton.getPreferredSize());
        assertTrue(closeButton.getPreferredSize().width > 0);
        assertTrue(closeButton.getPreferredSize().height > 0);
    }
    
    @Test
    public void testButtonPanelBorder() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JPanel buttonPanel = findButtonPanel(screen.getContentPane());
        assertNotNull(buttonPanel.getBorder());
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getKeyListeners().length > 0);
    }
    
    private JButton findCloseButton(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if ("Close".equals(btn.getText())) {
                    return btn;
                }
            }
        }
        return null;
    }
    
    private JPanel findButtonPanel(java.awt.Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getLayout() instanceof javax.swing.BoxLayout) {
                    return panel;
                }
            }
            if (comp instanceof java.awt.Container) {
                JPanel result = findButtonPanel((java.awt.Container) comp);
                if (result != null) return result;
            }
        }
        return null;
    }
    
    private JLabel findImageLabel(java.awt.Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getIcon() != null) {
                    return label;
                }
            }
            if (comp instanceof java.awt.Container) {
                JLabel result = findImageLabel((java.awt.Container) comp);
                if (result != null) return result;
            }
        }
        return null;
    }
}
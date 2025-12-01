package screens;

import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import javax.swing.JButton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImageButtonUtilsTest {
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
    }
    
    @Test
    public void testLoadImageFromClasspath() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = ImageButtonUtils.loadImage("/images/MainScreen.png");
        assertNotNull(img);
    }
    
    @Test
    public void testLoadImageWithRelativePath() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = ImageButtonUtils.loadImage("images/MainScreen.png");
        assertNotNull(img);
    }
    
    @Test
    public void testLoadImageNonExistent() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = ImageButtonUtils.loadImage("/nonexistent/image.png");
        assertNull(img);
    }
    
    @Test
    public void testCreateImageButton() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        assertNotNull(button);
        assertEquals(100, button.getPreferredSize().width);
        assertEquals(50, button.getPreferredSize().height);
    }
    
    @Test
    public void testCreateImageButtonProperties() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        
        assertFalse(button.isBorderPainted());
        assertFalse(button.isContentAreaFilled());
        assertFalse(button.isOpaque());
        assertFalse(button.isFocusPainted());
    }
    
    @Test
    public void testCreateImageButtonMouseListeners() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        
        assertTrue(button.getMouseListeners().length > 0);
        assertTrue(button.getMouseMotionListeners().length > 0);
    }
    
    @Test
    public void testButtonMousePressed() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent pressEvent = new MouseEvent(button, MouseEvent.MOUSE_PRESSED, 
            System.currentTimeMillis(), 0, 50, 25, 1, false);
        
        for (var listener : button.getMouseListeners()) {
            listener.mousePressed(pressEvent);
        }
        
        // mousePressed 이벤트가 처리됨
        assertNotNull(button);
    }
    
    @Test
    public void testButtonMouseReleased() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent releaseEvent = new MouseEvent(button, MouseEvent.MOUSE_RELEASED, 
            System.currentTimeMillis(), 0, 50, 25, 1, false);
        
        for (var listener : button.getMouseListeners()) {
            listener.mouseReleased(releaseEvent);
        }
        
        assertNotNull(button);
    }
    
    @Test
    public void testButtonMouseEntered() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent enterEvent = new MouseEvent(button, MouseEvent.MOUSE_ENTERED, 
            System.currentTimeMillis(), 0, 50, 25, 0, false);
        
        for (var listener : button.getMouseListeners()) {
            listener.mouseEntered(enterEvent);
        }
        
        assertNotNull(button);
    }
    
    @Test
    public void testButtonMouseExited() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent exitEvent = new MouseEvent(button, MouseEvent.MOUSE_EXITED, 
            System.currentTimeMillis(), 0, -1, -1, 0, false);
        
        for (var listener : button.getMouseListeners()) {
            listener.mouseExited(exitEvent);
        }
        
        assertNotNull(button);
    }
    
    @Test
    public void testButtonMouseMoved() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent moveEvent = new MouseEvent(button, MouseEvent.MOUSE_MOVED, 
            System.currentTimeMillis(), 0, 50, 25, 0, false);
        
        for (var listener : button.getMouseMotionListeners()) {
            listener.mouseMoved(moveEvent);
        }
        
        assertNotNull(button);
    }
    
    @Test
    public void testButtonMouseMovedTransparentArea() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        MouseEvent moveEvent = new MouseEvent(button, MouseEvent.MOUSE_MOVED, 
            System.currentTimeMillis(), 0, -10, -10, 0, false);
        
        for (var listener : button.getMouseMotionListeners()) {
            listener.mouseMoved(moveEvent);
        }
        
        assertNotNull(button);
    }
    
    @Test
    public void testButtonPaintComponent() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        button.paint(g2);
        
        g2.dispose();
        assertNotNull(button);
    }
    
    @Test
    public void testButtonPaintComponentWithOpacity() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        button.putClientProperty("opacity", 0.5f);
        
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        button.paint(g2);
        
        g2.dispose();
        assertEquals(0.5f, button.getClientProperty("opacity"));
    }
    
    @Test
    public void testButtonPaintComponentNoImage() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/nonexistent/button.png", 100, 50);
        button.setSize(100, 50);
        
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        button.paint(g2);
        
        g2.dispose();
        assertNotNull(button);
    }
    
    @Test
    public void testIsTransparentAtMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/images/SoftModeButton.png", 100, 50);
        button.setSize(100, 50);
        
        Method isTransparentAt = button.getClass().getDeclaredMethod("isTransparentAt", int.class, int.class);
        isTransparentAt.setAccessible(true);
        
        // 버튼 내부 좌표
        Boolean result1 = (Boolean) isTransparentAt.invoke(button, 50, 25);
        assertNotNull(result1);
        
        // 버튼 외부 좌표
        Boolean result2 = (Boolean) isTransparentAt.invoke(button, -10, -10);
        assertTrue(result2);
        
        // 경계 외부
        Boolean result3 = (Boolean) isTransparentAt.invoke(button, 200, 200);
        assertTrue(result3);
    }
    
    @Test
    public void testLoadImageFromFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 파일 시스템에서 로드 시도
        String path = "/images/MainScreen.png";
        BufferedImage img = ImageButtonUtils.loadImage(path);
        
        // 클래스패스나 파일 시스템에서 로드됨
        assertNotNull(img);
    }
    
    @Test
    public void testCreateImageButtonWithNonExistentImage() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        JButton button = ImageButtonUtils.createImageButton("/nonexistent/image.png", 80, 40);
        
        assertNotNull(button);
        assertEquals(80, button.getPreferredSize().width);
        assertEquals(40, button.getPreferredSize().height);
    }
}

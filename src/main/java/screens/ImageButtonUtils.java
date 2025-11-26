package screens;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

/**
 * 이미지 버튼 생성 및 이미지 로딩 유틸리티 클래스
 */
public class ImageButtonUtils {

    /**
     * 이미지 로드 (클래스패스 + 파일 폴백)
     */
    public static BufferedImage loadImage(String resourcePath) throws IOException {
        String absPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        java.net.URL url = ImageButtonUtils.class.getResource(absPath);
        if (url != null) {
            return ImageIO.read(url);
        }

        String loaderPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        java.io.InputStream is = ImageButtonUtils.class.getClassLoader().getResourceAsStream(loaderPath);
        if (is != null) {
            try {
                BufferedImage img = ImageIO.read(is);
                return img;
            } finally {
                is.close();
            }
        }

        String cwd = System.getProperty("user.dir");
        java.io.File f = new java.io.File(cwd + "/src/main/resources" + absPath);
        if (f.exists()) {
            return ImageIO.read(f);
        }

        return null;
    }

    /**
     * 애니메이션 효과가 있는 이미지 버튼 생성
     */
    public static JButton createImageButton(String resourcePath, int width, int height) {
        JButton btn = new JButton() {
            private BufferedImage buttonImage;
            private boolean isPressed = false;
            private float opacity = 1.0f;
            
            {
                try {
                    buttonImage = loadImage(resourcePath);
                    if (buttonImage != null) {
                        System.out.println("[ImageButton] Loaded: " + resourcePath 
                            + " (" + buttonImage.getWidth() + "x" + buttonImage.getHeight() + ")");
                    } else {
                        System.err.println("[ImageButton] Not found: " + resourcePath);
                    }
                } catch (Exception e) {
                    buttonImage = null;
                    System.err.println("[ImageButton] Failed to load: " + resourcePath + " -> " + e);
                }
                
                // 마우스 애니메이션
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        if (!isTransparentAt(e.getX(), e.getY())) {
                            isPressed = true;
                            opacity = 0.7f;
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseReleased(java.awt.event.MouseEvent e) {
                        if (!isTransparentAt(e.getX(), e.getY())) {
                            isPressed = false;
                            opacity = 1.0f;
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (!isTransparentAt(e.getX(), e.getY())) {
                            opacity = 0.85f;
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        opacity = 1.0f;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                });
                
                addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(java.awt.event.MouseEvent e) {
                        if (isTransparentAt(e.getX(), e.getY())) {
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            if (opacity != 1.0f && !isPressed) {
                                opacity = 1.0f;
                                repaint();
                            }
                        } else {
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            if (opacity == 1.0f && !isPressed) {
                                opacity = 0.85f;
                                repaint();
                            }
                        }
                    }
                });
            }
            
            private boolean isTransparentAt(int x, int y) {
                if (buttonImage == null) return false;
                
                int w = getWidth();
                int h = getHeight();
                
                if (x < 0 || y < 0 || x >= w || y >= h) return true;
                
                int imgX = (int) ((double) x / w * buttonImage.getWidth());
                int imgY = (int) ((double) y / h * buttonImage.getHeight());
                
                if (imgX < 0 || imgY < 0 || imgX >= buttonImage.getWidth() || imgY >= buttonImage.getHeight()) {
                    return true;
                }
                
                int pixel = buttonImage.getRGB(imgX, imgY);
                int alpha = (pixel >> 24) & 0xff;
                return alpha < 1;
            }
            
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
                
                if (buttonImage != null) {
                    g2d.setComposite(java.awt.AlphaComposite.getInstance(
                        java.awt.AlphaComposite.SRC_OVER, opacity));
                    
                    int w = getWidth();
                    int h = getHeight();
                    
                    if (isPressed) {
                        int offset = 5;
                        g2d.drawImage(buttonImage, offset, offset, w - offset * 2, h - offset * 2, this);
                    } else {
                        g2d.drawImage(buttonImage, 0, 0, w, h, this);
                    }
                } else {
                    g2d.setColor(java.awt.Color.LIGHT_GRAY);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(java.awt.Color.BLACK);
                    g2d.drawString("Button", 10, getHeight()/2);
                }
                
                g2d.dispose();
            }
        };
        
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        
        return btn;
    }
}

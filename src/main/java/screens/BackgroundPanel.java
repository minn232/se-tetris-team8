package screens;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * 배경 이미지를 표시하는 패널
 */
public class BackgroundPanel extends JPanel {
    private BufferedImage background;

    public BackgroundPanel(String resourcePath) {
        try {
            background = ImageButtonUtils.loadImage(resourcePath);
            if (background != null) {
                System.out.println("[BackgroundPanel] Loaded: " + resourcePath 
                    + " (" + background.getWidth() + "x" + background.getHeight() + ")");
            } else {
                System.err.println("[BackgroundPanel] Not found: " + resourcePath);
            }
        } catch (Exception e) {
            background = null;
            System.err.println("[BackgroundPanel] Failed to load: " + resourcePath + " -> " + e);
        }
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            int w = getWidth(), h = getHeight();
            g.drawImage(background, 0, 0, w, h, this);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

package screens;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Settings;

/**
 * 싱글플레이 모드 선택 화면
 */
public class ModeSelectionScreen extends JFrame {

    public ModeSelectionScreen() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Select Game Mode");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        
        int btnWidth = (int)(80 * scale);
        int btnHeight = (int)(100 * scale);
        
        JButton normalButton = addButton(mainPanel, "/images/NormalModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 30 * scale), (int)(height / 2.0 - 20 * scale));
        normalButton.addActionListener(e -> {
            System.out.println("[ModeSelection] Normal Mode selected");
            dispose();
        });
        
        JButton timeAttackButton = addButton(mainPanel, "/images/TimeattackModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 130 * scale), (int)(height / 2.0 - 20 * scale));
        timeAttackButton.addActionListener(e -> {
            System.out.println("[ModeSelection] Time Attack Mode selected");
            dispose();
        });
        
        JButton itemButton = addButton(mainPanel, "/images/ItemModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 230 * scale), (int)(height / 2.0 - 20 * scale));
        itemButton.addActionListener(e -> {
            System.out.println("[ModeSelection] Item Mode selected");
            dispose();
        });
        
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
    }
    
    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }
}

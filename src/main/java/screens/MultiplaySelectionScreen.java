package screens;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Settings;

/**
 * 멀티플레이 모드 선택 화면
 */
public class MultiplaySelectionScreen extends JFrame {

    public MultiplaySelectionScreen() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Select Multiplayer Mode");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        
        int btnWidth = (int)(100 * scale);
        int btnHeight = (int)(100 * scale);
        
        JButton p2pButton = addButton(mainPanel, "/images/P2PBattleButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 60 * scale), (int)(height / 2.0 - 20 * scale));
        p2pButton.addActionListener(e -> {
            System.out.println("[MultiplaySelection] P2P Battle selected");
            dispose();
            new ModeSelectionScreen().setVisible(true);
        });
        
        JButton networkButton = addButton(mainPanel, "/images/NetworkBattleButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 210 * scale), (int)(height / 2.0 - 20 * scale));
        networkButton.addActionListener(e -> {
            System.out.println("[MultiplaySelection] Network Battle selected");
            dispose();
            new ModeSelectionScreen().setVisible(true);
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

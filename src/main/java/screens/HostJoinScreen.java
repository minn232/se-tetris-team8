package screens;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Settings;

/**
 * 네트워크 배틀 - Host/Join 선택 화면
 */
public class HostJoinScreen extends JFrame {

    public HostJoinScreen() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Host or Join");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        
        int btnWidth = (int)(120 * scale);
        int btnHeight = (int)(120 * scale);
        
        JButton hostButton = addButton(mainPanel, "/images/HostButton.png", 
            btnWidth-50, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 80 * scale), (int)(height / 2.0 - 30 * scale));
        hostButton.addActionListener(e -> {
            System.out.println("[HostJoin] Host selected");
            dispose();
            new ModeSelectionScreen().setVisible(true);
        });
        
        JButton joinButton = addButton(mainPanel, "/images/HostingButton.png", 
            btnWidth+30, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 +170 * scale), (int)(height / 2.0 - 30 * scale));
        joinButton.addActionListener(e -> {
            System.out.println("[HostJoin] Join selected");
            dispose();
            // TODO: Join 게임 시작 로직
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

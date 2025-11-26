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
    
    private JButton[] buttons;
    private int selectedIndex = 0;

    public MultiplaySelectionScreen() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Select Multiplayer Mode");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);
        
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
            (int)(width / 2.0 - btnWidth / 2.0 + 200 * scale), (int)(height / 2.0 - 19 * scale));
        networkButton.addActionListener(e -> {
            System.out.println("[MultiplaySelection] Network Battle selected");
            dispose();
            new HostJoinScreen().setVisible(true);
        });
        
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
        
        // 버튼 배열 초기화 (키보드 네비게이션용)
        buttons = new JButton[]{p2pButton, networkButton};
        
        // 모든 버튼의 포커스 비활성화
        for (JButton btn : buttons) {
            btn.setFocusable(false);
        }
        
        // 초기 포커스 설정
        updateButtonFocus();
        
        // 키보드 리스너 추가
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_LEFT -> {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    updateButtonFocus();
                }
            }
            case java.awt.event.KeyEvent.VK_RIGHT -> {
                if (selectedIndex < buttons.length - 1) {
                    selectedIndex++;
                    updateButtonFocus();
                }
            }
            case java.awt.event.KeyEvent.VK_SPACE, java.awt.event.KeyEvent.VK_ENTER -> 
                buttons[selectedIndex].doClick();
        }
    }
    
    private void updateButtonFocus() {
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                ((javax.swing.JComponent) buttons[i]).putClientProperty("opacity", 0.75f);
                buttons[i].repaint();
            } else {
                ((javax.swing.JComponent) buttons[i]).putClientProperty("opacity", 1.0f);
                buttons[i].repaint();
            }
        }
    }
    
    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }
}

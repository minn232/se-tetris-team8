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
    
    private JButton[] buttons;
    private int selectedIndex = 0;
    private final boolean isBattleMode;

    public ModeSelectionScreen() {
        this(false); // 기본은 싱글플레이
    }
    
    public ModeSelectionScreen(boolean isBattleMode) {
        this.isBattleMode = isBattleMode;
        
        // 네비게이션 히스토리에 추가
        ScreenNavigator.getInstance().push("ModeSelection", isBattleMode);
        
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Select Game Mode");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        
        int btnWidth = (int)(80 * scale);
        int btnHeight = (int)(120 * scale);
        
        // 싱글플레이와 배틀 모드에 따라 버튼 구성 변경
        if (isBattleMode) {
            // 배틀 모드: 3개 버튼 (Soft, TimeAttack, Item)
            JButton softButton = addButton(mainPanel, "/images/SoftModeButton.png", 
                btnWidth, btnHeight, 
                (int)(width / 2.0 - btnWidth / 2.0 + 30 * scale), (int)(height / 2.0 - 20 * scale));
            softButton.addActionListener(e -> {
                System.out.println("[ModeSelection] Soft Mode selected");
                dispose();
                new DifficultySelectionScreen(false, isBattleMode).setVisible(true);
            });
            
            JButton timeAttackButton = addButton(mainPanel, "/images/TimeattackModeButton.png", 
                btnWidth, btnHeight, 
                (int)(width / 2.0 - btnWidth / 2.0 + 130 * scale), (int)(height / 2.0 - 20 * scale));
            timeAttackButton.addActionListener(e -> {
                System.out.println("[ModeSelection] Time Attack Mode selected");
                dispose();
                new DifficultySelectionScreen(false, isBattleMode, true).setVisible(true);
            });
            
            JButton itemButton = addButton(mainPanel, "/images/ItemModeButton.png", 
                btnWidth, btnHeight, 
                (int)(width / 2.0 - btnWidth / 2.0 + 230 * scale), (int)(height / 2.0 - 20 * scale));
            itemButton.addActionListener(e -> {
                System.out.println("[ModeSelection] Item Mode selected");
                dispose();
                new DifficultySelectionScreen(true, isBattleMode).setVisible(true);
            });
            
            buttons = new JButton[]{softButton, timeAttackButton, itemButton};
        } else {
            // 싱글플레이: 2개 버튼 (Soft, Item) - 가운데 정렬
            JButton softButton = addButton(mainPanel, "/images/SoftModeButton.png", 
                btnWidth, btnHeight, 
                (int)(width / 2.0 - btnWidth / 2.0 + 80 * scale), (int)(height / 2.0 - 20 * scale));
            softButton.addActionListener(e -> {
                System.out.println("[ModeSelection] Soft Mode selected");
                dispose();
                new DifficultySelectionScreen(false, isBattleMode).setVisible(true);
            });
            
            JButton itemButton = addButton(mainPanel, "/images/ItemModeButton.png", 
                btnWidth, btnHeight, 
                (int)(width / 2.0 - btnWidth / 2.0 + 180 * scale), (int)(height / 2.0 - 20 * scale));
            itemButton.addActionListener(e -> {
                System.out.println("[ModeSelection] Item Mode selected");
                dispose();
                new DifficultySelectionScreen(true, isBattleMode).setVisible(true);
            });
            
            buttons = new JButton[]{softButton, itemButton};
        }
        
        // 뒤로가기 버튼 추가
        int backBtnSize = (int)(40 * scale);
        int backBtnX = width - backBtnSize - (int)(10 * scale);
        int backBtnY = height - backBtnSize - (int)(40 * scale);
        
        JButton backButton = addButton(mainPanel, "/images/BackButton.png", 
            backBtnSize, backBtnSize, backBtnX, backBtnY);
        backButton.addActionListener(e -> {
            System.out.println("[ModeSelection] Back button clicked");
            ScreenNavigator.getInstance().goBack(this);
        });
        backButton.setFocusable(false);
        
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
        
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
            case java.awt.event.KeyEvent.VK_ESCAPE -> 
                ScreenNavigator.getInstance().goBack(this);
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

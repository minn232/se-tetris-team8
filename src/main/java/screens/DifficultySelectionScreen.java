package screens;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Board;
import core.Difficulty;
import core.Settings;

/**
 * 난이도 선택 화면
 */
public class DifficultySelectionScreen extends JFrame {
    
    private final boolean isItemMode;
    private JButton[] buttons;
    private int selectedIndex = 0;

    public DifficultySelectionScreen(boolean isItemMode) {
        this.isItemMode = isItemMode;
        
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();
        
        setTitle("Select Difficulty");
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
        
        JButton easyButton = addButton(mainPanel, "/images/EasyModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 30 * scale), (int)(height / 2.0 - 20 * scale));
        easyButton.addActionListener(e -> {
            System.out.println("[DifficultySelection] Easy Mode selected, ItemMode: " + isItemMode);
            startGame(Difficulty.EASY);
        });
        
        JButton normalButton = addButton(mainPanel, "/images/NormalModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 130 * scale), (int)(height / 2.0 - 20 * scale));
        normalButton.addActionListener(e -> {
            System.out.println("[DifficultySelection] Normal Mode selected, ItemMode: " + isItemMode);
            startGame(Difficulty.NORMAL);
        });
        
        JButton hardButton = addButton(mainPanel, "/images/HardModeButton.png", 
            btnWidth, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 230 * scale), (int)(height / 2.0 - 20 * scale));
        hardButton.addActionListener(e -> {
            System.out.println("[DifficultySelection] Hard Mode selected, ItemMode: " + isItemMode);
            startGame(Difficulty.HARD);
        });
        
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
        
        // 버튼 배열 초기화 (키보드 네비게이션용)
        buttons = new JButton[]{easyButton, normalButton, hardButton};
        
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
    
    private void startGame(Difficulty difficulty) {
        dispose();
        
        Board board = new Board(difficulty, isItemMode);
        GamePanel gamePanel = new GamePanel(board, isItemMode);
        
        JFrame gameFrame = new JFrame("Tetris - " + difficulty.name());
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.add(gamePanel);
        gameFrame.pack();
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        
        // 게임 창이 닫힐 때 InGameBGM 정지
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                BackgroundMusicPlayer.getInstance().stop();
            }
        });
        
        gameFrame.setVisible(true);
    }
    
    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }
}

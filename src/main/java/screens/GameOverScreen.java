package screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Board;
import core.Difficulty;
import core.Settings;

/**
 * 게임 오버 화면
 * 최종 점수를 표시하고 재시작, 메인메뉴, 종료 버튼 제공
 */
public class GameOverScreen extends JFrame {

    private final int finalScore;
    private final Difficulty difficulty;
    private final boolean isItemMode;
    private final boolean isTimeAttackMode;
    private JButton[] buttons;
    private int selectedIndex = 0;

    public GameOverScreen(int finalScore, Difficulty difficulty, boolean isItemMode) {
        this(finalScore, difficulty, isItemMode, false);
    }

    public GameOverScreen(int finalScore, Difficulty difficulty, boolean isItemMode, boolean isTimeAttackMode) {
        this.finalScore = finalScore;
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        this.isTimeAttackMode = isTimeAttackMode;
        initializeUI();
    }
    
    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 0.5);
        int height = (int)(Settings.getWindowHeight() * 0.7);
        int baseFontSize = Settings.getBaseFontSize();
        
        setTitle("Game Over");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel scoreLabel = new JLabel("Score: " + finalScore);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.33)));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton restartButton = createButton("Restart");
        JButton mainMenuButton = createButton("Main Menu");
        JButton exitButton = createButton("Exit");

        restartButton.addActionListener(e -> {
            dispose();
            restartGame();
        });
        
        mainMenuButton.addActionListener(e -> {
            dispose();
            ScreenNavigator.getInstance().clear();
            new Mainmenu().setVisible(true);
        });
        
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(scoreLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(restartButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(mainMenuButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(exitButton);
        
        add(mainPanel);
        
        // 키보드 포커스를 받을 수 있도록 설정
        setFocusable(true);
        
        // 버튼 배열 초기화
        buttons = new JButton[]{restartButton, mainMenuButton, exitButton};
        
        // 키보드 이벤트 리스너 추가
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        
        // 버튼들이 포커스를 받지 않도록 설정
        for (JButton button : buttons) {
            button.setFocusable(false);
        }
        
        // 초기 하이라이트 설정
        updateButtonHighlight();
    }
    
    private void restartGame() {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board(difficulty, isItemMode);
            GamePanel panel = new GamePanel(board, isItemMode, isTimeAttackMode);

            JFrame gameFrame = new JFrame("SE Tetris Team8");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setLayout(new BorderLayout());
            gameFrame.add(panel, BorderLayout.CENTER);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);
            SwingUtilities.invokeLater(panel::requestFocusInWindow);
        });
    }
    
    private JButton createButton(String text) {
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension((int)(200 * scaleFactor), (int)(40 * scaleFactor)));
        button.setFont(new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
        return button;
    }
    
    // 키보드 입력 처리
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + buttons.length) % buttons.length;
                updateButtonHighlight();
                break;
            case KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % buttons.length;
                updateButtonHighlight();
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                buttons[selectedIndex].doClick();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }
    
    // 버튼 하이라이트 업데이트
    private void updateButtonHighlight() {
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                buttons[i].setBackground(new Color(100, 150, 255));
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(true);
            } else {
                buttons[i].setBackground(null);
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(false);
            }
        }
        repaint();
    }
}
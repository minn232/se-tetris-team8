package com.team.tetris.screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.team.tetris.ranking.RankingBoard;
import com.team.tetris.core.Board;
import com.team.tetris.core.Difficulty;
import com.team.tetris.core.Settings;

/**
 * 게임 오버 화면
 * 최종 점수를 표시하고 재시작, 메인메뉴, 랭킹보드 버튼 제공
 */
public class GameOverScreen extends JFrame {

    private final int finalScore;
    private final Difficulty difficulty;
    private final boolean isItemMode;

    public GameOverScreen(int finalScore, Difficulty difficulty, boolean isItemMode) {
        this.finalScore = finalScore;
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        initializeUI();
    }
    
    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 1.1);
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
        JButton leaderboardButton = createButton("Leaderboard");

        restartButton.addActionListener(e -> {
            dispose();
            restartGame();
        });
        
        mainMenuButton.addActionListener(e -> {
            dispose();
            new Mainmenu().setVisible(true);
        });
        
        leaderboardButton.addActionListener(e -> {
            new RankingBoard().setVisible(true);
        });
        
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(scoreLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(restartButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(mainMenuButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(leaderboardButton);
        
        add(mainPanel);
    }
    
    private void restartGame() {
        SwingUtilities.invokeLater(() -> {
            Board board = new Board(difficulty, isItemMode);
            GamePanel panel = new GamePanel(board, isItemMode);

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
}
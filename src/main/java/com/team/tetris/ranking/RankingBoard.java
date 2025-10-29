package com.team.tetris.ranking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.team.tetris.core.Settings;

/**
 * 랭킹 보드 UI
 * 일반 모드와 아이템 모드 랭킹을 탭으로 분리하여 표시
 */
public class RankingBoard extends JFrame {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String NORMAL_RANKING_FILE = "normal_rankings.dat";
    private static final String ITEM_RANKING_FILE = "item_rankings.dat";
    
    private final String highlightPlayerName;
    private final int highlightScore;
    private final boolean showReturnButton;
    private final boolean isItemMode;
    
    public RankingBoard() {
        this(null, -1, false, false);
    }
    
    public RankingBoard(String highlightPlayerName, int highlightScore, boolean showReturnButton) {
        this(highlightPlayerName, highlightScore, showReturnButton, false);
    }
    
    public RankingBoard(String highlightPlayerName, int highlightScore, boolean showReturnButton, boolean isItemMode) {
        this.highlightPlayerName = highlightPlayerName;
        this.highlightScore = highlightScore;
        this.showReturnButton = showReturnButton;
        this.isItemMode = isItemMode;
        initializeUI();
    }

    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 1.39);
        int height = (int)(Settings.getWindowHeight() * 1.33);
        
        setTitle("Ranking Board");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel normalModePanel = createRankingPanel(
            RankingManager.getInstance(NORMAL_RANKING_FILE).getRankings(),
            "Normal Mode",
            false // 일반 모드
        );
        
        JPanel itemModePanel = createRankingPanel(
            RankingManager.getInstance(ITEM_RANKING_FILE).getRankings(),
            "Item Mode",
            true // 아이템 모드
        );

        tabbedPane.addTab("Normal Mode", normalModePanel);
        tabbedPane.addTab("Item Mode", itemModePanel);
        
        // 아이템 모드인 경우 아이템 모드 탭을 기본으로 선택
        if (isItemMode) {
            tabbedPane.setSelectedIndex(1);
        }

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        
        // 게임 종료 후 표시되는 경우 버튼 추가
        if (showReturnButton) {
            JPanel buttonPanel = createButtonPanel();
            mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        }

        add(mainContainer);
    }

    private JPanel createRankingPanel(List<RankingEntry> rankings, String title, boolean isItemMode) {
        int baseFontSize = Settings.getBaseFontSize();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = createTitlePanel(title, baseFontSize);
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        addRankingEntries(contentPanel, rankings, baseFontSize, isItemMode);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        mainMenuButton.setPreferredSize(new java.awt.Dimension((int)(150 * scaleFactor), (int)(40 * scaleFactor)));
        mainMenuButton.addActionListener(e -> {
            dispose();
            new com.team.tetris.screens.Mainmenu().setVisible(true);
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        exitButton.setPreferredSize(new java.awt.Dimension((int)(150 * scaleFactor), (int)(40 * scaleFactor)));
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }
    
    private JPanel createTitlePanel(String title, int baseFontSize) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.33)));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        
        return titlePanel;
    }
    
    private void addRankingEntries(JPanel panel, List<RankingEntry> rankings, int baseFontSize, boolean isItemMode) {
        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);
            
            // 방금 입력한 항목인지 확인 (이름과 점수가 모두 일치)
            boolean isHighlighted = highlightPlayerName != null 
                && entry.getPlayerName().equals(highlightPlayerName)
                && entry.getScore() == highlightScore;
            
            // 난이도 정보 가져오기 (null이면 기본값 표시)
            String difficultyStr = entry.getDifficulty() != null 
                ? entry.getDifficulty().name() 
                : "NORMAL";
            
            JLabel rankLabel = new JLabel(String.format("%d. %s - %d [%s] (%s)",
                i + 1,
                entry.getPlayerName(),
                entry.getScore(),
                difficultyStr,
                entry.getTimestamp().format(DATE_FORMATTER)
            ));
            
            // 강조 표시
            if (isHighlighted) {
                rankLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.0)));
                rankLabel.setForeground(new Color(255, 215, 0)); // 골드 색상
                rankLabel.setOpaque(true);
                rankLabel.setBackground(new Color(50, 50, 50)); // 어두운 배경
                rankLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            } else {
                rankLabel.setFont(new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
            }
            
            rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(rankLabel);
            panel.add(Box.createVerticalStrut(10));
        }
    }
}
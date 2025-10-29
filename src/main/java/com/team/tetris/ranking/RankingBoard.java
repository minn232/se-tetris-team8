package com.team.tetris.ranking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
    
    
    public RankingBoard() {
        initializeUI();
    }

    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 1.39);
        int height = (int)(Settings.getWindowHeight() * 1.33);
        
        setTitle("Ranking Board");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        JPanel normalModePanel = createRankingPanel(
            RankingManager.getInstance(NORMAL_RANKING_FILE).getRankings(),
            "Normal Mode"
        );
        
        JPanel itemModePanel = createRankingPanel(
            RankingManager.getInstance(ITEM_RANKING_FILE).getRankings(),
            "Item Mode"
        );

        tabbedPane.addTab("Normal Mode", normalModePanel);
        tabbedPane.addTab("Item Mode", itemModePanel);

        add(tabbedPane);
    }

    private JPanel createRankingPanel(List<RankingEntry> rankings, String title) {
        int baseFontSize = Settings.getBaseFontSize();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = createTitlePanel(title, baseFontSize);
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        addRankingEntries(contentPanel, rankings, baseFontSize);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
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
    
    private void addRankingEntries(JPanel panel, List<RankingEntry> rankings, int baseFontSize) {
        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);
            JLabel rankLabel = new JLabel(String.format("%d. %s - %d (%s)",
                i + 1,
                entry.getPlayerName(),
                entry.getScore(),
                entry.getTimestamp().format(DATE_FORMATTER)
            ));
            rankLabel.setFont(new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
            rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(rankLabel);
            panel.add(Box.createVerticalStrut(10));
        }
    }
}
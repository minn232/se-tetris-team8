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

public class RankingBoard extends JFrame {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private JTabbedPane tabbedPane;

    public RankingBoard() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("랭킹 보드");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        
        // 일반 모드 랭킹 패널
        JPanel normalModePanel = createRankingPanel(
            RankingManager.getInstance().getRankings(),
            "일반 모드 랭킹"
        );
        
        // 아이템 모드 랭킹 패널
        JPanel itemModePanel = createRankingPanel(
            RankingManager.getInstance("item_rankings.dat").getRankings(),
            "아이템 모드 랭킹"
        );

        tabbedPane.addTab("일반 모드", normalModePanel);
        tabbedPane.addTab("아이템 모드", itemModePanel);

        add(tabbedPane);
    }

    private JPanel createRankingPanel(List<RankingEntry> rankings, String title) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 제목 패널 추가 (가운데 정렬을 위한 별도 패널)
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        
        // 제목 레이블
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 제목 패널에 여백을 추가하여 가운데 정렬
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 랭킹 목록 추가
        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);
            JLabel rankLabel = new JLabel(String.format("%d. %s - %d점 (%s)",
                i + 1,
                entry.getPlayerName(),
                entry.getScore(),
                entry.getTimestamp().format(formatter)
            ));
            rankLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(rankLabel);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        // 스크롤 패널에 contentPanel을 추가하고, 메인 패널의 중앙에 배치
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
}
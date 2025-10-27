/*
    랭킹보드 창을 구현한 클래스.
    RankingEntry 클래스를 가져와 랭킹 list를 받는다.
    그 후 랭킹 list에서 이름/점수/시간을 차례로 받아온 후,
    시간은 포맷을 지정하여 일정한 형식으로 랭킹보드에 출력한다.
    추가로 패널을 넘어가게 기록될 경우 스크롤을 이용하도록 하였다.
 */





package com.team.tetris.ranking;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//랭킹보드 창을 구현한 클래스
public class RankingBoard extends JFrame {
    //랭킹보드에 입력될 시간의 포맷
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //랭킹보드 생성자
    public RankingBoard() {
        //UI 초기화 메소드
        initializeUI();
    }

    //랭킹보드 UI
    private void initializeUI() {
        //랭킹보드 창 설정
        setTitle("랭킹 보드");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //메인 패널과 랭킹 패널 설정.
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BoxLayout(rankingPanel, BoxLayout.Y_AXIS));

        //랭킹 목록을 가져옴
        List<RankingEntry> rankings = RankingManager.getInstance().getRankings();
        //가져온 랭킹목록을 랭킹 패널에 추가
        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);
            JLabel rankLabel = new JLabel(String.format("%d. %s - %d점 (%s)",
                i + 1,
                entry.getPlayerName(),
                entry.getScore(),
                entry.getTimestamp().format(formatter)
            ));
            rankingPanel.add(rankLabel);
            rankingPanel.add(Box.createVerticalStrut(5));
        }

        //랭킹 패널을 스크롤로 읽을 수 있도록.
        JScrollPane scrollPane = new JScrollPane(rankingPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }
}
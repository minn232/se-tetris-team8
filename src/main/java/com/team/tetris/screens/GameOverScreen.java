/*
    게임이 끝날 때 나오는 게임오버 화면을 구현한 클래스.
    게임 클래스에서 finalScore인 최종점수 값을 인자로 받아와야 한다.
    score, 다시하기, 메인메뉴, 랭킹보드 버튼이 포함되어 있다.
    게임 클래스에선 아래 코드를 적절한 위치에 추가해야 한다.

 */


 


package com.team.tetris.screens;

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

import com.team.tetris.ranking.RankingBoard;
import com.team.tetris.core.Settings;

//게임 오버 시 나오는 화면. 새로 창을 뜨게 함
public class GameOverScreen extends JFrame {

    //게임 클래스에서 받아온 최종점수를 이 클래스에서 사용하기 위한 변수를 저장.
    private final int finalScore;

    //이 클래스의 생성자. 게임클래스에서 객체 생성 시 최종점수 값을 인자로 받아야한다.
    public GameOverScreen(int finalScore) {
        this.finalScore = finalScore;
        initializeUI(); //UI 초기화 메서드 호출
    }
    
    //게임오버 UI
    private void initializeUI() {
        // Settings에서 해상도 정보 가져오기
        int width = (int)(Settings.getWindowWidth() * 1.1);
        int height = (int)(Settings.getWindowHeight() * 0.7);
        int baseFontSize = Settings.getBaseFontSize();
        
        setTitle("Game Over");          //창 제목
        setSize(width, height);       //창 크기
        setLocationRelativeTo(null);        //창 화면 중앙으로
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  //창 닫을 때 프로그램이 종료되지 않게.
        
        //메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));    //컴포넌트 수직으로 배치
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));   //패널 내부 여백
        
        // 점수 표시 레이블
        JLabel scoreLabel = new JLabel("Score: " + finalScore); //점수 표기 방식
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.33))); //글꼴 (24 기준)
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //가운데정렬
        
        // 버튼들
        JButton restartButton = createButton("Restart");
        JButton mainMenuButton = createButton("Main Menu");
        JButton leaderboardButton = createButton("Leaderboard");
 


         // 버튼 동작 추가
        restartButton.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new Mainmenu().setVisible(true);
        });
        
        mainMenuButton.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new Mainmenu().setVisible(true);
        });
        
        leaderboardButton.addActionListener(e -> {
            // 랭킹보드는 현재 창을 닫지 않고 새 창으로 열기
            new RankingBoard().setVisible(true);
        });
        
        // 컴포넌트 추가
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
    
    //버튼 생성 메서드
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
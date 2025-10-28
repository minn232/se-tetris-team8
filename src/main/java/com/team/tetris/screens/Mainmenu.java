package com.team.tetris.screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.team.tetris.core.Board;
import com.team.tetris.core.Difficulty;
import com.team.tetris.ranking.RankingBoard;  // RankingBoard import 추가

public class Mainmenu extends JFrame {
    // 랭킹 보드 버튼 추가
    private final JButton startButton, itemModeButton, rankingButton, settingsButton, helpButton, exitButton;
    private boolean isItemMode;

    public Mainmenu() {
        // 기본 화면(WINDOW) 설정
        setTitle("Tetris");                       // 창 제목
        setSize(400, 500);                   // 창 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // 창 완전히 닫기
        setLocationRelativeTo(null);                    // 창 화면 중앙에 오도록
        
        // 메인 패널 설정
        JPanel mainPanel = new JPanel(); //메인 패널 오브젝트 생성
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));    // 컴포넌트(버튼)들의 배치를 수직으로                         
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));   //패널 내부 여백 설정
        
        // 제목 라벨
        JLabel titleLabel = new JLabel("TETRIS");   // 메인 패널 제목 label 오브젝트 생성
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));   //제목의 글꼴
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);   //text 가운데 정렬
        
        // 버튼 생성 함수로 버튼 초기화
        startButton = createButton("게임 시작");
        itemModeButton = createButton("아이템 모드로 시작");
        rankingButton = createButton("랭킹 보드");  // 랭킹 보드 버튼 추가
        settingsButton = createButton("설정");
        helpButton = createButton("게임 방법");
        exitButton = createButton("게임 종료");
        
        // 각 버튼 이벤트 처리
        startButton.addActionListener(e -> startGame());
        itemModeButton.addActionListener(e -> startItemMode());
        rankingButton.addActionListener(e -> showRankingBoard());  // 랭킹 보드 이벤트 추가
        settingsButton.addActionListener(e -> openSettings());
        helpButton.addActionListener(e -> showHelp());
        exitButton.addActionListener(e -> System.exit(0));
        
        // 컴포넌트를 간격설정해서 추가
        mainPanel.add(Box.createVerticalGlue()); //윗공간 확보
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(itemModeButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rankingButton);  // 랭킹 보드 버튼 배치
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(settingsButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(helpButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);
        mainPanel.add(Box.createVerticalGlue()); //아랫공간 확보
        
        add(mainPanel);
    }
    
    // 랭킹 보드 표시 메서드 추가
    private void showRankingBoard() {
        new RankingBoard().setVisible(true);
    }
    
    // 버튼 생성 method
    private JButton createButton(String text) {
        JButton button = new JButton(text); //오브젝트 생성
        button.setAlignmentX(Component.CENTER_ALIGNMENT);   //버튼 가운데 정렬
        button.setMaximumSize(new Dimension(200, 40)); //버튼 크기 설정
        button.setFont(new Font("Arial", Font.PLAIN, 16)); //버튼 글꼴
        return button;
    }
    
    private void startGame() {
        isItemMode = false;
        showDifficultyDialog();
    }

    private void startItemMode() {
        isItemMode = true;
        showDifficultyDialog();
    }
    
    private void openSettings() {
        // TODO: 설정 창 구현
        System.out.println("설정 열기");
    }

    private void showDifficultyDialog() {
        Object[] options = {"HARD", "NORMAL", "EASY"};
        int sel = JOptionPane.showOptionDialog(
            this,
            "난이도를 선택하세요",
            "난이도 선택",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[1]
        );

        Difficulty difficulty;
        switch (sel) {
            case 0 -> difficulty = Difficulty.HARD;
            case 1 -> difficulty = Difficulty.NORMAL;
            case 2 -> difficulty = Difficulty.EASY;
            default -> {
                return;  // 취소하거나 창을 닫은 경우
            }
        }

        startTetrisGame(difficulty);
    }

    private void startTetrisGame(Difficulty difficulty) {
        dispose();  // 메인 메뉴 창 닫기

        SwingUtilities.invokeLater(() -> {
            
            Board board = new Board(difficulty, isItemMode);
            GamePanel panel = new GamePanel(board, isItemMode);
            
            if (isItemMode) {
                // TODO: 아이템 모드에 필요한 추가 설정
                // board.enableItemMode(); 같은 메서드 호출
            }

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


    // 게임 방법 함수
    private void showHelp() {
        // 팝업 창으로 구현
        JDialog helpDialog = new JDialog(this, "게임 방법", true);  // window의 자식으로서 팝업 창 생성
        helpDialog.setSize(300, 450);   //팝업 창 크기
        helpDialog.setLocationRelativeTo(this); //window 기준 창의 중앙에 오도록
        
        JPanel helpPanel = new JPanel();    //게임 방법 패널 오브젝트 생성
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS)); // 컴포넌트 수직배치
        helpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //패널 내부 여백
        
        JLabel titleLabel = new JLabel("테트리스 게임 방법");   //패널 제목 label 오브젝트 생성
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); //제목 글꼴 설정
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //제목 가운데 정렬
        
        // 게임 방법 텍스트 오브젝트 생성
        JTextArea helpText = new JTextArea("""
            - → : 블록 오른쪽으로 이동
            - ← : 블록 왼쪽으로 이동
            - ↑ : 블록 시계방향으로 회전
            - ↓ : 블록 빠르게 내리기
            - SPACE : 블록 즉시 내리기
            - P : 게임 일시정지

            SCORE :
            한 줄을 가득 채우면
            해당 줄이 사라지고
            점수를 획득합니다.

            GAME OVER :
            블록이 쌓여 화면 상단에
            닿으면 게임이 종료됩니다.
            """);


            //TODO: 아이템에 관한 설명 추가

        helpText.setEditable(false);    // 텍스트 수정 불가
        helpText.setBackground(null);   //배경 설정 x
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));    //텍스트 글꼴
        
        //닫기 버튼 
        JButton closeButton = new JButton("닫기");  //닫기 버튼 오브젝트 생성
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);  //버튼 가운데 정렬
        closeButton.addActionListener(e -> helpDialog.dispose()); //버튼 클릭시 팝업 창 닫는 이벤트
        
        //컴포넌트를 간격설정해서 추가
        helpPanel.add(titleLabel);
        helpPanel.add(Box.createVerticalStrut(20));
        helpPanel.add(helpText);
        helpPanel.add(Box.createVerticalStrut(20));
        helpPanel.add(closeButton);
        
        helpDialog.add(helpPanel);
        helpDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        //EDT에서 UI 생성
        SwingUtilities.invokeLater(() -> {
            Mainmenu menu = new Mainmenu();
            menu.setVisible(true);
        });
    }
}
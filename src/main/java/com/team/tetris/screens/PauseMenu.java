/*
    일시정지 메뉴 구현
    ESC 키 또는 일시정지 버튼 클릭시 팝업창으로 표시
    [게임 재개], [메인 메뉴], [랭킹 보드] 버튼
    game class에 일시정지 상태 관리 코드를 붙여넣어야 하기 때문에
    문제발생시 쌍방으로 수정해야할 수 있음.


---------------------------------------------------------

    // 게임 화면 클래스에 추가할 코드
        public class TetrisGame extends JFrame {
            private boolean isPaused = false;
            private JButton pauseButton;
            
            // ...existing code...

            private void initializeUI() {
                // ...existing code...

                // 일시정지 버튼 추가
                pauseButton = new JButton("||");
                pauseButton.setPreferredSize(new Dimension(40, 40));
                pauseButton.addActionListener(e -> showPauseMenu());

                // ESC 키 바인딩
                getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                            .put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
                getRootPane().getActionMap().put("pause", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showPauseMenu();
                    }
                });
            }

            private void showPauseMenu() {
                isPaused = true;
                PauseMenu pauseMenu = new PauseMenu(this);
                pauseMenu.setVisible(true);

                if (pauseMenu.isGameResumed()) {
                    isPaused = false;
                    // 게임 재개 로직
                } else if (pauseMenu.isMainMenuSelected()) {
                    // 메인 메뉴로 이동하는 로직
                    dispose();
                    // MainMenu.show(); // 메인 메뉴 표시
                }
            }

            // 게임 업데이트 메서드에서 사용
            private void updateGame() {
                if (isPaused) {
                    return; // 일시정지 상태에서는 게임 업데이트 중지
                }
                
                // ...여기에 게임 로직...

            }
        }

---------------------------------------------------------------
*/





package com.team.tetris.screens;

import javax.swing.*;
import java.awt.*;
import com.team.tetris.ranking.RankingBoard;

//일시정지 메뉴를 구현. dialog로 구현.
public class PauseMenu extends JDialog {
    private boolean isGameResumed = false;      //게임 재개 버튼 클릭 여부
    private boolean isMainMenuSelected = false; //메인 메뉴 버튼 클릭 ㅇ부

    //생성자. JDialog의 프레임을 받아옴. 팝업창 켜있는 동안 게임 창 클릭못함
    public PauseMenu(JFrame parent) {
        super(parent, "일시 정지", true);
        initializeUI();
    }

    //일시정지 UI
    private void initializeUI() {
        //창 설정
        setSize(300, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);

        //메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 버튼 생성
        JButton resumeButton = createButton("게임 재개");
        JButton mainMenuButton = createButton("메인 메뉴");
        JButton rankingButton = createButton("랭킹 보드");

        // 버튼 이벤트 설정
        resumeButton.addActionListener(e -> {
            isGameResumed = true;
            dispose();
        });

        mainMenuButton.addActionListener(e -> {
            isMainMenuSelected = true;
            dispose();
        });

        rankingButton.addActionListener(e -> {
            RankingBoard rankingBoard = new RankingBoard();
            rankingBoard.setVisible(true);
            // 랭킹보드가 닫히면 일시정지 메뉴는 계속 표시됨
        });

        // 컴포넌트 추가
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(resumeButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(mainMenuButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(rankingButton);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    //버튼 생성 메소드
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }

    //외부에서 게임 재개 버튼 클릭 여부
    public boolean isGameResumed() {
        return isGameResumed;
    }

    //외부에서 메인 메뉴 버튼 클릭 여부
    public boolean isMainMenuSelected() {
        return isMainMenuSelected;
    }
}
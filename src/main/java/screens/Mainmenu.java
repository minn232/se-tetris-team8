package screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import core.Board;
import core.Difficulty;
import core.Settings;
import ranking.RankingBoard;

public class Mainmenu extends JFrame {
    // 랜킹 보드 버튼 추가
    private final JButton startButton, itemModeButton, battleModeButton, rankingButton, settingsButton, helpButton, exitButton;
    private final JButton[] buttons;  
    private int selectedIndex = 0;   
    private boolean isItemMode;

    public Mainmenu() {
        // Settings에서 해상도 가져오기
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        
        // UIManager를 통해 다이얼로그 폰트 크기 조정
        javax.swing.UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, baseFontSize));
        javax.swing.UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
        
        // 기본 화면(WINDOW) 설정
        setTitle("Tetris");                       // 창 제목
        setSize(width, height);                   // 창 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // 창 완전히 닫기
        setLocationRelativeTo(null);                    // 창 화면 중앙에 오도록
        setFocusable(true);
        
        // 메인 패널 설정
        JPanel mainPanel = new JPanel(); //메인 패널 오브젝트 생성
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));    // 컴포넌트(버튼)들의 배치를 수직으로                         
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));   //패널 내부 여백 설정
        
        // 제목 라벨
        JLabel titleLabel = new JLabel("TETRIS");   // 메인 패널 제목 label 오브젝트 생성
        titleLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 2.67)));   //제목의 글꼴 (48 기준)
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);   //text 가운데 정렬
        
        // 버튼 생성 함수로 버튼 초기화
        startButton = createButton("Start Game");
        itemModeButton = createButton("Start Item Mode");
        battleModeButton = createButton("Battle Mode");
        rankingButton = createButton("Ranking Board");
        settingsButton = createButton("Settings");
        helpButton = createButton("How to Play");
        exitButton = createButton("Exit");
        
        // 각 버튼 이벤트 처리
        startButton.addActionListener(e -> startGame());
        itemModeButton.addActionListener(e -> startItemMode());
        battleModeButton.addActionListener(e -> startBattleMode());
        rankingButton.addActionListener(e -> showRankingBoard());  // 랜킹 보드 이벤트 추가
        settingsButton.addActionListener(e -> openSettings());
        helpButton.addActionListener(e -> showHelp());
        exitButton.addActionListener(e -> System.exit(0));

        // 버튼 배열 초기화 (추가)
        buttons = new JButton[]{startButton, itemModeButton, battleModeButton, rankingButton, settingsButton, helpButton, exitButton};
        
        // 키보드 이벤트 리스너 추가 (추가)
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        
        // 컴포넌트를 간격설정해서 추가
        mainPanel.add(Box.createVerticalGlue()); //윗공간 확보
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(itemModeButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(battleModeButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(rankingButton);  // 랜킹 보드 버튼 배치
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(settingsButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(helpButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);
        mainPanel.add(Box.createVerticalGlue()); //아랫공간 확보
        
        add(mainPanel);
        updateButtonHighlight();
    }
    
    // 랭킹 보드 표시 메서드 추가
    private void showRankingBoard() {
        new RankingBoard().setVisible(true);
    }
    
    // 버튼 생성 method
    private JButton createButton(String text) {
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        JButton button = new JButton(text); //오브젝트 생성
        button.setAlignmentX(Component.CENTER_ALIGNMENT);   //버튼 가운데 정렬
        button.setMaximumSize(new Dimension((int)(200 * scaleFactor), (int)(40 * scaleFactor))); //버튼 크기 설정
        button.setFont(new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89))); //버튼 글꼴 (16 기준)
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
    
    private void startBattleMode() {
        // 대전 모드는 난이도 선택 없이 바로 시작
        showBattleDifficultyDialog();
    }
    
    private void openSettings() {
        SettingsScreen settingsScreen = new SettingsScreen();
        settingsScreen.setVisible(true);
        
        // Settings 창이 닫힌 후, 해상도 변경이 있을 수 있으므로 메인메뉴 새로고침
        settingsScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // 설정 다시 로드
                Settings.load();
                // 메인메뉴 재생성
                refreshMainMenu();
            }
        });
    }
    
    private void refreshMainMenu() {
        // 현재 창 닫고 새로운 Mainmenu 열기
        dispose();
        SwingUtilities.invokeLater(() -> {
            Mainmenu newMenu = new Mainmenu();
            newMenu.setVisible(true);
        });
    }

    private void showDifficultyDialog() {
        // 커스텀 다이얼로그 생성
        JDialog difficultyDialog = new JDialog(this, "Difficulty", true);
        difficultyDialog.setSize(400, 200);
        difficultyDialog.setLocationRelativeTo(this);
        difficultyDialog.setFocusable(true);
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Choose a difficulty");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, Settings.getBaseFontSize()));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 난이도 버튼들 생성
        JButton hardButton = new JButton("HARD");
        JButton normalButton = new JButton("NORMAL");
        JButton easyButton = new JButton("EASY");
        
        // 버튼 스타일링
        JButton[] diffButtons = {hardButton, normalButton, easyButton};
        for (JButton btn : diffButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(150, 35));
            btn.setFont(new Font("Arial", Font.PLAIN, Settings.getBaseFontSize()));
            btn.setFocusable(false);
        }
        
        // 선택된 난이도를 저장할 변수
        final int[] selectedDifficulty = {1}; // 0=HARD, 1=NORMAL, 2=EASY (기본값: NORMAL)
        final boolean[] dialogClosed = {false};
        
        // 버튼 하이라이트 업데이트 함수
        Runnable updateDifficultyHighlight = () -> {
            for (int i = 0; i < diffButtons.length; i++) {
                if (i == selectedDifficulty[0]) {
                    diffButtons[i].setBackground(new java.awt.Color(100, 150, 255));
                    diffButtons[i].setForeground(java.awt.Color.BLACK);
                    diffButtons[i].setOpaque(true);
                } else {
                    diffButtons[i].setBackground(null);
                    diffButtons[i].setForeground(java.awt.Color.BLACK);
                    diffButtons[i].setOpaque(false);
                }
            }
            difficultyDialog.repaint();
        };
        
        // 버튼 클릭 이벤트
        hardButton.addActionListener(e -> {
            selectedDifficulty[0] = 0;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        normalButton.addActionListener(e -> {
            selectedDifficulty[0] = 1;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        easyButton.addActionListener(e -> {
            selectedDifficulty[0] = 2;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        
        // 키보드 이벤트 리스너
        difficultyDialog.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        selectedDifficulty[0] = (selectedDifficulty[0] - 1 + diffButtons.length) % diffButtons.length;
                        updateDifficultyHighlight.run();
                        break;
                    case KeyEvent.VK_RIGHT:
                        selectedDifficulty[0] = (selectedDifficulty[0] + 1) % diffButtons.length;
                        updateDifficultyHighlight.run();
                        break;
                    case KeyEvent.VK_ENTER:
                        diffButtons[selectedDifficulty[0]].doClick();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dialogClosed[0] = false;
                        difficultyDialog.dispose();
                        break;
                }
            }
        });
        
        // 컴포넌트 배치
        dialogPanel.add(Box.createVerticalGlue());
        dialogPanel.add(messageLabel);
        dialogPanel.add(Box.createVerticalStrut(30));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(hardButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(normalButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(easyButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        dialogPanel.add(buttonPanel);
        dialogPanel.add(Box.createVerticalGlue());
        
        difficultyDialog.add(dialogPanel);
        
        // 초기 하이라이트 설정
        updateDifficultyHighlight.run();
        
        // 다이얼로그 표시
        difficultyDialog.setVisible(true);
        
        // 다이얼로그가 닫힌 후 처리
        if (dialogClosed[0]) {
            Difficulty difficulty;
            switch (selectedDifficulty[0]) {
                case 0 -> difficulty = Difficulty.HARD;
                case 1 -> difficulty = Difficulty.NORMAL;
                case 2 -> difficulty = Difficulty.EASY;
                default -> {
                    return;
                }
            }
            startTetrisGame(difficulty);
        }
    }
    
    private void showBattleDifficultyDialog() {
        // 커스텀 다이얼로그 생성
        JDialog difficultyDialog = new JDialog(this, "Battle Mode - Difficulty", true);
        difficultyDialog.setSize(400, 200);
        difficultyDialog.setLocationRelativeTo(this);
        difficultyDialog.setFocusable(true);
        
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Choose a difficulty");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, Settings.getBaseFontSize()));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // 난이도 버튼들 생성
        JButton hardButton = new JButton("HARD");
        JButton normalButton = new JButton("NORMAL");
        JButton easyButton = new JButton("EASY");
        
        // 버튼 스타일링
        JButton[] diffButtons = {hardButton, normalButton, easyButton};
        for (JButton btn : diffButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(150, 35));
            btn.setFont(new Font("Arial", Font.PLAIN, Settings.getBaseFontSize()));
            btn.setFocusable(false);
        }
        
        // 선택된 난이도를 저장할 변수
        final int[] selectedDifficulty = {1}; // 0=HARD, 1=NORMAL, 2=EASY (기본값: NORMAL)
        final boolean[] dialogClosed = {false};
        
        // 버튼 하이라이트 업데이트 함수
        Runnable updateDifficultyHighlight = () -> {
            for (int i = 0; i < diffButtons.length; i++) {
                if (i == selectedDifficulty[0]) {
                    diffButtons[i].setBackground(new java.awt.Color(100, 150, 255));
                    diffButtons[i].setForeground(java.awt.Color.BLACK);
                    diffButtons[i].setOpaque(true);
                } else {
                    diffButtons[i].setBackground(null);
                    diffButtons[i].setForeground(java.awt.Color.BLACK);
                    diffButtons[i].setOpaque(false);
                }
            }
            difficultyDialog.repaint();
        };
        
        // 버튼 클릭 이벤트
        hardButton.addActionListener(e -> {
            selectedDifficulty[0] = 0;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        normalButton.addActionListener(e -> {
            selectedDifficulty[0] = 1;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        easyButton.addActionListener(e -> {
            selectedDifficulty[0] = 2;
            dialogClosed[0] = true;
            difficultyDialog.dispose();
        });
        
        // 키보드 이벤트 리스너
        difficultyDialog.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        selectedDifficulty[0] = (selectedDifficulty[0] - 1 + diffButtons.length) % diffButtons.length;
                        updateDifficultyHighlight.run();
                        break;
                    case KeyEvent.VK_RIGHT:
                        selectedDifficulty[0] = (selectedDifficulty[0] + 1) % diffButtons.length;
                        updateDifficultyHighlight.run();
                        break;
                    case KeyEvent.VK_ENTER:
                        diffButtons[selectedDifficulty[0]].doClick();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        dialogClosed[0] = false;
                        difficultyDialog.dispose();
                        break;
                }
            }
        });
        
        // 컴포넌트 배치
        dialogPanel.add(messageLabel);
        dialogPanel.add(Box.createVerticalStrut(20));
        dialogPanel.add(hardButton);
        dialogPanel.add(Box.createVerticalStrut(10));
        dialogPanel.add(normalButton);
        dialogPanel.add(Box.createVerticalStrut(10));
        dialogPanel.add(easyButton);
        
        difficultyDialog.add(dialogPanel);
        updateDifficultyHighlight.run();
        
        // 다이얼로그 표시
        difficultyDialog.setVisible(true);
        
        // 다이얼로그가 닫힌 후 처리
        if (dialogClosed[0]) {
            Difficulty difficulty;
            switch (selectedDifficulty[0]) {
                case 0 -> difficulty = Difficulty.HARD;
                case 1 -> difficulty = Difficulty.NORMAL;
                case 2 -> difficulty = Difficulty.EASY;
                default -> {
                    return;
                }
            }
            startBattleGame(difficulty);
        }
    }
    
    private void startBattleGame(Difficulty difficulty) {
        dispose();  // 메인 메뉴 창 닫기
        
        SwingUtilities.invokeLater(() -> {
            BattleGamePanel battlePanel = new BattleGamePanel(difficulty);
            
            JFrame gameFrame = new JFrame("Tetris - Battle Mode");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.add(battlePanel);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);
            
            battlePanel.requestFocusInWindow();
        });
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

    // 키보드 입력 처리 (추가)
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
                buttons[selectedIndex].doClick();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    // 버튼 하이라이트 업데이트 (추가)
    private void updateButtonHighlight() {
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                // 선택된 버튼 하이라이트
                buttons[i].setBackground(new java.awt.Color(100, 150, 255));
                buttons[i].setForeground(java.awt.Color.BLACK);
                buttons[i].setOpaque(true);
            } else {
                // 선택되지 않은 버튼 기본 스타일
                buttons[i].setBackground(null);
                buttons[i].setForeground(java.awt.Color.BLACK);
                buttons[i].setOpaque(false);
            }
        }
        repaint();
    }

    // 게임 방법 함수
    private void showHelp() {
        // Settings에서 해상도 정보 가져오기
        int width = (int)(Settings.getWindowWidth() * 1.11);
        int height = (int)(Settings.getWindowHeight() * 1.11);
        int baseFontSize = Settings.getBaseFontSize();
        
        // 팝업 창으로 구현
        JDialog helpDialog = new JDialog(this, "How To Play", true);  // window의 자식으로서 팝업 창 생성
        helpDialog.setSize(width, height);   //팝업 창 크기
        helpDialog.setLocationRelativeTo(this); //window 기준 창의 중앙에 오도록
        
        JPanel helpPanel = new JPanel();    //게임 방법 패널 오브젝트 생성
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS)); // 컴포넌트 수직배치
        helpPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //패널 내부 여백
        
        JLabel titleLabel = new JLabel("How to Play Tetris");   //패널 제목 label 오브젝트 생성
        titleLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.11))); //제목 글꼴 설정 (20 기준)
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //제목 가운데 정렬
        
        // Settings에서 현재 키 바인딩 가져오기
        String leftKey = KeyEvent.getKeyText(Settings.getKeyLeft());
        String rightKey = KeyEvent.getKeyText(Settings.getKeyRight());
        String downKey = KeyEvent.getKeyText(Settings.getKeyDown());
        String rotateKey = KeyEvent.getKeyText(Settings.getKeyRotate());
        String hardDropKey = KeyEvent.getKeyText(Settings.getKeyHardDrop());
        
        // 게임 방법 텍스트 오브젝트 생성 (영어로 변경, Settings에서 키 가져옴)
        JTextArea helpText = new JTextArea(String.format("""
            CONTROLS:
            - %s : Move block right
            - %s : Move block left
            - %s : Rotate block clockwise
            - %s : Soft drop (move down faster)
            - %s : Hard drop (instant drop)
            - P : Pause/Resume game

            SCORING:
            Fill a complete horizontal line
            to clear it and earn points.
            Clear multiple lines at once
            for bonus points!

            GAME OVER:
            When blocks stack up to the top
            of the screen, the game ends.

            DIFFICULTY:
            - EASY: Slower speed, more I-blocks
            - NORMAL: Standard gameplay
            - HARD: Faster speed, fewer I-blocks
            
            ITEM MODE:
            Special white item blocks appear
            every 10 lines cleared. Each block
            has a unique power marked by a letter:
            
            - L (Line Block): Clears the entire
              horizontal line where 'L' is placed
            
            - S (Slow Block): Reduces game speed
              for a limited time
            
            - T (Transform Block): Converts the
              next 5 blocks into I-blocks
            
            - W (Weight Block): Falls through and
              erases blocks below it. Movement locks
              when touching other blocks.
            
            - B (Bomb Block): Explodes in a 3x3
              area when placed, clearing all blocks
              in that zone
            
            NOTE:
            You can customize key bindings
            in the Settings menu.
            """, rightKey, leftKey, rotateKey, downKey, hardDropKey));

        helpText.setEditable(false);    // 텍스트 수정 불가
        helpText.setBackground(null);   //배경 설정 x
        helpText.setFont(new Font("Arial", Font.PLAIN, baseFontSize));    //텍스트 글꼴 (기본 폰트 크기 사용)
        helpText.setLineWrap(true);     // 자동 줄바꿈
        helpText.setWrapStyleWord(true); // 단어 단위로 줄바꿈
        
        // 스크롤 패널에 텍스트 추가
        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        //닫기 버튼 
        JButton closeButton = new JButton("Close");  //닫기 버튼 오브젝트 생성
        closeButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);  //버튼 가운데 정렬
        closeButton.addActionListener(e -> helpDialog.dispose()); //버튼 클릭시 팝업 창 닫는 이벤트
        
        //컴포넌트를 간격설정해서 추가
        helpPanel.add(titleLabel);
        helpPanel.add(Box.createVerticalStrut(20));
        helpPanel.add(scrollPane);
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
package screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import core.Board;
import core.Difficulty;
import core.Position;
import core.Settings;
import core.ShapeType;
import core.Tetromino;
import network.NetworkManager;

/**
 * P2P 네트워크 대전 모드 게임 패널 - 두 개의 보드를 좌우로 배치
 */
public class P2PBattlePanel extends JPanel {

    private long lastRTT = 0;
    private boolean isLagging = false;
    private final boolean isHost;

    private final Board myBoard;      // 내 보드
    private final Board enemyBoard;   // 상대 보드 (렌더링 전용)

    private final Timer timer;
    private Timer networkTimer;
    private Timer syncTimer;  // 블록 동기화용 타이머
    private final int CELL;
    private final int BOARD_W;
    private final int BOARD_H;
    private final int SIDE_W;
    private final int GAP;  // 보드 사이 간격

    private boolean paused = false;
    private final int baseDelay = 800;  // 대전모드 기본 속도

    // 시간제한 모드
    private final boolean isTimeAttack;
    private long timeLimit = 180000;  // 3분 (밀리초)
    private long gameStartTime;
    private long pausedTime = 0;
    private long pauseStartTime = 0;

    // 각 보드별 플래시 애니메이션
    private int[] flashingRowsMy = null;
    private int[] flashingRowsEnemy = null;
    private long flashUntilMy = 0;
    private long flashUntilEnemy = 0;
    private static final long FLASH_MS = 150;

    // 승자 표시
    private String winner = null;

    // 내가 보낸 공격 패턴 (UI 표시용)
    private List<ShapeType[]> outgoingAttackPattern = new java.util.ArrayList<>();
    
    // 받은 공격 패턴 (대기 중, UI 표시용)
    private List<ShapeType[]> incomingAttackPattern = new java.util.ArrayList<>();

    // ===== CHAT UI =====
    private javax.swing.JTextArea chatArea;
    private javax.swing.JTextField chatInput;
    private javax.swing.JButton chatSendBtn;

    // ===== 네트워크 상태 체크용 =====
    private long lastAliveTime = System.currentTimeMillis();
    private boolean connectionLost = false;
    
    // ===== 게임 오버 버튼 네비게이션 =====
    private int selectedButtonIndex = 0;
    private javax.swing.JButton restartButton;
    private javax.swing.JButton menuButton;

        public P2PBattlePanel(
        Difficulty difficulty, 
        boolean isItemMode, 
        boolean isTimeAttack,
        boolean isHost
    ) {
        this.isTimeAttack = isTimeAttack;
        this.isHost = isHost; 
        
        this.myBoard = new Board(difficulty, isItemMode);
        this.enemyBoard = new Board(difficulty, isItemMode);  // 렌더링 전용
        this.gameStartTime = System.currentTimeMillis();
        
        // 호스트와 클라이언트 모두 독립적으로 블록 생성
        // myBoard는 자동으로 초기화됨

        // 메인 메뉴 음악 끄고 게임 음악 켜기 (설정된 볼륨으로)
        BackgroundMusicPlayer.getInstance().stop();
        BackgroundMusicPlayer.getInstance().play("/music/InGameBGM.wav", Settings.getGameMusicVolume());

        // Settings에서 셀 크기 및 화면 크기 계산
        this.CELL = Settings.getCellSize();
        this.BOARD_W = Board.COLS * CELL;
        this.BOARD_H = Board.ROWS * CELL;
        this.SIDE_W = (int) (200 * Settings.getScaleFactor());
        this.GAP = (int) (50 * Settings.getScaleFactor());

        // 전체 패널 크기: SIDE + BOARD + GAP + BOARD + SIDE
        int totalWidth = SIDE_W + BOARD_W + GAP + BOARD_W + SIDE_W;

        // ★ 수정된 부분
        setPreferredSize(new Dimension(totalWidth, BOARD_H + 200));

        setBackground(Color.BLACK);
        setFocusable(true);


        // 키 입력 처리
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

// 게임 타이머
        timer = new Timer(baseDelay, e -> {
            if (!paused && winner == null) {
                // 내 보드 자동 낙하
                if (!myBoard.isGameOver()) {
                    // 자동 한 칸 낙하
                    boolean moved = myBoard.moveDown();

                    if (!moved) {
                        // 블록이 고정되었을 때 (moveDown이 false 반환)
                        checkBlockPlacement();
                    }

                    if (moved) {
                        checkFlashing(myBoard, true);
                    }
                }
                
                // 상대방 보드 자동 낙하
                if (!enemyBoard.isGameOver()) {
                    boolean enemyMoved = enemyBoard.moveDown();
                    if (enemyMoved) {
                        checkFlashing(enemyBoard, false);
                    }
                }

                // 승자 확인
                checkWinner();
                repaint();
            }
        });
        timer.start();

        // 네트워크 상태 체크 타이머 추가
        networkTimer = new Timer(100, ev -> checkNetworkStatus());
        networkTimer.start();
        
        // 블록 동기화 타이머 (100ms 주기) - 양쪽 모두 전송
        syncTimer = new Timer(100, ev -> {
            if (!paused && winner == null) {
                sendBlockShapes();
            }
        });
        syncTimer.start();

        // 네트워크 메시지 수신 핸들러
        NetworkManager.getInstance().setMessageListener(msg -> {
            handleNetworkMessage(msg);
        });
        
        // 호스트는 게임 시작 시 초기 블록 정보 전송 (지연 실행) - 비활성화
        // 각자 독립적으로 블록 생성
        /*
        if (isHost) {
            javax.swing.Timer initTimer = new javax.swing.Timer(100, e -> {
                sendInitialBlocks();
                ((javax.swing.Timer)e.getSource()).stop();
            });
            initTimer.setRepeats(false);
            initTimer.start();
        }
        */
// ===== CHAT UI INIT =====
        setLayout(null);

// 채팅 화면
        chatArea = new javax.swing.JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

// ★ 배경 검정, 글씨 흰색
        chatArea.setBackground(Color.BLACK);
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(chatArea);
        scroll.setBounds(10, BOARD_H + 10, 400, 90);

// 스크롤도 검정 테마
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE));

// 입력창
        chatInput = new javax.swing.JTextField();
        chatInput.setBounds(10, BOARD_H + 110, 300, 30);

// ★ 입력창도 배경 검정 + 흰 글씨
        chatInput.setBackground(Color.BLACK);
        chatInput.setForeground(Color.WHITE);
        chatInput.setCaretColor(Color.WHITE);
        chatInput.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE));

// Send 버튼
        chatSendBtn = new javax.swing.JButton("Send");
        chatSendBtn.setBounds(320, BOARD_H + 110, 90, 30);

// ★ 버튼도 검정 스타일
        chatSendBtn.setBackground(new Color(30, 30, 30));
        chatSendBtn.setForeground(Color.WHITE);
        chatSendBtn.setBorder(javax.swing.BorderFactory.createLineBorder(Color.WHITE));

        chatSendBtn.addActionListener(e -> {
            sendChat();
            requestFocusInWindow(); // 게임 패널로 포커스 복귀
        });
        
        chatInput.addActionListener(e -> {
            sendChat();
            requestFocusInWindow(); // 게임 패널로 포커스 복귀
        });

        add(scroll);
        add(chatInput);
        add(chatSendBtn);

    }

    // 네트워크 지연/끊김 상태 체크
    private void checkNetworkStatus() {

        long now = System.currentTimeMillis();

        // RTT 업데이트
        lastRTT = NetworkManager.getInstance().getRTT();

        // 상대방 최신 PONG 시각 가져오기
        long pongTime = NetworkManager.getInstance().getLastPingTime();
        if (pongTime > 0) {
            lastAliveTime = pongTime;
        }

        // 1) RTT 기반 랙 여부 판단
        isLagging = lastRTT > 200;

        // 2) 5초 이상 응답 없음 = 연결 끊김
        if (now - lastAliveTime > 5000) {
            timer.stop();
            JOptionPane.showMessageDialog(
                    this,
                    "Network connection has been lost.",
                    "Connection Lost",
                    JOptionPane.ERROR_MESSAGE
            );
            returnToMenu();
        }

        repaint();
    }

    private void checkBlockPlacement() {
        // 블록이 고정되었을 때 호출됨 (moveDown이 false를 반환한 직후)
        System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 블록 배치 감지! 대기 중인 공격: " + incomingAttackPattern.size());
        
        // 대기 중인 공격 줄을 보드에 적용 (블록이 고정되기 전에 적용)
        if (!incomingAttackPattern.isEmpty()) {
            System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 공격 적용 중...");
            myBoard.addPendingAttackLines(incomingAttackPattern);
            myBoard.applyPendingAttackLines();
            incomingAttackPattern.clear();
            System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 공격 적용 완료!");
        }

        // 블록 배치 시 보드 전체 상태 전송
        sendBoardState();
    }

    private void handleKeyPress(KeyEvent e) {
        int code = e.getKeyCode();

        // ENTER → 채팅창 포커스 이동 (채팅창이 포커스 없을 때만)
        if (code == KeyEvent.VK_ENTER && !chatInput.isFocusOwner()) {
            chatInput.requestFocusInWindow();
            return;
        }

        // 일시정지 (호스트만, 항상 처리)
        if (code == KeyEvent.VK_P) {
            if (isHost) {
                togglePause();
            }
            return;
        }

        // 승자가 있거나 pause 상태면 게임 조작 무시
        if (winner != null || paused) {
            return;
        }

        // 내 보드 조작
        if (!myBoard.isGameOver()) {
            if (code == Settings.getKeyLeft(Settings.Player.P1)) {
                myBoard.moveLeft();
                sendAction("LEFT");
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyRight(Settings.Player.P1)) {
                myBoard.moveRight();
                sendAction("RIGHT");
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyDown(Settings.Player.P1)) {
                myBoard.moveDown();
                sendAction("DOWN");
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyRotate(Settings.Player.P1)) {
                myBoard.rotate();
                sendAction("ROTATE");
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyHardDrop(Settings.Player.P1)) {
                myBoard.hardDrop();
                sendAction("HARDDROP");
                checkFlashing(myBoard, true);
            }
        }

        repaint();
    }


    private void checkFlashing(Board board, boolean isMine) {
        int[] rows = board.pollClearingRows();
        if (rows != null && rows.length > 0) {
            if (isMine) {
                flashingRowsMy = rows;
                flashUntilMy = System.currentTimeMillis() + FLASH_MS;

                // 2줄 이상 클리어 시 상대에게 공격 전송
            if (rows.length >= 2) {
                List<ShapeType[]> attackPattern = myBoard.getAttackPattern(rows);

                // 이전 공격 초기화 후 새 공격으로 교체
                outgoingAttackPattern.clear();
                outgoingAttackPattern = new java.util.ArrayList<>(attackPattern);
                
                // 상대에게 전송
                sendAttackPattern(attackPattern);
            }
            } else {
                flashingRowsEnemy = rows;
                flashUntilEnemy = System.currentTimeMillis() + FLASH_MS;
            }

            // 플래시 애니메이션 타이머
            if (isMine && flashingRowsMy != null) {
                Timer flashTimer = new Timer(20, null);
                flashTimer.addActionListener(ev -> {
                    if (System.currentTimeMillis() >= flashUntilMy) {
                        myBoard.clearRows(flashingRowsMy);
                        flashingRowsMy = null;
                        ((Timer) ev.getSource()).stop();
                    }
                    repaint();
                });
                flashTimer.setRepeats(true);
                flashTimer.start();
            } else if (!isMine && flashingRowsEnemy != null) {
                Timer flashTimer = new Timer(20, null);
                flashTimer.addActionListener(ev -> {
                    if (System.currentTimeMillis() >= flashUntilEnemy) {
                        enemyBoard.clearRows(flashingRowsEnemy);
                        flashingRowsEnemy = null;
                        ((Timer) ev.getSource()).stop();
                    }
                    repaint();
                });
                flashTimer.setRepeats(true);
                flashTimer.start();
            }
        }
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            if (isTimeAttack) {
                pauseStartTime = System.currentTimeMillis();
            }
            // 상대에게 PAUSE 알림
            NetworkManager.getInstance().send("PAUSE");
            showPauseMenu();
        } else {
            if (isTimeAttack && pauseStartTime > 0) {
                pausedTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
            // 상대에게 RESUME 알림
            NetworkManager.getInstance().send("RESUME");
        }
        repaint();
    }

    private javax.swing.JDialog pauseDialog = null;
    private javax.swing.JButton[] pauseButtons = null;
    private int selectedPauseButton = 0;
    
    private void showPauseMenu() {
        int baseFontSize = Settings.getBaseFontSize();
        double scale = Settings.getScaleFactor();
        
        pauseDialog = new javax.swing.JDialog((java.awt.Frame) null, "Pause", true);
        pauseDialog.setLayout(new java.awt.BorderLayout());
        pauseDialog.setSize((int)(400 * scale), (int)(250 * scale));
        pauseDialog.setLocationRelativeTo(this);
        pauseDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        
        // 메시지 패널
        javax.swing.JPanel messagePanel = new javax.swing.JPanel();
        javax.swing.JLabel messageLabel = new javax.swing.JLabel("Game Paused");
        messageLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.33)));
        messagePanel.add(messageLabel);
        pauseDialog.add(messagePanel, java.awt.BorderLayout.CENTER);
        
        // 버튼 패널
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setLayout(new java.awt.GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 40, 20, 40));
        
        javax.swing.JButton resumeButton = new javax.swing.JButton("Resume");
        javax.swing.JButton restartButton = new javax.swing.JButton("Restart");
        javax.swing.JButton menuButton = new javax.swing.JButton("Main Menu");
        javax.swing.JButton exitButton = new javax.swing.JButton("Exit");
        
        for (javax.swing.JButton btn : new javax.swing.JButton[]{resumeButton, restartButton, menuButton, exitButton}) {
            btn.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
            btn.setFocusable(false);
        }
        
        resumeButton.addActionListener(e -> {
            pauseDialog.dispose();
            paused = false;
            if (isTimeAttack && pauseStartTime > 0) {
                pausedTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
            NetworkManager.getInstance().send("RESUME");
            requestFocusInWindow();
        });
        
        restartButton.addActionListener(e -> {
            pauseDialog.dispose();
            // 네트워크 대전에서는 재시작 불가
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Restart is not available in network mode.", 
                "Info", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            paused = false;
            if (isTimeAttack && pauseStartTime > 0) {
                pausedTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
            NetworkManager.getInstance().send("RESUME");
            requestFocusInWindow();
        });
        
        menuButton.addActionListener(e -> {
            pauseDialog.dispose();
            timer.stop();
            returnToMenu();
        });
        
        exitButton.addActionListener(e -> {
            pauseDialog.dispose();
            timer.stop();
            System.exit(0);
        });
        
        buttonPanel.add(resumeButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(menuButton);
        buttonPanel.add(exitButton);
        pauseDialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        pauseButtons = new javax.swing.JButton[]{resumeButton, restartButton, menuButton, exitButton};
        selectedPauseButton = 0;
        updatePauseButtonHighlight();
        
        pauseDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handlePauseKeyPress(e);
            }
        });
        
        pauseDialog.setFocusable(true);
        pauseDialog.setVisible(true);
    }
    
    private void handlePauseKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (selectedPauseButton > 0) {
                    selectedPauseButton--;
                    updatePauseButtonHighlight();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (selectedPauseButton < pauseButtons.length - 1) {
                    selectedPauseButton++;
                    updatePauseButtonHighlight();
                }
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                pauseButtons[selectedPauseButton].doClick();
                break;
        }
    }
    
    private void updatePauseButtonHighlight() {
        for (int i = 0; i < pauseButtons.length; i++) {
            if (i == selectedPauseButton) {
                pauseButtons[i].setBackground(new Color(100, 150, 255));
                pauseButtons[i].setOpaque(true);
            } else {
                pauseButtons[i].setBackground(null);
                pauseButtons[i].setOpaque(false);
            }
        }
    }

    private void checkWinner() {
        boolean myOver = myBoard.isGameOver();
        boolean enemyOver = enemyBoard.isGameOver();

        // 시간제한 모드: 시간이 다 되면 점수로 승부 판정
        if (isTimeAttack && winner == null) {
            long elapsedTime = System.currentTimeMillis() - gameStartTime - pausedTime;
            if (elapsedTime >= timeLimit) {
                int myScore = myBoard.getScore();
                int enemyScore = enemyBoard.getScore();

                if (myScore > enemyScore) {
                    winner = "YOU WIN";
                } else if (enemyScore > myScore) {
                    winner = "YOU LOSE";
                } else {
                    winner = "DRAW";
                }
                timer.stop();
                showGameOver();
                return;
            }
        }

        if (myOver && enemyOver) {
            winner = "DRAW";
            timer.stop();
            networkTimer.stop();
            showGameOver();
        } else if (myOver) {
            // 내가 먼저 죽었다 → 상대에게 GAMEOVER 알림 보내기
            NetworkManager.getInstance().send("GAMEOVER");

            winner = "YOU LOSE";
            timer.stop();
            networkTimer.stop();
            showGameOver();
        } else if (enemyOver) {
            winner = "YOU WIN";
            timer.stop();
            networkTimer.stop();
            showGameOver();
        }
    }

    private void showGameOver() {
        SwingUtilities.invokeLater(() -> {
            String message = winner;
            int baseFontSize = Settings.getBaseFontSize();
            double scale = Settings.getScaleFactor();

            // 커스텀 패널 생성
            javax.swing.JPanel panel = new javax.swing.JPanel();
            panel.setLayout(new java.awt.BorderLayout(10, 10));
            panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            javax.swing.JLabel messageLabel = new javax.swing.JLabel(message);
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messageLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.5)));
            panel.add(messageLabel, java.awt.BorderLayout.CENTER);
            
            // 버튼 패널
            javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
            buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));
            
            restartButton = new javax.swing.JButton("Restart");
            menuButton = new javax.swing.JButton("Return to Menu");
            
            restartButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
            menuButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
            
            restartButton.setPreferredSize(new Dimension((int)(120 * scale), (int)(40 * scale)));
            menuButton.setPreferredSize(new Dimension((int)(150 * scale), (int)(40 * scale)));
            
            restartButton.setFocusable(false);
            menuButton.setFocusable(false);
            
            buttonPanel.add(restartButton);
            buttonPanel.add(menuButton);
            
            panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
            
            // 초기 하이라이트
            selectedButtonIndex = 0;
            updateGameOverButtonHighlight();
            
            // 다이얼로그 생성
            javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) null, "Game Over", true);
            dialog.setContentPane(panel);
            dialog.setSize((int)(400 * scale), (int)(200 * scale));
            dialog.setLocationRelativeTo(this);
            dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
            
            // 키 리스너 추가
            dialog.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    int code = e.getKeyCode();
                    
                    if (code == java.awt.event.KeyEvent.VK_LEFT) {
                        if (selectedButtonIndex > 0) {
                            selectedButtonIndex--;
                            updateGameOverButtonHighlight();
                        }
                    } else if (code == java.awt.event.KeyEvent.VK_RIGHT) {
                        if (selectedButtonIndex < 1) {
                            selectedButtonIndex++;
                            updateGameOverButtonHighlight();
                        }
                    } else if (code == java.awt.event.KeyEvent.VK_ENTER || code == java.awt.event.KeyEvent.VK_SPACE) {
                        dialog.dispose();
                        returnToMenu();
                    }
                }
            });
            
            // 버튼 액션 리스너
            restartButton.addActionListener(e -> {
                dialog.dispose();
                returnToMenu();
            });
            
            menuButton.addActionListener(e -> {
                dialog.dispose();
                returnToMenu();
            });
            
            dialog.setFocusable(true);
            dialog.requestFocusInWindow();
            dialog.setVisible(true);
        });
    }
    
    private void updateGameOverButtonHighlight() {
        if (restartButton != null && menuButton != null) {
            if (selectedButtonIndex == 0) {
                restartButton.setBackground(new Color(100, 150, 255));
                restartButton.setForeground(Color.WHITE);
                restartButton.setOpaque(true);
                menuButton.setBackground(null);
                menuButton.setForeground(Color.BLACK);
                menuButton.setOpaque(false);
            } else {
                menuButton.setBackground(new Color(100, 150, 255));
                menuButton.setForeground(Color.WHITE);
                menuButton.setOpaque(true);
                restartButton.setBackground(null);
                restartButton.setForeground(Color.BLACK);
                restartButton.setOpaque(false);
            }
        }
    }

    private void returnToMenu() {

        NetworkManager.getInstance().setMessageListener(null);
        NetworkManager.getInstance().close();

        timer.stop();
        networkTimer.stop();
        if (syncTimer != null) {
            syncTimer.stop();
        }
        BackgroundMusicPlayer.getInstance().stop();

        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            Mainmenu menu = new Mainmenu();
            menu.setVisible(true);
        });
    }

    // 매 프레임 블록 shape 전송
    private void sendBlockShapes() {
        Tetromino current = myBoard.getCurrent();
        ShapeType next = myBoard.getNextShape();
        
        StringBuilder sb = new StringBuilder();
        sb.append("SHAPES:").append(isHost ? "HOST" : "CLIENT").append(":");
        sb.append("{");
        
        // 현재 블록
        if (current != null) {
            sb.append("\"current\":\"").append(current.getShape().name()).append("\",");
        } else {
            sb.append("\"current\":null,");
        }
        
        // 다음 블록
        sb.append("\"next\":\"").append(next.name()).append("\"");
        sb.append("}");
        
        NetworkManager.getInstance().send(sb.toString());
    }

    // ===== 네트워크 관련 메서드 =====
    // 보드 전체 상태 전송 (블록 배치 시)
    private void sendBoardState() {
        int score = myBoard.getScore();
        ShapeType[][] grid = myBoard.getGrid();
        
        StringBuilder sb = new StringBuilder();
        sb.append("BOARDSTATE:").append(isHost ? "HOST" : "CLIENT").append(":");
        sb.append("{");
        
        // 점수
        sb.append("\"score\":").append(score).append(",");
        
        // 고정된 블록들의 grid 전송
        sb.append("\"grid\":[");
        for (int y = 0; y < Board.ROWS; y++) {
            sb.append("[");
            for (int x = 0; x < Board.COLS; x++) {
                ShapeType s = grid[y][x];
                sb.append(s == null ? "\"0\"" : "\"" + s.name() + "\"");
                if (x < Board.COLS - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (y < Board.ROWS - 1) {
                sb.append(",");
            }
        }
        sb.append("]").append(",");
        
        // 받을 공격 패턴 (incomingAttackPattern)
        sb.append("\"incoming\":[");
        for (int i = 0; i < incomingAttackPattern.size(); i++) {
            ShapeType[] row = incomingAttackPattern.get(i);
            sb.append("[");
            for (int j = 0; j < row.length; j++) {
                sb.append(row[j] == null ? "\"0\"" : "\"" + row[j].name() + "\"");
                if (j < row.length - 1) sb.append(",");
            }
            sb.append("]");
            if (i < incomingAttackPattern.size() - 1) sb.append(",");
        }
        sb.append("]").append(",");
        
        // 보낼 공격 패턴 (outgoingAttackPattern)
        sb.append("\"outgoing\":[");
        for (int i = 0; i < outgoingAttackPattern.size(); i++) {
            ShapeType[] row = outgoingAttackPattern.get(i);
            sb.append("[");
            for (int j = 0; j < row.length; j++) {
                sb.append(row[j] == null ? "\"0\"" : "\"" + row[j].name() + "\"");
                if (j < row.length - 1) sb.append(",");
            }
            sb.append("]");
            if (i < outgoingAttackPattern.size() - 1) sb.append(",");
        }
        sb.append("]");
        
        sb.append("}");
        
        NetworkManager.getInstance().send(sb.toString());
        System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 보드 상태 전송 (incoming: " + 
                          incomingAttackPattern.size() + "줄, outgoing: " + outgoingAttackPattern.size() + "줄)");
    }
    
    // 초기 블록 정보 전송 (호스트만)
    private void sendInitialBlocks() {
        Tetromino current = myBoard.getCurrent();
        ShapeType nextShape = myBoard.getNextShape();
        
        StringBuilder sb = new StringBuilder();
        sb.append("INIT:{");
        
        // 현재 블록
        if (current != null) {
            sb.append("\"current\":\"").append(current.getShape().name()).append("\",");
            System.out.println("[Host] Sending current: " + current.getShape().name());
        }
        
        // 다음 블록
        sb.append("\"next\":\"").append(nextShape.name()).append("\"");
        System.out.println("[Host] Sending next: " + nextShape.name());
        sb.append("}");
        
        String message = sb.toString();
        System.out.println("[Host] Sending INIT: " + message);
        NetworkManager.getInstance().send(message);
    }
    
    // 조작 이벤트 전송
    private void sendAction(String action) {
        NetworkManager.getInstance().send("ACTION:" + action);
    }

    // 공격 패턴 전송
    private void sendAttackPattern(List<ShapeType[]> pattern) {
        // 내가 보낸 공격 패턴을 저장 (UI 표시용)
        outgoingAttackPattern = new java.util.ArrayList<>(pattern);

        StringBuilder sb = new StringBuilder();
        sb.append("ATTACK:{\"rows\":[");

        for (int i = 0; i < pattern.size(); i++) {
            ShapeType[] row = pattern.get(i);
            sb.append("[");
            for (int j = 0; j < row.length; j++) {
                ShapeType s = row[j];
                sb.append(s == null ? "\"0\"" : "\"" + s.name() + "\"");
                if (j < row.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (i < pattern.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]}");
        NetworkManager.getInstance().send("ATTACK:" + (isHost ? "HOST" : "CLIENT") + ":" + sb.toString());
    }

    // 네트워크 메시지 수신
    private void handleNetworkMessage(String msg) {

        // 🔥 상대 alive 처리: 반드시 제일 위에 넣어야 함
        lastAliveTime = System.currentTimeMillis();

        // ===== CHAT =====
        if (msg.startsWith("CHAT:")) {
            String text = msg.substring(5);
            chatArea.append("ENEMY: " + text + "\n");
            return;
        }
        
        // ===== 블록 shape 정보 수신 =====
        if (msg.startsWith("SHAPES:")) {
            String body = msg.substring(7); // HOST:{...} or CLIENT:{...}
            int colonIndex = body.indexOf(":");
            if (colonIndex < 0) return;
            
            String sender = body.substring(0, colonIndex); // HOST or CLIENT
            String json = body.substring(colonIndex + 1);
            
            // 내가 보낸 메시지는 무시
            if ((sender.equals("HOST") && isHost) || (sender.equals("CLIENT") && !isHost)) {
                return;
            }
            
            // 상대 메시지 처리
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    handleBlockShapes(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            return;
        }
        
        // ===== 초기 블록 정보 수신 (클라이언트만) =====
        if (msg.startsWith("INIT:")) {
            if (!isHost) {
                String json = msg.substring(5);
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        handleInitialBlocks(json);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    repaint();
                });
            }
            return;
        }

        // ===== 새 블록 정보 수신 (deprecated) =====
        if (msg.startsWith("NEWBLOCK:")) {
            String json = msg.substring(9);
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    handleNewBlock(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                repaint();
            });
            return;
        }
        
        // ===== 보드 전체 상태 수신 =====
        if (msg.startsWith("BOARDSTATE:")) {
            String body = msg.substring(11); // HOST:{...} or CLIENT:{...}
            int colonIndex = body.indexOf(":");
            if (colonIndex < 0) return;
            
            String sender = body.substring(0, colonIndex);
            String json = body.substring(colonIndex + 1);
            
            // 내가 보낸 메시지는 무시
            if ((sender.equals("HOST") && isHost) || (sender.equals("CLIENT") && !isHost)) {
                return;
            }
            
            // 상대 보드 상태 업데이트
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    handleBoardState(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                repaint();
            });
            return;
        }
        
        // ===== 조작 이벤트 수신 =====
        if (msg.startsWith("ACTION:")) {
            String action = msg.substring(7);
            javax.swing.SwingUtilities.invokeLater(() -> {
                handleEnemyAction(action);
                repaint();
            });
            return;
        }

        // ===== 일시정지/재개 =====
        if (msg.equals("PAUSE")) {
            paused = true;
            if (isTimeAttack) {
                pauseStartTime = System.currentTimeMillis();
            }
            repaint();
            return;
        }

        if (msg.equals("RESUME")) {
            paused = false;
            if (isTimeAttack && pauseStartTime > 0) {
                pausedTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
            repaint();
            return;
        }

        // ===== 공격 패턴 수신 =====
        if (msg.startsWith("ATTACK:")) {
            String body = msg.substring(7); // HOST:{"rows":[...}
            int p = body.indexOf(":");
            if (p < 0) return;

            String sender = body.substring(0, p);   // HOST or CLIENT
            String json = body.substring(p + 1);

            // 🔥 내가 보낸 공격이면 무시
            if ((sender.equals("HOST") && isHost) ||
                (sender.equals("CLIENT") && !isHost)) {
                return;
            }

            // 🔥 상대 공격은 임시 저장 (블록 배치 시 적용)
            List<ShapeType[]> patterns = parseAttackPattern(json);
            System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 공격 수신! 줄 수: " + patterns.size() + ", 발신자: " + sender);
            SwingUtilities.invokeLater(() -> {
                int incomingLines = patterns.size();
                
                // 이미 10줄이면 무시
                if (incomingAttackPattern.size() >= 10) {
                    System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 공격 무시 (이미 10줄)");
                    repaint();
                    return;
                }
                
                // 기존 + 새로운 줄이 10을 초과하면, 아래쪽(앞쪽)부터 제거
                int totalLines = incomingAttackPattern.size() + incomingLines;
                if (totalLines > 10) {
                    int toRemove = totalLines - 10;
                    // 앞쪽(아래쪽)부터 제거
                    for (int i = 0; i < toRemove && !incomingAttackPattern.isEmpty(); i++) {
                        incomingAttackPattern.remove(0);
                    }
                    System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 아래쪽 " + toRemove + "줄 제거 (10줄 제한)");
                }
                
                // 새로운 공격을 리스트 앞에 추가하여 가장 아래쪽에 배치되도록 함
                incomingAttackPattern.addAll(0, patterns);
                System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 대기 중인 총 공격 줄: " + incomingAttackPattern.size());
                repaint();
            });

            return;
        }


        // ===== 상대 게임 오버 =====
        if (msg.equals("GAMEOVER")) {
            if (winner == null) {
                winner = "YOU WIN";
                timer.stop();
                showGameOver();
            }
        }
    }

    // 상대 보드(grid) 상태 갱신
    private void sendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) {
            return;
        }

        NetworkManager.getInstance().send("CHAT:" + msg);
        chatArea.append("ME: " + msg + "\n");
        chatInput.setText("");
    }
    
    // 블록 shape 정보 수신 처리
    private void handleBlockShapes(String json) {
        try {
            // current 파싱
            ShapeType currentShape = null;
            int currentIndex = json.indexOf("\"current\":");
            if (currentIndex != -1) {
                int valueStart = currentIndex + 10;
                if (json.startsWith("null", valueStart)) {
                    currentShape = null;
                } else {
                    int quoteStart = json.indexOf("\"", valueStart) + 1;
                    int quoteEnd = json.indexOf("\"", quoteStart);
                    if (quoteStart > 0 && quoteEnd > quoteStart) {
                        String currentStr = json.substring(quoteStart, quoteEnd);
                        currentShape = ShapeType.valueOf(currentStr);
                    }
                }
            }
            
            // next 파싱
            int nextIndex = json.indexOf("\"next\":\"");
            ShapeType nextShape = null;
            if (nextIndex != -1) {
                int nextEnd = json.indexOf("\"", nextIndex + 8);
                if (nextEnd > nextIndex + 8) {
                    String nextStr = json.substring(nextIndex + 8, nextEnd);
                    nextShape = ShapeType.valueOf(nextStr);
                }
            }
            
            // 상대방의 블록을 enemyBoard에 표시
            if (currentShape != null) {
                Tetromino current = enemyBoard.getCurrent();
                // current 블록이 없거나 shape이 다르면 새로 생성
                if (current == null || current.getShape() != currentShape) {
                    Tetromino newCurrent = new Tetromino(currentShape, Board.COLS / 2 - 2, 0);
                    enemyBoard.overrideCurrent(newCurrent);
                }
            } else {
                enemyBoard.overrideCurrent(null);
            }
            
            if (nextShape != null) {
                enemyBoard.setNextShape(nextShape);
            }
            
        } catch (Exception ex) {
            System.err.println("[🔥 handleBlockShapes 에러] " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // 초기 블록 정보 수신 처리 (클라이언트만)
    private void handleInitialBlocks(String json) {
        try {
            System.out.println("[Client] Received INIT: " + json);
            
            // current 파싱
            int currentIndex = json.indexOf("\"current\":\"");
            ShapeType currentShape = null;
            if (currentIndex != -1) {
                int currentEnd = json.indexOf("\"", currentIndex + 11);
                String currentStr = json.substring(currentIndex + 11, currentEnd);
                currentShape = ShapeType.valueOf(currentStr);
                System.out.println("[Client] Current shape: " + currentShape);
            }
            
            // next 파싱
            int nextIndex = json.indexOf("\"next\":\"");
            ShapeType nextShape = null;
            if (nextIndex != -1) {
                int nextEnd = json.indexOf("\"", nextIndex + 8);
                String nextStr = json.substring(nextIndex + 8, nextEnd);
                nextShape = ShapeType.valueOf(nextStr);
                System.out.println("[Client] Next shape: " + nextShape);
            }
            
            // 클라이언트: 호스트의 블록을 내 보드에 설정
            if (currentShape != null) {
                Tetromino currentBlock = new Tetromino(currentShape, Board.COLS / 2 - 2, 0);
                myBoard.overrideCurrent(currentBlock);
                System.out.println("[Client] Set my current block: " + currentShape);
            }
            
            // 다음 블록도 호스트와 동일하게 설정
            if (nextShape != null) {
                myBoard.setNextShape(nextShape);
                System.out.println("[Client] Set my next block: " + nextShape);
            }
            
        } catch (Exception ex) {
            System.err.println("[Client] Error handling INIT: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // 새 블록 정보 수신 처리
    private void handleNewBlock(String json) {
        try {
            // next 파싱
            int nextIndex = json.indexOf("\"next\":\"");
            if (nextIndex == -1) return;
            int nextEnd = json.indexOf("\"", nextIndex + 8);
            String nextStr = json.substring(nextIndex + 8, nextEnd);
            ShapeType nextShape = ShapeType.valueOf(nextStr);
            
            // score 파싱
            int scoreIndex = json.indexOf("\"score\":");
            int scoreEnd = json.indexOf(",", scoreIndex);
            if (scoreEnd == -1) scoreEnd = json.indexOf("}", scoreIndex);
            String scoreStr = json.substring(scoreIndex + 8, scoreEnd).trim();
            int score = Integer.parseInt(scoreStr);
            
            // grid 파싱
            int gridStart = json.indexOf("\"grid\":") + 7;
            int gridEnd = json.lastIndexOf("]}");
            if (gridStart < 7 || gridEnd < 0) return;
            
            String gridJson = json.substring(gridStart, gridEnd + 1);
            String[] rowStrs = gridJson.substring(1, gridJson.length() - 1).split("\\],\\[");
            
            ShapeType[][] newGrid = new ShapeType[Board.ROWS][Board.COLS];
            for (int y = 0; y < rowStrs.length && y < Board.ROWS; y++) {
                String row = rowStrs[y].replace("[", "").replace("]", "");
                String[] cols = row.split(",");
                for (int x = 0; x < cols.length && x < Board.COLS; x++) {
                    String v = cols[x].replace("\"", "");
                    newGrid[y][x] = v.equals("0") ? null : ShapeType.valueOf(v);
                }
            }
            
            // 상대 보드 업데이트
            enemyBoard.setGrid(newGrid);
            enemyBoard.setScore(score);
            
            // 상대 보드 next 설정
            enemyBoard.setNextShape(nextShape);
            
            // 클라이언트는 호스트가 보낸 next를 자신의 다음 블록으로도 설정
            if (!isHost) {
                myBoard.setNextShape(nextShape);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // 보드 전체 상태 수신 처리
    private void handleBoardState(String json) {
        try {
            // score 파싱
            int scoreIndex = json.indexOf("\"score\":");
            int scoreEnd = json.indexOf(",", scoreIndex);
            if (scoreEnd == -1) scoreEnd = json.indexOf("}", scoreIndex);
            String scoreStr = json.substring(scoreIndex + 8, scoreEnd).trim();
            int score = Integer.parseInt(scoreStr);
            
            // grid 파싱
            int gridStart = json.indexOf("\"grid\":") + 7;
            int gridCommaIndex = json.indexOf("],\"incoming\"");
            if (gridCommaIndex < 0) gridCommaIndex = json.lastIndexOf("]}");
            
            String gridJson = json.substring(gridStart, gridCommaIndex + 1);
            String[] rowStrs = gridJson.substring(1, gridJson.length() - 1).split("\\],\\[");
            
            ShapeType[][] newGrid = new ShapeType[Board.ROWS][Board.COLS];
            for (int y = 0; y < rowStrs.length && y < Board.ROWS; y++) {
                String row = rowStrs[y].replace("[", "").replace("]", "");
                String[] cols = row.split(",");
                for (int x = 0; x < cols.length && x < Board.COLS; x++) {
                    String v = cols[x].replace("\"", "");
                    newGrid[y][x] = v.equals("0") ? null : ShapeType.valueOf(v);
                }
            }
            
            // 상대 보드 업데이트
            enemyBoard.setGrid(newGrid);
            enemyBoard.setScore(score);
            
            // incoming 파싱 (상대의 incoming = 상대가 받을 공격 = 내가 보낸 공격)
            int incomingStart = json.indexOf("\"incoming\":[");
            if (incomingStart >= 0) {
                int incomingArrayStart = incomingStart + 12;
                int incomingArrayEnd = json.indexOf("],\"outgoing\"", incomingArrayStart);
                if (incomingArrayEnd < 0) incomingArrayEnd = json.indexOf("]}", incomingArrayStart);
                
                String incomingContent = json.substring(incomingArrayStart, incomingArrayEnd);
                
                // 상대방의 incoming이 비어있으면 (공격을 적용했으면)
                if (incomingContent.trim().isEmpty()) {
                    // 내가 보낸 공격이 적용되었으므로 outgoing 초기화
                    if (!outgoingAttackPattern.isEmpty()) {
                        System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 상대가 공격을 받음. outgoing 초기화");
                        outgoingAttackPattern.clear();
                    }
                }
            }
            
            // outgoing 파싱 (상대의 outgoing = 상대가 보낸 공격)
            // ATTACK 메시지로 이미 incomingAttackPattern에 추가되므로 별도 처리 불필요
            
            System.out.println("[" + (isHost ? "HOST" : "CLIENT") + "] 보드 상태 수신 완료 - Score: " + score);
            
        } catch (Exception ex) {
            System.err.println("[handleBoardState 에러] " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // 상대방 조작 이벤트 처리
    private void handleEnemyAction(String action) {
        switch (action) {
            case "LEFT" -> enemyBoard.moveLeft();
            case "RIGHT" -> enemyBoard.moveRight();
            case "DOWN" -> enemyBoard.moveDown();
            case "ROTATE" -> enemyBoard.rotate();
            case "HARDDROP" -> enemyBoard.hardDrop();
        }
        checkFlashing(enemyBoard, false);
    }

    // ATTACK 패턴 JSON 문자열 -> List<ShapeType[]> 변환
    private List<ShapeType[]> parseAttackPattern(String json) {
        List<ShapeType[]> list = new java.util.ArrayList<>();

        int start = json.indexOf("[[") + 2;
        int end = json.lastIndexOf("]]");
        if (start < 2 || end < 0 || start >= end) {
            return list;
        }

        String content = json.substring(start, end);
        String[] rowStrs = content.split("\\],\\[");

        for (String row : rowStrs) {
            String cleaned = row.replace("[", "").replace("]", "");
            String[] cols = cleaned.split(",");
            ShapeType[] line = new ShapeType[cols.length];

            for (int i = 0; i < cols.length; i++) {
                String v = cols[i].replace("\"", "");
                line[i] = v.equals("0") ? null : ShapeType.valueOf(v);
            }

            list.add(line);
        }

        return list;
    }

    @Override
protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        int startX1 = SIDE_W;
        int startX2 = SIDE_W + BOARD_W + GAP;

        // 내 보드 사이드바
        drawSidebar(g2, myBoard, 0, "YOU");

        // 내 보드 영역
        g2.translate(startX1, 0);
        drawBoard(g2, myBoard, flashingRowsMy);
        drawCurrent(g2, myBoard);
        drawGrid(g2);
        g2.translate(-startX1, 0);

        // 보드 사이 간격
        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(SIDE_W + BOARD_W, 0, GAP, BOARD_H);

        // 상대 보드 영역
        g2.translate(startX2, 0);
        drawBoard(g2, enemyBoard, flashingRowsEnemy);
        drawCurrent(g2, enemyBoard);
        drawGrid(g2);
        g2.translate(-startX2, 0);

        // 상대 보드 사이드바
        drawSidebar(g2, enemyBoard, SIDE_W + BOARD_W + GAP + BOARD_W, "ENEMY");

        if (paused) {
            drawPaused(g2);
        }
        if (winner != null) {
            drawWinner(g2);
        }

        // 네트워크 지연 표시
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g2.setColor(isLagging ? Color.RED : Color.GREEN);
        String lagText = "RTT: " + lastRTT + "ms";
        g2.drawString(lagText, 20, 40);

        g2.dispose();

    }

    private void fillCell(Graphics2D g, int x, int y, Color c) {
        int px = x * CELL;
        int py = y * CELL;
        g.setColor(c);
        g.fillRect(px, py, CELL, CELL);
        g.setColor(c.darker());
        g.drawRect(px, py, CELL, CELL);
    }

    private void drawBoard(Graphics2D g, Board board, int[] flashRows) {
        ShapeType[][] grid = board.getGrid();
        for (int y = 0; y < Board.ROWS; y++) {
            boolean isFlashing = false;
            if (flashRows != null) {
                for (int fr : flashRows) {
                    if (fr == y) {
                        isFlashing = true;
                        break;
                    }
                }
            }

            for (int x = 0; x < Board.COLS; x++) {
                ShapeType s = grid[y][x];
                if (s != null) {
                    Color color = isFlashing ? Color.WHITE : s.getColor();
                    fillCell(g, x, y, color);
                }
            }
        }
    }

    private void drawCurrent(Graphics2D g, Board board) {
        Tetromino cur = board.getCurrent();
        if (cur == null || board.isGameOver()) {
            return;
        }

        // 현재 아이템 블록인지 확인
        items.ItemBlock currentItem = board.getCurrentItemBlock();

        if (currentItem != null) {
            Position[] blocks = cur.getBlocks();
            Color blockColor = Color.WHITE;

            for (int i = 0; i < blocks.length; i++) {
                Position p = blocks[i];
                int px = cur.getX() + p.x;
                int py = cur.getY() + p.y;
                if (px >= 0 && px < Board.COLS && py >= 0 && py < Board.ROWS) {
                    fillCell(g, px, py, blockColor);

                    char symbol;
                    if (currentItem instanceof items.SlowBlock slowBlock) {
                        symbol = slowBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof items.LineBlock lineBlock) {
                        symbol = lineBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof items.BombBlock bombBlock) {
                        symbol = bombBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof items.WeightBlock weightBlock) {
                        symbol = weightBlock.getBlockSymbol(i);
                    } else {
                        symbol = currentItem.getSymbol();
                    }

                    g.setColor(Color.BLACK);
                    g.setFont(g.getFont().deriveFont(Font.BOLD, CELL * 0.8f));
                    int symbolX = px * CELL + CELL / 4;
                    int symbolY = py * CELL + CELL * 3 / 4;
                    g.drawString(String.valueOf(symbol), symbolX, symbolY);
                }
            }
        } else {
            Color c = cur.getShape().getColor();
            for (Position p : cur.getBlocks()) {
                int px = cur.getX() + p.x;
                int py = cur.getY() + p.y;
                if (px >= 0 && px < Board.COLS && py >= 0 && py < Board.ROWS) {
                    fillCell(g, px, py, c);
                }
            }
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(20, 20, 20));
        for (int x = 0; x <= Board.COLS; x++) {
            g.drawLine(x * CELL, 0, x * CELL, BOARD_H);
        }
        for (int y = 0; y <= Board.ROWS; y++) {
            g.drawLine(0, y * CELL, BOARD_W, y * CELL);
        }
    }

    private void drawSidebar(Graphics2D g, Board board, int sx, String playerName) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(sx, 0, SIDE_W, BOARD_H);

        g.setColor(Color.WHITE);
        int baseFontSize = Settings.getBaseFontSize();

        // Player name
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float) (baseFontSize * 0.89)));
        g.drawString(playerName, sx + 20, 25);

        // NEXT
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float) baseFontSize));
        g.drawString("NEXT", sx + 20, 60);
        drawNextPreview(g, sx + 20, 80, board);

        // ITEM (아이템 모드일 때만 표시)
        int yOffset = 0;
        if (board.isItemMode()) {
            items.ItemBlock nextItem = board.getNextItemBlock();
            if (nextItem != null) {
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, (float) (baseFontSize * 0.78)));
                g.drawString("ITEM", sx + 20, 150);
                drawItemPreview(g, sx + 20, 165, nextItem);
                yOffset = 50;
            }
        }

        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float) baseFontSize));
        g.drawString("SCORE", sx + 20, 190 + yOffset);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float) baseFontSize));
        g.drawString(String.valueOf(board.getScore()), sx + 20, 218 + yOffset);

        // TIME (시간제한 모드일 때만 표시)
        if (isTimeAttack) {
            long elapsedTime = System.currentTimeMillis() - gameStartTime - pausedTime;
            long remainingTime = Math.max(0, timeLimit - elapsedTime);
            int seconds = (int) (remainingTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            g.setColor(remainingTime < 30000 ? Color.RED : Color.YELLOW);
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float) baseFontSize));
            g.drawString("TIME", sx + 20, 240 + yOffset);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, (float) baseFontSize));
            g.drawString(String.format("%d:%02d", minutes, seconds), sx + 20, 268 + yOffset);
            yOffset += 50;
        }

        // 대기 중인 공격 줄 표시
        List<ShapeType[]> pendingPatterns;
        if (board == myBoard) {
            // 내 보드: 내가 받을 공격 패턴 표시 (incoming)
            pendingPatterns = incomingAttackPattern;
        } else {
            // 상대 보드: 내가 보낼 공격 패턴 표시 (outgoing)
            pendingPatterns = outgoingAttackPattern;
        }
        int pendingLines = pendingPatterns.size();
        int displayLines = 10;
        int miniCellSize = 8;
        int miniCols = 10;
        int startX = sx + 20;
        int startY = 250 + yOffset;

        g.setColor(new Color(40, 40, 40));
        g.fillRect(startX - 2, startY - 2, miniCols * miniCellSize + 4, displayLines * miniCellSize + 4);

        for (int row = 0; row < displayLines; row++) {
            for (int col = 0; col < miniCols; col++) {
                int x = startX + col * miniCellSize;
                int y = startY + row * miniCellSize;

                int reverseRow = displayLines - 1 - row;
                if (reverseRow < pendingLines) {
                    ShapeType[] pattern = pendingPatterns.get(reverseRow);
                    if (pattern[col] != null) {
                        g.setColor(new Color(128, 128, 128));
                        g.fillRect(x, y, miniCellSize, miniCellSize);
                        g.setColor(new Color(80, 80, 80));
                        g.drawRect(x, y, miniCellSize, miniCellSize);
                    } else {
                        g.setColor(new Color(30, 30, 30));
                        g.fillRect(x, y, miniCellSize, miniCellSize);
                    }
                } else {
                    g.setColor(new Color(60, 60, 60));
                    g.drawRect(x, y, miniCellSize, miniCellSize);
                }
            }
        }

        if (pendingLines > 10) {
            g.setColor(Color.RED);
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float) (baseFontSize * 0.75)));
            g.drawString("+" + (pendingLines - 10), startX + miniCols * miniCellSize + 5, startY + displayLines * miniCellSize / 2);
        }
    }

    private void drawNextPreview(Graphics2D g, int px, int py, Board board) {
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - 10, py - 10, 80, 80, 8, 8);

        ShapeType next = board.getNextShape();
        if (next != null) {
            Position[] offs = next.getOffsets(0);
            int minx = 99, miny = 99, maxx = -99, maxy = -99;
            for (Position p : offs) {
                minx = Math.min(minx, p.x);
                maxx = Math.max(maxx, p.x);
                miny = Math.min(miny, p.y);
                maxy = Math.max(maxy, p.y);
            }

            int cell = CELL / 2;
            int w = (maxx - minx + 1) * cell;
            int h = (maxy - miny + 1) * cell;
            int cx = px + (80 - w) / 2 - 10;
            int cy = py + (80 - h) / 2 - 10;

            for (Position p : offs) {
                int cxp = cx + (p.x - minx) * cell;
                int cyp = cy + (p.y - miny) * cell;
                g.setColor(next.getColor());
                g.fillRect(cxp, cyp, cell, cell);
                g.setColor(next.getColor().darker());
                g.drawRect(cxp, cyp, cell, cell);
            }
        }
    }

    private void drawItemPreview(Graphics2D g, int px, int py, items.ItemBlock item) {
        ShapeType baseShape = item.getBaseShape();
        Position[] offs = baseShape.getOffsets(0);

        int minx = 99, miny = 99, maxx = -99, maxy = -99;
        for (Position p : offs) {
            minx = Math.min(minx, p.x);
            maxx = Math.max(maxx, p.x);
            miny = Math.min(miny, p.y);
            maxy = Math.max(maxy, p.y);
        }

        int cell = CELL / 2;
        int previewSize = 80;
        int padding = 10;
        int w = (maxx - minx + 1) * cell;
        int h = (maxy - miny + 1) * cell;
        int cx = px + (previewSize - w) / 2 - padding;
        int cy = py + (previewSize - h) / 2 - padding;

        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - padding, py - padding, previewSize, previewSize, 8, 8);

        for (int i = 0; i < offs.length; i++) {
            Position p = offs[i];
            int cxp = cx + (p.x - minx) * cell;
            int cyp = cy + (p.y - miny) * cell;

            g.setColor(Color.WHITE);
            g.fillRect(cxp, cyp, cell, cell);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(cxp, cyp, cell, cell);

            char symbol;
            if (item instanceof items.SlowBlock slowBlock) {
                symbol = slowBlock.getBlockSymbol(i);
            } else if (item instanceof items.LineBlock lineBlock) {
                symbol = lineBlock.getBlockSymbol(i);
            } else {
                symbol = item.getSymbol();
            }

            g.setColor(Color.BLACK);
            g.setFont(g.getFont().deriveFont(Font.BOLD, cell * 0.8f));
            int symbolX = cxp + cell / 4;
            int symbolY = cyp + cell * 3 / 4;
            g.drawString(String.valueOf(symbol), symbolX, symbolY);
        }
    }

    private void drawPaused(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 48f));
        String pauseText = "PAUSED";
        int textWidth = g.getFontMetrics().stringWidth(pauseText);
        g.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    private void drawWinner(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (winner.equals("YOU WIN")) {
            g.setColor(Color.YELLOW);
        } else if (winner.equals("YOU LOSE")) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.WHITE);
        }

        g.setFont(g.getFont().deriveFont(Font.BOLD, 56f));
        String winText = winner;
        int textWidth = g.getFontMetrics().stringWidth(winText);
        g.drawString(winText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    
}

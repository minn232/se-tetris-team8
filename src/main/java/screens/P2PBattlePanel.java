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

    // 블록 배치 감지용
    private Tetromino lastCurrentMy = null;

    // ===== CHAT UI =====
    private javax.swing.JTextArea chatArea;
    private javax.swing.JTextField chatInput;
    private javax.swing.JButton chatSendBtn;

    // ===== 네트워크 상태 체크용 =====
    private long lastAliveTime = System.currentTimeMillis();
    private boolean connectionLost = false;

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
                if (!myBoard.isGameOver()) {
                    // 블록 고정 후 새 블록이 떴는지 체크 + 공격줄 적용
                    checkBlockPlacement();

                    // 자동 한 칸 낙하
                    boolean moved = myBoard.moveDown();

                    if (moved) {
                        checkFlashing(myBoard, true);
                        sendBoardState(); // 낙하 상태도 상대에게 전송
                    }
                }

                // 승자 확인
                checkWinner();
                repaint();
            }
        });
        timer.start();

        // 네트워크 상태 체크 타이머 추가
        new Timer(100, ev -> checkNetworkStatus()).start();

        // 초기 현재 블록 저장
        lastCurrentMy = myBoard.getCurrent();

        // 네트워크 메시지 수신 핸들러
        NetworkManager.getInstance().setMessageListener(msg -> {
            handleNetworkMessage(msg);
        });
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

        chatSendBtn.addActionListener(e -> sendChat());
        chatInput.addActionListener(e -> sendChat());

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
                    "네트워크 연결이 끊어졌습니다.",
                    "Connection Lost",
                    JOptionPane.ERROR_MESSAGE
            );
            returnToMenu();
        }

        repaint();
    }

    private void checkBlockPlacement() {
        Tetromino current = myBoard.getCurrent();

        // 새로운 블록이 생성되었을 때 (블록이 배치된 직후)
        if (current != lastCurrentMy && lastCurrentMy != null) {
            // 대기 중인 공격 줄을 적용
            myBoard.applyPendingAttackLines();

            // 내 보드 상태를 네트워크로 전송
            sendBoardState();
        }

        // 현재 블록 저장
        lastCurrentMy = current;
    }

    private void handleKeyPress(KeyEvent e) {
        int code = e.getKeyCode();

        // ENTER → 채팅창 포커스 이동 (추가 옵션)
        if (code == KeyEvent.VK_ENTER) {
            chatInput.requestFocusInWindow();
            return;
        }

        // 일시정지
        if (code == KeyEvent.VK_P) {
            togglePause();
            return;
        }

        // 게임 종료 시 키 입력 무시
        if (winner != null || paused) {
            return;
        }

        // 내 보드 조작
        if (!myBoard.isGameOver()) {
            if (code == Settings.getKeyLeft(Settings.Player.P1)) {
                myBoard.moveLeft();
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyRight(Settings.Player.P1)) {
                myBoard.moveRight();
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyDown(Settings.Player.P1)) {
                myBoard.moveDown();
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyRotate(Settings.Player.P1)) {
                myBoard.rotate();
                checkFlashing(myBoard, true);
            } else if (code == Settings.getKeyHardDrop(Settings.Player.P1)) {
                myBoard.hardDrop();
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

                // ✔ 내 pending 공격에도 추가해야 오른쪽 UI가 업데이트됨
                myBoard.addPendingAttackLines(attackPattern);

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
            showPauseMenu();
        } else {
            if (isTimeAttack && pauseStartTime > 0) {
                pausedTime += System.currentTimeMillis() - pauseStartTime;
                pauseStartTime = 0;
            }
        }
        repaint();
    }

    private void showPauseMenu() {
        String[] options = {"Resume", "Quit to Menu"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Game Paused",
                "Pause Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            // Resume
            paused = false;
            requestFocusInWindow();
        } else if (choice == 1) {
            // Quit to Menu
            timer.stop();
            returnToMenu();
        } else {
            // 창을 닫은 경우
            paused = false;
            requestFocusInWindow();
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
            showGameOver();
        } else if (myOver) {
            // 내가 먼저 죽었다 → 상대에게 GAMEOVER 알림 보내기
            NetworkManager.getInstance().send("GAMEOVER");

            winner = "YOU LOSE";
            timer.stop();
            showGameOver();
        } else if (enemyOver) {
            winner = "YOU WIN";
            timer.stop();
            showGameOver();
        }
    }

    private void showGameOver() {
        SwingUtilities.invokeLater(() -> {
            String message = winner;

            String[] options = {"Return to Menu"};
            JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            returnToMenu();
        });
    }

    private void returnToMenu() {

        NetworkManager.getInstance().setMessageListener(null);
        NetworkManager.getInstance().close();

        timer.stop();
        BackgroundMusicPlayer.getInstance().stop();
        BackgroundMusicPlayer.getInstance().play("/music/MainBGM.wav", Settings.getMainMusicVolume());

        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            Mainmenu menu = new Mainmenu();
            menu.setVisible(true);
        });
    }

    // ===== 네트워크 관련 메서드 =====
    // 내 보드 상태 전송 (grid + current + score)
    private void sendBoardState() {
        ShapeType[][] grid = myBoard.getGrid();
        Tetromino cur = myBoard.getCurrent();

        StringBuilder sb = new StringBuilder();
        sb.append("BOARD:{");

        // ===== GRID =====
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
        sb.append("],");

        // ===== CURRENT BLOCK =====
        if (cur != null) {
            sb.append("\"cur\":{");
            sb.append("\"shape\":\"").append(cur.getShape().name()).append("\",");
            sb.append("\"x\":").append(cur.getX()).append(",");
            sb.append("\"y\":").append(cur.getY()).append(",");
            sb.append("\"rot\":").append(cur.getRotation());
            sb.append("},");
        } else {
            sb.append("\"cur\":null,");
        }

        sb.append("\"score\":").append(myBoard.getScore());
        sb.append("}");

        NetworkManager.getInstance().send(sb.toString());
    }

    // 공격 패턴 전송
    private void sendAttackPattern(List<ShapeType[]> pattern) {
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

        // ===== 상대 보드 상태 갱신 (grid + current) =====
        if (msg.startsWith("BOARD:")) {
            String json = msg.substring(6);

            // 네트워크 스레드 → EDT 로 넘기기
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    updateEnemyBoard(json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                repaint();
            });
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

            // 🔥 상대 공격은 적용
            List<ShapeType[]> patterns = parseAttackPattern(json);
            SwingUtilities.invokeLater(() -> {
                myBoard.addPendingAttackLines(patterns);
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
    // 상대 보드(grid + current) 상태 갱신
    private void updateEnemyBoard(String json) {
        try {
            // ----- 1) GRID 파싱 -----
            int gridStart = json.indexOf("\"grid\":") + 7;
            int gridEnd = json.indexOf("],\"cur\"");
            if (gridStart < 7 || gridEnd < 0) {
                return;
            }

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

            // Board.grid 내부 값만 복사
            enemyBoard.setGrid(newGrid);

            // ----- 2) CURRENT 파싱 -----
            int curIndex = json.indexOf("\"cur\":");
            if (curIndex == -1) {
                // cur 필드가 아예 없으면 current 제거
                enemyBoard.overrideCurrent(null);
                return;
            }

            int valueStart = curIndex + 6; // "cur": 뒤
            if (json.startsWith("null", valueStart)) {
                // "cur":null 인 경우
                enemyBoard.overrideCurrent(null);
                return;
            }

            // "cur":{ ... } 영역 추출
            int braceStart = json.indexOf('{', curIndex);
            int braceEnd = json.indexOf('}', braceStart);
            if (braceStart == -1 || braceEnd == -1) {
                enemyBoard.overrideCurrent(null);
                return;
            }

            String curJson = json.substring(braceStart + 1, braceEnd);
            String[] fields = curJson.split(",");

            ShapeType type = null;
            int cx = 0;
            int cy = 0;
            int rot = 0;

            for (String f : fields) {
                String[] kv = f.split(":");
                if (kv.length != 2) {
                    continue;
                }

                String key = kv[0].replace("\"", "").trim();
                String val = kv[1].replace("\"", "").trim();

                switch (key) {
                    case "shape" ->
                        type = ShapeType.valueOf(val);
                    case "x" ->
                        cx = Integer.parseInt(val);
                    case "y" ->
                        cy = Integer.parseInt(val);
                    case "rot" ->
                        rot = Integer.parseInt(val);
                }
            }

            if (type != null) {
                Tetromino enemyCur = new Tetromino(type, cx, cy);
                // Tetromino.rotate() 는 보드 충돌 검사 안 하니까 여기서 여러 번 돌려도 됨
                for (int i = 0; i < rot; i++) {
                    enemyCur.rotate();
                }
                enemyBoard.overrideCurrent(enemyCur);
            } else {
                enemyBoard.overrideCurrent(null);
            }
            if (type != null) {
                Tetromino enemyCur = new Tetromino(type, cx, cy);
                for (int i = 0; i < rot; i++) {
                    enemyCur.rotate();
                }
                enemyBoard.overrideCurrent(enemyCur);
            } else {
                enemyBoard.overrideCurrent(null);
            }

            // ===== SCORE 파싱 =====
            int scoreIndex = json.indexOf("\"score\":");
            if (scoreIndex != -1) {
                int end = json.indexOf("}", scoreIndex);
                if (end == -1) {
                    end = json.length();
                }
                String val = json.substring(scoreIndex + 8, end).trim();
                try {
                    int enemyScore = Integer.parseInt(val);
                    enemyBoard.setScore(enemyScore);   // ★ 상대 점수 갱신
                } catch (Exception ignored) {
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
         }
    }

    private void sendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) {
            return;
        }

        NetworkManager.getInstance().send("CHAT:" + msg);
        chatArea.append("ME: " + msg + "\n");
        chatInput.setText("");

            
        // ★ 게임 패널로 포커스 되돌리기
        requestFocusInWindow();
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
        List<ShapeType[]> pendingPatterns = board.getPendingAttackPattern();
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

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

/**
 * 대전 모드 게임 패널 - 두 개의 보드를 좌우로 배치
 */
public class BattleGamePanel extends JPanel {
    private final Board board1;  // Player 1 보드
    private final Board board2;  // Player 2 보드
    
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
    private int[] flashingRows1 = null;
    private int[] flashingRows2 = null;
    private long flashUntil1 = 0;
    private long flashUntil2 = 0;
    private static final long FLASH_MS = 150;
    
    // 승자 표시
    private String winner = null;
    
    // 블록 배치 감지용
    private Tetromino lastCurrent1 = null;
    private Tetromino lastCurrent2 = null;
    
    public BattleGamePanel(Difficulty difficulty) {
        this(difficulty, false, false);
    }
    
    public BattleGamePanel(Difficulty difficulty, boolean isItemMode) {
        this(difficulty, isItemMode, false);
    }
    
    public BattleGamePanel(Difficulty difficulty, boolean isItemMode, boolean isTimeAttack) {
        this.isTimeAttack = isTimeAttack;
        this.board1 = new Board(difficulty, isItemMode);
        this.board2 = new Board(difficulty, isItemMode);
        this.gameStartTime = System.currentTimeMillis();

        // 메인 메뉴 음악 끄고 게임 음악 켜기 (설정된 볼륨으로)
        BackgroundMusicPlayer.getInstance().stop();
        BackgroundMusicPlayer.getInstance().play("/music/InGameBGM.wav", Settings.getGameMusicVolume());
        
        // Settings에서 셀 크기 및 화면 크기 계산
        this.CELL = Settings.getCellSize();
        this.BOARD_W = Board.COLS * CELL;
        this.BOARD_H = Board.ROWS * CELL;
        this.SIDE_W = (int)(200 * Settings.getScaleFactor());
        this.GAP = (int)(50 * Settings.getScaleFactor());
        
        // 전체 패널 크기: SIDE + BOARD + GAP + BOARD + SIDE
        int totalWidth = SIDE_W + BOARD_W + GAP + BOARD_W + SIDE_W;
        setPreferredSize(new Dimension(totalWidth, BOARD_H));
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
                // Player 1 업데이트
                if (!board1.isGameOver()) {
                    checkBlockPlacement(board1, board2, 1);
                    board1.moveDown();   // DEBUG: 자동 낙하 일시 중지
                    checkFlashing(board1, 1);
                }
                
                // Player 2 업데이트
                if (!board2.isGameOver()) {
                    checkBlockPlacement(board2, board1, 2);
                    board2.moveDown();   // DEBUG: 자동 낙하 일시 중지
                    checkFlashing(board2, 2);
                }
                
                // 승자 확인
                checkWinner();
                repaint();
            }
        });
        timer.start();
        
        // 초기 현재 블록 저장
        lastCurrent1 = board1.getCurrent();
        lastCurrent2 = board2.getCurrent();
    }
    
    private void checkBlockPlacement(Board myBoard, Board opponentBoard, int playerNum) {
        Tetromino current = myBoard.getCurrent();
        Tetromino lastCurrent = (playerNum == 1) ? lastCurrent1 : lastCurrent2;
        
        // 새로운 블록이 생성되었을 때 (블록이 배치된 직후)
        if (current != lastCurrent && lastCurrent != null) {
            // 대기 중인 공격 줄을 적용
            myBoard.applyPendingAttackLines();
        }
        
        // 현재 블록 저장
        if (playerNum == 1) {
            lastCurrent1 = current;
        } else {
            lastCurrent2 = current;
        }
    }
    
    private void handleKeyPress(KeyEvent e) {
        int code = e.getKeyCode();
        
        // 일시정지
        if (code == KeyEvent.VK_P) {
            togglePause();
            return;
        }
        
        // 게임 종료 시 키 입력 무시
        if (winner != null || paused) return;
        
        // Player 1 조작 (Settings에서 가져온 키)
        if (!board1.isGameOver()) {
            if (code == Settings.getKeyLeft(Settings.Player.P1)) {
                board1.moveLeft();
                checkFlashing(board1, 1);
            } else if (code == Settings.getKeyRight(Settings.Player.P1)) {
                board1.moveRight();
                checkFlashing(board1, 1);
            } else if (code == Settings.getKeyDown(Settings.Player.P1)) {
                board1.moveDown();
                checkFlashing(board1, 1);
            } else if (code == Settings.getKeyRotate(Settings.Player.P1)) {
                board1.rotate();
                checkFlashing(board1, 1);
            } else if (code == Settings.getKeyHardDrop(Settings.Player.P1)) {
                board1.hardDrop();
                checkFlashing(board1, 1);
            }
        }
        
        // Player 2 조작 (Settings에서 가져온 키)
        if (!board2.isGameOver()) {
            if (code == Settings.getKeyLeft(Settings.Player.P2)) {
                board2.moveLeft();
                checkFlashing(board2, 2);
            } else if (code == Settings.getKeyRight(Settings.Player.P2)) {
                board2.moveRight();
                checkFlashing(board2, 2);
            } else if (code == Settings.getKeyDown(Settings.Player.P2)) {
                board2.moveDown();
                checkFlashing(board2, 2);
            } else if (code == Settings.getKeyRotate(Settings.Player.P2)) {
                board2.rotate();
                checkFlashing(board2, 2);
            } else if (code == Settings.getKeyHardDrop(Settings.Player.P2)) {
                board2.hardDrop();
                checkFlashing(board2, 2);
            }
        }
        
        repaint();
    }
    
    private void checkFlashing(Board board, int playerNum) {
        int[] rows = board.pollClearingRows();
        if (rows != null && rows.length > 0) {
            // 2줄 이상 삭제 시 상대방에게 공격 (삭제한 줄 수 그대로)
            if (rows.length >= 2) {
                Board opponent = (playerNum == 1) ? board2 : board1;
                // 삭제될 줄의 패턴을 가져와서 상대방에게 전달
                java.util.List<ShapeType[]> attackPatterns = board.getAttackPattern(rows);
                opponent.addPendingAttackLines(attackPatterns);
            }
            
            if (playerNum == 1) {
                flashingRows1 = rows;
                flashUntil1 = System.currentTimeMillis() + FLASH_MS;
                
                Timer flashTimer = new Timer(16, ev -> {
                    if (flashingRows1 == null) {
                        ((Timer) ev.getSource()).stop();
                        return;
                    }
                    if (System.currentTimeMillis() >= flashUntil1) {
                        board1.clearRows(flashingRows1);
                        flashingRows1 = null;
                        ((Timer) ev.getSource()).stop();
                    }
                    repaint();
                });
                flashTimer.setRepeats(true);
                flashTimer.start();
            } else {
                flashingRows2 = rows;
                flashUntil2 = System.currentTimeMillis() + FLASH_MS;
                
                Timer flashTimer = new Timer(16, ev -> {
                    if (flashingRows2 == null) {
                        ((Timer) ev.getSource()).stop();
                        return;
                    }
                    if (System.currentTimeMillis() >= flashUntil2) {
                        board2.clearRows(flashingRows2);
                        flashingRows2 = null;
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
        
        switch (choice) {
            case 0 -> {
                // Resume
                paused = false;
                requestFocusInWindow();
            }
            case 1 -> {
                // Quit to Menu
                timer.stop();
                returnToMenu();
            }
            default -> {
                // 창을 닫은 경우
                paused = false;
                requestFocusInWindow();
            }
        }
    }
    
    private void checkWinner() {
        boolean p1Over = board1.isGameOver();
        boolean p2Over = board2.isGameOver();
        
        // 시간제한 모드: 시간이 다 되면 점수로 승부 판정
        if (isTimeAttack && winner == null) {
            long elapsedTime = System.currentTimeMillis() - gameStartTime - pausedTime;
            if (elapsedTime >= timeLimit) {
                int score1 = board1.getScore();
                int score2 = board2.getScore();
                
                if (score1 > score2) {
                    winner = "PLAYER 1";
                } else if (score2 > score1) {
                    winner = "PLAYER 2";
                } else {
                    winner = "DRAW";
                }
                timer.stop();
                showGameOver();
                return;
            }
        }
        
        if (p1Over && p2Over) {
            winner = "DRAW";
            timer.stop();
            showGameOver();
        } else if (p1Over) {
            winner = "PLAYER 2";
            timer.stop();
            showGameOver();
        } else if (p2Over) {
            winner = "PLAYER 1";
            timer.stop();
            showGameOver();
        }
    }
    
    private void showGameOver() {
        SwingUtilities.invokeLater(() -> {
            String message = winner.equals("DRAW") ? 
                "It's a DRAW!" : 
                winner + " WINS!";
            
            String[] options = {"Return to Menu", "Restart"};
            int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == 0) {
                returnToMenu();
            } else if (choice == 1) {
                restartGame();
            }
        });
    }
    
    private void restartGame() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            // 같은 난이도로 새 대전 게임 시작
            Difficulty currentDifficulty = board1.getDifficulty();
            javax.swing.JFrame frame = new javax.swing.JFrame("Tetris Battle Mode");
            BattleGamePanel newPanel = new BattleGamePanel(currentDifficulty);
            frame.add(newPanel);
            frame.pack();
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            newPanel.requestFocusInWindow();
        });
    }
    
    private void returnToMenu() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
        SwingUtilities.invokeLater(() -> {
            Mainmenu menu = new Mainmenu();
            menu.setVisible(true);
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        int startX1 = SIDE_W;
        int startX2 = SIDE_W + BOARD_W + GAP;
        
        // Player 1 사이드바
        drawSidebar(g2, board1, 0, "PLAYER 1");
        
        // Player 1 보드 영역
        g2.translate(startX1, 0);
        drawBoard(g2, board1, flashingRows1);
        drawCurrent(g2, board1);
        drawGrid(g2);
        g2.translate(-startX1, 0);
        
        // 보드 사이 간격 (사이드바와 같은 색)
        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(SIDE_W + BOARD_W, 0, GAP, BOARD_H);
        
        // Player 2 보드 영역
        g2.translate(startX2, 0);
        drawBoard(g2, board2, flashingRows2);
        drawCurrent(g2, board2);
        drawGrid(g2);
        g2.translate(-startX2, 0);
        
        // Player 2 사이드바
        drawSidebar(g2, board2, startX2 + BOARD_W, "PLAYER 2");
        
        // 일시정지 표시
        if (paused) {
            drawPaused(g2);
        }
        
        // 승자 표시
        if (winner != null) {
            drawWinner(g2);
        }
        
        g2.dispose();
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
        if (cur == null || board.isGameOver()) return;
        
        // 현재 아이템 블록인지 확인
        items.ItemBlock currentItem = board.getCurrentItemBlock();
        
        if (currentItem != null) {
            Position[] blocks = cur.getBlocks();
            
            // 모든 아이템 블록은 흰색으로 표시
            Color blockColor = Color.WHITE;
            
            for (int i = 0; i < blocks.length; i++) {
                Position p = blocks[i];
                int px = cur.getX() + p.x;
                int py = cur.getY() + p.y;
                if (px >= 0 && px < Board.COLS && py >= 0 && py < Board.ROWS) {
                    // 흰색으로 셀 채우기
                    fillCell(g, px, py, blockColor);
                    
                    // 아이템 심볼 표시 (검정색 글자)
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
            // 일반 블록으로 렌더링
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
        for (int x = 0; x <= Board.COLS; x++)
            g.drawLine(x * CELL, 0, x * CELL, BOARD_H);
        for (int y = 0; y <= Board.ROWS; y++)
            g.drawLine(0, y * CELL, BOARD_W, y * CELL);
    }
    
    private void drawSidebar(Graphics2D g, Board board, int sx, String playerName) {
        g.setColor(new Color(20, 20, 20));
        g.fillRect(sx, 0, SIDE_W, BOARD_H);
        
        g.setColor(Color.WHITE);
        int baseFontSize = Settings.getBaseFontSize();
        
        // Player name
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.89)));
        g.drawString(playerName, sx + 20, 25);
        
        // NEXT
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("NEXT", sx + 20, 60);
        drawNextPreview(g, sx + 20, 80, board);
        
        // ITEM (아이템 모드일 때만 표시)
        int yOffset = 0;
        if (board.isItemMode()) {
            items.ItemBlock nextItem = board.getNextItemBlock();
            if (nextItem != null) {
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.78)));
                g.drawString("ITEM", sx + 20, 150);
                drawItemPreview(g, sx + 20, 165, nextItem);
                yOffset = 50;
            }
        }
        
        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("SCORE", sx + 20, 190 + yOffset);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)baseFontSize));
        g.drawString(String.valueOf(board.getScore()), sx + 20, 218 + yOffset);
        
        // TIME (시간제한 모드일 때만 표시)
        if (isTimeAttack) {
            long elapsedTime = System.currentTimeMillis() - gameStartTime - pausedTime;
            long remainingTime = Math.max(0, timeLimit - elapsedTime);
            int seconds = (int)(remainingTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            
            g.setColor(remainingTime < 30000 ? Color.RED : Color.YELLOW);
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
            g.drawString("TIME", sx + 20, 240 + yOffset);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)baseFontSize));
            g.drawString(String.format("%d:%02d", minutes, seconds), sx + 20, 268 + yOffset);
            yOffset += 50;
        }
        
        // 대기 중인 공격 줄 표시 (미니 보드 형태 - 항상 10줄 표시)
        List<ShapeType[]> pendingPatterns = board.getPendingAttackPattern();
        int pendingLines = pendingPatterns.size();
        int displayLines = 10; // 항상 10줄
        int miniCellSize = 8;
        int miniCols = 10;
        int startX = sx + 20;
        int startY = 250 + yOffset;
        
        // 배경
        g.setColor(new Color(40, 40, 40));
        g.fillRect(startX - 2, startY - 2, miniCols * miniCellSize + 4, displayLines * miniCellSize + 4);
        
        // 미니 블록들 그리기
        for (int row = 0; row < displayLines; row++) {
            for (int col = 0; col < miniCols; col++) {
                int x = startX + col * miniCellSize;
                int y = startY + row * miniCellSize;
                
                // 아래쪽부터 채우기 위해 역순으로 체크
                int reverseRow = displayLines - 1 - row;
                if (reverseRow < pendingLines) {
                    // 공격 줄이 있는 부분: 패턴에 따라 그리기
                    ShapeType[] pattern = pendingPatterns.get(reverseRow);
                    if (pattern[col] != null) {
                        // 블록이 있는 칸: 회색으로 채우기
                        g.setColor(new Color(128, 128, 128));
                        g.fillRect(x, y, miniCellSize, miniCellSize);
                        g.setColor(new Color(80, 80, 80));
                        g.drawRect(x, y, miniCellSize, miniCellSize);
                    }
                } else {
                    // 공격이 없는 부분: 빈 그리드만 표시
                    g.setColor(new Color(60, 60, 60));
                    g.drawRect(x, y, miniCellSize, miniCellSize);
                }
            }
        }
        
        // 10줄 초과이면 "+N" 표시
        if (pendingLines > 10) {
            g.setColor(Color.RED);
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.75)));
            g.drawString("+" + (pendingLines - 10), startX + miniCols * miniCellSize + 5, startY + displayLines * miniCellSize / 2);
        }
    }
    
    private void drawNextPreview(Graphics2D g, int px, int py, Board board) {
        // 배경
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
        // 아이템 블록의 기본 형태를 가져옴
        ShapeType baseShape = item.getBaseShape();
        Position[] offs = baseShape.getOffsets(0);
        
        int minx = 99, miny = 99, maxx = -99, maxy = -99;
        for (Position p : offs) {
            minx = Math.min(minx, p.x); maxx = Math.max(maxx, p.x);
            miny = Math.min(miny, p.y); maxy = Math.max(maxy, p.y);
        }
        
        int cell = CELL / 2;
        int previewSize = 80;
        int padding = 10;
        int w = (maxx - minx + 1) * cell;
        int h = (maxy - miny + 1) * cell;
        int cx = px + (previewSize - w) / 2 - padding;
        int cy = py + (previewSize - h) / 2 - padding;

        // 배경
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - padding, py - padding, previewSize, previewSize, 8, 8);

        // 아이템 블록은 흰색 배경에 검은색 문자로 표시
        for (int i = 0; i < offs.length; i++) {
            Position p = offs[i];
            int cxp = cx + (p.x - minx) * cell;
            int cyp = cy + (p.y - miny) * cell;
            
            // 흰색 배경으로 표시
            g.setColor(Color.WHITE);
            g.fillRect(cxp, cyp, cell, cell);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(cxp, cyp, cell, cell);
            
            // 아이템 블록 심볼 표시
            char symbol;
            if (item instanceof items.SlowBlock slowBlock) {
                symbol = slowBlock.getBlockSymbol(i);
            } else if (item instanceof items.LineBlock lineBlock) {
                symbol = lineBlock.getBlockSymbol(i);
            } else {
                symbol = item.getSymbol();
            }
            
            // 아이템 심볼 표시
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
        g.setColor(Color.YELLOW);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 56f));
        String winText = winner.equals("DRAW") ? "DRAW!" : winner + " WINS!";
        int textWidth = g.getFontMetrics().stringWidth(winText);
        g.drawString(winText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }
    
    private void fillCell(Graphics2D g, int x, int y, Color color) {
        int px = x * CELL;
        int py = y * CELL;
        g.setColor(color);
        g.fillRect(px, py, CELL, CELL);
        g.setColor(color.darker());
        g.drawRect(px, py, CELL, CELL);
    }
}

package com.team.tetris.screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.team.tetris.core.Board;
import com.team.tetris.core.Position;
import com.team.tetris.core.ShapeType;
import com.team.tetris.core.Tetromino;
import com.team.tetris.ranking.RankingManager;

public class GamePanel extends JPanel {
    private final boolean isItemMode;
    private final Board board;
    private final Timer timer;

    private static final int CELL = 30;
    private static final int BOARD_W = Board.COLS * CELL;
    private static final int BOARD_H = Board.ROWS * CELL;
    private static final int SIDE_W  = 200;

    // 일시정지/속도
    private boolean paused = false;
    private final int baseDelay;         // 난이도별 시작 속도
    private int currentDelay;      // 현재 타이머 딜레이(ms)
    private int minDelay = 150;    // 너무 빨라지지 않도록 하한
    private int stepPerLine = 50;  // 줄 1개 삭제 시 50ms 가속

    public GamePanel(Board board, boolean isItemMode) {
        this.board = board;
        this.isItemMode = isItemMode;

        setPreferredSize(new Dimension(BOARD_W + SIDE_W, BOARD_H));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                // P: 일시정지 / 재개 + 메뉴
                if (code == KeyEvent.VK_P) {
                    togglePauseAndMenu();
                    repaint();
                    return;
                }

                // 진행 중 ESC: 강제 종료 옵션
                if (!paused && !board.isGameOver() && code == KeyEvent.VK_ESCAPE) {
                    onForceQuit();
                    return;
                }

                if (board.isGameOver() || paused) return;

                // 기본 조작키
                switch (code) {
                    case KeyEvent.VK_LEFT  -> board.moveLeft();
                    case KeyEvent.VK_RIGHT -> board.moveRight();
                    case KeyEvent.VK_DOWN  -> board.moveDown();
                    case KeyEvent.VK_UP    -> board.rotate();
                    case KeyEvent.VK_SPACE -> board.hardDrop();
                }

                // 속도 반영(줄 삭제 누적에 따라)
                updateSpeedByClears();
                repaint();
            }
        });

        // 난이도별 시작 속도
        baseDelay = switch (board.getDifficulty()) {
            case EASY   -> 1000;
            case NORMAL -> 800;
            case HARD   -> 600;
        };
        currentDelay = baseDelay;

        timer = new Timer(currentDelay, e -> {
            if (!board.isGameOver() && !paused) {
                board.moveDown();
                updateSpeedByClears(); // 자동 낙하 후에도 갱신
                repaint();
            } else {
                if (board.isGameOver()) {
                    ((Timer) e.getSource()).stop(); // 게임오버 시 완전 정지
                    handleGameOver();  // 게임오버 처리 호출
                }
                repaint();
            }
        });
        timer.setInitialDelay(currentDelay);
        timer.start();
    }

    private void handleGameOver() {
    if (board.isGameOver()) {
        timer.stop();
        int finalScore = board.getScore();
        
        RankingManager manager = isItemMode ? 
            RankingManager.getInstance("item_rankings.dat") :
            RankingManager.getInstance();
            
        // 게임오버 시 창 닫고 GameOverScreen 표시
        SwingUtilities.invokeLater(() -> {
            java.awt.Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();  // 현재 게임 창 닫기
                if (manager.getRankings().size() < 10 || manager.shouldInputName(finalScore)) {
                    new NameInputScreen(finalScore, isItemMode).setVisible(true);
                } else {
                    new GameOverScreen(finalScore).setVisible(true);
                }
            }
        });
    }
}

    // ==== 속도 가속 (줄 삭제 누적 기반) ====
    private void updateSpeedByClears() {
        int cleared = board.getTotalLinesCleared();
        int target = Math.max(minDelay, baseDelay - stepPerLine * cleared);
        if (target != currentDelay) {
            currentDelay = target;
            timer.setDelay(currentDelay);
            timer.setInitialDelay(currentDelay);
        }
    }

    // ==== 일시정지/메뉴 ====
    private void togglePauseAndMenu() {
        paused = !paused;
        if (paused) {
            Object[] options = {"게임 재개", "게임 재시작", "메인 메뉴", "프로그램 종료"};
            int sel = JOptionPane.showOptionDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "일시정지",
                    "Pause",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]
            );
            switch (sel) {
                case 0 -> { // 재개
                    paused = false;
                }
                case 1 -> { // 재시작
                    board.reset();
                    paused = false;
                    currentDelay = baseDelay;
                    timer.setDelay(currentDelay);
                    timer.setInitialDelay(currentDelay);
                }
                case 2 -> { // 메인 메뉴
                    closeGameOnly();
                    SwingUtilities.invokeLater(() -> {
                        new Mainmenu().setVisible(true);  // 메인메뉴 화면 표시
                    });
                }
                case 3 -> { // 종료
                    System.exit(0);
                }
                default -> { /* 닫기/취소 시 아무것도 안 함 */ }
            }

        }
    }

    private void onForceQuit() {
        int result = JOptionPane.showOptionDialog(
            SwingUtilities.getWindowAncestor(this),
            "강제 종료할까요?",
            "Quit",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new Object[]{"예", "아니오"},
            "아니오"
        );
        if (result == 0) {  // "예" 선택
        System.exit(0);
    }
        
    }

     private void closeGameOnly() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    // ==== 렌더링 ====
    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        drawBoard(g2);
        drawCurrent(g2);
        drawGrid(g2);
        drawSidebar(g2);

        if (paused) drawPaused(g2);

        g2.dispose();
    }

    private void drawBoard(Graphics2D g) {
        ShapeType[][] grid = board.getGrid();
        for (int y = 0; y < Board.ROWS; y++) {
            for (int x = 0; x < Board.COLS; x++) {
                ShapeType s = grid[y][x];
                if (s != null) fillCell(g, x, y, s.getColor());
            }
        }
    }

    private void drawCurrent(Graphics2D g) {
        Tetromino cur = board.getCurrent();
        if (cur == null) return;
        Color c = cur.getShape().getColor();
        for (Position p : cur.getBlocks()) {
            int px = cur.getX() + p.x;
            int py = cur.getY() + p.y;
            if (px >= 0 && px < Board.COLS && py >= 0 && py < Board.ROWS) {
                fillCell(g, px, py, c);
            }
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(40, 40, 40));
        for (int x = 0; x <= Board.COLS; x++)
            g.drawLine(x * CELL, 0, x * CELL, BOARD_H);
        for (int y = 0; y <= Board.ROWS; y++)
            g.drawLine(0, y * CELL, BOARD_W, y * CELL);
    }

    private void drawSidebar(Graphics2D g) {
        int sx = BOARD_W;
        g.setColor(new Color(20, 20, 20));
        g.fillRect(sx, 0, SIDE_W, BOARD_H);

        g.setColor(Color.WHITE);

        // SCORE
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("SCORE", sx + 20, 40);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 18f));
        g.drawString(String.valueOf(board.getScore()), sx + 20, 68);

        // DIFFICULTY
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("DIFFICULTY", sx + 20, 110);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 16f));
        String diff = board.getDifficulty().name().toLowerCase();
        diff = Character.toUpperCase(diff.charAt(0)) + diff.substring(1);
        g.drawString(diff, sx + 20, 134);

        // LEVEL (= 누적 삭제 줄 수)
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("LEVEL", sx + 20, 170);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 16f));
        g.drawString(String.valueOf(board.getTotalLinesCleared()), sx + 20, 194);

        // SPEED (현재 ms)
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("SPEED", sx + 20, 230);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, 16f));
        g.drawString(currentDelay + " ms", sx + 20, 254);

        // NEXT
        g.setFont(g.getFont().deriveFont(Font.BOLD, 18f));
        g.drawString("NEXT", sx + 20, 290);
        drawNextPreview(g, sx + 20, 310);
    }

    private void drawNextPreview(Graphics2D g, int px, int py) {
        ShapeType n = board.getNextShape();
        if (n == null) return;
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - 10, py - 10, 150, 150, 12, 12);

        Position[] offs = n.getOffsets(0);
        int minx = 99, miny = 99, maxx = -99, maxy = -99;
        for (Position p : offs) {
            minx = Math.min(minx, p.x); maxx = Math.max(maxx, p.x);
            miny = Math.min(miny, p.y); maxy = Math.max(maxy, p.y);
        }
        int cell = CELL / 2;
        int w = (maxx - minx + 1) * cell;
        int h = (maxy - miny + 1) * cell;
        int cx = px + (150 - w) / 2;
        int cy = py + (150 - h) / 2;

        for (Position p : offs) {
            int cxp = cx + (p.x - minx) * cell;
            int cyp = cy + (p.y - miny) * cell;
            g.setColor(n.getColor());
            g.fillRect(cxp, cyp, cell, cell);
            g.setColor(n.getColor().darker());
            g.drawRect(cxp, cyp, cell, cell);
        }
    }

    private void drawPaused(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, BOARD_W + SIDE_W, BOARD_H);
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 30f));
        g.drawString("PAUSED (P)", 50, BOARD_H / 2 - 10);
    }

    private void fillCell(Graphics2D g, int x, int y, Color color) {
        int px = x * CELL, py = y * CELL;
        g.setColor(color);
        g.fillRect(px, py, CELL, CELL);
        g.setColor(color.darker());
        g.drawRect(px, py, CELL, CELL);
    }
}

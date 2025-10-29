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
import com.team.tetris.core.Settings;
import com.team.tetris.core.ShapeType;
import com.team.tetris.core.Tetromino;
import com.team.tetris.ranking.RankingManager;

public class GamePanel extends JPanel {
    private final boolean isItemMode;
    private final Board board;
    private final Timer timer;

    private final int CELL;
    private final int BOARD_W;
    private final int BOARD_H;
    private final int SIDE_W;

    // 일시정지/속도
    private boolean paused = false;
    private final int baseDelay;         // 난이도별 시작 속도
    private int currentDelay;      // 현재 타이머 딜레이(ms)
    private int minDelay = 150;    // 너무 빨라지지 않도록 하한
    private int stepPerLine = 50;  // 줄 1개 삭제 시 50ms 가속
    
    // 슬로우 효과 관련
    private int normalDelay;       // 슬로우 효과가 없을 때의 정상 딜레이
    private final Timer slowEffectTimer; // 슬로우 효과 타이머 (0.1초마다 업데이트)

    public GamePanel(Board board, boolean isItemMode) {
        this.board = board;
        this.isItemMode = isItemMode;

        // Settings에서 셀 크기 및 화면 크기 계산
        this.CELL = Settings.getCellSize();
        this.BOARD_W = Board.COLS * CELL;
        this.BOARD_H = Board.ROWS * CELL;
        this.SIDE_W = (int)(200 * Settings.getScaleFactor());

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

                // 기본 조작키 - Settings에서 가져옴
                if (code == Settings.getKeyLeft()) {
                    board.moveLeft();
                } else if (code == Settings.getKeyRight()) {
                    board.moveRight();
                } else if (code == Settings.getKeyDown()) {
                    board.moveDown();
                } else if (code == Settings.getKeyRotate()) {
                    board.rotate();
                } else if (code == Settings.getKeyHardDrop()) {
                    board.hardDrop();
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
                board.updateSlowEffect(); // 슬로우 효과 상태 업데이트
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
        
        // 슬로우 효과 타이머 초기화 (100ms마다 실행)
        this.slowEffectTimer = new Timer(100, e -> {
            if (board.isSlowEffectActive()) {
                repaint(); // 슬로우 효과가 활성화되어 있을 때만 다시 그리기
            }
        });
        slowEffectTimer.start();
    }

    private void handleGameOver() {
    if (board.isGameOver()) {
        timer.stop();
        slowEffectTimer.stop(); // 슬로우 효과 타이머도 정지
        int finalScore = board.getScore();
        
        RankingManager manager = isItemMode ? 
            RankingManager.getInstance("item_rankings.dat") :
            RankingManager.getInstance();
            
        // 게임오버 시 창 닫고 GameOverScreen 표시
        SwingUtilities.invokeLater(() -> {
            java.awt.Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
                if (manager.getRankings().size() < 10 || manager.shouldInputName(finalScore)) {
                    new NameInputScreen(finalScore, board.getDifficulty(), isItemMode).setVisible(true);
                } else {
                    new GameOverScreen(finalScore, board.getDifficulty(), isItemMode).setVisible(true);
                }
            }
        });
    }
}

    // ==== 속도 가속 (줄 삭제 누적 기반) ====
    private void updateSpeedByClears() {
        int cleared = board.getTotalLinesCleared();
        normalDelay = Math.max(minDelay, baseDelay - stepPerLine * cleared);
        
        // 슬로우 효과가 활성화되어 있으면 속도를 0.5배로 감소 (딜레이 2배)
        int target;
        if (board.isSlowEffectActive()) {
            target = normalDelay * 2;
        } else {
            target = normalDelay;
        }
        
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
                    normalDelay = baseDelay;
                    timer.setDelay(currentDelay);
                    timer.setInitialDelay(currentDelay);
                    if (!slowEffectTimer.isRunning()) {
                        slowEffectTimer.start(); // 슬로우 효과 타이머 재시작
                    }
                }
                case 2 -> { // 메인 메뉴
                    timer.stop();
                    slowEffectTimer.stop(); // 슬로우 효과 타이머 정지
                    closeGameOnly();
                    SwingUtilities.invokeLater(() -> {
                        new Mainmenu().setVisible(true);  // 메인메뉴 화면 표시
                    });
                }
                case 3 -> { // 종료
                    timer.stop();
                    slowEffectTimer.stop();
                    System.exit(0);
                }
                default -> { /* 닫기/취소 시 아무것도 안 함 */ }
            }

        }
    }

    private void onForceQuit() {
        int result = JOptionPane.showOptionDialog(
            SwingUtilities.getWindowAncestor(this),
            "Quit?",
            "Quit",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new Object[]{"Yes", "No"},
            "No"
        );
        if (result == 0) {  // "예" 선택
        timer.stop();
        slowEffectTimer.stop();
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
        
        // 현재 아이템 블록인지 확인
        com.team.tetris.items.ItemBlock currentItem = board.getCurrentItemBlock();
        
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
                    if (currentItem instanceof com.team.tetris.items.SlowBlock slowBlock) {
                        symbol = slowBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof com.team.tetris.items.LineBlock lineBlock) {
                        symbol = lineBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof com.team.tetris.items.BombBlock bombBlock) {
                        symbol = bombBlock.getBlockSymbol(i);
                    } else if (currentItem instanceof com.team.tetris.items.WeightBlock weightBlock) {
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
        
        int baseFontSize = Settings.getBaseFontSize();

        // SCORE
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("SCORE", sx + 20, 40);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)baseFontSize));
        g.drawString(String.valueOf(board.getScore()), sx + 20, 68);

        // DIFFICULTY
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("DIFFICULTY", sx + 20, 110);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        String diff = board.getDifficulty().name().toLowerCase();
        diff = Character.toUpperCase(diff.charAt(0)) + diff.substring(1);
        g.drawString(diff, sx + 20, 134);

        // LEVEL (= 누적 삭제 줄 수)
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("LEVEL", sx + 20, 170);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        g.drawString(String.valueOf(board.getTotalLinesCleared()), sx + 20, 194);

        // SPEED (현재 ms)
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("SPEED", sx + 20, 230);
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        g.drawString(currentDelay + " ms", sx + 20, 254);

        // NEXT
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        String nextLabel = "NEXT";
        if (board.getNextItemBlock() != null) {
            nextLabel = "NEXT (ITEM)";
        }
        g.drawString(nextLabel, sx + 20, 290);
        drawNextPreview(g, sx + 20, 310);
        
        // 아이템 모드에서 아이템 정보 표시
        if (isItemMode && board.getItemManager() != null) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            g.drawString("ITEMS PROGRESS", sx + 20, 480);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 12f));
            int itemProgress = board.getItemManager().getTotalLinesCleared() % 2;
            g.drawString(itemProgress + "/2 lines", sx + 20, 500);
        }
        
        // 슬로우 효과 타이머 표시
        if (board.isSlowEffectActive()) {
            long remainingTime = board.getSlowEffectRemainingTime();
            double seconds = remainingTime / 1000.0;
            
            g.setFont(g.getFont().deriveFont(Font.BOLD, 16f));
            g.setColor(new Color(173, 216, 230)); // 슬로우 블록과 같은 색상
            g.drawString("SLOW EFFECT", sx + 20, 540);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 14f));
            g.drawString(String.format("%.1f sec", seconds), sx + 20, 560);
            g.setColor(Color.WHITE); // 색상 원복
        }
    }

    private void drawNextPreview(Graphics2D g, int px, int py) {
        // 배경
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - 10, py - 10, 150, 150, 12, 12);

        // 아이템 블록이 있는지 먼저 확인
        com.team.tetris.items.ItemBlock nextItem = board.getNextItemBlock();
        if (nextItem != null) {
            // 아이템 블록 표시
            drawItemPreview(g, px, py, nextItem);
        } else {
            // 일반 블록 표시
            ShapeType n = board.getNextShape();
            if (n != null) {
                drawShapePreview(g, px, py, n);
            }
        }
    }

    private void drawItemPreview(Graphics2D g, int px, int py, com.team.tetris.items.ItemBlock item) {
        // 아이템 블록의 기본 형태를 가져옴
        ShapeType baseShape = item.getBaseShape();
        Position[] offs = baseShape.getOffsets(0);
        
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

        // 아이템 블록은 특별한 색상으로 표시
        for (int i = 0; i < offs.length; i++) {
            Position p = offs[i];
            int cxp = cx + (p.x - minx) * cell;
            int cyp = cy + (p.y - miny) * cell;
            g.setColor(item.getColor());  // 아이템 전용 색상 사용
            g.fillRect(cxp, cyp, cell, cell);
            g.setColor(item.getColor().darker());
            g.drawRect(cxp, cyp, cell, cell);
            
            // 아이템 블록임을 나타내는 심볼 표시 (SlowBlock인 경우 각 블록마다 다른 심볼)
            char symbol;
            if (item instanceof com.team.tetris.items.SlowBlock slowBlock) {
                symbol = slowBlock.getBlockSymbol(i);
            } else {
                symbol = item.getSymbol();
            }
            
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(Font.BOLD, cell * 0.8f));
            int symbolX = cxp + cell / 4;
            int symbolY = cyp + cell * 3 / 4;
            g.drawString(String.valueOf(symbol), symbolX, symbolY);
        }
    }

    private void drawShapePreview(Graphics2D g, int px, int py, ShapeType n) {
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

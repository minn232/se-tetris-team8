package screens;

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

import core.Board;
import core.Position;
import core.Settings;
import core.ShapeType;
import core.Tetromino;
import ranking.RankingManager;

public class GamePanel extends JPanel {
    private final boolean isItemMode;
    private final boolean isTimeAttackMode;
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
    
    // 플래시 애니메이션(컬럼 전체 하얗게 번쩍)용
    private int[] flashingRows = null;
    private long flashUntil = 0;
    private static final long FLASH_MS = 150;

    public GamePanel(Board board, boolean isItemMode, boolean isTimeAttackMode) {
        this.board = board;
        this.isItemMode = isItemMode;
        this.isTimeAttackMode = isTimeAttackMode;
        
        // 메인 메뉴 음악 끄고 게임 음악 켜기 (설정된 볼륨으로)
        BackgroundMusicPlayer.getInstance().stop();
        BackgroundMusicPlayer.getInstance().play("/music/InGameBGM.wav", Settings.getGameMusicVolume());

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

                // 기본 조작키 - Settings에서 가져옴 (WASD + 방향키 지원)
                if (code == Settings.getKeyLeft() || code == Settings.getKeyLeft(Settings.Player.P2)) {
                    board.moveLeft();
                } else if (code == Settings.getKeyRight() || code == Settings.getKeyRight(Settings.Player.P2)) {
                    board.moveRight();
                } else if (code == Settings.getKeyDown() || code == Settings.getKeyDown(Settings.Player.P2)) {
                    board.moveDown();
                } else if (code == Settings.getKeyRotate() || code == Settings.getKeyRotate(Settings.Player.P2)) {
                    board.rotate();
                } else if (code == Settings.getKeyHardDrop() || code == Settings.getKeyHardDrop(Settings.Player.P2)) {
                    board.hardDrop();
                }

                // 키 입력 직후에도 줄 삭제 애니메이션 즉시 시작
                int[] rows = board.pollClearingRows();
                if (rows != null && rows.length > 0) {
                    flashingRows = rows;
                    flashUntil = System.currentTimeMillis() + FLASH_MS;
                    repaint(); // 즉시 화면 갱신하여 플래시 표시

                    Timer flashTimer = new Timer(16, ev -> {
                        if (flashingRows == null) {
                            ((Timer) ev.getSource()).stop();
                            return;
                        }
                        if (System.currentTimeMillis() >= flashUntil) {
                            board.clearRows(flashingRows);
                            flashingRows = null;
                            updateSpeedByClears();
                            ((Timer) ev.getSource()).stop();
                        }
                        repaint();
                    });
                    flashTimer.setRepeats(true);
                    flashTimer.start();
                    return; // 플래시 시작했으면 여기서 종료
                }

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

                // (3) 삭제 예약 확인 - moveDown() 직후 즉시 확인
                int[] rows = board.pollClearingRows();
                if (rows != null && rows.length > 0) {
                    flashingRows = rows;
                    flashUntil = System.currentTimeMillis() + FLASH_MS;

                    // 🔥 고속 플래시 타이머 (16ms 간격 = 약 60FPS)
                    Timer flashTimer = new Timer(16, ev -> {
                        if (flashingRows == null) {
                            ((Timer) ev.getSource()).stop();
                            return;
                        }
                        if (System.currentTimeMillis() >= flashUntil) {
                            board.clearRows(flashingRows);
                            flashingRows = null;
                            updateSpeedByClears();
                            ((Timer) ev.getSource()).stop();
                        }
                        repaint();
                    });
                    flashTimer.setRepeats(true);
                    flashTimer.start();
                    repaint(); // 즉시 화면 갱신하여 플래시 시작
                    return; // 플래시 시작했으면 여기서 종료
                }

                updateSpeedByClears();
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
        BackgroundMusicPlayer.getInstance().stop(); // 게임 음악 정지
        int finalScore = board.getScore();
        
        RankingManager manager;
        if (isItemMode) {
            manager = RankingManager.getInstance("item_rankings.dat");
        } else if (isTimeAttackMode) {
            manager = RankingManager.getInstance("timeattack_rankings.dat");
        } else {
            manager = RankingManager.getInstance();
        }
            
        // 게임오버 시 처리
        SwingUtilities.invokeLater(() -> {
            java.awt.Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) {
                w.dispose();
                
                // 랭킹에 들어가는지 확인
                if (manager.getRankings().size() < 10 || manager.shouldInputName(finalScore)) {
                    // 랭킹 진입: 이름 입력 화면 표시 (이름 입력 후 스코어보드 표시)
                    new NameInputScreen(finalScore, board.getDifficulty(), isItemMode, isTimeAttackMode).setVisible(true);
                } else {
                    // 랭킹 미진입: 게임오버 화면 표시
                    new GameOverScreen(finalScore, board.getDifficulty(), isItemMode, isTimeAttackMode).setVisible(true);
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
            BackgroundMusicPlayer.getInstance().pause(); // 음악 일시정지
            Object[] options = {"resume", "restart", "main menu", "exit"};
            int sel = JOptionPane.showOptionDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "pause",
                    "Pause",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]
            );
            switch (sel) {
                case 0 -> { // 재개
                    paused = false;
                    BackgroundMusicPlayer.getInstance().resume(); // 음악 재개
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
                    BackgroundMusicPlayer.getInstance().resume(); // 음악 재개
                }
                case 2 -> { // 메인 메뉴
                    timer.stop();
                    slowEffectTimer.stop(); // 슬로우 효과 타이머 정지
                    BackgroundMusicPlayer.getInstance().stop(); // 게임 음악 정지
                    closeGameOnly();
                    SwingUtilities.invokeLater(() -> {
                        Mainmenu mainmenu = new Mainmenu();
                        mainmenu.setVisible(true);  // 메인메뉴 화면 표시 (생성자에서 MainBGM 재생)
                    });
                }
                case 3 -> { // 종료
                    timer.stop();
                    slowEffectTimer.stop();
                    System.exit(0);
                }
                default -> { 
                    // 닫기/취소 시 재개
                    paused = false;
                    BackgroundMusicPlayer.getInstance().resume();
                }
            }

        }
    }

    private void onForceQuit() {
        // 헤드리스 환경(테스트 환경)에서는 다이얼로그를 띄울 수 없으므로 조용히 리턴
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        
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
            BackgroundMusicPlayer.getInstance().stop();
            System.exit(0);
        }
    }

     private void closeGameOnly() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();
    }

    // ==== 렌더링 ====
    @Override
    public void paintComponent(Graphics g) {
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
            // 플래시 효과: 삭제될 줄은 흰색으로 번쩍임
            boolean isFlashing = false;
            if (flashingRows != null) {
                for (int fr : flashingRows) {
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

    private void drawCurrent(Graphics2D g) {
        Tetromino cur = board.getCurrent();
        if (cur == null) return;
        
        // 플래시 애니메이션 중에는 현재 블록을 그리지 않음 (이미 fixToBoard()로 그리드에 고정됨)
        if (flashingRows != null) return;
        
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
        double scale = Settings.getScaleFactor();
        int margin = (int)(20 * scale);
        int lineSpacing = (int)(24 * scale);
        
        int yPos = (int)(30 * scale);

        // NEXT - 맨 위로 이동
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        String nextLabel = "NEXT";
        if (board.getNextItemBlock() != null) {
            nextLabel = "NEXT (ITEM)";
        }
        g.drawString(nextLabel, sx + margin, yPos);
        yPos += (int)(20 * scale);
        drawNextPreview(g, sx + margin, yPos);
        g.setColor(Color.WHITE);
        yPos += (int)(110 * scale);

        // SCORE - 미리보기 아래로 이동
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("SCORE", sx + margin, yPos);
        yPos += lineSpacing;
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)baseFontSize));
        g.drawString(String.valueOf(board.getScore()), sx + margin, yPos);
        yPos += (int)(40 * scale);

        // DIFFICULTY
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("DIFFICULTY", sx + margin, yPos);
        yPos += lineSpacing;
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        String diff = board.getDifficulty().name().toLowerCase();
        diff = Character.toUpperCase(diff.charAt(0)) + diff.substring(1);
        g.drawString(diff, sx + margin, yPos);
        yPos += (int)(35 * scale);

        // LEVEL (= 누적 삭제 줄 수)
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("LEVEL", sx + margin, yPos);
        yPos += lineSpacing;
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        g.drawString(String.valueOf(board.getTotalLinesCleared()), sx + margin, yPos);
        yPos += (int)(35 * scale);

        // SPEED (현재 ms)
        g.setFont(g.getFont().deriveFont(Font.BOLD, (float)baseFontSize));
        g.drawString("SPEED", sx + margin, yPos);
        yPos += lineSpacing;
        g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.89)));
        g.drawString(currentDelay + " ms", sx + margin, yPos);
        yPos += (int)(45 * scale);
        
        // 아이템 모드에서 아이템 정보 표시
        if (isItemMode && board.getItemManager() != null) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.78)));
            g.drawString("ITEMS PROGRESS", sx + margin, yPos);
            yPos += (int)(20 * scale);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.67)));
            int itemProgress = board.getItemManager().getTotalLinesCleared() % 10;
            g.drawString(itemProgress + "/10 lines", sx + margin, yPos);
            yPos += (int)(30 * scale);
        }
        
        // 슬로우 효과 타이머 표시
        int effectYPos = yPos;
        if (board.isSlowEffectActive()) {
            long remainingTime = board.getSlowEffectRemainingTime();
            double seconds = remainingTime / 1000.0;
            
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.89)));
            g.setColor(new Color(173, 216, 230)); // 슬로우 블록과 같은 색상
            g.drawString("SLOW EFFECT", sx + margin, effectYPos);
            effectYPos += (int)(20 * scale);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.78)));
            g.drawString(String.format("%.1f sec", seconds), sx + margin, effectYPos);
            g.setColor(Color.WHITE); // 색상 원복
            effectYPos += (int)(30 * scale);
        }
        
        // Transform 효과 표시
        int transformRemaining = board.getTransformRemainingBlocks();
        if (transformRemaining > 0) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float)(baseFontSize * 0.89)));
            g.setColor(new Color(255, 215, 0)); // 골드 색상
            g.drawString("TRANSFORM", sx + margin, effectYPos);
            effectYPos += (int)(20 * scale);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, (float)(baseFontSize * 0.78)));
            g.drawString(String.format("%d blocks left", transformRemaining), sx + margin, effectYPos);
            g.setColor(Color.WHITE); // 색상 원복
        }
    }

    private void drawNextPreview(Graphics2D g, int px, int py) {
        // 배경 크기를 스케일에 맞게 조정
        double scale = Settings.getScaleFactor();
        int previewSize = (int)(80 * scale);
        int padding = (int)(10 * scale);
        g.setColor(new Color(60, 60, 60));
        g.fillRoundRect(px - padding, py - padding, previewSize, previewSize, 8, 8);

        // 아이템 블록이 있는지 먼저 확인
        items.ItemBlock nextItem = board.getNextItemBlock();
        
        if (nextItem != null) {
            drawItemPreview(g, px, py, nextItem);
        } else {
            ShapeType n = board.getNextShape();
            if (n != null) {
                drawShapePreview(g, px, py, n);
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
        
        double scale = Settings.getScaleFactor();
        int cell = CELL / 2;
        int previewSize = (int)(80 * scale);
        int padding = (int)(10 * scale);
        int w = (maxx - minx + 1) * cell;
        int h = (maxy - miny + 1) * cell;
        int cx = px + (previewSize - w) / 2 - padding;
        int cy = py + (previewSize - h) / 2 - padding;

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

    private void drawShapePreview(Graphics2D g, int px, int py, ShapeType n) {
        Position[] offs = n.getOffsets(0);
        int minx = 99, miny = 99, maxx = -99, maxy = -99;
        for (Position p : offs) {
            minx = Math.min(minx, p.x); maxx = Math.max(maxx, p.x);
            miny = Math.min(miny, p.y); maxy = Math.max(maxy, p.y);
        }
        double scale = Settings.getScaleFactor();
        int cell = CELL / 2;
        int previewSize = (int)(80 * scale);
        int padding = (int)(10 * scale);
        int w = (maxx - minx + 1) * cell;
        int h = (maxy - miny + 1) * cell;
        int cx = px + (previewSize - w) / 2 - padding;
        int cy = py + (previewSize - h) / 2 - padding;

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

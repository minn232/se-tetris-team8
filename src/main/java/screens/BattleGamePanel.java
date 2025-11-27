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

import core.BattleMode;
import core.Board;
import core.Difficulty;
import core.Position;
import core.Settings;
import core.ShapeType;
import core.Tetromino;

/**
 * 대전 모드 게임 패널
 * 두 개의 보드를 나란히 표시하고 줄 클리어 시 상대에게 줄을 전송
 */
public class BattleGamePanel extends JPanel {
    private final Board player1Board;
    private final Board player2Board;
    private Timer timer;
    
    private final int CELL;
    private final int BOARD_W;
    private final int BOARD_H;
    private final int SIDE_W;
    private final int GAP = 50; // 두 보드 사이 간격
    
    private boolean paused = false;
    private final int baseDelay = 800;
    private int currentDelay;
    
    // 플래시 애니메이션
    private int[] player1FlashingRows = null;
    private int[] player2FlashingRows = null;
    private long player1FlashUntil = 0;
    private long player2FlashUntil = 0;
    private static final long FLASH_MS = 150;
    
    // 포커스된 플레이어 (1 또는 2)
    private int focusedPlayer = 1;

    public BattleGamePanel(Difficulty difficulty) {
        // 플레이어1 보드 (왼쪽)
        player1Board = new Board(difficulty, false, new BattleMode() {
            @Override
            public void sendLinesToOpponent(int lines) {
                player2Board.getPendingLines().addLines(lines);
                System.out.println("플레이어1 -> 플레이어2: " + lines + "줄 전송");
            }
        });
        
        // 플레이어2 보드 (오른쪽)
        player2Board = new Board(difficulty, false, new BattleMode() {
            @Override
            public void sendLinesToOpponent(int lines) {
                player1Board.getPendingLines().addLines(lines);
                System.out.println("플레이어2 -> 플레이어1: " + lines + "줄 전송");
            }
        });
        
        this.CELL = Settings.getCellSize();
        this.BOARD_W = Board.COLS * CELL;
        this.BOARD_H = Board.ROWS * CELL;
        this.SIDE_W = (int)(180 * Settings.getScaleFactor());
        
        int totalWidth = SIDE_W + BOARD_W + GAP + BOARD_W + SIDE_W;
        setPreferredSize(new Dimension(totalWidth, BOARD_H));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        currentDelay = baseDelay;
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                
                // P: 일시정지
                if (code == KeyEvent.VK_P) {
                    togglePause();
                    repaint();
                    return;
                }
                
                // ESC: 강제 종료
                if (!paused && code == KeyEvent.VK_ESCAPE) {
                    onForceQuit();
                    return;
                }
                
                if (paused) return;
                
                // 플레이어 1 조작키 (WASD + G)
                if (!player1Board.isGameOver()) {
                    if (code == KeyEvent.VK_A) {
                        player1Board.moveLeft();
                    } else if (code == KeyEvent.VK_D) {
                        player1Board.moveRight();
                    } else if (code == KeyEvent.VK_S) {
                        player1Board.moveDown();
                    } else if (code == KeyEvent.VK_W) {
                        player1Board.rotate();
                    } else if (code == KeyEvent.VK_G) {
                        player1Board.hardDrop();
                    }
                }
                
                // 플레이어 2 조작키 (방향키 + /)
                if (!player2Board.isGameOver()) {
                    if (code == KeyEvent.VK_LEFT) {
                        player2Board.moveLeft();
                    } else if (code == KeyEvent.VK_RIGHT) {
                        player2Board.moveRight();
                    } else if (code == KeyEvent.VK_DOWN) {
                        player2Board.moveDown();
                    } else if (code == KeyEvent.VK_UP) {
                        player2Board.rotate();
                    } else if (code == KeyEvent.VK_SLASH) {
                        player2Board.hardDrop();
                    }
                }
                
                checkFlashAnimations();
                repaint();
            }
        });
        
        // 게임 타이머
        timer = new Timer(currentDelay, e -> {
            if (!paused) {
                if (!player1Board.isGameOver()) {
                    player1Board.moveDown();
                }
                if (!player2Board.isGameOver()) {
                    player2Board.moveDown();
                }
                
                checkFlashAnimations();
                
                // 두 플레이어 모두 게임 오버 시 종료
                if (player1Board.isGameOver() && player2Board.isGameOver()) {
                    timer.stop();
                    showGameOver();
                }
                
                repaint();
            }
        });
        timer.start();
    }
    
    private void checkFlashAnimations() {
        // 플레이어1 애니메이션 체크
        int[] rows1 = player1Board.pollClearingRows();
        if (rows1 != null && rows1.length > 0) {
            player1FlashingRows = rows1;
            player1FlashUntil = System.currentTimeMillis() + FLASH_MS;
        }
        
        // 플레이어2 애니메이션 체크
        int[] rows2 = player2Board.pollClearingRows();
        if (rows2 != null && rows2.length > 0) {
            player2FlashingRows = rows2;
            player2FlashUntil = System.currentTimeMillis() + FLASH_MS;
        }
        
        // 애니메이션 종료 체크
        long now = System.currentTimeMillis();
        if (player1FlashingRows != null && now >= player1FlashUntil) {
            player1Board.clearRows(player1FlashingRows);
            player1FlashingRows = null;
        }
        if (player2FlashingRows != null && now >= player2FlashUntil) {
            player2Board.clearRows(player2FlashingRows);
            player2FlashingRows = null;
        }
    }
    
    private void togglePause() {
        paused = !paused;
        if (paused) {
            showPauseMenu();
        }
    }
    
    private void showPauseMenu() {
        String[] options = {"계속하기", "다시 시작", "메인 메뉴"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "게임이 일시정지되었습니다.",
            "일시정지",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 0) {
            paused = false;
        } else if (choice == 1) {
            restart();
        } else if (choice == 2) {
            goToMainMenu();
        }
    }
    
    private void onForceQuit() {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "정말 종료하시겠습니까?",
            "종료 확인",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            timer.stop();
            goToMainMenu();
        }
    }
    
    private void showGameOver() {
        String winner;
        
        if (player1Board.isGameOver() && !player2Board.isGameOver()) {
            winner = "플레이어 2 승리!";
        } else if (player2Board.isGameOver() && !player1Board.isGameOver()) {
            winner = "플레이어 1 승리!";
        } else {
            // 둘 다 게임 오버 - 점수로 판단
            if (player1Board.getScore() > player2Board.getScore()) {
                winner = "플레이어 1 승리!";
            } else if (player2Board.getScore() > player1Board.getScore()) {
                winner = "플레이어 2 승리!";
            } else {
                winner = "무승부!";
            }
        }
        
        String message = String.format(
            "%s\n\n플레이어 1 점수: %d\n플레이어 2 점수: %d",
            winner,
            player1Board.getScore(),
            player2Board.getScore()
        );
        
        SwingUtilities.invokeLater(() -> {
            String[] options = {"다시 시작", "메인 메뉴"};
            int choice = JOptionPane.showOptionDialog(
                this,
                message,
                "게임 종료",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == 0) {
                restart();
            } else {
                goToMainMenu();
            }
        });
    }
    
    private void restart() {
        player1Board.reset();
        player2Board.reset();
        player1FlashingRows = null;
        player2FlashingRows = null;
        currentDelay = baseDelay;
        timer.setDelay(currentDelay);
        paused = false;
        timer.restart();
        requestFocusInWindow();
    }
    
    private void goToMainMenu() {
        timer.stop();
        SwingUtilities.getWindowAncestor(this).dispose();
        SwingUtilities.invokeLater(() -> new Mainmenu().setVisible(true));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        int yOffset = 0;
        
        // 플레이어1 영역 (왼쪽)
        int player1X = SIDE_W;
        drawPlayerArea(g2, player1Board, player1X, yOffset, player1FlashingRows, 
                      player1FlashUntil, "Player 1", focusedPlayer == 1);
        
        // 플레이어2 영역 (오른쪽)
        int player2X = SIDE_W + BOARD_W + GAP;
        drawPlayerArea(g2, player2Board, player2X, yOffset, player2FlashingRows,
                      player2FlashUntil, "Player 2", focusedPlayer == 2);
        
        // 일시정지 표시
        if (paused) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("맑은 고딕", Font.BOLD, 40));
            String pauseText = "일시정지 (P)";
            int textWidth = g2.getFontMetrics().stringWidth(pauseText);
            g2.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }
    
    private void drawPlayerArea(Graphics2D g2, Board board, int xOffset, int yOffset,
                                int[] flashingRows, long flashUntil, String playerName,
                                boolean isFocused) {
        // 포커스 표시
        if (isFocused) {
            g2.setColor(new Color(255, 255, 0, 100));
            g2.fillRect(xOffset - 5, yOffset - 5, BOARD_W + 10, BOARD_H + 10);
        }
        
        // 보드 테두리
        g2.setColor(Color.WHITE);
        g2.drawRect(xOffset, yOffset, BOARD_W, BOARD_H);
        
        // 그리드 그리기
        ShapeType[][] grid = board.getGrid();
        long now = System.currentTimeMillis();
        boolean isFlashing = flashingRows != null && now < flashUntil;
        
            for (int y = 0; y < Board.ROWS; y++) {
            boolean rowFlashing = false;
            if (isFlashing && flashingRows != null) {
                for (int row : flashingRows) {
                    if (row == y) {
                        rowFlashing = true;
                        break;
                    }
                }
            }            for (int x = 0; x < Board.COLS; x++) {
                int px = xOffset + x * CELL;
                int py = yOffset + y * CELL;
                
                if (rowFlashing) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(px, py, CELL, CELL);
                } else if (grid[y][x] != null) {
                    g2.setColor(getShapeColor(grid[y][x]));
                    g2.fillRect(px, py, CELL, CELL);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(px, py, CELL, CELL);
                }
            }
        }
        
        // 현재 블록 그리기
        Tetromino current = board.getCurrent();
        if (current != null && !board.isGameOver()) {
            g2.setColor(getShapeColor(current.getShape()));
            for (Position p : current.getBlocks()) {
                int x = current.getX() + p.x;
                int y = current.getY() + p.y;
                if (y >= 0 && y < Board.ROWS && x >= 0 && x < Board.COLS) {
                    int px = xOffset + x * CELL;
                    int py = yOffset + y * CELL;
                    g2.fillRect(px, py, CELL, CELL);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(px, py, CELL, CELL);
                    g2.setColor(getShapeColor(current.getShape()));
                }
            }
        }
        
        // 사이드 정보
        drawSideInfo(g2, board, xOffset, yOffset, playerName);
    }
    
    private void drawSideInfo(Graphics2D g2, Board board, int boardX, int yOffset, 
                              String playerName) {
        int sideX = boardX + BOARD_W + 10;
        int y = yOffset + 30;
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        
        // 플레이어 이름
        g2.drawString(playerName, sideX, y);
        y += 30;
        
        // 점수
        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        g2.drawString("Scores", sideX, y);
        y += 20;
        g2.setColor(Color.RED);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        g2.drawString(String.valueOf(board.getScore()), sideX, y);
        y += 40;
        
        // 다음 블록
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        g2.drawString("Next", sideX, y);
        y += 30;
        
        ShapeType nextShape = board.getNextShape();
        if (nextShape != null) {
            drawMiniShape(g2, nextShape, sideX + 10, y);
        }
        y += 80;
        
        // 조작키 표시
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        if (playerName.equals("Player 1")) {
            g2.drawString("Controls:", sideX, y);
            y += 18;
            g2.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            g2.drawString("W: Rotate", sideX, y);
            y += 15;
            g2.drawString("A/D: Move", sideX, y);
            y += 15;
            g2.drawString("S: Down", sideX, y);
            y += 15;
            g2.drawString("G: Hard Drop", sideX, y);
        } else {
            g2.drawString("Controls:", sideX, y);
            y += 18;
            g2.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            g2.drawString("↑: Rotate", sideX, y);
            y += 15;
            g2.drawString("←/→: Move", sideX, y);
            y += 15;
            g2.drawString("↓: Down", sideX, y);
            y += 15;
            g2.drawString("/: Hard Drop", sideX, y);
        }
        y += 30;
        
        // 넘어올 줄 표시
        int pendingCount = board.getPendingLines().getCount();
        if (pendingCount > 0) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            g2.drawString("줄을 삭제한 블럭", sideX, y);
            y += 20;
            g2.drawString("모양저장 빈 칸이", sideX, y);
            y += 20;
            g2.drawString("존재", sideX, y);
            y += 30;
            
            g2.setColor(Color.RED);
            g2.setFont(new Font("맑은 고딕", Font.BOLD, 24));
            g2.drawString("+" + pendingCount, sideX + 20, y);
        }
    }
    
    private void drawMiniShape(Graphics2D g2, ShapeType shape, int x, int y) {
        Position[] blocks = shape.getOffsets(0);
        g2.setColor(getShapeColor(shape));
        
        int miniCell = CELL / 2;
        for (Position p : blocks) {
            int px = x + p.x * miniCell;
            int py = y + p.y * miniCell;
            g2.fillRect(px, py, miniCell, miniCell);
            g2.setColor(Color.BLACK);
            g2.drawRect(px, py, miniCell, miniCell);
            g2.setColor(getShapeColor(shape));
        }
    }
    
    private Color getShapeColor(ShapeType type) {
        return switch (type) {
            case I -> Color.CYAN;
            case O -> Color.YELLOW;
            case T -> new Color(128, 0, 128);
            case S -> Color.GREEN;
            case Z -> Color.RED;
            case J -> Color.BLUE;
            case L -> Color.ORANGE;
        };
    }
}

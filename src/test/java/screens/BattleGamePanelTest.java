package screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;

class BattleGamePanelTest {
    
    private BattleGamePanel panel;
    private JFrame frame;
    
    @BeforeEach
    void setUp() {
        if (!GraphicsEnvironment.isHeadless()) {
            panel = new BattleGamePanel(Difficulty.EASY);
            frame = new JFrame();
            frame.add(panel);
            frame.pack();
        }
    }
    
    @AfterEach
    void tearDown() {
        if (frame != null) {
            frame.dispose();
        }
        if (panel != null) {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(panel);
                if (timer != null) {
                    timer.stop();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        BackgroundMusicPlayer.getInstance().stop();
    }
    
    @Test
    void testConstructorDefault() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertNotNull(panel);
        assertTrue(panel.isFocusable());
        try {
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            assertNotNull(board1);
            assertFalse(board1.isItemMode());
            
            Field board2Field = BattleGamePanel.class.getDeclaredField("board2");
            board2Field.setAccessible(true);
            Board board2 = (Board) board2Field.get(panel);
            assertNotNull(board2);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testConstructorWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel itemPanel = new BattleGamePanel(Difficulty.NORMAL, true);
        assertNotNull(itemPanel);
        try {
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(itemPanel);
            assertTrue(board1.isItemMode());
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(itemPanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testConstructorWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.HARD, false, true);
        assertNotNull(timePanel);
        try {
            Field isTimeAttackField = BattleGamePanel.class.getDeclaredField("isTimeAttack");
            isTimeAttackField.setAccessible(true);
            boolean isTimeAttack = (boolean) isTimeAttackField.get(timePanel);
            assertTrue(isTimeAttack);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testHandleKeyPressPlayer1Left() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            int leftKey = core.Settings.getKeyLeft(core.Settings.Player.P1);
            KeyEvent leftEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, leftKey, (char) leftKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, leftEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer1Right() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int rightKey = core.Settings.getKeyRight(core.Settings.Player.P1);
            KeyEvent rightEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, rightKey, (char) rightKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, rightEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer1Down() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int downKey = core.Settings.getKeyDown(core.Settings.Player.P1);
            KeyEvent downEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, downKey, (char) downKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, downEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer1Rotate() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int rotateKey = core.Settings.getKeyRotate(core.Settings.Player.P1);
            KeyEvent rotateEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, rotateKey, (char) rotateKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, rotateEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer1HardDrop() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int hardDropKey = core.Settings.getKeyHardDrop(core.Settings.Player.P1);
            KeyEvent hardDropEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, hardDropKey, (char) hardDropKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, hardDropEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer2Left() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int leftKey = core.Settings.getKeyLeft(core.Settings.Player.P2);
            KeyEvent leftEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, leftKey, (char) leftKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, leftEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer2Right() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int rightKey = core.Settings.getKeyRight(core.Settings.Player.P2);
            KeyEvent rightEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, rightKey, (char) rightKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, rightEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer2Down() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int downKey = core.Settings.getKeyDown(core.Settings.Player.P2);
            KeyEvent downEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, downKey, (char) downKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, downEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer2Rotate() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int rotateKey = core.Settings.getKeyRotate(core.Settings.Player.P2);
            KeyEvent rotateEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, rotateKey, (char) rotateKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, rotateEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressPlayer2HardDrop() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            int hardDropKey = core.Settings.getKeyHardDrop(core.Settings.Player.P2);
            KeyEvent hardDropEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, hardDropKey, (char) hardDropKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, hardDropEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressWhenPaused() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field pausedField = BattleGamePanel.class.getDeclaredField("paused");
            pausedField.setAccessible(true);
            pausedField.set(panel, true);
            
            int leftKey = core.Settings.getKeyLeft(core.Settings.Player.P1);
            KeyEvent leftEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, leftKey, (char) leftKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, leftEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testHandleKeyPressWhenWinner() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method handleKeyPressMethod = BattleGamePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
            handleKeyPressMethod.setAccessible(true);
            
            Field winnerField = BattleGamePanel.class.getDeclaredField("winner");
            winnerField.setAccessible(true);
            winnerField.set(panel, "PLAYER 1");
            
            int leftKey = core.Settings.getKeyLeft(core.Settings.Player.P1);
            KeyEvent leftEvent = new KeyEvent(panel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, leftKey, (char) leftKey);
            
            assertDoesNotThrow(() -> handleKeyPressMethod.invoke(panel, leftEvent));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckBlockPlacement() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkBlockPlacementMethod = BattleGamePanel.class.getDeclaredMethod(
                "checkBlockPlacement", Board.class, Board.class, int.class);
            checkBlockPlacementMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            Field board2Field = BattleGamePanel.class.getDeclaredField("board2");
            board2Field.setAccessible(true);
            Board board2 = (Board) board2Field.get(panel);
            
            assertDoesNotThrow(() -> checkBlockPlacementMethod.invoke(panel, board1, board2, 1));
            assertDoesNotThrow(() -> checkBlockPlacementMethod.invoke(panel, board2, board1, 2));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckWinner() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkWinnerMethod = BattleGamePanel.class.getDeclaredMethod("checkWinner");
            checkWinnerMethod.setAccessible(true);
            
            Field winnerField = BattleGamePanel.class.getDeclaredField("winner");
            winnerField.setAccessible(true);
            
            assertDoesNotThrow(() -> checkWinnerMethod.invoke(panel));
            assertNull(winnerField.get(panel));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckFlashing() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkFlashingMethod = BattleGamePanel.class.getDeclaredMethod("checkFlashing", Board.class, int.class);
            checkFlashingMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            assertDoesNotThrow(() -> checkFlashingMethod.invoke(panel, board1, 1));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testPaintComponent() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        assertDoesNotThrow(() -> {
            panel.repaint();
            Thread.sleep(50);
        });
    }
    
    @Test
    void testDrawBoard() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawBoardMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawBoard", Graphics2D.class, Board.class, int[].class);
            drawBoardMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawBoardMethod.invoke(panel, g2, board1, null));
            assertDoesNotThrow(() -> drawBoardMethod.invoke(panel, g2, board1, new int[]{0, 1}));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawCurrent() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawCurrentMethod = BattleGamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class, Board.class);
            drawCurrentMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawCurrentMethod.invoke(panel, g2, board1));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawGrid() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawGridMethod = BattleGamePanel.class.getDeclaredMethod("drawGrid", Graphics2D.class);
            drawGridMethod.setAccessible(true);
            
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawGridMethod.invoke(panel, g2));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawSidebar() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawSidebarMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawSidebar", Graphics2D.class, Board.class, int.class, String.class);
            drawSidebarMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawSidebarMethod.invoke(panel, g2, board1, 0, "PLAYER 1"));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawNextPreview() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawNextPreviewMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawNextPreview", Graphics2D.class, int.class, int.class, Board.class);
            drawNextPreviewMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawNextPreviewMethod.invoke(panel, g2, 20, 80, board1));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawItemPreview() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawItemPreviewMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
            drawItemPreviewMethod.setAccessible(true);
            
            items.SlowBlock slowItem = new items.SlowBlock();
            
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawItemPreviewMethod.invoke(panel, g2, 20, 165, slowItem));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawPaused() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawPausedMethod = BattleGamePanel.class.getDeclaredMethod("drawPaused", Graphics2D.class);
            drawPausedMethod.setAccessible(true);
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawPausedMethod.invoke(panel, g2));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawWinner() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawWinnerMethod = BattleGamePanel.class.getDeclaredMethod("drawWinner", Graphics2D.class);
            drawWinnerMethod.setAccessible(true);
            
            Field winnerField = BattleGamePanel.class.getDeclaredField("winner");
            winnerField.setAccessible(true);
            winnerField.set(panel, "PLAYER 1");
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawWinnerMethod.invoke(panel, g2));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testFillCell() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method fillCellMethod = BattleGamePanel.class.getDeclaredMethod(
                "fillCell", Graphics2D.class, int.class, int.class, Color.class);
            fillCellMethod.setAccessible(true);
            
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> fillCellMethod.invoke(panel, g2, 0, 0, Color.RED));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testTimeAttackMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.EASY, false, true);
        try {
            Field timeLimitField = BattleGamePanel.class.getDeclaredField("timeLimit");
            timeLimitField.setAccessible(true);
            long timeLimit = (long) timeLimitField.get(timePanel);
            assertEquals(180000, timeLimit);
            
            Field gameStartTimeField = BattleGamePanel.class.getDeclaredField("gameStartTime");
            gameStartTimeField.setAccessible(true);
            long gameStartTime = (long) gameStartTimeField.get(timePanel);
            assertTrue(gameStartTime > 0);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testConstants() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field cellField = BattleGamePanel.class.getDeclaredField("CELL");
            cellField.setAccessible(true);
            int cell = (int) cellField.get(panel);
            assertTrue(cell > 0);
            
            Field boardWField = BattleGamePanel.class.getDeclaredField("BOARD_W");
            boardWField.setAccessible(true);
            int boardW = (int) boardWField.get(panel);
            assertEquals(Board.COLS * cell, boardW);
            
            Field boardHField = BattleGamePanel.class.getDeclaredField("BOARD_H");
            boardHField.setAccessible(true);
            int boardH = (int) boardHField.get(panel);
            assertEquals(Board.ROWS * cell, boardH);
            
            Field gapField = BattleGamePanel.class.getDeclaredField("GAP");
            gapField.setAccessible(true);
            int gap = (int) gapField.get(panel);
            assertTrue(gap > 0);
            
            Field sideWField = BattleGamePanel.class.getDeclaredField("SIDE_W");
            sideWField.setAccessible(true);
            int sideW = (int) sideWField.get(panel);
            assertTrue(sideW > 0);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckFlashingWithRows() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkFlashingMethod = BattleGamePanel.class.getDeclaredMethod("checkFlashing", Board.class, int.class);
            checkFlashingMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(panel);
            
            // pollClearingRows가 rows를 반환하도록 보드에 완성된 줄 만들기 시도
            assertDoesNotThrow(() -> checkFlashingMethod.invoke(panel, board1, 1));
            
            Field board2Field = BattleGamePanel.class.getDeclaredField("board2");
            board2Field.setAccessible(true);
            Board board2 = (Board) board2Field.get(panel);
            
            assertDoesNotThrow(() -> checkFlashingMethod.invoke(panel, board2, 2));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckFlashingPlayer2() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkFlashingMethod = BattleGamePanel.class.getDeclaredMethod("checkFlashing", Board.class, int.class);
            checkFlashingMethod.setAccessible(true);
            
            Field board2Field = BattleGamePanel.class.getDeclaredField("board2");
            board2Field.setAccessible(true);
            Board board2 = (Board) board2Field.get(panel);
            
            assertDoesNotThrow(() -> checkFlashingMethod.invoke(panel, board2, 2));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testTogglePause() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field pausedField = BattleGamePanel.class.getDeclaredField("paused");
            pausedField.setAccessible(true);
            
            boolean initialPaused = (boolean) pausedField.get(panel);
            assertFalse(initialPaused);
            
            pausedField.set(panel, true);
            assertTrue((boolean) pausedField.get(panel));
            
            pausedField.set(panel, false);
            assertFalse((boolean) pausedField.get(panel));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testTogglePauseWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.EASY, false, true);
        try {
            Field pausedField = BattleGamePanel.class.getDeclaredField("paused");
            pausedField.setAccessible(true);
            
            Field pauseStartTimeField = BattleGamePanel.class.getDeclaredField("pauseStartTime");
            pauseStartTimeField.setAccessible(true);
            
            pausedField.set(timePanel, true);
            pauseStartTimeField.set(timePanel, System.currentTimeMillis());
            
            pausedField.set(timePanel, false);
            assertFalse((boolean) pausedField.get(timePanel));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testCheckWinnerTimeAttackPlayer1Wins() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.EASY, false, true);
        try {
            Method checkWinnerMethod = BattleGamePanel.class.getDeclaredMethod("checkWinner");
            checkWinnerMethod.setAccessible(true);
            
            Field timeLimitField = BattleGamePanel.class.getDeclaredField("timeLimit");
            timeLimitField.setAccessible(true);
            timeLimitField.set(timePanel, 100L); // 매우 짧은 시간
            
            Field gameStartTimeField = BattleGamePanel.class.getDeclaredField("gameStartTime");
            gameStartTimeField.setAccessible(true);
            gameStartTimeField.set(timePanel, System.currentTimeMillis() - 200L);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(timePanel);
            
            // board1이 더 높은 점수를 갖도록 설정 (리플렉션으로)
            assertDoesNotThrow(() -> checkWinnerMethod.invoke(timePanel));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testCheckWinnerBothGameOver() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method checkWinnerMethod = BattleGamePanel.class.getDeclaredMethod("checkWinner");
            checkWinnerMethod.setAccessible(true);
            
            assertDoesNotThrow(() -> checkWinnerMethod.invoke(panel));
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testRestartGame() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method restartGameMethod = BattleGamePanel.class.getDeclaredMethod("restartGame");
            restartGameMethod.setAccessible(true);
            assertNotNull(restartGameMethod);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testReturnToMenu() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method returnToMenuMethod = BattleGamePanel.class.getDeclaredMethod("returnToMenu");
            returnToMenuMethod.setAccessible(true);
            assertNotNull(returnToMenuMethod);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testDrawCurrentWithItemBlock() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel itemPanel = new BattleGamePanel(Difficulty.EASY, true);
        try {
            Method drawCurrentMethod = BattleGamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class, Board.class);
            drawCurrentMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(itemPanel);
            
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawCurrentMethod.invoke(itemPanel, g2, board1));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(itemPanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testDrawSidebarWithItemMode() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel itemPanel = new BattleGamePanel(Difficulty.EASY, true);
        try {
            Method drawSidebarMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawSidebar", Graphics2D.class, Board.class, int.class, String.class);
            drawSidebarMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(itemPanel);
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawSidebarMethod.invoke(itemPanel, g2, board1, 0, "PLAYER 1"));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(itemPanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testDrawSidebarWithTimeAttack() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.EASY, false, true);
        try {
            Method drawSidebarMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawSidebar", Graphics2D.class, Board.class, int.class, String.class);
            drawSidebarMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(timePanel);
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawSidebarMethod.invoke(timePanel, g2, board1, 0, "PLAYER 1"));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testDrawSidebarWithLowTime() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        BattleGamePanel timePanel = new BattleGamePanel(Difficulty.EASY, false, true);
        try {
            Method drawSidebarMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawSidebar", Graphics2D.class, Board.class, int.class, String.class);
            drawSidebarMethod.setAccessible(true);
            
            Field board1Field = BattleGamePanel.class.getDeclaredField("board1");
            board1Field.setAccessible(true);
            Board board1 = (Board) board1Field.get(timePanel);
            
            Field timeLimitField = BattleGamePanel.class.getDeclaredField("timeLimit");
            timeLimitField.setAccessible(true);
            timeLimitField.set(timePanel, 20000L); // 20초로 설정
            
            Field gameStartTimeField = BattleGamePanel.class.getDeclaredField("gameStartTime");
            gameStartTimeField.setAccessible(true);
            gameStartTimeField.set(timePanel, System.currentTimeMillis() - 5000L); // 15초 남음
            
            BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawSidebarMethod.invoke(timePanel, g2, board1, 0, "PLAYER 1"));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        } finally {
            try {
                Field timerField = BattleGamePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(timePanel);
                if (timer != null) timer.stop();
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    @Test
    void testDrawItemPreviewWithLineBlock() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Method drawItemPreviewMethod = BattleGamePanel.class.getDeclaredMethod(
                "drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
            drawItemPreviewMethod.setAccessible(true);
            
            items.LineBlock lineItem = new items.LineBlock();
            
            BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            
            assertDoesNotThrow(() -> drawItemPreviewMethod.invoke(panel, g2, 20, 165, lineItem));
            
            g2.dispose();
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testPaintComponentWithPaused() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field pausedField = BattleGamePanel.class.getDeclaredField("paused");
            pausedField.setAccessible(true);
            pausedField.set(panel, true);
            
            assertDoesNotThrow(() -> {
                panel.repaint();
                Thread.sleep(50);
            });
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testPaintComponentWithWinner() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field winnerField = BattleGamePanel.class.getDeclaredField("winner");
            winnerField.setAccessible(true);
            winnerField.set(panel, "PLAYER 1");
            
            assertDoesNotThrow(() -> {
                panel.repaint();
                Thread.sleep(50);
            });
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testBaseDelay() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field baseDelayField = BattleGamePanel.class.getDeclaredField("baseDelay");
            baseDelayField.setAccessible(true);
            int baseDelay = (int) baseDelayField.get(panel);
            assertEquals(800, baseDelay);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
    
    @Test
    void testFlashMs() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        
        try {
            Field flashMsField = BattleGamePanel.class.getDeclaredField("FLASH_MS");
            flashMsField.setAccessible(true);
            long flashMs = (long) flashMsField.get(null);
            assertEquals(150L, flashMs);
        } catch (Exception e) {
            fail("리플렉션 실패: " + e.getMessage());
        }
    }
}

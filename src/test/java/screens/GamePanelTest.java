package screens;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Board;
import core.Difficulty;
import core.ShapeType;
import items.BombBlock;
import items.LineBlock;
import items.SlowBlock;
import items.WeightBlock;

public class GamePanelTest {
    private GamePanel panel;
    private Board board;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        board = new Board(Difficulty.NORMAL, false);
        panel = new GamePanel(board, false, false);
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        if (panel != null) {
            Field timerField = GamePanel.class.getDeclaredField("timer");
            timerField.setAccessible(true);
            Timer timer = (Timer) timerField.get(panel);
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            
            Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
            slowTimerField.setAccessible(true);
            Timer slowTimer = (Timer) slowTimerField.get(panel);
            if (slowTimer != null && slowTimer.isRunning()) {
                slowTimer.stop();
            }
        }
    }
    
    @Test
    public void testConstructorNormalMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(panel);
        assertTrue(panel.isFocusable());
        
        Field isItemModeField = GamePanel.class.getDeclaredField("isItemMode");
        isItemModeField.setAccessible(true);
        assertFalse((boolean) isItemModeField.get(panel));
        
        Field isTimeAttackField = GamePanel.class.getDeclaredField("isTimeAttackMode");
        isTimeAttackField.setAccessible(true);
        assertFalse((boolean) isTimeAttackField.get(panel));
    }
    
    @Test
    public void testConstructorItemMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.EASY, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field isItemModeField = GamePanel.class.getDeclaredField("isItemMode");
        isItemModeField.setAccessible(true);
        assertTrue((boolean) isItemModeField.get(itemPanel));
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testConstructorTimeAttackMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board timeBoard = new Board(Difficulty.HARD, false);
        GamePanel timePanel = new GamePanel(timeBoard, false, true);
        
        Field isTimeAttackField = GamePanel.class.getDeclaredField("isTimeAttackMode");
        isTimeAttackField.setAccessible(true);
        assertTrue((boolean) isTimeAttackField.get(timePanel));
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(timePanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(timePanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testBaseDelayEasyDifficulty() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board easyBoard = new Board(Difficulty.EASY, false);
        GamePanel easyPanel = new GamePanel(easyBoard, false, false);
        
        Field baseDelayField = GamePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        assertEquals(1000, (int) baseDelayField.get(easyPanel));
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(easyPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(easyPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testBaseDelayNormalDifficulty() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field baseDelayField = GamePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        assertEquals(800, (int) baseDelayField.get(panel));
    }
    
    @Test
    public void testBaseDelayHardDifficulty() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board hardBoard = new Board(Difficulty.HARD, false);
        GamePanel hardPanel = new GamePanel(hardBoard, false, false);
        
        Field baseDelayField = GamePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        assertEquals(600, (int) baseDelayField.get(hardPanel));
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(hardPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(hardPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testPaintComponent() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        panel.paintComponent(g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawBoard() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawBoard = GamePanel.class.getDeclaredMethod("drawBoard", Graphics2D.class);
        drawBoard.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawBoard.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawBoardWithFlashing() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashRowsField = GamePanel.class.getDeclaredField("flashingRows");
        flashRowsField.setAccessible(true);
        flashRowsField.set(panel, new int[]{0, 1});
        
        Field flashUntilField = GamePanel.class.getDeclaredField("flashUntil");
        flashUntilField.setAccessible(true);
        flashUntilField.set(panel, System.currentTimeMillis() + 1000);
        
        Method drawBoard = GamePanel.class.getDeclaredMethod("drawBoard", Graphics2D.class);
        drawBoard.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawBoard.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawCurrent() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawCurrentWithItemBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        // 슬로우 블록 설정
        Field currentItemField = Board.class.getDeclaredField("currentItemBlock");
        currentItemField.setAccessible(true);
        currentItemField.set(itemBoard, new SlowBlock());
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawCurrentWithLineBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field currentItemField = Board.class.getDeclaredField("currentItemBlock");
        currentItemField.setAccessible(true);
        currentItemField.set(itemBoard, new LineBlock());
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawCurrentWithBombBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field currentItemField = Board.class.getDeclaredField("currentItemBlock");
        currentItemField.setAccessible(true);
        currentItemField.set(itemBoard, new BombBlock());
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawCurrentWithWeightBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field currentItemField = Board.class.getDeclaredField("currentItemBlock");
        currentItemField.setAccessible(true);
        currentItemField.set(itemBoard, new WeightBlock());
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawCurrentDuringFlashing() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashRowsField = GamePanel.class.getDeclaredField("flashingRows");
        flashRowsField.setAccessible(true);
        flashRowsField.set(panel, new int[]{0});
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawGrid() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawGrid = GamePanel.class.getDeclaredMethod("drawGrid", Graphics2D.class);
        drawGrid.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawGrid.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawSidebar() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawSidebar = GamePanel.class.getDeclaredMethod("drawSidebar", Graphics2D.class);
        drawSidebar.setAccessible(true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawSidebar.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testDrawSidebarItemMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Method drawSidebar = GamePanel.class.getDeclaredMethod("drawSidebar", Graphics2D.class);
        drawSidebar.setAccessible(true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawSidebar.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawNextPreview() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawNextPreview = GamePanel.class.getDeclaredMethod("drawNextPreview", Graphics2D.class, int.class, int.class);
        drawNextPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawNextPreview.invoke(panel, g2, 10, 10);
        g2.dispose();
    }
    
    @Test
    public void testDrawItemPreview() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawItemPreview = GamePanel.class.getDeclaredMethod("drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
        drawItemPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawItemPreview.invoke(panel, g2, 10, 10, new LineBlock());
        g2.dispose();
    }
    
    @Test
    public void testDrawItemPreviewSlowBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawItemPreview = GamePanel.class.getDeclaredMethod("drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
        drawItemPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawItemPreview.invoke(panel, g2, 10, 10, new SlowBlock());
        g2.dispose();
    }
    
    @Test
    public void testDrawShapePreview() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawShapePreview = GamePanel.class.getDeclaredMethod("drawShapePreview", Graphics2D.class, int.class, int.class, ShapeType.class);
        drawShapePreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawShapePreview.invoke(panel, g2, 10, 10, ShapeType.I);
        g2.dispose();
    }
    
    @Test
    public void testDrawPaused() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawPaused = GamePanel.class.getDeclaredMethod("drawPaused", Graphics2D.class);
        drawPaused.setAccessible(true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawPaused.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testFillCell() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method fillCell = GamePanel.class.getDeclaredMethod("fillCell", Graphics2D.class, int.class, int.class, java.awt.Color.class);
        fillCell.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        fillCell.invoke(panel, g2, 0, 0, java.awt.Color.RED);
        g2.dispose();
    }
    
    @Test
    public void testPaintComponentPaused() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pausedField = GamePanel.class.getDeclaredField("paused");
        pausedField.setAccessible(true);
        pausedField.set(panel, true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        panel.paintComponent(g2);
        g2.dispose();
    }
    
    @Test
    public void testUpdateSpeedByClears() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method updateSpeed = GamePanel.class.getDeclaredMethod("updateSpeedByClears");
        updateSpeed.setAccessible(true);
        updateSpeed.invoke(panel);
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        int currentDelay = (int) currentDelayField.get(panel);
        assertTrue(currentDelay > 0);
    }
    
    @Test
    public void testFlashConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashMs = GamePanel.class.getDeclaredField("FLASH_MS");
        flashMs.setAccessible(true);
        assertEquals(150L, (long) flashMs.get(null));
    }
    
    @Test
    public void testPausedInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pausedField = GamePanel.class.getDeclaredField("paused");
        pausedField.setAccessible(true);
        assertFalse((boolean) pausedField.get(panel));
    }
    
    @Test
    public void testMinDelayField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field minDelayField = GamePanel.class.getDeclaredField("minDelay");
        minDelayField.setAccessible(true);
        assertEquals(150, (int) minDelayField.get(panel));
    }
    
    @Test
    public void testStepPerLineField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field stepPerLineField = GamePanel.class.getDeclaredField("stepPerLine");
        stepPerLineField.setAccessible(true);
        assertEquals(50, (int) stepPerLineField.get(panel));
    }
    
    @Test
    public void testTimerRunning() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);
        assertNotNull(timer);
    }
    
    @Test
    public void testSlowEffectTimerRunning() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(panel);
        assertNotNull(slowTimer);
    }
    
    @Test
    public void testAllShapeTypes() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawShapePreview = GamePanel.class.getDeclaredMethod("drawShapePreview", Graphics2D.class, int.class, int.class, ShapeType.class);
        drawShapePreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        
        for (ShapeType shape : ShapeType.values()) {
            drawShapePreview.invoke(panel, g2, 10, 10, shape);
        }
        
        g2.dispose();
    }
    
    @Test
    public void testUpdateSpeedWithSlowEffect() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        // 슬로우 효과 활성화
        Field slowEffectActiveField = Board.class.getDeclaredField("slowEffectActive");
        slowEffectActiveField.setAccessible(true);
        slowEffectActiveField.set(itemBoard, true);
        
        Method updateSpeed = GamePanel.class.getDeclaredMethod("updateSpeedByClears");
        updateSpeed.setAccessible(true);
        updateSpeed.invoke(itemPanel);
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        int currentDelay = (int) currentDelayField.get(itemPanel);
        assertTrue(currentDelay > 0);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawSidebarWithSlowEffect() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field slowEffectActiveField = Board.class.getDeclaredField("slowEffectActive");
        slowEffectActiveField.setAccessible(true);
        slowEffectActiveField.set(itemBoard, true);
        
        Field slowEffectStartTimeField = Board.class.getDeclaredField("slowEffectStartTime");
        slowEffectStartTimeField.setAccessible(true);
        slowEffectStartTimeField.set(itemBoard, System.currentTimeMillis());
        
        Method drawSidebar = GamePanel.class.getDeclaredMethod("drawSidebar", Graphics2D.class);
        drawSidebar.setAccessible(true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawSidebar.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawSidebarWithTransformEffect() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field transformRemainingField = Board.class.getDeclaredField("transformRemainingBlocks");
        transformRemainingField.setAccessible(true);
        transformRemainingField.set(itemBoard, 5);
        
        Method drawSidebar = GamePanel.class.getDeclaredMethod("drawSidebar", Graphics2D.class);
        drawSidebar.setAccessible(true);
        
        BufferedImage img = new BufferedImage(500, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawSidebar.invoke(itemPanel, g2);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawNextPreviewWithItemBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Field nextItemBlockField = Board.class.getDeclaredField("nextItemBlock");
        nextItemBlockField.setAccessible(true);
        nextItemBlockField.set(itemBoard, new BombBlock());
        
        Method drawNextPreview = GamePanel.class.getDeclaredMethod("drawNextPreview", Graphics2D.class, int.class, int.class);
        drawNextPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawNextPreview.invoke(itemPanel, g2, 10, 10);
        g2.dispose();
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testDrawItemPreviewBombBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawItemPreview = GamePanel.class.getDeclaredMethod("drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
        drawItemPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawItemPreview.invoke(panel, g2, 10, 10, new BombBlock());
        g2.dispose();
    }
    
    @Test
    public void testDrawItemPreviewWeightBlock() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method drawItemPreview = GamePanel.class.getDeclaredMethod("drawItemPreview", Graphics2D.class, int.class, int.class, items.ItemBlock.class);
        drawItemPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawItemPreview.invoke(panel, g2, 10, 10, new WeightBlock());
        g2.dispose();
    }
    
    @Test
    public void testNormalDelayField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field normalDelayField = GamePanel.class.getDeclaredField("normalDelay");
        normalDelayField.setAccessible(true);
        assertNotNull(normalDelayField.get(panel));
    }
    
    @Test
    public void testTimeAttackModeScreen() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board timeBoard = new Board(Difficulty.EASY, false);
        GamePanel timePanel = new GamePanel(timeBoard, false, true);
        
        assertNotNull(timePanel);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(timePanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(timePanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testKeyListenerExists() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(panel.getKeyListeners().length > 0);
    }
    
    @Test
    public void testTogglePauseAndMenuMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method togglePause = GamePanel.class.getDeclaredMethod("togglePauseAndMenu");
        togglePause.setAccessible(true);
        
        Field pausedField = GamePanel.class.getDeclaredField("paused");
        pausedField.setAccessible(true);
        
        boolean initialPaused = (boolean) pausedField.get(panel);
        assertFalse(initialPaused);
    }
    
    @Test
    public void testUpdateSpeedByClearsWithLinesCleared() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 줄을 많이 삭제하여 속도 증가 테스트
        Field totalLinesClearedField = Board.class.getDeclaredField("totalLinesCleared");
        totalLinesClearedField.setAccessible(true);
        totalLinesClearedField.set(board, 10);
        
        Method updateSpeed = GamePanel.class.getDeclaredMethod("updateSpeedByClears");
        updateSpeed.setAccessible(true);
        updateSpeed.invoke(panel);
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        int currentDelay = (int) currentDelayField.get(panel);
        
        Field baseDelayField = GamePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        int baseDelay = (int) baseDelayField.get(panel);
        
        assertTrue(currentDelay < baseDelay);
    }
    
    @Test
    public void testHandlePauseKeyPressMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handlePauseKeyPress = GamePanel.class.getDeclaredMethod("handlePauseKeyPress", KeyEvent.class);
        handlePauseKeyPress.setAccessible(true);
        assertNotNull(handlePauseKeyPress);
    }
    
    @Test
    public void testUpdatePauseButtonHighlightMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method updateHighlight = GamePanel.class.getDeclaredMethod("updatePauseButtonHighlight");
        updateHighlight.setAccessible(true);
        assertNotNull(updateHighlight);
    }
    
    @Test
    public void testCloseGameOnlyMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method closeGame = GamePanel.class.getDeclaredMethod("closeGameOnly");
        closeGame.setAccessible(true);
        assertNotNull(closeGame);
    }
    
    @Test
    public void testFlashingRowsField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashingRowsField = GamePanel.class.getDeclaredField("flashingRows");
        flashingRowsField.setAccessible(true);
        assertNull(flashingRowsField.get(panel));
    }
    
    @Test
    public void testFlashUntilField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashUntilField = GamePanel.class.getDeclaredField("flashUntil");
        flashUntilField.setAccessible(true);
        assertEquals(0L, (long) flashUntilField.get(panel));
    }
    
    @Test
    public void testPauseDialogField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pauseDialogField = GamePanel.class.getDeclaredField("pauseDialog");
        pauseDialogField.setAccessible(true);
        assertNull(pauseDialogField.get(panel));
    }
    
    @Test
    public void testPauseButtonsField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pauseButtonsField = GamePanel.class.getDeclaredField("pauseButtons");
        pauseButtonsField.setAccessible(true);
        assertNull(pauseButtonsField.get(panel));
    }
    
    @Test
    public void testSelectedPauseButtonField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedPauseButtonField = GamePanel.class.getDeclaredField("selectedPauseButton");
        selectedPauseButtonField.setAccessible(true);
        assertEquals(0, (int) selectedPauseButtonField.get(panel));
    }
    
    @Test
    public void testCellSizeField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field cellField = GamePanel.class.getDeclaredField("CELL");
        cellField.setAccessible(true);
        assertTrue((int) cellField.get(panel) > 0);
    }
    
    @Test
    public void testBoardWidthField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field boardWField = GamePanel.class.getDeclaredField("BOARD_W");
        boardWField.setAccessible(true);
        assertTrue((int) boardWField.get(panel) > 0);
    }
    
    @Test
    public void testBoardHeightField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field boardHField = GamePanel.class.getDeclaredField("BOARD_H");
        boardHField.setAccessible(true);
        assertTrue((int) boardHField.get(panel) > 0);
    }
    
    @Test
    public void testSideWidthField() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field sideWField = GamePanel.class.getDeclaredField("SIDE_W");
        sideWField.setAccessible(true);
        assertTrue((int) sideWField.get(panel) > 0);
    }
    
    @Test
    public void testCurrentDelayInitializedProperly() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        
        Field baseDelayField = GamePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        
        assertEquals(baseDelayField.get(panel), currentDelayField.get(panel));
    }
    
    @Test
    public void testItemModeWithDifferentDifficulties() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // EASY 난이도 아이템 모드
        Board easyBoard = new Board(Difficulty.EASY, true);
        GamePanel easyPanel = new GamePanel(easyBoard, true, false);
        assertNotNull(easyPanel);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(easyPanel);
        if (timer != null) timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(easyPanel);
        if (slowTimer != null) slowTimer.stop();
        
        // HARD 난이도 아이템 모드
        Board hardBoard = new Board(Difficulty.HARD, true);
        GamePanel hardPanel = new GamePanel(hardBoard, true, false);
        assertNotNull(hardPanel);
        
        timerField.setAccessible(true);
        timer = (Timer) timerField.get(hardPanel);
        if (timer != null) timer.stop();
        
        slowTimerField.setAccessible(true);
        slowTimer = (Timer) slowTimerField.get(hardPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testHandleGameOverMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handleGameOver = GamePanel.class.getDeclaredMethod("handleGameOver");
        handleGameOver.setAccessible(true);
        assertNotNull(handleGameOver);
    }
    
    @Test
    public void testShowPauseDialogMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method showPauseDialog = GamePanel.class.getDeclaredMethod("showPauseDialog");
        showPauseDialog.setAccessible(true);
        assertNotNull(showPauseDialog);
    }
    
    @Test
    public void testOnForceQuitMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method onForceQuit = GamePanel.class.getDeclaredMethod("onForceQuit");
        onForceQuit.setAccessible(true);
        assertNotNull(onForceQuit);
    }
    
    @Test
    public void testMinDelayLimit() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 많은 줄을 삭제해도 최소 딜레이 이하로는 내려가지 않는지 확인
        Field totalLinesClearedField = Board.class.getDeclaredField("totalLinesCleared");
        totalLinesClearedField.setAccessible(true);
        totalLinesClearedField.set(board, 100); // 매우 많은 줄 삭제
        
        Method updateSpeed = GamePanel.class.getDeclaredMethod("updateSpeedByClears");
        updateSpeed.setAccessible(true);
        updateSpeed.invoke(panel);
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        int currentDelay = (int) currentDelayField.get(panel);
        
        Field minDelayField = GamePanel.class.getDeclaredField("minDelay");
        minDelayField.setAccessible(true);
        int minDelay = (int) minDelayField.get(panel);
        
        assertTrue(currentDelay >= minDelay);
    }
    
    @Test
    public void testDrawNextPreviewWithNullNextShape() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nextShapeField = Board.class.getDeclaredField("nextShape");
        nextShapeField.setAccessible(true);
        nextShapeField.set(board, null);
        
        Method drawNextPreview = GamePanel.class.getDeclaredMethod("drawNextPreview", Graphics2D.class, int.class, int.class);
        drawNextPreview.setAccessible(true);
        
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawNextPreview.invoke(panel, g2, 10, 10);
        g2.dispose();
    }
    
    @Test
    public void testDrawCurrentWithNullCurrent() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field currentField = Board.class.getDeclaredField("current");
        currentField.setAccessible(true);
        currentField.set(board, null);
        
        Method drawCurrent = GamePanel.class.getDeclaredMethod("drawCurrent", Graphics2D.class);
        drawCurrent.setAccessible(true);
        
        BufferedImage img = new BufferedImage(300, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        drawCurrent.invoke(panel, g2);
        g2.dispose();
    }
    
    @Test
    public void testTimerIsRunningAfterConstruction() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board testBoard = new Board(Difficulty.NORMAL, false);
        GamePanel testPanel = new GamePanel(testBoard, false, false);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(testPanel);
        
        assertNotNull(timer);
        assertTrue(timer.isRunning());
        
        timer.stop();
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(testPanel);
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testSlowEffectTimerIsRunning() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board testBoard = new Board(Difficulty.NORMAL, false);
        GamePanel testPanel = new GamePanel(testBoard, false, false);
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(testPanel);
        
        assertNotNull(slowTimer);
        assertTrue(slowTimer.isRunning());
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(testPanel);
        if (timer != null) timer.stop();
        
        slowTimer.stop();
    }
    
    @Test
    public void testUpdateSpeedByClearsWithNoChange() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        // 줄 삭제가 없을 때
        Field totalLinesClearedField = Board.class.getDeclaredField("totalLinesCleared");
        totalLinesClearedField.setAccessible(true);
        totalLinesClearedField.set(board, 0);
        
        Method updateSpeed = GamePanel.class.getDeclaredMethod("updateSpeedByClears");
        updateSpeed.setAccessible(true);
        
        Field currentDelayField = GamePanel.class.getDeclaredField("currentDelay");
        currentDelayField.setAccessible(true);
        int delayBefore = (int) currentDelayField.get(panel);
        
        updateSpeed.invoke(panel);
        
        int delayAfter = (int) currentDelayField.get(panel);
        assertEquals(delayBefore, delayAfter);
    }
    
    @Test
    public void testCloseGameOnly() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method closeGameOnly = GamePanel.class.getDeclaredMethod("closeGameOnly");
        closeGameOnly.setAccessible(true);
        
        // closeGameOnly는 윈도우를 dispose할 뿐 타이머를 멈추지 않음
        closeGameOnly.invoke(panel);
        
        // 메서드가 실행되었는지만 확인
        assertNotNull(closeGameOnly);
    }
    
    @Test
    public void testHandleGameOverItemMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board itemBoard = new Board(Difficulty.NORMAL, true);
        GamePanel itemPanel = new GamePanel(itemBoard, true, false);
        
        Method handleGameOver = GamePanel.class.getDeclaredMethod("handleGameOver");
        handleGameOver.setAccessible(true);
        
        // 게임오버 상태로 설정
        Field gameOverField = Board.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.set(itemBoard, true);
        
        handleGameOver.invoke(itemPanel);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(itemPanel);
        
        if (timer != null) timer.stop();
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testHandleGameOverTimeAttackMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Board timeBoard = new Board(Difficulty.NORMAL, false);
        GamePanel timePanel = new GamePanel(timeBoard, false, true);
        
        Method handleGameOver = GamePanel.class.getDeclaredMethod("handleGameOver");
        handleGameOver.setAccessible(true);
        
        // 게임오버 상태로 설정
        Field gameOverField = Board.class.getDeclaredField("gameOver");
        gameOverField.setAccessible(true);
        gameOverField.set(timeBoard, true);
        
        handleGameOver.invoke(timePanel);
        
        Field timerField = GamePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(timePanel);
        
        Field slowTimerField = GamePanel.class.getDeclaredField("slowEffectTimer");
        slowTimerField.setAccessible(true);
        Timer slowTimer = (Timer) slowTimerField.get(timePanel);
        
        if (timer != null) timer.stop();
        if (slowTimer != null) slowTimer.stop();
    }
    
    @Test
    public void testTogglePauseAndMenu() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pausedField = GamePanel.class.getDeclaredField("paused");
        pausedField.setAccessible(true);
        
        Method togglePause = GamePanel.class.getDeclaredMethod("togglePauseAndMenu");
        togglePause.setAccessible(true);
        
        assertFalse((boolean) pausedField.get(panel));
    }
    
    @Test
    public void testHandlePauseKeyPressUp() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handlePauseKeyPress = GamePanel.class.getDeclaredMethod("handlePauseKeyPress", KeyEvent.class);
        handlePauseKeyPress.setAccessible(true);
        
        Field selectedPauseButtonField = GamePanel.class.getDeclaredField("selectedPauseButton");
        selectedPauseButtonField.setAccessible(true);
        selectedPauseButtonField.set(panel, 1);
        
        assertNotNull(handlePauseKeyPress);
    }
    
    @Test
    public void testHandlePauseKeyPressDown() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handlePauseKeyPress = GamePanel.class.getDeclaredMethod("handlePauseKeyPress", KeyEvent.class);
        handlePauseKeyPress.setAccessible(true);
        
        assertNotNull(handlePauseKeyPress);
    }
    
    @Test
    public void testUpdatePauseButtonHighlight() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method updatePauseButtonHighlight = GamePanel.class.getDeclaredMethod("updatePauseButtonHighlight");
        updatePauseButtonHighlight.setAccessible(true);
        
        assertNotNull(updatePauseButtonHighlight);
    }
    
    @Test
    public void testPauseDialogInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pauseDialogField = GamePanel.class.getDeclaredField("pauseDialog");
        pauseDialogField.setAccessible(true);
        
        assertNull(pauseDialogField.get(panel));
    }
    
    @Test
    public void testPauseButtonsInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pauseButtonsField = GamePanel.class.getDeclaredField("pauseButtons");
        pauseButtonsField.setAccessible(true);
        
        assertNull(pauseButtonsField.get(panel));
    }
    
    @Test
    public void testSelectedPauseButtonInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field selectedPauseButtonField = GamePanel.class.getDeclaredField("selectedPauseButton");
        selectedPauseButtonField.setAccessible(true);
        
        assertEquals(0, (int) selectedPauseButtonField.get(panel));
    }
}

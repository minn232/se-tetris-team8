package screens;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}

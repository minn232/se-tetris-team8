package screens;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

public class P2PBattlePanelTest {
    private P2PBattlePanel panel;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;

        // 생성자 4번째 인자 isHost=true 추가
        panel = new P2PBattlePanel(Difficulty.NORMAL, false, false, true);
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (panel != null) {
            try {
                Field timerField = P2PBattlePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(panel);
                if (timer != null) timer.stop();
            } catch (Exception ignored) {}
        }
    }
    
    @Test
    public void testConstructorNormalMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(panel);
        assertTrue(panel.isFocusable());
        
        Field isTimeAttackField = P2PBattlePanel.class.getDeclaredField("isTimeAttack");
        isTimeAttackField.setAccessible(true);
        assertFalse((boolean) isTimeAttackField.get(panel));
    }
    
    @Test
    public void testConstructorItemMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        P2PBattlePanel itemPanel =
                new P2PBattlePanel(Difficulty.NORMAL, true, false, true);

        assertNotNull(itemPanel);
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(itemPanel);
        assertTrue(myBoard.isItemMode());
        
        Field timerField = P2PBattlePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(itemPanel);
        timer.stop();
    }
    
    @Test
    public void testConstructorTimeAttackMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        P2PBattlePanel timePanel =
                new P2PBattlePanel(Difficulty.NORMAL, false, true, true);

        assertNotNull(timePanel);
        
        Field isTimeAttackField = P2PBattlePanel.class.getDeclaredField("isTimeAttack");
        isTimeAttackField.setAccessible(true);
        assertTrue((boolean) isTimeAttackField.get(timePanel));
        
        Field timeLimitField = P2PBattlePanel.class.getDeclaredField("timeLimit");
        timeLimitField.setAccessible(true);
        long timeLimit = (long) timeLimitField.get(timePanel);
        assertEquals(180000L, timeLimit);
        
        Field timerField = P2PBattlePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(timePanel);
        timer.stop();
    }
    
    @Test
    public void testBoardsInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        assertNotNull(myBoardField.get(panel));
        
        Field enemyBoardField = P2PBattlePanel.class.getDeclaredField("enemyBoard");
        enemyBoardField.setAccessible(true);
        assertNotNull(enemyBoardField.get(panel));
    }
    
    @Test
    public void testBaseDelay() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field baseDelayField = P2PBattlePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        assertEquals(800, baseDelayField.get(panel));
    }
    
    @Test
    public void testTimerInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field timerField = P2PBattlePanel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        Timer timer = (Timer) timerField.get(panel);
        
        assertNotNull(timer);
        assertTrue(timer.isRunning());
    }
    
    @Test
    public void testPausedInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pausedField = P2PBattlePanel.class.getDeclaredField("paused");
        pausedField.setAccessible(true);
        assertFalse((boolean) pausedField.get(panel));
    }
    
    @Test
    public void testWinnerInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field winnerField = P2PBattlePanel.class.getDeclaredField("winner");
        winnerField.setAccessible(true);
        assertNull(winnerField.get(panel));
    }
    
    @Test
    public void testFlashConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashMsField = P2PBattlePanel.class.getDeclaredField("FLASH_MS");
        flashMsField.setAccessible(true);
        assertEquals(150L, flashMsField.get(null));
    }
    
    @Test
    public void testFlashingRowsInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashingRowsMyField = P2PBattlePanel.class.getDeclaredField("flashingRowsMy");
        flashingRowsMyField.setAccessible(true);
        assertNull(flashingRowsMyField.get(panel));
        
        Field flashingRowsEnemyField = P2PBattlePanel.class.getDeclaredField("flashingRowsEnemy");
        flashingRowsEnemyField.setAccessible(true);
        assertNull(flashingRowsEnemyField.get(panel));
    }
    
    @Test
    public void testChatUIInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatAreaField = P2PBattlePanel.class.getDeclaredField("chatArea");
        chatAreaField.setAccessible(true);
        javax.swing.JTextArea chatArea = (javax.swing.JTextArea) chatAreaField.get(panel);
        
        assertNotNull(chatArea);
        assertFalse(chatArea.isEditable());
        
        Field chatInputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        chatInputField.setAccessible(true);
        assertNotNull(chatInputField.get(panel));
        
        Field chatSendBtnField = P2PBattlePanel.class.getDeclaredField("chatSendBtn");
        chatSendBtnField.setAccessible(true);
        assertEquals("Send",
            ((javax.swing.JButton) chatSendBtnField.get(panel)).getText());
    }
    
    @Test
    public void testNetworkStatusInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field lastRTTField = P2PBattlePanel.class.getDeclaredField("lastRTT");
        lastRTTField.setAccessible(true);
        assertEquals(0L, lastRTTField.get(panel));
        
        Field isLaggingField = P2PBattlePanel.class.getDeclaredField("isLagging");
        isLaggingField.setAccessible(true);
        assertFalse((boolean) isLaggingField.get(panel));
        
        Field connectionLostField = P2PBattlePanel.class.getDeclaredField("connectionLost");
        connectionLostField.setAccessible(true);
        assertFalse((boolean) connectionLostField.get(panel));
    }
    
    @Test
    public void testCheckNetworkStatusMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("checkNetworkStatus");
        assertNotNull(m);
    }
    
    @Test
    public void testCheckBlockPlacementMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("checkBlockPlacement");
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel));
    }
    
    @Test
    public void testHandleKeyPressMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        assertNotNull(m);
    }
    
    @Test
    public void testCheckFlashingMethodWithNoRows() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field boardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        boardField.setAccessible(true);
        Board b = (Board) boardField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("checkFlashing", Board.class, boolean.class);
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel, b, true));
    }
    
    @Test
    public void testTogglePauseMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("togglePause");
        assertNotNull(m);
    }
    
    @Test
    public void testSendBoardStateMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("sendBoardState");
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel));
    }
    
    @Test
    public void testSendAttackPatternMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        List<ShapeType[]> pattern = new ArrayList<>();
        pattern.add(new ShapeType[]{ShapeType.I, null, ShapeType.O, null, null, null, null, null, null, null});
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("sendAttackPattern", List.class);
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel, pattern));
    }
    
    @Test
    public void testHandleNetworkMessageChat() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatAreaField = P2PBattlePanel.class.getDeclaredField("chatArea");
        chatAreaField.setAccessible(true);
        javax.swing.JTextArea chatArea = (javax.swing.JTextArea) chatAreaField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("handleNetworkMessage", String.class);
        m.setAccessible(true);
        m.invoke(panel, "CHAT:Hello");
        
        assertTrue(chatArea.getText().contains("ENEMY: Hello"));
    }
    
    @Test
    public void testHandleNetworkMessageGameOver() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("handleNetworkMessage", String.class);
        m.setAccessible(true);
        
        assertDoesNotThrow(() -> m.invoke(panel, "GAMEOVER"));
    }
    
    @Test
    public void testUpdateEnemyBoardMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String json = "{\"grid\":[[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]],\"cur\":null,\"score\":100}";
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("updateEnemyBoard", String.class);
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel, json));
    }
    
    @Test
    public void testSendChatWithEmptyMessage() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatInputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        chatInputField.setAccessible(true);
        
        javax.swing.JTextField chatInput = (javax.swing.JTextField) chatInputField.get(panel);
        chatInput.setText("");
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("sendChat");
        m.setAccessible(true);
        assertDoesNotThrow(() -> m.invoke(panel));
        assertEquals("", chatInput.getText());
    }
    
    @Test
    public void testSendChatWithMessage() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatInputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        chatInputField.setAccessible(true);
        javax.swing.JTextField chatInput = (javax.swing.JTextField) chatInputField.get(panel);
        chatInput.setText("Test message");
        
        Field chatAreaField = P2PBattlePanel.class.getDeclaredField("chatArea");
        chatAreaField.setAccessible(true);
        javax.swing.JTextArea chatArea = (javax.swing.JTextArea) chatAreaField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("sendChat");
        m.setAccessible(true);
        m.invoke(panel);
        
        assertTrue(chatArea.getText().contains("ME: Test message"));
        assertEquals("", chatInput.getText());
    }
    
    @Test
    public void testParseAttackPatternMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String json = "{\"rows\":[[\"I\",\"0\",\"O\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]]}";
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("parseAttackPattern", String.class);
        m.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        List<ShapeType[]> result = (List<ShapeType[]>) m.invoke(panel, json);
        
        assertNotNull(result);
    }
    
    @Test
    public void testPaintComponent() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        assertDoesNotThrow(() -> panel.paintComponent(g2));
        g2.dispose();
    }
    
    @Test
    public void testFillCellMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("fillCell",
                java.awt.Graphics2D.class, int.class, int.class, Color.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2, 0, 0, Color.RED));
        g2.dispose();
    }
    
    @Test
    public void testDrawBoardMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field boardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        boardField.setAccessible(true);
        Board b = (Board) boardField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawBoard",
                java.awt.Graphics2D.class, Board.class, int[].class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2, b, null));
        g2.dispose();
    }
    
    @Test
    public void testDrawCurrentMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field boardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        boardField.setAccessible(true);
        Board b = (Board) boardField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawCurrent",
                java.awt.Graphics2D.class, Board.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2, b));
        g2.dispose();
    }
    
    @Test
    public void testDrawGridMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawGrid",
                java.awt.Graphics2D.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2));
        g2.dispose();
    }
    
    @Test
    public void testDrawSidebarMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field boardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        boardField.setAccessible(true);
        Board b = (Board) boardField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawSidebar",
                java.awt.Graphics2D.class, Board.class, int.class, String.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2, b, 0, "YOU"));
        g2.dispose();
    }
    
    @Test
    public void testDrawNextPreviewMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field boardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        boardField.setAccessible(true);
        Board b = (Board) boardField.get(panel);
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawNextPreview",
                java.awt.Graphics2D.class, int.class, int.class, Board.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2, 100, 100, b));
        g2.dispose();
    }
    
    @Test
    public void testDrawPausedMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawPaused",
                java.awt.Graphics2D.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2));
        g2.dispose();
    }
    
    @Test
    public void testDrawWinnerMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field winnerField = P2PBattlePanel.class.getDeclaredField("winner");
        winnerField.setAccessible(true);
        winnerField.set(panel, "YOU WIN");
        
        Method m = P2PBattlePanel.class.getDeclaredMethod("drawWinner",
                java.awt.Graphics2D.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(panel, g2));
        g2.dispose();
    }
    
    @Test
    public void testCELLConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field f = P2PBattlePanel.class.getDeclaredField("CELL");
        f.setAccessible(true);
        assertTrue((int) f.get(panel) > 0);
    }
    
    @Test
    public void testBoardDimensions() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field fw = P2PBattlePanel.class.getDeclaredField("BOARD_W");
        fw.setAccessible(true);
        assertTrue((int) fw.get(panel) > 0);
        
        Field fh = P2PBattlePanel.class.getDeclaredField("BOARD_H");
        fh.setAccessible(true);
        assertTrue((int) fh.get(panel) > 0);
    }
    
    @Test
    public void testSideWidthAndGap() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field fw = P2PBattlePanel.class.getDeclaredField("SIDE_W");
        fw.setAccessible(true);
        assertTrue((int) fw.get(panel) > 0);
        
        Field fg = P2PBattlePanel.class.getDeclaredField("GAP");
        fg.setAccessible(true);
        assertTrue((int) fg.get(panel) > 0);
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(panel.getKeyListeners().length > 0);
    }
    
    @Test
    public void testActionListenersOnChatComponents() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field sendBtn = P2PBattlePanel.class.getDeclaredField("chatSendBtn");
        sendBtn.setAccessible(true);
        javax.swing.JButton b1 = (javax.swing.JButton) sendBtn.get(panel);
        assertTrue(b1.getActionListeners().length > 0);
        
        Field inputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        inputField.setAccessible(true);
        javax.swing.JTextField b2 = (javax.swing.JTextField) inputField.get(panel);
        assertTrue(b2.getActionListeners().length > 0);
    }
    
    @Test
    public void testGameStartTimeInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field f = P2PBattlePanel.class.getDeclaredField("gameStartTime");
        f.setAccessible(true);
        assertTrue((long) f.get(panel) > 0);
    }
    
    @Test
    public void testPausedTimeInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field f = P2PBattlePanel.class.getDeclaredField("pausedTime");
        f.setAccessible(true);
        assertEquals(0L, (long) f.get(panel));
    }
    
    @Test
    public void testLastCurrentMyInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field f = P2PBattlePanel.class.getDeclaredField("lastCurrentMy");
        f.setAccessible(true);
        assertNotNull(f.get(panel));
    }
}

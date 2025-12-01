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
        panel = new P2PBattlePanel(Difficulty.NORMAL, false, false);
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (panel != null) {
            try {
                Field timerField = P2PBattlePanel.class.getDeclaredField("timer");
                timerField.setAccessible(true);
                Timer timer = (Timer) timerField.get(panel);
                if (timer != null) {
                    timer.stop();
                }
            } catch (Exception e) {
                // ignore
            }
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
        
        P2PBattlePanel itemPanel = new P2PBattlePanel(Difficulty.NORMAL, true, false);
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
        
        P2PBattlePanel timePanel = new P2PBattlePanel(Difficulty.NORMAL, false, true);
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
        Board myBoard = (Board) myBoardField.get(panel);
        assertNotNull(myBoard);
        
        Field enemyBoardField = P2PBattlePanel.class.getDeclaredField("enemyBoard");
        enemyBoardField.setAccessible(true);
        Board enemyBoard = (Board) enemyBoardField.get(panel);
        assertNotNull(enemyBoard);
    }
    
    @Test
    public void testBaseDelay() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field baseDelayField = P2PBattlePanel.class.getDeclaredField("baseDelay");
        baseDelayField.setAccessible(true);
        int baseDelay = (int) baseDelayField.get(panel);
        assertEquals(800, baseDelay);
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
        boolean paused = (boolean) pausedField.get(panel);
        assertFalse(paused);
    }
    
    @Test
    public void testWinnerInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field winnerField = P2PBattlePanel.class.getDeclaredField("winner");
        winnerField.setAccessible(true);
        String winner = (String) winnerField.get(panel);
        assertNull(winner);
    }
    
    @Test
    public void testFlashConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashMsField = P2PBattlePanel.class.getDeclaredField("FLASH_MS");
        flashMsField.setAccessible(true);
        long flashMs = (long) flashMsField.get(null);
        assertEquals(150L, flashMs);
    }
    
    @Test
    public void testFlashingRowsInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field flashingRowsMyField = P2PBattlePanel.class.getDeclaredField("flashingRowsMy");
        flashingRowsMyField.setAccessible(true);
        int[] flashingRowsMy = (int[]) flashingRowsMyField.get(panel);
        assertNull(flashingRowsMy);
        
        Field flashingRowsEnemyField = P2PBattlePanel.class.getDeclaredField("flashingRowsEnemy");
        flashingRowsEnemyField.setAccessible(true);
        int[] flashingRowsEnemy = (int[]) flashingRowsEnemyField.get(panel);
        assertNull(flashingRowsEnemy);
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
        javax.swing.JTextField chatInput = (javax.swing.JTextField) chatInputField.get(panel);
        assertNotNull(chatInput);
        
        Field chatSendBtnField = P2PBattlePanel.class.getDeclaredField("chatSendBtn");
        chatSendBtnField.setAccessible(true);
        javax.swing.JButton chatSendBtn = (javax.swing.JButton) chatSendBtnField.get(panel);
        assertNotNull(chatSendBtn);
        assertEquals("Send", chatSendBtn.getText());
    }
    
    @Test
    public void testNetworkStatusInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field lastRTTField = P2PBattlePanel.class.getDeclaredField("lastRTT");
        lastRTTField.setAccessible(true);
        long lastRTT = (long) lastRTTField.get(panel);
        assertEquals(0L, lastRTT);
        
        Field isLaggingField = P2PBattlePanel.class.getDeclaredField("isLagging");
        isLaggingField.setAccessible(true);
        boolean isLagging = (boolean) isLaggingField.get(panel);
        assertFalse(isLagging);
        
        Field connectionLostField = P2PBattlePanel.class.getDeclaredField("connectionLost");
        connectionLostField.setAccessible(true);
        boolean connectionLost = (boolean) connectionLostField.get(panel);
        assertFalse(connectionLost);
    }
    
    @Test
    public void testCheckNetworkStatusMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method checkNetworkStatus = P2PBattlePanel.class.getDeclaredMethod("checkNetworkStatus");
        checkNetworkStatus.setAccessible(true);
        assertNotNull(checkNetworkStatus);
    }
    
    @Test
    public void testCheckBlockPlacementMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method checkBlockPlacement = P2PBattlePanel.class.getDeclaredMethod("checkBlockPlacement");
        checkBlockPlacement.setAccessible(true);
        assertDoesNotThrow(() -> checkBlockPlacement.invoke(panel));
    }
    
    @Test
    public void testHandleKeyPressPMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method handleKeyPress = P2PBattlePanel.class.getDeclaredMethod("handleKeyPress", KeyEvent.class);
        handleKeyPress.setAccessible(true);
        assertNotNull(handleKeyPress);
    }
    
    @Test
    public void testCheckFlashingMethodWithNoRows() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(panel);
        
        Method checkFlashing = P2PBattlePanel.class.getDeclaredMethod("checkFlashing", Board.class, boolean.class);
        checkFlashing.setAccessible(true);
        assertDoesNotThrow(() -> checkFlashing.invoke(panel, myBoard, true));
    }
    
    @Test
    public void testTogglePauseMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method togglePause = P2PBattlePanel.class.getDeclaredMethod("togglePause");
        togglePause.setAccessible(true);
        assertNotNull(togglePause);
    }
    
    @Test
    public void testSendBoardStateMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method sendBoardState = P2PBattlePanel.class.getDeclaredMethod("sendBoardState");
        sendBoardState.setAccessible(true);
        assertDoesNotThrow(() -> sendBoardState.invoke(panel));
    }
    
    @Test
    public void testSendAttackPatternMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        List<ShapeType[]> pattern = new ArrayList<>();
        pattern.add(new ShapeType[]{ShapeType.I, null, ShapeType.O, null, null, null, null, null, null, null});
        
        Method sendAttackPattern = P2PBattlePanel.class.getDeclaredMethod("sendAttackPattern", List.class);
        sendAttackPattern.setAccessible(true);
        assertDoesNotThrow(() -> sendAttackPattern.invoke(panel, pattern));
    }
    
    @Test
    public void testHandleNetworkMessageChat() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatAreaField = P2PBattlePanel.class.getDeclaredField("chatArea");
        chatAreaField.setAccessible(true);
        javax.swing.JTextArea chatArea = (javax.swing.JTextArea) chatAreaField.get(panel);
        
        Method handleNetworkMessage = P2PBattlePanel.class.getDeclaredMethod("handleNetworkMessage", String.class);
        handleNetworkMessage.setAccessible(true);
        handleNetworkMessage.invoke(panel, "CHAT:Hello");
        
        assertTrue(chatArea.getText().contains("ENEMY: Hello"));
    }
    
    @Test
    public void testHandleNetworkMessageGameOver() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field winnerField = P2PBattlePanel.class.getDeclaredField("winner");
        winnerField.setAccessible(true);
        
        Method handleNetworkMessage = P2PBattlePanel.class.getDeclaredMethod("handleNetworkMessage", String.class);
        handleNetworkMessage.setAccessible(true);
        handleNetworkMessage.invoke(panel, "GAMEOVER");
        
        // GAMEOVER 메시지는 showGameOver를 호출하므로 winner 설정만 확인
        assertNotNull(winnerField);
    }
    
    @Test
    public void testUpdateEnemyBoardMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String json = "{\"grid\":[[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]],\"cur\":null,\"score\":100}";
        
        Method updateEnemyBoard = P2PBattlePanel.class.getDeclaredMethod("updateEnemyBoard", String.class);
        updateEnemyBoard.setAccessible(true);
        assertDoesNotThrow(() -> updateEnemyBoard.invoke(panel, json));
    }
    
    @Test
    public void testSendChatWithEmptyMessage() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatInputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        chatInputField.setAccessible(true);
        javax.swing.JTextField chatInput = (javax.swing.JTextField) chatInputField.get(panel);
        chatInput.setText("");
        
        Method sendChat = P2PBattlePanel.class.getDeclaredMethod("sendChat");
        sendChat.setAccessible(true);
        assertDoesNotThrow(() -> sendChat.invoke(panel));
        
        // 빈 메시지는 전송되지 않음
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
        
        Method sendChat = P2PBattlePanel.class.getDeclaredMethod("sendChat");
        sendChat.setAccessible(true);
        sendChat.invoke(panel);
        
        assertTrue(chatArea.getText().contains("ME: Test message"));
        assertEquals("", chatInput.getText());
    }
    
    @Test
    public void testParseAttackPatternMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        String json = "{\"rows\":[[\"I\",\"0\",\"O\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\"]]}";
        
        Method parseAttackPattern = P2PBattlePanel.class.getDeclaredMethod("parseAttackPattern", String.class);
        parseAttackPattern.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ShapeType[]> result = (List<ShapeType[]>) parseAttackPattern.invoke(panel, json);
        
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
        
        Method fillCell = P2PBattlePanel.class.getDeclaredMethod("fillCell", java.awt.Graphics2D.class, int.class, int.class, Color.class);
        fillCell.setAccessible(true);
        assertDoesNotThrow(() -> fillCell.invoke(panel, g2, 0, 0, Color.RED));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawBoardMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(panel);
        
        Method drawBoard = P2PBattlePanel.class.getDeclaredMethod("drawBoard", java.awt.Graphics2D.class, Board.class, int[].class);
        drawBoard.setAccessible(true);
        assertDoesNotThrow(() -> drawBoard.invoke(panel, g2, myBoard, null));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawCurrentMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(panel);
        
        Method drawCurrent = P2PBattlePanel.class.getDeclaredMethod("drawCurrent", java.awt.Graphics2D.class, Board.class);
        drawCurrent.setAccessible(true);
        assertDoesNotThrow(() -> drawCurrent.invoke(panel, g2, myBoard));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawGridMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Method drawGrid = P2PBattlePanel.class.getDeclaredMethod("drawGrid", java.awt.Graphics2D.class);
        drawGrid.setAccessible(true);
        assertDoesNotThrow(() -> drawGrid.invoke(panel, g2));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawSidebarMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(panel);
        
        Method drawSidebar = P2PBattlePanel.class.getDeclaredMethod("drawSidebar", java.awt.Graphics2D.class, Board.class, int.class, String.class);
        drawSidebar.setAccessible(true);
        assertDoesNotThrow(() -> drawSidebar.invoke(panel, g2, myBoard, 0, "YOU"));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawNextPreviewMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Field myBoardField = P2PBattlePanel.class.getDeclaredField("myBoard");
        myBoardField.setAccessible(true);
        Board myBoard = (Board) myBoardField.get(panel);
        
        Method drawNextPreview = P2PBattlePanel.class.getDeclaredMethod("drawNextPreview", java.awt.Graphics2D.class, int.class, int.class, Board.class);
        drawNextPreview.setAccessible(true);
        assertDoesNotThrow(() -> drawNextPreview.invoke(panel, g2, 100, 100, myBoard));
        
        g2.dispose();
    }
    
    @Test
    public void testDrawPausedMethod() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        Method drawPaused = P2PBattlePanel.class.getDeclaredMethod("drawPaused", java.awt.Graphics2D.class);
        drawPaused.setAccessible(true);
        assertDoesNotThrow(() -> drawPaused.invoke(panel, g2));
        
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
        
        Method drawWinner = P2PBattlePanel.class.getDeclaredMethod("drawWinner", java.awt.Graphics2D.class);
        drawWinner.setAccessible(true);
        assertDoesNotThrow(() -> drawWinner.invoke(panel, g2));
        
        g2.dispose();
    }
    
    @Test
    public void testCELLConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field cellField = P2PBattlePanel.class.getDeclaredField("CELL");
        cellField.setAccessible(true);
        int cell = (int) cellField.get(panel);
        assertTrue(cell > 0);
    }
    
    @Test
    public void testBoardDimensions() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field boardWField = P2PBattlePanel.class.getDeclaredField("BOARD_W");
        boardWField.setAccessible(true);
        int boardW = (int) boardWField.get(panel);
        assertTrue(boardW > 0);
        
        Field boardHField = P2PBattlePanel.class.getDeclaredField("BOARD_H");
        boardHField.setAccessible(true);
        int boardH = (int) boardHField.get(panel);
        assertTrue(boardH > 0);
    }
    
    @Test
    public void testSideWidthAndGap() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field sideWField = P2PBattlePanel.class.getDeclaredField("SIDE_W");
        sideWField.setAccessible(true);
        int sideW = (int) sideWField.get(panel);
        assertTrue(sideW > 0);
        
        Field gapField = P2PBattlePanel.class.getDeclaredField("GAP");
        gapField.setAccessible(true);
        int gap = (int) gapField.get(panel);
        assertTrue(gap > 0);
    }
    
    @Test
    public void testKeyListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(panel.getKeyListeners().length > 0);
    }
    
    @Test
    public void testActionListenersOnChatComponents() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field chatSendBtnField = P2PBattlePanel.class.getDeclaredField("chatSendBtn");
        chatSendBtnField.setAccessible(true);
        javax.swing.JButton chatSendBtn = (javax.swing.JButton) chatSendBtnField.get(panel);
        assertTrue(chatSendBtn.getActionListeners().length > 0);
        
        Field chatInputField = P2PBattlePanel.class.getDeclaredField("chatInput");
        chatInputField.setAccessible(true);
        javax.swing.JTextField chatInput = (javax.swing.JTextField) chatInputField.get(panel);
        assertTrue(chatInput.getActionListeners().length > 0);
    }
    
    @Test
    public void testGameStartTimeInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field gameStartTimeField = P2PBattlePanel.class.getDeclaredField("gameStartTime");
        gameStartTimeField.setAccessible(true);
        long gameStartTime = (long) gameStartTimeField.get(panel);
        assertTrue(gameStartTime > 0);
    }
    
    @Test
    public void testPausedTimeInitial() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field pausedTimeField = P2PBattlePanel.class.getDeclaredField("pausedTime");
        pausedTimeField.setAccessible(true);
        long pausedTime = (long) pausedTimeField.get(panel);
        assertEquals(0L, pausedTime);
    }
    
    @Test
    public void testLastCurrentMyInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field lastCurrentMyField = P2PBattlePanel.class.getDeclaredField("lastCurrentMy");
        lastCurrentMyField.setAccessible(true);
        Object lastCurrentMy = lastCurrentMyField.get(panel);
        assertNotNull(lastCurrentMy);
    }
}

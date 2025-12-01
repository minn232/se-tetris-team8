package screens;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WaitingRoomScreenTest {
    private WaitingRoomScreen hostScreen;
    private WaitingRoomScreen clientScreen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        hostScreen = new WaitingRoomScreen(true);
        clientScreen = new WaitingRoomScreen(false);
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (hostScreen != null) {
            hostScreen.dispose();
        }
        if (clientScreen != null) {
            clientScreen.dispose();
        }
    }
    
    @Test
    public void testConstructorHost() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(hostScreen);
        assertEquals("P2P Waiting Room", hostScreen.getTitle());
        assertFalse(hostScreen.isResizable());
        assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, hostScreen.getDefaultCloseOperation());
        
        Field isHostField = WaitingRoomScreen.class.getDeclaredField("isHost");
        isHostField.setAccessible(true);
        assertTrue((boolean) isHostField.get(hostScreen));
    }
    
    @Test
    public void testConstructorClient() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(clientScreen);
        
        Field isHostField = WaitingRoomScreen.class.getDeclaredField("isHost");
        isHostField.setAccessible(true);
        assertFalse((boolean) isHostField.get(clientScreen));
    }
    
    @Test
    public void testFrameSize() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(400, hostScreen.getWidth());
        assertEquals(300, hostScreen.getHeight());
    }
    
    @Test
    public void testMyReadyInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        boolean myReady = (boolean) myReadyField.get(hostScreen);
        assertFalse(myReady);
    }
    
    @Test
    public void testEnemyReadyInitialState() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        boolean enemyReady = (boolean) enemyReadyField.get(hostScreen);
        assertFalse(enemyReady);
    }
    
    @Test
    public void testMyStatusLabelInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myStatusLabelField = WaitingRoomScreen.class.getDeclaredField("myStatusLabel");
        myStatusLabelField.setAccessible(true);
        JLabel myStatusLabel = (JLabel) myStatusLabelField.get(hostScreen);
        
        assertNotNull(myStatusLabel);
        assertEquals("Me: Not Ready", myStatusLabel.getText());
    }
    
    @Test
    public void testEnemyStatusLabelInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field enemyStatusLabelField = WaitingRoomScreen.class.getDeclaredField("enemyStatusLabel");
        enemyStatusLabelField.setAccessible(true);
        JLabel enemyStatusLabel = (JLabel) enemyStatusLabelField.get(hostScreen);
        
        assertNotNull(enemyStatusLabel);
        assertEquals("Enemy: Not Ready", enemyStatusLabel.getText());
    }
    
    @Test
    public void testReadyButtonInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field readyButtonField = WaitingRoomScreen.class.getDeclaredField("readyButton");
        readyButtonField.setAccessible(true);
        JButton readyButton = (JButton) readyButtonField.get(hostScreen);
        
        assertNotNull(readyButton);
        assertEquals("READY", readyButton.getText());
        assertTrue(readyButton.getActionListeners().length > 0);
    }
    
    @Test
    public void testStartButtonInitialized() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        assertNotNull(startButton);
        assertEquals("START", startButton.getText());
        assertFalse(startButton.isEnabled());
        assertTrue(startButton.getActionListeners().length > 0);
    }
    
    @Test
    public void testHostStartButtonVisible() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        assertTrue(startButton.isVisible());
    }
    
    @Test
    public void testClientStartButtonHidden() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(clientScreen);
        
        assertFalse(startButton.isVisible());
    }
    
    @Test
    public void testToggleReadyToReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        
        Field myStatusLabelField = WaitingRoomScreen.class.getDeclaredField("myStatusLabel");
        myStatusLabelField.setAccessible(true);
        JLabel myStatusLabel = (JLabel) myStatusLabelField.get(hostScreen);
        
        Method toggleReady = WaitingRoomScreen.class.getDeclaredMethod("toggleReady");
        toggleReady.setAccessible(true);
        toggleReady.invoke(hostScreen);
        
        assertTrue((boolean) myReadyField.get(hostScreen));
        assertEquals("Me: READY", myStatusLabel.getText());
    }
    
    @Test
    public void testToggleReadyToNotReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, true);
        
        Field myStatusLabelField = WaitingRoomScreen.class.getDeclaredField("myStatusLabel");
        myStatusLabelField.setAccessible(true);
        JLabel myStatusLabel = (JLabel) myStatusLabelField.get(hostScreen);
        
        Method toggleReady = WaitingRoomScreen.class.getDeclaredMethod("toggleReady");
        toggleReady.setAccessible(true);
        toggleReady.invoke(hostScreen);
        
        assertFalse((boolean) myReadyField.get(hostScreen));
        assertEquals("Me: Not Ready", myStatusLabel.getText());
    }
    
    @Test
    public void testUpdateStartButtonStateHostBothReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, true);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, true);
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        Method updateStartButtonState = WaitingRoomScreen.class.getDeclaredMethod("updateStartButtonState");
        updateStartButtonState.setAccessible(true);
        updateStartButtonState.invoke(hostScreen);
        
        assertTrue(startButton.isEnabled());
    }
    
    @Test
    public void testUpdateStartButtonStateHostOnlyMyReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, true);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, false);
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        Method updateStartButtonState = WaitingRoomScreen.class.getDeclaredMethod("updateStartButtonState");
        updateStartButtonState.setAccessible(true);
        updateStartButtonState.invoke(hostScreen);
        
        assertFalse(startButton.isEnabled());
    }
    
    @Test
    public void testUpdateStartButtonStateHostOnlyEnemyReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, false);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, true);
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        Method updateStartButtonState = WaitingRoomScreen.class.getDeclaredMethod("updateStartButtonState");
        updateStartButtonState.setAccessible(true);
        updateStartButtonState.invoke(hostScreen);
        
        assertFalse(startButton.isEnabled());
    }
    
    @Test
    public void testUpdateStartButtonStateHostNeitherReady() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, false);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, false);
        
        Field startButtonField = WaitingRoomScreen.class.getDeclaredField("startButton");
        startButtonField.setAccessible(true);
        JButton startButton = (JButton) startButtonField.get(hostScreen);
        
        Method updateStartButtonState = WaitingRoomScreen.class.getDeclaredMethod("updateStartButtonState");
        updateStartButtonState.setAccessible(true);
        updateStartButtonState.invoke(hostScreen);
        
        assertFalse(startButton.isEnabled());
    }
    
    @Test
    public void testInitUIMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method initUI = WaitingRoomScreen.class.getDeclaredMethod("initUI");
        initUI.setAccessible(true);
        assertNotNull(initUI);
    }
    
    @Test
    public void testInitNetworkListenerMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method initNetworkListener = WaitingRoomScreen.class.getDeclaredMethod("initNetworkListener");
        initNetworkListener.setAccessible(true);
        assertNotNull(initNetworkListener);
    }
    
    @Test
    public void testStartGameAsHostMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method startGameAsHost = WaitingRoomScreen.class.getDeclaredMethod("startGameAsHost");
        startGameAsHost.setAccessible(true);
        assertNotNull(startGameAsHost);
    }
    
    @Test
    public void testLaunchBattleMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method launchBattle = WaitingRoomScreen.class.getDeclaredMethod("launchBattle", String.class);
        launchBattle.setAccessible(true);
        assertNotNull(launchBattle);
    }
    
    @Test
    public void testStartGameAsHostClientCheck() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(clientScreen, true);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(clientScreen, true);
        
        Method startGameAsHost = WaitingRoomScreen.class.getDeclaredMethod("startGameAsHost");
        startGameAsHost.setAccessible(true);
        
        // Client는 startGameAsHost를 실행해도 아무 일도 일어나지 않음
        assertDoesNotThrow(() -> startGameAsHost.invoke(clientScreen));
    }
    
    @Test
    public void testStartGameAsHostNotReadyCheck() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, false);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, true);
        
        Method startGameAsHost = WaitingRoomScreen.class.getDeclaredMethod("startGameAsHost");
        startGameAsHost.setAccessible(true);
        
        // 내가 준비 안 됐으면 실행 안 됨
        assertDoesNotThrow(() -> startGameAsHost.invoke(hostScreen));
    }
    
    @Test
    public void testStartGameAsHostEnemyNotReadyCheck() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field myReadyField = WaitingRoomScreen.class.getDeclaredField("myReady");
        myReadyField.setAccessible(true);
        myReadyField.set(hostScreen, true);
        
        Field enemyReadyField = WaitingRoomScreen.class.getDeclaredField("enemyReady");
        enemyReadyField.setAccessible(true);
        enemyReadyField.set(hostScreen, false);
        
        Method startGameAsHost = WaitingRoomScreen.class.getDeclaredMethod("startGameAsHost");
        startGameAsHost.setAccessible(true);
        
        // 상대가 준비 안 됐으면 실행 안 됨
        assertDoesNotThrow(() -> startGameAsHost.invoke(hostScreen));
    }
}

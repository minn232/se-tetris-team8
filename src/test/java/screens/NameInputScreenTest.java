package screens;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JTextField;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.Difficulty;

public class NameInputScreenTest {
    private NameInputScreen screen;
    
    @BeforeEach
    public void setUp() {
        if (GraphicsEnvironment.isHeadless()) return;
        screen = new NameInputScreen(10000, Difficulty.NORMAL, false, false);
    }
    
    @AfterEach
    public void tearDown() {
        if (GraphicsEnvironment.isHeadless()) return;
        if (screen != null) {
            screen.dispose();
        }
    }
    
    @Test
    public void testConstructorNormalMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertNotNull(screen);
        assertEquals("New Record!", screen.getTitle());
        
        Field finalScoreField = NameInputScreen.class.getDeclaredField("finalScore");
        finalScoreField.setAccessible(true);
        assertEquals(10000, (int) finalScoreField.get(screen));
        
        Field difficultyField = NameInputScreen.class.getDeclaredField("difficulty");
        difficultyField.setAccessible(true);
        assertEquals(Difficulty.NORMAL, difficultyField.get(screen));
        
        Field isItemModeField = NameInputScreen.class.getDeclaredField("isItemMode");
        isItemModeField.setAccessible(true);
        assertFalse((boolean) isItemModeField.get(screen));
        
        Field isTimeAttackField = NameInputScreen.class.getDeclaredField("isTimeAttackMode");
        isTimeAttackField.setAccessible(true);
        assertFalse((boolean) isTimeAttackField.get(screen));
    }
    
    @Test
    public void testConstructorItemMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen itemScreen = new NameInputScreen(50000, Difficulty.EASY, true, false);
        
        Field isItemModeField = NameInputScreen.class.getDeclaredField("isItemMode");
        isItemModeField.setAccessible(true);
        assertTrue((boolean) isItemModeField.get(itemScreen));
        
        itemScreen.dispose();
    }
    
    @Test
    public void testConstructorTimeAttackMode() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen timeAttackScreen = new NameInputScreen(30000, Difficulty.HARD, false, true);
        
        Field isTimeAttackField = NameInputScreen.class.getDeclaredField("isTimeAttackMode");
        isTimeAttackField.setAccessible(true);
        assertTrue((boolean) isTimeAttackField.get(timeAttackScreen));
        
        timeAttackScreen.dispose();
    }
    
    @Test
    public void testDefaultCloseOperation() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertEquals(javax.swing.JFrame.DO_NOTHING_ON_CLOSE, screen.getDefaultCloseOperation());
    }
    
    @Test
    public void testNameFieldExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nameField = NameInputScreen.class.getDeclaredField("nameField");
        nameField.setAccessible(true);
        JTextField textField = (JTextField) nameField.get(screen);
        
        assertNotNull(textField);
    }
    
    @Test
    public void testNameFieldMaxLength() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nameField = NameInputScreen.class.getDeclaredField("nameField");
        nameField.setAccessible(true);
        JTextField textField = (JTextField) nameField.get(screen);
        
        // DocumentFilter가 10글자 제한 적용
        textField.setText("abcdefghij"); // 10글자
        assertTrue(textField.getText().length() <= 10);
    }
    
    @Test
    public void testSubmitScoreMethodExists() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method submitScore = NameInputScreen.class.getDeclaredMethod("submitScore");
        submitScore.setAccessible(true);
        assertNotNull(submitScore);
    }
    
    @Test
    public void testNameFieldTextSetting() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nameField = NameInputScreen.class.getDeclaredField("nameField");
        nameField.setAccessible(true);
        JTextField textField = (JTextField) nameField.get(screen);
        
        textField.setText("Player");
        assertEquals("Player", textField.getText());
    }
    
    @Test
    public void testEmptyNameFieldTrim() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nameField = NameInputScreen.class.getDeclaredField("nameField");
        nameField.setAccessible(true);
        JTextField textField = (JTextField) nameField.get(screen);
        
        textField.setText("   ");
        assertEquals("", textField.getText().trim());
    }
    
    @Test
    public void testItemModeRankingFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen itemScreen = new NameInputScreen(25000, Difficulty.NORMAL, true, false);
        
        Field isItemModeField = NameInputScreen.class.getDeclaredField("isItemMode");
        isItemModeField.setAccessible(true);
        assertTrue((boolean) isItemModeField.get(itemScreen));
        
        itemScreen.dispose();
    }
    
    @Test
    public void testTimeAttackModeRankingFile() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen timeScreen = new NameInputScreen(15000, Difficulty.HARD, false, true);
        
        Field isTimeAttackField = NameInputScreen.class.getDeclaredField("isTimeAttackMode");
        isTimeAttackField.setAccessible(true);
        assertTrue((boolean) isTimeAttackField.get(timeScreen));
        
        timeScreen.dispose();
    }
    
    @Test
    public void testMaxNameLengthConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field maxNameLength = NameInputScreen.class.getDeclaredField("MAX_NAME_LENGTH");
        maxNameLength.setAccessible(true);
        assertEquals(10, (int) maxNameLength.get(null));
    }
    
    @Test
    public void testAlphabetPatternConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field alphabetPattern = NameInputScreen.class.getDeclaredField("ALPHABET_PATTERN");
        alphabetPattern.setAccessible(true);
        assertEquals("[a-zA-Z]+", alphabetPattern.get(null));
    }
    
    @Test
    public void testItemRankingFileConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field itemRankingFile = NameInputScreen.class.getDeclaredField("ITEM_RANKING_FILE");
        itemRankingFile.setAccessible(true);
        assertEquals("item_rankings.dat", itemRankingFile.get(null));
    }
    
    @Test
    public void testTimeAttackRankingFileConstant() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field timeAttackRankingFile = NameInputScreen.class.getDeclaredField("TIMEATTACK_RANKING_FILE");
        timeAttackRankingFile.setAccessible(true);
        assertEquals("timeattack_rankings.dat", timeAttackRankingFile.get(null));
    }
    
    @Test
    public void testInitializeUI() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Method initializeUI = NameInputScreen.class.getDeclaredMethod("initializeUI");
        initializeUI.setAccessible(true);
        
        assertDoesNotThrow(() -> initializeUI.invoke(screen));
    }
    
    @Test
    public void testWindowListenerRegistered() {
        if (GraphicsEnvironment.isHeadless()) return;
        
        assertTrue(screen.getWindowListeners().length > 0);
    }
    
    @Test
    public void testAllDifficulties() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen easyScreen = new NameInputScreen(5000, Difficulty.EASY, false, false);
        Field difficultyField = NameInputScreen.class.getDeclaredField("difficulty");
        difficultyField.setAccessible(true);
        assertEquals(Difficulty.EASY, difficultyField.get(easyScreen));
        easyScreen.dispose();
        
        NameInputScreen hardScreen = new NameInputScreen(20000, Difficulty.HARD, false, false);
        assertEquals(Difficulty.HARD, difficultyField.get(hardScreen));
        hardScreen.dispose();
    }
    
    @Test
    public void testNameFieldActionListener() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        Field nameField = NameInputScreen.class.getDeclaredField("nameField");
        nameField.setAccessible(true);
        JTextField textField = (JTextField) nameField.get(screen);
        
        // ActionListener가 등록되어 있는지 확인 (엔터 키로 제출)
        assertTrue(textField.getActionListeners().length > 0);
    }
    
    @Test
    public void testHighScores() throws Exception {
        if (GraphicsEnvironment.isHeadless()) return;
        
        NameInputScreen highScoreScreen = new NameInputScreen(100000, Difficulty.HARD, false, false);
        
        Field finalScoreField = NameInputScreen.class.getDeclaredField("finalScore");
        finalScoreField.setAccessible(true);
        assertEquals(100000, (int) finalScoreField.get(highScoreScreen));
        
        highScoreScreen.dispose();
    }
}

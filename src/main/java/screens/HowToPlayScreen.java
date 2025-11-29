package screens;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import core.Settings;

/**
 * 게임 설명 화면
 */
public class HowToPlayScreen extends JDialog {

    public HowToPlayScreen() {
        super((java.awt.Frame) null, "How to Play", true); // 모달 다이얼로그
        
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        
        setTitle("How to Play");
        setSize((int)(width * 0.9), (int)(height * 0.95));
        setResizable(true); // 크기 조절 가능하도록 변경
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // 키 바인딩 가져오기 (1P, 2P)
        String p1RightKey = KeyEvent.getKeyText(Settings.KeyBinding.RIGHT.getKeyCode(Settings.Player.P1));
        String p1LeftKey = KeyEvent.getKeyText(Settings.KeyBinding.LEFT.getKeyCode(Settings.Player.P1));
        String p1RotateKey = KeyEvent.getKeyText(Settings.KeyBinding.ROTATE.getKeyCode(Settings.Player.P1));
        String p1DownKey = KeyEvent.getKeyText(Settings.KeyBinding.DOWN.getKeyCode(Settings.Player.P1));
        String p1HardDropKey = KeyEvent.getKeyText(Settings.KeyBinding.HARD_DROP.getKeyCode(Settings.Player.P1));
        
        String p2RightKey = KeyEvent.getKeyText(Settings.KeyBinding.RIGHT.getKeyCode(Settings.Player.P2));
        String p2LeftKey = KeyEvent.getKeyText(Settings.KeyBinding.LEFT.getKeyCode(Settings.Player.P2));
        String p2RotateKey = KeyEvent.getKeyText(Settings.KeyBinding.ROTATE.getKeyCode(Settings.Player.P2));
        String p2DownKey = KeyEvent.getKeyText(Settings.KeyBinding.DOWN.getKeyCode(Settings.Player.P2));
        String p2HardDropKey = KeyEvent.getKeyText(Settings.KeyBinding.HARD_DROP.getKeyCode(Settings.Player.P2));
        
        // 설명 텍스트
        JTextArea helpText = new JTextArea(String.format("""
            ═══════════════════════════════════════
            SINGLE PLAYER CONTROLS (1P):
            ═══════════════════════════════════════
            - %s : Move block right
            - %s : Move block left
            - %s : Rotate block clockwise
            - %s : Soft drop (move down faster)
            - %s : Hard drop (instant drop)
            - P : Pause/Resume game

            ═══════════════════════════════════════
            MULTIPLAYER CONTROLS:
            ═══════════════════════════════════════
            PLAYER 1 (1P):
            - %s/%s/%s/%s : Move/Rotate
            - %s : Hard drop
            
            PLAYER 2 (2P):
            - %s/%s/%s/%s : Move/Rotate
            - %s : Hard drop

            BATTLE MODE:
            - Clear 2+ lines at once to send
              attack lines to opponent's board
            - Attack lines appear as gray blocks
              with holes at strategic positions
            - Max 10 pending attack lines shown
              in mini board preview
            - First to reach Game Over loses!

            TIME ATTACK MODE:
            - 3 minute time limit
            - Winner determined by higher score
            - Time shown in yellow (red <30sec)

            ═══════════════════════════════════════
            SCORING:
            ═══════════════════════════════════════
            Fill a complete horizontal line
            to clear it and earn points.
            Clear multiple lines at once
            for bonus points!

            GAME OVER:
            When blocks stack up to the top
            of the screen, the game ends.

            ═══════════════════════════════════════
            DIFFICULTY:
            ═══════════════════════════════════════
            - EASY: Slower speed, more I-blocks
            - NORMAL: Standard gameplay
            - HARD: Faster speed, fewer I-blocks
            
            ═══════════════════════════════════════
            ITEM MODE:
            ═══════════════════════════════════════
            Special white item blocks appear
            every 10 lines cleared. Each block
            has a unique power marked by a letter:
            
            - L (Line Block): Clears the entire
              horizontal line where 'L' is placed
            
            - S (Slow Block): Reduces game speed
              for a limited time
            
            - T (Transform Block): Converts the
              next 5 blocks into I-blocks
            
            - W (Weight Block): Falls through and
              erases blocks below it. Movement locks
              when touching other blocks.
            
            - B (Bomb Block): Explodes in a 3x3
              area when placed, clearing all blocks
              in that zone

            ═══════════════════════════════════════
            NETWORK PLAY (P2P):
            ═══════════════════════════════════════
            1. One player hosts as Server
            2. Other player connects as Client
            3. Share IP address and port (7777)
            4. Battle mode rules apply
            5. Winner: Survive longer or higher
               score (in time attack mode)
            
            ═══════════════════════════════════════
            NOTE:
            You can customize key bindings
            in the Settings menu for both players.
            ═══════════════════════════════════════
            """, 
            p1RightKey, p1LeftKey, p1RotateKey, p1DownKey, p1HardDropKey,
            p1LeftKey, p1RightKey, p1DownKey, p1RotateKey, p1HardDropKey,
            p2LeftKey, p2RightKey, p2DownKey, p2RotateKey, p2HardDropKey));
        
        helpText.setEditable(false);
        helpText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int)(baseFontSize * 0.9)));
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 스크롤 패널
        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        // 닫기 버튼
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, baseFontSize));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
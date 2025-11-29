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
        setSize((int)(width * 0.7), (int)(height * 0.8));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // 키 바인딩 가져오기
        String rightKey = KeyEvent.getKeyText(Settings.KeyBinding.RIGHT.getKeyCode());
        String leftKey = KeyEvent.getKeyText(Settings.KeyBinding.LEFT.getKeyCode());
        String rotateKey = KeyEvent.getKeyText(Settings.KeyBinding.ROTATE.getKeyCode());
        String downKey = KeyEvent.getKeyText(Settings.KeyBinding.DOWN.getKeyCode());
        String hardDropKey = KeyEvent.getKeyText(Settings.KeyBinding.HARD_DROP.getKeyCode());
        
        // 설명 텍스트
        JTextArea helpText = new JTextArea(String.format("""
            CONTROLS:
            - %s : Move block right
            - %s : Move block left
            - %s : Rotate block clockwise
            - %s : Soft drop (move down faster)
            - %s : Hard drop (instant drop)
            - P : Pause/Resume game

            SCORING:
            Fill a complete horizontal line
            to clear it and earn points.
            Clear multiple lines at once
            for bonus points!

            GAME OVER:
            When blocks stack up to the top
            of the screen, the game ends.

            DIFFICULTY:
            - EASY: Slower speed, more I-blocks
            - NORMAL: Standard gameplay
            - HARD: Faster speed, fewer I-blocks
            
            ITEM MODE:
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
            
            NOTE:
            You can customize key bindings
            in the Settings menu.
            """, rightKey, leftKey, rotateKey, downKey, hardDropKey));
        
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
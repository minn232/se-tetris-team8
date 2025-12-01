package screens;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import core.Difficulty;
import core.Settings;
import ranking.RankingEntry;
import ranking.RankingManager;

/**
 * 랭킹 등록을 위한 이름 입력 화면
 * 최종 점수가 랭킹에 들었을 때 호출됨
 * 최대 10글자, 알파벳만 입력 가능
 */
public class NameInputScreen extends JFrame {

    private static final int MAX_NAME_LENGTH = 10;
    private static final String ALPHABET_PATTERN = "[a-zA-Z]+";
    private static final String ITEM_RANKING_FILE = "item_rankings.dat";
    private static final String TIMEATTACK_RANKING_FILE = "timeattack_rankings.dat";
    
    private final int finalScore;
    private final Difficulty difficulty;
    private final boolean isItemMode;
    private final boolean isTimeAttackMode;
    private JTextField nameField;

    public NameInputScreen(int finalScore, Difficulty difficulty, boolean isItemMode, boolean isTimeAttackMode) {
        this.finalScore = finalScore;
        this.difficulty = difficulty;
        this.isItemMode = isItemMode;
        this.isTimeAttackMode = isTimeAttackMode;
        initializeUI();
    }

    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 0.6);
        int height = (int)(Settings.getWindowHeight() * 0.525);
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        setTitle("New Record!");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                // 이름 입력 없이 닫은 경우에도 스코어보드 표시 (isItemMode 전달)
                new ranking.RankingBoard(null, finalScore, true, isItemMode).setVisible(true);
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("Congratulations! Enter your name!");
        messageLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, baseFontSize));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(MAX_NAME_LENGTH);
        nameField.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, baseFontSize));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setMaximumSize(new Dimension((int)(250 * scaleFactor), (int)(35 * scaleFactor)));
        
        // 엔터 키로 제출
        nameField.addActionListener(e -> submitScore());
        
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (newText.length() <= MAX_NAME_LENGTH && text.matches(ALPHABET_PATTERN)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, baseFontSize));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension((int)(120 * scaleFactor), (int)(40 * scaleFactor)));
        submitButton.addActionListener(e -> submitScore());

        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(submitButton);

        add(mainPanel);
    }

    private void submitScore() {
        String playerName = nameField.getText().trim();
        
        if (!playerName.isEmpty()) {
            RankingManager manager;
            if (isItemMode) {
                manager = RankingManager.getInstance(ITEM_RANKING_FILE);
            } else if (isTimeAttackMode) {
                manager = RankingManager.getInstance(TIMEATTACK_RANKING_FILE);
            } else {
                manager = RankingManager.getInstance();
            }
            
            // 랭킹에 추가 (난이도 정보 포함)
            manager.addEntry(new RankingEntry(playerName, finalScore, LocalDateTime.now(), difficulty));
            dispose();
            
            // 스코어보드를 표시하고 새로 입력한 항목 강조 (isItemMode 전달)
            new ranking.RankingBoard(playerName, finalScore, true, isItemMode).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter your name!");
        }
    }
}
package ranking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import core.Settings;

/**
 * 랭킹 보드 UI
 * 일반 모드와 아이템 모드 랭킹을 탭으로 분리하여 표시
 */
public class RankingBoard extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String NORMAL_RANKING_FILE = "normal_rankings.dat";
    private static final String TIMEATTACK_RANKING_FILE = "timeattack_rankings.dat";
    private static final String ITEM_RANKING_FILE = "item_rankings.dat";
    
    private final String highlightPlayerName;
    private final int highlightScore;
    private final boolean showReturnButton;
    private final boolean isItemMode;
    private final boolean isTimeAttackMode;
    
    private JButton[] buttons;
    private int selectedIndex = 0;
    private JTabbedPane tabbedPane;
    private boolean focusOnButtons = false; // true: 버튼 포커스, false: 탭 포커스
    
    public RankingBoard() {
        super((java.awt.Frame) null, "Ranking Board", true); // 모달 다이얼로그로 생성
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        
        setTitle("Ranking Board");
        setSize((int)(width * 0.8), (int)(height * 0.9));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        this.highlightPlayerName = null;
        this.highlightScore = -1;
        this.showReturnButton = false;
        this.isItemMode = false;
        this.isTimeAttackMode = false;
        initializeUI();
    }
    
    public RankingBoard(String highlightPlayerName, int highlightScore, boolean showReturnButton) {
        this(highlightPlayerName, highlightScore, showReturnButton, false, false);
    }
    
    public RankingBoard(String highlightPlayerName, int highlightScore, boolean showReturnButton, boolean isItemMode, boolean isTimeAttackMode) {
        super((java.awt.Frame) null, "Ranking Board", true); // 모달 다이얼로그로 생성
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        
        setTitle("Ranking Board");
        setSize((int)(width * 0.8), (int)(height * 0.9));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        this.highlightPlayerName = highlightPlayerName;
        this.highlightScore = highlightScore;
        this.showReturnButton = showReturnButton;
        this.isItemMode = isItemMode;
        this.isTimeAttackMode = isTimeAttackMode;
        initializeUI();
    }

    private void initializeUI() {
        int width = (int)(Settings.getWindowWidth() * 0.5);
        int height = (int)(Settings.getWindowHeight());
        
        setTitle("Ranking Board");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        
        JPanel softModePanel = createRankingPanel(
            RankingManager.getInstance(NORMAL_RANKING_FILE).getRankings(),
            "Soft Mode",
            false // Soft 모드
        );
        
        JPanel itemModePanel = createRankingPanel(
            RankingManager.getInstance(ITEM_RANKING_FILE).getRankings(),
            "Item Mode",
            true // 아이템 모드
        );

        tabbedPane.addTab("Soft Mode", softModePanel);
        tabbedPane.addTab("Item Mode", itemModePanel);
        
        // 모드에 따라 해당 탭을 기본으로 선택
        if (isItemMode) {
            tabbedPane.setSelectedIndex(1); // Item Mode 탭
        }
        // 그 외(Soft Mode)는 기본값인 0번 탭이 선택됨

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        
        // 게임 종료 후 표시되는 경우 버튼 추가
        if (showReturnButton) {
            JPanel buttonPanel = createButtonPanel();
            mainContainer.add(buttonPanel, BorderLayout.SOUTH);
            
            // 키보드 네비게이션 설정
            setupKeyboardNavigation();
        } else {
            // 일반 랭킹 보기의 경우 Close 버튼 추가
            JPanel closeButtonPanel = createCloseButtonPanel();
            mainContainer.add(closeButtonPanel, BorderLayout.SOUTH);
            
            // ESC 키 및 탭 전환을 위한 키보드 리스너 추가
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dispose();
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        int currentIndex = tabbedPane.getSelectedIndex();
                        if (currentIndex > 0) {
                            tabbedPane.setSelectedIndex(currentIndex - 1);
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        int currentIndex = tabbedPane.getSelectedIndex();
                        if (currentIndex < tabbedPane.getTabCount() - 1) {
                            tabbedPane.setSelectedIndex(currentIndex + 1);
                        }
                    }
                }
            });
        }

        add(mainContainer);
    }
    
    private void setupKeyboardNavigation() {
        // 키보드 포커스를 받을 수 있도록 설정
        setFocusable(true);
        
        // 버튼들의 포커스 비활성화
        for (JButton btn : buttons) {
            btn.setFocusable(false);
        }
        
        // 초기 하이라이트 설정
        updateButtonHighlight();
        
        // 키보드 이벤트 리스너 추가
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }
    
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (showReturnButton && focusOnButtons) {
                    // 버튼에서 탭으로 포커스 이동
                    focusOnButtons = false;
                    updateButtonHighlight();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (showReturnButton && !focusOnButtons) {
                    // 탭에서 버튼으로 포커스 이동
                    focusOnButtons = true;
                    selectedIndex = 0; // 첫 번째 버튼 선택
                    updateButtonHighlight();
                }
                break;
            case KeyEvent.VK_LEFT:
                if (focusOnButtons) {
                    // 버튼 간 이동
                    if (selectedIndex > 0) {
                        selectedIndex--;
                        updateButtonHighlight();
                    }
                } else {
                    // 탭 전환
                    int currentIndex = tabbedPane.getSelectedIndex();
                    if (currentIndex > 0) {
                        tabbedPane.setSelectedIndex(currentIndex - 1);
                    }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (focusOnButtons) {
                    // 버튼 간 이동
                    if (selectedIndex < buttons.length - 1) {
                        selectedIndex++;
                        updateButtonHighlight();
                    }
                } else {
                    // 탭 전환
                    int currentIndex = tabbedPane.getSelectedIndex();
                    if (currentIndex < tabbedPane.getTabCount() - 1) {
                        tabbedPane.setSelectedIndex(currentIndex + 1);
                    }
                }
                break;
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE:
                if (focusOnButtons) {
                    buttons[selectedIndex].doClick();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                dispose();
                // showReturnButton이 true이면 메인 메뉴로 복귀
                if (showReturnButton) {
                    screens.ScreenNavigator.getInstance().clear();
                    new screens.Mainmenu().setVisible(true);
                }
                break;
        }
    }
    
    private void updateButtonHighlight() {
        for (int i = 0; i < buttons.length; i++) {
            if (focusOnButtons && i == selectedIndex) {
                buttons[i].setBackground(new Color(100, 150, 255));
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(true);
            } else {
                buttons[i].setBackground(null);
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(false);
            }
        }
        repaint();
    }

    private JPanel createRankingPanel(List<RankingEntry> rankings, String title, boolean isItemMode) {
        int baseFontSize = Settings.getBaseFontSize();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = createTitlePanel(title, baseFontSize);
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        addRankingEntries(contentPanel, rankings, baseFontSize, isItemMode);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        mainMenuButton.setPreferredSize(new java.awt.Dimension((int)(150 * scaleFactor), (int)(40 * scaleFactor)));
        mainMenuButton.addActionListener(e -> {
            dispose();
            screens.ScreenNavigator.getInstance().clear();
            new screens.Mainmenu().setVisible(true);
        });
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        exitButton.setPreferredSize(new java.awt.Dimension((int)(150 * scaleFactor), (int)(40 * scaleFactor)));
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        
        // 버튼 배열 초기화
        buttons = new JButton[]{mainMenuButton, exitButton};
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }
    
    private JPanel createCloseButtonPanel() {
        int baseFontSize = Settings.getBaseFontSize();
        double scaleFactor = Settings.getScaleFactor();
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        closeButton.setPreferredSize(new java.awt.Dimension((int)(100 * scaleFactor), (int)(40 * scaleFactor)));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        return buttonPanel;
    }
    
    private JPanel createTitlePanel(String title, int baseFontSize) {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.33)));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalGlue());
        
        return titlePanel;
    }
    
    private void addRankingEntries(JPanel panel, List<RankingEntry> rankings, int baseFontSize, boolean isItemMode) {
        for (int i = 0; i < rankings.size(); i++) {
            RankingEntry entry = rankings.get(i);
            
            // 방금 입력한 항목인지 확인 (이름과 점수가 모두 일치)
            boolean isHighlighted = highlightPlayerName != null 
                && entry.getPlayerName().equals(highlightPlayerName)
                && entry.getScore() == highlightScore;
            
            // 난이도 정보 가져오기 (null이면 기본값 표시)
            String difficultyStr = entry.getDifficulty() != null 
                ? entry.getDifficulty().name() 
                : "NORMAL";
            
            JLabel rankLabel = new JLabel(String.format("%d. %s - %d [%s] (%s)",
                i + 1,
                entry.getPlayerName(),
                entry.getScore(),
                difficultyStr,
                entry.getTimestamp().format(DATE_FORMATTER)
            ));
            
            // 강조 표시
            if (isHighlighted) {
                rankLabel.setFont(new Font("Arial", Font.BOLD, (int)(baseFontSize * 1.0)));
                rankLabel.setForeground(new Color(255, 215, 0)); // 골드 색상
                rankLabel.setOpaque(true);
                rankLabel.setBackground(new Color(50, 50, 50)); // 어두운 배경
                rankLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            } else {
                rankLabel.setFont(new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
            }
            
            rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(rankLabel);
            panel.add(Box.createVerticalStrut(10));
        }
    }
}
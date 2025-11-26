package screens;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Settings;
import ranking.RankingBoard;

public class Mainmenu extends JFrame {

    public Mainmenu() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        double scale = Settings.getScaleFactor();
        
        javax.swing.UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, baseFontSize));
        javax.swing.UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
        
        setTitle("Tetris");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);
        
        int btnSingleW = (int)(70 * scale);
        int btnSingleH = (int)(75 * scale);
        int btnMultiW = (int)(80 * scale);
        int btnMultiH = (int)(80 * scale);
        int btnRankingW = (int)(70 * scale);
        int btnRankingH = (int)(75 * scale);
        int btnHowtoW = (int)(80 * scale);
        int btnHowtoH = (int)(60 * scale);
        int btnSettingsW = (int)(70 * scale);
        int btnSettingsH = (int)(70 * scale);
        int btnExitW = (int)(65 * scale);
        int btnExitH = (int)(60 * scale);
        
        JButton singleplayButton = addButton(mainPanel, "/images/SinglePlayButton.png", 
            btnSingleW, btnSingleH, 
            (int)(width / 2.0 + 10), (int)(height / 2.0 - 35 * scale));
        singleplayButton.addActionListener(e -> new ModeSelectionScreen().setVisible(true));
        
        JButton multiplayButton = addButton(mainPanel, "/images/MultiPlayButton.png", 
            btnMultiW, btnMultiH, 
            (int)(width / 2.0 + 100 * scale), (int)(height / 2.0 - 40 * scale));
        multiplayButton.addActionListener(e -> new MultiplaySelectionScreen().setVisible(true));
            
        JButton rankingBoardButton = addButton(mainPanel, "/images/RankingboardButton.png", 
            btnRankingW, btnRankingH, 
            (int)(width / 2.0 + 200 * scale), (int)(height / 2.0 - 32 * scale));
        rankingBoardButton.addActionListener(e -> {
            RankingBoard rankingBoard = new RankingBoard();
            rankingBoard.setVisible(true); // 모달이므로 이 줄이 끝나기 전까지 다른 창 클릭 불가
        });
            
        JButton howtoplayButton = addButton(mainPanel, "/images/HowtoplayButton.png", 
            btnHowtoW, btnHowtoH, 
            (int)(width / 2.0 + 5 * scale), (int)(height / 2.0 + 65 * scale));
        howtoplayButton.addActionListener(e -> {
            HowToPlayScreen howToPlayScreen = new HowToPlayScreen();
            howToPlayScreen.setVisible(true); // 모달이므로 창이 닫힐 때까지 메인메뉴 클릭 불가
        });
            
        JButton settingsButton = addButton(mainPanel, "/images/SettingButton.png", 
            btnSettingsW, btnSettingsH, 
            (int)(width / 2.0 + 105 * scale), (int)(height / 2.0 + 60 * scale));
        settingsButton.addActionListener(e -> openSettings());
        
        JButton exitButton = addButton(mainPanel, "/images/ExitButton.png", 
            btnExitW, btnExitH, 
            (int)(width / 2.0 + 204 * scale), (int)(height / 2.0 + 63 * scale));
        exitButton.addActionListener(e -> {
            System.exit(0); // 프로그램 종료
        });
        
        BackgroundPanel bg = new BackgroundPanel("images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
    }

    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }

    private void openSettings() {
        int currentWidth = Settings.getWindowWidth();
        int currentHeight = Settings.getWindowHeight();
        
        SettingsScreen settingsScreen = new SettingsScreen();
        settingsScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsScreen.setVisible(true);
        
        settingsScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (!Settings.getResolution().equals(currentWidth + "x" + currentHeight)) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new Mainmenu().setVisible(true));
                }
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Mainmenu().setVisible(true));
    }
}
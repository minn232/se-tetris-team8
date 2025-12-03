package screens;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Difficulty;
import core.Settings;
import network.NetworkManager;

/**
 * 네트워크 대전 모드 선택 화면 (Host 전용)
 * Host는 모드 선택 → 바로 MODE 메시지 전송 → 즉시 P2PBattlePanel로 이동
 * Client는 이 화면을 절대 보지 않음
 */
public class NetworkModeSelectionScreen extends JFrame {

    private final JButton[] modeButtons;
    private int selectedIndex = 0;

    private final boolean isHost;

    private String selectedMode = "SOFT"; // 기본 모드

    public NetworkModeSelectionScreen(boolean isHost) {
        this.isHost = isHost;
        
        // 네비게이션 히스토리에 추가
        ScreenNavigator.getInstance().push("NetworkModeSelection", isHost);

        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();

        setTitle("Select Battle Mode");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);

        // 버튼 크기 계산
        int btnWidth = (int) (80 * scale);
        int btnHeight = (int) (120 * scale);

        // ====== 모드 버튼 3개 ======
        JButton softButton = addButton(mainPanel, "/images/SoftModeButton.png",
                btnWidth, btnHeight,
                (int) (width / 2.0 - btnWidth / 2.0 + 30 * scale),
                (int) (height / 2.0 - 20 * scale));

        JButton timeAttackButton = addButton(mainPanel, "/images/TimeattackModeButton.png",
                btnWidth, btnHeight,
                (int) (width / 2.0 - btnWidth / 2.0 + 130 * scale),
                (int) (height / 2.0 - 20 * scale));

        JButton itemButton = addButton(mainPanel, "/images/ItemModeButton.png",
                btnWidth, btnHeight,
                (int) (width / 2.0 - btnWidth / 2.0 + 230 * scale),
                (int) (height / 2.0 - 20 * scale));

        modeButtons = new JButton[]{softButton, timeAttackButton, itemButton};

        // Host만 버튼 활성화
        if (!isHost) {
            for (JButton b : modeButtons) b.setEnabled(false);
        }

        // Host 모드 선택 이벤트
        softButton.addActionListener(e -> selectMode("SOFT"));
        timeAttackButton.addActionListener(e -> selectMode("TIME"));
        itemButton.addActionListener(e -> selectMode("ITEM"));
        
        // 뒤로가기 버튼 추가
        int backBtnSize = (int)(40 * scale);
        int backBtnX = width - backBtnSize - (int)(10 * scale);
        int backBtnY = height - backBtnSize - (int)(40 * scale);
        
        JButton backButton = addButton(mainPanel, "/images/BackButton.png", 
            backBtnSize, backBtnSize, backBtnX, backBtnY);
        backButton.addActionListener(e -> {
            System.out.println("[NetworkModeSelection] Back button clicked");
            ScreenNavigator.getInstance().goBack(this);
        });
        backButton.setFocusable(false);

        // Background UI
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);

        // 키 네비게이션
        for (JButton btn : modeButtons) btn.setFocusable(false);
        updateFocus();

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setVisible(true);
    }

    private void selectMode(String mode) {
        selectedMode = mode;

        // Host → Client 모드 전송
        NetworkManager.getInstance().send("MODE:" + mode);
        System.out.println("[HOST] Selected Mode → " + mode);

        // Host는 바로 전투 화면으로 이동
        openBattlePanel();
    }

    private void openBattlePanel() {
        SwingUtilities.invokeLater(() -> {
            dispose();

            boolean isItemMode = selectedMode.equals("ITEM");
            boolean isTimeAttack = selectedMode.equals("TIME");

            JFrame frame = new JFrame("P2P Battle");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 🔥 Host → isHost = true 전달
            P2PBattlePanel panel =
                new P2PBattlePanel(Difficulty.NORMAL, isItemMode, isTimeAttack, isHost);

            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow();
        });
    }



    private JButton addButton(JPanel panel, String imagePath, int w, int h, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, w, h);
        btn.setBounds(x, y, w, h);
        panel.add(btn);
        return btn;
    }

    private void handleKeyPress(int keyCode) {
        if (!isHost) return;

        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    updateFocus();
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (selectedIndex < modeButtons.length - 1) {
                    selectedIndex++;
                    updateFocus();
                }
            }
            case KeyEvent.VK_SPACE, KeyEvent.VK_ENTER -> {
                modeButtons[selectedIndex].doClick();
            }
            case KeyEvent.VK_ESCAPE -> 
                ScreenNavigator.getInstance().goBack(this);
        }
    }

    private void updateFocus() {
        for (int i = 0; i < modeButtons.length; i++) {
            float opacity = (i == selectedIndex) ? 0.75f : 1.0f;
            modeButtons[i].putClientProperty("opacity", opacity);
            modeButtons[i].repaint();
        }
    }
}

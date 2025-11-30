package screens;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Settings;
import network.NetworkManager;

/**
 * P2P 네트워크용 모드 선택 화면
 * Host만 모드 변경 가능, Client는 선택 비활성화
 * Host/Client Ready → Host가 Start → 게임 시작
 */
public class NetworkModeSelectionScreen extends JFrame {

    private JButton[] modeButtons;
    private JButton readyButton;
    private JButton startButton;

    private int selectedIndex = 0;

    private boolean isHost;
    private boolean meReady = false;
    private boolean enemyReady = false;

    private String selectedMode = "SOFT"; // 기본 모드

    public NetworkModeSelectionScreen(boolean isHost) {
        this.isHost = isHost;

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

        // Host만 모드 선택 가능 / Client는 비활성화
        if (!isHost) {
            for (JButton b : modeButtons) b.setEnabled(false);
        }

        // 버튼 이벤트 → Host만 모드 전송
        softButton.addActionListener(e -> selectMode("SOFT"));
        timeAttackButton.addActionListener(e -> selectMode("TIME"));
        itemButton.addActionListener(e -> selectMode("ITEM"));

        // ===== READY 버튼 =====
        readyButton = new JButton("Ready");
        readyButton.setBounds(50, height - 150, 150, 40);
        readyButton.addActionListener(e -> toggleReady());
        mainPanel.add(readyButton);

        // ====== START 버튼(Host만 활성화) ======
        startButton = new JButton("Start Game");
        startButton.setBounds(width - 250, height - 150, 150, 40);
        startButton.setEnabled(false); // 둘 다 ready일 때만 활성화
        mainPanel.add(startButton);

        // Start 버튼 → Host만 클릭 가능
        startButton.addActionListener(e -> startGame());

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

        // ===== Network Handler =====
        NetworkManager.getInstance().setMessageListener(this::onNetworkMessage);

        setVisible(true);
    }

    // ===== 모드 선택 (Host만 호출) =====
    private void selectMode(String mode) {
        selectedMode = mode;
        NetworkManager.getInstance().send("MODE:" + mode);
        System.out.println("[HOST] Mode Selected → " + mode);
    }

    // ===== Ready 토글 =====
    private void toggleReady() {
        meReady = !meReady;

        String msg = meReady ? "READY:ME" : "READY_CANCEL:ME";
        NetworkManager.getInstance().send(msg);

        readyButton.setText(meReady ? "Ready ✔" : "Ready");

        checkStartAvailability();
    }

    // ===== Start 버튼 클릭 (Host) =====
    private void startGame() {
        if (!isHost) return;
        if (!(meReady && enemyReady)) return;

        NetworkManager.getInstance().send("START_GAME");
        openGamePanel();
    }

    // ===== Network Handler =====
    private void onNetworkMessage(String msg) {
        if (msg.startsWith("MODE:")) {
            selectedMode = msg.substring(5);
            System.out.println("[CLIENT] Mode Sync → " + selectedMode);
        }
        else if (msg.equals("READY:ME") || msg.equals("READY:HOST")) {
            enemyReady = true;
        }
        else if (msg.equals("READY_CANCEL:ME") || msg.equals("READY_CANCEL:HOST")) {
            enemyReady = false;
        }
        else if (msg.equals("START_GAME")) {
            openGamePanel();
        }

        checkStartAvailability();
    }

    // ===== Ready 상태에 따라 Host Start 버튼 활성화 =====
    private void checkStartAvailability() {
        if (isHost) {
            startButton.setEnabled(meReady && enemyReady);
        }
    }

    // ===== 게임 시작 =====
    private void openGamePanel() {
        SwingUtilities.invokeLater(() -> {
            dispose();

            boolean isItemMode = selectedMode.equals("ITEM");
            boolean isTimeAttack = selectedMode.equals("TIME");

            new DifficultySelectionScreen(isItemMode, true, isTimeAttack).setVisible(true);
        });
    }

    // ===== UI 버튼 =====
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

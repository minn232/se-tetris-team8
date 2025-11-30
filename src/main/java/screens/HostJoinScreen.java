package screens;

import java.awt.BorderLayout;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Settings;
import network.NetworkManager;

/**
 * 네트워크 배틀 - Host/Join 선택 화면
 */
public class HostJoinScreen extends JFrame {

    private JButton[] buttons;
    private int selectedIndex = 0;

    public HostJoinScreen() {
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        double scale = Settings.getScaleFactor();

        setTitle("Host or Join");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);

        int btnWidth = (int)(120 * scale);
        int btnHeight = (int)(120 * scale);

        // ===============================
        //  HOST 버튼
        // ===============================
        JButton hostButton = addButton(
            mainPanel,
            "/images/HostButton.png",
            btnWidth - 50, btnHeight,
            (int)(width / 2.0 - btnWidth / 2.0 + 80 * scale),
            (int)(height / 2.0 - 30 * scale)
        );
        hostButton.addActionListener(e -> {
            System.out.println("[HostJoin] Host selected");
            dispose();

            try {
                // -------- 포트 입력받기 ---------
                String defaultPort = "12345";
                String portStr = javax.swing.JOptionPane.showInputDialog(
                    this,
                    "사용할 포트 번호를 입력하세요:",
                    defaultPort
                );

                if (portStr == null || portStr.trim().isEmpty()) {
                    new Mainmenu().setVisible(true);
                    return;
                }

                int port = Integer.parseInt(portStr.trim());

                // -------- IP 정보 가져오기 --------
                String hostIP = InetAddress.getLocalHost().getHostAddress();

                System.out.println("HOST IP: " + hostIP);
                System.out.println("HOST PORT: " + port);

                // 서버 시작
                NetworkManager.getInstance().startServer(port);

                // ----- 서버 대기 메시지 표시 -----
                final boolean[] cancelled = {false};
                Thread waitThread = new Thread(() -> {
                    while (!NetworkManager.getInstance().isConnected() && !cancelled[0]) {
                        try { Thread.sleep(100); } catch (Exception ex) {}
                    }
                    if (!cancelled[0] && NetworkManager.getInstance().isConnected()) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(
                                null,
                                "상대방이 접속했습니다!"
                            );
                            new ModeSelectionScreen(true).setVisible(true);
                        });
                    }
                });
                waitThread.start();

                String[] options = {"닫기"};
                int result = javax.swing.JOptionPane.showOptionDialog(
                    null,
                    "서버 대기 중...\n" +
                    "IP: " + hostIP + "\n" +
                    "포트: " + port + "\n" +
                    "상대방이 접속할 때까지 대기합니다.",
                    "서버 대기",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                if (result == 0 || result == javax.swing.JOptionPane.CLOSED_OPTION) {
                    cancelled[0] = true;
                    NetworkManager.getInstance().close();
                    javax.swing.SwingUtilities.invokeLater(() -> new Mainmenu().setVisible(true));
                }

            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(null,
                    "서버 시작 실패: " + ex.getMessage());
                new Mainmenu().setVisible(true);
            }
        });

        // ===============================
        //  JOIN 버튼 (IP + PORT 입력)
        // ===============================
        JButton joinButton = addButton(
            mainPanel,
            "/images/HostingButton.png",
            btnWidth + 30, btnHeight,
            (int)(width / 2.0 - btnWidth / 2.0 + 170 * scale),
            (int)(height / 2.0 - 30 * scale)
        );
        joinButton.addActionListener(e -> {
            System.out.println("[HostJoin] Join selected");
            dispose();

            String ip = null;
            boolean validInput = false;

            while (!validInput) {
                ip = javax.swing.JOptionPane.showInputDialog(
                    this,
                    "서버 IP 주소를 입력하세요:",
                    "localhost"
                );

                if (ip == null || ip.trim().isEmpty()) {
                    new Mainmenu().setVisible(true);
                    return;
                }

                if (isValidIP(ip.trim())) {
                    validInput = true;
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "올바른 IP 형식이 아닙니다.\n예: 192.168.0.1 또는 localhost",
                        "입력 오류",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                }
            }

            // -------- 포트 입력받기 --------
            String portStr = javax.swing.JOptionPane.showInputDialog(
                this,
                "포트 번호를 입력하세요:",
                "12345"
            );
            if (portStr == null || portStr.trim().isEmpty()) {
                new Mainmenu().setVisible(true);
                return;
            }

            int port = Integer.parseInt(portStr.trim());

            // 클라이언트 시작
            NetworkManager.getInstance().startClient(ip.trim(), port);

            // 접속 대기
            new Thread(() -> {
                int attempts = 0;
                while (!NetworkManager.getInstance().isConnected() && attempts < 50) {
                    try { Thread.sleep(100); } catch (Exception ex) {}
                    attempts++;
                }

                if (NetworkManager.getInstance().isConnected()) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, "서버에 접속했습니다!");
                        new ModeSelectionScreen(true).setVisible(true);
                    });
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, "서버 접속 실패!");
                        new Mainmenu().setVisible(true);
                    });
                }
            }).start();
        });

        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);

        // 버튼 배열 초기화
        buttons = new JButton[]{hostButton, joinButton};

        // 포커스 비활성화
        for (JButton btn : buttons) {
            btn.setFocusable(false);
        }

        updateButtonFocus();

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    /**
     * IP 주소 유효성 검사
     */
    private boolean isValidIP(String ip) {
        if ("localhost".equalsIgnoreCase(ip)) return true;

        String ipv4Pattern =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipv4Pattern);
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case java.awt.event.KeyEvent.VK_LEFT -> {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    updateButtonFocus();
                }
            }
            case java.awt.event.KeyEvent.VK_RIGHT -> {
                if (selectedIndex < buttons.length - 1) {
                    selectedIndex++;
                    updateButtonFocus();
                }
            }
            case java.awt.event.KeyEvent.VK_SPACE, java.awt.event.KeyEvent.VK_ENTER ->
                buttons[selectedIndex].doClick();
        }
    }

    private void updateButtonFocus() {
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                ((javax.swing.JComponent) buttons[i]).putClientProperty("opacity", 0.75f);
            } else {
                ((javax.swing.JComponent) buttons[i]).putClientProperty("opacity", 1.0f);
            }
            buttons[i].repaint();
        }
    }

    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }
}

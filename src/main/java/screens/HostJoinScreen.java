package screens;

import java.awt.BorderLayout;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
        //  HOST 버튼 (자동 포트)
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
                // ===== 자동 포트 할당 =====
                ServerSocket tempSocket = new ServerSocket(0);
                int port = tempSocket.getLocalPort();
                tempSocket.close();

                // IP 가져오기
                String hostIP = InetAddress.getLocalHost().getHostAddress();

                NetworkManager.getInstance().startServer(port);

                final boolean[] cancelled = {false};

                // ===============================
                //  모달이 아닌 WAITING JDialog 만들기
                // ===============================
                JDialog waitingDialog = new JDialog();
                waitingDialog.setTitle("Server Waiting");
                waitingDialog.setModal(false);
                waitingDialog.setSize(300, 200);
                waitingDialog.setLocationRelativeTo(null);

                JPanel dialogPanel = new JPanel();
                dialogPanel.setLayout(new BorderLayout());

                JOptionPane pane = new JOptionPane(
                    "Waiting for client...\n\nIP: " + hostIP + "\nPort: " + port,
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null,
                    new Object[]{"Close"}
                );

                waitingDialog.setContentPane(pane);

                // Close 버튼 눌렀을 때
                pane.addPropertyChangeListener(evt -> {
                    if (JOptionPane.VALUE_PROPERTY.equals(evt.getPropertyName())) {
                        cancelled[0] = true;
                        waitingDialog.dispose();
                        NetworkManager.getInstance().close();
                        new Mainmenu().setVisible(true);
                    }
                });

                waitingDialog.setVisible(true);

                // ===============================
                //  클라이언트 접속 대기 스레드
                // ===============================
                Thread waitThread = new Thread(() -> {
                    while (!NetworkManager.getInstance().isConnected() && !cancelled[0]) {
                        try { Thread.sleep(100); } catch (Exception ignored) {}
                    }

                    if (!cancelled[0] && NetworkManager.getInstance().isConnected()) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            waitingDialog.dispose(); // ⭐ 자동 닫힘
                            JOptionPane.showMessageDialog(null, "Client connected!");
                            new ModeSelectionScreen(true).setVisible(true);
                        });
                    }
                });

                waitThread.start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "Failed to start server: " + ex.getMessage());
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

            JTextField ipField = new JTextField("localhost");
            JTextField portField = new JTextField("12345");

            Object[] message = {
                "Enter Server IP:",
                ipField,
                "Enter Server Port:",
                portField
            };

            int option = JOptionPane.showConfirmDialog(
                null,
                message,
                "Connect to Server",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (option != JOptionPane.OK_OPTION) {
                new Mainmenu().setVisible(true);
                return;
            }

            String ip = ipField.getText().trim();
            String portStr = portField.getText().trim();

            if (ip.isEmpty() || portStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Both IP and Port must be entered.");
                new Mainmenu().setVisible(true);
                return;
            }

            if (!isValidIP(ip)) {
                JOptionPane.showMessageDialog(null, "Invalid IP format.");
                new Mainmenu().setVisible(true);
                return;
            }

            int port = Integer.parseInt(portStr);

            NetworkManager.getInstance().startClient(ip, port);

            new Thread(() -> {
                int attempts = 0;
                while (!NetworkManager.getInstance().isConnected() && attempts < 50) {
                    try { Thread.sleep(100); } catch (Exception ignored) {}
                    attempts++;
                }

                if (NetworkManager.getInstance().isConnected()) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Connected to server!");
                        new ModeSelectionScreen(true).setVisible(true);
                    });
                } else {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Failed to connect to server.");
                        new Mainmenu().setVisible(true);
                    });
                }
            }).start();
        });

        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);

        buttons = new JButton[]{hostButton, joinButton};

        for (JButton btn : buttons) btn.setFocusable(false);

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
                buttons[i].putClientProperty("opacity", 0.75f);
            } else {
                buttons[i].putClientProperty("opacity", 1.0f);
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

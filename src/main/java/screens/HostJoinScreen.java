package screens;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(null);

        int btnWidth = (int)(120 * scale);
        int btnHeight = (int)(120 * scale);

        // HOST
        JButton hostButton = addButton(
            mainPanel,
            "/images/HostButton.png",
            btnWidth - 50, btnHeight,
            (int)(width / 2.0 - btnWidth / 2.0 + 80 * scale),
            (int)(height / 2.0 - 30 * scale)
        );
        hostButton.addActionListener(e -> startAsHost());

        // JOIN
        JButton joinButton = addButton(
            mainPanel,
            "/images/HostingButton.png",
            btnWidth + 30, btnHeight,
            (int)(width / 2.0 - btnWidth / 2.0 + 170 * scale),
            (int)(height / 2.0 - 30 * scale)
        );
        joinButton.addActionListener(e -> startAsClient());

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

    // =========================
    // 최근 접속 IP 저장 / 로드
    // =========================
    private String loadLastIP() {
        try {
            File file = new File("last_ip.txt");
            if (!file.exists()) return "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            String ip = br.readLine();
            br.close();
            return ip == null ? "" : ip.trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void saveLastIP(String ip) {
        try {
            FileWriter fw = new FileWriter("last_ip.txt");
            fw.write(ip);
            fw.close();
        } catch (Exception ignored) {}
    }

    // =========================
    // HOST START
    // =========================
    private void startAsHost() {
        dispose();

        try {
            ServerSocket tempSocket = new ServerSocket(0);
            int port = tempSocket.getLocalPort();
            tempSocket.close();

            String hostIP = InetAddress.getLocalHost().getHostAddress();
            NetworkManager.getInstance().startServer(port);

            final boolean[] cancelled = {false};

            // HOST 대기 팝업
            JDialog waitingDialog = new JDialog();
            waitingDialog.setTitle("Server Waiting");
            waitingDialog.setModal(false);
            waitingDialog.setSize(300, 200);
            waitingDialog.setLocationRelativeTo(null);

            JOptionPane pane = new JOptionPane(
                "Waiting for client...\n\nIP: " + hostIP + "\nPort: " + port,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{"Close"}
            );
            waitingDialog.setContentPane(pane);

            pane.addPropertyChangeListener(evt -> {
                if (JOptionPane.VALUE_PROPERTY.equals(evt.getPropertyName())) {
                    cancelled[0] = true;
                    waitingDialog.dispose();
                    NetworkManager.getInstance().close();
                    new Mainmenu().setVisible(true);
                }
            });

            waitingDialog.setVisible(true);

            // Client 접속 대기 스레드
            new Thread(() -> {
                while (!NetworkManager.getInstance().isConnected() && !cancelled[0]) {
                    try { Thread.sleep(100); } catch (Exception ignored) {}
                }

                if (!cancelled[0] && NetworkManager.getInstance().isConnected()) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        waitingDialog.dispose();
                        JOptionPane.showMessageDialog(null, "Client connected!");

                        new WaitingRoomScreen(true).setVisible(true);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                "Failed to start server: " + ex.getMessage());
            new Mainmenu().setVisible(true);
        }
    }

    // =========================
    // CLIENT START
    // =========================
    private void startAsClient() {
        dispose();

        JTextField ipField = new JTextField(loadLastIP().isEmpty() ? "localhost" : loadLastIP());
        JTextField portField = new JTextField("12345");

        Object[] message = {
            "Enter Server IP:", ipField,
            "Enter Server Port:", portField
        };

        int option = JOptionPane.showConfirmDialog(
            null, message, "Connect to Server", JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            new Mainmenu().setVisible(true);
            return;
        }

        String ip = ipField.getText().trim();
        String portStr = portField.getText().trim();

        if (!isValidIP(ip)) {
            JOptionPane.showMessageDialog(null, "Invalid IP format.");
            new Mainmenu().setVisible(true);
            return;
        }

        int port = Integer.parseInt(portStr);

        JOptionPane.showMessageDialog(null, "Connecting to server...\n" + ip + ":" + port);

        NetworkManager.getInstance().startClient(ip, port);

        new Thread(() -> {
            int attempts = 0;

            while (!NetworkManager.getInstance().isConnected() && attempts < 50) {
                try { Thread.sleep(100); } catch (Exception ignored) {}
                attempts++;
            }

            if (NetworkManager.getInstance().isConnected()) {
                saveLastIP(ip); // 최근 IP 저장

                javax.swing.SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Connected to server!");
                    new WaitingRoomScreen(false).setVisible(true);
                });
            } else {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Failed to connect.");
                    new Mainmenu().setVisible(true);
                });
            }
        }).start();
    }

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
            if (i == selectedIndex)
                buttons[i].putClientProperty("opacity", 0.75f);
            else
                buttons[i].putClientProperty("opacity", 1.0f);
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

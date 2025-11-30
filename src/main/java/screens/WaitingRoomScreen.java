package screens;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Difficulty;
import network.NetworkManager;

public class WaitingRoomScreen extends JFrame {

    private final boolean isHost;
    private boolean myReady = false;
    private boolean enemyReady = false;

    private JLabel myStatusLabel;
    private JLabel enemyStatusLabel;
    private JButton readyButton;
    private JButton startButton;

    public WaitingRoomScreen(boolean isHost) {
        this.isHost = isHost;

        setTitle("P2P Waiting Room");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        initNetworkListener();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        setContentPane(panel);

        JLabel title = new JLabel(isHost ? "HOST Waiting Room" : "CLIENT Waiting Room");
        title.setBounds(90, 20, 250, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        myStatusLabel = new JLabel("Me: Not Ready");
        myStatusLabel.setBounds(50, 70, 300, 30);
        panel.add(myStatusLabel);

        enemyStatusLabel = new JLabel("Enemy: Not Ready");
        enemyStatusLabel.setBounds(50, 110, 300, 30);
        panel.add(enemyStatusLabel);

        readyButton = new JButton("READY");
        readyButton.setBounds(50, 160, 120, 40);
        readyButton.addActionListener(e -> toggleReady());
        panel.add(readyButton);

        startButton = new JButton("START");
        startButton.setBounds(200, 160, 120, 40);
        startButton.setEnabled(false);
        panel.add(startButton);

        // Client는 START 버튼 숨김
        if (!isHost) {
            startButton.setVisible(false);
        }

        // Host only
        startButton.addActionListener(e -> startGameAsHost());
    }

    // Ready toggle
    private void toggleReady() {
        myReady = !myReady;

        if (myReady) {
            myStatusLabel.setText("Me: READY");
            NetworkManager.getInstance().send("READY");
        } else {
            myStatusLabel.setText("Me: Not Ready");
            NetworkManager.getInstance().send("UNREADY");
        }

        updateStartButtonState();
    }

    private void updateStartButtonState() {
        if (isHost) {
            startButton.setEnabled(myReady && enemyReady);
        }
    }

    private void initNetworkListener() {

        NetworkManager.getInstance().setMessageListener(msg -> {

            // 상대 READY / UNREADY
            if (msg.equals("READY")) {
                enemyReady = true;
                enemyStatusLabel.setText("Enemy: READY");
                updateStartButtonState();
            }
            else if (msg.equals("UNREADY")) {
                enemyReady = false;
                enemyStatusLabel.setText("Enemy: Not Ready");
                updateStartButtonState();
            }

            // ========= Host가 모드 선택 후 클라이언트에게 보내는 MODE 전달 =========
            else if (msg.startsWith("MODE:")) {
                String mode = msg.substring(5);

                SwingUtilities.invokeLater(() -> {
                    dispose();
                    launchBattle(mode);
                });
            }
        });
    }

    // Host START 버튼 → Host만 모드 선택 화면으로 이동
    private void startGameAsHost() {
        if (!isHost) return;
        if (!myReady || !enemyReady) return;

        // Host만 모드 선택 화면으로 이동
        dispose();
        new NetworkModeSelectionScreen(true).setVisible(true);
    }

    // 클라이언트도 Host의 모드 결정 받으면 바로 BattlePanel로 이동
    private void launchBattle(String mode) {
        JFrame frame = new JFrame("P2P Battle");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        boolean isItem = mode.equals("ITEM");
        boolean isTime = mode.equals("TIME");

        P2PBattlePanel panel =
                new P2PBattlePanel(Difficulty.NORMAL, isItem, isTime);

        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.requestFocusInWindow();
    }
}

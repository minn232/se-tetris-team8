package screens;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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
    private Timer readyStatusTimer;

    public WaitingRoomScreen(boolean isHost) {
        this.isHost = isHost;

        setTitle("P2P Waiting Room");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        initNetworkListener();
        startReadyStatusTimer();
    }

    private void startReadyStatusTimer() {
        // 100ms마다 ready 상태 전송
        readyStatusTimer = new Timer(100, e -> {
            if (myReady) {
                NetworkManager.getInstance().send("READY");
            } else {
                NetworkManager.getInstance().send("UNREADY");
            }
        });
        readyStatusTimer.start();
    }

    @Override
    public void dispose() {
        if (readyStatusTimer != null) {
            readyStatusTimer.stop();
        }
        super.dispose();
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

        if (!isHost) {
            startButton.setVisible(false);
        }

        startButton.addActionListener(e -> startGameAsHost());
    }

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
            else if (msg.startsWith("MODE:")) {

                String mode = msg.substring(5);

                SwingUtilities.invokeLater(() -> {
                    dispose();
                    launchBattle(mode);
                });
            }
        });
    }

    private void startGameAsHost() {
        if (!isHost) return;
        if (!myReady || !enemyReady) return;

        dispose();
        new NetworkModeSelectionScreen(true).setVisible(true);
    }

    private void launchBattle(String mode) {

        JFrame frame = new JFrame("P2P Battle");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        boolean isItem = mode.equals("ITEM");
        boolean isTime = mode.equals("TIME");

        // 🔥🔥🔥 수정된 부분: isHost 추가
        P2PBattlePanel panel =
            new P2PBattlePanel(Difficulty.NORMAL, isItem, isTime, isHost);

        frame.setContentPane(panel);

        frame.setSize(
            panel.getPreferredSize().width + 20,
            panel.getPreferredSize().height + 180
        );

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.requestFocusInWindow();
    }
}

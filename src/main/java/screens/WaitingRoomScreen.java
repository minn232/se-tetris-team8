package screens;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import network.NetworkManager;

/**
 * 네트워크 대전 대기방
 * Host / Client 모두 같은 화면이지만 Host만 Start 가능
 */
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

        // 나의 Ready 상태
        myStatusLabel = new JLabel("Me: Not Ready");
        myStatusLabel.setBounds(50, 70, 300, 30);
        panel.add(myStatusLabel);

        // 상대 Ready 상태
        enemyStatusLabel = new JLabel("Enemy: Not Ready");
        enemyStatusLabel.setBounds(50, 110, 300, 30);
        panel.add(enemyStatusLabel);

        // READY 버튼
        readyButton = new JButton("READY");
        readyButton.setBounds(50, 160, 120, 40);
        readyButton.addActionListener(e -> toggleReady());
        panel.add(readyButton);

        // START 버튼 (Host만)
        startButton = new JButton("START");
        startButton.setBounds(200, 160, 120, 40);
        startButton.setEnabled(false);
        panel.add(startButton);

        if (!isHost) {
            startButton.setVisible(false); // Client는 Start 버튼 숨김
        }

        startButton.addActionListener(e -> startGame());
    }

    // Ready ON/OFF
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

    // Host만 Start 버튼 활성화
    private void updateStartButtonState() {
        if (isHost) {
            startButton.setEnabled(myReady && enemyReady);
        }
    }

    // 네트워크 메시지 처리
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

            // ===== Host가 STARTGAME 보낸 경우 =====
            else if (msg.equals("STARTGAME")) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    launchModeSelectionScreen();  // ← 여기로 이동!!!
                });
            }
        });
    }

    // Host가 게임 시작 버튼 클릭 시 실행
    private void startGame() {
        if (!isHost) return;

        if (myReady && enemyReady) {
            NetworkManager.getInstance().send("STARTGAME");

            dispose();
            launchModeSelectionScreen(); // ← 여기로 이동!!!
        }
    }

    // ============================
    // 다음 화면: 모드 선택 화면으로 이동
    // ============================
    private void launchModeSelectionScreen() {
        new NetworkModeSelectionScreen(true).setVisible(true);
    }
}
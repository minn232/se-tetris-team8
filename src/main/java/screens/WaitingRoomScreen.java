package screens;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import network.NetworkManager;

/**
 * P2P 대전모드 - 대기방 화면
 * Host/Client 모두 Ready 가능
 * Host만 Start 가능
 * Start 누르면 -> NetworkModeSelectionScreen으로 이동
 */
public class WaitingRoomScreen extends JFrame {

    private boolean isHost;
    private boolean meReady = false;
    private boolean enemyReady = false;

    private JLabel myStatusLabel;
    private JLabel enemyStatusLabel;
    private JButton readyButton;
    private JButton startButton;

    public WaitingRoomScreen(boolean isHost) {
        this.isHost = isHost;

        setTitle("Waiting Room");
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // ====== 상태 라벨 ======
        myStatusLabel = new JLabel("ME: Not Ready");
        myStatusLabel.setBounds(50, 30, 300, 30);
        myStatusLabel.setForeground(Color.WHITE);

        enemyStatusLabel = new JLabel("ENEMY: Not Ready");
        enemyStatusLabel.setBounds(50, 70, 300, 30);
        enemyStatusLabel.setForeground(Color.WHITE);

        // ====== Ready 버튼 ======
        readyButton = new JButton("Ready");
        readyButton.setBounds(50, 150, 120, 40);
        readyButton.addActionListener(e -> toggleReady());

        // ====== Start 버튼 (Host만 활성화) ======
        startButton = new JButton("Start");
        startButton.setBounds(200, 150, 120, 40);
        startButton.setEnabled(false);   // 둘 다 Ready 해야 활성화됨

        if (!isHost) startButton.setEnabled(false);

        startButton.addActionListener(e -> hostStartGame());

        // ====== 컴포넌트 추가 ======
        add(myStatusLabel);
        add(enemyStatusLabel);
        add(readyButton);
        add(startButton);

        // ====== 배경 패널 ======
        JPanel bg = new JPanel();
        bg.setBackground(Color.BLACK);
        bg.setBounds(0, 0, 400, 300);
        add(bg);

        // 네트워크 메시지 수신 핸들러
        NetworkManager.getInstance().setMessageListener(this::onNetworkMessage);

        setVisible(true);
    }

    // ================================
    // Ready / Cancel Ready 전송
    // ================================
    private void toggleReady() {
        meReady = !meReady;

        if (meReady) {
            myStatusLabel.setText("ME: Ready");
            readyButton.setText("Cancel Ready");
            NetworkManager.getInstance().send("READY:ME");
        } else {
            myStatusLabel.setText("ME: Not Ready");
            readyButton.setText("Ready");
            NetworkManager.getInstance().send("READY_CANCEL:ME");
        }

        updateStartButton();
    }

    // ================================
    // Host가 Start 누르는 경우
    // ================================
    private void hostStartGame() {
        if (!isHost) return;
        if (!(meReady && enemyReady)) return;

        NetworkManager.getInstance().send("GOTO_MODE_SELECT");

        openModeSelection();
    }

    // ================================
    // 네트워크 메시지 처리
    // ================================
    private void onNetworkMessage(String msg) {

        // 상대 Ready
        if (msg.equals("READY:ME") || msg.equals("READY:HOST")) {
            enemyReady = true;
            enemyStatusLabel.setText("ENEMY: Ready");
        }
        // 상대 Ready 취소
        else if (msg.equals("READY_CANCEL:ME") || msg.equals("READY_CANCEL:HOST")) {
            enemyReady = false;
            enemyStatusLabel.setText("ENEMY: Not Ready");
        }
        // Host가 모드 선택 화면으로 이동하라는 신호
        else if (msg.equals("GOTO_MODE_SELECT")) {
            openModeSelection();
        }

        updateStartButton();
    }

    // ================================
    // Start 버튼 활성화 여부 결정 (Host)
    // ================================
    private void updateStartButton() {
        if (isHost) {
            startButton.setEnabled(meReady && enemyReady);
        }
    }

    // ================================
    // 모드 선택 화면 열기
    // ================================
    private void openModeSelection() {
        SwingUtilities.invokeLater(() -> {
            dispose();
            new NetworkModeSelectionScreen(isHost).setVisible(true);
        });
    }
}

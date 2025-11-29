package screens;

import java.awt.GridLayout;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import network.NetworkManager;

public class P2PMenuScreen extends JPanel {
    private final JFrame frame;

    public P2PMenuScreen(JFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(4, 1, 10, 10));

        JButton serverBtn = new JButton("서버 열기");
        JButton clientBtn = new JButton("클라이언트 접속");

        JTextField ipField = new JTextField("서버 IP 입력");
        JLabel status = new JLabel("상태: 대기중", SwingConstants.CENTER);

        add(serverBtn);
        add(clientBtn);
        add(ipField);
        add(status);

        // =============================
        // 🔥 서버 모드
        // =============================
        serverBtn.addActionListener(e -> {
            NetworkManager.getInstance().startServer(7777);

            String myIp = getLocalIPAddress();

            status.setText("<html>서버 실행중...<br>IP: " + myIp +
                    "<br>포트: 7777<br>이 주소로 상대방이 접속해야 합니다.</html>");

            // 연결 감시 쓰레드
            new Thread(() -> {
                while (!NetworkManager.getInstance().isConnected()) {
                    try { Thread.sleep(200); } catch (Exception ignored) {}
                }
                status.setText("연결 성공! 게임 화면으로 이동 중...");
                openBattle();
            }).start();
        });

        // =============================
        // 🔥 클라이언트 모드
        // =============================
        clientBtn.addActionListener(e -> {
            String ip = ipField.getText().trim();
            NetworkManager.getInstance().startClient(ip, 7777);

            status.setText("서버 접속 시도 중...");

            new Thread(() -> {
                while (!NetworkManager.getInstance().isConnected()) {
                    try { Thread.sleep(200); } catch (Exception ignored) {}
                }
                status.setText("서버 연결됨! 게임 화면으로 이동 중...");
                openBattle();
            }).start();
        });
    }

    // =============================
    // 🔥 P2P 배틀 화면으로 전환
    // =============================
    private void openBattle() {
        SwingUtilities.invokeLater(() -> {
            // TODO: 난이도와 모드를 선택할 수 있도록 개선
            // 현재는 기본값으로 NORMAL, 아이템 모드 false, 시간제한 모드 false
            frame.setContentPane(new P2PBattlePanel(core.Difficulty.NORMAL, false, false));
            frame.revalidate();
            frame.repaint();
        });
    }

    // =============================
    // 🔥 내 로컬 IP 가져오기
    // =============================
    private String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "IP 조회 실패";
        }
    }
}

package screens;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import core.Board;
import core.Difficulty;
import network.NetworkManager;

public class P2PBattlePanel extends JPanel {

    private final JTextArea chatLog;
    private final JTextField chatInput;

    public P2PBattlePanel(JFrame frame) {

        setLayout(new BorderLayout());

        // ======= 보드 영역 (내 보드 + 상대 보드) =======
        JPanel boards = new JPanel(new GridLayout(1, 2));

        // 내 보드 → 기존 GamePanel 재사용
        Board myBoardCore = new Board(Difficulty.NORMAL, false);
        GamePanel myBoard = new GamePanel(myBoardCore, false);

        // 상대 보드 → 간단 렌더 패널
        EnemyBoardPanel enemyBoard = new EnemyBoardPanel();

        boards.add(myBoard);
        boards.add(enemyBoard);

        add(boards, BorderLayout.CENTER);

        // ======= 채팅 영역 =======
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatLog = new JTextArea();
        chatLog.setEditable(false);

        JScrollPane scroll = new JScrollPane(chatLog);
        chatPanel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        chatInput = new JTextField();
        JButton sendBtn = new JButton("전송");

        bottom.add(chatInput, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        chatPanel.add(bottom, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.SOUTH);

        // 채팅 전송 버튼
        sendBtn.addActionListener(e -> sendChat());
        chatInput.addActionListener(e -> sendChat());
    }

    private void sendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;

        NetworkManager.getInstance().send("CHAT:" + msg);
        chatLog.append("나: " + msg + "\n");
        chatInput.setText("");
    }

    public void addChat(String msg) {
        chatLog.append(msg + "\n");
    }
}

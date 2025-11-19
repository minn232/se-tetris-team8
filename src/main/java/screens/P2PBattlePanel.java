package screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

    private Board myBoard;
    private Board enemyBoard;

    private GamePanel myPanel;
    private GamePanel enemyPanel;

    private JTextArea chatLog;
    private JTextField chatInput;

    public P2PBattlePanel(JFrame frame) {

        setLayout(new BorderLayout());

        // ==========================
        // 1) 내 보드 / 상대 보드
        // ==========================
        myBoard = new Board(Difficulty.NORMAL, false);
        enemyBoard = new Board(Difficulty.NORMAL, false); // 렌더링 전용

        myPanel = new GamePanel(myBoard, false);
        enemyPanel = new GamePanel(enemyBoard, false);

        // 상대가 포커스를 받지 못하게
        enemyPanel.setFocusable(false);

        JPanel boardArea = new JPanel(new GridLayout(1, 2, 15, 0));
        boardArea.add(myPanel);
        boardArea.add(enemyPanel);

        add(boardArea, BorderLayout.CENTER);

        // ==========================
        // 채팅 UI
        // ==========================
        chatLog = new JTextArea();
        chatLog.setEditable(false);
        chatLog.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(chatLog);

        chatInput = new JTextField();
        JButton sendBtn = new JButton("전송");

        JPanel chatBottom = new JPanel(new BorderLayout());
        chatBottom.add(chatInput, BorderLayout.CENTER);
        chatBottom.add(sendBtn, BorderLayout.EAST);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(chatBottom, BorderLayout.SOUTH);
        chatPanel.setPreferredSize(new Dimension(0, 150));

        add(chatPanel, BorderLayout.SOUTH);

        // ==========================
        // 채팅 보내기
        // ==========================
        sendBtn.addActionListener(e -> sendChat());
        chatInput.addActionListener(e -> sendChat());

        // ==========================
        // 네트워크 메시지 수신 핸들러
        // ==========================
        NetworkManager.getInstance().setMessageListener(msg -> {

            // 채팅
            if (msg.startsWith("CHAT:")) {
                addChat("[상대] " + msg.substring(5));
            }

            // 보드 업데이트 (추후 구현)
            if (msg.startsWith("BOARD:")) {
                String json = msg.substring(6);
                // TODO: enemyBoard 반영
                enemyPanel.repaint();
            }

        });
    }

    private void sendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;

        NetworkManager.getInstance().send("CHAT:" + msg);
        addChat("[나] " + msg);

        chatInput.setText("");
    }

    private void addChat(String msg) {
        chatLog.append(msg + "\n");
        chatLog.setCaretPosition(chatLog.getDocument().getLength());
    }
}

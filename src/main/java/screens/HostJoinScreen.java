package screens;

import java.awt.BorderLayout;
import java.net.ServerSocket;

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
        
        JButton hostButton = addButton(mainPanel, "/images/HostButton.png", 
            btnWidth-50, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 + 80 * scale), (int)(height / 2.0 - 30 * scale));
        hostButton.addActionListener(e -> {
            System.out.println("[HostJoin] Host selected");
            dispose();
            
            try {
                // 서버 모드로 시작
                ServerSocket serverSocket = new ServerSocket(0); // 0 = 자동 할당
                int port = serverSocket.getLocalPort();
                serverSocket.close(); // 임시로 열었던 소켓 닫기
                System.out.println("할당된 포트: " + port);
                
                NetworkManager.getInstance().startServer(port);
                
                // 대기 중 닫기 가능한 대화상자
                final boolean[] cancelled = {false};
                Thread waitThread = new Thread(() -> {
                    while (!NetworkManager.getInstance().isConnected() && !cancelled[0]) {
                        try { Thread.sleep(100); } catch (Exception ex) {}
                    }
                    if (!cancelled[0] && NetworkManager.getInstance().isConnected()) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            javax.swing.JOptionPane.showMessageDialog(null, "상대방이 접속했습니다!");
                            new ModeSelectionScreen(true).setVisible(true);
                        });
                    }
                });
                waitThread.start();
                
                String[] options = {"닫기"};
                int result = javax.swing.JOptionPane.showOptionDialog(
                    null,
                    "서버 대기 중...\n포트: " + port + "\n상대방이 접속할 때까지 대기합니다.",
                    "서버 대기",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                
                // 닫기 버튼 클릭 시
                if (result == 0 || result == javax.swing.JOptionPane.CLOSED_OPTION) {
                    cancelled[0] = true;
                    NetworkManager.getInstance().close();
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        new Mainmenu().setVisible(true);
                    });
                }
            } catch (java.io.IOException ioEx) {
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "서버 시작 실패: " + ioEx.getMessage());
                new Mainmenu().setVisible(true);
            }
        });
        
        JButton joinButton = addButton(mainPanel, "/images/HostingButton.png", 
            btnWidth+30, btnHeight, 
            (int)(width / 2.0 - btnWidth / 2.0 +170 * scale), (int)(height / 2.0 - 30 * scale));
        joinButton.addActionListener(e -> {
            System.out.println("[HostJoin] Join selected");
            dispose();
            
            String ip = null;
            boolean validInput = false;
            
            while (!validInput) {
                // IP 주소 입력 받기
                ip = javax.swing.JOptionPane.showInputDialog(
                    this, 
                    "서버 IP 주소를 입력하세요:",
                    "localhost"
                );
                
                // 취소 또는 빈 입력
                if (ip == null || ip.trim().isEmpty()) {
                    new Mainmenu().setVisible(true);
                    return;
                }
                
                // IP 주소 유효성 검사
                if (isValidIP(ip.trim())) {
                    validInput = true;
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "올바른 IP 주소 형식이 아닙니다.\n예: 192.168.0.1 또는 localhost",
                        "입력 오류",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            
            if (ip != null && !ip.trim().isEmpty()) {
                int port = 12345;
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
            } else {
                new Mainmenu().setVisible(true);
            }
        });
        
        BackgroundPanel bg = new BackgroundPanel("/images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
        
        // 버튼 배열 초기화 (키보드 네비게이션용)
        buttons = new JButton[]{hostButton, joinButton};
        
        // 모든 버튼의 포커스 비활성화
        for (JButton btn : buttons) {
            btn.setFocusable(false);
        }
        
        // 초기 포커스 설정
        updateButtonFocus();
        
        // 키보드 리스너 추가
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
        // localhost 허용
        if ("localhost".equalsIgnoreCase(ip)) {
            return true;
        }
        
        // IPv4 형식만 허용: xxx.xxx.xxx.xxx (점으로 구분된 4개의 숫자)
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
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
                buttons[i].repaint();
            } else {
                ((javax.swing.JComponent) buttons[i]).putClientProperty("opacity", 1.0f);
                buttons[i].repaint();
            }
        }
    }
    
    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = ImageButtonUtils.createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }
}

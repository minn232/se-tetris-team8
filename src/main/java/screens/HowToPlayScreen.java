package screens;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import core.Settings;

/**
 * 게임 설명 화면
 */
public class HowToPlayScreen extends JDialog {

    public HowToPlayScreen() {
        super((java.awt.Frame) null, "How to Play", true); // 모달 다이얼로그
        
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        double scale = Settings.getScaleFactor();
        
        setTitle("How to Play");
        setSize(width, height);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // HowToPlay 이미지 표시
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/HowToPlay.png"));
            // 이미지를 화면 크기에 맞게 스케일링 (버튼 공간 제외)
            int imageHeight = (int)(height - 70*scale); // 버튼 영역 제외 (scale 비례)
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                width, 
                imageHeight,
                Image.SCALE_SMOOTH
            );
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println("Failed to load HowToPlay.png: " + e.getMessage());
        }
        
        // Close 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        closeButton.setPreferredSize(new java.awt.Dimension((int)(100 * scale), (int)(40 * scale)));
        closeButton.addActionListener(e -> dispose());
        closeButton.setFocusable(false);
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(closeButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // ESC 키로 닫기
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }
}
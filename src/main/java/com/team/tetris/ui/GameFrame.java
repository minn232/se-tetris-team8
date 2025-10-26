package com.team.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;
import com.team.tetris.core.Tetromino;
import com.team.tetris.render.GamePanel;

public class GameFrame extends JFrame {
    private final Board board = new Board();

    // 점수는 게임 중에 변경되므로 final 제거
    private int score = 0;
    // 점수 라벨은 여러 곳에서 갱신해야 하므로 필드로 승격
    private final JLabel scoreLabel = new JLabel();

    public GameFrame() {
        super("SE Tetris Team8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 중앙: 보드
        GamePanel gamePanel = new GamePanel(board);

        // 우측: 다음 블록/점수 패널
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nextLabel = new JLabel("Next Block");
        nextLabel.setFont(nextLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel nextPreview = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawString("(미리보기 자리)", 10, 20);
            }
        };
        nextPreview.setPreferredSize(new Dimension(140, 120));
        nextPreview.setBackground(new Color(250, 250, 250));
        nextPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 점수 라벨 초기화
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 16f));
        scoreLabel.setText("Score: " + score);

        side.add(nextLabel);
        side.add(Box.createVerticalStrut(6));
        side.add(nextPreview);
        side.add(Box.createVerticalStrut(20));
        side.add(scoreLabel);
        side.add(Box.createVerticalGlue());

        // 레이아웃 배치
        JPanel root = new JPanel(new BorderLayout());
        root.add(gamePanel, BorderLayout.CENTER);
        root.add(side, BorderLayout.EAST);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        // ===== 테스트: 랜덤 블록 하나 보드에 배치 =====
        ShapeType[] types = ShapeType.values();
        ShapeType t = types[new Random().nextInt(types.length)];
        Tetromino piece = new Tetromino(t, Board.COLS / 2, 1); // 중앙 위쪽
        if (piece.canPlace(board)) {
            piece.place(board);
            // 테스트로 점수 100 추가 (실제 게임 로직에서는 클리어/드롭 등에서 호출)
            updateScore(100);
        }
        gamePanel.repaint();
    }

    /** 점수 갱신 헬퍼 */
    private void updateScore(int delta) {
        score += delta;
        scoreLabel.setText("Score: " + score);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set LookAndFeel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(GameFrame::new);
    }
}

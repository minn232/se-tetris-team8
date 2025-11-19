package screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class EnemyBoardPanel extends JPanel {

    private int[][] boardData = new int[20][10]; // 상대 보드 데이터

    public EnemyBoardPanel() {
        setPreferredSize(new Dimension(300, 600));
    }

    public void updateBoard(int[][] data) {
        this.boardData = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 단순 박스 렌더링
        for (int y = 0; y < boardData.length; y++) {
            for (int x = 0; x < boardData[y].length; x++) {
                if (boardData[y][x] != 0) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GRAY);
                }
                g.fillRect(x * 30, y * 30, 30, 30);
                g.setColor(Color.BLACK);
                g.drawRect(x * 30, y * 30, 30, 30);
            }
        }
    }
}

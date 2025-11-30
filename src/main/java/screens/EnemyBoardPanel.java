package screens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import core.Position;
import core.ShapeType;
import core.Tetromino;

public class EnemyBoardPanel extends JPanel {

    private ShapeType[][] boardData = new ShapeType[20][10]; // 고정된 상대 보드 데이터
    private Tetromino enemyCurrent = null; // 상대 현재 블록

    private final int CELL = 30;

    public EnemyBoardPanel() {
        setPreferredSize(new Dimension(10 * CELL, 20 * CELL));
        setBackground(Color.BLACK);
    }

    /**
     * 상대방 grid + 현재블록을 한 번에 업데이트
     */
    public void updateBoard(ShapeType[][] grid, Tetromino cur) {
        this.boardData = grid;
        this.enemyCurrent = cur;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ---------------------------
        // 1) 고정 블록(Grid) 렌더링
        // ---------------------------
        for (int y = 0; y < boardData.length; y++) {
            for (int x = 0; x < boardData[y].length; x++) {
                ShapeType s = boardData[y][x];

                if (s != null) {
                    g.setColor(s.getColor());
                    g.fillRect(x * CELL, y * CELL, CELL, CELL);
                }

                // Grid 라인
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x * CELL, y * CELL, CELL, CELL);
            }
        }

        // ---------------------------
        // 2) 현재 Tetromino 렌더링
        // ---------------------------
        if (enemyCurrent != null) {
            g.setColor(enemyCurrent.getShape().getColor());

            for (Position p : enemyCurrent.getBlocks()) {
                int px = enemyCurrent.getX() + p.x;
                int py = enemyCurrent.getY() + p.y;

                if (px >= 0 && px < 10 && py >= 0 && py < 20) {
                    g.fillRect(px * CELL, py * CELL, CELL, CELL);

                    g.setColor(g.getColor().darker());
                    g.drawRect(px * CELL, py * CELL, CELL, CELL);

                    g.setColor(enemyCurrent.getShape().getColor());
                }
            }
        }
    }
}

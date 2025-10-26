package com.team.tetris.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JPanel;

import com.team.tetris.core.Board;

public class GamePanel extends JPanel {
    private final Board board;

    // ===== 렌더 옵션 =====
    private static final int CELL = 26;    // 셀 한 변 픽셀
    private static final int PAD  = 12;    // 보드 바깥 패딩

    // 블록 렌더링 모드
    private static final boolean DRAW_RING = true;  // true: 링(테두리), false: 꽉 찬 원
    private static final int RING_WIDTH = 6;        // 링 두께(px)
    private static final int MARGIN = 3;            // 셀 내부 여백

    // 글로우(부드러운 외곽, 0으로 끄기)
    private static final int GLOW_STEPS = 2;        // 단계 수(0이면 없음)
    private static final int GLOW_EXTRA = 3;        // 단계당 두께 증가량
    private static final int GLOW_ALPHA = 70;       // 0~255

    // === 테두리 X 표시 옵션 ===
    private static final boolean DRAW_BORDER_X = true;
    private static final Color BORDER_X_COLOR = Color.WHITE;
    private static final float BORDER_X_STROKE = 3f; // 기본 그리기 스트로크
    private static final float BORDER_X_LINE_STROKE = 5f; // drawXShape 내부 교차선 굵기
    private static final int BORDER_X_MARGIN = 4;   // X 모양 셀 내부 여백

    public GamePanel(Board board) {
        this.board = board;
        int w = PAD * 2 + CELL * (Board.COLS + 2); // 좌우 X 포함
        int h = PAD * 2 + CELL * (Board.ROWS + 2); // 상하 X 포함
        setPreferredSize(new Dimension(w, h));
        setBackground(new Color(30, 30, 30));
    }

    // 블록 타입별 색상
    private Color colorOf(char ch) {
        return switch (ch) {
            case 'I' -> new Color(0, 255, 255);   // 시안
            case 'O' -> new Color(255, 255, 0);   // 노랑
            case 'T' -> new Color(160, 0, 240);   // 보라
            case 'L' -> new Color(255, 160, 0);   // 주황
            case 'J' -> new Color(0, 80, 255);    // 파랑
            case 'S' -> new Color(0, 200, 0);     // 초록
            case 'Z' -> new Color(220, 0, 0);     // 빨강
            default  -> Color.WHITE;              // 기타
        };
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        // 안티앨리어싱
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int ox = PAD;
        final int oy = PAD;

        // 전체 배경(보더 포함 영역)
        g.setColor(Color.BLACK);
        g.fillRect(ox, oy, CELL * (Board.COLS + 2), CELL * (Board.ROWS + 2));

        // ── 1) 테두리 X 그리기 (보드 바깥 테두리 1셀 영역) ──
        if (DRAW_BORDER_X) {
            g.setColor(BORDER_X_COLOR);
            g.setStroke(new BasicStroke(BORDER_X_STROKE));

            for (int r = 0; r < Board.ROWS + 2; r++) {
                for (int c = 0; c < Board.COLS + 2; c++) {
                    boolean isBorder = (r == 0 || r == Board.ROWS + 1 || c == 0 || c == Board.COLS + 1);
                    if (!isBorder) continue;

                    int x = ox + c * CELL;
                    int y = oy + r * CELL;
                    drawXShape(g, x, y, CELL, BORDER_X_MARGIN);
                }
            }
        }

        // ── 2) 내부 보드(10x20) 원형 렌더 ──
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLS; c++) {
                char ch = board.getCell(r, c);
                if (ch == ' ') continue;

                // 셀 기준 위치 (테두리 1셀 오프셋)
                int cellX = ox + (c + 1) * CELL;
                int cellY = oy + (r + 1) * CELL;

                // 원 위치/크기
                int d = CELL - MARGIN * 2; // 지름
                int x = cellX + MARGIN;
                int y = cellY + MARGIN;

                Color base = colorOf(ch);

                // 글로우(외곽선 확장 + 반투명)
                if (GLOW_STEPS > 0) {
                    for (int i = GLOW_STEPS; i >= 1; i--) {
                        int w = RING_WIDTH + i * GLOW_EXTRA;
                        g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), GLOW_ALPHA));
                        g.drawOval(x, y, d, d);
                    }
                }

                // 메인 도형
                g.setColor(base);
                if (DRAW_RING) {
                    g.setStroke(new BasicStroke(RING_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawOval(x, y, d, d);
                } else {
                    g.fillOval(x, y, d, d);
                }
            }
        }
    }

    /** 이전 코드의 X 모양 그리기 로직을 헬퍼로 이식 */
    private void drawXShape(Graphics2D g, int x, int y, int size, int margin) {
        int x1 = x + margin, y1 = y + margin;
        int x2 = x + size - margin, y2 = y + size - margin;

        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(BORDER_X_LINE_STROKE)); // 교차선 굵기
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y1, x1, y2);
        g.setStroke(old);
    }
}

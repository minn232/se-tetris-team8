package com.team.tetris.core;

import java.util.List;

public class Tetromino {
    public final ShapeType type;
    public int rot;      // 0~3
    public int x, y;     // 원점(열, 행). 보드 기준

    public Tetromino(ShapeType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.rot = 0;
    }

    public List<Position> cells() {
        return type.cells(rot, x, y);
    }

    public List<Position> cellsIf(int rotDelta, int dx, int dy) {
        int newRot = rot + rotDelta;
        return type.cells(newRot, x + dx, y + dy);
    }

    /** 보드에 배치 가능한지(충돌/경계 검사) */
    public boolean canPlace(Board board) {
        return board.canPlace(cells());
    }

    /** 보드에 실제로 그리기 */
    public void place(Board board) {
        board.place(cells(), type.ch);
    }
}
package com.team.tetris.core;

public class Tetromino {
    private final ShapeType shape;
    protected int x, y;
    private int rot; // 0~3
    protected Position[] blocks; // 현재 회전에 따른 상대 좌표 4개

    public Tetromino(ShapeType shape, int x, int y) {
        this.shape = shape;
        this.x = x; this.y = y;
        this.rot = 0;
        if (shape != null) {
            this.blocks = shape.getOffsets(rot);
        }
    }

    public void move(int dx, int dy) { this.x += dx; this.y += dy; }

    public Tetromino getRotatedCopy() {
        Tetromino c = new Tetromino(shape, x, y);
        c.rot = (this.rot + 1) & 3;
        c.blocks = shape.getOffsets(c.rot);
        return c;
    }

    public void rotate() {
        this.rot = (this.rot + 1) & 3;
        this.blocks = shape.getOffsets(this.rot);
    }
    
    public void setBlocks(Position[] customBlocks) {
        this.blocks = customBlocks;
    }

    public Position[] getBlocks() { return blocks; }
    public int getX() { return x; }
    public int getY() { return y; }
    public ShapeType getShape() { return shape; }
    public int getRotation() { return rot; }
}

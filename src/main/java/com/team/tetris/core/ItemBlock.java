package com.team.tetris.core;

import java.awt.Color;

/**
 * 모든 아이템 블록의 공통 기능을 담은 추상 클래스
 * 새로운 아이템 블록을 추가할 때 이 클래스를 상속받아 구현
 */
public abstract class ItemBlock extends Tetromino {
    protected boolean lockedHorizontal = false;
    
    public ItemBlock(int x, int y, Position[] blockShape) {
        super(null, x, y);
        this.blocks = blockShape;
    }
    
    @Override
    public void rotate() {
        // 아이템 블록은 기본적으로 회전 불가
    }
    
    @Override
    public ShapeType getShape() {
        return null; // 아이템 블록은 ShapeType이 없음
    }
    
    // 각 아이템 블록이 구현해야 하는 고유 기능
    public abstract boolean moveDown(Board board);
    public abstract void moveLeft(Board board);
    public abstract void moveRight(Board board);
    public abstract Color getColor(); // 각 아이템별 색상
    
    // 좌우 이동 잠금 상태 반환
    public boolean isLockedHorizontal() {
        return lockedHorizontal;
    }
}

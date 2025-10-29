package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;

/**
 * 라인 아이템 블록 - 'L' 문자가 있는 블록이 배치된 줄의 모든 블록을 제거
 * 랜덤한 테트로미노 형태를 가지며, 회전에 따라 'L' 위치가 변경됨
 */
public class LineBlock implements ItemBlock {
    
    private static final Random random = new Random();
    private final ShapeType baseShape;
    private final int lineBlockIndexInRotation0;
    private int rotation;
    
    public LineBlock() {
        ShapeType[] allShapes = ShapeType.values();
        this.baseShape = allShapes[random.nextInt(allShapes.length)];
        this.rotation = 0;
        
        int blockCount = baseShape.getOffsets(0).length;
        this.lineBlockIndexInRotation0 = random.nextInt(blockCount);
    }
    
    @Override
    public String getName() {
        return "Line";
    }
    
    @Override
    public Color getColor() {
        return new Color(100, 255, 100);
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape;
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        // Board에서 특별한 처리 필요
    }
    
    public void activateEffectAtPosition(Object boardObj, int x, int y, int blockIndex) {
        if (!(boardObj instanceof Board)) return;
        
        if (blockIndex == getLineBlockIndex()) {
            Board board = (Board) boardObj;
            board.clearLine(y);
        }
    }
    
    @Override
    public String getDescription() {
        return "전체 가로줄을 제거합니다";
    }
    
    @Override
    public char getSymbol() {
        return 'L';
    }
    
    public char getBlockSymbol(int blockIndex) {
        return (blockIndex == getLineBlockIndex()) ? 'L' : ' ';
    }
    
    public int getLineBlockIndex() {
        return baseShape.getMappedIndex(rotation, lineBlockIndexInRotation0);
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    
    public int getRotation() {
        return rotation;
    }
}
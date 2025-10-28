package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;

/**
 * 라인 아이템 블록 - 'L' 문자가 있는 블록이 배치된 줄의 모든 블록을 제거
 */
public class LineBlock implements ItemBlock {
    
    private static final Random random = new Random();
    private final ShapeType baseShape;
    private final int targetBlockIndex; // 'L' 문자를 표시할 블록의 인덱스
    
    public LineBlock() {
        // 모든 ShapeType 중 랜덤으로 선택
        ShapeType[] allShapes = ShapeType.values();
        this.baseShape = allShapes[random.nextInt(allShapes.length)];
        
        // 해당 shape의 블록 개수에 따라 랜덤으로 하나 선택
        int blockCount = baseShape.getOffsets(0).length;
        this.targetBlockIndex = random.nextInt(blockCount);
    }
    
    @Override
    public String getName() {
        return "Line";
    }
    
    @Override
    public Color getColor() {
        return new Color(100, 255, 100); // 녹색
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape; // 랜덤으로 선택된 모양
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        // 기본 activateEffect는 사용하지 않음
        // Board에서 특별한 처리가 필요함
    }
    
    /**
     * LineBlock 전용 효과 발동 메소드
     * 'L' 문자가 있는 블록의 위치에서만 효과 발동
     */
    public void activateEffectAtPosition(Object boardObj, int x, int y, int blockIndex) {
        if (!(boardObj instanceof Board)) return;
        
        // 'L' 문자가 있는 블록에서만 효과 발동
        if (blockIndex == targetBlockIndex) {
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
    
    /**
     * 특정 블록 인덱스에 대한 심볼을 반환
     * targetBlockIndex에 해당하는 블록만 'L'을 표시하고, 나머지는 공백
     */
    public char getBlockSymbol(int blockIndex) {
        return (blockIndex == targetBlockIndex) ? 'L' : ' ';
    }
    
    /**
     * 'L' 문자가 표시되는 블록의 인덱스를 반환
     */
    public int getTargetBlockIndex() {
        return targetBlockIndex;
    }
}
package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;

/**
 * 슬로우 아이템 블록 - 10초간 게임 속도를 0.5배로 감소
 * 랜덤한 테트로미노 형태를 가지며, 'S' 표시
 */
public class SlowBlock implements ItemBlock {
    
    private static final Random random = new Random();
    private final ShapeType baseShape;
    
    public SlowBlock() {
        ShapeType[] allShapes = ShapeType.values();
        this.baseShape = allShapes[random.nextInt(allShapes.length)];
    }
    
    @Override
    public String getName() {
        return "Slow";
    }
    
    @Override
    public Color getColor() {
        return new Color(173, 216, 230);
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape;
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        if (!(boardObj instanceof Board)) return;
        
        Board board = (Board) boardObj;
        board.activateSlowEffect();
    }
    
    @Override
    public String getDescription() {
        return "10초간 게임 속도를 0.5배로 감소시킵니다";
    }
    
    @Override
    public char getSymbol() {
        return 'S';
    }
    
    public char getBlockSymbol(int blockIndex) {
        return (blockIndex == 0) ? 'S' : ' ';
    }
}
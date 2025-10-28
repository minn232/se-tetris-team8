package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;

/**
 * 슬로우 아이템 블록 - 10초간 게임 속도를 0.5배로 감소
 */
public class SlowBlock implements ItemBlock {
    
    private static final Random random = new Random();
    private final char[] blockSymbols; // 각 블록별 심볼 배열
    private final ShapeType baseShape;
    
    public SlowBlock() {
        // 모든 ShapeType 중 랜덤으로 선택
        ShapeType[] allShapes = ShapeType.values();
        this.baseShape = allShapes[random.nextInt(allShapes.length)];
        
        // 테트로미노의 블록 개수만큼 S, L, O, W를 순서대로 또는 랜덤으로 배치
        this.blockSymbols = generateBlockSymbols();
    }
    
    /**
     * 각 블록에 들어갈 심볼을 생성
     */
    private char[] generateBlockSymbols() {
        // 테트로미노는 항상 4개 블록이므로 S, L, O, W를 순서대로 배치
        return new char[]{'S', 'L', 'O', 'W'};
    }
    
    @Override
    public String getName() {
        return "Slow";
    }
    
    @Override
    public Color getColor() {
        return new Color(173, 216, 230); // 연한 파란색
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape; // 랜덤으로 선택된 모양
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        if (!(boardObj instanceof Board)) return;
        
        Board board = (Board) boardObj;
        // 슬로우 효과 활성화 (GamePanel에서 처리하도록 이벤트 발생)
        board.activateSlowEffect();
    }
    
    @Override
    public String getDescription() {
        return "10초간 게임 속도를 0.5배로 감소시킵니다";
    }
    
    @Override
    public char getSymbol() {
        return 'S'; // 기본 심볼은 S로 설정 (UI 표시용)
    }
    
    /**
     * 특정 블록 인덱스의 심볼을 반환
     * @param blockIndex 블록 인덱스 (0-3)
     * @return 해당 블록의 심볼
     */
    public char getBlockSymbol(int blockIndex) {
        if (blockIndex >= 0 && blockIndex < blockSymbols.length) {
            return blockSymbols[blockIndex];
        }
        return 'S'; // 기본값
    }
}
package com.team.tetris.items;

import java.awt.Color;

/**
 * 아이템 블록의 시각적 표현을 위한 enum
 */
public enum ItemShapeType {
    LINE(new Color(100, 255, 100), 'L'),      // 녹색 라인
    SLOW(new Color(173, 216, 230), 'S');      // 연한 파란색 슬로우
    
    private final Color color;
    private final char symbol;
    
    ItemShapeType(Color color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }
    
    public Color getColor() {
        return color;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    /**
     * 아이템 블록에서 ItemShapeType을 가져오는 헬퍼 메서드
     */
    public static ItemShapeType fromItemBlock(ItemBlock item) {
        return switch (item.getSymbol()) {
            case 'L' -> LINE;
            case 'S', 'O', 'W' -> SLOW; // S, L, O, W는 모두 SLOW 블록
            default -> LINE; // 기본값
        };
    }
}
package com.team.tetris.items;

import java.awt.Color;
import java.util.Random;

import com.team.tetris.core.Board;
import com.team.tetris.core.ShapeType;

/**
 * 변환 아이템 블록 - 현재 조각이 이 아이템일 때 '회전' 키를 누르면 I 블록으로 즉시 변환.
 * 랜덤한 테트로미노 기본형을 가지며, 표시 문자는 'T'.
 * (키 입력으로 발동하므로 activateEffect는 사용하지 않음)
 */
public class TransformBlock implements ItemBlock {

    private static final Random random = new Random();
    private final ShapeType baseShape;

    public TransformBlock() {
        ShapeType[] values = ShapeType.values();
        this.baseShape = values[random.nextInt(values.length)];
    }

    @Override
    public String getName() {
        return "TransformToI";
    }

    @Override
    public Color getColor() {
        // 아이템 블록 렌더용(현재는 하얀색 셀 위에 문자로 표기)
        return new Color(240, 240, 240);
    }

    @Override
    public ShapeType getBaseShape() {
        return baseShape;
    }

    @Override
    public void activateEffect(Object board, int x, int y) {
        // 키 입력으로만 발동. 여기서는 별도 처리 없음.
    }

    @Override
    public String getDescription() {
        return "회전 키를 누르면 현재 조각을 I 블록으로 즉시 변환합니다.";
    }

    @Override
    public char getSymbol() {
        return 'T';
    }

    /** (선택) 첫 블록에만 문자 T를 찍고 나머지는 공백 처리하고 싶을 때 사용 */
    public char getBlockSymbol(int blockIndex) {
        return (blockIndex == 0) ? 'T' : ' ';
    }
}

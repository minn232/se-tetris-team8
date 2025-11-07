package items;

import java.awt.Color;

import core.ShapeType;

/**
 * 아이템 블록의 기본 인터페이스
 * 모든 아이템 블록은 이 인터페이스를 구현해야 함
 */
public interface ItemBlock {
    
    /**
     * 아이템 블록의 이름을 반환
     * @return 아이템 이름
     */
    String getName();
    
    /**
     * 아이템 블록의 색상을 반환
     * @return 아이템 색상
     */
    Color getColor();
    
    /**
     * 아이템 블록의 기본 형태를 반환 (원래 ShapeType의 기능 유지)
     * @return 기본 블록 형태
     */
    ShapeType getBaseShape();
    
    /**
     * 아이템 블록이 배치될 때 실행되는 효과
     * @param board 게임 보드
     * @param x 배치된 x 좌표
     * @param y 배치된 y 좌표
     */
    void activateEffect(Object board, int x, int y);
    
    /**
     * 아이템 블록의 설명을 반환
     * @return 아이템 설명
     */
    String getDescription();
    
    /**
     * 아이템 블록을 나타내는 영문자
     * @return 영문자
     */
    char getSymbol();
}
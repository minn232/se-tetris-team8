package items;

import java.awt.Color;
import java.util.Random;

import core.Board;
import core.ShapeType;

/**
 * 폭탄 아이템 블록 - 3x3 영역의 블록을 파괴
 * 랜덤한 테트로미노 형태를 가지며, 한 블록에 'B' 표시
 */
public class BombBlock implements ItemBlock {
    
    private static final Random random = new Random();
    private final ShapeType baseShape;
    private final int bombIndexInRotation0;
    private int rotation;
    
    public BombBlock() {
        ShapeType[] shapes = ShapeType.values();
        this.baseShape = shapes[random.nextInt(shapes.length)];
        this.rotation = 0;
        this.bombIndexInRotation0 = random.nextInt(baseShape.getOffsets(0).length);
    }
    
    @Override
    public String getName() {
        return "Bomb";
    }
    
    @Override
    public Color getColor() {
        return baseShape.getColor();
    }
    
    @Override
    public ShapeType getBaseShape() {
        return baseShape;
    }
    
    @Override
    public void activateEffect(Object boardObj, int x, int y) {
        if (!(boardObj instanceof Board)) return;
        
        Board board = (Board) boardObj;
        explode(board, x, y);
    }
    
    @Override
    public String getDescription() {
        return "3x3 영역의 블록을 파괴합니다";
    }
    
    @Override
    public char getSymbol() {
        return 'B';
    }
    
    public char getBlockSymbol(int blockIndexInCurrentRotation) {
        // 현재 rotation에서의 인덱스가 폭탄 블록 인덱스와 일치하는지 확인
        return (blockIndexInCurrentRotation == getBombIndex()) ? 'B' : ' ';
    }
    
    private void explode(Board board, int centerX, int centerY) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int targetX = centerX + dx;
                int targetY = centerY + dy;
                
                if (targetX >= 0 && targetX < Board.COLS && 
                    targetY >= 0 && targetY < Board.ROWS) {
                    board.getGrid()[targetY][targetX] = null;
                }
            }
        }
    }
    
    public int getBombIndex() {
        return baseShape.getMappedIndex(rotation, bombIndexInRotation0);
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    
    public int getRotation() {
        return rotation;
    }
}

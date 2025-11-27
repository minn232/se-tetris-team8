package core;

import java.util.Random;

/**
 * 대전 모드에서 상대방으로부터 받은 줄을 관리하는 클래스
 */
public class PendingLines {
    private int count = 0;
    private final Random random = new Random();
    
    /**
     * 받을 줄 추가
     */
    public void addLines(int lines) {
        count += lines;
    }
    
    /**
     * 받을 줄이 있는지 확인
     */
    public boolean hasPendingLines() {
        return count > 0;
    }
    
    /**
     * 현재 대기 중인 줄 개수
     */
    public int getCount() {
        return count;
    }
    
    /**
     * 대기 중인 줄들을 보드에 추가
     * @param grid 보드 그리드
     * @return 추가된 줄 개수
     */
    public int applyToBoard(ShapeType[][] grid) {
        if (count <= 0 || grid == null) return 0;
        
        int linesToAdd = count;
        count = 0;
        
        // 위로 줄들을 밀어올림
        for (int i = 0; i < linesToAdd; i++) {
            // 맨 위 줄이 비어있지 않으면 게임 오버 상황
            for (int x = 0; x < Board.COLS; x++) {
                if (grid[0][x] != null) {
                    // 게임 오버 처리는 Board에서 담당
                    return i; // 지금까지 추가한 줄 수 반환
                }
            }
            
            // 모든 줄을 한 칸씩 위로 이동
            for (int y = 0; y < Board.ROWS - 1; y++) {
                System.arraycopy(grid[y + 1], 0, grid[y], 0, Board.COLS);
            }
            
            // 맨 아래에 새 줄 추가 (한 칸은 비워둠)
            int emptyColumn = random.nextInt(Board.COLS);
            for (int x = 0; x < Board.COLS; x++) {
                if (x == emptyColumn) {
                    grid[Board.ROWS - 1][x] = null;
                } else {
                    // 회색 블록으로 채움
                    grid[Board.ROWS - 1][x] = ShapeType.O; // O 블록 색상 사용
                }
            }
        }
        
        return linesToAdd;
    }
    
    /**
     * 초기화
     */
    public void reset() {
        count = 0;
    }
}

package core;

import java.awt.Color;

public enum ShapeType {
    I(new int[][][]{
            {{0,1},{1,1},{2,1},{3,1}},
            {{2,0},{2,1},{2,2},{2,3}},
            {{0,2},{1,2},{2,2},{3,2}},
            {{1,0},{1,1},{1,2},{1,3}}
    }, new Color(0,255,255), new Color(255,140,0),
       new int[][]{{0,1,2,3}, {0,1,2,3}, {3,2,1,0}, {3,2,1,0}}),  // CYAN -> ORANGE
    O(new int[][][]{
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}}
    }, new Color(255,255,0), new Color(255,255,0),
       new int[][]{{0,1,2,3}, {0,1,2,3}, {0,1,2,3}, {0,1,2,3}}),  // YELLOW -> YELLOW
    T(new int[][][]{
            {{1,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{2,1},{1,2}},
            {{1,0},{0,1},{1,1},{1,2}}
    }, new Color(128,0,128), new Color(213,94,0),
       new int[][]{{0,1,2,3}, {1,2,0,3}, {3,2,1,0}, {3,0,2,1}}),  // MAGENTA -> VERMILION
    S(new int[][][]{
            {{1,0},{2,0},{0,1},{1,1}},
            {{1,0},{1,1},{2,1},{2,2}},
            {{1,1},{2,1},{0,2},{1,2}},
            {{0,0},{0,1},{1,1},{1,2}}
    }, new Color(0,255,0), new Color(0,180,180),
       new int[][]{{0,1,2,3}, {2,3,0,1}, {3,2,1,0}, {1,0,3,2}}),  // GREEN -> BLUISH GREEN
    Z(new int[][][]{
            {{0,0},{1,0},{1,1},{2,1}},
            {{2,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{1,2},{2,2}},
            {{1,0},{0,1},{1,1},{0,2}}
    }, new Color(255,0,0), new Color(204,121,167),
       new int[][]{{0,1,2,3}, {0,2,1,3}, {3,2,1,0}, {3,1,2,0}}),  // RED -> REDDISH PURPLE
    J(new int[][][]{
            {{0,0},{0,1},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{1,2}},
            {{0,1},{1,1},{2,1},{2,2}},
            {{1,0},{1,1},{0,2},{1,2}}
    }, new Color(0,0,255), new Color(135,206,235),
       new int[][]{{0,1,2,3}, {1,3,2,0}, {3,0,1,2}, {2,0,1,3}}),  // BLUE -> SKY BLUE
    L(new int[][][]{
            {{2,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{1,2},{2,2}},
            {{0,1},{1,1},{2,1},{0,2}},
            {{0,0},{1,0},{1,1},{1,2}}
    }, new Color(255,165,0), new Color(0,114,178),
       new int[][]{{0,1,2,3}, {3,0,1,2}, {3,0,1,2}, {0,1,2,3}}),  // ORANGE -> BLUE
    
    // 대전 모드에서 상대방으로부터 받은 줄을 표시하는 회색 블록
    GRAY(new int[][][]{
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}}
    }, new Color(128,128,128), new Color(128,128,128),
       new int[][]{{0,1,2,3}, {0,1,2,3}, {0,1,2,3}, {0,1,2,3}});  // GRAY -> GRAY

    private final int[][][] rotations; // [rot][4][2]
    private final Color normalColor;    // 일반 모드 색상
    private final Color colorBlindColor; // 색맹 모드 색상
    // blockMapping[currentRotation][indexInRotation0] = indexInCurrentRotation
    private final int[][] blockMapping;

    ShapeType(int[][][] rotations, Color normalColor, Color colorBlindColor, int[][] blockMapping) {
        this.rotations = rotations;
        this.normalColor = normalColor;
        this.colorBlindColor = colorBlindColor;
        this.blockMapping = blockMapping;
    }

    public Position[] getOffsets(int rot) {
        int[][] raw = rotations[rot & 3];
        Position[] out = new Position[raw.length];
        for (int i = 0; i < raw.length; i++) out[i] = new Position(raw[i][0], raw[i][1]);
        return out;
    }

    /**
     * 현재 설정에 따라 적절한 색상 반환
     */
    public Color getColor() {
        return Settings.isColorBlind() ? colorBlindColor : normalColor;
    }

    /**
     * 일반 모드 색상 반환
     */
    public Color getNormalColor() {
        return normalColor;
    }

    /**
     * 색맹 모드 색상 반환
     */
    public Color getColorBlindColor() {
        return colorBlindColor;
    }

    /**
     * rotation 0의 블록 인덱스가 특정 rotation에서 어떤 인덱스가 되는지 반환
     * @param rotation 현재 회전 상태 (0~3)
     * @param indexInRotation0 rotation 0에서의 블록 인덱스
     * @return 현재 rotation에서의 블록 인덱스
     */
    public int getMappedIndex(int rotation, int indexInRotation0) {
        return blockMapping[rotation & 3][indexInRotation0];
    }
}

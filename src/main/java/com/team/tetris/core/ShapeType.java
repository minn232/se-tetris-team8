package com.team.tetris.core;

import java.awt.Color;

public enum ShapeType {
    I(new int[][][]{
            {{0,1},{1,1},{2,1},{3,1}},
            {{2,0},{2,1},{2,2},{2,3}},
            {{0,2},{1,2},{2,2},{3,2}},
            {{1,0},{1,1},{1,2},{1,3}}
    }, new Color(0,255,255),
      new int[][]{{0,1,2,3}, {0,1,2,3}, {3,2,1,0}, {3,2,1,0}}),
      // rotation 0의 인덱스 i가 다른 rotation에서 어떤 인덱스가 되는지
    O(new int[][][]{
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}}
    }, new Color(255,255,0),
      new int[][]{{0,1,2,3}, {0,1,2,3}, {0,1,2,3}, {0,1,2,3}}),
    T(new int[][][]{
            {{1,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{2,1},{1,2}},
            {{1,0},{0,1},{1,1},{1,2}}
    }, new Color(128,0,128),
      new int[][]{{0,1,2,3}, {1,2,0,3}, {3,2,1,0}, {3,0,2,1}}),
    S(new int[][][]{
            {{1,0},{2,0},{0,1},{1,1}},
            {{1,0},{1,1},{2,1},{2,2}},
            {{1,1},{2,1},{0,2},{1,2}},
            {{0,0},{0,1},{1,1},{1,2}}
    }, new Color(0,255,0),
      new int[][]{{0,1,2,3}, {2,3,0,1}, {3,2,1,0}, {1,0,3,2}}),
    Z(new int[][][]{
            {{0,0},{1,0},{1,1},{2,1}},
            {{2,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{1,2},{2,2}},
            {{1,0},{0,1},{1,1},{0,2}}
    }, new Color(255,0,0),
      new int[][]{{0,1,2,3}, {0,2,1,3}, {3,2,1,0}, {3,1,2,0}}),
    J(new int[][][]{
            {{0,0},{0,1},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{1,2}},
            {{0,1},{1,1},{2,1},{2,2}},
            {{1,0},{1,1},{0,2},{1,2}}
    }, new Color(0,0,255),
      new int[][]{{0,1,2,3}, {1,3,2,0}, {3,0,1,2}, {2,0,1,3}}),
    L(new int[][][]{
            {{2,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{1,2},{2,2}},
            {{0,1},{1,1},{2,1},{0,2}},
            {{0,0},{1,0},{1,1},{1,2}}
    }, new Color(255,165,0),
      new int[][]{{0,1,2,3}, {3,0,1,2}, {3,0,1,2}, {0,1,2,3}});

    private final int[][][] rotations; // [rot][4][2]
    private final Color color;
    // blockMapping[currentRotation][indexInRotation0] = indexInCurrentRotation
    // rotation 0의 인덱스 i가 다른 rotation에서 어떤 인덱스가 되는지
    private final int[][] blockMapping;

    ShapeType(int[][][] rotations, Color color, int[][] blockMapping) {
        this.rotations = rotations;
        this.color = color;
        this.blockMapping = blockMapping;
    }

    public Position[] getOffsets(int rot) {
        int[][] raw = rotations[rot & 3];
        Position[] out = new Position[raw.length];
        for (int i = 0; i < raw.length; i++) out[i] = new Position(raw[i][0], raw[i][1]);
        return out;
    }

    public Color getColor() { return color; }
    
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

package com.team.tetris.core;

import java.awt.Color;

public enum ShapeType {
    I(new int[][][]{
            {{0,1},{1,1},{2,1},{3,1}},
            {{2,0},{2,1},{2,2},{2,3}},
            {{0,2},{1,2},{2,2},{3,2}},
            {{1,0},{1,1},{1,2},{1,3}}
    }, new Color(0,255,255), new Color(255,140,0)),  // CYAN -> ORANGE
    O(new int[][][]{
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}}
    }, new Color(255,255,0), new Color(255,255,0)),  // YELLOW -> YELLOW
    T(new int[][][]{
            {{1,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{2,1},{1,2}},
            {{1,0},{0,1},{1,1},{1,2}}
    }, new Color(128,0,128), new Color(213,94,0)),  // MAGENTA -> VERMILION
    S(new int[][][]{
            {{1,0},{2,0},{0,1},{1,1}},
            {{1,0},{1,1},{2,1},{2,2}},
            {{1,1},{2,1},{0,2},{1,2}},
            {{0,0},{0,1},{1,1},{1,2}}
    }, new Color(0,255,0), new Color(0,180,180)),  // GREEN -> BLUISH GREEN
    Z(new int[][][]{
            {{0,0},{1,0},{1,1},{2,1}},
            {{2,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{1,2},{2,2}},
            {{1,0},{0,1},{1,1},{0,2}}
    }, new Color(255,0,0), new Color(204,121,167)),  // RED -> REDDISH PURPLE
    J(new int[][][]{
            {{0,0},{0,1},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{1,2}},
            {{0,1},{1,1},{2,1},{2,2}},
            {{1,0},{1,1},{0,2},{1,2}}
    }, new Color(0,0,255), new Color(135,206,235)),  // BLUE -> SKY BLUE
    L(new int[][][]{
            {{2,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{1,2},{2,2}},
            {{0,1},{1,1},{2,1},{0,2}},
            {{0,0},{1,0},{1,1},{1,2}}
    }, new Color(255,165,0), new Color(0,114,178));  // ORANGE -> BLUE

    private final int[][][] rotations; // [rot][4][2]
    private final Color normalColor;    // 일반 모드 색상
    private final Color colorBlindColor; // 색맹 모드 색상

    ShapeType(int[][][] rotations, Color normalColor, Color colorBlindColor) {
        this.rotations = rotations;
        this.normalColor = normalColor;
        this.colorBlindColor = colorBlindColor;
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
}

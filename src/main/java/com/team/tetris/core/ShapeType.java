package com.team.tetris.core;

import java.awt.Color;

public enum ShapeType {
    I(new int[][][]{
            {{0,1},{1,1},{2,1},{3,1}},
            {{2,0},{2,1},{2,2},{2,3}},
            {{0,2},{1,2},{2,2},{3,2}},
            {{1,0},{1,1},{1,2},{1,3}}
    }, new Color(0,255,255)),
    O(new int[][][]{
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{2,1}}
    }, new Color(255,255,0)),
    T(new int[][][]{
            {{1,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{2,1},{1,2}},
            {{1,0},{0,1},{1,1},{1,2}}
    }, new Color(128,0,128)),
    S(new int[][][]{
            {{1,0},{2,0},{0,1},{1,1}},
            {{1,0},{1,1},{2,1},{2,2}},
            {{1,1},{2,1},{0,2},{1,2}},
            {{0,0},{0,1},{1,1},{1,2}}
    }, new Color(0,255,0)),
    Z(new int[][][]{
            {{0,0},{1,0},{1,1},{2,1}},
            {{2,0},{1,1},{2,1},{1,2}},
            {{0,1},{1,1},{1,2},{2,2}},
            {{1,0},{0,1},{1,1},{0,2}}
    }, new Color(255,0,0)),
    J(new int[][][]{
            {{0,0},{0,1},{1,1},{2,1}},
            {{1,0},{2,0},{1,1},{1,2}},
            {{0,1},{1,1},{2,1},{2,2}},
            {{1,0},{1,1},{0,2},{1,2}}
    }, new Color(0,0,255)),
    L(new int[][][]{
            {{2,0},{0,1},{1,1},{2,1}},
            {{1,0},{1,1},{1,2},{2,2}},
            {{0,1},{1,1},{2,1},{0,2}},
            {{0,0},{1,0},{1,1},{1,2}}
    }, new Color(255,165,0));

    private final int[][][] rotations; // [rot][4][2]
    private final Color color;

    ShapeType(int[][][] rotations, Color color) {
        this.rotations = rotations;
        this.color = color;
    }

    public Position[] getOffsets(int rot) {
        int[][] raw = rotations[rot & 3];
        Position[] out = new Position[raw.length];
        for (int i = 0; i < raw.length; i++) out[i] = new Position(raw[i][0], raw[i][1]);
        return out;
    }

    public Color getColor() { return color; }
}

package blocks;

import java.awt.Color;

public class BlockColorPalette {
    // 일반 팔레트
    public static final Color[] NORMAL_COLORS = {
        Color.CYAN,    // I
        Color.BLUE,    // J
        Color.ORANGE,  // L
        Color.YELLOW,  // O
        Color.GREEN,   // S
        Color.MAGENTA, // T
        Color.RED,     // Z
        Color.WHITE    // 아이템 블록(일반)
    };
    // 색맹 팔레트 (CUDO 권장 팔레트 + 흰색)
    public static final Color[] COLORBLIND_COLORS = {
        new Color(255,140,0),       // ORANGE
        new Color(135,206,235),     // SKY BLUE
        new Color(0,180,180),       // BLUISH GREEN
        new Color(255,255,0),       // YELLOW
        new Color(0,114,178),       // BLUE
        new Color(213,94,0),        // VERMILION
        new Color(204,121,167),     // REDDISH PURPLE
        Color.WHITE                 // 아이템 블록(색맹)
    };

    public static Color pickColor(int idx, boolean colorBlind) {
        if (colorBlind) {
            return COLORBLIND_COLORS[idx % COLORBLIND_COLORS.length];
        } else {
            return NORMAL_COLORS[idx % NORMAL_COLORS.length];
        }
    }

    public static Color pickColor(int idx) {
        return pickColor(idx, util.Settings.isColorBlind());
    }
}

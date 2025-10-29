package com.team.tetris.core;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    private static final File SETTINGS_FILE = new File("src/main/data/settings.dat");

    // Key binding enum for better organization
    public enum KeyBinding {
        DOWN("keyDown", KeyEvent.VK_DOWN),
        LEFT("keyLeft", KeyEvent.VK_LEFT),
        RIGHT("keyRight", KeyEvent.VK_RIGHT),
        ROTATE("keyRotate", KeyEvent.VK_UP),
        HARD_DROP("keyHardDrop", KeyEvent.VK_SPACE);

        private final String configKey;
        private final int defaultValue;
        private int currentValue;

        KeyBinding(String configKey, int defaultValue) {
            this.configKey = configKey;
            this.defaultValue = defaultValue;
            this.currentValue = defaultValue;
        }

        public String getConfigKey() { return configKey; }
        public int getDefaultValue() { return defaultValue; }
        public int getValue() { return currentValue; }
        public void setValue(int value) { this.currentValue = value; }
        public void reset() { this.currentValue = defaultValue; }
    }

    // Other settings
    private static boolean colorBlind = false;
    private static String scoreboardFile = "scoreboard.csv";
    private static String resolution = "360x450";

    static {
        load();
    }

    public static void load() {
        if (!SETTINGS_FILE.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SETTINGS_FILE))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> settingsMap = (Map<String, Object>) ois.readObject();
            
            // Load key bindings
            for (KeyBinding kb : KeyBinding.values()) {
                if (settingsMap.containsKey(kb.getConfigKey())) {
                    kb.setValue((Integer) settingsMap.get(kb.getConfigKey()));
                }
            }
            
            // Load other settings
            if (settingsMap.containsKey("colorBlind")) {
                colorBlind = (Boolean) settingsMap.get("colorBlind");
            }
            if (settingsMap.containsKey("scoreboardFile")) {
                scoreboardFile = (String) settingsMap.get("scoreboardFile");
            }
            if (settingsMap.containsKey("resolution")) {
                resolution = (String) settingsMap.get("resolution");
            }
        } catch (IOException | ClassNotFoundException e) {
            // ignore and keep defaults
        }
    }

    public static void save() {
        try {
            // 디렉토리가 없으면 생성
            File dir = SETTINGS_FILE.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            
            // 설정을 Map으로 저장
            Map<String, Object> settingsMap = new HashMap<>();
            
            // Save all key bindings
            for (KeyBinding kb : KeyBinding.values()) {
                settingsMap.put(kb.getConfigKey(), kb.getValue());
            }
            
            // Save other settings
            settingsMap.put("colorBlind", colorBlind);
            settingsMap.put("scoreboardFile", scoreboardFile);
            settingsMap.put("resolution", resolution);
            
            // 직렬화하여 저장
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE))) {
                oos.writeObject(settingsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Key binding getters/setters - delegate to enum
    public static int getKeyDown() { return KeyBinding.DOWN.getValue(); }
    public static void setKeyDown(int k) { KeyBinding.DOWN.setValue(k); }

    public static int getKeyLeft() { return KeyBinding.LEFT.getValue(); }
    public static void setKeyLeft(int k) { KeyBinding.LEFT.setValue(k); }

    public static int getKeyRight() { return KeyBinding.RIGHT.getValue(); }
    public static void setKeyRight(int k) { KeyBinding.RIGHT.setValue(k); }

    public static int getKeyRotate() { return KeyBinding.ROTATE.getValue(); }
    public static void setKeyRotate(int k) { KeyBinding.ROTATE.setValue(k); }

    public static int getKeyHardDrop() { return KeyBinding.HARD_DROP.getValue(); }
    public static void setKeyHardDrop(int k) { KeyBinding.HARD_DROP.setValue(k); }

    // Other settings getters/setters
    public static boolean isColorBlind() { return colorBlind; }
    public static void setColorBlind(boolean b) { colorBlind = b; }

    public static String getScoreboardFile() { return scoreboardFile; }
    public static void setScoreboardFile(String f) { scoreboardFile = f; }

    public static String getResolution() { return resolution; }
    public static void setResolution(String r) { resolution = r; }

    // 해상도에 따른 scale factor (360x450 기준)
    public static double getScaleFactor() {
        return switch (resolution) {
            case "480x600" -> 1.33;
            case "600x750" -> 1.67;
            default -> 1.0;
        };
    }

    // 해상도에 따른 창 크기
    public static int getWindowWidth() {
        return switch (resolution) {
            case "480x600" -> 480;
            case "600x750" -> 600;
            default -> 360;
        };
    }

    public static int getWindowHeight() {
        return switch (resolution) {
            case "480x600" -> 600;
            case "600x750" -> 750;
            default -> 450;
        };
    }

    // 해상도에 따른 기본 폰트 크기
    public static int getBaseFontSize() {
        return switch (resolution) {
            case "480x600" -> 18;
            case "600x750" -> 22;
            default -> 14;
        };
    }

    // 게임 셀 크기
    public static int getCellSize() {
        return switch (resolution) {
            case "480x600" -> 30;
            case "600x750" -> 37;
            default -> 22;
        };
    }
}

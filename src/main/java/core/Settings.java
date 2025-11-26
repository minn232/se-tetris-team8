package core;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        public int getKeyCode() {
            return currentValue;
        }
    }

    // Other settings
    private static boolean colorBlind = false;
    private static String scoreboardFile = "scoreboard.csv";
    private static String resolution = "640x360";
    private static float mainMusicVolume = 1.0f;
    private static float gameMusicVolume = 1.0f;
    
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
            if (settingsMap.containsKey("mainMusicVolume")) {
                mainMusicVolume = (Float) settingsMap.get("mainMusicVolume");
            }
            if (settingsMap.containsKey("gameMusicVolume")) {
                gameMusicVolume = (Float) settingsMap.get("gameMusicVolume");
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
            settingsMap.put("mainMusicVolume", mainMusicVolume);
            settingsMap.put("gameMusicVolume", gameMusicVolume);
            
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

    public static float getMainMusicVolume() { return mainMusicVolume; }
    public static void setMainMusicVolume(float v) { mainMusicVolume = Math.max(0f, Math.min(1f, v)); }

    public static float getGameMusicVolume() { return gameMusicVolume; }
    public static void setGameMusicVolume(float v) { gameMusicVolume = Math.max(0f, Math.min(1f, v)); }

    // 해상도에 따른 scale factor (기본 기준: 640x360 -> 1.0)
    public static double getScaleFactor() {
        try {
            String[] p = resolution.split("x");
            int w = Integer.parseInt(p[0].trim());
            return (double) w / 640.0;
        } catch (Exception e) {
            return 1.0;
        }
    }

    // 해상도에 따른 창 크기 (문자열을 직접 파싱)
    public static int getWindowWidth() {
        try {
            String[] p = resolution.split("x");
            return Integer.parseInt(p[0].trim());
        } catch (Exception e) {
            return 640;
        }
    }

    public static int getWindowHeight() {
        try {
            String[] p = resolution.split("x");
            return Integer.parseInt(p[1].trim());
        } catch (Exception e) {
            return 360;
        }
    }

    // 해상도에 따른 기본 폰트 크기 (height 기준)
    public static int getBaseFontSize() {
        int h = getWindowHeight();
        if (h >= 1080) return 36;
        if (h >= 720)  return 22;
        return 14;
    }

    // 게임 셀 크기 (기본 24 @ 640x360, 비례 확장)
    public static int getCellSize() {
        double scale = getScaleFactor();
        return (int) Math.max(8, Math.round(24 * scale));
    }
     
    /**
     * 모든 설정을 기본값으로 재설정합니다.
     */
    public static void resetToDefaults() {
        // 모든 키 바인딩 초기화
        for (KeyBinding kb : KeyBinding.values()) {
            kb.reset();
        }

        // 기타 설정 초기화
        colorBlind = false;
        scoreboardFile = "scoreboard.csv";
        resolution = "640x360";
        mainMusicVolume = 1.0f;
        gameMusicVolume = 1.0f;
    }
}

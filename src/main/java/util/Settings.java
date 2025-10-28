package util;

import java.awt.event.KeyEvent;
import java.io.*;

public class Settings {
    private static final File SETTINGS_FILE = new File("src/main/data/settings.csv");

    // defaults
    private static int keyDown = KeyEvent.VK_DOWN;
    private static int keyLeft = KeyEvent.VK_LEFT;
    private static int keyRight = KeyEvent.VK_RIGHT;
    private static int keyRotate = KeyEvent.VK_UP;
    private static int keyHardDrop = KeyEvent.VK_SPACE;
    private static boolean colorBlind = false;
    private static String scoreboardFile = "scoreboard.csv";
    private static String resolution = "480x600";

    static {
        load();
    }

    public static void load() {
        if (!SETTINGS_FILE.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "keyDown": keyDown = Integer.parseInt(value); break;
                    case "keyLeft": keyLeft = Integer.parseInt(value); break;
                    case "keyRight": keyRight = Integer.parseInt(value); break;
                    case "keyRotate": keyRotate = Integer.parseInt(value); break;
                    case "keyHardDrop": keyHardDrop = Integer.parseInt(value); break;
                    case "colorBlind": colorBlind = Boolean.parseBoolean(value); break;
                    case "scoreboardFile": scoreboardFile = value; break;
                    case "resolution": resolution = value; break;
                }
            }
        } catch (IOException e) {
            // ignore and keep defaults
        }
    }

    public static void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            bw.write("keyDown," + keyDown + "\n");
            bw.write("keyLeft," + keyLeft + "\n");
            bw.write("keyRight," + keyRight + "\n");
            bw.write("keyRotate," + keyRotate + "\n");
            bw.write("keyHardDrop," + keyHardDrop + "\n");
            bw.write("colorBlind," + colorBlind + "\n");
            bw.write("scoreboardFile," + scoreboardFile + "\n");
            bw.write("resolution," + resolution + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getKeyDown() { return keyDown; }
    public static void setKeyDown(int k) { keyDown = k; }

    public static int getKeyLeft() { return keyLeft; }
    public static void setKeyLeft(int k) { keyLeft = k; }

    public static int getKeyRight() { return keyRight; }
    public static void setKeyRight(int k) { keyRight = k; }

    public static int getKeyRotate() { return keyRotate; }
    public static void setKeyRotate(int k) { keyRotate = k; }

    public static int getKeyHardDrop() { return keyHardDrop; }
    public static void setKeyHardDrop(int k) { keyHardDrop = k; }

    public static boolean isColorBlind() { return colorBlind; }
    public static void setColorBlind(boolean b) { colorBlind = b; }

    public static String getScoreboardFile() { return scoreboardFile; }
    public static void setScoreboardFile(String f) { scoreboardFile = f; }

    public static String getResolution() { return resolution; }
    public static void setResolution(String r) { resolution = r; }

}

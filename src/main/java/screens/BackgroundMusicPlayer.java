package screens;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 배경음악 재생 관리 클래스
 * 싱글톤 패턴으로 구현하여 전역에서 음악 제어 가능
 */
public class BackgroundMusicPlayer {
    
    private static BackgroundMusicPlayer instance;
    private Clip clip;
    private boolean isPlaying = false;
    
    private BackgroundMusicPlayer() {
        // private 생성자로 싱글톤 보장
    }
    
    public static BackgroundMusicPlayer getInstance() {
        if (instance == null) {
            instance = new BackgroundMusicPlayer();
        }
        return instance;
    }
    
    /**
     * 음악 파일을 로드하고 반복 재생 시작
     * @param musicPath 음악 파일 경로 (예: "/music/MainBGM.mp3")
     */
    public void play(String musicPath) {
        play(musicPath, 0.7f); // 기본 볼륨으로 재생
    }
    
    /**
     * 음악 파일을 로드하고 지정된 볼륨으로 반복 재생 시작
     * @param musicPath 음악 파일 경로 (예: "/music/MainBGM.mp3")
     * @param volume 초기 볼륨 (0.0 ~ 1.0)
     */
    public void play(String musicPath, float volume) {
        if (isPlaying) {
            System.out.println("Music already playing, ignoring request");
            return; // 이미 재생 중이면 무시
        }
        
        System.out.println("Attempting to play music: " + musicPath);
        
        try {
            // 기존 클립이 있으면 정리
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
                
                // 음악 파일 로드 - 이미지 로딩과 동일한 방식
                InputStream audioSrc = null;
                
                // 1. getResource() 시도
                String absPath = musicPath.startsWith("/") ? musicPath : "/" + musicPath;
                java.net.URL url = getClass().getResource(absPath);
                if (url != null) {
                    System.out.println("✓ Found via getResource(): " + url);
                    audioSrc = url.openStream();
                }
                
                // 2. ClassLoader.getResourceAsStream() 시도
                if (audioSrc == null) {
                    String loaderPath = musicPath.startsWith("/") ? musicPath.substring(1) : musicPath;
                    audioSrc = getClass().getClassLoader().getResourceAsStream(loaderPath);
                    if (audioSrc != null) {
                        System.out.println("✓ Found via ClassLoader: " + loaderPath);
                    }
                }
                
                // 3. 파일 시스템 직접 접근 시도
                if (audioSrc == null) {
                    String cwd = System.getProperty("user.dir");
                    java.io.File f = new java.io.File(cwd + "/src/main/resources" + absPath);
                    if (f.exists()) {
                        System.out.println("✓ Found via file system: " + f.getAbsolutePath());
                        audioSrc = new java.io.FileInputStream(f);
                    }
                }
                
                if (audioSrc == null) {
                    System.err.println("❌ Music file not found: " + musicPath);
                    System.err.println("Tried:");
                    System.err.println("  1. getResource(" + absPath + ")");
                    System.err.println("  2. ClassLoader.getResourceAsStream(" + (musicPath.startsWith("/") ? musicPath.substring(1) : musicPath) + ")");
                    System.err.println("  3. File: " + System.getProperty("user.dir") + "/src/main/resources" + absPath);
                    return;
                }
                
                System.out.println("✓ Music file found, loading...");
                
                // BufferedInputStream으로 감싸기
                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                
                // WAV 파일 로드
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                
                System.out.println("✓ Audio stream created");
                
                // Clip 생성 및 로드
                clip = AudioSystem.getClip();
                System.out.println("✓ Clip created");
                
                clip.open(audioStream);
                System.out.println("✓ Clip opened");
                
                // 볼륨을 재생 전에 먼저 설정
                setVolume(volume);
                System.out.println("✓ Volume set to: " + volume);
                
                // Clip 이벤트 리스너 추가 (디버깅용)
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        System.out.println("⚠️ Clip stopped! isRunning=" + clip.isRunning());
                        if (!clip.isRunning()) {
                            isPlaying = false;
                        }
                    }
                    if (event.getType() == LineEvent.Type.CLOSE) {
                        System.out.println("⚠️ Clip closed!");
                        isPlaying = false;
                    }
                    if (event.getType() == LineEvent.Type.START) {
                        System.out.println("✓ Clip started playing");
                    }
                });
                
                // 무한 반복 설정
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("✓ Clip looping started");
                
                // 재생 시작
                clip.start();
                isPlaying = true;
                
                System.out.println("✓ Music playback started successfully!");
                System.out.println("✓ Clip is running: " + clip.isRunning());
                System.out.println("✓ Clip is active: " + clip.isActive());
                System.out.println("✓ Clip frame length: " + clip.getFrameLength());
                System.out.println("✓ Clip microsecond length: " + clip.getMicrosecondLength() / 1000000.0 + " seconds");
                
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                System.err.println("❌ Failed to play background music: " + e.getMessage());
                e.printStackTrace();
            }
    }
    
    /**
     * 음악 정지
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            System.out.println("Stopping music...");
            clip.stop();
            clip.close();
            isPlaying = false;
            System.out.println("Music stopped");
        }
    }
    
    /**
     * 음악 일시정지
     */
    public void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            isPlaying = false;
        }
    }
    
    /**
     * 음악 재개
     */
    public void resume() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
            isPlaying = true;
        }
    }
    
    /**
     * 볼륨 조절
     * @param volume 0.0 (무음) ~ 1.0 (최대)
     */
    public void setVolume(float volume) {
        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (Exception e) {
                System.err.println("Failed to set volume: " + e.getMessage());
            }
        }
    }
    
    /**
     * 현재 재생 중인지 확인
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}

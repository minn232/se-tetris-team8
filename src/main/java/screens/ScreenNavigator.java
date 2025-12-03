package screens;

import java.util.Stack;

import javax.swing.JFrame;

/**
 * 화면 네비게이션 히스토리를 관리하는 싱글톤 클래스
 * 뒤로가기 기능을 위해 이전 화면들을 스택으로 관리
 */
public class ScreenNavigator {
    
    private static ScreenNavigator instance;
    private final Stack<ScreenInfo> history;
    
    private ScreenNavigator() {
        history = new Stack<>();
    }
    
    public static ScreenNavigator getInstance() {
        if (instance == null) {
            instance = new ScreenNavigator();
        }
        return instance;
    }
    
    /**
     * 현재 화면을 히스토리에 추가
     * Mainmenu인 경우 히스토리를 초기화하고 추가
     */
    public void push(String screenName, Object... params) {
        // Mainmenu로 가는 경우 히스토리 초기화
        if ("Mainmenu".equals(screenName)) {
            history.clear();
        }
        history.push(new ScreenInfo(screenName, params));
    }
    
    /**
     * 이전 화면으로 돌아가기
     */
    public void goBack(JFrame currentScreen) {
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        
        if (!history.isEmpty()) {
            history.pop(); // 현재 화면 제거
            
            if (!history.isEmpty()) {
                ScreenInfo previous = history.pop(); // 이전 화면도 스택에서 제거 (openScreen에서 다시 push됨)
                openScreen(previous);
            } else {
                // 히스토리가 비어있으면 메인 메뉴로
                new Mainmenu().setVisible(true);
            }
        } else {
            // 히스토리가 없으면 메인 메뉴로
            new Mainmenu().setVisible(true);
        }
    }
    
    /**
     * 히스토리 초기화 (메인 메뉴로 돌아갈 때)
     */
    public void clear() {
        history.clear();
    }
    
    /**
     * 새로운 플로우 시작 (이전 히스토리 무시)
     */
    public void startNewFlow(String screenName, Object... params) {
        history.clear();
        push(screenName, params);
    }
    
    /**
     * ScreenInfo를 기반으로 화면 열기
     */
    private void openScreen(ScreenInfo info) {
        switch (info.screenName) {
            case "Mainmenu" -> new Mainmenu().setVisible(true);
            
            case "ModeSelection" -> {
                boolean isBattle = info.params.length > 0 && (boolean) info.params[0];
                new ModeSelectionScreen(isBattle).setVisible(true);
            }
            
            case "DifficultySelection" -> {
                boolean isItemMode = info.params.length > 0 && (boolean) info.params[0];
                boolean isBattleMode = info.params.length > 1 && (boolean) info.params[1];
                boolean isTimeAttack = info.params.length > 2 && (boolean) info.params[2];
                new DifficultySelectionScreen(isItemMode, isBattleMode, isTimeAttack).setVisible(true);
            }
            
            case "MultiplaySelection" -> new MultiplaySelectionScreen().setVisible(true);
            
            case "HostJoin" -> new HostJoinScreen().setVisible(true);
            
            case "NetworkModeSelection" -> {
                boolean isHost = info.params.length > 0 && (boolean) info.params[0];
                new NetworkModeSelectionScreen(isHost).setVisible(true);
            }
            
            case "Settings" -> new SettingsScreen().setVisible(true);
            
            default -> {
                System.err.println("Unknown screen: " + info.screenName);
                new Mainmenu().setVisible(true);
            }
        }
    }
    
    /**
     * 화면 정보를 저장하는 내부 클래스
     */
    private static class ScreenInfo {
        final String screenName;
        final Object[] params;
        
        ScreenInfo(String screenName, Object... params) {
            this.screenName = screenName;
            this.params = params;
        }
    }
}

package screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Settings;

public class Mainmenu extends JFrame {

    public Mainmenu() {
        // Settings에서 해상도 가져오기
        int width = Settings.getWindowWidth();
        int height = Settings.getWindowHeight();
        int baseFontSize = Settings.getBaseFontSize();
        
        // UIManager를 통해 다이얼로그 폰트 크기 조정
        javax.swing.UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, baseFontSize));
        javax.swing.UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, (int)(baseFontSize * 0.89)));
        
        // 기본 화면(WINDOW) 설정
        setTitle("Tetris");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFocusable(true);
        
        // 메인 패널 설정 (비어있는 패널)
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false); // 투명하게 설정해야 배경이 보임
        mainPanel.setLayout(null); // 절대 위치 지정을 위해 null 레이아웃 사용
        
        // StartGame 버튼 생성
        JButton singleplayButton = addButton(mainPanel, "/images/SinglePlayButton.png", 100, 100, width / 2 - 0, height / 2 - 50);
        JButton multiplayButton = addButton(mainPanel, "/images/MultiPlayButton.png", 100, 100, width / 2 + 100, height / 2 - 50);
        JButton rankingBoardButton = addButton(mainPanel, "/images/RankingboardButton.png", 160, 160, width / 2 + 155, height / 2 - 80);
        JButton howtoplayButton = addButton(mainPanel, "/images/HowtoplayButton.png", 150, 150, width / 2 - 20, height / 2 + 10);
        JButton settingsButton = addButton(mainPanel, "/images/SettingButton.png", 130, 130, width / 2 + 80, height / 2 + 20);
        JButton exitButton = addButton(mainPanel, "/images/ExitButton.png", 130, 130, width / 2 + 170, height / 2 + 20);
        
        // 배경 패널 설정
        BackgroundPanel bg = new BackgroundPanel("images/MainScreen.png");
        bg.setLayout(new BorderLayout());
        bg.add(mainPanel, BorderLayout.CENTER);
        setContentPane(bg);
    }

    /**
     * 버튼을 생성하고 패널에 추가하는 헬퍼 메서드
     * 
     * @param panel 버튼을 추가할 패널
     * @param imagePath 버튼 이미지 경로
     * @param buttonWidth 버튼 가로 크기
     * @param buttonHeight 버튼 세로 크기
     * @param x 버튼 가로 위치
     * @param y 버튼 세로 위치
     * @return 생성된 버튼
     */
    private JButton addButton(JPanel panel, String imagePath, int buttonWidth, int buttonHeight, int x, int y) {
        JButton btn = createImageButton(imagePath, buttonWidth, buttonHeight);
        btn.setBounds(x, y, buttonWidth, buttonHeight);
        panel.add(btn);
        return btn;
    }

    /**
     * 이미지 버튼 생성 헬퍼 메서드 (BackgroundPanel과 동일한 고품질 렌더링)
     */
    private JButton createImageButton(String resourcePath, int width, int height) {
        JButton btn = new JButton() {
            private BufferedImage buttonImage;
            
            {
                // BackgroundPanel과 동일한 이미지 로드 로직
                try {
                    buttonImage = loadImage(resourcePath);
                    if (buttonImage != null) {
                        System.out.println("[Mainmenu] Button image loaded: " + resourcePath 
                            + " (" + buttonImage.getWidth() + "x" + buttonImage.getHeight() + ")");
                    } else {
                        System.err.println("[Mainmenu] Button image not found: " + resourcePath);
                    }
                } catch (Exception e) {
                    buttonImage = null;
                    System.err.println("[Mainmenu] Failed to load button image: " + resourcePath + " -> " + e);
                }
            }
            
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (buttonImage != null) {
                    // BackgroundPanel과 동일한 방식으로 이미지 그리기
                    int w = getWidth();
                    int h = getHeight();
                    g.drawImage(buttonImage, 0, 0, w, h, this);
                } else {
                    // 이미지 로드 실패 시 텍스트 표시
                    g.setColor(java.awt.Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(java.awt.Color.BLACK);
                    g.drawString("Button", 10, getHeight()/2);
                }
            }
        };
        
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        
        return btn;
    }

    /**
     * 이미지 로드 헬퍼 메서드 (클래스패스 + 파일 폴백)
     */
    private BufferedImage loadImage(String resourcePath) throws java.io.IOException {
        String absPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        java.net.URL url = getClass().getResource(absPath);
        if (url != null) {
            return ImageIO.read(url);
        }

        String loaderPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
        java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(loaderPath);
        if (is != null) {
            BufferedImage img = ImageIO.read(is);
            is.close();
            return img;
        }

        String cwd = System.getProperty("user.dir");
        java.io.File f = new java.io.File(cwd + "/src/main/resources" + absPath);
        if (f.exists()) {
            return ImageIO.read(f);
        }

        return null;
    }

    private static class BackgroundPanel extends JPanel {
        private BufferedImage background;

        BackgroundPanel(String resourcePath) {
            try {
                String absPath = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
                java.net.URL url = getClass().getResource(absPath);
                System.out.println("[Mainmenu] getResource('" + absPath + "') -> " + url);
                if (url != null) {
                    background = ImageIO.read(url);
                    System.out.println("[Mainmenu] Loaded from getResource: " + absPath + " (" + background.getWidth() + "x" + background.getHeight() + ")");
                    setOpaque(true);
                    return;
                }

                String loaderPath = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
                java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(loaderPath);
                System.out.println("[Mainmenu] getResourceAsStream('" + loaderPath + "') -> " + (is != null));
                if (is != null) {
                    background = ImageIO.read(is);
                    System.out.println("[Mainmenu] Loaded from ClassLoader: " + loaderPath + " (" + background.getWidth() + "x" + background.getHeight() + ")");
                    is.close();
                    setOpaque(true);
                    return;
                }

                String cwd = System.getProperty("user.dir");
                java.io.File f = new java.io.File(cwd + "/src/main/resources" + absPath);
                System.out.println("[Mainmenu] Trying file fallback: " + f.getAbsolutePath() + " -> exists=" + f.exists());
                if (f.exists()) {
                    background = ImageIO.read(f);
                    System.out.println("[Mainmenu] Loaded from file: " + f.getAbsolutePath() + " (" + background.getWidth() + "x" + background.getHeight() + ")");
                    setOpaque(true);
                    return;
                }

                System.err.println("[Mainmenu] Resource not found: " + resourcePath + " — put file at src/main/resources" + absPath + " (case-sensitive).");
            } catch (java.io.IOException e) {
                background = null;
                System.err.println("[Mainmenu] Failed to load background image: " + resourcePath + " -> " + e);
            } catch (RuntimeException e) {
                background = null;
                System.err.println("[Mainmenu] Runtime error loading background image: " + resourcePath + " -> " + e);
            }
            setOpaque(true);
        }

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                int w = getWidth(), h = getHeight();
                g.drawImage(background, 0, 0, w, h, this);
            } else {
                g.setColor(java.awt.Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Mainmenu menu = new Mainmenu();
            menu.setVisible(true);
        });
    }
}
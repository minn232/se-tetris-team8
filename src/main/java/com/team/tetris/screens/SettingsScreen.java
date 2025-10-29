package com.team.tetris.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.team.tetris.core.Settings;

import javax.swing.JComboBox;

import com.team.tetris.ranking.RankingManager;

public class SettingsScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    // Map to store key binding buttons
    private Map<Settings.KeyBinding, JButton> keyButtons = new HashMap<>();
    private JCheckBox chkColorBlind;
    private JButton btnClearScores;

    private JButton[] buttons;
    private int selectedIndex = 0;

    public SettingsScreen() {
        super("Settings");
        setLayout(new BorderLayout());

        // 해상도에 따라 폰트 크기와 창 크기를 동적으로 적용
        String res = Settings.getResolution();
        int fontSize = 14, width = 360, height = 450;
        if ("480x600".equals(res)) { fontSize = 18; width = 480; height = 600; }
        else if ("600x750".equals(res)) { fontSize = 22; width = 600; height = 750; }
        setSize(width, height);

        // 해상도 패널
        JPanel resPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel resLabel = new JLabel("Window size:");
        resLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        resPanel.add(resLabel);
        String[] resolutions = {"360x450", "480x600", "600x750"};
        JComboBox<String> resCombo = new JComboBox<>(resolutions);
        resCombo.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        resCombo.setSelectedItem(Settings.getResolution());
        resPanel.add(resCombo);

        // 키 매핑 패널
        JPanel keyPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        Font small = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);

        // 각 키 바인딩에 대해 라벨과 버튼 생성
        addKeyBindingRow(keyPanel, "Down:", Settings.KeyBinding.DOWN, small);
        addKeyBindingRow(keyPanel, "Left:", Settings.KeyBinding.LEFT, small);
        addKeyBindingRow(keyPanel, "Right:", Settings.KeyBinding.RIGHT, small);
        addKeyBindingRow(keyPanel, "Rotate:", Settings.KeyBinding.ROTATE, small);
        addKeyBindingRow(keyPanel, "Hard Drop:", Settings.KeyBinding.HARD_DROP, small);

        // Colorblind 및 점수판 패널 (넓은 레이아웃)
        JPanel bottom = new JPanel(new GridLayout(1, 2, 40, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkColorBlind = new JCheckBox("Color-blind mode");
        chkColorBlind.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        chkColorBlind.setSelected(Settings.isColorBlind());
        leftPanel.add(chkColorBlind);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClearScores = new JButton("Clear Scoreboard");
        btnClearScores.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        rightPanel.add(btnClearScores);

        bottom.add(leftPanel);
        bottom.add(rightPanel);

        // 버튼 패널
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
        btnSave.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        btnCancel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        south.add(btnCancel);
        south.add(btnSave);

        JPanel mainSouthPanel = new JPanel(new BorderLayout());
        mainSouthPanel.add(bottom, BorderLayout.CENTER);
        mainSouthPanel.add(south, BorderLayout.SOUTH);

        add(resPanel, BorderLayout.NORTH);
        add(keyPanel, BorderLayout.CENTER);
        add(mainSouthPanel, BorderLayout.SOUTH); // 수정된 부분

        pack();
        setLocationRelativeTo(null);

        // 키 변경 핸들러 - 리팩토링된 버전
        setupKeyBindingListeners();

        btnClearScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(SettingsScreen.this,
                        "Are you sure you want to clear the scoreboard? This cannot be undone.", "Clear Scoreboard",
                        JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    try {
                        // normal_rankings.dat 초기화
                        RankingManager normalRankings = RankingManager.getInstance("normal_rankings.dat");
                        normalRankings.clearRankings();
                        
                        // item_rankings.dat 초기화 (있다면)
                        // RankingManager itemRankings = RankingManager.getInstance("item_rankings.dat");
                        // itemRankings.clearRankings();
                        
                        JOptionPane.showMessageDialog(SettingsScreen.this, "Scoreboard cleared.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SettingsScreen.this, "Failed to clear scoreboard: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.setResolution((String)resCombo.getSelectedItem());
                Settings.setColorBlind(chkColorBlind.isSelected());
                Settings.save();
                JOptionPane.showMessageDialog(SettingsScreen.this, "Settings saved.");
                dispose();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        chkColorBlind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Settings.setColorBlind(chkColorBlind.isSelected());
                // TODO: Board 색상 즉시 반영 로직 필요시 구현
            }
        });

        // 키보드 포커스를 받을 수 있도록 설정
        setFocusable(true);

        // 버튼 배열 초기화
        buttons = new JButton[]{btnSave, btnCancel, btnClearScores};

        // 키보드 이벤트 리스너 추가
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // 버튼들이 포커스를 받지 않도록 설정
        for (JButton button : buttons) {
            button.setFocusable(false);
        }

        // 초기 하이라이트 설정
        updateButtonHighlight();
    }

    /**
     * 키 바인딩 행을 추가하는 헬퍼 메서드
     */
    private void addKeyBindingRow(JPanel panel, String labelText, Settings.KeyBinding binding, Font font) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(font);
        lbl.setForeground(Color.BLACK);
        
        JButton btn = new JButton(KeyEvent.getKeyText(binding.getValue()));
        btn.setFont(font);
        
        panel.add(lbl);
        panel.add(btn);
        
        keyButtons.put(binding, btn);
    }

    /**
     * 모든 키 바인딩 버튼에 리스너 설정
     */
    private void setupKeyBindingListeners() {
        for (Map.Entry<Settings.KeyBinding, JButton> entry : keyButtons.entrySet()) {
            Settings.KeyBinding binding = entry.getKey();
            JButton button = entry.getValue();
            
            button.addActionListener(e -> {
                // Settings에서 해상도 정보 가져오기
                int baseFontSize = Settings.getBaseFontSize();
                double scaleFactor = Settings.getScaleFactor();
                
                JDialog dlg = new JDialog(SettingsScreen.this, "Press a key", true);
                JLabel msg = new JLabel("Press the desired key now", SwingConstants.CENTER);
                msg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, baseFontSize));
                dlg.add(msg);
                dlg.setSize((int)(300 * scaleFactor), (int)(100 * scaleFactor));
                dlg.setLocationRelativeTo(SettingsScreen.this);
                dlg.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent ke) {
                        int code = ke.getKeyCode();
                        binding.setValue(code);
                        button.setText(KeyEvent.getKeyText(code));
                        dlg.dispose();
                    }
                });
                dlg.setFocusable(true);
                dlg.setVisible(true);
            });
        }
    }

    // 키보드 입력 처리
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + buttons.length) % buttons.length;
                updateButtonHighlight();
                break;
            case KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % buttons.length;
                updateButtonHighlight();
                break;
            case KeyEvent.VK_ENTER:
                buttons[selectedIndex].doClick();
                break;
            case KeyEvent.VK_ESCAPE:
                dispose();
                break;
        }
    }

    // 버튼 하이라이트 업데이트
    private void updateButtonHighlight() {
        for (int i = 0; i < buttons.length; i++) {
            if (i == selectedIndex) {
                buttons[i].setBackground(new Color(100, 150, 255));
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(true);
            } else {
                buttons[i].setBackground(null);
                buttons[i].setForeground(Color.BLACK);
                buttons[i].setOpaque(false);
            }
        }
        repaint();
    }
}

package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import util.Settings;

import javax.swing.JComboBox;

public class SettingsScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton btnChangeDown, btnChangeUp, btnChangeLeft, btnChangeRight, btnChangeRotate;
    private JCheckBox chkColorBlind;
    private JButton btnClearScores;

    public SettingsScreen() {
        super("Settings");
        setLayout(new BorderLayout());

        // 해상도에 따라 폰트 크기와 창 크기를 동적으로 적용
        String res = Settings.getResolution();
        int fontSize = 18, width = 480, height = 600;
        if ("640x800".equals(res)) { fontSize = 24; width = 640; height = 800; }
        else if ("800x1000".equals(res)) { fontSize = 32; width = 800; height = 1000; }
        setSize(width, height);

        // 해상도 패널
        JPanel resPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resPanel.setBackground(Color.DARK_GRAY);
        JLabel resLabel = new JLabel("Window size:");
        resLabel.setForeground(Color.WHITE);
        resLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        resPanel.add(resLabel);
        String[] resolutions = {"480x600", "640x800", "800x1000"};
        JComboBox<String> resCombo = new JComboBox<>(resolutions);
        resCombo.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
        resCombo.setSelectedItem(Settings.getResolution());
        resPanel.add(resCombo);

        // 키 매핑 패널
        JPanel keyPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        keyPanel.setBackground(Color.BLACK);
        Font small = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);

        JLabel lblDown = new JLabel("Down:");
        lblDown.setFont(small);
        lblDown.setForeground(Color.WHITE);
        btnChangeDown = new JButton(KeyEvent.getKeyText(Settings.getKeyDown()));
        btnChangeDown.setFont(small);
        keyPanel.add(lblDown);
        keyPanel.add(btnChangeDown);

        JLabel lblLeft = new JLabel("Left:");
        lblLeft.setFont(small);
        lblLeft.setForeground(Color.WHITE);
        btnChangeLeft = new JButton(KeyEvent.getKeyText(Settings.getKeyLeft()));
        btnChangeLeft.setFont(small);
        keyPanel.add(lblLeft);
        keyPanel.add(btnChangeLeft);

        JLabel lblRight = new JLabel("Right:");
        lblRight.setFont(small);
        lblRight.setForeground(Color.WHITE);
        btnChangeRight = new JButton(KeyEvent.getKeyText(Settings.getKeyRight()));
        btnChangeRight.setFont(small);
        keyPanel.add(lblRight);
        keyPanel.add(btnChangeRight);

        JLabel lblUp = new JLabel("Up:");
        lblUp.setFont(small);
        lblUp.setForeground(Color.WHITE);
        btnChangeUp = new JButton(KeyEvent.getKeyText(Settings.getKeyUp()));
        btnChangeUp.setFont(small);
        keyPanel.add(lblUp);
        keyPanel.add(btnChangeUp);

        JLabel lblRotate = new JLabel("Rotate:");
        lblRotate.setFont(small);
        lblRotate.setForeground(Color.WHITE);
        btnChangeRotate = new JButton(KeyEvent.getKeyText(Settings.getKeyRotate()));
        btnChangeRotate.setFont(small);
        keyPanel.add(lblRotate);
        keyPanel.add(btnChangeRotate);

        // Colorblind 및 점수판 패널 (넓은 레이아웃)
        JPanel bottom = new JPanel(new GridLayout(1, 2, 40, 0));
        bottom.setBackground(Color.DARK_GRAY);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.DARK_GRAY);
        chkColorBlind = new JCheckBox("Color-blind mode");
        chkColorBlind.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        chkColorBlind.setForeground(Color.BLACK);
        chkColorBlind.setSelected(Settings.isColorBlind());
        leftPanel.add(chkColorBlind);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.DARK_GRAY);
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


        // 키 변경 핸들러: 다음 키 입력을 캡처하는 작은 대화상자 열기
        ActionListener changeKeyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton src = (JButton) e.getSource();
                JDialog dlg = new JDialog(SettingsScreen.this, "Press a key", true);
                JLabel msg = new JLabel("Press the desired key now", SwingConstants.CENTER);
                msg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
                dlg.add(msg);
                dlg.setSize(300, 100);
                dlg.setLocationRelativeTo(SettingsScreen.this);
                dlg.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent ke) {
                        int code = ke.getKeyCode();
                        if (src == btnChangeDown) {
                            Settings.setKeyDown(code);
                            btnChangeDown.setText("Down: " + KeyEvent.getKeyText(code));
                        } else if (src == btnChangeUp) {
                            Settings.setKeyUp(code);
                            btnChangeUp.setText("Up: " + KeyEvent.getKeyText(code));
                        } else if (src == btnChangeLeft) {
                            Settings.setKeyLeft(code);
                            btnChangeLeft.setText("Left: " + KeyEvent.getKeyText(code));
                        } else if (src == btnChangeRight) {
                            Settings.setKeyRight(code);
                            btnChangeRight.setText("Right: " + KeyEvent.getKeyText(code));
                        } else if (src == btnChangeRotate) {
                            Settings.setKeyRotate(code);
                            btnChangeRotate.setText("Rotate: " + KeyEvent.getKeyText(code));
                        }
                        dlg.dispose();
                    }
                });
                dlg.setFocusable(true);
                dlg.setVisible(true);
            }
        };

        btnChangeDown.addActionListener(changeKeyListener);
        btnChangeUp.addActionListener(changeKeyListener);
        btnChangeLeft.addActionListener(changeKeyListener);
        btnChangeRight.addActionListener(changeKeyListener);
        btnChangeRotate.addActionListener(changeKeyListener);

        btnClearScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ok = JOptionPane.showConfirmDialog(SettingsScreen.this,
                        "Are you sure you want to clear the scoreboard? This cannot be undone.", "Clear Scoreboard",
                        JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    File f = new File(Settings.getScoreboardFile());
                    try (FileWriter fw = new FileWriter(f, false)) {
                        fw.write("[]");
                        JOptionPane.showMessageDialog(SettingsScreen.this, "Scoreboard cleared.");
                    } catch (IOException ex) {
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
                // Board가 열려 있으면 색상 즉시 반영
                for (java.awt.Window w : java.awt.Window.getWindows()) {
                    if (w instanceof component.Board && w.isVisible()) {
                        ((component.Board)w).drawBoard();
                    }
                }
            }
        });

    }

}

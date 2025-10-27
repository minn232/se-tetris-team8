package com.team.tetris.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.team.tetris.core.Board;
import com.team.tetris.core.Difficulty;
import com.team.tetris.render.GamePanel;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("SE Tetris Team8");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Difficulty diff = askDifficulty();
        Board board = new Board(diff);
        GamePanel panel = new GamePanel(board);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        SwingUtilities.invokeLater(panel::requestFocusInWindow);
    }

    private Difficulty askDifficulty() {
        Object[] options = {"EASY", "NORMAL", "HARD"};
        int sel = JOptionPane.showOptionDialog(this, "Select Difficulty", "Difficulty",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (sel == 0) return Difficulty.EASY;
        if (sel == 2) return Difficulty.HARD;
        return Difficulty.NORMAL;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}

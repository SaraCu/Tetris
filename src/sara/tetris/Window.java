package sara.tetris;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private JLabel scoreLabel;

    public Window() {

        scoreLabel = new JLabel(" 0");
        add(scoreLabel, BorderLayout.NORTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        var game = new Window();
        game.setVisible(true);
    }

    JLabel getScoreLabel() {

        return scoreLabel;
    }
}

package sara.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 22;
    private static final int INTERVAL = 300;

    private Timer timer;
    private boolean isFallingFinished = false;
    private int score = 0;
    private int currentX = 0;
    private int currentY = 0;
    private JLabel scoreLabel;
    private Shape currentShape;
    private ShapeType[] board;

    private Color[] colors = {
            new Color(0, 0, 0),
            new Color(255, 175, 64),
            new Color(164, 255, 148),
            new Color(102, 102, 204),
            new Color(255, 253, 148),
            new Color(240, 43, 194),
            new Color(181, 230, 232),
            new Color(187, 102, 227)
    };

    public Board(Window parent) {

        setFocusable(true);
        scoreLabel = parent.getScoreLabel();
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(new Color(6, 2, 61));
        scoreLabel.setForeground(Color.yellow);
        addKeyListener(new BoardKeyAdapter());

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        setBackground(new Color(6, 2, 61));
        draw(g);
    }

    protected void start() {

        currentShape = new Shape();
        board = new ShapeType[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newShape();

        timer = new Timer(INTERVAL, new GameCycle());
        timer.start();
    }

    private int getSquareWidth() {

        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int getSquareHeight() {

        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private ShapeType getShapeTypeAt(int x, int y) {

        return board[(y * BOARD_WIDTH) + x];
    }

    private void draw(Graphics g) {

        var size = getSize();
        int top = (int) size.getHeight() - BOARD_HEIGHT * getSquareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {

            for (int j = 0; j < BOARD_WIDTH; j++) {

                ShapeType shape = getShapeTypeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != ShapeType.NoShape) {

                    drawSquare(g, j * getSquareWidth(),
                            top + i * getSquareHeight(), shape);
                }
            }
        }

        if (currentShape.getShape() != ShapeType.NoShape) {

            for (int i = 0; i < 4; i++) {

                int x = currentX + currentShape.getX(i);
                int y = currentY - currentShape.getY(i);

                drawSquare(g, x * getSquareWidth(),
                        top + (BOARD_HEIGHT - y - 1) * getSquareHeight(),
                        currentShape.getShape());
            }
        }
    }

    private void dropDown() {

        int newY = currentY;

        while (newY > 0) {

            if (!tryMoveShape(currentShape, currentX, newY - 1)) {

                break;
            }

            newY--;
        }

        shapeDropped();
    }

    private void move() {

        if (!tryMoveShape(currentShape, currentX, currentY - 1)) {

            shapeDropped();
        }
    }

    private void clearBoard() {

        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {

            board[i] = ShapeType.NoShape;
        }
    }

    private void shapeDropped() {

        for (int i = 0; i < 4; i++) {

            int x = currentX + currentShape.getX(i);
            int y = currentY - currentShape.getY(i);
            board[(y * BOARD_WIDTH) + x] = currentShape.getShape();
        }

        clearLines();

        if (!isFallingFinished) {

            newShape();
        }
    }

    private void newShape() {

        currentShape.setRandomShape();
        currentX = BOARD_WIDTH / 2 + 1;
        currentY = BOARD_HEIGHT - 1 + currentShape.minY();

        if (!tryMoveShape(currentShape, currentX, currentY)) {

            currentShape.setShape(ShapeType.NoShape);
            timer.stop();

            var msg = String.format("Game over. Score: %d", score);
            scoreLabel.setText(msg);
        }
    }

    private boolean tryMoveShape(Shape newShape, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newShape.getX(i);
            int y = newY - newShape.getY(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {

                return false;
            }

            if (getShapeTypeAt(x, y) != ShapeType.NoShape) {

                return false;
            }
        }

        currentShape = newShape;
        currentX = newX;
        currentY = newY;

        repaint();

        return true;
    }

    private void clearLines() {

        int linesToClear = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {

            boolean clear = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {

                if (getShapeTypeAt(j, i) == ShapeType.NoShape) {

                    clear = false;
                    break;
                }
            }

            if (clear) {

                linesToClear++;

                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = getShapeTypeAt(j, k + 1);
                    }
                }
            }
        }

        if (linesToClear > 0) {

            score += linesToClear * 10;

            scoreLabel.setText(String.valueOf(score));
            isFallingFinished = true;
            currentShape.setShape(ShapeType.NoShape);
        }
    }

    private void drawSquare(Graphics g, int x, int y, ShapeType shape) {

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, getSquareWidth() - 2, getSquareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + getSquareHeight() - 1, x, y);
        g.drawLine(x, y, x + getSquareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + getSquareHeight() - 1,
                x + getSquareWidth() - 1, y + getSquareHeight() - 1);
        g.drawLine(x + getSquareWidth() - 1, y + getSquareHeight() - 1,
                x + getSquareWidth() - 1, y + 1);
    }

    private void doGameCycle() {

        update();
        repaint();
    }

    private void update() {

        if (isFallingFinished) {

            isFallingFinished = false;
            newShape();
        } else {

            move();
        }
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    class BoardKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (currentShape.getShape() == ShapeType.NoShape) {

                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    tryMoveShape(currentShape, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMoveShape(currentShape, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    dropDown();
                    break;
                case KeyEvent.VK_UP:
                    tryMoveShape(currentShape.rotate(), currentX, currentY);
                    break;
            }
        }
    }
}

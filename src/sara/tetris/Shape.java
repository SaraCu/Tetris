package sara.tetris;

import java.util.Random;

public class Shape {

    private ShapeType shapeType;
    private int[][] coordinates = new int[4][2];
    private int[][][] shapeCoordinates = new int[][][]{
            {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
    };

    public Shape() {

        setShape(ShapeType.NoShape);
    }

    private void setX(int index, int x) {
        coordinates[index][0] = x;
    }

    private void setY(int index, int y) {
        coordinates[index][1] = y;
    }

    public int getX(int index) {
        return coordinates[index][0];
    }

    public int getY(int index) {
        return coordinates[index][1];
    }

    public ShapeType getShape() {
        return shapeType;
    }

    protected void setShape(ShapeType shape) {

        for (int i = 0; i < 4; i++) {

            System.arraycopy(shapeCoordinates[shape.ordinal()][i], 0, coordinates[i], 0, 2);
        }

        shapeType = shape;
    }

    public void setRandomShape() {

        var r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;

        ShapeType[] values = ShapeType.values();
        setShape(values[x]);
    }

    public int minY() {

        int m = coordinates[0][1];

        for (int i = 0; i < 4; i++) {

            m = Math.min(m, coordinates[i][1]);
        }

        return m;
    }

    public Shape rotate() {

        if (shapeType == ShapeType.OShape) {

            return this;
        }

        var result = new Shape();
        result.shapeType = shapeType;

        for (int i = 0; i < 4; ++i) {

            result.setX(i, -getY(i));
            result.setY(i, getX(i));
        }

        return result;
    }
}


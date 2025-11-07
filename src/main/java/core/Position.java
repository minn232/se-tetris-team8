package core;

public class Position {
    public int x; // 열(가로)
    public int y; // 행(세로)

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position add(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
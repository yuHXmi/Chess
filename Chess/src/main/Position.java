package main;

public class Position {
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean equals(Position other) {
        return row == other.row && col == other.col;
    }
}

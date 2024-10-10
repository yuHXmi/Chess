package main;

public class Move {
    public Position start;
    public Position end;

    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public boolean equals(Move other) {
        return start.equals(other.start) && end.equals(other.end);
    }
}

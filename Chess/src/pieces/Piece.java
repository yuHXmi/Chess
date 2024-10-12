package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

    Board board;

    public String color;
    public int type;
    public Position position;
    public boolean moved = false;
    public List<Move> moves = new ArrayList<>();

    public Piece(Board board, String color, Position position) {

        this.board = board;
        this.color = color;
        this.position = position;
        setDefaultValue();
    }

    void setDefaultValue() {}

    public boolean canAttackSquare(int row, int col) {
        for (Move move : moves) {
            if (move.end.row == row && move.end.col == col) {
                return true;
            }
        }

        return false;
    }

    public void changePosition(Position position) {
        this.position = new Position(position.row, position.col);
        moved = true;
    }

    boolean isEmptySquare(int row, int col) {
        return board.pieces[row][col] == null;
    }

    boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    boolean isValidAttack(int row, int col) {
        return isInsideBoard(row, col) && (isEmptySquare(row, col) || !board.pieces[row][col].color.equals(color));
    }

    public abstract List<Move> addMoves();

    public void updateMoves() {
        moves = addMoves();
    }

    public int getID() {
        return position.row * 8 + position.col;
    }
}

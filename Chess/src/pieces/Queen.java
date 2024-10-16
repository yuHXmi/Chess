package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        type = 4;
    }

    @Override
    public List<Move> addMoves() {

        Rook rook = new Rook(board, color, position);
        Bishop bishop = new Bishop(board, color, position);
        List<Move> moves = new ArrayList<>();
        moves.addAll(rook.addMoves());
        moves.addAll(bishop.addMoves());

        return moves;
    }
}

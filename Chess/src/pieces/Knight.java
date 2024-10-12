package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        type = 1;
    }

    public List<Move> addMoves() {

        List<Move> moves = new ArrayList<>();
        int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        int row = position.row;
        int col = position.col;
        for (int i = 0; i < 8; i++) {

            int newRow = row + dx[i];
            int newCol = col + dy[i];
            if (isValidAttack(newRow, newCol)) {
                moves.add(new Move(position, new Position(newRow, newCol)));
            }
        }

        return moves;
    }
}

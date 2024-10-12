package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        type = 2;
    }

    public List<Move> addMoves() {

        List<Move> moves = new ArrayList<>();
        int[] dx = {1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1};

        int row = position.row;
        int col = position.col;
        for (int j = 0; j < 4; j++) {
            for (int i = 1; i < 8; i++) {

                int newRow = row + i * dx[j];
                int newCol = col + i * dy[j];

                if (isValidAttack(newRow, newCol)) {
                    moves.add(new Move(position, new Position(newRow, newCol)));
                }

                if (!isInsideBoard(newRow, newCol) || !isEmptySquare(newRow, newCol))
                    break;
            }
        }

        return moves;
    }
}

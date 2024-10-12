package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        type = 3;
    }

    public List<Move> addMoves() {

        List<Move> moves = new ArrayList<>();
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

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

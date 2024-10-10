package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        name = "king";
        type = 5;
        value = 30000;
    }

    public boolean checkCastle(Piece rook) {

        if (rook == null || moved || rook.moved)
            return false;

        int diffCol = rook.position.col - position.col;
        int dy = Integer.compare(diffCol, 0);

        for (int i = 0; i <= 2; i++) {
            if (isAttackedSquare(position.row, position.col + dy * i)) {
                return false;
            }
        }

        for (int i = 1; i < Math.abs(diffCol); i++) {
            if (!isEmptySquare(position.row, position.col + dy * i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canAttackSquare(int row, int col) {

        if (position.row == row && position.col == col)
            return false;

        int diffRow = Math.abs(position.row - row);
        int diffCol = Math.abs(position.col - col);

        return diffRow <= 1 && diffCol <= 1;
    }

    boolean isValidAttack(int row, int col) {
        return super.isValidAttack(row, col) && isSafeSquare(row, col);
    }

    boolean isAttackedSquare(int row, int col) {

        for (Piece piece : board.pieceList) {
            if (piece.color != color && piece.canAttackSquare(row, col)) {
                return true;
            }
        }

        return false;
    }

    boolean isSafeSquare(int row, int col) {

        if (!super.isValidAttack(row, col))
            return false;

        for (Piece piece : board.pieceList) {
            if (piece.color != color && piece.canAttackSquare(row, col)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<Move> addMoves() {

        List<Move> moves = new ArrayList<Move>();
        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        int row = position.row;
        int col = position.col;
        for (int i = 0; i < 8; i++) {

            int newRow = row + dx[i];
            int newCol = col + dy[i];

            if (isSafeSquare(newRow, newCol)) {
                moves.add(new Move(position, new Position(newRow, newCol)));
            }
        }

        if (checkCastle(board.pieces[row][0])) {
            moves.add(new Move(position, new Position(row, 0)));
        }

        if (checkCastle(board.pieces[row][7])) {
            moves.add(new Move(position, new Position(row, 7)));
        }

        return moves;
    }
}

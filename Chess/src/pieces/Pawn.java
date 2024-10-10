package pieces;

import board.Board;
import main.Move;
import main.Position;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Pawn extends Piece {

    public Pawn(Board board, String color, Position position) {
        super(board, color, position);
    }

    void setDefaultValue() {
        name = "pawn";
        type = 0;
        value = 100;
    }

    @Override
    public boolean canAttackSquare(int row, int col) {

        if (position.row == row && position.col == col)
            return false;

        int diffRow = position.row - row;
        int diffCol = position.col - col;

        return Math.abs(diffCol) == 1 && ((color.equals("black") && diffRow == -1) || (color.equals("white") && diffRow == 1));
    }

    boolean isValidAttack(int row, int col) {
        return super.isValidAttack(row, col) && !isEmptySquare(row, col);
    }

    boolean isValidMove(int row, int col) {
        return isInsideBoard(row, col) && isEmptySquare(row, col);
    }

    public List<Move> addMoves() {

        int d;
        if (color.equals("black")) {
            d = 1;
        } else {
            d = -1;
        }
        List<Move> moves = new ArrayList<Move>();

        int row = position.row;
        int col = position.col;

        if (isValidAttack(row + d, col - 1)) {
            moves.add(new Move(position, new Position(row + d, col - 1)));
        }

        if (isValidAttack(row + d, col + 1)) {
            moves.add(new Move(position, new Position(row + d, col + 1)));
        }

        if (board.lastMove != null) {

            Piece lastMovePiece = board.pieces[board.lastMove.end.row][board.lastMove.end.col];
            if (lastMovePiece instanceof Pawn) {

                int epRow = lastMovePiece.position.row;
                int epCol = lastMovePiece.position.col;
                if (abs(epRow - board.lastMove.start.row) == 2 && row == epRow && abs(col - epCol) == 1) {
                    moves.add(new Move(position, new Position(row + d, epCol)));
                }
            }
        }

        if (isValidMove(row + d, col)) {
            moves.add(new Move(position, new Position(row + d, col)));

            if (!moved && isValidMove(row + d * 2, col)) {
                moves.add(new Move(position, new Position(row + d * 2, col)));
            }
        }

        return moves;
    }

    public List<Move> addAttack() {
        int d;
        if (color.equals("black")) {
            d = 1;
        } else {
            d = -1;
        }
        List<Move> moves = new ArrayList<Move>();

        int row = position.row;
        int col = position.col;

        if (super.isValidAttack(row + d, col - 1)) {
            moves.add(new Move(position, new Position(row + d, col - 1)));
        }

        if (super.isValidAttack(row + d, col + 1)) {
            moves.add(new Move(position, new Position(row + d, col + 1)));
        }

        return moves;
    }
}

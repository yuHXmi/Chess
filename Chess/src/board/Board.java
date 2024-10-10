package board;

import main.Move;
import main.Position;
import pieces.*;

import java.util.ArrayList;
import java.util.List;

public class Board {

    public String playerTurn;

    public final int PAWN = 0;
    public final int KNIGHT = 1;
    public final int BISHOP = 2;
    public final int ROOK = 3;
    public final int QUEEN = 4;
    public final int KING = 5;
    public final int EMPTY = 6;
    public final int BLACK = 7;
    public final int WHITE = 8;

    final int[] standard_piece = {
        ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK,
        PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN,
        ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK
    };

    final int[] standard_color = {
        BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
        BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK, BLACK,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE,
        WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE
    };

    public Piece[][] pieces = new Piece[8][8];
    public List<Piece> pieceList = new ArrayList<>();
    public Move lastMove = null;
    public Piece[] promotion;

    public Board(String playerTurn) {

        this.playerTurn = playerTurn;

        setBoard();
    }

    public Board copyBoard() {

        Board newBoard = new Board(playerTurn);
        newBoard.pieceList = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != null) {
                    Piece newPiece = copyPiece(newBoard, pieces[row][col]);
                    newBoard.pieces[row][col]  = newPiece;
                    newBoard.pieceList.add(newPiece);
                } else {
                    newBoard.pieces[row][col] = null;
                }
            }
        }

        if (lastMove != null) {
            newBoard.lastMove = copyMove(lastMove);
        }

        return newBoard;
    }

    Move copyMove(Move other) {
        Position start = new Position(other.start.row, other.start.col);
        Position end = new Position(other.end.row, other.end.col);
        return new Move(start, end);
    }

    Piece copyPiece(Board board, Piece piece) {

        Piece newPiece = null;
        String color = piece.color;
        Position position = new Position(piece.position.row, piece.position.col);

        switch (piece.type) {
            case PAWN:
                newPiece = new Pawn(board, color, position);
                break;
            case KNIGHT:
                newPiece = new Knight(board, color, position);
                break;
            case BISHOP:
                newPiece = new Bishop(board, color, position);
                break;
            case ROOK:
                newPiece = new Rook(board, color, position);
                break;
            case QUEEN:
                newPiece = new Queen(board, color, position);
                break;
            case KING:
                newPiece = new King(board, color, position);
                break;
        }

        newPiece.moves = new ArrayList<>();
        for (Move move : piece.moves) {
            newPiece.moves.add(copyMove(move));
        }

        newPiece.moved = piece.moved;

        return newPiece;
    }

    public void updatePiecesMoves() {

        List<Piece> kingList = new ArrayList<>();

        for (Piece piece : pieceList) {
            if (piece instanceof King) {
                kingList.add(piece);
                continue;
            }
            piece.updateMoves();
        }

        for (Piece piece : kingList) {
            piece.updateMoves();
        }
    }

    public void makeMove(Move move) {

        if (move == null) {
            return;
        }

        int oldRow = move.start.row;
        int oldCol = move.start.col;
        int newRow = move.end.row;
        int newCol = move.end.col;

        if (pieces[oldRow][oldCol] instanceof King && Math.abs(oldCol - newCol) > 2) {
            castle(pieces[oldRow][oldCol], pieces[newRow][newCol]);
        } else
        if (pieces[oldRow][oldCol] instanceof Pawn && oldCol != newCol && pieces[newRow][newCol] == null) {
            enPassant(move);
        } else {

            pieceList.remove(pieces[newRow][newCol]);
            pieceList.remove(pieces[oldRow][oldCol]);
            pieces[newRow][newCol] = pieces[oldRow][oldCol];
            pieces[oldRow][oldCol] = null;
            pieces[newRow][newCol].changePosition(move.end);
            pieceList.add(pieces[newRow][newCol]);
            lastMove = move;
        }

        updatePiecesMoves();
    }

    public boolean checkPromotion() {
        if (lastMove == null)
            return false;

        int row = lastMove.end.row;
        int col = lastMove.end.col;

        return pieces[row][col] instanceof Pawn && (row == 0 || row == 7);
    }

    public void castle(Piece king, Piece rook) {
        int diffCol = rook.position.col - king.position.col;
        int dy = Integer.compare(diffCol, 0);
        makeMove(new Move(rook.position, new Position(king.position.row, king.position.col + dy)));
        makeMove(new Move(king.position, new Position(king.position.row, king.position.col + dy * 2)));
    }

    public void enPassant(Move move) {
        Position position = new Position(move.start.row, move.end.col);
        makeMove(new Move(move.start, position));
        makeMove(new Move(position, move.end));
    }

    public void setPromotion(Piece pawn) {

        promotion = new Piece[4];
        Position position = new Position(pawn.position.row, pawn.position.col);

        promotion[0] = new Queen(this, pawn.color, position);
        promotion[1] = new Rook(this, pawn.color, position);
        promotion[2] = new Bishop(this, pawn.color, position);
        promotion[3] = new Knight(this, pawn.color, position);
    }

    void setBoard() {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                int id = row * 8 + col;

                String color;
                if (standard_color[id] == BLACK) {
                    color = "black";
                } else {
                    color = "white";
                }

                Position position = new Position(row, col);
                Piece newPiece = null;
                switch (standard_piece[id]) {
                    case PAWN:
                        newPiece = new Pawn(this, color, position);
                        break;
                    case KNIGHT:
                        newPiece = new Knight(this, color, position);
                        break;
                    case BISHOP:
                        newPiece = new Bishop(this, color, position);
                        break;
                    case ROOK:
                        newPiece = new Rook(this, color, position);
                        break;
                    case QUEEN:
                        newPiece = new Queen(this, color, position);
                        break;
                    case KING:
                        newPiece = new King(this, color, position);
                        break;
                    case EMPTY:
                        break;
                }

                pieces[row][col] = newPiece;
                if (newPiece != null) {
                    pieceList.add(newPiece);
                }
            }
        }
    }

    public void doPromotion(int type) {

        int row = lastMove.end.row;
        int col = lastMove.end.col;

        pieceList.remove(pieces[row][col]);
        pieces[row][col] = promotion[type];
        pieceList.add(pieces[row][col]);
    }

    Piece findKing(String kingColor) {

        for (Piece piece : pieceList) {
            if (piece instanceof King && piece.color == kingColor) {
                return piece;
            }
        }

        return null;
    }

    public boolean kingIsAttacked(String kingColor) {

        Piece king = findKing(kingColor);

        if (king == null)
            return false;

        for (Piece piece : pieceList) {
            if (piece.color != kingColor && piece.canAttackSquare(king.position.row, king.position.col)) {
                return true;
            }
        }

        return false;
    }

    boolean haveLegalMove(String color) {

        for (Piece piece : pieceList) {
            if (piece.color == color) {

                if (piece.moves == null) {
                    continue;
                }

                for (Move move : piece.moves) {
                    Board newBoard = this.copyBoard();
                    newBoard.makeMove(move);

                    if (!newBoard.kingIsAttacked(color)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isCheckMate(String color) {
        return kingIsAttacked(color) && !haveLegalMove(color);
    }

    public boolean isStaleMate(String color) {
        return !kingIsAttacked(color) && !haveLegalMove(color);
    }
}

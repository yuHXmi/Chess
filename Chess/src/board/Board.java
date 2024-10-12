package board;

import main.Move;
import main.Position;
import pieces.*;

import java.util.ArrayList;
import java.util.List;

public class Board {

    public String playerTurn;

    final int PAWN = 0;
    final int KNIGHT = 1;
    final int BISHOP = 2;
    final int ROOK = 3;
    final int QUEEN = 4;
    public final int KING = 5;

    public Piece[][] pieces = new Piece[8][8];
    public List<Piece> pieceList = new ArrayList<>();
    public Move lastMove = null;
    public Piece[] promotion;

    public Board(String playerTurn) {

        this.playerTurn = playerTurn;
    }

//    public List<Move> generateTacticalMoves(String currentTurn) {
//
//        List<Move> moves = new ArrayList<>();
//        for (Piece piece : pieceList) {
//
//            if (piece.color != currentTurn) {
//                continue;
//            }
//
//            for (Move move : piece.moves) {
//                if (isTakePiece(move)) {
//                    moves.add(move);
//                }
//            }
//        }
//
//        return moves;
//    }

    public String getPositionKey() {
        StringBuilder key = new StringBuilder();

        // Lưu trữ trạng thái của các quân cờ
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (pieces[row][col] != null) {
                    key.append(pieces[row][col].type)
                            .append(pieces[row][col].color.charAt(0)) // 'b' for black, 'w' for white
                            .append(row)
                            .append(col);
                    if (pieces[row][col] instanceof King || pieces[row][col] instanceof Rook) {
                        key.append(pieces[row][col].moved);
                    }
                }
            }
        }

        // Lưu trữ trạng thái lượt chơi
        key.append(playerTurn);

        // Lưu trữ nước đi cuối cùng (nếu có)
        if (lastMove != null) {
            key.append(lastMove.start.row)
                    .append(lastMove.start.col)
                    .append(lastMove.end.row)
                    .append(lastMove.end.col);
        }

        return key.toString();
    }



    public Board copyBoard() {

        Board newBoard = new Board(playerTurn);

        for (Piece piece : pieceList) {
            Piece clonePiece = copyPiece(newBoard, piece);
            newBoard.pieces[piece.position.row][piece.position.col] = clonePiece;
            newBoard.pieceList.add(clonePiece);
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

        if (isCastle(move)) {
            castle(pieces[oldRow][oldCol], pieces[newRow][newCol]);
        } else
        if (isEnpassant(move)) {
            enPassant(move);
        } else {

            pieceList.remove(pieces[newRow][newCol]);
            pieceList.remove(pieces[oldRow][oldCol]);
            pieces[newRow][newCol] = copyPiece(this, pieces[oldRow][oldCol]);
            pieces[oldRow][oldCol] = null;
            pieces[newRow][newCol].changePosition(move.end);
            pieceList.add(pieces[newRow][newCol]);
            lastMove = copyMove(move);
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

    public boolean isTakePiece(Move move) {
        return pieces[move.end.row][move.end.col] != null || isEnpassant(move);
    }

    public boolean isCastle(Move move) {
        int oldRow = move.start.row;
        int oldCol = move.start.col;
        int newCol = move.end.col;

        return pieces[oldRow][oldCol] instanceof King && Math.abs(oldCol - newCol) > 2;
    }

    public boolean isEnpassant(Move move) {
        int oldRow = move.start.row;
        int oldCol = move.start.col;
        int newRow = move.end.row;
        int newCol = move.end.col;

        return pieces[oldRow][oldCol] instanceof Pawn && oldCol != newCol && pieces[newRow][newCol] == null;
    }

    public void enPassant(Move move) {
        Position position = new Position(move.start.row, move.end.col);
        makeMove(new Move(position, move.end));
        makeMove(new Move(move.start, move.end));
    }

    public void setPromotion(Piece pawn) {

        promotion = new Piece[4];
        Position position = new Position(pawn.position.row, pawn.position.col);

        promotion[0] = new Queen(this, pawn.color, position);
        promotion[1] = new Rook(this, pawn.color, position);
        promotion[2] = new Bishop(this, pawn.color, position);
        promotion[3] = new Knight(this, pawn.color, position);
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

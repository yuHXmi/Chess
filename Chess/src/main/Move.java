package main;

import board.Board;
import pieces.Piece;

import java.util.ArrayList;

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

    public static Move parseMove(String moveStr, Board board, String currentTurn) {
        Position end;
        ArrayList<Piece> possiblePieces = new ArrayList<>();
        int pieceType;

        moveStr = moveStr.replace("x", "").replace("+", "").replace("#", "");
        
        int start_row = -1;
        int start_col = -1;

        if (moveStr.equals("O-O")) {
            start_row = currentTurn.equals("white") ? 7 : 0;
            end = new Position(start_row, 7);
            pieceType = 5;
        } else
        if (moveStr.equals("O-O-O")) {
            start_row = currentTurn.equals("white") ? 7 : 0;
            end = new Position(start_row, 0);
            pieceType = 5;
        } else {
            if (Character.isLowerCase(moveStr.charAt(0))) {
                // Ví dụ: "e4"
                end = switch (moveStr.length()) {
                    case 2 -> new Position(8 - (moveStr.charAt(1) - '0'), moveStr.charAt(0) - 'a');
                    case 3 -> {
                        start_col = moveStr.charAt(0) - 'a';
                        yield new Position(8 - (moveStr.charAt(2) - '0'), moveStr.charAt(1) - 'a');
                    }
                    default -> null;
                };

                pieceType = 0; // Quân tốt (pawn)
            } else {

                char pieceChar = moveStr.charAt(0);
                end = switch (moveStr.length()) {
                    case 3 -> new Position(8 - (moveStr.charAt(2) - '0'), moveStr.charAt(1) - 'a');
                    case 4 -> {
                        if (Character.isDigit(moveStr.charAt(1))) {
                            start_row = 8 - (moveStr.charAt(1) - '0');
                        } else {
                            start_col = moveStr.charAt(1) - 'a';
                        }
                        yield new Position(8 - (moveStr.charAt(3) - '0'), moveStr.charAt(2) - 'a');
                    }
                    default -> null;
                };

                pieceType = switch (pieceChar) {
                    case 'N' -> 1; // Knight
                    case 'B' -> 2; // Bishop
                    case 'R' -> 3; // Rook
                    case 'Q' -> 4; // Queen
                    case 'K' -> 5; // King
                    default -> throw new IllegalArgumentException("Invalid piece type: " + pieceChar);
                };
            }
        }

        for (Piece piece : board.pieceList) {
            if ((start_row == -1 || start_row == piece.position.row) && (start_col == -1 || start_col == piece.position.col)) {
                if (piece.type == pieceType && piece.color.equals(currentTurn)) {
                    for (Move move : piece.moves) {
                        if (move.end.equals(end)) {
                            possiblePieces.add(piece);
                        }
                    }
                }
            }
        }

        if (possiblePieces.isEmpty()) {
            throw new IllegalArgumentException("No valid piece found for move: " + moveStr);
        }

        Piece movingPiece = possiblePieces.getFirst();
        return new Move(movingPiece.position, end);
    }

}

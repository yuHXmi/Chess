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
        Position end = null;
        ArrayList<Piece> possiblePieces = new ArrayList<>();
        int pieceType;

        // Loại bỏ dấu x (ăn quân) hoặc dấu + (chiếu tướng) khỏi nước đi
        moveStr = moveStr.replace("x", "").replace("+", "").replace("#", "");
        
        int start_row = -1;
        int start_col = -1;

        // Xử lý nước đi nếu là quân tốt (không có ký tự đại diện)
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
                switch (moveStr.length()) {
                    case 2:
                        end = new Position(8 - (moveStr.charAt(1) - '0'), moveStr.charAt(0) - 'a');
                        break;
                    case 3:
                        start_col = moveStr.charAt(0) - 'a';
                        end = new Position(8 - (moveStr.charAt(2) - '0'), moveStr.charAt(1) - 'a');
                        break;
                }

                pieceType = 0; // Quân tốt (pawn)
            } else {
                // Xác định loại quân từ ký tự đầu tiên
                char pieceChar = moveStr.charAt(0);
                switch (moveStr.length()) {
                    case 3:
                        end = new Position(8 - (moveStr.charAt(2) - '0'), moveStr.charAt(1) - 'a');
                        break;
                    case 4:
                        if (Character.isDigit(moveStr.charAt(1))) {
                            start_row = 8 - (moveStr.charAt(1) - '0');
                        } else {
                            start_col =  moveStr.charAt(1) - 'a';
                        }
                        end = new Position(8 - (moveStr.charAt(3) - '0'), moveStr.charAt(2) - 'a');
                        break;
                }

                switch (pieceChar) {
                    case 'N': pieceType = 1; break; // Knight
                    case 'B': pieceType = 2; break; // Bishop
                    case 'R': pieceType = 3; break; // Rook
                    case 'Q': pieceType = 4; break; // Queen
                    case 'K': pieceType = 5; break; // King
                    default:
                        throw new IllegalArgumentException("Invalid piece type: " + pieceChar);
                }
            }
        }
        // Tìm quân cờ có thể di chuyển đến vị trí đích
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

        Piece movingPiece = possiblePieces.get(0);
        return new Move(movingPiece.position, end);
    }

}

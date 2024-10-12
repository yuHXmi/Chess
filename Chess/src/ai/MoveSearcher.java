package ai;

import board.Board;
import main.GamePanel;
import main.Move;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Map;

public class MoveSearcher {

    static GamePanel gp;

    static Map<String, ArrayList<String>> openingMoves;

    final int maxDepth = 4;
    final int midGamePieceThreshold = 12;

    Board board;
    String aiTurn;
    BoardEvaluator midGameEvaluate = new BoardEvaluator(0);
    BoardEvaluator endGameEvaluate = new BoardEvaluator(1);

    public static void addGamePanel(GamePanel gamePanel) {
        gp = gamePanel;
    }

    public static void addOpeningMoves(Map<String, ArrayList<String>> opening) {
        openingMoves = opening;
    }

    public MoveSearcher(Board board) {
        this.board = board;
        aiTurn = changeTurn(board.playerTurn);
    }

    String changeTurn(String currentTurn) {
        return currentTurn == "black" ? "white" : "black";
    }

    BoardValue search(int depth, Board board, String currentTurn, int alpha, int beta) {

        String boardPosition = board.getPositionKey();  //tạo ra một key cho vị trí bàn cờ hiện tại
        if (openingMoves.containsKey(boardPosition)) {
            // Nếu vị trí bàn cờ khớp với một khai cuộc, thực hiện nước đi khai cuộc
            ArrayList<String> moves = openingMoves.get(boardPosition);
            String turn = "white";
            Board newBoard = new Board(board.playerTurn);
            gp.setBoard(newBoard);

            System.out.println("**********");
            for (int i = 0; i < Math.min(gp.countMove, moves.size()) ; i++) {
                String nextMove = moves.get(i);
                System.out.println(nextMove);
                Move parsedMove = Move.parseMove(nextMove, newBoard, turn); // Chuyển string nước đi thành đối tượng Move
                newBoard.makeMove(parsedMove);
                turn = changeTurn(turn);
            }
            return new BoardValue(newBoard, 0);  // Trả về giá trị của bàn cờ sau khi thực hiện khai cuộc
        }

        if (depth == 0) {
            if (board.pieceList.size() > midGamePieceThreshold) {
                return new BoardValue(board, midGameEvaluate.evaluate(board));
            }
            return new BoardValue(board, endGameEvaluate.evaluate(board));
        }

        BoardValue nextBoard = new BoardValue(null, 0);
        if (currentTurn == board.playerTurn) {
            nextBoard.value = Integer.MIN_VALUE + depth;
        } else {
            nextBoard.value = Integer.MAX_VALUE - depth;
        }

        for (Piece piece : board.pieceList) {
            if (piece.color != currentTurn) {
                continue;
            }

            for (Move move : piece.moves) {

                Board newBoard = board.copyBoard();
                newBoard.makeMove(move);

                if (newBoard.kingIsAttacked(currentTurn)) {
                    continue;
                }

                if (newBoard.checkPromotion()) {

                    Piece lastMovePiece = newBoard.pieces[newBoard.lastMove.end.row][newBoard.lastMove.end.col];
                    for (int i = 0; i < 4; i++) {

                        Board cloneBoard = newBoard.copyBoard();
                        cloneBoard.setPromotion(lastMovePiece);
                        cloneBoard.doPromotion(i);

                        BoardValue currentBoard = new BoardValue(cloneBoard, search(depth - 1, cloneBoard, changeTurn(currentTurn), alpha, beta).value);

                        if (currentTurn == aiTurn) {
                            nextBoard.minimize(currentBoard);
                            beta = Math.min(beta, currentBoard.value);
                        } else {
                            nextBoard.maximize(currentBoard);
                            alpha = Math.max(alpha, currentBoard.value);
                        }

                        if (alpha >= beta) {
                            break;
                        }
                    }
                } else {

                    BoardValue currentBoard = new BoardValue(newBoard, search(depth - 1, newBoard, changeTurn(currentTurn), alpha, beta).value);

                    if (currentTurn == aiTurn) {
                        nextBoard.minimize(currentBoard);
                        beta = Math.min(beta, currentBoard.value);
                    } else {
                        nextBoard.maximize(currentBoard);
                        alpha = Math.max(alpha, currentBoard.value);
                    }
                }

                if (alpha >= beta) {
                    break;
                }
            }
        }

        if (nextBoard.board == null && !board.kingIsAttacked(currentTurn)) {
            nextBoard.value = 0;
        }

        return nextBoard;
    }

//    private BoardValue quiescenceSearch(Board board, String currentTurn, int alpha, int beta) {
//
//        if (board.isStaleMate(currentTurn)) {
//            return new BoardValue(board, 0);
//        }
//
//        if (board.isCheckMate(currentTurn)) {
//            if (currentTurn == aiTurn) {
//                return new BoardValue(board, Integer.MAX_VALUE);
//            }
//            return new BoardValue(board, Integer.MIN_VALUE);
//        }
//
//        int standPat;
//        if (board.pieceList.size() > midGamePieceThreshold) {
//            standPat = midGameEvaluate.evaluate(board, currentTurn);
//        } else {
//            standPat = endGameEvaluate.evaluate(board, currentTurn);
//        }
//
//        if (standPat >= beta) return new BoardValue(board, beta);
//        if (alpha < standPat) alpha = standPat;
//
//        for (Move move : board.generateTacticalMoves(currentTurn)) {
//            Board newBoard = board.copyBoard();
//            newBoard.makeMove(move);
//
//            if (newBoard.kingIsAttacked(currentTurn)) continue;
//
//            int score = -quiescenceSearch(newBoard, changeTurn(currentTurn), -beta, -alpha).value;
//
//            if (score >= beta) return new BoardValue(newBoard, beta);
//            if (score > alpha) alpha = score;
//        }
//        return new BoardValue(board, alpha);
//    }

    public Board getBoard() {
        BoardValue boardValue = search(maxDepth, board, aiTurn, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.print("Move value: ");
        System.out.println(boardValue.value);
        return boardValue.board;
    }
}

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

    BoardValue opening(String boardPosition) {

        ArrayList<String> moves = openingMoves.get(boardPosition);
        String turn = "white";
        Board newBoard = new Board(board.playerTurn);
        gp.setBoard(newBoard);

        boolean check = false;
        System.out.println("**********");
        for (String move : moves) {
            if (check) {
                break;
            }
            if (newBoard.getPositionKey().equals(boardPosition)) {
                check = true;
            }
            System.out.println(move);
            Move parsedMove = Move.parseMove(move, newBoard, turn); // Chuyển string nước đi thành đối tượng Move
            newBoard.makeMove(parsedMove);
            turn = changeTurn(turn);
        }

        return new BoardValue(newBoard, 0);  // Trả về giá trị của bàn cờ sau khi thực hiện khai cuộc
    }

    BoardValue findNextMove() {

        String boardPosition = board.getPositionKey();  //tạo ra một key cho vị trí bàn cờ hiện tại
        if (openingMoves.containsKey(boardPosition)) {
            // Nếu vị trí bàn cờ khớp với một khai cuộc, thực hiện nước đi khai cuộc
            return opening(boardPosition);
        }

        return search(maxDepth, board, aiTurn, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    BoardValue search(int depth, Board board, String currentTurn, int alpha, int beta) {

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

                    int value;
                    String boardPosition = board.getPositionKey();
                    int count = gp.countBoardRepeat.getOrDefault(boardPosition, 0);
                    if (count == 2) {
                        value = 0;
                    } else {
                        gp.countBoardRepeat.put(boardPosition, count + 1);
                        value = search(depth - 1, newBoard, changeTurn(currentTurn), alpha, beta).value;
                        gp.countBoardRepeat.put(boardPosition, count);
                    }

                    BoardValue currentBoard = new BoardValue(newBoard, value);

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

    public Board getBoard() {
        BoardValue boardValue = findNextMove();
        System.out.print("Move value: ");
        System.out.println(boardValue.value);
        return boardValue.board;
    }
}

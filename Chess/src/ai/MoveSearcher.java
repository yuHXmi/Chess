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
    static Move[][] killer = new Move[256][2];

    static final int maxDepth = 6;
    static final int midGamePieceThreshold = 12;

    static final int[][] MVV_LVA = {
        {15, 14, 13, 12, 11, 10},  // victim P, attacker P, N, B, R, K, Q
        {25, 24, 23, 22, 21, 20}, // victim N, attacker P, N, B, R, K, Q
        {35, 34, 33, 32, 31, 30}, // victim B, attacker P, N, B, R, K, Q
        {45, 44, 43, 42, 41, 40}, // victim R, attacker P, N, B, R, K, Q
        {55, 54, 53, 52, 51, 50}, // victim Q, attacker P, N, B, R, K, Q
        {0, 0, 0, 0, 0, 0},    // victim K, attacker P, N, B, R, K, Q
    };

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
        return currentTurn.equals("black") ? "white" : "black";
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
            Move parsedMove = Move.parseMove(move, newBoard, turn);
            newBoard.makeMove(parsedMove);
            turn = changeTurn(turn);
        }

        return new BoardValue(newBoard, 0);
    }

    BoardValue findNextMove() {

        String boardPosition = board.getPositionKey();
        if (openingMoves.containsKey(boardPosition)) {
            return opening(boardPosition);
        }

        return search(maxDepth, board, aiTurn, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    void sortMove(ArrayList<MoveValue> moves, int i) {
        for (int j = i + 1; j < moves.size(); j++) {
            if (moves.get(j).value > moves.get(i).value) {
                MoveValue tmp = new MoveValue(board.copyMove(moves.get(i).move), moves.get(i).value);
                moves.set(i, new MoveValue(board.copyMove(moves.get(j).move), moves.get(j).value));
                moves.set(j, tmp);
            }
        }
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
            nextBoard.value = Integer.MIN_VALUE + 10 - depth;
        } else {
            nextBoard.value = Integer.MAX_VALUE - 10 + depth;
        }
        int rootDepth = gp.moveCount + maxDepth - depth;

        ArrayList<MoveValue> moves = new ArrayList<>();
        for (Piece piece : board.pieceList) {
            if (piece.color.equals(currentTurn)) {
                for (Move move : piece.moves) {

                    int row = move.end.row;
                    int col = move.end.col;
                    int value = 0;
                    if (board.pieces[row][col] != null) {
                        value = MVV_LVA[board.pieces[row][col].type][piece.type];
                    } else {
                        for (int i = 0; i < 2; i++) {
                            if (killer[rootDepth][i] != null && move.equals(killer[rootDepth][i])) {
                                value = 5 - i;
                                break;
                            }
                        }
                    }
                    moves.add(new MoveValue(move, value));
                }
            }
        }
        for (int i = 0; i < moves.size(); i++) {
            sortMove(moves, i);
            Move move = moves.get(i).move;
            Board newBoard = board.copyBoard();
            newBoard.makeMove(move);

            if (newBoard.kingIsAttacked(currentTurn)) {
                continue;
            }

            if (newBoard.checkPromotion()) {

                Piece lastMovePiece = newBoard.pieces[newBoard.lastMove.end.row][newBoard.lastMove.end.col];
                for (int j = 0; j < 4; j++) {

                    Board cloneBoard = newBoard.copyBoard();
                    cloneBoard.setPromotion(lastMovePiece);
                    cloneBoard.doPromotion(j);

                    BoardValue currentBoard = new BoardValue(cloneBoard, search(depth - 1, cloneBoard, changeTurn(currentTurn), alpha, beta).value);

                    if (currentTurn.equals(aiTurn)) {
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

                if (currentTurn.equals(aiTurn)) {
                    nextBoard.minimize(currentBoard);
                    beta = Math.min(beta, currentBoard.value);
                } else {
                    nextBoard.maximize(currentBoard);
                    alpha = Math.max(alpha, currentBoard.value);
                }
            }

            if (alpha >= beta) {
                int row = move.end.row;
                int col = move.end.col;
                if (board.pieces[row][col] == null && (killer[rootDepth][1] == null || !killer[rootDepth][1].equals(move))) {
                    killer[rootDepth][1] = board.copyMove(killer[rootDepth][0]);
                    killer[rootDepth][0] = board.copyMove(move);
                }
                break;
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

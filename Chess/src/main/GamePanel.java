package main;

import ai.MoveSearcher;
import board.Board;
import pieces.*;
import ui.UI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    public final int tileSize = 76;
    public final int pieceSize = 58;
    public final int maxScreenCol = 8;
    public final int maxScreenRow = 8;
    public final int screenWidth = maxScreenCol * tileSize;
    public final int screenHeight = maxScreenRow * tileSize;

    // FPS
    public final int FPS = 120;
    final double drawInterval = 1e9 / FPS;
    double delta = 0;

    // SYSTEM
    Thread gameThread;
    public MouseHandler mouseH = new MouseHandler(this);
    boolean changeTurnDelay = false;

    // GAME STATE
    final int SELECT_SIDE = 0;
    final int PLAYING = 1;
    final int END_GAME = 2;

    int gameState;


    // BOARD & PIECES
    public Board board;
    public Piece pickedPiece;
    public Map<String, Integer> countBoardRepeat = new HashMap<>();
    public int moveCount = 0;

    // PLAYER
    public String playerTurn;
    public String currentTurn = "white";

    // UI
    UI ui;

    // OPENING
    Map<String, ArrayList<String>> openingMoves;

    // CHESS
    public final int PAWN = 0;
    public final int KNIGHT = 1;
    public final int BISHOP = 2;
    public final int ROOK = 3;
    public final int QUEEN = 4;
    public final int KING = 5;
    public final int EMPTY = 6;

    public final int WHITE = 0;
    public final int BLACK = 1;

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

    public GamePanel() {

        setUpGame();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);
        this.setFocusable(true);
    }

    public void setUpGame() {
        gameState = SELECT_SIDE;
        ui = new UI(this);
        setUpBoard();
    }

    public void setUpBoard() {
        board = new Board(playerTurn);
        setBoard(board);
        countBoardRepeat.put(board.getPositionKey2(), 1);
        PGNReader.addGamePanel(this);
        openingMoves = PGNReader.readPGN("res/openings.pgn");
        MoveSearcher.addOpeningMoves(openingMoves);
        MoveSearcher.addGamePanel(this);
    }

    public void launchGame() {

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        long lastPrint = System.nanoTime();
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            if (currentTime - lastPrint >= 1000000000) {
                lastPrint = currentTime;
            }
        }
    }

    public void setBoard(Board board) {

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
                    case EMPTY:
                        break;
                }

                if (newPiece != null) {
                    board.pieceList.add(newPiece);
                    board.pieces[row][col] = newPiece;
                }
            }
        }

        board.updatePiecesMoves();
    }

    void getPromotion() {

        int row = board.lastMove.end.row;
        int col = board.lastMove.end.col;

        int d;
        if (playerTurn.equals("white")) {
            d = 1;
        } else {
            d = -1;
        }

        int pressRow = mouseH.pressedY / tileSize;
        int pressCol = mouseH.pressedX / tileSize;

        board.setPromotion(board.pieces[row][col]);

        for (int i = 0; i < 4; i++) {

            if (mouseH.pressedX > -1 && row + d * i == pressRow && col == pressCol) {

                board.doPromotion(i);

                mouseH.pressedX = -1;
                mouseH.pressedY = -1;
            }
        }
    }

    Move movePiece() {

        if (mouseH.releaseX > -1 && pickedPiece != null) {

            int oldRow = pickedPiece.position.row;
            int oldCol = pickedPiece.position.col;
            int newRow = mouseH.releaseY / tileSize;
            int newCol = mouseH.releaseX / tileSize;

            if (pickedPiece instanceof King) {

                if (((King) pickedPiece).checkCastle(board.pieces[oldRow][0])) {
                    for (int i = 0; i <= oldCol - 2; i++) {
                        if (oldRow == newRow && newCol == i) {
                            return new Move(pickedPiece.position, new Position(oldRow, 0));
                        }
                    }
                }

                if (((King) pickedPiece).checkCastle(board.pieces[oldRow][7])) {
                    for (int i = oldCol + 2; i <= 7; i++) {
                        if (oldRow == newRow && newCol == i) {
                            return new Move(pickedPiece.position, new Position(oldRow, 7));
                        }
                    }
                }
            }

            Move currentMove = new Move(pickedPiece.position, new Position(newRow, newCol));
            List<Move> moves = pickedPiece.moves;
            for (Move move : moves) {
                if (move.equals(currentMove)) {
                    return move;
                }
            }

            mouseH.pressedX = -1;
            mouseH.pressedY = -1;
        }

        return null;
    }

    void pickPiece() {

        pickedPiece = null;
        if (mouseH.pressedX > -1) {

            int row = mouseH.pressedY / tileSize;
            int col = mouseH.pressedX / tileSize;

            int x = col * tileSize + (tileSize - pieceSize) / 2;
            int y = row * tileSize + (tileSize - pieceSize) / 2;
            Rectangle hitBox = new Rectangle(x, y, pieceSize, pieceSize);

            if (board.pieces[row][col] != null && board.pieces[row][col].color.equals(playerTurn) && hitBox.contains(mouseH.pressedX, mouseH.pressedY)) {
                pickedPiece = board.pieces[row][col];
            }
        }
    }

    Move getPlayerMove() {

        // Move pieces
        Move nextMove = movePiece();
        mouseH.releaseX = -1;
        mouseH.releaseY = -1;
        if (nextMove != null) {
            return nextMove;
        }

        // Pick piece
        pickPiece();

        return null;
    }

    public void changeTurn() {
        moveCount++;
        changeTurnDelay = false;
        pickedPiece = null;
        currentTurn = currentTurn.equals("black") ? "white" : "black";
        if (currentTurn.equals(playerTurn)) {
            fixPlayerLegalMoves();
        }

        String boardPosition = board.getPositionKey2();
        int count = countBoardRepeat.getOrDefault(boardPosition, 0);
        countBoardRepeat.put(boardPosition, count + 1);
    }

    void fixPlayerLegalMoves() {
        for (Piece piece : board.pieceList) {
            if (piece.color.equals(playerTurn)) {

                ArrayList<Move> legalMoves = new ArrayList<>();
                for (Move move : piece.moves) {

                    Board cloneBoard = board.copyBoard();
                    cloneBoard.makeMove(move);
                    if (!cloneBoard.kingIsAttacked(playerTurn)) {
                        legalMoves.add(move);
                    }
                }

                piece.moves = legalMoves;
            }
        }
    }

    void gamePlay() {
        gameState = PLAYING;
        setUpBoard();
    }

    void selectSide() {
        Rectangle blackRec = new Rectangle(229, 182, 150, 100);
        Rectangle whiteRec = new Rectangle(229, 326, 150, 100);

        if (mouseH.pressedX != -1) {
            if (blackRec.contains(mouseH.pressedX, mouseH.pressedY)) {
                playerTurn = "black";
                gamePlay();
            } else if (whiteRec.contains(mouseH.pressedX, mouseH.pressedY)) {
                playerTurn = "white";
                gamePlay();
            }
        }
    }

    public void update() {

        // Choose side
        if (gameState == SELECT_SIDE) {
            selectSide();
            return;
        }

        if (!changeTurnDelay) {
            changeTurnDelay = true;
            return;
        }

        if (board.isCheckMate(currentTurn) || board.isStaleMate(currentTurn) || countBoardRepeat.getOrDefault(board.getPositionKey2(), 0) == 3) {
            gameState = END_GAME;
        }

        if (gameState == END_GAME) {
            return;
        }

        // Check promotion
        if (board.checkPromotion()) {
            getPromotion();
            return;
        }

        Move nextMove;

        if (currentTurn.equals(playerTurn)) {

            nextMove = getPlayerMove();
            if (nextMove == null) {
                return;
            }

            board.makeMove(nextMove);
            changeTurn();
        } else {
            // get AI move
            MoveSearcher moveSearcher = new MoveSearcher(board);
            board = moveSearcher.getBoard();

            changeTurn();
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        ui.drawBoard(g2);
        ui.drawItems(g2);
        ui.drawAllPieces(g2);
        ui.drawPromotion(g2);

        if (gameState == SELECT_SIDE) {
            ui.drawSelection(g2);
        }

        if (board.isCheckMate(currentTurn)) {
            ui.drawText(g2, "CHECKMATE!");
        }

        if (board.isStaleMate(currentTurn) || countBoardRepeat.getOrDefault(board.getPositionKey2(), 0) == 3) {
            ui.drawText(g2, "STALEMATE!");
        }

        g2.dispose();
    }
}

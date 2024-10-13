package ui;

import main.GamePanel;
import main.Move;
import pieces.King;
import pieces.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UI {

    GamePanel gp;

    public OverlayNextMove overlayNextMove;
    public OverlayTake overlayTake;
    public OverlayCurrentSquare overlayCurrentSquare;
    public Font maruMonica;

    final String[] piecesName = {"pawn", "knight", "bishop", "rook", "queen", "king"};
    BufferedImage[] whitePiecesImage = new BufferedImage[6];
    BufferedImage[] blackPiecesImage = new BufferedImage[6];

    public UI(GamePanel gp) {
        this.gp = gp;
        setItems();
        setFont();
        loadPiecesImage();
    }

    void setItems() {
        overlayNextMove = new OverlayNextMove(gp);
        overlayTake = new OverlayTake(gp);
        overlayCurrentSquare = new OverlayCurrentSquare(gp);
    }

    void setFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadPiecesImage() {
        try {

            for (int i = 0; i < 6; i++) {
                blackPiecesImage[i] = ImageIO.read(getClass().getResourceAsStream("/pieces/black_" + piecesName[i] + ".png"));
            }

            for (int i = 0; i < 6; i++) {
                whitePiecesImage[i] = ImageIO.read(getClass().getResourceAsStream("/pieces/white_" + piecesName[i] + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawSelection(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

        Color color1 = new Color(236,236,252);
        Color color2 = new Color(180,196,220);

        g2.setFont(maruMonica);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 50));

        int width = 150;
        int height = 100;
        int x = (gp.screenWidth - width) / 2;
        int y = gp.tileSize * 2 + 30;
        // 229 182

        g2.setColor(color1);
        g2.fillRoundRect(x, y, width, height, 5, 5);

        String text = "BLACK";
        g2.setColor(Color.white);
        g2.drawString(text, getXForCenteredText(g2, text) + 2, y + 67);
        g2.setColor(Color.black);
        g2.drawRoundRect(x, y, width, height, 5, 5);
        g2.drawString(text, getXForCenteredText(g2, text), y + 65);


        y = gp.tileSize * 6 - height - 30;
        // 229 326

        g2.setColor(color2);
        g2.fillRoundRect(x, y, width, height, 5, 5);

        text = "WHITE";
        g2.setColor(Color.black);
        g2.drawString(text, getXForCenteredText(g2, text) + 2, y + 67);
        g2.setColor(Color.white);
        g2.drawRoundRect(x, y, width, height, 5, 5);
        g2.drawString(text, getXForCenteredText(g2, text), y + 65);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public void drawBoard(Graphics2D g2) {

        Color color1 = new Color(236,236,252);
        Color color2 = new Color(180,196,220);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(color1);
                } else {
                    g2.setColor(color2);
                }

                g2.fillRect(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
            }
        }

        g2.setFont(maruMonica);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20));
        for (int row = 0; row < 8; row++) {
            if (row % 2 == 0) {
                g2.setColor(color2);
            } else {
                g2.setColor(color1);
            }
            g2.drawString(String.valueOf(8 - row), 5,row * gp.tileSize + 20);
        }

        for (int col = 0; col < 8; col++) {
            if (col % 2 == 0) {
                g2.setColor(color1);
            } else {
                g2.setColor(color2);
            }
            g2.drawString(String.valueOf((char)('A' + col)), (col + 1) * gp.tileSize - 15, gp.screenHeight - 7);
        }
    }

    public void drawLastMove(Graphics2D g2, int row, int col) {
        g2.setColor(new Color(162, 253, 217));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.fillRect(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public void drawItems(Graphics2D g2) {

        if (gp.board.lastMove != null) {
            drawLastMove(g2, gp.board.lastMove.start.row, gp.board.lastMove.start.col);
            drawLastMove(g2, gp.board.lastMove.end.row, gp.board.lastMove.end.col);
        }

        if (gp.mouseH.releaseX == -1 && gp.pickedPiece != null) {

            overlayCurrentSquare.drawImage(g2, gp.mouseH.dragY / gp.tileSize, gp.mouseH.dragX / gp.tileSize);

            List<Move> moves = gp.pickedPiece.moves;
            for (Move move : moves) {
                if (gp.board.pieces[move.end.row][move.end.col] == null) {
                    overlayNextMove.drawImage(g2, move.end.row, move.end.col);
                } else {
                    overlayTake.drawImage(g2, move.end.row, move.end.col);
                }
            }

            // Draw castle
            if (gp.pickedPiece instanceof King) {

                int row = gp.pickedPiece.position.row;
                int col = gp.pickedPiece.position.col;

                if (((King) gp.pickedPiece).checkCastle(gp.board.pieces[row][0])) {
                    overlayNextMove.drawImage(g2, row, col - 2);
                }

                if (((King) gp.pickedPiece).checkCastle(gp.board.pieces[row][7])) {
                    overlayNextMove.drawImage(g2, row, col + 2);
                }
            }
        }
    }

    BufferedImage getPieceImage(Piece piece) {
        if (piece.color == "black") {
            return blackPiecesImage[piece.type];
        }
        return whitePiecesImage[piece.type];
    }

    void drawPiece(Graphics2D g2, Piece piece) {

        int x = piece.position.col * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2;
        int y = piece.position.row * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2;
        BufferedImage image = getPieceImage(piece);

        g2.drawImage(image, x, y, gp.pieceSize, gp.pieceSize, null);
    }

    void drawPiece(Graphics2D g2, Piece piece, int x, int y) {
        BufferedImage image = getPieceImage(piece);
        g2.drawImage(image, x, y, gp.pieceSize, gp.pieceSize, null);
    }

    public void drawAllPieces(Graphics2D g2) {

        for (Piece piece : gp.board.pieceList) {
            if (gp.pickedPiece != piece) {
                drawPiece(g2, piece);
            }
        }

        if (gp.pickedPiece != null) {
            Piece piece = gp.pickedPiece;
            int x = piece.position.col * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2 + gp.mouseH.dragX - gp.mouseH.pressedX;
            int y = piece.position.row * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2 + gp.mouseH.dragY - gp.mouseH.pressedY;
            drawPiece(g2, gp.pickedPiece, x, y);
        }
    }

    public void drawPromotion(Graphics2D g2) {

        if (!gp.board.checkPromotion())
            return;

        if (gp.board.promotion == null) {
            return;
        }

        int row = gp.board.lastMove.end.row;
        int col = gp.board.lastMove.end.col;
        if (gp.playerTurn == "white") {

            int x = gp.tileSize * col;
            int y = gp.tileSize * row;
            g2.setColor(Color.black);
            g2.fillRoundRect(x - 5, y - 3, gp.tileSize + 10, gp.tileSize * 4 + 6, 5, 5);
            g2.setColor(new Color(242, 252, 252));
            g2.fillRoundRect(x - 2, y, gp.tileSize + 4, gp.tileSize * 4, 5, 5);
        } else {

            int x = gp.tileSize * col;
            int y = gp.tileSize * (row - 3);
            g2.setColor(Color.black);
            g2.fillRoundRect(x - 5, y - 3, gp.tileSize + 10, gp.tileSize * 4 + 6, 5, 5);
            g2.setColor(new Color(242, 252, 252));
            g2.fillRoundRect(x - 2, y, gp.tileSize + 4, gp.tileSize * 4, 5, 5);
        }

        int d;
        if (gp.playerTurn == "white") {
            d = 1;
        } else {
            d = -1;
        }

        for (int i = 0; i < 4; i++) {

            int x = col * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2;
            int y = (row + d * i) * gp.tileSize + (gp.tileSize - gp.pieceSize) / 2;
            drawPiece(g2, gp.board.promotion[i], x, y);
        }
    }

    public int getXForCenteredText(Graphics2D g2, String text) {

        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

    public void drawDarkScreen(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public void drawText(Graphics2D g2, String text) {
        drawDarkScreen(g2);
        g2.setFont(maruMonica);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60));
        g2.setColor(Color.white);
        g2.drawString(text, getXForCenteredText(g2, text) + 1, 304);
        Color color = new Color(200,12,80);
        g2.setColor(color);
        g2.drawString(text, getXForCenteredText(g2, text), 300);
    }
}

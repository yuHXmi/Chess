package main;

import board.Board;

import java.util.*;
import java.io.*;

public class PGNReader {

    static GamePanel gp;

    static void addGamePanel(GamePanel gamePanel) {
        gp = gamePanel;
    }

    public static Map<String, ArrayList<String>> readPGN(String filePath) {

        Map<String, ArrayList<String>> openings = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            ArrayList<String> moves = new ArrayList<>();
            String openingName = "";
            while ((line = br.readLine()) != null) {

                if (line.startsWith("[Opening")) {
                    if (!moves.isEmpty()) {
                        Board board = new Board(gp.playerTurn);
                        gp.setBoard(board);
                        String currentTurn = "white";
                        for (String move : moves) {
                            openings.put(board.getPositionKey(), moves);
                            Move realMove = Move.parseMove(move, board, currentTurn);
                            board.makeMove(realMove);
                            currentTurn = currentTurn.equals("white") ? "black" : "white";
                        }
                        moves = new ArrayList<>();
                    }
                    openingName = line.split("\"")[1];
                }
                else if (line.startsWith("[Variation")) {
                    openingName += " (" + line.split("\"")[1] + ")";
                }
                else if (line.matches("^[0-9]+\\..*")) {
                    String[] parts = line.split(" ");
                    for (String move : parts) {
                        if (!move.matches("^[0-9]+\\.")) {
                            moves.add(move);
                        }
                    }
                }
            }

            if (!moves.isEmpty()) {
                openings.put(openingName, moves);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return openings;
    }
}

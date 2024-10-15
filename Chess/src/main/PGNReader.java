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
        Map<String, Integer> currentCount = new HashMap<>();
        Map<String, Integer> nextCount = new HashMap<>();

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
                        String lastBoard;
                        for (int i = 0; i < moves.size(); i++) {

                            lastBoard = board.getPositionKey();

                            String move = moves.get(i);
                            Move realMove = Move.parseMove(move, board, currentTurn);
                            board.makeMove(realMove);
                            currentTurn = currentTurn.equals("white") ? "black" : "white";

                            String currentBoard = board.getPositionKey();
                            int count = currentCount.getOrDefault(currentBoard, 0) + moves.size() - i;
                            currentCount.put(currentBoard, count);

                            if (nextCount.getOrDefault(lastBoard, 0) < count) {
                                openings.put(lastBoard, moves);
                                nextCount.put(lastBoard, count);
                            }
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

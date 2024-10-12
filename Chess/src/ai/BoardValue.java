package ai;

import board.Board;

import java.util.Random;

public class BoardValue {

    Board board;
    int value;

    BoardValue(Board board, int value) {
        this.board = board;
        this.value = value;
    }

    void maximize(BoardValue other) {
        if (board == null || value < other.value) {
            board = other.board;
            value = other.value;
            return;
        }
        if (value > other.value)
            return;

        Random rand = new Random();
        if (rand.nextInt(2) == 0) {
            board = other.board;
            value = other.value;
        }
    }

    void minimize(BoardValue other) {
        if (board == null || value > other.value) {
            board = other.board;
            value = other.value;
            return;
        }
        if (value < other.value)
            return;

        Random rand = new Random();
        if (rand.nextInt(2) == 0) {
            board = other.board;
            value = other.value;
        }
    }
}

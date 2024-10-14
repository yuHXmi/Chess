package ai;

import board.Board;

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
        }
    }

    void minimize(BoardValue other) {
        if (board == null || value > other.value) {
            board = other.board;
            value = other.value;
        }
    }
}

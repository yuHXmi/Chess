package ai;

import main.Move;
import main.Position;

public class MoveValue {
    Move move;
    int value;

    MoveValue (Move move, int value) {
        this.move = move;
        this.value = value;
    }

    MoveValue (Move move) {
        this.move = move;
        value = 0;
    }
}

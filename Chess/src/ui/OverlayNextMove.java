package ui;

import main.GamePanel;

public class OverlayNextMove extends BoardItem {

    public OverlayNextMove(GamePanel gp) {
        super(gp);
    }

    void setDefaultValue() {
        name = "overlay_next_move";
        height = 20;
        width = 20;
    }
}

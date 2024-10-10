package ui;

import main.GamePanel;

public class OverlayLastMove extends BoardItem {

    public OverlayLastMove(GamePanel gp) {
        super(gp);
    }

    void setDefaultValue() {
        name = "overlay_last_move";
        height = gp.tileSize;
        width = gp.tileSize;
    }
}

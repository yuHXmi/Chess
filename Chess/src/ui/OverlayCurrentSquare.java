package ui;

import main.GamePanel;

public class OverlayCurrentSquare extends BoardItem {

    public OverlayCurrentSquare(GamePanel gp) {
        super(gp);
    }

    void setDefaultValue() {
        name = "overlay_current_square";
        height = gp.tileSize;
        width = gp.tileSize;
    }
}

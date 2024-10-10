package ui;

import main.GamePanel;

public class OverlayTake extends BoardItem {

    public OverlayTake(GamePanel gp) {
        super(gp);
    }

    void setDefaultValue() {
        name = "overlay_take";
        height = 54;
        width = 54;
    }
}

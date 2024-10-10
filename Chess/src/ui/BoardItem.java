package ui;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BoardItem {

    GamePanel gp;

    String name;
    BufferedImage image;
    int height, width;

    BoardItem(GamePanel gp) {
        this.gp = gp;
        setDefaultValue();
        loadImage();
    }

    void setDefaultValue() {}

    void loadImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/board/" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawImage(Graphics2D g2, int row, int col) {
        int x = col * gp.tileSize + (gp.tileSize - height) / 2;
        int y = row * gp.tileSize + (gp.tileSize - width) / 2;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.drawImage(image, x, y, height, width, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}

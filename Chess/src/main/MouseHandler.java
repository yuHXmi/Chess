package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseListener, MouseMotionListener {

    GamePanel gp;
    public int pressedX, pressedY;
    public int dragX, dragY;
    public int releaseX, releaseY;

    public MouseHandler(GamePanel gp) {

        this.gp = gp;
        pressedX = -1;
        pressedY = -1;
        dragX = -1;
        dragY = -1;
        releaseX = -1;
        releaseY = -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressedX = e.getX();
        pressedY = e.getY();
        dragX = pressedX;
        dragY = pressedY;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressedX = -1;
        pressedY = -1;
        dragX = -1;
        dragY = -1;

        releaseX = e.getX();
        releaseY = e.getY();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragX = e.getX();
        dragY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

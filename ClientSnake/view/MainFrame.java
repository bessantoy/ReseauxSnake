package view;

import javax.swing.JFrame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainFrame extends JFrame implements KeyListener {

    public MainFrame() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed");

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // TODO
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // TODO
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            // TODO
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            // TODO
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
package view;

import javax.swing.JFrame;

import network.Network;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainFrame extends JFrame implements KeyListener {
    Network network = null;

    public MainFrame(Network network) {
        this.network = network;
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed");

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            network.sendMovementSignal("RIGHT");
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            network.sendMovementSignal("LEFT");
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            network.sendMovementSignal("UP");
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            network.sendMovementSignal("DOWN");
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
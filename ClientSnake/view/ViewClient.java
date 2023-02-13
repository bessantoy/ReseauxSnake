package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.ArrayList;
import java.awt.GridLayout;

import model.Network;
import network.Human;

public class ViewClient extends JFrame {
  private transient Network network = null;
  private JPanel panelTop;
  private JTextArea lobbyTextArea;
  private JLabel labelTitle;

  public ViewClient(Network network) {
    this.network = network;
    setTitle("Game");
    setSize(500, 500);
    Dimension windowSize = getSize();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Point centerPoint = ge.getCenterPoint();
    int dx = centerPoint.x - windowSize.width / 2;
    int dy = centerPoint.y - windowSize.height / 2 - 350;
    setLocation(dx, dy);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    panelTop = new JPanel();
    lobbyTextArea = new JTextArea();
    lobbyTextArea.setEditable(false);
    lobbyTextArea.setLineWrap(true);
    lobbyTextArea.setWrapStyleWord(true);

    labelTitle = new JLabel("Snake Game");

    panelTop.setLayout(new GridLayout(2, 1));
    panelTop.add(lobbyTextArea);
    panelTop.add(labelTitle);
    add("North", panelTop);
  }

  public void update(ArrayList<Human> lobby) {
    String lobbyString = "";
    for (Human human : lobby) {
      if (human.isClient())
      lobbyString += human.getUsername();
    lobbyTextArea.setText("");
  }
}

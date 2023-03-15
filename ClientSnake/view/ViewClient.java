package view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import network.Network;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.net.InetAddress;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import utils.HumanFeatures;
import utils.LobbyFeatures;

public class ViewClient extends JFrame {
  private Network network = null;
  private JPanel panelTop;
  private JPanel panelMiddle;
  private JTextPane lobbyLabel;
  private JPanel panelInit;
  private JButton initGame;
  private JPanel panelJoin;
  private JButton joinLobby;
  private JTextField name;
  private JButton launchGame;
  private JButton exit;
  private JComboBox<String> layout;

  public ViewClient(Network network, int id) {
    this.network = network;
    setTitle("Client Snake");
    setSize(500, 500);
    Dimension windowSize = getSize();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Point centerPoint = ge.getCenterPoint();
    int dx = centerPoint.x - windowSize.width / 2;
    int dy = centerPoint.y - windowSize.height / 2 - 350;
    setLocation(dx, dy);
    addWindowListener(new WindowEventHandler(this));
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    panelTop = new JPanel();

    lobbyLabel = new JTextPane();
    lobbyLabel.setEditable(false);
    lobbyLabel.setFont(new java.awt.Font("Arial", 1, 20));
    lobbyLabel.setText("Lobby Info :\n    Lobby empty");

    panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.X_AXIS));
    panelTop.add(lobbyLabel);

    panelInit = new JPanel();

    initGame = new JButton();
    initGame.setText("Init Game");

    String[] layouts = {
        "alone", "aloneNoWall", "arena", "arenaNoWall", "small", "smalArena", "smallArenaNoWall", "smallNoWall"
    };
    layout = new JComboBox<>(layouts);
    layout.setFont(new java.awt.Font("Arial", 1, 20));
    layout.setSelectedIndex(0);

    initGame.addActionListener(e -> network.sendLobbySignal("INIT#" + layout.getSelectedItem()));

    panelInit.setLayout(new GridLayout(1, 2));
    panelInit.add(initGame);
    panelInit.add(layout);

    panelJoin = new JPanel();

    joinLobby = new JButton();
    joinLobby.setText("Join Lobby");

    joinLobby.addActionListener(e -> network.sendLobbySignal("JOIN#" + name.getText()));

    name = new JTextField();
    name.setFont(new java.awt.Font("Arial", 1, 20));
    name.setText("Anonym");

    panelJoin.setLayout(new GridLayout(1, 2));
    panelJoin.add(joinLobby);
    panelJoin.add(name);

    launchGame = new JButton();
    launchGame.setText("Launch Game");

    launchGame.addActionListener(e -> network.sendLobbySignal("LAUNCH"));

    exit = new JButton();
    exit.setText("Exit");

    exit.addActionListener(e -> exit());

    panelMiddle = new JPanel();
    panelMiddle.setLayout(new GridLayout(4, 1));
    panelMiddle.add(panelInit);
    panelMiddle.add(panelJoin);
    panelMiddle.add(launchGame);
    panelMiddle.add(exit);

    setLayout(new GridLayout(2, 1));
    add("top", panelTop);
    add("middle", panelMiddle);

    update(network.getLobbyFeatures(), id);

    setVisible(true);

  }

  public void exit() {
    network.stopConnection();
    this.dispose();
    System.exit(0);
  }

  public void update(LobbyFeatures lobby, int id) {
    handleLobbyUpdate(lobby, id);
    handleButtonsUpdate(lobby, id);
  }

  private void handleLobbyUpdate(LobbyFeatures lobby, int id) {
    String lobbyString = "Lobby Info : ";
    if (lobby.getPlayers().isEmpty()) {
      lobbyString += "Lobby empty";
    } else {
      lobbyString += "\n";
      for (HumanFeatures humanFeatures : lobby.getPlayers()) {
        lobbyString += "    " + humanFeatures.getUsername();
        if (humanFeatures.getId() == id) {
          lobbyString += " (you)";
        }
        lobbyString += "\n";
      }
    }
    lobbyLabel.setText(lobbyString);
  }

  private void handleButtonsUpdate(LobbyFeatures lobby, int id) {
    if (!lobby.isGameInitialised()) {
      initGame.setEnabled(true);
      initGame.setText("Init Game");
      launchGame.setEnabled(false);
      joinLobby.setEnabled(false);
      joinLobby.setText("Join Lobby");
    } else {
      initGame.setEnabled(false);
      layout.setSelectedItem(lobby.getMap());
      joinLobby.setEnabled(true);
      if (lobby.isClientInLobby(id)) {
        removeActionListeners(joinLobby);
        joinLobby.addActionListener(e -> network.sendLobbySignal("LEAVE"));
        joinLobby.setText("Leave Lobby");
        launchGame.setEnabled(lobby.isGameInitialised());
      } else {
        removeActionListeners(joinLobby);
        joinLobby.addActionListener(e -> network.sendLobbySignal("JOIN#" + name.getText()));
        joinLobby.setText("Join Lobby");
        launchGame.setEnabled(false);
      }
    }
  }

  private void removeActionListeners(JButton button) {
    for (int i = 0; i < button.getActionListeners().length; i++) {
      button.removeActionListener(button.getActionListeners()[i]);
    }
  }

}

class WindowEventHandler extends WindowAdapter {
  ViewClient vc;

  public WindowEventHandler(ViewClient vc) {
    this.vc = vc;
  }

  @Override
  public void windowClosing(WindowEvent evt) {
    vc.exit();
  }
}

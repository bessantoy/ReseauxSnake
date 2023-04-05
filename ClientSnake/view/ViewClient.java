package view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.Client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;

import utils.HumanFeatures;
import utils.LobbyFeatures;

public class ViewClient extends JFrame {
  private Client network = null;
  private JScrollPane panelTop;
  private JPanel panelMiddle;
  private JPanel lobbyInfo;
  private JPanel panelReset;
  private JPanel panelOptions;
  private JButton resetGame;
  private JButton createLobby;
  private JButton launchGame;
  private JButton exit;
  private JComboBox<String> layout;
  private JComboBox<String> level;

  public ViewClient(Client network, List<Integer> lobbies) {
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

    panelTop = new JScrollPane();
    panelTop.setPreferredSize(new Dimension(500, 300));
    panelTop.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    panelTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    lobbyInfo = new JPanel();
    lobbyInfo.setLayout(new BoxLayout(lobbyInfo, BoxLayout.Y_AXIS));

    panelTop.setViewportView(lobbyInfo);

    panelReset = new JPanel();
    panelOptions = new JPanel();

    resetGame = new JButton();
    resetGame.setText("Reset Game");
    resetGame.addActionListener(
        e -> network.sendLobbySignal("INIT#" + layout.getSelectedItem() + "#" + level.getSelectedItem()));

    String[] layouts = {
        "alone", "aloneNoWall", "arena", "arenaNoWall", "small", "smallArena", "smallArenaNoWall", "smallNoWall"
    };
    layout = new JComboBox<>(layouts);
    layout.setFont(new java.awt.Font("Arial", 1, 16));
    layout.setSelectedIndex(0);

    String[] levels = { "Advanced", "Random", "Down" };
    level = new JComboBox<>(levels);
    level.setFont(new java.awt.Font("Arial", 1, 16));
    level.setSelectedIndex(0);

    level.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          network.sendLobbySignal("LEVEL#" + e.getItem());
        }
      }
    });

    panelReset.setLayout(new GridLayout(1, 2));
    panelReset.add(resetGame);

    panelOptions.setLayout(new GridLayout(1, 2));
    panelOptions.add(layout);
    panelOptions.add(level);

    panelReset.add(panelOptions);

    createLobby = new JButton();
    createLobby.setText("Create Lobby");

    createLobby.addActionListener(e -> network.sendLobbySignal("CREATE"));

    launchGame = new JButton();
    launchGame.setText("Launch Game");

    launchGame.addActionListener(e -> network.sendLobbySignal("LAUNCH"));

    exit = new JButton();
    exit.setText("Exit");

    exit.addActionListener(e -> exit());

    panelMiddle = new JPanel();
    panelMiddle.setLayout(new GridLayout(4, 1));
    panelMiddle.add(createLobby);
    panelMiddle.add(panelReset);
    panelMiddle.add(launchGame);
    panelMiddle.add(exit);

    setLayout(new GridLayout(2, 1));
    add("top", panelTop);
    add("middle", panelMiddle);

    update(lobbies);

    setVisible(true);

  }

  public void exit() {
    network.stopConnection();
    this.dispose();
    System.exit(0);
  }

  public void update(LobbyFeatures lobby, int id) {
    System.out.println("update lobby");
    handleLobbyUpdate(lobby, id);
    handleButtonsUpdate(lobby, id);
  }

  public void update(List<Integer> lobbies) {
    handleLobbiesUpdate((ArrayList<Integer>) lobbies);
    handleButtonsUpdate();
  }

  private void handleLobbiesUpdate(ArrayList<Integer> lobbies) {
    if (lobbies == null) {
      return;
    }
    lobbyInfo.removeAll();
    for (int lobby : lobbies) {
      JPanel containerPanel = new JPanel();
      BoxLayout containerLayout = new BoxLayout(containerPanel, BoxLayout.X_AXIS);
      containerPanel.setLayout(containerLayout);

      containerPanel.setSize(new Dimension(500, 50));

      JTextPane lobbyPanel = new JTextPane();
      lobbyPanel.setEditable(false);
      lobbyPanel.setText("Lobby " + lobby);
      lobbyPanel.setFont(new java.awt.Font("Arial", 1, 16));
      // align the text vertically in the middle
      lobbyPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
      lobbyPanel.setMaximumSize(new Dimension(380, 50));
      lobbyPanel.setMinimumSize(new Dimension(380, 50));
      lobbyPanel.setPreferredSize(new Dimension(380, 50));

      containerPanel.add(lobbyPanel);

      JButton joinButton = new JButton("Join");
      joinButton.setMaximumSize(new Dimension(100, 50));
      joinButton.setMinimumSize(new Dimension(100, 50));
      joinButton.setPreferredSize(new Dimension(100, 50));
      joinButton.addActionListener(e -> network.sendLobbySignal("JOIN#" + lobby));
      containerPanel.add(joinButton);

      lobbyInfo.add(containerPanel);
    }
    lobbyInfo.revalidate();
    lobbyInfo.repaint();
  }

  private void handleLobbyUpdate(LobbyFeatures lobby, int id) {
    lobbyInfo.removeAll();
    if (lobby == null) {
      return;
    }
    JTextPane lobbyPanel = new JTextPane();
    lobbyPanel.setEditable(false);
    String lobbyStringInfo = "Lobby " + lobby.getId() + " : " + lobby.getGameInstanceFeatures().getMap() + " "
        + lobby.getGameInstanceFeatures().getLevelAI() + "\n";
    int nbPlayers = 1;
    for (HumanFeatures player : lobby.getPlayers()) {
      lobbyStringInfo += "    " + player.getUsername();
      if (player.getId() == id) {
        lobbyStringInfo += " (You)";
      }
      if (nbPlayers > lobby.getGameInstanceFeatures().getPlayerCapacity()) {
        lobbyStringInfo += " (Spectator)";
      }
      lobbyStringInfo += "\n";
      nbPlayers++;
    }
    lobbyPanel.setText(lobbyStringInfo);
    lobbyPanel.setFont(new java.awt.Font("Arial", 1, 16));

    lobbyInfo.add(lobbyPanel);
    lobbyInfo.revalidate();
    lobbyInfo.repaint();
  }

  private void handleButtonsUpdate(LobbyFeatures lobby, int id) {
    if (lobby.isClientInLobby(id)) {
      removeActionListeners(createLobby);
      createLobby.addActionListener(e -> network.sendLobbySignal("LEAVE"));
      createLobby.setText("Leave Lobby");
      layout.setEnabled(true);
      level.setEnabled(true);
      if (!lobby.isGameInitialised()) {
        launchGame.setEnabled(false);
        resetGame.setEnabled(true);
      } else {
        launchGame.setEnabled(true);
        resetGame.setEnabled(true);
        layout.setSelectedItem(lobby.getGameInstanceFeatures().getMap());
        level.setSelectedItem(lobby.getGameInstanceFeatures().getLevelAI());
      }
    } else {
      removeActionListeners(createLobby);
      createLobby.addActionListener(e -> network.sendLobbySignal("CREATE"));
      createLobby.setText("Create Lobby");
      launchGame.setEnabled(false);
      resetGame.setEnabled(false);
      layout.setEnabled(false);
      level.setEnabled(false);
    }
  }

  private void handleButtonsUpdate() {
    removeActionListeners(createLobby);
    createLobby.addActionListener(e -> network.sendLobbySignal("CREATE"));
    createLobby.setText("Create Lobby");
    launchGame.setEnabled(false);
    resetGame.setEnabled(false);
    layout.setEnabled(false);
    level.setEnabled(false);
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

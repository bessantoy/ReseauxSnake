package view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import network.Network;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.GameFeatures;

public class ViewSnakeGame {

	private JFrame jFrame;
	private PanelSnakeGame panelSnake;

	public ViewSnakeGame(PanelSnakeGame panelSnake, Network network) {

		jFrame = new MainFrame(network);
		jFrame.setTitle("Game");
		jFrame.setSize(new Dimension(panelSnake.getSizeX() * 45, panelSnake.getSizeY() * 45 + 100));
		Dimension windowSize = jFrame.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2 - 350;
		jFrame.setLocation(dx, dy);
		jFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(jFrame,
						"Are you sure you want to close the game ?", "Close Game?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					jFrame.dispose();
					network.handleLeaveGame();
				}
			}
		});

		// jFrame.setLayout(new BorderLayout());

		this.panelSnake = panelSnake;

		jFrame.add("Center", panelSnake);

		jFrame.setVisible(true);

	}

	public void update(GameFeatures game) {
		panelSnake.updateInfoGame((ArrayList<FeaturesSnake>) game.getFeaturesSnakes(),
				(ArrayList<FeaturesItem>) game.getFeaturesItems());
		panelSnake.repaint();

	}

	public JFrame getjFrame() {
		return jFrame;
	}

	public void setjFrame(JFrame jFrame) {
		this.jFrame = jFrame;
	}

}

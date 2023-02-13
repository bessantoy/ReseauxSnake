package view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;

import model.Network;
import utils.GameFeatures;

public class ViewSnakeGame {

	private JFrame jFrame;

	private Network network;

	private PanelSnakeGame panelSnake;

	public ViewSnakeGame(PanelSnakeGame panelSnake, Network network) {

		this.network = network;
		jFrame = new MainFrame(network);

		jFrame.setTitle("Game");

		jFrame.setSize(new Dimension(panelSnake.getSizeX() * 45, panelSnake.getSizeY() * 45 + 100));
		Dimension windowSize = jFrame.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2 - 350;
		jFrame.setLocation(dx, dy);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// jFrame.setLayout(new BorderLayout());

		this.panelSnake = panelSnake;

		jFrame.add("Center", panelSnake);

		jFrame.setVisible(true);

	}

	public void update(GameFeatures game) {
		panelSnake.updateInfoGame(game.getFeaturesSnakes(), game.getFeaturesItems());
		panelSnake.repaint();

	}

	public JFrame getjFrame() {
		return jFrame;
	}

	public void setjFrame(JFrame jFrame) {
		this.jFrame = jFrame;
	}

}

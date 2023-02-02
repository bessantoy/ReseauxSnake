package view;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import javax.swing.JFrame;

import utils.GameFeatures;

public class ViewSnakeGame {

	JFrame jFrame;

	PanelSnakeGame panelBomberman;

	public ViewSnakeGame(PanelSnakeGame panelBomberman) {

		jFrame = new MainFrame();

		jFrame.setTitle("Game");

		jFrame.setSize(new Dimension(panelBomberman.getSizeX() * 45, panelBomberman.getSizeY() * 45));
		Dimension windowSize = jFrame.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();
		int dx = centerPoint.x - windowSize.width / 2;
		int dy = centerPoint.y - windowSize.height / 2 - 350;
		jFrame.setLocation(dx, dy);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// jFrame.setLayout(new BorderLayout());

		this.panelBomberman = panelBomberman;

		jFrame.add("Center", panelBomberman);

		jFrame.setVisible(true);

	}

	public void update(GameFeatures game) {

		panelBomberman.updateInfoGame(game.getFeaturesSnakes(), game.getFeaturesItems());

		panelBomberman.repaint();

	}

}

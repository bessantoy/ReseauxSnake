package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Network;
import utils.GameFeatures;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewCommand {

	JFrame jFrame;
	JLabel jtext;

	StateViewCommand state;

	Network network;

	JButton initChoice;
	JButton pauseChoice;
	JButton playChoice;
	JButton stepChoice;

	public ViewCommand(Network network) {

		this.network = network;

		jFrame = new JFrame();
		jFrame.setTitle("Bouton");
		jFrame.setSize(new Dimension(700, 300));
		Dimension windowSize = jFrame.getSize();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Point centerPoint = ge.getCenterPoint();

		int dx = centerPoint.x - windowSize.width / 2 + 1000;
		int dy = centerPoint.y - windowSize.height / 2 - 350;
		jFrame.setLocation(dx, dy);

		Icon icon_restart = new ImageIcon("icons/icon_restart.png");
		initChoice = new JButton(icon_restart);

		Icon icon_play = new ImageIcon("icons/icon_play.png");
		playChoice = new JButton(icon_play);

		Icon icon_step = new ImageIcon("icons/icon_step.png");
		stepChoice = new JButton(icon_step);

		Icon icon_pause = new ImageIcon("icons/icon_pause.png");
		pauseChoice = new JButton(icon_pause);

		initChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendClientSignal("RESTART");
				state.clickRestart();
			}
		});

		//
		pauseChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendClientSignal("PAUSE");
				state.clickPause();
			}
		});

		playChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendClientSignal("RESUME");
				state.clickPlay();
			}
		});

		stepChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendClientSignal("STEP");
				state.clickStep();
			}
		});

		JSlider j = new JSlider(1, 10);

		j.setValue((int) network.getGameFeatures().getSpeed());
		j.setMajorTickSpacing(1);
		j.setPaintTicks(true);
		j.setPaintLabels(true);

		j.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evenement) {
				JSlider source = (JSlider) evenement.getSource();
				if (!source.getValueIsAdjusting()) {
					double speed = source.getValue();
					network.getGameFeatures().setSpeed((long) speed);
					System.out.println("Vitesse changée à : " + speed);
				}
			}
		});

		jFrame.setLayout(new GridLayout(2, 1));

		JPanel haut = new JPanel();
		haut.setLayout(new GridLayout(1, 4));
		haut.add(initChoice);
		haut.add(pauseChoice);
		haut.add(playChoice);
		haut.add(stepChoice);

		jFrame.add(haut);

		JPanel bas = new JPanel();
		bas.setLayout(new GridLayout(1, 2));

		bas.add(j);

		jtext = new JLabel("Tour : ", JLabel.CENTER);

		bas.add(jtext);
		jFrame.add(bas);

		jFrame.setVisible(true);

		state = new StateStarting(this);

	}

	public void setState(StateViewCommand state) {
		this.state = state;
	}

	public void update(GameFeatures game) {

		jtext.setText("Tour :" + game.getTurn());

	}

}
package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import network.Network;
import utils.GameFeatures;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewCommand {

	private JPanel panelMain;
	JLabel jtext;

	StateViewCommand state;

	Network network;

	JButton initChoice;
	JButton pauseChoice;
	JButton playChoice;
	JButton stepChoice;

	JSlider j;

	private boolean userChangeValue = true;

	public ViewCommand(Network network, JFrame mainFrame) {

		this.network = network;

		panelMain = new JPanel(new GridLayout(2, 1));
		panelMain.setMaximumSize(new Dimension(1000, 100));

		Icon iconRestart = new ImageIcon("icons/icon_restart.png");
		initChoice = new JButton(iconRestart);

		Icon iconPlay = new ImageIcon("icons/icon_play.png");
		playChoice = new JButton(iconPlay);

		Icon iconStep = new ImageIcon("icons/icon_step.png");
		stepChoice = new JButton(iconStep);

		Icon iconPause = new ImageIcon("icons/icon_pause.png");
		pauseChoice = new JButton(iconPause);

		initChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendViewCommandSignal("RESTART");
				state.clickRestart();
			}
		});

		//
		pauseChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendViewCommandSignal("PAUSE");
				state.clickPause();
			}
		});

		playChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendViewCommandSignal("RESUME");
				state.clickPlay();
			}
		});

		stepChoice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evenement) {
				network.sendViewCommandSignal("STEP");
				state.clickStep();
			}
		});

		this.j = new JSlider(1, 10);

		j.setValue((int) network.getGameFeatures().getSpeed() / 1000);
		j.setMajorTickSpacing(1);
		j.setPaintTicks(true);
		j.setPaintLabels(true);
		j.setFocusable(false);

		j.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evenement) {
				JSlider source = (JSlider) evenement.getSource();
				if (!source.getValueIsAdjusting() && userChangeValue) {
					System.out.println("changed");
					double speed = source.getValue();
					System.out.println(source.getValue());
					network.sendViewCommandSignal("SPEED");
					network.sendViewCommandSignal(String.valueOf(speed));
				}
			}
		});

		panelMain.setLayout(new GridLayout(2, 1));

		JPanel haut = new JPanel();
		haut.setLayout(new GridLayout(1, 4));
		haut.add(initChoice);
		haut.add(pauseChoice);
		haut.add(playChoice);
		haut.add(stepChoice);

		panelMain.add(haut);

		JPanel bas = new JPanel();
		bas.setLayout(new GridLayout(1, 2));

		bas.add(j);

		jtext = new JLabel("Tour : ", SwingConstants.CENTER);

		bas.add(jtext);
		panelMain.add(bas);

		mainFrame.add("South", panelMain);

		panelMain.setVisible(true);

		state = new StateStarting(this);

	}

	public void setState(StateViewCommand state) {
		this.state = state;
	}

	public void update(GameFeatures game) {

		jtext.setText("Tour :" + game.getTurn());
		switch (game.getState()) {
			case PLAYING:
				this.setState(new StateRunning(this));
				break;
			case PAUSED:
				this.setState(new StateWaiting(this));
				break;
			case OVER:
				this.setState(new StateWaiting(this));
				break;
			case STARTING:
				this.setState(new StateStarting(this));
				break;

		}
	}

	public void updateSlider(Double speed) {
		if (this.j.getValue() != speed.intValue()) {
			userChangeValue = false;
			this.j.setValue(speed.intValue());
			userChangeValue = true;
		}
	}

}
package model;

import java.io.IOException;

import utils.GameState;

public abstract class Game implements Runnable {

	int turn;
	int maxTurn;
	GameState state;

	Thread thread;

	long time = 1000;

	protected Game(int maxTurn) {

		this.maxTurn = maxTurn;

	}

	public void init() {
		this.turn = 0;
		state = GameState.STARTING;

		initializeGame();

	}

	public void step() {

		if (this.gameContinue() & turn < maxTurn) {
			turn++;
			try {
				takeTurn();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			state = GameState.OVER;
			gameOver();
		}
	}

	public void run() {

		while (state == GameState.STARTING || state == GameState.PLAYING) {

			step();
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void pause() {
		state = GameState.PAUSED;
	}

	public void launch() {
		state = GameState.PLAYING;
		this.thread = new Thread(this);
		this.thread.start();

	}

	public abstract void initializeGame();

	public abstract void takeTurn() throws IOException;

	public abstract boolean gameContinue();

	public abstract void gameOver();

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getTurn() {
		return turn;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

}

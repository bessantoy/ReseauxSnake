package model;

import java.io.IOException;

public abstract class Game implements Runnable {

	int turn;
	int maxTurn;
	boolean isRunning;

	Thread thread;

	long time = 100;

	protected Game(int maxTurn) {

		this.maxTurn = maxTurn;

	}

	public void init() {
		this.turn = 0;
		isRunning = true;

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
			isRunning = false;

			gameOver();
		}
	}

	public void run() {

		while (isRunning == true) {

			step();
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public void pause() {

		isRunning = false;
	}

	public void launch() {
		isRunning = true;
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

}

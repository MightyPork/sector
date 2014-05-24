package com.porcupine.time;


import com.porcupine.math.Calc;


/**
 * Precise game timer
 */
public class Timer {

	/**
	 * Ticks elapsed since last updateTimer().<br>
	 * Used to count how many frames to skip.
	 */
	public int ticksMissed;


	/** Speed multiplier. */
	public float timerSpeedMultiplier;


	/**
	 * How much of the next tick has already elapsed.
	 */
	public float renderDeltaTime;


	/**
	 * How many update ticks are to be run since last time
	 * 
	 * @return update ticks needed
	 */
	public int getTicksMissed() {
		return ticksMissed;
	}

	/**
	 * Get render delta time, 0-1
	 * 
	 * @return delta time
	 */
	public double getDeltaTime() {
		return renderDeltaTime;
	}

	/**
	 * @return speed multiplier
	 */
	public double getSpeed() {
		return timerSpeedMultiplier;
	}

	/**
	 * Set speed multiplier
	 * 
	 * @param speed new speed multiplier
	 */
	public void setSpeed(double speed) {
		timerSpeedMultiplier = (float) speed;
	}



	private float ticksPerSecond;
	private double lastUpdateSecs;
	private long lastUpdateMillis;
	private long lastSyncMillis;
	private long syncCounter;
	private double timeSyncAdjustment;


	/**
	 * init timer
	 * 
	 * @param ticksPerSecond logic update ticks per second
	 */
	public Timer(float ticksPerSecond) {
		timerSpeedMultiplier = 1.0F;
		renderDeltaTime = 0.0F;
		timeSyncAdjustment = 1.0D;
		this.ticksPerSecond = ticksPerSecond;

		lastUpdateMillis = System.currentTimeMillis();
		lastSyncMillis = System.nanoTime() / 1000000;
	}

	/**
	 * Updates all fields of the Timer using the current time
	 */
	public void update() {
		long l = System.currentTimeMillis();
		long deltaMillis = l - lastUpdateMillis;
		long millis = System.nanoTime() / 1000000;
		double secs = millis / 1000D;

		if (deltaMillis > 1000L) {
			lastUpdateSecs = secs;
		} else if (deltaMillis < 0L) {
			lastUpdateSecs = secs;
		} else {
			syncCounter += deltaMillis;

			if (syncCounter > 1000L) {
				long d1 = millis - lastSyncMillis;
				double d2 = (double) syncCounter / (double) d1;
				timeSyncAdjustment += (d2 - timeSyncAdjustment) * 0.2D;
				lastSyncMillis = millis;
				syncCounter = 0L;
			}

			if (syncCounter < 0L) {
				lastSyncMillis = millis;
			}
		}

		lastUpdateMillis = l;

		double delta = (secs - lastUpdateSecs) * timeSyncAdjustment;
		lastUpdateSecs = secs;

		delta = Calc.clampd(delta, 0, 1);

		renderDeltaTime += delta * timerSpeedMultiplier * ticksPerSecond;
		ticksMissed = (int) renderDeltaTime;
		renderDeltaTime -= ticksMissed;

		ticksMissed = Calc.clampi(ticksMissed, 0, 10);
	}
}

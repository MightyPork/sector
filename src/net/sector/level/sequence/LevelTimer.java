package net.sector.level.sequence;


/**
 * Level count-down timer
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class LevelTimer {
	private long original = 0;
	private long remains = 0;
	private long lastUpdateTime = 0;
	private boolean running = false;
	private boolean finished;

	/**
	 * Construct a level timer (not started)
	 * 
	 * @param seconds seconds of the count-down
	 */
	public LevelTimer(int seconds) {
		remains = seconds * 1000;
		original = remains;
	}

	/**
	 * Get seconds remaining
	 * 
	 * @return seconds remaining
	 */
	public int getRemainingTime() {
		return Math.round(remains / 1000);
	}

	/**
	 * Get remaining time in format " #:##", or "##:##".
	 * 
	 * @return time formatted as Min:sec, with trailing space in mins and
	 *         trailing zero in secs.
	 */
	public String getRemainingTimeFormatted() {
		int rawSecs = getRemainingTime();
		int sec = rawSecs % 60;
		int mins = (int) Math.floor(rawSecs / 60);

		String s = "00" + sec;
		s = s.substring(s.length() - 2, s.length());

		String m = "  " + mins;
		m = m.substring(m.length() - 2, m.length());

		return m + ":" + s;
	}

	/**
	 * Pause the count-down
	 */
	public void pause() {
		running = false;
	}

	/**
	 * Resume the count-down
	 */
	public void resume() {
		lastUpdateTime = getMs();
		running = true;
	}

	/**
	 * Start the count-down
	 */
	public void start() {
		resume();
	}

	/**
	 * Restart the timer
	 */
	public void restart() {
		remains = original;
		start();
	}

	/**
	 * Update the timer (on game update tick)
	 */
	public void update() {

		if (!isRunning()) return;

		long now = getMs();

		long sinceLastUpdate = now - lastUpdateTime;

		remains -= sinceLastUpdate;

		lastUpdateTime = now;

		if (remains <= 0) {
			finished = true;
			return;
		}
	}

	/**
	 * Get if count-down ended
	 * 
	 * @return is finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Get if timer is running.
	 * 
	 * @return is running
	 */
	public boolean isRunning() {
		return running && !finished;
	}

	/**
	 * Get current time in milliseconds.
	 * 
	 * @return time ms
	 */
	private long getMs() {
		return System.currentTimeMillis();
	}
}

package net.sector.sounds;


public class Music {
	public static void stopAll() {
		Sounds.timer_loop.stop();
		Sounds.shield_loop.stop();
		pauseMusic();
	}

	public static void pauseMusic() {
		Sounds.musDesignerLoop.pauseLoop();
		Sounds.musIngameLoop.pauseLoop();
		Sounds.musMenuLoop.pauseLoop();
	}

	public static void prepareLoops() {
		Sounds.musDesignerLoop.playAsMusicLoop(1, 0.1f);
		Sounds.musDesignerLoop.pauseLoop();

		Sounds.musIngameLoop.playAsMusicLoop(1, 0.15f);
		Sounds.musIngameLoop.pauseLoop();

		Sounds.musMenuLoop.playAsMusicLoop(1, 0.07f);
		Sounds.musMenuLoop.pauseLoop();
	}

	public static void playIntro() {
		Sounds.musIntro.playEffect(1, 0.35f, false);
	}

	public static void playMenu() {
		Sounds.musDesignerLoop.pauseLoop();
		Sounds.musIngameLoop.pauseLoop();

		Sounds.musMenuLoop.resumeLoop();
	}

	public static void playDesigner() {
		Sounds.musIngameLoop.pauseLoop();
		Sounds.musMenuLoop.pauseLoop();

		Sounds.musDesignerLoop.resumeLoop();
	}

	public static void playIngame() {
		Sounds.musDesignerLoop.pauseLoop();
		Sounds.musMenuLoop.pauseLoop();

		Sounds.musIngameLoop.resumeLoop();
	}
}

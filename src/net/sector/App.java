package net.sector;


import static org.lwjgl.opengl.GL11.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;

import javax.swing.*;

import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenMenuMain;
import net.sector.gui.screens.ScreenSplash;
import net.sector.input.Keys;
import net.sector.level.SuperContext;
import net.sector.sounds.Sounds;
import net.sector.threads.ThreadSaveScreenshot;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;
import com.porcupine.time.FpsMeter;
import com.porcupine.time.Timer;
import com.porcupine.util.FileUtils;



/**
 * SECTOR main class
 * 
 * @author MightyPork
 */
public class App {

	/** Flag indicating that network threads failed to load. */
	public static boolean offlineMode = false;

	/** instance */
	public static App inst;

	private static DisplayMode windowDisplayMode = null;

	/** current screen */
	public static Screen screen = null;

	private static boolean lockInstance() {
		final File lockFile = new File(Utils.getGameFolder(), ".lock");
		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						try {
							fileLock.release();
							randomAccessFile.close();
							lockFile.delete();
						} catch (Exception e) {
							System.out.println("Unable to remove lock file.");
							e.printStackTrace();
						}
					}
				});
				return true;
			}
		} catch (Exception e) {
			System.out.println("Unable to create and/or lock file.");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Is if FS
	 * 
	 * @return is in fs
	 */
	public static boolean isFullscreen() {
		return Display.isFullscreen();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		inst = new App();
		try {
			inst.start();
		} catch (Throwable t) {
			showCrashReport(t);
		}

	}

	/**
	 * Show crash report dialog with error stack trace.
	 * 
	 * @param error
	 */
	public static void showCrashReport(Throwable error) {


		Log.e(error);

		try {
			inst.deinit();
		} catch (Throwable t) {}

		JFrame f = new JFrame("SECTOR has crashed!");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));


		StringWriter sw = new StringWriter();
		error.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		String errorLogAsString = "Not found.";
		String wholeLogAsString = "Not found.";

		try {
			wholeLogAsString = FileUtils.fileToString(Utils.getGameSubfolder(Constants.FILE_LOG));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			errorLogAsString = FileUtils.fileToString(Utils.getGameSubfolder(Constants.FILE_LOG_E));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String txt = "";
		txt = "";
		txt += "SECTOR HAS CRASHED!\n";
		txt += "\n";
		txt += "Please report it to MightyPork:\n";
		txt += "\tE-mail: ondra@ondrovo.com\n";
		txt += "\tTwitter: #MightyPork (post log via pastebin.com)\n";
		txt += "\n";
		txt += "\n";
		txt += "Version: " + Constants.VERSION_NAME + "\n";
		txt += "\n";
		txt += "\n";
		txt += "### STACK TRACE ###\n";
		txt += "\n";
		txt += exceptionAsString + "\n";
		txt += "\n";
		txt += "\n";
		txt += "### ERROR LOG ###\n";
		txt += "\n";
		txt += errorLogAsString + "\n";
		txt += "\n";
		txt += "\n";
		txt += "### FULL LOG ###\n";
		txt += "\n";
		txt += wholeLogAsString + "\n";


		// Create Scrolling Text Area in Swing
		JTextArea ta = new JTextArea(txt, 20, 70);
		ta.setFont(new Font("Courier", 0, 16));
		ta.setMargin(new Insets(10, 10, 10, 10));
		ta.setEditable(false);
		ta.setLineWrap(false);
		JScrollPane sbrText = new JScrollPane(ta);
		sbrText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder()));
		sbrText.setWheelScrollingEnabled(true);
		sbrText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sbrText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		// Create Quit Button
		JButton btnQuit = new JButton("Quit");
		btnQuit.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(btnQuit);


		f.getContentPane().add(sbrText);
		f.getContentPane().add(buttonPane);

		// Close when the close button is clicked
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



		//Display Frame
		f.pack(); // Adjusts frame to size of components

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation((dim.width - f.getWidth()) / 2, (dim.height - f.getHeight()) / 2);

		f.setVisible(true);

		while (true) {}
	}



	private void deinit() {
		Display.destroy();
		Mouse.destroy();
		Keyboard.destroy();
		Sounds.soundManager.clear();
		AL.destroy();
	}

	/**
	 * Quit to OS
	 */
	public void exit() {
		deinit();
		System.exit(0);
	}

	/**
	 * Get current screen
	 * 
	 * @return screen
	 */
	public Screen getScreen() {
		return screen;
	}

	/**
	 * Get screen size
	 * 
	 * @return size
	 */
	public Coord getSize() {
		return new Coord(Display.getWidth(), Display.getHeight());
	}

// INIT

	private void init() throws LWJGLException {

		GameConfig.initLoad();

		Log.enable(GameConfig.logEnabled);
		Log.setPrintToStdout(GameConfig.logStdOut);

		Log.i("Game version: " + Constants.VERSION_NAME);

		// init display
		Display.setDisplayMode(windowDisplayMode = new DisplayMode(Constants.WINDOW_SIZE_X, Constants.WINDOW_SIZE_Y));
		Display.setResizable(GameConfig.enableResize);
		Display.setVSyncEnabled(GameConfig.enableVsync);
		Display.setTitle(Constants.TITLEBAR);

		int samples = GameConfig.antialiasing;
		while (true) {
			try {
				Display.create(new PixelFormat().withSamples(samples).withAlphaBits(4));
				Log.i("Created display with " + samples + "x multisampling.");
				break;
			} catch (LWJGLException e) {
				Log.w("Failed to create display with " + samples + "x multisampling, trying " + samples / 2 + "x.");
				if (samples >= 2) {
					samples /= 2;
				} else if (samples == 1) {
					samples = 0;
				} else if (samples == 0) {
					Log.e("Could not create display.", e);
					exit();
				}
			}
		}


		Sounds.soundManager.setMaxSources(256);
		Sounds.soundManager.init();
		Sounds.setListener(Constants.LISTENER_POS);
		applySoundConfig();
		Mouse.create();
		Keyboard.create();
		Keyboard.enableRepeatEvents(false);
		Keys.init();

		LoadingManager.loadForSplash();

		StaticInitializer.initOnStartup();


		//Display.update();
		if (GameConfig.startInFullscreen) {
			switchFullscreen();
			Display.update();
		}
	}

	/**
	 * Apply sounds configuration (from config)
	 */
	public void applySoundConfig() {
		Sounds.soundManager.setSoundVolume(Calc.clampf(GameConfig.audioVolumeSound / 100f, 0, 1));
		Sounds.soundManager.setMusicVolume(Calc.clampf(GameConfig.audioVolumeMusic / 100f, 0, 1));
	}

	private void start() throws LWJGLException {

		if (!lockInstance()) {
			System.out.println("No more than 1 instance of Sector can be running at a time.");

			JOptionPane.showMessageDialog(null, "SECTOR is already running.", "Instance error", JOptionPane.ERROR_MESSAGE);

			exit();
			return;
		}

		Log.enable(true);
		Log.setPrintToStdout(true);

		init();
		mainLoop();
		deinit();
	}

// INIT END


// UPDATE LOOP

	/** fps meter */
	public FpsMeter fpsMeter;

	/** timer */
	public Timer timer;

	private long timerAfterResize;

	private void mainLoop() {
		screen = new ScreenSplash();

		screen.init();

		timer = new Timer(Constants.FPS_UPDATE);
		fpsMeter = new FpsMeter();

		while (!Display.isCloseRequested()) {
			glLoadIdentity();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			int j = timer.ticksMissed;

			if (j > 1) fpsMeter.drop(j - 1);
			timer.update();

			for (int i = 0; i < j; i++) {
				screen.update();
			}

			fpsMeter.frame();
			float delta = timer.renderDeltaTime;
			if (!screen.deltaEnabled()) delta = 0;
			screen.render(delta);
			Display.update();

//			boolean fs2 = Display.isFullscreen();
//			if (fs != fs2) {
//				screen.onFullscreenChange();
//				fs = fs2;
//			}

			if (Keys.justPressed(Keyboard.KEY_F11)) {
				Log.f2("F11, toggle fullscreen.");
				switchFullscreen();
				screen.onFullscreenChange();
				Keys.destroyChangeState(Keyboard.KEY_F11);
			}

			if (Keys.justPressed(Keyboard.KEY_F2)) {
				Log.f2("F2, taking screenshot.");
				takeScreenshot();
				Keys.destroyChangeState(Keyboard.KEY_F2);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
					Log.f2("Ctrl+Q, force quit.");
					Keys.destroyChangeState(Keyboard.KEY_Q);
					exit();
					return;
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
					Log.f2("Ctrl+M, go to main menu.");
					Keys.destroyChangeState(Keyboard.KEY_M);
					screen.rootPanel.onClose();
					screen.rootPanel.onBlur();
					replaceScreen(new ScreenMenuMain());
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					Log.f2("Ctrl+F, switch fullscreen.");
					Keys.destroyChangeState(Keyboard.KEY_F);
					switchFullscreen();
					screen.onFullscreenChange();
				}
			}

			if (Display.wasResized()) {
				screen.onWindowResize();
				timerAfterResize = 0;
			} else {
				timerAfterResize++;
				if (timerAfterResize > Constants.FPS_UPDATE * 0.3) {
					timerAfterResize = 0;
					int x = Display.getX();
					int y = Display.getY();

					int w = Display.getWidth();
					int h = Display.getHeight();
					if (w % 2 != 0 || h % 2 != 0) {
						try {
							Display.setDisplayMode(windowDisplayMode = new DisplayMode(w - w % 2, h - h % 2));
							screen.onWindowResize();
							Display.setLocation(x, y);
						} catch (LWJGLException e) {
							e.printStackTrace();
						}
					}
				}
			}

			try {
				Display.sync(Constants.FPS_RENDER);
			} catch (Throwable t) {
				Log.e("Your graphics card driver does not support fullscreen properly.", t);

				try {
					Display.setDisplayMode(windowDisplayMode);
				} catch (LWJGLException e) {
					Log.e("Error going back from corrupted fullscreen.");
					showCrashReport(e);
				}
			}
		}

		SuperContext.saveUserList(); // just to make sure nothing gets lost.
	}

// UPDATE LOOP END


	private void takeScreenshot() {
		Sounds.shutter.playEffect(1, 1f, false);
		glReadBuffer(GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		new ThreadSaveScreenshot(buffer, width, height, bpp).start();
	}

	/**
	 * Replace screen
	 * 
	 * @param newScreen new screen
	 */
	public void replaceScreen(Screen newScreen) {
		screen = newScreen;
		screen.init();
	}

	/**
	 * Replace screen, don't init it
	 * 
	 * @param newScreen new screen
	 */
	public void replaceScreenNoInit(Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Toggle FS if possible
	 */
	public void switchFullscreen() {
		try {
			if (!Display.isFullscreen()) {
				Log.f3("Entering fullscreen.");
				// save window resize
				windowDisplayMode = new DisplayMode(Display.getWidth(), Display.getHeight());

				Display.setDisplayMode(Display.getDesktopDisplayMode());
				Display.setFullscreen(true);
				Display.update();
//				
//				
//				DisplayMode mode = Display.getDesktopDisplayMode(); //findDisplayMode(WIDTH, HEIGHT);
//				Display.setDisplayModeAndFullscreen(mode);
			} else {
				Log.f3("Leaving fullscreen.");
				Display.setDisplayMode(windowDisplayMode);
				Display.update();
			}
		} catch (Throwable t) {
			Log.e("Failed to toggle fullscreen mode.", t);
			try {
				Display.setDisplayMode(windowDisplayMode);
				Display.update();
			} catch (Throwable t1) {
				showCrashReport(t1);
			}
		}
	}

	/**
	 * Get 1 for window, 2 for fullscreen.
	 * 
	 * @return 1 or 2
	 */
	public static int fs2() {
		return isFullscreen() ? 2 : 1;
	}


}

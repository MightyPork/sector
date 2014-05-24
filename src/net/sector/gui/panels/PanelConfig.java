package net.sector.gui.panels;


import net.sector.GameConfig;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.Checkbox;
import net.sector.gui.widgets.input.Slider;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.sounds.Music;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.porcupine.color.RGB;


/**
 * Overlay panel for paused game.
 * 
 * @author MightyPork
 */
public class PanelConfig extends PanelGui {

	private static final int SAVE = 0;
	private static final int CANCEL = 1;
	private Checkbox ckInitFullscreen;
	private Checkbox ckVsync;
	private Checkbox ckSplash;
	private Checkbox ckUpdateAlerts;

	private int soundVol = GameConfig.audioVolumeSound;
	private int musicVol = GameConfig.audioVolumeMusic;
	private Slider sliderSound;
	private Slider sliderMusic;
	private Button bnCancel;
	private Button bnSave;
	private Checkbox ckResizable;
	private int origin;


	/**
	 * @param screen
	 * @param origin 0 = menu, 1 = pause screen
	 */
	public PanelConfig(Screen screen, int origin) {
		super(screen);
		this.origin = origin;
	}

	@Override
	public void initGui(GuiRoot root) {
		WindowFrame frame = new WindowFrame();
		frame.setPadding(10, 10, 6, 6);
		frame.enableShadow(true);
		root.setRootWidget(frame);

		LayoutV v = new LayoutV(Align.CENTER);
		v.add(new Text("Settings", "small_heading").setMarginsV(5, 10));
		v.add(new Gap(0, 5));

		LayoutV v2 = new LayoutV(Align.LEFT);

		v2.add(ckSplash = (Checkbox) new Checkbox(-1, "animate splash screen").setFont("small_text"));
		v2.add(ckInitFullscreen = (Checkbox) new Checkbox(-1, "start in fullscreen").setFont("small_text"));
		v2.add(ckVsync = (Checkbox) new Checkbox(-1, "enable v-sync").setFont("small_text"));
		v2.add(ckResizable = (Checkbox) new Checkbox(-1, "resizable window").setFont("small_text"));
		v2.add(new Gap(0, 5));
		v2.add(ckUpdateAlerts = (Checkbox) new Checkbox(-1, "enable update alerts").setFont("small_text"));

		ckSplash.setChecked(GameConfig.enableSplash);
		ckInitFullscreen.setChecked(GameConfig.startInFullscreen);
		ckVsync.setChecked(GameConfig.enableVsync);
		ckResizable.setChecked(GameConfig.enableResize);
		ckUpdateAlerts.setChecked(GameConfig.enableUpdateAlerts);

		v2.add(new Gap(0, 15));

		// TODO add sliders
		LayoutH h2;

		h2 = new LayoutH(Align.CENTER);
		h2.add(new Text("Sound", "small_text").setTextAlign(Align.RIGHT).setMinWidth(100));
		h2.add(new Gap(5, 0));
		h2.add(sliderSound = new Slider(250, soundVol / 100D));
		v2.add(h2);

		h2 = new LayoutH(Align.CENTER);
		h2.add(new Text("Music", "small_text").setTextAlign(Align.RIGHT).setMinWidth(100));
		h2.add(new Gap(5, 0));
		h2.add(sliderMusic = new Slider(250, musicVol / 100D));
		v2.add(h2);
		v2.add(new Gap(0, 20));

		v.add(v2);


		LayoutH h = new LayoutH(Align.CENTER);
		h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
		h.add(new Gap(10, 0));
		h.add(bnSave = new Button(SAVE, "Save", "small_text"));
		v.add(h);

		frame.add(v);
	}

	@Override
	public void actionPerformed(Widget widget) {


		if (widget.id == CANCEL) {
			closePanel();
			return;
		}

		if (widget.id == SAVE) {
			GameConfig.setNewProp(GameConfig.pk_splash, ckSplash.isChecked());
			GameConfig.setNewProp(GameConfig.pk_win_fs, ckInitFullscreen.isChecked());
			GameConfig.setNewProp(GameConfig.pk_vsync, ckVsync.isChecked());
			GameConfig.setNewProp(GameConfig.pk_win_resize, ckResizable.isChecked());
			GameConfig.setNewProp(GameConfig.pk_update_notifications, ckUpdateAlerts.isChecked());

			GameConfig.setNewProp(GameConfig.pk_music_volume, Math.round(sliderMusic.getValue() * 100));
			GameConfig.setNewProp(GameConfig.pk_sound_volume, Math.round(sliderSound.getValue() * 100));

			GameConfig.saveLoad();
			GameConfig.useLoaded();

			Music.pauseMusic();
			app.applySoundConfig();
			if (origin == 0) {
				Music.playMenu();
			} else if (origin == 1) {
				Music.playIngame();
			}

			Display.setResizable(GameConfig.enableResize);

			closePanel();
			return;
		}

	}


	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnCancel);
		}

		if (key == Keyboard.KEY_RETURN && down) {
			actionPerformed(bnSave);
		}
	}

	@Override
	public void onFocus() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onBlur() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}
	
	@Override
	public RGB getBackgroundColor() {
		return new RGB(0, 0.4);
	}


}
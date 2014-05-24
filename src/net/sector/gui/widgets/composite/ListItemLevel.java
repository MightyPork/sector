package net.sector.gui.widgets.composite;


import net.sector.App;
import net.sector.gui.panels.highscore.PanelHiscoreLocal;
import net.sector.gui.panels.highscore.PanelHiscoreShared;
import net.sector.gui.screens.ScreenDesigner;
import net.sector.gui.widgets.display.Icon;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.ButtonIcon;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.input.Function;
import net.sector.level.ELevel;
import net.sector.level.LevelBundle;
import net.sector.level.SuperContext;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


public class ListItemLevel extends LayoutH {

	protected ButtonIcon bnPlay;

	public LevelBundle level = null;

	private ButtonIcon bnHighscore;

	private Text txTitle;

	private Text txSubTitle;

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;



		RGB topC = new RGB(0x5D3DD1, 0.4);
		RGB downC = new RGB(0x3D71D1, 0.4);

		if (isMouseOver(mouse) && isPanelOnTop()) {
			topC = new RGB(0x5D3DD1, 0.6);
			downC = new RGB(0x3D71D1, 0.6);
		}

		RenderUtils.quadRectGradV(rect, topC, downC);

		super.render(mouse);
	}

	public ListItemLevel(LevelBundle level) {
		super(Align.CENTER);
		this.level = level;

		setMinWidth(600);
		setMinHeight(60);

		String texture = "designer_icons";

		add(new Gap(8, 0));
		if (level == null || level.type == ELevel.INTERNAL) {
			add(new Icon(texture, 3, 2).setTooltip("Built-in level", new RGB(0xffff66)).setColor(new RGB(0xFFB836)));
		} else if (level.type == ELevel.LOCAL) {
			add(new Icon(texture, 3, 2).setTooltip("Local level", new RGB(0xffcc55)).setColor(new RGB(0xFF7826)));
		} else {
			add(new Icon(texture, 0, 2).setTooltip("Shared level", new RGB(0x9999ff)).setColor(new RGB(0x709DFF)));
		}

		add(new Gap(5, 0));

		LayoutV lv = (LayoutV) new LayoutV(Align.LEFT).setMinWidth(440);
		lv.add(txTitle = (Text) new Text("Level title", "level_title").setTextAlign(Align.LEFT).setMinWidth(440));
		lv.add(txSubTitle = (Text) new Text("Level subtitle", "level_subtitle").setTextAlign(Align.LEFT).setMinWidth(440));
		add(lv);

		txTitle.setMarginsV(10, 0);
		txSubTitle.setColorText(new RGB(0xffffff, 0.7)).setMarginsV(0, 0);

		add(new Gap(5, 0));

		add(bnHighscore = (ButtonIcon) new ButtonIcon(-1, texture, 2, 2).setColor(new RGB(0x11ccff)).setPadding(3, 3));
		add(new Gap(5, 0));
		add(bnPlay = (ButtonIcon) new ButtonIcon(-1, texture, 5, 2).setColor(RGB.GREEN).setPadding(3, 3));
		add(new Gap(5, 0));
		calcChildSizes();


		bnPlay.setTooltip("Play", new RGB(0x66ff66));
		bnHighscore.setTooltip("Highscore", new RGB(0x99ccff));


		bnPlay.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {

				SuperContext.startGame(ListItemLevel.this.level);

				App.inst.replaceScreen(new ScreenDesigner());

				return true;
			}
		});

		bnHighscore.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {

				if (ListItemLevel.this.level.type == ELevel.NET) {
					ListItemLevel.this.getPanel().openPanel(new PanelHiscoreShared(ListItemLevel.this.getPanel().screen, ListItemLevel.this.level));
				} else {
					ListItemLevel.this.getPanel().openPanel(new PanelHiscoreLocal(ListItemLevel.this.getPanel().screen, ListItemLevel.this.level));
				}

				return true;
			}
		});

		if (level != null) {
			txTitle.setText(level.title);
			txSubTitle.setText(level.subtitle);
		}

	}
}

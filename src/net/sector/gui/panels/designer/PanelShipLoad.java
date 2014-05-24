package net.sector.gui.panels.designer;


import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sector.Constants;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.display.TextWithBackground;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.Function;
import net.sector.level.SuperContext;
import net.sector.util.Align;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.util.FileSuffixFilter;
import com.porcupine.util.FileUtils;



public class PanelShipLoad extends PanelGui {

	public static interface IShipLoadDialogListener {
		public void onFileSelected(File outFile);
	}


	private static final int CANCEL = 0;


	private CompositeScrollBox scrollBox;
	private Button bnCancel;

	private EntryFactory entryFactory = new EntryFactory();

	private IShipLoadDialogListener listener = null;


	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 * @param listener listener
	 */
	public PanelShipLoad(Screen screen, IShipLoadDialogListener listener) {
		super(screen);
		this.listener = listener;
	}

	private Function<Boolean> onClickHandler = new Function<Boolean>() {

		@Override
		public Boolean run(Object... args) {
			Widget w = (Widget) args[0];

			String fname = w.getTag();

			final File path = new File(getShipDir(), fname + "." + Constants.SUFFIX_SHIP);

			if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
				if (path.exists()) {
					// delete file.
					PanelDialogModal p;

					IDialogListener dialoghandler = new IDialogListener() {
						@Override
						public void onDialogButton(int dialogId, int button) {
							if (button == 0) return;
							if (button == 1) {

								try {
									path.delete();
									loadFiles();
								} catch (Exception e) {
									Log.e("Error deleting file.", e);
								}

							}
						}
					};

					String msg = "Delete ship design \"" + w.getText() + "\"?";

					p = new PanelDialogModal(screen, dialoghandler, -1, true, msg, "Cancel", "Delete");
					p.setEnterButton(1);
					openPanel(p);
				}

			} else {
				if (listener != null) {

					if (fname.equalsIgnoreCase("///EMPTY///")) {
						Log.f3("Loading empty design.");
						listener.onFileSelected(null);
						closePanel();
						return true;
					}

					if (fname.equalsIgnoreCase("///LAST_SHIP///")) {
						File path2 = SuperContext.getGameContext().levelBundle.getLastShipFile();
						Log.f3("Loading ship from file: " + path2);
						listener.onFileSelected(path2);
						closePanel();
						return true;
					}

					if (!path.exists()) {
						Log.w("File does not exist: " + path);
						PanelDialogModal p = new PanelDialogModal(screen, null, -1, true, "File not found.", "OK");
						openPanel(p);

					} else {
						Log.f3("Loading ship from file: " + path);
						listener.onFileSelected(path);
						closePanel();
					}
				}
			}
			return true;
		}

	};



	private class EntryFactory implements IWidgetFactory {

		@Override
		public Widget getWidget() {
			return getItem(null);
		}

		public Text getItem(String text, String tag, RGB color) {
			Text t = new TextWithBackground(text, "small_text").setBackgroundColor(new RGB(0x479EF5)).setColorText(color);

			t.addOnClickHandler(onClickHandler);
			t.setPadding(6, 4);
			t.setMargins(2, 1, 2, 1);
			t.setTextAlign(Align.LEFT);
			t.setMinWidth(400);

			t.setTag(tag);

			return t;

		}

		public Text getItem(File file) {
			String text = "";
			String tag = "";
			RGB color = RGB.WHITE;

			if (file != null) {
				text = FileUtils.removeExtension(file);
				tag = FileUtils.removeExtension(file);
			}

			return getItem(text, tag, color);
		}
	}


	@Override
	public void initGui(GuiRoot root) {


		//@formatter:off
		WindowFrame frame = new WindowFrame();
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Load ship design", "dialog_heading").setMarginsV(10, 15));	
				v.add(new Text("Click design to load.", "small_text"));		
				v.add(new Text("Delete+click to delete files.", "small_text").setColorText(new RGB(0xffffff,0.6)));	
	
				
				scrollBox = new CompositeScrollBox(8, entryFactory);
				
				v.add(scrollBox);
				
				LayoutH h = new LayoutH(Align.CENTER);
					h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);

		//@formatter:on

		loadFiles();
	}

	private File getShipDir() {
		return Utils.getGameSubfolder(Constants.DIR_SHIPS);
	}

	private void loadFiles() {
		scrollBox.removeAll();

		List<File> files = FileUtils.listFolder(getShipDir(), new FileSuffixFilter(Constants.SUFFIX_SHIP));

		Comparator<File> fc = new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		};

		Collections.sort(files, fc);

		// SuperContext.getGameContext().levelBundle.getLastShipFile()
		if (SuperContext.getGameContext().levelBundle.getLastShipFile().exists()) {
			scrollBox.addItem(entryFactory.getItem("[Last Used Ship]", "///LAST_SHIP///", RGB.ORANGE));
		}
		scrollBox.addItem(entryFactory.getItem("[Empty]", "///EMPTY///", RGB.ORANGE));

		for (File f : files) {
			scrollBox.addItem(entryFactory.getItem(f));
		}

		scrollBox.refresh();
	}


	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == CANCEL) {
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
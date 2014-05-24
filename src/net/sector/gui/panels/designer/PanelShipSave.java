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
import net.sector.gui.widgets.input.TextInput;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.Function;
import net.sector.util.Align;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.util.FileSuffixFilter;
import com.porcupine.util.FileUtils;



public class PanelShipSave extends PanelGui {

	public static interface IShipSaveDialogListener {
		public void onFileSelected(File outFile);
	}


	private static final int CANCEL = 0;
	private static final int SAVE = 1;
	private static final int EDIT = 2;


	private CompositeScrollBox scrollBox;
	private TextInput edFilename;
	private Button bnCancel;
	private Button bnSave;
	private EntryFactory entryFactory = new EntryFactory();

	private IShipSaveDialogListener listener = null;


	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 * @param listener listener
	 */
	public PanelShipSave(Screen screen, IShipSaveDialogListener listener) {
		super(screen);
		this.listener = listener;
	}

	private File getShipDir() {
		return Utils.getGameSubfolder(Constants.DIR_SHIPS);
	}

	private Function<Boolean> onClickHandler = new Function<Boolean>() {

		@Override
		public Boolean run(Object... args) {
			Widget w = (Widget) args[0];
			String fname = w.getTag();

			final File path = new File(getShipDir(), fname + ".xml");

			if (path.exists() && Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {

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

			} else {
				edFilename.setText(w.getTag());
				actionPerformed(edFilename);
			}
			return true;
		}

	};



	private class EntryFactory implements IWidgetFactory {

		@Override
		public Widget getWidget() {
			return getItem(null);
		}

		public Text getItem(File file) {
			Text t = new TextWithBackground("", "small_text").setBackgroundColor(new RGB(0x479EF5));

			t.addOnClickHandler(onClickHandler);
			t.setPadding(6, 4);
			t.setMargins(2, 1, 2, 1);
			t.setTextAlign(Align.LEFT);
			t.setMinWidth(400);

			if (file != null) {
				t.setText(FileUtils.removeExtension(file));
				t.setTag(FileUtils.removeExtension(file));
			}
			return t;

		}
	}


	@Override
	public void initGui(GuiRoot root) {


		//@formatter:off
		WindowFrame frame = new WindowFrame();
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Save ship design", "dialog_heading").setMarginsV(10, 15));	
				v.add(new Text("Delete+click to delete files.", "small_text").setColorText(new RGB(0xffffff,0.7)));				
				
				scrollBox = new CompositeScrollBox(8, entryFactory);
				
				v.add(scrollBox);
				v.add(edFilename = (TextInput) new TextInput(EDIT, "", "small_text").setMinWidth(440));
				
				edFilename.setAllowedChars(TextInput.CHARS_FILENAME);
				
				LayoutV v2 = (LayoutV) new LayoutV(Align.RIGHT).setMinWidth(440);
					LayoutH h = new LayoutH(Align.CENTER);
						h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
						h.add(new Gap(5,0));
						h.add(bnSave = new Button(SAVE, "OK", "small_text"));
						bnSave.setEnabled(false);
						h.add(new Gap(10,0));
					v2.add(h);
				v.add(v2);
			
			frame.add(v);
		
		root.setRootWidget(frame);

		//@formatter:on

		loadFiles();
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

		for (File f : files) {
			scrollBox.addItem(entryFactory.getItem(f));
		}

		scrollBox.refresh();
	}


	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == EDIT) {
			bnSave.setEnabled(widget.getText().length() > 0);
		}

		if (widget.id == CANCEL) {
			closePanel();
			return;
		}

		if (widget.id == SAVE) {
			String fname = edFilename.getText();

			final File path = new File(getShipDir(), fname + "." + Constants.SUFFIX_SHIP);

			if (listener != null) {
				if (path.exists()) {

					PanelDialogModal p;

					IDialogListener dialoghandler = new IDialogListener() {
						@Override
						public void onDialogButton(int dialogId, int button) {
							if (button == 0) return;
							if (button == 1) {

								listener.onFileSelected(path);
								closePanel();

							}
						}
					};

					String msg = "The file already exists!";

					p = new PanelDialogModal(screen, dialoghandler, -1, true, msg, "Cancel", "Overwrite");
					p.setEnterButton(1);
					openPanel(p);

				} else {
					listener.onFileSelected(path);
					closePanel();
				}
			}

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
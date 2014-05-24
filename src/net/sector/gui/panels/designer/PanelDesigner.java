package net.sector.gui.panels.designer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.sector.App;
import net.sector.gui.panels.Panel;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.designer.PanelShipLoad.IShipLoadDialogListener;
import net.sector.gui.panels.designer.PanelShipSave.IShipSaveDialogListener;
import net.sector.gui.panels.dialogs.EDialogColor;
import net.sector.gui.panels.dialogs.PanelDialogCaptureInput;
import net.sector.gui.panels.dialogs.PanelDialogCaptureInput.ICaptureInputDialogListener;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenDesigner;
import net.sector.gui.screens.ScreenGame;
import net.sector.gui.screens.ScreenLevels;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.*;
import net.sector.gui.widgets.composite.ShipDesignerTable.EShipStructureError;
import net.sector.gui.widgets.composite.ShipDesignerTable.EdMode;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.display.TextDouble;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.ButtonIcon;
import net.sector.gui.widgets.input.ButtonVertical;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.TriggerBundle;
import net.sector.level.EBuildingMode;
import net.sector.level.GameContext;
import net.sector.level.GameCursor;
import net.sector.level.ship.DiscoveryTable;
import net.sector.level.ship.PieceBundle;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.ship.ShipBundle;
import net.sector.level.ship.modules.EnergySystem;
import net.sector.level.ship.modules.Shield;
import net.sector.util.Align;
import net.sector.util.Log;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.coord.CoordI;
import com.porcupine.struct.Struct3;
import com.porcupine.util.StringUtils;


/**
 * Main menu panel
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelDesigner extends PanelGui {

	private static final int id_BACK = 0, id_PLAY = 1, id_SHIELD_UP = 2, id_SHIELD_DOWN = 3, id_ENERGY_UP = 4, id_ENERGY_DOWN = 5;

	private ShipDesignerTable table;
	private Text txDesignStatus;
	private ShipDesignerController ctrl;
	private ShipDesignerShop shop;

	private Button bnPlay, bnShUp, bnShDown, bnEnUp, bnEnDown, bnBack;
	private ArrayList<ButtonVertical> groupButtons = new ArrayList<ButtonVertical>();


	private static final int id_ARRANGE = 30, id_SELECT = 31, id_UNSELECT = 32, id_LEVEL_UP = 33;
	private static final int id_LEVEL_DOWN = 34, id_MOVE_UP = 35, id_MOVE_DOWN = 36, id_MOVE_LEFT = 37;
	private static final int id_MOVE_RIGHT = 38, id_DELETE = 39, id_ROT_CW = 40, id_ROT_CCW = 41, id_CONTROLS = 42, id_SELECT_ALL = 23;
	private static final int id_ANY_TAB = 53, id_SAVE = 54, id_LOAD = 55;

	@SuppressWarnings("unused")
	private Button bnArrang, bnSel, bnUnsel, bnLvlUp, bnLvlDn, bnMvUp, bnMvDn, bnMvLt, bnMvRt, bnDel, bnRotCW, bnRotCCW, bnControls, bnSelAll,
			bnSave, bnLoad;

	private Text txShLevel, txEnLevel;

	private TextDouble[] infoTexts;


	private int shieldLevel = 1;
	private int shieldLevelMax = Shield.getLevelMax();

	private int energyLevel = 1;
	private int energyLevelMax = EnergySystem.getLevelMax();

	private RGB white = new RGB(1, 1, 1);
	private RGB orange = new RGB(1, 0.7, 0);
	private RGB red = new RGB(1, 0, 0);
	private RGB green = new RGB(0, 1, 0);
	private RGB blue = new RGB(0.4, 0.5, 1);
	private RGB purple = new RGB(0.7, 0.3, 1);

	private GameContext context;
	private GameCursor cursor;
	private ShipBundle shipBundle;
	private DiscoveryTable discoveries;
	private Text txtControl;

	/**
	 * Create designer
	 * 
	 * @param screen screen
	 * @param context game context
	 */
	public PanelDesigner(Screen screen, GameContext context) {
		super(screen);

		context.restoreCursor();

		this.context = context;

		this.cursor = context.getCursor();

		this.discoveries = context.getCursor().discoveryTable;
		this.shipBundle = context.getCursor().shipBundle;

		shieldLevelMax = discoveries.getDiscoveryLevel("shield");
		energyLevelMax = discoveries.getDiscoveryLevel("energy");

		shieldLevel = shipBundle.shieldLevel;
		energyLevel = shipBundle.energyLevel;

		if (shieldLevelMax > 0 && shieldLevel == 0) shieldLevel = 1;
		if (energyLevelMax > 0 && energyLevel == 0) energyLevel = 1;
	}

	@Override
	public void onFocus() {
		super.onFocus();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void onBlur() {
		super.onBlur();
	}

	private ButtonVertical makeLeftTab(int id, String text) {
		ButtonVertical bv = new ButtonVertical(id, text, "small_button");
		bv.setTheme(ETheme.DES_TAB_BUTTON);
		bv.setMargins(0, 3, 0, 3);
		bv.setPadding(0, 8);
		bv.bdrs[0] = true;
		bv.bdrs[1] = false;
		bv.bdrs[2] = true;
		bv.bdrs[3] = true;
		bv.sndClick = true;
		return bv;
	}

	@Override
	public void initGui(GuiRoot gui) {
		LayoutH h1, h2;
		LayoutV v1, v2;
		WindowFrame frame, f2;

		frame = new WindowFrame();
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(false);

		//@formatter:off
		
		v1 = new LayoutV(Align.CENTER);

			// TOP MENU BUTTONS
			h1 = new LayoutH(Align.CENTER);
							
				bnBack = new Button(id_BACK, "Back");	
				bnBack.setMinWidth(100);
				h1.add(bnBack);
				
				txDesignStatus = (Text) new Text("", "designer_info").setTextAlign(Align.CENTER).setMinWidth(500);
				h1.add(txDesignStatus);
				
				bnPlay = new Button(id_PLAY, "Play");
				bnPlay.setMinWidth(100);
				h1.add(bnPlay);
				
			v1.add(h1);
				
			h1 = new LayoutH(Align.TOP);	
			
				v2 = (LayoutV) new LayoutV(Align.CENTER).setMargins(0, 0, 0, 0);
				
				v2.add(new Gap(0, 10));
				
				for(Entry<String,String> g: PieceRegistry.groups.entrySet()) {
					ButtonVertical bv;
					String text = g.getValue();
					v2.add(bv = (ButtonVertical) makeLeftTab(id_ANY_TAB, text).setTag(g.getKey()));
					groupButtons.add(bv);
				}
				
				groupButtons.get(0).setSelected(true);
				
				h1.add(v2);
			
				table = new ShipDesignerTable(shipBundle.getShipDesign(), discoveries);		
				shop = new ShipDesignerShop(table, discoveries);	
				ctrl = new ShipDesignerController(cursor.money);	
				ctrl.buildMode = cursor.buildMode;
				
				if(ctrl.buildMode==EBuildingMode.LOCKED) {
					for(Widget w: groupButtons) {
						w.setEnabled(false);
					}
				}
				
				shop.setMarginsH(0, 5);
						
				ctrl.addTable(shop);
				ctrl.addTable(table);
				
				table.setDesignerGui(this);
				
				h1.add(shop);
				h1.add(table);
								
				f2 = new WindowFrame();
				f2.enableShadow(false);
				f2.setPadding(10, 10, 10, 10);
				f2.setMinHeight(380);
				f2.setBorderSize(2);
				f2.setTheme(ETheme.DES_PANEL);
				f2.setAlignH(Align.CENTER);
				f2.setAlignV(Align.TOP);
				
					v2 = new LayoutV(Align.CENTER);					
						infoTexts = new TextDouble[4];
					
						// MONEY INDICATORS
						v2.add(new Gap(0,4));
						v2.add(infoTexts[0] = mkSidebarTextDouble());
						v2.add(new Gap(0,10));
						
						v2.add(infoTexts[1] = mkSidebarTextDouble());
						v2.add(new Gap(0,3));
						v2.add(infoTexts[2] = mkSidebarTextDouble());
						v2.add(new Gap(0,3));
						v2.add(infoTexts[3] = mkSidebarTextDouble());
						
						// TRIGGER INDICATOR
						v2.add(new Gap(0,20));
						v2.add(txtControl = mkSidebarTextSimple().setColorText(RGB.RED).setTextAlign(0));

						v2.add(new Gap(0,50));
						
						Widget w;
						
						// ENERGY
						v2.add(w=new Text("Energy System", "designer_infopanel").setColorText(white).setMargins(0, 15, 0, -6));
						if(energyLevelMax==0) w.setVisible(false);
						
						h2 = new LayoutH(Align.CENTER);
						h2.setMargins(0, -6, 0, 0);
							h2.add(bnEnDown = new Button(id_ENERGY_DOWN, "<<", "designer_infopanel"));
							h2.add(txEnLevel = (Text) new Text(energyLevel+"", "designer_infopanel").setTextAlign(Align.CENTER).setColorText(green).setMinWidth(20));					
							h2.add(bnEnUp = new Button(id_ENERGY_UP, ">>", "designer_infopanel"));
						v2.add(h2);
						if(energyLevelMax==0) {
							h2.setVisible(false);
							w.setVisible(false);
						}
						
						if(ctrl.buildMode==EBuildingMode.LOCKED) {
							bnEnDown.setEnabled(false);
							bnEnUp.setEnabled(false);
						}
						
						// SHIELD
						v2.add(w = new Text("Force Shield", "designer_infopanel").setColorText(white).setMargins(0, 15, 0, -6));						
						if(shieldLevelMax==0) w.setVisible(false);
						
						h2 = new LayoutH(Align.CENTER);
						h2.setMargins(0, -6, 0, 0);
							h2.add(bnShDown = new Button(id_SHIELD_DOWN, "<<", "designer_infopanel"));
							h2.add(txShLevel = (Text) new Text(shieldLevel+"", "designer_infopanel").setTextAlign(Align.CENTER).setColorText(green).setMinWidth(20));					
							h2.add(bnShUp = new Button(id_SHIELD_UP, ">>", "designer_infopanel"));
						v2.add(h2);	
						
						if(shieldLevelMax==0) {
							h2.setVisible(false);
							w.setVisible(false);
						}
						
						if(ctrl.buildMode==EBuildingMode.LOCKED) {
							bnShDown.setEnabled(false);
							bnShUp.setEnabled(false);
						}
						
						bnShDown.sndClick = bnShUp.sndClick = bnEnDown.sndClick = bnEnUp.sndClick = true;
					
					f2.add(v2);
				
				h1.add(f2);
				
			v1.add(h1);
			
			// LOWER NAVBAR
			h1 = new LayoutH(Align.CENTER);
				String texture = "designer_icons";
				RGB color = RGB.WHITE;
				h1.add(bnArrang = new ButtonIcon(id_ARRANGE, texture, 0, 0).setTooltip("Editing", color));
				h1.add(bnSel = new ButtonIcon(id_SELECT, texture, 1, 0).setTooltip("Selecting", color));
				h1.add(new Gap(4,0));
				h1.add(bnSelAll = new ButtonIcon(id_SELECT_ALL, texture, 7, 0).setTooltip("Select all", color));
				h1.add(bnUnsel = new ButtonIcon(id_UNSELECT, texture, 2, 0).setTooltip("Cancel selection", color));
				h1.add(bnDel = new ButtonIcon(id_DELETE, texture, 4, 1).setTooltip("Destroy", color));				
				h1.add(new Gap(8,0));				
				h1.add(bnLvlDn = new ButtonIcon(id_LEVEL_DOWN, texture, 4, 0).setTooltip("Level down", color));				
				h1.add(bnLvlUp = new ButtonIcon(id_LEVEL_UP, texture, 3, 0).setTooltip("Level up", color));
				h1.add(new Gap(4,0));
				h1.add(bnRotCCW = new ButtonIcon(id_ROT_CCW, texture, 6, 0).setTooltip("Rotate", color));
				h1.add(bnRotCW = new ButtonIcon(id_ROT_CW, texture, 5, 0).setTooltip("Rotate", color));				
				h1.add(new Gap(4,0));
				h1.add(bnMvLt = new ButtonIcon(id_MOVE_LEFT, texture, 3, 1).setTooltip("Move left", color));
				h1.add(bnMvRt = new ButtonIcon(id_MOVE_RIGHT, texture, 2, 1).setTooltip("Move right", color));
				h1.add(bnMvUp = new ButtonIcon(id_MOVE_UP, texture, 0, 1).setTooltip("Move up", color));
				h1.add(bnMvDn = new ButtonIcon(id_MOVE_DOWN, texture, 1, 1).setTooltip("Move down", color));
				h1.add(new Gap(4,0));
				h1.add(bnControls = new ButtonIcon(id_CONTROLS, texture, 5, 1).setTooltip("Controls", color));
				h1.add(new Gap(8,0));
				h1.add(bnSave = new ButtonIcon(id_SAVE, texture, 6, 1).setTooltip("Save", color));
				h1.add(bnLoad = new ButtonIcon(id_LOAD, texture, 7, 1).setTooltip("Load", color));

				
				if(ctrl.buildMode == EBuildingMode.LOCKED) {
					bnDel.enabled = bnLvlUp.enabled = bnLvlDn.enabled = bnRotCCW.enabled = bnRotCW.enabled = false;
					bnLoad.enabled = bnMvDn.enabled = bnMvLt.enabled = bnMvRt.enabled = bnMvUp.enabled = false;
				}
				
			v1.add(h1);
		
			// size-less drag controlling widget.
			v1.add(ctrl);
		
		frame.add(v1);
		
		gui.setRootWidget(frame);	
		
		actionPerformed(frame);
		
		if(ctrl.buildMode==EBuildingMode.LOCKED) shop.clearSlots();
		
		//@formatter:on
	}

	private TextDouble mkSidebarTextDouble() {
		return (TextDouble) new TextDouble(140).setFonts("designer_infopanel", "designer_infopanel").setMarginsH(2, 4).setMarginsV(-1, -1);
	}

	private Text mkSidebarTextSimple() {
		return (Text) new Text("", "smaller_text").setMinWidth(140).setMarginsH(2, 4).setMarginsV(-1, -1);
	}

	@Override
	public boolean hasBackgroundLayer() {
		return false;
	}

	@Override
	protected void renderPanel() {
		super.renderPanel();

		if (!isTop()) return;

		txtControl.setText(ctrl.pieceTriggerDesc);

		// if not locked, show validity test.
		if (ctrl.buildMode != EBuildingMode.LOCKED) {
			txDesignStatus.setText(table.lastErrorFound.getString());
			if (!table.lastErrorFound.isValid()) {
				txDesignStatus.setColorText(red);
			} else {
				txDesignStatus.setColorText(green);
			}
		} else {
			txDesignStatus.setText("Design is locked.");
			txDesignStatus.setColorText(orange);
		}

		// if locked or free, hide all sidebar texts.
		if (ctrl.buildMode == EBuildingMode.LOCKED || ctrl.buildMode == EBuildingMode.FREE) {
			txDesignStatus.text = "";

			for (int cnt = 0; cnt < infoTexts.length; cnt++) {
				infoTexts[cnt].eraseTexts();
			}

			if (ctrl.buildMode == EBuildingMode.FREE) {
				infoTexts[0].setTextLeft("Free building mode.", orange);
			}

			return;
		}


		// update context info.
		// label, text, color
		ArrayList<Struct3<String, String, RGB>> infos = new ArrayList<Struct3<String, String, RGB>>();


		infos.add(new Struct3<String, String, RGB>("Your Cash", StringUtils.formatInt(ctrl.getTotalMoney()), orange));

		if (ctrl.infoIsBought) {
			if (ctrl.infoValue) {
				infos.add(new Struct3<String, String, RGB>("Piece value", StringUtils.formatInt(ctrl.infoValueCost), green));
			}

			if (ctrl.infoRepair) {
				infos.add(new Struct3<String, String, RGB>("Repair", StringUtils.formatInt(ctrl.infoRepairCost), red));
			} else {
				if (ctrl.infoUpgrade) {
					infos.add(new Struct3<String, String, RGB>("Upgrade", StringUtils.formatInt(ctrl.infoUpgradeCost), blue));
				}
				if (ctrl.infoDowngrade) {
					infos.add(new Struct3<String, String, RGB>("Downgrade", StringUtils.formatInt(ctrl.infoDowngradeCost), purple));
				}
			}
		} else {
			if (ctrl.infoBuy) {
				infos.add(new Struct3<String, String, RGB>("Price", StringUtils.formatInt(ctrl.infoBuyCost), blue));
			}
			if (ctrl.infoUpgrade) {
				infos.add(new Struct3<String, String, RGB>("Upgrade", StringUtils.formatInt(ctrl.infoUpgradeCost), blue));
			}
			if (ctrl.infoDowngrade) {
				infos.add(new Struct3<String, String, RGB>("Downgrade", StringUtils.formatInt(ctrl.infoDowngradeCost), purple));
			}
		}
		if (!ctrl.isDragging()) {

			if (bnEnUp.lastRenderHover && bnEnUp.isEnabled()) {
				int n = PieceRegistry.getLevelChangeCost(EnergySystem.getBaseCost(), energyLevel, energyLevel + 1);
				infos.add(new Struct3<String, String, RGB>("Upgrade", StringUtils.formatInt(n), blue));
			}

			if (bnEnDown.lastRenderHover && bnEnDown.isEnabled()) {
				int n = PieceRegistry.getLevelChangeCost(EnergySystem.getBaseCost(), energyLevel, energyLevel - 1);
				infos.add(new Struct3<String, String, RGB>("Downgrade", StringUtils.formatInt(n), purple));
			}

			if (bnShUp.lastRenderHover && bnShUp.isEnabled()) {
				int n = PieceRegistry.getLevelChangeCost(Shield.getBaseCost(), shieldLevel, shieldLevel + 1);
				infos.add(new Struct3<String, String, RGB>("Upgrade", StringUtils.formatInt(n), blue));
			}

			if (bnShDown.lastRenderHover && bnShDown.isEnabled()) {
				int n = PieceRegistry.getLevelChangeCost(Shield.getBaseCost(), shieldLevel, shieldLevel - 1);
				infos.add(new Struct3<String, String, RGB>("Downgrade", StringUtils.formatInt(n), purple));
			}

		}

		for (int cnt = 0; cnt < infoTexts.length; cnt++) {
			if (cnt < infos.size()) {
				Struct3<String, String, RGB> info = infos.get(cnt);
				infoTexts[cnt].setTextLeft(info.a, white).setTextRight(info.b, info.c);
			} else {
				infoTexts[cnt].setTextLeft("", white).setTextRight("", white);
			}
		}

	}

	@Override
	public void actionPerformed(Widget widget) {

		int id = widget.getId();

		if (ctrl.buildMode == EBuildingMode.LOCKED && id == id_ANY_TAB) {
			return;
		}

		CoordI move;

		switch (id) {
			case id_ANY_TAB:
				String group = widget.getTag();
				shop.fillWithPieceGroup(group);

				for (ButtonVertical b : groupButtons) {
					b.setSelected(false);
				}

				((ButtonVertical) widget).setSelected(true);

				break;

			case id_PLAY:

				EShipStructureError e = table.checkDesign();

				if (e.isValid()) {

					PieceBundle[][] pieces = table.exportShipDesign();
					cursor.shipBundle = new ShipBundle(pieces, shieldLevel, energyLevel);
					cursor.money = ctrl.getTotalMoney();
					if (!context.levelBundle.hadDefaultShip || cursor.buildMode == EBuildingMode.LOCKED) context.saveCursorInBundle();

					context.saveShipToFile();

					app.replaceScreen(new ScreenGame(context));

				} else {

					PanelDialogModal p = new PanelDialogModal(screen, null, -1, true, e.getDescription(), "OK");
					openPanel(p);

				}

				break;

			case id_LOAD:
				if (cursor.buildMode == EBuildingMode.LOCKED) {
					break;
				}

				openPanel(new PanelShipLoad(screen, new IShipLoadDialogListener() {

					@Override
					public void onFileSelected(File inFile) {

						try {

							PieceBundle[][] pieces = table.exportShipDesign();
							int cost = new ShipBundle(pieces, shieldLevel, energyLevel).getTotalCost();

							cursor.money = ctrl.getTotalMoney() + cost;

							if (inFile == null) {
								// empty design requested...
								cursor.shipBundle = new ShipBundle(new PieceBundle[1][1], 1, 1);
								cursor.shipBundle.reduceForDiscoveryTable(cursor.discoveryTable);
							} else {
								// load ship
								cursor.shipBundle.xmlFromStream(new FileInputStream(inFile));

								// reduce for discovery table and cost
								cursor.shipBundle.reduceForDiscoveryTable(cursor.discoveryTable);

								if (cursor.buildMode != EBuildingMode.FREE) {
									cursor.shipBundle.reduceForTotalCost(cursor.money);
									// pay for new ship
									cursor.money -= cursor.shipBundle.getTotalCost();
								}

							}

							// save to context
							context.saveCursor();

							// reload designer
							App.inst.replaceScreen(new ScreenDesigner());

						} catch (Exception e) {
							Log.e("Error loading file.", e);

							Panel p = new PanelDialogModal(screen, null, -1, true, "Loading failed.\n" + e.getMessage(), "OK");
							PanelDesigner.this.openPanel(p);
						}
					}

				}));

				break;

			case id_SAVE:
				final ShipBundle currentShip = new ShipBundle(table.exportShipDesign(), shieldLevel, energyLevel);

				openPanel(new PanelShipSave(screen, new IShipSaveDialogListener() {

					@Override
					public void onFileSelected(File outFile) {
						try {
							currentShip.xmlToStream(new FileOutputStream(outFile));
						} catch (Exception e) {
							Log.e("Error saving file.", e);

							Panel p = new PanelDialogModal(screen, null, -1, true, "The ship could not be saved.\n" + e.getMessage(), "OK");
							openPanel(p);
						}
					}

				}));

				break;

			case id_BACK:
				cursor.shipBundle = new ShipBundle(table.exportShipDesign(), shieldLevel, energyLevel);
				cursor.money = ctrl.getTotalMoney();
				if (!context.levelBundle.hadDefaultShip || cursor.buildMode == EBuildingMode.LOCKED) context.saveCursorInBundle();
				app.replaceScreen(new ScreenLevels());
				break;

			case id_SHIELD_DOWN:
				if (shieldLevel > 1) {
					int mny = Math.abs(PieceRegistry.getLevelChangeCost(Shield.getBaseCost(), shieldLevel, shieldLevel - 1));
					ctrl.addMoney(mny);
					shieldLevel--;
					txShLevel.setText(shieldLevel + "");

				}
				break;

			case id_SHIELD_UP:
				if (shieldLevel < shieldLevelMax) {
					int mny = Math.abs(PieceRegistry.getLevelChangeCost(Shield.getBaseCost(), shieldLevel, shieldLevel + 1));
					if (ctrl.hasMoney(mny)) {
						ctrl.consumeMoney(mny);
						shieldLevel++;
						txShLevel.setText(shieldLevel + "");
					}
				}
				break;

			case id_ENERGY_DOWN:
				if (energyLevel > 1) {
					int mny = Math.abs(PieceRegistry.getLevelChangeCost(EnergySystem.getBaseCost(), energyLevel, energyLevel - 1));
					ctrl.addMoney(mny);
					energyLevel--;
					txEnLevel.setText(energyLevel + "");
				}
				break;

			case id_ENERGY_UP:
				if (energyLevel < energyLevelMax) {
					int mny = Math.abs(PieceRegistry.getLevelChangeCost(EnergySystem.getBaseCost(), energyLevel, energyLevel + 1));
					if (ctrl.hasMoney(mny)) {
						ctrl.consumeMoney(mny);
						energyLevel++;
						txEnLevel.setText(energyLevel + "");
					}
				}
				break;

			case id_ARRANGE:
				if (table.getEditMode() == EdMode.ARRANGE) break;
				ctrl.cancelDrag();
				table.setEditMode(EdMode.ARRANGE);
				bnArrang.setSelected(true);
				bnSel.setSelected(false);
				bnDel.setSelected(false);
				break;

			case id_SELECT:
				if (table.getEditMode() == EdMode.SELECT) break;
				ctrl.cancelDrag();
				table.setEditMode(EdMode.SELECT);
				bnArrang.setSelected(false);
				bnSel.setSelected(true);
				bnDel.setSelected(false);
				break;

			case id_SELECT_ALL:
				table.selectAll();
				break;

			case id_UNSELECT:
				table.unselectAll();
				break;

			case id_LEVEL_UP:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				if (ctrl.isDragging()) {
					table.levelChangeDragged(1);
				} else {
					table.levelChangeSelected(1);
				}
				break;

			case id_LEVEL_DOWN:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				if (ctrl.isDragging()) {
					table.levelChangeDragged(-1);
				} else {
					table.levelChangeSelected(-1);
				}
				break;

			case id_ROT_CCW:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				if (ctrl.isDragging()) {
					table.rotatePiece(ctrl.getDragged(), 1);
				} else {
					table.rotateSelected(1);
				}
				break;

			case id_ROT_CW:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				if (ctrl.isDragging()) {
					table.rotatePiece(ctrl.getDragged(), -1);
				} else {
					table.rotateSelected(-1);
				}
				break;

			case id_MOVE_UP:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				move = new CoordI(0, -1);
				if (table.isAnySelected()) {
					table.moveSelected(move);
				} else {
					table.moveAll(move);
				}
				break;

			case id_MOVE_DOWN:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				move = new CoordI(0, 1);
				if (table.isAnySelected()) {
					table.moveSelected(move);
				} else {
					table.moveAll(move);
				}
				break;

			case id_MOVE_LEFT:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				move = new CoordI(-1, 0);
				if (table.isAnySelected()) {
					table.moveSelected(move);
				} else {
					table.moveAll(move);
				}
				break;

			case id_MOVE_RIGHT:
				if (ctrl.buildMode == EBuildingMode.LOCKED) break;
				move = new CoordI(1, 0);
				if (table.isAnySelected()) {
					table.moveSelected(move);
				} else {
					table.moveAll(move);
				}
				break;

			case id_DELETE:
				if (ctrl.isDragging()) {
					table.sellPiece(ctrl.getDragged());
					ctrl.resetDragInfo();
					break;
				} else {
					deleteSelectedAsk();
				}
				break;

			case id_CONTROLS:
				if (ctrl.isDragging()) {
					// can't set controls of dragged piece
					break;
				} else if (table.isAnySelected()) {
					// set controls of selected pieces

					editControlsSelected();

				} else {
					// let the table handle this event.
					//openPanel(new PanelDialogModal(screen, this, dlg_INFO, true, "No pieces selected!", "OK"));
				}
				break;
		}

		boolean locked = ctrl.buildMode == EBuildingMode.LOCKED;
		bnShDown.setEnabled(shieldLevel > 1 && !locked);
		bnShUp.setEnabled(shieldLevel < shieldLevelMax && !locked);

		bnEnDown.setEnabled(energyLevel > 1 && !locked);
		bnEnUp.setEnabled(energyLevel < energyLevelMax && !locked);

		EdMode mode = table.getEditMode();
		bnArrang.setSelected(mode == EdMode.ARRANGE);
		bnSel.setSelected(mode == EdMode.SELECT);
		bnDel.setSelected(mode == EdMode.DELETE);

		getRootWidget().updatePositions();

		table.checkDesign();
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (down) {
			switch (key) {
				case Keyboard.KEY_ESCAPE:
					actionPerformed(bnBack);
					break;

				case Keyboard.KEY_RETURN:
				case Keyboard.KEY_E:
					actionPerformed(bnControls);
					break;

				case Keyboard.KEY_LEFT:
					actionPerformed(bnMvLt);
					break;

				case Keyboard.KEY_RIGHT:
					actionPerformed(bnMvRt);
					break;

				case Keyboard.KEY_UP:
					actionPerformed(bnMvUp);
					break;

				case Keyboard.KEY_DOWN:
					actionPerformed(bnMvDn);
					break;

				case Keyboard.KEY_DELETE:
					actionPerformed(bnDel);
					break;

				case Keyboard.KEY_N:
					actionPerformed(bnRotCCW);
					break;

				case Keyboard.KEY_M:
					actionPerformed(bnRotCW);
					break;
			}
		}

		table.checkDesign();
	}


	public void deleteSelectedAsk() {
		if (ctrl.buildMode == EBuildingMode.LOCKED) return;

		if (table.isAnySelected()) {
			int count = table.selectedSlots.size();
			String msg = "";
			if (count > 1) {
				msg = "Really delete " + count + " selected pieces?";
			} else {
				msg = "Really delete 1 selected piece?";
			}

			IDialogListener handler = new IDialogListener() {
				@Override
				public void onDialogButton(int id, int btn) {

					if (btn == 1) {
						table.deleteSelected();
					}

				}
			};

			openPanel(new PanelDialogModal(screen, handler, -1, true, msg, "No", "Yes").setEnterButton(1));
		}
	}

	public void editControlsSelected() {
		if (table.isAnySelected()) {
			// set controls of selected pieces

			boolean differentTriggers = false;
			boolean differentPieces = false;
			TriggerBundle trigger = null;
			String pieceLabel = null;

			boolean anyTriggerPresent = false;

			for (CoordI tile : table.selectedSlots) {
				DraggablePiece p = table.slots[tile.y][tile.x];
				if (p != null) {
					if (p.hasTrigger()) {
						if (trigger != null && !trigger.equals(p.getTrigger())) differentTriggers = true;
						String name = PieceRegistry.getPieceLabel(p.id);
						if (pieceLabel != null && !pieceLabel.equals(name)) differentPieces = true;
						trigger = p.getTrigger();
						pieceLabel = name;
						anyTriggerPresent = true;
					}
				}
			}

			if (!anyTriggerPresent) {
				openPanel(new PanelDialogModal(screen, null, -1, true, "Selected pieces are passive.\nTry with weapons.", "OK"));
				return;
			}

			String triggerName = "";

			if (differentPieces) pieceLabel = "Various pieces";
			if (differentTriggers) {
				triggerName = "Various triggers";
			} else if (trigger != null) {
				triggerName = trigger.getLabel(false);
			}

			ICaptureInputDialogListener handler = new ICaptureInputDialogListener() {
				@Override
				public void onCaptureInputDialogClosed(int dialogId, TriggerBundle newTrigger) {
					if (newTrigger == null) {
						table.unselectAll();
						return;
					}

					for (CoordI tile : table.selectedSlots) {
						DraggablePiece p = table.slots[tile.y][tile.x];
						if (p != null) {
							if (p.hasTrigger()) {
								p.setTrigger(newTrigger);
							}
						}
					}

					table.unselectAll();

				}
			};


			PanelDialogCaptureInput p = new PanelDialogCaptureInput(screen, handler, -1, true, pieceLabel, triggerName);
			p.setColorTheme(EDialogColor.BLUE);
			openPanel(p);


		}
	}
}

package peakaboo.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;

import peakaboo.common.Version;
import peakaboo.controller.mapper.MapController;
import peakaboo.controller.mapper.AllMapsModel;
import peakaboo.controller.plotter.ChannelCompositeMode;
import peakaboo.controller.plotter.PlotController;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.datatypes.eventful.PeakabooMessageListener;
import peakaboo.datatypes.peaktable.Element;
import peakaboo.datatypes.peaktable.TransitionSeriesType;
import peakaboo.datatypes.tasks.TaskList;
import peakaboo.mapping.MapResultSet;
import peakaboo.ui.swing.filters.FiltersetViewer;
import peakaboo.ui.swing.fitting.CurveFittingView;
import peakaboo.ui.swing.icons.IconFactory;
import peakaboo.ui.swing.widgets.ClearPanel;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.ToolbarImageButton;
import peakaboo.ui.swing.widgets.ImageButton.Layout;
import peakaboo.ui.swing.widgets.dialogues.AboutDialogue;
import peakaboo.ui.swing.widgets.dialogues.ScanInfoDialogue;
import peakaboo.ui.swing.widgets.dialogues.SimpleFileFilter;
import peakaboo.ui.swing.widgets.dialogues.SimpleIODialogues;
import peakaboo.ui.swing.widgets.pictures.SavePicture;
import peakaboo.ui.swing.widgets.tasks.TaskListView;
import peakaboo.ui.swing.widgets.toggle.ComplexToggle;

/**
 * 
 * This class is the main window for Peakaboo, the plotting window
 * 
 * @author Nathaniel Sherry, 2009
 */

public class PeakabooPlotterSwing {

	String savedSessionFileName;

	PlotController controller;

	PlotCanvas canvas;
	JComboBox titleCombo;
	JFrame frame;
	JSpinner scanNo;
	JLabel scanLabel;
	JToggleButton scanBlock;
	JLabel channelLabel;
	JMenuItem mapMenuItem;
	JMenuItem snapshotMenuItem;
	JSpinner energy;
	JSlider zoomSlider;
	JMenuItem saveSession;
	JButton toolbarSnapshot;
	JButton toolbarMap;
	JButton toolbarInfo;
	JPanel zoomPanel;
	JPanel bottomPanel;
	JPanel scanSelector;
	JScrollPane scrolledCanvas;

	String savePictureFolder;

	public PeakabooPlotterSwing() {

		savedSessionFileName = null;

		frame = new JFrame(Version.title);
		frame.setIconImage(IconFactory.getImage(Version.logo));

		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D toy = bi.createGraphics();

		controller = new PlotController(toy);

		frame.setPreferredSize(new Dimension(1000, 470));
		initGUI();

		controller.addListener(new PeakabooMessageListener() {
			

			public void change(Object message) {

				//special notifications go here...

			}

			public void change() {
				setWidgetsState();
			}
		});

		setWidgetsState();

	}

	public void setWidgetsState() {

		
		snapshotMenuItem.setEnabled(false);
		toolbarSnapshot.setEnabled(false);

		// anything to do with the dataset
		if (controller.hasDataSet()) {

			bottomPanel.setEnabled(true);
			snapshotMenuItem.setEnabled(true);
			toolbarSnapshot.setEnabled(true);
			
			if (controller.getFittedElements().size() == 0
					|| controller.datasetScanCount() == 0) {
				mapMenuItem.setEnabled(false);
				toolbarMap.setEnabled(false);
			} else {
				mapMenuItem.setEnabled(true);
				toolbarMap.setEnabled(true);
			}

			if (controller.getScanHasExtendedInformation()) {
				toolbarInfo.setEnabled(true);
			} else {
				toolbarInfo.setEnabled(false);
			}

			energy.setValue(controller.getMaxEnergy());
			
			if (controller.getChannelCompositeType() == ChannelCompositeMode.NONE) {

				scanNo.setValue(controller.getScanNumber() + 1);
				scanBlock.setSelected(controller.getScanDiscarded());
				scanSelector.setEnabled(true);
				
			} else {
				scanSelector.setEnabled(false);
			}

		} else {
			bottomPanel.setEnabled(false);
		}

		zoomSlider.setValue((int) (controller.getZoom() * 100.0));
		setTitleBar();

		fullRedraw();

	}

	private void actionOpenData() {

		List<String> files;

		files = openNewDataset();

		if (files != null) {

			TaskList<Boolean> reading = controller
					.TASK_readFileListAsDataset(files);
			new TaskListView(frame, reading);

			// set some controls based on the fact that we have just loaded a
			// new data set
			savedSessionFileName = null;
			saveSession.setEnabled(false);

		}

	}

	private void actionMap() {
		if (controller.hasDataSet()) {

			TaskList<MapResultSet> tasks = controller.TASK_getDataForMapFromSelectedRegions();
			if (tasks == null)
				return;
			new TaskListView(frame, tasks);

			if (!tasks.isAborted()) {

				MapController mapController = controller.getMapController();
				PeakabooMapperSwing mapperWindow;

				Coord<Integer> dataDimensions = controller.getDataDimensions();
				
				MapResultSet results = tasks.getResult();
				AllMapsModel datamodel = new AllMapsModel(results);
				
				datamodel.dataDimensions = dataDimensions;
				datamodel.dimensionsProvided = controller.hasDimensions();
				datamodel.badPoints = controller.getDiscardedScanList();
				datamodel.realDimensions = controller.getRealDimensions();
				datamodel.realDimensionsUnits = controller.getRealDimensionsUnits();
				
				if (mapController == null) {
					
					mapperWindow = new PeakabooMapperSwing
					(
							frame, datamodel, controller.getDatasetName(), true, 
							controller.getDataSourceFolder(), dataDimensions, results
					);
					
				} else {
					
					int dy = mapController.getDataHeight();
					int dx = mapController.getDataWidth();
					
					//if the data does not match size, discard it. either it was from
					//a funny data set that isn't square or (more likely) they reloaded the
					//data set
					//TODO: invistigate -- is it easier to null the mapController pointer when
					//loading new data and assume the data is the same size otherwise?
					if (dataDimensions.x * dataDimensions.y == dx*dy) {
						dataDimensions.x = dx;
						dataDimensions.y = dy;
					}
										
					mapperWindow = new PeakabooMapperSwing
					(
							frame, datamodel, controller.getDatasetName(), true, 
							controller.getDataSourceFolder(), dataDimensions, results
					);
				}

				controller.setMapController(mapperWindow.showDialog());
			}
		}
	}

	private void actionSaveSession(boolean forceSaveAs) {

		if (savedSessionFileName == null || forceSaveAs) {

			String filename = getSavePreferencesFile();
			if (filename != null) {
				if (!filename.endsWith(".peakaboo"))
					filename += ".peakaboo";
				controller.savePreferences(filename);
				savedSessionFileName = filename;
				saveSession.setEnabled(true);
			}
		} else {
			controller.savePreferences(savedSessionFileName);
		}

	}

	private void actionSavePicture() {
		if (savePictureFolder == null)
			savePictureFolder = controller.getDataSourceFolder();
		savePictureFolder = new SavePicture(frame, controller,
				savePictureFolder).getStartingFolder();
	}

	private void actionSaveFittingInformation() {

		if (savePictureFolder == null)
			savePictureFolder = controller.getDataSourceFolder();
		String saveFile = SimpleIODialogues.chooseFileSave(frame,
				"Save Fitting Data to Text File", savePictureFolder, "txt",
				"Text Files");
		if (saveFile != null) {

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						new File(saveFile)));
				savePictureFolder = new File(saveFile).getParent();

				List<Element> elements = controller.getFittedElements();
				double intensity;

				for (Element e : elements) {

					for (TransitionSeriesType tst : controller
							.getTransitionSeriesTypesForElement(e, true)) {

						if (controller.getTransitionSeriesForElement(e, tst).visible == false)
							continue;
						intensity = controller
								.getTransitionSeriesIntensityForElement(e, tst);
						writer.write(e.toString() + " " + tst.toString() + ", "
								+ SigDigits.roundDoubleTo(intensity, 2) + "\n");
					}

				}

				writer.flush();
				writer.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void actionLoadSession() {
		String filename = getLoadPreferencesFile();
		if (filename != null)
			controller.loadPreferences(filename);
		savedSessionFileName = filename;
		saveSession.setEnabled(true);
	}

	public void actionShowInfo() {

		new ScanInfoDialogue(frame, controller);

	}

	private void initGUI() {

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas = new PlotCanvas(controller);
		canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		channelLabel = new JLabel("");
		channelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		channelLabel.setFont(channelLabel.getFont().deriveFont(Font.PLAIN));
		
		canvas.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {}

			public void mouseMoved(MouseEvent e) {
				mouseMoveCanvasEvent(e.getX());
			}

		});

		frame.setTitle(Version.title);

		Container pane = frame.getContentPane();

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		pane.setLayout(layout);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		populateToolbar(toolBar);
		pane.add(toolBar, c);

		scrolledCanvas = new JScrollPane(canvas);
		scrolledCanvas.setAutoscrolls(true);
		scrolledCanvas.setBorder(Spacing.bNone());
		scrolledCanvas.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrolledCanvas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		canvas.addMouseMotionListener(new MouseMotionListener() {
			
			private int x0;
			private Point p0;
			
			private BoundedRangeModel horizontalModel = scrolledCanvas.getHorizontalScrollBar().getModel();
			
			public void mouseMoved(MouseEvent e) {
				x0 = horizontalModel.getValue();
				p0 = e.getLocationOnScreen();
			}
			
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				Point p1 = e.getLocationOnScreen();
				int x1 = p0.x - p1.x + x0;
				
				horizontalModel.setValue(x1);
				
			}
		});
		
		// pane.add(scrolledCanvas, c);

		JPanel canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(scrolledCanvas, BorderLayout.CENTER);
		canvasPanel.add(createBottomBar(), BorderLayout.SOUTH);
		canvasPanel.setPreferredSize(new Dimension(600, 300));

		// DockingManager.setFloatingEnabled(true);
		// DockingManager.setDefaultSiblingSize(0.2f);

		DefaultDockingPort optionsPort = new DefaultDockingPort();
		DefaultDockingPort spectrumPort = new DefaultDockingPort();
		optionsPort.getDockingProperties().setTabPlacement(JTabbedPane.TOP);
		optionsPort.setPreferredSize(new Dimension(800, 100));
		spectrumPort.getDockingProperties().setTabPlacement(JTabbedPane.TOP);
		spectrumPort.setPreferredSize(new Dimension(800, 100));
		// port.setTabsAsDragSource(true);

		CurveFittingView cfv = new CurveFittingView(controller);
		FiltersetViewer fsv = new FiltersetViewer(controller, frame);

		// dock the main panel
		DockingManager.registerDockable(canvasPanel, "Spectrum");

		// dock the side panels
		DockingManager.registerDockable(fsv, "Filters");
		DockingManager.registerDockable(cfv, "Peak Fitting");

		optionsPort.dock(fsv, DockingConstants.CENTER_REGION);
		optionsPort.dock(cfv, DockingConstants.CENTER_REGION);
		spectrumPort.dock(canvasPanel, DockingConstants.CENTER_REGION);

		// DockingManager.dock(cfv, fsv, DockingConstants.CENTER_REGION);

		// set the ratio of side panel vs main panel
		// DockingManager.setSplitProportion((DockingPort) port, 0.2f);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				optionsPort, spectrumPort);
		split.setResizeWeight(0);
		split.setOneTouchExpandable(true);
		split.setBorder(Spacing.bNone());
		pane.add(split, c);

		createMenu();

		// Display the window.
		frame.pack();
		frame.setVisible(true);

	}

	private void setTitleBar() {
		frame.setTitle(getTitleBarString());
	}

	private String getTitleBarString() {
		StringBuffer titleString;
		titleString = new StringBuffer(Version.title);
		if (controller.hasDataSet()) {

			String dataSetName = controller.getDatasetName();
			titleString.append(" (" + dataSetName + ")");

			if (controller.getChannelCompositeType() == ChannelCompositeMode.NONE) {
				titleString.append(" - " + controller.getCurrentScanName());
			} else {
				titleString
						.append(" - " + controller.getChannelCompositeType());
			}

		}

		return titleString.toString();
	}

	private void populateToolbar(JToolBar toolbar) {

		toolbar.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;

		JButton button = new ToolbarImageButton("document-open", "Open",
				"Open a new data set");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				actionOpenData();
			}
		});
		toolbar.add(button, c);

		button = new JButton("Export Data");
		// controls.add(button);

		toolbarSnapshot = new ToolbarImageButton("picture", "Save Image",
				"Save a picture of the current plot");
		toolbarSnapshot.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				actionSavePicture();
			}
		});
		c.gridx += 1;
		toolbar.add(toolbarSnapshot, c);

		toolbarInfo = new ToolbarImageButton("info", "Scan Info",
				"Displays extended information about this data set");
		toolbarInfo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				actionShowInfo();
			}
		});
		c.gridx += 1;
		toolbarInfo.setEnabled(false);
		toolbar.add(toolbarInfo, c);

		toolbarMap = new ToolbarImageButton(
				"map",
				"Map Fittings",
				"Display a 2D map of the relative intensities of the fitted elements",
				true);
		toolbarMap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				actionMap();
			}
		});
		c.gridx += 1;
		toolbarMap.setEnabled(false);
		toolbar.add(toolbarMap, c);

		// controls.addSeparator();

		c.gridx += 1;
		c.weightx = 1.0;
		toolbar.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;

		// toolbar.addSeparator();

		// toolbar.addSeparator();

		JPanel energyControls = new ClearPanel();
		energyControls.setBorder(Spacing.bMedium());
		energyControls.setLayout(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.gridy = 0;
		c2.weightx = 0;
		c2.weighty = 0;
		c2.fill = GridBagConstraints.NONE;
		c2.anchor = GridBagConstraints.EAST;

		JLabel energyLabel = new JLabel("Max Energy (keV): ");
		energyControls.add(energyLabel, c2);

		energy = new JSpinner();
		energy.setModel(new SpinnerNumberModel(20.48, 0.0, 204.8, 0.01));

		c2.gridx += 1;
		energyControls.add(energy, c2);
		energy.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {

				double value = ((Double) energy.getValue());
				controller.setMaxEnergy(value);

			}
		});
		c.gridx += 1;
		toolbar.add(energyControls, c);

		button = new ToolbarImageButton("about", "About");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new AboutDialogue(frame);
			}
		});
		c.gridx += 1;
		toolbar.add(button, c);

	}

	public void createMenu() {

		// Where the GUI is created:
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		ActionListener fileMenuListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String command = e.getActionCommand();

				if (command == "Open Data...") {

					actionOpenData();

				} else if (command == "Export Data as Compressed File") {

				} else if (command == "Export Plot as Image") {

					actionSavePicture();

				} else if (command == "Export Fittings as Text") {

					actionSaveFittingInformation();

				} else if (command == "Save Session") {

					actionSaveSession(false);

				} else if (command == "Save Session As...") {

					actionSaveSession(true);

				} else if (command == "Load Session...") {

					actionLoadSession();

				} else if (command == "Exit") {

					System.exit(0);

				}

			}

		};

		// FILE Menu
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				"Read and Write Data Sets");

		menuItem = new JMenuItem("Open Data...", IconFactory
				.getMenuIcon("document-open"));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				java.awt.event.ActionEvent.CTRL_MASK));
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Opens new data sets.");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menu.addSeparator();

		saveSession = new JMenuItem("Save Session", IconFactory
				.getMenuIcon("document-save"));
		saveSession.addActionListener(fileMenuListener);
		menu.add(saveSession);
		saveSession.setEnabled(false);

		menuItem = new JMenuItem("Save Session As...", IconFactory
				.getMenuIcon("document-save-as"));
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menuItem = new JMenuItem("Load Session...");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		// SEPARATOR
		/*
		 * menu.addSeparator();
		 * 
		 * menuItem = new JMenuItem("Export Data as Compressed File");
		 * menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
		 * java.awt.event.ActionEvent.CTRL_MASK));
		 * menuItem.setMnemonic(KeyEvent.VK_E);
		 * menuItem.getAccessibleContext().setAccessibleDescription(
		 * "Opens new data sets.");
		 * menuItem.addActionListener(fileMenuListener); menu.add(menuItem);
		 */

		// SEPARATOR
		menu.addSeparator();

		snapshotMenuItem = new JMenuItem("Export Plot as Image", IconFactory
				.getMenuIcon("picture"));
		snapshotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				java.awt.event.ActionEvent.CTRL_MASK));
		snapshotMenuItem.setMnemonic(KeyEvent.VK_P);
		snapshotMenuItem.getAccessibleContext().setAccessibleDescription(
				"Saves the current plot as an image");
		snapshotMenuItem.addActionListener(fileMenuListener);
		menu.add(snapshotMenuItem);

		menuItem = new JMenuItem("Export Fittings as Text", IconFactory
				.getMenuIcon("textfile"));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Saves the current fitting data to a text file");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		// SEPARATOR
		menu.addSeparator();

		menuItem = new JMenuItem("Exit", IconFactory
				.getMenuIcon("window-close"));
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Opens new data sets.");
		menuItem.addActionListener(fileMenuListener);
		menu.add(menuItem);

		menuBar.add(menu);

		// VIEW Menu
		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription(
				"Change the way the plot is viewed");
		menuBar.add(menu);

		ActionListener toggleOptionListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String command = e.getActionCommand();
				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();

				if (command == "Logarithmic Plot") {
					controller.setViewLog(menuitem.isSelected());
				} else if (command == "Axes") {
					controller.setShowAxes(menuitem.isSelected());
				} else if (command == "Title") {
					controller.setShowTitle(menuitem.isSelected());
				} else if (command == "Monochrome") {
					controller.setMonochrome(menuitem.isSelected());
				}

			}

		};

		ActionListener viewStyleListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String command = e.getActionCommand();

				if (command == "Individual Spectrum") {

					controller.setShowChannelSingle();

				} else if (command == "Mean Spectrum") {

					controller.setShowChannelAverage();

				} else if (command == "Top Tenth per Channel") {

					controller.setShowChannelMaximum();

				}

			}

		};

		final JMenuItem logPlot, axes, monochrome, title;

		// a group of JMenuItems
		logPlot = new JCheckBoxMenuItem("Logarithmic Plot");
		logPlot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
				java.awt.event.ActionEvent.CTRL_MASK));
		logPlot.setMnemonic(KeyEvent.VK_L);
		logPlot.getAccessibleContext().setAccessibleDescription(
				"Toggles the plot to be shown in logarithmic scale");
		logPlot.addActionListener(toggleOptionListener);
		menu.add(logPlot);

		axes = new JCheckBoxMenuItem("Axes");
		axes.addActionListener(toggleOptionListener);
		menu.add(axes);

		title = new JCheckBoxMenuItem("Title");
		title.addActionListener(toggleOptionListener);
		menu.add(title);

		monochrome = new JCheckBoxMenuItem("Monochrome");
		monochrome.setMnemonic(KeyEvent.VK_O);
		monochrome.addActionListener(toggleOptionListener);
		menu.add(monochrome);

		// Element Markings submenu
		JMenu elementTitles = new JMenu("Curve Fit");
		final JCheckBoxMenuItem etitles, emarkings, eintensities;

		// element name option
		etitles = new JCheckBoxMenuItem("Names");
		etitles.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controller.setShowElementTitles(etitles.isSelected());
			}
		});
		elementTitles.add(etitles);

		// element markings option
		emarkings = new JCheckBoxMenuItem("Markings");
		emarkings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controller.setShowElementMarkers(emarkings.isSelected());
			}
		});
		elementTitles.add(emarkings);

		// element intensities option
		eintensities = new JCheckBoxMenuItem("Heights");
		eintensities.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				controller.setShowElementIntensities(eintensities.isSelected());
			}
		});
		elementTitles.add(eintensities);

		menu.add(elementTitles);

		// SEPARATOR
		menu.addSeparator();

		final JRadioButtonMenuItem individual, average, maximum;

		// a group of radio button menu items
		ButtonGroup viewGroup = new ButtonGroup();

		individual = new JRadioButtonMenuItem("Individual Spectrum");
		individual.setSelected(true);
		individual.setMnemonic(KeyEvent.VK_I);
		individual.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				java.awt.event.ActionEvent.CTRL_MASK));
		individual.addActionListener(viewStyleListener);
		viewGroup.add(individual);
		menu.add(individual);

		average = new JRadioButtonMenuItem("Mean Spectrum");
		average.setMnemonic(KeyEvent.VK_M);
		average.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
				java.awt.event.ActionEvent.CTRL_MASK));
		average.addActionListener(viewStyleListener);
		viewGroup.add(average);
		menu.add(average);

		maximum = new JRadioButtonMenuItem("Top Tenth per Channel");
		maximum.setMnemonic(KeyEvent.VK_T);
		maximum.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				java.awt.event.ActionEvent.CTRL_MASK));
		maximum.addActionListener(viewStyleListener);
		viewGroup.add(maximum);
		menu.add(maximum);

		// SEPARATOR
		menu.addSeparator();

		final JMenuItem raw, fittings;

		raw = new JCheckBoxMenuItem("Raw Data Outline");
		raw.setMnemonic(KeyEvent.VK_O);
		raw.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
				controller.setShowRawData(menuitem.isSelected());
			}

		});
		menu.add(raw);

		fittings = new JCheckBoxMenuItem("Individual Fittings");
		fittings.setSelected(controller.getShowIndividualSelections());
		fittings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				JCheckBoxMenuItem menuitem = (JCheckBoxMenuItem) e.getSource();
				controller.setShowIndividualSelections(menuitem.isSelected());
			}

		});
		menu.add(fittings);

		// SELECT Menu
		ActionListener selectMenuListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String command = e.getActionCommand();

				if (command == "Map Visible Fittings") {
					actionMap();
				}

			}

		};

		menu = new JMenu("Mapping");
		menu.setMnemonic(KeyEvent.VK_S);

		mapMenuItem = new JMenuItem("Map Visible Fittings");
		mapMenuItem.setMnemonic(KeyEvent.VK_M);
		mapMenuItem.addActionListener(selectMenuListener);
		mapMenuItem.setEnabled(false); // not until we have data and fittings
		menu.add(mapMenuItem);

		menuBar.add(menu);

		frame.setJMenuBar(menuBar);

		controller.addListener(new PeakabooSimpleListener() {

			public void change() {

				raw.setSelected(controller.getShowRawData());
				fittings.setSelected(controller.getShowIndividualSelections());

				logPlot.setSelected(controller.getViewLog());
				axes.setSelected(controller.getShowAxes());
				monochrome.setSelected(controller.getMonochrome());

				switch (controller.getChannelCompositeType()) {

				case NONE:
					individual.setSelected(true);
					break;
				case AVERAGE:
					average.setSelected(true);
					break;
				case MAXIMUM:
					maximum.setSelected(true);
					break;

				}

			}
		});

	}

	private JPanel createBottomBar() {
		bottomPanel = new ClearPanel();
		bottomPanel.setBorder(Spacing.bTiny());

		bottomPanel.setLayout(new BorderLayout());

		channelLabel.setBorder(Spacing.bSmall());
		bottomPanel.add(channelLabel, BorderLayout.CENTER);

		zoomPanel = createZoomPanel();
		bottomPanel.add(zoomPanel, BorderLayout.EAST);

		scanSelector = new ClearPanel();
		scanSelector.setLayout(new BoxLayout(scanSelector, BoxLayout.X_AXIS));

		scanNo = new JSpinner();
		scanNo.getEditor().setPreferredSize(new Dimension(50, 0));
		scanLabel = new JLabel("Scan");
		scanLabel.setBorder(Spacing.bSmall());
		scanBlock = new ComplexToggle("discard-scan", "", "Flag this scan as bad to exclude it from the data set");

		scanSelector.add(scanLabel);
		scanSelector.add(Box.createHorizontalStrut(2));
		scanSelector.add(scanNo);
		scanSelector.add(Box.createHorizontalStrut(4));
		scanSelector.add(scanBlock);
		scanSelector.add(Box.createHorizontalStrut(4));

		scanNo.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSpinner scan = (JSpinner) e.getSource();
				int value = (Integer) ((scan).getValue());
				controller.setScanNumber(value - 1);
			}
		});
		bottomPanel.add(scanSelector, BorderLayout.WEST);

		scanBlock.setFocusable(false);
		scanBlock.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				controller.setScanDiscarded(scanBlock.isSelected());
			}
		});
		

		return bottomPanel;

	}

	private JPanel createZoomPanel() {

		JPanel panel = new ClearPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton out = new ImageButton("zoom-out", "Zoom Out", Layout.IMAGE);
		JButton in = new ImageButton("zoom-in", "Zoom In", Layout.IMAGE);

		in.setMargin(Spacing.iNone());
		out.setMargin(Spacing.iNone());

		zoomSlider = new JSlider(10, 500);
		zoomSlider.setPaintLabels(false);
		zoomSlider.setPaintTicks(false);
		zoomSlider.setValue(100);
		Dimension prefSize = zoomSlider.getPreferredSize();
		prefSize.width /= 2;
		zoomSlider.setPreferredSize(prefSize);

		zoomSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				controller.setZoom(zoomSlider.getValue() / 100.0);
			}
		});
		out.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int current = zoomSlider.getValue();
				zoomSlider.setValue(current - 10);
			}
		});
		in.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int current = zoomSlider.getValue();
				zoomSlider.setValue(current + 10);
			}
		});

		panel.add(out);
		panel.add(zoomSlider);
		panel.add(in);

		return panel;

	}

	private void fullRedraw() {
		frame.getContentPane().validate();
		frame.repaint();
	}

	// prompts the user with a file selection dialogue
	// reads the returned file list, loads the related
	// data set, and returns it to the caller
	public List<String> openNewDataset() {

		JFileChooser chooser = new JFileChooser(controller
				.getDataSourceFolder());
		chooser.setMultiSelectionEnabled(true);

		chooser.setDialogTitle("Select Data Files to Open");

		// Note: source for ExampleFileFilter can be found in FileChooserDemo,
		// under the demo/jfc directory in the JDK.
		SimpleFileFilter filter = new SimpleFileFilter();
		filter.addExtension("xml");
		// filter.addExtension("_spectra.dat");
		filter.addExtension("zip");
		filter.setDescription("All Readable Formats");
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File[] filenameArray = chooser.getSelectedFiles();

			List<String> filenames = new ArrayList<String>(filenameArray.length);
			for (int i = 0; i < filenameArray.length; i++) {
				filenames.add(filenameArray[i].toString());
			}

			return filenames;

		} else {

			return null;

		}

	}

	public String getSavePreferencesFile() {

		return SimpleIODialogues.chooseFileSave(frame,
				"Save This Peakaboo Session", controller.getDataSourceFolder(),
				"peakaboo", "Saved Peakaboo Sessions");

	}

	public String getLoadPreferencesFile() {
		return SimpleIODialogues.chooseFileOpen(frame,
				"Select Peakaboo Session File", controller
						.getDataSourceFolder(), "peakaboo",
				"Saved Peakaboo Sessions");
	}

	/*
	 * ======================================== METHODS FOR HANDLING CANVAS
	 * EVENTS ========================================
	 */
	public void paintCanvasEvent(Graphics g) {

		controller.setImageWidth(canvas.getWidth());
		controller.setImageHeight(canvas.getHeight());

		g.setColor(new Color(1.0f, 1.0f, 1.0f));
		g.fillRect(0, 0, (int) controller.getImageWidth(), (int) controller
				.getImageHeight());

		controller.draw(g);

	}

	public void mouseMoveCanvasEvent(int x) {

		int channel = controller.channelFromCoordinate(x, canvas.getWidth());
		double energy = controller.getEnergyForChannel(channel);
		Pair<Double, Double> values = controller.getValueForChannel(channel);

		String sep = ",  ";

		if (values != null) {

			DecimalFormat fmtObj = new DecimalFormat("#######0.00");

			channelLabel.setText("View Type: "
					+ controller.getChannelCompositeType().toString()
					+ sep
					+ "Channel: "
					+ String.valueOf(channel)
					+ sep
					+ "Energy: "
					+ fmtObj.format(energy)
					+ sep
					+ "Value: "
					+ fmtObj.format(values.first)
					+ ((values.first.equals(values.second)) ? "" : sep
							+ "Unfiltered Value: "
							+ fmtObj.format(values.second)));

		} else {

			channelLabel.setText("View Type: "
					+ controller.getChannelCompositeType().toString() + sep
					+ "Channel: " + "-");
		}
	}

}
package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.Scheme;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.GObjGroup;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.synthesize.Synthesizer;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.packageparse.PackageParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JCheckBoxMenuItem;

/**
 * Visual Specification Language ee.ioc.cs.editor.editor.Editor main module
 * for combining separate logical units (Built in the
 * IconEditor) into a structured schema.
 * @author Aulo
 * @link http://vsledit.sourceforge.net
 * @version 1.0
 */
public class Editor
	extends JFrame {

	int objCount;
	int mouseX; // Mouse X coordinate.
	int mouseY; // Mouse Y coordinate.

	public MouseOps mListener;
	DrawingArea drawingArea;
	JMenuBar menuBar;
	JMenu menu;
	JMenu submenu;
	JMenuItem menuItem;
	JPanel infoPanel; // Panel for runtime information, mouse coordinates, selected objects etc.
	public JPanel mainPanel = new JPanel();
	JLabel posInfo; // Mouse position.
	VPackage vPackage;
	Palette palette;
	Scheme scheme;
	ConnectionList connections;
	ObjectList objects;
	GObj currentObj;
	GObj obj1;
	Port firstPort;
	Port currentPort;
	Connection currentCon;
	Dimension drawAreaSize = new Dimension(600, 500);

	public static final String WINDOW_TITLE = "Editor";

	/**
	 * Class constructor [1].
	 */
	public Editor() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initialize();
		validate();
	} // Editor

	/**
	 * Class constructor [2].
	 * @param fileName - package file name.
	 */
	public Editor(String fileName) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initialize();
		File file = new File(fileName);

		loadPackage(file);
	} // Editor

	/**
	 * Application initializer.
	 */
	public void initialize() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});

		scheme = new Scheme();
		objects = scheme.objects;
		connections = scheme.connections;
		mListener = new MouseOps(this);

		drawingArea = new DrawingArea();
		drawingArea.setBackground(Color.white);
		drawingArea.setGridVisible(getGridVisibility());
		drawingArea.setGridStep(getGridStep());

		infoPanel = new JPanel(new GridLayout(1, 2));
		posInfo = new JLabel();

		drawingArea.addMouseListener(mListener);

		drawingArea.addMouseMotionListener(mListener);
		drawingArea.setPreferredSize(drawAreaSize);
		JScrollPane areaScrollPane = new JScrollPane(drawingArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(areaScrollPane, BorderLayout.CENTER);

		infoPanel.add(posInfo);

		mainPanel.add(infoPanel, BorderLayout.SOUTH);
		posInfo.setText("-");
		makeMenu();

		getContentPane().add(mainPanel);

		Look look = new Look();

		look.setGUI(this);
		look.changeLayout(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEFAULT_LAYOUT));
	}

	/**
	 * Returns grid step from the properties file.
	 * @return int - grid step from the properties file.
	 */
	public int getGridStep() {
		String sGridStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP);
		int iGridStep = 10;

		if (sGridStep != null) {
			iGridStep = Integer.parseInt(sGridStep);
		}
		return iGridStep;
	} // getGridStep

	/**
	 * Check if the grid should be visible or not.
	 * @return boolean - grid visibility from the properties file.
	 */
	public boolean getGridVisibility() {
		String vis = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID);

		if (vis != null) {
			int v = Integer.parseInt(vis);

			if (v < 1) {
				return false;
			}
			else {
				return true;
			}
		}
		return false;
	} // getGridVisibility

	/**
	 * Build menu.
	 */
	public void makeMenu() {
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		menu = new JMenu(Menu.MENU_FILE);
		menu.setMnemonic(KeyEvent.VK_F);
		menuItem = new JMenuItem(Menu.SAVE_SCHEME, KeyEvent.VK_S);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuItem = new JMenuItem(Menu.LOAD_SCHEME, KeyEvent.VK_O);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(Menu.PRINT, KeyEvent.VK_P);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(Menu.EXIT, KeyEvent.VK_X);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_EDIT);
		menu.setMnemonic(KeyEvent.VK_E);
		// [Aulo] 06.01.2004 - Commented out the Clone menu item
		// as it is already defined in the object popup menu and
		// otherwise would require implementing all the object
		// popup items in the current menu as well.
		/*
		 menuItem = new JMenuItem("Clone", KeyEvent.VK_C);
		 menuItem.addActionListener(mListener);
		 menu.add(menuItem);
		 */menuItem = new JMenuItem(Menu.SELECT_ALL, KeyEvent.VK_A);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuItem = new JMenuItem(Menu.CLEAR_ALL, KeyEvent.VK_C);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuItem = new JCheckBoxMenuItem(Menu.GRID, getGridVisibility());
		menuItem.setMnemonic('G');
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_PACKAGE);
		menu.setMnemonic(KeyEvent.VK_P);

		menuItem = new JMenuItem(Menu.LOAD, KeyEvent.VK_L);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.CLOSE, KeyEvent.VK_C);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.INFO, KeyEvent.VK_I);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_SCHEME);
		menu.setMnemonic(KeyEvent.VK_S);

		menuItem = new JMenuItem(Menu.SPECIFICATION, KeyEvent.VK_S);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		/* menuItem = new JMenuItem("Planner");
		 menuItem.addActionListener(mListener);
		 menu.add(menuItem);

		 menuItem = new JMenuItem("Plan, compile, run");
		 menuItem.setActionCommand("Run");
		 menuItem.addActionListener(mListener);
		 menu.add(menuItem);*/

		// menu.setMnemonic(KeyEvent.VK_A);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_OPTIONS);
		menu.setMnemonic(KeyEvent.VK_O);

		submenu = new JMenu(Menu.MENU_LAYOUT);
		submenu.setMnemonic(KeyEvent.VK_L);
		menuItem = new JMenuItem(Look.LOOK_3D, KeyEvent.VK_3);
		menuItem.addActionListener(mListener);
		submenu.add(menuItem);

		menuItem = new JMenuItem(Look.LOOK_METAL, KeyEvent.VK_M);
		menuItem.addActionListener(mListener);
		submenu.add(menuItem);

		menuItem = new JMenuItem(Look.LOOK_MOTIF, KeyEvent.VK_M);
		menuItem.addActionListener(mListener);
		submenu.add(menuItem);

		menuItem = new JMenuItem(Look.LOOK_WINDOWS, KeyEvent.VK_W);
		menuItem.addActionListener(mListener);
		submenu.add(menuItem);

		menuItem = new JMenuItem(Menu.SETTINGS, KeyEvent.VK_S);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.add(submenu);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_HELP);
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);

		menuItem = new JMenuItem(Menu.DOCS, KeyEvent.VK_D);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(Menu.LICENSE, KeyEvent.VK_L);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuItem = new JMenuItem(Menu.ABOUT, KeyEvent.VK_A);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

	}

	class DrawingArea
		extends JPanel {

		private boolean showGrid = false;
		private int gridStep = 10;

		public void setGridStep(int i) {
			if (i > 0) {
				this.gridStep = i;
			}
		}

		public boolean isGridVisible() {
			return this.showGrid;
		}

		public void setGridVisible(boolean b) {
			this.showGrid = b;
			repaint();
		}

		protected void drawGrid(Graphics g) {
			g.setColor(Color.lightGray);
			for (int i = 0; i < getWidth(); i += gridStep) {
				// draw vertical lines
				g.drawLine(i, 0, i, getHeight());
				// draw horizontal lines
				g.drawLine(0, i, getWidth(), i);
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Connection rel;

			if (this.showGrid) {
				drawGrid(g);
			}
			GObj obj;
			boolean antiAliasing = isAntialiasingOn();

			ee.ioc.cs.vsle.graphics.Shape.setAntialiasing(antiAliasing);

			for (int i = 0; i < objects.size(); i++) {
				obj = (GObj) objects.get(i);
				obj.drawClassGraphics(g);
			}
			g.setColor(Color.blue);
			for (int i = 0; i < connections.size(); i++) {
				rel = (Connection) connections.get(i);
				rel.drawRelation(g);
			}

			if (firstPort != null) {
				currentCon.drawRelation(g);
				Point p = (Point) currentCon.breakPoints.get(currentCon.breakPoints.size() - 1);

				g.drawLine(p.x, p.y, mouseX, mouseY);
			}
			g.setColor(Color.black);
			if (currentObj != null) {
				// ee.ioc.cs.editor.vclass.PackageClass pClass = (ee.ioc.cs.editor.vclass.PackageClass)classes.get(currentObj.name);
				currentObj.drawClassGraphics(g);
			}

			if (mListener.state.equals(State.dragBox)) {
				g.setColor(Color.gray);
				g.drawRect(mListener.startX, mListener.startY, mouseX - mListener.startX, mouseY - mListener.startY);
			}
		}
	}

	/**
	 * Read the application properties file to see whether the objects
	 * should be drawn antialiased or not.
	 * @return boolean - antialiasing on/off.
	 */
	public boolean isAntialiasingOn() {
		int iAntiAliasing = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));

		if (iAntiAliasing < 1) {
			return false;
		}
		else {
			return true;
		}
	} // isAntialiasingOn

	/**
	 * Display information dialog to application user.
	 * @param title - information dialog title.
	 * @param text - text displayed in the information dialog.
	 */
	public void showInfoDialog(String title, String text) {
		JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Overridden so we can exit when window is closed
	 * @param e - Window Event.
	 */
	protected void processWindowEvent(WindowEvent e) {
		// super.processWindowEvent(e); // automatic closing disabled, confirmation asked instead.
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			exitApplication();
		}
	}

	/**
	 * Close application.
	 */
	public void exitApplication() {
		int confirmed = JOptionPane.showConfirmDialog(null, "Exit Application?", Menu.EXIT, JOptionPane.OK_CANCEL_OPTION);

		switch (confirmed) {
			case JOptionPane.OK_OPTION:
				System.exit(0);
				break;

			case JOptionPane.CANCEL_OPTION:
				break;
		}
	}

	/**
	 * Method for cloning objects, currently invoked either
	 * from the Edit menu Clone selection or from the Object popup
	 * menu Clone selection.
	 */
	public void cloneObjects() {
		ArrayList selected = objects.getSelected();
		// objCount = objects.size();
		ArrayList newObjects = new ArrayList();
		GObj obj;

		// clone every selected objects
		for (int i = 0; i < selected.size(); i++) {
			obj = (GObj) selected.get(i);
			GObj newObj = (GObj) obj.clone();

			newObj.setPosition(newObj.x + 20, newObj.y + 20);
			newObjects.add(newObj);
		}

		// some of these objects might have been groups, so ungroup everything
		ObjectList objects2 = new ObjectList();

		for (int i = 0; i < newObjects.size(); i++) {
			obj = (GObj) newObjects.get(i);
			objects2.addAll(obj.getComponents());
		}

		// now the hard part - we have to clone all the connections
		Connection con;
		GObj beginObj;
		GObj endObj;
		ConnectionList newConnections = new ConnectionList();

		for (int i = 0; i < connections.size(); i++) {
			con = (Connection) connections.get(i);
			int beginNum = con.beginPort.getNumber();
			int endNum = con.endPort.getNumber();

			beginObj = null;
			endObj = null;
			for (int j = 0; j < objects2.size(); j++) {
				obj = (GObj) objects2.get(j);
				if (obj.name.equals(con.beginPort.obj.name)) {
					beginObj = obj;
					if (endObj != null) {
						Port beginPort = (Port) beginObj.ports.get(beginNum);
						Port endPort = (Port) endObj.ports.get(endNum);

						beginPort.setConnected(true);
						endPort.setConnected(true);

						Connection con2 = new Connection(beginPort, endPort);
						Point p;

						for (int l = 0; l < con.breakPoints.size(); l++) {
							p = (Point) con.breakPoints.get(l);
							con2.addBreakPoint(new Point(p.x + 20, p.y + 20));
						}
						beginPort.addConnection(con2);
						endPort.addConnection(con2);
						newConnections.add(con2);
					}
				}
				if (obj.name.equals(con.endPort.obj.name)) {
					endObj = obj;
					if (beginObj != null) {
						Port beginPort = (Port) beginObj.ports.get(beginNum);
						Port endPort = (Port) endObj.ports.get(endNum);

						beginPort.setConnected(true);
						endPort.setConnected(true);
						Connection con3 = new Connection(beginPort, endPort);

						beginPort.addConnection(con3);
						endPort.addConnection(con3);
						newConnections.add(con3);
					}
				}
			}
		}
		connections.addAll(newConnections);
		for (int i = 0; i < connections.size(); i++) {
			con = (Connection) connections.get(i);
		}
		db.p("objcount on enne" + objCount);
		for (int i = 0; i < objects2.size(); i++) {
			obj = (GObj) objects2.get(i);
			obj.setName(obj.className + "_" + Integer.toString(objCount));
			objCount++;
		}
		db.p("objcount on " + objCount);
		for (int i = 0; i < selected.size(); i++) {
			obj = (GObj) selected.get(i);
			obj.setSelected(false);
		}
		objects.addAll(newObjects);
		repaint();
	}

	/**
	 * Draw object connections.
	 */
	public void drawConnections() {
		for (int i = 0; i < connections.size(); i++) {
			Connection con = (Connection) connections.get(i);

			con.drawRelation(drawingArea.getGraphics());
		}
	}

	/**
	 * Method for grouping objects.
	 */
	public void groupObjects() {
		ArrayList selected = objects.getSelected();
		GObj obj;

		for (int i = 0; i < selected.size(); i++) {
			obj = (GObj) selected.get(i);
			obj.setSelected(false);
		}
		GObjGroup og = new GObjGroup(selected);

		og.strict = true;
		og.setAsGroup(true);
		objects.removeAll(selected);
		objects.add(og);
		repaint();
		db.p(og);
	}

	/**
	 * Method for ungrouping objects.
	 */
	public void ungroupObjects() {
		GObj obj;

		for (int i = 0; i < objects.getSelected().size(); i++) {
			obj = (GObj) objects.getSelected().get(i);
			if (obj.isGroup()) {
				objects.addAll( ( (GObjGroup) obj).objects);
				objects.remove(obj);
				obj = null;
				currentObj = null;
			}
		}
		repaint();
	}

	/**
	 * Method for deleting selected objects.
	 */
	public void deleteObjects() {
		GObj obj;

		connections.removeAll(currentObj.getConnections());
		objects.remove(currentObj);
		currentObj = null;
		ArrayList removable = new ArrayList();

		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			if (obj.isSelected()) {
				connections.removeAll(obj.getConnections());
				removable.add(obj);
			}
		}
		objects.removeAll(removable);
		repaint();
	}

	/**
	 * Get last file path used for loading or saving schema, package, etc.
	 * from / into a file.
	 * @return String - last used path from system properties.
	 */
	public static String getLastPath() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_PATH);
	}

	/**
	 * Get system documentation URL value.
	 * @return String - system documentation URL.
	 */
	public static String getSystemDocUrl() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DOCUMENTATION_URL);
	}

	/**
	 * Stores the last path used for loading or saving schema, package, etc.
	 * into system properties.
	 * @param path - last path used for loading or saving schema, package, etc.
	 */
	public static void setLastPath(String path) {
		if (path != null) {
			if (path.indexOf("/") > -1) {
				path = path.substring(0, path.lastIndexOf("/"));
			}
			else if (path.indexOf("\\") > -1) {
				path = path.substring(0, path.lastIndexOf("\\"));
			}
		}
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_PATH, path);
	}

	/**
	 * Upon platform, use OS-specific methods for opening the URL in required browser.
	 * @param url - URL to be opened in a browser. Capable of browsing
	 *              local documentation as well if path is given with file://
	 */
	public static void openInBrowser(String url) {
		try {
			// Check if URL is defined, otherwise there is no reason for opening
			// the browser in the first place.
			if (url != null && url.trim().length() > 0) {
				// Get OS type.
				String osType = getOsType();

				// Open URL with OS-specific methods.
				if (osType != null && osType.equalsIgnoreCase("Windows")) {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if Operating System type is Windows.
	 * @param osType - Operating System type.
	 * @return boolean - Operating System belongs to the Windows family or not.
	 */
	public static boolean isWin(String osType) {
		if (osType != null && osType.startsWith("Windows")) {
			return true;
		}
		return false;
	}

	/**
	 * Return operating system type. Uses isWin, isMac, isUnix
	 * methods for deciding on Os type and returns always the
	 * internally defined Os Type (WIN,MAC or UNIX).
	 * @return String - internally defined OS TYPE.
	 */
	public static String getOsType() {
		Properties sysProps = System.getProperties();

		try {
			if (sysProps != null) {
				String osType = sysProps.getProperty("os.name");

				if (isWin(osType)) {
					return "Windows";
				}
				else {
					return "NotWindows";
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Package loader.
	 * @param f - package file to be loaded.
	 */
	void loadPackage(File f) {
		if (f.exists()) {
			RuntimeProperties.packageDir = f.getParent() + File.separator;
			PackageParser pp = new PackageParser(f);

			vPackage = pp.getPackage();
			scheme.packageName = vPackage.name;
			palette = new Palette(vPackage, mListener, this);
			validate();
		}
		else {
			JOptionPane.showMessageDialog(null, "Cannot read file " + f, "Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Open application options dialog.
	 */
	public void openOptionsDialog() {
		OptionsDialog o = new OptionsDialog();

		o.setVisible(true);
	}

	/**
	 * Removes all objects.
	 */
	public void clearObjects() {
		objects.removeAll(objects);
		connections.removeAll(connections);
		repaint();
	}

	/**
	 * Returns the window width.
	 * @return - window height.
	 */
	private static final int getWinWidth() {
		return 650;
	}

	/**
	 * Returns the window height.
	 * @return int - window height.
	 */
	private static final int getWinHeight() {
		return 600;
	}

	/**
	 * Hilight ports of the object.
	 */
	public void hilightPorts() {
		ArrayList ps = currentObj.getPorts();

		for (int i = 0; i < ps.size(); i++) {
			Port p = (Port) ps.get(i);

			p.setHilighted(!p.isHilighted());
		}
		repaint();
	}

	/**
	 * Main method for module unit-testing.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {

		String directory = System.getProperty("user.dir") + System.getProperty("file.separator");

		RuntimeProperties.debugInfo = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEBUG_INFO));

		JFrame window;

		try {
			if (args.length > 0) {
				if (args[0].equals("-p")) {
					if (args.length == 3) {
						RuntimeProperties.packageDir = directory + args[2] + System.getProperty("file.separator");
					}
					else {
						RuntimeProperties.packageDir = directory;
					}
					Synthesizer synth = new Synthesizer();

					synth.parseFromCommandLine(args[1]);
				}
				else {
					// Esimeses hoos vaatame, kas moodulite fail on ette antud k�surealt.
					db.p(args[0] + " read from command line.");
					window = new Editor(directory + args[0]);
					window.setTitle(WINDOW_TITLE);
					window.setSize(getWinWidth(), getWinHeight());
					window.setVisible(true);
				}

			}
			else {
				// Kui k�surealt ei olnud ette antud, v�tame vaikev��rtuse application.properties failist.
				db.p("No module file name was given as the command line argument, reading the application.properties file.");
				String paletteFile = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PALETTE_FILE);

				if (paletteFile != null && paletteFile.trim().length() > 0) {
					// Leidsime vastava kirje.
					db.p("Found module file name " + paletteFile + " from the " + PropertyBox.APP_PROPS_FILE_NAME + ".properties file.");
					window = new Editor(directory + paletteFile);
				}
				else {
					// application.properties failis polnud vastavat kirjet vaikimisi laetava faili kohta.
					db.p("Module file name was not specified in command line nor in the application.properties file. Starting without.");
					RuntimeProperties.packageDir = directory;
					window = new Editor();
				}
				window.setTitle(WINDOW_TITLE);
				window.setSize(getWinWidth(), getWinHeight());
				window.setVisible(true);
			}
		}
		catch (Exception e) {
			window = new Editor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(getWinWidth(), getWinHeight());
			window.setVisible(true);
		}

		// log application executions, also making sure that the properties file is
		// available for writing (required by some of current application modules).
		RuntimeProperties.genFileDir = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_EXECUTED, new java.util.Date().toString());
	}

}

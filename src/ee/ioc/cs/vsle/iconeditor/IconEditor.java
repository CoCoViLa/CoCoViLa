package ee.ioc.cs.vsle.iconeditor;


import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.iconeditor.IconMouseOps;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.Scheme;
import ee.ioc.cs.vsle.graphics.Rect;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.graphics.Dot;
import ee.ioc.cs.vsle.graphics.Line;
import ee.ioc.cs.vsle.graphics.ShapeGroup;
import ee.ioc.cs.vsle.graphics.Oval;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.graphics.Arc;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.editor.Look;
import ee.ioc.cs.vsle.editor.OptionsDialog;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.State;
import ee.ioc.cs.vsle.util.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JCheckBoxMenuItem;


public class IconEditor extends JFrame {

	int mouseX, mouseY; // Mouse X and Y coordinates.
	int shapeCount;
	BoundingBox boundingbox;
	public IconMouseOps mListener;
	public static DrawingArea drawingArea;
	JMenuBar menuBar;
	JMenu menu;
	JMenu submenu;
	JMenu exportmenu;
	JMenuItem menuItem;
	JPanel infoPanel; // Panel for runtime information, mouse coordinates, selected objects etc.
	public JPanel mainPanel = new JPanel();
	JLabel posInfo; // Mouse position.
	VPackage vPackage;
	IconPalette palette;
	Scheme scheme;
	Dimension drawAreaSize = new Dimension(700, 500);
	ShapeGroup shapeList = new ShapeGroup(new ArrayList());
	Shape currentShape;
	ArrayList ports = new ArrayList();

	public static final String WINDOW_TITLE = "IconEditor";

	/**
	 * Class constructor [1].
	 */
	public IconEditor() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initialize();
		palette = new IconPalette(mListener, this);
		validate();
	}

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
		mListener = new IconMouseOps(this);

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
	 * Change layout immediately as the layout selection changes.
	 * @param selectedLayout - layout selected from the layouts menu for application layout.
	 */
	public void changeLayout(String selectedLayout) {
		if (selectedLayout.equals(Look.LOOK_WINDOWS)) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception uie) {
				uie.printStackTrace();
			}
		} else if (selectedLayout.equals(Look.LOOK_METAL)) {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception uie) {
				uie.printStackTrace();
			}
		} else if (selectedLayout.equals(Look.LOOK_MOTIF)) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception uie) {
				uie.printStackTrace();
			}
		} else if (selectedLayout.equals(Look.LOOK_3D)) {
			try {
				UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception uie) {
				uie.printStackTrace();
			}
		}
	}

	public boolean getGridVisibility() {
		String vis = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID);

		if (vis != null) {
			int v = Integer.parseInt(vis);

			if (v < 1) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public int getGridStep() {
		String sGridStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP);
		int iGridStep = 10;

		if (sGridStep != null) {
			iGridStep = Integer.parseInt(sGridStep);
		}
		return iGridStep;
	}

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

		exportmenu = new JMenu(Menu.EXPORT_MENU);
		exportmenu.setMnemonic(KeyEvent.VK_E);

		menuItem = new JMenuItem(Menu.XML, KeyEvent.VK_X);
		menuItem.addActionListener(mListener);
		exportmenu.add(menuItem);

		menu.add(exportmenu);

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

		menuItem = new JMenuItem(Menu.SELECT_ALL, KeyEvent.VK_A);
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

	/**
	 * Save shape to file in XML format.
	 */
	public void exportShapesToXML() {

		StringBuffer xmlBuffer = new StringBuffer();

		if (boundingbox != null) {
			xmlBuffer.append("<?xml version='1.0' encoding='utf-8'?>");
			xmlBuffer.append("\n");
			xmlBuffer.append("<drawing>");
			xmlBuffer = appendShapes(xmlBuffer);
			xmlBuffer = appendPorts(xmlBuffer);
			xmlBuffer.append("</drawing>");
			saveToFile(xmlBuffer.toString(), "xml");
		} else {
			JOptionPane.showMessageDialog(null, "Please define a bounding box.", "Bounding box undefined", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private StringBuffer appendShapes(StringBuffer buf) {
		buf.append("<graphics>");
		buf.append(boundingbox.toFile(0, 0));
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);

			if (!(shape instanceof BoundingBox)) {
				String shapeXML = shape.toFile(boundingbox.x, boundingbox.y);

				if (shapeXML != null) {
					buf.append(shapeXML);
				}
			}
		}
		buf.append("</graphics>");
		return buf;
	}

	private StringBuffer appendPorts(StringBuffer buf) {
		if (ports != null && ports.size() > 0) {
			buf.append("<ports>");
			for (int i = 0; i < ports.size(); i++) {
				IconPort p = (IconPort) ports.get(i);

				buf.append("<port name=\"");
				buf.append(p.getName());
				buf.append("\" x=\"");
				buf.append(p.getX() - boundingbox.x);
				buf.append("\" y=\"");
				buf.append(p.getY() - boundingbox.y);
				buf.append("\" portConnection=\"");
				if (p.isArea()) {
					buf.append("area");
				}
				buf.append("\" strict=\"");
				buf.append(p.isStrict());
				buf.append("\">");

				buf.append("<open>");
				buf.append("<graphics>");
				buf.append("<bounds x=\"-5\" y=\"-5\" width=\"10\" height=\"10\" />");
				buf.append("</graphics>");
				buf.append("</open>");
				buf.append("<closed>");
				buf.append("<graphics>");
				buf.append("<rect x=\"-3\" y=\"-3\" width=\"6\" height=\"6\" colour=\"0\" filled=\"true\" />");
				buf.append("</graphics>");
				buf.append("</closed>");
				buf.append("</port>");

			}
			buf.append("</ports>");
		}
		return buf;
	}

	public DrawingArea getDrawingArea() {
		return drawingArea;
	}

	class DrawingArea extends JPanel {

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

			if (this.showGrid) {
				drawGrid(g);
			}

			// joonistame koik listis olevad objektid ekraanile, see k�ib v�ga lihtsalt. tegelt v�iks j�rgneva panna
			// ka shapeListi meetodiks, st siin �tleks lihtsalt shapeList.draw(g), aga suurt vahet pole
			Shape shape;
			boolean antiAliasing = isAntialiasingOn();

			Shape.setAntialiasing(antiAliasing);

			for (int i = 0; i < shapeList.size(); i++) {
				shape = (Shape) shapeList.get(i);
				// 2 esimest parameetrit 0,0.0 on offset ehk palju me teda nihutame (oli vajalik kui shape on objekti graafika osa sest
				// siis peame arvestama ka objekti asukohta, antud juhul pole oluline, aga ma ei viitsi meetodeid �mber
				// kirjutada, tulevikus v�ib seda teha. Kolmas parameeter ehk 1 on suurenduskordaja
				if (g != null) {
					shape.draw(0, 0, 1f, 1f, g);
				}
			}

			IconPort port;

			for (int i = 0; i < ports.size(); i++) {
				port = (IconPort) ports.get(i);
				if (g != null) {
					port.draw(0, 0, 1, g);
				}
			}

			// That's all with drawing!
			// Look at classes Shape and ShapeList.

			/* We do not need this code, but we will keep it for reference.
			 Connection rel;

			 GObj obj;
			 for (int i = 0; i < objects.size(); i++) {
			 obj = (GObj)objects.get(i);
			 obj.drawClassGraphics(g);
			 }
			 g.setColor(Color.blue);
			 for (int i = 0; i < connections.size(); i++) {
			 rel = (Connection)connections.get(i);
			 rel.drawRelation(g);
			 }

			 if (firstPort != null) {
			 currentCon.drawRelation(g);
			 Point p = (Point)currentCon.breakPoints.get(currentCon.breakPoints.size()-1);
			 g.drawLine(p.x, p.y, mouseX, mouseY);
			 }
			 g.setColor(Color.black); */
			
			/*
			 if (currentShape != null) {
			 currentShape.drawClassGraphics(g);
			 } */

			if (mListener.state.equals(State.dragBox)) {
				Graphics2D g2 = (Graphics2D) g;

				g2.setColor(Color.gray);
				g2.setStroke(new BasicStroke((float) 1.0));
				g2.drawRect(mListener.startX, mListener.startY, mouseX - mListener.startX, mouseY - mListener.startY);
			} else {

				Graphics2D g2 = (Graphics2D) g;

				g2.setColor(mListener.color);
				g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4}, 0));
				final int width = Math.abs(mouseX - mListener.startX);
				final int height = Math.abs(mouseY - mListener.startY);

				if (mListener.state.equals(State.drawRect)) {
					g.drawRect(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
				} else if (mListener.state.equals(State.boundingbox)) {
					g.setColor(Color.darkGray);
					g.drawRect(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
				} else if (mListener.state.equals(State.drawFilledRect)) {

					g.fillRect(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
				} else if (mListener.state.equals(State.drawLine)) {
					g.drawLine(mListener.startX, mListener.startY, mouseX, mouseY);
				} else if (mListener.state.equals(State.drawOval)) {
					g.drawOval(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
				} else if (mListener.state.equals(State.drawFilledOval)) {
					g.fillOval(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
				} else if (mListener.state.equals(State.drawArc)) {
					g.drawArc(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height, 0, 180);
				} else if (mListener.state.equals(State.drawFilledArc)) {
					g.fillArc(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height, 0, 180);
				}

			}

		}
	}

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

		case JOptionPane.CANCEL_OPTION:
			break;
		}
	}

	/**
	 * Store application properties.
	 * @param propFile - properties file name (without an extension .properties).
	 * @param propName - property name to be saved.
	 * @param propValue - saved property value.
	 */
	public static void setProperty(String propFile, String propName,
	String propValue) {
		db.p(propFile + " " + propName + " " + propValue);
		// Read properties file.
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(propFile + ".properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		properties.put(propName, propValue);
		// Write properties file.
		try {
			properties.store(new FileOutputStream(propFile + ".properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes all objects.
	 */
	public void clearObjects() {
		shapeList.removeAll(shapeList);
		enableBoundingBoxButton(null);
		repaint();
	}

	/**
	 * Method for enabling bounding box button if no bounding
	 * box is defined after it was deleted.
	 * @param theShape - the shape deleted. If not a bounding box, then
	 *                   the bounding box might still exist and the button
	 *                   is not enabled.
	 */
	public void enableBoundingBoxButton(Shape theShape) {
		if (theShape instanceof BoundingBox) {
			palette.boundingbox.setEnabled(true);
			boundingbox = null;
		}
	}

	/**
	 * Get last file path used for loading or saving schema, package, etc.
	 * from / into a file.
	 * @return String - last used path from system properties.
	 */
	public static String getLastPath() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_PATH);
	}

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
			} else if (path.indexOf("\\") > -1) {
				path = path.substring(0, path.lastIndexOf("\\"));
			}
		}
		setProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_PATH, path);
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
		} catch (Exception e) {
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
				} else {
					return "NotWindows";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Open application options dialog.
	 */
	public void openOptionsDialog() {
		OptionsDialog o = new OptionsDialog();

		o.setVisible(true);
	}

	/**
	 * Method for deleting selected objects.
	 */
	public void deleteObjects() {
		Shape shape;

		shapeList.remove(currentShape);

		enableBoundingBoxButton(currentShape);

		currentShape = null;
		ArrayList removable = new ArrayList();

		for (int i = 0; i < shapeList.size(); i++) {
			shape = (Shape) shapeList.get(i);
			if (shape.isSelected()) {
				removable.add(shape);
			}
		}
		shapeList.removeAll(removable);
		repaint();
	}

	/**
	 * Method for grouping objects.
	 */
	public void groupObjects() {
		ArrayList selected = shapeList.getSelected();

		Shape shape;

		for (int i = 0; i < selected.size(); i++) {
			shape = (Shape) selected.get(i);
			shape.setSelected(false);
		}
		ShapeGroup sg = new ShapeGroup(selected);

		sg.strict = true;
		sg.setAsGroup(true);
		shapeList.removeAll(selected);
		shapeList.add(sg);
		repaint();
		db.p(sg);
	}

	/**
	 * Method for ungrouping objects.
	 */
	public void ungroupObjects() {
		Shape shape;

		for (int i = 0; i < shapeList.getSelected().size(); i++) {
			shape = (Shape) shapeList.getSelected().get(i);
			if (shape.isGroup()) {
				shapeList.addAll(((ShapeGroup) shape).shapes);
				shapeList.remove(shape);
				shape = null;
				currentShape = null;
			}
		}
		repaint();
	}

	public javax.swing.filechooser.FileFilter getFileFilter(final String format) {
		if (format != null && format.trim().length() > 0) {
			javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
				public String getDescription() {
					return format.toUpperCase() + " files (*." + format.toLowerCase() + ")";
				}

				public boolean accept(java.io.File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith("." + format.toLowerCase());
				}
			};

			return filter;
		}
		return null;
	}

	/**
	 * Saves any input string into a file.
	 * @param content - file content.
	 * @param format - file format (also the default file extension).
	 */
	public void saveToFile(String content, String format) {
		try {
			if (format != null) {
				format = format.toLowerCase();
			} else {
				throw new Exception("File format unspecified.");
			}
			JFileChooser fc = new JFileChooser(getLastPath());

			// [Aulo] 11.02.2004
			// Set custom file filter.
			fc.setFileFilter(getFileFilter(format));

			int returnVal = fc.showSaveDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				// [Aulo] 11.02.2004
				// Check if the file name ends with a required extension. If not,
				// append the default extension to the file name.
				if (!file.getAbsolutePath().toLowerCase().endsWith("." + format)) {
					file = new File(file.getAbsolutePath() + "." + format);
				}

				// store the last open directory in system properties.
				setLastPath(file.getAbsolutePath());
				boolean valid = true;

				// [Aulo] 04.01.2004
				// Check if file with a predefined name already exists.
				// If file exists, confirm file overwrite, otherwise leave
				// file as it is.
				if (file.exists()) {
					JOptionPane confirmPane = new JOptionPane();

					if (confirmPane.showConfirmDialog(null, "File exists.\nOverwrite file?", "Confirm Save", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
						valid = false;
					}
				}
				if (valid) {
					// Save scheme.
					try {
						FileOutputStream out = new FileOutputStream(new File(file.getAbsolutePath()));

						out.write(content.getBytes());
						out.flush();
						out.close();
						JOptionPane.showMessageDialog(null, "Saved to: " + file.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);

					} catch (Exception exc) {
						exc.printStackTrace();
					}

				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Verify from the properties file if the antialiasing should
	 * be on or off.
	 * @return boolean - anti-aliasing is on or off.
	 */
	public boolean isAntialiasingOn() {
		boolean antiAliasingOn = true;

		try {
			int iAntiAliasing = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));

			if (iAntiAliasing < 1) {
				antiAliasingOn = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return antiAliasingOn;
	} // isAntialiasingOn

	/**
	 * Sets all objects selected.
	 * @param b - select or deselect shapes.
	 */
	public void selectAllObjects(boolean b) {
		if (shapeList != null && shapeList.size() > 0) {
			for (int i = 0; i < shapeList.size(); i++) {
				Shape shape = (Shape) shapeList.get(i);

				shape.setSelected(b);
			}
			repaint();
		}
	} // selectAllObjects

	/**
	 * Clones the currently selected object.
	 */
	public void cloneObject() {
		Shape shape = null;

		if (currentShape instanceof Rect) {
			shape = new Rect(currentShape.getX(), currentShape.getY(), currentShape.width, currentShape.height, currentShape.getColor().getRGB(), currentShape.isFilled(), currentShape.getStrokeWidth(), currentShape.getTransparency());
		} else if (currentShape instanceof Oval) {
			shape = new Oval(currentShape.getX(), currentShape.getY(), currentShape.width, currentShape.height, currentShape.getColor().getRGB(), currentShape.isFilled(), currentShape.getStrokeWidth(), currentShape.getTransparency());
		} else if (currentShape instanceof Line) {
			shape = new Line(currentShape.getStartX(), currentShape.getStartY(), currentShape.getEndX(), currentShape.getEndY(), currentShape.getColor().getRGB(), currentShape.getStrokeWidth(), currentShape.getTransparency());
		} else if (currentShape instanceof Dot) {
			shape = new Dot(currentShape.getX(), currentShape.getY(), currentShape.getColor().getRGB(), currentShape.getStrokeWidth(), currentShape.getTransparency());
		} else if (currentShape instanceof Arc) {
			shape = new Arc(currentShape.getX(), currentShape.getY(), currentShape.width, currentShape.height, currentShape.getStartAngle(), currentShape.getArcAngle(), currentShape.getColor().getRGB(), currentShape.isFilled(), currentShape.getStrokeWidth(), currentShape.getTransparency());
		} else if (currentShape instanceof Text) {
			shape = new Text(currentShape.getX(), currentShape.getY(), currentShape.getFont(), currentShape.getColor(), currentShape.getTransparency(), currentShape.getText());
		}

		if (shape != null) {
			selectAllObjects(false);
			shape.setSelected(true);
			shape.x = shape.getX() + 5;
			shape.y = shape.getY() + 5;
			shapeList.add(shape);
			repaint();
		}
	} // cloneObject

	/**
	 * Returns the selected shape if any shapes selected,
	 * otherwise returns null. Called externally.
	 * @return Shape - selected shape.
	 */
	public Shape getSelectedShape() {
		if (shapeList != null && shapeList.size() > 0) {
			for (int i = 0; i < shapeList.size(); i++) {
				Shape s = (Shape) shapeList.get(i);

				if (s.isSelected()) {
					return s;
				}
			}
		}
		return null;
	} // getSelectedShape

	/**
	 * Main method for module unit-testing.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {

		String directory = System.getProperty("user.dir") + System.getProperty("file.separator");

		PropertyBox.APP_PROPS_FILE_PATH = directory;
		RuntimeProperties.debugInfo = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEBUG_INFO));
		JFrame window;

		try {
			RuntimeProperties.packageDir = directory;
			window = new IconEditor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(700, 600);
			window.setVisible(true);
		} catch (Exception e) {
			window = new IconEditor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(700, 600);
			window.setVisible(true);
		}

		// log application executions, also making sure that the properties file is
		// available for writing (required by some of current application modules).
		RuntimeProperties.genFileDir = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		setProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_EXECUTED, new java.util.Date().toString());

	}

}

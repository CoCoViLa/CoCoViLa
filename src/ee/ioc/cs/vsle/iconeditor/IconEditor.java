package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.iconeditor.IconMouseOps;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.*;

import javax.swing.*;

public class IconEditor
	extends JFrame {

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
	JLabel posInfo; // Label for displaying mouse position information.
	VPackage vPackage;
	IconPalette palette;
	Scheme scheme;
	Dimension drawAreaSize = new Dimension(700, 500);
	ShapeGroup shapeList = new ShapeGroup(new ArrayList());
	Shape currentShape;
	ArrayList ports = new ArrayList();
	IconKeyOps keyListener;

	public static final String WINDOW_TITLE = "IconEditor";
	public static boolean classParamsOk = false;
	public static boolean packageParamsOk = false;

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
		keyListener = new IconKeyOps(this);
		scheme = new Scheme();
		shapeList.addAll(scheme.objects);
		scheme.packageName = "IconEditor";
		mListener = new IconMouseOps(this);

		drawingArea = new DrawingArea();
		drawingArea.setBackground(Color.white);
		drawingArea.setGridVisible(getGridVisibility());

		// Initializes key listeners, for keyboard shortcuts.
		drawingArea.addKeyListener(keyListener);

		infoPanel = new JPanel(new GridLayout(1, 2));
		posInfo = new JLabel();

		drawingArea.addMouseListener(mListener);

		drawingArea.addMouseMotionListener(mListener);
		drawingArea.setPreferredSize(drawAreaSize);
		JScrollPane areaScrollPane = new JScrollPane(drawingArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.
			HORIZONTAL_SCROLLBAR_AS_NEEDED);

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(areaScrollPane, BorderLayout.CENTER);

		infoPanel.add(posInfo);

		mainPanel.add(infoPanel, BorderLayout.SOUTH);
		posInfo.setText("-");
		makeMenu();

		getContentPane().add(mainPanel);

		Look look = new Look();
		look.setGUI(this);
		look.changeLayout(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.DEFAULT_LAYOUT));
	}

	/**
	 * Move object with keys, executed by the KeyOps.
	 * @param moveX int - object x coordinate change.
	 * @param moveY int - object y coordinate change.
	 */
	public void moveObject(int moveX, int moveY) {
		moveX = moveX * RuntimeProperties.nudgeStep;
		moveY = moveY * RuntimeProperties.nudgeStep;
		for (int i = 0; i < shapeList.getSelected().size(); i++) {
			Shape s = (Shape) shapeList.getSelected().get(i);
			s.setPosition(moveX, moveY);
		}
		for (int i = 0; i < ports.size(); i++) {
			IconPort p = (IconPort) ports.get(i);
			if (p.isSelected()) {
				p.setPosition(moveX, moveY);
			}
		}
		repaint();
	} // moveObject

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
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(Menu.LOAD_SCHEME, KeyEvent.VK_O);
		menuItem.addActionListener(mListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		exportmenu = new JMenu(Menu.EXPORT_MENU);
		exportmenu.setMnemonic(KeyEvent.VK_E);

		menuItem = new JMenuItem(Menu.EXPORT_CLASS, KeyEvent.VK_C);
		menuItem.addActionListener(mListener);
		exportmenu.add(menuItem);

		menuItem = new JMenuItem(Menu.EXPORT_TO_PACKAGE, KeyEvent.VK_P);
		menuItem.addActionListener(mListener);
		exportmenu.add(menuItem);

		menu.add(exportmenu);

		menuItem = new JMenuItem(Menu.CREATE_PACKAGE, KeyEvent.VK_C);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(Menu.PRINT, KeyEvent.VK_P);
		menuItem.addActionListener(mListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_P, ActionEvent.CTRL_MASK));
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
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(Menu.CLEAR_ALL, KeyEvent.VK_C);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuItem = new JCheckBoxMenuItem(Menu.GRID, getGridVisibility());
		menuItem.setMnemonic('G');
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem(Menu.CLASS_PROPERTIES, KeyEvent.VK_P);
		menuItem.addActionListener(mListener);
		menu.add(menuItem);

		menuBar.add(menu);

		menu = new JMenu(Menu.MENU_OPTIONS);
		menu.setMnemonic(KeyEvent.VK_O);

		submenu = new JMenu(Menu.MENU_LAYOUT);
		submenu.setMnemonic(KeyEvent.VK_L);
		menuItem = new JMenuItem(Look.LOOK_CUSTOM, KeyEvent.VK_C);
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
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_J, ActionEvent.CTRL_MASK));
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

	public void fixShape() {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);
			if (shape.isSelected()) {
				shape.setFixed(!shape.isFixed());
			}
		}
	} // fixShape

	/**
	 * Returns XML representing shapes on the screen.
	 * @param appendXMLtag - append xml formatting or not.
	 * @return StringBuffer - XML representing shapes on the screen.
	 */
	public StringBuffer getShapesInXML(boolean appendXMLtag) {
		StringBuffer xmlBuffer = new StringBuffer();
		if (appendXMLtag) {
			xmlBuffer.append("<?xml version='1.0' encoding='utf-8'?>\n");
			xmlBuffer.append("\n");
		}
		xmlBuffer.append("<class");
		if (RuntimeProperties.classIsRelation) {
			xmlBuffer.append(" type=\"relation\"");
		} else {
			xmlBuffer.append(" type=\"class\"");
		}
		xmlBuffer.append(">\n");
		xmlBuffer.append("<name>" + RuntimeProperties.className + "</name>\n");
		xmlBuffer.append("<description>" + RuntimeProperties.classDescription + "</description>\n");
		String classIcon = RuntimeProperties.classIcon;
		if (classIcon != null && classIcon.lastIndexOf("/") >= 0) classIcon = classIcon.substring(classIcon.lastIndexOf("/") + 1);
		if (classIcon != null && classIcon.lastIndexOf("\\") >= 0) classIcon = classIcon.substring(classIcon.lastIndexOf("\\") + 1);
		xmlBuffer.append("<icon>" + classIcon + "</icon>\n");
		xmlBuffer = appendShapes(xmlBuffer);
		xmlBuffer = appendPorts(xmlBuffer);
		xmlBuffer = appendClassFields(xmlBuffer);
		xmlBuffer.append("</class>\n");

		return xmlBuffer;
	} // getShapesInXML

	public void createPackage() {
		PackagePropertiesDialog p = new PackagePropertiesDialog();
		p.setVisible(true);
		savePackage();
	} // createPackage

	private void savePackage() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\'1.0\' encoding=\'utf-8\'?>\n");
		sb.append("\n");
		sb.append("<!DOCTYPE package SYSTEM \"" + RuntimeProperties.packageDtd + "\">\n");
		sb.append("<package>\n");
		sb.append("<name>" + RuntimeProperties.packageName + "</name>\n");
		sb.append("<description>" + RuntimeProperties.packageDesc + "</description>\n");
		sb.append("</package>");
		if (this.packageParamsOk) {
			saveToFile(sb.toString(), "xml");
		}
	} // savePackage

	/**
	 * Save shape to file in XML format.
	 */
	public void exportShapesToXML() {
		classParamsOk = true;
		validateClassParams();
		if (classParamsOk) {
			StringBuffer xmlBuffer = new StringBuffer();

			if (boundingbox != null) {
				xmlBuffer = getShapesInXML(true);
				saveToFile(xmlBuffer.toString(), "xml");
			} else {
				JOptionPane.showMessageDialog(null, "Please define a bounding box.",
					"Bounding box undefined",
					JOptionPane.INFORMATION_MESSAGE);
			}
		}
	} // exportShapesToXML

	public void exportShapesToPackage() {
		classParamsOk = true;
		validateClassParams();
		if (classParamsOk) {
			if (boundingbox != null) {
				saveToPackage();
			} else {
				JOptionPane.showMessageDialog(null, "Please define a bounding box.", "Bounding box undefined", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	} // exportShapesToPackage

	private void validateClassParams() {
		if (RuntimeProperties.className == null ||
			RuntimeProperties.classDescription == null ||
			RuntimeProperties.classIcon == null ||
			(RuntimeProperties.className != null && RuntimeProperties.className.trim().length() == 0) ||
			(RuntimeProperties.classDescription != null && RuntimeProperties.classDescription.trim().length() == 0) ||
			(RuntimeProperties.classIcon != null && RuntimeProperties.classIcon.trim().length() == 0)) {
			new ClassPropertiesDialog(false);
		}
	} // validateClassParams

	private StringBuffer appendShapes(StringBuffer buf) {
		buf.append("<graphics>\n");
		if (boundingbox != null) buf.append(boundingbox.toFile(0, 0));
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);
			if (!(shape instanceof BoundingBox)) {
				String shapeXML = null;
				shapeXML = shape.toFile(boundingbox.x, boundingbox.y);

				if (shapeXML != null) buf.append(shapeXML);
			}
		}
		buf.append("</graphics>\n");
		return buf;
	} // appendShapes

	private StringBuffer appendPorts(StringBuffer buf) {
		if (ports != null && ports.size() > 0) {
			buf.append("<ports>\n");
			for (int i = 0; i < ports.size(); i++) {
				IconPort p = (IconPort) ports.get(i);

				buf.append("<port name=\"");
				buf.append(p.getName());
				buf.append("\" type=\"");
				buf.append(p.type);
				buf.append("\" x=\"");
				buf.append(p.getX() - boundingbox.x);

				buf.append("\" y=\"");
				buf.append(p.getY() - boundingbox.y);

				buf.append("\" portConnection=\"");

				if (p.isArea()) buf.append("area");
				buf.append("\" strict=\"");
				buf.append(p.isStrict());
				buf.append("\" />\n");
				/*
				if(!RuntimeProperties.classIsRelation) {
				  buf.append("<open>\n");
				  buf.append("<graphics>\n");
				  buf.append("<bounds x=\"-5\" y=\"-5\" width=\"10\" height=\"10\" />\n");
				  buf.append("</graphics>\n");
				  buf.append("</open>\n");
				  buf.append("<closed>\n");
				  buf.append("<graphics>\n");
				  buf.append("<rect x=\"-3\" y=\"-3\" width=\"6\" height=\"6\" colour=\"0\" filled=\"true\" />\n");
				  buf.append("</graphics>\n");
				  buf.append("</closed>\n");
				}
				buf.append("</port>\n");
				*/
			}
			buf.append("</ports>");
		}
		return buf;
	} // appendPorts

	public StringBuffer appendClassFields(StringBuffer buf) {
		ClassPropertiesDialog.removeEmptyRows();
		if (RuntimeProperties.dbrClassFields != null && RuntimeProperties.dbrClassFields.getRowCount() > 0) {
			buf.append("<fields>\n");
			for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
				String fieldName = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[0], i);
				String fieldType = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[1], i);
				String fieldValue = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[2], i);

				if (fieldType == null) fieldType = "";
				if (fieldValue == null) fieldValue = "";
				if (fieldValue.equals(""))
					buf.append("<field name=\"" + fieldName + "\" type=\"" + fieldType + "\"/>\n");
				else
					buf.append("<field name=\"" + fieldName + "\" type=\"" + fieldType + "\" value=\"" + fieldValue + "\" />\n");

			}
			buf.append("</fields>\n");
		}
		return buf;
	} // appendClassFields

	public void selectShapesInsideBox(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);
			if (shape.isInsideRect(x1, y1, x2, y2)) {
				shape.setSelected(true);
			}
		}
		for (int i = 0; i < ports.size(); i++) {
			IconPort port = (IconPort) ports.get(i);
			if (port.isInsideRect(x1, y1, x2, y2)) {
				port.setSelected(true);
			}
		}
	} // selectShapesInsideBox

	public Shape checkInside(int x, int y) {
		for (int i = shapeList.size() - 1; i >= 0; i--) {
			Shape shape = (Shape) shapeList.get(i);
			if (shape.contains(x, y)) {
				return shape;
			}
		}
		return null;
	}

	public DrawingArea getDrawingArea() {
		return drawingArea;
	}

	class DrawingArea extends JPanel {

		private boolean showGrid = false;

		public boolean isGridVisible() {
			return this.showGrid;
		}

		public void setGridVisible(boolean b) {
			this.showGrid = b;
			repaint();
		}

		protected void drawGrid(Graphics g) {
			g.setColor(Color.lightGray);
			for (int i = 0; i < getWidth(); i += RuntimeProperties.gridStep) {
				// draw vertical lines
				g.drawLine(i, 0, i, getHeight());
				// draw horizontal lines
				g.drawLine(0, i, getWidth(), i);
			}
		}

		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);

			if (this.showGrid) drawGrid(g2);

			if (RuntimeProperties.isAntialiasingOn) {
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			}

			for (int i = 0; i < shapeList.size(); i++) {
				Shape shape = (Shape) shapeList.get(i);
				//2 esimest parameetrit 0,0.0 on offset ehk palju me teda nihutame (oli vajalik kui shape on objekti graafika osa sest
				//siis peame arvestama ka objekti asukohta, antud juhul pole oluline, aga ma ei viitsi meetodeid �mber
				//kirjutada, tulevikus voib seda teha. Kolmas parameeter ehk 1 on suurenduskordaja
				if (g2 != null) {
					shape.draw(0, 0, 1f, 1f, g2);
				}
			}

			IconPort port;
			for (int i = 0; i < ports.size(); i++) {
				port = (IconPort) ports.get(i);
				if (g2 != null) {
					port.draw(0, 0, 1, g2);
				}
			}

			if (mListener.state.equals(State.dragBox)) {
				g2.setColor(Color.gray);
				g2.setStroke(new BasicStroke((float) 1.0));
				g2.drawRect(mListener.startX, mListener.startY, mouseX - mListener.startX, mouseY - mListener.startY);
			} else {

				int red = mListener.color.getRed();
				int green = mListener.color.getGreen();
				int blue = mListener.color.getBlue();

				int alpha = mListener.getTransparency();
				g2.setColor(new Color(red, green, blue, alpha));


				if (mListener.lineType > 0) {
					g2.setStroke(new BasicStroke((float) mListener.strokeWidth, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_ROUND, 50,
						new float[]{mListener.lineType, mListener.lineType}
						, 0));
				} else {
					g2.setStroke(new BasicStroke((float) mListener.strokeWidth));
				}

				final int width = Math.abs(mouseX - mListener.startX);
				final int height = Math.abs(mouseY - mListener.startY);

				if (!mListener.mouseState.equals("released")) {
					if (mListener.state.equals(State.drawRect)) {
						g2.drawRect(Math.min(mListener.startX, mouseX),
							Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.boundingbox)) {
						g2.setColor(Color.darkGray);
						g2.drawRect(Math.min(mListener.startX, mouseX),
							Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.drawFilledRect)) {
						g2.fillRect(Math.min(mListener.startX, mouseX),
							Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.drawLine)) {
						g2.drawLine(mListener.startX, mListener.startY, mouseX, mouseY);
					} else if (mListener.state.equals(State.drawOval)) {
						g2.drawOval(Math.min(mListener.startX, mouseX),
							Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.drawFilledOval)) {
						g2.fillOval(Math.min(mListener.startX, mouseX),
							Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.drawArc)) {
						g.drawRect(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
					} else if (mListener.state.equals(State.drawArc1)) {
						g.drawRect(mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight);
						g.drawLine(mListener.startX + mListener.arcWidth / 2,
							mListener.startY + mListener.arcHeight / 2,
							mouseX, mouseY);
					} else if (mListener.state.equals(State.drawArc2)) {
						if (mListener.fill) {
							g2.fillArc(mListener.startX, mListener.startY, mListener.arcWidth,
								mListener.arcHeight, mListener.arcStartAngle,
								mListener.arcAngle);

						} else {
							g2.drawArc(mListener.startX, mListener.startY, mListener.arcWidth,
								mListener.arcHeight, mListener.arcStartAngle,
								mListener.arcAngle);
						}
					} else if (mListener.state.equals(State.drawFilledArc)) {
						g.drawRect(Math.min(mListener.startX, mouseX), Math.min(mListener.startY, mouseY), width, height);
					}
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
		JOptionPane.showMessageDialog(null, text, title,
			JOptionPane.INFORMATION_MESSAGE);
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
		int confirmed = JOptionPane.showConfirmDialog(null,
			"Exit Application?",
			Menu.EXIT,
			JOptionPane.OK_CANCEL_OPTION);
		switch (confirmed) {
			case JOptionPane.OK_OPTION:
				System.exit(0);
			case JOptionPane.CANCEL_OPTION:
				break;
		}
	}


	/**
	 * Removes all objects.
	 */
	public void clearObjects() {
		mListener.state = State.selection;
		shapeList = new ShapeGroup(new ArrayList());
		ports = new ArrayList();
		palette.boundingbox.setEnabled(true);
		boundingbox = null;
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

	public static String getSystemDocUrl() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.DOCUMENTATION_URL);
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
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " +
						url);
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
	} // getOsType

	/**
	 * Open application options dialog.
	 */
	public void openOptionsDialog() {
		OptionsDialog o = new OptionsDialog();
		o.setVisible(true);
		repaint();
	} // openOptionsDialog

	public void saveScheme() {

		JFileChooser fc = new JFileChooser(getLastPath());
		CustomFileFilter txtFilter = new CustomFileFilter(CustomFileFilter.extensionTxt, CustomFileFilter.descriptionTxt);

		fc.setFileFilter(txtFilter);
		int returnVal = fc.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			// [Aulo] 11.02.2004
			// Check if the file name ends with a required extension. If not,
			// append the default extension to the file name.
			if (!file.getAbsolutePath().toLowerCase().endsWith(CustomFileFilter.extensionTxt)) {
				file = new File(file.getAbsolutePath() + "." + CustomFileFilter.extensionTxt);
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
					StringBuffer xml = new StringBuffer();

					xml.append(getGraphicsToString().toString());
					xml.append(getClassPropsToString().toString());

					FileOutputStream out = new FileOutputStream(new File(file.getAbsolutePath()));
					out.write(xml.toString().getBytes());
					out.flush();
					out.close();
					JOptionPane.showMessageDialog(null, "Saved to: " + file.getName(),
						"Saved",
						JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}

	} // saveScheme

	/**
	 * Load the previously saved scheme and display it on the drawing canvas.
	 */
	public void loadScheme() {
		JFileChooser fc = new JFileChooser(getLastPath());
		CustomFileFilter filter = new CustomFileFilter(CustomFileFilter.extensionTxt, CustomFileFilter.descriptionTxt);

		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			setLastPath(file.getAbsolutePath());
			try {
				mListener.state = State.selection;
				shapeCount = 0;
				shapeList = new ShapeGroup(new ArrayList());
				ports = new ArrayList();
				palette.boundingbox.setEnabled(true);
				loadGraphicsFromFile(file);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	} // loadScheme

	// Print the drawing canvas.
	public void print() {
		PrintUtilities.printComponent(getDrawingArea());
	} // print

	/**
	 * Method for deleting selected objects.
	 */
	public void deleteObjects() {
		shapeList.remove(currentShape);

		// Enable the bounding box button if we deleted a bounding box.
		if (boundingbox != null && boundingbox.isSelected()) {
			palette.boundingbox.setEnabled(true);
			boundingbox = null;
		}
		currentShape = null;

		// Remove all selected shapes.
		ArrayList removable = new ArrayList();
		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);
			if (shape.isSelected()) {
				removable.add(shape);
			}
		}
		shapeList.removeAll(removable);

		// Remove all selected ports.
		removable = new ArrayList();
		for (int i = 0; i < ports.size(); i++) {
			IconPort port = (IconPort) ports.get(i);
			if (port.isSelected()) {
				removable.add(port);
			}
		}
		ports.removeAll(removable);

		// Refresh the drawing canvas.
		repaint();
	} // deleteObjects

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
		shapeList.removeAll(selected);
		shapeList.add(sg);
		repaint();
	}

	/**
	 * Method for ungrouping objects.
	 */
	public void ungroupObjects() {
		Shape shape;
		for (int i = 0; i < shapeList.getSelected().size(); i++) {
			shape = (Shape) shapeList.getSelected().get(i);
			if (shape.getName() != null && shape.getName().startsWith("GROUP")) {
				shapeList.addAll(((ShapeGroup) shape).shapes);
				shapeList.remove(shape);
				shape = null;
				currentShape = null;
			}
		}
		repaint();
	}

	public static javax.swing.filechooser.FileFilter getFileFilter(final String format) {
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
					if (confirmPane.showConfirmDialog(null,
						"File exists.\nOverwrite file?",
						"Confirm Save",
						JOptionPane.OK_CANCEL_OPTION) ==
						JOptionPane.CANCEL_OPTION) {
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
						JOptionPane.showMessageDialog(null, "Saved to: " + file.getName(),
							"Saved",
							JOptionPane.INFORMATION_MESSAGE);

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
	 * Saves any input string into a file.
	 */
	public void saveToPackage() {

		try {

			// Package file chooser.
			JFileChooser fc = new JFileChooser(getLastPath());
			fc.setFileFilter(getFileFilter("xml"));
			fc.setDialogTitle("Choose package");

			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				// Check if the file name ends with a required extension. If not,
				// append the default extension to the file name.
				if (!file.getAbsolutePath().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}

				// store the last open directory in system properties.
				setLastPath(file.getAbsolutePath());

				try {
					// Read the contents of the package, escaping the package end that
					// will be appended later.
					BufferedReader in = new BufferedReader(new FileReader(file));
					String str;
					StringBuffer content = new StringBuffer();
					// Read file contents to be appended to.
					while ((str = in.readLine()) != null) {
						if (str.equalsIgnoreCase("</package>")) {
							break;
						} else {
							content.append(str + "\n");
						}
					}

					// File read, append the xml of current drawing.
					content.append(getShapesInXML(false));
					content.append("</package>");
					in.close();

					FileOutputStream out = new FileOutputStream(new File(file.getAbsolutePath()));
					out.write(content.toString().getBytes());
					out.flush();
					out.close();
					JOptionPane.showMessageDialog(null, "Saved to package: " + file.getName(), "Saved", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	} // saveToPackage

	/**
	 * Sets all objects selected or unselected, depending on the method parameter value.
	 * @param b - select or unselect shapes.
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
	 * Sets all ports selected or unselected, depending on the method parameter value.
	 * @param b boolean - select or unselect ports.
	 */
	public void selectAllPorts(boolean b) {
		if (ports != null && ports.size() > 0) {
			for (int i = 0; i < ports.size(); i++) {
				IconPort port = (IconPort) ports.get(i);
				port.setSelected(b);
			}
			repaint();
		}
	} // selectAllPorts

	/**
	 * Clones the currently selected object.
	 */
	public void cloneObject() {
		ShapeGroup sl = new ShapeGroup(new ArrayList());

		for (int i = 0; i < shapeList.size(); i++) {

			Shape shape = (Shape) shapeList.get(i);
			boolean isFixed = shape.isFixed();
			sl.add(shape);


			if (shape.isSelected()) {

				shape.setSelected(false);
				if (shape instanceof Rect) {
					shape = new Rect(shape.getX(), shape.getY(),
						shape.width, shape.height,
						shape.getColor().getRed(), shape.isFilled(), shape.getStrokeWidth(),
						shape.getTransparency(), shape.getLineType());
				} else if (shape instanceof Oval) {
					shape = new Oval(shape.getX(), shape.getY(),
						shape.width, shape.height,
						shape.getColor().getRGB(), shape.isFilled(),
						shape.getStrokeWidth(), shape.getTransparency(), shape.getLineType());
				} else if (shape instanceof Line) {
					shape = new Line(shape.getStartX(), shape.getStartY(),
						shape.getEndX(), shape.getEndY(),
						shape.getColor().getRGB(), shape.getStrokeWidth(),
						shape.getTransparency(), shape.getLineType());
				} else if (shape instanceof Dot) {
					shape = new Dot(shape.getX(), shape.getY(), shape.getColor().getRGB(),
						shape.getStrokeWidth(), shape.getTransparency());
				} else if (shape instanceof Arc) {
					shape = new Arc(shape.getX(), shape.getY(),
						shape.width, shape.height,
						shape.getStartAngle(), shape.getArcAngle(),
						shape.getColor().getRGB(), shape.isFilled(),
						shape.getStrokeWidth(), shape.getTransparency(), shape.getLineType());
				} else if (shape instanceof Text) {
					shape = new Text(shape.getX(), shape.getY(),
						shape.getFont(), shape.getColor(),
						shape.getTransparency(), shape.getText());
				}
				if (shape != null) {
					shape.setSelected(true);
					shape.x = shape.getX() + 5;
					shape.y = shape.getY() + 5;
					shape.setFixed(isFixed);
					sl.add(shape);
				}
			}

		}
		shapeList = sl;
		repaint();
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
				if (s.isSelected()) return s;
			}
		}
		return null;
	} // getSelectedShape

	/**
	 * Load graphics.
	 * @param f - package file to be loaded.
	 */
	public void loadGraphicsFromFile(File f) {
		try {
			emptyClassFields();
			BufferedReader in = new BufferedReader(new FileReader(f));
			String str;
			while ((str = in.readLine()) != null) {
				processShapes(str);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // loadGraphicsFromFile

	/**
	 * Empty the class fields DBResult.
	 */
	private void emptyClassFields() {
		if (RuntimeProperties.dbrClassFields != null && RuntimeProperties.dbrClassFields.getRowCount() > 0) {
			while (RuntimeProperties.dbrClassFields.getRowCount() > 0) {
				RuntimeProperties.dbrClassFields.removeRow(RuntimeProperties.dbrClassFields.getRowCount());
			}
		}
	} // emptyClassFields

	/**
	 * Returns a StringBuffer with all class properties for saving on a disk in text format.
	 * @return StringBuffer - class properties for saving on a disk in text format.
	 */
	public StringBuffer getClassPropsToString() {
		StringBuffer sb = new StringBuffer();

		// Add class name.
		if (RuntimeProperties.className != null && RuntimeProperties.className.trim().length() > 0) {
			sb.append("CLASSNAME:" + RuntimeProperties.className.trim() + "\n");
		}

		// Add class decription.
		if (RuntimeProperties.classDescription != null && RuntimeProperties.classDescription.trim().length() > 0) {
			sb.append("CLASSDESCRIPTION:" + RuntimeProperties.classDescription.trim() + "\n");
		}

		// Add class icon.
		if (RuntimeProperties.classIcon != null && RuntimeProperties.classIcon.trim().length() > 0) {
			sb.append("CLASSICON:" + RuntimeProperties.classIcon.trim() + "\n");
		}

		// Add boolean value representing if the class is a relation.
		sb.append("CLASSISRELATION:" + RuntimeProperties.classIsRelation + "\n");
		ClassPropertiesDialog.removeEmptyRows();
		// Add class fields.
		if (RuntimeProperties.dbrClassFields != null && RuntimeProperties.dbrClassFields.getRowCount() > 0) {
			for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
				String fieldName = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[0], i);
				String fieldType = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[1], i);
				String fieldValue = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[2], i);

				if (fieldType == null) fieldType = "";
				if (fieldValue == null) fieldValue = "";
				sb.append("CLASSFIELD:" + fieldName + ":" + fieldType + ":" + fieldValue + "\n");
			} // end for.
		} // end adding class fields.
		return sb;
	} // getClassPropsToString

	/**
	 * Returns a StringBuffer with all drawn class graphics for saving on a disk in text format.
	 * @return StringBuffer - class graphics for saving on a disk in text format.
	 */
	public StringBuffer getGraphicsToString() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < shapeList.size(); i++) {
			Shape shape = (Shape) shapeList.get(i);
			sb.append(shape.toText());
			sb.append("\n");
		} // end for.

		for (int i = 0; i < ports.size(); i++) {
			IconPort port = (IconPort) ports.get(i);
			sb.append(port.toText());
			sb.append("\n");
		} // end for.

		return sb;
	} // getGraphicsToString

	public void processShapes(String str) {

		if (str != null) {
			if (str.startsWith("LINE:")) {
				str = str.substring(5);
				int x1 = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y1 = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int x2 = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y2 = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int strokeW = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int lt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fixed = Boolean.valueOf(str).booleanValue();

				Line line = new Line(x1, y1, x2, y2, colorInt, strokeW, transp, lt);
				line.setFixed(fixed);
				shapeList.add(line);
			} else if (str.startsWith("ARC:")) {
				str = str.substring(4);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int width = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int height = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int startAngle = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int arcAngle = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fill = Boolean.valueOf(str.substring(0, str.indexOf(":"))).booleanValue();
				str = str.substring(str.indexOf(":") + 1);
				int strokeW = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int lt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fixed = Boolean.valueOf(str).booleanValue();

				Arc arc = new Arc(x, y, width, height, startAngle, arcAngle, colorInt, fill, strokeW, transp, lt);
				arc.setFixed(fixed);
				shapeList.add(arc);
			} else if (str.startsWith("BOUNDS:")) {
				str = str.substring(7);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int width = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int height = Integer.parseInt(str);
				BoundingBox b = new BoundingBox(x, y, width, height);
				this.boundingbox = b;
				shapeList.add(b);
				palette.boundingbox.setEnabled(false);
			} else if (str.startsWith("DOT:")) {
				str = str.substring(4);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int width = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int height = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int strokeW = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fixed = Boolean.valueOf(str).booleanValue();

				Dot dot = new Dot(x, y, colorInt, strokeW, transp);
				dot.setFixed(fixed);
				shapeList.add(dot);
			} else if (str.startsWith("OVAL:")) {
				str = str.substring(5);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int width = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int height = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fill = Boolean.valueOf(str.substring(0, str.indexOf(":"))).booleanValue();
				str = str.substring(str.indexOf(":") + 1);
				int strokeW = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int lt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fixed = Boolean.valueOf(str).booleanValue();

				Oval oval = new Oval(x, y, width, height, colorInt, fill, strokeW, transp, lt);
				oval.setFixed(fixed);
				shapeList.add(oval);
			} else if (str.startsWith("RECT:")) {
				str = str.substring(5);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int width = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int height = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fill = Boolean.valueOf(str.substring(0, str.indexOf(":"))).booleanValue();
				str = str.substring(str.indexOf(":") + 1);
				int strokeW = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int lt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean fixed = Boolean.valueOf(str).booleanValue();

				Rect rect = new Rect(x, y, width, height, colorInt, fill, strokeW, transp, lt);
				rect.setFixed(fixed);
				shapeList.add(rect);
			} else if (str.startsWith("TEXT:")) {
				str = str.substring(5);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int colorInt = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				String fontName = str.substring(0, str.indexOf(":"));
				str = str.substring(str.indexOf(":") + 1);
				String fontStyle = str.substring(0, str.indexOf(":"));
				str = str.substring(str.indexOf(":") + 1);
				int fontSize = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int transp = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);

				Font font = null;

				if (fontStyle.equalsIgnoreCase("0"))
					font = new Font(fontName, Font.PLAIN, fontSize);
				else if (fontStyle.equalsIgnoreCase("1"))
					font = new Font(fontName, Font.BOLD, fontSize);
				else if (fontStyle.equalsIgnoreCase("2")) font = new Font(fontName, Font.ITALIC, fontSize);
				if (font != null) {
					Text text = new Text(x, y, font, new Color(colorInt), transp, str);
					shapeList.add(text);
				}

			} else if (str.startsWith("PORT:")) {
				str = str.substring(5);
				int x = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				int y = Integer.parseInt(str.substring(0, str.indexOf(":")));
				str = str.substring(str.indexOf(":") + 1);
				boolean isAreaConn = Boolean.valueOf(str.substring(0, str.indexOf(":"))).booleanValue();
				str = str.substring(str.indexOf(":") + 1);
				boolean isStrict = Boolean.valueOf(str.substring(0, str.indexOf(":"))).booleanValue();
				str = str.substring(str.indexOf(":") + 1);

				IconPort port = new IconPort(str, x, y, isAreaConn, isStrict);
				ports.add(port);
			} else if (str.startsWith("CLASSNAME:")) {
				RuntimeProperties.className = str.substring(10);
			} else if (str.startsWith("CLASSDESCRIPTION:")) {
				RuntimeProperties.classDescription = str.substring(17);
			} else if (str.startsWith("CLASSICON:")) {
				RuntimeProperties.classIcon = str.substring(10);
			} else if (str.startsWith("CLASSISRELATION:")) {
				str = str.substring(16);
				if (str != null && str.equalsIgnoreCase("true") || str.equalsIgnoreCase("false")) {
					RuntimeProperties.classIsRelation = Boolean.valueOf(str).booleanValue();
				}
			} else if (str.startsWith("CLASSFIELD:")) {
				str = str.substring(11);
				String fieldName = str.substring(0, str.indexOf(":"));
				str = str.substring(str.indexOf(":") + 1);
				String fieldType = str.substring(0, str.indexOf(":"));
				str = str.substring(str.indexOf(":") + 1);
				String fieldValue = str;
				String[] classFields = {fieldName, fieldType, fieldValue};
				RuntimeProperties.dbrClassFields.appendRow(classFields);
			}
		}
		repaint();
	} // processShapes

	private static void initializeRuntimeProperties() {
		String directory = System.getProperty("user.dir") + System.getProperty("file.separator");

		PropertyBox.APP_PROPS_FILE_PATH = directory;
		RuntimeProperties.packageDir = directory;
		RuntimeProperties.zoomFactor = 100.0;
		RuntimeProperties.debugInfo = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEBUG_INFO));
		RuntimeProperties.gridStep = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP));
		RuntimeProperties.customLayout = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.CUSTOM_LAYOUT);
		RuntimeProperties.packageDtd = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PACKAGE_DTD);
		RuntimeProperties.genFileDir = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		RuntimeProperties.nudgeStep = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.NUDGE_STEP));
		;
		int aa = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));
		if (aa == 0) {
			RuntimeProperties.isAntialiasingOn = false;
		} else {
			RuntimeProperties.isAntialiasingOn = true;
		}
	} // initializeRuntimeProperties


	/**
	 * Main method for module unit-testing.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		initializeRuntimeProperties();

		JFrame window;

		try {
			window = new IconEditor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(775, 600);
			window.setVisible(true);
		} catch (Exception e) {
			window = new IconEditor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(775, 600);
			window.setVisible(true);
		}

		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.LAST_EXECUTED,
			new java.util.Date().toString());
	}

	public void zoom(double newZ, double oldZ) {
		for (int i = 0; i < shapeList.size(); i++) {
			Shape s = (Shape) shapeList.get(i);
			s.setMultSize((float) newZ, (float) oldZ);
		}
		for (int i = 0; i < ports.size(); i++) {
			IconPort p = (IconPort) ports.get(i);
			p.setMultSize((float) newZ, (float) oldZ);
		}
	} // zoom

} // end of class

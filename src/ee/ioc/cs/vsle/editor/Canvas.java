package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.packageparse.PackageParser;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.util.VMath;
import ee.ioc.cs.vsle.util.PrintUtilities;
import ee.ioc.cs.vsle.util.db;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.io.*;

/**
 */
public class Canvas extends JPanel implements ActionListener {
	int mouseX; // Mouse X coordinate.
	int mouseY; // Mouse Y coordinate.
	int objCount;
	VPackage vPackage;
	Palette palette;
	Scheme scheme;
	ConnectionList connections;
	ObjectList objects;
	GObj currentObj;
	Port firstPort;
	Port currentPort;
	Connection currentCon;
	public MouseOps mListener;
	public KeyOps keyListener;
	private boolean showGrid = false;
	Dimension drawAreaSize = new Dimension(600, 500);
	JPanel infoPanel;
	JLabel posInfo;
	DrawingArea drawingArea;


	public Canvas(File f) {
		super();
		if (f.exists()) {
			RuntimeProperties.packageDir = f.getParent() + File.separator;
			PackageParser pp = new PackageParser(f);
			vPackage = pp.getPackage();
			Scheme scheme = new Scheme();
			scheme.packageName = vPackage.name;
			initialize();
			palette = new Palette(vPackage, mListener, this);
			validate();
		} else {
			JOptionPane.showMessageDialog(null, "Cannot read file " + f, "Error",
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	void initialize() {
		scheme = new Scheme();
		objects = scheme.objects;
		connections = scheme.connections;
		mListener = new MouseOps(this);
        keyListener = new KeyOps(this);
		drawingArea = new DrawingArea();
		drawingArea.setBackground(Color.white);
		setGridVisible(getGridVisibility());
		drawingArea.setFocusable(true);
		infoPanel = new JPanel(new GridLayout(1, 2));
		posInfo = new JLabel();
		drawingArea.addMouseListener(mListener);
		drawingArea.addMouseMotionListener(mListener);
		drawingArea.setPreferredSize(drawAreaSize);

		// Initializes key listeners, for keyboard shortcuts.
		drawingArea.addKeyListener(keyListener);

		infoPanel = new JPanel(new GridLayout(1, 2));
		posInfo = new JLabel();

		// Initializes key listeners, for keyboard shortcuts.

		JScrollPane areaScrollPane = new JScrollPane(drawingArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setLayout(new BorderLayout());
		add(areaScrollPane, BorderLayout.CENTER);
		infoPanel.add(posInfo);
		add(infoPanel, BorderLayout.SOUTH);
		posInfo.setText("-");
	}

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
			} else {
				return true;
			}
		}
		return false;
	} // getGridVisibility


	public void stopRelationAdding() {
		currentObj = null;
		currentPort = null;
		currentCon = null;
		if (firstPort != null) {
			if (firstPort.connections.size() < 1)
				firstPort.setConnected(false);
			firstPort = null;
		}

		mListener.state = State.selection;
		Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(cursor);
		repaint();
	}

	/**
	 * Method for grouping objects.
	 */
	public void groupObjects() {
		ArrayList selected = objects.getSelected();
		if (selected.size() > 1) {
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
		}
	} // groupObjects

	/**
	 * Move object with keys, executed by the KeyOps.
	 * @param moveX int - object x coordinate change.
	 * @param moveY int - object y coordinate change.
	 */
	public void moveObject(int moveX, int moveY) {
		for (int i = 0; i < objects.getSelected().size(); i++) {
			GObj obj = (GObj) objects.getSelected().get(i);
			if (!(obj instanceof RelObj))
				obj.setPosition(obj.getX() + moveX, obj.getY() + moveY);

			// check if a strict port exists on the object

			if (obj.isStrict()) {
				Port port, port2;
				GObj obj2;
				ArrayList ports = obj.getPorts();

				for (int j = 0; j < ports.size(); j++) {
					port = (Port) ports.get(j);
					if (port.isStrict()) {
						port2 = port.getStrictConnected();
						// if the port is connected to another port, and they are not both selected, we might
						// wanna remove the connection
						if (port2 != null && !port2.obj.isSelected()) {
							// We dont want to remove the connection, if the objects belong to the same group
							if (!(obj.isGroup() && obj.includesObject(port2.obj))) {
								if (Math.abs(port.getRealCenterX() - port2.getRealCenterX()) > 1 || Math.abs(port.getRealCenterY() - port2.getRealCenterY()) > 1) {
									connections.remove(port, port2);
								}
							}
						}

						obj2 = objects.checkInside(port.obj.getX() + moveX + port.getCenterX(), port.obj.getY() + moveY + port.getCenterY(), obj);
						if (obj2 != null && !obj2.isSelected()) {
							port2 = obj2.portContains(port.obj.getX() + moveX + port.getCenterX(), port.obj.getY() + moveY + port.getCenterY());

							if (port2 != null && port2.isStrict()) {
								if (!port.isConnected()) {
									port.setConnected(true);
									port2.setConnected(true);
									Connection con = new Connection(port, port2);

									port2.addConnection(con);
									port.addConnection(con);
									connections.add(con);
								}
								obj.setPosition(port2.obj.x + port2.getCenterX() - ((port.obj.x - obj.x) + port.getCenterX()), port2.obj.y + port2.getCenterY() - ((port.obj.y - obj.y) + port.getCenterY()));
							}
						}
					}
				}
			}

			for (int j = 0; j < connections.size(); j++) {
				Connection relation = (Connection) connections.get(j);
				if (obj.includesObject(relation.endPort.obj) || obj.includesObject(relation.beginPort.obj)) {
					relation.calcBreakPoints();
				}
			}

		}
		objects.updateRelObjs();
		repaint();
	} // moveObject

	/**
	 * Method for ungrouping objects.
	 */
	public void ungroupObjects() {
		GObj obj;
		for (int i = 0; i < objects.getSelected().size(); i++) {
			obj = (GObj) objects.getSelected().get(i);
			if (obj.isGroup()) {
				objects.addAll(((GObjGroup) obj).objects);
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
		ArrayList removable = new ArrayList();
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			if (obj.isSelected()) {
				connections.removeAll(obj.getConnections());
				removable.add(obj);
			}
		}
		objects.removeAll(removable);
		objects.deleteExcessRels(connections);
		currentObj = null;
		repaint();

	}

	public void selectAllObjects() {
		GObj obj;
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			obj.setSelected(true);
		}
		repaint();
	} // selectAllObjects

	/**
	 * Removes all objects.
	 */
	public void clearObjects() {
		mListener.state = State.selection;
		objects.removeAll(objects);
		connections.removeAll(connections);
		repaint();
	}


	/**
	 * Method for cloning objects, currently invoked either
	 * from the Edit menu Clone selection or from the Object popup
	 * menu Clone selection.
	 */
	public void cloneObject() {
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
		for (int i = 0; i < objects2.size(); i++) {
			obj = (GObj) objects2.get(i);
			obj.setName(obj.className + "_" + Integer.toString(objCount));
			objCount++;
		}

		for (int i = 0; i < selected.size(); i++) {
			obj = (GObj) selected.get(i);
			obj.setSelected(false);
		}
		objects.addAll(newObjects);
		repaint();
	}

	/**
	 * Draw object connections.

	public void drawConnections() {
		for (int i = 0; i < connections.size(); i++) {
			Connection con = (Connection) connections.get(i);
			con.drawRelation(getGraphics());
		}
	}*/

	/**
	 * Hilight ports of the object.
	 */

	public void hilightPorts() {
		for (int i = 0; i < objects.getSelected().size(); i++) {
			GObj obj = (GObj) objects.getSelected().get(i);
			ArrayList ps = obj.getPorts();
			for (int port_index = 0; port_index < ps.size(); port_index++) {
				Port p = (Port) ps.get(port_index);
				p.setHilighted(!p.isHilighted());
			}
		}
		repaint();
	} // hilightPorts

	public void print() {
		PrintUtilities.printComponent(this);
	} // print

	public void loadScheme() {

	} // loadScheme

	public void saveScheme() {
		for (int i = 0; i < objects.size(); i++) {
			GObj obj = (GObj) objects.get(i);
			db.p(obj.toXML());
		}
		for (int i = 0; i < connections.size(); i++) {
			Connection con = (Connection) connections.get(i);
			db.p(con.toXML());
		}
	}


	/**
	 * Open the object properties dialog.
	 */
	public void openPropertiesDialog() {
		if (objects.getSelected().size() == 1) {
			ObjectPropertiesEditor prop = new ObjectPropertiesEditor((GObj) objects.getSelected().get(0), this);
			prop.pack();
			prop.setVisible(true);
		}
	} // openPropertiesDialog


	public boolean isGridVisible() {
		return this.showGrid;
	}

	public void setGridVisible(boolean b) {
		this.showGrid = b;
		repaint();
	}

	/**
	 * Open application options dialog.
	 */


	public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Menu.OBJECT_DELETE)) {
				deleteObjects();
			} else if (e.getActionCommand().equals(Menu.PROPERTIES)) {
				openPropertiesDialog();
			} else if (e.getActionCommand().equals(Menu.GROUP)) {
				groupObjects();
			} else if (e.getActionCommand().equals(Menu.UNGROUP)) {
				ungroupObjects();
			} else if (e.getActionCommand().equals(Menu.CLONE)) {
				cloneObject();
			} else if (e.getActionCommand().equals(Menu.HLPORTS)) {
				hilightPorts();
			} else if (e.getActionCommand().equals(Menu.GRID)) {
			    this.setGridVisible(!this.isGridVisible());
			}


		 else if (e.getActionCommand().equals(Menu.BACKWARD)) {
				// MOVE OBJECT BACKWARD IN THE LIST
				// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
				objects.sendBackward(currentObj, 1);
				repaint();
			} else if (e.getActionCommand().equals(Menu.FORWARD)) {
				// MOVE OBJECT FORWARD IN THE LIST
				// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
				objects.bringForward(currentObj, 1);
				repaint();
			} else if (e.getActionCommand().equals(Menu.TOFRONT)) {
				// MOVE OBJECT TO THE FRONT IN THE LIST,
				// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
				objects.bringToFront(currentObj);
				repaint();
			} else if (e.getActionCommand().equals(Menu.TOBACK)) {
				// MOVE OBJECT TO THE BACK IN THE LIST
				// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
				objects.sendToBack(currentObj);
				repaint();
			}
	}


	class DrawingArea extends JPanel {
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
		Connection rel;
		if (showGrid) drawGrid(g2);
		GObj obj;
		if (RuntimeProperties.isAntialiasingOn) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
				java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			obj.drawClassGraphics(g2);
		}
		g2.setColor(Color.blue);
		for (int i = 0; i < connections.size(); i++) {
			rel = (Connection) connections.get(i);
			rel.drawRelation(g2);
		}
		if (firstPort != null && mListener.state.equals(State.addRelation)) {
			currentCon.drawRelation(g2);
			Point p = (Point) currentCon.breakPoints.get(
				currentCon.breakPoints.size() - 1);
			g2.drawLine(p.x, p.y, mouseX, mouseY);
		} else if (firstPort != null && mListener.state.startsWith("??") && currentObj != null) {
			double angle = VMath.calcAngle(firstPort.getAbsoluteX(), firstPort.getAbsoluteY(), mouseX, mouseY);
			currentObj = (RelObj) currentObj;
			((RelObj) currentObj).angle = angle;
			currentObj.Xsize = (float) Math.sqrt(Math.pow((mouseX - firstPort.getAbsoluteX()) / (double) currentObj.width, 2.0) + Math.pow((mouseY - firstPort.getAbsoluteY()) / (double) currentObj.width, 2.0));
			currentObj.y = firstPort.getAbsoluteY();
			currentObj.x = firstPort.getAbsoluteX();
			currentObj.drawClassGraphics(g2);
		} else if (currentObj != null && !mListener.state.startsWith("?")) {
// ee.ioc.cs.editor.vclass.PackageClass pClass = (ee.ioc.cs.editor.vclass.PackageClass)classes.get(currentObj.name);
			g2.setColor(Color.black);
			currentObj.drawClassGraphics(g2);
		}
		if (mListener.state.equals(State.dragBox)) {
			g2.setColor(Color.gray);
			g2.drawRect(mListener.startX, mListener.startY,
				mouseX - mListener.startX, mouseY - mListener.startY);
		}
	}

	}

}

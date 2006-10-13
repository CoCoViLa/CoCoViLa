package ee.ioc.cs.vsle.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ee.ioc.cs.vsle.ccl.CCL;
import ee.ioc.cs.vsle.ccl.CompileException;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.packageparse.PackageParser;
import ee.ioc.cs.vsle.util.PrintUtilities;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.util.VMath;
import ee.ioc.cs.vsle.vclass.ClassPainter;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.GObjGroup;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.vclass.RelObj;
import ee.ioc.cs.vsle.vclass.Scheme;
import ee.ioc.cs.vsle.vclass.VPackage;

/**
 */
public class Canvas extends JPanel implements ActionListener {
	int mouseX; // Mouse X coordinate.
	int mouseY; // Mouse Y coordinate.
	private String workDir;
	int objCount;
	VPackage vPackage;
	Palette palette;
	Scheme scheme;
	ConnectionList connections;
	ObjectList objects;
    ArrayList<ClassPainter> classPainters;
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
    BufferedImage backgroundImage;
    ExecutorService executor;
    private float scale = 1.0f;
    private boolean enableClassPainter = true;

	public Canvas(File f) {
		super();
		if (f.exists()) {
			setWorkDir(f.getParent() + RuntimeProperties.FS);
			PackageParser pp = new PackageParser(f);
			vPackage = pp.getPackage();
			initialize();
			palette = new Palette(vPackage, this);
			m_canvasTitle = vPackage.getName();
			validate();
		} else {
			JOptionPane.showMessageDialog(this, "Cannot read file " + f, "Error",
				JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private String m_canvasTitle;
	
	public void setTitle( String title ) {
		m_canvasTitle = title;
	}
	
	public String getTitle() {
		return m_canvasTitle;
	}
	
    void initialize() {
		scheme = new Scheme();
		scheme.packageName = vPackage.getName();
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

		JScrollPane areaScrollPane = new JScrollPane(drawingArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setLayout(new BorderLayout());
		add(areaScrollPane, BorderLayout.CENTER);
		infoPanel.add(posInfo);
		add(infoPanel, BorderLayout.SOUTH);
		posInfo.setText("-");
        executor = Executors.newSingleThreadExecutor();
        
        if (vPackage.hasPainters()) {
            classPainters = new ArrayList<ClassPainter>();
            if (!createPainterPrototypes()) {
                JOptionPane.showMessageDialog(this,
                        "One or more errors occured. See the error log for details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
	}

    private boolean createPainterPrototypes() {
        boolean success = true;
        CCL classLoader = new CCL();
        classLoader.setCompileDir(workDir);
        try {
            classLoader.addURL(new File(vPackage.getPath()).getParentFile().toURL());
        } catch (MalformedURLException e1) {
            // TODO Clean up this class loading mess
            e1.printStackTrace();
        }
        
        for (PackageClass pclass : vPackage.classes) {
            if (pclass.painterName == null)
                continue;
            
            try {
                if (classLoader.compile2(pclass.painterName)) {
                    Class painterClass = classLoader.loadClass(pclass.painterName);
                    pclass.painterPrototype = (ClassPainter) painterClass.newInstance();
                } else {
                    success = false;
                }
            } catch (CompileException e) {
                success = false;
                e.printStackTrace();
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            }
        }
        return success;
    }
    
	/**
	 * Check if the grid should be visible or not.
	 * @return boolean - grid visibility from the properties file.
	 */
	public boolean getGridVisibility() {
		String vis = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID);
		if (vis != null) {
			int v = Integer.parseInt(vis);
			return v >= 1;
		}
		return false;
	} // getGridVisibility

	public VPackage getCurrentPackage() {
		return vPackage;
	}

	public void stopRelationAdding() {
		currentObj = null;
		currentPort = null;
		currentCon = null;
		if (firstPort != null) {
			if (firstPort.getConnections().size() < 1)
				firstPort.setConnected(false);
			firstPort = null;
		}

		mListener.setState(State.selection);
		drawingArea.repaint();
	}


    /**
	 * Method for grouping objects.
	 */
	public void groupObjects() {
		ArrayList<GObj> selected = objects.getSelected();
		if (selected.size() > 1) {
			GObj obj;
			for (int i = 0; i < selected.size(); i++) {
				obj = selected.get(i);
				obj.setSelected(false);
			}
			GObjGroup og = new GObjGroup(selected);
			og.strict = true;
			og.setAsGroup(true);
			objects.removeAll(selected);
			objects.add(og);
			drawingArea.repaint();
		}
	} // groupObjects

	/**
	 * Move object with keys, executed by the KeyOps.
	 * @param moveX int - object x coordinate change.
	 * @param moveY int - object y coordinate change.
	 */
	public void moveObject(int moveX, int moveY) {
		for (int i = 0; i < objects.getSelected().size(); i++) {
			GObj obj = objects.getSelected().get(i);
			if (!(obj instanceof RelObj))
				obj.setPosition(obj.getX() + moveX, obj.getY() + moveY);

			// check if a strict port exists on the object

			if (obj.isStrict()) {
				Port port, port2;
				GObj obj2;
				ArrayList<Port> ports = obj.getPorts();

				for (int j = 0; j < ports.size(); j++) {
					port = ports.get(j);
					if (port.isStrict()) {
						port2 = port.getStrictConnected();
						// if the port is connected to another port, and they are not both selected, we might
						// wanna remove the connection
						if (port2 != null && !port2.getObject().isSelected()) {
							// We dont want to remove the connection, if the objects belong to the same group
							if (!(obj.isGroup() && obj.includesObject(port2.getObject()))) {
								if (Math.abs(port.getRealCenterX() - port2.getRealCenterX()) > 1 || Math.abs(port.getRealCenterY() - port2.getRealCenterY()) > 1) {
									connections.remove(port, port2);
								}
							}
						}

						obj2 = objects.checkInside(port.getObject().getX() + moveX + port.getCenterX(), port.getObject().getY() + moveY + port.getCenterY(), obj);
						if (obj2 != null && !obj2.isSelected()) {
							port2 = obj2.portContains(port.getObject().getX() + moveX + port.getCenterX(), port.getObject().getY() + moveY + port.getCenterY());

							if (port2 != null && port2.isStrict()) {
								if (!port.isConnected()) {
									port.setConnected(true);
									port2.setConnected(true);
									Connection con = new Connection(port, port2);

									port2.addConnection(con);
									port.addConnection(con);
									connections.add(con);
								}
								obj.setPosition(port2.getObject().x + port2.getCenterX() - ((port.getObject().x - obj.x) + port.getCenterX()), port2.getObject().y + port2.getCenterY() - ((port.getObject().y - obj.y) + port.getCenterY()));
							}
						}
					}
				}
			}

			for (int j = 0; j < connections.size(); j++) {
				Connection relation = connections.get(j);
				if (obj.includesObject(relation.endPort.getObject()) || obj.includesObject(relation.beginPort.getObject())) {
					relation.calcEndBreakPoints();
				}
			}

		}
		objects.updateRelObjs();
		drawingArea.repaint();
	} // moveObject

	/**
	 * Method for ungrouping objects.
	 */
	public void ungroupObjects() {
		GObj obj;
		for (int i = 0; i < objects.getSelected().size(); i++) {
			obj = objects.getSelected().get(i);
			if (obj.isGroup()) {
				objects.addAll(((GObjGroup) obj).objects);
				objects.remove(obj);
				obj = null;
				currentObj = null;
			}
		}
		drawingArea.repaint();
	}

	/**
	 * Method for deleting selected objects.
	 */
	public void deleteObjects() {
		ArrayList<GObj> removableObjs = new ArrayList<GObj>();
		Connection con;
		for (int i = 0; i < connections.size(); i++) {
			con = connections.get(i);
			if (con.isSelected()) {
				connections.remove(con);
			}
		}
		GObj obj;
		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);
			if (obj.isSelected()) {
				connections.removeAll(obj.getConnections());
				removableObjs.add(obj);
			}
		}
		objects.removeAll(removableObjs);
		objects.deleteExcessRels(connections);
		currentObj = null;
		drawingArea.repaint();
	}

	public void selectAllObjects() {
		GObj obj;
		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);
			obj.setSelected(true);
		}
		drawingArea.repaint();
	} // selectAllObjects

	/**
	 * Removes all objects.
	 */
	public void clearObjects() {
		mListener.setState(State.selection);
		objects.removeAll(objects);
		connections.removeAll(connections);
		drawingArea.repaint();
	}


	/**
	 * Method for cloning objects, currently invoked either
	 * from the Edit menu Clone selection or from the Object popup
	 * menu Clone selection.
	 */
	public void cloneObject() {
		ArrayList<GObj> selected = objects.getSelected();
		// objCount = objects.size();
		ArrayList<GObj> newObjects = new ArrayList<GObj>();
		GObj obj;
		// clone every selected objects
		for (int i = 0; i < selected.size(); i++) {
			obj = selected.get(i);
			GObj newObj = obj.clone();
			newObj.setPosition(newObj.x + 20, newObj.y + 20);
			newObjects.add(newObj);
		}
		// some of these objects might have been groups, so ungroup everything
		ObjectList objects2 = new ObjectList();
		for (int i = 0; i < newObjects.size(); i++) {
			obj = newObjects.get(i);
			objects2.addAll(obj.getComponents());
		}

		GObj obj2;
		for (int i = 0; i < objects2.size(); i++) {
			obj = objects2.get(i);
			if (obj instanceof RelObj) {
				for (int j = 0; j < objects2.size(); j++) {
					obj2 = objects2.get(j);
					if (((RelObj) obj).startPort.getObject().getName().equals(obj2.getName()))
						((RelObj) obj).startPort.setObject( obj2 );
					if (((RelObj) obj).endPort.getObject().getName().equals(obj2.getName()))
						((RelObj) obj).endPort.setObject( obj2 );
				}
			}
		}


		// now the hard part - we have to clone all the connections
		Connection con;
		GObj beginObj;
		GObj endObj;
		ConnectionList newConnections = new ConnectionList();
		for (int i = 0; i < connections.size(); i++) {
			con = connections.get(i);
			int beginNum = con.beginPort.getNumber();
			int endNum = con.endPort.getNumber();
			beginObj = null;
			endObj = null;
			for (int j = 0; j < objects2.size(); j++) {
				obj = objects2.get(j);
				if (obj.getName().equals(con.beginPort.getObject().getName())) {
					beginObj = obj;
					if (endObj != null) {
						Port beginPort = beginObj.ports.get(beginNum);
						Port endPort = endObj.ports.get(endNum);
						beginPort.setConnected(true);
						endPort.setConnected(true);
						Connection con2 = new Connection(beginPort, endPort);
						Point p;
						for (int l = 0; l < con.breakPoints.size(); l++) {
							p = con.breakPoints.get(l);
							con2.addBreakPoint(new Point(p.x + 20, p.y + 20));
						}
						beginPort.addConnection(con2);
						endPort.addConnection(con2);
						newConnections.add(con2);
					}
				}
				if (obj.getName().equals(con.endPort.getObject().getName())) {
					endObj = obj;
					if (beginObj != null) {
						Port beginPort = beginObj.ports.get(beginNum);
						Port endPort = endObj.ports.get(endNum);
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
			con = connections.get(i);
		}
		for (int i = 0; i < objects2.size(); i++) {
			obj = objects2.get(i);
			obj.setName(obj.className + "_" + Integer.toString(objCount));
			objCount++;
		}

		for (int i = 0; i < selected.size(); i++) {
			obj = selected.get(i);
			obj.setSelected(false);
		}
		objects.addAll(newObjects);
		drawingArea.repaint();
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
			GObj obj = objects.getSelected().get(i);
			ArrayList<Port> ps = obj.getPorts();
			for (int port_index = 0; port_index < ps.size(); port_index++) {
				Port p = ps.get(port_index);
				p.setHilighted(!p.isHilighted());
			}
		}
		drawingArea.repaint();
	} // hilightPorts

	public void print() {
		PrintUtilities.printComponent(this);
	} // print

	public void loadScheme(File file) {
		scheme = SchemeLoader.getScheme(file, vPackage);
		if (scheme == null) {
			JOptionPane.showMessageDialog(this,
                    "See the error log for details.",
                    "Error loading scheme", JOptionPane.ERROR_MESSAGE);
			return;
		}
		connections = scheme.connections;
		objects = scheme.objects;
		mListener.setState(State.selection);
		drawingArea.repaint();
	} // loadScheme

	public void saveScheme(File file) {
		try {
			PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(file)));
			out.println("<?xml version='1.0' encoding='utf-8'?>");

			out.println("<!DOCTYPE scheme SYSTEM \"" + RuntimeProperties.SCHEME_DTD + "\">");
            out.println("<scheme package=\""+vPackage.getName()+"\">");
			for (int i = 0; i < objects.size(); i++) {
				GObj obj = objects.get(i);
				out.print(obj.toXML());
			}
			for (int i = 0; i < connections.size(); i++) {
				Connection con = connections.get(i);
				out.print(con.toXML());
			}
			out.println("</scheme>");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void saveSchemeAsClass(String classText) {
		ObjectPropertiesEditor prop = new ObjectPropertiesEditor(objects.getSelected().get(0), this);
		prop.pack();
		prop.setVisible(true);
		prop.setLocationRelativeTo(this);

	}

	/**
	 * Open the object properties dialog.
	 */
	public void openPropertiesDialog() {
		if (objects.getSelected().size() == 1) {
			ObjectPropertiesEditor prop = new ObjectPropertiesEditor(objects.getSelected().get(0), this);
			prop.pack();
			prop.setLocationRelativeTo(this);
			prop.setVisible(true);

		}
	} // openPropertiesDialog


	public boolean isGridVisible() {
		return this.showGrid;
	}

	public void setGridVisible(boolean b) {
		this.showGrid = b;
		drawingArea.repaint();
	}

	/**
	 * Open application options dialog.
	 * @param e - Action Event.
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
		} else if (e.getActionCommand().equals(Menu.BACKWARD)) {
			// MOVE OBJECT BACKWARD IN THE LIST
			// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
			objects.sendBackward(currentObj, 1);
			drawingArea.repaint();
		} else if (e.getActionCommand().equals(Menu.FORWARD)) {
			// MOVE OBJECT FORWARD IN THE LIST
			// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
			objects.bringForward(currentObj, 1);
			drawingArea.repaint();
		} else if (e.getActionCommand().equals(Menu.TOFRONT)) {
			// MOVE OBJECT TO THE FRONT IN THE LIST,
			// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
			objects.bringToFront(currentObj);
			drawingArea.repaint();
		} else if (e.getActionCommand().equals(Menu.TOBACK)) {
			// MOVE OBJECT TO THE BACK IN THE LIST
			// NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
			objects.sendToBack(currentObj);
			drawingArea.repaint();
		} else if (e.getActionCommand().equals(Menu.MAKECLASS)) {
			ClassSaveDialog csd = new ClassSaveDialog(((GObjGroup)currentObj).getSpec(connections), this);
            csd.pack();

			csd.setLocationRelativeTo(this);
			csd.setVisible(true);

		} else if (e.getActionCommand().equals(Menu.VIEWCODE)) {
            CodeViewer cv = new CodeViewer( currentObj.getClassName(), getWorkDir() );
			cv.setSize(550, 450);
			cv.setVisible(true);
		}
	}


	class DrawingArea extends JPanel {
		protected void drawGrid(Graphics g) {
            g.setColor(Color.lightGray);

            Rectangle vr = getVisibleRect();
            int step = RuntimeProperties.gridStep;
            int bx = vr.x + vr.width;
            int by = vr.y + vr.height;

            // draw vertical lines
            for (int i = (vr.x + step - 1) / step * step; i < bx; i += step)
                g.drawLine(i, vr.y, i, by);

            // draw horizontal lines
            for (int i = (vr.y + step - 1) / step * step; i < by; i += step)
				g.drawLine(vr.x, i, bx, i);
		}


        @Override
		protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);

            if (backgroundImage != null)
                g2.drawImage(backgroundImage, 0, 0, null);

            Connection rel;
			if (showGrid) drawGrid(g2);
			GObj obj;


			if (RuntimeProperties.isAntialiasingOn) {
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			}


			for (int i = 0; i < objects.size(); i++) {
				obj = objects.get(i);
				obj.drawClassGraphics(g2);
			}

			g2.setColor(Color.blue);
			for (int i = 0; i < connections.size(); i++) {
				rel = connections.get(i);
				rel.drawRelation(g2);
			}
            
            if (enableClassPainter && classPainters != null) {
                for (ClassPainter painter : classPainters)
                    painter.paint(g2, scale);
            }

            if (firstPort != null && mListener.state.equals(State.addRelation)) {
				currentCon.drawRelation(g2);
				Point p = currentCon.breakPoints.get(
					currentCon.breakPoints.size() - 1);
				g2.drawLine(p.x, p.y, mouseX, mouseY);
			} else if (firstPort != null && mListener.state.startsWith("??") && currentObj != null) {
				Point p = VMath.nearestPointOnRectangle
					(firstPort.getStartX(), firstPort.getStartY(),
						firstPort.getWidth(), firstPort.getHeight(), mouseX, mouseY);

				double angle = VMath.calcAngle(p.x, p.y, mouseX, mouseY);
				((RelObj) currentObj).angle = angle;
				currentObj.Xsize = (float) Math.sqrt(Math.pow((mouseX - p.x) / (double) currentObj.width, 2.0) + Math.pow((mouseY - p.y) / (double) currentObj.width, 2.0));
				currentObj.y = p.y;
				currentObj.x = p.x;
				currentObj.drawClassGraphics(g2);
			} else if (currentObj != null && !mListener.state.startsWith("?") && mListener.mouseOver) {
				g2.setColor(Color.black);
				currentObj.drawClassGraphics(g2);
			}
			if (mListener.state.equals(State.dragBox)) {
				g2.setColor(Color.gray);
                // a shape width negative height or width cannot be drawn
                int rectX = Math.min(mListener.startX, mouseX);
                int rectY = Math.min(mListener.startY, mouseY);
                int width = Math.abs(mouseX - mListener.startX);
                int height = Math.abs(mouseY - mListener.startY);
				g2.drawRect(rectX, rectY, width, height);
			}
		}
	}

    /**
     * Update mouse position in info label 
     */
    public void setPosInfo(int x, int y) {
        posInfo.setText(x + ", " + y);
    }

    /**
     * Sets the background image for the scheme. The preferred size of the
     * drawing area is incremented if necessary to fit the whole image.
     * The current image is removed when <code>image</code> is <code>null</code>.
     * @param image The image
     */
    public void setBackgroundImage(BufferedImage image) {        
        clearBackgroundImage();
        if (image != null) {
            backgroundImage = image;
            drawAreaSize.height = Math.max(image.getHeight(), drawingArea.getHeight());
            drawAreaSize.width = Math.max(image.getWidth(), drawingArea.getWidth());
            drawingArea.repaint(0, 0, image.getWidth(), image.getHeight());
            drawingArea.revalidate();
        }
    }

    /**
     * @return The current background image or <code>null</code> if there is no background image.
     */
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    /**
     * Removes the background image if one is set.
     * Resources will be freed and a <code>repaint()</code> of the image area is requested. 
     */
    public void clearBackgroundImage() {
        if (backgroundImage != null) {
            int width  = backgroundImage.getWidth();
            int height = backgroundImage.getHeight();
            backgroundImage.flush();
            backgroundImage = null;
            drawingArea.repaint(0, 0, width, height);
        }
    }

    /**
	 * @param workDir the workDir to set
	 */
	void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	/**
	 * @return the workDir
	 */
	String getWorkDir() {
		return workDir;
	}

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isEnableClassPainter() {
        return enableClassPainter;
    }

    public void setEnableClassPainter(boolean enableClassPainter) {
        this.enableClassPainter = enableClassPainter;
        drawingArea.repaint();
    }
    
    private HashSet<Long> m_runners = new HashSet<Long>();
    
    public void registerRunner( long id ) {
    	m_runners.add( id );
    }
    
    public void unregisterRunner( long id ) {
    	m_runners.remove( id );
    }
    
    public void destroy() {
    	for ( long id : m_runners ) {
			ProgramRunnerEvent event = new ProgramRunnerEvent( this, id, ProgramRunnerEvent.DESTROY );
			
			EventSystem.queueEvent( event );
		}
    	m_runners.clear();
    	drawingArea = null;
    }
}

package ee.ioc.cs.vsle.editor;

import java.io.*;
import java.util.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.iconeditor.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6.1.2004
 * Time: 23:18:00
 * To change this template use Options | File Templates.
 */
class MouseOps
	extends MouseInputAdapter
	implements ActionListener {

	Editor editor;
	int relCount;
	public Point draggedBreakPoint;
	public String state = "";

	// variable for object resize
	float initialXSize, initialYSize;
	int startX, startY;

	public MouseOps(Editor e) {
		this.editor = e;
	}

	public void setState(String state) {
		this.state = state;
	}

	void addObj() {
		editor.objects.add(editor.currentObj);
		editor.currentObj = null;
	}

	/**
	 * Mouse entered event from the MouseMotionListener. Invoked when the mouse enters a component.
	 * @param e MouseEvent - Mouse event performed.
	 */
	/*
	   public void mouseEntered(MouseEvent e) {
	   Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
	   editor.setCursor(cursor);
	   } */

	/**
	 * Mouse exited event from the MouseMotionListener. Invoked when the mouse exits a component.
	 * @param e MouseEvent - Mouse event performed.
	 */
	public void mouseExited(MouseEvent e) {
	//	Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

	//	editor.setCursor(cursor);
	}

	private void openObjectPopupMenu(int x, int y) {
		ObjectPopupMenu popupMenu = new ObjectPopupMenu(editor.mListener);

		popupMenu.show(editor.getContentPane(), x, y);

		if (editor.objects.getSelected().size() < 2) {
			if (editor.currentObj.isGroup()) {
				popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, true);
			} else {
				popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
			}
			popupMenu.enableDisableMenuItem(popupMenu.itemProperties, true);
			popupMenu.enableDisableMenuItem(popupMenu.itemGroup, false);
		} else {
			popupMenu.enableDisableMenuItem(popupMenu.itemProperties, false);
			popupMenu.enableDisableMenuItem(popupMenu.itemGroup, true);
			popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
		}
	}

	public void mouseClicked(MouseEvent e) {

		int x, y;

		x = e.getX();
		y = e.getY();

		if (SwingUtilities.isRightMouseButton(e)) {
			Connection relation = editor.connections.nearPoint(x, y);

			if (relation != null) {
				ConnectionPopupMenu popupMenu = new ConnectionPopupMenu(relation, editor.connections, editor.getContentPane());

				popupMenu.show(editor.getContentPane(), x, y);
			} else {
				editor.currentObj = editor.objects.checkInside(x, y);
				if (editor.currentObj != null) {
				/*	Port port = editor.currentObj.portContains(x, y);

					if (port != null) {
						PortPopupMenu popupMenu = new PortPopupMenu(port);

						popupMenu.show(editor.getContentPane(), x, y);
					} else {*/
						openObjectPopupMenu(x, y);
				//	}
				}

			}

			if (state.equals(State.magnifier)) {
				editor.drawAreaSize.width = (int) (editor.drawAreaSize.width + 0.5 * editor.drawAreaSize.width);
				editor.drawAreaSize.height = (int) (editor.drawAreaSize.height + 0.5 * editor.drawAreaSize.height);
				editor.drawingArea.setPreferredSize(editor.drawAreaSize);
				editor.objects.updateSize(0.5f, 0.5f);
				editor.connections.calcAllBreakPoints();
			}
		} // **********End of RIGHT mouse button controls**********************************************
		else {

			// **********Magnifier	Code************************
			if (state.equals(State.magnifier)) {
				editor.drawAreaSize.width = (int) (editor.drawAreaSize.width * 0.9);
				editor.drawAreaSize.height = (int) (editor.drawAreaSize.height * 0.9);
				editor.drawingArea.setPreferredSize(editor.drawAreaSize);
				editor.objects.updateSize(1.2f, 1.2f);
				editor.connections.calcAllBreakPoints();
			}

			// **********Relation adding code**************************
			if (state.equals(State.addRelation)) {
				GObj obj = editor.objects.checkInside(x, y);

				if (obj != null) {
					Port port = obj.portContains(x, y);

					if (port != null) {
						if (editor.firstPort == null) {
							editor.firstPort = port;
							editor.firstPort.setConnected(true);
							editor.currentCon = new Connection();
							editor.currentCon.addBreakPoint(new Point(editor.firstPort.getX() + editor.firstPort.obj.getX(), editor.firstPort.getY() + editor.firstPort.obj.getY()));
							editor.obj1 = obj;
							editor.mouseX = x;
							editor.mouseY = y;
						}else if  (canBeConnected(editor.firstPort, port)) {

							if (port == editor.firstPort) {
								editor.firstPort.setConnected(false);
								editor.firstPort = null;
								editor.currentCon = null;
							} else {
								port.setConnected(true);
								editor.currentCon.beginPort = editor.firstPort;
								editor.currentCon.endPort = port;
								editor.firstPort.addConnection(editor.currentCon);
								port.addConnection(editor.currentCon);
								editor.currentCon.addBreakPoint(new Point(port.getX() + port.obj.getX(), port.getY() + port.obj.getY()));
								editor.connections.add(editor.currentCon);
								editor.firstPort = null;
								editor.obj1 = null;
							}
						}
					}
				} else {
					if (editor.firstPort != null) {
						editor.currentCon.addBreakPoint(new Point(x, y));
						// firstPort.setConnected(false);
						// firstPort=null;
					}
				}

			} // **********Selecting objects code*********************
			else if (state.equals(State.selection)) {
				Connection con = editor.connections.nearPoint(x, y);

				if (con != null) {
					con.selected = true;
				} else {
					editor.connections.clearSelected();
				}
				GObj obj = editor.objects.checkInside(x, y);

				if (obj == null) {
					editor.objects.clearSelected();
				} else {
					if (e.isShiftDown()) {}
					else {
						editor.objects.clearSelected();
						obj.setSelected(true);
					}
				}

			} else {
				if(state.startsWith("??")) { // if class is of type relation
					addingSpecialRelation(y,x);
				} else 	if (editor.currentObj != null) {
					addObj();
					state = State.selection;
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);

					editor.setCursor(cursor);
				}
			}
		}

		editor.repaint();
	}

	private void addingSpecialRelation(int y, int x) {
		GObj obj = editor.objects.checkInside(x, y);
		if (obj != null) {
			Port port = obj.portContains(x, y);
			if (port != null) {
				if (editor.firstPort == null) {
					editor.firstPort = port;
					editor.firstPort.setConnected(true);
					editor.obj1 = obj;
					editor.mouseX = x;
					editor.mouseY = y;
				} else {
                    Port port1 = (Port)editor.currentObj.ports.get(0);
                    Port port2 = (Port)editor.currentObj.ports.get(1);
					Connection con = new Connection(editor.firstPort, port1);
					editor.firstPort.addConnection(con);
					port1.addConnection(con);
					editor.connections.add(con);
					con = new Connection(port2, port);
					port2.addConnection(con);
					port.addConnection(con);
					editor.connections.add(con);
					port.setConnected(true);
                    RelObj thisObj = (RelObj)editor.currentObj;
                    thisObj.startPort = editor.firstPort;
                    thisObj.endPort = port;
                    editor.firstPort = null;
                    addObj();
                    startAddingObject();
                    //setState(State.selection);
					editor.objects.updateRelObjs();

				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (state.equals(State.selection)) {
			editor.mouseX = e.getX();
			editor.mouseY = e.getY();
			Connection con = editor.connections.nearPoint(editor.mouseX, editor.mouseY);

			if (con != null) {
				draggedBreakPoint = con.breakPointContains(editor.mouseX, editor.mouseY);
				if (draggedBreakPoint != null) {
					state = State.dragBreakPoint;
				}
			} else {

				GObj obj = editor.objects.checkInside(editor.mouseX, editor.mouseY);

				if (obj != null) {
					if (e.isShiftDown()) {
						obj.setSelected(true);
					} else {
						if (!obj.isSelected()) {
							editor.objects.clearSelected();
							obj.setSelected(true);
						}
					}
					if (obj.controlRectContains(editor.mouseX, editor.mouseY) != 0) {
						initialXSize = obj.getXSize();
						initialYSize = obj.getYSize();
						state = State.resize;
					} else {
						state = State.drag;
						editor.repaint();
					}
				} else {
					state = State.dragBox;
					startX = editor.mouseX;
					startY = editor.mouseY;
				}
				// drawConnections();
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		GObj obj;
		Connection relation;

		ArrayList selectedObjs = editor.objects.getSelected();

		if (state.equals(State.dragBreakPoint)) {
			draggedBreakPoint.x = x;
			draggedBreakPoint.y = y;
			editor.repaint();
		}
		if (state.equals(State.drag)) {
			for (int i = 0; i < selectedObjs.size(); i++) {
				obj = (GObj) selectedObjs.get(i);
				if (!(obj instanceof RelObj))
				obj.setPosition(obj.getX() + (x - editor.mouseX), obj.getY() + (y - editor.mouseY));

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
								if (! (obj.isGroup() && obj.includesObject(port2.obj))) {
									if (Math.abs(port.getRealCenterX() - port2.getRealCenterX()) > 1 || Math.abs(port.getRealCenterY() - port2.getRealCenterY()) > 1) {
										editor.connections.remove(port, port2);
									}
								}
							}

							obj2 = editor.objects.checkInside(port.obj.getX() + (x - editor.mouseX) + port.getCenterX(), port.obj.getY() + (y - editor.mouseY) + port.getCenterY(), obj);
							if (obj2 != null && !obj2.isSelected()) {
								port2 = obj2.portContains(port.obj.getX() + (x - editor.mouseX) + port.getCenterX(), port.obj.getY() + (y - editor.mouseY) + port.getCenterY());

								if (port2 != null && port2.isStrict()) {
									if (!port.isConnected()) {
										port.setConnected(true);
										port2.setConnected(true);
										Connection con = new Connection(port, port2);

										port2.addConnection(con);
										port.addConnection(con);
										editor.connections.add(con);
									}
									obj.setPosition(port2.obj.x + port2.getCenterX() - ( (port.obj.x - obj.x) + port.getCenterX()), port2.obj.y + port2.getCenterY() - ( (port.obj.y - obj.y) + port.getCenterY()));
								}
							}
						}
					}
				}

				for (int j = 0; j < editor.connections.size(); j++) {
					relation = (Connection) editor.connections.get(j);
					if (obj.includesObject(relation.endPort.obj) || obj.includesObject(relation.beginPort.obj)) {
						relation.calcBreakPoints();
					}
				}

			}
			editor.objects.updateRelObjs();
			editor.mouseX = x;
			editor.mouseY = y;
			editor.repaint();
		}
		if (state.equals(State.dragBox)) {
			editor.mouseX = x;
			editor.mouseY = y;
			editor.repaint();
		}
		if (state.equals(State.resize)) {
			for (int i = 0; i < selectedObjs.size(); i++) {
				obj = (GObj) selectedObjs.get(i);
				float newXSize = (obj.getWidth() * initialXSize + (x - editor.mouseX)) / obj.getWidth();
				float newYSize = (obj.getHeight() * initialYSize + (y - editor.mouseY)) / obj.getHeight();

				if (newXSize > 0) {
					obj.setXSize(newXSize);
				}
				if (newYSize > 0) {
					obj.setYSize(newYSize);
				}

				for (int j = 0; j < editor.connections.size(); j++) {
					relation = (Connection) editor.connections.get(j);
					if (obj.includesObject(relation.endPort.obj) || obj.includesObject(relation.beginPort.obj)) {
						relation.calcBreakPoints();
					}
				}
			}
			editor.repaint();
			editor.drawConnections();
			editor.objects.updateRelObjs();
		}
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// update mouse position in info label
		editor.posInfo.setText(Integer.toString(x) + ", " + Integer.toString(y));

		// check if port needs to be nicely drawn coz of mouseover
		if (state.equals(State.addRelation)||state.startsWith("??")) {
			if (editor.currentPort != null) {
				editor.currentPort.setSelected(false);
				// currentPort=null;
				editor.repaint();
			}

			GObj obj = editor.objects.checkInside(x, y);

			if (obj != null) {
				Port port = obj.portContains(x, y);

				if (port != null) {
					if (editor.firstPort!=null) {
						if (canBeConnected(editor.firstPort, port)) {
							port.setSelected(true);
							editor.currentPort = port;
							editor.repaint();
						} else {
							port.setSelected(true);
							editor.currentPort = port;
							editor.repaint();
						}
					}
				}
			}
		}

		// if we're adding a new object...
		if (editor.currentObj != null && editor.vPackage.hasClass(state)) {
			editor.currentObj.x = x;
			editor.currentObj.y = y;

			// Kui objektil on moni strict port, chekime kas teda kuskile panna on;
			if (editor.currentObj.isStrict()) {
				Port port, port2;
				GObj obj;

				for (int i = 0; i < editor.currentObj.ports.size(); i++) {
					port = (Port) editor.currentObj.ports.get(i);
					port2 = port.getStrictConnected();
					if (port2 != null) {
						if (Math.abs(port.getRealCenterX() - port2.getRealCenterX()) > 1 || Math.abs(port.getRealCenterY() - port2.getRealCenterY()) > 1) {
							editor.connections.remove(port, port2);
						}
					}

					obj = editor.objects.checkInside(x + port.getCenterX(), y + port.getCenterY());
					if (obj != null) {
						port2 = obj.portContains(x + port.getCenterX(), y + port.getCenterY());

						if (port2 != null && port2.isStrict()) {
							if (!port.isConnected()) {
								port.setConnected(true);
								port2.setConnected(true);
								Connection con = new Connection(port, port2);

								port2.addConnection(con);
								port.addConnection(con);
								editor.connections.add(con);
							}
							editor.currentObj.x = port2.obj.x + port2.getCenterX() - port.getCenterX();
							editor.currentObj.y = port2.obj.y + port2.getCenterY() - port.getCenterY();
						}
					}
				}
			}

			Rectangle rect = new Rectangle(x - 10, y - 10, editor.currentObj.getRealWidth() + 10, editor.currentObj.getRealHeight() + 10);

			editor.drawingArea.scrollRectToVisible(rect);
			if (x + editor.currentObj.getRealWidth() > editor.drawAreaSize.width) {
				editor.drawAreaSize.width = x + editor.currentObj.getRealWidth();
				editor.drawingArea.setPreferredSize(editor.drawAreaSize);
				editor.drawingArea.setPreferredSize(editor.drawAreaSize);
			}

			if (y + editor.currentObj.getRealHeight() > editor.drawAreaSize.height) {
				editor.drawAreaSize.height = y + editor.currentObj.getRealHeight();
				editor.drawingArea.setPreferredSize(editor.drawAreaSize);
				editor.drawingArea.revalidate();
			}
			editor.repaint();
		} else if (state.startsWith("??") && editor.firstPort!=null) { //if class is of type relation
			editor.repaint();
		}
		if (editor.firstPort != null) {
			editor.mouseX = x;
			editor.mouseY = y;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (state.equals(State.dragBreakPoint)) {
			state = State.selection;
		}
		if (state.equals(State.drag)) {
			state = State.selection;
		}
		if (state.equals(State.resize)) {
			state = State.selection;
		}
		if (state.equals(State.dragBox)) {
			editor.objects.selectObjectsInsideBox(startX, startY, editor.mouseX, editor.mouseY);
			state = State.selection;
			editor.repaint();
		}
		if (editor.objects.getSelected() != null && editor.objects.getSelected().size() > 0) {
			String selObjects = editor.objects.getSelected().toString();

			if (selObjects != null) {
				selObjects = selObjects.replaceAll("null ", " ");
			}
			editor.posInfo.setText("Selection: " + selObjects);
		}
	}

	private boolean canBeConnected(Port firstPort, Port port) {
		if (firstPort.type.equals(port.type))
			return true;

		if ((port.isAny() || firstPort.isAny()) && !(port.isAny() || firstPort.isAny())) {
			return true;
		}
		if (port.type.equals("alias") && firstPort.type.substring(firstPort.type.length() - 2, firstPort.type.length()).equals("[]"))
			return true;
		if (firstPort.type.equals("alias") && port.type.substring(port.type.length() - 2, port.type.length()).equals("[]"))
			return true;

		return false;
	}


	// *********************************************************************
	// functions for mouse operations, to keep mouse* function somewhat tidy
	// *********************************************************************

	// *********************************************************************
	// end of mouse control functions
	// *********************************************************************

	public void actionPerformed(ActionEvent e) {

		// JmenuItem chosen
		if (e.getSource().getClass().getName() == "javax.swing.JMenuItem" ||
			e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem") {

			if (e.getActionCommand().equals(Menu.SAVE_SCHEME)) {
			  editor.saveScheme();
			} else if (e.getActionCommand().equals(Menu.LOAD_SCHEME)) {
			  editor.loadScheme();
			} else if (e.getActionCommand().equals(Menu.LOAD)) {
				JFileChooser fc = new JFileChooser(editor.getLastPath());
				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File pack = fc.getSelectedFile();

					editor.setLastPath(pack.getAbsolutePath());
					db.p("Loading package: " + pack.getName());
					editor.loadPackage(pack);
					editor.validate();
				}
			} else if (e.getActionCommand().equals(Menu.CLOSE)) {
				editor.vPackage = null;
				editor.palette.removeToolbar();
				editor.validate();
			} else if (e.getActionCommand().equals(Menu.INFO)) {
				String message;

				if (editor.vPackage != null) {
					message = editor.vPackage.description;
				} else {
					message = "No packages loaded";
				}
				JOptionPane.showMessageDialog(null, message);
			} else if (e.getActionCommand().equals(Menu.PRINT)) {
			  editor.print();
			} else if (e.getActionCommand().equals(Menu.EXIT)) {
				editor.exitApplication();
			} else if (e.getActionCommand().equals(Menu.GRID)) {
			  boolean isGridVisible = editor.drawingArea.isGridVisible();
			  if(isGridVisible) {
				isGridVisible = false;
			  } else {
				isGridVisible = true;
			  }
			  editor.drawingArea.setGridVisible(isGridVisible);
			} else if (e.getActionCommand().equals(Menu.CLEAR_ALL)) {
				editor.clearObjects();
			} else if (e.getActionCommand().equals(Menu.SPECIFICATION)) {
				ProgramTextEditor programEditor = new ProgramTextEditor(editor.connections, editor.objects, editor.vPackage);

				programEditor.setSize(550, 450);
				programEditor.setVisible(true);
			}
			/* else if (e.getActionCommand().equals("Planner")) {
				 PlannerEditor plannerEditor = new PlannerEditor(objects, connections);
				 plannerEditor.setSize(450, 260);
				 plannerEditor.setVisible(true);

				 } */
			else if (e.getActionCommand().equals(Menu.SELECT_ALL)) {
			  editor.selectAllObjects();
			} else if (e.getActionCommand().equals(Menu.CLONE)) {
				editor.cloneObject();
			} else if (e.getActionCommand().equals(Menu.HLPORTS)) {
				editor.hilightPorts();
			}
			/* else if (e.getActionCommand().equals("Run")) {
				 ee.ioc.cs.editor.editor.ResultsWindow resultsWindow= new ee.ioc.cs.editor.editor.ResultsWindow(objects, connections, classes);
				 resultsWindow.setSize(450, 260);
				 resultsWindow.setVisible(true);
				 }*/
			else if (e.getActionCommand().equals(Menu.DOCS)) {
				String documentationUrl = editor.getSystemDocUrl();

				if (documentationUrl != null && documentationUrl.trim().length() > 0) {
					editor.openInBrowser(documentationUrl);
				} else {
					editor.showInfoDialog("Missing information", "No documentation URL defined in properties.");
				}

			} else if (e.getActionCommand().equals(Menu.ABOUT)) {
				new AboutDialog(null,editor);
			} else if (e.getActionCommand().equals(Menu.LICENSE)) {
			  new LicenseDialog(null,editor);
			} else if (e.getActionCommand().equals(Menu.OBJECT_DELETE)) {
				editor.deleteObjects();
			} else if (e.getActionCommand().equals(Menu.PROPERTIES)) {
			  editor.openPropertiesDialog();
			} else if (e.getActionCommand().equals(Menu.GROUP)) {
				editor.groupObjects();
			} else if (e.getActionCommand().equals(Menu.UNGROUP)) {
				editor.ungroupObjects();
			} else if (e.getActionCommand().equals(Menu.SETTINGS)) {
				editor.openOptionsDialog();
			} else if (e.getActionCommand().equals(Look.LOOK_WINDOWS)) {
				try {
					Look.changeLayout(Look.LOOK_WINDOWS);
				} catch (Exception uie) {
				}
			} else if (e.getActionCommand().equals(Look.LOOK_METAL)) {
				try {
					Look.changeLayout(Look.LOOK_METAL);
				} catch (Exception uie) {
				}
			} else if (e.getActionCommand().equals(Look.LOOK_MOTIF)) {
				try {
					Look.changeLayout(Look.LOOK_MOTIF);
				} catch (Exception uie) {
				}
			} else if (e.getActionCommand().equals(Look.LOOK_CUSTOM)) {
				try {
					Look.changeLayout(Look.LOOK_CUSTOM);
				} catch (Exception uie) {
				}
			}
		}

		// Jbutton pressed
		if (e.getSource().getClass().getName() == "javax.swing.JButton") {
			if (e.getActionCommand().equals(State.relation)) {
				editor.mListener.setState(State.addRelation);
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);

				editor.setCursor(cursor);
			} else if (e.getActionCommand().equals(State.selection)) {
				Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);

				editor.setCursor(cursor);
				editor.mListener.setState(State.selection);
			} else if (e.getActionCommand().equals(State.magnifier)) {
				editor.mListener.setState(State.magnifier);
			} else {
				editor.mListener.setState(e.getActionCommand());
				startAddingObject();
			}
			editor.drawingArea.grabFocus();
		}
	}

	private void startAddingObject() {
		GObj obj;
		PackageClass pClass;

		if (state.startsWith("??")) {
			pClass = editor.vPackage.getClass(state.substring(2));
			obj = new RelObj(0, 0, pClass.graphics.getWidth(), pClass.graphics.getHeight(), pClass.toString());
		} else {
			pClass = editor.vPackage.getClass(state);
			obj = new GObj(0, 0, pClass.graphics.getWidth(), pClass.graphics.getHeight(), pClass.toString());
		}

		obj.ports = (ArrayList) pClass.ports.clone();
		Port port;

		for (int i = 0; i < obj.ports.size(); i++) {
			port = (Port) obj.ports.get(i);
			obj.ports.set(i, port.clone());
			port = (Port) obj.ports.get(i);
			port.setObject(obj);

			if (port.isStrict()) {
				obj.strict = true;
			}
			db.p("opg " + port.openGraphics);
			if (port.x + port.openGraphics.boundX < obj.portOffsetX1) {
				obj.portOffsetX1 = port.x + port.openGraphics.boundX;
			}

			if (port.y + port.openGraphics.boundY < obj.portOffsetY1) {
				obj.portOffsetY1 = port.y + port.openGraphics.boundY;
			}

			if (port.x + port.openGraphics.boundWidth > obj.width + obj.portOffsetX2) {
				obj.portOffsetX2 = Math.max((port.x + port.openGraphics.boundX + port.openGraphics.boundWidth) - obj.width, 0);
			}

			if (port.y + port.openGraphics.boundHeight > obj.height + obj.portOffsetY2) {
				obj.portOffsetY2 = Math.max((port.y + port.openGraphics.boundY + port.openGraphics.boundHeight) - obj.height, 0);
			}

			// deep clone port's connectionlist
			port.connections = (ArrayList) port.connections.clone();
		}
		// clone fields arraylist
		obj.fields = (ArrayList) pClass.fields.clone();
		// deep clone each separate field
		ClassField field;

		for (int i = 0; i < obj.fields.size(); i++) {
			field = (ClassField) obj.fields.get(i);
			obj.fields.set(i, field.clone());
		}

		obj.graphics = pClass.graphics;
		obj.setName(pClass.name + "_" + Integer.toString(editor.objCount));
		editor.objCount++;

		obj.graphics = pClass.graphics;
		editor.currentObj = obj;
		Cursor cursor;
		if (state.startsWith("??")) {
			cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		} else {
			cursor = new Cursor(Cursor.HAND_CURSOR);
		}


		editor.setCursor(cursor);
	}
}

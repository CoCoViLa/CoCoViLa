package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.graphics.Shape;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Mouse operations on Canvas.
 */
class MouseOps
	extends MouseInputAdapter
	implements ActionListener {

	ArrayList selectedObjs = new ObjectList();
	Canvas canvas;
	public Point draggedBreakPoint;
	public String state = "";
	int cornerClicked;

	int startX, startY;

	public MouseOps(Canvas e) {
		this.canvas = e;
	}


	public void setState(String state) {
		this.state = state;
		if (state.equals("selection"))
			canvas.currentObj = null;
	}

	void addObj() {
		canvas.objects.add(canvas.currentObj);
		canvas.currentObj = null;
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
		ObjectPopupMenu popupMenu = new ObjectPopupMenu(canvas);
		popupMenu.show(canvas, x, y);

		if (canvas.objects.getSelected().size() < 2) {
			if (canvas.currentObj.isGroup()) {
				popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, true);
			} else {
				popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
			}
			popupMenu.enableDisableMenuItem(popupMenu.itemProperties, true);
			popupMenu.enableDisableMenuItem(popupMenu.itemGroup, false);

			// Enable or disable order changing menu items.
			if (canvas.objects.indexOf(canvas.currentObj) == canvas.objects.size() - 1) {
				popupMenu.enableDisableMenuItem(popupMenu.itemForward, false);
				popupMenu.enableDisableMenuItem(popupMenu.itemToFront, false);
			} else {
				popupMenu.enableDisableMenuItem(popupMenu.itemForward, true);
				popupMenu.enableDisableMenuItem(popupMenu.itemToFront, true);
			}

			if (canvas.objects.indexOf(canvas.currentObj) == 0) {
				popupMenu.enableDisableMenuItem(popupMenu.itemBackward, false);
				popupMenu.enableDisableMenuItem(popupMenu.itemToBack, false);
			} else {
				popupMenu.enableDisableMenuItem(popupMenu.itemBackward, true);
				popupMenu.enableDisableMenuItem(popupMenu.itemToBack, true);
			}

		} else {
			popupMenu.enableDisableMenuItem(popupMenu.itemBackward, false);
			popupMenu.enableDisableMenuItem(popupMenu.itemForward, false);
			popupMenu.enableDisableMenuItem(popupMenu.itemToFront, false);
			popupMenu.enableDisableMenuItem(popupMenu.itemToBack, false);
			popupMenu.enableDisableMenuItem(popupMenu.itemGroup, true);
			popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
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
			//if right mouse button clicked, check whether
			Connection relation = canvas.connections.nearPoint(x, y);
			if (relation != null) {
				ConnectionPopupMenu popupMenu = new ConnectionPopupMenu(relation, canvas.connections, canvas);
				popupMenu.show(canvas, x, y);
			} else {
				canvas.currentObj = canvas.objects.checkInside(x, y);
				if (canvas.currentObj != null) {
					openObjectPopupMenu(x, y);
				}
			}

			if (state.equals(State.magnifier)) {
				canvas.drawAreaSize.width = (int) (canvas.drawAreaSize.width * 0.8);
				canvas.drawAreaSize.height = (int) (canvas.drawAreaSize.height * 0.8);
				canvas.drawingArea.setPreferredSize(canvas.drawAreaSize);
				canvas.objects.updateSize(0.8f, 0.8f);
				canvas.drawingArea.revalidate();
				canvas.connections.calcAllBreakPoints();
			}
		} // **********End of RIGHT mouse button controls**********************************************
		else {

			// **********Magnifier	Code************************
			if (state.equals(State.magnifier)) {
				canvas.drawAreaSize.width = (int) (canvas.drawAreaSize.width * 1.25);
				canvas.drawAreaSize.height = (int) (canvas.drawAreaSize.height * 1.25);
				canvas.drawingArea.setPreferredSize(canvas.drawAreaSize);
				canvas.drawingArea.revalidate();
				canvas.objects.updateSize(1.25f, 1.25f);
				canvas.connections.calcAllBreakPoints();

			}

			// **********Relation adding code**************************
			if (state.equals(State.addRelation)) {
				GObj obj = canvas.objects.checkInside(x, y);

				if (obj != null) {
					Port port = obj.portContains(x, y);

					if (port != null) {
						if (canvas.firstPort == null) {
							canvas.firstPort = port;
							canvas.firstPort.setConnected(true);
							canvas.currentCon = new Connection();
							canvas.currentCon.addBreakPoint(new Point(canvas.firstPort.getX() + canvas.firstPort.obj.getX(), canvas.firstPort.getY() + canvas.firstPort.obj.getY()));
							canvas.mouseX = x;
							canvas.mouseY = y;
						} else if (canBeConnected(canvas.firstPort, port)) {

							if (port == canvas.firstPort) {
								canvas.firstPort.setConnected(false);
								canvas.firstPort = null;
								canvas.currentCon = null;
							} else {
								port.setConnected(true);
								canvas.currentCon.beginPort = canvas.firstPort;
								canvas.currentCon.endPort = port;
								canvas.firstPort.addConnection(canvas.currentCon);
								port.addConnection(canvas.currentCon);
								canvas.currentCon.addBreakPoint(new Point(port.getX() + port.obj.getX(), port.getY() + port.obj.getY()));
								canvas.connections.add(canvas.currentCon);
								canvas.firstPort = null;
							}
						}
					}
				} else {
					if (canvas.firstPort != null) {
						if (e.getClickCount() == 2) {
							canvas.stopRelationAdding();
						} else
							canvas.currentCon.addBreakPoint(new Point(x, y));
						// firstPort.setConnected(false);
						// firstPort=null;
					}
				}

			} // **********Selecting objects code*********************
			else if (state.equals(State.selection)) {
				Connection con = canvas.connections.nearPoint(x, y);

				if (con != null) {
					con.selected = true;
				} else {
					canvas.connections.clearSelected();
				}
				GObj obj = canvas.objects.checkInside(x, y);

				if (obj == null) {
					canvas.objects.clearSelected();
				} else {
					if (e.isShiftDown()) {
					} else {
						canvas.objects.clearSelected();
						obj.setSelected(true);
					}
				}

			} else {
				if (state.startsWith("??")) { // if class is of type relation
					addingSpecialRelation(y, x);
				} else if (canvas.currentObj != null) {
					addObj();
					state = State.selection;
					Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);

					canvas.setCursor(cursor);
				}
			}
		}

		canvas.repaint();
	}

	private void addingSpecialRelation(int y, int x) {
		GObj obj = canvas.objects.checkInside(x, y);
		if (obj != null) {
			Port port = obj.portContains(x, y);
			if (port != null) {
				if (canvas.firstPort == null) {
					canvas.firstPort = port;
					canvas.firstPort.setConnected(true);
					canvas.mouseX = x;
					canvas.mouseY = y;
				} else {
					Port port1 = (Port) canvas.currentObj.ports.get(0);
					Port port2 = (Port) canvas.currentObj.ports.get(1);
					Connection con = new Connection(canvas.firstPort, port1);
					canvas.firstPort.addConnection(con);
					port1.addConnection(con);
					canvas.connections.add(con);
					con = new Connection(port2, port);
					port2.addConnection(con);
					port.addConnection(con);
					canvas.connections.add(con);
					port.setConnected(true);
					RelObj thisObj = (RelObj) canvas.currentObj;
					thisObj.startPort = canvas.firstPort;
					thisObj.endPort = port;
					canvas.firstPort = null;
					addObj();
					startAddingObject();
					//setState(State.selection);
					canvas.objects.updateRelObjs();
					//editor.currentObj = null;

				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		if (state.equals(State.selection)) {
			canvas.mouseX = e.getX();
			canvas.mouseY = e.getY();
			Connection con = canvas.connections.nearPoint(canvas.mouseX, canvas.mouseY);
			if (con != null) {
				draggedBreakPoint = con.breakPointContains(canvas.mouseX, canvas.mouseY);
			}
			if (con != null && draggedBreakPoint != null) {
				state = State.dragBreakPoint;


			} else {
				GObj obj = canvas.objects.checkInside(canvas.mouseX, canvas.mouseY);
				if (obj != null) {
					if (e.isShiftDown()) {
						obj.setSelected(true);
					} else {
						if (!obj.isSelected()) {
							canvas.objects.clearSelected();
							obj.setSelected(true);
						}
					}
					state = State.drag;
					canvas.repaint();

				} else {
					cornerClicked = canvas.objects.controlRectContains(canvas.mouseX, canvas.mouseY);
					if (cornerClicked != 0) {
						//if (obj.isSelected())
						state = State.resize;

					} else {
						state = State.dragBox;
						startX = canvas.mouseX;
						startY = canvas.mouseY;
					}
				}
				// drawConnections();
				selectedObjs = canvas.objects.getSelected();

			}

		}

	}

	public void mouseDragged(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();
		GObj obj;
		Connection relation;

		if (state.equals(State.dragBreakPoint)) {
			draggedBreakPoint.x = x;
			draggedBreakPoint.y = y;
			canvas.repaint();
		}
		if (state.equals(State.drag)) {
			int x1, x2, y1, y2, newX, newY;
			for (int i = 0; i < selectedObjs.size(); i++) {
				obj = (GObj) selectedObjs.get(i);
				if (!(obj instanceof RelObj)) {

					if (RuntimeProperties.snapToGrid == 1) {
						//use the following when  snap to grid
						x1 = Math.round(x / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						x2 = Math.round(canvas.mouseX / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						y1 = Math.round(y / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						y2 = Math.round(canvas.mouseY / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						newX = Math.round((obj.getX() + (x1 - x2)) / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						newY = Math.round((obj.getY() + (y1 - y2)) / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
						obj.setPosition(newX, newY);
					} else {
						//use this when not snap to grid
						obj.setPosition(obj.getX() + (x - canvas.mouseX), obj.getY() + (y - canvas.mouseY));
					}
				}

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
										canvas.connections.remove(port, port2);
									}
								}
							}

							obj2 = canvas.objects.checkInside(port.obj.getX() + (x - canvas.mouseX) + port.getCenterX(), port.obj.getY() + (y - canvas.mouseY) + port.getCenterY(), obj);
							if (obj2 != null && !obj2.isSelected()) {
								port2 = obj2.portContains(port.obj.getX() + (x - canvas.mouseX) + port.getCenterX(), port.obj.getY() + (y - canvas.mouseY) + port.getCenterY());

								if (port2 != null && port2.isStrict()) {
									if (!port.isConnected()) {
										port.setConnected(true);
										port2.setConnected(true);
										Connection con = new Connection(port, port2);

										port2.addConnection(con);
										port.addConnection(con);
										canvas.connections.add(con);
									}
									obj.setPosition(port2.obj.x + port2.getCenterX() - ((port.obj.x - obj.x) + port.getCenterX()), port2.obj.y + port2.getCenterY() - ((port.obj.y - obj.y) + port.getCenterY()));
								}
							}
						}
					}
				}

				for (int j = 0; j < canvas.connections.size(); j++) {
					relation = (Connection) canvas.connections.get(j);
					if (selectedObjs.contains(relation.endPort.obj) && selectedObjs.contains(relation.beginPort.obj)) {
						relation.calcAllBreakPoints();
					} else if (obj.includesObject(relation.endPort.obj) || obj.includesObject(relation.beginPort.obj)) {
						relation.calcEndBreakPoints();
					}

				}


			}
			canvas.objects.updateRelObjs();
			canvas.mouseX = x;
			canvas.mouseY = y;
			canvas.repaint();
		}
		if (state.equals(State.dragBox)) {
			canvas.mouseX = x;
			canvas.mouseY = y;
			canvas.repaint();
		}
		if (state.equals(State.resize)) {
			for (int i = 0; i < selectedObjs.size(); i++) {
				obj = (GObj) selectedObjs.get(i);

				obj.resize(x - canvas.mouseX, y - canvas.mouseY, cornerClicked);

				for (int j = 0; j < canvas.connections.size(); j++) {
					relation = (Connection) canvas.connections.get(j);
					if (obj.includesObject(relation.endPort.obj) || obj.includesObject(relation.beginPort.obj)) {
						relation.calcAllBreakPoints();
					}
				}
			}
			canvas.mouseX = x;
			canvas.mouseY = y;
			canvas.repaint();

			canvas.objects.updateRelObjs();
		}
	}

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// update mouse position in info label
		canvas.posInfo.setText(Integer.toString(x) + ", " + Integer.toString(y));

		// check if port needs to be nicely drawn coz of mouseover
		if (state.equals(State.addRelation) || state.startsWith("??")) {
			if (canvas.currentPort != null) {
				canvas.currentPort.setSelected(false);
				canvas.repaint();
			}

			GObj obj = canvas.objects.checkInside(x, y);

			if (obj != null) {
				Port port = obj.portContains(x, y);

				if (port != null) {
					if (canvas.firstPort != null) {
						if (canBeConnected(canvas.firstPort, port)) {
							port.setSelected(true);
							canvas.currentPort = port;
							canvas.repaint();
						}
					} else {
						port.setSelected(true);
						canvas.currentPort = port;
						canvas.repaint();
					}
				}
			}
		}

		// if we're adding a new object...
		if (canvas.currentObj != null && canvas.vPackage.hasClass(state)) {
			//use these when snap to grid
			if (RuntimeProperties.snapToGrid == 1) {
				canvas.currentObj.x = Math.round(x / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
				canvas.currentObj.y = Math.round(y / RuntimeProperties.gridStep) * RuntimeProperties.gridStep;
			} else {
				//Use these when not snap to grid:
				canvas.currentObj.y = y;
				canvas.currentObj.x = x;
			}
			// Kui objektil on moni strict port, chekime kas teda kuskile panna on;
			if (canvas.currentObj.isStrict()) {
				Port port, port2;
				GObj obj;

				for (int i = 0; i < canvas.currentObj.ports.size(); i++) {
					port = (Port) canvas.currentObj.ports.get(i);
					port2 = port.getStrictConnected();
					if (port2 != null) {
						if (Math.abs(port.getRealCenterX() - port2.getRealCenterX()) > 1 || Math.abs(port.getRealCenterY() - port2.getRealCenterY()) > 1) {
							canvas.connections.remove(port, port2);
						}
					}

					obj = canvas.objects.checkInside(x + port.getCenterX(), y + port.getCenterY());
					if (obj != null) {
						port2 = obj.portContains(x + port.getCenterX(), y + port.getCenterY());

						if (port2 != null && port2.isStrict()) {
							if (!port.isConnected()) {
								port.setConnected(true);
								port2.setConnected(true);
								Connection con = new Connection(port, port2);

								port2.addConnection(con);
								port.addConnection(con);
								canvas.connections.add(con);
							}
							canvas.currentObj.x = port2.obj.x + port2.getCenterX() - port.getCenterX();
							canvas.currentObj.y = port2.obj.y + port2.getCenterY() - port.getCenterY();
						}
					}
				}
			}

			Rectangle rect = new Rectangle(x - 10, y - 10, canvas.currentObj.getRealWidth() + 10, canvas.currentObj.getRealHeight() + 10);

			canvas.drawingArea.scrollRectToVisible(rect);
			if (x + canvas.currentObj.getRealWidth() > canvas.drawAreaSize.width) {
				canvas.drawAreaSize.width = x + canvas.currentObj.getRealWidth();
				canvas.drawingArea.setPreferredSize(canvas.drawAreaSize);
				canvas.drawingArea.setPreferredSize(canvas.drawAreaSize);
			}

			if (y + canvas.currentObj.getRealHeight() > canvas.drawAreaSize.height) {
				canvas.drawAreaSize.height = y + canvas.currentObj.getRealHeight();
				canvas.drawingArea.setPreferredSize(canvas.drawAreaSize);
				canvas.drawingArea.revalidate();
			}
			canvas.repaint();
		} else if (state.startsWith("??") && canvas.firstPort != null) { //if class is of type relation
			canvas.repaint();
		}
		if (canvas.firstPort != null) {
			canvas.mouseX = x;
			canvas.mouseY = y;
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
			canvas.objects.selectObjectsInsideBox(startX, startY, canvas.mouseX, canvas.mouseY);
			state = State.selection;
			canvas.repaint();
		}
		if (canvas.objects.getSelected() != null && canvas.objects.getSelected().size() > 0) {
			String selObjects = canvas.objects.getSelected().toString();

			if (selObjects != null) {
				selObjects = selObjects.replaceAll("null ", " ");
			}
			canvas.posInfo.setText("Selection: " + selObjects);
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


	public void actionPerformed(ActionEvent e) {
		// Jbutton pressed
		if (e.getSource().getClass().getName() == "javax.swing.JButton") {
			if (e.getActionCommand().equals(State.relation)) {
				canvas.mListener.setState(State.addRelation);
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);

				canvas.setCursor(cursor);
			} else if (e.getActionCommand().equals(State.selection)) {
				Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);

				canvas.setCursor(cursor);
				canvas.mListener.setState(State.selection);
			} else if (e.getActionCommand().equals(State.magnifier)) {
				canvas.mListener.setState(State.magnifier);
			} else {
				canvas.mListener.setState(e.getActionCommand());
				startAddingObject();
			}
			canvas.drawingArea.grabFocus();
		}
	}

	private void startAddingObject() {
		GObj obj;
		PackageClass pClass;

		if (state.startsWith("??")) {
			pClass = canvas.vPackage.getClass(state.substring(2));
			obj = new RelObj(0, 0, pClass.graphics.getWidth(), pClass.graphics.getHeight(), pClass.toString());
		} else {
			pClass = canvas.vPackage.getClass(state);
			obj = new GObj(0, 0, pClass.graphics.getWidth(), pClass.graphics.getHeight(), pClass.toString());
		}


		obj.shapes = (ArrayList) pClass.graphics.shapes.clone();
		Shape shape;
		for (int i = 0; i < obj.shapes.size(); i++) {
			shape = (Shape) obj.shapes.get(i);
			obj.shapes.set(i, shape.clone());
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

		obj.setName(pClass.name + "_" + Integer.toString(canvas.objCount));
		canvas.objCount++;

		canvas.currentObj = obj;
		Cursor cursor;
		if (state.startsWith("??")) {
			cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		} else {
			cursor = new Cursor(Cursor.HAND_CURSOR);
		}
		canvas.setCursor(cursor);
	}
}

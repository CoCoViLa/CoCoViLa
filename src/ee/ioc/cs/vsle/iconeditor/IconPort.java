package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ClassGraphics;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.vclass.Connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BasicStroke;

/**
 * <p>Title: IconPort</p>
 * <p>Description: Port class for using in Icon Editor application.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class IconPort implements Cloneable, Serializable {

  GObj obj;
  int width = 6;
  int height = 6;
  String name;
  String type;
  String dataType;
  int x;
  int y;
  boolean strict;
  boolean area;
  ClassGraphics openGraphics;
  ClassGraphics closedGraphics;
  Graphics2D graphics;
  ArrayList connections = new ArrayList();
  boolean selected = false;
  boolean connected = false;
  boolean known = false;
  boolean target = false;
  boolean watched = false;

  /**
   * Class constructor. Constructs new port for the Icon Editor.
   * @param name String - port name.
   * @param x int - port x coordinate.
   * @param y int - port y coordinate.
   * @param isAreaConn boolean - is port area connectible.
   * @param isStrict boolean - is port a strict one.
   */
  IconPort (String name, int x, int y, boolean isAreaConn, boolean isStrict) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.area = isAreaConn;
    this.strict = isStrict;
  } // IconPort

  /**
   * Returns port x coordinate.
   * @return int - port x coordinate.
   */
  int getX() {
    return x;
  } // getX

  /**
   * Returns port y coordinate.
   * @return int - port y coordinate.
   */
  int getY() {
    return y;
  } // getY

  /**
   * Returs center x coordinate of the port.
   * @return int - port center x coordinate.
   */
  int getCenterX() {
    return (int) (x + openGraphics.boundX + (openGraphics.boundWidth) / 2);
  } // getCenterX

  /**
   * Returns the textual format of the port. Used at saving the object to disk
   * for later loading and continuing working on.
   * @return String - textual format of the port.
   */
  public String toText() {
	return "PORT:"+getX()+":"+getY()+":"+isArea()+":"+isStrict()+":"+getName();
  } // toText

  /**
   * Returns true if the port is inside the selection rectangle and false otherwise.
   * @param x1 int - x coordinate of the selection rectangle starting point.
   * @param y1 int - y coordinate of the selection rectangel starting point.
   * @param x2 int - x coordinate of the selection rectangle ending point.
   * @param y2 int - y coordinate of the selection rectangle ending point.
   * @return boolean - the port is inside the selection rectangle or not.
   */
  public boolean isInsideRect(int x1, int y1, int x2, int y2) {
	if (x1 < x-6 && y1 < y-6 && x2 > (x + width) && y2 > (y + height)) {
		return true;
	}
	return false;
  } // isInsideRect

  /**
   * Set size using zoom multiplication.
   * @param s float - set size using zoom multiplication.
   */
  public void setMultSize(float s1, float s2) {
	x = (int)(x*s1/s2);
	y = (int)(y*s1/s2);
   } // setMultSize

  /**
   * Returns center y coordinate of the port.
   * @return int - port center y coordinate.
   */
  int getCenterY() {
    return (int) (y + openGraphics.boundY + (openGraphics.boundHeight) / 2);
  } // getCenterY

  /**
   * Returns center x coordinate of the port calculated from object bounds.
   * @return int - center x coordinate of the port calculated from object bounds.
   */
  int getRealCenterX() {
    return (int) (obj.x + x + openGraphics.boundX + (openGraphics.boundWidth) / 2);
  } // getRealCenterX

  /**
   * Returns center y coordinate of the port calculated from object bounds.
   * @return int - center y coordinate of the port calculated from object bounds.
   */
  int getRealCenterY() {
    return (int) (obj.y + y + openGraphics.boundY + (openGraphics.boundHeight) / 2);
  } // getRealCenterY

  /**
   * Returns port width.
   * @return int - port width.
   */
  int getWidth() {
    return (int) openGraphics.boundWidth;
  } // getWidth

  /**
   * Returns port height.
   * @return int - port height.
   */
  int getHeight() {
    return (int) openGraphics.boundHeight;
  } // getHeight

  /**
   * Returns port name.
   * @return String - port name.
   */
  public String getName() {
    return name;
  } // getName

  /**
   * Sets port a new name.
   * @param s String - new name for the port.
   */
  public void setName(String s) {
    this.name = s;
  } // setName

  /**
   * Returns port name.
   * @return String - port name.
   */
  public String toString() {
    return name;
  } // toString

  /**
   * Sets port selected or unselected.
   * @param b boolean - value specifying if to set port selected or unselected.
   */
  public void setSelected(boolean b) {
    selected = b;
    if(b) {
    if(graphics!=null) drawSelection(graphics);
    }
  } // setSelected

  /**
   * Sets port connected or unconnected.
   * @param b boolean - value specifying if to set port connected or unconnected.
   */
  public void setConnected(boolean b) {
    connected = b;
  } // setConnected

  /**
   * Set port known or unknown.
   * @param b boolean - value specifying if port should be known or unknown.
   */
  void setKnown(boolean b) {
    known = b;
  } // setKnown

  /**
   * Set port as target or not.
   * @param b boolean - value specifying if port shold be a target or not.
   */
  void setTarget(boolean b) {
    target = b;
  } // setTarget

  /**
   * Set port watched or not.
   * @param b boolean - value specifying if port is watched or not.
   */
  void setWatch(boolean b) {
    watched = b;
  } // setWatch

  /**
   * Check if mouse clicked inside port area.
   * @param xCoord int - mouse pointer x coordinate.
   * @param yCoord int - mouse pointer y coordinate.
   * @return boolean - clicked inside port area or not.
   */
  public boolean isInside(int xCoord, int yCoord) {
    if(xCoord>=getX()-6 && xCoord<=getX()+6 && yCoord>=getY()-6 && yCoord<=getY()+6) {
      return true;
    }
    return false;
  } // isInside

  /**
   * Returns true if port is selected, false otherwise.
   * @return boolean - port is selected or not.
   */
  public boolean isSelected() {
    return selected;
  } // isSelected

  /**
   * Returns true if port is connected, false otherwise.
   * @return boolean - port is connected or not.
   */
  public boolean isConnected() {
    return connected;
  } // isConnected

  /**
   * Returns true if port is known, false otherwise.
   * @return boolean - port is known or not.
   */
  boolean isKnown() {
    return known;
  } // isKnown

  /**
   * Returns true if port is a target, false otherwise.
   * @return boolean - port is target or not.
   */
  boolean isTarget() {
    return target;
  } // isTarget

  /**
   * Returns true if port is watched, false otherwise.
   * @return boolean - port is watched or not.
   */
  boolean isWatched() {
    return watched;
  } // isWatched

  /**
   * Returns true if port is strict, false otherwise.
   * @return boolean - port is strict or not.
   */
  boolean isStrict() {
    return strict;
  } // isStrict

  /**
   * Returns true if port is area connectible, false otherwise.
   * @return boolean - port is area connectible.
   */
  boolean isArea() {
    return area;
  } // isArea

  /**
   * Returns port object type.
   * @return String - port object type.
   */
  String getType() {
    for (int i = 0; i < obj.fields.size(); i++) {
      if ( ( (ClassField) obj.fields.get(i)).name.equals(name)) {
        return ( (ClassField) obj.fields.get(i)).type;
      }
    }
    return null;
  } // getType

  /**
   * Returns class field of the port.
   * @return ClassField - class field of the port.
   */
  ClassField getField() {
    for (int i = 0; i < obj.fields.size(); i++) {
      if ( ( (ClassField) obj.fields.get(i)).name.equals(name)) {
        return ( (ClassField) obj.fields.get(i));
      }
    }
    return null;
  } // getField

  /**
   * Set port object.
   * @param obj GObj - port object.
   */
  void setObject(GObj obj) {
    this.obj = obj;
  } // setObject

  /**
   * Returns port number.
   * @return int - port number.
   */
  int getNumber() {
    IconPort port;
    for (int j = 0; j < obj.ports.size(); j++) {
      port = (IconPort) obj.ports.get(j);
      if (port == this) {
        return j;
      }
    }
    return -1;
  } // getNumber

  /**
   * Add connection to the port.
   * @param con Connection - connection added to the port.
   */
  void addConnection(Connection con) {
    connections.add(con);
  } // addConnection

  /**
   * Returns a list of connections for the current port.
   * @return ArrayList - list of connections for the current port.
   */
  ArrayList getConnections() {
    return connections;
  } // getConnections

  /**
   * Clone the port.
   * @return Object - cloned port.
   */
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  } // clone

  /**
   * Set the port a new position.
   * @param x int - new x coordinate of the port.
   * @param y int - new y coordinate of the port.
   */
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  } // setPosition

  /**
   * Draw the selection markers if object selected.
   * @param g Graphics2D - Object's Graphics.
   */
  public void drawSelection(Graphics2D g) {
    g.setColor(Color.black);
    g.setStroke(new BasicStroke((float)1.0));

	g.drawRect(x-6, y-6, 3, 3);
	g.drawRect(x + width-3, y-6, 3, 3);
	g.drawRect(x-6, y + height-3, 3, 3);
	g.drawRect(x + width-3, y + height-3, 3, 3);

  } // drawSelection

  /**
   * Draw the port. Supports drawing with transparent colors.
   * @param xModifier int
   * @param yModifier int
   * @param size float - defines the resizing multiplier (used at zooming), default: 1.0
   * @param g Graphics
   */
  void draw (int xModifier, int yModifier, float size, Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    this.graphics = g2;
    g2.setColor(Color.red);

	int a = (int)(((xModifier + x)*size)-(height/2));
    int b = (int)(((yModifier + y)*size)-(width/2));
	int c = (int)(this.height * size);
	int d = (int)(this.width * size);

    g2.drawOval(a, b, c, d);

    // Draw selection markers if object selected.
    if (selected) {
      drawSelection(g2);
    }

  } // draw


} // end of class

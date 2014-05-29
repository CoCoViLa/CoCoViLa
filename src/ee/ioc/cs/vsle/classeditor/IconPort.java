package ee.ioc.cs.vsle.classeditor;

import ee.ioc.cs.vsle.vclass.ClassGraphics;

import java.io.Serializable;
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

	int width = 10;
	int height = 10;
	String name;
	private String type;
	int x;
	int y;
	boolean strict;
	boolean area;
	boolean multi;
	ClassGraphics openGraphics;
	ClassGraphics closedGraphics;
	Graphics2D graphics;
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
	IconPort(String name, int x, int y, boolean isAreaConn, boolean isStrict, boolean isMulti ) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.area = isAreaConn;
		this.strict = isStrict;
		this.multi = isMulti;
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
		return (x + openGraphics.getBoundX() + (openGraphics.getBoundWidth()) / 2);
	} // getCenterX

	/**
	 * Returns the textual format of the port. Used at saving the object to disk
	 * for later loading and continuing working on.
	 * @return String - textual format of the port.
	 */
	public String toText() {
		return "PORT:" + getX() + ":" + getY() + ":" + isArea() + ":" + isStrict() + ":" + getName();
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
		if (x1 < x - width && y1 < y - height && x2 > (x + width) && y2 > (y + height)) {
			return true;
		}
		return false;
	} // isInsideRect

	/**
	 * Set size using zoom multiplication.
	 * @param s1 X zoom size multiplication
	 * @param s2 Y zoom size multiplication
	 */
	public void setMultSize(float s1, float s2) {
		x = (int) (x * s1 / s2);
		y = (int) (y * s1 / s2);
	} // setMultSize

	/**
	 * Returns port width.
	 * @return int - port width.
	 */
	int getWidth() {
	    return width;
	} // getWidth

	/**
	 * Returns port height.
	 * @return int - port height.
	 */
	int getHeight() {
	    return height;
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
	@Override
	public String toString() {
		return name;
	} // toString

	/**
	 * Sets port selected or unselected.
	 * @param b boolean - value specifying if to set port selected or unselected.
	 */
	public void setSelected(boolean b) {
		selected = b;
		if (b) {
			if (graphics != null) drawSelection(graphics);
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
		if (xCoord >= getX() - width && xCoord <= getX() + width && yCoord >= getY() - height && yCoord <= getY() + height) {
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

	boolean isMulti() {
		return multi;
	}
	
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
		return type;
	} // getType

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
		g.setStroke(new BasicStroke((float) 1.0));

		g.drawRect(x - width, y - height, 3, 3);
		g.drawRect(x + width - width/2, y - height, 3, 3);
		g.drawRect(x - width, y + height - height/2, 3, 3);
		g.drawRect(x + width - width/2, y + height - height/2, 3, 3);

	} // drawSelection

	/**
	 * Draw the port. Supports drawing with transparent colors.
	 * @param xModifier int
	 * @param yModifier int
	 * @param size float - defines the resizing multiplier (used at zooming), default: 1.0
	 * @param g Graphics
	 */
	void draw(int xModifier, int yModifier, float size, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.graphics = g2;
		g2.setColor(Color.red);

		int a = (int) (((xModifier + x) * size) - (height / 2));
		int b = (int) (((yModifier + y) * size) - (width / 2));
		int c = (int) (this.height * size);
		int d = (int) (this.width * size);

		g2.fillOval(a, b, c, d);
		//g2.drawLine(a+width/2, b, a+width/2, b + height);

		// Draw selection markers if object selected.
		if (selected) {
			drawSelection(g2);
		}

	} // draw
	public void shift(int offsetX, int offsetY) {
		x += offsetX;
		y += offsetY;
	}

	/**
	 * @param type the type to set
	 */
	void setType(String type) {
		this.type = type;
	}

} // end of class

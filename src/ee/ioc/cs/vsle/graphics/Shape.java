package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

import java.awt.*;

import ee.ioc.cs.vsle.vclass.*;

public class Shape
	implements Serializable, Cloneable {

	float size = 1; // percentage for resizing, 1 means real size

	public int x;
	public int y;

	/* difWithMasterX, difWithMasterY variables are for resizing an shape group, we need to know
	 the intitial difference to make it work correctly*/
	int difWithMasterX;
	int difWithMasterY;

	int portOffsetX1 = 0;
	int portOffsetX2 = 0;
	int portOffsetY1 = 0;
	int portOffsetY2 = 0;

	public int width;
	public int height;

	String className;
	String name;

	public ArrayList ports = new ArrayList();
	public ArrayList fields = new ArrayList();

	boolean selected;
	public boolean strict;
	boolean group = false;
	public static boolean antialiasing;

	public ClassGraphics graphics;

	public Shape() {
	}

	public Shape(int x, int y, int width, int height, String name) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.className = name;
	}

	public void setTransparency(double transparencyPercentage) {
	}

	public void setColor(Color col) {
	}

	public void setFont(Font f) {
	}

	public void setText(String s) {
	}

	public void setStrokeWidth(double strokeWidth) {
	}

	public boolean isFilled() {
		return false;
	}

	public double getStrokeWidth() {
		return 1.0;
	}

	public double getTransparency() {
		return 0.0;
	}

	public String toString() {
		return name;
	}

	ArrayList getPorts() {
		return ports;
	}

	public Color getColor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void draw(int x, int y, float Xsize, float Ysize, Graphics g) {
	}

	public float getSize() {
		return size;
	}

	public int getRealWidth() {
		return (int) (width * size);
	}

	public int getRealHeight() {
		return (int) (height * size);
	}

	public String toFile(int boundingboxX, int boundingboxY) {
		return null;
	}

	public String toText() {
		return null;
	}

	public void setRotation(double degrees) {
	}

	Graphics getGraphics() {
		return null;
	}

	public static void setAntialiasing(boolean b) {
		antialiasing = b;
	}

	public boolean isAntialiasingOn() {
		return this.antialiasing;
	}

	public String getText() {
		return null;
	}

	public boolean contains(int pointX, int pointY) {
		if ((pointX > x + (int) (size * portOffsetX1))
			&& (pointY > y + (int) (size * portOffsetY1))) {
			if ((pointX < x + (int) (size * (width + portOffsetX2))
				&& (pointY < y + (int) (size * (height + portOffsetY2))))) {
				return true;
			}
		}
		return false;
	}

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if ((x1 < x + portOffsetX1) && (y1 < y + portOffsetY1)) {
			if ((x2 > x + (int) (size * width) + portOffsetX2)
				&& (y2 > y + (int) (size * height) + portOffsetY2)) {
				return true;
			}
		}
		return false;
	}

	public boolean isGroup() {
		return group;
	}

	public void setMultSize(float s) {
		this.size = this.size * s;
	}

	public void setAsGroup(boolean b) {
		group = b;
	}

	public void setSelected(boolean set) {
		selected = set;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClassName(String name) {
		this.className = name;
	}

	public String getClassName() {
		return className;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getStartX() {
		return 0;
	}

	public int getStartY() {
		return 0;
	}

	public int getStartAngle() {
		return 0;
	}

	public int getArcAngle() {
		return 0;
	}

	public int getEndX() {
		return 0;
	}

	public Font getFont() {
		return null;
	}

	public int getEndY() {
		return 0;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isStrict() {
		return strict;
	}

	Port portContains(int pointX, int pointY) {
		Port port;
		for (int i = 0; i < ports.size(); i++) {
			port = (Port) ports.get(i);
			if (port.inBoundsX(pointX) && port.inBoundsY(pointY)) {
				return port;
			}
		}
		return null;
	}

	boolean includesObject(Shape shape) {
		if (shape == this) {
			return true;
		} else {
			return false;
		}
	}

	ArrayList getComponents() {
		ArrayList c = new ArrayList();

		c.add(this);
		return c;
	}

	public void resize(int deltaW, int deltaH, int cornerClicked) {
	}

	public int controlRectContains(int pointX, int pointY) {
		return 0;
	}

	public Object clone() {
		try {
			Shape shape = (Shape) super.clone();
			Port port;
			shape.ports = (ArrayList) ports.clone();
			/*
						 for (int i = 0; i < shape.ports.size(); i++) {
			  port = (Port) shape.ports.get(i);
			  port = (Port) port.clone();
			  port.setConnected(false);
			  shape.ports.set(i, port);
			  port.obj = obj;
			  port.connections = new ArrayList();
						 }
			 */
			shape.fields = (ArrayList) fields.clone();
			//deep clone each separate field
			ClassField field;
			for (int i = 0; i < fields.size(); i++) {
				field = (ClassField) fields.get(i);
				shape.fields.set(i, field.clone());
			}

			return shape;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}

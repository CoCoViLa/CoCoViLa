package ee.ioc.cs.vsle.graphics;

import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Graphics;

public class ShapeGroup
	extends Shape
	implements Serializable {

	public ArrayList shapes = new ArrayList();

	public ShapeGroup(ArrayList shapes) {
		super();
		this.shapes = shapes;
		setBounds();
	}

	public void removeAll() {
		shapes.removeAll(shapes);
	}

	public void removeAll(ArrayList a) {
		shapes.removeAll(a);
	}

	public void addAll(ArrayList a) {
		shapes.addAll(a);
	}

	public Shape checkInside(int x, int y) {
		Shape shape;

		for (int i = shapes.size() - 1; i >= 0; i--) {
			shape = (Shape) shapes.get(i);
			if (shape.contains(x, y)) {
				return shape;
			}
		}
		return null;
	}

	public int indexOf(Shape s) {
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i) == s) {
				return i;
			}
		}
		return 0;
	}

	public boolean isLocatedAtPoint(int x, int y) {
		for (int i = shapes.size() - 1; i >= 0; i--) {
			Shape shape = (Shape) shapes.get(i);

			if (shape.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	public void sendToBack(Shape shape) {
		shapes.remove(shape);
		shapes.add(0, shape);
	}

	public void bringToFront(Shape shape) {
		shapes.remove(shape);
		shapes.add(shape);
	}

	public void bringForward(Shape shape, int step) {
		int shapeIndex = shapes.indexOf(shape);

		if (shapeIndex + step < shapes.size()) {
			shapes.remove(shape);
			shapes.add(shapeIndex + step, shape);
		}
	}

	public void sendBackward(Shape shape, int step) {
		int shapeIndex = shapes.indexOf(shape);

		if (shapeIndex - step >= 0) {
			shapes.remove(shape);
			shapes.add(shapeIndex - step, shape);
		}
	}

	public void selectShapesInsideBox(int x1, int y1, int x2, int y2) {
		Shape shape;

		for (int i = 0; i < shapes.size(); i++) {
			shape = (Shape) shapes.get(i);
			if (shape.isInside(x1, y1, x2, y2)) {
				shape.setSelected(true);
			}
		}
	}

	public void eraseShape(int x, int y) {
		if (isLocatedAtPoint(x, y)) {
			Shape theShape = checkInside(x, y);

			if (theShape != null) {
				if (theShape.getName() != null) {
					if (!theShape.getName().equals(BoundingBox.name)) {
						shapes.remove(theShape);
					}
				}
				else {
					shapes.remove(theShape);
				}
			}
		}
	}

	public void clearSelected() {
		Shape shape;

		for (int i = 0; i < shapes.size(); i++) {
			shape = (Shape) shapes.get(i);
			shape.setSelected(false);
		}
	}

	public Shape checkInside(int x, int y, Shape asker) {
		Shape shape;

		for (int i = 0; i < shapes.size(); i++) {
			shape = (Shape) shapes.get(i);
			if (shape.contains(x, y) && shape != asker) {
				return shape;
			}
		}
		return null;
	}

	public void add(Shape s) {
		shapes.add(s);
	}

	public void removeAll(ShapeGroup sg) {
		removeAll(sg.shapes);
	}

	public void remove(Shape s) {
		shapes.remove(s);
	}

	public Object get(int i) {
		return shapes.get(i);
	}

	public int size() {
		return shapes.size();
	}

	public ArrayList getSelected() {
		ArrayList a = new ArrayList();

		for (int i = 0; i < shapes.size(); i++) {
			Shape shape = (Shape) shapes.get(i);

			if (shape != null && shape.isSelected()) {
				a.add(shape);
			}
		}
		return a;
	}

	void setBounds() {
		int x1, x2, y1, y2;
		Shape shape;

		if (shapes != null && shapes.size() > 0) {
			shape = (Shape) shapes.get(0);
			x1 = shape.x + shape.portOffsetX1;
			y1 = shape.y + shape.portOffsetY1;
			x2 = shape.x + shape.getRealWidth() + shape.portOffsetX2;
			y2 = shape.y + shape.getRealHeight() + shape.portOffsetY2;

			for (int i = 1; i < shapes.size(); i++) {
				shape = (Shape) shapes.get(i);
				if (shape.x < x1) {
					x1 = shape.x + shape.portOffsetX1;
				}
				if (shape.y < y1) {
					y1 = shape.y + shape.portOffsetY1;
				}
				if (shape.y + shape.getRealHeight() > y2) {
					y2 = shape.y + shape.getRealHeight() + shape.portOffsetX2;
				}
				if (shape.x + shape.getRealWidth() > x2) {
					x2 = shape.x + shape.getRealWidth() + shape.portOffsetY2;

				}
			}
			x = x1;
			y = y1;
			height = y2 - y1;
			width = x2 - x1;
			for (int i = 0; i < shapes.size(); i++) {
				shape = (Shape) shapes.get(i);
				shape.difWithMasterX = shape.x - x;
				shape.difWithMasterY = shape.y - y;
			}
		}
	}

	public void setFont(java.awt.Font f) {}

	public void setText(String s) {}

	public boolean contains(int pointX, int pointY) {
		if ( (pointX > x) && (pointY > y)) {
			if ( (pointX < x + (int) (size * width)) && (pointY < y + (int) (size * height))) {
				return true;
			}
		}
		return false;
	}

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if ( (x1 < x) && (y1 < y)) {
			if ( (x2 > x + (int) (size * width)) && (y2 > y + (int) (size * height))) {
				return true;
			}
		}
		return false;
	}

	Port portContains(int pointX, int pointY) {
		Port port;
		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			port = shape.portContains(pointX, pointY);
			if (port != null) {
				return port;
			}

			/* for (int i = 0; i<shape.ports.size(); i++) {
			 port = (ee.ioc.cs.editor.vclass.Port)shape.ports.get(i);
			 if ((pointX > shape.x + (int)(size*port.getX())-shape.PORTSIZE) && (pointY > shape.y + (int)(size*port.getY()) -PORTSIZE)) {
			 if ((pointX < shape.x + (int)(size*port.getX()) + PORTSIZE) && (pointY < shape.y + (int)(size*port.getY()) + PORTSIZE)) {
			 return port;
			 }
			 }
			 }   */
		}
		return null;
	}

	public void setPosition(int x, int y) {
		Shape shape;
		int changeX = x - this.x;
		int changeY = y - this.y;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			shape.setPosition(shape.getX() + changeX, shape.getY() + changeY);
		}
		this.x = x;
		this.y = y;

	}

	ArrayList getComponents() {
		ArrayList c = new ArrayList();
		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			c.addAll(shape.getComponents());
		}
		return c;

	}

	boolean includesObject(Shape checkObj) {
		ArrayList c = new ArrayList();
		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			if (shape.includesObject(checkObj)) {
				return true;
			}
		}
		return false;

	}

	ArrayList getPorts() {
		ArrayList c = new ArrayList();
		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			c.addAll(shape.getPorts());
		}
		return c;

	}

	public void draw(int x, int y, float Xsize, float Ysize, Graphics g) {
		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			shape.draw(0, 0, 1, 1, g);
		}
	}

	void setSize(float s) {
		float change = s / size;

		size = s;

		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);

			shape.setMultSize(change);
			shape.x = x + (int) (size * shape.difWithMasterX);
			shape.y = y + (int) (size * shape.difWithMasterY);
		}
	}

	public void setMultSize(float s) {
		size = s * size;

		float change = s;

		Shape shape;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);

			shape.setMultSize(change);
			shape.x = x + (int) (size * shape.difWithMasterX);
			shape.y = y + (int) (size * shape.difWithMasterY);
		}

	}

	public String toString() {
		Shape shape;
		String s = name;

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			s += " " + shape;
		}
		return s;
	}

	public Object clone() {
		ShapeGroup g = (ShapeGroup)super.clone();
		Shape shape;
		ArrayList newList = new ArrayList();

		for (int j = 0; j < shapes.size(); j++) {
			shape = (Shape) shapes.get(j);
			shape = (Shape) shape.clone();
			newList.add(shape);
		}
		g.shapes = newList;
		return g;

	}

}

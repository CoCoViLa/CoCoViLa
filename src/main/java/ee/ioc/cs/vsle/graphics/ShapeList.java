package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

public class ShapeList
	extends ArrayList<Shape>
	implements Serializable {

	public ShapeList() {
		super();
	}

	public void eraseShape(int x, int y) {
		if (isLocatedAtPoint(x, y)) {
			Shape theShape = checkInside(x, y);

			if (theShape != null) {
				if (theShape.getName() != null) {
					if (!theShape.getName().equals(BoundingBox.name)) {
						this.remove(theShape);
					}
				} else {
					this.remove(theShape);
				}
			}
		}
	}

	boolean isLocatedAtPoint(int x, int y) {
		for (int i = this.size() - 1; i >= 0; i--) {
			Shape shape = this.get(i);

			if (shape.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	public Shape checkInside(int x, int y) {
		Shape shape;

		for (int i = this.size() - 1; i >= 0; i--) {
			shape = this.get(i);
			if (shape.contains(x, y)) {
				return shape;
			}
		}
		return null;
	}

	Shape checkInside(int x, int y, Shape asker) {
		for (Shape shape: this) {
			if (shape.contains(x, y) && shape != asker) {
				return shape;
			}
		}
		return null;
	}

	void selectShapesInsideBox(int x1, int y1, int x2, int y2) {
		for (Shape shape: this) {
			if (shape.isInside(x1, y1, x2, y2)) {
				shape.setSelected(true);
			}
		}
	}

	void sendToBack(Shape shape) {
		this.remove(shape);
		this.add(0, shape);
	}

	void bringToFront(Shape shape) {
		this.remove(shape);
		this.add(shape);
	}

	void bringForward(Shape shape, int step) {
		int shapeIndex = this.indexOf(shape);

		if (shapeIndex + step < this.size()) {
			this.remove(shape);
			this.add(shapeIndex + step, shape);
		}
	}

	void sendBackward(Shape shape, int step) {
		int shapeIndex = this.indexOf(shape);

		if (shapeIndex - step >= 0) {
			this.remove(shape);
			this.add(shapeIndex - step, shape);
		}
	}

	void clearSelected() {
		for (Shape shape: this) {
			shape.setSelected(false);
		}
	}

	public ArrayList<Shape> getSelected() {
		ArrayList<Shape> a = new ArrayList<Shape>();
		for (Shape shape: this) {
			if (shape.isSelected()) {
				a.add(shape);
			}
		}
		return a;
	}

}

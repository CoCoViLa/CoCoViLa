package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

import java.awt.*;

import ee.ioc.cs.vsle.util.*;

public class ShapeGroup extends Shape implements Serializable {

  private boolean selected = false;
  private String name = "GROUP";

  float size = 1; // percentage for resizing, 1 means real size

  public ArrayList shapes = new ArrayList();

  public void drawSelection() {
  }

  public void setAntialiasing(boolean b) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setAntialiasing(b);
	}
  }

  public String toText() {
	StringBuffer text = new StringBuffer();
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  text.append(shape.toText());
    }
	return text.toString();
  }

  public int getX() {
	return this.x;
  }

  public int getY() {
	return this.y;
  }

  public float getSize() {
	return size;
  }

  public Color getColor() {
	return null;
  }

  public double getTransparency() {
	return 0.0;
  }

  public double getStrokeWidth() {
	return 1.0;
  }

  public boolean isFilled() {
	return false;
  }

  public void setStrokeWidth(double d) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setStrokeWidth(d);
    }
  }

  public void setColor(Color col) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setColor(col);
	}
  }

  public void setTransparency(double d) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setTransparency(d);
	}
  }

  public String toFile(int boundingboxX, int boundingboxY) {
	StringBuffer text = new StringBuffer();
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  text.append(shape.toFile(boundingboxX, boundingboxY));
	}
	return text.toString();
  }

  public int getRealHeight() {
	return 0;
  }

  public int getRealWidth() {
	return 0;
  }

  public ShapeGroup(ArrayList shapes) {
	super();
	this.shapes = shapes;
	setBounds();
  }

  public void setName(String s) {
	this.name = s;
  }

  public String getName() {
	return this.name;
  }

  public void resize(int deltaW, int deltaH, int cornerClicked) {
  }

  public boolean isSelected() {
	return selected;
  }

  public void setSelected(boolean b) {
	this.selected = b;
  }

  public int controlRectContains(int pointX, int pointY) {
	return 0;
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
	  x1 = shape.x;
	  y1 = shape.y;
	  x2 = shape.x + shape.getRealWidth();
	  y2 = shape.y + shape.getRealHeight();

	  for (int i = 1; i < shapes.size(); i++) {
		shape = (Shape) shapes.get(i);
		if (shape.x < x1) {
		  x1 = shape.x;
		}
		if (shape.y < y1) {
		  y1 = shape.y;
		}
		if (shape.y + shape.getRealHeight() > y2) {
		  y2 = shape.y + shape.getRealHeight();
		}
		if (shape.x + shape.getRealWidth() > x2) {
		  x2 = shape.x + shape.getRealWidth();

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

  public boolean contains(int pointX, int pointY) {
	if ( (pointX > x) && (pointY > y)) {
	  if ( (pointX < x + (int) (this.size * width))
		  && (pointY < y + (int) (this.size * height))) {
		return true;
	  }
	}
	return false;
  }

  public boolean isInside(int x1, int y1, int x2, int y2) {
	if (x1 > x && y1 > y && x2 < x + (int) (this.size * width) && y2 < y + (int) (this.size * height)) {
		return true;
	}
	return false;
  }

  public boolean isInsideRect(int x1, int y1, int x2, int y2) {
	if (x1 < x && y1 < y && x2 > x + (int) (size * width) && y2 > y + (int) (size * height)) {
		return true;
	}
	return false;
  }

  public ArrayList getShapes() {
	return this.shapes;
  }

  public void setPosition(int deltaX, int deltaY) {
	Shape shape;
	for (int j = 0; j < shapes.size(); j++) {
	  shape = (Shape) shapes.get(j);
	  if(shape instanceof ShapeGroup && shape.isSelected()) {
		for (int k = 0; k < shape.getShapes().size(); k++) {
		  Shape s = (Shape) shape.getShapes().get(k);
		  s.setPosition(s.getX()+deltaX, s.getY()+deltaY);
        }
      } else {
		shape.setPosition(shape.getX() + deltaX, shape.getY() + deltaY);
	  }
	}
	setXY();
  }

  private void setXY() {
	int x = 0;
	int y = 0;
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  if(i==0) {
		x = shape.getX();
		y = shape.getY();
	  } else {
		if(shape.getX()<x) x = shape.getX();
		if(shape.getY()<y) y = shape.getY();
      }
    }
	this.x = x;
	this.y = y;
  }

  ArrayList getComponents() {
	ArrayList c = new ArrayList();
	Shape shape;

	for (int j = 0; j < shapes.size(); j++) {
	  shape = (Shape) shapes.get(j);
	  c.add(shape);
	}
	return c;

  }

  boolean includesObject(Shape checkObj) {
	for (int j = 0; j < shapes.size(); j++) {
	  Shape shape = (Shape) shapes.get(j);
	  if (shape==checkObj) {
		return true;
	  }
	}
	return false;

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

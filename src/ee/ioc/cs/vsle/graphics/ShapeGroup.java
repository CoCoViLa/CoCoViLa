package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

import java.awt.*;

public class ShapeGroup extends Shape implements Serializable {

  private boolean selected = false;

  private String name = "GROUP";

  float size = 1; // percentage for resizing, 1 means real size

  public ArrayList shapes = new ArrayList();

  /**
   * Defines if the shape is resizable or not.
   */
  private boolean fixed = false;


  public void drawSelection() {
  } // drawSelection

  public String toText() {
	StringBuffer text = new StringBuffer();
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  text.append(shape.toText());
    }
	return text.toString();
  } // toText

  public void setFixed(boolean b) {
	this.fixed = b;
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setFixed(b);
    }
  } // setFixed

  public boolean isFixed() {
	return this.fixed;
  } // isFixed

  public int getX() {
	return this.x;
  } // getX

  public int getY() {
	return this.y;
  } // getY

  public float getSize() {
	return size;
  } // getSize

  public Color getColor() {
	return null;
  } // getColor

  public double getTransparency() {
	return 0.0;
  } // getTransparency

  public double getStrokeWidth() {
	return 1.0;
  } // getStrokeWidth

  public boolean isFilled() {
	return false;
  } // isFilled

  public void setStrokeWidth(double d) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setStrokeWidth(d);
    }
  } // setStrokeWidth

  public void setColor(Color col) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setColor(col);
	}
  } // setColor

  public void setTransparency(double d) {
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  shape.setTransparency(d);
	}
  } // setTransparency

  public String toFile(int boundingboxX, int boundingboxY) {
	StringBuffer text = new StringBuffer();
	for(int i=0;i<shapes.size();i++) {
	  Shape shape = (Shape)shapes.get(i);
	  text.append(shape.toFile(boundingboxX, boundingboxY));
	}
	return text.toString();
  } // toFile

  public int getRealHeight() {
	return 0;
  } // getRealHeight

  public int getRealWidth() {
	return 0;
  } // getRealWidth

  public ShapeGroup(ArrayList shapes) {
	super();
	this.shapes = shapes;
	setBounds();
  } // ShapeGroup

  public void setName(String s) {
	this.name = s;
  } // setName

  public String getName() {
	return this.name;
  } // getName

  public void resize(int deltaW, int deltaH, int cornerClicked) {
  } // resize

  public boolean isSelected() {
	return selected;
  } // isSelected

  public void setSelected(boolean b) {
	this.selected = b;
  } // setSelected

  public int controlRectContains(int pointX, int pointY) {
	return 0;
  } // controlRectContains

  public void removeAll() {
	shapes.removeAll(shapes);
  } // removeAll

  public void removeAll(ArrayList a) {
	shapes.removeAll(a);
  } // removeAll

  public void addAll(ArrayList a) {
	shapes.addAll(a);
  } // addAll

  public Shape checkInside(int x, int y) {
	Shape shape;
	for (int i = shapes.size() - 1; i >= 0; i--) {
	  shape = (Shape) shapes.get(i);
	  if (shape.contains(x, y)) {
		return shape;
	  }
	}
	return null;
  } // checkInside

  public int indexOf(Shape s) {
	for (int i = 0; i < shapes.size(); i++) {
	  if (shapes.get(i) == s) {
		return i;
	  }
	}
	return 0;
  } // indexOf

  public boolean isLocatedAtPoint(int x, int y) {
	for (int i = shapes.size() - 1; i >= 0; i--) {
	  Shape shape = (Shape) shapes.get(i);

	  if (shape.contains(x, y)) {
		return true;
	  }
	}
	return false;
  } // isLocatedAtPoint

  public void sendToBack(Shape shape) {
	shapes.remove(shape);
	shapes.add(0, shape);
  } // sendToBack

  public void bringToFront(Shape shape) {
	shapes.remove(shape);
	shapes.add(shape);
  } // bringToFront

  public void bringForward(Shape shape, int step) {
	int shapeIndex = shapes.indexOf(shape);

	if (shapeIndex + step < shapes.size()) {
	  shapes.remove(shape);
	  shapes.add(shapeIndex + step, shape);
	}
  } // bringForward

  public void sendBackward(Shape shape, int step) {
	int shapeIndex = shapes.indexOf(shape);

	if (shapeIndex - step >= 0) {
	  shapes.remove(shape);
	  shapes.add(shapeIndex - step, shape);
	}
  } // sendBackward

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
  } // eraseShape

  public void clearSelected() {
	Shape shape;
	for (int i = 0; i < shapes.size(); i++) {
	  shape = (Shape) shapes.get(i);
	  shape.setSelected(false);
	}
  } // clearSelected

  public Shape checkInside(int x, int y, Shape asker) {
	Shape shape;
	for (int i = 0; i < shapes.size(); i++) {
	  shape = (Shape) shapes.get(i);
	  if (shape.contains(x, y) && shape != asker) {
		return shape;
	  }
	}
	return null;
  } // checkInside

  public void add(Shape s) {
	shapes.add(s);
  } // add

  public void removeAll(ShapeGroup sg) {
	removeAll(sg.shapes);
  } // removeAll

  public void remove(Shape s) {
	shapes.remove(s);
  } // remove

  public Object get(int i) {
	return shapes.get(i);
  } // get

  public int size() {
	return shapes.size();
  } // size

  public ArrayList getSelected() {
	ArrayList a = new ArrayList();
	for (int i = 0; i < shapes.size(); i++) {
	  Shape shape = (Shape) shapes.get(i);
	  if (shape != null && shape.isSelected()) {
		a.add(shape);
	  }
	}
	return a;
  } // getSelected

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
  } // setBounds

  public boolean contains(int pointX, int pointY) {
	if ( (pointX > x) && (pointY > y)) {
	  if ( (pointX < x + (int) (this.size * width))
		  && (pointY < y + (int) (this.size * height))) {
		return true;
	  }
	}
	return false;
  } // contains

  public boolean isInside(int x1, int y1, int x2, int y2) {
	if (x1 > x && y1 > y && x2 < x + (int) (this.size * width) && y2 < y + (int) (this.size * height)) {
		return true;
	}
	return false;
  } // isInside

  public boolean isInsideRect(int x1, int y1, int x2, int y2) {
	if (x1 < x && y1 < y && x2 > x + (int) (size * width) && y2 > y + (int) (size * height)) {
		return true;
	}
	return false;
  } // isInsideRect

  public ArrayList getShapes() {
	return this.shapes;
  } // getShapes

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
  } // setPosition

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
  } // setXY

  ArrayList getComponents() {
	ArrayList c = new ArrayList();
	Shape shape;

	for (int j = 0; j < shapes.size(); j++) {
	  shape = (Shape) shapes.get(j);
	  c.add(shape);
	}
	return c;
  } // getComponents

  boolean includesObject(Shape checkObj) {
	for (int j = 0; j < shapes.size(); j++) {
	  Shape shape = (Shape) shapes.get(j);
	  if (shape==checkObj) {
		return true;
	  }
	}
	return false;
  } // includesObject

  public void draw(int x, int y, float Xsize, float Ysize, Graphics g) {
	Shape shape;
	for (int j = 0; j < shapes.size(); j++) {
	  shape = (Shape) shapes.get(j);
	  shape.draw(0, 0, 1, 1, g);
	}
  } // draw



  public void setMultSize(float s1, float s2) {
	for (int j = 0; j < shapes.size(); j++) {
	  Shape shape = (Shape) shapes.get(j);
	  shape.setMultSize(s1, s2);
//	  shape.x = x + (int) (size * shape.difWithMasterX);
//	  shape.y = y + (int) (size * shape.difWithMasterY);
	}
  } // setMultSize

  public String toString() {
	Shape shape;
	String s = name;

	for (int j = 0; j < shapes.size(); j++) {
	  shape = (Shape) shapes.get(j);
	  s += " " + shape;
	}
	return s;
  } // toString

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
  } // clone

}

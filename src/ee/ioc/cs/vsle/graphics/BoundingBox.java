package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

import java.awt.*;

public class BoundingBox extends Shape 	implements Serializable {

  String xEquation;
  String yEquation;
  String widthEquation;
  String heightEquation;
  boolean filled = false;

  /**
   * Shape color.
   */
  Color color;

  /**
   * Percentage of shape transparency.
   */
  float transparency = (float) 1.0;

  /**
   * Percentage for resizing, 1 means real size
   */
  private float size = 1;

  /**
   * Name of the shape.
   */
  public static final String name = "BoundingBox";

  /**
   * Shape graphics.
   */
  Graphics2D g2;

  /**
   * Line weight, logically equals to stroke width.
   */
  private float lineWeight;

  /**
   * Alpha value of a color, used
   * for defining the transparency of a filled shape.
   */
  private float alpha;

  /**
   * Indicates if the shape is selected or not.
   */
  private boolean selected = false;

  /**
   * Indicates if the shape should be drawn antialiased or not.
   */
  boolean antialiasing = true;

  public BoundingBox(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.color = Color.lightGray;
	this.filled = true;
	setStrokeWidth(1.0);
	this.transparency = (float) 0.2;
  } // BoundingBox

  public void setName(String s) {
  } // setName

  public String getName() {
	return this.name;
  } // getName

  public void setSelected(boolean b) {
	this.selected = b;
  } // setSelected

  public float getSize() {
	return this.size;
  } // getSize

  public boolean isSelected() {
	return this.selected;
  } // isSelected

  public void setAntialiasing(boolean b) {
	this.antialiasing = b;
  } // setAntialiasing

  public String toString() {
	return getName();
  } // toString

  public void setPosition(int x, int y) {
	this.x = x;
	this.y = y;
  } // setPosition

  public int getRealHeight() {
	return (int) (getHeight() * getSize());
  } // getRealHeight

  public int getRealWidth() {
	return (int) (getWidth() * getSize());
  } // getRealWidth

  public boolean isInside(int x1, int y1, int x2, int y2) {
	if (x1 > x && y1 > y && x2 < x + (int) (size * width) && y2 < y + (int) (size * height)) {
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

  public boolean contains(int pointX, int pointY) {
	if (pointX > x + size && pointY > y + size  && pointX < x + size + width && pointY < y + size + height) {
		return true;
	}
	return false;
  } // contains

  /**
   * Set the size of zoom multiplication.
   * @param f float - zoom multiplication factor.
   */
  public void setMultSize(float f) {
	this.size = getSize() * f;
  } // setMultSize

  /**
   * Returns the color of the rectangle.
   * @return Color - color of the rectangle.
   */
  public Color getColor() {
	return this.color;
  } // getColor

  /**
   * Set the color of a shape.
   * @param col Color - color of a shape.
   */
  public void setColor(Color col) {
	this.color = col;
  } // setColor

  /**
   * Returns a boolean value representing if the shape is filled or not.
   * @return boolean - a boolean value representing if the shape is filled or not.
   */
  public boolean isFilled() {
	return this.filled;
  } // isFilled

  /**
   * Returns the transparency of the shape.
   * @return double - the transparency of the shape.
   */
  public double getTransparency() {
	return this.transparency;
  } // getTransparency

  /**
   * Returns the stroke with of a shape.
   * @return double - stroke width of a shape.
   */
  public double getStrokeWidth() {
	return this.lineWeight;
  } // getStrokeWidth

  /**
   * Resizes current object.
   * @param deltaW int - change of object with.
   * @param deltaH int - change of object height.
   * @param cornerClicked int - number of the clicked corner.
   */
  public void resize(int deltaW, int deltaH, int cornerClicked) {

	if (cornerClicked == 1) { // TOP-LEFT
	  if ( (this.width - deltaW) > 0 && (this.height - deltaH) > 0) {
		this.x += deltaW;
		this.y += deltaH;
		this.width -= deltaW;
		this.height -= deltaH;
	  }
	}
	else if (cornerClicked == 2) { // TOP-RIGHT
	  if ( (this.width + deltaW) > 0 && (this.height - deltaH) > 0) {
		this.y += deltaH;
		this.width += deltaW;
		this.height -= deltaH;
	  }
	}
	else if (cornerClicked == 3) { // BOTTOM-LEFT
	  if ( (this.width - deltaW) > 0 && (this.height + deltaH) > 0) {
		this.x += deltaW;
		this.width -= deltaW;
		this.height += deltaH;
	  }
	}
	else if (cornerClicked == 4) { // BOTTOM-RIGHT
	  if ( (this.width + deltaW) > 0 && (this.height + deltaH) > 0) {
		this.width += deltaW;
		this.height += deltaH;
	  }
	}
  } // resize

  /**
   * Returns the number representing a corner the mouse was clicked in.
   * 1: top-left, 2: top-right, 3: bottom-left, 4: bottom-right.
   * Returns 0 if the click was not in the corner.
   * @param pointX int - mouse x coordinate.
   * @param pointY int - mouse y coordinate.
   * @return int - corner number the mouse was clicked in.
   */
  public int controlRectContains(int pointX, int pointY) {
	if ( (pointX >= x) && (pointY >= y)) {
	  if ( (pointX <= x + 4) && (pointY <= y + 4)) {
		return 1;
	  }
	}
	if ( (pointX >= x + (int) (size * (width)) - 4) && (pointY >= y)) {
	  if ( (pointX <= x + (int) (size * (width))) && (pointY <= y + 4)) {
		return 2;
	  }
	}
	if ( (pointX >= x) && (pointY >= y + (int) (size * (height)) - 4)) {
	  if ( (pointX <= x + 4) && (pointY <= y + (int) (size * (height)))) {
		return 3;
	  }
	}
	if ( (pointX >= x + (int) (size * (width)) - 4)
		&& (pointY >= y + (int) (size * (height)) - 4)) {
	  if ( (pointX <= x + (int) (size * (width)))
		  && (pointY <= y + (int) (size * (height)))) {
		return 4;
	  }
	}
	return 0;
  } // controlRectContains

  /**
   * Draw the selection markers if object selected.
   */
  public void drawSelection() {
	g2.setColor(Color.black);
	g2.setStroke(new BasicStroke( (float) 1.0));
	g2.fillRect(x, y, 4, 4);
	g2.fillRect(x + (int) (size * width) - 4, y, 4, 4);
	g2.fillRect(x, y + (int) (size * height) - 4, 4, 4);
	g2.fillRect(x + (int) (size * width) - 4, y + (int) (size * height) - 4,
			   4, 4);
  } // drawSelection

  /**
   * Return a specification of the shape to be written into a file in XML format.
   * @param boundingboxX - x coordinate of the bounding box.
   * @param boundingboxY - y coordinate of the bounding box.
   * @return String - specification of a shape.
   */
  public String toFile(int boundingboxX, int boundingboxY) {
	return "<bounds x=\"0\" y=\"0\" width=\"" + width + "\" height=\""
		+ height + "\"/>";
  } // toFile

  /**
   * Return a text string representing the shape. Required for storing
   * scheme of shapes on a disk for later loading into the IconEditor
   * for continuing the work.
   * @return String - text string representing the shape.
   */
  public String toText() {
   return "BOUNDS:"+x+":"+y+":"+width+":"+height;
  } // toText

  /**
   * Set width of the line stroke the rectangle is drawn with.
   * @param width double - rectangle drawing line stroke width.
   */
  public void setStrokeWidth(double width) {
	try {
	  if (width >= 0.0) {
		this.lineWeight = (float) width;
	  }
	  else {
		throw new Exception("Stroke width undefined or negative.");
	  }
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
  } // setStrokeWidth

  /**
   * Returns x coordinate of the rectangle.
   * @return int - rectangle x coordinate.
   */
  public int getX() {
	return x;
  } // getX

  /**
   * Returns y coordinate of the rectangle.
   * @return int - rectangle y coordinate.
   */
  public int getY() {
	return y;
  } // getY

  /**
   * Returns width of the rectangle.
   * @return int - rectangle width.
   */
  int getWidth() {
	return width;
  } // getWidth

  /**
   * Returns height of the rectangle.
   * @return int - rectangle height.
   */
  int getHeight() {
	return height;
  } // getHeight

  /**
   * Set the percentage of transparency.
   * @param transparencyPercentage double - the percentage of transparency.
   */
  public void setTransparency(double transparencyPercentage) {
	this.transparency = (float) transparencyPercentage;
  } // setTransparency

  void drawDynamic(int xModifier, int yModifier, float Xsize, float Ysize,
				   Graphics g, HashMap table) {
	/*int drawx = xModifier + x + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(xEquation, table);
		 int drawy = yModifier + y + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(yEquation, table)
		 int drawWidth = width + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(widthEquation, table);
		 int drawHeight = height + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(heightEquation, table);
		 g.drawRect(drawx, drawy, drawWidth, drawHeight);*/
  } // drawDynamic

  /**
   * Draw rectangle.
   * @param xModifier int -
   * @param yModifier int -
   * @param Xsize float - zoom factor.
   * @param Ysize float - zoom factor.
   * @param g Graphics - class graphics.
   */
  public void draw(int xModifier, int yModifier, float Xsize, float Ysize,
				   Graphics g) {
	g2 = (Graphics2D) g;

	g2.setStroke(new BasicStroke(this.lineWeight));

	alpha = (float) 0.2;

	// Set the box color: light-gray, with a transparency defined by "alpha" value.
	g2.setColor(new Color(0.0f, 0.0f, 0.0f, alpha));

	// draw the bounding box rectangle.
	g2.fillRect(xModifier + (int) (Xsize * x),
				yModifier + (int) (Ysize * y), (int) (Xsize * width),
				(int) (Ysize * height));

	// Draw selection markers if object selected.
	if (selected) {
	  drawSelection();
	}

  } // draw

}
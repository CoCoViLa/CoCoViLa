package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

/**
 * <p>Title: ee.ioc.cs.editor.graphics.Dot</p>
 * <p>Description: Shape of type ee.ioc.cs.editor.graphics.Dot</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class Dot extends Shape implements Serializable {

  /**
   * X coordinate of the dot.
   */
  int x;

  /**
   * Y coordinate of the dot.
   */
  int y;

  /**
   * Color of the dot.
   */
  Color color;

  /**
   * Alpha value of a color, used
   * for defining the transparency of a filled shape.
   */
  private float alpha;

  /**
   * Shape graphics.
   */
  Graphics2D g2;

  /**
   * Line weight, logically equals to stroke width.
   */
  private float lineWeight;

  /**
   * Name of the shape.
   */
  private String name;

  /**
   * Percentage for resizing, 1 means real size.
   */
  private float size = 1;

  /**
   * Dot transparency.
   */
  float transparency = (float) 1.0;

  /**
   * Indicates if the shape is selected or not.
   */
  private boolean selected = false;

  /**
   * Indicates if the dot should be drawn antialiased or not.
   */
  private boolean antialiasing = true;

  /**
   * Indicates if the dot is filled or not.
   */
  private boolean filled = false;

  /**
   * Class constructor.
   * @param x int - x coordinate of the dot.
   * @param y int - y coordinate of the dot.
   * @param colorInt int - dot color.
   * @param strokeWidth double - width of the line the dot is drawn with.
   * @param transp double -  transparency (Alpha) value (0..100%).
   */
  public Dot(int x, int y, int colorInt, double strokeWidth, double transp) {
	this.x = x;
	this.y = y;
	this.width = (int) strokeWidth / 2;
	this.height = (int) strokeWidth / 2;
	this.color = new Color(colorInt);
	this.transparency = (float) transp;
	this.lineWeight = (float) strokeWidth;
  } // Dot

  public void setName(String s) {
	this.name = s;
  } // setName

  public boolean isFilled() {
	return this.filled;
  } // isFilled

  public String getName() {
	return this.name;
  } // getName

  public void setSelected(boolean b) {
	this.selected = b;
  } // setSelected

  public String toString() {
	return getName();
  } // toString

  public void setAntialiasing(boolean b) {
	this.antialiasing = b;
  } // setAntialiasing

  public int getRealHeight() {
	return (int) (getHeight() * getSize());
  } // getRealHeight

  public int getRealWidth() {
	return (int) (getWidth() * getSize());
  } // getRealWidth

  public float getSize() {
	return this.size;
  } // getSize

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
	if ( (pointX > x + size) && (pointY > y + size) ) {
	  if ( (pointX < x + size) && (pointY < y + size) ) {
		return true;
	  }
	}
	return false;
  } // contains

  /**
   * Set the size of zoom multiplication.
   * @param f float - zoom multiplication.
   */
  public void setMultSize(float f) {
	this.size = getSize() * f;
  } // set mult size

  /**
   * Set the color of a shape.
   * @param col Color - color of a shape.
   */
  public void setColor(Color col) {
	this.color = col;
  } // setColor

  /**
   * Move the shape into a new position.
   * @param x int - new x coordinate of the shape.
   * @param y int - new y coordinate of the shape.
   */
  public void setPosition(int x, int y) {
	this.x = x;
	this.y = y;
  } // setPosition

  /**
   * Returns a boolean value indicating if the shape is selected or not.
   * @return boolean - a boolean value indicating if the shape is selected or not.
   */
  public boolean isSelected() {
	return this.selected;
  } // isSelected

  /**
   * Set width of the line stroke the rectangle is drawn with.
   * @param width double - rectangle drawing line stroke width.
   */
  public void setStrokeWidth(double width) {
  } // setStrokeWidth

  /**
   * Set the percentage of transparency.
   * @param transparencyPercentage double - the percentage of transparency.
   */
  public void setTransparency(double transparencyPercentage) {
	this.transparency = (float) transparencyPercentage;
  } // setTransparency

  /**
   * Returns the color of the dot.
   * @return Color - color of the dot.
   */
  public Color getColor() {
	return this.color;
  } // getColor

  /**
   * Returns the stroke with of a shape.
   * @return double - stroke width of a shape.
   */
  public double getStrokeWidth() {
	return this.lineWeight;
  } // getStrokeWidth

  /**
   * Returns the transparency of the shape.
   * @return double - the transparency of the shape.
   */
  public double getTransparency() {
	return this.transparency;
  } // getTransparency

  /**
   * Returns the x coordinate of the dot.
   * @return int - x coordinate of the dot.
   */
  public int getX() {
	return x;
  } // getX

  /**
   * Returns the y coordinate of the dot.
   * @return int - y coordinate of the dot.
   */
  public int getY() {
	return y;
  } // getY

  /**
   * Returns the width of the dot (the difference between the dot's beginning and
   * end x coordinates).
   * @return int - width of the dot.
   */
  int getWidth() {
	return width;
  } // getWidth

  /**
   * Returns the height of the dot (the difference between the dot's beginning and
   * end y coordinates).
   * @return int - height of the dot.
   */
  int getHeight() {
	return height;
  } // getHeight

  /**
   * Return a specification of the shape to be written into a file in XML format.
   * @param boundingboxX - x coordinate of the bounding box.
   * @param boundingboxY - y coordinate of the bounding box.
   * @return String - specification of a shape.
   */
  public String toFile(int boundingboxX, int boundingboxY) {
	int colorInt = 0;

	if (color != null) {
	  colorInt = color.getRGB();
	}
	return "<rect x=\"" + (x - boundingboxX) + "\" y=\""
		+ (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height
		+ "\" colour=\"" + colorInt + "\"/>";
  } // toFile

  public String toText() {
	int colorInt = 0;

	if (color != null) {
	  colorInt = color.getRGB();
	}
   return "DOT:"+x+":"+y+":"+width+":"+height+":"+colorInt+":"+(int)this.lineWeight+":"+(int)this.transparency;
  } // toText

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
	g2.drawRect(x, y, 4, 4);
	g2.drawRect(x + (int) (size * width) - 4, y, 4, 4);
	g2.drawRect(x, y + (int) (size * height) - 4, 4, 4);
	g2.drawRect(x + (int) (size * width) - 4, y + (int) (size * height) - 4,
			   4, 4);
  } // drawSelection

  /**
   * Draw the dot. Supports drawing with transparent colors.
   * @param xModifier int -
   * @param yModifier int -
   * @param sizeX float - defines the resizing multiplier for x coordinate (used at zooming), default: 1.0
   * @param sizeY float - defines the resizing multiplier for y coordinate (used at zooming), default: 1.0
   * @param g Graphics
   */
  public void draw(int xModifier, int yModifier, float sizeX, float sizeY, Graphics g) {
	g2 = (Graphics2D) g;

	g2.setStroke(new BasicStroke(this.lineWeight));

	// The user can specify the percentage of transparency between 0..100%.
	// The value of transparency is defined as a float value between 0..1.
	alpha = (float) (1 - (this.transparency / 100));

	// Separate the color to separate Red, Green and Blue, for allowing
	// the Graphics to be drawn with a transparent color.
	float red = (float) color.getRed() * 100 / 256 / 100;
	float green = (float) color.getGreen() * 100 / 256 / 100;
	float blue = (float) color.getBlue() * 100 / 256 / 100;

	// Set the Graphics a new transparent color.
	g2.setColor(new Color(red, green, blue, alpha));

	int w = (int) this.lineWeight / 2;

	g2.drawRect(xModifier + x, yModifier + y, w, w);

	// Draw selection markers if object selected.
	if (selected) {
	  drawSelection();
	}

  } // draw

}

package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

import ee.ioc.cs.vsle.editor.*;

public class Arc extends Shape implements Serializable, Cloneable {

  /**
   * Arc angles. Can be used for rotating the Arc.
   */
  int startAngle;
  int arcAngle;

  /**
   * Alpha value of a color, used
   * for defining the transparency of a filled shape.
   */
  private float alpha;

  /**
   * Fill the arc or not.
   */
  boolean filled = false;

  /**
   * Color of the arc.
   */
  Color color;

  /**
   * Line weight, logically equals to stroke width.
   */
  private float lineWeight;

  /**
   * Arc transparency.
   */
  float transparency = (float) 1.0;

  /**
   * Name of the shape.
   */
  String name;

  int lineType;

  /**
   * Indicates if the shape is selected or not.
   */
  private boolean selected = false;

  /**
   * Defines if the shape is resizable or not.
   */
  private boolean fixed = false;

  /**
   * Class constructor.
   * @param x int - x coordinate of the beginning of the arc.
   * @param y int - y coordinate of the beginning of the arc.
   * @param width int - arc width (arc end x coordinate - arc start x coordinate).
   * @param height int - arc height (arc end y coordinate - arc start y coordinate).
   * @param startAngle int - arc starting angle, zero by default.
   * @param arcAngle int - arc ending angle, 180 by default.
   * @param colorInt int - arc color.
   * @param fill boolean - boolean value indicating whether to fill the constructed arc with
   *                       a specified color or not.
   * @param strokeWidth double - width of the line the arc is drawn with.
   * @param transp double - transparency (Alpha) value (0..100%).
   * @param lineType - shape line type.
   */
  public Arc(int x, int y, int width, int height, int startAngle, int arcAngle,
			 int colorInt, boolean fill, double strokeWidth, double transp, int lineType) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.startAngle = startAngle;
	this.arcAngle = arcAngle;
	this.color = new Color(colorInt);
	this.filled = fill;
	this.transparency = (float) transp;
	this.lineWeight = (float) strokeWidth;
	this.lineType = lineType;
  } // Arc

  /**
   * Set the shape dimensions fixed in which case the shape cannot be resized.
   * @param b boolean - fix or unfix the shape.
   */
  public void setFixed(boolean b) {
	this.fixed = b;
  } // setFixed

  /**
   * Returns a boolean value representing if the shape is fixed or not.
   * @return boolean - boolean value representing if the shape is fixed or not.
   */
  public boolean isFixed() {
	return this.fixed;
  } // isFixed.

  /**
   * Returns the name of the shape.
   * @return String - the name of the shape.
   */
  public String getName() {
	return this.name;
  } // getName

  /**
   * Returns the real height of the shape.
   * @return int - the real height of the shape.
   */
  public int getRealHeight() {
	return getHeight();
  } // getRealHeight

  /**
   * Returns the real width of the shape.
   * @return int - the real width of the shape.
   */
  public int getRealWidth() {
	return getWidth();
  } // getRealWidth

  /**
   * Returns a boolean value representing if the shape is selected or not.
   * @return boolean - boolean value representing if the shape is selected or not.
   */
  public boolean isSelected() {
	return this.selected;
  } // isSelected

  /**
   * Returns the name of the shape. In future implementations should return the
   * textual representation of the shape, ie. the return value of the currently
   * implemented "toText" method.
   * @return String - the name of the shape.
   */
  public String toString() {
	return getName();
  } // toString

  /**
   * Returns a boolean value representing if the mouse was clicked inside the shape.
   * @param x1 int - x1 coordinate of the mouse pointer.
   * @param y1 int - y1 coordinate of the mouse pointer.
   * @param x2 int - x2 coordinate of the mouse pointer.
   * @param y2 int - y2 coordinate of the mouse pointer.
   * @return boolean - boolean value representing if the mouse was clicked inside the shape.
   */
  public boolean isInside(int x1, int y1, int x2, int y2) {
	if (x1 > x && y1 > y && x2 < x + width && y2 < y + height) {
		return true;
	}
	return false;
  } // isInside

  /**
   * Returns a boolean value representing if the shape is in the selection rectangle.
   * @param x1 int - x coordinate of the starting corner of the selection rectangle.
   * @param y1 int - y coordinate of the starting corner of the selection rectangle.
   * @param x2 int - x coordinate of the ending corner of the selection rectangle.
   * @param y2 int - y coordinate of the ending corner of the selection rectangle.
   * @return boolean - boolean value representing if the shape is in the selection rectangle.
   */
  public boolean isInsideRect(int x1, int y1, int x2, int y2) {
	if (x1 < x && y1 < y && x2 > x + width && y2 > y + height) {
		return true;
	}
	return false;
  } // isInsideRect

  /**
   * Set the shape selected or unselected.
   * @param b boolean - boolean value representing if to set the shape selected or unselected.
   */
  public void setSelected(boolean b) {
	this.selected = b;
  } // setSelected

  /**
   * Specify the name of the shape.
   * @param s String - the name of the shape.
   */
  public void setName(String s) {
	this.name = s;
  } // setName

  /**
   * Set size using zoom multiplication.
   * @param s1 float - set size using zoom multiplication.
   * @param s2 float - set size using zoom multiplication.
   */
  public void setMultSize(float s1, float s2) {
	x = x*(int)s1/(int)s2;
	y = y*(int)s1/(int)s2;
	width = width*(int)s1/(int)s2;
	height = height*(int)s1/(int)s2;
  } // setMultSize

   /**
	* Set shape position.
	* @param x int - new x coordinate of the shape.
	* @param y int - new y coordinate of the shape.
	*/
   public void setPosition(int x, int y) {
	 this.x = getX() + x;
	 this.y = getY() + y;
   } // setPosition

  /**
   * Set the color of a shape.
   * @param col Color - color of a shape.
   */
  public void setColor(Color col) {
	this.color = col;
  } // setColor

  /**
   * Set the percentage of transparency.
   * @param transparencyPercentage double - the percentage of transparency.
   */
  public void setTransparency(double transparencyPercentage) {
	this.transparency = (float) transparencyPercentage;
  } // setTransparency

  /**
   * Specify the line type used at drawing the shape.
   * @param lineType int
   */
  public void setLineType(int lineType) {
	this.lineType = lineType;
  } // setLineType

  /**
   * Returns the start angle of the shape.
   * @return int - start angle of the shape.
   */
  public int getStartAngle() {
	return this.startAngle;
  } // getStartAngle

  /**
   * Returns the angle of the arc.
   * @return int - angle of the arc.
   */
  public int getArcAngle() {
	return this.arcAngle;
  } // getArcAngle

  /**
   * Returns the color of the arc.
   * @return Color - color of the arc.
   */
  public Color getColor() {
	return this.color;
  } // getColor

  /**
   * Returns a boolean value representing if the shape is filled or not.
   * @return boolean - a boolean value representing if the shape is filled or not.
   */
  public boolean isFilled() {
	return this.filled;
  } // isFilled

  /**
   * Returns the stroke with of a shape.
   * @return double - stroke width of a shape.
   */
  public double getStrokeWidth() {
	return this.lineWeight;
  } // getStrokeWidth

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
   * Returns the transparency of the shape.
   * @return double - the transparency of the shape.
   */
  public double getTransparency() {
	return this.transparency;
  } // getTransparency

  /**
   * Returns the line typ of the shape.
   * @return int - line type of the shape.
   */
  public int getLineType() {
	return this.lineType;
  } // getLineType

  /**
   * Returns the x coordinate of the beginning of the arc.
   * @return int - x coordinate of the beginning of the arc.
   */
  public int getX() {
	return x;
  } // getX

  /**
   * Returns the y coordinate of the beginning of the arc.
   * @return int - y coordinate of the beginning of the arc.
   */
  public int getY() {
	return y;
  } // getY

  /**
   * Returns the width of the arc (the difference between the arc's beginning and
   * end x coordinates).
   * @return int - width of the arc.
   */
  int getWidth() {
	return this.width;
  } // getWidth

  /**
   * Returns the height of the arc (the difference between the arc's beginning and
   * end y coordinates).
   * @return int - height of the arc.
   */
  int getHeight() {
	return this.height;
  } // getHeight

  /**
   * Resizes current object.
   * @param deltaW int - change of object with.
   * @param deltaH int - change of object height.
   * @param cornerClicked int - number of the clicked corner.
   */
  public void resize(int deltaW, int deltaH, int cornerClicked) {
	if(!isFixed()) {
	  if (cornerClicked == 1) { // TOP-LEFT
		if (this.width - deltaW > 0 && this.height - 2 * deltaH > 0) {
		  this.x += deltaW;
		  this.y += deltaH;
		  this.width -= deltaW;
		  this.height -= 2 * deltaH;
		}
	  }
	  else if (cornerClicked == 2) { // TOP-RIGHT
		if (this.width + deltaW > 0 && this.height - 2 * deltaH > 0) {
		  this.y += deltaH;
		  this.width += deltaW;
		  this.height -= 2 * deltaH;
		}
	  }
	  else if (cornerClicked == 3) { // BOTTOM-LEFT
		if (this.width - deltaW > 0 && this.height + deltaH > 0) {
		  this.x += deltaW;
		  this.width -= deltaW;
		  this.height += deltaH;
		}
	  }
	  else if (cornerClicked == 4) { // BOTTOM-RIGHT
		if (this.width + deltaW > 0 && this.height + deltaH > 0) {
		  this.width += deltaW;
		  this.height += deltaH;
		}
	  }
	}
  } // resize

  public boolean contains(int pointX, int pointY) {
	if (pointX > x && pointY > y && pointX < x + width && pointY < y + height) {
		return true;
	}
	return false;
  } // contains

  /**
   * Returns the number representing a corner the mouse was clicked in.
   * 1: top-left, 2: top-right, 3: bottom-left, 4: bottom-right.
   * Returns 0 if the click was not in the corner.
   * @param pointX int - mouse x coordinate.
   * @param pointY int - mouse y coordinate.
   * @return int - corner number the mouse was clicked in.
   */
  public int controlRectContains(int pointX, int pointY) {
	if (pointX >= x && pointY >= y && pointX <= x + 4 && pointY <= y + 4) {
		return 1;
	}
	if (pointX >= x + width - 4 && pointY >= y && pointX <= x + width && pointY <= y + 4) {
		return 2;
	}
	if (pointX >= x && pointY >= y + height - 4 && pointX <= x + 4 && pointY <= y + height) {
		return 3;
	}
	if (pointX >= x + width - 4 && pointY >= y + height - 4 && pointX <= x + width && pointY <= y + height) {
		return 4;
	}
	return 0;
  } // controlRectContains

  /**
   * Return a specification of the shape to be written into a file in XML format.
   * @param boundingboxX - x coordinate of the bounding box.
   * @param boundingboxY - y coordinate of the bounding box.
   * @return String - specification of a shape.
   */
  public String toFile(int boundingboxX, int boundingboxY) {
	String fill = "false";

	if (filled) {
	  fill = "true";
	}
	int colorInt = 0;

	if (color != null) {
	  colorInt = color.getRGB();
	}
	return "<arc x=\"" + (x - boundingboxX) + "\" y=\"" + (y - boundingboxY)
		+ "\" width=\"" + width + "\" height=\"" + height + "\" startAngle=\""
		+ startAngle + "\" arcAngle=\"" + arcAngle + "\" colour=\"" + colorInt
		+ "\" filled=\"" + fill + "\" fixed=\""+isFixed()+"\" stroke=\""+(int)this.lineWeight+"\" lineType=\""+this.lineType+"\" transparency=\""+(int)this.transparency+"\"/>\n";
  } // toFile

  public String toText() {
	String fill = "false";
	if (filled) fill = "true";
	int colorInt = 0;
	if (color != null) colorInt = color.getRGB();
   return "ARC:"+x+":"+y+":"+width+":"+height+":"+startAngle+":"+arcAngle+":"+colorInt+":"+fill+":"+(int)this.lineWeight+":"+this.lineType+":"+(int)this.transparency+":"+isFixed();
  } // toText

  /**
   * Draw the selection markers if object selected.
   * @param g2 Graphics2D - shape graphics.
   */
  public void drawSelection(Graphics2D g2) {
	g2.setColor(Color.black);
	g2.setStroke(new BasicStroke( (float) 1.0));
	g2.fillRect(x, y, 4, 4);
	g2.fillRect(x + width - 4, y, 4, 4);
	g2.fillRect(x, y + height - 3, 4, 4);
	g2.fillRect(x + width - 4, y + height - 3, 4, 4);
  } // drawSelection

  /**
   * Draw the arc. Supports drawing with transparent colors.
   * @param xModifier int -
   * @param yModifier int -
   * @param Xsize float - defines the resizing multiplier (used at zooming), default: 1.0
   * @param Ysize float - defines the resizing multiplier (uset at zooming), default: 1.0
   * @param g2 Graphics - shape graphics.
   */
  public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2) {
	if(getLineType()>0) {
	  g2.setStroke(new BasicStroke(this.lineWeight, BasicStroke.CAP_BUTT,
								   BasicStroke.JOIN_ROUND, 50,
								   new float[] {getLineType(),getLineType()}
								   , 0));
    } else {
	  g2.setStroke(new BasicStroke(lineWeight));
	}

	// The user can specify the percentage of transparency between 0..100%.
	// The value of transparency is defined as a float value between 0..1.
	alpha = (float) (1 - (this.transparency / 100));

	// Separate the color to separate Red, Green and Blue, for allowing
	// the Graphics to be drawn with a transparent color.
	float red = 0;
   if(color!=null) red = (float) color.getRed() / 256;
	float green = 0;
   if(color!=null) green = (float) color.getGreen() / 256;
	float blue = 0;
   if(color!=null) blue = (float) color.getBlue() / 256;

	// Set the Graphics a new transparent color.
	g2.setColor(new Color(red, green, blue, alpha));

	int a = xModifier + (int) (Xsize * x);
	int b = yModifier + (int) (Ysize * y);
	int c = (int) (Xsize * width);
	int d = (int) (Ysize * height);

	if (filled) {
	  g2.fillArc(a,b,c,d,startAngle,arcAngle);
	}
	else {
	  g2.drawArc(a,b,c,d,startAngle,arcAngle);
	}

	// Draw selection markers if object selected.
	if (selected) {
	  drawSelection(g2);
	}

	} // draw

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {

			return null;
		}
	} // clone
} // end of class

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
	 * Line weight, logically equals to stroke width.
	 */
	private float lineWeight;

	/**
	 * Name of the shape.
	 */
	private String name;

	/**
	 * Dot transparency.
	 */
	float transparency = (float) 1.0;

	/**
	 * Indicates if the shape is selected or not.
	 */
	private boolean selected = false;

	/**
	 * Indicates if the dot is filled or not.
	 */
	private boolean filled = false;

	/**
	 * Defines if the shape is resizable or not.
	 */
	private boolean fixed = false;

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

	public void setFixed(boolean b) {
		this.fixed = b;
	}

	public boolean isFixed() {
		return this.fixed;
	}

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

	public int getRealHeight() {
		return getHeight();
	} // getRealHeight

	public int getRealWidth() {
		return getWidth();
	} // getRealWidth

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if (x1 > x && y1 > y && x2 < x && y2 < y) {
			return true;
		}
		return false;
	} // isInside

	public boolean isInsideRect(int x1, int y1, int x2, int y2) {
		if (x1 < x && y1 < y && x2 > x && y2 > y) {
			return true;
		}
		return false;
	} // isInsideRect

	public boolean contains(int pointX, int pointY) {
		if (pointX > x && pointY > y && pointX < x && pointY < y) {
			return true;
		}
		return false;
	} // contains

	/**
	 * Set size using zoom multiplication.
	 * @param s1 float - set size using zoom multiplication.
	 * @param s2 float - set size using zoom multiplication.
	 */
	public void setMultSize(float s1, float s2) {
		x = x * (int) s1 / (int) s2;
		y = y * (int) s1 / (int) s2;
		width = width * (int) s1 / (int) s2;
		height = height * (int) s1 / (int) s2;
	} // setMultSize

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
		this.x = getX() + x;
		this.y = getY() + y;
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
		this.lineWeight = (float) width;
	} // setStrokeWidth

	/**
	 * Set the percentage of transparency.
	 * @param transparencyPercentage double - the percentage of transparency.
	 */
	public void setTransparency(double transparencyPercentage) {
		this.transparency = (float) transparencyPercentage;
	} // setTransparency

	public void setLineType(int lineType) {
	}

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
	 * Returns the line typ of the shape.
	 * @return int - line type of the shape.
	 */
	public int getLineType() {
		return 0;
	} // getLineType

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
			+ "\" colour=\"" + colorInt + "\" fixed=\"" + isFixed() + "\" stroke=\"" + (int) this.lineWeight + "\" transparency=\"" + (int) this.transparency + "\"/>\n";
	} // toFile

	public String toText() {
		int colorInt = 0;
		if (color != null) colorInt = color.getRGB();
		return "DOT:" + x + ":" + y + ":" + width + ":" + height + ":" + colorInt + ":" + (int) this.lineWeight + ":" + (int) this.transparency + ":" + isFixed();
	} // toText

	/**
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
		if (!isFixed()) {
			if (cornerClicked == 1) { // TOP-LEFT
				if (this.width - deltaW > 0 && this.height - deltaH > 0) {
					this.x += deltaW;
					this.y += deltaH;
					this.width -= deltaW;
					this.height -= deltaH;
				}
			} else if (cornerClicked == 2) { // TOP-RIGHT
				if (this.width + deltaW > 0 && this.height - deltaH > 0) {
					this.y += deltaH;
					this.width += deltaW;
					this.height -= deltaH;
				}
			} else if (cornerClicked == 3) { // BOTTOM-LEFT
				if (this.width - deltaW > 0 && this.height + deltaH > 0) {
					this.x += deltaW;
					this.width -= deltaW;
					this.height += deltaH;
				}
			} else if (cornerClicked == 4) { // BOTTOM-RIGHT
				if (this.width + deltaW > 0 && this.height + deltaH > 0) {
					this.width += deltaW;
					this.height += deltaH;
				}
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
   * Draw the selection markers if object selected.
   * @param g2 Graphics2D - shape graphics.
   */
  public void drawSelection(Graphics2D g2) {
	g2.setColor(Color.black);
	g2.setStroke(new BasicStroke( (float) 1.0));
	g2.drawRect(x, y, 4, 4);
	g2.drawRect(x + width - 4, y, 4, 4);
	g2.drawRect(x, y + height - 4, 4, 4);
	g2.drawRect(x + width - 4, y + height - 4, 4, 4);
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
	Graphics2D g2 = (Graphics2D) g;

	g2.setStroke(new BasicStroke(this.lineWeight));

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

	int w = (int) this.lineWeight / 2;

	int a = xModifier + (int) (sizeX * x);
	int b = yModifier + (int) (sizeY * y);

	g2.drawRect(a, b, w, w);

	// Draw selection markers if object selected.
	if (selected) {
	  drawSelection(g2);
	}
	}



}

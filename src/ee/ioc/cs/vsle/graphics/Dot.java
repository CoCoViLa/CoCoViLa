package ee.ioc.cs.vsle.graphics;

import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * <p>Title: ee.ioc.cs.editor.graphics.Dot</p>
 * <p>Description: Shape of type ee.ioc.cs.editor.graphics.Dot</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class Dot
	extends Shape
	implements Serializable {

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
	 */private float alpha;

	/**
	 * Line weight, logically equals to stroke width.
	 */
	private float lineWeight;

	/**
	 * ee.ioc.cs.editor.graphics.Dot stroke width.
	 */
	private BasicStroke stroke;
	double weight;

	/**
	 * ee.ioc.cs.editor.graphics.Dot transparency.
	 */
	float transparency = (float) 1.0;

	/**
	 * Rotation angle in degrees.
	 */
	double rotation = 0.0;

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
		this.weight = strokeWidth;
		this.transparency = (float) transp;
		lineWeight = (float) strokeWidth;
		stroke = new BasicStroke(lineWeight);
	} // ee.ioc.cs.editor.graphics.Dot

	/**
	 * Set the color of a shape.
	 * @param col Color - color of a shape.
	 */
	public void setColor(Color col) {
		this.color = col;
	} // setColor

	public void setFont(java.awt.Font f) {}

	public void setText(String s) {}

	/**
	 * Set width of the line stroke the rectangle is drawn with.
	 * @param width double - rectangle drawing line stroke width.
	 */
	public void setStrokeWidth(double width) {} // setStrokeWidth

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
		return this.stroke.getLineWidth();
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
		return "<rect x=\"" + (x - boundingboxX) + "\" y=\"" + (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height + "\" colour=\"" + colorInt + "\"/>";
	} // toFile

	/**
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
		db.p("width=" + this.width + ", height=" + this.height);
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
		if ( (pointX >= x + (int) (size * (width)) - 4) && (pointY >= y + (int) (size * (height)) - 4)) {
			if ( (pointX <= x + (int) (size * (width))) && (pointY <= y + (int) (size * (height)))) {
				return 4;
			}
		}
		return 0;
	} // controlRectContains

	/**
	 * Draw the selection markers if object selected.
	 * @param g Graphics2D - Object's Graphics.
	 */
	private void drawSelection(Graphics2D g) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke( (float) 1.0));
		g.drawRect(x, y, 4, 4);
		g.drawRect(x + (int) (size * width) - 4, y, 4, 4);
		g.drawRect(x, y + (int) (size * height) - 4, 4, 4);
		g.drawRect(x + (int) (size * width) - 4, y + (int) (size * height) - 4, 4, 4);
	} // drawSelection

	/**
	 * Specify rotation angle.
	 * @param degrees double - rotation angle.
	 */
	public void setRotation(double degrees) {
		this.rotation = degrees;
	} // setRotation

	/**
	 * Draw the dot. Supports drawing with transparent colors.
	 * @param xModifier int
	 * @param yModifier int
	 * @param size float - defines the resizing multiplier (used at zooming), default: 1.0
	 * @param g Graphics
	 */
	void draw(int xModifier, int yModifier, float size, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(stroke);

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

		int w = (int) weight / 2;

		g2.drawRect(xModifier + x, yModifier + y, w, w);

		// Draw selection markers if object selected.
		if (selected) {
			drawSelection(g2);
		}

	} // draw

}

package ee.ioc.cs.vsle.graphics;

import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Arc
	extends Shape
	implements Serializable {

	/**
	 * ee.ioc.cs.editor.graphics.Arc angles.
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
	 * ee.ioc.cs.editor.graphics.Arc stroke width.
	 */private BasicStroke stroke;

	/**
	 * ee.ioc.cs.editor.graphics.Line weight, logically equals to stroke width.
	 */
	private float lineWeight;

	/**
	 * ee.ioc.cs.editor.graphics.Arc transparency.
	 */
	float transparency = (float) 1.0;

	/**
	 * Rotation angle in degrees.
	 */
	double rotation = 0.0;

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
	 */
	public Arc(int x, int y, int width, int height, int startAngle, int arcAngle, int colorInt, boolean fill, double strokeWidth, double transp) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startAngle = startAngle;
		this.arcAngle = arcAngle;
		this.color = new Color(colorInt);
		this.filled = fill;
		this.transparency = (float) transp;
		setStrokeWidth(strokeWidth);
	} // ee.ioc.cs.editor.graphics.Arc

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
	 * Set the percentage of transparency.
	 * @param transparencyPercentage double - the percentage of transparency.
	 */
	public void setTransparency(double transparencyPercentage) {
		this.transparency = (float) transparencyPercentage;
	} // setTransparency

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
		return this.stroke.getLineWidth();
	} // getStrokeWidth

	/**
	 * Set width of the line stroke the rectangle is drawn with.
	 * @param width double - rectangle drawing line stroke width.
	 */
	public void setStrokeWidth(double width) {
		try {
			if (width >= 0.0) {
				lineWeight = (float) width;
				stroke = new BasicStroke(lineWeight);
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
		return width;
	} // getWidth

	/**
	 * Returns the height of the arc (the difference between the arc's beginning and
	 * end y coordinates).
	 * @return int - height of the arc.
	 */
	int getHeight() {
		return height;
	} // getHeight

	/**
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
		db.p("width=" + this.width + ", height=" + this.height);
		if (cornerClicked == 1) { // TOP-LEFT
			if ( (this.width - deltaW) > 0 && (this.height - 2 * deltaH) > 0) {
				this.x += deltaW;
				this.y += deltaH;
				this.width -= deltaW;
				this.height -= 2 * deltaH;
			}
		}
		else if (cornerClicked == 2) { // TOP-RIGHT
			if ( (this.width + deltaW) > 0 && (this.height - 2 * deltaH) > 0) {
				this.y += deltaH;
				this.width += deltaW;
				this.height -= 2 * deltaH;
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
		if ( (pointX >= x) && (pointY >= y + (int) (size * (height / 2)) - 4)) {
			if ( (pointX <= x + 4) && (pointY <= y + (int) (size * (height / 2)))) {
				return 3;
			}
		}
		if ( (pointX >= x + (int) (size * (width)) - 4) && (pointY >= y + (int) (size * (height / 2)) - 4)) {
			if ( (pointX <= x + (int) (size * (width))) && (pointY <= y + (int) (size * (height / 2)))) {
				return 4;
			}
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
		return "<arc x=\"" + (x - boundingboxX) + "\" y=\"" + (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height + "\" startAngle=\"" + startAngle + "\" arcAngle=\"" + arcAngle + "\" colour=\"" + colorInt + "\" filled=\"" + fill + "\"/>";
	} // toFile

	/**
	 * Draw the selection markers if object selected.
	 * @param g Graphics2D - Object's Graphics.
	 */
	private void drawSelection(Graphics2D g) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke( (float) 1.0));
		g.fillRect(x, y, 4, 4);
		g.fillRect(x + (int) (size * width) - 4, y, 4, 4);
		g.fillRect(x, y + (int) (size * height / 2) - 4, 4, 4);
		g.fillRect(x + (int) (size * width) - 4, y + (int) (size * height / 2) - 4, 4, 4);
	} // drawSelection

	/**
	 * Specify rotation angle.
	 * @param degrees double - rotation angle.
	 */
	public void setRotation(double degrees) {
		this.rotation = degrees;
	} // setRotation

	/**
	 * Draw the arc. Supports drawing with transparent colors.
	 * @param xModifier int
	 * @param yModifier int
	 * @param size float - defines the resizing multiplier (used at zooming), default: 1.0
	 * @param g Graphics
	 */
	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics g) {

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

		if (isAntialiasingOn()) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}

		if (filled) {
			g2.fillArc(xModifier + (int) (Xsize * x), yModifier + (int) (Ysize * y), (int) (Xsize * width), (int) (Ysize * height), startAngle, arcAngle);
		}
		else {
			g2.drawArc(xModifier + (int) (Xsize * x), yModifier + (int) (Ysize * y), (int) (Xsize * width), (int) (Ysize * height), startAngle, arcAngle);
		}

		// Draw selection markers if object selected.
		if (selected) {
			drawSelection(g2);
		}

	} // draw

}

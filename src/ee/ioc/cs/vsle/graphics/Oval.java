package ee.ioc.cs.vsle.graphics;

import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;

public class Oval
	extends Shape
	implements Serializable {

	boolean filled = false;
	Color color;
	private BasicStroke stroke;
	float transparency = (float) 1.0;

	/**
	 * Alpha value of a color, used
	 * for defining the transparency of a filled shape.
	 */
	private float alpha;

	/**
	 * Rotation angle in degrees.
	 */
	double rotation = 0.0;

	/**
	 * ee.ioc.cs.editor.graphics.Line weight, logically equals to stroke width.
	 */
	private float lineWeight;

	public Oval(int x, int y, int width, int height, int colorInt, boolean fill, double strokeWidth, double transp) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = new Color(colorInt);
		this.filled = fill;
		this.transparency = (float) transp;
		setStrokeWidth(strokeWidth);
	}

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
	}

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
	 * Returns the color of the oval.
	 * @return Color - color of the oval.
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
	 * Returns the transparency of the shape.
	 * @return double - the transparency of the shape.
	 */
	public double getTransparency() {
		return this.transparency;
	} // getTransparency

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	int getWidth() {
		return width;
	}

	int getHeight() {
		return height;
	}

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
		return "<oval x=\"" + (x - boundingboxX) + "\" y=\"" + (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height + "\" colour=\"" + colorInt + "\" filled=\"" + fill + "\"/>";
	} // toFile

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
		g.fillRect(x, y, 4, 4);
		g.fillRect(x + (int) (size * width) - 4, y, 4, 4);
		g.fillRect(x, y + (int) (size * height) - 4, 4, 4);
		g.fillRect(x + (int) (size * width) - 4, y + (int) (size * height) - 4, 4, 4);
	} // drawSelection

	/**
	 * Specify rotation angle.
	 * @param degrees double - rotation angle.
	 */
	public void setRotation(double degrees) {
		this.rotation = degrees;
	} // setRotation

	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(stroke);

		alpha = (float) (1 - (this.transparency / 100));

		float red = (float) color.getRed() * 100 / 256 / 100;
		float green = (float) color.getGreen() * 100 / 256 / 100;
		float blue = (float) color.getBlue() * 100 / 256 / 100;

		g2.setColor(new Color(red, green, blue, alpha));

		if (isAntialiasingOn()) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}

		if (filled) {
			g2.fillOval(xModifier + (int) (Xsize * x), yModifier + (int) (Ysize * y), (int) (Xsize * width), (int) (Ysize * height));
		}
		else {
			g2.drawOval(xModifier + (int) (Xsize * x), yModifier + (int) (Ysize * y), (int) (Xsize * width), (int) (Ysize * height));
		}
		if (selected) {
			drawSelection(g2);
		}

	}
}

package ee.ioc.cs.vsle.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.awt.Font;
import java.awt.BasicStroke;

public class Text
	extends Shape
	implements Serializable {

	String s;
	Font font;
	Color color;
	float transparency;

	/**
	 * Alpha value of a color, used
	 * for defining the transparency of a filled shape.
	 */
	private float alpha;

	public Text(int x, int y, Font font, Color color, double transp, String s) {
		this.x = x;
		this.y = y;
		this.font = font;
		this.color = color;
		this.s = s;
		this.transparency = (float) transp;
	}

	public void setStrokeWidth() {}

	/**
	 * Returns the used font.
	 * @return Font - used font.
	 */
	public Font getFont() {
		return this.font;
	} // getFont

	/**
	 * Returns the text string.
	 * @return String - the text string.
	 */
	public String getText() {
		return this.s;
	} // getText

	/**
	 * Set the color of a shape.
	 * @param col Color - color of a shape.
	 */
	public void setColor(Color col) {
		this.color = col;
	} // setColor

	/**
	 * Set string font.
	 * @param f Font - string font.
	 */
	public void setFont(Font f) {
		this.font = f;
	} // setFont

	/**
	 * Set string text.
	 * @param s String - string text.
	 */
	public void setText(String s) {
		this.s = s;
	} // setText

	/**
	 * Returns the transparency of the shape.
	 * @return double - the transparency of the shape.
	 */
	public double getTransparency() {
		return this.transparency;
	} // getTransparency

	/**
	 * Returns the color of the text.
	 * @return Color - color of the text.
	 */
	public Color getColor() {
		return this.color;
	} // getColor

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
	 * Draw the selection markers if object selected.
	 * @param g Graphics2D - Object's Graphics.
	 */
	private void drawSelection(Graphics2D g) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke( (float) 1.0));
		g.drawRect(x, y, 4, 4);
		g.drawRect(x + (int) (size * (g.getClipBounds().getWidth())) - 4, y, 4, 4);
		g.drawRect(x, y + (int) (size * (g.getClipBounds().getHeight())) - 4, 4, 4);
		g.drawRect(x + (int) (size * (g.getClipBounds().getWidth())) - 4, y + (int) (size * (g.getClipBounds().getHeight())) - 4, 4, 4);
	} // drawSelection

	/**
	 * Set the percentage of transparency.
	 * @param transparencyPercentage double - the percentage of transparency.
	 */
	public void setTransparency(double transparencyPercentage) {
		this.transparency = (float) transparencyPercentage;
	} // setTransparency

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
		return "<text string=\"" + s + "\" colour=\"" + colorInt + "\" x=\"" + (x - boundingboxX) + "\" y=\"" + (y - boundingboxY) + "\" fontname=\"" + font.getName() + "\" fontstyle=\"" + font.getStyle() + "\" fontsize=\"" + font.getSize() + "\"/>";
	} // toFile

	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics g) {
		System.out.println("x=" + x + ", y=" + y);
		Graphics2D g2 = (Graphics2D) g;

		g2.setFont(font);

		alpha = (float) (1 - (this.transparency / 100));

		float red = (float) color.getRed() * 100 / 256 / 100;
		float green = (float) color.getGreen() * 100 / 256 / 100;
		float blue = (float) color.getBlue() * 100 / 256 / 100;

		g2.setColor(new Color(red, green, blue, alpha));

		if (isAntialiasingOn()) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g2.drawString(s, xModifier + (int) (Xsize * x), yModifier + (int) (Ysize * y));

		this.width = g2.getClipBounds().width;
		this.height = g2.getClipBounds().height;

		if (selected) {
			drawSelection(g2);
		}

	}

}

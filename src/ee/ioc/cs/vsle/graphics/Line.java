package ee.ioc.cs.vsle.graphics;

import java.io.Serializable;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Line
	extends Shape
	implements Serializable {

	int startX;
	int startY;
	int endX;
	int endY;

	/**
	 * ee.ioc.cs.editor.graphics.Line weight, logically equals to stroke width.
	 */
	private float lineWeight;

	Color color;
	private BasicStroke stroke;
	float transparency = (float) 1.0;

	/**
	 * Alpha value of a color, used
	 * for defining the transparency of a filled shape.
	 */
	private float alpha;

	double rotation = 0.0;

	public Line(int x1, int y1, int x2, int y2, int colorInt, double strokeWidth, double transp) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
		this.color = new Color(colorInt);
		setStrokeWidth(strokeWidth);
		this.transparency = (float) transp;
		this.x = Math.min(x1, x2);
		this.y = Math.min(y1, y2);
		this.width = Math.max(x1, x2) - this.x; // endX - startX;
		this.height = Math.max(y1, y2) - this.y; // endY - startY;
	}

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
	 * Set the color of a shape.
	 * @param col Color - color of a shape.
	 */
	public void setColor(Color col) {
		this.color = col;
	} // setColor

	/**
	 * Returns the color of the line.
	 * @return Color - color of the line.
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
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
		if (cornerClicked == 1) { // TOP-LEFT
			if ( (this.width - deltaW) >= 0 && (this.height - deltaH) >= 0) {
				this.endX += deltaW;
				this.endY += deltaH;
				this.width -= deltaW;
				this.height -= deltaH;
			}
		}
		else if (cornerClicked == 2) { // TOP-RIGHT
			if ( (this.width + deltaW) >= 0 && (this.height - deltaH) >= 0) {
				this.endX += deltaW;
				this.endY += deltaH;
				this.width += deltaW;
				this.height -= deltaH;
			}
		}
		else if (cornerClicked == 3) { // BOTTOM-LEFT
			if ( (this.width - deltaW) >= 0 && (this.height + deltaH) >= 0) {
				this.startX += deltaW;
				this.startY += deltaH;
				this.width -= deltaW;
				this.height -= deltaH;
			}
		}
		else if (cornerClicked == 4) { // BOTTOM-RIGHT
			if ( (this.width + deltaW) >= 0 && (this.height + deltaH) >= 0) {
				this.startX += deltaW;
				this.startY += deltaH;
				this.width -= deltaW;
				this.height -= deltaH;
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

	public void setPosition(int x, int y) {

		if (this.endX > this.startX) {
			this.endX = this.endX + (x - startX);
			this.startX = x;
		}
		else {
			this.startX = this.startX + (x - endX);
			this.endX = x;
		}

		if (this.endY > this.startY) {
			this.endY = this.endY + (y - startY);
			this.startY = y;
		}
		else {
			this.startY = this.startY + (y - endY);
			this.endY = y;
		}

		// this.endY = this.endY + (y - startY);
		// this.startY = y;
		this.x = x;
		this.y = y;
	}

	public boolean contains(int pointX, int pointY) {
		float distance = calcDistance(startX, startY, endX, endY, pointX, pointY);

		if (distance <= 3) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Calculates the distance of a point from a line give by 2 points.
	 * @param x1 int
	 * @param y1 int
	 * @param x2 int
	 * @param y2 int
	 * @param pointX int
	 * @param pointY int
	 * @return float
	 */
	float calcDistance(int x1, int y1, int x2, int y2, int pointX, int pointY) {
		int calc1 = (pointX - x1) * (x2 - x1) + (pointY - y1) * (y2 - y1);
		int calc2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

		float U = (float) calc1 / (float) calc2;

		float intersectX = x1 + U * (x2 - x1);
		float intersectY = y1 + U * (y2 - y1);

		double distance = Math.sqrt( (pointX - intersectX) * (pointX - intersectX) + (pointY - intersectY) * (pointY - intersectY));

		double distanceFromEnd1 = Math.sqrt( (x1 - pointX) * (x1 - pointX) + (y1 - pointY) * (y1 - pointY));
		double distanceFromEnd2 = Math.sqrt( (x2 - pointX) * (x2 - pointX) + (y2 - pointY) * (y2 - pointY));
		double lineLength = Math.sqrt( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		if (lineLength < Math.max(distanceFromEnd1, distanceFromEnd2)) {
			distance = Math.max(Math.min(distanceFromEnd1, distanceFromEnd2), distance);
		}
		return (float) distance;
	} // calcDistance

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

	void setLine(int x1, int y1, int x2, int y2) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	/**
	 * Returns the line end x coordinate.
	 * @return int - line end x coordinate.
	 */
	public int getEndX() {
		return endX;
	} // getEndX

	/**
	 * Returns the line end y coordinate.
	 * @return int - line end y coordinate.
	 */
	public int getEndY() {
		return endY;
	} // getEndY

	int getX1() {
		return startX;
	}

	int getY1() {
		return startY;
	}

	int getX2() {
		return endX;
	}

	int getY2() {
		return endY;
	}

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
		return "<line x1=\"" + (startX - boundingboxX) + "\" y1=\"" + (startY - boundingboxY) + "\" x2=\"" + (endX - boundingboxX) + "\" y2=\"" + (endY - boundingboxY) + "\" colour=\"" + colorInt + "\"/>";
	} // toFile

	/**
	 * Draw the selection markers if object selected.
	 * @param g Graphics2D - Object's Graphics.
	 */
	private void drawSelection(Graphics2D g) {
		g.setColor(Color.black);
		g.setStroke(new BasicStroke( (float) 1.0));
		g.fillRect(startX - 2, startY - 2, 4, 4);
		g.fillRect(endX - 2, endY - 2, 4, 4);
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

		g2.drawLine(xModifier + (int) (Xsize * startX), yModifier + (int) (Ysize * startY), xModifier + (int) (Xsize * endX), yModifier + (int) (Ysize * endY));

		this.width = Math.abs(getStartX() - getEndX());
		this.height = Math.abs(getStartY() - getEndY());

		if (selected) {
			drawSelection(g2);
		}

	}
}

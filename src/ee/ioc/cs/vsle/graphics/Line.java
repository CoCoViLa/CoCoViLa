package ee.ioc.cs.vsle.graphics;

import java.io.*;

import ee.ioc.cs.vsle.util.*;

import java.awt.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.editor.Canvas;

public class Line extends Shape implements Serializable {

	int startX;
	int startY;
	int endX;
	int endY;
	public int fixedX1;
	public int fixedX2;
	public int fixedY1;
	public int fixedY2;
	String x1Exp;
    String x2Exp;
	String y1Exp;
	String y2Exp;


	private BasicStroke stroke;
	Color color;

	private boolean selected = false;

	private boolean fixed = false;

	public Line(int x1, int y1, int x2, int y2, int colorInt, double strokeWidth, double transp, int lineType) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
		color = new Color(colorInt);
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)transp);

		if (lineType > 0) {
			stroke = new BasicStroke((float)strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 50,
				new float[]{lineType, lineType}, 0);
		} else {
			stroke = new BasicStroke((float)strokeWidth);
		}
		this.x = Math.min(x1, x2);
		this.y = Math.min(y1, y2);
		this.width = Math.max(x1, x2) - this.x; // endX - startX;
		this.height = Math.max(y1, y2) - this.y; // endY - startY;
	} // Line

	public void setFixed(boolean b) {
		this.fixed = b;
	} // setFixed

	public boolean isFixed() {
		return this.fixed;
	} // isFixed

	public void setStrokeWidth(double d) {
        stroke = new BasicStroke((float)d, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
	}

	public void setTransparency(int d) {
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), d);
	}

	public void setLineType(int lineType) {
		stroke = new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), new float[]{lineType, lineType}, stroke.getDashPhase());
	}

	public boolean isFilled() {
		return false;
	} // isFilled

	public int getRealHeight() {
		return getHeight();
	} // getRealHeight

	public BasicStroke getStroke() {
		return stroke;
	}

	public double getStrokeWidth() {
		return stroke.getLineWidth();
	}

	public int getLineType() {
		if (stroke.getDashArray() != null)
			return (int)stroke.getDashArray()[0];
		else
			return 0;
	} // getLineType

	public int getTransparency() {
		return color.getAlpha();
	} // getTransparency

	public String getName() {
		return null;
	}

	public int getRealWidth() {
		return getWidth();
	} // getRealWidth

	public void setSelected(boolean b) {
		this.selected = b;
	} // setSelected

	public boolean isInside(int x1, int y1, int x2, int y2) {
		int minx = Math.min(startX, endX);
		int miny = Math.min(startX, startY);
		int maxx = Math.max(startX, endX);
		int maxy = Math.max(startX, startY);

		if (x1 > minx && y1 > miny && x2 < maxx && y2 < maxy) {
			return true;
		}
		return false;
	} // isInside

	public boolean isInsideRect(int x1, int y1, int x2, int y2) {
		int minx = Math.min(startX, endX);
		int miny = Math.min(startY, endY);
		int maxx = Math.max(startX, endX);
		int maxy = Math.max(endY, startY);
		if (x1 < minx && y1 < miny && x2 > maxx && y2 > maxy) {
			return true;
		}
		return false;
	} // isInsideRect

	/**
	 * Set size using zoom multiplication.
	 * @param s1 float - set size using zoom multiplication.
	 * @param s2 float - set size using zoom multiplication.
	 */
	public void setMultSize(float s1, float s2) {
		startX = startX * (int) s1 / (int) s2;
		startY = startY * (int) s1 / (int) s2;
		endX = endX * (int) s1 / (int) s2;
		endY = endY * (int) s1 / (int) s2;
		width = width * (int) s1 / (int) s2;
		height = height * (int) s1 / (int) s2;
	} // setMultSize

	public boolean isSelected() {
		return this.selected;
	} // isSelected


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
	 * Returns the transparency of the shape.
	 * @return double - the transparency of the shape.
	 */


	/**
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
		if (!isFixed()) {
			if (cornerClicked == 1) { // TOP-LEFT
				this.startX += deltaW;
				this.startY += deltaH;
			} else if (cornerClicked == 2) { // TOP-RIGHT
				endX += deltaW;
				endY += deltaH;
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
		db.p(pointX + " " + pointY + " " + endX + " " + endY + " " + startX + " " + startY + " ");
		if (pointX >= startX - 2 && pointY >= startY - 2 && pointX <= startX + 2 && pointY <= startY + 2) {
			return 1;
		}
		if (pointX >= endX - 2 && pointY >= endY - 2 && pointX <= endX + 2 && pointY <= endY + 2) {
			return 2;
		}
		return 0;
	} // controlRectContains

	public void setPosition(int x, int y) {
		endX = endX + x;
		startX = startX + x;
		endY = endY + y;
		startY = startY + y;
	} // setPosition

	public boolean contains(int pointX, int pointY) {
		float distance = calcDistance(startX, startY, endX, endY, pointX, pointY);
		if (distance <= 3) {
			return true;
		} else {
			return false;
		}
	} // contains

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

		double distance = Math.sqrt(
			(pointX - intersectX) * (pointX - intersectX)
			+ (pointY - intersectY) * (pointY - intersectY));

		double distanceFromEnd1 = Math.sqrt(
			(x1 - pointX) * (x1 - pointX) + (y1 - pointY) * (y1 - pointY));
		double distanceFromEnd2 = Math.sqrt(
			(x2 - pointX) * (x2 - pointX) + (y2 - pointY) * (y2 - pointY));
		double lineLength = Math.sqrt(
			(x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		if (lineLength < Math.max(distanceFromEnd1, distanceFromEnd2)) {
			distance = Math.max(Math.min(distanceFromEnd1, distanceFromEnd2),
				distance);
		}
		return (float) distance;
	} // pointDistanceFromLine

	void setLine(int x1, int y1, int x2, int y2) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
	} // setLine

	public int getStartX() {
		return startX;
	} // getStartX

	public int getStartY() {
		return startY;
	} // getStartY

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
	} // getX1

	int getY1() {
		return startY;
	} // getY1

	int getX2() {
		return endX;
	} // getX2

	int getY2() {
		return endY;
	} // getY2

	public int getX() {
		return x;
	} // getX

	public int getY() {
		return y;
	} // getY

	int getWidth() {
		return width;
	} // getWidth

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

		if (color != null) colorInt = color.getRGB();

		return "<line x1=\"" + (startX - boundingboxX) + "\" y1=\""
			+ (startY - boundingboxY) + "\" x2=\"" + (endX - boundingboxX)
			+ "\" y2=\"" + (endY - boundingboxY) + "\" colour=\"" + colorInt
			+ "\" fixed=\"" + isFixed() + "\" stroke=\"" + (int)stroke.getLineWidth() + "\" lineType=\"" + this.getLineType() + "\" transparency=\"" + getTransparency() + "\"/>\n";
	} // toFile

	public String toText() {
		int colorInt = 0;
		if (color != null) colorInt = color.getRGB();
		return "LINE:" + startX + ":" + startY + ":" + endX + ":" + endY + ":" + colorInt + ":" + (int)stroke.getLineWidth()+ ":" + getLineType()+ ":" + getTransparency() + ":" + isFixed();
	} // toText

	/**
	 * Draw the selection markers if object selected.
	 * @param g2 Graphics2D - shape graphics.
	 */
	public void drawSelection(Graphics2D g2) {
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke((float) 1.0));
		g2.fillRect(startX - 2, startY - 2, 4, 4);
		g2.fillRect(endX - 2, endY - 2, 4, 4);
	} // drawSelection

	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2) {
		g2.setColor(color);
	  	g2.setStroke(stroke);

		int a = 0, b = 0, c = 0, d = 0;
		if (fixedX1 == 0)
			a = xModifier + (int) (Xsize * startX);
		else if (fixedX1 == -1)
			a = xModifier + startX;
		else
			a = xModifier + (int)(Xsize * startX) - fixedX1;

		if (fixedX2 == 0)
			c = xModifier + (int) (Xsize * endX);
		else if (fixedX2 == -1)
			c = xModifier + endX;
		else
			c = xModifier + (int)(Xsize * endX) - fixedX2;

		if (fixedY1 == 0)
			b = yModifier + (int) (Ysize * startY);
		else if (fixedY1 == -1)
			b = yModifier + startY;
		else
			b = yModifier + (int)(Ysize * startY) - fixedY1;

		if (fixedY2 == 0)
			d = yModifier + (int) (Ysize * endY);
		else if (fixedY2 == -1)
			d = yModifier + endY;
		else
			d = yModifier + (int)(Ysize * endY) - fixedY2;

		g2.drawLine(a, b, c, d);

		if (selected) {
			drawSelection(g2);
		}

	} // draw

	public Object clone() {
		return super.clone();
	} // clone


}

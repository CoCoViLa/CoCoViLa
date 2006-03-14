package ee.ioc.cs.vsle.graphics;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 15.06.2004
 * Time: 16:14:17
 * To change this template use Options | File Templates.
 */
public class Polygon extends Shape {

    private static final long serialVersionUID = 1L;

    boolean filled = false;
    int[] xPoints;
	int[] yPoints;
	int[] xFixed;
	int[] yFixed;


	private BasicStroke stroke;
	/**
	 * Line weight, logically equals to stroke width.
	 */
	Color color;

	/**
	 * Indicates if the shape is selected or not.
	 */
	private boolean selected = false;

	/**
	 * Defines if the shape is resizable or not.
	 */
	private boolean fixed = false;

/*	public Line(int x1, int y1, int x2, int y2, int colorInt, double strokeWidth, double transp) {
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
		this.color = new Color(colorInt);
		this.lineWeight = (float) strokeWidth;
		this.transparency = (float) transp;
		this.x = Math.min(x1, x2);
		this.y = Math.min(y1, y2);
		this.width = Math.max(x1, x2) - this.x; // endX - startX;
		this.height = Math.max(y1, y2) - this.y; // endY - startY;
	}*/

	public Polygon(int[] xPoints, int[] yPoints, int colorInt, double strokeWidth, double transp, int lineType) {


		color = new Color(colorInt);
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)transp);

		if (lineType > 0) {
			stroke = new BasicStroke((float)strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 50,
				new float[]{lineType, lineType}, 0);
		} else {
			stroke = new BasicStroke((float)strokeWidth);
		}
	} // Line

	public Polygon(int colorInt, boolean b, double strokeWidth, double transp, int lineType) {
		filled = b;
		color = new Color(colorInt);
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)transp);
		if (lineType > 0) {
			stroke = new BasicStroke((float)strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 50,
				new float[]{lineType, lineType}, 0);
		} else {
			stroke = new BasicStroke((float)strokeWidth);
		}

	}

	public void setPoints(int[] xs, int[] ys, int[] fxs, int[] fys) {
		xPoints = xs;
		yPoints = ys;
		xFixed = fxs;
		yFixed = fys;
	}

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

	public double getStrokeWidth() {
		return stroke.getLineWidth();
	}

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
		return false;
	} // isInside

	public boolean isInsideRect(int x1, int y1, int x2, int y2) {
		return false;
	} // isInsideRect

	/**
	 * Set size using zoom multiplication.
	 * @param s1 float - set size using zoom multiplication.
	 * @param s2 float - set size using zoom multiplication.
	 */
	public void setMultSize(float s1, float s2) {
		width = width * (int) s1 / (int) s2;
		height = height * (int) s1 / (int) s2;
	} // setMultSize

	public boolean isSelected() {
		return this.selected;
	} // isSelected

	/**
	 * Returns the line typ of the shape.
	 * @return int - line type of the shape.
	 */
	public int getLineType() {
		if (stroke.getDashArray() != null)
			return (int)stroke.getDashArray()[0];
		else
			return 0;
	} // getLineType

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
	public int getTransparency() {
		return color.getAlpha();
	} // getTransparency

	/**
	 * Resizes current object.
	 * @param deltaW int - change of object with.
	 * @param deltaH int - change of object height.
	 * @param cornerClicked int - number of the clicked corner.
	 */
	public void resize(int deltaW, int deltaH, int cornerClicked) {
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
        return 0;
	} // controlRectContains

	public void setPosition(int x, int y) {
	} // setPosition

	public boolean contains(int pointX, int pointY) {
		return false;
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

		return null;
	} // toFile

	public String toText() {
		return null;
	} // toText

	/**
	 * Draw the selection markers if object selected.
	 * @param g2 Graphics2D - shape graphics.
	 */
	public void drawSelection(Graphics2D g2) {
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke((float) 1.0));
	} // drawSelection

	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2) {
		g2.setColor(color);
	  	g2.setStroke(stroke);

        int[] x = new int[xPoints.length];
		int[] y = new int[xPoints.length];

		for (int i = 0; i < xPoints.length; i++) {
			if (xFixed[i] == 0)
            	x[i] = (int)(xModifier + Xsize * xPoints[i]);
			else if (xFixed[i] == -1)
				x[i] = xModifier + xPoints[i];
			else
				x[i] = (int)(xModifier + Xsize * xPoints[i]) - xFixed[i];

			if (yFixed[i] == 0)
            	y[i] = (int)(yModifier + Ysize * yPoints[i]);
			else if (yFixed[i] == -1)
				y[i] = yModifier + yPoints[i];
			else
				y[i] = (int)(yModifier + Ysize * yPoints[i]) - yFixed[i];
		}


		if (filled) {
			g2.fillPolygon(x, y, xPoints.length);
		} else {
			g2.drawPolygon(x, y, xPoints.length);
		}


		if (selected) {
			drawSelection(g2);
		}

	} // draw

	public BasicStroke getStroke() {
		return stroke;
	}
	public Polygon clone() {
		return (Polygon) super.clone();
	} // clone

}

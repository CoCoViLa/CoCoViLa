package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;
import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.util.db;

import java.awt.*;

public class Rect extends Shape implements Serializable {
	String xEquation;
	String yEquation;
	String widthEquation;
	String heightEquation;
	boolean filled = false;
	Color color;
    private BasicStroke stroke;
	String name;
	private boolean selected = false;

	/**
	 * Defines if the shape is resizable or not.
	 */
	private boolean fixed = false;

	public Rect(int x, int y, int width, int height, int colorInt, boolean filled,
				double strokeWidth, int transp) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = new Color(colorInt);
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), transp);
		this.filled = filled;
		stroke = new BasicStroke((float) strokeWidth);
	} // Rect

	/**
	 * Shape constructor.
	 * @param x int - shape x coordinate.
	 * @param y int - shape y coordinate.
	 * @param width int - width of the shape.
	 * @param height int - height of the shape.
	 * @param colorInt int - color of the shape.
	 * @param filled boolean - the shape is filled or not.
	 * @param strokeWidth double - line width of the shape.
	 * @param transp double - shape transparency percentage.
	 * @param lineType int - shape line type.
	 */
	public Rect(int x, int y, int width, int height, int colorInt, boolean filled,
				double strokeWidth, int transp, int lineType) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.filled = filled;
		this.color = new Color(colorInt);
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), transp);
		if (lineType > 0) {
			stroke = new BasicStroke((float) strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 50,
				new float[]{lineType, lineType}, 0);
		} else {
			stroke = new BasicStroke((float) strokeWidth);
		}
	} // Rect

	public void setFixed(boolean b) {
		this.fixed = b;
	}

	public boolean isFixed() {
		return this.fixed;
	}

	public boolean contains(int pointX, int pointY) {
		if (pointX > x && pointY > y && pointX < (x + width) && pointY < (y + height)) {
			return true;
		}
		return false;
	} // contains

	public String getName() {
		return this.name;
	} // getName

	public int getRealHeight() {
		return getHeight();
	} // getRealHeight

	public int getRealWidth() {
		return getWidth();
	} // getRealWidth

	/**
	 * Returns the color of the rectangle.
	 * @return Color - color of the rectangle.
	 */
	public Color getColor() {
		return this.color;
	} // getColor

	public boolean isSelected() {
		return this.selected;
	} // isSelected

	public String toString() {
		return getName();
	} // toString

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if (x1 > x && y1 > y && x2 < (x + width) && y2 < (y + height)) {
			return true;
		}
		return false;
	} // isInside

	public boolean isInsideRect(int x1, int y1, int x2, int y2) {
		if (x1 < x && y1 < y && x2 > (x + width) && y2 > (y + height)) {
			return true;
		}
		return false;
	} // isInsideRect

	public void setSelected(boolean b) {
		this.selected = b;
	} // setSelected

	public void setName(String s) {
		this.name = s;
	} // setName

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

	public void setPosition(int x, int y) {
		this.x = getX() + x;
		this.y = getY() + y;
	} // setPosition

	/**
	 * Returns a boolean value representing if the shape is filled or not.
	 * @return boolean - a boolean value representing if the shape is filled or not.
	 */
	public boolean isFilled() {
		return this.filled;
	} // isFilled

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
	 * @param g2 - graphics.
	 */
	public void drawSelection(Graphics2D g2) {
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke((float) 1.0));
		g2.fillRect(x, y, 4, 4);
		g2.fillRect(x + width - 4, y, 4, 4);
		g2.fillRect(x, y + height - 4, 4, 4);
		g2.fillRect(x + width - 4, y + height - 4, 4, 4);
	} // drawSelection

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
		return "<rect x=\"" + (x - boundingboxX) + "\" y=\""
			+ (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height
			+ "\" colour=\"" + colorInt + "\" filled=\"" + fill
			+ "\" fixed=\"" + isFixed() + "\" stroke=\"" + (int)stroke.getLineWidth() + "\" linetype=\"" + this.getLineType() + "\" transparency=\"" + getTransparency() + "\"/>\n";
	} // toFile

	public String toText() {
		String fill = "false";
		if (filled) fill = "true";
		int colorInt = 0;
		if (color != null) colorInt = color.getRGB();
		return "RECT:" + x + ":" + y + ":" + width + ":" + height + ":" + colorInt + ":" + fill + ":" + (int)stroke.getLineWidth() + ":" + this.getLineType() + ":" + getTransparency() + ":" + isFixed();
	} // toText

	public void setStrokeWidth(double d) {
        stroke = new BasicStroke((float)d, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
	}

	public void setTransparency(int d) {
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)d);
	}

	public void setLineType(int lineType) {
		stroke = new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), new float[]{lineType, lineType}, stroke.getDashPhase());
	}

	/**
	 * Returns x coordinate of the rectangle.
	 * @return int - rectangle x coordinate.
	 */
	public int getX() {
		return x;
	} // getX

	/**
	 * Returns y coordinate of the rectangle.
	 * @return int - rectangle y coordinate.
	 */
	public int getY() {
		return y;
	} // getY

	/**
	 * Returns width of the rectangle.
	 * @return int - rectangle width.
	 */
	int getWidth() {
		return width;
	} // getWidth

	/**
	 * Returns height of the rectangle.
	 * @return int - rectangle height.
	 */
	int getHeight() {
		return height;
	} // getHeight


	void drawDynamic(int xModifier, int yModifier, float Xsize, float Ysize,
					 Graphics g, HashMap table) {
		/*int drawx = xModifier + x + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(xEquation, table);
						 int drawy = yModifier + y + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(yEquation, table)
						 int drawWidth = width + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(widthEquation, table);
						 int drawHeight = height + ee.ioc.cs.editor.Equations.EquationSolver.calcValue(heightEquation, table);
						 g.drawRect(drawx, drawy, drawWidth, drawHeight);*/
	} // drawDynamic

	/**
	 * Draw rectangle.
	 * @param xModifier int -
	 * @param yModifier int -
	 * @param Xsize float - zoom factor.
	 * @param Ysize float - zoom factor.
	 * @param g2 Graphics - class graphics.
	 */
	public void draw(int xModifier, int yModifier, float Xsize, float Ysize, Graphics2D g2) {
		g2.setStroke(stroke);
		g2.setColor(color);

		int a = xModifier + (int) (Xsize * x);
		int b = yModifier + (int) (Ysize * y);
		int c = (int) (Xsize * width);
		int d = (int) (Ysize * height);

		if (filled) {
			g2.fillRect(a, b, c, d);
		} else {
			g2.drawRect(a, b, c, d);
		}

		if (selected) {
			drawSelection(g2);
		}

	} // draw

	public BasicStroke getStroke() {
		return stroke;
	}

	public Object clone() {
		return super.clone();
	} // clone
}

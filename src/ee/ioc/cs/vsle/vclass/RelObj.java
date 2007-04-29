package ee.ioc.cs.vsle.vclass;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.*;

/**
 * Relation class
 */
public class RelObj extends GObj {
    private static final long serialVersionUID = 1L;
	public Port startPort;
	public Port endPort;
	public int endX, endY;

	public RelObj(int x, int y, int width, int height, String name) {
		super(x, y, width, height, name);
	}

	public RelObj() {
		// do nothing
	}

	@Override
	public boolean contains(int pointX, int pointY) {
		float f = VMath.pointDistanceFromLine(x, y, endX, endY, pointX, pointY);
		if (f < height + 4) {
			return true;
		}
		return false;
	}

	@Override
	void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
        AffineTransform origTransform = g2.getTransform();
        g2.rotate(angle, xPos, yPos);

		for (Shape s: shapes)
			s.draw(xPos, yPos, Xsize, Ysize, g2);

		g2.setTransform(origTransform);
	} // draw


	@Override
	public void drawClassGraphics(Graphics2D g, float scale) {
		draw(getX(), getY(), getXsize(), getYsize(), g);
		int xModifier = getX();
		int yModifier = getY();

		for (ClassField field: fields) {
			if (field.defaultGraphics != null) {
				field.defaultGraphics.angle = angle;
				field.defaultGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.getName(), field.value);
			}
			if (field.isKnown() && field.knownGraphics != null) {
				field.knownGraphics.angle = angle;
				field.knownGraphics.drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.getName(), field.value);
			}
		}

		g.setColor(Color.black);
		if (isSelected())
			drawSelectionMarks(g);
	}

	/**
	 * Sets the coordinates of the end points of the relation object. The fields
	 * Xsize and angle are updated accordingly.
	 * 
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 */
	public void setEndPoints(Point start, Point end) {
		setEndPoints(start.x, start.y, end.x, end.y);
	}

	/**
	 * Sets the coordinates of the end points of the relation object.
	 * 
	 * @see RelObj#setEndPoints(Point, Point)
	 * @param x1
	 *            X coordinate of the start point
	 * @param y1
	 *            Y coordinate of the start point
	 * @param x2
	 *            X coordinate of the start point
	 * @param y2
	 *            Y coordinate of the start point
	 */
	public void setEndPoints(int x1, int y1, int x2, int y2) {
		x = x1;
		y = y1;
		endX = x2;
		endY = y2;

		Xsize = (float) Math.sqrt(Math.pow((x - endX), 2.0) 
				+ Math.pow((y - endY), 2.0)) / width;
		angle = VMath.calcAngle(x, y, endX, endY);
	}

	private void drawSelectionMarks(Graphics g) {
		g.fillRect(x - CORNER_SIZE / 2, y - CORNER_SIZE / 2,
				CORNER_SIZE,  CORNER_SIZE);
		g.fillRect((int) (x + width * Xsize * Math.cos(angle)) 
				- CORNER_SIZE / 2,
				(int) (y + width * Xsize * Math.sin(angle))
				- CORNER_SIZE / 2, CORNER_SIZE, CORNER_SIZE);
	}

	@Override
	public RelObj clone() {
		RelObj obj = (RelObj) super.clone();
		obj.startPort = startPort.clone();
		obj.endPort = endPort.clone();
		return obj;
	}

	/**
	 * Generates SAX events that are necessary to serialize this object to XML.
	 * @param th the receiver of events
	 * @throws SAXException 
	 */
	@Override
	public void toXML(TransformerHandler th) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		
		attrs.addAttribute(null, null, "name", StringUtil.CDATA, name);
		attrs.addAttribute(null, null, "type", StringUtil.CDATA, className);

		th.startElement(null, null, "relobject", attrs);

		attrs.clear();
		attrs.addAttribute(null, null, "x", StringUtil.CDATA,
				Integer.toString(x));
		attrs.addAttribute(null, null, "y", StringUtil.CDATA,
				Integer.toString(y));
		attrs.addAttribute(null, null, "endX", StringUtil.CDATA,
				Integer.toString(endX));
		attrs.addAttribute(null, null, "endY", StringUtil.CDATA,
				Integer.toString(endY));
		attrs.addAttribute(null, null, "angle", StringUtil.CDATA,
				Double.toString(angle));
		attrs.addAttribute(null, null, "width", StringUtil.CDATA,
				Integer.toString(width));
		attrs.addAttribute(null, null, "height", StringUtil.CDATA,
				Integer.toString(height));
		attrs.addAttribute(null, null, "xsize", StringUtil.CDATA,
				Double.toString(Xsize));
		attrs.addAttribute(null, null, "ysize", StringUtil.CDATA,
				Double.toString(Ysize));
		attrs.addAttribute(null, null, "strict", StringUtil.CDATA,
				Boolean.toString(isStrict()));

		th.startElement(null, null, "relproperties", attrs);
		th.endElement(null, null, "relproperties");
		
		th.startElement(null, null, "fields", null);

		for (ClassField field: fields)
			field.toXML(th);

		th.endElement(null, null, "fields");
		th.endElement(null, null, "relobject");
	}
}

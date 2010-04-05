package ee.ioc.cs.vsle.vclass;

import java.awt.*;
import java.awt.geom.*;

import javax.xml.transform.sax.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.*;

/**
 * Relation class
 */
public class RelObj extends GObj {
    private static final long serialVersionUID = 1L;
	private Port startPort;
	private Port endPort;
	private int endY;
    private int endX;

//	public RelObj(int x, int y, int width, int height, String name) {
//		super(x, y, width, height, name);
//	}

	public RelObj() {
		// do nothing
	}

	@Override
	public boolean contains(int pointX, int pointY) {
		float f = VMath.pointDistanceFromLine(getX(), getY(), getEndX(), getEndY(), pointX, pointY);
		if (f < getHeight() + 4) {
			return true;
		}
		return false;
	}

	@Override
	protected void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
        AffineTransform origTransform = g2.getTransform();
        g2.rotate(getAngle(), xPos, yPos);

		for (Shape s: getShapes())
			s.draw(xPos, yPos, Xsize, Ysize, g2);

		g2.setTransform(origTransform);
	} // draw


	@Override
	public void drawClassGraphics(Graphics2D g, float scale) {
		draw(getX(), getY(), getXsize(), getYsize(), g);
		int xModifier = getX();
		int yModifier = getY();

		for (ClassField field: getFields()) {
			if (field.getDefaultGraphics() != null) {
				field.getDefaultGraphics().setAngle( getAngle() );
				field.getDefaultGraphics().drawSpecial(xModifier,
					yModifier, getXsize(), getYsize(), g, field.getName(), field.value);
			}
			if (field.isKnown() && field.getKnownGraphics() != null) {
				field.getKnownGraphics().setAngle( getAngle() );
				field.getKnownGraphics().drawSpecial(xModifier,
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
		setX( x1 );
		setY( y1 );
		setEndX( x2 );
		setEndY( y2 );

		setXsize( (float) Math.sqrt(Math.pow((getX() - getEndX()), 2.0) 
				+ Math.pow((getY() - getEndY()), 2.0)) / getWidth() );
		setAngle( VMath.calcAngle(getX(), getY(), getEndX(), getEndY()) );
	}

	private void drawSelectionMarks(Graphics g) {
		g.fillRect(getX() - CORNER_SIZE / 2, getY() - CORNER_SIZE / 2,
				CORNER_SIZE,  CORNER_SIZE);
		g.fillRect((int) (getX() + getWidth() * getXsize() * Math.cos(getAngle())) 
				- CORNER_SIZE / 2,
				(int) (getY() + getWidth() * getXsize() * Math.sin(getAngle()))
				- CORNER_SIZE / 2, CORNER_SIZE, CORNER_SIZE);
	}

	@Override
	public RelObj clone() {
		RelObj obj = (RelObj) super.clone();
		obj.setStartPort( getStartPort().clone() );
		obj.setEndPort( getEndPort().clone() );
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
		
		attrs.addAttribute("", "", "name", StringUtil.CDATA, getName());
		attrs.addAttribute("", "", "type", StringUtil.CDATA, getClassName());

		th.startElement("", "", "relobject", attrs);

		/* Do not save values that can (and have to) be calculated (#2953636).
		attrs.clear();
		attrs.addAttribute("", "", "x", StringUtil.CDATA,
				Integer.toString(getX()));
		attrs.addAttribute("", "", "y", StringUtil.CDATA,
				Integer.toString(getY()));
		attrs.addAttribute("", "", "endX", StringUtil.CDATA,
				Integer.toString(getEndX()));
		attrs.addAttribute("", "", "endY", StringUtil.CDATA,
				Integer.toString(getEndY()));
		attrs.addAttribute("", "", "angle", StringUtil.CDATA,
				Double.toString(getAngle()));
		attrs.addAttribute("", "", "width", StringUtil.CDATA,
				Integer.toString(getWidth()));
		attrs.addAttribute("", "", "height", StringUtil.CDATA,
				Integer.toString(getHeight()));
		attrs.addAttribute("", "", "xsize", StringUtil.CDATA,
				Double.toString(getXsize()));
		attrs.addAttribute("", "", "ysize", StringUtil.CDATA,
				Double.toString(getYsize()));
		attrs.addAttribute("", "", "strict", StringUtil.CDATA,
				Boolean.toString(isStrict()));

		th.startElement("", "", "relproperties", attrs);
		th.endElement("", "", "relproperties");
		*/

		attrs.clear();
		th.startElement("", "", "fields", attrs);

		for (ClassField field: getFields())
			field.toXML(th);

		th.endElement("", "", "fields");
		th.endElement("", "", "relobject");
	}

    /**
     * @param startPort the startPort to set
     */
    public void setStartPort( Port startPort ) {
        this.startPort = startPort;
    }

    /**
     * @return the startPort
     */
    public Port getStartPort() {
        return startPort;
    }

    /**
     * @param endPort the endPort to set
     */
    public void setEndPort( Port endPort ) {
        this.endPort = endPort;
    }

    /**
     * @return the endPort
     */
    public Port getEndPort() {
        return endPort;
    }

    /**
     * @param endX the endX to set
     */
    public void setEndX( int endX ) {
        this.endX = endX;
    }

    /**
     * @return the endX
     */
    public int getEndX() {
        return endX;
    }

    /**
     * @param endY the endY to set
     */
    public void setEndY( int endY ) {
        this.endY = endY;
    }

    /**
     * @return the endY
     */
    public int getEndY() {
        return endY;
    }
}

package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.graphics.Shape;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class GObj implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public float Xsize = 1; // percentage for resizing, 1 means real size
	public float Ysize = 1;
	static final int CORNER_SIZE = 6;
    private static final float MIN_SCALE = 0.1f; // the minimal value for X|Ysize

	/* difWithMasterX, difWithMasterY variables are for resizeing an object group, we need to know
	 the intitial difference to make it work correctly*/
	public int x, y, difWithMasterX, difWithMasterY;
	public int width, height;
	public String className;
	protected String name;
	private boolean isStatic = false;
	
	private ArrayList<Port> ports = new ArrayList<Port>();
	public ArrayList<ClassField> fields = new ArrayList<ClassField>();
	public ArrayList<Shape> shapes = new ArrayList<Shape>();

	private boolean selected;
	public boolean group = false;
	private boolean strict;
	/**
	 * Is this object the superclass of the scheme?
	 */
	private boolean superClass;

	public int portOffsetX1 = 0;
	public int portOffsetX2 = 0;
	public int portOffsetY1 = 0;
	public int portOffsetY2 = 0;

    /**
     * The rotation of the object in radians
     */
    protected double angle = 0d;

	public GObj() {
		// default constructor
	}

	public GObj(int x, int y, int width, int height, String name) {
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.setClassName(name);
	}

	public boolean contains(int pointX, int pointY) {
        Point p = toObjectSpace(pointX, pointY);

		if ((p.x > getX() + (int) (getXsize() * portOffsetX1))
			&& (p.y > getY() + (int) (getYsize() * portOffsetY1))) {
			if ((p.x < getX() + (int) (getXsize() * (getWidth() + portOffsetX2))
				&& (p.y < getY() + (int) (getYsize() * (getHeight() + portOffsetY2))))) {
				return true;
			}
		}
		return false;
	}

	public boolean isInside(int x1, int y1, int x2, int y2) {
		if ((x1 < getX() + portOffsetX1) && (y1 < getY() + portOffsetY1)) {
			if ((x2 > getX() + (int) (getXsize() * getWidth()) + portOffsetX2)
				&& (y2 > getY() + (int) (getYsize() * getHeight()) + portOffsetY2)) {
				return true;
			}
		}
		return false;
	}

	public void resize(int changeX, int changeY, int corner) {
        /*
         * All these special cases and formulas are for making
         * sure that the object's dimensions will not become negative
         * even when the mouse is moved very quickly. If there is a
         * cleaner way to do it then please implement it.
         */
        switch (corner) { // changeX
        case 1: // top left
        case 3: // bottom left
            if (changeX > 0 && (Xsize - changeX / width < MIN_SCALE))
                changeX = (int) ((Xsize - MIN_SCALE) * width + .5);
            if (changeX != 0) {
                setX(x + changeX);
                setXsize(Xsize - (float) changeX / width);
            }
            break;
        case 2: // top right
        case 4: // bottom right
            if (changeX < 0 && (Xsize + changeX / width < MIN_SCALE))
                changeX = (int) ((MIN_SCALE - Xsize) * width - .5);
            if (changeX != 0)
                setXsize(Xsize + (float) changeX / width);
            break;
        default:
            throw new IllegalArgumentException("The argument corner can have values 1, 2, 3 or 4.");
        }

        switch (corner) { // changeY
        case 1: // top left
        case 2: // top right
            if (changeY > 0 && (Ysize - changeY / width < MIN_SCALE))
                changeY = (int) ((Ysize - MIN_SCALE) * width + .5);
            if (changeY != 0) {
                setY(y + changeY);
                setYsize(Ysize - (float) changeY / height);
            }
            break;
        case 3: // bottom left
        case 4: // bottom right
            if (changeY < 0 && (Ysize + changeY / width < MIN_SCALE))
                changeY = (int) ((MIN_SCALE - Ysize) * height - .5);
            if (changeY != 0)
                setYsize(Ysize + (float) changeY / height);
            break;
        default:
            // an exception was already thrown in previous switch statement
        }
	}

	public Port portContains(int pointX, int pointY) {
        Point p = toObjectSpace(pointX, pointY);
		Port port;

		for (int i = 0; i < getPorts().size(); i++) {
			port = getPorts().get(i);
			if (port.inBoundsX(p.x) && port.inBoundsY(p.y)) {
				return port;
			}
		}
		return null;
	}

	public int controlRectContains(int pointX, int pointY) {
        Point p = toObjectSpace(pointX, pointY);

		if ((p.x >= getX() + portOffsetX1 - CORNER_SIZE - 1) && (p.y >= getY() + portOffsetY1 - CORNER_SIZE - 1)) {
			if ((p.x <= getX() + portOffsetX1 - 1)
				&& (p.y <= getY() + portOffsetY1 -1)) {
				return 1;
			}
		}
		if ((p.x >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) + 1)
			&& (p.y >= getY() + portOffsetY1 - CORNER_SIZE - 1)) {
			if ((p.x <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2) + CORNER_SIZE + 1))
				&& (p.y <= getY() + portOffsetY1 +  CORNER_SIZE)) {
				return 2;
			}
		}
		if ((p.x >= getX() + portOffsetX1 - CORNER_SIZE - 1)
			&& (p.y >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) + 1)) {
			if ((p.x <= getX() + portOffsetX1 - 1)
				&& (p.y <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2) + CORNER_SIZE + 1))) {
				return 3;
			}
		}
		if ((p.x >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) +1)
			&& (p.y >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) + 1)) {
			if ((p.x <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2) + CORNER_SIZE + 1))
				&& (p.y <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2) + CORNER_SIZE + 1))) {
				return 4;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return getName();
	}

	public boolean isStrict() {
		return strict;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getRealWidth() {
		return (int) (getWidth() * getXsize());
	}

	public int getRealHeight() {
		return (int) (getHeight() * getYsize());
	}

	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setClassName(String name) {
		this.className = name;
	}

	public String getClassName() {
		return className;
	}

	public void setSelected(boolean set) {
		selected = set;
	}

	public void setMultXSize(float s) {
		this.setXsize(this.getXsize() * s);
	}

	public void setMultYSize(float s) {
		this.setYsize(this.getYsize() * s);
	}


	public boolean isSelected() {
		return selected;
	}

	public boolean isGroup() {
		return group;
	}

	public ArrayList<Connection> getConnections() {
		ArrayList<Connection> c = new ArrayList<Connection>();
		Port port;

		for (int i = 0; i < getPorts().size(); i++) {
			port = getPorts().get(i);
			c.addAll(port.getConnections());
		}
		return c;
	}

	public boolean includesObject(GObj obj) {
		 return obj == this;
	}

	public ArrayList<GObj> getComponents() {
		ArrayList<GObj> c = new ArrayList<GObj>();

		c.add(this);
		return c;
	}

	public ArrayList<Port> getPorts() {
		return ports;
	}


	void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
		Shape s;
		for (int i = 0; i < shapes.size(); i++) {
			s = shapes.get(i);
			s.draw(xPos, yPos, Xsize, Ysize, g2);
		}
	} // draw

    public double getCenterX() {
        return getX() + getRealWidth() / 2.0;
    }

    public double getCenterY() {
        return getY() + getRealHeight() / 2.0;
    }
    
	public void drawClassGraphics(Graphics2D g2, float scale) {
        AffineTransform origTransform = g2.getTransform();
        g2.rotate(angle, getCenterX(), getCenterY());

		// hilight superclass
		if (isSuperClass()) {
			Composite c = g2.getComposite();
			g2.setColor(Color.GREEN);
			g2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, .2f));
			g2.fillRect(getX(), getY(), getRealWidth(), getRealHeight());
			g2.setComposite(c);
		}

		draw(getX(), getY(), getXsize(), getYsize(), g2);

		int xModifier = getX();
		int yModifier = getY();
        g2.setColor(Color.black);
        
        if( isStatic() ) {
        	g2.setFont( new Font( "Times New Roman", Font.ITALIC, 20 ) );
			g2.drawString( "s", xModifier, yModifier );
		}

		for (int i = 0; i < getPorts().size(); i++) {
			ClassGraphics graphics;
			Port port = getPorts().get(i);

			if (port.isSelected() || port.isConnected() 
					|| port.isHilighted()) {

				graphics = port.getClosedGraphics();

			} else 
				graphics = port.getOpenGraphics();
			
			graphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y),
					getXsize(), getYsize(), g2);
		}

		for (int i = 0; i < fields.size(); i++) { //print all field values
			ClassField field = fields.get(i);
			if (field.defaultGraphics != null) {
				if (!TypeUtil.isArray(field.type)) {
					field.defaultGraphics.drawSpecial(xModifier, yModifier, getXsize(), getYsize(), g2, field.getName(), field.value);
				} else {
					String[] split = field.value.split( ClassField.ARRAY_TOKEN );
					int textOffset = 0;
					for (int j = 0; j < split.length; j++) {
						field.defaultGraphics.drawSpecial(xModifier, yModifier+textOffset, getXsize(), getYsize(), g2, field.getName(), split[j]);
						textOffset += 12;
					}
				}
			}
			if (field.isKnown() && field.knownGraphics !=null) {
				if (!TypeUtil.isArray(field.type)) {
					field.knownGraphics.drawSpecial(xModifier, yModifier, getXsize(), getYsize(), g2, field.getName(), field.value);
				} else  {
					String[] split = field.value.split( ClassField.ARRAY_TOKEN );
					int textOffset = 0;
					for (int j = 0; j < split.length; j++) {
						field.knownGraphics.drawSpecial(xModifier, yModifier+textOffset, getXsize(), getYsize(), g2, field.getName(), split[j]);
						textOffset += 12;
					}
				}
			}
		}

		g2.setColor(Color.black);
		if (isSelected() == true) {
			drawSelectionMarks(g2, scale);
		}

        g2.setTransform(origTransform);
	}

	private void drawSelectionMarks(Graphics g, float scale) {
		g.fillRect(getX() + portOffsetX1 - CORNER_SIZE -1,
				getY() + portOffsetY1 - CORNER_SIZE - 1,
				CORNER_SIZE,  CORNER_SIZE);

		g.fillRect(getX() + (int) (getXsize() * (getWidth()
				+ portOffsetX2)) + 1, getY() + portOffsetY1
				- CORNER_SIZE -1,  CORNER_SIZE,  CORNER_SIZE);

		g.fillRect(getX() + portOffsetX1 - CORNER_SIZE -1,
				getY() + (int) (getYsize() * (portOffsetY2 
				+ getHeight())) + 1, CORNER_SIZE, CORNER_SIZE);

		g.fillRect(getX() + (int) (getXsize() * (portOffsetX2 
				+ getWidth())) + 1, getY() + (int) (getYsize()
				* (portOffsetY2 + getHeight())) + 1, CORNER_SIZE, CORNER_SIZE);
	}

	@Override
	public GObj clone() {
		try {
			GObj obj = (GObj) super.clone();

            obj.setPorts(new ArrayList<Port>());
			for (Port port : getPorts()) {
				port = port.clone();
				obj.getPorts().add(port);
				port.setObject(obj);
				port.setConnections(new ArrayList<Connection>());
			}

			obj.setFields(new ArrayList<ClassField>());
			// deep clone each separate field
			ClassField field;

			for (int i = 0; i < getFields().size(); i++) {
				field = getFields().get(i);
				obj.getFields().add(field.clone());
			}

			return obj;
		} catch (CloneNotSupportedException e) {
			db.p("Unable to clone.");
			return null;
		}
	}

	public float getXsize() {
		return Xsize;
	}

	public void setXsize(float xsize) {
		Xsize = xsize;
	}

	public float getYsize() {
		return Ysize;
	}

	public void setYsize(float ysize) {
		Ysize = ysize;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Replaces the current port list with the specified one.
	 * Further modifications of the list might not be reflected correctly
	 * in the state of this object. Therefore, it is recommended that the
	 * list is not modified directly. New ports can be added through the
	 * {@code addPort()} method.
	 * 
	 * @param ports list of the ports
	 */
	public void setPorts(ArrayList<Port> ports) {
		this.ports = ports;
		for (Port port : ports) {
			if (port.isStrict()) {
				this.strict = true;
				return;
			}
		}
	}

	/**
	 * Appends the specified port at the end of the list of the ports.
	 * 
	 * @param port a port
	 */
	public void addPort(Port port) {
		ports.add(port);
		if (!strict && port.isStrict())
			strict = true;
	}

	public ArrayList<ClassField> getFields() {
		return fields;
	}

	public void setFields(ArrayList<ClassField> fields) {
		this.fields = fields;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	/**
	 * Generates SAX events that are necessary to serialize this object to XML.
	 * @param th the receiver of events
	 * @throws SAXException 
	 */
	public void toXML(TransformerHandler th) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		
		attrs.addAttribute(null, null, "name", StringUtil.CDATA, name);
		attrs.addAttribute(null, null, "type", StringUtil.CDATA, className);
		attrs.addAttribute(null, null, "static", StringUtil.CDATA, Boolean.toString( isStatic() ));
		
		th.startElement(null, null, "object", attrs);

		attrs.clear();
		attrs.addAttribute(null, null, "x", StringUtil.CDATA,
				Integer.toString(x));
		attrs.addAttribute(null, null, "y", StringUtil.CDATA,
				Integer.toString(y));
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
        if (angle != 0.0)
            attrs.addAttribute(null, null, "angle", StringUtil.CDATA,
                    Double.toString(angle));

		th.startElement(null, null, "properties", attrs);
		th.endElement(null, null, "properties");
		
		th.startElement(null, null, "fields", null);

		for (ClassField field: fields)
			field.toXML(th);

		th.endElement(null, null, "fields");
		th.endElement(null, null, "object");
	}

	/**
	 * Returns {@code true} if one or more ports are strictly connected.
	 * 
	 * @return true if this object is strictly connected, {@code false}
	 *         otherwise
	 */
	public boolean isStrictConnected() {
		for (Port port : ports) {
			for (Connection con : port.getConnections()) {
				if (con.isStrict())
					return true;
			}
		}
		return false;
	}

	/**
	 * Is this object the superclass of the scheme?
	 * @return true, if the object is superclass, false otherwise
	 */
	public boolean isSuperClass() {
		return superClass;
	}

	/**
	 * Set or unset the object as a scheme superclass.
	 * There should be only one superclass at a time.
	 * @param superClass true to set the class as a superclass
	 */
	public void setSuperClass(boolean superClass) {
		this.superClass = superClass;
	}

	/**
	 * Returns fields by name.
	 * @param fldName the name of the field
	 * @return the field with the specified name or null
	 */
	public ClassField getField(String fldName) {
		if (fields == null || fldName == null)
			return null;
		
		for (ClassField f : fields) {
			if (fldName.equals(f.getName()))
				return f;
		}
		return null;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

    /**
     * Transforms the specified point coordinates to the object coordinate
     * space (inverse of toCanvasSpace()).
     * @param pointX the x coordinate
     * @param pointY the y coordinate
     * @return transformed point
     */
    public Point toObjectSpace(int pointX, int pointY) {
        Point p = new Point(pointX, pointY);
        
        if (angle != 0.0) {
            double cx = getCenterX();
            double cy = getCenterY();

            double sin = Math.sin(angle);
            double cos = Math.cos(angle);

            p.x = (int) Math.round(pointX * cos + pointY * sin
                    + cx - cx * cos - cy * sin);
            p.y = (int) Math.round(-pointX * sin + pointY * cos
                    + cy + cx * sin - cy * cos);
        }
        return p;
    }

    /**
     * Transform the specified point coordinates to the canvas coordinate
     * space (inverse of toObjectSpace()).
     * @param pointX the x coordinate
     * @param pointY the y coordinate
     * @return transformed point
     */
    public Point toCanvasSpace(int pointX, int pointY) {
        Point p = new Point(pointX, pointY);
        
        if (angle != 0.0) {
            double cx = getCenterX();
            double cy = getCenterY();

            double sin = Math.sin(angle);
            double cos = Math.cos(angle);

            p.x = (int) Math.round(pointX * cos - pointY * sin
                    + cx - cx * cos + cy * sin);
            p.y = (int) Math.round(pointX * sin + pointY * cos
                    + cy - cx * sin - cy * cos);
        }
        return p;
    }

    /**
     * Sets the rotation of this object.
     * @param theta the rotation in radians
     */
    public void setAngle(double theta) {
        angle = theta;
    }

    /**
     * Returns the rotation of this object.
     * @return rotation in radians
     */
    public double getAngle() {
        return angle;
    }
}

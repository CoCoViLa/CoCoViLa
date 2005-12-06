package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.graphics.Shape;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.*;

public class GObj implements Serializable, Cloneable {

	public float Xsize = 1; // percentage for resizing, 1 means real size
	public float Ysize = 1;
	static final int CORNER_SIZE = 6;

	/* difWithMasterX, difWithMasterY variables are for resizeing an object group, we need to know
	 the intitial difference to make it work correctly*/
	public int x, y, difWithMasterX, difWithMasterY;
	public int width, height;
	public String className;
	public String name;

	public ArrayList ports = new ArrayList();
	public ArrayList fields = new ArrayList();
	public ArrayList shapes = new ArrayList();

	public ArrayList classRelations;
	public boolean draggable;
	public boolean selected;
	public boolean group = false;
	public boolean strict;
//	public ClassGraphics graphics;

	public int portOffsetX1 = 0;
	public int portOffsetX2 = 0;
	public int portOffsetY1 = 0;
	public int portOffsetY2 = 0;

	public GObj() {
	}

	public GObj(int x, int y, int width, int height, String name) {
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		this.setClassName(name);
	}

	public boolean contains(int pointX, int pointY) {
		if ((pointX > getX() + (int) (getXsize() * portOffsetX1))
			&& (pointY > getY() + (int) (getYsize() * portOffsetY1))) {
			if ((pointX < getX() + (int) (getXsize() * (getWidth() + portOffsetX2))
				&& (pointY < getY() + (int) (getYsize() * (getHeight() + portOffsetY2))))) {
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

	public void resize (int changeX, int changeY, int corner) {
		if (corner == 1) {
			if (Xsize > 0.1 || changeX < 0) {
				setX(x + changeX);
				setXsize((width * Xsize - changeX) /(float)width);
			}
			if (Ysize > 0.1 || changeY < 0) {
			    setY(y + changeY);
				setYsize((height * Ysize - changeY) /(float)height);
			}
		}
		if (corner == 2) {
			if (Xsize > 0.1  || changeX > 0) {
				setXsize((width * Xsize + changeX) /(float)width);
			}
			if (Ysize > 0.1  || changeY < 0) {
			    setY(y + changeY);
				setYsize((height * Ysize - changeY) /(float)height);
			}
		}

		if (corner == 3) {
			if (Xsize > 0.1 || changeX < 0) {
				setX(x + changeX);
				setXsize((width * Xsize - changeX) /(float)width);
			}
			if (Ysize > 0.1 || changeY > 0) {
				setYsize((height * Ysize + changeY) /(float)height);
			}
		}

		if (corner == 4) {
			if (Xsize > 0.1 || changeX > 0) {
				setXsize((width * Xsize + changeX) /(float)width);
			}
			if (Ysize > 0.1  || changeY > 0) {
				setYsize((height * Ysize + changeY) /(float)height);
			}
		}


	}

	public Port portContains(int pointX, int pointY) {
		Port port;

		for (int i = 0; i < getPorts().size(); i++) {
			port = (Port) getPorts().get(i);
			if (port.inBoundsX(pointX) && port.inBoundsY(pointY)) {
				return port;
			}
		}
		return null;
	}

	public int controlRectContains(int pointX, int pointY) {
		if ((pointX >= getX() + portOffsetX1 - CORNER_SIZE - 1) && (pointY >= getY() + portOffsetY1 - CORNER_SIZE - 1)) {
			if ((pointX <= getX() + portOffsetX1 - 1)
				&& (pointY <= getY() + portOffsetY1 -1)) {
				return 1;
			}
		}
		if ((pointX >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) + 1)
			&& (pointY >= getY() + portOffsetY1 - CORNER_SIZE - 1)) {
			if ((pointX <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2) + CORNER_SIZE + 1))
				&& (pointY <= getY() + portOffsetY1 +  CORNER_SIZE)) {
				return 2;
			}
		}
		if ((pointX >= getX() + portOffsetX1 - CORNER_SIZE - 1)
			&& (pointY >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) + 1)) {
			if ((pointX <= getX() + portOffsetX1 - 1)
				&& (pointY <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2) + CORNER_SIZE + 1))) {
				return 3;
			}
		}
		if ((pointX >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) +1)
			&& (pointY >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) + 1)) {
			if ((pointX <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2) + CORNER_SIZE + 1))
				&& (pointY <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2) + CORNER_SIZE + 1))) {
				return 4;
			}
		}
		return 0;
	}

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

	public void setDraggable(boolean set) {
		draggable = set;
	}

	public boolean isDraggable() {
		return draggable;
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

	public void setAsGroup(boolean b) {
		setGroup(b);
	}

	public boolean isGroup() {
		return group;
	}

	public ArrayList getConnections() {
		ArrayList c = new ArrayList();
		Port port;

		for (int i = 0; i < getPorts().size(); i++) {
			port = (Port) getPorts().get(i);
			c.addAll(port.getConnections());
		}
		return c;
	}

	public boolean includesObject(GObj obj) {
		if (obj == this) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList getComponents() {
		ArrayList c = new ArrayList();

		c.add(this);
		return c;
	}

	public ArrayList getPorts() {
		return ports;
	}


	void draw(int xPos, int yPos, float Xsize, float Ysize, Graphics2D g2) {
		Shape s;
		for (int i = 0; i < shapes.size(); i++) {
			s = (Shape) shapes.get(i);
			s.draw(xPos, yPos, Xsize, Ysize, g2);
		}
	} // draw

	public void drawClassGraphics(Graphics2D g2) {
		draw(getX(), getY(), getXsize(), getYsize(), g2);

		int xModifier = getX();
		int yModifier = getY();
        g2.setColor(Color.black);
		for (int i = 0; i < getPorts().size(); i++) {
			Port port = (Port) getPorts().get(i);

			if (port.isSelected()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g2);
			} else if (port.isConnected()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g2);
			} else if (port.isHilighted()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g2);
			} else {
				port.openGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g2);
			}
		}

		for (int i = 0; i < fields.size(); i++) { //print all field values
			ClassField field = (ClassField)fields.get(i);
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
			drawSelectionMarks(g2);
		}
	}

	private void drawSelectionMarks(Graphics g) {
		g.fillRect(getX() + portOffsetX1 - CORNER_SIZE -1, getY() + portOffsetY1 - CORNER_SIZE - 1,  CORNER_SIZE,  CORNER_SIZE);
		g.fillRect(getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) + 1,
			getY() + portOffsetY1  - CORNER_SIZE -1,  CORNER_SIZE,  CORNER_SIZE);
		g.fillRect(getX() + portOffsetX1 - CORNER_SIZE -1,
			getY() + (int) (getYsize() * (portOffsetY2 + getHeight())) + 1, CORNER_SIZE, CORNER_SIZE);
		g.fillRect(getX() + (int) (getXsize() * (portOffsetX2 + getWidth())) + 1,
			getY() + (int) (getYsize() * (+portOffsetY2 + getHeight())) + 1, CORNER_SIZE, CORNER_SIZE);
	}

	public Object clone() {
		try {
			GObj obj = (GObj) super.clone();
			Port port;

			obj.setPorts((ArrayList) getPorts().clone());
			for (int i = 0; i < obj.getPorts().size(); i++) {
				port = (Port) obj.getPorts().get(i);
				port = (Port) port.clone();
				port.setConnected(false);
				obj.getPorts().set(i, port);
				port.obj = obj;
				port.connections = new ArrayList();
			}

			obj.setFields((ArrayList) getFields().clone());
			// deep clone each separate field
			ClassField field;

			for (int i = 0; i < getFields().size(); i++) {
				field = (ClassField) getFields().get(i);
				obj.getFields().set(i, field.clone());
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

	public void setPorts(ArrayList ports) {
		this.ports = ports;
	}

	public ArrayList getFields() {
		return fields;
	}

	public void setFields(ArrayList fields) {
		this.fields = fields;
	}

	public ArrayList getClassRelations() {
		return classRelations;
	}

	public void setClassRelations(ArrayList classRelations) {
		this.classRelations = classRelations;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	/*
	public ClassGraphics getGraphics() {
		return graphics;
	}

	public void setGraphics(ClassGraphics graphics) {
		this.graphics = graphics;
	}*/

	public String toXML() {
		String xml = "<object name=\""+name+"\" type=\""+className+"\" >\n";
        xml += "  <properties x=\""+ x+"\" y=\""+y+"\" width=\""+ width+"\" height=\""+height+"\" xsize=\""+Xsize+"\" ysize=\""+Ysize+"\" strict=\""+strict+"\" />\n";
		xml += "  <fields>\n";
		for (int i = 0; i < fields.size(); i++) {
			ClassField field = (ClassField)fields.get(i);
            xml += StringUtil.indent(4) + field.toXML();
		}
		xml += "  </fields>\n";
        xml += "</object>\n";
		return xml;

	}
}

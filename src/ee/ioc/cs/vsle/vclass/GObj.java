package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;

public class GObj implements Serializable, Cloneable {

	public float Xsize = 1; // percentage for resizing, 1 means real size
	public float Ysize = 1;

	/* difWithMasterX, difWithMasterY variables are for resizeing an object group, we need to know
	 the intitial difference to make it work correctly*/
	public int x, y, difWithMasterX, difWithMasterY;
	public int width, height;
	public String className;
	public String name;
	public ArrayList ports = new ArrayList();
	public ArrayList fields = new ArrayList();
	public ArrayList classRelations;
	public boolean draggable;
	public boolean selected;
	public boolean group = false;
	public boolean strict;
	public ClassGraphics graphics;

	public int portOffsetX1 = 0;
	public int portOffsetX2 = 0;
	public int portOffsetY1 = 0;
	public int portOffsetY2 = 0;

	GObj() {
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
		if ((pointX >= getX() + portOffsetX1) && (pointY >= getY() + portOffsetY1)) {
			if ((pointX <= getX() + portOffsetX1 + 4)
				&& (pointY <= getY() + portOffsetY1 + 4)) {
				return 1;
			}
		}
		if ((pointX >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) - 4)
			&& (pointY >= getY() + portOffsetY1)) {
			if ((pointX <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)))
				&& (pointY <= getY() + portOffsetY1 + 4)) {
				return 2;
			}
		}
		if ((pointX >= getX() + portOffsetX1)
			&& (pointY >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) - 4)) {
			if ((pointX <= getX() + portOffsetX1 + 4)
				&& (pointY <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)))) {
				return 3;
			}
		}
		if ((pointX >= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) - 4)
			&& (pointY >= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)) - 4)) {
			if ((pointX <= getX() + (int) (getXsize() * (getWidth() + portOffsetX2)))
				&& (pointY <= getY() + (int) (getYsize() * (getHeight() + portOffsetY2)))) {
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

	public void setXSize(float size) {
		this.setXsize(size);
	}

	public void setYSize(float size) {
		this.setYsize(size);
	}

	public void setMultXSize(float s) {
		this.setXsize(this.getXsize() * s);
	}

	public void setMultYSize(float s) {
		this.setYsize(this.getYsize() * s);
	}

	public float getXSize() {
		return getXsize();
	}

	public float getYSize() {
		return getYsize();
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

	public void drawClassGraphics(Graphics g) {
		getGraphics().draw(getX(), getY(), getXsize(), getYsize(), g);
		int xModifier = getX();
		int yModifier = getY();
        g.setColor(Color.black);
		if (getGraphics().showFields == true) {
			int textOffset = 5;
			for (int i = 0; i < getFields().size(); i++) {
				ClassField f = (ClassField) getFields().get(i);
				if (f.value != null) {
					if (f.isPrimOrStringArray()) {
						String[] split = f.value.split("�");
						for (int j = 0; j < split.length; j++) {
							g.drawString(split[j], getX() + 5, getY() + 8 + textOffset);
							textOffset += 12;
						}
						textOffset += 6;
					} else if (f.isPrimitiveOrString()) {
						g.drawString(f.value, getX() + 5, getY() + 8 + textOffset);
						textOffset += 18;
					}
				}
			}
		}


		for (int i = 0; i < getPorts().size(); i++) {
			Port port = (Port) getPorts().get(i);

			if (port.isSelected()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g);
			} else if (port.isConnected()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g);
			} else if (port.isHilighted()) {
				port.closedGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g);
			} else {
				port.openGraphics.draw(xModifier + (int) (getXsize() * port.x),
					yModifier + (int) (getYsize() * port.y), getXsize(), getYsize(), g);
			}
		}
		g.setColor(Color.black);
		if (isSelected() == true) {
			g.drawRect(getX() + portOffsetX1, getY() + portOffsetY1, 4, 4);
			g.drawRect(getX() + (int) (getXsize() * (getWidth() + portOffsetX2)) - 4,
				getY() + portOffsetY1, 4, 4);
			g.drawRect(getX() + portOffsetX1,
				getY() + (int) (getYsize() * (portOffsetY2 + getHeight())) - 4, 4, 4);
			g.drawRect(getX() + (int) (getXsize() * (portOffsetX2 + getWidth())) - 4,
				getY() + (int) (getYsize() * (+portOffsetY2 + getHeight())) - 4, 4, 4);
		}
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

	public ClassGraphics getGraphics() {
		return graphics;
	}

	public void setGraphics(ClassGraphics graphics) {
		this.graphics = graphics;
	}

}

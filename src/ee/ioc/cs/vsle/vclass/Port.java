package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

public class Port implements Cloneable, Serializable {

    public String id;
	public GObj obj;
	int width, height;
	public String name;
	public String type;
	public int x;
	public int y;
	boolean strict, area;
	public ClassGraphics openGraphics, closedGraphics;
	public ArrayList connections = new ArrayList();
	boolean selected = false, connected = false;
	boolean known = false, target = false;
	boolean watched = false;
	boolean hilighted = false;

	public Port(String name, String type, int x, int y, String portConnection, String strict) {
		this.name = name;
		this.type = type;
		this.x = x;

		this.y = y;
		if (portConnection.equals("area")) {
			area = true;
		} else {
			area = false;

		}

		if (strict.equals("true")) {
			this.strict = true;
		} else {
			this.strict = false;
		}
	}


	public Port getStrictConnected() {
		Connection con;

		for (int i = 0; i < connections.size(); i++) {
			con = (Connection) connections.get(i);

			if (con.beginPort.isStrict()) {
				if (con.beginPort == this) {
					return con.endPort;
				} else {
					return con.beginPort;
				}
			}
		}
		return null;
	}

	public int getX() {
		return (int) (obj.getXsize() * x);
	}

	public int getY() {
		return (int) (obj.getYsize() * y);
	}

	public int getAbsoluteX() {
		return (int) (obj.getXsize() * x + obj.getX());
	}

	public int getAbsoluteY() {
		return (int) (obj.getYsize() * y + obj.getY());
	}

	public int getCenterX() {
		return (int) (obj.getXsize()
			* (x + openGraphics.boundX + (openGraphics.boundWidth) / 2));
	}

	public int getCenterY() {
		return (int) (obj.getYsize()
			* (y + openGraphics.boundY + (openGraphics.boundHeight) / 2));
	}

	public int getRealCenterX() {
		return (int) (obj.getXsize()
			* (obj.getX() + x + openGraphics.boundX + (openGraphics.boundWidth) / 2));
	}

	public int getRealCenterY() {
		return (int) (obj.getYsize()
			* (obj.getY() + y + openGraphics.boundY + (openGraphics.boundHeight) / 2));
	}

	public int getStartX() {
		return (int) (obj.getXsize() * (openGraphics.boundX + x) + obj.getX());
	}

	public int getStartY() {
		return (int) (obj.getYsize() * (openGraphics.boundY + y) + obj.getY());
	}

	public int getWidth() {
		return (int) (obj.getXsize() * openGraphics.boundWidth);
	}

	public int getHeight() {
		return (int) (obj.getYsize() * openGraphics.boundHeight);
	}

	public boolean inBoundsX(int pointX) {
		if (obj.getX() + obj.getXsize() * (x + openGraphics.boundX) < pointX
			&& (obj.getX() + obj.getXsize() * (x + openGraphics.boundX + openGraphics.boundWidth) > pointX)) {
			return true;
		} else {
			return false;
		}

	}

	public boolean inBoundsY(int pointY) {
		if (obj.getY() + (obj.getYsize() * (y + openGraphics.boundY)) < pointY
			&& (obj.getY()
			+ obj.getYsize()
			* (y + openGraphics.boundY + openGraphics.boundHeight))
			> pointY) {
			return true;
		} else {
			return false;
		}

	}

	public String getName() {
		return name;
	}

	public String toString() {
		if (id!=null)
			return id;
		else
			return name;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	public void setConnected(boolean b) {
		connected = b;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setKnown(boolean b) {
		known = b;
	}

	public void setTarget(boolean b) {
		target = b;
	}

	public boolean isKnown() {
		return known;
	}

	public boolean isTarget() {
		return target;
	}

	public boolean isWatched() {
		return watched;
	}

	public boolean isStrict() {
		return strict;
	}

	public String getType() {
		for (int i = 0; i < obj.getFields().size(); i++) {
			if (((ClassField) obj.getFields().get(i)).getName().equals(name)) {
				return ((ClassField) obj.getFields().get(i)).type;
			}
		}
		return null;
	}

	public ClassField getField() {
		for (int i = 0; i < obj.getFields().size(); i++) {
			if (((ClassField) obj.getFields().get(i)).getName().equals(name)) {
				return ((ClassField) obj.getFields().get(i));
			}
		}
		return null;
	}

	public boolean isArea() {
		return area;
	}

	public void setWatch(boolean w) {
		watched = w;
	}

	public void setObject(GObj obj) {
		this.obj = obj;
	}

	public int getNumber() {
		Port port;

		for (int j = 0; j < obj.getPorts().size(); j++) {
			port = (Port) obj.getPorts().get(j);
			if (port == this) {
				return j;
			}
		}
		return -1;
	}

	public void addConnection(Connection con) {
		connections.add(con);
	}

	public void setHilighted(boolean b) {
		hilighted = b;
	}

	public boolean isHilighted() {
		return hilighted;
	}

	public ArrayList getConnections() {
		return connections;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {

			return null;
		}
	}

	public boolean isAny() {
		if (name.equals("*any") || type.equals("any"))
			return true;
		return false;
	}

}

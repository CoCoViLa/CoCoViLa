package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

public class Port implements Cloneable, Serializable {

	private String id;
	private GObj obj;
	private int width, height;
	private String name;
	private String type;
	public int x;
	public int y;
	private boolean strict, area;
	private ClassGraphics openGraphics, closedGraphics;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private boolean selected = false, connected = false;
	private boolean known = false, target = false;
	private boolean watched = false;
	private boolean hilighted = false;

	private boolean isMulti = false;//1:* type e.g. "{int}" will be transformed into int[]
	
	public Port(String name, String type, int x, int y, String portConnection, String strict) {
		this.name = name;
		this.type = type.trim();
		this.x = x;

		this.y = y;
		
		area = portConnection.equals("area");

		this.strict = Boolean.parseBoolean( strict );

		if( this.type.startsWith("[") && this.type.endsWith("]") ) {
			this.type = this.type.substring( 0, type.length() - 1 ).substring( 1 );
			
			isMulti = true;
		}
	}

	public Port getStrictConnected() {
		Connection con;

		for (int i = 0; i < connections.size(); i++) {
			con = connections.get(i);

			if (con.beginPort.isStrict()) {
				if (con.beginPort == this) {
					return con.endPort;
				}
				return con.beginPort;
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
		return (obj.getX() + obj.getXsize() * (x + openGraphics.boundX) < pointX
			&& (obj.getX() + obj.getXsize() * (x + openGraphics.boundX + openGraphics.boundWidth) > pointX));
	}

	public boolean inBoundsY(int pointY) {
		return (obj.getY() + (obj.getYsize() * (y + openGraphics.boundY)) < pointY
			&& (obj.getY()
			+ obj.getYsize()
			* (y + openGraphics.boundY + openGraphics.boundHeight))
			> pointY);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		if (id!=null)
			return id;
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
		return type;
//        for (ClassField field: obj.getFields()) {
//			if (field.getName().equals(name))
//				return field.type;
//		}
//		return null;
	}

	public ClassField getField() {
        for (ClassField field: obj.getFields()) {
			if (field.getName().equals(name))
				return field;
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

	public GObj getObject() {
		return obj;
	}
	public int getNumber() {
		Port port;

		for (int j = 0; j < obj.getPorts().size(); j++) {
			port = obj.getPorts().get(j);
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

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public Port clone() {
		try {
			return (Port) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isAny() {
		if (name.equals("*any") || type.equals("any"))
			return true;
		return false;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public ClassGraphics getClosedGraphics() {
		return closedGraphics;
	}


	public void setClosedGraphics(ClassGraphics closedGraphics) {
		this.closedGraphics = closedGraphics;
	}


	public ClassGraphics getOpenGraphics() {
		return openGraphics;
	}


	public void setOpenGraphics(ClassGraphics openGraphics) {
		this.openGraphics = openGraphics;
	}


	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}


	public boolean isMulti() {
		return isMulti;
	}

}

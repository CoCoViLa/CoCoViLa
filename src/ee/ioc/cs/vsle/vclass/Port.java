package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

import ee.ioc.cs.vsle.util.TypeUtil;

public class Port implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private GObj obj;
	//private int width, height;
	private String name;
	private String type;
	public int x;
	public int y;
	private boolean strict, area;
	private ClassGraphics openGraphics, closedGraphics;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private boolean selected = false;
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

	public ArrayList<Port> getStrictConnected() {
		ArrayList<Port> ports = new ArrayList<Port>();

		for (Connection con : connections) {
			if (con.isStrict())
				ports.add(con.beginPort == this ? con.endPort : con.beginPort);
		}
		return ports;
	}

	public int getX() {
		return (int) (obj.getXsize() * x);
	}

	public int getY() {
		return (int) (obj.getYsize() * y);
	}

	public int getAbsoluteX() {
        return getAbsoluteCenter().x;
	}

	public int getAbsoluteY() {
        return getAbsoluteCenter().y;
	}

    public Point getAbsoluteCenter() {
        return obj.toCanvasSpace(Math.round(obj.getXsize() * x + obj.getX()),
                Math.round(obj.getYsize() * y + obj.getY()));
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
		return (int) (obj.getX() + obj.getXsize() 
				* (x + openGraphics.boundX + (openGraphics.boundWidth / 2)));
	}

	public int getRealCenterY() {
		return (int) (obj.getY() + obj.getYsize()
				* (y + openGraphics.boundY + (openGraphics.boundHeight / 2)));
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

	@Override
	public String toString() {
		if (id!=null)
			return id;
		return name;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isConnected() {
		return connections.size() > 0;
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

	/**
	 * <p>Returns {@code true} if this port is a strict port.</p>
	 * <p>Strict ports are connected automatically when one port is placed
	 * above or close to the other port on the scheme. The connection
	 * between strictly connected ports is deleted implicitly when the
	 * ports are detached.</p>
	 * <p>Strict ports can also be connected with normal connections.
	 * It might make sense to prohibit this but currently some pakcages
	 * (gearbox: 1.syn) depend on this feature.</p>
	 * @return true if this port is a strict port, {@code false} otherwise
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Returns true if this port has a strict connection.
	 * @see #isStrict()
	 * @return true if this port has a strict connection, false otherwise
	 */
	public boolean isStrictConnected() {
		for (Connection con : connections) {
			if (con.isStrict())
				return true;
		}
		return false;
	}

	public String getType() {
		return type;
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

	public boolean removeConnection(Connection con) {
		return connections.remove(con);
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

	@Override
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

	/**
	 * Returns true if this port is directly connected to the specified port.
	 * Directly connected means that there exists a {@code Connection} instance
	 * having the end ports the {@code port} and {@code this} port.
	 * 
	 * @param port
	 *            the port
	 * @return true, if this port is directly connected to the {@code port},
	 *         {@code false otherwise}
	 */
	public boolean isConnectedTo(Port port) {
		for (Connection conn : connections) {
			if (conn.beginPort == port || conn.endPort == port)
				return true;
		}
		return false;
	}

	/**
	 * Checks if this port can be connected at all. For example, the syntax
	 * rules of a visual language might disallow connecting some ports
	 * in certain cases. These rules are implemented in auxiliary classes
	 * such as a scheme daemon. Also, superclasses cannot be connected.
	 * @return true, if this port can possibly be connected to some other port,
	 * 		   false otherwise.
	 */
	public boolean canBeConnected() {
        // obj can be null when adding a relation class and the object is not
        // created yet. A relation class can never be a superclass and 
        // should always be connected.
		return (obj == null) || !obj.isSuperClass();
	}

	/**
	 * Checks whether the specified port can be connected to {@code this} port.
	 * 
	 * @param port
	 *            a port
	 * @return true if {@code this} port and {@code port} can be connected
	 */
	public boolean canBeConnectedTo(Port port) {
		return Port.canBeConnected(this, port);
	}

	/**
	 * Checks whether the specified ports can be connected by a connection.
	 * 
	 * @param port1 first port
	 * @param port2 second port
	 * @return <code>true</code> if {@code port1} and {@code port2} can be connected.
	 */
	public static boolean canBeConnected(Port port1, Port port2) {

		if (!port1.canBeConnected() || !port2.canBeConnected())
			return false;

		if (port1.isMulti() && port2.isMulti())
			return false;
		else if (port1.isMulti() && port1.getType().equals(port2.getType())
				|| port2.isMulti() && port2.getType().equals(port1.getType())) 
			return true;
		else if (port1.getType().equals(port2.getType()))
			return true;
		else if ((port1.isAny() || port2.isAny()) 
				&& !(port1.isAny() || port2.isAny()))
			return true;
		else if (TypeUtil.TYPE_ALIAS.equals(port2.getType()) 
				&& port1.getType().substring(port1.getType().length() - 2,
						port1.getType().length()).equals("[]"))
			return true;
		else if (TypeUtil.TYPE_ALIAS.equals(port1.getType()) 
				&& port2.getType().substring(port2.getType().length() - 2,
						port2.getType().length()).equals("[]"))
			return true;
		else 
			return false;
	}

	/**
	 * Returns true if this port contains the center point of the specified
	 * port.
	 * 
	 * @param port
	 *            the port
	 * @return true, if this port contains the center point of the port, false
	 *         otherwise
	 */
	public boolean contains(Port port) {
		return inBoundsX(port.getRealCenterX())
				&& inBoundsY(port.getRealCenterY());
	}
}

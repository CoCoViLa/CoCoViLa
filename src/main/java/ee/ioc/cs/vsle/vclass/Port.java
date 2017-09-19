package ee.ioc.cs.vsle.vclass;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ee.ioc.cs.vsle.graphics.Shape.*;

public class Port implements ee.ioc.cs.vsle.api.Port, Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Port.class);

	private String id;
	private GObj obj;
	private ClassField field;
	private String name;
	private String type;
	private int x;
	private int y;
	private boolean strict, area, isMulti;
	private ClassGraphics openGraphics, closedGraphics;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private boolean selected = false;
	private boolean known = false, target = false;
	private boolean watched = false;
	private boolean hilighted = false;

	public static final ClassGraphics DEFAULT_OPEN_GRAPHICS;
	public static final ClassGraphics DEFAULT_CLOSED_GRAPHICS;
	
	static {
	    DEFAULT_OPEN_GRAPHICS = new ClassGraphics();
	    DEFAULT_OPEN_GRAPHICS.addShape( new Oval( -4, -4, 8, 8, createColor( 12632256, 255 ), true, 1.0f, 0, true ) );
	    DEFAULT_OPEN_GRAPHICS.addShape( new Oval( -4, -4, 8, 8, createColor( 0, 255 ), false, 1.0f, 0, true ) );
	    DEFAULT_OPEN_GRAPHICS.setBounds( -4, -4, 8, 8 );
	    
	    DEFAULT_CLOSED_GRAPHICS = new ClassGraphics();
	    DEFAULT_CLOSED_GRAPHICS.addShape( new Oval( -4, -4, 8, 8, createColor( 0, 255 ), true, 1.0f, 0, true ) );
	    DEFAULT_CLOSED_GRAPHICS.setBounds( -4, -4, 8, 8 );
	}
	
	public Port(String name, String type, int x, int y, String portConnection, boolean strict, boolean multi ) {
		this.name = name;
		this.type = type.trim();
		this.x = x;

		this.y = y;
		
		if ( portConnection != null )
            area = portConnection.equals("area");

		this.strict = strict;
		this.isMulti = multi;
		
		setOpenGraphics( DEFAULT_OPEN_GRAPHICS );
		setClosedGraphics( DEFAULT_CLOSED_GRAPHICS );
	}

	public ArrayList<Port> getStrictConnected() {
		ArrayList<Port> ports = new ArrayList<Port>();

		for (Connection con : connections) {
			if (con.isStrict())
				ports.add(con.getBeginPort() == this ? con.getEndPort() : con.getBeginPort());
		}
		return ports;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
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
			* (x + openGraphics.getBoundX() + (openGraphics.getBoundWidth()) / 2));
	}

	public int getCenterY() {
		return (int) (obj.getYsize()
			* (y + openGraphics.getBoundY() + (openGraphics.getBoundHeight()) / 2));
	}

	public int getRealCenterX() {
		return (int) (obj.getX() + obj.getXsize() 
				* (x + openGraphics.getBoundX() + (openGraphics.getBoundWidth() / 2)));
	}

	public int getRealCenterY() {
		return (int) (obj.getY() + obj.getYsize()
				* (y + openGraphics.getBoundY() + (openGraphics.getBoundHeight() / 2)));
	}

	public int getStartX() {
		return (int) (obj.getXsize() * (openGraphics.getBoundX() + x) + obj.getX());
	}

	public int getStartY() {
		return (int) (obj.getYsize() * (openGraphics.getBoundY() + y) + obj.getY());
	}

	public int getWidth() {
		return (int) (obj.getXsize() * openGraphics.getBoundWidth());
	}

	public int getHeight() {
		return (int) (obj.getYsize() * openGraphics.getBoundHeight());
	}

	public boolean inBoundsX(int pointX) {
		return (obj.getX() + obj.getXsize() * (x + openGraphics.getBoundX()) < pointX
			&& (obj.getX() + obj.getXsize() * (x + openGraphics.getBoundX() + openGraphics.getBoundWidth()) > pointX));
	}

	public boolean inBoundsY(int pointY) {
		return (obj.getY() + (obj.getYsize() * (y + openGraphics.getBoundY())) < pointY
			&& (obj.getY()
			+ obj.getYsize()
			* (y + openGraphics.getBoundY() + openGraphics.getBoundHeight()))
			> pointY);
	}

	@Override
	public String getName() {
		return name;
	}

	public String getNameWithObject() {
	    
	    return ( getObject() != null ? getObject().getName() + "." : "" ) + getName();
	}
	
	@Override
    public String toString() {
        return "Port " + getNameWithObject();
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

	@Override
	public String getType() {
		return type;
	}

	public ClassField getField() {
		return field;
	}

	public boolean isArea() {
		return area;
	}

	public void setWatch(boolean w) {
		watched = w;
	}

	public void setObject(GObj obj) {
		this.obj = obj;
		this.field = obj.getSpecField( name );
		
		if( this.field == null ) {
		    logger.warn( "Port " + name + " does not have the corresponding specification field" );
		}
	}

	@Override
	public GObj getObject() {
		return obj;
	}

    public String getConnectionId() {
        if ( id != null )
            return id;
        return name;
    }
	
	public int getNumber() {
		Port port;

		for (int j = 0; j < obj.getPortList().size(); j++) {
			port = obj.getPortList().get(j);
			if (port == this) {
				return j;
			}
		}
		return -1;
	}

	public void addConnection(Connection con) {
	    if( !connections.contains( con ) )
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

	@Override
	public List<ee.ioc.cs.vsle.api.Connection> getConnections() {
	    return new ArrayList<ee.ioc.cs.vsle.api.Connection>(connections);
	}

	public ArrayList<Connection> getConnectionList() {
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
			if (conn.getBeginPort() == port || conn.getEndPort() == port)
				return true;
		}
		return false;
	}

	/**
	 * Checks if this port can be connected at all. For example, the syntax
	 * rules of a visual language might disallow connecting some ports
	 * in certain cases. These rules are implemented in auxiliary classes
	 * such as a scheme daemon.
	 * @return true, if this port can possibly be connected to some other port,
	 * 		   false otherwise.
	 */
	public boolean canBeConnected() {
        // obj can be null when adding a relation class and the object is not
        // created yet. A relation class can never be a superclass and 
        // should always be connected.
		return true;
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
		else if ( port1.isMulti() ) {
		    return port1.field != null && port1.field.isAlias() && ((Alias)port1.field).acceptsType( port2.getType() );
		}
		else if ( port2.isMulti() ) {
		    return port2.field != null && port2.field.isAlias() && ((Alias)port2.field).acceptsType( port1.getType() );
		}
		else if (port1.isAny() && port2.isAny())
			return false;
		else if (port1.isAny() || port2.isAny())
			return true;
		else if ( port1.field != null && port1.field.isAlias() ) {
		    if( port2.field != null && port2.field.isAlias() && ((Alias)port2.field).equalsByTypes( (Alias)port1.field ) ) {
		        return true;
		    }
            return ((Alias)port1.field).acceptsType( port2.getType() );
		}
		else if ( port2.field != null && port2.field.isAlias() ) {
            if( port1.field != null && port1.field.isAlias() && ((Alias)port1.field).equalsByTypes( (Alias)port2.field ) ) {
                return true;
            }
            return ((Alias)port2.field).acceptsType( port1.getType() );
        }
		else 
			return port1.getType().equals(port2.getType());
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

package ee.ioc.cs.vsle.vclass;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A <tt>list</tt> for storing and manipulating connections
 * between scheme objects.
 */
public class ConnectionList extends ArrayList<Connection> {

	private static final long serialVersionUID = 1L;

	/**
	 * Class constructor.
	 */
	public ConnectionList() {
		super();
	} // ee.ioc.cs.editor.vclass.ConnectionList

	/**
	 * Constructs a <tt>ConnectionList</tt> containing the elements
	 * of the specified collection, in the order they are returned
	 * by the collection's iterator.
	 *  
	 * @param collection the collection whose elements are to be placed
	 * 		  into this list.
	 */
	public ConnectionList(Collection<? extends Connection> collection) {
		super(collection.size());
		for (Connection conn : collection)
			this.add(conn);
	}

	/**
	 * Returns a connection if the distance of a pointer location is closer than
	 * four pixels of the line.
	 * @param x int - x coordinate of the pointer.
	 * @param y int - y coordinate of the pointer.
	 * @return ee.ioc.cs.editor.vclass.Connection - ee.ioc.cs.editor.vclass.Connection returned if the pointer is located close enough
	 *                      to the line. Null returned if the pointer is not close enough.
	 */
	public Connection nearPoint(int x, int y) {
		Connection relation;

		for (int i = 0; i < this.size(); i++) {
			relation = this.get(i);
			if (relation.distanceFromPoint(x, y) < Connection.NEAR_DISTANCE
					// ignore implicit connections, otherwise connections
					// between strict ports or between relation class endport
					// and the connected objects's port could get selected
					// and deleted.
					&& !relation.isImplicit()) {
				return relation;
			}
		}
		return null;
	} // nearPoint

	/**
	 * Removes all relations.
	 * @param objects list of relations to be emptied.
	 */
	@Override
	public boolean removeAll(Collection<?> objects) {
		boolean modified = false;

		for (Object obj : objects)
			modified = remove(obj) || modified;

		return modified;
	} // removeAll

	/**
	 * Remove a connection.
	 * @param obj the connection to be removed.
	 */
	@Override
	public boolean remove(Object obj) {
		boolean modified = false;
		if (obj instanceof Connection) {
			Connection relation = (Connection) obj;
			modified = super.remove(relation);
			if (modified) {
				relation.beginPort.getConnections().remove(relation);
				relation.endPort.getConnections().remove(relation);
			}
		}
		return modified;
	} // remove

	/**
	 * Remove a connection between specified ports.
	 * @param p1 Port connected port 1.
	 * @param p2 Port connected port 2.
	 */
	public void remove(Port p1, Port p2) {
		Connection con;

		for (int i = 0; i < this.size(); i++) {
			con = this.get(i);
			if ((con.beginPort == p1 && con.endPort == p2)
				|| (con.beginPort == p2 && con.endPort == p1)) {
				remove(con);
			}
		}
	} // remove

	/**
	 * Make the currently selected connections not selected.
	 */
	public void clearSelected() {
		for (Connection con : this)
			con.setSelected(false);
	} // clearSelected

	/**
	 * Adds a new connection to the connection list. The connection lists of
	 * the ports are updated. It is assumed that the end ports are both
	 * present. The state is not modified if the list already contains the
	 * specified connection.
	 * @param con a connection
	 * @return true if the list was modified, false if the connection was
	 * 		   already present in the list.
	 */
	@Override
	public boolean add(Connection con) {
		boolean modified = false;
		if (!super.contains(con)) {
			super.add(con);

			if (!con.beginPort.getConnections().contains(con))
				con.beginPort.addConnection(con);

			assert !con.endPort.getConnections().contains(con);
			
			con.endPort.addConnection(con);
			modified = true;
		}
		return modified;
	}

	/**
	 * Creates a new connection between the specified ports and adds the new
	 * connection to the list. The connection lists of the ports are updated.
	 * 
	 * @param beginPort the begin port of the new connection
	 * @param endPort the end port of the new connection
	 */
	public void add(Port beginPort, Port endPort) {
		add(new Connection(beginPort, endPort));
	}

	/**
	 * Adds all the connections in the specified collection to this connection
	 * list. If a connection is already in this list it is not added again.
	 * The connection lists of the end ports of the added connections
	 * are updated.
	 * @param connections the list of connections
	 * @return true if this list was modified
	 */
	@Override
	public boolean addAll(Collection<? extends Connection> connections) {
		boolean modified = false;
		for (Connection con : connections)
			modified = add(con) || modified;

		return modified;
	}
}

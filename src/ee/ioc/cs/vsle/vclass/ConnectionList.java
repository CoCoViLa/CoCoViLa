package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 */
public class ConnectionList
	extends ArrayList
	implements Serializable {

	/**
	 * Class constructor.
	 */
	public ConnectionList() {
		super();
	} // ee.ioc.cs.editor.vclass.ConnectionList

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
			relation = (Connection)this.get(i);
			if (relation.distanceFromPoint(x, y) < 4) {
				return relation;
			}
		}
		return null;
	} // nearPoint

	/**
	 * Removes all relations.
	 * @param relations ArrayList - list of relations to be emptied.
	 */
	public void removeAll(ArrayList relations) {
		super.removeAll(relations);
		Connection con;

		for (int i = 0; i < relations.size(); i++) {
			con = (Connection) relations.get(i);
			con.beginPort.connections.remove(con);
			if (con.beginPort.connections.isEmpty()) {
				con.beginPort.setConnected(false);
			}
			con.endPort.connections.remove(con);
			if (con.endPort.connections.isEmpty()) {
				con.endPort.setConnected(false);
			}
		}
	} // removeAll

	/**
	 * Remove a connection.
	 * @param relation ee.ioc.cs.editor.vclass.Connection - a connection to be removed.
	 */
	public void remove(Connection relation) {
		super.remove(relation);
		relation.beginPort.connections.remove(relation);
		if (relation.beginPort.connections.isEmpty()) {
			relation.beginPort.setConnected(false);
		}
		relation.endPort.connections.remove(relation);
		if (relation.endPort.connections.isEmpty()) {
			relation.endPort.setConnected(false);
		}
	} // remove

	/**
	 * Remove a connection between specified ports.
	 * @param p1 ee.ioc.cs.editor.vclass.Port - connected port 1.
	 * @param p2 ee.ioc.cs.editor.vclass.Port - connected port 2.
	 */
	public void remove(Port p1, Port p2) {
		Connection con;

		for (int i = 0; i < this.size(); i++) {
			con = (Connection)this.get(i);
			if ( (con.beginPort == p1 && con.endPort == p2) || (con.beginPort == p2 && con.endPort == p1)) {
				super.remove(con);
				p2.setConnected(false);
				p2.connections.remove(con);
				p1.setConnected(false);
				p1.connections.remove(con);
			}
		}
	} // remove

	/**
	 * Calculate breakpoints for all connections.
	 */
	public void calcAllBreakPoints() {
		Connection relation;

		for (int i = 0; i < this.size(); i++) {
			relation = (Connection)this.get(i);
			relation.calcBreakPoints();
		}
	} // calcAllBreakPoints

	/**
	 * Make the currently selected connection not selected.
	 */
	public void clearSelected() {
		Connection con;

		for (int i = 0; i < this.size(); i++) {
			con = (Connection)this.get(i);
			con.selected = false;
		}
	} // clearSelected

}

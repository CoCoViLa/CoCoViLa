package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.editor.Menu;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * <p>Title: ee.ioc.cs.editor.editor.ConnectionPopupMenu</p>
 * <p>Description: Popup menu opened if clicked on a connection line with a right mouse button.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class ConnectionPopupMenu
	extends JPopupMenu
	implements ActionListener {

	Connection connection;
	ConnectionList connections;
	Container contentPane;

	/**
	 * Class constructor.
	 * @param relation ee.ioc.cs.editor.vclass.Connection - selected connection.
	 * @param relations ee.ioc.cs.editor.vclass.ConnectionList - list of all defined connections.
	 * @param contentPane Container - editor's content pane.
	 */ConnectionPopupMenu(Connection relation, ConnectionList relations, Container contentPane) {
		super();

		this.connection = relation;
		this.connections = relations;
		this.contentPane = contentPane;

		JMenuItem menuItem = new JMenuItem(Menu.DELETE);

		menuItem.addActionListener(this);
		menuItem.setActionCommand(Menu.RELATION_DELETE);
		this.add(menuItem);
		if (relation.numOfBreakPoints < 2) {
			menuItem = new JMenuItem(Menu.ADD_BREAKPOINT);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(Menu.ADDBREAKPOINT);
			this.add(menuItem);
		}
		if (relation.numOfBreakPoints > 0) {
			menuItem = new JMenuItem(Menu.REMOVE_BREAKPOINT);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(Menu.REMOVEBREAKPOINT);
			this.add(menuItem);
		}
	} // ee.ioc.cs.editor.editor.ConnectionPopupMenu

	/**
	 * Action event listener.
	 * @param e ActionEvent - performed action event.
	 */ public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == Menu.RELATION_DELETE) {
			connections.remove(connection);
		}
		else if (e.getActionCommand() == Menu.ADDBREAKPOINT) {
			connection.addBreakPoint();
		}
		else {
			connection.removeBreakPoint();
		}
		contentPane.repaint();
	} // actionPerformed

}

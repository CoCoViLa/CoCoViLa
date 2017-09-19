package ee.ioc.cs.vsle.editor;

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

import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.editor.Menu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * <p>Popup menu opened if clicked on a connection line with a right mouse button.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Ando Saabas
 * @version 1.0
 */
public class ConnectionPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Connection connection;
	private Canvas canvas;

	// coordinates of the clicked point
	private int clickX;
	private int clickY;

	/**
	 * Class constructor.
	 * @param relation selected connection
	 * @param canvas editor's canvas
	 * @param clickX X coordinate of the clicked point
	 * @param clickY Y coordinate of the clicked point
	 */
	ConnectionPopupMenu(Connection relation, Canvas canvas, int clickX,
			int clickY) {
		super();

		this.connection = relation;
		this.canvas = canvas;
		this.clickX = clickX;
		this.clickY = clickY;

		JMenuItem menuItem = new JMenuItem(Menu.DELETE_REL);

		menuItem.addActionListener(this);
		menuItem.setActionCommand(Menu.RELATION_DELETE);
		this.add(menuItem);

		menuItem = new JMenuItem(Menu.ADD_BREAKPOINT);
		menuItem.addActionListener(this);
		menuItem.setActionCommand(Menu.ADDBREAKPOINT);
		this.add(menuItem);

		if (relation.breakPointContains(clickX, clickY) != null) {
			menuItem = new JMenuItem(Menu.REMOVE_BREAKPOINT);
			menuItem.addActionListener(this);
			menuItem.setActionCommand(Menu.REMOVEBREAKPOINT);
			this.add(menuItem);
		}
	} // ee.ioc.cs.editor.editor.ConnectionPopupMenu

	/**
	 * Action event listener.
	 * @param e ActionEvent - performed action event.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == Menu.RELATION_DELETE) {
			canvas.removeConnection(connection);
		} else if (e.getActionCommand() == Menu.ADDBREAKPOINT) {
			connection.addBreakPoint(connection.indexOf(clickX, clickY),
					new Point(clickX, clickY));
			// select the connection to make the new breakpoint visible
			connection.setSelected(true);
		} else {
			connection.removeBreakPoint(clickX, clickY);
		}
		canvas.drawingArea.repaint();
	} // actionPerformed
}

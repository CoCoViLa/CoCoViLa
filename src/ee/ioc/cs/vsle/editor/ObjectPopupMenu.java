package ee.ioc.cs.vsle.editor;

import java.awt.event.*;
import javax.swing.*;


/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:17:47
 * To change this template use Options | File Templates.
 */
public class ObjectPopupMenu extends JPopupMenu {

	// ee.ioc.cs.editor.editor.Menu items displayed in the menu.
	JMenuItem itemDelete;
	JMenuItem itemProperties;
	JMenuItem itemGroup;
	JMenuItem itemUngroup;
	JMenuItem itemClone;
	JMenuItem itemHLPorts;

	/**
	 * Build the popup menu by adding menu items and action listeners for the menu items in it.
	 * @param mListener - ee.ioc.cs.editor.editor.Editor mouse listener.
	 */
	ObjectPopupMenu(MouseOps mListener) {
		super();

		itemDelete = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
		itemDelete.addActionListener(mListener);
		itemDelete.setActionCommand(Menu.OBJECT_DELETE);
		itemDelete.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
		this.add(itemDelete);

		itemProperties = new JMenuItem(Menu.PROPERTIES, KeyEvent.VK_R);
		itemProperties.addActionListener(mListener);
		itemProperties.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemProperties);

		itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
		itemGroup.addActionListener(mListener);
		itemGroup.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemGroup);

		itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
		itemUngroup.addActionListener(mListener);
		itemUngroup.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemUngroup);

		itemHLPorts = new JMenuItem(Menu.HLPORTS, KeyEvent.VK_H);
		itemHLPorts.addActionListener(mListener);
		itemHLPorts.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemHLPorts);

		itemClone = new JMenuItem(Menu.CLONE, KeyEvent.VK_C);
		itemClone.addActionListener(mListener);
		itemClone.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemClone);
	}

	/**
	 * Method for enabling or disabling menu items.
	 * @param item - menu item to be enabled or disabled.
	 * @param b - enable or disable the menu item.
	 */
	void enableDisableMenuItem(JMenuItem item, boolean b) {
		int index = getComponentIndex(item);

		getComponent(index).setEnabled(b);
	}

}

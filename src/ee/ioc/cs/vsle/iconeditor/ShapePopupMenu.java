package ee.ioc.cs.vsle.iconeditor;


import java.awt.event.KeyEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import ee.ioc.cs.vsle.editor.Menu;


/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:17:47
 * To change this template use Options | File Templates.
 */
public class ShapePopupMenu extends JPopupMenu {

	// Menu items displayed in the menu.
	JMenuItem itemDelete;
	JMenuItem itemGroup;
	JMenuItem itemUngroup;
	JMenuItem itemClone;
	JMenuItem itemBackward;
	JMenuItem itemForward;
	JMenuItem itemToFront;
	JMenuItem itemToBack;

	/**
	 * Build the popup menu by adding menu items and action listeners for the menu items in it.
	 * @param mListener - IconEditor mouse listener.
	 */
	ShapePopupMenu(IconMouseOps mListener) {
		super();

		itemDelete = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
		itemDelete.addActionListener(mListener);
		itemDelete.setActionCommand(Menu.OBJECT_DELETE);
		this.add(itemDelete);

		itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
		itemGroup.addActionListener(mListener);
		this.add(itemGroup);

		itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
		itemUngroup.addActionListener(mListener);
		this.add(itemUngroup);

		this.addSeparator();

		itemClone = new JMenuItem(Menu.CLONE, KeyEvent.VK_C);
		itemClone.addActionListener(mListener);
		this.add(itemClone);

		this.addSeparator();

		itemBackward = new JMenuItem(Menu.BACKWARD, KeyEvent.VK_B);
		itemBackward.addActionListener(mListener);
		this.add(itemBackward);

		itemForward = new JMenuItem(Menu.FORWARD, KeyEvent.VK_F);
		itemForward.addActionListener(mListener);
		this.add(itemForward);

		itemToFront = new JMenuItem(Menu.TOFRONT, KeyEvent.VK_R);
		itemToFront.addActionListener(mListener);
		this.add(itemToFront);

		itemToBack = new JMenuItem(Menu.TOBACK, KeyEvent.VK_A);
		itemToBack.addActionListener(mListener);
		this.add(itemToBack);

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

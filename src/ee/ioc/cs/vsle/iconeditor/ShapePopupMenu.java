package ee.ioc.cs.vsle.iconeditor;

import java.awt.event.*;
import javax.swing.*;

import ee.ioc.cs.vsle.editor.Menu;

/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:17:47
 * To change this template use Options | File Templates.
 */
public class ShapePopupMenu
	extends JPopupMenu {

	// Menu items displayed in the menu.
	JMenuItem itemDelete;
	JMenuItem itemGroup;
	JMenuItem itemUngroup;
	JMenuItem itemClone;
	JCheckBoxMenuItem itemFixed;
	JMenuItem itemBackward;
	JMenuItem itemForward;
	JMenuItem itemToFront;
	JMenuItem itemToBack;

	IconEditor editor;

	/**
	 * Build the popup menu by adding menu items and action listeners for the menu items in it.
	 * @param mListener - IconEditor mouse listener.
	 */
	ShapePopupMenu(IconMouseOps mListener, IconEditor editor) {
		super();
		this.editor = editor;

		itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
		itemGroup.addActionListener(mListener);
		itemGroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemGroup);

		itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
		itemUngroup.addActionListener(mListener);
		itemUngroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemUngroup);

		this.addSeparator();

		itemClone = new JMenuItem(Menu.CLONE, KeyEvent.VK_C);
		itemClone.addActionListener(mListener);
		itemClone.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemClone);

		itemDelete = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
		itemDelete.addActionListener(mListener);
		itemDelete.setActionCommand(Menu.OBJECT_DELETE);
		itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		this.add(itemDelete);

		JCheckBoxMenuItem checkItemFixed = new JCheckBoxMenuItem(Menu.FIXED, isShapeFixed());
		checkItemFixed.setMnemonic('F');
		checkItemFixed.addActionListener(mListener);
		checkItemFixed.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		this.add(checkItemFixed);

		this.addSeparator();

		itemBackward = new JMenuItem(Menu.BACKWARD, KeyEvent.VK_B);
		itemBackward.addActionListener(mListener);
		itemBackward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
		this.add(itemBackward);

		itemForward = new JMenuItem(Menu.FORWARD, KeyEvent.VK_F);
		itemForward.addActionListener(mListener);
		itemForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0));
		this.add(itemForward);

		itemToFront = new JMenuItem(Menu.TOFRONT, KeyEvent.VK_R);
		itemToFront.addActionListener(mListener);
		itemToFront.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemToFront);

		itemToBack = new JMenuItem(Menu.TOBACK, KeyEvent.VK_A);
		itemToBack.addActionListener(mListener);
		itemToBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemToBack);

	}

	/**
	 * Returns true if the current shape is true and false otherwise.
	 * @return boolean - the current shape is fixed or not.
	 */
	public boolean isShapeFixed() {
	  return editor.currentShape.isFixed();
	} // isShapeFixed

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

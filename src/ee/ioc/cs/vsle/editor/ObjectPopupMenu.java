package ee.ioc.cs.vsle.editor;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;


/**
 */
public class ObjectPopupMenu extends JPopupMenu {

	// ee.ioc.cs.editor.editor.Menu items displayed in the menu.
	JMenuItem itemDelete;
	JMenuItem itemProperties;
	JMenuItem itemGroup;
	JMenuItem itemUngroup;
	JMenuItem itemClone;
	JMenuItem itemHLPorts;
	JMenuItem itemShowGrid;
	JMenuItem itemBackward;
	JMenuItem itemForward;
	JMenuItem itemToFront;
	JMenuItem itemToBack;
	JMenuItem itemMakeClass;
	JMenuItem itemViewCode;

	JMenu submenuOrder;

	/**
	 * Build the popup menu by adding menu items and action listeners for the menu items in it.
	 * @param canvas - ee.ioc.cs.editor.editor.Editor mouse listener.
	 */
	ObjectPopupMenu(Canvas canvas) {
		super();

		submenuOrder = new JMenu(Menu.MENU_ORDER);

		itemClone = new JMenuItem(Menu.CLONE, KeyEvent.VK_C);
		itemClone.addActionListener(canvas);
		itemClone.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemClone);

		itemDelete = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
		itemDelete.addActionListener(canvas);
		itemDelete.setActionCommand(Menu.OBJECT_DELETE);
		itemDelete.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_DELETE, 0));
		this.add(itemDelete);

		itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
		itemGroup.addActionListener(canvas);
		itemGroup.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemGroup);

		itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
		itemUngroup.addActionListener(canvas);
		itemUngroup.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemUngroup);

		itemProperties = new JMenuItem(Menu.PROPERTIES, KeyEvent.VK_R);
		itemProperties.addActionListener(canvas);
		itemProperties.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemProperties);

		itemHLPorts = new JMenuItem(Menu.HLPORTS, KeyEvent.VK_H);
		itemHLPorts.addActionListener(canvas);
		itemHLPorts.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemHLPorts);

		itemViewCode = new JMenuItem(Menu.VIEWCODE);
		itemViewCode.addActionListener(canvas);
		itemViewCode.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemViewCode);

		itemMakeClass = new JMenuItem(Menu.MAKECLASS);
		itemMakeClass.addActionListener(canvas);
		itemMakeClass.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemMakeClass);


		itemShowGrid = new JCheckBoxMenuItem(Menu.GRID, canvas.isGridVisible());
		itemShowGrid.setMnemonic('G');
		itemShowGrid.addActionListener(canvas);
		this.add(itemShowGrid);



        itemBackward = new JMenuItem(Menu.BACKWARD, KeyEvent.VK_B);
		itemBackward.addActionListener(canvas);
		itemBackward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
		submenuOrder.add(itemBackward);

		itemForward = new JMenuItem(Menu.FORWARD, KeyEvent.VK_F);
		itemForward.addActionListener(canvas);
		itemForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0));
		submenuOrder.add(itemForward);

		itemToFront = new JMenuItem(Menu.TOFRONT, KeyEvent.VK_R);
		itemToFront.addActionListener(canvas);
		itemToFront.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
		submenuOrder.add(itemToFront);

		itemToBack = new JMenuItem(Menu.TOBACK, KeyEvent.VK_A);
		itemToBack.addActionListener(canvas);
		itemToBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		submenuOrder.add(itemToBack);
		submenuOrder.setMnemonic('O');

		this.add(submenuOrder);
	}

	/**
	 * Method for enabling or disabling menu items.
	 * @param item - menu item to be enabled or disabled.
	 * @param b - enable or disable the menu item.
	 */
	void enableDisableMenuItem(JMenuItem item, boolean b) {
	   if(item!=null) {
		 Component[] components = submenuOrder.getMenuComponents();
		 for (int i = 0; i < components.length; i++) {
		   JMenuItem menuitem = (JMenuItem) components[i];
		   if (menuitem == item) {
			 submenuOrder.getMenuComponent(i).setEnabled(b);
		   }
		 }
	   }
	} // enableDisableMenuItem


}

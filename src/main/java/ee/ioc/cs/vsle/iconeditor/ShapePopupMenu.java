package ee.ioc.cs.vsle.iconeditor;

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

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

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

	JCheckBoxMenuItem itemFixed;

	JMenu submenuOrder;

	IconEditor editor;

	/**
	 * Build the popup menu by adding menu items and action listeners for the menu items in it.
	 * @param mListener - IconEditor mouse listener.
	 * @param editor - IconEditor reference.
	 */
	ShapePopupMenu(IconMouseOps mListener, IconEditor editor) {
		super();
		this.editor = editor;

		submenuOrder = new JMenu(Menu.MENU_ORDER);

		itemClone = new JMenuItem(Menu.CLONE, KeyEvent.VK_C);
		itemClone.addActionListener(mListener);
		itemClone.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemClone);

		itemDelete = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
		itemDelete.addActionListener(mListener);
		itemDelete.setActionCommand(Menu.OBJECT_DELETE);
		itemDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		this.add(itemDelete);

/*		itemGroup = new JMenuItem(Menu.GROUP, KeyEvent.VK_G);
		itemGroup.addActionListener(mListener);
		itemGroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemGroup);

		itemUngroup = new JMenuItem(Menu.UNGROUP, KeyEvent.VK_U);
		itemUngroup.addActionListener(mListener);
		itemUngroup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
		this.add(itemUngroup);

		JCheckBoxMenuItem checkItemFixed = new JCheckBoxMenuItem(Menu.FIXED, isShapeFixed());
		checkItemFixed.setMnemonic('F');
		checkItemFixed.addActionListener(mListener);
		checkItemFixed.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		this.add(checkItemFixed);
*/
		itemBackward = new JMenuItem(Menu.BACKWARD, KeyEvent.VK_B);
		itemBackward.addActionListener(mListener);
		itemBackward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
		submenuOrder.add(itemBackward);

		itemForward = new JMenuItem(Menu.FORWARD, KeyEvent.VK_F);
		itemForward.addActionListener(mListener);
		itemForward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0));
		submenuOrder.add(itemForward);

		itemToFront = new JMenuItem(Menu.TOFRONT, KeyEvent.VK_R);
		itemToFront.addActionListener(mListener);
		itemToFront.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
		submenuOrder.add(itemToFront);

		itemToBack = new JMenuItem(Menu.TOBACK, KeyEvent.VK_A);
		itemToBack.addActionListener(mListener);
		itemToBack.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		submenuOrder.add(itemToBack);
		submenuOrder.setMnemonic('O');

		this.add(submenuOrder);

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


} // end of class

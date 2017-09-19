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

import ee.ioc.cs.vsle.editor.Menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * Created by IntelliJ IDEA.
 * Author: Aulo Aasmaa
 * Date: 14.10.2003
 * Time: 9:15:36
 */
public class IconPortPopupMenu
	extends JPopupMenu
	implements ActionListener {

  /**
   * Port, for which the properties are set. Selected in the IconEditor application.
   */
  IconPort port;

  /**
   * Reference to the IconEditor application.
   */
  IconEditor editor;

  /**
   * Class constructor.
   * @param port IconPort - port reference. Selected in the IconEditor application.
   * @param editor IconEditor - IconEditor application reference.
   */
  IconPortPopupMenu(IconPort port, IconEditor editor) {
	super();
	this.port = port;
	this.editor = editor;

	JMenuItem menuItem = new JMenuItem(Menu.DELETE, KeyEvent.VK_D);
	menuItem.addActionListener(this);
	menuItem.setActionCommand(Menu.DELETE);
	this.add(menuItem);
	this.addSeparator();
	menuItem = new JMenuItem(Menu.PROPERTIES, KeyEvent.VK_P);
	menuItem.addActionListener(this);
	menuItem.setActionCommand(Menu.PROPERTIES);
	this.add(menuItem);
  } // IconPortPopupMenu constructor

  /**
   * Action event listener method.
   * @param e ActionEvent - action event performed.
   */
  public void actionPerformed(ActionEvent e) {
	  if (e.getActionCommand().equals(Menu.DELETE)) {
		  editor.ports.remove(editor.ports.indexOf(port));
		  editor.repaint();
	  } else if (e.getActionCommand().equals(Menu.PROPERTIES)) {
		  new PortPropertiesDialog(editor, port).setVisible( true );
	  }
  } // actionPerformed

}

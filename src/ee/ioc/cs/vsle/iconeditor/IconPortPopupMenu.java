package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.editor.Menu;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:15:36
 * To change this template use Options | File Templates.
 */
public class IconPortPopupMenu
	extends JPopupMenu
	implements ActionListener {

	IconPort port;
	IconEditor editor;

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
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(Menu.DELETE)) {
			editor.ports.remove(editor.ports.indexOf(port));
			editor.repaint();
		} else if (e.getActionCommand().equals(Menu.PROPERTIES)) {
			PortPropertiesDialog ppd = new PortPropertiesDialog(editor, port);
			ppd.setPortName(port.getName());
			ppd.setStrict(port.isStrict());
			ppd.setAreaConn(port.isArea());
			ppd.setModal(true);
		}
	}

}

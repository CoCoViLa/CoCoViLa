package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.Port;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 */
public class PortPopupMenu extends JPopupMenu
	implements ActionListener {

	Port port;

	PortPopupMenu(Port port) {
		super();
		this.port = port;
		JMenuItem menuItem = new JMenuItem("Set as known");

		menuItem.addActionListener(this);
		menuItem.setActionCommand("known");
		this.add(menuItem);
		menuItem = new JMenuItem("Set as target");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("target");
		this.add(menuItem);
		menuItem = new JMenuItem("Add watch");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("watch");
		this.add(menuItem);
		menuItem = new JMenuItem("Remove watch");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("nowatch");
		this.add(menuItem);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("known")) {
			port.setKnown(true);
			port.setTarget(false);
		} else if (e.getActionCommand().equals("target")) {
			port.setKnown(false);
			port.setTarget(true);
		} else if (e.getActionCommand().equals("watch")) {
			port.setWatch(true);
		} else if (e.getActionCommand().equals("nowatch")) {
			port.setWatch(false);
		}
	}

}

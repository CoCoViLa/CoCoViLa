package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.PackageClass;

import java.io.File;
import java.awt.BorderLayout;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;

/**
 */
public class Palette {
	public JToolBar toolBar;
	Editor editor;

	public Palette(VPackage vPackage, MouseOps mListener, Editor ed) {
		toolBar = new JToolBar();
		this.editor = ed;
		JButton[] buttons = new JButton[50];
		ImageIcon icon;

		// add relation and selection tools
		icon = new ImageIcon("images/mouse.gif");
		JButton selection = new JButton(icon);

		selection.setActionCommand(State.selection);
		selection.addActionListener(mListener);
		selection.setToolTipText("Select / Drag");
		toolBar.add(selection);

		icon = new ImageIcon("images/rel.gif");
		JButton relation = new JButton(icon);

		relation.setActionCommand(State.relation);
		relation.addActionListener(mListener);
		relation.setToolTipText("Relation");
		toolBar.add(relation);

		icon = new ImageIcon("images/magnifier.gif");
		JButton magnifier = new JButton(icon);

		magnifier.setActionCommand(State.magnifier);
		magnifier.addActionListener(mListener);
		magnifier.setToolTipText("Magnifier");
		toolBar.add(magnifier);

		icon = new ImageIcon("images/rel.gif");
		JButton draw = new JButton(icon);

		draw.setActionCommand("clonedrawing");
		draw.addActionListener(mListener);
		toolBar.add(draw);

		// read package info and add it to tables
		for (int i = 0; i < vPackage.classes.size(); i++) {
			PackageClass pClass = (PackageClass) vPackage.classes.get(i);

			icon = new ImageIcon(RuntimeProperties.packageDir + File.separator + pClass.icon);
			buttons[i] = new JButton(icon);
			buttons[i].setActionCommand(pClass.name);
			buttons[i].addActionListener(mListener);
			toolBar.add(buttons[i]);
		}

		editor.mainPanel.add(toolBar, BorderLayout.NORTH);
	}

	void removeToolbar() {
		editor.mainPanel.remove(toolBar);
	}
}

package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.util.*;

import java.io.File;
import java.awt.BorderLayout;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;

/**

 */
public class Palette {
	public JToolBar toolBar;
	Canvas canvas;

	public Palette(VPackage vPackage, MouseOps mListener, Canvas canv) {
		toolBar = new JToolBar();
		this.canvas = canv;
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

		// read package info and add it to tables
		for (int i = 0; i < vPackage.classes.size(); i++) {
			PackageClass pClass = (PackageClass) vPackage.classes.get(i);

			icon = new ImageIcon(
				RuntimeProperties.packageDir + File.separator + pClass.icon);
			buttons[i] = new JButton(icon);
			buttons[i].setToolTipText(pClass.description);
			if (pClass.relation == true) {
				buttons[i].setActionCommand("??" + pClass.name); //to denote a class which is a relation
			} else
				buttons[i].setActionCommand(pClass.name);
			buttons[i].addActionListener(mListener);
			toolBar.add(buttons[i]);
		}

		db.p("siin");
		canvas.add(toolBar, BorderLayout.NORTH);
	}

	void removeToolbar() {
		canvas.remove(toolBar);
	}
}

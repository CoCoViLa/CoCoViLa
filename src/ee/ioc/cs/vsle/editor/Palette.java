package ee.ioc.cs.vsle.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.VPackage;

/**
 * Menubar that contains buttons for choosing tools
 * and classes from the current package.
 */
public class Palette implements ActionListener {
	public JToolBar toolBar;
	Canvas canvas;
    ArrayList<JToggleButton> buttons;

	public Palette(VPackage vPackage, Canvas canv) {
		toolBar = new JToolBar();
        buttons = new ArrayList<JToggleButton>();
		this.canvas = canv;
		ImageIcon icon;

		// add relation and selection tools
		icon = new ImageIcon("images/mouse.gif");
		JToggleButton selection = new JToggleButton(icon);

		selection.setActionCommand(State.selection);
        selection.setSelected(true);
		selection.addActionListener(this);
		selection.setToolTipText("Select / Drag");
        buttons.add(selection);
		toolBar.add(selection);

		icon = new ImageIcon("images/rel.gif");
		JToggleButton relation = new JToggleButton(icon);

		relation.setActionCommand(State.relation);
		relation.addActionListener(this);
		relation.setToolTipText("Relation");
        buttons.add(relation);
		toolBar.add(relation);

		icon = new ImageIcon("images/magnifier.gif");
		JToggleButton magnifier = new JToggleButton(icon);

		magnifier.setActionCommand(State.magnifier);
		magnifier.addActionListener(this);
		magnifier.setToolTipText("Magnifier");
        buttons.add(magnifier);
		toolBar.add(magnifier);

        toolBar.addSeparator();
        
		// read package info and add it to tables
		for (int i = 0; i < vPackage.classes.size(); i++) {
			PackageClass pClass = vPackage.classes.get(i);
			if (pClass.icon.equals("default.gif")) {
                icon = new ImageIcon("images/default.gif");
			} else {
				icon = new ImageIcon(
					RuntimeProperties.packageDir + File.separator + pClass.icon);
			}
			JToggleButton button = new JToggleButton(icon);
			button.setToolTipText(pClass.description);
			if (pClass.relation == true)
				button.setActionCommand("??" + pClass.name); //to denote a class which is a relation
			else
				button.setActionCommand(pClass.name);

            button.addActionListener(this);
            buttons.add(button);
			toolBar.add(button);
		}
		canvas.add(toolBar, BorderLayout.NORTH);
	}

    public void actionPerformed(ActionEvent e) {
        int i = buttons.lastIndexOf(e.getSource());
        if (i < 0)
            return;

        // ignore the click if the button _was_ already selected
        JToggleButton button = buttons.get(i);
        if (!button.isSelected()) {
            button.setSelected(true);
            return;
        } else {
            for (JToggleButton b: buttons) {
                if (b != button)
                    b.setSelected(false);
            }
        }
        
        String cmd = e.getActionCommand();
        
        if (State.relation.equals(cmd))
            canvas.mListener.setState(State.addRelation);
        else if (State.selection.equals(cmd))
            canvas.mListener.setState(State.selection);
        else if (State.magnifier.equals(cmd))
            canvas.mListener.setState(State.magnifier);
        else {
            canvas.mListener.setState(cmd);
            canvas.mListener.startAddingObject();
        }
        canvas.drawingArea.grabFocus();
        canvas.drawingArea.repaint();
    }

    void resetButtons() {
        for (int i = 0; i < buttons.size(); i++)
            buttons.get(i).setSelected(i == 0);
    }
}

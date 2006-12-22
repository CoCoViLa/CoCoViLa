package ee.ioc.cs.vsle.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.VPackage;

/**
 * Menubar that contains buttons for choosing tools
 * and classes from the current package.
 */
public class Palette implements ActionListener {
	
	/**
	 * Allowed zoom levels of the scheme view. Should always contain
	 * at least the value 1.0f.
	 */
	public static final float[] ZOOM_LEVELS 
			= { .1f, .2f, .5f, .75f, 1.0f, 1.5f, 2.0f, 4.0f };

	public JToolBar toolBar;
	Canvas canvas;
    ArrayList<JToggleButton> buttons;

	public Palette(VPackage vPackage, final Canvas canv) {
		toolBar = new JToolBar();
        buttons = new ArrayList<JToggleButton>();
		this.canvas = canv;
		ImageIcon icon;

		// add relation and selection tools
		icon = FileFuncs.getImageIcon("images/mouse.gif", false );
		JToggleButton selection = new JToggleButton(icon);

		selection.setActionCommand(State.selection);
        selection.setSelected(true);
		selection.addActionListener(this);
		selection.setToolTipText("Select / Drag");
        buttons.add(selection);
		toolBar.add(selection);

		icon = FileFuncs.getImageIcon("images/rel.gif", false );
		JToggleButton relation = new JToggleButton(icon);

		relation.setActionCommand(State.addRelation);
		relation.addActionListener(this);
		relation.setToolTipText("Relation");
        buttons.add(relation);
		toolBar.add(relation);

        toolBar.addSeparator();

		// read package info and add it to tables
		for (int i = 0; i < vPackage.classes.size(); i++) {
			PackageClass pClass = vPackage.classes.get(i);
			if (pClass.icon.equals("default.gif")) {
                icon = FileFuncs.getImageIcon("images/default.gif", false );
			} else {
				icon = FileFuncs.getImageIcon( canvas.getWorkDir() + pClass.icon, true );
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

		toolBar.addSeparator();
		toolBar.add(Box.createGlue());

		JComboBox zoom = getZoomComboBox(getDefaultZoom());

		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i = ((JComboBox) e.getSource()).getSelectedIndex();
				canvas.setScale(ZOOM_LEVELS[i]);
			}
			
		});
		toolBar.add(zoom);

		canvas.add(toolBar, BorderLayout.NORTH);
	}

	/**
	 * Reads the saved default zoom value. The returned value is guaranteed
	 * to exist in ZOOM_LEVELS. It is assumed that the value 1.0f is always
	 * present in ZOOM_LEVELS.
	 * @return default zoom
	 */
	public static float getDefaultZoom() {
		float zoom;
		
		try {
			zoom = Float.parseFloat(PropertyBox.getProperty(
				PropertyBox.ZOOM_LEVEL));

			// validate the value
			for (float f : ZOOM_LEVELS) {
				if (zoom == f)
					break;
			}
		} catch (NumberFormatException e) {
			db.p(e);
			zoom = 1.0f;
		}
		
		return zoom;
	}

	/**
	 * Creates and returns a new combo box for selecting a zoom level from
	 * predefined zoom values.
	 * @param selectedZoom the zoom value that should be selected by default
	 * @return zoom combo box
	 */
	public static JComboBox getZoomComboBox(float selectedZoom) {
		JComboBox zoom = new JComboBox();
		for (int i = 0; i < ZOOM_LEVELS.length; i++) {
			String label = Integer.toString((int) (ZOOM_LEVELS[i] * 100.0f));
			zoom.addItem(label + "%");
			
			if (ZOOM_LEVELS[i] == selectedZoom)
				zoom.setSelectedIndex(i);
		}

		// Avoid stretching the combobox to fill the whole space.
		// The width + 15 hack is needed because of GTK LnF
		Dimension d = zoom.getPreferredSize();
		d.width += 15;
		zoom.setPreferredSize(d);
		zoom.setMinimumSize(d);
		zoom.setMaximumSize(d);

		return zoom;
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
        }
		for (JToggleButton b: buttons) {
		    if (b != button)
		        b.setSelected(false);
		}
        
        canvas.mListener.setState(e.getActionCommand());

        canvas.drawingArea.grabFocus();
        canvas.drawingArea.repaint();
    }

    void resetButtons() {
        for (int i = 0; i < buttons.size(); i++)
            buttons.get(i).setSelected(i == 0);
    }
}

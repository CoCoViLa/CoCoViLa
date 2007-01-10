package ee.ioc.cs.vsle.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.util.db;
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

	public static final Dimension BUTTON_SPACE = new Dimension(2, 0);
	public static final Dimension PANEL_SPACE = new Dimension(5, 0);
	public static final Insets BUTTON_BORDER = new Insets(0, 0, 0, 0);
	
	public JPanel toolBar;
	Canvas canvas;
    ArrayList<JToggleButton> buttons;

	public Palette(VPackage vPackage, final Canvas canv) {
		this.canvas = canv;

		buttons = new ArrayList<JToggleButton>();

		toolBar = new JPanel();
		toolBar.setOpaque(false); // some lnf-s may use gradients for bg
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.LINE_AXIS));
		toolBar.setBorder(new EmptyBorder(2, 0, 2, 0));

		toolBar.add(createToolPanel());
		toolBar.add(Box.createRigidArea(PANEL_SPACE));
		toolBar.add(new ScrollableBar(createClassPanel(vPackage)));
		toolBar.add(Box.createRigidArea(PANEL_SPACE));
		toolBar.add(createZoomPanel());

		canvas.add(toolBar, BorderLayout.NORTH);
	}

	private JComponent createClassPanel(VPackage vPackage) {
		JPanel classPanel = new JPanel();
		classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.LINE_AXIS));
		classPanel.setOpaque(false);
        
        // read package info and add it to the palette
		for (int i = 0; i < vPackage.classes.size(); i++) {
			PackageClass pClass = vPackage.classes.get(i);

			ImageIcon icon;
			if ("default.gif".equals(pClass.icon))
				icon = FileFuncs.getImageIcon("images/default.gif", false);
			else
				icon = FileFuncs.getImageIcon(canvas.getWorkDir()
						+ pClass.icon, true);

			String actionCmd;
			if (pClass.relation == true)
				// to denote a class which is a relation
				actionCmd = State.addRelObjPrefix + pClass.name; 
			else
				actionCmd = pClass.name;
			
			JToggleButton button = createButton(icon, pClass.description,
					actionCmd);

			classPanel.add(button);
			
			if (i < vPackage.classes.size() - 1)
				classPanel.add(Box.createRigidArea(BUTTON_SPACE));
		}
		return classPanel;
	}

	private JComponent createZoomPanel() {
		JPanel zoomPanel = new JPanel();
		zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.LINE_AXIS));
		zoomPanel.setOpaque(false);
		
		JComboBox zoom = getZoomComboBox(getDefaultZoom());

		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i = ((JComboBox) e.getSource()).getSelectedIndex();
				canvas.setScale(ZOOM_LEVELS[i]);
			}
			
		});
		zoomPanel.add(zoom);

		zoomPanel.setPreferredSize(zoomPanel.getMinimumSize());
		zoomPanel.setMaximumSize(zoomPanel.getMinimumSize());

		return zoom;
	}

	private JComponent createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.LINE_AXIS));
        toolPanel.setOpaque(false);

		JToggleButton selection = createButton(
				FileFuncs.getImageIcon("images/mouse.gif", false),
				"Select / Drag", State.selection);

		selection.setSelected(true);
		toolPanel.add(selection);

		toolPanel.add(Box.createRigidArea(BUTTON_SPACE));

		JToggleButton relation = createButton(
				FileFuncs.getImageIcon("images/rel.gif", false),
				"Relation", State.addRelation);

		toolPanel.add(relation);

		toolPanel.setPreferredSize(toolPanel.getMinimumSize());
		toolPanel.setMaximumSize(toolPanel.getMinimumSize());

		return toolPanel;
	}

	private JToggleButton createButton(ImageIcon icon, String descr,
			String actionCmd) {
		
		JToggleButton button = new JToggleButton(icon);

		button.setActionCommand(actionCmd);
		button.setToolTipText(descr);

		// Palette buttons should be small, that's what JToolBar is doing
		button.setMargin(BUTTON_BORDER);

		button.addActionListener(this);
        buttons.add(button);
        
        return button;
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
		Dimension d = zoom.getPreferredSize();
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

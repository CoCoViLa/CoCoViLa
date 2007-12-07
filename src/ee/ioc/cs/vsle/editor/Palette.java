package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

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

	public static final Dimension BUTTON_SPACE = new Dimension(1, 0);
	public static final Dimension PANEL_SPACE = new Dimension(5, 0);
	public static final Insets BUTTON_BORDER = new Insets(2, 2, 2, 2);

	private JPanel toolBar;
	private Canvas canvas;
    private ArrayList<JToggleButton> buttons;

    private final MouseListener mouseLst = new MouseAdapter() {

        @Override
        public void mouseClicked( MouseEvent e ) {
            if( SwingUtilities.isRightMouseButton( e ) ) {
                int i = buttons.lastIndexOf( e.getSource() );
                
                if ( i < 0 ) {
                    return;
                }
                
                String action = buttons.get(i).getActionCommand();
                
                if( State.isAddObject( action ) || State.isAddRelClass( action ) ) {
                    canvas.openClassCodeViewer( State.getClassName( action ) );
                }
            }
        }
    };
    
	public Palette(VPackage vPackage, final Canvas canv) {
		this.canvas = canv;

		buttons = new ArrayList<JToggleButton>();

		toolBar = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		toolBar.setOpaque(false); // there is transparent space between buttons
		toolBar.setBorder(new EmptyBorder(2, 0, 2, 0));

		c.gridx = 0;
		toolBar.add(createToolPanel(), c);

		c.gridx = 2;
		toolBar.add(createZoomPanel(), c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.insets = new Insets(0, PANEL_SPACE.width, 0, PANEL_SPACE.width);
		toolBar.add(new ScrollableBar(createClassPanel(vPackage)), c);

		canvas.add(toolBar, BorderLayout.PAGE_START);
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

			JToggleButton button = createButton(icon, pClass.name + " " + pClass.getDescription(),
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

		JComboBox zoom = getZoomComboBox( RuntimeProperties.getZoomFactor() );

		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int i = ((JComboBox) e.getSource()).getSelectedIndex();
				canvas.setScale(ZOOM_LEVELS[i]);
			}

		});
		zoomPanel.add(zoom);

		return zoomPanel;
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
		button.addMouseListener( mouseLst );
        buttons.add(button);

        return button;
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

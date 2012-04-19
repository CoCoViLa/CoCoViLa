package ee.ioc.cs.vsle.editor;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import ee.ioc.cs.vsle.util.FileFuncs;

/**
 * Abstract base class for Class Editor and Scheme Editor toolbars that
 * contains common functionality.
 */
public abstract class PaletteBase implements ActionListener {

    /**
     * Allowed zoom levels of the scheme view. Should always contain
     * at least the value 1.0f.
     */
    public static final float[] ZOOM_LEVELS
                    = { .1f, .2f, .5f, .75f, 1.0f, 1.5f, 2.0f, 4.0f };

    public static final Dimension BUTTON_SPACE = new Dimension(1, 0);
    public static final Dimension PANEL_SPACE = new Dimension(5, 0);

    // To make buttons take less space on the toolbar
    public static final Insets BUTTON_BORDER = new Insets(2, 2, 2, 2);

    protected ArrayList<JToggleButton> buttons;
    protected JComponent toolBar;
    protected MouseListener mouseListener;

    protected abstract void setState(String state);

    @Override
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
            if (b != button) {
                b.setSelected(false);
            }
        }

        setState(e.getActionCommand());
    }

    protected PaletteBase() {
        buttons = new ArrayList<JToggleButton>();
    }

    protected JToggleButton createButton(String iconString, String descr,
            String actionCmd) {

        return createButton(FileFuncs.getImageIcon(iconString, false),
                descr, actionCmd);
    }

    protected JToggleButton createButton(ImageIcon icon, String descr,
            String actionCmd) {

        JToggleButton button = new JToggleButton(icon);
        // Workaround for 2849980
        button.setSelectedIcon(icon);
        button.setActionCommand(actionCmd);
        button.setToolTipText(descr);

        // Palette buttons should be smaller, that's what JToolBar is doing
        button.setMargin(BUTTON_BORDER);

        button.addActionListener(getButtonActionListener());
        button.addMouseListener(getButtonMouseListener());
        buttons.add(button);
        return button;
    }

    public void resetButtons() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(i == 0);
        }
        toolBar.revalidate();
    }

    /**
     * Creates and returns a new combo box for selecting a zoom level from
     * predefined zoom values.
     * @param selectedZoom the zoom value that should be selected by default
     * @return zoom combo box
     */
    public static JComboBox getZoomComboBox(float selectedZoom) {
        JComboBox zoom = new JComboBox() {
            // get{Min,Max}Size methods need to be overriden because
            // these cannot be set to a constant value.  The sizes may
            // change when, for example, L'n'F is changed.
            @Override
            public Dimension getMaximumSize() {
                return super.getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return super.getPreferredSize();
            }
        };
        for (int i = 0; i < ZOOM_LEVELS.length; i++) {
            String label = Integer.toString((int) (ZOOM_LEVELS[i] * 100.0f));
            zoom.addItem(label + "%");

            if (ZOOM_LEVELS[i] == selectedZoom)
                zoom.setSelectedIndex(i);
        }
        return zoom;
    }

    protected abstract ActionListener getZoomListener();

    /**
     * Returns an action listener that is attached to buttons created with
     * createButton().
     * @return button action listener
     */
    protected ActionListener getButtonActionListener() {
        return this;
    }

    /**
     * Returns a mouse listener that is attached to buttons created with
     * createButton();
     * @return button mouse listener
     */
    protected MouseListener getButtonMouseListener() {
        return mouseListener;
    }

    /**
     * Creates a panel containing zoom level selection control with
     * appropriate action listeners already attached.
     * @return zoom control panel
     */
    protected JComponent createZoomPanel() {
        // Note that unlike JButtons, JComboBoxes do not differentiate
        // their look depending on their container (JToolBar or sth else).
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.LINE_AXIS));
        zoomPanel.setOpaque(false);

        JComboBox zoom = getZoomComboBox(RuntimeProperties.getZoomFactor());
        zoom.addActionListener(getZoomListener());
        // No need to set size as get*Size methods are overriden
        zoomPanel.add(zoom);

        return zoomPanel;
    }
    
    protected void destroy() {
        for ( JToggleButton button : buttons ) {
            button.removeActionListener(getButtonActionListener());
            button.removeMouseListener(getButtonMouseListener());
            // Flush icons, otherwise updated icons will not get displayed.
            ((ImageIcon) button.getIcon()).getImage().flush();
        }
        buttons.clear();
        buttons = null;
        mouseListener = null;
    }
}

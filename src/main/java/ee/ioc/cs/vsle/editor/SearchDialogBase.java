package ee.ioc.cs.vsle.editor;

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

import static ee.ioc.cs.vsle.util.GuiUtil.buildGridBagConstraints;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public abstract class SearchDialogBase {

    // Common UI strings
    public static final String LABEL_FIND = "Find:";

    public static final String BUTTON_FIND = "Find";
    public static final String BUTTON_CLOSE = "Close";

    public static final String BORDER_TITLE_SEARCH_IN = "Search In";
    public static final String BORDER_TITLE_OPTIONS = "Search Options";

    public static final String CHB_BACKWARD = "Backward";
    public static final String CHB_CASE = "Case Sensitive";
    public static final String CHB_FIELD_NAMES = "Field Names";
    public static final String CHB_FIELD_VALUES = "Field Values";
    public static final String CHB_OBJECT_NAMES = "Object Names";
    public static final String CHB_REGEX = "Regular Expression";
    public static final String CHB_WRAP = "Wrap Search";

    // Common constants
    public static final int SEARCH_FLD_COLS = 10;

    // The search window
    protected JDialog searchDialog;

    // Common GUI actions
    protected Action defaultCloseAction;
    protected Action defaultFindAction;

    // Increased visibility for inner classes
    protected JTextField searchField;

    // Action names
    protected static final String ACTION_CLOSE = "closeAction";
    protected static final String ACTION_FIND = "findAction";

    /**
     * Constructor for subclasses.
     * Takes care of building a basic GUI if the subclass implements
     * a few methods.
     * @param owner owner window
     */
    protected SearchDialogBase(Window owner) {
        searchDialog = new JDialog(owner, getWindowTitle());
        searchDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        searchDialog.setAlwaysOnTop(true);
        searchDialog.setLocationRelativeTo(owner);

        // Create actions and init keyboard mappings
        defaultFindAction = getFindAction();
        defaultCloseAction = getCloseAction();

        ActionMap am = searchDialog.getRootPane().getActionMap();
        InputMap im = searchDialog.getRootPane().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        am.put(ACTION_CLOSE, defaultCloseAction);
        am.put(ACTION_FIND, defaultFindAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ACTION_CLOSE);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ACTION_FIND);

        // Construct the window components
        searchDialog.add(makeSearchPanel(), BorderLayout.PAGE_START);
        searchDialog.add(makeOptionPanel(), BorderLayout.CENTER);
        searchDialog.add(makeButtonPanel(), BorderLayout.PAGE_END);

        searchDialog.setMinimumSize(RuntimeProperties.WINDOW_MIN_DIM);
        searchDialog.pack();
    }

    /**
     * Makes the window visible and requests focus.
     */
    public void show() {
        assert SwingUtilities.isEventDispatchThread();

        searchDialog.requestFocus();
        searchDialog.setVisible(true);
    }

    /**
     * Returns the title of the window.
     * This method has to be implemented by subclasses.
     * @return the title of the window
     */
    protected abstract String getWindowTitle();

    /**
     * Create and return the panel containing Find and Close buttons.
     * The default implementation returns a button panel with Find and
     * Close buttons bound to corresponding actions.
     * @return button panel
     */
    protected JComponent makeButtonPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.add(Box.createHorizontalGlue());
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnFind = new JButton(defaultFindAction);
        p.add(btnFind);

        p.add(Box.createHorizontalStrut(5));

        JButton btnClose = new JButton(defaultCloseAction);
        p.add(btnClose);

        return p;
    }

    /**
     * Create and return the panel containing checkboxes for search options.
     * The default implementation returns an empty panel.
     * @return an empty panel
     */
    protected JComponent makeOptionPanel() {
        return new JPanel();
    }

    /**
     * Creates and returns the panel containing textfield for the text
     * to be searched for etc.
     * @return search panel
     */
    protected JPanel makeSearchPanel() {
        searchField = new JTextField(SEARCH_FLD_COLS);

        JLabel searchLabel = new JLabel(LABEL_FIND);
        searchLabel.setLabelFor(searchField);
        searchLabel.setDisplayedMnemonic(KeyEvent.VK_F);

        GridBagLayout gridbag = new GridBagLayout();
        JPanel searchPanel = new JPanel(gridbag);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        buildGridBagConstraints(c, 0, 0, 1, 1, 0, 0,
                GridBagConstraints.NONE, GridBagConstraints.LINE_START);
        searchPanel.add(searchLabel, c);

        buildGridBagConstraints(c, 1, 0, GridBagConstraints.REMAINDER,
                1, 1, 0, GridBagConstraints.HORIZONTAL,
                GridBagConstraints.LINE_START);
        searchPanel.add(searchField, c);

        return searchPanel;
    }

    /**
     * Hides the window and releases some resources.
     * The window can be made visible again using show().
     */
    public void dispose() {
        assert SwingUtilities.isEventDispatchThread();

        searchDialog.dispose();
    }

    /**
     * Helper function for creating and initializing JCheckBoxes.
     * @param label the text of the checkbox; null is accepted
     * @param enabled enable or disable the checkbox
     * @param selected select or unselect the checkbox
     * @param mnemonic mnemonic key from KeyEvent.VK_*, a negative value
     * for no mnemonic key. 
     * @return a new checkbox initialized with the specified values
     */
    protected JCheckBox initCheckBox(String label, boolean enabled,
            boolean selected, int mnemonic) {

        JCheckBox chbox = new JCheckBox(label, selected);

        chbox.setEnabled(enabled);

        if (mnemonic > -1) {
            chbox.setMnemonic(mnemonic);
        }

        return chbox;
    }

    protected abstract Action getFindAction();

    /**
     * Returns a default implementation of close action.
     * Subsequent calls return the same instance.
     * @return close action instance
     */
    protected Action getCloseAction() {
        if (defaultCloseAction == null) {
            defaultCloseAction = new CloseAction();
        }
        return defaultCloseAction;
    }

    /**
     * Action class for closing the search dialog.
     */
    class CloseAction extends AbstractAction {

        public CloseAction() {
            super(BUTTON_CLOSE);
        }

        public void actionPerformed(ActionEvent e) {
            dispose();
        }

    }
}

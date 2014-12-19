package ee.ioc.cs.vsle.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ee.ioc.cs.vsle.util.GuiUtil;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;

/**
 * Search dialog for finding objects on the scheme by name, field values etc.
 */
public class SchemeSearchDialog extends SearchDialogBase {

    // UI strings
    private static final String WINDOW_TITLE = "Scheme Search";

    // The scheme search dialog is a singleton
    private static SchemeSearchDialog myInstance;

    // Search In panel checkboxes
    protected JCheckBox chbFieldValues;
    protected JCheckBox chbFieldNames;
    protected JCheckBox chbObjectNames;

    // Search Options panel checkboxes
    protected JCheckBox chbCase;
    protected JCheckBox chbWrap;
    protected JCheckBox chbBackward;
    protected JCheckBox chbRegex;

    /**
     * Private constructor.
     * The getDialog() method should be used for getting an instance.
     * @param owner the owner of this dialog
     */
    private SchemeSearchDialog(final Window owner) {
        super(owner);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateEnabled();
            }
            public void insertUpdate(DocumentEvent e) {
                updateEnabled();
            }
            public void removeUpdate(DocumentEvent e) {
                updateEnabled();
            }
        });
    }

    public static SchemeSearchDialog getDialog() {
        assert SwingUtilities.isEventDispatchThread();

        if (myInstance == null) {
            myInstance = new SchemeSearchDialog(Editor.getInstance());
        }

        return myInstance;
    }

    /**
     * Creates a new search option panel
     * @return search option panel
     */
    @Override
    protected JComponent makeOptionPanel() {
        // Search In panel
        JPanel searchInPanel = new JPanel(new GridBagLayout());
        searchInPanel.setBorder(
                BorderFactory.createTitledBorder(BORDER_TITLE_SEARCH_IN));

        chbFieldValues = initCheckBox(CHB_FIELD_VALUES, true, true,
                KeyEvent.VK_V);

        chbFieldNames = initCheckBox(CHB_FIELD_NAMES, true, false,
                KeyEvent.VK_A);

        chbObjectNames = initCheckBox(CHB_OBJECT_NAMES, true, true,
                KeyEvent.VK_O);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;

        searchInPanel.add(chbFieldValues, c);

        c.gridx = 1;
        searchInPanel.add(chbFieldNames, c);

        c.gridx = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        searchInPanel.add(chbObjectNames, c);

        // Search Options subpanel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(
                BorderFactory.createTitledBorder(BORDER_TITLE_OPTIONS));

        chbCase = initCheckBox(CHB_CASE, false, true, KeyEvent.VK_C);
        chbWrap = initCheckBox(CHB_WRAP, false, true, KeyEvent.VK_W);
        chbBackward = initCheckBox(CHB_BACKWARD, false, false, KeyEvent.VK_B);
        chbRegex = initCheckBox(CHB_REGEX, false, false, KeyEvent.VK_R);

        GuiUtil.resetGridBagConstraints(c);
        c.anchor = GridBagConstraints.NORTHWEST;

        optionsPanel.add(chbWrap, c);

        c.gridx = 1;
        optionsPanel.add(chbCase, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        optionsPanel.add(chbBackward, c);

        c.gridx = 1;
        optionsPanel.add(chbRegex, c);

        // Wrapper panel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());

        // Common constraints for all subpanels
        GuiUtil.resetGridBagConstraints(c);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 8, 10, 8);
        c.weightx = 1.0;

        wrapperPanel.add(searchInPanel, c);

        c.gridy = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        wrapperPanel.add(optionsPanel, c);

        return wrapperPanel;
    }

    protected void updateEnabled() {
        String s = searchField.getText();
        if (s != null && s.length() > 0) {
            defaultFindAction.setEnabled(true);
        } else {
            defaultFindAction.setEnabled(false);
            ((FindAction) defaultFindAction).resetSearch();
        }
    }

    @Override
    protected Action getFindAction() {
        if (defaultFindAction == null) {
            defaultFindAction = new FindAction();
        }
        return defaultFindAction;
    }

    @Override
    protected String getWindowTitle() {
        return WINDOW_TITLE;
    }

    /**
     * Action class for searching objects from scheme.
     */
    class FindAction extends AbstractAction {

        private int offset;

        public FindAction() {
            super(BUTTON_FIND);
            setEnabled(false);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_N));
        }

        public void actionPerformed(ActionEvent e) {
            String needle = searchField.getText();

            // Ignore empty search string
            if (needle == null || needle.length() < 1) {
                return;
            }

            // Ignore empty object list
            ObjectList objects = getObjectList();
            if (objects == null || objects.size() < 1) {
                return;
            }

            // When offset is larger than the actual number of objects
            // and wrappin is allowed the search should wrap.
            if (offset >= objects.size() && chbWrap.isSelected()) {
                offset = 0;
            }

            int indexBound = objects.size();

            boolean found = false;

            for (int i = offset; i < indexBound; i++) {
                GObj obj = objects.get(i);

                if (matches(needle, obj)) {
                    found = true;
                    focus(obj);
                    offset = i + 1;
                    break;
                }

                // If the end of object list is reached and we started
                // at nonzero offset and wrap is on then scan from the
                // beginning up to offset.
                if (offset > 0 && chbWrap.isSelected()
                        && i == objects.size() - 1) {

                    i = -1; // will be incremented at the end of loop
                    indexBound = offset;
                }
            }

            // Clear selections to indicate failure
            if (!found) {
                focus(null);
            }
        }

        private void focus(GObj obj) {
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if (canvas != null) {
                canvas.focusObject(obj);
            }
        }

        private boolean matches(String needle, GObj obj) {

            // Objec name
            if (chbObjectNames.isSelected()) {
                String objName = obj.getName();
                if (objName != null && objName.contains(needle)) {
                    return true;
                }
            }

            // Fields, Values
            boolean fvalues = chbFieldValues.isSelected();
            boolean fnames = chbFieldNames.isSelected();

            if (fnames || fvalues) {
                Collection<ClassField> fields = obj.getFields();
                if (fields != null && fields.size() > 0) {
                    for (ClassField field : fields) {
                        if (field.isPrimitiveOrString() || field.isArray()) {
                            // Check field name
                            if (fnames) {
                                String name = field.getName();
                                if (name != null && name.contains(needle)) {
                                    return true;
                                }
                            }

                            // Check value
                            if (fvalues) {
                                // Something fancier should be done with
                                // array types, probably.
                                String val = field.getValue();
                                if (val != null && val.contains(needle)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        /**
         * The internal state of this FindAction is reset so that
         * the next action is started from the beginning.
         */
        public void resetSearch() {
            offset = 0;
        }

        private ObjectList getObjectList() {
            ObjectList list = null;
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if (canvas != null) {
                list = canvas.getObjectList();
            }
            return list;
        }
    }
}

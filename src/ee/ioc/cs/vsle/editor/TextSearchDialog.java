package ee.ioc.cs.vsle.editor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Reusable TextSearchDialog.
 * This search dialog can be bound to any Window containing a searchable
 * JTextComponent or to a Window containing possibly many alternative text
 * components which are used through a proxy object implementing
 * the TextEditView interface.
 */
public class TextSearchDialog {

    private static final int SEARCH_FLD_COLS = 10;

    // UI strings
    private static final String WINDOW_TITLE = "Text Search";
    private static final String LABEL_FIND = "Find:";
    private static final String BUTTON_FIND = "Find";
    private static final String BUTTON_CLOSE = "Close";

    // Action names
    private static final String ACTION_CLOSE = "closeAction";
    public static final String ACTION_FIND = "findAction";

    // The haystack text component.  When textComponent is not specified
    // at construction time, the component is dynamic and should be asked
    // each time from the editView.
    private JTextComponent textComponent;
    private TextEditView editView;

    // The search window
    private final JDialog searchDialog;

    // Increased visibility for inner classes

    protected final JTextField searchField;

    // GUI actions
    protected final Action closeAction;
    protected final Action findAction;

    // We allow only one search dialog per parent window even when there
    // are multiple text components or tabs in the window.
    protected static Map<Window, TextSearchDialog> viewCache;

    /**
     * Attaches a text search window to the specified frame.
     * The search function is bound to Ctrl+F shortcut key
     * and the corresponding action name is ACTION_FIND.
     * The arguments cannot be null.
     * @param frame the frame the search function is attached to, should
     * actually be the same object as returned by the specified view later
     * @param view the proxy which is asked for active text area
     */
    public static void attachTo(JFrame frame, final TextEditView view) {
        JRootPane rp = frame.getRootPane();

        InputMap im = rp.getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK),
                ACTION_FIND);

        rp.getActionMap().put(ACTION_FIND, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TextSearchDialog.showDialog(view);
            }
        });
    }

    /**
     * Returns an existing search dialog for the window returned by the
     * specified editView or creates a new one if there is no previously
     * created dialog that is not closed yet.
     * @param editView object specifying the owner window of the dialog,
     *        cannot be null
     * @return text search dialog
     */
    public static TextSearchDialog getDialog(TextEditView editView) {
        assert SwingUtilities.isEventDispatchThread();

        if (editView == null) {
            throw new IllegalArgumentException("TextEditView cannot be null");
        }

        Window w = editView.getWindow();
        if (w == null) {
            throw new IllegalArgumentException("Owner window cannot be null");
        }

        TextSearchDialog dialog = getSearchDialogFor(editView.getWindow());
        dialog.setViewComponent(editView);
        
        return dialog;
    }

    /**
     * Returns an existing search dialog for the window returned by the
     * specified editView or creates a new one if there is no previously
     * created dialog that is not closed yet.  The arguments cannot be null.
     * @param owner the owner window of the dialog
     * @param document the text component contained in the owner window
     * @return text search dialog
     */
    public static TextSearchDialog getDialog(Window owner,
            JTextComponent document) {

        assert SwingUtilities.isEventDispatchThread();
        
        if (owner == null) {
            throw new IllegalArgumentException("Owner window cannot be null");
        }

        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        TextSearchDialog dialog = getSearchDialogFor(owner);
        dialog.setTextComponent(document);

        return dialog;
    }

    /**
     * Creates a new search dialog for the specified window and makes it
     * visible using the show() method.  If there exists already a visible
     * dialog then it will be focused.
     * @param editView object specifying the owner window of the dialog,
     *        cannot be null
     */
    public static void showDialog(TextEditView editView) {
        getDialog(editView).show();
    }

    /**
     * Creates a new search dialog for the specified window and makes it
     * visible using the show() method.  If there exists already a visible
     * dialog then it will be focused.
     * @param owner the owner window of the dialog
     * @param document the text component contained in the owner window
     */
    public static void showDialog(Window owner, JTextComponent document) {
        getDialog(owner, document).show();
    }

    /**
     * Set the dialog visible and focused and optionally update 
     * the search string from selection.
     * @param initNeedle true to update the search string, false to skip update
     */
    public void show(boolean initNeedle) {
        assert SwingUtilities.isEventDispatchThread();

        if (initNeedle) {
            updateNeedleFromText();
        }
        searchDialog.requestFocus();
        // The search field should get the focus to make is easy to input
        // and change the search string.
        searchField.requestFocusInWindow();
        searchDialog.setVisible(true);
    }

    /**
     * Set the dialog visible and focused and update the search string
     * from selection.
     */
    public void show() {
        show(true);
    }

    /**
     * Sets the search string and update the search text field.
     * The value of the search field is selected to make it easy to replace
     * from the keyboard.
     * @param needle search string
     */
    public void setNeedle(String needle) {
        assert SwingUtilities.isEventDispatchThread();

        searchField.setText(needle);
        searchField.selectAll();
    }

    /**
     * Hides the window and releases resources.
     * After destruction the window cannot be made visible again.
     */
    public void destroy() {
        assert SwingUtilities.isEventDispatchThread();

        searchDialog.dispose();
    }

    /**
     * Returns the text component to be searched from.
     * @return current text component, or null
     */
    protected JTextComponent getTextComponent() {
        return textComponent == null 
                ? editView.getTextComponent() : textComponent;
    }

    private TextSearchDialog(Window owner) {
        searchDialog = new JDialog(owner, WINDOW_TITLE);
        searchDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        searchDialog.setAlwaysOnTop(true);
        searchDialog.setLocationRelativeTo(owner);
        
        searchField = new JTextField(SEARCH_FLD_COLS);

        // Create actions and init keyboard mappings
        findAction = new FindAction();
        closeAction = new CloseAction();

        ActionMap am = searchDialog.getRootPane().getActionMap();
        InputMap im = searchDialog.getRootPane().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        am.put(ACTION_CLOSE, closeAction);
        am.put(ACTION_FIND, findAction);

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
     * Create and return the panel containing checkboxes for search options. 
     * @return the options panel
     */
    private JPanel makeOptionPanel() {
        // for future extension
        return new JPanel();
    }

    /**
     * Create and return the panel containing Find and Close buttons.
     * @return button panel
     */
    private JPanel makeButtonPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        p.add(Box.createHorizontalGlue());
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnFind = new JButton(findAction);
        p.add(btnFind);

        p.add(Box.createHorizontalStrut(5));

        JButton btnClose = new JButton(closeAction);
        p.add(btnClose);

        return p;
    }

    private static TextSearchDialog getSearchDialogFor(final Window window) {
        TextSearchDialog dialog = null;

        if (viewCache == null) {
            viewCache = new HashMap<Window, TextSearchDialog>();
        } else {
            dialog = viewCache.get(window);
        }

        if (dialog == null) {
            dialog = new TextSearchDialog(window);
            viewCache.put(window, dialog);
            dialog.searchDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    if (viewCache != null) {
                        viewCache.remove(window);
                        // let the garbage collector do its work
                        if (viewCache.size() < 1) {
                            viewCache = null;
                        }
                    }
                }
                
            });
        }
        return dialog;
    }

    private void setTextComponent(JTextComponent comp) {
        this.textComponent = comp;
        this.editView = null;
    }

    private void setViewComponent(TextEditView editView) {
        this.editView = editView;
        this.textComponent = null;
    }

    protected void updateEnabled() {
        String s = searchField.getText();
        findAction.setEnabled(s != null && s.length() > 0);
    }

    /**
     * When a reasonably small chunk of text is selected in the text component
     * then use the selected text as the needle and update the value of
     * the search field.
     * @return true when the search field was updated, false otherwise
     */
    private boolean updateNeedleFromText() {
        boolean rv = false;
        JTextComponent tc = getTextComponent();
        if (tc != null) {
            String s = tc.getSelectedText();
            if (s != null && s.length() > 0
                    && s.length() < 2 * SEARCH_FLD_COLS) {
                setNeedle(s);
                rv = true;
            }
        }
        return rv;
    }

    /**
     * Creates and returns the panel containing textfield for the text
     * to be searched for etc.
     * @return search panel
     */
    private JPanel makeSearchPanel() {
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

        JLabel searchLabel = new JLabel(LABEL_FIND);
        searchLabel.setLabelFor(searchField);
        searchLabel.setDisplayedMnemonic(KeyEvent.VK_F);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 5, 5);

        JPanel searchPanel = new JPanel(gridbag);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        c.gridx = 0;
        c.gridy = 0;
        searchPanel.add(searchLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        searchPanel.add(searchField, c);

        return searchPanel;
    }

    /**
     * Action class for closing the search dialog.
     */
    class CloseAction extends AbstractAction {

        public CloseAction() {
            super(BUTTON_CLOSE);
        }

        public void actionPerformed(ActionEvent e) {
            destroy();
        }

    }

    /**
     * Action class for searching text.
     */
    class FindAction extends AbstractAction {

        public FindAction() {
            super(BUTTON_FIND);
            setEnabled(false);
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_N));
        }

        public void actionPerformed(ActionEvent e) {
            String needle = searchField.getText();
            if (needle == null || needle.length() < 1)
                return;

            JTextComponent comp = getTextComponent();
            if (comp == null)
                return;

            int curPos = comp.getCaretPosition();
            String s = comp.getText();

            int found = s.indexOf(needle, curPos);
            if (found < 0)
                found = s.indexOf(needle);

            if (found > -1) {
                comp.setCaretPosition(found);
                comp.select(found, found + needle.length());
            }
        }
    }
}

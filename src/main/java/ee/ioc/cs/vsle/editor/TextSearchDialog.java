package ee.ioc.cs.vsle.editor;

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
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
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
public class TextSearchDialog extends SearchDialogBase {

    // UI strings
    private static final String WINDOW_TITLE = "Text Search";

    // The haystack text component.  When textComponent is not specified
    // at construction time, the component is dynamic and should be asked
    // each time from the editView.
    private JTextComponent textComponent;
    private TextEditView editView;

    // We allow only one search dialog per parent window even when there
    // are multiple text components or tabs in the window.
    protected static Map<Window, TextSearchDialog> viewCache;

    /**
     * Private constructor.
     * The methods getDialog* or showDialog* should be used.
     * @param window the owner window
     */
    private TextSearchDialog(Window window) {
        super(window);

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
    @Override
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
     * Returns the text component to be searched from.
     * @return current text component, or null
     */
    protected JTextComponent getTextComponent() {
        return textComponent == null 
                ? editView.getTextComponent() : textComponent;
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
        defaultFindAction.setEnabled(s != null && s.length() > 0);
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

    @Override
    protected String getWindowTitle() {
        return WINDOW_TITLE;
    }

    /**
     * Returns the find action instance.
     * The returned value is stored into findAction field and returned
     * on subsequent calls.
     * @return the find action
     */
    @Override
    protected Action getFindAction() {
        if (defaultFindAction == null) {
            defaultFindAction = new FindAction();
        }
        return defaultFindAction;
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

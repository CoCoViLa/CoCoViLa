package ee.ioc.cs.vsle.editor;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.*;

import ee.ioc.cs.vsle.util.*;

import java.awt.Component;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Helper class for undo-redo support of text components.
 * This class defines corresponding Action classes and provides methods
 * for creating menu items and a toolbar button panel.  It also provides
 * the PROPERY_SAVED property, that reflects the current saved state of the
 * content of the text component.
 */
public class UndoRedoDocumentPanel {

    /**
     * The name of the property that indicates whether the content of the
     * text component is saved or not. Changes are reported to registered
     * PropertyChangeListeners.
     */
    public static final String PROPERTY_SAVED = "saved";

    private Document m_document;
    private UndoManager undoManager;

    private JButton m_buttonUndo;
    private JButton m_buttonRedo;

    private UndoAction undoAction;
    private RedoAction redoAction;

    private JPanel undoRedoPanel;
    private UndoableEditListener listener;

    private boolean saved;
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * For tracking the last save. Only un/redoing to the last saved state
     * means the document is saved. This is incremented at each markSaved()
     * call done when isSaved() returns false.
     */
    private int saveId;

    /**
     * Parent window where this panel is installed, could be null.
     */
    private Component parentComponent;

    /**
     * Constructor that registers the new instance as a listener of
     * undoable edit events emitted by the specified document.
     * @param doc the document to be monitored for undoable events
     */
    public UndoRedoDocumentPanel(Document doc) {
        m_document = doc;

        listener = new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {

                UndoableEdit edit;
                if (isSaved()) {
                    // If the document was saved we need to insert a special
                    // edit to keep the state in sync.  This edit replaces
                    // the new edit by an edit that includes the new edit,
                    // and in addition stores the save id and keeps state
                    // in sync on undo/redo.
                    edit = new CompoundEdit() {

                        private int editSaveId = getSaveId();

                        @Override
                        public void redo() throws CannotRedoException {
                            super.redo();
                            setSaved(false);
                        }

                        @Override
                        public void undo() throws CannotUndoException {
                            super.undo();
                            if (editSaveId == getSaveId()) {
                                setSaved(true);
                            }
                        }

                        @Override
                        public boolean addEdit(UndoableEdit anEdit) {
                            boolean rv = super.addEdit(anEdit);
                            // this CompundEdit needs to be closed, otherwise
                            // it would keep accepting new edits and the user
                            // could never un/redo anything because the last
                            // CompoundEdit is still inProgress. When a smarter
                            // logic for collapsing edits gets implemented
                            // we probably need to reimplement parts of
                            // super.addEdit here.
                            end();
                            return rv;
                        }
                    };

                    edit.addEdit(e.getEdit());
                } else {
                    edit = e.getEdit();
                }
                getUndoManager().addEdit(edit);
                setSaved(false);
                updateUndoState();
                updateRedoState();
            }
        };
        m_document.addUndoableEditListener(listener);
    }

    /**
     * Constructor that registers the new instance as a listener of
     * undoable edit events emitted by the specified document.
     * @param doc the document to be monitored for undoable events
     * @param parent the parent window, could be null.
     */
    public UndoRedoDocumentPanel(Document doc, Component parent) {
        this(doc);
        parentComponent = parent;
    }

    /**
     * Returns an int value that gets incremented by each markSaved call when
     * isSaved returns true.
     * @return save id
     */
    int getSaveId() {
        return saveId;
    }

    /**
     * Returns a panel with buttons for undo and redo actions.
     * Subsequent calls return the same instance.
     * @return undo-redo panel with two buttons
     */
    public JPanel getUndoRedoPanel() {
        if (undoRedoPanel == null) {
            undoRedoPanel = new JPanel();
            undoRedoPanel.setOpaque(false);
            undoRedoPanel.add(getUndoButton(true));
            undoRedoPanel.add(getRedoButton(true));
        }
        return undoRedoPanel;
    }

    /**
     * Returns the undo action instance.
     * @return the undo action instance
     */
    public Action getUndoAction() {
        if (undoAction == null) {
            undoAction = new UndoAction();
        }
        return undoAction;
    }

    /**
     * Returns the redo action instance.
     * @return the redo action instance
     */
    public Action getRedoAction() {
        if (redoAction == null) {
            redoAction = new RedoAction();
        }
        return redoAction;
    }

    /**
     * Empties the undo manager edit history sending each edit a die message.
     * State of the actions is also updated.
     */
    public void discardAllEdits() {
        if (undoManager != null) {
            undoManager.discardAllEdits();
            updateUndoState();
            updateRedoState();
        }
    }

    /**
     * Deregisteres the document listener and clears undo manager's history.
     * Useful for cases where the document is used further but no undo-redo
     * support is needed. The instance and actions created by this instance
     * should not be used after a dispose() message.
     */
    public void dispose() {
        m_document.removeUndoableEditListener(listener);
        discardAllEdits();
    }

    /**
     * This method needs to be called whenever the document is saved.
     * If the document is already saved the call is ignored.  Otherwise
     * the property PROPERY_SAVED is set true and saveId is incremented.
     */
    public void markSaved() {
        if (isSaved()) {
            return;
        }

        saveId++;

        // The last UndoableEdit needs to be replaced by an edit that would
        // in addidion keep the saved state in sync.
        CompoundEdit edit = new CompoundEdit() {

            private int editSaveId = getSaveId();

            @Override
            public boolean replaceEdit(UndoableEdit anEdit) {
                return addEdit(anEdit);
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                if (editSaveId == getSaveId()) {
                    setSaved(true);
                }
            }

            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                setSaved(false);
            }
        };

        UndoableEditEvent ev = new UndoableEditEvent(m_document, edit);
        listener.undoableEditHappened(ev);

        edit.end();

        setSaved(true);
        updateRedoState();
        updateUndoState();
    }

    /**
     * Sets the value of PROPERTY_SAVED property showing whether the document
     * is saved or not. Listeners are notified on the same thread.
     * @param saved the property value.
     */
    void setSaved(boolean saved) {
        if (this.saved != saved) {
            this.saved = saved;
            if (propertyChangeSupport != null) {
                propertyChangeSupport.firePropertyChange(PROPERTY_SAVED,
                        !saved, saved);
            }
        }
    }

    /**
     * Returns true if the current state of the document has been saved.
     * @return true if the current state is saved, false otherwise
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Add a property change listener.
     * @param propListener listener
     */
    public void addPropertyChangeListener(PropertyChangeListener propListener) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propListener);
    }

    /**
     * Creates and returns the undo button instance.
     * @param hideText true to hide the action text; false to show it.
     * @return the undo button instance
     */
    private JButton getUndoButton(boolean hideText) {
        if (m_buttonUndo == null) {
            Action action = getUndoAction();
            if (action.getValue(Action.LARGE_ICON_KEY) == null) {
                action.putValue(Action.LARGE_ICON_KEY,
                        FileFuncs.getImageIcon("images/undo.png", false));
            }
            m_buttonUndo = new JButton(action);
            m_buttonUndo.setHideActionText(hideText);
        }
        return m_buttonUndo;
    }

    /**
     * Creates and returns the redo button instance.
     * @param hideText true to hide the action text; false to show it.
     * @return the redo button instance
     */
    private JButton getRedoButton(boolean hideText) {
        if (m_buttonRedo == null) {
            Action action = getRedoAction();
            if (action.getValue(Action.LARGE_ICON_KEY) == null) {
                action.putValue(Action.LARGE_ICON_KEY,
                        FileFuncs.getImageIcon("images/redo.png", false));
            }
            m_buttonRedo = new JButton(action);
            m_buttonRedo.setHideActionText(hideText);
        }
        return m_buttonRedo;
    }

    /**
     * Creates, if not created yet, and returns the undo manager instance.
     * @return the undo manager instance
     */
    UndoManager getUndoManager() {
        if (undoManager == null) {
            undoManager = new UndoManager();
        }
        return undoManager;
    }

    /**
     * Enables/disables the undo action if there are/are no undoable edits.
     */
    void updateUndoState() {
        if (undoAction != null) {
            if (undoManager != null && undoManager.canUndo()) {
                undoAction.setEnabled(true);
                undoAction.putValue(Action.NAME,
                        undoManager.getUndoPresentationName());
            } else {
                undoAction.setEnabled(false);
                undoAction.putValue(Action.NAME, Menu.UNDO);
            }
        }
    }

    /**
     * Enables/disables the redo action if there are/are no redoable edits.
     */
    void updateRedoState() {
        if (redoAction != null) {
            if (undoManager != null && undoManager.canRedo()) {
                redoAction.setEnabled(true);
                redoAction.putValue(Action.NAME,
                        undoManager.getRedoPresentationName());
            } else {
                redoAction.setEnabled(false);
                redoAction.putValue(Action.NAME, Menu.REDO);
            }
        }
    }

    /**
     * Returns the parent window where this panel is installed.
     * @return the parent window, or null.
     */
    Component getParentComponent() {
        return parentComponent;
    }

    /**
     * Undo action that is used for menu items and buttons.
     * The action defines an accelerator key Ctrl+z.
     */
    class UndoAction extends AbstractAction {

        public UndoAction() {
            putValue(Action.NAME, Menu.UNDO);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                            InputEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager um = getUndoManager();
            if (um.canUndo()) {
                try {
                    um.undo();
                } catch (CannotUndoException ex) {
                    JOptionPane.showMessageDialog(getParentComponent(),
                            "Undo failed unexpectedly.\n" +
                            "If you can reproduce it, please report a bug.",
                            "Undo Failed", JOptionPane.ERROR_MESSAGE);
                }
                updateUndoState();
                updateRedoState();
            }
        }
    }

    /**
     * Redo action that is used for menu items and buttons.
     * The action defines an accelerator key Ctrl+y.
     */
    class RedoAction extends AbstractAction {

        public RedoAction() {
            putValue(Action.NAME, Menu.REDO);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                            InputEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager um = getUndoManager();
            if (um.canRedo()) {
                try {
                    um.redo();
                } catch (CannotRedoException ex) {
                    JOptionPane.showMessageDialog(getParentComponent(),
                            "Redo failed unexpectedly.\n" +
                            "If you can reproduce it, please report a bug.",
                            "Redo Failed", JOptionPane.ERROR_MESSAGE);
                }
                updateRedoState();
                updateUndoState();
            }
        }
    }
}

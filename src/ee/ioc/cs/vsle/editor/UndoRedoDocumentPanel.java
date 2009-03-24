package ee.ioc.cs.vsle.editor;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.*;

import ee.ioc.cs.vsle.util.*;

import java.awt.event.*;

/**
 * Helper class for undo-redo support of text components.
 * This class defines corresponding Action classes and provides methods
 * for creating menu items and a toolbar button panel.
 */
public class UndoRedoDocumentPanel {

    private Document m_document;
    private UndoManager undoManager;

    private JButton m_buttonUndo;
    private JButton m_buttonRedo;

    private UndoAction undoAction;
    private RedoAction redoAction;

    private JPanel undoRedoPanel;
    private UndoableEditListener listener;

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
                getUndoManager().addEdit(e.getEdit());
                updateUndoState();
                updateRedoState();
            }
        };
        m_document.addUndoableEditListener(listener);
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
                undoAction.putValue(Action.NAME, undoManager.getUndoPresentationName());
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
                redoAction.putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                redoAction.setEnabled(false);
                redoAction.putValue(Action.NAME, Menu.REDO);
            }
        }
    }

    /**
     * Undo action that is used for menu items and buttons.
     * The action defines an accelerator key Ctrl+z.
     */
    class UndoAction extends AbstractAction {

        public UndoAction() {
            putValue(Action.NAME, Menu.UNDO);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager um = getUndoManager();
            if (um.canUndo()) {
                um.undo();
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
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UndoManager um = getUndoManager();
            if (um.canRedo()) {
                um.redo();
                updateRedoState();
                updateUndoState();
            }
        }
    }
}

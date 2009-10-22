package ee.ioc.cs.vsle.editor;

import java.awt.Window;

import javax.swing.text.JTextComponent;

/**
 * Interface for text editor windows.
 * Using this interface a client can query the current active text area
 * and other properties of the bound text editor window.
 */
public interface TextEditView {

    /**
     * Returns the current active text editor component.  This method may
     * be constant when there is only one text editor area in the window
     * and it is always active.  In case of tabbed windows the method can
     * return a different value on each call.
     * @return the active editor component or null
     */
    public JTextComponent getTextComponent();

    /**
     * Returns the editor window.  This method should be constant and not null.
     * @return the editor window
     */
    public Window getWindow();

}

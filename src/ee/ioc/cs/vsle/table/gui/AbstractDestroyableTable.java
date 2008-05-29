/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;

import javax.swing.*;

/**
 * If a table has been destroyed, all further events will be ignored
 * 
 * @author pavelg
 */
public abstract class AbstractDestroyableTable extends JTable {

    private boolean destroyed;
    
    @Override
    protected void processEvent(AWTEvent e) {
        if( !destroyed ) {
            super.processEvent( e );
        }
    }
    
    /**
     * Clears references
     */
    public abstract void destroy();
    
    /**
     * Sets destroyed flag to true
     */
    protected void setDestroyed() {
        destroyed = true;
    }
}

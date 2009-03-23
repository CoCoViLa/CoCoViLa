/**
 * 
 */
package ee.ioc.cs.vsle.util;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * @author pavelg
 *
 */
public final class GuiUtil {

    /**
     * GridBagConstraints helper
     * 
     * @param gbc
     * @param x
     * @param y
     * @param w
     * @param h
     * @param wx
     * @param wy
     * @param fill
     * @param anchor
     */
    public static GridBagConstraints buildGridBagConstraints( GridBagConstraints gbc, int x, int y, int w, int h, int wx, int wy, int fill, int anchor ) {

        gbc.gridx = x; // start cell in a row
        gbc.gridy = y; // start cell in a column
        gbc.gridwidth = w; // how many column does the control occupy in the row
        gbc.gridheight = h; // how many column does the control occupy in the column
        gbc.weightx = wx; // relative horizontal size
        gbc.weighty = wy; // relative vertical size
        gbc.fill = fill; // the way how the control fills cells
        gbc.anchor = anchor; // alignment

        return gbc;
    }

    /**
     * Resets all parameters except insets to default values.
     * @param gbc the constraints object to reset
     */
    public static final void resetGridBagConstraints(GridBagConstraints gbc) {
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;

        gbc.ipadx = 0;
        gbc.ipady = 0;
    }

    /**
     * Takes a tooltip from the renderer and returns it 
     * only if the string does NOT fit the bounds of the renderer
     * 
     * @param event
     * @param table
     * @return
     */
    public static String getTableToolTipTextByWidth( MouseEvent event, JTable table ) {
        
        Point p = event.getPoint();

        // Locate the renderer under the event location
        int hitColumnIndex = table.columnAtPoint(p);
        int hitRowIndex = table.rowAtPoint(p);
        int colWidth = table.getColumnModel().getTotalColumnWidth() / table.getColumnCount() - table.getColumnModel().getColumnMargin();
        
        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
            
            Object value = table.getValueAt( hitRowIndex, hitColumnIndex );
            
            if( value == null ) {
                return null;
            }
            
            String tip = value.toString();
            Component component = table.prepareRenderer(table.getCellRenderer(hitRowIndex, hitColumnIndex), hitRowIndex, hitColumnIndex);

            if( colWidth <= SwingUtilities.computeStringWidth( component.getFontMetrics( component.getFont() ), tip ) + 1 ) {
                return tip;
            }
        }
        
        return null;
    }
    
    public static Component addComponentAsFlow( Component comp, int align ) {
        JPanel panel = new JPanel( new FlowLayout( align ) );
        panel.add( comp );
        return panel;
    }
}

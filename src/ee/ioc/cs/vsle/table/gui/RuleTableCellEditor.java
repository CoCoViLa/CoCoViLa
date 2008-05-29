/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import ee.ioc.cs.vsle.table.*;

/**
 * @author pavelg
 *
 * Class for rendering rule cells and editing existing rules
 */
public class RuleTableCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private DefaultTableCellRenderer renderer;
    
    {
        renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment( SwingConstants.CENTER );
    }
    
    @Override
    public Component getTableCellRendererComponent( JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int column ) {
        
        return renderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    }

    @Override
    public Component getTableCellEditorComponent( final JTable table, final Object value,
            boolean isSelected, int row, int column ) {
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                RuleInputDialog dialog = new RuleInputDialog( table, 
                        ((RuleTableHeader)table).getRuleTable().getStorage().getInputFields(), 
                        (Rule)value );
                dialog.setVisible( true );
                
                if( dialog.isDataValid() ) {
                    fireEditingStopped();
                } else {
                    fireEditingCanceled();
                }
            }
        } );
        
        return getTableCellRendererComponent( table, value, isSelected, false, row, column );
    }
    
    @Override
    public Object getCellEditorValue() {
        //existing rule is being edited, no need to replace it with itself
        return null;
    }

    @Override
    public boolean isCellEditable( EventObject anEvent ) {
        if (anEvent instanceof MouseEvent) { 
            return ((MouseEvent)anEvent).getClickCount() >= 2;
        }
        return false;
    }

}

/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import ee.ioc.cs.vsle.table.*;

/**
 * @author pavelg
 *
 * Class for rendering rule cells and editing existing rules
 */
public class RuleTableCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private TableCellRenderer renderer;
    
    RuleTableCellEditor( boolean isOneLineRend ) {
        renderer = isOneLineRend ? createSingleLineRenderer() : new RenderingPanel();
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

    /**
     * @return
     */
    private static TableCellRenderer createSingleLineRenderer() {
        
      DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setHorizontalAlignment( SwingConstants.CENTER );
      
      return renderer;
    }
    
    /**
     * 3-line Rule renderer
     */
    private static class RenderingPanel extends JPanel implements TableCellRenderer {

        private static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); 
        
        private JLabel varRend = new JLabel( "", SwingConstants.CENTER );
        private JLabel condRend = new JLabel( "", SwingConstants.CENTER );
        private JLabel valRend = new JLabel( "", SwingConstants.CENTER );
        private boolean init = true;

        RenderingPanel() {
            setLayout( new GridLayout( 3, 1 ) );
            add( varRend );
            add( condRend );
            add( valRend );
        }
        
        @Override
        public Component getTableCellRendererComponent( JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column ) {
            
            setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
            setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
            
            setFont(table.getFont());

            if (hasFocus) {
                Border border = null;
                if (isSelected) {
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
                }
                
                setBorder(border);

                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                setBorder(noFocusBorder);
            }

            setValue((Rule)value); 
            
            return this;
        }
        
        private void setValue( Rule rule ) {
            
            if( !init ) return;
            
            varRend.setText( rule.getField().getId() );
            condRend.setText( rule.toStringCond() );
            valRend.setText( rule.toStringValue() );
        }

        @Override
        public void setBackground( Color bg ) {
            
            super.setBackground( bg );
            
            if( !init ) return;
            
            varRend.setBackground( bg );
            condRend.setBackground( bg );
            valRend.setBackground( bg );
        }

        @Override
        public void setForeground( Color fg ) {
            
            super.setForeground( fg );
            
            if( !init ) return;
            
            varRend.setForeground( fg );
            condRend.setForeground( fg );
            valRend.setForeground( fg ); 
        }
        
        @Override
        public void setFont( Font font ) {
            
            super.setFont( font );
            
            if( !init ) return;
            
            varRend.setFont( font );
            condRend.setFont( font );
            valRend.setFont( font ); 
        }
    }
}

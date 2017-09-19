/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
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
            @Override
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
//      renderer.setUI( new VerticalLabelUI(false) );TODO
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
    
    private static class VerticalLabelUI extends BasicLabelUI
    {
        static {
            labelUI = new VerticalLabelUI(false);
        }
        
        protected boolean clockwise;
        VerticalLabelUI( boolean clockwise )
        {
            super();
            this.clockwise = clockwise;
        }
        

        @Override
        public Dimension getPreferredSize(JComponent c) 
        {
            Dimension dim = super.getPreferredSize(c);
            return new Dimension( dim.height, dim.width );
        }   

        private static Rectangle paintIconR = new Rectangle();
        private static Rectangle paintTextR = new Rectangle();
        private static Rectangle paintViewR = new Rectangle();
        private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

        @Override
        public void paint(Graphics g, JComponent c) 
        {

            
            JLabel label = (JLabel)c;
            String text = label.getText();
            Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

            if ((icon == null) && (text == null)) {
                return;
            }

            FontMetrics fm = g.getFontMetrics();
            paintViewInsets = c.getInsets(paintViewInsets);

            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;
            
            // Use inverted height & width
            paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

            paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
            paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

            String clippedText = 
                layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

            Graphics2D g2 = (Graphics2D) g;
            AffineTransform tr = g2.getTransform();
            if( clockwise )
            {
                g2.rotate( Math.PI / 2 ); 
                g2.translate( 0, - c.getWidth() );
            }
            else
            {
                g2.rotate( - Math.PI / 2 ); 
                g2.translate( - c.getHeight(), 0 );
            }

            if (icon != null) {
                icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fm.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                }
                else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }
            
            
            g2.setTransform( tr );
        }
    }
}

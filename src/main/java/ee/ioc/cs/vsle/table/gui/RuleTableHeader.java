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

import static ee.ioc.cs.vsle.table.gui.TableConstants.*;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Class representing rule table's header use instead of JTableHeader
 */
public class RuleTableHeader extends JTable {

    private RuleTable parentTable;
    private TableModelListener lst;
    
    /**
     * Constructor
     * 
     * @param parentTable
     */
    RuleTableHeader( final RuleTable parentTable ) {
        
        this.parentTable = parentTable;
        
        final RuleTableModel delegateModel = parentTable.getModel();
        final int orientation = parentTable.getOrientation();
        
        final TableModel dataModelF = new AbstractTableModel() {

            @Override
            public int getColumnCount() {
                return ( orientation == HORIZONTAL ) ? delegateModel.getColumnCount() : 1;
            }

            @Override
            public int getRowCount() {
                return ( orientation == HORIZONTAL ) ? 1 : delegateModel.getRowCount();
            }

            @Override
            public Object getValueAt( int row, int col ) {
                return delegateModel.getRule( orientation == HORIZONTAL ? col : row );
            }
            
            @Override
            public void setValueAt( Object value, int rowIndex, int columnIndex ) {
                //do nothing here because a rule is edited directly
            }
            
            @Override
            public Class<?> getColumnClass( int columnIndex ) {
                return Rule.class;
            }

            @Override
            public boolean isCellEditable( int rowIndex, int columnIndex ) {
                return true;
            }
        };
        
        setRowSelectionAllowed( orientation == VERTICAL );
        setColumnSelectionAllowed( orientation == HORIZONTAL );

        setModel( dataModelF );
        
        initRenderer();
        
        lst = new TableModelListener() {

            @Override
            public void tableChanged( final TableModelEvent e ) {
                //always fully rebuild the table header
                RuleTableHeader.this.tableChanged( new TableModelEvent(
                        dataModelF, TableModelEvent.HEADER_ROW ) );
            }
        };
        
        delegateModel.addTableModelListener( lst );
    }

    private void initRenderer() {
        RuleTableCellEditor ed = new RuleTableCellEditor( getRowHeight() == ROW_HEIGHT_NORM );
        setDefaultEditor( Rule.class, ed );
        setDefaultRenderer( Rule.class, ed );
    }
    
    @Override
    public void setRowHeight( int rowHeight ) {
        super.setRowHeight( rowHeight );
        
        initRenderer();
    }

    @Override
    public String getToolTipText( MouseEvent event ) {
        
        return GuiUtil.getTableToolTipTextByWidth( event, this );
    }
    
    /**
     * @return
     */
    public RuleTable getRuleTable() {
        return parentTable;
    }
    
    /**
     * 
     */
    public void destroy() {
        parentTable.getModel().removeTableModelListener( lst );
        lst = null;
        parentTable = null;
    }
}

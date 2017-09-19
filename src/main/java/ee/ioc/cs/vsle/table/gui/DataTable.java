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

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.event.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Data table
 */
public class DataTable extends JTable {

    private Table expTable;
    private RuleTable hct, vct;
    
    private TableModelListener modelLst;
    private TableEvent.Listener tableListener;
    
    /**
     * Constructor
     * 
     * @param expTable
     * @param hct
     * @param vct
     */
    public DataTable( Table expTable, RuleTable hct, RuleTable vct ) {
        
        this.expTable = expTable;
        this.hct = hct;
        this.vct = vct;
        
        final DataTableModel dm = new DataTableModel();
        setModel( dm );
        setRowSelectionAllowed( true );
        setColumnSelectionAllowed( true );
        setCellSelectionEnabled( true );
        
        modelLst = new TableModelListener() {

            @Override
            public void tableChanged( final TableModelEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        DataTable.this.tableChanged( new TableModelEvent(dataModel, TableModelEvent.HEADER_ROW) );
                    }
                } );
            }
        };
        
        hct.getModel().addTableModelListener( modelLst );
        vct.getModel().addTableModelListener( modelLst );
        
        tableListener = new TableEvent.Listener() {

            @Override
            public void tableChanged( TableEvent e ) {
                
                if( ( e.getType() & TableEvent.DATA ) > 0 ) {
                    dm.updateColumnType();
                    dm.fireTableDataChanged();
                }
            }
        };
        
        TableEvent.addTableListener( tableListener );
    }
    
    @Override
    public String getToolTipText( MouseEvent event ) {
        
        return GuiUtil.getTableToolTipTextByWidth( event, this );
    }

    /**
     * Clears references
     */
    public void destroy() {
        
        TableEvent.removeTableListener( tableListener );
        tableListener = null;
        expTable = null;
        hct.getModel().removeTableModelListener( modelLst );
        vct.getModel().removeTableModelListener( modelLst );
        hct = null;
        vct = null;
        modelLst = null;
    }
    
    /**
     * @author pavelg
     *
     */
    class DataTableModel extends AbstractTableModel {
        
        Class<?> columnClass = Object.class;
        
        DataTableModel() {
            updateColumnType();
        }
        
        private void updateColumnType() {
            TableField field =  expTable.getOutputField();

            if( field != null ) {
                TypeToken token = TypeToken.getTypeToken( field.getType() );

                if( token != TypeToken.TOKEN_OBJECT ) {
                    columnClass = token.getWrapperClass();
                } else {
                    columnClass = Object.class;
                }
            }
        }
        
        @Override
        public int getColumnCount() { 
            return vct.getColumnCount(); 
        }
        @Override
        public int getRowCount() { 
            return hct.getRowCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) { 
            return expTable.getCellValueAt( rowIndex, columnIndex );
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            expTable.setCellValueAt( value, rowIndex, columnIndex );
        }
        
        @Override
        public Class<?> getColumnClass( int columnIndex ) {
            return columnClass;
        }

        @Override
        public boolean isCellEditable( int rowIndex, int columnIndex ) {
            return true;
        }
    }
    
}

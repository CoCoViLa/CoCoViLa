/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Class representing the rule table, both with horizontal and vertical orientation.
 * It has two addition GUI components to be layouted separately - 
 * header and pane with control buttons.
 */
public class RuleTable extends JTable {

    private RuleTableHeader fixedTableHeader;
    private ControlPanel controlPanel;
    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;
    private RuleTableModel model;
    private int orientation = HORIZONTAL;
    private Table table;
    
    /**
     * @param expTable
     * @param orientation
     */
    public RuleTable( Table expTable, int orientation ) {

        table = expTable;
        
        this.orientation = orientation;
        
        setModel( model = new RuleTableModel( orientation, expTable ) );
        
        setRowSelectionAllowed( orientation == HORIZONTAL );
        setColumnSelectionAllowed( orientation == VERTICAL );
        
        fixedTableHeader = new RuleTableHeader( this );
    }
 
    @Override
    public RuleTableModel getModel() {
        return model;
    }
    
    /**
     * @return panel with buttons for managing table's rows and columns
     */
    public JPanel getControlPanel() {
        
        if( controlPanel == null ) {
            controlPanel = new ControlPanel();
        }
        
        return controlPanel;
    }
    
    /**
     * @return table header as the separate component
     */
    public JTable getFixedTableHeader() {
        return fixedTableHeader;
    }

    /**
     * @return orientation
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * @return underlying expert table
     */
    public Table getStorage() {
        return table;
    }
    
    /**
     * Clears references
     */
    public void destroy() {
        
        fixedTableHeader.destroy();
        fixedTableHeader = null;
        model.destroy();
        model = null;
        table = null;
        controlPanel.destroy();
        controlPanel = null;
    }
    
    private class ControlPanel extends JPanel {
        
        private ActionListener actionLst;
        private JButton addRule;
        private JButton delRule;
        private JButton addData;
        private JButton delData;
        private JButton dataUp;
        private JButton dataDown;
        private JButton ruleLeft;
        private JButton ruleRight;
        
        ControlPanel() {
            initLayout();
            initActionListener();
        }
        
        private void initLayout() {
            
            setLayout( new BorderLayout() );
            
            JPanel panel = new JPanel( new GridBagLayout() );
            
            addRule = new JButton( "+Rule");
            delRule = new JButton( "-Rule");
            String entry = orientation == HORIZONTAL ? "Row" : "Column";
            addData = new JButton( "+" + entry );
            delData = new JButton( "-" + entry );
            dataUp = new JButton( orientation == HORIZONTAL ? "^" : "<" );
            dataDown = new JButton( orientation == HORIZONTAL ? "v" : ">" );
            ruleLeft = new JButton( orientation == HORIZONTAL ? "<" : "^" );
            ruleRight = new JButton( orientation == HORIZONTAL ? ">" : "v" );
            
            Font font = UIManager.getFont("Button.font").deriveFont( Font.PLAIN );
            dataUp.setFont( font );
            dataDown.setFont( font );
            ruleLeft.setFont( font );
            ruleRight.setFont( font );

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets( 2, 2, 2, 2 );
            
            //rules
            panel.add( addRule, GuiUtil.buildGridBagConstraints( gbc, 0, 0, 2, 1, 5, 5, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( delRule, GuiUtil.buildGridBagConstraints( gbc, 0, 1, 2, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( ruleLeft, GuiUtil.buildGridBagConstraints( gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( ruleRight, GuiUtil.buildGridBagConstraints( gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            
            //entries
            panel.add( addData, GuiUtil.buildGridBagConstraints( gbc, 2, 0, 2, 1, 0, 5, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( delData, GuiUtil.buildGridBagConstraints( gbc, 2, 1, 2, 1, 0, 5, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( dataUp, GuiUtil.buildGridBagConstraints( gbc, 2, 2, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            panel.add( dataDown, GuiUtil.buildGridBagConstraints( gbc, 3, 2, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );
            
            panel.setBorder( BorderFactory.createTitledBorder( orientation == HORIZONTAL ? "Horizontal" : "Vertical" ) );
            
            add( panel, BorderLayout.NORTH );
            
        }
        
        /**
         * Init action listener
         */
        private void initActionListener() {
            
            actionLst = new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    
                    Object source = e.getSource();
                    
                    if( source == addRule ) {

                        final int col = orientation == HORIZONTAL ? fixedTableHeader.getSelectedColumn() : fixedTableHeader.getSelectedRow();

                        RuleInputDialog dialog = new RuleInputDialog( RuleTable.this, table.getInputFields() );
                        dialog.setVisible( true );

                        if( dialog.isDataValid() ) {
                            int newPosition = col > -1 ? col + 1 : 0;

                            model.addRule( newPosition, dialog.getRule() );

                            restoreRuleSelection( newPosition );   
                        }
                    } else if( source == delRule ) {
                        
                        final int col = orientation == HORIZONTAL ? fixedTableHeader.getSelectedColumn() : fixedTableHeader.getSelectedRow();
                        
                        if( col > -1 ) {
                            int res = JOptionPane.showConfirmDialog( RuleTable.this, 
                                    "Are you sure to delete this rule?", "Delete rule", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE );
                            
                            if( res == JOptionPane.OK_OPTION ) {

                                model.deleteRule( col );

                                int lastCol = orientation == HORIZONTAL ? fixedTableHeader.getColumnCount() - 1 : fixedTableHeader.getRowCount() - 1;
                                System.err.println( col + " " + lastCol );
                                int position = col < lastCol ? col : lastCol;

                                if( position > -1 )
                                    restoreRuleSelection( position );  
                            }
                        }
                    } else if( source == ruleLeft || source == ruleRight ) {
                        
                        final int col = orientation == HORIZONTAL ? fixedTableHeader.getSelectedColumn() : fixedTableHeader.getSelectedRow();
                        
                        if( col == -1 )
                            return;
                        
                        int colCount = orientation == HORIZONTAL ? fixedTableHeader.getColumnCount() : fixedTableHeader.getRowCount();
                        
                        final int newPosition = source == ruleLeft ? col -1 : col + 1;

                        if( newPosition < 0 || newPosition >= colCount )
                            return;
                        
                        model.moveRule( col, newPosition );
                        
                        restoreRuleSelection( newPosition );
                    } 
                    ////////////////////////////////////////---ENTRIES
                    else if( source == addData ) {
                        
                        int row = orientation == HORIZONTAL ? RuleTable.this.getSelectedRow() : RuleTable.this.getSelectedColumn();
                        
                        int position = row + 1;
                        
                        model.addData( position );
                        
                        restoreEntrySelection( position );
                        
                    } else if( source == delData ) {
                        
                        final int row = orientation == HORIZONTAL ? RuleTable.this.getSelectedRow() : RuleTable.this.getSelectedColumn();
                        
                        if( row > -1 ) {
                            model.deleteData( row );
                            
                            int lastRow = orientation == HORIZONTAL ? RuleTable.this.getRowCount() - 1 : RuleTable.this.getColumnCount() - 1;
                            
                            int position = row < lastRow ? row : lastRow;
                            
                            if( position > -1 )
                                restoreEntrySelection( position );
                        }
                    } else if( source == dataUp || source == dataDown ) {
                        
                        int row = orientation == HORIZONTAL ? RuleTable.this.getSelectedRow() : RuleTable.this.getSelectedColumn();
                        
                        if( row == -1 )
                            return;
                        
                        int rowCount = orientation == HORIZONTAL ? RuleTable.this.getRowCount() : RuleTable.this.getColumnCount();
                        
                        final int newPosition = source == dataUp ? row -1 : row + 1;

                        if( newPosition < 0 || newPosition >= rowCount )
                            return;
                        
                        model.moveData( row, newPosition );
                        
                        //restore the selection
                        restoreEntrySelection( newPosition );
                    }
                }
            };
            
            addData.addActionListener( actionLst );
            addRule.addActionListener( actionLst );
            delRule.addActionListener( actionLst );
            delData.addActionListener( actionLst );
            dataUp.addActionListener( actionLst );
            dataDown.addActionListener( actionLst );
            ruleLeft.addActionListener( actionLst );
            ruleRight.addActionListener( actionLst );
        }
        
        /**
         * Restore entry selection
         * 
         * @param position
         */
        private void restoreEntrySelection( final int position ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    if( orientation == HORIZONTAL ) {
                        setRowSelectionInterval( position, position );
                        int count = getColumnCount() - 1;
                        if( count > 0 )
                            setColumnSelectionInterval( 0, count );
                    } else {
                        setColumnSelectionInterval( position, position );
                        int count = getRowCount() - 1;
                        if( count > 0 )
                            setRowSelectionInterval( 0, count );
                        
                    }
                }
            } );
        }
        
        /**
         * restore the selection of header
         * 
         * @param position
         */
        private void restoreRuleSelection( final int position ) {

            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    
                    if( orientation == HORIZONTAL ) {
                        fixedTableHeader.setRowSelectionInterval( 0, 0 );
                        fixedTableHeader.setColumnSelectionInterval( position, position );
                    } else {
                        fixedTableHeader.setRowSelectionInterval( position, position );
                        fixedTableHeader.setColumnSelectionInterval( 0, 0 );
                    }
                }
            } );    
        }
        
        public void destroy() {
            
            addData.removeActionListener( actionLst );
            addRule.removeActionListener( actionLst );
            delRule.removeActionListener( actionLst );
            delData.removeActionListener( actionLst );
            dataUp.removeActionListener( actionLst );
            dataDown.removeActionListener( actionLst );
            ruleLeft.removeActionListener( actionLst );
            ruleRight.removeActionListener( actionLst );
            
            actionLst = null;
            
            removeAll();
            
            addData = null;
            addRule = null;
            delRule = null;
            delData = null;
            dataUp = null;
            dataDown = null;
            ruleLeft = null;
            ruleRight = null;
        }
    }
}

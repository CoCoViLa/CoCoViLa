/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;

import static ee.ioc.cs.vsle.table.gui.TableConstants.*;

/**
 * @author pavelg
 *
 * Panel with all tables and controls
 */
public class TableMainPanel extends JPanel {

    private RuleTable hct;
    private RuleTable vct;
    private DataTable dt;
    private ListSelectionListener listSelectionLst;
    private List<ListSelectionModel> selectionModels;
    private Table table;
    private TestQueryPanel queryPane;
    private JComboBox dc;
    private ActionListener dtActionLst;
    
    /**
     * Constructor
     * 
     * @param expTable
     */
    public TableMainPanel( Table expTable ) {
        
        table = expTable;
        
        initLayout();
        initListeners();
    }
    
    /**
     * Initialize the layout
     * 
     * @param expTable
     */
    private void initLayout() {
        
        setLayout( new BorderLayout() );
        
        JPanel aggregateTablePanel = new JPanel();
        add( aggregateTablePanel, BorderLayout.NORTH );
        //TODO add Query panel to layout
        //add( getTestQueryPanel(), BorderLayout.SOUTH );
        
        hct = new RuleTable( table, HORIZONTAL );
        vct = new RuleTable( table, VERTICAL );
        aggregateTablePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        //--------------------------controls
        GuiUtil.buildGridBagConstraints( c, 0, 0, 1, 1, 45, 25, GridBagConstraints.BOTH, GridBagConstraints.CENTER );
        JPanel controls = new JPanel( new BorderLayout() );
        controls.add( hct.getControlPanel(), BorderLayout.WEST );
        controls.add( Box.createGlue(), BorderLayout.CENTER );
        controls.add( vct.getControlPanel(), BorderLayout.EAST );
        aggregateTablePanel.add( controls, c );
        
        //--------------------------horizontal table
        JPanel hp = new JPanel(new BorderLayout() );
        
        hct.setBorder( BorderFactory.createLineBorder( Color.black ) );
        hp.add( hct, BorderLayout.NORTH );

        JPanel hlp = new JPanel( new BorderLayout() );

        final JTable headerH = hct.getFixedTableHeader();
        
        headerH.setBorder( BorderFactory.createLineBorder( Color.black ) );
        hlp.add( headerH, BorderLayout.SOUTH );
        
        //labels for horizontal conditions
        GuiUtil.buildGridBagConstraints( c, 0, 1, 1, 1, 0, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER );        
        aggregateTablePanel.add( hlp, c );
        
        //table for horizontal conditions
        GuiUtil.buildGridBagConstraints( c, 0, 2, 1, 1, 0, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER );        
        aggregateTablePanel.add( hp, c );
        
        //--------------------------vertical table
        JPanel vp = new JPanel(new BorderLayout() );
        
        vct.setBorder( BorderFactory.createLineBorder( Color.black ) );
        vp.add( vct, BorderLayout.SOUTH );
        
        JPanel vlp = new JPanel( new BorderLayout() );
      
        final JTable headerV = vct.getFixedTableHeader();
        headerV.setBorder( BorderFactory.createLineBorder( Color.black ) );
        vlp.add( headerV, BorderLayout.SOUTH );
        
        //labels for vertical conditions
        GuiUtil.buildGridBagConstraints( c, 3, 0, 1, 2, 70, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER );        
        aggregateTablePanel.add( vlp, c );
        
        //table for vertical conditions
        GuiUtil.buildGridBagConstraints( c, 1, 0, 1, 2, 200, 0, GridBagConstraints.BOTH, GridBagConstraints.CENTER );  
        aggregateTablePanel.add( vp, c );
        
        //-------------------------------data table
        GuiUtil.buildGridBagConstraints( c, 1, 2, 1, 1, 0, 50, GridBagConstraints.BOTH, GridBagConstraints.PAGE_END ); 
        JPanel dp = new JPanel(new BorderLayout() );
        dt = new DataTable( table, hct, vct );
        dt.setBorder( BorderFactory.createLineBorder( Color.black ) );
        dp.add( dt, BorderLayout.NORTH );
        aggregateTablePanel.add( dp, c );
        
        //data table chooser
        if( table.isAliasOutput() ) {
            GuiUtil.buildGridBagConstraints( c, 1, 3, 1, 1, 0, 50, GridBagConstraints.BOTH, GridBagConstraints.PAGE_END ); 
            JPanel chooserPane = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            chooserPane.add( new JLabel( "Output: " ) );
            dc = new JComboBox( table.getOutputFields().toArray() );
            dc.setRenderer( new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent( JList list,
                        Object value, int index, boolean isSelected,
                        boolean cellHasFocus ) {
                    
                    JLabel lbl = (JLabel)super.getListCellRendererComponent( list, value, index, isSelected,
                            cellHasFocus );
                    
                    TableField tf = (TableField)value;
                    lbl.setText( tf.getType() + " " + tf.getId() );
                    return lbl;
                }
                
            });
            chooserPane.add( dc );
            aggregateTablePanel.add( chooserPane, c );
        }
    }
    
    /**
     * Initialize selection listener
     */
    private void initListeners() {
        
        final ListSelectionModel hctRowModel = hct.getSelectionModel();
        final ListSelectionModel vctColumnModel = vct.getColumnModel().getSelectionModel();
        final ListSelectionModel dtColumnModel = dt.getColumnModel().getSelectionModel();
        final ListSelectionModel dtRowModel = dt.getSelectionModel();
        
        listSelectionLst = new ListSelectionListener() {

            boolean isWorking = false;
            
            @Override
            public void valueChanged( ListSelectionEvent e ) {
                
                if( isWorking )
                    return;

                Object source = e.getSource();

                int row;
                int col;
                
                if( !isWorking 
                        && ( source == hctRowModel 
                                || source == vctColumnModel ) ) {
                    
                    if( ( row = hct.getSelectedRow() ) > -1 )
                        dt.setRowSelectionInterval( row, row );
                    
                    if( ( col = vct.getSelectedColumn() ) > -1 )
                        dt.setColumnSelectionInterval( col, col );
                    
                } else if( source == dtRowModel || source == dtColumnModel ) {
                    
                    row = dt.getSelectedRow();
                    col = dt.getSelectedColumn();
                    
                    if( row == -1 || col == -1 ) {
                        return;
                    }
                    
                    isWorking = true;
                    
                    hct.setRowSelectionInterval( row, row );
                    vct.setColumnSelectionInterval( col, col );
                    
                    if( ( col = hct.getColumnCount() - 1 ) > -1 )
                        hct.setColumnSelectionInterval( 0, col );
                    
                    if( ( row = vct.getRowCount() - 1 ) > -1 )
                        vct.setRowSelectionInterval( 0, row );
                    
                    isWorking = false;
                }
                
            }
        };
        
        selectionModels = new ArrayList<ListSelectionModel>( Arrays.asList( hctRowModel, vctColumnModel, dtColumnModel, dtRowModel ) );
        
        for ( ListSelectionModel model : selectionModels ) {
            model.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            model.addListSelectionListener( listSelectionLst );
        }
        
        hct.getFixedTableHeader().getColumnModel().getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        vct.getFixedTableHeader().getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        
        dtActionLst = new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent arg0 ) {
                TableField tf = (TableField) dc.getSelectedItem();
                table.setOutputField( tf, true );
            }
        };
        dc.addActionListener( dtActionLst);
    }
    
    /**
     * @return
     */
    public Table getTable() {
        return table;
    }
    
    /**
     * @return
     */
    private JPanel getTestQueryPanel() {
        
        if( queryPane == null ) {
            queryPane = new TestQueryPanel();
        }
        
        return queryPane;
    }
    
    /**
     * 
     */
    public void destroy() {
        
        for ( ListSelectionModel model : selectionModels ) {
            model.removeListSelectionListener( listSelectionLst );
        }
        selectionModels.clear();
        listSelectionLst = null;
        
        dt.destroy();
        dt = null;
        hct.destroy();
        hct = null;
        vct.destroy();
        vct = null;
        table = null;
        if( queryPane != null ) {
            queryPane.destroy();
            queryPane = null;
        }
        if( dc != null ) {
            dc.removeActionListener( dtActionLst );
            dc = null;
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension( 740, 480 );
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension( 400, 300 );
    }

    /**
     * @author pavelg
     * 
     * TODO implement
     */
    private class TestQueryPanel extends JPanel {
        
        TestQueryPanel() {
//          new FlowLayout( FlowLayout.LEFT ) 
            JButton queryTable = new JButton( "Query Table" );
            
            queryTable.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    
                }
            } );
            queryPane.add( queryTable );
        }
        
        String createTestString() {
            
            return "";
        }
        
        void destroy() {
            
        }
    }
}

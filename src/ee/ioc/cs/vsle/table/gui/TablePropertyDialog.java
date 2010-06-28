/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ee.ioc.cs.vsle.table.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 * @author pavelg
 *
 */
public class TablePropertyDialog extends JDialog {

    private boolean isOk = false;
    
    private static final Object[] allowedTypes = new Object[] { 
        TYPE_STRING, TYPE_INT, TYPE_DOUBLE, TYPE_LONG, TYPE_BOOLEAN, TYPE_FLOAT, TYPE_SHORT, TYPE_BYTE };
    
    private List<FieldPane> inputFields = new ArrayList<FieldPane>();
    private FieldPane outputField;
    private JPanel inputFieldsPane;
    private JButton jbtAddInput;
    private JButton jbtOk;
    private JButton jbtCancel;
    private ActionListener actionListener;
    private JTextField jtfTableId;
    private JTextField jtfDefault;
    private Table table;
    private boolean editMode = false;
    
    /**
     * @param frame
     */
    TablePropertyDialog( JFrame frame ) {
        
        super( frame, "New expert table", Dialog.ModalityType.APPLICATION_MODAL );
        
        init();
        initActionListener();
        
        setLocationRelativeTo( frame );
    }
    
    TablePropertyDialog( JFrame frame, Table tab ) {
        super(frame, (tab.getTableId() != null 
                ? "Table properties: " + tab.getTableId() : "New expert table"),
                Dialog.ModalityType.APPLICATION_MODAL);

        init();
        initActionListener();
        
        setLocationRelativeTo( frame );
        
        editMode = true;
        table = tab;
        
        initFromTable();
        
    }

    /**
     * 
     */
    private void init() {
        
        JPanel root = new JPanel();
        root.setLayout( new BoxLayout( root, BoxLayout.Y_AXIS ) );
        setContentPane( root );
        
        jtfTableId = new JTextField( 10 );
        addFlowToPanel( root, jtfTableId, FlowLayout.LEFT ).setBorder( BorderFactory.createTitledBorder( "Table ID:" ) );
        
        inputFieldsPane = new JPanel();
        inputFieldsPane.setLayout( new BoxLayout( inputFieldsPane, BoxLayout.Y_AXIS ) );
        inputFieldsPane.setBorder( BorderFactory.createTitledBorder( "Input fields:" ) );
        addFlowToPanel( root, inputFieldsPane, FlowLayout.LEFT );
        
        addInputFieldPane( false );
        
        //output field
        JPanel outputPane = new JPanel();
        outputPane.setLayout( new BoxLayout( outputPane, BoxLayout.Y_AXIS ) );
        outputPane.setBorder( BorderFactory.createTitledBorder( "Output field:" ) );
        addFlowToPanel( root, outputPane, FlowLayout.LEFT );
        
        addFlowToPanel( outputPane, outputField = new FieldPane(), FlowLayout.LEFT );

        // default output value
        JPanel defaultOutput = new JPanel(new GridLayout(1, 2, 5, 5));
        JLabel lblDefault = new JLabel("Default value:");
        jtfDefault = new JTextField(10);
        defaultOutput.add(lblDefault);
        defaultOutput.add(jtfDefault);
        addFlowToPanel(outputPane, defaultOutput, FlowLayout.LEFT);

        jbtAddInput = new JButton( "Add Input" );
        addFlowToPanel( root, jbtAddInput, FlowLayout.RIGHT );
        
        jbtOk = new JButton( "OK" );
        jbtCancel = new JButton( "Cancel" );
        JPanel buttonFlow = addFlowToPanel( root, jbtOk, FlowLayout.RIGHT );
        buttonFlow.add( jbtCancel );
        buttonFlow.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        
        //other
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setLocationByPlatform( true );
        setResizable( false );
        pack();
    }

    /**
     * 
     */
    private void initActionListener() {
        
        actionListener = new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                
                Object source = e.getSource();
                
                if( source == jbtAddInput ) {
                    
                    addInputFieldPane( true );
                    
                } else if( source == jbtOk ) {
                    
                    try {
                        table = createOrEditTable();
                    } catch( TableException ex ) {
                        JOptionPane.showMessageDialog( TablePropertyDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                    
                    isOk = true;
                    dispose();
                    
                } else if( source == jbtCancel ) {
                    
                    dispose();
                }
            }
            
        };
        
        jbtAddInput.addActionListener( actionListener );
        jbtOk.addActionListener( actionListener );
        jbtCancel.addActionListener( actionListener );
    }
    
    @Override
    public void dispose() {
        super.dispose();
        jbtAddInput.removeActionListener( actionListener );
        jbtOk.removeActionListener( actionListener );
        jbtCancel.removeActionListener( actionListener );
        actionListener = null;
        inputFields.clear();
    }
    
    /**
     * @return
     * @throws TableException
     */
    private Table createOrEditTable() throws TableException {
    
        String tableId = jtfTableId.getText();
        
        if( tableId == null || tableId.trim().length() == 0 ) {
            throw new TableException( "Table ID cannot be empty" );
        }
        
        TableFieldList inputs = new TableFieldList();
        
        for ( FieldPane input : inputFields ) {
            String name = input.getFieldName();
            
            if( name == null || name.trim().length() == 0 ) {
                continue;
            }
            
            inputs.add( new TableField( name, input.getType() ) );
        }
        
        if( inputs.isEmpty() ) {
            throw new TableException( "At least one input field has to be provided" );
        }
        
        String outName = outputField.getFieldName();
        
        if( outName == null || outName.trim().length() == 0 ) {
            throw new TableException( "Ouput field's name cannot be empty" );
        }
        
        TableField outField = new TableField( outName, outputField.getType() );
        
        if( inputs.containsByID( outField ) ) {
            throw new TableException( "Ouput field cannot be in the input list\n" + outField );
        }

        String dv = jtfDefault.getText();
        if (dv != null && dv.length() < 1) {
            // Should the empty string be allowed as a default value?
            // Currently the empty string is treated as missing value.
            dv = null;
        }

        if( editMode ) {
            table.changePropertiesAndVerify( tableId, inputs, outField );
            table.setDefaultValue(dv);
            return null;
        }

        Table tab = Table.createEmptyTable( tableId );

        tab.addInputFields( inputs );
        tab.setOutputField( outField );
        tab.setDefaultValue(dv);

        return tab;
    }
    
    private void initFromTable() {
        
        jtfTableId.setText( table.getTableId() );
        if (table.hasDefaultValue()) {
            jtfDefault.setText(table.getDefaultValue().toString());
        }

        outputField.jcboxType.setSelectedItem( table.getOutputField().getType() );
        outputField.jtfName.setText( table.getOutputField().getId() );
        
        inputFields.clear();
        inputFieldsPane.removeAll();
        
        for ( TableField input : table.getInputFields() ) {
            FieldPane fp = addInputFieldPane( false );
            fp.jcboxType.setSelectedItem( input.getType() );
            fp.jtfName.setText( input.getId() );
        }
        
        pack();
    }
    
    /**
     * 
     */
    private FieldPane addInputFieldPane( boolean doPack ) {
        FieldPane fp = new FieldPane();
        inputFields.add( fp );
        addFlowToPanel( inputFieldsPane, fp, FlowLayout.LEFT );
        
        if( doPack )
            pack();
        
        return fp;
    }
    
    /**
     * @param parent
     * @param comp
     * @param align
     * @return
     */
    private JPanel addFlowToPanel( JPanel parent, JComponent comp, int align ) {
        JPanel flow = new JPanel( new FlowLayout( align ) );
        flow.add( comp );
        parent.add( flow );
        
        return flow;
    }
    
    /**
     * @return the isOk
     */
    public boolean isOk() {
        return isOk;
    }
    
    /**
     * @return
     */
    public Table getTable() {
        return table;
    }
    
    /**
     * @author pavelg
     *
     */
    private class FieldPane extends JPanel {
        
        private JComboBox jcboxType;
        private JTextField jtfName;
        
        /**
         * 
         */
        FieldPane() {
            
            setLayout( new GridLayout( 1, 2, 5, 5 ) );
            
            jcboxType = new JComboBox( allowedTypes );
            jtfName = new JTextField( 10 );
            
            add( jcboxType );
            add( jtfName );
        }
        
        /**
         * @param type
         * @param name
         */
        FieldPane( String type, String name ) {
            jcboxType.setSelectedItem( type );
            jtfName.setText( name );
        }

        /**
         * @return the type
         */
        public String getType() {
            return jcboxType.getSelectedItem().toString();
        }

        /**
         * @return the name
         */
        public String getFieldName() {
            return jtfName.getText();
        }
    }
}

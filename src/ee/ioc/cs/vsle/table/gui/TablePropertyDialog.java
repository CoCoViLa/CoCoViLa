/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import static ee.ioc.cs.vsle.util.TypeUtil.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class TablePropertyDialog extends JDialog {

    private boolean isOk = false;
    
    private static final Object[] allowedTypes = new Object[] { 
        TYPE_STRING, TYPE_INT, TYPE_DOUBLE, TYPE_LONG, TYPE_BOOLEAN, TYPE_FLOAT, TYPE_SHORT, TYPE_BYTE };
    
    private static final Object[] allowedAliasTypes;
    
    static {
        List<Object> types = new ArrayList<Object>(Arrays.asList( allowedTypes ));
        types.add( TYPE_OBJECT );
        allowedAliasTypes = types.toArray();
    }
    
    private List<FieldPane> inputFields = new ArrayList<FieldPane>();
    private List<FieldPane> outputFields = new ArrayList<FieldPane>();
    private JPanel inputFieldsPane;
    private JPanel outputFieldsPane;
    private JButton jbtAddInput;
    private JButton jbtAddOutput;
    private JButton jbtOk;
    private JButton jbtCancel;
    private ActionListener actionListener;
    private JTextField jtfTableId;
    private Table table;
    private boolean editMode = false;
    private boolean isAliasOutput = false;

    private JRadioButton jrbSingleOutput;

    private JRadioButton jrbAliasOutput;

    private JTextField jtfAliasName;

    private JComboBox jcbAliasType;

    private JPanel aliasPane;
    
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
        
        //input fields
        inputFieldsPane = new JPanel();
        inputFieldsPane.setLayout( new BoxLayout( inputFieldsPane, BoxLayout.Y_AXIS ) );
        inputFieldsPane.setBorder( BorderFactory.createTitledBorder( "Input fields:" ) );
        addFlowToPanel( root, inputFieldsPane, FlowLayout.LEFT );
        
        addInputFieldPane( false );
        
        jbtAddInput = new JButton( "Add Input" );
        addFlowToPanel( root, jbtAddInput, FlowLayout.RIGHT );
        
        //output fields
        JPanel outputPane = new JPanel();
        outputPane.setLayout( new BoxLayout( outputPane, BoxLayout.Y_AXIS ) );
        outputPane.setBorder( BorderFactory.createTitledBorder( "Output field" ) );
        addFlowToPanel( root, outputPane, FlowLayout.LEFT );
        
        //--radiobuttons for choosing output type
        JPanel outputTypePane = new JPanel();
        outputTypePane.setLayout( new BoxLayout( outputTypePane, BoxLayout.X_AXIS ) );
        jrbSingleOutput = new JRadioButton( "Single", !isAliasOutput );
        jrbAliasOutput = new JRadioButton( "Alias", isAliasOutput );
        ButtonGroup bg = new ButtonGroup();
        bg.add( jrbSingleOutput );
        bg.add( jrbAliasOutput );
        outputTypePane.add( jrbSingleOutput );
        outputTypePane.add( jrbAliasOutput );
        addFlowToPanel( outputPane, outputTypePane, FlowLayout.LEFT );
        
        aliasPane = new JPanel( new GridLayout( 1, 3, 5, 5 ));
        aliasPane.setBorder( BorderFactory.createTitledBorder( "Alias" ) );
        jcbAliasType = new JComboBox( allowedAliasTypes );
        jtfAliasName = new JTextField( 5 );
        jbtAddOutput = new JButton( "Add Element" );
        aliasPane.add( jcbAliasType );
        aliasPane.add( jtfAliasName );
        aliasPane.add( jbtAddOutput );
        addFlowToPanel( outputPane, aliasPane, FlowLayout.LEFT );
        aliasPane.setVisible( isAliasOutput );
        
        //--fields
        outputFieldsPane = new JPanel();
        outputFieldsPane.setLayout( new BoxLayout( outputFieldsPane, BoxLayout.Y_AXIS ) );
        addFlowToPanel( outputPane, outputFieldsPane, FlowLayout.LEFT );
        addOutputFieldPane( false );
        final TitledBorder border = BorderFactory.createTitledBorder( "Elements" );
        
        ActionListener outputTypeChooserLst = new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                if( e.getSource() == jrbSingleOutput ) {
                    if( isAliasOutput ) {
                        isAliasOutput = false;
                        aliasPane.setVisible( false );
                        outputFieldsPane.setBorder( null );
                        //remove all elements except the first one
                        if( outputFields.size() > 1 ) {
                            for( int i = outputFields.size() - 1; i > 0; i-- ) {
                                outputFieldsPane.remove( i );
                                outputFields.remove( i );
                            }
                        }
                        outputFields.get( 0 ).jcboxType.setEnabled( true );
                        pack();
                    }
                } else if( e.getSource() == jrbAliasOutput ) {
                    if( !isAliasOutput ) {
                        isAliasOutput = true;
                        aliasPane.setVisible( true );
                        outputFieldsPane.setBorder( border );
                        //choose default alias type as its first element's one
                        jcbAliasType.setSelectedItem( outputFields.get( 0 ).jcboxType.getSelectedItem() );
                        pack();
                    }
                } else if( e.getSource() == jcbAliasType ) {
                    checkAliasType();
                }
            }
        };
        jrbSingleOutput.addActionListener( outputTypeChooserLst );
        jrbAliasOutput.addActionListener( outputTypeChooserLst );
        jcbAliasType.addActionListener( outputTypeChooserLst );
        //--rb

        //buttons
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

    protected void checkAliasType() {
        
        String type = jcbAliasType.getSelectedItem().toString();
        
        if( isAliasOutput && !TYPE_OBJECT.equals( type ) ) {
            for ( FieldPane fp : outputFields ) {
                fp.jcboxType.setSelectedItem( type );
                fp.jcboxType.setEnabled( false );
            }
        } else {
            for ( FieldPane fp : outputFields ) {
                fp.jcboxType.setEnabled( true );
            }
        }
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
                    
                } if( source == jbtAddOutput ) {
                    
                    if( isAliasOutput ) {
                        addOutputFieldPane( true );
                        checkAliasType();
                    }
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
        jbtAddOutput.addActionListener( actionListener );
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
            
            if( !StringUtil.isJavaIdentifier( name ) ) {
                throw new TableException( "Input's identifier " + name + " is invalid" );
            }
            
            inputs.add( new TableField( name, input.getType() ) );
        }
        
        if( inputs.isEmpty() ) {
            throw new TableException( "At least one input field has to be provided" );
        }
        
        TableField alias = null;
        if( isAliasOutput ) {
            String outName = jtfAliasName.getText();
            
            if( outName == null || outName.trim().length() == 0 
                    || !StringUtil.isJavaIdentifier( outName ) ) {
                throw new TableException( "Alias name is empty or invalid" );
            }
            alias = new TableField( outName, jcbAliasType.getSelectedItem().toString() );
        }
        
        TableFieldList outputs = new TableFieldList();
        
        for ( FieldPane output : outputFields ) {
            String name = output.getFieldName();
            if( name == null || name.trim().length() == 0 ) {
                continue;
            }
            if( !StringUtil.isJavaIdentifier( name ) ) {
                throw new TableException( "Output's identifier " + name + " is invalid" );
            }
            
            TableField tb = new TableField( name, output.getType() );
            
            if( inputs.containsByID( tb ) ) {
                throw new TableException( "Ouput field cannot be in the input list\n" + tb.getId() );
            }
            
            outputs.add( tb );
            
            String dv = output.getFieldDefaultValue();
            if (dv != null && dv.trim().length() < 1) {
                // Should the empty string be allowed as a default value?
                // Currently the empty string is treated as missing value.
                dv = null;
            }
            tb.setDefaultValueFromString( dv );
        }
        
        if( outputs.isEmpty() ) {
            throw new TableException( "At least one output field has to be provided" );
        }

        if( editMode ) {
            table.changePropertiesAndVerify( tableId, inputs, outputs, alias );
            return null;
        }

        Table tab = Table.createEmptyTable( tableId );
        tab.addInputFields( inputs );
        tab.setAliasOutput( alias );
        tab.addOutputFields( outputs );

        return tab;
    }
    
    private void initFromTable() {
        
        jtfTableId.setText( table.getTableId() );

        outputFields.clear();
        outputFieldsPane.removeAll();
        
        for ( TableField output : table.getOutputFields() ) {
            FieldPane fp = addOutputFieldPane( false );
            fp.jtfName.setText( output.getId() );
            fp.jcboxType.setSelectedItem( output.getType() );
            Object value = output.getDefaultValue();
            if( value != null )
                fp.jtfValue.setText( value.toString() );
        }
        
        jrbAliasOutput.setSelected( isAliasOutput = table.isAliasOutput() );
        aliasPane.setVisible( isAliasOutput );
        if( isAliasOutput ) {
            jtfAliasName.setText( table.getAliasOutput().getId() );
            jcbAliasType.setSelectedItem( table.getAliasOutput().getType() );
        }
        jrbSingleOutput.setSelected( !isAliasOutput );
        
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
     * 
     */
    private FieldPane addOutputFieldPane( boolean doPack ) {
        FieldPane fp = new FieldPane( isAliasOutput ? allowedAliasTypes : allowedTypes, true );
        outputFields.add( fp );
        addFlowToPanel( outputFieldsPane, fp, FlowLayout.LEFT );
        
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
        private JTextField jtfValue;
        
        /**
         * 
         */
        FieldPane() {
            
            this( allowedTypes, false );
        }
        
        FieldPane( Object[] types, boolean defaultValue ) {
            
            setLayout( new GridLayout( 
                            1, 
                            defaultValue ? 3 : 2, 
                            5, 5 ) );
            
            int colWidth = defaultValue ? 5 : 10;
            jcboxType = new JComboBox( types );
            jtfName = new JTextField( colWidth );
            
            add( jcboxType );
            add( jtfName );
            
            if( defaultValue ) {
                jtfValue = new JTextField( colWidth );
                add( jtfValue );
            }
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
        
        public String getFieldDefaultValue() {
            if( jtfValue != null ) {
                return jtfValue.getText();
            }
            return null;
        }
    }
}

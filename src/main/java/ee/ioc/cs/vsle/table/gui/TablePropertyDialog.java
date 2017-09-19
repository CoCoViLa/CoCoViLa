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

import static ee.ioc.cs.vsle.util.TypeUtil.*;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class TablePropertyDialog extends JDialog {

    private boolean isOk = false;
    
    private static final Object[] singleTypes = new Object[] { 
        TYPE_STRING, TYPE_INT, TYPE_DOUBLE, TYPE_LONG, TYPE_BOOLEAN, TYPE_FLOAT, TYPE_SHORT, TYPE_BYTE };
    
    private static final Object[] allowedInputTypes;
    private static final Object[] allowedOuputTypes;
    private static final Object[] allowedAliasTypes;
    
    static {
        List<Object> inputTypes = new ArrayList<Object>(Arrays.asList( singleTypes ));
        for ( Object object : singleTypes ) {
            inputTypes.add( object.toString() + "[]" );
        }
        allowedInputTypes = inputTypes.toArray();
        
        allowedOuputTypes = singleTypes;
        
        List<Object> aliasTypes = new ArrayList<Object>(Arrays.asList( singleTypes ));
        aliasTypes.add( TYPE_OBJECT );
        allowedAliasTypes = aliasTypes.toArray();
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
        
        this( frame, null );
    }
    
    TablePropertyDialog( JFrame frame, Table tab ) {
        super(frame, (tab != null && tab.getTableId() != null 
                ? "Table properties: " + tab.getTableId() : "New expert table"),
                Dialog.ModalityType.APPLICATION_MODAL);

        init();
        initActionListener();
        
        setLocationRelativeTo( frame );
        
        if( tab != null ) {
            editMode = true;
            table = tab;

            initFromTable();
        }
        
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
        
        JPanel ip = new JPanel(new BorderLayout());
        ip.setBorder( BorderFactory.createTitledBorder( "Input fields:" ) );
        JScrollPane sp = new JScrollPane( inputFieldsPane, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        ip.add( sp, BorderLayout.CENTER );
        ip.setPreferredSize( new Dimension( 350, 250 ) );
        root.add( ip );
        
        addInputFieldPane( false );
        
        jbtAddInput = new JButton( "Add Input" );
        addFlowToPanel( root, jbtAddInput, FlowLayout.RIGHT );
        
        //output fields
        JPanel outputPane = new JPanel();
        outputPane.setLayout( new BoxLayout( outputPane, BoxLayout.Y_AXIS ) );
        outputPane.setBorder( BorderFactory.createTitledBorder( "Output field" ) );
        
        JPanel op = new JPanel(new BorderLayout());
        op.add( outputPane, BorderLayout.CENTER );
        root.add( op );
        
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
        setSize( getPreferredSize() );
        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
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
        
        TableFieldList<InputTableField> inputs = new TableFieldList<InputTableField>();
        
        for ( FieldPane input : inputFields ) {
            String name = input.getFieldName();
            
            if( name == null || name.trim().length() == 0 ) {
                continue;
            }
            
            if( !StringUtil.isJavaIdentifier( name ) ) {
                throw new TableException( "Input's identifier " + name + " is invalid" );
            }
            
            InputTableField inputField;
            inputs.add( inputField = new InputTableField( name, input.getType() ) );
            inputField.setQuestion( input.question );
            inputField.setConstraints( input.constraints );
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
        
        TableFieldList<TableField> outputs = new TableFieldList<TableField>();
        
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
        
        for ( InputTableField input : table.getInputFields() ) {
            FieldPane fp = addInputFieldPane( false );
            fp.jcboxType.setSelectedItem( input.getType() );
            fp.jtfName.setText( input.getId() );
            fp.question = input.getQuestion();
            fp.constraints = input.getConstraints();
            fp.update();
        }
        
    }
    
    /**
     * 
     */
    private FieldPane addInputFieldPane( boolean doPack ) {
        FieldPane fp = new FieldPane();
        inputFields.add( fp );
        addFlowToPanel( inputFieldsPane, fp, FlowLayout.LEFT );
        inputFieldsPane.revalidate();
        return fp;
    }
    
    /**
     * 
     */
    private FieldPane addOutputFieldPane( boolean doPack ) {
        FieldPane fp = new FieldPane( isAliasOutput ? allowedAliasTypes : allowedOuputTypes, true );
        outputFields.add( fp );
        addFlowToPanel( outputFieldsPane, fp, FlowLayout.LEFT );
        outputFieldsPane.revalidate();
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
        private JButton jbDetails;
        private JTextField jtfName;
        private JTextField jtfValue;
        private JPanel jpComponents;
        private JTextArea jtaDetails;
        private String question;
        private List<TableFieldConstraint> constraints;
        private boolean isOutput;
        
        /**
         * 
         */
        FieldPane() {
            
            this( allowedInputTypes, false );
        }
        
        FieldPane( Object[] types, boolean isOutput ) {
            
            this.isOutput = isOutput;
            
            jpComponents = new JPanel();
            
            jpComponents.setLayout( new GridBagLayout() );
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets( 5, 5, 5, 5 );

            jcboxType = new JComboBox( types );
            
            jpComponents.add( jcboxType, GuiUtil.buildGridBagConstraints( gbc, 0,
                    0, 1, 1, 0, 0, GridBagConstraints.BOTH,
                    GridBagConstraints.WEST ) );
            
            int colWidth = isOutput ? 5 : 10;
            jtfName = new JTextField( colWidth );
            
            jtfName.getDocument().addDocumentListener( new DocumentListener() {
                
                @Override
                public void removeUpdate( DocumentEvent e ) {
                    update();
                }
                
                @Override
                public void insertUpdate( DocumentEvent e ) {
                    update();
                }
                
                @Override
                public void changedUpdate( DocumentEvent e ) {
                    update();
                }
                
            });
            
            jpComponents.add( jtfName, GuiUtil.buildGridBagConstraints( gbc, 1,
                    0, 1, 1, 0, 0, GridBagConstraints.BOTH,
                    GridBagConstraints.WEST ) );
            
            if( isOutput ) {
                jtfValue = new JTextField( colWidth );
                jpComponents.add( jtfValue, GuiUtil.buildGridBagConstraints( gbc, 2,
                        0, 1, 1, 0, 0, GridBagConstraints.BOTH,
                        GridBagConstraints.WEST ) );
            } else {
                jbDetails = new JButton( "(...)" );
                jpComponents.add( jbDetails, GuiUtil.buildGridBagConstraints( gbc, 2,
                        0, 1, 1, 0, 0, GridBagConstraints.BOTH,
                        GridBagConstraints.WEST ) );
                
                jbDetails.addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        final TableInputPropDialog inpDialog = 
                            new TableInputPropDialog( 
                                    TablePropertyDialog.this, 
                                    getType(), getFieldName(), 
                                    question, constraints );

                        inpDialog.setCallback( new Runnable() {
                            @Override
                            public void run() {
                                question = inpDialog.getQuestion();
                                constraints = inpDialog.getConstraints();
                                update();
                            }
                        } );
                        inpDialog.setVisible( true );
                    }
                });
            }
            
            setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
            add( GuiUtil.addComponentAsFlow( jpComponents, FlowLayout.LEFT ) );
            
            update();
        }

        private void update() {
            
            if( isOutput ) return;
            
            if( getFieldName().trim().length() > 0 ) {
                jbDetails.setEnabled( true );
            } else {
                jbDetails.setEnabled( false );
            }
            
            StringBuilder text = new StringBuilder();
            
            if( question != null && question.trim().length() > 0 ) {
                text.append( "Custom question: \"" )
                    .append( MessageFormat.format( question, getFieldName(), getType() ) )
                    .append( "\"" );
            }  
            
            if( constraints != null ) {
                for ( TableFieldConstraint constr : constraints ) {
                    if( text.length() > 0 )
                        text.append( "\n" );
                    text.append( MessageFormat.format( constr.printConstraint(), getFieldName() ) );
                }
            }
            
            if( text.length() > 0 ) {
                if( jtaDetails == null ) {
                    jtaDetails = new JTextArea(0, 20);
                    jtaDetails.setLineWrap( true );
                    jtaDetails.setWrapStyleWord( true );
                    jtaDetails.setEditable( false );
                    add( GuiUtil.addComponentAsFlow( jtaDetails, FlowLayout.LEFT ) );
                    jtaDetails.setBackground( jtaDetails.getParent().getBackground() );
                }
                jtaDetails.setText( text.toString() );
                jtaDetails.setVisible( true );
            } else {
                if( jtaDetails != null ) {
                    jtaDetails.setVisible( false );
                }
            }
            
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

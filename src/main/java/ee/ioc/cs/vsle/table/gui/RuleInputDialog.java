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
import java.util.*;
import java.util.List;

import javax.swing.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Dialog for creating and editing rules
 */
public class RuleInputDialog extends JDialog {
    
    private JComboBox cboxVar;
    private JComboBox cboxCond;
    private JTextField tfValue;
    private JButton btnSave;
    private JButton btnCancel;
    private boolean valid = false;
    private Rule rule;
    private List<InputTableField> fields;
    
    /**
     * Constructor for new rule
     * 
     * @param table
     * @param fields input fields
     */
    public RuleInputDialog( JTable table, List<InputTableField> fields ) {
        
        super( SwingUtilities.getWindowAncestor( table ), "New rule", Dialog.ModalityType.APPLICATION_MODAL );
        this.fields = fields;
        init();
    }
    
    /**
     * Constructor for existing rule
     * 
     * @param table
     * @param fields
     * @param rule
     */
    public RuleInputDialog( JTable table, List<InputTableField> fields, Rule rule ) {
        
        this( table, fields );
        
        setTitle( "Edit rule" );
        
        if( rule != null ) {
            initRule( rule );
        } 
    }
    
    /**
     * Init components and layout
     */
    private void init() {
        setLocationRelativeTo( null );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        
        JPanel root = new JPanel();
        setContentPane( root );
        root.setLayout( new BoxLayout( root, BoxLayout.Y_AXIS ) );
        root.setBorder( BorderFactory.createEmptyBorder( 15, 5, 5, 5 ) );
        
        JPanel main = new JPanel();
        main.setLayout( new BoxLayout( main, BoxLayout.X_AXIS ) );
        
        ListCellRenderer listRenderer = new ListCellRenderer() {
            
            private JLabel lbl = new JLabel( "", SwingConstants.CENTER );
            
            @Override
            public Component getListCellRendererComponent( JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus ) {
                
                if( value instanceof TableField ) {
                    TableField field = ((TableField)value);
                    lbl.setText( " " + field.getType() + " " + field.getId() );
                } else if( value != null ){
                    lbl.setText( value.toString() );
                }
                
                lbl.setOpaque( true );
                
                if (isSelected) {
                    lbl.setBackground(list.getSelectionBackground());
                    lbl.setForeground(list.getSelectionForeground());
                }
                else {
                    lbl.setBackground(list.getBackground());
                    lbl.setForeground(list.getForeground());
                }
                
                return lbl;
            }
        };
        
        cboxVar = new JComboBox( fields.toArray() );
        cboxVar.setRenderer( listRenderer );
        main.add( cboxVar );
        main.add( Box.createHorizontalStrut( 10 ) );
        cboxCond = new JComboBox();
        initConditions();
        cboxCond.setRenderer( listRenderer );
        main.add( cboxCond );
        main.add( Box.createHorizontalStrut( 10 ) );
        tfValue = new JTextField( 10 );
        main.add( tfValue );
        
        JPanel buttonPane = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
        btnSave = new JButton( "Save" );
        buttonPane.add( btnSave );
        btnCancel = new JButton( "Cancel" );
        buttonPane.add( btnCancel );
        
        ActionListener lst = new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                
                if( e.getSource() == btnSave ) {

                    try {
                        if ( rule != null ) {

                            //Test before changing the actual rule (yuck).
                            //This is done because if a new condition has been set 
                            //and setValueFromString() throws an exception,
                            //old condition will not be restored.
                            createRuleFromGUI();

                            ConditionItem item = (ConditionItem) cboxCond.getSelectedItem();
                            rule.setCondition( item.getCond() );
                            rule.setValueFromString( tfValue.getText() );
                            rule.setNegative( item.isNegative() );

                        } else {
                            rule = createRuleFromGUI();
                        }
                    } catch( TableException ex ) {
                        JOptionPane.showMessageDialog( RuleInputDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                        return;
                    }

                    valid = true;
                    dispose();
                    btnSave.removeActionListener( this );

                } else if( e.getSource() == btnCancel ) {
                    dispose();
                    btnCancel.removeActionListener( this );
                } else if( e.getSource() == cboxVar ) {
                    initConditions();
                }
            }
            
        };
        
        btnSave.addActionListener( lst );
        btnCancel.addActionListener( lst );
        cboxVar.addActionListener( lst );
        
        root.add( main );
        root.add( Box.createVerticalStrut( 10 ) );
        root.add( buttonPane );
        setResizable( false );
        pack();
        
    }
    
    private void initConditions() {
        TableField tf = (TableField) cboxVar.getSelectedItem();

        Object currentCond = cboxCond.getSelectedItem();
        
        List<ConditionItem> items = new ArrayList<RuleInputDialog.ConditionItem>();
        for ( ConditionItem item : ConditionItem.values() ) {
            System.out.println(item.toString() + " accepts " + tf.getType() + ": " + item.getCond().acceptsType( tf.getType() ) );
            if( item.getCond().acceptsType( tf.getType() ) ) {
                items.add( item );
            }
        }
        ComboBoxModel model = new DefaultComboBoxModel(items.toArray());
        
        cboxCond.setModel( model );
        cboxCond.setSelectedItem( currentCond );
    }
    
    /**
     * Set rule values to GUI components
     * 
     * @param rule
     */
    private void initRule( Rule r ) {
        this.rule = r;
        
        cboxVar.setSelectedItem( rule.getField() );
        cboxVar.setEnabled( false );
        cboxCond.setSelectedItem( ConditionItem.getItem( rule.getCondition(), rule.isNegative() ) );
        
        if( rule.getValue() != null ) {
            tfValue.setText( TypeUtil.toTokenString( rule.getValue() ) );
        }
    }
    
    /**
     * @return new rule
     */
    private Rule createRuleFromGUI() {
        ConditionItem item = (ConditionItem) cboxCond.getSelectedItem();
        return Rule.createRule( 
                (InputTableField) cboxVar.getSelectedItem(), 
                ( item.isNegative() ? "!" : "" ) + item.getCond().getKeyword(), 
                tfValue.getText() );
    }
    
    /**
     * @return rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * @return true if rule is OK, otherwise false
     */
    public boolean isDataValid() {
        return valid;
    }
    
    /**
     * @author pavelg
     *
     */
    private enum ConditionItem {
        
        EQ( Condition.COND_EQUALS, false ), 
        NEQ( Condition.COND_EQUALS, true ), 
        LESS( Condition.COND_LESS, false ), 
        GREQ( Condition.COND_LESS, true ), 
        LESSEQ( Condition.COND_LESS_OR_EQUAL, false ), 
        GREATER( Condition.COND_LESS_OR_EQUAL, true ), 
        IN( Condition.COND_IN_ARRAY, false ), 
        NOTIN( Condition.COND_IN_ARRAY, true ), 
        IN_INPUT_ARRAY( Condition.COND_IN_INPUT_ARRAY, true ), 
        NOT_IN_INPUT_ARRAY( Condition.COND_IN_INPUT_ARRAY, false ), 
        MATCH( Condition.REG_EXP_MATCH, false ), 
        NOTMATCH( Condition.REG_EXP_MATCH, true ), 
        SUBSTR( Condition.SUBSTRING, false ), 
        NOTSUBSTR( Condition.SUBSTRING, true ), 
        SUBSTR_OF_INPUT( Condition.SUBSTRING_Of_INPUT, true ), 
        NOT_SUBSTR_OF_INPUT( Condition.SUBSTRING_Of_INPUT, false ), 
        SUBSET( Condition.SUBSET, false ), 
        NOTSUBSET( Condition.SUBSET, true ), 
        STRICTSUBSET( Condition.STRICT_SUBSET, false ), 
        NOTSTRICTSUBSET( Condition.STRICT_SUBSET, true );
        
        private Condition cond;
        private boolean isNegative;
        
        /**
         * @param cond
         * @param isNegative
         */
        ConditionItem( Condition cond, boolean isNegative ) {
            this.cond = cond;
            this.isNegative = isNegative;
        }
        

        /**
         * @return the cond
         */
        public Condition getCond() {
            return cond;
        }

        /**
         * @return the isNegative
         */
        public boolean isNegative() {
            return isNegative;
        }
        
        @Override
        public String toString() {
            return isNegative ? cond.getOppositeSymbol() : cond.getSymbol();
        }
        
        /**
         * @param c
         * @param neg
         * @return
         */
        public static ConditionItem getItem( Condition c, boolean neg ) {
            
            for ( ConditionItem item : values() ) {
                if( item.getCond() == c && item.isNegative() == neg ) {
                    return item;
                }
            }
            
            throw new IllegalArgumentException( "No such item" );
        }
    }

}

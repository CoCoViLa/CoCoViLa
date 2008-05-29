/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Dialog for creating and editing rules
 */
public class RuleInputDialog extends JDialog {
    
    private JComboBox cboxVar;
    private JCheckBox chboxNot;
    private JComboBox cboxCond;
    private JTextField tfValue;
    private JButton btnSave;
    private JButton btnCancel;
    private boolean valid = false;
    private Rule rule;
    private List<TableField> fields;
    
    /**
     * Constructor for new rule
     * 
     * @param table
     * @param fields input fields
     */
    public RuleInputDialog( JTable table, List<TableField> fields ) {
        
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
    public RuleInputDialog( JTable table, List<TableField> fields, Rule rule ) {
        
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
        root.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        
        JPanel main = new JPanel();
        main.setLayout( new BoxLayout( main, BoxLayout.X_AXIS ) );
        
        ListCellRenderer listRenderer = new ListCellRenderer() {
            
            private JLabel lbl = new JLabel();
            
            @Override
            public Component getListCellRendererComponent( JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus ) {
                
                if( value instanceof TableField ) {
                    TableField field = ((TableField)value);
                    lbl.setText( " " + field.getType() + " " + field.getId() );
                } else if( value instanceof Condition ) {
                    Condition con = ((Condition)value);
                    lbl.setText( con.getKeyword() );
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
        main.add( buildGridPanel( "var:", cboxVar ) );
        main.add( Box.createHorizontalStrut( 5 ) );
        chboxNot = new JCheckBox();
        chboxNot.setHorizontalAlignment(JCheckBox.CENTER);
        main.add( buildGridPanel( "not:", chboxNot ) );
        main.add( Box.createHorizontalStrut( 5 ) );
        cboxCond = new JComboBox( new Object[]{ Condition.COND_EQUALS, Condition.COND_LESS, Condition.COND_LESS_OR_EQUAL, Condition.COND_IN_ARRAY } );
        cboxCond.setRenderer( listRenderer );
        main.add( buildGridPanel( "cond:", cboxCond ) );
        main.add( Box.createHorizontalStrut( 5 ) );
        tfValue = new JTextField( 10 );
        main.add( buildGridPanel( "value:", tfValue ) );
        
        JPanel buttonPane = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
        btnSave = new JButton( "Save" );
        buttonPane.add( btnSave );
        btnCancel = new JButton( "Cancel" );
        buttonPane.add( btnCancel );
        
        ActionListener lst = new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                
                if( e.getSource() == btnSave ) {

//                  SwingUtilities.invokeLater( new Runnable() {
//                  public void run() {
                    try {
                        if ( rule != null ) {

                            //Test before changing the actual rule (yuck).
                            //This is done because if a new condition has been set 
                            //and setValueFromString() throws an exception,
                            //old condition will not be restored.
                            createRuleFromGUI();

                            rule.setCondition( (Condition) cboxCond.getSelectedItem() );
                            rule.setValueFromString( tfValue.getText() );
                            rule.setNegative( chboxNot.isSelected() );

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
//                  }
//                  } );

                } else if( e.getSource() == btnCancel ) {
                    dispose();
                    btnCancel.removeActionListener( this );
                }
            }
            
        };
        
        btnSave.addActionListener( lst );
        btnCancel.addActionListener( lst );
        
        root.add( main );
        root.add( Box.createVerticalStrut( 10 ) );
        root.add( buttonPane );
        setResizable( false );
        pack();
        
    }
    
    /**
     * @param lbl
     * @param comp
     * @return
     */
    private JPanel buildGridPanel( String lbl, JComponent comp ) {
        JPanel panel = new JPanel( new GridLayout( 2, 1, 5, 5 ) );
        panel.add( new JLabel( lbl, SwingConstants.CENTER ) );
        panel.add( comp );
        
        return panel;
    }
    
    /**
     * Set rule values to GUI components
     * 
     * @param rule
     */
    private void initRule( Rule r ) {
        this.rule = r;
        
        cboxVar.setSelectedItem( rule.getField() );
        chboxNot.setSelected( rule.isNegative() );
        cboxCond.setSelectedItem( rule.getCondition() );
        if( rule.getValue() != null )
            tfValue.setText( TypeUtil.toTokenString( rule.getValue() ) );
    }
    
    /**
     * @return new rule
     */
    private Rule createRuleFromGUI() {
        return Rule.createRule( 
                (TableField) cboxVar.getSelectedItem(), 
                ( chboxNot.isSelected() ? "!" : "" ) + ( (Condition) cboxCond.getSelectedItem() ).getKeyword(), 
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
}

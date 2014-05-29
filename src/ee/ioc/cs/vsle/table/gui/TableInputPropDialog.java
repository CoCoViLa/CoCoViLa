/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
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
public class TableInputPropDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jpButtons = null;
    private JButton jbOK = null;
    private JButton jbCancel = null;
    private JPanel jpCenter = null;
    private JTextField jtfCustomQuestion = null;
    private JPanel jpCustomQuestion = null;
    private JPanel jpConstraints = null;
    private JButton jbReset = null;
    private JLabel jlGlue = null;
    private JLabel jlQuestionNote = null;
    private JButton jbAddConstraint = null;
    private JButton jbCheckConstraints = null;
    private JPanel jpAddConstraintFlow = null;
    private JPanel jpConstraintList = null;
    private JLabel jlConstraintsNote = null;
    private JPanel jpConstraintsBottom = null;
    private JPanel jpConstaintsNoteFlow = null;
    /**
     * This method initializes jpButtons	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpButtons() {
        if ( jpButtons == null ) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.weightx = 1.0D;
            gridBagConstraints5.gridy = 0;
            jlGlue = new JLabel();
            jlGlue.setText("");
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 3;
            gridBagConstraints3.gridy = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 0.0D;
            gridBagConstraints2.gridy = 0;
            jpButtons = new JPanel();
            jpButtons.setLayout(new GridBagLayout());
            jpButtons.add(getJbOK(), gridBagConstraints4);
            jpButtons.add(getJbCancel(), gridBagConstraints3);
            jpButtons.add(getJbReset(), gridBagConstraints2);
            jpButtons.add(jlGlue, gridBagConstraints5);
        }
        return jpButtons;
    }

    /**
     * This method initializes jbOK	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbOK() {
        if ( jbOK == null ) {
            jbOK = new JButton();
            jbOK.setText("OK");
            jbOK.addActionListener( new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    
                    if( !checkConstraints() ) 
                        return;
                    
                    setVisible( false );
                    if( okCallback != null )
                        okCallback.run();
                }
            } );
        }
        return jbOK;
    }

    /**
     * This method initializes jbCancel	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbCancel() {
        if ( jbCancel == null ) {
            jbCancel = new JButton();
            jbCancel.setText("Cancel");
            jbCancel.addActionListener( new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    dispose();
                }
            } );
        }
        return jbCancel;
    }

    /**
     * This method initializes jpCenter	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpCenter() {
        if ( jpCenter == null ) {
            jlConstraintsNote = new JLabel();
            jlConstraintsNote.setText("Note: only checked constraints will be saved");
            jlConstraintsNote.setBorder(BorderFactory.createLineBorder(Color.gray, 0));
            GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
            gridBagConstraints21.gridx = 0;
            gridBagConstraints21.fill = GridBagConstraints.BOTH;
            gridBagConstraints21.gridwidth = 2;
            gridBagConstraints21.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridwidth = 2;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.gridy = 0;
            jpCenter = new JPanel();
            jpCenter.setLayout(new GridBagLayout());
            jpCenter.add(getJpCustomQuestion(), gridBagConstraints1);
            if( availableConstraints.length > 0 )
                jpCenter.add(getJpConstraints(), gridBagConstraints21);
        }
        return jpCenter;
    }

    /**
     * This method initializes jtfCustomQuestion	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJtfCustomQuestion() {
        if ( jtfCustomQuestion == null ) {
            jtfCustomQuestion = new JTextField();
            jtfCustomQuestion.setColumns(0);
            jtfCustomQuestion.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            jtfCustomQuestion.setText("");
        }
        return jtfCustomQuestion;
    }

    /**
     * This method initializes jpCustomQuestion	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpCustomQuestion() {
        if ( jpCustomQuestion == null ) {
            jlQuestionNote = new JLabel();
            jlQuestionNote.setText("Note: include {0} for name and {1} for type");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridx = -1;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 0.0D;
            gridBagConstraints.gridwidth = 2;
            jpCustomQuestion = new JPanel();
            jpCustomQuestion.setLayout(new BorderLayout());
            jpCustomQuestion.setBorder(BorderFactory.createTitledBorder(null, "Custom Question", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            jpCustomQuestion.add(getJtfCustomQuestion(), BorderLayout.CENTER);
            jpCustomQuestion.add(jlQuestionNote, BorderLayout.SOUTH);
        }
        return jpCustomQuestion;
    }

    /**
     * This method initializes jpConstraints	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpConstraints() {
        if ( jpConstraints == null ) {
            jpConstraints = new JPanel();
            jpConstraints.setLayout(new BorderLayout());
            jpConstraints.setBorder(BorderFactory.createTitledBorder(null, "Constraints", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            jpConstraints.add(getJpConstraintList(), BorderLayout.CENTER);
            jpConstraints.add(getJpConstraintsBottom(), BorderLayout.SOUTH);
        }
        return jpConstraints;
    }

    /**
     * This method initializes jbReset	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbReset() {
        if ( jbReset == null ) {
            jbReset = new JButton();
            jbReset.setText("Reset");
            jbReset.addActionListener( new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    initFromOrig();
                }
            } );
        }
        return jbReset;
    }

    /**
     * This method initializes jbAddConstraint	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbAddConstraint() {
        if ( jbAddConstraint == null ) {
            jbAddConstraint = new JButton();
            jbAddConstraint.setHorizontalAlignment(SwingConstants.CENTER);
            jbAddConstraint.setText("Add");
            jbAddConstraint.addActionListener( new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    addNewConstraintItemPanel();
                }
            } );
        }
        return jbAddConstraint;
    }
    
    private JButton getJbCheckConstraints() {
        if ( jbCheckConstraints == null ) {
            jbCheckConstraints = new JButton();
            jbCheckConstraints.setHorizontalAlignment(SwingConstants.CENTER);
            jbCheckConstraints.setText("Check");
            jbCheckConstraints.addActionListener( new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    checkConstraints();
                }
            } );
        }
        return jbCheckConstraints;
    }

    /**
     * This method initializes jpAddConstraintFlow	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpAddConstraintFlow() {
        if ( jpAddConstraintFlow == null ) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.RIGHT);
            jpAddConstraintFlow = new JPanel();
            jpAddConstraintFlow.setLayout(flowLayout);
            jpAddConstraintFlow.add(getJbAddConstraint(), null);
            jpAddConstraintFlow.add(getJbCheckConstraints(), null);
        }
        return jpAddConstraintFlow;
    }

    /**
     * This method initializes jpConstraintList	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpConstraintList() {
        if ( jpConstraintList == null ) {
            jpConstraintList = new JPanel();
            jpConstraintList.setLayout(new BoxLayout(jpConstraintList, BoxLayout.Y_AXIS));
        }
        return jpConstraintList;
    }

    private List<ConstraintItemPanel> constraintPanels = new ArrayList<ConstraintItemPanel>();
    
    private void addNewConstraintItemPanel() {
        
        addConstraintItemPanel( new ConstraintItemPanel() );
    }
    
    private void addConstraintItemPanel( ConstraintItemPanel panel ) {
        
        constraintPanels.add( panel );
        getJpConstraintList().add( panel );
        TableInputPropDialog.this.pack();
    }

    /**
     * This method initializes jpConstraintsBottom	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpConstraintsBottom() {
        if ( jpConstraintsBottom == null ) {
            jpConstraintsBottom = new JPanel();
            jpConstraintsBottom.setLayout(new BoxLayout(getJpConstraintsBottom(), BoxLayout.Y_AXIS));
            jpConstraintsBottom.add(getJpAddConstraintFlow(), null);
            jpConstraintsBottom.add(getJpConstaintsNoteFlow(), null);
        }
        return jpConstraintsBottom;
    }

    /**
     * This method initializes jpConstaintsNoteFlow	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpConstaintsNoteFlow() {
        if ( jpConstaintsNoteFlow == null ) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setAlignment(FlowLayout.LEFT);
            jpConstaintsNoteFlow = new JPanel();
            jpConstaintsNoteFlow.setLayout(flowLayout1);
            jpConstaintsNoteFlow.add(jlConstraintsNote, null);
        }
        return jpConstaintsNoteFlow;
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
//        new TableInputPropDialog( null, "String" ).setVisible( true );
    }

    private Collection<TableFieldConstraint> origConstraints;
    private String origQuestion;
    private String type;
    
    public TableInputPropDialog( Window owner, String type, String name, String question, Collection<TableFieldConstraint> constraints ) {
        super( owner );
        this.type = type;
        availableConstraints = getAvailableConstraints();
        initialize();
        this.origQuestion = question;
        this.origConstraints = constraints;
        setTitle( "Input " + type + ":" + name + " properties");
        initFromOrig();
        pack();
    }
    
    private void initFromOrig() {
        boolean enableReset = false;
        
        if( origQuestion != null && origQuestion.length() > 0 ) {
            getJtfCustomQuestion().setText( origQuestion );
            enableReset = true;
        }
        if( origConstraints != null && origConstraints.size() > 0 ) {
            constraintPanels.clear();
            getJpConstraintList().removeAll();
            for ( TableFieldConstraint constr : origConstraints ) {
                addConstraintItemPanel( new ConstraintItemPanel( constr ) );
            } 
            enableReset = true;
        } else if( getJpConstraintList().getComponentCount() == 0 ) {
            addNewConstraintItemPanel();
        }
        
        getJbReset().setEnabled( enableReset );
    }
    
    private Runnable okCallback;
    
    public void setCallback( Runnable cb ) {
        okCallback = cb;
    }
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(311, 251);
        this.setModal(true);
        this.setContentPane( getJContentPane() );
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if ( jContentPane == null ) {
            jContentPane = new JPanel();
            jContentPane.setLayout( new BorderLayout() );
            jContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jContentPane.add(getJpButtons(), BorderLayout.SOUTH);
            jContentPane.add(getJpCenter(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    public String getQuestion() {
        return getJtfCustomQuestion().getText();
    }
    
    public List<TableFieldConstraint> getConstraints() {
        
        List<TableFieldConstraint> list = new ArrayList<TableFieldConstraint>();
        for ( ConstraintItemPanel pane : constraintPanels ) {
            if( pane.getJchbEnableConstraint().isSelected() 
                    && pane.constrValuePane != null ) {
                TableFieldConstraint c = pane.constrValuePane.getConstraint();
                if( c != null )
                    list.add( c );
            }
        }
        return list;
    }
    
    private boolean checkConstraints() {
        boolean isOk = true;
        for ( ConstraintItemPanel pane : constraintPanels ) {
            if( pane.getJchbEnableConstraint().isSelected() 
                    && pane.constrValuePane != null ) {
                isOk = pane.constrValuePane.checkConstraint();
            }
        }
        return isOk;
    }
    
    private class ConstraintItemPanel extends JPanel {
        
        private JCheckBox jchbEnableConstraint = null;
        private JPanel jpConstraint = null;
        private JComboBox jcbConstraint = null;
        private JPanel jpConstraintBody = null;
        private InputConstraintPanel constrValuePane;
        
        public ConstraintItemPanel() {
            setLayout(new BorderLayout());
            add(getJchbEnableConstraint(), BorderLayout.WEST);
            add(getJpConstraint(), BorderLayout.CENTER);
            updateFromCheckbox();
        }
        
        public ConstraintItemPanel( final TableFieldConstraint constr ) {
            this();

            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    getJchbEnableConstraint().setSelected( true );
                    updateFromCheckbox();
                    getJcbConstraint().setSelectedItem( Constraint.valueOf( constr.getName() ) );
                    
                    if( constrValuePane != null )
                        constrValuePane.setConstraint( constr );
                }
            } );
        }
        
        /**
         * This method initializes jchbEnableConstraint 
         *  
         * @return javax.swing.JCheckBox    
         */
        private JCheckBox getJchbEnableConstraint() {
            if ( jchbEnableConstraint == null ) {
                jchbEnableConstraint = new JCheckBox();
                jchbEnableConstraint.addActionListener( new ActionListener() {
                    
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        updateFromCheckbox();
                    }
                });
            }
            return jchbEnableConstraint;
        }

        /**
         * This method initializes jpConstraint 
         *  
         * @return javax.swing.JPanel   
         */
        private JPanel getJpConstraint() {
            if ( jpConstraint == null ) {
                jpConstraint = new JPanel();
                jpConstraint.setLayout(new BorderLayout());
                jpConstraint.add(getJcbConstraint(), BorderLayout.WEST);
                jpConstraint.add(getJpConstraintBody(), BorderLayout.CENTER);
            }
            return jpConstraint;
        }
        
        /**
         * This method initializes jcbConstraint    
         *  
         * @return javax.swing.JComboBox    
         */
        private JComboBox getJcbConstraint() {
            if ( jcbConstraint == null ) {
                jcbConstraint = new JComboBox();
                jcbConstraint.setPreferredSize(new Dimension(100, 27));
                jcbConstraint.addActionListener( new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed( java.awt.event.ActionEvent e ) {
                        getJpConstraintBody().removeAll();
                        constrValuePane = ((Constraint)jcbConstraint.getSelectedItem())
                                    .newPanelInstance(TableInputPropDialog.this);
                        getJpConstraintBody().add( 
                                constrValuePane,
                                BorderLayout.CENTER );
                        TableInputPropDialog.this.validate();
                    }
                } );
            }
            return jcbConstraint;
        }

        /**
         * This method initializes jpConstraintBody 
         *  
         * @return javax.swing.JPanel   
         */
        private JPanel getJpConstraintBody() {
            if ( jpConstraintBody == null ) {
                jpConstraintBody = new JPanel();
                jpConstraintBody.setLayout(new BorderLayout());
            }
            return jpConstraintBody;
        }
        
        private void updateFromCheckbox() {
            if( getJchbEnableConstraint().isSelected() ) {
                if( getJcbConstraint().getItemCount() == 0 ) {//enabled for the first time, need to init
                    List<Constraint> constraints = new ArrayList<Constraint>();
                    for ( Constraint constraint : availableConstraints ) {
                        
                        if( !constraint.constraintInstance.isCompatibleWith( getConstraints() ) )
                            continue;
                        constraints.add( constraint );
                    }
                    ComboBoxModel model = new DefaultComboBoxModel( constraints.toArray() );
                    getJcbConstraint().setModel( model );
                    if( model.getSize() > 0 )
                        getJcbConstraint().setSelectedIndex( 0 );
                } else {
                    if( constrValuePane != null 
                            && !constrValuePane.getConstraint().isCompatibleWith( getConstraints() ) ) {
                        
                        SwingUtilities.invokeLater( new Runnable() {
                            @Override
                            public void run() {
                                getJchbEnableConstraint().setSelected( false );
                            }
                        } );
                        return;
                    }
                }
                setConstraintComponentsEnabled( getJcbConstraint().getItemCount() > 0 );
            } else {
                setConstraintComponentsEnabled( false );
            }
        }
        
        private void setConstraintComponentsEnabled( boolean b ) {
            getJcbConstraint().setEnabled( b );
            for ( Component comp : getJpConstraintBody().getComponents() ) {
                comp.setEnabled( b );
            }
        }
    }
    
    final Constraint[] availableConstraints;
    
    private Constraint[] getAvailableConstraints() {
        List<Constraint> cs = new ArrayList<Constraint>();
        
        for ( Constraint constraint : Constraint.values() ) {
            if( constraint.acceptsType( type ) ) {
                cs.add( constraint );
            }
        }
        
        return cs.toArray(new Constraint[cs.size()]);
    }
    
    //constrait enum
    private enum Constraint {
        List(ListConstraintPanel.class, new TableFieldConstraint.List()), 
        Range(RangeConstraintPanel.class, new TableFieldConstraint.Range());
        
        private final Class<? extends InputConstraintPanel> panelClass;
        private final TableFieldConstraint constraintInstance;
        
        Constraint(Class<? extends InputConstraintPanel> panelClass, 
                TableFieldConstraint constraintInstance) {
            this.panelClass = panelClass;
            this.constraintInstance = constraintInstance;
        }
        
        private InputConstraintPanel newPanelInstance( TableInputPropDialog encInst ) {
                Constructor<? extends InputConstraintPanel> panelCon;
                try {
                    panelCon = panelClass.getConstructor(TableInputPropDialog.class);
                    InputConstraintPanel panel = panelCon.newInstance(encInst);
                    return panel;
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                return null;
        }
        
        private boolean acceptsType( String type ) {
            return constraintInstance.acceptType( type );
        }
    }
    
    //abstract input panel
    public abstract class InputConstraintPanel extends JPanel {
        
        public abstract TableFieldConstraint getConstraint();
        
        public abstract void setConstraint( TableFieldConstraint c );
        
        @Override
        public abstract void setEnabled( boolean b );
        
        private Border errorBorder;
        
        private Border getErrorBorder() {
            if( errorBorder == null ) {
                errorBorder = BorderFactory.createLineBorder( Color.red );
            }
            return errorBorder;
        }
        
        public boolean checkConstraint() {
            
            TableFieldConstraint c = null;
            try {
                c = getConstraint();
            } catch( Exception e ) {
                System.err.println( "Exception in InputConstraintPanel.checkConstraint()" );
            }
            
            if( c == null || !c.isCorrect() ) {
                setBorder( getErrorBorder() );
                return false;
            }

            setBorder( null );
            return true;
        }
    }
    
    //list panel
    public class ListConstraintPanel extends InputConstraintPanel {
        
        private JLabel jlValues;
        private JTextField jtfValues;
        private TableFieldConstraint.List lc;
        
        public ListConstraintPanel() {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
            jlValues = new JLabel( "Values:" );
            add( jlValues );
            jtfValues = new JTextField();
            add( jtfValues );
        }

        @Override
        public TableFieldConstraint getConstraint() {
            if( lc == null )
                lc = new TableFieldConstraint.List();
            String text = jtfValues.getText().trim();
            if( text.length() > 0 )
                lc.setValuesFromString( type, text );
            return lc;
        }
        
        @Override
        public void setEnabled( boolean b ) {
            jlValues.setEnabled( b );
            jtfValues.setEnabled( b );
        }

        @Override
        public void setConstraint( TableFieldConstraint c ) {
            StringBuilder sb = new StringBuilder();
            for( Object o : ((TableFieldConstraint.List)c).getValueList() ) {
                if( sb.length() > 0 )
                    sb.append( TypeUtil.ARRAY_TOKEN );
                sb.append( o );
            }
            jtfValues.setText( sb.toString() );
        }
    }
    
    public class RangeConstraintPanel extends InputConstraintPanel {
        
        private JLabel jlMin = new JLabel( "Min:" );
        private JLabel jlMax = new JLabel( "Max:" );
        private JTextField jtfMin = new JTextField( 5 );
        private JTextField jtfMax = new JTextField( 5 );
        private TableFieldConstraint.Range rc;
        
        public RangeConstraintPanel() {
            setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
            add( jlMin );
            add( jtfMin );
            add( jlMax );
            add( jtfMax );
        }

        @Override
        public TableFieldConstraint getConstraint() {

            if( rc == null )
                rc = new TableFieldConstraint.Range();
            rc.setValuesFromString( type, jtfMin.getText(), jtfMax.getText() );
            return rc;
        }
        
        @Override
        public void setEnabled( boolean b ) {
            jlMin.setEnabled( b );
            jlMax.setEnabled( b );
            jtfMin.setEnabled( b );
            jtfMax.setEnabled( b );
        }

        @Override
        public void setConstraint( TableFieldConstraint c ) {
            TableFieldConstraint.Range _rc = (TableFieldConstraint.Range)c;
            
            if( _rc.getMin() != null )
                jtfMin.setText( _rc.getMin().toString() );
            
            if( _rc.getMax() != null )
                jtfMax.setText( _rc.getMax().toString() );
        }
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"

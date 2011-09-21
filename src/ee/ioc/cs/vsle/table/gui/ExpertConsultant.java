/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.JSlider;

import com.sun.org.apache.bcel.internal.generic.*;

import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 *
 */
public class ExpertConsultant extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jpQuestionMain = null;
    private JPanel jpButtons = null;
    private JButton jbNext = null;
    private JButton jbFinish = null;
    private JButton jbBack = null;
    private JButton jbCancel = null;
    
    private Table table;
    
    /**
     * This method initializes jpButtons	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJpButtons() {
        if ( jpButtons == null ) {
            jpButtons = new JPanel();
            jpButtons.setLayout(new FlowLayout());
            jpButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jpButtons.add(getJbCancel(), null);
            jpButtons.add(getJbBack(), null);
            jpButtons.add(getJbNext(), null);
            jpButtons.add(getJbFinish(), null);
        }
        return jpButtons;
    }

    /**
     * This method initializes jbNext	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbNext() {
        if ( jbNext == null ) {
            jbNext = new JButton();
            jbNext.setText("Next");
        }
        return jbNext;
    }

    /**
     * This method initializes jbFinish	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbFinish() {
        if ( jbFinish == null ) {
            jbFinish = new JButton();
            jbFinish.setText("Finish");
            jbFinish.setEnabled(false);
        }
        return jbFinish;
    }

    /**
     * This method initializes jbBack	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJbBack() {
        if ( jbBack == null ) {
            jbBack = new JButton();
            jbBack.setText("Back");
        }
        return jbBack;
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
        }
        return jbCancel;
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
            jContentPane.add(getJpButtons(), BorderLayout.SOUTH);
            jContentPane.add(getQuestionMainPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }
    
    private JPanel getQuestionMainPanel() {
        if ( jpQuestionMain == null ) {
            jpQuestionMain = new JPanel();
            jpQuestionMain.setLayout( new BorderLayout() );
        }
        return jpQuestionMain;
    }
    
    private void setTableInputField( InputTableField field ) {
        getQuestionMainPanel().removeAll();
        if( field != null ) {
            QuestionPanel qp = new QuestionPanel( field );
            getQuestionMainPanel().add( qp, BorderLayout.CENTER );
            inputPanels.push( qp );
        }
        getJContentPane().revalidate();
    }
    
    private void setQuestionPanel( QuestionPanel qp ) {
        getQuestionMainPanel().removeAll();
        if( qp != null ) {
            getQuestionMainPanel().add( qp, BorderLayout.CENTER );
        }
        getJContentPane().revalidate();
    }
    
    /**
         * @param args
         */
    public static void main( String[] args ) {
        // TODO Auto-generated method stub

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                VPackage pack = PackageXmlProcessor.load( new File("/Users/pavelg/Dropbox/CoCoViLa_packages/MyTest/MyTest2.xml") );
                if( pack == null ) {
                    System.out.println( "Package null, aborting" );
                    return;
                }
                Table table = (Table)TableManager.getTable( pack, 
                        "ExampleCons1" );//Example123, TestEmpty
                ExpertConsultant thisClass = new ExpertConsultant(table);
                thisClass.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                thisClass.setVisible( true );
            }
        } );
    }

    /**
     * This is the default constructor
     */
    public ExpertConsultant() {
        this( null );
    }

    public ExpertConsultant( Table table ) {
        super();
        this.table = table;
        if( table != null )
            System.out.println("Expert System Constant: " + table.getTableId() + " inputs: " + table.getInputFields().size()
                    + " rows: " + table.getRowCount() + " cols: " + table.getColumnCount() 
                    + " hr: " + table.getHRules().size() + " vr: " + table.getVRules().size());
        initialize();
        
        initTable();
    }
    
    class State {
        Map<InputTableField, Object> inputsToValues = new LinkedHashMap<InputTableField, Object>();
        List<Integer> availableRowIds = new ArrayList<Integer>();
        List<Integer> availableColumnIds = new ArrayList<Integer>();
    }
    
    Stack<State> states = new Stack<ExpertConsultant.State>();
    
    private void initTable() {
        if( table == null ) return;
        
        InputTableField firstInput = TableInferenceEngine.getFirstTableInput( table );
        
        if( firstInput == null ) {
            System.out.println( "Consultant: no inputs are required! Output: " + table.queryTable( null, false ) );
            return;
        }
        
        System.out.println( "Consultant: first input is " + firstInput.getId() );
        
        setTableInputField( firstInput );

        ActionListener lst = new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                if( e.getSource() == getJbNext() ) {
                    QuestionPanel qp = inputPanels.peek();
                    
                    Object val = null;
                    try {
                        val = TypeUtil.createObjectFromString( qp.field.getType(), (String)qp.getValue() );
                    } catch ( Exception ex ) {
                        ex.printStackTrace();
                    }
                    
                    if( val == null ) return;
                    System.out.println( "Consultant: " + qp.field.getId() + " value is " + val );

                    State state = new State();
                    state.inputsToValues.put( qp.field, val );
                    states.push( state );
                    
                    //first, try horisontal rules
                    System.out.println( "Consultant: horizontal" );
                    InputTableField nextInput = TableInferenceEngine.getNextInputAndRelevantIds( 
                            table.getHRules(), qp.field, state.inputsToValues, 
                            table.getOrderedRowIds(), state.availableRowIds );

                    //next, vertical
                    if( nextInput == null ) {
                        System.out.println( "Consultant: vertical" );
                        nextInput = TableInferenceEngine.getNextInputAndRelevantIds( 
                                table.getVRules(), qp.field, state.inputsToValues, 
                                table.getOrderedColumnIds(), state.availableColumnIds );
                    }

                    System.out.println("Consultant: Next input is: " + nextInput );

                    System.out.println( "Consultant: Rows: " + state.availableRowIds );
                    System.out.println( "Consultant: Cols: " + state.availableColumnIds );

                    if( nextInput == null ) {
                        System.out.println( "Consultant: no additional inputs are required! Output: "/* + table.queryTable( null, false ) */);
                        return;
                    }

                    System.out.println( "Consultant: next input is " + nextInput.getId() );

                    setTableInputField( nextInput );
                        
                } else if( e.getSource() == getJbBack() ) {
                    setQuestionPanel( inputPanels.pop() );
                }
                getJbBack().setEnabled( !inputPanels.isEmpty() );
            }
        };
        getJbNext().addActionListener( lst );
        getJbBack().addActionListener( lst );
    }
    
    private Stack<QuestionPanel> inputPanels = new Stack<ExpertConsultant.QuestionPanel>();
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(385, 215);
        this.setResizable(false);
        this.setContentPane( getJContentPane() );
        this.setTitle("Expert System Consultant");
    }

    private static class QuestionPanel extends JPanel {
        
        private InputTableField field;
        private JPanel jpNorthQuestion = null;
        private JPanel jpCenter = null;
        private JLabel jlblQuestion = null;
        private JLabel jlblInputType = null;
        private JSlider jsIntRangeSlider = null;
        private Callback callback;
        
        interface Callback {
            Object getValue();
        }
        
        QuestionPanel( InputTableField field ) {
            this.field = field;
            initLayout();
        }
        
        private Object getValue() {
            return callback != null ? callback.getValue() : null;
        }
        
        private JComponent createInputComponent() {
            String type = field.getType();
            System.out.println( "Analyzing type: " + type );
            
            if( TypeUtil.isArray( type ) ) {
                System.out.println("Array");
                //TODO implement more sophisticated array entry GUI
                return createInputTextField();
            }
            
            List<TableFieldConstraint> constrList = field.getConstraints();
            
            if( constrList != null && !constrList.isEmpty() ) {
                //currently only take first constraint
                TableFieldConstraint constr = constrList.get( 0 );
                
                if( constr instanceof TableFieldConstraint.List ) {
                    TableFieldConstraint.List list = (TableFieldConstraint.List)constr;
                    JComboBox jcbox = new JComboBox( list.getValueList() );
                    return jcbox;
                } else if( constr instanceof TableFieldConstraint.Range ) {
                    TableFieldConstraint.Range range = (TableFieldConstraint.Range)constr;
                    
                    @SuppressWarnings( "rawtypes" )
                    SpinnerNumberModel model = new SpinnerNumberModel( null, (Comparable)range.getMin(), (Comparable)range.getMax(), 1 );
                    
                    if( TypeUtil.isIntegral( type ) ) {
                        JSlider jsl = new JSlider();
                        jsl.setName("");
                        jsl.setPaintTicks(true);
                        jsl.setSnapToTicks(true);
                        jsl.setMinorTickSpacing(5);
                        jsl.setMajorTickSpacing(50);
                        jsl.setPaintLabels(true);
                    } else if( TypeUtil.isFractional( type ) ) {
                        JSpinner jsp = new JSpinner();
                        jsp.setModel( null );
                    } else if( TypeUtil.isString( type ) ) {
                        
                    }
                }
                return createInputTextField();
            }
            
            return createInputTextField();
        }
        
        private JComponent createInputTextField() {
            final JTextField jtf = new JTextField(20);
            callback = new Callback() {
                @Override
                public Object getValue() {
                    return jtf.getText();
                }
            };
            return jtf;
        }
        
        private void initLayout() {
            setLayout( new BorderLayout() );
            add(getJpNorthQuestion(), BorderLayout.NORTH);
            add(GuiUtil.addComponentAsFlow( createInputComponent(), FlowLayout.CENTER ), 
                    BorderLayout.CENTER);
        }
        
        private JLabel getJlblQuestion() {
            if( jlblQuestion == null ) {
                jlblQuestion = new JLabel();
                String q = field.getQuestion() != null ? field.getQuestionText() 
                        : "What is the value of an input (" + field.getType() + ") " + field.getId() + "?";
                jlblQuestion.setText( q );
                jlblQuestion.setToolTipText(""); 
            }
            return jlblQuestion;
        }
        
        /**
         * This method initializes jpNorthQuestion  
         *  
         * @return javax.swing.JPanel   
         */
        private JPanel getJpNorthQuestion() {
            if ( jpNorthQuestion == null ) {
                jpNorthQuestion = new JPanel();
                jpNorthQuestion.setLayout(new FlowLayout());
                jpNorthQuestion.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
                jpNorthQuestion.add(getJlblQuestion(), null);
            }
            return jpNorthQuestion;
        }

        /**
         * This method initializes jpCenter 
         *  
         * @return javax.swing.JPanel   
         */
        private JPanel getJpCenter() {
            if ( jpCenter == null ) {
                GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
                gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
                gridBagConstraints1.gridy = 3;
                gridBagConstraints1.weightx = 1.0;
                gridBagConstraints1.gridx = 0;
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                jlblInputType = new JLabel();
                jlblInputType.setText("Current value: 35");
                jlblInputType.setVisible(true);
                jpCenter = new JPanel();
                jpCenter.setLayout(new GridBagLayout());
                jpCenter.add(jlblInputType, gridBagConstraints);
                jpCenter.add(getJsIntRangeSlider(), gridBagConstraints1);
            }
            return jpCenter;
        }
        
        /**
         * This method initializes jsIntRangeSlider 
         *  
         * @return javax.swing.JSlider  
         */
        private JSlider getJsIntRangeSlider() {
            if ( jsIntRangeSlider == null ) {
                jsIntRangeSlider = new JSlider();
                jsIntRangeSlider.setName("");
                jsIntRangeSlider.setPaintTicks(true);
                jsIntRangeSlider.setSnapToTicks(true);
                jsIntRangeSlider.setMinorTickSpacing(5);
                jsIntRangeSlider.setMajorTickSpacing(50);
                jsIntRangeSlider.setPaintLabels(true);
            }
            return jsIntRangeSlider;
        }
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"

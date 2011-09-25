/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.TableInferenceEngine.InputFieldAndSelectedId;
import ee.ioc.cs.vsle.table.exception.*;
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
        updateMainPanel();
    }
    
    private void setQuestionPanel( QuestionPanel qp ) {
        getQuestionMainPanel().removeAll();
        getQuestionMainPanel().add( qp, BorderLayout.CENTER );
        updateMainPanel();
    }
    
    /**
     * Shows table output
     * 
     * @param value
     * @param inputValues
     */
    private void setAnswerPanel( Object value, Map<InputTableField, Object> inputValues ) {
        getQuestionMainPanel().removeAll();
        String result = value != null ? value.toString() : "Output is undefined!";
        
        JPanel jpBox = new JPanel();
        jpBox.setLayout( new BoxLayout( jpBox, BoxLayout.Y_AXIS ) );

        JPanel jpOutput = GuiUtil.addComponentAsFlow( new JLabel( result ), FlowLayout.CENTER );
        jpOutput.setBorder( BorderFactory.createTitledBorder( "Expert Table Output" ) );
        jpBox.add( jpOutput );
        
        if( inputValues != null && !inputValues.isEmpty() ) {
            JPanel jpInputs = new JPanel();
            jpInputs.setLayout( new BoxLayout( jpInputs, BoxLayout.Y_AXIS ) );
            jpInputs.setBorder( BorderFactory.createTitledBorder( "Given input values" ) );
            jpBox.add( jpInputs );
            
            for ( Map.Entry<InputTableField,Object> entry : inputValues.entrySet() ) {
                InputTableField field = entry.getKey();
                String input = "(" + field.getType() + ") " + field.getId() + " = " + entry.getValue();
                jpInputs.add( GuiUtil.addComponentAsFlow( new JLabel( input ), FlowLayout.CENTER ) );
            }
        }
        
        getQuestionMainPanel().add( jpBox, BorderLayout.CENTER );
        updateMainPanel();
    }
    
    private void updateMainPanel() {
        getQuestionMainPanel().revalidate();
        getQuestionMainPanel().repaint();
    }
    
    /**
         * @param args
         */
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                VPackage pack = PackageXmlProcessor.load( new File("/Users/pavelg/Dropbox/CoCoViLa_packages/MyTest/MyTest2.xml") );
                if( pack == null ) {
                    System.out.println( "Package null, aborting" );
                    return;
                }
                Table table = (Table)TableManager.getTable( pack, 
                        "ExampleCons1" );//Example123, TestEmpty
                ExpertConsultant thisClass = new ExpertConsultant(null, table);
                thisClass.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                thisClass.setVisible( true );
            }
        } );
    }

    /**
     * This is the default constructor
     */
    public ExpertConsultant() {
        this( null, (String)null );
    }

    public ExpertConsultant( Window parent, String path ) {
        this( parent, TableManager.openTable( parent, path ).getFirst() );
    }
    
    public ExpertConsultant( Window parent, Table table ) {
        super();
        setLocationRelativeTo( parent );
        
        this.table = table;
        if( table != null )
            System.out.println("Expert System Constant: " + table.getTableId() + " inputs: " + table.getInputFields().size()
                    + " rows: " + table.getRowCount() + " cols: " + table.getColumnCount() 
                    + " hr: " + table.getHRules().size() + " vr: " + table.getVRules().size());
        initialize();
        
        initTable();
    }
    
    private class State {
        Map<InputTableField, Object> inputsToValues = new LinkedHashMap<InputTableField, Object>();
        List<Integer> availableRowIds = new ArrayList<Integer>();
        List<Integer> availableColumnIds = new ArrayList<Integer>();
        boolean isFirst = false;
        boolean isFinished = false;
        int selectedRowId = -1;
        int selectedColId = -1;
    }
    
    private Stack<State> states = new Stack<ExpertConsultant.State>();
    
    private void initTable() {
        if( table == null ) return;
        
        State initState = new State();
        initState.isFirst = true;
        states.push( initState );
        
        InputTableField firstInput = TableInferenceEngine.getFirstTableInput( table );
        
        if( firstInput == null ) {
            initState.isFinished = true;
            System.out.println( "Consultant: no inputs are required! Output: " + table.queryTable( null, false ) );
            setAnswerPanel( table.queryTable( null, false ), initState.inputsToValues );
            checkButtonStates();
            return;
        }
        
        System.out.println( "Consultant: first input is " + firstInput.getId() );
        
        initState.availableRowIds.addAll( table.getOrderedRowIds() );
        initState.availableColumnIds.addAll( table.getOrderedColumnIds() );
        
        setTableInputField( firstInput );

        checkButtonStates();
    }
    
    /**
     * 
     */
    private void checkNextInput() {
        QuestionPanel qp = inputPanels.peek();
        
        Object val = null;
        String type = qp.field.getType();
        String sval = (String)qp.getValue();
        try {
            val = TypeUtil.createObjectFromString( type, sval );
            //TODO implement constraints check!!!
        } catch ( Exception ex ) {
            if( RuntimeProperties.isLogDebugEnabled() )
                ex.printStackTrace();
        }
        
        if( val == null ) {
            JOptionPane.showMessageDialog( ExpertConsultant.this, 
                    "Entered value \'" + sval + "\' is not of type \'" + type + "\'", 
                    "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        System.out.println( "Consultant: " + qp.field.getId() + " value is " + val );
        
        State prevState = states.peek();
        System.out.println( "Next! rows: " + prevState.availableRowIds + " cols: " + prevState.availableColumnIds );
        
        State state = new State();
        state.inputsToValues.putAll( prevState.inputsToValues );
        state.inputsToValues.put( qp.field, val );
        state.selectedRowId = prevState.selectedRowId;
        state.selectedColId = prevState.selectedColId;
        states.push( state );
        
        InputTableField nextInput = null;
        
        //first, try horisontal rules
        System.out.println( "Consultant: horizontal" );
        if( state.selectedRowId < 0 ) {
            InputFieldAndSelectedId res = TableInferenceEngine.getNextInputAndRelevantIds( 
                table.getHRules(), qp.field, state.inputsToValues, 
                prevState.availableRowIds, state.availableRowIds );
            
            if( res.getSelectedId() != null )//check if a suitable row has been found
                state.selectedRowId = res.getSelectedId();
            else
                nextInput = res.getInput();//if not, this could be next input (or null)
        }

        //next, vertical
        System.out.println( "Consultant: vertical" );
        if( state.selectedColId < 0 ) {
            InputFieldAndSelectedId res = TableInferenceEngine.getNextInputAndRelevantIds( 
                    table.getVRules(), qp.field, state.inputsToValues, 
                    prevState.availableColumnIds, state.availableColumnIds );

            if( res.getSelectedId() != null )//check if a suitable column has been found
                state.selectedColId = res.getSelectedId();
            else if( nextInput == null )
                nextInput = res.getInput();//if not, this could be next input (or null)
        }

        System.out.println( "Consultant: Rows: " + state.availableRowIds + " selected: " + state.selectedRowId );
        System.out.println( "Consultant: Cols: " + state.availableColumnIds + " selected: " + state.selectedColId );
        System.out.println( "Consultan: derived next input: " + nextInput );
        
        if( nextInput == null 
                && state.selectedRowId > -1 && state.selectedColId > -1 ) {
            Object output = null;
            try {
                output = table.getOutputValue( state.selectedRowId, state.selectedColId );
            } catch ( TableCellValueUndefinedException e ) {
                //
            } catch ( Exception e ) {
                JOptionPane.showMessageDialog( ExpertConsultant.this, 
                        "Error occured, unable to get an output value from the table", 
                        "Error", JOptionPane.ERROR_MESSAGE );
                states.pop();
                return;
            }

            state.isFinished = true;
            
            System.out.println( "Consultant: no additional inputs are required! Output: " + output
                    + " row: " + state.selectedRowId + " col: " + state.selectedColId );
            setAnswerPanel( output, state.inputsToValues );
        } else if( nextInput != null ) {
            System.out.println( "Consultant: next input is " + nextInput.getId() );
            setTableInputField( nextInput );
        } else {
            System.out.println( "Dunno what to do next...");
            state.isFinished = true;
            setAnswerPanel( null, state.inputsToValues );
        }
    }
    
    private void checkButtonStates() {
        State currentState = states.peek();
        
        getJbBack().setEnabled( !currentState.isFirst );
        getJbNext().setEnabled( !currentState.isFinished );
        getJbFinish().setEnabled( currentState.isFinished );
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
        this.setTitle("Expert System Consultant" + ( table != null ? ": " + table.getTableId() : "" ) );
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        
        //init listeners

        ActionListener lst = new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                if( e.getSource() == getJbNext() ) {
                    //
                    checkNextInput();
                } else if( e.getSource() == getJbBack() ) {
                    //
                    if( !states.pop().isFinished ) { 
                            inputPanels.pop();
                    }
                    setQuestionPanel( inputPanels.peek() );
                    System.out.println("Back! " + inputPanels.peek().field.getId() );
                    System.out.println("State: " + states.peek().inputsToValues );
                } else if( e.getSource() == getJbCancel() 
                        || e.getSource() == getJbFinish() ) {
                    getJbNext().removeActionListener( this );
                    getJbBack().removeActionListener( this );
                    getJbCancel().removeActionListener( this );
                    getJbFinish().removeActionListener( this );
                    dispose();
                    return;
                }
                checkButtonStates();
            }
        };
        getJbNext().addActionListener( lst );
        getJbBack().addActionListener( lst );
        getJbCancel().addActionListener( lst );
        getJbFinish().addActionListener( lst );
    }

    
    /**
     * Contains label with a question and component for value entry 
     */
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
            System.out.println( "Analyzing type: " + type + " of input " + field.getId() );
            
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
                    final JComboBox jcbox = new JComboBox( list.getValueList() );
                    callback = new Callback() {
                        @Override
                        public Object getValue() {
                            return jcbox.getSelectedItem();
                        }
                    };
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

/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
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
public class ExpertConsultant extends JDialog {

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

            JScrollPane scroll = new JScrollPane( jpInputs, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            jpBox.add( scroll );
            
            for ( Map.Entry<InputTableField,Object> entry : inputValues.entrySet() ) {
                InputTableField field = entry.getKey();
                String input = /*"(" + field.getType() + ") " +*/ field.getId() + " = " + entry.getValue();
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
                    if (RuntimeProperties.isLogDebugEnabled()) db.p( "Package null, aborting" );
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
        this( parent, table, null, true );
    }
    
    public ExpertConsultant( Window parent, Table table, Map<InputTableField, Object> knownInputs, boolean showResultPanel ) {
        super();
        setLocationRelativeTo( parent );
        
        this.table = table;
        this.knownInputs = knownInputs;
        this.showResultPanel = showResultPanel;
        
//        if( table != null )
//            if (RuntimeProperties.isLogDebugEnabled()) db.p("Expert System Constant: " + table.getTableId() + " inputs: " + table.getInputFields().size()
//                    + " rows: " + table.getRowCount() + " cols: " + table.getColumnCount() 
//                    + " hr: " + table.getHRules().size() + " vr: " + table.getVRules().size());
        initialize();
        
        initTable();
    }
    
    private boolean showResultPanel;
    private Map<InputTableField, Object> knownInputs;// = new LinkedHashMap<InputTableField, Object>();
    
    private class State {
        Map<InputTableField, Object> inputsToValues = new LinkedHashMap<InputTableField, Object>();
        List<Integer> availableRowIds = new ArrayList<Integer>();
        List<Integer> availableColumnIds = new ArrayList<Integer>();
        boolean isFirst = false;
        boolean isFinished = false;
        int selectedRowId = -1;
        int selectedColId = -1;
        boolean isFromKnown = false;
    }
    
    //this list should act as a stack, but with possibility to iterate elements
    //without popping them out (needed for enabling/disabling the Back button)
    private LinkedList<State> states = new LinkedList<ExpertConsultant.State>();
    
    private void initTable() {
        if( table == null ) return;
        
        State initState = new State();
        initState.isFirst = true;
        states.push( initState );
        
        InputTableField firstInput = TableInferenceEngine.getFirstTableInput( table );
        
        if( firstInput == null ) {
            initState.isFinished = true;
//            if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: no inputs are required! Output: " + table.queryTable( null, false ) );
            setResult( table.queryTable( null, false ), initState.inputsToValues );
            checkButtonStates();
            return;
        }
        
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: first input is " + firstInput.getId() );
        
        initState.availableRowIds.addAll( table.getOrderedRowIds() );
        initState.availableColumnIds.addAll( table.getOrderedColumnIds() );
        
        checkButtonStates();
        
        setNextInput( firstInput );
    }
    
    /**
     * 
     */
    private void checkNextInput( InputTableField currentInput, Object val ) {
        
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: " + currentInput.getId() + " value is " + val );
        
        State prevState = states.peek();
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Next! rows: " + prevState.availableRowIds + " cols: " + prevState.availableColumnIds );
        
        State state = new State();
        state.inputsToValues.putAll( prevState.inputsToValues );
        state.inputsToValues.put( currentInput, val );
        state.selectedRowId = prevState.selectedRowId;
        state.selectedColId = prevState.selectedColId;
        states.push( state );
        
        InputTableField nextInput = null;
        
        //first, try horisontal rules
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: horizontal" );
        if( state.selectedRowId < 0 ) {
            InputFieldAndSelectedId res = TableInferenceEngine.getNextInputAndRelevantIds( 
                table.getHRules(), currentInput, state.inputsToValues, 
                prevState.availableRowIds, state.availableRowIds );
            
            if( res.getSelectedId() != null )//check if a suitable row has been found
                state.selectedRowId = res.getSelectedId();
            else
                nextInput = res.getInput();//if not, this could be next input (or null)
        }

        //next, vertical
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: vertical" );
        if( state.selectedColId < 0 ) {
            InputFieldAndSelectedId res = TableInferenceEngine.getNextInputAndRelevantIds( 
                    table.getVRules(), currentInput, state.inputsToValues, 
                    prevState.availableColumnIds, state.availableColumnIds );

            if( res.getSelectedId() != null )//check if a suitable column has been found
                state.selectedColId = res.getSelectedId();
            else if( nextInput == null )
                nextInput = res.getInput();//if not, this could be next input (or null)
        }

        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: Rows: " + state.availableRowIds + " selected: " + state.selectedRowId );
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: Cols: " + state.availableColumnIds + " selected: " + state.selectedColId );
        if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultan: derived next input: " + nextInput );
        
        if( nextInput == null 
                && state.selectedRowId > -1 && state.selectedColId > -1 ) {
            Object output = null;
            try {
                output = table.getOutputValue( state.selectedRowId, state.selectedColId );
            } catch ( TableCellValueUndefinedException e ) {
                //TODO handle this exception
            } catch ( Exception e ) {
                JOptionPane.showMessageDialog( ExpertConsultant.this, 
                        "Error occured, unable to get an output value from the table", 
                        "Error", JOptionPane.ERROR_MESSAGE );
                states.pop();
                return;
            }

            state.isFinished = true;
            
            if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: no additional inputs are required! Output: " + output
                    + " row: " + state.selectedRowId + " col: " + state.selectedColId );
            setResult( output, state.inputsToValues );
        } else if( nextInput != null ) {
            if (RuntimeProperties.isLogDebugEnabled()) db.p( "Consultant: next input is " + nextInput.getId() );
            setNextInput( nextInput );
        } else {
            if (RuntimeProperties.isLogDebugEnabled()) db.p( "Dunno what to do next...");
            state.isFinished = true;
            setResult( null, state.inputsToValues );
        }
    }
    
    private void setNextInput( InputTableField inputField ) {
    
        Object value = null;
        if( knownInputs != null && ( value = knownInputs.get( inputField ) ) != null ) {
            //the value of next input is known and can be used immediately
            State currentState = states.peek();
            currentState.isFromKnown = true;
            checkNextInput( inputField, value );
        } else {
            //otherwise build question panel and wait for user input
            setTableInputField( inputField );
        }

        checkButtonStates();
    }
    
    /**
     * This method is called after pressing Next button
     */
    private void checkNextInputFromQuestionPanel() {
        
        QuestionPanel qp = inputPanels.peek();
        InputTableField currentInput = qp.field;
        
        String type = currentInput.getType();
        String sval = (String)qp.getValue();
        
        Object value = null;
        
        try {
            value = TypeUtil.createObjectFromString( type, sval );
            
            currentInput.verifyConstraints( value );
        } catch ( TableInputConstraintViolationException ex ) {
            JOptionPane.showMessageDialog( ExpertConsultant.this, 
                    ex.getMessage(), 
                    "Constraint Violation", JOptionPane.ERROR_MESSAGE );
            return;
        } catch ( Exception ex ) {
            if( RuntimeProperties.isLogDebugEnabled() )
                ex.printStackTrace();
        }
        
        if( value == null ) {
            JOptionPane.showMessageDialog( ExpertConsultant.this, 
                    "Entered value \'" + sval + "\' is not of type \'" + type + "\'", 
                    "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }
        
        checkNextInput( currentInput, value );
    }
    
    private void setResult( Object value, Map<InputTableField, Object> inputValues ) {
        
        returnValue = value;
        
        if( showResultPanel ) {
            setAnswerPanel( value, inputValues );
        } else {
            returnOk = true;
            dispose();
        }
    }
    
    private Object returnValue;
    private boolean returnOk = false;
    
    public boolean isOk() {
        return returnOk;
    }
    
    public Object getValue() {
        return returnValue;
    }
    
    private void checkButtonStates() {
        
        State currentState = states.peek();
        getJbNext().setEnabled( !currentState.isFinished );
        getJbFinish().setEnabled( currentState.isFinished );
        
        //for the Back button, make sure that there is a state
        //where it should be possible to back up, i.e.
        //which is not first and not with isFromKnown flag
        boolean enabled = false;
        for( Iterator<State> it = states.iterator(); it.hasNext(); ) {
            currentState = it.next();
            
            if( currentState.isFinished ) 
                continue;
            else if( !currentState.isFirst && !currentState.isFromKnown ) {
                enabled = true;
                break;
            }
        }
        getJbBack().setEnabled( enabled );
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
                    checkNextInputFromQuestionPanel();
                } else if( e.getSource() == getJbBack() ) {
                    
                    if( !states.pop().isFinished ) { 
                        inputPanels.pop();
                    }
                    //check other states, because there are also no corresponding 
                    //input panels for "from known" states
                    for( Iterator<State> stateIt = states.iterator(); stateIt.hasNext(); ) {
                        if( stateIt.next().isFromKnown ) {
                            stateIt.remove();//pop
                            continue;
                        }
                        break;
                    }
                    returnValue = null;
                    setQuestionPanel( inputPanels.peek() );
                    if (RuntimeProperties.isLogDebugEnabled()) db.p("Back! " + inputPanels.peek().field.getId() );
                    if (RuntimeProperties.isLogDebugEnabled()) db.p("State: " + states.peek().inputsToValues );
                } else if( e.getSource() == getJbCancel() 
                        || ( returnOk = ( e.getSource() == getJbFinish() ) ) ) {
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
        private JLabel jlblQuestion = null;
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
            if (RuntimeProperties.isLogDebugEnabled()) db.p( "Analyzing type: " + type + " of input " + field.getId() );
            
            if( TypeUtil.isArray( type ) ) {
                if (RuntimeProperties.isLogDebugEnabled()) db.p("Array");
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
                    
//                    @SuppressWarnings( "rawtypes" )
//                    SpinnerNumberModel model = new SpinnerNumberModel( null, (Comparable)range.getMin(), (Comparable)range.getMax(), 1 );
                    
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
            add( getJpNorthQuestion(), BorderLayout.NORTH );
            add( GuiUtil.addComponentAsFlow( createInputComponent(), FlowLayout.CENTER ), 
                    BorderLayout.CENTER );
            add( GuiUtil.addComponentAsFlow( getJtaConstraints(), FlowLayout.CENTER ), 
                    BorderLayout.SOUTH );
            getJtaConstraints().setBackground( getJtaConstraints().getParent().getBackground() );
        }
        
        private JTextArea jtaConstraints;
        
        private JTextArea getJtaConstraints() {
            //shows all options
            if( jtaConstraints == null ) {
                List<TableFieldConstraint> constraints = field.getConstraints();
                StringBuilder text = new StringBuilder();

                if( constraints != null ) {
                    for ( TableFieldConstraint constr : constraints ) {
                        if( constr instanceof TableFieldConstraint.List ) 
                            continue;//do not display list constraint because combo-box already 
                        if( text.length() > 0 )
                            text.append( "\n" );
                        text.append( MessageFormat.format( constr.printConstraint(), field.getId() ) );
                    }
                }

                jtaConstraints = new JTextArea(0, 20);
                jtaConstraints.setEditable( false );
                jtaConstraints.setText( text.toString() );
            }
            return jtaConstraints;
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

//        /**
//         * This method initializes jpCenter 
//         *  
//         * @return javax.swing.JPanel   
//         */
//        private JPanel getJpCenter() {
//            if ( jpCenter == null ) {
//                GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
//                gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
//                gridBagConstraints1.gridy = 3;
//                gridBagConstraints1.weightx = 1.0;
//                gridBagConstraints1.gridx = 0;
//                GridBagConstraints gridBagConstraints = new GridBagConstraints();
//                gridBagConstraints.gridx = 0;
//                gridBagConstraints.gridy = 2;
//                jlblInputType = new JLabel();
//                jlblInputType.setText("Current value: 35");
//                jlblInputType.setVisible(true);
//                jpCenter = new JPanel();
//                jpCenter.setLayout(new GridBagLayout());
//                jpCenter.add(jlblInputType, gridBagConstraints);
//                jpCenter.add(getJsIntRangeSlider(), gridBagConstraints1);
//            }
//            return jpCenter;
//        }
        
//        /**
//         * This method initializes jsIntRangeSlider 
//         *  
//         * @return javax.swing.JSlider  
//         */
//        private JSlider getJsIntRangeSlider() {
//            if ( jsIntRangeSlider == null ) {
//                jsIntRangeSlider = new JSlider();
//                jsIntRangeSlider.setName("");
//                jsIntRangeSlider.setPaintTicks(true);
//                jsIntRangeSlider.setSnapToTicks(true);
//                jsIntRangeSlider.setMinorTickSpacing(5);
//                jsIntRangeSlider.setMajorTickSpacing(50);
//                jsIntRangeSlider.setPaintLabels(true);
//            }
//            return jsIntRangeSlider;
//        }
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"

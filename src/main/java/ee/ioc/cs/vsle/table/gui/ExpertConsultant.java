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
import javax.swing.text.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.TableInferenceEngine.InputFieldAndSelectedId;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavelg
 *
 */
public class ExpertConsultant extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ExpertConsultant.class);
    
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
        qp.updateFocus();
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
        String result = value != null ? value.toString() : "undefined!";
        
        JPanel jpBox = new JPanel();
        jpBox.setLayout( new BoxLayout( jpBox, BoxLayout.Y_AXIS ) );

        JEditorPane resPane = new JEditorPane("text/html", "<html><body>"
                + "<font face=\"Arial\" color=\"black\" size=\"4\"><p style=\"text-align:center\">" 
                + result + "</font></body><html>" );
        resPane.setEditable( false );
        resPane.setToolTipText( "" );
        JPanel jpOutput = GuiUtil.addComponentAsFlow( resPane, FlowLayout.CENTER );
        resPane.setBackground( jpOutput.getBackground() );
        jpOutput.setBorder( BorderFactory.createTitledBorder( "The result is:" ) );
        jpBox.add( jpOutput );
        
        if( inputValues != null && !inputValues.isEmpty() ) {
            JPanel jpInputs = new JPanel();
            jpInputs.setLayout( new BoxLayout( jpInputs, BoxLayout.Y_AXIS ) );
            jpInputs.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

            JScrollPane scroll = new JScrollPane( jpInputs, 
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            scroll.setBorder( BorderFactory.createTitledBorder( "for the given input values:" ) );
            jpBox.add( scroll );
            
            for ( Map.Entry<InputTableField,Object> entry : inputValues.entrySet() ) {
                InputTableField field = entry.getKey();
                StringBuilder sb = new StringBuilder();
                
                if( field.getQuestion() != null )
                    sb.append( field.getQuestionText() );
                else
                    sb.append( "What is the value of an input (" )
                        .append( field.getType() ).append( ") " )
                        .append( field.getId() ).append( "?" );
                sb.append( "\nAnswer: " ).append(  entry.getValue() );
                JTextArea jta = new JTextArea();
                jta.setText( sb.toString() );
                jta.setEditable( false );
                jta.setLineWrap( true );
                jta.setWrapStyleWord( true );
                jta.setBackground( jpInputs.getBackground() );
                jpInputs.add( GuiUtil.addComponentAsBorderPaneCenter( jta ) );
                jpInputs.add( Box.createVerticalStrut( 5 ) );
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
                    logger.debug( "Package null, aborting" );
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
//            logger.debug("Expert System Constant: " + table.getTableId() + " inputs: " + table.getInputFields().size()
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

        @Override
        public String toString() {
            return "State [inputsToValues=" + inputsToValues + ", isFirst="
                    + isFirst + ", isFinished=" + isFinished + ", isFromKnown="
                    + isFromKnown + ", availableRowIds=" + availableRowIds
                    + ", availableColumnIds=" + availableColumnIds
                    + ", selectedRowId=" + selectedRowId + ", selectedColId="
                    + selectedColId + "]";
        }
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
//            logger.debug( "Consultant: no inputs are required! Output: " + table.queryTable( null, false ) );
            setResult( table.queryTable( null, false ), initState.inputsToValues );
            checkButtonStates();
            return;
        }
        
        logger.debug( "Consultant: first input is " + firstInput.getId() );
        
        initState.availableRowIds.addAll( table.getOrderedRowIds() );
        initState.availableColumnIds.addAll( table.getOrderedColumnIds() );
        
        checkButtonStates();
        
        setNextInput( firstInput );
    }
    
    /**
     * 
     */
    private void checkNextInput( InputTableField currentInput, Object val ) {
        
        logger.debug( "Consultant: " + currentInput.getId() + " value is " + val );
        
        State prevState = states.peek();
        logger.debug( "Next! rows: " + prevState.availableRowIds + " cols: " + prevState.availableColumnIds );
        
        State state = new State();
        state.inputsToValues.putAll( prevState.inputsToValues );
        state.inputsToValues.put( currentInput, val );
        state.selectedRowId = prevState.selectedRowId;
        state.selectedColId = prevState.selectedColId;
        states.push( state );
        
        InputTableField nextInput = null;
        
        //first, try horisontal rules
        logger.debug( "\nConsultant: horizontal" );
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
        logger.debug( "\nConsultant: vertical" );
        if( state.selectedColId < 0 ) {
            InputFieldAndSelectedId res = TableInferenceEngine.getNextInputAndRelevantIds( 
                    table.getVRules(), currentInput, state.inputsToValues, 
                    prevState.availableColumnIds, state.availableColumnIds );

            if( res.getSelectedId() != null )//check if a suitable column has been found
                state.selectedColId = res.getSelectedId();
            else if( nextInput == null )
                nextInput = res.getInput();//if not, this could be next input (or null)
        }

        logger.debug( "Consultant: Rows: " + state.availableRowIds + " selected: " + state.selectedRowId );
        logger.debug( "Consultant: Cols: " + state.availableColumnIds + " selected: " + state.selectedColId );
        logger.debug( "Consultan: derived next input: " + nextInput );
        
        if( nextInput != null ) {
            logger.debug( "Consultant: next input is " + nextInput.getId() );
            setNextInput( nextInput );
            return;
        } 
        
        //first, check ids and try to recover if they are -1
        if( state.selectedRowId == -1 && !state.availableRowIds.isEmpty() ) {
            //pick first suitable id
            state.selectedRowId = state.availableRowIds.get( 0 );
        }
        //same for columns
        if( state.selectedColId == -1 && !state.availableColumnIds.isEmpty() ) {
            //pick first suitable id
            state.selectedColId = state.availableColumnIds.get( 0 );
        }
        
        if( state.selectedRowId > -1 && state.selectedColId > -1 ) {
            Object output = null;
            try {
                output = table.getOutputValue( state.selectedRowId, state.selectedColId );
            } catch ( TableCellValueUndefinedException e ) {
                //TODO handle this exception
                System.out.println( "checkNextInput: " + e.getMessage() );
            } catch ( Exception e ) {
                JOptionPane.showMessageDialog( ExpertConsultant.this, 
                        "Error occured, unable to get an output value from the table", 
                        "Error", JOptionPane.ERROR_MESSAGE );
                states.pop();
                return;
            }

            state.isFinished = true;
            
            logger.debug( "Consultant: no additional inputs are required! Output: " + output
                    + " row: " + state.selectedRowId + " col: " + state.selectedColId );
            setResult( output, state.inputsToValues );
        } else {
            logger.debug( "Dunno what to do next...");
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
        System.out.println("checkButtonStates");
        State currentState = states.peek();
        getJbNext().setEnabled( !currentState.isFinished );
        getJbFinish().setEnabled( currentState.isFinished );
        
        //for the Back button, make sure that there is a state
        //where it should be possible to back up, i.e.
        //which is not first and not with isFromKnown flag
        boolean enabled = false;
        for( Iterator<State> it = states.iterator(); it.hasNext(); ) {
            currentState = it.next();
            System.out.println("Back: " + currentState + " " + enabled);
            if( !currentState.isFirst ) {
                if( currentState.isFinished ) {
                    enabled = true;
                }
                else if( !currentState.isFromKnown ) {
                    enabled = true;
                    break;
                }
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
        this.setSize(385, 240);
        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
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
                    logger.debug("Back! " + inputPanels.peek().field.getId() );
                    logger.debug("State: " + states.peek().inputsToValues );
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

    interface Callback {
        Object getValue();
    }
    
    /**
     * Contains label with a question and component for value entry 
     */
    private class QuestionPanel extends JPanel implements KeyListener {
        
        private InputTableField field;
        private JPanel jpNorthQuestion = null;
        private JTextComponent jtcQuestion = null;
        private JComponent inputComp;
        private Callback callback;
        
        QuestionPanel( InputTableField field ) {
            this.field = field;
            initLayout();
        }
        
        private Object getValue() {
            return callback != null ? callback.getValue() : null;
        }
        
        private ActionListener lstNext = new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent e ) {
                ExpertConsultant.this.getJbNext().doClick();
            }
        };
        
        private JComponent getInputComponent() {
            if( inputComp == null ) {
                String type = field.getType();
                logger.debug( "Analyzing type: " + type + " of input " + field.getId() );

                if( TypeUtil.isArray( type ) ) {
                    logger.debug("Array");
                    //TODO implement more sophisticated array entry GUI
                    return inputComp = createInputTextField();
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
                        jcbox.addKeyListener( this );
                        return inputComp = jcbox;
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
                    return inputComp = createInputTextField();
                }

                return inputComp = createInputTextField();
            }

            return inputComp;
        }
        
        private JComponent createInputTextField() {
            final JTextField jtf = new JTextField(20);
            callback = new Callback() {
                @Override
                public Object getValue() {
                    return jtf.getText();
                }
            };
            jtf.addKeyListener( this );
            return jtf;
        }
        
        private void initLayout() {
            setLayout( new BorderLayout() );
            add( getJpNorthQuestion(), BorderLayout.NORTH );
            add( GuiUtil.addComponentAsFlow( getInputComponent(), FlowLayout.CENTER ), 
                    BorderLayout.CENTER );
            updateFocus();
            add( GuiUtil.addComponentAsFlow( getJtaConstraints(), FlowLayout.CENTER ), 
                    BorderLayout.SOUTH );
            getJtaConstraints().setBackground( getJtaConstraints().getParent().getBackground() );
        }
        
        private void updateFocus() {
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    getInputComponent().requestFocus();
                }
            } );
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
        
        private JTextComponent getJtaQuestion() {
            if( jtcQuestion == null ) {
                String q = field.getQuestion() != null ? field.getQuestionText() 
                        : "What is the value of an input (" + field.getType() + ") " + field.getId() + "?";
                jtcQuestion = new JEditorPane("text/html", "<html><body>"
                                        + "<font face=\"Arial\" color=\"black\" size=\"4\"><p style=\"text-align:center\">" 
                                        + q + "</font></body><html>" );
                jtcQuestion.setEditable( false );
                jtcQuestion.setToolTipText( "" );
            }
            return jtcQuestion;
        }
        
        /**
         * This method initializes jpNorthQuestion  
         *  
         * @return javax.swing.JPanel   
         */
        private JPanel getJpNorthQuestion() {
            if ( jpNorthQuestion == null ) {
                jpNorthQuestion = new JPanel();
                jpNorthQuestion.setLayout( new BorderLayout() );
                jpNorthQuestion.setBorder( BorderFactory.createEmptyBorder( 15, 15, 0, 15 ) );
                jpNorthQuestion.add( getJtaQuestion(), BorderLayout.CENTER );
                getJtaQuestion().setBackground( jpNorthQuestion.getBackground() );
            }
            return jpNorthQuestion;
        }

        @Override
        public void keyReleased( KeyEvent e ) {
            if( e.getKeyCode() == KeyEvent.VK_ENTER )
                ExpertConsultant.this.getJbNext().doClick();
        }

        @Override
        public void keyPressed( KeyEvent e ) {}
        
        @Override
        public void keyTyped( KeyEvent e ) {}

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

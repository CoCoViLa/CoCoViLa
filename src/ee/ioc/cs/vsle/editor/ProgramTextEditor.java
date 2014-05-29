package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import ee.ioc.cs.vsle.event.*;

/**
 * This class contains only GUI and interacts with ProgrammRunner via
 * ProgramRunnerEvents
 */
public class ProgramTextEditor extends JFrame implements ActionListener, TextEditView {

    private ProgramRunnerFeedbackEventListener m_lst = new ProgramRunnerFeedbackEventListener();

    private JButton computeGoal, runProg, computeAll, propagate, invoke, invokeNew, refreshSpec;
    private JTextArea jta_runResult;
    private JTextComponent jta_spec, jta_generatedCode;

    private StatusBar statusBar;
    private JPanel progText, specText, runResult;
    private JTextField invokeField;
    private JTabbedPane tabbedPane;
    private JCheckBox propagateOnRunNew;
    private long m_progRunnerID;
    private UndoRedoDocumentPanel codeUndoRedoSupport;
    private UndoRedoDocumentPanel specUndoRedoSupport;

    // the title should be unique for each spec window because it is used as an
    // ID for managing frames
    private String m_title;

    private FontChangeEvent.Listener fontListener = new FontChangeEvent.Listener() {

        @Override
        public void fontChanged( FontChangeEvent e ) {
            if( e.getElement() == RuntimeProperties.Fonts.CODE ) {
                jta_runResult.setFont( e.getFont() );
                jta_spec.setFont( e.getFont() );
                jta_generatedCode.setFont( e.getFont() );
            }
        }
    };
    
    private static HashMap<String, JFrame> s_frames = new HashMap<String, JFrame>();

    public ProgramTextEditor( long prunnerID, String title ) {

        super( title + " - Specification" );

        s_frames.put( title, this );

        m_title = title;

        ProgramRunnerFeedbackEvent.registerListener( m_lst );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );

        m_progRunnerID = prunnerID;

        setLocationByPlatform( true );

        initUI();

        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
                ProgramRunnerEvent evt = new ProgramRunnerEvent( this, m_progRunnerID, ProgramRunnerEvent.REQUEST_SPEC );

                EventSystem.queueEvent( evt );
            }
        } );

    }

    private void initUI() {

        // addComponentListener( new ComponentResizer(
        // ComponentResizer.CARE_FOR_MINIMUM ) );

        tabbedPane = new JTabbedPane();

        if ( RuntimeProperties.isSyntaxHighlightingOn() ) {
            // jta_spec = new JavaColoredTextPane();
            jta_spec = SyntaxDocument.createEditor();
        } else {
            JTextArea ta = new JTextArea();
            ta.setLineWrap( true );
            ta.setWrapStyleWord( true );
            jta_spec = ta;
        }

        jta_spec.addKeyListener( new CommentKeyListener() );
        Font font = RuntimeProperties.getFont( RuntimeProperties.Fonts.CODE );
        jta_spec.setFont( font );
        JScrollPane areaScrollPane = new JScrollPane( jta_spec );
        areaScrollPane.setRowHeaderView( new LineNumberView( jta_spec ) );
        areaScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        specText = new JPanel();
        specText.setLayout( new BorderLayout() );
        specText.add( areaScrollPane, BorderLayout.CENTER );
        JToolBar progToolBar = new JToolBar();
        progToolBar.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        computeGoal = new JButton( "Compute goal" );
        computeGoal.addActionListener( this );
        progToolBar.add( computeGoal );
        computeAll = new JButton( "Compute all" );
        computeAll.addActionListener( this );

        specUndoRedoSupport = new UndoRedoDocumentPanel(jta_spec.getDocument());
        progToolBar.add( computeAll );
        progToolBar.add(specUndoRedoSupport.getUndoRedoPanel());
        refreshSpec = new JButton( "Update from Scheme" );
        refreshSpec.addActionListener( this );
        progToolBar.add( refreshSpec );

        specText.add( progToolBar, BorderLayout.NORTH );
        tabbedPane.addTab( "Specification", specText );

        if ( RuntimeProperties.isSyntaxHighlightingOn() ) {
            // jta_generatedCode = new JavaColoredTextPane();
            jta_generatedCode = SyntaxDocument.createEditor();
        } else {
            JTextArea ta = new JTextArea();
            ta.setLineWrap( true );
            ta.setWrapStyleWord( true );
            jta_generatedCode = ta;
        }

        jta_generatedCode.addKeyListener( new CommentKeyListener() );
        jta_generatedCode.setFont( font );
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        runProg = new JButton( "Compile & Run" );
        runProg.addActionListener( this );
        toolBar.add( runProg );
        propagateOnRunNew = new JCheckBox( "Propagate", RuntimeProperties.isPropagateValues() );
        propagateOnRunNew.setOpaque( false );
        toolBar.add( propagateOnRunNew );
        codeUndoRedoSupport = new UndoRedoDocumentPanel(jta_generatedCode.getDocument());
        toolBar.add(codeUndoRedoSupport.getUndoRedoPanel());
        JScrollPane programAreaScrollPane = new JScrollPane( jta_generatedCode );
        programAreaScrollPane.setRowHeaderView( new LineNumberView( jta_generatedCode ) );
        programAreaScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        programAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        progText = new JPanel();
        progText.setLayout( new BorderLayout() );
        progText.add( programAreaScrollPane, BorderLayout.CENTER );
        progText.add( toolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Program", progText );

        jta_runResult = new JTextArea();
        jta_runResult.setFont( font );
        JToolBar resultToolBar = new JToolBar();
        propagate = new JButton( "Propagate values" );
        propagate.addActionListener( this );
        resultToolBar.add( propagate );
        invokeNew = new JButton( "Invoke New" );
        invokeNew.addActionListener( this );
        resultToolBar.add( invokeNew );
        invoke = new JButton( "Invoke" );
        invoke.addActionListener( this );
        resultToolBar.add( invoke );
        resultToolBar.add( new JLabel( " Count: " ) );
        invokeField = new JTextField( 4 );
        resultToolBar.add( invokeField );
        resultToolBar.add( Box.createGlue() );
        JScrollPane runResultAreaScrollPane = new JScrollPane( jta_runResult );

        runResultAreaScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        runResult = new JPanel();
        runResult.setLayout( new BorderLayout() );
        runResult.add( runResultAreaScrollPane, BorderLayout.CENTER );
        runResult.add( resultToolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Run results", runResult );

        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.setBorder( BorderFactory.createLoweredBevelBorder() );

        contentPane.add( tabbedPane, BorderLayout.CENTER );

        statusBar = new StatusBar();

        contentPane.add( statusBar, BorderLayout.SOUTH );

        TextSearchDialog.attachTo(this, this);

        FontChangeEvent.addFontChangeListener( fontListener );
        
        setContentPane( contentPane );
        validate();
    }

    @Override
    public void dispose() {
        super.dispose();

        s_frames.remove( m_title );

        if ( jta_spec != null && jta_spec instanceof JavaColoredTextPane ) {
            ( (JavaColoredTextPane) jta_spec ).destroy();
            jta_spec = null;
        }

        if ( jta_generatedCode != null && jta_generatedCode instanceof JavaColoredTextPane ) {
            ( (JavaColoredTextPane) jta_generatedCode ).destroy();
            jta_generatedCode = null;
        }

        if ( m_lst != null ) {
            ProgramRunnerFeedbackEvent.unregisterListener( m_lst );
            m_lst = null;
        }

        FontChangeEvent.removeFontChangeListener( fontListener );
        
        ProgramRunnerEvent event = new ProgramRunnerEvent( this, m_progRunnerID, ProgramRunnerEvent.DESTROY );

        EventSystem.queueEvent( event );

    }

    public static JFrame getFrame( String packageName ) {
        return s_frames.get( packageName );
    }

    @Override
    public void actionPerformed( ActionEvent e ) {

        int op = -1;
        ProgramRunnerEvent evt = null;

        if ( ( e.getSource() == refreshSpec ) ) {

            evt = new ProgramRunnerEvent( this, m_progRunnerID, ProgramRunnerEvent.REQUEST_SPEC );

        } else if ( ( e.getSource() == computeGoal ) || ( e.getSource() == computeAll ) ) {

            op = ( e.getSource() == computeGoal ) ? ProgramRunnerEvent.COMPUTE_GOAL : ProgramRunnerEvent.COMPUTE_ALL;

            evt = new ProgramRunnerEvent( this, m_progRunnerID, op );

            evt.setSpecText( jta_spec.getText() );

            evt.setRequestFeedback( true );
        } else if ( ( e.getSource() == runProg ) || ( e.getSource() == invokeNew ) ) {

            op = ProgramRunnerEvent.RUN_NEW;

            if ( propagateOnRunNew.isSelected() ) {
                op |= ProgramRunnerEvent.PROPAGATE;
            }

            evt = new ProgramRunnerEvent( this, m_progRunnerID, op );

            evt.setProgramText( jta_generatedCode.getText() );

            evt.setRequestFeedback( true );
        } else if ( e.getSource() == propagate ) {

            op = ProgramRunnerEvent.PROPAGATE;

            evt = new ProgramRunnerEvent( this, m_progRunnerID, op );
        } else if ( e.getSource() == invoke ) {

            op = ProgramRunnerEvent.RUN | ProgramRunnerEvent.PROPAGATE;

            evt = new ProgramRunnerEvent( this, m_progRunnerID, op );

            try {
                evt.setRepeat( Integer.parseInt( invokeField.getText() ) );
            } catch ( NumberFormatException ee ) {
            }

            evt.setRequestFeedback( true );
        }

        if ( evt != null ) {
            EventSystem.queueEvent( evt );
        }
    }

    class ProgramRunnerFeedbackEventListener implements ProgramRunnerFeedbackEvent.Listener {

        @Override
        public void onProgramRunnerFeedbackEvent( final ProgramRunnerFeedbackEvent event ) {

            if ( event.getId() == m_progRunnerID ) {

                final int type = event.getType();

                SwingUtilities.invokeLater( new Runnable() {

                    @Override
                    public void run() {
                        if ( type == ProgramRunnerFeedbackEvent.TEXT_SPECIFICATION ) {

                            jta_spec.setText( event.getText() );
                            jta_spec.setCaretPosition( 0 );
                            tabbedPane.setSelectedComponent( specText );

                        } else if ( type == ProgramRunnerFeedbackEvent.TEXT_PROGRAM ) {

                            jta_generatedCode.setText( event.getText() );
                            jta_generatedCode.setCaretPosition( 0 );
                            tabbedPane.setSelectedComponent( progText );

                        } else if ( type == ProgramRunnerFeedbackEvent.TEXT_RESULT ) {

                            jta_runResult.append( event.getText() );
                            jta_runResult.setCaretPosition( jta_runResult.getDocument().getLength() );
                            tabbedPane.setSelectedComponent( runResult );
                        } else if ( type == ProgramRunnerFeedbackEvent.DISPOSE ) {
                            dispose();
                        } else if ( type == ProgramRunnerFeedbackEvent.WORKING ) {

                            boolean enable = !event.isWorking();
                            computeGoal.setEnabled( enable );
                            runProg.setEnabled( enable );
                            computeAll.setEnabled( enable );
                            propagate.setEnabled( enable );
                            invoke.setEnabled( enable );
                            invokeNew.setEnabled( enable );
                            refreshSpec.setEnabled( enable );
                            propagateOnRunNew.setEnabled( enable );

                            statusBar.setWorking( !enable );
                        }

                    }
                } );
            }
        }

    }

    private class StatusBar extends JPanel {

        private JLabel m_lblStatusColor;
        private JLabel m_lblMessage;
        private boolean isWorking = false;

        private StatusBar() {
            setLayout( new BorderLayout( 0, 0 ) );
            setBorder( BorderFactory.createRaisedBevelBorder() );

            JPanel colorPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            colorPanel.setBorder( BorderFactory.createLoweredBevelBorder() );

            m_lblStatusColor = new JLabel();
            m_lblStatusColor.setToolTipText( "Left-click here to abort the task." );
            m_lblStatusColor.addMouseListener( new MouseAdapter() {

                @Override
                public void mouseClicked( MouseEvent e ) {
                    if ( isWorking && SwingUtilities.isLeftMouseButton( e ) ) {
                        RunningThreadManager.removeThread( m_progRunnerID, true );
                    }
                }
            } );

            m_lblStatusColor.setPreferredSize( new Dimension( 16, 16 ) );
            m_lblStatusColor.setOpaque( true );
            m_lblStatusColor.setBorder( BorderFactory.createEmptyBorder() );
            m_lblStatusColor.setBackground( Color.green );
            colorPanel.add( m_lblStatusColor );
            add( colorPanel, BorderLayout.WEST );

            JPanel messagePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            messagePanel.setBorder( BorderFactory.createLoweredBevelBorder() );
            m_lblMessage = new JLabel( " " );
            m_lblMessage.setPreferredSize( new Dimension( 100, 16 ) );
            messagePanel.add( m_lblMessage );
            add( messagePanel, BorderLayout.CENTER );
        }

        void setWorking( boolean working ) {

            isWorking = working;

            if ( working ) {
                m_lblStatusColor.setBackground( Color.red );
                m_lblMessage.setText( " Working..." );
            } else {
                m_lblStatusColor.setBackground( Color.green );
                m_lblMessage.setText( " " );
            }
        }
    }

    static class CommentKeyListener implements KeyListener {

        @Override
        public void keyTyped( KeyEvent e ) {
        }

        @Override
        public void keyPressed( KeyEvent e ) {
        }

        @Override
        public void keyReleased( KeyEvent e ) {
            if ( e.getKeyChar() == '/' && ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 ) && ( e.getSource() instanceof JTextArea ) ) {

                JTextArea area = (JTextArea) e.getSource();

                try {
                    int line = area.getLineOfOffset( area.getCaretPosition() );
                    int start = area.getLineStartOffset( line );
                    int end = area.getLineEndOffset( line );
                    int length = end - start;
                    String text = area.getText( start, length );

                    if ( text.trim().startsWith( "//" ) ) {
                        int ind = text.indexOf( "//" );
                        area.replaceRange( "", start + ind, start + ind + 2 );
                    } else {
                        area.insert( "//", start );
                    }

                    area.setCaretPosition( area.getLineEndOffset( area.getLineOfOffset( area.getCaretPosition() ) ) );

                } catch ( BadLocationException ex ) {
                }
            } else if ( e.getKeyChar() == '/' && ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 )
                    && ( e.getSource() instanceof JTextComponent ) ) {

                try {
                    JTextComponent comp = (JTextComponent) e.getSource();
                    Document doc = comp.getDocument();

                    Element map = doc.getDefaultRootElement();

                    int line = map.getElementIndex( comp.getCaretPosition() );

                    int lineCount = map.getElementCount();

                    Element lineElem = map.getElement( line );
                    int start = lineElem.getStartOffset();

                    int endOffset = lineElem.getEndOffset();
                    int end = ( ( line == lineCount - 1 ) ? ( endOffset - 1 ) : endOffset );
                    int length = end - start;

                    String text = doc.getText( start, length );

                    if ( text.trim().startsWith( "//" ) ) {
                        int ind = text.indexOf( "//" );

                        int ss = start + ind;
                        int ee = start + ind + 2;

                        if ( doc instanceof AbstractDocument ) {
                            ( (AbstractDocument) doc ).replace( ss, ee - ss, "", null );
                        } else {
                            doc.remove( ss, ee - ss );
                            doc.insertString( ss, "", null );
                        }

                    } else {
                        doc.insertString( start, "//", null );
                    }

                    int l = map.getElementIndex( comp.getCaretPosition() );
                    Element le = map.getElement( l );
                    int eo = le.getEndOffset();

                    comp.setCaretPosition( ( ( l == lineCount - 1 ) ? ( eo - 1 ) : eo ) );

                } catch ( BadLocationException e1 ) {
                    e1.printStackTrace();
                }
            }
        }

    }

    /**
     * Returns the text component in the selected tab.
     */
    @Override
    public JTextComponent getTextComponent() {
        switch (tabbedPane.getSelectedIndex()) {
        case 0:
            return jta_spec;
        case 1:
            return jta_generatedCode;
        case 2:
            return jta_runResult;
        default:
            return null;
        }
    }

    /**
     * Returns the root window.
     */
    @Override
    public Window getWindow() {
        return this;
    }

}

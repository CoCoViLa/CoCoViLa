package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import javax.swing.text.*;

import ee.ioc.cs.vsle.event.*;

/**
 * This class contains only GUI and interacts with ProgrammRunner via ProgramRunnerEvents
 */
public class ProgramTextEditor extends JFrame implements ActionListener {

	private ProgramRunnerFeedbackEventListener m_lst = new ProgramRunnerFeedbackEventListener();
	
    private JButton computeGoal, runProg, computeAll, propagate, invoke, invokeNew, refreshSpec;
    private JTextArea jta_runResult;
    private JTextComponent jta_spec, jta_generatedCode;
    
    private JPanel progText, specText, runResult;
    private JTextField invokeField;
    private JTabbedPane tabbedPane;
    
    private long m_progRunnerID;
    
    private String m_packageName;
    
    private static HashMap<String, JFrame> s_frames = new HashMap<String, JFrame>();
    
    public ProgramTextEditor( long prunnerID, String packageName ) {
    	
        super( packageName + " - Specification" );
        
        s_frames.put( packageName, this );
        
        m_packageName = packageName;
        
        ProgramRunnerFeedbackEvent.registerListener( m_lst );
        
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        
        m_progRunnerID = prunnerID;
        
        setLocationByPlatform( true );
        
        initUI();
        
        SwingUtilities.invokeLater( new Runnable() {

        	public void run() {
        		ProgramRunnerEvent evt = new ProgramRunnerEvent( this, 
        				m_progRunnerID, 
        				ProgramRunnerEvent.REQUEST_SPEC );

        		EventSystem.queueEvent( evt );
        	}} 
        );
    	
    }

    private void initUI() {
    	
    	tabbedPane = new JTabbedPane();

        if( RuntimeProperties.isSyntaxHighlightingOn ) {
        	jta_spec = new JavaColoredTextPane();
        } else {
        	jta_spec = new JTextArea();
        }
        
        jta_spec.addKeyListener( new CommentKeyListener() );
        jta_spec.setFont( RuntimeProperties.font );
        JScrollPane areaScrollPane = new JScrollPane( jta_spec );

        areaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

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
        
        progToolBar.add( computeAll );
        progToolBar.add( new UndoRedoDocumentPanel( jta_spec.getDocument() ) );
        refreshSpec = new JButton( "Update from Scheme" );
        refreshSpec.addActionListener( this );
        progToolBar.add( refreshSpec );
        progToolBar.add( new FontResizePanel( jta_spec ) );

        specText.add( progToolBar, BorderLayout.NORTH );
        tabbedPane.addTab( "Specification", specText );

        if( RuntimeProperties.isSyntaxHighlightingOn ) {
        	jta_generatedCode = new JavaColoredTextPane();
        } else {
        	jta_generatedCode = new JTextArea();
        }
        
        jta_generatedCode.addKeyListener( new CommentKeyListener() );
        jta_generatedCode.setFont( RuntimeProperties.font );
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        runProg = new JButton( "Compile & Run" );
        runProg.addActionListener( this );
        toolBar.add( runProg );
        toolBar.add( new UndoRedoDocumentPanel( jta_generatedCode.getDocument() ) );
        toolBar.add( new FontResizePanel( jta_generatedCode ) );
        JScrollPane programAreaScrollPane = new JScrollPane( jta_generatedCode );

        programAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        
        programAreaScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        progText = new JPanel();
        progText.setLayout( new BorderLayout() );
        progText.add( programAreaScrollPane, BorderLayout.CENTER );
        progText.add( toolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Program", progText );

        jta_runResult = new JTextArea();
        jta_runResult.setFont( RuntimeProperties.font );
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

        runResultAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        runResult = new JPanel();
        runResult.setLayout( new BorderLayout() );
        runResult.add( runResultAreaScrollPane, BorderLayout.CENTER );
        runResult.add( resultToolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Run results", runResult );
        
        getContentPane().add( tabbedPane );
        validate();
    }
    
    public void dispose() {
    	super.dispose();
    	
    	s_frames.remove( m_packageName );
    	
    	if( jta_spec != null && jta_spec instanceof JavaColoredTextPane ) {
    		((JavaColoredTextPane)jta_spec).destroy();
    		jta_spec = null;
    	}
    	
    	if( jta_generatedCode != null && jta_generatedCode instanceof JavaColoredTextPane ) {
    		((JavaColoredTextPane)jta_generatedCode).destroy();
    		jta_generatedCode = null;
    	}
    	
    	if( m_lst != null ) {
    		ProgramRunnerFeedbackEvent.unregisterListener( m_lst );
    		m_lst = null;
    	}
    	
    }
    
    public static JFrame getFrame( String packageName ) {
    	return s_frames.get( packageName );
    }
    
    public void actionPerformed( ActionEvent e ) {
    	
    	int op = -1;
    	ProgramRunnerEvent evt = null;
    	
    	if ( ( e.getSource() == refreshSpec ) ) {
    		
    		evt = new ProgramRunnerEvent( this, m_progRunnerID, ProgramRunnerEvent.REQUEST_SPEC );
    		
    	} else if ( ( e.getSource() == computeGoal ) || ( e.getSource() == computeAll ) ) {    
        	
        	op = ( e.getSource() == computeGoal )
        		? ProgramRunnerEvent.COMPUTE_GOAL
        		: ProgramRunnerEvent.COMPUTE_ALL;

        	evt = new ProgramRunnerEvent( this, m_progRunnerID, op );

        	evt.setSpecText( jta_spec.getText() );
	
        	evt.setRequestFeedback( true );
        }
        else if ( ( e.getSource() == runProg ) || ( e.getSource() == invokeNew ) ) {
        	
        	op = ProgramRunnerEvent.RUN_NEW;

        	evt = new ProgramRunnerEvent( this, m_progRunnerID, op );

        	evt.setProgramText( jta_generatedCode.getText() );
    	
        	evt.setRequestFeedback( true );
        }
        else if ( e.getSource() == propagate ) {
        	
        	op = ProgramRunnerEvent.PROPAGATE;

        	evt = new ProgramRunnerEvent( this, m_progRunnerID, op );
        }
        else if ( e.getSource() == invoke ) {
        	
        	op = ProgramRunnerEvent.RUN | ProgramRunnerEvent.PROPAGATE;

        	evt = new ProgramRunnerEvent( this, m_progRunnerID, op );
        	
        	try {
        		evt.setRepeat( Integer.parseInt( invokeField.getText() ) );
        	} catch( NumberFormatException ee ) {}
        	
        	evt.setRequestFeedback( true );
        }
        
        if( evt != null ) {
        	EventSystem.queueEvent( evt );
        }
    }

    class ProgramRunnerFeedbackEventListener implements ProgramRunnerFeedbackEvent.Listener {

    	public void onProgramRunnerFeedbackEvent( ProgramRunnerFeedbackEvent event) {

    		if( event.getId() == m_progRunnerID ) {
    			
    			final int type = event.getType();
    			final String text = event.getText();
    			
    			SwingUtilities.invokeLater( new Runnable() {

    				public void run() {
    					if( type == ProgramRunnerFeedbackEvent.TEXT_SPECIFICATION ) {

    						jta_spec.setText( text );
    						tabbedPane.setSelectedComponent( specText );

    					} else if( type == ProgramRunnerFeedbackEvent.TEXT_PROGRAM ) {

    						jta_generatedCode.setText( text );
    						tabbedPane.setSelectedComponent( progText );

    					} else if( type == ProgramRunnerFeedbackEvent.TEXT_RESULT ) {

    						jta_runResult.append( text );
    						tabbedPane.setSelectedComponent( runResult );
    					}
    				}} 
    			);
    		}
    	}

    }
    
    static class CommentKeyListener implements KeyListener {

        public void keyTyped( KeyEvent e ) {
        }


        public void keyPressed( KeyEvent e ) {
        }


        public void keyReleased( KeyEvent e ) {
            if ( e.getKeyChar() == '/'
                 && ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 )
                 && ( e.getSource() instanceof JTextArea ) ) {

                JTextArea area = ( JTextArea ) e.getSource();

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

                    area.setCaretPosition( area.getLineEndOffset(
                            area.getLineOfOffset( area.getCaretPosition() ) ) );

                } catch ( BadLocationException ex ) {
                }
            }
            else if(e.getKeyChar() == '/'
                && ( ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 )
                && ( e.getSource() instanceof JTextComponent )) {
            	            	
            	try {
            		JTextComponent comp = (JTextComponent)e.getSource();
                	Document doc = comp.getDocument();
                	
                	Element map = doc.getDefaultRootElement();
                	
                	int line = map.getElementIndex(comp.getCaretPosition());
                	
                	int lineCount = map.getElementCount();
                	
                	Element lineElem = map.getElement(line);
                	int start = lineElem.getStartOffset();
                	
                	int endOffset = lineElem.getEndOffset();
                	int end = ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
                	int length = end - start;
                	
					String text = doc.getText( start, length );
					
					if ( text.trim().startsWith( "//" ) ) {
                        int ind = text.indexOf( "//" );
                        
                        int ss = start + ind;
                        int ee = start + ind + 2;
                        
                        if (doc instanceof AbstractDocument) {
                            ((AbstractDocument)doc).replace(ss, ee - ss, "",
                                                            null);
                        }
                        else {
                            doc.remove(ss, ee - ss);
                            doc.insertString(ss, "", null);
                        }

                    } else {
                    	doc.insertString(start, "//", null);
                    }
					
					int l = map.getElementIndex(comp.getCaretPosition());
					Element le = map.getElement(l);
		            int eo = le.getEndOffset();

		            
					comp.setCaretPosition(((l == lineCount - 1) ? (eo - 1) : eo));
					
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
            }
        }

    }

}

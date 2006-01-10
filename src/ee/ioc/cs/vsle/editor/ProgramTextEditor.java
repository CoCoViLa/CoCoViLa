package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.text.*;

/**
 */
public class ProgramTextEditor extends JFrame implements ActionListener {

    private JButton computeGoal, runProg, computeAll, propagate, invoke, invokeNew;
    private JTextArea jta_runResult;
    private JavaColoredTextPane jta_spec, jta_generatedCode;
    
    private JPanel progText, specText, runResult;
    private JTextField invokeField;
    private JTabbedPane tabbedPane;
    
    private ProgramRunner runner;
    
    private Editor editor;

    public ProgramTextEditor( Editor ed, ProgramRunner prunner ) {
        super( "Specification" );
        
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        
        editor = ed;

        this.runner = prunner;
        
        tabbedPane = new JTabbedPane();

        jta_spec = new JavaColoredTextPane();
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
        progToolBar.add( new FontResizePanel( jta_spec ) );

        specText.add( progToolBar, BorderLayout.NORTH );
        tabbedPane.addTab( "Specification", specText );

        jta_generatedCode = new JavaColoredTextPane();
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

        jta_spec.append( runner.getSpec() );

        getContentPane().add( tabbedPane );
        validate();
    }

    public void dispose() {
    	super.dispose();
    	
    	if( jta_spec != null) {
    		jta_spec.destroy();
    		jta_spec = null;
    	}
    	
    	if( jta_generatedCode != null) {
    		jta_generatedCode.destroy();
    		jta_generatedCode = null;
    	}
    	
    }
    public void actionPerformed( ActionEvent e ) {
        if ( ( e.getSource() == computeGoal ) || ( e.getSource() == computeAll ) ) {            
        	String res = runner.compute( jta_spec.getText(), e.getSource() == computeAll );
        	if( res != null ) {
        		jta_generatedCode.setText( res );
        		tabbedPane.setSelectedComponent( progText );
        	}
        	validate();
        }
        else if ( e.getSource() == runProg ) {
        	String result = runner.compileAndRun( jta_generatedCode.getText() );
        	if ( result != null ) {
        		jta_runResult.setText( result );
                tabbedPane.setSelectedComponent( runResult );
            }
        }
        else if ( e.getSource() == propagate ) {
        	runner.runPropagate();
        	editor.repaint();
        }
        else if ( e.getSource() == invoke ) {
        	jta_runResult.append( runner.invoke( invokeField.getText() ) );
        }
        else if ( e.getSource() == invokeNew ) {
        	jta_runResult.append( runner.invokeNew( invokeField.getText() ) );
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

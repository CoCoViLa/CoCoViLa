package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.ccl.*;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import javax.swing.text.*;

/**
 */
public class ProgramTextEditor extends JFrame implements ActionListener {

    JButton computeGoal, runProg, computeAll, propagate, invoke;
    JTextArea runResultArea;
    JavaColoredTextPane textArea, programTextArea;
    
    JPanel progText, specText, runResult;
    JTextField invokeField;
    VPackage vPackage;
    JTabbedPane tabbedPane;
    ObjectList objects;
    Object runnableObject;
    ProgramRunner runner;
    ClassList classList;
    String mainClassName = new String();
    Editor editor;

    public ProgramTextEditor( ArrayList relations, ObjectList objs, VPackage vPackage, Editor ed ) {
        super( "Specification" );
        editor = ed;
        this.vPackage = vPackage;
        objects = GroupUnfolder.unfold( objs );

        tabbedPane = new JTabbedPane();

        textArea = new JavaColoredTextPane();
        textArea.addKeyListener( new CommentKeyListener() );
        textArea.setFont( RuntimeProperties.font );
        JScrollPane areaScrollPane = new JScrollPane( textArea );

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
        progToolBar.add( new UndoRedoDocumentPanel( textArea.getDocument() ) );
        progToolBar.add( new FontResizePanel( textArea ) );

        specText.add( progToolBar, BorderLayout.NORTH );
        tabbedPane.addTab( "Specification", specText );

        programTextArea = new JavaColoredTextPane();
        programTextArea.addKeyListener( new CommentKeyListener() );
        programTextArea.setFont( RuntimeProperties.font );
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        runProg = new JButton( "Compile & Run" );
        runProg.addActionListener( this );
        toolBar.add( runProg );
        toolBar.add( new UndoRedoDocumentPanel( programTextArea.getDocument() ) );
        toolBar.add( new FontResizePanel( programTextArea ) );
        JScrollPane programAreaScrollPane = new JScrollPane( programTextArea );

        programAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        progText = new JPanel();
        progText.setLayout( new BorderLayout() );
        progText.add( programAreaScrollPane, BorderLayout.CENTER );
        progText.add( toolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Program", progText );

        runResultArea = new JTextArea();
        runResultArea.setFont( RuntimeProperties.font );
        JToolBar resultToolBar = new JToolBar();

        propagate = new JButton( "Propagate values" );
        propagate.addActionListener( this );
        resultToolBar.add( propagate );
        invoke = new JButton( "Invoke" );
        invoke.addActionListener( this );
        resultToolBar.add( invoke );
        invokeField = new JTextField( 4 );
        resultToolBar.add( invokeField );
        JScrollPane runResultAreaScrollPane = new JScrollPane( runResultArea );

        runResultAreaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

        runResult = new JPanel();
        runResult.setLayout( new BorderLayout() );
        runResult.add( runResultAreaScrollPane, BorderLayout.CENTER );
        runResult.add( resultToolBar, BorderLayout.NORTH );

        tabbedPane.addTab( "Run results", runResult );

        ISpecGenerator sgen = SpecGenFactory.getInstance().getCurrentSpecGen();

        textArea.append( sgen.generateSpec( objects, relations, vPackage ) );

        getContentPane().add( tabbedPane );
        validate();
    }


    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == computeGoal ) {            
        	compute( false );
        }
        else if ( e.getSource() == computeAll ) {
        	compute( true );            
        }
        else if ( e.getSource() == runProg ) {
        	compileAndRun();
        }
        else if ( e.getSource() == propagate ) {
        	propagate();
        }
        else if ( e.getSource() == invoke ) {
        	invoke();
        }
    }

    void compileAndRun()
    {
    	Synthesizer.getInstance().makeProgram( programTextArea.getText(), classList,
                           mainClassName );
        runner = new ProgramRunner();
        ArrayList<String> watchFields = watchableFields( objects );

        try {
            runnableObject = runner.compileAndRun( mainClassName,
                    watchFields, runResultArea );
            if ( runnableObject != null ) {
                tabbedPane.setSelectedComponent( runResult );
            }
        } catch ( CompileException ce ) {
            ErrorWindow.showErrorMessage(
                    "Compilation failed:\n " + ce.excDesc );
        }
    }
    
    void invoke()
    {
    	ArrayList<String> watchFields = watchableFields( objects );

        if ( runnableObject != null ) {
            if ( !invokeField.getText().equals( "" ) ) {
                int k = Integer.parseInt( invokeField.getText() );

                for ( int i = 0; i < k; i++ ) {
                    runner.run( watchFields, runResultArea );
                }
            } else {
                runner.run( watchFields, runResultArea );
            }
            
            propagate();
        }
    }
    
    void propagate()
    {
    	db.p( "propageerin" );
        if ( runnableObject != null ) {
            runner.runPropagate( runnableObject, objects );
        }
        editor.repaint();
    }
    
    void compute()
    {
    	compute( true );
    }
    
    private void compute( boolean computeAll ) {
    	
        try {
            String fullSpec = textArea.getText();

            mainClassName = SpecParser.getInstance().getClassName( fullSpec );
            
            classList = SpecParser.getInstance().parseSpecification( fullSpec );
            programTextArea.setText( "" );
            programTextArea.append(
            		Synthesizer.getInstance().makeProgramText( fullSpec, computeAll, classList, mainClassName ) );
            tabbedPane.setSelectedComponent( progText );
        } catch ( UnknownVariableException uve ) {

            db.p( "Fatal error: variable " + uve.excDesc + " not declared" );
            ErrorWindow.showErrorMessage(
                    "Fatal error: variable " + uve.excDesc + " not declared" );

        } catch ( LineErrorException lee ) {
            db.p( "Fatal error on line " + lee.excDesc );
            ErrorWindow.showErrorMessage(
                    "Syntax error on line '" + lee.excDesc + "'" );

        } catch ( EquationException ee ) {
            ErrorWindow.showErrorMessage( ee.excDesc );

        } catch ( MutualDeclarationException lee ) {
            db.p(
                    "Mutual recursion in specifications, between classes "
                    + lee.excDesc );
            ErrorWindow.showErrorMessage(
                    "Mutual recursion in specifications, classes " + lee.excDesc );

        } catch ( SpecParseException spe ) {
            db.p( spe.excDesc );
            ErrorWindow.showErrorMessage( spe.excDesc );

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        validate();
    }
    
    private ArrayList<String> watchableFields( ObjectList objects ) {
        ClassField field;
        GObj obj;

        objects = GroupUnfolder.unfold( objects );
        ArrayList<String> watchFields = new ArrayList<String>();

        for ( int i = 0; i < objects.size(); i++ ) {
            obj = ( GObj ) objects.get( i );
            for ( int j = 0; j < obj.fields.size(); j++ ) {
                field = ( ClassField ) obj.fields.get( j );
                if ( field.isWatched() ) {
                    watchFields.add( obj.name + "." + field.getName() );
                }
            }
        }
        return watchFields;
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

package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

import ee.ioc.cs.vsle.util.*;

/**
 * Source code viewer and editor for package component metaclasses.
 */
public class CodeViewer extends JFrame implements ActionListener,
        PropertyChangeListener {

    public enum MODE {
        FILE, SPEC
    }
    
    // OS X specific properties
    private static final String DOC_FILE = "Window.documentFile";
    private static final String MODIFIED = "Window.documentModified";

    private JTextComponent		textArea;
    private JPanel				specText;
    private File				openFile;
    private UndoRedoDocumentPanel undoRedo;
    
    private FontChangeEvent.Listener fontListener = new FontChangeEvent.Listener() {
        @Override
        public void fontChanged( FontChangeEvent e ) {
            if( e.getElement() == RuntimeProperties.Fonts.CODE ) {
                getTextArea().setFont( e.getFont() );
            }
        }
    };
    
    private MODE mode = MODE.FILE;
    private ISpecExtendable extendable;
    /**
     * Returns the main text component for editing the source code.
     * @return the main text component
     */
    JTextComponent getTextArea() {
        return textArea;
    }

    private CodeViewer(File file) {
        super();
        initLayout();
        openFile( file );
    }
    
	private CodeViewer(String name, String path, String extension) {
		this(new File( path + File.separator + name + extension ));
	}

	public CodeViewer(String name, String path) {
		this(name, path, ".java");
	}
	
	public CodeViewer(ISpecExtendable extendable) {
	    super(extendable.getTitle());
	    this.extendable = extendable;
	    mode = MODE.SPEC;
	    initLayout();
	    getTextArea().setText( extendable.getSpecText() );
	    markSaved();
	    open();
    }

	public void open() {
        setPreferredSize(new Dimension(550, 450));
        setMinimumSize(getMinimumSize());
        pack();
        setVisible(true);        
    }
	
	private void initLayout() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);

		if( RuntimeProperties.isSyntaxHighlightingOn() ) {
			textArea = SyntaxDocument.createEditor();
        } else {
        	JTextArea ta = new JTextArea();
        	ta.setLineWrap( true );
        	ta.setWrapStyleWord( true );
        	textArea = ta;
        }
		
		textArea.addKeyListener(new ProgramTextEditor.CommentKeyListener());
		textArea.setFont(RuntimeProperties.getFont(RuntimeProperties.Fonts.CODE));
		textArea.addMouseListener( new JavaClassOpener() );
		
		FontChangeEvent.addFontChangeListener( fontListener );
		
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setRowHeaderView(new LineNumberView(textArea));
		areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);

		getContentPane().add(specText);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem new_ = new JMenuItem( "New" );
		new_.setActionCommand( "New" );
		new_.addActionListener(this);
		new_.setEnabled( mode == MODE.FILE );
		fileMenu.add(new_);
		
		JMenuItem open = new JMenuItem( "Open..." );
		open.setActionCommand( "Open" );
		open.addActionListener(this);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
		        InputEvent.CTRL_DOWN_MASK));
		open.setEnabled( mode == MODE.FILE );
		fileMenu.add(open);
		
		fileMenu.add( new JSeparator() );
		
		JMenuItem save = new JMenuItem( "Save" );
		save.setActionCommand( "Save" );
		save.addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		        InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(save);
		
		JMenuItem saveAs = new JMenuItem( "Save As..." );
		saveAs.setActionCommand( "SaveAs" );
		saveAs.addActionListener(this);
		saveAs.setEnabled( mode == MODE.FILE );
		fileMenu.add(saveAs);
		
		fileMenu.add( new JSeparator() );
		
		JMenuItem close = new JMenuItem( "Close" );
		close.setActionCommand( "Close" );
		close.addActionListener(this);
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
		        InputEvent.CTRL_DOWN_MASK));

		fileMenu.add(close);
		
		menuBar.add(fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);

		JMenuItem find = new JMenuItem("Find...");
		find.addActionListener(this);
		find.setMnemonic(KeyEvent.VK_F);
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
		        InputEvent.CTRL_DOWN_MASK));
		editMenu.add(find);

		undoRedo = new UndoRedoDocumentPanel(textArea.getDocument(),
		        this);

		undoRedo.addPropertyChangeListener(this);
		editMenu.add(undoRedo.getUndoAction());
		editMenu.add(undoRedo.getRedoAction());

		menuBar.add(editMenu);

		setJMenuBar( menuBar );
	}
	
	private void openFile( File file ) {
	    if (openFile != null && undoRedo != null 
	            && !undoRedo.isSaved() && !confirmClose()) {
	        return;
	    }

	    this.openFile = file;
	    getRootPane().putClientProperty(DOC_FILE, file);

		setTitle(file.getAbsolutePath());
		
		final String fileText = FileFuncs.getFileContents(file);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
			    JTextComponent ta = getTextArea();
			    ta.setText(fileText);
			    ta.setCaretPosition( 0 );
			    ta.requestFocusInWindow();

			    // Assume the document is saved initially
			    // and the already existing edit history does not
			    // make sense for the new content, hence it is
			    // discarded.
			    markSaved();
			    discardAllEdits();
			}
		} );
	}

	void markSaved() {
	    undoRedo.markSaved();
	}

	void discardAllEdits() {
	    undoRedo.discardAllEdits();
	}

	private boolean saveFile( boolean saveAs ) {
	    
	    if(mode == MODE.SPEC) {
            extendable.setSpecText( textArea.getText() );
            markSaved();
            return true;
        }
	    
	    File openFileNew = openFile;

		if( saveAs ) {
			JFileChooser fc = new JFileChooser( ( openFile != null ) ? openFile.getParent() : null );
			fc.setSelectedFile( openFile );
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );

			if ( fc.showSaveDialog( CodeViewer.this ) == JFileChooser.APPROVE_OPTION ) {
			    openFileNew = fc.getSelectedFile();
			} else {
				return false;
			}
		}

		if (FileFuncs.writeFile(openFileNew, textArea.getText())) {
		    openFile = openFileNew;
		    setTitle(openFile.getAbsolutePath());
		    markSaved();
		    return true;
		}

		JOptionPane.showMessageDialog(this,
		        "Saving to file \"" + openFileNew + "\" failed.\n" +
		        "Make sure the disk is not full and " +
		        "file permissions are correct,\n" +
		        "or try saving with a different file name.",
		        "Error Saving File", JOptionPane.ERROR_MESSAGE);

		return false;
	}

	public void actionPerformed(ActionEvent e) {
		
		if ( e.getActionCommand().equals( "New" ) ) {
			textArea.setText("");
			setTitle( "" );
			JFileChooser fc = new JFileChooser( ( openFile != null ) ? openFile.getParent() : null );
			openFile = null;
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );

			if ( fc.showSaveDialog( CodeViewer.this ) == JFileChooser.APPROVE_OPTION ) {
				this.openFile = fc.getSelectedFile();
				setTitle( openFile.getAbsolutePath() );
			}			
		} else if ( e.getActionCommand().equals( "Open" ) ) {
			
			JFileChooser fc = new JFileChooser( ( openFile != null ) ? openFile.getParent() : null );
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			
			if ( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {

                File file = fc.getSelectedFile();
                openFile( file );
			}
			
		} else if ( e.getActionCommand().equals( "Save" ) ) {
			
			saveFile( false );
			
		} else if ( e.getActionCommand().equals( "SaveAs" ) ) {
			
			saveFile( true );
			
		} else if ( e.getActionCommand().equals( "Close" ) ) {
		    if (confirmClose()) {
			dispose();
		    }
		} else if ("Find...".equals(e.getActionCommand())) {
		    TextSearchDialog.showDialog(this, textArea);
		}
	}

	@Override
	public void dispose() {
		if (textArea != null) {
		    FontChangeEvent.removeFontChangeListener( fontListener );
		    fontListener = null;
			textArea = null;
		}
		super.dispose();
	}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (UndoRedoDocumentPanel.PROPERTY_SAVED.equals(evt.getPropertyName())) {
            String title;
            if (mode == MODE.FILE && openFile != null) {
                title = openFile.getAbsolutePath();
            } else if(mode == MODE.SPEC) {
                title = extendable.getTitle();
            }
            else return;
            // Put an asterisk in front of the title for unsaved files

            if (Boolean.TRUE.equals(evt.getNewValue())) {
                setTitle(title);
                getRootPane().putClientProperty(MODIFIED, Boolean.FALSE);
            } else {
                setTitle("*" + title);
                getRootPane().putClientProperty(MODIFIED, Boolean.TRUE);
            }
        }
    }

    /**
     * Confirm close action. If the file is saved, true is returned.
     * Otherwise the user is prompted for a Yes/No/Cancel answer.  True
     * is returned if the user was able to save the file or chose not to save.
     * @return true if it is ok to close, false otherwise
     */
    boolean confirmClose() {
        boolean rv = true; // close silently if there are no changes

        if (undoRedo != null && !undoRedo.isSaved()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "The file " 
                    + (openFile == null ? "" : "'" + openFile.getName() + "' ")
                    + "has been modified. Save changes?",
                    "Save File", JOptionPane.YES_NO_CANCEL_OPTION);

            switch (answer) {
            case JOptionPane.YES_OPTION:
                rv = saveFile(false);
                break;
            case JOptionPane.NO_OPTION:
                rv = true;
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                rv = false;
                break;
            }
        }
        return rv;
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        // ask confirmation when window is closed with unsaved content
        if (WindowEvent.WINDOW_CLOSING != e.getID() || confirmClose()) {
            super.processWindowEvent(e);
        }
    }
    
    private class JavaClassOpener extends MouseAdapter {

        @Override
        public void mouseClicked( MouseEvent e ) {
            
            if(SwingUtilities.isRightMouseButton( e )
                    && e.getClickCount() == 1
                    && ( e.getModifiers() & KeyEvent.CTRL_MASK ) > 0 ) {
                
                String text = textArea.getSelectedText();
                if( StringUtil.isJavaIdentifier( text ) && openFile != null ) {
                    
                    File newFile = new File( openFile.getParent(), text + ".java" );
                    if(newFile.exists()) {
                        if(( e.getModifiers() & KeyEvent.SHIFT_MASK ) > 0)
                            new CodeViewer( newFile ).open();
                        else
                            openFile( newFile );
                    }
                }
            }
        }
    }
}

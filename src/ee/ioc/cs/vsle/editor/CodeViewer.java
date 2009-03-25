package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Source code viewer and editor for package component metaclasses.
 */
public class CodeViewer extends JFrame implements ActionListener {

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

    /**
     * Returns the main text component for editing the source code.
     * @return the main text component
     */
    JTextComponent getTextArea() {
        return textArea;
    }

	private CodeViewer(String name, String path, String extension) {
		super();
		initLayout();
		openFile( new File( path + name + extension ) );
	}

	public CodeViewer(String name, String path) {
		this(name, path, ".java");
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
		fileMenu.add(new_);
		
		JMenuItem open = new JMenuItem( "Open..." );
		open.setActionCommand( "Open" );
		open.addActionListener(this);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
		        InputEvent.CTRL_DOWN_MASK));
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

		undoRedo = new UndoRedoDocumentPanel(textArea.getDocument());
		editMenu.add(undoRedo.getUndoAction());
		editMenu.add(undoRedo.getRedoAction());

		menuBar.add(editMenu);

		setJMenuBar( menuBar );
	}
	
	private void openFile( File file ) {
		this.openFile = file;
		setTitle(file.getAbsolutePath());
		
		final String fileText = FileFuncs.getFileContents(file);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
			    JTextComponent ta = getTextArea();
			    ta.setText(fileText);
			    ta.setCaretPosition( 0 );
			    ta.requestFocusInWindow();
			}
		} );
	}
	
	private void saveFile( boolean saveAs ) {
	    File openFileNew = openFile;

		if( saveAs ) {
			JFileChooser fc = new JFileChooser( ( openFile != null ) ? openFile.getParent() : null );
			fc.setSelectedFile( openFile );
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );

			if ( fc.showSaveDialog( CodeViewer.this ) == JFileChooser.APPROVE_OPTION ) {
			    openFileNew = fc.getSelectedFile();
			} else {
				return;
			}
		}

		if (FileFuncs.writeFile(openFileNew, textArea.getText())) {
		    openFile = openFileNew;
		    setTitle(openFile.getAbsolutePath());
		    //undoRedo.markSaved();
		} else {
		    JOptionPane.showMessageDialog(this,
		            "Saving to file \"" + openFileNew + "\" failed.\n" +
		            "Make sure the disk is not full and " +
		            "file permissions are correct,\n" +
		            "or try saving with a different file name.",
		            "Error Saving File", JOptionPane.ERROR_MESSAGE);
		}
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
			dispose();
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
}

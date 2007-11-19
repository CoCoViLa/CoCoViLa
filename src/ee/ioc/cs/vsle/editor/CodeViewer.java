package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA. User: Ando Date: 28.03.2005 Time: 21:12:15
 */
public class CodeViewer extends JFrame implements ActionListener {

	JTextComponent		textArea;
	JPanel				specText;
	File				file;

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
		
		addComponentListener(new ComponentResizer(ComponentResizer.CARE_FOR_MINIMUM));

		if( RuntimeProperties.isSyntaxHighlightingOn() ) {
			textArea = SyntaxDocument.createEditor();
        } else {
        	JTextArea ta = new JTextArea();
        	ta.setLineWrap( true );
        	ta.setWrapStyleWord( true );
        	textArea = ta;
        }
		
		textArea.addKeyListener(new ProgramTextEditor.CommentKeyListener());
		textArea.setFont(RuntimeProperties.getFont());
		
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setRowHeaderView(new LineNumberView(textArea));
		areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBar.add(new FontResizePanel(textArea));
		toolBar.add(new UndoRedoDocumentPanel(textArea.getDocument()));

		specText.add(toolBar, BorderLayout.NORTH);

		getContentPane().add(specText);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem new_ = new JMenuItem( "New" );
		new_.setActionCommand( "New" );
		new_.addActionListener(this);
		fileMenu.add(new_);
		
		JMenuItem open = new JMenuItem( "Open..." );
		open.setActionCommand( "Open" );
		open.addActionListener(this);
		fileMenu.add(open);
		
		fileMenu.add( new JSeparator() );
		
		JMenuItem save = new JMenuItem( "Save" );
		save.setActionCommand( "Save" );
		save.addActionListener(this);
		fileMenu.add(save);
		
		JMenuItem saveAs = new JMenuItem( "Save As..." );
		saveAs.setActionCommand( "SaveAs" );
		saveAs.addActionListener(this);
		fileMenu.add(saveAs);
		
		fileMenu.add( new JSeparator() );
		
		JMenuItem close = new JMenuItem( "Close" );
		close.setActionCommand( "Close" );
		close.addActionListener(this);
		fileMenu.add(close);
		
		menuBar.add(fileMenu);
		setJMenuBar( menuBar );
	}
	
	private void openFile( File file ) {
		this.file = file;
		setTitle(file.getAbsolutePath());
		
		final String fileText = FileFuncs.getFileContents(file);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				textArea.setText(fileText);
				textArea.setCaretPosition( 0 );
				textArea.requestFocusInWindow();
			}
		} );
	}
	
	private void saveFile( boolean saveAs ) {
		
		if( saveAs ) {
			JFileChooser fc = new JFileChooser( ( file != null ) ? file.getParent() : null );
			fc.setSelectedFile( file );
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );

			if ( fc.showSaveDialog( CodeViewer.this ) == JFileChooser.APPROVE_OPTION ) {
				this.file = fc.getSelectedFile();
				setTitle( file.getAbsolutePath() );
			} else {
				return;
			}
		}
		
		FileFuncs.writeFile( file, textArea.getText() );
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if ( e.getActionCommand().equals( "New" ) ) {
			textArea.setText("");
			setTitle( "" );
			JFileChooser fc = new JFileChooser( ( file != null ) ? file.getParent() : null );
			file = null;
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );

			if ( fc.showSaveDialog( CodeViewer.this ) == JFileChooser.APPROVE_OPTION ) {
				this.file = fc.getSelectedFile();
				setTitle( file.getAbsolutePath() );
			}			
		} else if ( e.getActionCommand().equals( "Open" ) ) {
			
			JFileChooser fc = new JFileChooser( ( file != null ) ? file.getParent() : null );
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
		}
	}

	@Override
	public void dispose() {
		if (textArea != null) {
//			textArea.destroy();

			textArea = null;
		}
		super.dispose();
	}
}

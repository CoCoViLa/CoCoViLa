package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
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
	JButton				saveBtn;
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

		if( RuntimeProperties.isSyntaxHighlightingOn ) {
			textArea = SyntaxDocument.createEditor();
        } else {
        	JTextArea ta = new JTextArea();
        	ta.setLineWrap( true );
        	ta.setWrapStyleWord( true );
        	textArea = ta;
        }
		
		//textArea = new JavaColoredTextPane();
		textArea.addKeyListener(new ProgramTextEditor.CommentKeyListener());
		textArea.setFont(RuntimeProperties.font);
		
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setRowHeaderView(new LineNumberView(textArea));
		areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel();
		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		saveBtn = new JButton("Save");
		saveBtn.setActionCommand( "Save" );
		saveBtn.addActionListener(this);
		toolBar.add(saveBtn);
		toolBar.add(new FontResizePanel(textArea));
		toolBar.add(new UndoRedoDocumentPanel(textArea.getDocument()));

		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		specText.add(toolBar, BorderLayout.NORTH);

		getContentPane().add(specText);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem open = new JMenuItem( "Open" );
		open.setActionCommand( "Open" );
		open.addActionListener(this);
		fileMenu.add(open);
		
		
		JMenuItem save = new JMenuItem( "Save" );
		save.setActionCommand( "Save" );
		save.addActionListener(this);
		fileMenu.add(save);
		
		fileMenu.add( new JSeparator() );
		
		JMenuItem close = new JMenuItem( "Close" );
		close.setActionCommand( "Close" );
		close.addActionListener(this);
		fileMenu.add(close);
		
		menuBar.add(fileMenu);
		setJMenuBar( menuBar );
		validate();
	}
	
	private void openFile( File file ) {
		this.file = file;
		setTitle(file.getAbsolutePath());
		
		final String fileText = FileFuncs.getFileContents(file);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				textArea.setText(fileText);
				textArea.setCaretPosition( 0 );
			}
		} );
	}
	
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals( "Save" ) ) {
			FileFuncs.writeFile(file, textArea.getText());
		} else if ( e.getActionCommand().equals( "Open" ) ) {
			JFileChooser fc = new JFileChooser(file.getParent());
			fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.JAVA ) );
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			
			if ( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {

                File file = fc.getSelectedFile();
                openFile( file );
			}
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

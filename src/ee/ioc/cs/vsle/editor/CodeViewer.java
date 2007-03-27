package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA. User: Ando Date: 28.03.2005 Time: 21:12:15
 */
public class CodeViewer extends JFrame implements ActionListener {

	JavaColoredTextPane	textArea;
	JPanel				specText;
	JButton				saveBtn;
	String				fileName;
	String				path;

	public CodeViewer(String name, String path, String extension) {
		super(name + extension);
		this.fileName = name + extension;
		this.path = path;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addComponentListener(new ComponentResizer(ComponentResizer.CARE_FOR_MINIMUM));

		final String fileText = FileFuncs.getFileContents(path + fileName);

		textArea = new JavaColoredTextPane();
		textArea.addKeyListener(new ProgramTextEditor.CommentKeyListener());
		textArea.setFont(RuntimeProperties.font);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				textArea.append(fileText);
				textArea.setCaretPosition( 0 );
			}
		} );
		
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setRowHeaderView(new LineNumberView(textArea));
		areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel();
		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		saveBtn = new JButton("Save");
		saveBtn.addActionListener(this);
		toolBar.add(saveBtn);
		toolBar.add(new FontResizePanel(textArea));
		toolBar.add(new UndoRedoDocumentPanel(textArea.getDocument()));

		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		specText.add(toolBar, BorderLayout.NORTH);

		getContentPane().add(specText);
		validate();
	}

	public CodeViewer(String name, String path) {
		this(name, path, ".java");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveBtn) {
			FileFuncs.writeFile(path + fileName, textArea.getText());
		}
	}

	@Override
	public void dispose() {
		if (textArea != null) {
			textArea.destroy();

			textArea = null;
		}
		super.dispose();
	}
}

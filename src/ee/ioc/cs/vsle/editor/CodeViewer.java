package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 28.03.2005
 * Time: 21:12:15
 * To change this template use Options | File Templates.
 */
public class CodeViewer extends JFrame implements ActionListener{
	JavaColoredTextPane textArea;
	JPanel specText;
	JButton saveBtn;
        String fileName;

        public CodeViewer(String name, String extension) {
            super(name + extension);
                this.fileName = name + extension;
                FileFuncs ff = new FileFuncs();

                String fileText = ff.getFileContents(RuntimeProperties.packageDir + fileName);

                textArea = new JavaColoredTextPane();
                textArea.addKeyListener( new ProgramTextEditor.CommentKeyListener() );
                textArea.setFont(RuntimeProperties.font);
                textArea.append(fileText);

                JScrollPane areaScrollPane = new JScrollPane(textArea);

                areaScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                specText = new JPanel();
                specText.setLayout(new BorderLayout());
                specText.add(areaScrollPane, BorderLayout.CENTER);
                JToolBar toolBar = new JToolBar();
                toolBar.setLayout( new FlowLayout( FlowLayout.LEFT ) );
                saveBtn = new JButton("Save");
                saveBtn.addActionListener(this);
                toolBar.add(saveBtn);
                toolBar.add(new FontResizePanel(textArea));
                toolBar.add(new UndoRedoDocumentPanel(textArea.getDocument()) );


                specText.setLayout(new BorderLayout());
                specText.add(areaScrollPane, BorderLayout.CENTER);
                specText.add(toolBar, BorderLayout.NORTH);

                getContentPane().add(specText);
                validate();
        }

	public CodeViewer(String name) {
		this( name, ".java" );
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveBtn) {
			FileFuncs ff = new FileFuncs();
			ff.writeFile(RuntimeProperties.packageDir + fileName, textArea.getText());
		}
	}

}

package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.GroupUnfolder;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 28.03.2005
 * Time: 21:12:15
 * To change this template use Options | File Templates.
 */
public class CodeViewer extends JFrame implements ActionListener{
	JTextArea textArea;
	JPanel specText;
	JButton saveBtn;
	GObj obj;
	public CodeViewer(GObj obj) {
		super();
        this.obj = obj;
		FileFuncs ff = new FileFuncs();

		String fileText = ff.getFileContents(RuntimeProperties.packageDir + obj.className + ".java");

		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		textArea.append(fileText);

		JScrollPane areaScrollPane = new JScrollPane(textArea);

		areaScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		specText = new JPanel();
		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		JToolBar toolBar = new JToolBar();

		saveBtn = new JButton("Save");
		saveBtn.addActionListener(this);
		toolBar.add(saveBtn);


		specText.setLayout(new BorderLayout());
		specText.add(areaScrollPane, BorderLayout.CENTER);
		specText.add(toolBar, BorderLayout.NORTH);

		getContentPane().add(specText);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveBtn) {
			FileFuncs ff = new FileFuncs();
			ff.writeFile(RuntimeProperties.packageDir + obj.className + ".java", textArea.getText());
		}
	}

}

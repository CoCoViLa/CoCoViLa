package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.FileFuncs;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 29.03.2005
 * Time: 19:44:21
 * To change this template use Options | File Templates.
 */
public class ClassSaveDialog extends JFrame implements ActionListener {


	JTextField textField;
	JButton cancel, ok;
	String text;
	Canvas canvas;

	public ClassSaveDialog(String text, Canvas canvas) {
		super();
		this.text = text;
		this.canvas = canvas;
		JPanel specText = new JPanel();
		textField = new JTextField("");
		textField.setCaretPosition(0);

		JPanel buttonPane = new JPanel();

		ok = new JButton("Save");
		ok.addActionListener(this);
		buttonPane.add(ok);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		buttonPane.add(cancel);

		specText.setLayout(new BorderLayout());

		specText.setLayout(new BorderLayout());
		specText.add(textField, BorderLayout.CENTER);
		specText.add(buttonPane, BorderLayout.SOUTH);

		getContentPane().add(specText);
		validate();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			String className = textField.getText();
		    String fileText = "class "+className+" {";
		    fileText += "\n    /*@ specification "+className+" {\n";
			fileText += text;
			fileText += "    }@*/\n \n}";

			if (className.length() != 0) {
				FileFuncs.writeFile(RuntimeProperties.packageDir + className+".java", fileText);
				canvas.currentObj.className = className;
				this.dispose();
				canvas.drawingArea.repaint();
			}

		}

		if (e.getSource() == cancel) {
			this.dispose();
			canvas.drawingArea.repaint();
		}
	}
}

package ee.ioc.cs.vsle.editor;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorWindow
	extends JFrame
	implements ActionListener {

	JTextArea textArea;
	JPanel errorText;
	ErrorWindow(String s) {
		super();
		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		JScrollPane areaScrollPane = new JScrollPane(textArea);

		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		errorText = new JPanel();
		errorText.setLayout(new BorderLayout());
		errorText.add(areaScrollPane, BorderLayout.CENTER);
		textArea.append(s);
		getContentPane().add(errorText);
		validate();
	}

	public void actionPerformed(ActionEvent e) {}
}

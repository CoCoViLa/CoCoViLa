package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.editor.Editor;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AboutDialog extends JDialog implements ActionListener {

    // Panels for the dialog components.
	private JPanel pnlMain = new JPanel();
	private JPanel pnlButtons = new JPanel();

    // Text area for the licensing information text.
	private JTextArea taLicenseText = new JTextArea();

    // Scroll pane for the text area holding the licensing information.
	private JScrollPane scrollPane = new JScrollPane(taLicenseText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // Action buttons.
	private JButton bttnOk = new JButton("OK");

    // Application references.
	IconEditor iEd;
	Editor eEd;

    /**
     * Dialog constructor.
     * @param iEd - Icon Editor application reference.
     * @param eEd - Scheme Editor application reference.
     */
	public AboutDialog(IconEditor iEd, Editor eEd) {
		this.iEd = iEd;
		this.eEd = eEd;
		setTitle("Credits and licensing information");

		pnlButtons.add(bttnOk);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(scrollPane, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		taLicenseText.setText(getLicenseText());
		taLicenseText.setLineWrap(true);
		taLicenseText.setEditable(false);
		taLicenseText.setFont(new Font("Arial", Font.PLAIN, 10));
		taLicenseText.setWrapStyleWord(true);
		taLicenseText.setAutoscrolls(true);

		getContentPane().add(pnlMain);
		setSize(new Dimension(340, 360));
		setResizable(false);
		if (iEd != null) setLocationRelativeTo(iEd);
		else if (eEd != null) setLocationRelativeTo(eEd);

		setVisible(true);

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					setVisible(false);
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

	} // AboutDialog

	/**
	 * Get the Lincese text from a text file specified
	 * in the PropertyBox.
	 * @return String - License text.
	 */
	public String getLicenseText() {
		StringBuffer textBuffer = new StringBuffer();
		try {
			String fileName = PropertyBox.GPL_EN_SHORT_LICENSE_FILE_NAME;
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			while ((str = in.readLine()) != null) {
				textBuffer.append(str);
				textBuffer.append("\n");
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textBuffer.toString();
	} // getLicenseText

	/**
	 * Action listener.
	 * @param evt ActionEvent - action event.
	 */
	public void actionPerformed(ActionEvent evt) {
	} // actionPerformed.

	/**
	 * Main method for module unit testing and debugging.
	 * @param args String[] - command line arguments.
	 */
	public static void main(String[] args) {
		new AboutDialog(new IconEditor(), null);
	} // main

} // end of class

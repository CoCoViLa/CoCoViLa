package ee.ioc.cs.vsle.iconeditor;


import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.util.PropertyBox;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class LicenseDialog extends JDialog implements ActionListener {

	private JPanel pnlMain = new JPanel();
	private JPanel pnlHeader = new JPanel();
	private JPanel pnlButtons = new JPanel();

	private JLabel lblLang = new JLabel("License language: ");

	private JComboBox cbLang = new JComboBox();

	private JTextArea taLicenseText = new JTextArea();

	private JScrollPane scrollPane = new JScrollPane(taLicenseText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	private JButton bttnOk = new JButton("OK");

	IconEditor iEd;
	Editor eEd;

	public LicenseDialog(IconEditor iEd, Editor eEd) {
		this.iEd = iEd;
		this.eEd = eEd;
		setTitle("Licensing information");

		pnlButtons.add(bttnOk);

		cbLang.addItem("English");
		cbLang.addItem("Eesti");

		FlowLayout fl = new FlowLayout();

		fl.setAlignment(FlowLayout.LEFT);

		pnlHeader.setLayout(fl);

		pnlHeader.add(lblLang);
		pnlHeader.add(cbLang);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlHeader, BorderLayout.NORTH);
		pnlMain.add(scrollPane, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		taLicenseText.setText(getLicenseText());
		taLicenseText.setCaretPosition(0);
		taLicenseText.setLineWrap(true);
		taLicenseText.setEditable(false);
		taLicenseText.setWrapStyleWord(true);

		getContentPane().add(pnlMain);
		setSize(new Dimension(500, 600));
		setResizable(false);
		if (iEd != null) {
			setLocationRelativeTo(iEd);
		} else if (eEd != null) {
			setLocationRelativeTo(eEd);
		}

		setVisible(true);

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					setVisible(false);
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

		cbLang.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent event) {
				if (event.getSource() == cbLang && event.getStateChange() == ItemEvent.SELECTED && cbLang.getItemCount() > 0) {
					taLicenseText.setText(getLicenseText());
					taLicenseText.setCaretPosition(0);
				}
			}
		}); // end cbLang item listener

	} // PortPropertiesDialog

	/**
	 * Get the Lincese text from a text file specified
	 * in the PropertyBox.
	 * @return String - License text.
	 */
	public String getLicenseText() {
		StringBuffer textBuffer = new StringBuffer();

		try {
			String fileName = PropertyBox.GPL_EN_LICENSE_FILE_NAME;

			if (cbLang != null && cbLang.getSelectedItem() != null && cbLang.getSelectedItem().toString().equalsIgnoreCase("Eesti")) {
				fileName = PropertyBox.GPL_EE_LICENSE_FILE_NAME;
			}
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
	public void actionPerformed(ActionEvent evt) {}

	/**
	 * Main method for module unit testing.
	 * @param args String[] - command line arguments.
	 */
	public static void main(String[] args) {
		LicenseDialog l = new LicenseDialog(new IconEditor(), null);
	} // main

}

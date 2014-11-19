package ee.ioc.cs.vsle.iconeditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

public class LicenseDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel pnlMain = new JPanel();
	private JPanel pnlHeader = new JPanel();
	private JPanel pnlButtons = new JPanel();

	private JLabel lblLang = new JLabel("License language: ");

	JComboBox cbLang = new JComboBox();

	JTextArea taLicenseText = new JTextArea();

	private JScrollPane scrollPane = new JScrollPane(taLicenseText, 
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	JButton bttnOk = new JButton("OK");

	public LicenseDialog(Component parent) {
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
		setPreferredSize(new Dimension(500, 600));
		setLocationRelativeTo(parent);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					dispose();
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

		cbLang.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent event) {
				if (event.getSource() == cbLang 
						&& event.getStateChange() == ItemEvent.SELECTED 
						&& cbLang.getItemCount() > 0) {
					taLicenseText.setText(getLicenseText());
					taLicenseText.setCaretPosition(0);
				}
			}
		}); // end cbLang item listener

        pack();
		setVisible(true);
	} // PortPropertiesDialog

	/**
	 * Get the Lincese text from a text file specified
	 * in the PropertyBox.
	 * @return String - License text.
	 */
	public String getLicenseText() {
		StringBuffer textBuffer = new StringBuffer();
		try {
			String fileName = RuntimeProperties.GPL_EN_LICENSE_FILE_NAME;
			if (cbLang != null && cbLang.getSelectedItem() != null 
					&& cbLang.getSelectedItem().toString().equalsIgnoreCase("Eesti")) {
				fileName = RuntimeProperties.GPL_EE_LICENSE_FILE_NAME;
			}
			
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);

			if( is == null ) return "";
			
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String str;
			while ((str = in.readLine()) != null) {
				textBuffer.append(str);
				textBuffer.append("\n");
			}
			in.close();
		} catch (IOException e) {
			db.p(e);
		}
		return textBuffer.toString();
	} // getLicenseText
} // end of class

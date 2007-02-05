package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.util.db;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		setSize(new Dimension(500, 600));
		setResizable(false);
		setLocationRelativeTo(parent);

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
				if (event.getSource() == cbLang 
						&& event.getStateChange() == ItemEvent.SELECTED 
						&& cbLang.getItemCount() > 0) {
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
			if (cbLang != null && cbLang.getSelectedItem() != null 
					&& cbLang.getSelectedItem().toString().equalsIgnoreCase("Eesti")) {
				fileName = PropertyBox.GPL_EE_LICENSE_FILE_NAME;
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

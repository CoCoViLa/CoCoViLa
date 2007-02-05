package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.util.db;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// Panels for the dialog components.
	private JPanel pnlMain = new JPanel();
	private JPanel pnlButtons = new JPanel();

    // Text area for the licensing information text.
	private JTextArea taLicenseText = new JTextArea();

    // Scroll pane for the text area holding the licensing information.
	private JScrollPane scrollPane = new JScrollPane(taLicenseText,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // Action buttons.
	JButton bttnOk = new JButton("OK");

    /**
     * Dialog constructor.
     * @param parent reference to the parent component, usually the main
     * 		  window of the application
     */
	public AboutDialog(Component parent) {
		setTitle("Credits and licensing information");

		pnlButtons.add(bttnOk);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(scrollPane, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		taLicenseText.setText(getLicenseText());
		
		String version = PropertyBox.getApplicationVersion();
		taLicenseText.append("\nVersion: ");
		taLicenseText.append(version == null ? "(development)" : version);
		taLicenseText.append("\n");

		taLicenseText.setEditable(false);
		taLicenseText.setAutoscrolls(true);

		getContentPane().add(pnlMain);
		setSize(new Dimension(340, 360));
		setLocationRelativeTo(parent);

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					dispose();
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
	} // AboutDialog

	/**
	 * Get the Lincese text from a text file specified
	 * in the PropertyBox.
	 * @return String - License text.
	 */
	public String getLicenseText() {
		StringBuffer textBuffer = new StringBuffer();
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(
					PropertyBox.GPL_EN_SHORT_LICENSE_FILE_NAME);

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

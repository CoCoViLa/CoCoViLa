package ee.ioc.cs.vsle.iconeditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class PortPropertiesDialog extends JDialog implements ActionListener {

	private String portName;
	private boolean isAreaConn;
	private boolean isStrict;

	IconPort port;

	private JPanel pnlMain = new JPanel();
	private JPanel pnlButtons = new JPanel();
	private JPanel pnlAttrs = new JPanel();

	private JLabel lblPortName = new JLabel(" Port Name:");
	private JLabel lblAreaConn = new JLabel(" Area Connected:");
	private JLabel lblIsStrict = new JLabel(" Strict Port:");

	private JTextField tfPortName = new JTextField();

	private JCheckBox checkAreaConn = new JCheckBox();
	private JCheckBox checkIsStrict = new JCheckBox();

	private JButton bttnOk = new JButton("OK");
	private JButton bttnCancel = new JButton("Cancel");

	IconEditor editor;

	PortPropertiesDialog(IconEditor editor, IconPort port) {
		this.editor = editor;
		this.port = port;
		if (this.port != null) {
			setTitle("Edit Port Properties");
		} else {
			setTitle("Define Port Properties");
		}

		pnlAttrs.setLayout(new GridLayout(0, 2));
		pnlAttrs.add(lblPortName);
		pnlAttrs.add(tfPortName);
		pnlAttrs.add(lblAreaConn);
		pnlAttrs.add(checkAreaConn);
		pnlAttrs.add(lblIsStrict);
		pnlAttrs.add(checkIsStrict);

		pnlButtons.add(bttnOk);
		pnlButtons.add(bttnCancel);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlAttrs, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);
		setSize(new Dimension(220, 130));
		setResizable(false);
//    setModal(true);
		setLocationRelativeTo(editor);
		setVisible(true);

		tfPortName.requestFocus();

		bttnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnCancel) {
					setVisible(false);
				}
			} // end actionPerformed
		}); // end bttnCancel Action Listener

		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (evt.getSource() == bttnOk) {
					setPortProperties();
				}
			} // end actionPerformed
		}); // end bttnOk Action Listener

	} // PortPropertiesDialog

	/**
	 * Draw displayed port on the drawing area in the IconEditor
	 */
	private void setPortProperties() {
		boolean valid = true;
		String portName = tfPortName.getText();
		if (portName != null && portName.trim().length() > 0) {
			portName = portName.trim();
		} else {
			// display information dialog to application user
			JOptionPane info_pane = new JOptionPane();
			info_pane.showMessageDialog(null, "Please define port name.");
			tfPortName.requestFocus();
			valid = false;
		}
		setPortName(portName);
		setAreaConn(checkAreaConn.isSelected());
		setStrict(checkIsStrict.isSelected());

		if (port == null) {
			if (valid) editor.mListener.drawPort(getPortName(), isAreaConn(), isStrict());
		} else {
			port.setName(getPortName());
			port.area = isAreaConn();
			port.strict = isStrict();
		}
		setVisible(!valid);
	} // setPortProperties

	public String getPortName() {
		return this.portName;
	}

	public boolean isAreaConn() {
		return this.isAreaConn;
	}

	public boolean isStrict() {
		return this.isStrict;
	}

	public void setPortName(String s) {
		if (s != null) {
			this.portName = s;
			tfPortName.setText(getPortName());
		}
	}

	public void setStrict(boolean b) {
		this.isStrict = b;
		checkIsStrict.setSelected(isStrict());
	}

	public void setAreaConn(boolean b) {
		this.isAreaConn = b;
		checkAreaConn.setSelected(isAreaConn());
	}

	/**
	 * Action listener.
	 * @param evt ActionEvent - action event.
	 */
	public void actionPerformed(ActionEvent evt) {
	}

	/**
	 * Main method for module unit testing.
	 * @param args String[] - command line arguments.
	 */
	public static void main(String[] args) {
		new PortPropertiesDialog(new IconEditor(), null);
	} // main

}

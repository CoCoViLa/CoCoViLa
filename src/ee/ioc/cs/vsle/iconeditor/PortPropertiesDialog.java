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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class PortPropertiesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private String portName;
	private String portType;
	private boolean isAreaConn;
	private boolean isStrict;

	IconPort port;

	private JPanel pnlMain = new JPanel();
	private JPanel pnlButtons = new JPanel();
	private JPanel pnlAttrs = new JPanel();

	private JLabel lblPortName = new JLabel(" Port Name:");
	private JLabel lblPortType = new JLabel(" Port Type:");
	private JLabel lblAreaConn = new JLabel(" Area Connected:");
	private JLabel lblIsStrict = new JLabel(" Strict Port:");

	private JTextField tfPortName = new JTextField();

	private JCheckBox checkAreaConn = new JCheckBox();
	private JCheckBox checkIsStrict = new JCheckBox();

	private JComboBox cbPortType = new JComboBox();

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
		pnlAttrs.add(lblPortType);
		pnlAttrs.add(cbPortType);
		pnlAttrs.add(lblAreaConn);
		pnlAttrs.add(checkAreaConn);
		pnlAttrs.add(lblIsStrict);
		pnlAttrs.add(checkIsStrict);

		cbPortType.setEditable(true);
		cbPortType.addItem("");
		cbPortType.addItem("int");
		cbPortType.addItem("Object");
		cbPortType.addItem("String");

		pnlButtons.add(bttnOk);
		pnlButtons.add(bttnCancel);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlAttrs, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);
		setSize(new Dimension(220, 150));
		setResizable(false);

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

       // Check if the port name is defined. Otherwise display the
	   // message dialog to the user informing that the port name is
	   // a required parameter.
		if (portName != null && portName.trim().length() > 0) {
			portName = portName.trim();
		} else {
			// display information dialog to application user
			JOptionPane.showMessageDialog(null, "Please define port name.");
			tfPortName.requestFocus();
			valid = false;
		}

        // If the port name was defined, check if the port type is defined. Otherwise
		// if the port type was not defined, display the message dialog to the user
		// informing that the port type is a required parameter.
		if(valid) {
		  if (cbPortType != null && cbPortType.getSelectedItem() != null &&
			  cbPortType.getSelectedItem().toString().trim().length() > 0) {
			setPortType(cbPortType.getSelectedItem().toString().trim());
		  }
		  else {
			JOptionPane.showMessageDialog(null, "Please define port type.");
			cbPortType.requestFocus();
			valid = false;
		  }
		}

        // Set other port properties.
		setPortName(portName);
		setAreaConn(checkAreaConn.isSelected());
		setStrict(checkIsStrict.isSelected());

        // The port was defined correctly and we are creating a new port (ie not editing
		// an already existing one), draw the new port with its properties on the canvas.
		if (port == null) {
			if (valid) editor.mListener.drawPort(getPortName(), isAreaConn(), isStrict(), getPortType());
		} else {
		    port.type = getPortType();
			port.setName(getPortName());
			port.area = isAreaConn();
			port.strict = isStrict();
		}

        // If the parameters were defined correctly, hide the dialog. Otherwise
		// keep the dialog visible, allowing the user correct any noted errors in
		// port parameters.
		setVisible(!valid);
	} // setPortProperties

	public String getPortType() {
	  return this.portType;
	}

	public String getPortName() {
		return this.portName;
	}

	public boolean isAreaConn() {
		return this.isAreaConn;
	}

	public boolean isStrict() {
		return this.isStrict;
	}

	public void setPortType(String s) {
	  if (s != null) {
		this.portType = s;
		cbPortType.setSelectedItem(s);
	  }
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

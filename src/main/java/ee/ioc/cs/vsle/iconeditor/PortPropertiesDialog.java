package ee.ioc.cs.vsle.iconeditor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.util.StringUtil;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class PortPropertiesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private String portName;
	private String portType;
	private boolean isAreaConn;
	private boolean isStrict;
	private boolean isMulti;
	
	IconPort port;

	private JPanel pnlMain = new JPanel();
	private JPanel pnlButtons = new JPanel();
	private JPanel pnlAttrs = new JPanel();

	private JLabel lblPortName = new JLabel(" Port Name:");
	private JLabel lblPortType = new JLabel(" Port Type:");
	private JLabel lblAreaConn = new JLabel(" Area Connected:");
	private JLabel lblIsStrict = new JLabel(" Strict Port:");
	private JLabel lblIsMulti = new JLabel(" Multi Port:");

	private JTextField tfPortName = new JTextField();

	private JCheckBox checkAreaConn = new JCheckBox();
	private JCheckBox checkIsStrict = new JCheckBox();
	private JCheckBox checkIsMulti = new JCheckBox();
	
	private JComboBox cbPortType = new JComboBox();

	private JButton bttnOk = new JButton("OK");
	private JButton bttnCancel = new JButton("Cancel");

	IconEditor editor;

	PortPropertiesDialog(IconEditor editor, IconPort port) {
		super(editor);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.editor = editor;
		
		pnlAttrs.setLayout(new GridLayout(0, 2));
		pnlAttrs.add(lblPortName);
		pnlAttrs.add(tfPortName);
		pnlAttrs.add(lblPortType);
		pnlAttrs.add(cbPortType);
		pnlAttrs.add(lblAreaConn);
		pnlAttrs.add(checkAreaConn);
		pnlAttrs.add(lblIsStrict);
		pnlAttrs.add(checkIsStrict);
		pnlAttrs.add(lblIsMulti);
		pnlAttrs.add(checkIsMulti);
		
		cbPortType.setEditable(true);
		cbPortType.addItem("");
		cbPortType.addItem(TYPE_INT);
		cbPortType.addItem(TYPE_DOUBLE);
		cbPortType.addItem(TYPE_OBJECT);
		cbPortType.addItem(TYPE_STRING);
		cbPortType.addItem(TYPE_ALIAS);

		pnlButtons.add(bttnOk);
		pnlButtons.add(bttnCancel);

		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlAttrs, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);

		if (port != null) {
			setTitle("Edit Port Properties");
			setPortName(port.getName());
			setStrict(port.isStrict());
			setAreaConn(port.isArea());
			setPortType(port.getType());
			setMulti(port.isMulti());
			this.port = port;
		} else {
			setTitle("Define Port Properties");
		}
		
		add(pnlMain);
		setLocationRelativeTo(editor);
                setResizable(false);
                pack();

		bttnCancel.addActionListener(this); // end bttnCancel Action Listener

		bttnOk.addActionListener(this);
	
	} // PortPropertiesDialog

	/**
	 * Draw displayed port on the drawing area in the IconEditor
	 */
	private void setPortProperties() {
		boolean valid = true;

		// Check if the port name is defined. Otherwise display the
		// message dialog to the user informing that the port name is
		// a required parameter.
		String name = tfPortName.getText();

		if (name != null) {
			name = name.trim();
			setPortName(name);
		}

		if (name == null || name.length() < 1) {
			// display information dialog to application user
			JOptionPane.showMessageDialog(this, "Please define port name.");
			tfPortName.requestFocusInWindow();
			valid = false;
		} else if (!checkIdentifier( name )) {
			JOptionPane.showMessageDialog(this, 
					"The port name is not a valid identifier.");
			tfPortName.requestFocusInWindow();
			valid = false;
		}

        // If the port name was defined, check if the port type is defined. Otherwise
		// if the port type was not defined, display the message dialog to the user
		// informing that the port type is a required parameter.
		if (valid) {
			String type = null;
			Object selected = cbPortType.getSelectedItem();

			if (selected != null)
				type = selected.toString();

			if (type != null) {
				type = type.trim();
				setPortType(type);
			}
			
			if (type == null || type.length() < 1) {
				JOptionPane.showMessageDialog(null, "Please define port type.");
				cbPortType.requestFocusInWindow();
				valid = false;
			} else if (!StringUtil.isJavaIdentifier(type)) {
				JOptionPane.showMessageDialog(null,
						"The type is not a valid identifier.");
				cbPortType.requestFocusInWindow();
				valid = false;
			}
		}

		setAreaConn(checkAreaConn.isSelected());
		setStrict(checkIsStrict.isSelected());
		setMulti(checkIsMulti.isSelected());

        // If the parameters were defined correctly, hide the dialog. Otherwise
		// keep the dialog visible, allowing the user correct any noted errors in
		// port parameters.
		if (valid) {
			// The port was defined correctly and we are creating a new port (ie not editing
			// an already existing one), draw the new port with its properties on the canvas.
			if (port == null) {
				editor.mListener.drawPort(getPortName(), isAreaConn(), isStrict(),
							getPortType(), isMulti() );
			} else {
				port.setType(getPortType());
				port.setName(getPortName());
				port.area = isAreaConn();
				port.strict = isStrict();
				port.multi = isMulti();
			}
			setVisible(false);
			dispose();
		}
	} // setPortProperties

	private boolean checkIdentifier( String name ) {
	    int idx = name.indexOf( '.' );
	    if( idx < 0 )
	        return StringUtil.isJavaIdentifier(name);
	    
	    do {
	        if( !StringUtil.isJavaIdentifier(name.substring( 0, idx ) ) )
	            return false;
	        name = name.substring( idx+1 );
	        idx = name.indexOf( '.' );
	    } while( idx > -1 );
	    
	    return StringUtil.isJavaIdentifier(name);
	}
	
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
		if (evt.getSource() == bttnCancel) {
			setVisible(false);
			dispose();
		} else if (evt.getSource() == bttnOk) {
			setPortProperties();
		}
	}

	public boolean isMulti() {
		return isMulti;
	}

	public void setMulti(boolean isMulti) {
		this.isMulti = isMulti;
		checkIsMulti.setSelected(isMulti);
	}
}

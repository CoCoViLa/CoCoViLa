package ee.ioc.cs.vsle.classeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for specifying package properties.
 * <p>Title: Package Properties Dialog</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Aulo Aasmaa
 * @version 1.0
 */

public class PackagePropertiesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

  ////////////////////////////
  // Dialog layout components.
  ////////////////////////////

  // Buttons
  private static final JButton bttnOk = new JButton("OK");
  private static final JButton bttnSave = new JButton("Save");
  private static final JButton bttnCancel = new JButton("Cancel");

  // Panels
  private static final JPanel pnlLabels = new JPanel();
  private static final JPanel pnlFields = new JPanel();
  private static final JPanel pnlButtons = new JPanel();
  private static final JPanel pnlProps = new JPanel();

  // Labels
  private static final JLabel lblName = new JLabel("Name:");
  private static final JLabel lblDesc = new JLabel("Description:");


  // Text fields
  private static final JTextField fldName = new JTextField();
  private static final JTextField fldDesc = new JTextField();

  // values to edit
  private String pckName = null;
  private String pckDesc = null;
  

  /**
   * Class constructor.
   */
  public PackagePropertiesDialog() {
	  new PackagePropertiesDialog(null, null);
  }
  public PackagePropertiesDialog(String name, String desc) {
	 
	this.pckName = name;
	this.pckDesc = desc;
	  
	this.setTitle("Package Properties");

	// add buttons to the buttons' panel.
	pnlButtons.add(bttnSave);
	pnlButtons.add(bttnCancel);

	// Labels and fields are stored on separate panels and grouped by the pnlGroups panel.
	pnlLabels.setPreferredSize(new Dimension(110,45));
	pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());
	pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
	pnlLabels.setLayout(new GridLayout(2,1));
	pnlLabels.add(lblName);
	pnlLabels.add(lblDesc);


	// Fields are stored on a separate panel.
	pnlFields.setPreferredSize(new Dimension(250,45));
	pnlFields.setMinimumSize(pnlFields.getPreferredSize());
	pnlFields.setMaximumSize(pnlFields.getPreferredSize());
	pnlFields.setLayout(new GridLayout(2,1));
	if(pckName != null){
		fldName.setText(pckName);
		fldDesc.setText(pckDesc);
	} else {
		fldName.setText("");
		fldDesc.setText("");
	}
	pnlFields.add(fldName);
	pnlFields.add(fldDesc);

	// Group labels and fields.
	pnlProps.setPreferredSize(new Dimension(360,90));
	pnlProps.setMinimumSize(pnlProps.getPreferredSize());
	pnlProps.setMaximumSize(pnlProps.getPreferredSize());
	pnlProps.add(pnlLabels);
	pnlProps.add(pnlFields);

	// Add group of labels and fields and a panel with buttons onto the content pane
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(pnlProps,BorderLayout.CENTER);
	getContentPane().add(pnlButtons,BorderLayout.SOUTH);

	// Specify dialog size, resizability and modality.
	// The dialog is made visible by the calling application.
	setSize(new Dimension(410,120));
	setResizable(false);
	setModal(true);

	//////////////////////////////////////////////////////
	////// ACTION LISTENERS AS ANONYMOUS CLASSES /////////
	//////////////////////////////////////////////////////

	// Ok button pressed, close the window and update class properties.
	bttnSave.addActionListener(new ActionListener() {
	  public void actionPerformed(final ActionEvent evt) {
		// Store the defined properties in runtime variables.
		storeVariables();
		// Close the dialog. The closing method validates variables.
		closeDialog(true);
	  }
	});

	// Cancel button pressed, just close the dialog without updating any class parameters.
	bttnCancel.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent evt) {
		// Close the dialog.
		closeDialog(false);
	  }
	});

  } // ClassPropertiesDialog

  /**
   * Updates class properties. Invoked by the press
   * on the Ok button.
   */
  private void storeVariables() {
	String name = fldName.getText();
	if(name!=null) name = name.trim();
	ClassEditor.packageName = name;

	String desc = fldDesc.getText();
	if(desc!=null) desc = desc.trim();
	ClassEditor.packageDesc = desc;
  } // storeVariables.

  /**
   * Close the dialog window.
   * @param validate - validate values if called by the OK button, otherwise
   * just pass the validation section.
   */
  private void closeDialog(boolean validate) {
	if(validate) {
	  if (valuesValid()) {
		  ClassEditor.packageParamsOk = true;
		setVisible(false);
	  }
	} else {
		ClassEditor.packageParamsOk = false;
	  setVisible(false);
	}
  } // closeDialog;

  /**
   * Validate values.
   * @return boolean
   */
  private boolean valuesValid() {
	boolean valid = true;

	if (PackagePropertiesDialog.fldName.getText() == null 
            || (PackagePropertiesDialog.fldName != null && PackagePropertiesDialog.fldName.getText().trim().length() == 0)) {
	  valid = false;
	  JOptionPane.showMessageDialog(null, "Please define package name.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
	  fldName.requestFocus();
	} else if (PackagePropertiesDialog.fldDesc.getText() == null 
            || (PackagePropertiesDialog.fldDesc != null && PackagePropertiesDialog.fldDesc.getText().trim().length() == 0)) {
	  valid = false;
	  JOptionPane.showMessageDialog(null, "Please define package description.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
	  fldDesc.requestFocus();
	}

   return valid;
  } // valuesValid

} // end of class

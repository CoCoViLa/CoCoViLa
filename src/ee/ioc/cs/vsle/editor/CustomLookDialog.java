package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ee.ioc.cs.vsle.util.*;

/**
 * Dialog for specifying the application Custom Look.
 * <p>Title: Custom Look Dialog</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Aulo Aasmaa
 * @version 1.0
 */

public class CustomLookDialog extends JDialog {

  ////////////////////////////
  // Dialog layout components.
  ////////////////////////////

  // Buttons
  private static final JButton bttnOk = new JButton("OK");
  private static final JButton bttnCancel = new JButton("Cancel");

  // Panels
  private static final JPanel pnlLabels = new JPanel();
  private static final JPanel pnlFields = new JPanel();
  private static final JPanel pnlButtons = new JPanel();
  private static final JPanel pnlProps = new JPanel();

  // Labels
  private static final JLabel lblLook = new JLabel("Look String:");

  // Text fields
  private static final JTextField fldLook = new JTextField();

  /**
   * Class constructor.
   */
  public CustomLookDialog() {
	this.setTitle("Application Custom Look And Feel Path");

	// add buttons to the buttons' panel.
	pnlButtons.add(bttnOk);
	pnlButtons.add(bttnCancel);

	// Labels and fields are stored on separate panels and grouped by the pnlGroups panel.
	pnlLabels.setPreferredSize(new Dimension(70,25));
	pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());
	pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
	pnlLabels.setLayout(new GridLayout(1,1));
	pnlLabels.add(lblLook);

	// Fields are stored on a separate panel.
	pnlFields.setPreferredSize(new Dimension(290,25));
	pnlFields.setMinimumSize(pnlFields.getPreferredSize());
	pnlFields.setMaximumSize(pnlFields.getPreferredSize());
	pnlFields.setLayout(new GridLayout(1,1));
	pnlFields.add(fldLook);

	fldLook.setToolTipText("<html>Please note that the defined custom look and feel<br>needs to be available from system classpath.");

	// Group labels and fields.
	pnlProps.setPreferredSize(new Dimension(360,50));
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
	setSize(new Dimension(410,90));
	setResizable(false);
	setModal(true);

	//////////////////////////////////////////////////////
	////// ACTION LISTENERS AS ANONYMOUS CLASSES /////////
	//////////////////////////////////////////////////////

	// Ok button pressed, close the window and update class properties.
	bttnOk.addActionListener(new ActionListener() {
	  public void actionPerformed(final ActionEvent evt) {
		// Store the defined properties in runtime variables.
		storeVariables();

		if(RuntimeProperties.customLayout!=null) {
		  OptionsDialog.cbDfltLayout.setSelectedItem(Look.LOOK_CUSTOM);
		  Look.changeLayout(Look.LOOK_CUSTOM);
		}

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

  } // CustomLookDialog

  /**
   * Updates class properties. Invoked by the press
   * on the Ok button.
   */
  private void storeVariables() {
	String look = fldLook.getText();
	if(look!=null) {
	  look = look.trim();
	  if(look.length()==0) {
		look = null;
	  }
	}
	RuntimeProperties.customLayout = look;

    PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,PropertyBox.CUSTOM_LAYOUT,look);

  } // storeVariables.

  /**
   * Close the dialog window.
   * @param validate - validate values if called by the OK button, otherwise
   * just pass the validation section.
   */
  private void closeDialog(boolean validate) {
	setVisible(false);
  } // closeDialog;

  public static void main(String[] args) {
	CustomLookDialog cd = new CustomLookDialog();
	cd.setVisible(true);
  }

}

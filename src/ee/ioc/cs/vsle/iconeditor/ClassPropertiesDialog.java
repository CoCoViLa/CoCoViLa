package ee.ioc.cs.vsle.iconeditor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;

/**
 * Dialog for specifying class properties. The class (class or relation)
 * cannot be exported from the application in XML format unless all necessary
 * properties have been set. Currently the required parameters include the
 * class name, class title and a class icon in GIF format.
 * <p>Title: Class Properties Dialog</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */

public class ClassPropertiesDialog extends JDialog {

  ////////////////////////////
  // Dialog layout components.
  ////////////////////////////

  // Buttons
  private static final JButton bttnOk = new JButton("OK");
  private static final JButton bttnCancel = new JButton("Cancel");
  private static final JButton bttnBrowseIcon = new JButton("...");

  // Panels
  private static final JPanel pnlLabels = new JPanel();
  private static final JPanel pnlFields = new JPanel();
  private static final JPanel pnlProps = new JPanel();
  private static final JPanel pnlButtons = new JPanel();
  private static final JPanel pnlClassIcon = new JPanel();
  private static final JPanel pnlRelation = new JPanel();

  // Labels
  private static final JLabel lblClassName = new JLabel("Class Name:");
  private static final JLabel lblClassDesc = new JLabel("Class Description:");
  private static final JLabel lblClassIcon = new JLabel("Class Icon:");
  private static final JLabel lblRelation = new JLabel("Class Is Relation:");

  // Text fields
  private static final JTextField fldClassName = new JTextField();
  private static final JTextField fldClassDesc = new JTextField();
  private static final JTextField fldClassIcon = new JTextField();

  // Checkboxes
  private static final JCheckBox chkRelation = new JCheckBox();

  // Defines if empty values are valid or not. If the dialog is opened
  // manually from the application's Edit menu, some of the fields can
  // be left empty. If in turn the dialog was opened automatically by
  // the application before exporting the drawing to XML format because
  // some fields were left empty, all the required fields need to be
  // filled before the dialog can be closed.
  private boolean emptyValuesValid = true;

  /**
   * Class constructor.
   */
  public ClassPropertiesDialog() {
	this.setTitle("Class Properties");

	// By default, the fields can be left empty.
	// The switch is turned by the calling application after
	// the creation of this dialog.
	setEmptyValuesValid(true);

	// add buttons to the buttons' panel.
	pnlButtons.add(bttnOk);
	pnlButtons.add(bttnCancel);

	// Labels and fields are stored on separate panels and grouped by the pnlGroups panel.
	pnlLabels.setPreferredSize(new Dimension(110,90));
	pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());
	pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
	pnlLabels.setLayout(new GridLayout(4,1));
	pnlLabels.add(lblClassName);
	pnlLabels.add(lblClassDesc);
	pnlLabels.add(lblClassIcon);
	pnlLabels.add(lblRelation);

	// The icon path field and browsing button on a separate panel.
	pnlClassIcon.setLayout(new BorderLayout());
	pnlClassIcon.add(fldClassIcon,BorderLayout.CENTER);
	pnlClassIcon.add(bttnBrowseIcon,BorderLayout.EAST);

	// The class can be defined also as a relation. The checkbox is on a separate panel.
	pnlRelation.setLayout(new BorderLayout());
	pnlRelation.add(chkRelation,BorderLayout.WEST);
	pnlRelation.add(new JLabel(" "),BorderLayout.CENTER);

	// Fields are stored on a separate panel.
	pnlFields.setPreferredSize(new Dimension(250,90));
	pnlFields.setMinimumSize(pnlFields.getPreferredSize());
	pnlFields.setMaximumSize(pnlFields.getPreferredSize());
	pnlFields.setLayout(new GridLayout(4,1));
	pnlFields.add(fldClassName);
	pnlFields.add(fldClassDesc);
	pnlFields.add(pnlClassIcon);
	pnlFields.add(pnlRelation);

    // Group labels and fields.
	pnlProps.setPreferredSize(new Dimension(360,110));
	pnlProps.setMinimumSize(pnlProps.getPreferredSize());
	pnlProps.setMaximumSize(pnlProps.getPreferredSize());
	pnlProps.add(pnlLabels);
	pnlProps.add(pnlFields);

	// Add group of labels and fields and a panel with buttons onto the content pane
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(pnlProps,BorderLayout.CENTER);
	getContentPane().add(pnlButtons,BorderLayout.SOUTH);

	// Initialize fields with runtime values.
	initialize();

	// Specify dialog size, resizability and modality.
	// The dialog is made visible by the calling application.
	setSize(new Dimension(410,160));
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

   // Icon browsing button pressed. Browse for the icon in GIF format
   // and set the browsed path to the Class Icon text field.
   bttnBrowseIcon.addActionListener(new ActionListener() {
	 public void actionPerformed(final ActionEvent evt) {
	   // Open the file open dialog for browsing the class icon in GIF format.
	   browseIcon();
	 }
   });

  } // ClassPropertiesDialog

  /**
   * Initializes the property fields with runtime variables.
   */
  private void initialize() {
	if(RuntimeProperties.className!=null) fldClassName.setText(RuntimeProperties.className);
	if(RuntimeProperties.classDescription!=null) fldClassDesc.setText(RuntimeProperties.classDescription);
	if(RuntimeProperties.classIcon!=null) fldClassIcon.setText(RuntimeProperties.classIcon);
	chkRelation.setSelected(RuntimeProperties.classIsRelation);
  } // initialize

  /**
   * Updates class properties. Invoked by the press
   * on the Ok button.
   */
  private void storeVariables() {
	String className = fldClassName.getText();
	if(className!=null) className = className.trim();
	RuntimeProperties.className = className;

	String classTitle = fldClassDesc.getText();
	if(classTitle!=null) classTitle = classTitle.trim();
	RuntimeProperties.classDescription = classTitle;

	String classIcon = fldClassIcon.getText();
	if(classIcon!=null) {
	  classIcon = classIcon.trim();
	}
	RuntimeProperties.classIcon = classIcon;

    boolean relation = chkRelation.isSelected();
	RuntimeProperties.classIsRelation = relation;

  } // storeVariables.

  /**
   * Browses for the class icon and sets the browsed path
   * to the Class Icon text field.
   */
  private void browseIcon() {
	JFileChooser fc = new JFileChooser(IconEditor.getLastPath());
	fc.setFileFilter(IconEditor.getFileFilter("gif"));
	int returnVal = fc.showOpenDialog(null);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	  File file = fc.getSelectedFile();
	  fldClassIcon.setText(file.getAbsolutePath());
	  IconEditor.setLastPath(file.getAbsolutePath());
	}
  } // browseIcon

  /**
   * Close the dialog window.
   * @param validate - validate values if called by the OK button, otherwise
   * just pass the validation section.
   */
  private void closeDialog(boolean validate) {
	if(validate) {
	  if (valuesValid()) {
		setVisible(false);
	  }
	} else {
	  IconEditor.classParamsOk = false;
	  setVisible(false);
    }
  } // closeDialog;

  /**
   * Validate values.
   * @return boolean
   */
  private boolean valuesValid() {
	boolean valid = true;
	if(!this.emptyValuesValid) {
	  if(this.fldClassName.getText()==null ||
		 (this.fldClassName!=null && this.fldClassName.getText().trim().length()==0)) {
		 valid = false;
		   JOptionPane.showMessageDialog(null, "Please define class name.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
		 fldClassName.requestFocus();
	   } else if(this.fldClassDesc.getText()==null ||
		  (this.fldClassDesc!=null && this.fldClassDesc.getText().trim().length()==0)) {
		  valid = false;
			JOptionPane.showMessageDialog(null, "Please define class description.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
		  fldClassDesc.requestFocus();
	   } else if(this.fldClassIcon.getText()==null ||
		 (this.fldClassIcon!=null && this.fldClassIcon.getText().trim().length()==0)) {
         valid = false;
  		 JOptionPane.showMessageDialog(null, "Please define class icon.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
		 fldClassIcon.requestFocus();
	  }
   }
   if(this.fldClassIcon!=null && this.fldClassIcon.getText()!=null && this.fldClassIcon.getText().trim().length()>0) {
	 if(!this.fldClassIcon.getText().trim().toLowerCase().endsWith(".gif")) {
	   valid = false;
	   this.fldClassIcon.setText("");
	   RuntimeProperties.classIcon="";
	   JOptionPane.showMessageDialog(null, "Only icons in GIF format allowed.", "Invalid icon format", JOptionPane.INFORMATION_MESSAGE);
	   fldClassIcon.requestFocus();
	 }
   }
   return valid;
  } // valuesValid

  /**
   * Defines if empty values are valid or not. If not valid,
   * then the dialog cannot be closed unless all class properties
   * are defined. The latter case is used for exporting the
   * image to Class or Relation.
   * @param b boolean - valid or not.
   */
  public void setEmptyValuesValid(boolean b) {
   this.emptyValuesValid = b;
 } // setEmptyValuesValid

}

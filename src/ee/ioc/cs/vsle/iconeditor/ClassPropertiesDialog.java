package ee.ioc.cs.vsle.iconeditor;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;
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
	private final JButton bttnOk = new JButton("OK");
	private final JButton bttnCancel = new JButton("Cancel");
	private final JButton bttnNewField = new JButton("Add New Field");
	private final JButton bttnDelField = new JButton("Delete Selected Fields");
	private final JButton bttnBrowseIcon = new JButton("...");

	// Panels
	private final JPanel pnlLabels = new JPanel();
	private final JPanel pnlFields = new JPanel();
	private final JPanel pnlProps = new JPanel();
	private final JPanel pnlTable = new JPanel();
	private final JPanel pnlButtons = new JPanel();
	private final JPanel pnlTableButtons = new JPanel();
	private final JPanel pnlClassIcon = new JPanel();
	private final JPanel pnlRelation = new JPanel();

	// Labels
	private final JLabel lblClassName = new JLabel("Class Name:");
	private final JLabel lblClassDesc = new JLabel("Class Description:");
	private final JLabel lblClassIcon = new JLabel("Class Icon:");
	private final JLabel lblRelation = new JLabel("Class Is Relation:");

	// Text fields
	private final JTextField fldClassName = new JTextField();
	private final JTextField fldClassDesc = new JTextField();
	private final JTextField fldClassIcon = new JTextField("default.gif");

	// Checkboxes
	private final JCheckBox chkRelation = new JCheckBox();

	private TitledBorder pnlTableTitle = BorderFactory.createTitledBorder("Class Fields");

	// Defines if empty values are valid or not. If the dialog is opened
	// manually from the application's Edit menu, some of the fields can
	// be left empty. If in turn the dialog was opened automatically by
	// the application before exporting the drawing to XML format because
	// some fields were left empty, all the required fields need to be
	// filled before the dialog can be closed.
	private boolean emptyValuesValid = true;

	// Table selection listener.
	ListSelectionListener listListener;

	// Table for class fields.
	public ClassFieldsTable tblClassFields = new ClassFieldsTable();

	// Table selection model.
	ListSelectionModel selectionModel = tblClassFields.getSelectionModel();

	// Scrollpanes.
	private JScrollPane spTableScrollPane = new JScrollPane(tblClassFields, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	/**
	 * Class constructor.
	 */
	public ClassPropertiesDialog(boolean emptyValid) {
		this.setTitle("Class Properties");
		setEmptyValuesValid(emptyValid);

		// add the table panel a border.
		pnlTable.setBorder(pnlTableTitle);

		// By default, the fields can be left empty.
		// The switch is turned by the calling application after
		// the creation of this dialog.
		setEmptyValuesValid(true);

		// add buttons to the buttons' panel.
		pnlButtons.add(bttnOk);
		pnlButtons.add(bttnCancel);

		// Labels and fields are stored on separate panels and grouped by the pnlGroups panel.
		pnlLabels.setPreferredSize(new Dimension(110, 90));
		pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());
		pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
		pnlLabels.setLayout(new GridLayout(4, 1));
		pnlLabels.add(lblClassName);
		pnlLabels.add(lblClassDesc);
		pnlLabels.add(lblClassIcon);
		pnlLabels.add(lblRelation);

		// The icon path field and browsing button on a separate panel.
		pnlClassIcon.setLayout(new BorderLayout());
		pnlClassIcon.add(fldClassIcon, BorderLayout.CENTER);
		pnlClassIcon.add(bttnBrowseIcon, BorderLayout.EAST);

		// The class can be defined also as a relation. The checkbox is on a separate panel.
		pnlRelation.setLayout(new BorderLayout());
		pnlRelation.add(chkRelation, BorderLayout.WEST);
		pnlRelation.add(new JLabel(" "), BorderLayout.CENTER);

		// Fields are stored on a separate panel.
		pnlFields.setPreferredSize(new Dimension(250, 90));
		pnlFields.setMinimumSize(pnlFields.getPreferredSize());
		pnlFields.setMaximumSize(pnlFields.getPreferredSize());
		pnlFields.setLayout(new GridLayout(4, 1));
		pnlFields.add(fldClassName);
		pnlFields.add(fldClassDesc);
		pnlFields.add(pnlClassIcon);
		pnlFields.add(pnlRelation);

		// Group labels and fields.
		pnlProps.setPreferredSize(new Dimension(360, 110));
		pnlProps.setMinimumSize(pnlProps.getPreferredSize());
		pnlProps.setMaximumSize(pnlProps.getPreferredSize());
		pnlProps.add(pnlLabels);
		pnlProps.add(pnlFields);

		pnlTableButtons.setLayout(new GridLayout(1, 2));
		pnlTableButtons.add(bttnNewField);
		pnlTableButtons.add(bttnDelField);

		// Set class fields table on its own panel together with the
		// scrollpane the table is lying on and the button for adding a new field to the table.
		pnlTable.setPreferredSize(new Dimension(300, 150));
		pnlTable.setMinimumSize(pnlTable.getPreferredSize());
		pnlTable.setMaximumSize(pnlTable.getPreferredSize());
		pnlTable.setLayout(new BorderLayout());
		pnlTable.add(spTableScrollPane, BorderLayout.CENTER);
		pnlTable.add(pnlTableButtons, BorderLayout.SOUTH);

		// Add group of labels and fields and a panel with buttons onto the content pane
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(pnlProps, BorderLayout.NORTH);
		getContentPane().add(pnlTable, BorderLayout.CENTER);
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		// Initialize fields with runtime values.
		initialize();

		bttnDelField.setEnabled(false);

		// Specify dialog size, resizability and modality.
		// The dialog is made visible by the calling application.
		setSize(new Dimension(410, 350));
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

		// New class field adding button pressed. Add an empty row to the end of the DBResult
		// and refresh the table.
		bttnNewField.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				addEmptyClassField();
			}
		});

		// Class field deleting button pressed. Delete the selected row from the DBResult
		// and refresh the table.
		bttnDelField.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				delClassField();
			}
		});

		/**
		 * Mouse event listener at table column headers.
		 */
		MouseAdapter listMouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int column = tblClassFields.convertColumnIndexToModel(tblClassFields.getColumnModel().getColumnIndexAtX(e.getX()));
				if (e.getClickCount() == 1 && column != -1) {
					RuntimeProperties.dbrClassFields = ((ClassFieldsTableModel) tblClassFields.getModel()).sort(column);
				}
			}
		};

		/**
		 * Add listener for mouse clicks on table headers.
		 * A click on a column header fires a column sorting method
		 */
		tblClassFields.getTableHeader().addMouseListener(listMouseListener);
		/**
		 * tabeli ridade peal liikumist kuulav funktsioon
		 */
		selectionModel.addListSelectionListener(listListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// handle table events here.
				if (tblClassFields.getSelectedRowCount() > 0) {
					bttnDelField.setEnabled(true);
				} else {
					bttnDelField.setEnabled(false);
				}
			}
		}); // end selectionModel ListSelectionListener
		setVisible(true);
	} // ClassPropertiesDialog

	/**
	 * Add an empty row to the end of the DBResult and scroll the added row visible.
	 */
	private void addEmptyClassField() {
		String[] emptyRow = {"", "", "", ""};
		RuntimeProperties.dbrClassFields.appendRow(emptyRow);
		tblClassFields.setData(RuntimeProperties.dbrClassFields);
		if (tblClassFields.getRowCount() < 2) {
			tblClassFields.setRowSelectionInterval(0, tblClassFields.getRowCount() - 1);
		} else {
			tblClassFields.setRowSelectionInterval(tblClassFields.getRowCount() - 1, tblClassFields.getRowCount() - 1);
		}
	} // addEmptyClassField

	/**
	 * Delete selected rows from the dbresult. Adds a temporary column to the dbresult for marking
	 * rows to be deleted. Loop over the table, check if the row is selected or not and mark it to the
	 * temporary column in the dbresult. Loop over the dbresult and delete all rows marked for deletion.
	 * Refresh table with a "cleaned" dbresult.
	 */
	private void delClassField() {
		if (tblClassFields.getRowCount() > 0 && tblClassFields.getSelectedRowCount() > 0) {
			RuntimeProperties.dbrClassFields.addColumn("DEL");

			// Loop over table rows to mark rows in the dbresult deleted.
			for (int i = 0; i < tblClassFields.getRowCount(); i++) {
				RuntimeProperties.dbrClassFields.setField("DEL", i + 1, String.valueOf(tblClassFields.isRowSelected(i)));
			}

			// delete marked rows from the dbresult.
			boolean removed = true;
			while (removed) {
				removed = false;
				for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
					if (Boolean.valueOf(RuntimeProperties.dbrClassFields.getFieldAsString("DEL", i)).booleanValue()) {
						RuntimeProperties.dbrClassFields.removeRow(i);
						removed = true;
						break;
					}
				}
			}
			// Remove the temporary column from the DBResult.
			RuntimeProperties.dbrClassFields.removeColumn("DEL");

			// refresh table.
			tblClassFields.setData(RuntimeProperties.dbrClassFields);
		}
	} // delClassField

	/**
	 * Initializes the property fields with runtime variables.
	 */
	private void initialize() {
		if (RuntimeProperties.className != null) fldClassName.setText(RuntimeProperties.className);
		if (RuntimeProperties.classDescription != null) fldClassDesc.setText(RuntimeProperties.classDescription);
		if (RuntimeProperties.classIcon != null) fldClassIcon.setText(RuntimeProperties.classIcon);
		chkRelation.setSelected(RuntimeProperties.classIsRelation);
		removeEmptyRows();
		tblClassFields.setData(RuntimeProperties.dbrClassFields);
	} // initialize

	/**
	 * Updates class properties. Invoked by the press
	 * on the Ok button.
	 */
	private void storeVariables() {
		String className = fldClassName.getText();
		if (className != null) className = className.trim();
		RuntimeProperties.className = className;

		String classTitle = fldClassDesc.getText();
		if (classTitle != null) classTitle = classTitle.trim();
		RuntimeProperties.classDescription = classTitle;

		String classIcon = fldClassIcon.getText();
		if (classIcon != null) {
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
		if (validate) {
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
		if (!this.emptyValuesValid) {
			if (this.fldClassName.getText() == null ||
				(this.fldClassName != null && this.fldClassName.getText().trim().length() == 0)) {
				valid = false;
				JOptionPane.showMessageDialog(null, "Please define class name.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
				fldClassName.requestFocus();
			} else if (this.fldClassDesc.getText() == null ||
				(this.fldClassDesc != null && this.fldClassDesc.getText().trim().length() == 0)) {
				valid = false;
				JOptionPane.showMessageDialog(null, "Please define class description.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
				fldClassDesc.requestFocus();
			} else if (this.fldClassIcon.getText() == null ||
				(this.fldClassIcon != null && this.fldClassIcon.getText().trim().length() == 0)) {
				valid = false;
				JOptionPane.showMessageDialog(null, "Please define class icon.", "Missing Property", JOptionPane.INFORMATION_MESSAGE);
				fldClassIcon.requestFocus();
			}
		}
		if (this.fldClassIcon != null && this.fldClassIcon.getText() != null && this.fldClassIcon.getText().trim().length() > 0) {
			if (!this.fldClassIcon.getText().trim().toLowerCase().endsWith(".gif")) {
				valid = false;
				this.fldClassIcon.setText("");
				RuntimeProperties.classIcon = "";
				JOptionPane.showMessageDialog(null, "Only icons in GIF format allowed.", "Invalid icon format", JOptionPane.INFORMATION_MESSAGE);
				fldClassIcon.requestFocus();
			}
		}
		if (valid) {
			if (!classFieldsValid()) {
				// Class fields invalid. Allow the user to apply corrections.
				valid = false;
			}
		}
		if (valid) {
			if (classFieldsValid()) {
				// Class fields valid. Remove empty rows from the DBResult.
				removeEmptyRows();
			}
		}
		return valid;
	} // valuesValid

	public static void removeEmptyRows() {
		boolean removed = true;
		while (removed) {
			removed = false;
			for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
				String fieldName = RuntimeProperties.dbrClassFields.getFieldAsString(
					RuntimeProperties.classDbrFields[0], i);
				String fieldType = RuntimeProperties.dbrClassFields.getFieldAsString(
					RuntimeProperties.classDbrFields[1], i);
				String fieldValue = RuntimeProperties.dbrClassFields.getFieldAsString(
					RuntimeProperties.classDbrFields[2], i);

				boolean bFieldNameDefined = false;
				boolean bFieldTypeDefined = false;
				boolean bFieldValueDefined = false;

				if (fieldName != null) {
					if (fieldName.trim().length() > 0) bFieldNameDefined = true;
				}
				if (fieldType != null) {
					if (fieldType.trim().length() > 0) bFieldTypeDefined = true;
				}
				if (fieldValue != null) {
					if (fieldValue.trim().length() > 0) bFieldValueDefined = true;
				}
				System.out.println("NameDef: " + bFieldNameDefined + " TypeDef: " + bFieldTypeDefined + " ValueDef: " + bFieldValueDefined);
				if (!bFieldNameDefined & !bFieldTypeDefined & !bFieldValueDefined) {
					RuntimeProperties.dbrClassFields.removeRow(i);
					removed = true;
					break;
				}
			}
			System.out.println(RuntimeProperties.dbrClassFields.getRowCount());
		}
	} // removeEmptyRows

	/**
	 * Validate class fields. Field name cannot be undefined if field
	 * type or value are defined. The entire row can be empty, but the
	 * field name is always required if either field type or value is defined.
	 * @return boolean - class fields valid or not.
	 */
	private boolean classFieldsValid() {
		boolean valid = true;

		for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
			String fieldName = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[0], i);
			String fieldType = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[1], i);
			String fieldValue = RuntimeProperties.dbrClassFields.getFieldAsString(RuntimeProperties.classDbrFields[2], i);

			boolean bFieldNameDefined = false;
			boolean bFieldTypeDefined = false;
			boolean bFieldValueDefined = false;

			if (fieldName != null) {
				fieldName = fieldName.trim();
				RuntimeProperties.dbrClassFields.setField(RuntimeProperties.classDbrFields[0], i, fieldName);
				if (fieldName.length() > 0) bFieldNameDefined = true;
			}

			if (fieldType != null) {
				fieldType = fieldType.trim();
				RuntimeProperties.dbrClassFields.setField(RuntimeProperties.classDbrFields[1], i, fieldType);
				if (fieldType.length() > 0) bFieldTypeDefined = true;
			}

			if (fieldValue != null) {
				fieldValue = fieldValue.trim();
				RuntimeProperties.dbrClassFields.setField(RuntimeProperties.classDbrFields[2], i, fieldValue);
				if (fieldValue.length() > 0) bFieldValueDefined = true;
			}

			if (bFieldNameDefined) {
				if (fieldName.indexOf(":") > -1) {
					valid = false;
					tblClassFields.setRowSelectionInterval(i - 1, i - 1);
					JOptionPane.showMessageDialog(null, "Field name cannot contain \":\"." + "\n" +
						"Please correct the field name at row no. " + i + ".", "Error in field name", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}

			if (bFieldTypeDefined) {
				if (fieldType.indexOf(":") > -1) {
					valid = false;
					tblClassFields.setRowSelectionInterval(i - 1, i - 1);
					JOptionPane.showMessageDialog(null, "Field type cannot contain \":\"." + "\n" +
						"Please correct the field type at row no. " + i + ".", "Error in field type", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}

			if ((bFieldTypeDefined || bFieldValueDefined) && !bFieldNameDefined) {
				valid = false;
				tblClassFields.setRowSelectionInterval(i - 1, i - 1);

				JOptionPane.showMessageDialog(null, "Field name is required if either field type or value is defined." + "\n" +
					"Please correct the field definition at row no. " + i + ".", "Error in field definition", JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}

		return valid;
	} // classFieldsValid

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

	/**
	 * Main method for module unit testing and debugging.
	 */
	public static void main(String[] args) {
		new ClassPropertiesDialog(false);
	} // main

} // end of class.
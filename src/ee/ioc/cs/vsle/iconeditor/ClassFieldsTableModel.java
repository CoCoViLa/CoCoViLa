package ee.ioc.cs.vsle.iconeditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import ee.ioc.cs.vsle.editor.RuntimeProperties;

import javax.swing.table.DefaultTableModel;

/**
 * Title:        ClassFieldsTableModel.
 * Description:  Model for the Class Fields Table used in the Class Properties
 *  			 dialog of the Icon Editor application.
 * Copyright:    Copyright (c) 2004.
 * author:       Aulo Aasmaa.
 * @version 1.0
 */

public class ClassFieldsTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	// table model column indexes
	public static final int iNAME = 0;
	public static final int iTYPE = 1;
	public static final int iVALUE = 2;

	// Shadow the parent field with a vector of more specific type.
	// this.dataVector and super.dataVector refer to the same instance.
	private Vector<Vector<String>> dataVector;

	private RowComparator comparator;

	/**
	 * Default class constructor
	 **/
	public ClassFieldsTableModel() {
		dataVector = new Vector<Vector<String>>();
		super.dataVector = dataVector;
	} // ClassFieldsTableModel

	/**
	 * Sorts values displayed in a table by a specified column.
	 *
	 * @param column column index
	 */
	public void sort(int column) {
		if (comparator == null)
			comparator = new RowComparator();
		
		comparator.setSortColumn(column);
		Collections.sort(dataVector, comparator);
	} // sort

	/**
	 * Returns the number of columns in the class field table.
	 * @return the number of columns
	 */
	@Override
	public int getColumnCount() {
		return RuntimeProperties.classDbrFields.length;
	} // getColumnCount

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValueAt(int row, int column) {
		return dataVector.get(row).get(column);
	}

	/**
	 * Returns the title of the specified table column defined
	 * in RuntimeProperties.
	 * @param col column index
	 * @return the title of the specified column
	 */
	@Override
	public String getColumnName(int col) {
		return RuntimeProperties.classTblFields[col];
	} // getColumnName

	/**
	 * Returns class of table column.
	 * @param i number of column
	 * @return String column class
	 */
	@Override
	public Class<?> getColumnClass(int i) {
		return String.class;
	} // getColumnClass

	/**
	 * Removes empty rows from the table and notifies listeners.
	 */
	public void removeEmptyRows() {
		for (int i = 0; i < dataVector.size(); i++) {
			Vector<String> row = dataVector.get(i);
			boolean empty = true;
			for (String cell : row) {
				// Search for non-empty strings, no need to check for null
				// as values are initialized to "".
				if (!"".equals(cell)) {
					empty = false;
					break;
				}
			}
			if (empty)
				dataVector.remove(i);
		}
		fireTableDataChanged();
	}

	/**
	 * Comparator for sorting row vectors by a column.
	 * The natural ordering of Strings is used.
	 */
	static final class RowComparator implements Comparator<Vector<String>> {

		private int sortDirection = 1; // 1 - asc, -1 - desc
		private int sortColumn = -1; // -1 for not sorted

		public void setSortColumn(int column) {
			if (sortColumn == column) {
				sortDirection *= -1;
			} else {
				sortDirection = 1;
				sortColumn = column;
			}
		}

		public int compare(Vector<String> row1, Vector<String> row2) {
			return sortDirection 
				* row1.get(sortColumn).compareTo(row2.get(sortColumn));
		}
	}
} // end of class
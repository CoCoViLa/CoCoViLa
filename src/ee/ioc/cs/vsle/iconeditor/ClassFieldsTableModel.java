package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.util.queryutil.DBResult;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

import javax.swing.table.AbstractTableModel;

/**
 * Title:        ClassFieldsTableModel.
 * Description:  Model for the Class Fields Table used in the Class Properties dialog of
 the Icon Editor application.
 * Copyright:    Copyright (c) 2004.
 * author:      Aulo Aasmaa.
 * @version 1.0
 */

public class ClassFieldsTableModel extends AbstractTableModel {

	// DBResult sorting direction.
	private String direction = "ascending";

	// dbresult column indexes for a sorting method.
	private static final int iNAME = 0;
	private static final int iTYPE = 1;
	private static final int iVALUE = 2;

	/**
	 * Class constructor
	 *  @param dbr DBResult
	 **/
	public ClassFieldsTableModel(DBResult dbr) {
		RuntimeProperties.dbrClassFields = dbr;
	} // ClassFieldsTableModel

	/* Sorts values displayed in a table by a specified column.
	 * Uses class global variable "direction" with values "ascending" and "descending"
	 * to switch between sorting directions.
	 *
	 * @param column - column index.
	 * @return DBResult - sorted DBResult.
	 */
	public DBResult sort(int column) {
		// switch the default sorting direction
		if (direction.equals("ascending")) {
			direction = "descending";
			RuntimeProperties.dbrClassFields = RuntimeProperties.dbrClassFields.orderBy(getDbrColumnName(column), DBResult.ASC);
		} else if (direction.equals("descending")) {
			direction = "ascending";
			RuntimeProperties.dbrClassFields = RuntimeProperties.dbrClassFields.orderBy(getDbrColumnName(column), DBResult.DESC);
		}
		fill(); // fill table with a sorted DBResult.
		return RuntimeProperties.dbrClassFields;
	} // sort

	/**
	 * Täidab tabeli väärtustega varem leitud result set'i põhjal.
	 */
	private void fill() {
		Object[] row;
		int colCount = getColumnCount();
		for (int i = 1; i <= RuntimeProperties.dbrClassFields.getRowCount(); i++) {
			row = new Object[colCount];
			row[iNAME] = RuntimeProperties.dbrClassFields.getField(RuntimeProperties.classDbrFields[0], i);
			row[iTYPE] = RuntimeProperties.dbrClassFields.getField(RuntimeProperties.classDbrFields[1], i);
			row[iVALUE] = RuntimeProperties.dbrClassFields.getField(RuntimeProperties.classDbrFields[2], i);
		}
		fireTableDataChanged();
	} // fill

	/**
	 * Tagastab päringu veergude arvu.
	 * @return int - päringu veergude arv.
	 */
	public int getColumnCount() {
		return RuntimeProperties.classDbrFields.length;
	} // getColumnCount

	/**
	 * Tagastab päringu ridade arvu.
	 * @return int - päringu ridade arv.
	 */
	public int getRowCount() {
		return RuntimeProperties.dbrClassFields.getRowCount();
	} // getRowCount

	/**
	 * Tagastab aplikatsiooni runtime properties klassis määratud tabeli veergude pealkirjad.
	 * @param col - veeru number, mille pealkirja küsitakse.
	 * @return String - küsitud veeru pealkiri.
	 */
	public String getColumnName(int col) {
		return RuntimeProperties.classTblFields[col];
	} // getColumnName

	/**
	 * Tagastab aplikatsiooni runtime properties klassis määratud dbresult-i veergude pealkirjad.
	 * @param col - veeru number, mille pealkirja küsitakse.
	 * @return String - küsitud veeru pealkiri.
	 */
	public String getDbrColumnName(int col) {
		return RuntimeProperties.dbrClassFields.getColumnName(col + 1);
	} // getDbrColumnName

	/**
	 * Päringu tulemusena ning muudel põhjustel tabeli refreshimist toetav meetod.
	 * @param o - tabeli lahtrisse paigutatav objekt.
	 * @param row - tabelis uuendatav rida
	 * @param col - tabelis uuendatav veerg
	 */
	public void setValueAt(Object o, int row, int col) {
		RuntimeProperties.dbrClassFields.setField(col + 1, row + 1, o);
	} // setValueAt

	/** Päringu tulemuste redigeerimine tabelis esitamiseks sobivaks.
	 *
	 * @param row - row index of an object requested. row index of row to be populated with additional values.
	 * @param col - column index of an object requested.
	 * @return Object - object found at row'th row and column'th column.
	 */
	public Object getValueAt(int row, int col) {
		return RuntimeProperties.dbrClassFields.getFieldAsObject(col + 1, row + 1);
	} // getValueAt

	/**
	 * Returns class of table column.
	 * @param i number of column
	 * @return Object column class
	 */
	public Class getColumnClass(int i) {
		return Object.class;
	} // getColumnClass

	/**
	 * Specify if the cell is editable or not. No need to specify if your table is generally not editable.
	 * @param row number of row
	 * @param col number of column
	 * @return boolean if cell editable or not
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	} // isCellEditable

} // end of class
package ee.ioc.cs.vsle.classeditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ClassFieldTable extends  DefaultTableModel {

	
		private static final long serialVersionUID = 1L;

		
		// Labels
	/*    private final JLabel lblName = new JLabel("Name");
	 	private final JLabel lblType = new JLabel("Type");
	 	private final JLabel lblValue = new JLabel("Value");
	 	private final JLabel lblNature = new JLabel("Nature");
	 	private final JLabel lblDesc = new JLabel("Description");
	 	private final JLabel lblHidden = new JLabel("Hidden");
	 	private final JLabel lblDef = new JLabel("Default");*/
	 	
	 	String[] columnNames = {"Name",
	 							"Type",
	 							"Value",
	 							"Nature",
	 						 	"Description",
	 						 	"Hidden",
	 						 	"Known",	 						 	
	 						 	"Default"};
	 	String[] columnVars = {"FIELD", "TYPE", "VALUE", 
					"Nature",
				 	"Description",
				 	"HIDDEN", "Known",
				 	"Default"};
	 	
		// Shadow the parent field with a vector of more specific type.
		// this.dataVector and super.dataVector refer to the same instance.
		private Vector<Vector<Object>> dataVector;

	//	private RowComparator comparator;
		private String [] natureValues = {"Normal", "Input", "Goal"};
		public JComboBox natureElmt = new JComboBox(natureValues);
	     
		/**
		 * Default class constructor
		 **/
		public ClassFieldTable() {
			dataVector = new Vector<Vector<Object>>();
			super.dataVector = dataVector;
		} 

		/**
		 * Sorts values displayed in a table by a specified column.
		 *
		 * @param column column index
		 */
	/*	public void sort(int column) {
			if (comparator == null)
				comparator = new RowComparator();
			
			comparator.setSortColumn(column);
			Collections.sort(dataVector, comparator);
		} // sort
*/
		/**
		 * Returns the number of columns in the class field table.
		 * @return the number of columns
		 */
		@Override
		public int getColumnCount() {			
			return columnNames.length;
		} // getColumnCount

		@Override
		public Object  getValueAt(int row, int column) {
			if(dataVector.size() < 1 ||  dataVector.get(row) == null || dataVector.get(row).get(column) == null){
				switch (column) {
		        case 0:case 1:case 2:case 3:case 4:return "";
		        case 5:case 6:case 7:
		        	return false;   }
			} else
			return dataVector.get(row).get(column);
			
			return null;
		}
/**
		 * Returns the title of the specified table column defined
		 * in RuntimeProperties.
		 * @param col column index
		 * @return the title of the specified column
		 */
		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		} // getColumnName

		@Override
		public Class<?> getColumnClass(int i) {
			/*switch (i) {
	        case 0:case 1:case 2:case 3:case 4:return String.class;
	        case 5:case 6:case 7:
	        	return boolean.class;       
			}*/	 
			//return String.class;
	        return getValueAt(0, i).getClass();
		} 

		public Vector<Vector<Object>> getDataVector() {
	        return dataVector;
	    }
		
		public void setDataVector( Vector<Vector<Object>> dataVector ) {
			this.dataVector = ( dataVector != null ) ? dataVector : new Vector<Vector<Object>>();
			super.setDataVector( dataVector, null );
		}
		
		/**
		 * Removes empty rows from the table and notifies listeners.
		 */
		public void removeEmptyRows() {
			for (int i = 0; i < dataVector.size(); i++) {
				Vector<Object> row = dataVector.get(i);
				// empty name == empty row
				if(row.get(0).equals("")){
					dataVector.remove(i);
				}				
			}
			fireTableDataChanged();
		}
				/*
				boolean empty = true;
				for (Object cell : row) {
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
		 * Returns number on empty rows
		 */
		public int getEmptyRowsCount() {
			int emptyRows = 0;
			for (int i = 0; i < dataVector.size(); i++) {
				Vector<Object> row = dataVector.get(i);
				boolean empty = true;
				for (Object cell : row) {
					// Search for non-empty strings, no need to check for null
					// as values are initialized to "".
					if (!"".equals(cell)) {
						empty = false;
						break;
					}
				}
				if (empty)
					emptyRows++;
			}
			return emptyRows;
		}		

		/**
		 * Comparator for sorting row vectors by a column.
		 * The natural ordering of Strings is used.
		 */
	/*	 class RowComparator implements Comparator<Vector<Object>> {

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

			@Override
			public int compare(Vector<Object> arg0, Vector<Object> arg1) {
				// TODO Auto-generated method stub
				return 0;
			}
		}*/
	}
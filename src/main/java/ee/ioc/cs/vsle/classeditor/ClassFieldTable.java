package ee.ioc.cs.vsle.classeditor;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import ee.ioc.cs.vsle.packageparse.PackageXmlProcessor;
import ee.ioc.cs.vsle.vclass.ClassGraphics;

import ee.ioc.cs.vsle.vclass.VPackage;

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

		public ClassGraphics[] defaults = {};
		public ClassGraphics[] knowns = {};
		
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
		File f;

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
		
		**/
		public TableModelListener cfTableModelListener = new TableModelListener(){

			@Override
			public void tableChanged(TableModelEvent arg0) {
				//default graphic
				if(arg0.getColumn() == 7 || arg0.getColumn() == 6){
					boolean marker = Boolean.valueOf(
							getValueAt(arg0.getFirstRow(), arg0.getColumn()).toString());
				//	System.out.println("Changed: " + marker + "; " + arg0.getSource().toString());
					if(marker){	
						String s = getGraphicSelection();
						if(s != null){
						
							VPackage pkg;
							if ( (pkg = PackageXmlProcessor.load(f, false)) != null ) {
								ClassGraphics cg = pkg.getClass(s).getGraphics();	
								ClassCanvas canvas = ClassEditor.getInstance().getCurrentCanvas();
								if(arg0.getColumn() == 7){
									updateGraphic(true, cg, arg0.getFirstRow());
									ClassEditor.getInstance().graphicsToShapes(canvas, cg, getValueAt(arg0.getFirstRow(),0).toString(), true);
									JOptionPane.showMessageDialog(null, "Template '" + s + "' loaded from " +f.getName() + " "
											+ "is set as default graphics for field = " + getValueAt(arg0.getFirstRow(),0),
											  "", JOptionPane.INFORMATION_MESSAGE);									
								} else{
									updateGraphic(false, cg, arg0.getFirstRow());
									ClassEditor.getInstance().graphicsToShapes(canvas, cg, getValueAt(arg0.getFirstRow(),0).toString(), false);
									JOptionPane.showMessageDialog(null, "Template '" + s + "' loaded from " +f.getName() + " "
											+ "is set as known graphics for field = " + getValueAt(arg0.getFirstRow(),0),
											  "", JOptionPane.INFORMATION_MESSAGE);		
								}
								canvas.repaint();
						 }
					  //  System.out.println("f: " +f.getName() + "; selection = " + s);
					   				    
					} else {
						// No graphic selected, rollback to false
						setValueAt(false, arg0.getFirstRow(), arg0.getColumn());
					}
				} else {
					if(arg0.getColumn() == 7 && defaults.length > arg0.getFirstRow()){						
						defaults[arg0.getFirstRow()] = null;
						removeShapes(true, getValueAt(arg0.getFirstRow(),0).toString());
					} else if(arg0.getColumn() == 6 && knowns.length > arg0.getFirstRow()) {
						knowns[arg0.getFirstRow()] = null;
						removeShapes(false, getValueAt(arg0.getFirstRow(),0).toString());
					}
				}
			}
		}
	};
		
		
		public String getGraphicSelection(){
			
			 String selection = null;
			
		     f = ClassEditor.getInstance().selectFile();
			 if(f != null){
				 ClassImport ci = new ClassImport( f, ClassEditor.getInstance().packageClassNamesList, ClassEditor.getInstance().packageClassList, 
						 ClassEditor.getInstance().templateNameList );
				 PopupCanvas popupCanvas = new PopupCanvas(ClassEditor.getInstance().getCurrentPackage(), f.getParent() + File.separator);
				 PortGraphicsDialog dialog = new  PortGraphicsDialog(ClassEditor.getInstance().templateNameList, "Select Template",null, popupCanvas, f, true);
			 
				 dialog.newJList( ClassEditor.getInstance().templateNameList);			  
				 dialog.setVisible( true );
				 dialog.repaint();
				 selection = dialog.getSelectedValue();
				 
			 }
			 return selection;
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
		
		public void updateGraphic(boolean def, ClassGraphics cg, int index){
			if(def){
				if(defaults.length < index+1){
					defaults = Arrays.copyOf(defaults, index+1);
					defaults[index] = cg;
				}
			
			} else {
				if(knowns.length < index+1){
					knowns = Arrays.copyOf(knowns, index+1);
					knowns[index] = cg;
				}
			}
		}
		
		public void removeGraphic(boolean def, int index){
			int i;
			if(def){
				if(index < defaults.length){					 
					ClassGraphics[] defaultscopy = Arrays.copyOf(defaults, defaults.length);	
					for (i = 0;  i < defaultscopy.length; i++){		
						if(i == index){
							//delete this one
							removeShapes(true, getDataVector().get(index).get(0).toString());
						}
						if(i > index){
							defaults[i-1] = defaultscopy[i];
						}
					}
					// not exactly the best way to handle array data, should be fixed in case array size will be >50
					defaults = Arrays.copyOf(defaults, defaults.length-1);
				}
			
			} else {
				if(index < knowns.length){					 
					ClassGraphics[] knownscopy = Arrays.copyOf(knowns, knowns.length);	
					for (i = 0;  i < knownscopy.length; i++){	
						if(i == index){
							//delete this one
							removeShapes(false, getDataVector().get(index).get(0).toString());
						}
						if(i > index){
							knowns[i-1] = knownscopy[i];
						}
					}
					// not exactly the best way to handle array data, should be fixed in case array size will be >50
					knowns = Arrays.copyOf(knowns, knowns.length-1);
				}
				
			}
		}

		public void removeShapes (boolean def, String name){
			ClassCanvas canvas = ClassEditor.getInstance().getCurrentCanvas();
			canvas.removeObjectByName(name, def);
			canvas.repaint();
		//	ClassEditor.getInstance().graphicsToShapes(canvas, cg, getValueAt(arg0.getFirstRow(),0).toString(), true);
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
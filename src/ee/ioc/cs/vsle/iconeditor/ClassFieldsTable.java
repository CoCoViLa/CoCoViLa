package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.util.queryutil.DBResult;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Title:        ClassFieldsTable.
 * Description:  Table for class fields displayed in the Class Propertied dialog
                 in the Icon Editor application.
 * Copyright:    2004
 * @author Aulo Aasmaa
 * @version 1.0
 */

public class ClassFieldsTable extends JTable {

  /**
   * Table constructor.
   */
  public ClassFieldsTable() {
  }

  /**
   * Set DBResult to application's JTable component.
   *
   * @param dbr - DBResult to show in the JTable component.
   */
  public void setData(final DBResult dbr) {
    super.setModel(new ClassFieldsTableModel(dbr));
    for (int i = 0; i < RuntimeProperties.classTblFields.length; i++) {
      super.getColumn(RuntimeProperties.classTblFields[i]).setCellRenderer(new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable tblDataTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          JLabel ret = (JLabel)super.getTableCellRendererComponent(tblDataTable, value, isSelected, hasFocus, row, column);
          return ret;
        }
      });
    } // end for loop

    if (dbr != null) {
      TableColumn column;

      column = getColumnModel().getColumn(0);
      column.setPreferredWidth(100);
      column.setMinWidth(column.getPreferredWidth());

      column = getColumnModel().getColumn(1);
      column.setPreferredWidth(100);
      column.setMinWidth(column.getPreferredWidth());

      column = getColumnModel().getColumn(2);
      column.setPreferredWidth(100);
      column.setMinWidth(column.getPreferredWidth());

    }
  }

} // end class ClassFieldsTable
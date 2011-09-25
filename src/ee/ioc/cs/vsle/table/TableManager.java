/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.api.Package;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

/**
 * Class for caching tables and providing access to the tables from outer packages
 */
public class TableManager {

    private static Map<Package, Map<String, Table>> tablesByPackages = new HashMap<Package, Map<String, Table>>();
    
    private static final FilenameFilter FILENAME_FILTER = new FilenameFilter() {

        @Override
        public boolean accept( File dir, String name ) {
            
            return name.toLowerCase().endsWith( CustomFileFilter.EXT.TBL.getExtension() );
        }
    };
    
    /**
     * Calls parser and caches tables for the specified package
     * 
     * @param pack
     * @param tableId
     * @return
     */
    public synchronized static IStructuralExpertTable getTable( Package pack, String tableId ) {
        
        if( !tablesByPackages.containsKey( pack ) ) {
            
            Map<String, Table> tables = new HashMap<String, Table>();
            
            File folder = new File( pack.getPath() );
            
            if( !folder.isDirectory() ) {
                folder = folder.getParentFile();
            }
            
            File[] tableFiles = folder.listFiles( FILENAME_FILTER );
            
            for ( int i = 0; i < tableFiles.length; i++ ) {
                
                //db.p( "Parsing table(s) from: " + tableFiles[i].getAbsolutePath() );
                
                tables.putAll( new TableXmlProcessor( tableFiles[i] ).parse() );
            }
            
            //this line can be reached if no errors occurred during parsing 
            tablesByPackages.put( pack, tables );
        }
        
        IStructuralExpertTable table = tablesByPackages.get( pack ).get( tableId );
        
        if( table != null ) {
            return table;
        }
        
        final String msg = "No such table: " + tableId;
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog( null, msg + "\n(NB! Table ids are case sensitive!)", "Error", JOptionPane.ERROR_MESSAGE );
            }
        } );
        
        throw new TableException( msg );
    }
    
    /**
     * Assumes that tables have been modified and forces to parses them again
     * 
     * @param pack
     */
    public synchronized static void updateTables( Package pack ) {
        
        Map<String, Table> tables = tablesByPackages.remove( pack );
        
        if( tables != null ) {
            for ( Table table : tables.values() ) {
                table.destroy();
            }
            tables.clear();
        }
    }

    /**
     * Given a file, returns possibly empty list of tables 
     * (mapping of table ids to their instances)
     * @param file
     * @return
     */
    public static Map<String, Table> getTablesFromFile( File file ) {
        
        try {
            return new TableXmlProcessor( file ).parse();
        } catch( TableException e ) {
            if( RuntimeProperties.isLogDebugEnabled() ) {
                e.printStackTrace();
            }
        }
        return new HashMap<String, Table>();
    }
    
    /**
     * Shows file chooser and if there is more that one table in a file,
     * displays a dialog to choose a table.
     * Returns a chosen table and its corresponding file, otherwise null
     * 
     * @param parent
     * @param path
     * @return
     */
    public static Pair<Table, File> openTable( Window parent, String path ) {

        JFileChooser fc = new JFileChooser( path );
        fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.TBL ) );
        fc.setDialogType( JFileChooser.OPEN_DIALOG );

        Pair<Table, File> pair = new Pair<Table, File>( null, null );
        
        if ( fc.showOpenDialog( parent ) 
                == JFileChooser.APPROVE_OPTION ) {

            Map<String, Table> tables = 
                    getTablesFromFile( fc.getSelectedFile() );

            pair = pair.setAtSecond( fc.getSelectedFile() );
            
            final Table table;

            if( tables.size() == 1 ) {
                table = tables.values().iterator().next();

            } else if( tables.size() > 1 ) {

                Object[] opts = tables.keySet().toArray();

                int res = JOptionPane.showOptionDialog( parent,
                        "Choose table", "Tables",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, opts, opts[0] ); 

                if( res != JOptionPane.CLOSED_OPTION ) {
                    table = tables.get( opts[res] );
                } else {
                    return pair;
                }
            } else {
                return pair;
            }

            return pair.setAtFirst( table );
        }
        return pair;   
    }
}

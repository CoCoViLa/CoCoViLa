/**
 * 
 */
package ee.ioc.cs.vsle.table;

import java.io.*;
import java.util.*;

import ee.ioc.cs.vsle.api.Package;
import ee.ioc.cs.vsle.util.*;

/**
 * Class for caching tables and providing access to the tables from outer packages
 */
public class TableManager {

    private static Map<Package, Map<String, Table>> tablesByPackages = new HashMap<Package, Map<String, Table>>();
    
    /**
     * Calls parser and caches tables for the specified package
     * 
     * @param pack
     * @param tableId
     * @return
     */
    public synchronized static Table getTable( Package pack, String tableId ) {
        
        if( !tablesByPackages.containsKey( pack ) ) {
            String path;
            File packFile = new File( pack.getPath() );
            
            if( packFile.isDirectory() ) {
                path = packFile.getAbsolutePath();
            } else {
                path = packFile.getParent();
            }
            
            db.p( "Parsing table(s) from: " + path + File.separator + "table.xml" );
            
            tablesByPackages.put( pack, TableParser.parse( path + File.separator + "table.xml" ) );
        }
        
        return tablesByPackages.get( pack ).get( tableId );
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
     * @param args
     */
    public static void main( String[] args ) {
        // TODO Auto-generated method stub
//        ProgramContext.getScheme().
    }

}

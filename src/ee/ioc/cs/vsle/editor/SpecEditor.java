/**
 * 
 */
package ee.ioc.cs.vsle.editor;

import java.io.*;

import javax.swing.*;

import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 * Class for testing specification without the Scheme Editor
 */
public class SpecEditor {


    /**
     * @param args
     */
    public static void main( String[] args ) {
        RuntimeProperties.init();
        System.out.println( "CP: " + RuntimeProperties.getCompilationClasspath());
        
        Look.getInstance().initDefaultLnF();
        
        String path = RuntimeProperties.getLastPath();
        
        path = ( path != null && new File( path ).exists() ) ? path 
                : RuntimeProperties.getWorkingDirectory();
        
        JFileChooser fc = new JFileChooser( path );
        
        fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.XML ) );

        int returnVal = fc.showOpenDialog( null );

        final PackageParser packageLoader = new PackageParser();
        
        SchemeContainer container = null;
        
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File pack = fc.getSelectedFile();

            db.p( "Loading package: " + pack.getName() );
            
            if( packageLoader.load( pack ) ) {

                container = new SchemeContainer( packageLoader.getPackage(), packageLoader.getPath() );

                if( JOptionPane.showConfirmDialog( null, "Open a scheme?", "Open", JOptionPane.YES_NO_OPTION ) 
                        == JOptionPane.OK_OPTION ) {
                    fc = new JFileChooser( packageLoader.getPath() );
                    fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.SYN ) );
                    returnVal = fc.showOpenDialog( null );
                    File schemeFile = fc.getSelectedFile();

                    if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                        container.loadScheme( schemeFile );
                    }
                }
            }
            
        } 
        
        if( container == null ){
            return;
        }
        
        final ProgramRunner runner = new ProgramRunner( container );

        ProgramTextEditor programEditor = new ProgramTextEditor( runner.getId(), container.getPackage().getName() );

        programEditor.setSize( 700, 450 );
        programEditor.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        programEditor.setVisible( true );
        
    }
    
}

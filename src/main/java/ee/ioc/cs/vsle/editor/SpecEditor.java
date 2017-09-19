/**
 * 
 */
package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.*;

import javax.swing.*;

import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

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

        SchemeContainer container = null;
        
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File packFile = fc.getSelectedFile();

            System.out.println( "Loading package: " + packFile.getName() );
            VPackage pkg;
            if((pkg = PackageXmlProcessor.load(packFile)) != null ) {

                container = new SchemeContainer( pkg, packFile.getParent() + File.separator );

                if( JOptionPane.showConfirmDialog( null, "Open a scheme?", "Open", JOptionPane.YES_NO_OPTION ) 
                        == JOptionPane.OK_OPTION ) {
                    fc = new JFileChooser( packFile.getParent() + File.separator );
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

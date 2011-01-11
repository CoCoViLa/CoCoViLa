package ee.ioc.cs.vsle.editor.scheme;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

public class SchemeExporter {

    public static void makeSchemeExportMenu( JMenu exportMenu,
            EditorActionListener aListener ) {
        
        JMenu submenu = new JMenu( Menu.MENU_SCHEME );
        exportMenu.add(submenu);
        
        JMenuItem menuItem = new JMenuItem( Menu.EXPORT_SCHEME );
        menuItem.addActionListener( aListener );
        submenu.add( menuItem );

        //Show Scheme Export dialog
        menuItem = new JMenuItem( Menu.EXPORT_SCHEME_AS_OBJECT );
        menuItem.addActionListener( aListener );
        submenu.add( menuItem );
    }

    public static void exportSchemeSpecification( Scheme scheme, String schemeTitle ) {

        VPackage pack = scheme.getPackage();
        
        File file = null;
        
        if( schemeTitle != null ) {
            file = new File( pack.getPath() + File.separator + schemeTitle );
        }
        
        if( (file = FileFuncs.showFileChooser( pack.getPath(), file, 
                SpecGenFactory.getInstance().getCurrentSpecGen().getFileFilter(), 
                Editor.getInstance(), true ) ) != null ) {
            
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            
            if ( RuntimeProperties.isLogInfoEnabled() )
                db.p( "Exporting scheme specification into: " + file.getName() );

            String className = file.getName();

            if( className.lastIndexOf( "." ) > -1 ) {
                className = className.substring( 0, className.lastIndexOf( "." ) );
            }

            String spec = SpecGenFactory.getInstance().getCurrentSpecGen().generateSpec( scheme, className );

            FileFuncs.writeFile( file, spec );
        }
    }
      
    public static void exportAsObject(Canvas canv) {
        
        ArrayList<GObj> selectedObjects = canv.getObjects().getSelected();
        ArrayList<Port> connectedPortsToOuterObjects = new ArrayList<Port>();
        
        if ( selectedObjects.size() > 1 ) {
            
            Set<SelectedObjConnectionToOuterObj> outerConnections = new LinkedHashSet<SelectedObjConnectionToOuterObj>();
            Set<Connection> innerConnections = new HashSet<Connection>();
            
            for ( GObj obj : selectedObjects ) {
//                System.out.println( obj );
                //find all connections with objects that are not selected
                for( Connection con : obj.getConnections() ) {
                    Port innerPort = con.getBeginPort();
                    GObj innerObj = innerPort.getObject();
                    Port outerPort = con.getEndPort();
                    GObj outerObj = outerPort.getObject();
                    if( innerObj == obj && !selectedObjects.contains( outerObj ) ) {
                    } else if( outerObj == obj && !selectedObjects.contains( innerObj ) ) {
                        outerObj = innerObj;
                        outerPort = innerPort;
                        innerObj = obj;
                        innerPort = con.getEndPort();
                    } else {
//                        System.out.println( con );
                        innerConnections.add( con );
                        continue;
                    }
                    outerConnections.add( new SelectedObjConnectionToOuterObj( innerObj, innerPort, outerObj, outerPort, con ) );
                    connectedPortsToOuterObjects.add( innerPort );
                    System.out.println(con);
                }
            }
            
//            System.out.println("Ports: " + connectedPortsToOuterObjects);
            
            SchemeContainer container = new SchemeContainer( canv.getPackage(), canv.getWorkDir() );
            //save new scheme
            Scheme newScheme = new Scheme(
                    container, 
                    new ObjectList( selectedObjects ), 
                    new ConnectionList( innerConnections ));
            container.setScheme( newScheme );
            System.out.println(selectedObjects);
            new SchemeExportDialog( container, connectedPortsToOuterObjects ).setVisible( true );
            
            if( true ) return;
            
            File file = FileFuncs.showFileChooser( canv.getPackage().getPath(), null, 
                    new CustomFileFilter( CustomFileFilter.EXT.SYN ), Editor.getInstance(), true );
            
            if( file == null ) 
                return;
            
            newScheme.saveToFile( file );
            
            
            //update package with new scheme class and icon
            //1. Write to package TODO
            String schemeName = FileFuncs.getName( file );
            new PackageXmlProcessor( new File(canv.getPackage().getPath()) ).addClassObject( schemeName );
            //2. Reload package
            canv.reloadCurrentPackage();
            
            //update canvas
            //1. remove objects
            canv.deleteSelectedObjects();
            //2. add new instance of newly created component TODO
            //3. modify connections TODO
            //4.repaint
            canv.repaint();
            
        }
    }
    
    static class SelectedObjConnectionToOuterObj {
        
        private GObj innerObj;
        private Port innerPort;
        private GObj outerObj;
        private Port outerPort;
        private Connection conn;
        
        public SelectedObjConnectionToOuterObj( GObj innerObj, Port innerPort,
                GObj outerObj, Port outerPort, Connection conn ) {
            super();
            this.innerObj = innerObj;
            this.innerPort = innerPort;
            this.outerObj = outerObj;
            this.outerPort = outerPort;
            this.conn = conn;
        }

        @Override
        public String toString() {
            return innerObj.getName() + " : " + conn.toString();
        }
    }

}

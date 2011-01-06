package ee.ioc.cs.vsle.editor.scheme;

import java.io.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
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

    public static void exportAsObject(Canvas canv) {
        ArrayList<GObj> selected = canv.getObjects().getSelected();
        if ( selected.size() > 1 ) {
            
            Set<SelectedObjConnectionToOuterObj> outerConnections = new LinkedHashSet<SelectedObjConnectionToOuterObj>();
            Set<Connection> innerConnections = new HashSet<Connection>();
            
            for ( GObj obj : selected ) {
//                obj.setSelected( false );
                //find all connections with objects that are not selected
                for( Connection con : obj.getConnections() ) {
                    GObj start = con.getBeginPort().getObject();
                    GObj end = con.getEndPort().getObject();
                    if( start == obj && !selected.contains( end ) ) {
                    } else if( end == obj && !selected.contains( start ) ) {
                        end = start;
                        start = obj;
                    } else {
                        innerConnections.add( con );
                        continue;
                    }
                    outerConnections.add( new SelectedObjConnectionToOuterObj( start, end, con ) );
                }
            }
            
            //save new scheme
            Scheme newScheme = new Scheme(
                    new SchemeContainer( canv.getPackage(), canv.getWorkDir() ), 
                    new ObjectList( selected ), 
                    new ConnectionList( innerConnections ));
            
            newScheme.saveToFile( new File( "/home/pavelg/workspace/cocovila_packages/Circuit/test.syn" ) );
            
            //update package with new scheme class and icon
            //1. Write to package TODO
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
    
    private static class SelectedObjConnectionToOuterObj {
        
        private GObj selected;
        private GObj outer;
        private Connection conn;
        /**
         * @param selected
         * @param outer
         * @param conn
         */
        private SelectedObjConnectionToOuterObj( GObj selected, GObj outer,
                Connection conn ) {
            super();
            this.selected = selected;
            this.outer = outer;
            this.conn = conn;
            System.err.println("outer: " + conn);
        }
    }

}

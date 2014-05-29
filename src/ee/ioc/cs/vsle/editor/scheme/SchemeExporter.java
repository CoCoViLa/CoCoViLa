package ee.ioc.cs.vsle.editor.scheme;

import java.awt.Point;
import java.io.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
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
      
    /**
     * Exports selected objects to a scheme and if needed, 
     * instantiates an object of this scheme and 
     * replaces selected objects with it preserving connections
     * if corresponding ports have been chosen
     * 
     * @param canv
     */
    public static void exportAsObject(Canvas canv) {
        
        ArrayList<GObj> selectedObjects = canv.getObjectList().getSelected();
        
        if ( selectedObjects.size() > 1 ) {
            
            Map<String, List<Connection>> innerPortNamesToConnections = new LinkedHashMap<String, List<Connection>>();
            Map<String, String> innerPortNamesToTypes = new LinkedHashMap<String, String>();
            List<ClassField> newFields = new ArrayList<ClassField>();
            Set<Connection> innerConnections = new HashSet<Connection>();
            
            for ( GObj obj : selectedObjects ) {
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
                        innerConnections.add( con );
                        continue;
                    }
                    
                    String portName = innerPort.getNameWithObject();
                    List<Connection> conList;
                    if( ( conList = innerPortNamesToConnections.get( portName ) ) == null ) {
                        conList = new ArrayList<Connection>();
                        innerPortNamesToConnections.put( portName, conList );
                    }
                    conList.add( con );
                    
                    if( innerPortNamesToTypes.containsKey( portName ) ) {
                        innerPortNamesToTypes.put( portName, innerPort.getField().getType() );
                    }
                }
                for( ClassField cf : obj.getFields() ) {
                    ClassField newCf = new ClassField( obj.getName() + "." + cf.getName(), cf.getType(), cf.getValue() );
                    newCf.setGoal( cf.isGoal() );
                    newCf.setInput( cf.isInput() );
                    newFields.add( newCf );
                }
            }
            
            SchemeContainer container = new SchemeContainer( canv.getPackage(), canv.getWorkDir() );
            //save new scheme
            Scheme newScheme = new Scheme(
                    container, 
                    new ObjectList( selectedObjects ), 
                    new ConnectionList( innerConnections ));
            container.setScheme( newScheme );
            
            SchemeExportDialog sed = new SchemeExportDialog( container, innerPortNamesToConnections.keySet() );
            
            if( !sed.isOk() ) {
                return;
            }
            
            String schemeName = sed.getSchemeName();
            String schemeDescription = sed.getDescription();
            String[] selectedPorts = sed.getSelectedPortNames();
            Point[] portPoints = sed.getPortPoints();
            VPackage selectedPackage = sed.getSelectedPackage();
            boolean modify = sed.shouldModifyCurrentScheme();
            ClassGraphics cg = sed.getClassGraphics();
            
            File packageFile = new File( selectedPackage.getPath() );
            File schemeFile = new File( packageFile.getParent(), schemeName + ".syn" );
            
            if( schemeFile.exists() && !FileFuncs.askToOverwriteFile( schemeFile, canv ) ) {
                return;
            }
            
            newScheme.saveToFile( schemeFile );
            
            PackageClass pc = new PackageClass( schemeName );
            pc.setIcon( sed.getImageFilename() );
            pc.setDescription( schemeDescription );
            
            pc.addGraphics( cg );
            for ( int i = 0; i < selectedPorts.length; i++ ) {
                String portName = selectedPorts[i];
                Point portLoc = portPoints[i];
                
                Port port = new Port( portName, 
                        innerPortNamesToTypes.containsKey( portName ) 
                            ? innerPortNamesToTypes.get( portName ) : "", 
                        portLoc.x, portLoc.y, null, false, false );
                
                pc.addPort( port );
            }
            
            if( sed.shouldExportFields() ) {
                for ( ClassField cf : newFields ) {
                    pc.addField( cf );
                }
            }
            //update package with new scheme class and icon
            //1. Write to package
            new PackageXmlProcessor( packageFile ).addPackageClass( pc );
            
            //2. Reload package
            canv.reloadCurrentPackage();
            
            if( modify ) {
                //update canvas
                //1. remove objects
                canv.deleteSelectedObjects();
                //2. add new instance of newly created component
                GObj obj = canv.createAndInitNewObject( schemeName );
                GObjGroup objGroup = new GObjGroup( selectedObjects );
                Point center = new Point(objGroup.getX() + objGroup.getWidth()/2, objGroup.getY() + objGroup.getHeight()/2);
                //set location, e.g. center of selected objects' group
                obj.setX( center.x - obj.getWidth()/2 );
                obj.setY( center.y - obj.getHeight()/2 );
                
                canv.addCurrentObject();
                
                //3. modify connections
                for( Port newPort : obj.getPortList() ) {
                    
                    String portName = newPort.getName();
                    if( innerPortNamesToConnections.containsKey( portName ) ) {
                        
                        List<Connection> conList = innerPortNamesToConnections.get( portName );
                        if( conList != null ) {
                            for ( Connection connection : conList ) {
                                if( connection.getBeginPort().getNameWithObject().equals( portName ) ) {
                                    connection.setBeginPort( newPort );
                                } else {
                                    connection.setEndPort( newPort );
                                }
                                connection.setStrict( false );
                                canv.addConnection( connection );
                            }
                        }
                    }
                }
                
                //4.repaint
                canv.repaint();
            }
        } else {
            JOptionPane.showMessageDialog( canv, "Two or more objects have to be selected!" );
        }
    }
}

package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.iconeditor.AboutDialog;
import ee.ioc.cs.vsle.iconeditor.LicenseDialog;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 9.06.2004
 * Time: 22:12:14
 * To change this template use Options | File Templates.
 */
public class EditorActionListener implements ActionListener {
    Editor editor;

    public EditorActionListener( Editor editor ) {
        this.editor = editor;
    }

    public void actionPerformed( ActionEvent e ) {

        // JmenuItem chosen
        if ( e.getSource().getClass().getName() == "javax.swing.JMenuItem" ||
             e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem" ) {

            if ( e.getActionCommand().equals( Menu.SAVE_SCHEME ) ) {

                JFileChooser fc = new JFileChooser( editor.getLastPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.extensionSyn,
                        CustomFileFilter.descriptionSyn );
                fc.setFileFilter( synFilter );
                int returnVal = fc.showSaveDialog( null );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {

                    File file = fc.getSelectedFile();

                    if ( !file.getAbsolutePath().toLowerCase().endsWith( CustomFileFilter.
                            extensionSyn ) ) {
                        file = new File( file.getAbsolutePath() + "." +
                                         CustomFileFilter.extensionSyn );
                    }

                    editor.setLastPath( file.getAbsolutePath() );
                    db.p( "Saving scheme: " + file.getName() );
                    editor.getCurrentCanvas().saveScheme( file );
                }
            } else if ( e.getActionCommand().equals( Menu.LOAD_SCHEME ) ) {
                JFileChooser fc = new JFileChooser( editor.getLastPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.extensionSyn,
                        CustomFileFilter.descriptionSyn );
                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( null );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fc.getSelectedFile();
                    db.p( "Loading scheme: " + file.getName() );
                    try {
                        editor.getCurrentCanvas().loadScheme( file );
                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }
                }

            } else if ( e.getActionCommand().equals( Menu.LOAD ) ) {
                JFileChooser fc = new JFileChooser( editor.getLastPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.extensionXML,
                        CustomFileFilter.descriptionXML );

                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( editor );

                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File pack = fc.getSelectedFile();

                    editor.setLastPath( pack.getAbsolutePath() );
                    db.p( "Loading package: " + pack.getName() );
                    editor.loadPackage( pack );
                    editor.validate();
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE ) ) {
                editor.clearPane();
            } else if ( e.getActionCommand().equals( Menu.INFO ) ) {
                String message;
                if ( editor.getCurrentPackage() != null ) {
                    message = editor.getCurrentPackage().description;
                } else {
                    message = "No packages loaded";
                }
                JOptionPane.showMessageDialog( null, message );
            } else if ( e.getActionCommand().equals( Menu.PRINT ) ) {
                editor.getCurrentCanvas().print();
            } else if ( e.getActionCommand().equals( Menu.EXIT ) ) {
                editor.exitApplication();
            } else if ( e.getActionCommand().equals( Menu.GRID ) ) {
                editor.getCurrentCanvas().setGridVisible( !editor.getCurrentCanvas().isGridVisible() );
            } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
                editor.getCurrentCanvas().clearObjects();
            } else if ( e.getActionCommand().equals( Menu.SPECIFICATION ) ) {
                ProgramTextEditor programEditor = new ProgramTextEditor( editor.getCurrentCanvas().
                        connections, editor.getCurrentCanvas().objects,
                                                  editor.getCurrentCanvas().vPackage, editor );

                programEditor.setSize( 550, 450 );
                programEditor.setVisible( true );
            }
            /* else if (e.getActionCommand().equals("Planner")) {
              PlannerEditor plannerEditor = new PlannerEditor(objects, connections);
              plannerEditor.setSize(450, 260);
              plannerEditor.setVisible(true);

              } */
            else if ( e.getActionCommand().equals( Menu.SELECT_ALL ) ) {
                editor.getCurrentCanvas().selectAllObjects();
            }
            /* else if (e.getActionCommand().equals("Run")) {
              ee.ioc.cs.editor.editor.ResultsWindow resultsWindow= new ee.ioc.cs.editor.editor.ResultsWindow(objects, connections, classes);
              resultsWindow.setSize(450, 260);
              resultsWindow.setVisible(true);
              }*/
            else if ( e.getActionCommand().equals( Menu.DOCS ) ) {
                String documentationUrl = editor.getSystemDocUrl();

                if ( documentationUrl != null && documentationUrl.trim().length() > 0 ) {
                    editor.openInBrowser( documentationUrl );
                } else {
                    editor.showInfoDialog( "Missing information",
                                           "No documentation URL defined in properties." );
                }

            } else if ( e.getActionCommand().equals( Menu.SETTINGS ) ) {
                editor.openOptionsDialog();
            } else if ( e.getActionCommand().equals( Menu.ABOUT ) ) {
                new AboutDialog( null, editor );
            } else if ( e.getActionCommand().equals( Menu.LICENSE ) ) {
                new LicenseDialog( null, editor );
            } else if ( e.getActionCommand().equals( Look.LOOK_WINDOWS ) ) {
                try {
                    Look.changeLayout( Look.LOOK_WINDOWS );
                } catch ( Exception uie ) {
                }
            } else if ( e.getActionCommand().equals( Look.LOOK_METAL ) ) {
                try {
                    Look.changeLayout( Look.LOOK_METAL );
                } catch ( Exception uie ) {
                }
            } else if ( e.getActionCommand().equals( Look.LOOK_MOTIF ) ) {
                try {
                    Look.changeLayout( Look.LOOK_MOTIF );
                } catch ( Exception uie ) {
                }
            } else if ( e.getActionCommand().equals( Look.LOOK_CUSTOM ) ) {
                try {
                    Look.changeLayout( Look.LOOK_CUSTOM );
                } catch ( Exception uie ) {
                }
            }
        }
    }
}

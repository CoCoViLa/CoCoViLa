package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.iconeditor.AboutDialog;
import ee.ioc.cs.vsle.iconeditor.LicenseDialog;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;

public class EditorActionListener implements ActionListener {
	
    public void actionPerformed( ActionEvent e ) {

        // JmenuItem chosen
        if ( e.getSource().getClass().getName() == "javax.swing.JMenuItem" ||
             e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem" ) {

            if ( e.getActionCommand().equals( Menu.SAVE_SCHEME ) ) {
                if( Editor.getInstance().getCurrentPackage() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                }

                JFileChooser fc = new JFileChooser( Editor.getInstance().getCurrentPackage().getPath() );
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

                    Editor.setLastPath( file.getAbsolutePath() );
                    db.p( "Saving scheme: " + file.getName() );
                    Editor.getInstance().getCurrentCanvas().saveScheme( file );
                    Editor.getInstance().getCurrentPackage().setLastScheme( file.getAbsolutePath() );
                }
            } else if ( e.getActionCommand().equals( Menu.LOAD_SCHEME ) ) {
                if( Editor.getInstance().getCurrentPackage() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                }
                JFileChooser fc = new JFileChooser( Editor.getInstance().getCurrentPackage().getPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.extensionSyn,
                        CustomFileFilter.descriptionSyn );
                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( null );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fc.getSelectedFile();
                    Editor.setLastPath( file.getAbsolutePath() );
                    if ( RuntimeProperties.isLogDebugEnabled() ) 
                    	db.p( "Loading scheme: " + file.getName() );
                    try {
                        Editor.getInstance().getCurrentCanvas().loadScheme( file );
                        Editor.getInstance().getCurrentPackage().setLastScheme( file.getAbsolutePath() );
                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }
                }

            } else if ( e.getActionCommand().equals( Menu.RELOAD_SCHEME ) ) {
            	if( Editor.getInstance().getCurrentPackage() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                } else if( Editor.getInstance().getCurrentPackage().getLastScheme() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No scheme has been recently saved or loaded", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                }
            	File file = new File( Editor.getInstance().getCurrentPackage().getLastScheme() );
            	if( file.exists() ) {
            		db.p( "Reloading scheme: " + file.getName() );
            		try {
            			Editor.getInstance().getCurrentCanvas().loadScheme( file );
            		} catch ( Exception exc ) {
            			exc.printStackTrace();
            		}
            	}
            } else if ( e.getActionCommand().equals( Menu.LOAD ) ) {
                JFileChooser fc = new JFileChooser( 
                		( Editor.getLastPath() != null && new File(Editor.getLastPath()).exists() )
                		? Editor.getLastPath() : RuntimeProperties.getWorkingDirectory() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.extensionXML,
                        CustomFileFilter.descriptionXML );

                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( Editor.getInstance() );

                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File pack = fc.getSelectedFile();

                    Editor.setLastPath( pack.getAbsolutePath() );
                    PropertyBox.setMultiProperty( PropertyBox.RECENT_PACKAGES, pack.getAbsolutePath(), true );
                    PropertyBox.setMultiProperty( PropertyBox.PALETTE_FILE, pack.getAbsolutePath(), true );
                    db.p( "Loading package: " + pack.getName() );
                    Editor.getInstance().loadPackage( pack );
                    //Editor.getInstance().validate();
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE ) ) {
                if ( Editor.getInstance().getCurrentPackage() != null ) {
                    PropertyBox.setMultiProperty( PropertyBox.PALETTE_FILE, Editor.getInstance().getCurrentPackage().getPath(), false );
                    Editor.getInstance().clearPane();
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE_ALL ) ) {
                while ( Editor.getInstance().getCurrentPackage() != null ) {
                    PropertyBox.setMultiProperty( PropertyBox.PALETTE_FILE, Editor.getInstance().getCurrentPackage().getPath(), false );
                    Editor.getInstance().clearPane();
                }
            } else if ( e.getActionCommand().equals( Menu.RELOAD ) ) {
                if ( Editor.getInstance().getCurrentPackage() != null ) {
                	File pack = new File(Editor.getInstance().getCurrentPackage().getPath());
                	if( pack.exists() ) {
                		Editor.getInstance().clearPane();
                		Editor.setLastPath( pack.getAbsolutePath() );
                		Editor.getInstance().loadPackage( pack );
                	}
                }
            } else if ( e.getActionCommand().equals( Menu.INFO ) ) {
                String message;
                if ( Editor.getInstance().getCurrentPackage() != null ) {
                    message = Editor.getInstance().getCurrentPackage().description;
                } else {
                    message = "No packages loaded";
                }
                JOptionPane.showMessageDialog( Editor.getInstance(), message );
            } else if ( e.getActionCommand().equals( Menu.PRINT ) ) {
                if( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().print();
                }
            } else if ( e.getActionCommand().equals( Menu.EXIT ) ) {
                Editor.getInstance().exitApplication();
            } else if ( e.getActionCommand().equals( Menu.GRID ) ) {
                if( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().setGridVisible(
                            !Editor.getInstance().getCurrentCanvas().isGridVisible() );
                }
            } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
                if( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().clearObjects();
                }
            } else if ( e.getActionCommand().equals( Menu.SPECIFICATION ) ) {
            	Canvas canv = Editor.getInstance().getCurrentCanvas();
                if( canv != null ) {
                	
                	JFrame frame = ProgramTextEditor.getFrame( canv.getTitle() );
                	if( frame != null ) {
                		
                		if( frame.getState() == JFrame.ICONIFIED ) {
                			frame.setState( JFrame.NORMAL );
                		}
                		
                		frame.toFront();
                		return;
                	}
                	
                	final ProgramRunner runner = new ProgramRunner( canv );
                	
            		ProgramTextEditor programEditor = new ProgramTextEditor( runner.getId(), canv.getTitle() );
            		
            		programEditor.addWindowListener( new WindowAdapter(){

            			public void windowClosing(WindowEvent e) {
            				runner.destroy();
            			}
                    });
            		
                    programEditor.setSize( 700, 450 );
                    programEditor.setVisible( true );
                } else {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
                }
            } else if ( e.getActionCommand().equals( Menu.RUN ) ) {
            	Canvas canv = Editor.getInstance().getCurrentCanvas();
                if( canv != null ) {
                	
                	final ProgramRunner runner = new ProgramRunner( canv );
            		
            		int op =  ProgramRunnerEvent.COMPUTE_ALL
            				| ProgramRunnerEvent.RUN_NEW
            				| ProgramRunnerEvent.DESTROY;
            		
            		ProgramRunnerEvent evt = new ProgramRunnerEvent( this, runner.getId(), op );
            		
            		EventSystem.queueEvent( evt );
            	} else {
            		JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
            	}
            } else if ( e.getActionCommand().equals( Menu.RUNPROPAGATE ) ) {
            	Canvas canv = Editor.getInstance().getCurrentCanvas();
                if( canv != null ) {
                	
                	final ProgramRunner runner = new ProgramRunner( canv );
            		
            		int op =  ProgramRunnerEvent.COMPUTE_ALL
    						| ProgramRunnerEvent.RUN_NEW
    						| ProgramRunnerEvent.PROPAGATE
    						| ProgramRunnerEvent.DESTROY;
    		
            		ProgramRunnerEvent evt = new ProgramRunnerEvent( this, runner.getId(), op );
    		
            		EventSystem.queueEvent( evt );
            		           		
            	} else {
            		JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
            	}
            } else if ( e.getActionCommand().equals( Menu.SCHEMEOPTIONS ) ) {
            	new SchemeSettingsDialog(Editor.getInstance());
            }
            /* else if (e.getActionCommand().equals("Planner")) {
              PlannerEditor plannerEditor = new PlannerEditor(objects, connections);
              plannerEditor.setSize(450, 260);
              plannerEditor.setVisible(true);

              } */
            else if ( e.getActionCommand().equals( Menu.SELECT_ALL ) ) {
                if( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().selectAllObjects();
                }
            }
            /* else if (e.getActionCommand().equals("Run")) {
              ee.ioc.cs.editor.editor.ResultsWindow resultsWindow= new ee.ioc.cs.editor.editor.ResultsWindow(objects, connections, classes);
              resultsWindow.setSize(450, 260);
              resultsWindow.setVisible(true);
              }*/
            else if ( e.getActionCommand().equals( Menu.DOCS ) ) {
                String documentationUrl = Editor.getSystemDocUrl();

                if ( documentationUrl != null && documentationUrl.trim().length() > 0 ) {
                    Editor.openInBrowser( documentationUrl );
                } else {
                    Editor.getInstance().showInfoDialog( "Missing information",
                                           "No documentation URL defined in properties." );
                }

            } else if ( e.getActionCommand().equals( Menu.SETTINGS ) ) {
                Editor.getInstance().openOptionsDialog();
            } else if ( e.getActionCommand().equals( Menu.ABOUT ) ) {
                new AboutDialog(Editor.getInstance());
            } else if ( e.getActionCommand().equals( Menu.LICENSE ) ) {
                new LicenseDialog(Editor.getInstance());
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
            } else if (e.getActionCommand().equals(Menu.CLASSPAINTER)) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if (canvas != null) {
                    canvas.setEnableClassPainter(!canvas.isEnableClassPainter());
                }
            }
        }
    }
}

package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.iconeditor.AboutDialog;
import ee.ioc.cs.vsle.iconeditor.LicenseDialog;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import java.awt.Frame;
import java.awt.event.*;
import java.io.File;

public class EditorActionListener implements ActionListener {
	
	/*
	 * For more information about Swing Actions see:
	 * "How to Use Actions"
	 * http://java.sun.com/docs/books/tutorial/uiswing/misc/action.html
	 */

	/**
	 * Undo Action. Invokes the undo() method on the active scheme.
	 * By default this action is bound to CTRL-z keys. 
	 */
	static class UndoAction extends AbstractAction {
	
		private static final long serialVersionUID = 1L;

		public UndoAction() {
			putValue(Action.NAME, Menu.UNDO);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent evt) {
			Canvas canvas = Editor.getInstance().getCurrentCanvas();
			if (canvas != null) {
				try {
					canvas.undoManager.undo();
				} catch (CannotUndoException e) {
					JOptionPane.showMessageDialog(Editor.getInstance(),
							"Undo failed. Please report the steps to reproduce this error.",
							"Cannot undo", JOptionPane.WARNING_MESSAGE);
					db.p(e);
				}
				Editor.getInstance().refreshUndoRedo();
				canvas.drawingArea.repaint();
			}
		}
	}

	/**
	 * Redo Action. Invokes the redo() method on the active scheme.
	 * By default this action is bound to CTRL-y keys.
	 */
	static class RedoAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RedoAction() {
			putValue(Action.NAME, Menu.REDO);
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent evt) {
			Canvas canvas = Editor.getInstance().getCurrentCanvas();
			if (canvas != null) {
				try {
					canvas.undoManager.redo();
				} catch (CannotRedoException e) {
					JOptionPane.showMessageDialog(Editor.getInstance(),
						"Redo failed. Please report the steps to reproduce this error.",
						"Cannot redo", JOptionPane.WARNING_MESSAGE);
					db.p(e);
				}

				Editor.getInstance().refreshUndoRedo();
				canvas.drawingArea.repaint();
			}
		}
	}

	/**
	 * Delete Action. Used for removing currently selected objects/connections
	 * from the scheme.
	 * By default this action is bound to the Delete key.
	 */
	static class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public DeleteAction() {
			putValue(Action.NAME, Menu.DELETE);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_D));
			putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_DELETE, 0));
		}

		public void actionPerformed(ActionEvent e) {
			Canvas canvas = Editor.getInstance().getCurrentCanvas();
			if (canvas != null)
				canvas.deleteObjects();
		}
	}

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
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.SYN );
                fc.setFileFilter( synFilter );
                int returnVal = fc.showSaveDialog( null );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {

                    File file = fc.getSelectedFile();

                    if ( !file.getAbsolutePath().toLowerCase().endsWith( CustomFileFilter.EXT.SYN.getExtension() ) ) {
                        file = new File( file.getAbsolutePath() + "." + CustomFileFilter.EXT.SYN.getExtension() );
                    }

                    Editor.setLastPath( file.getAbsolutePath() );
                    db.p( "Saving scheme: " + file.getName() );
                    Editor.getInstance().getCurrentCanvas().saveScheme( file );
                    Editor.getInstance().getCurrentPackage().setLastScheme( file.getAbsolutePath() );
                    Editor.getInstance().updateWindowTitle();
                }
            } else if ( e.getActionCommand().equals( Menu.LOAD_SCHEME ) ) {
                if( Editor.getInstance().getCurrentPackage() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
                    return;
                }
                JFileChooser fc = new JFileChooser( Editor.getInstance().getCurrentPackage().getPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.SYN );
                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog(Editor.getInstance());
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fc.getSelectedFile();
                    Editor.setLastPath( file.getAbsolutePath() );
                    if ( RuntimeProperties.isLogDebugEnabled() ) 
                    	db.p( "Loading scheme: " + file.getName() );
                    try {
                        if (Editor.getInstance().getCurrentCanvas().loadScheme(file)) {
                        	Editor.getInstance().getCurrentPackage().setLastScheme(file.getAbsolutePath());
                        	Editor.getInstance().updateWindowTitle();
                        }
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
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.XML );

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
                    Editor.getInstance().updateWindowTitle();
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE ) ) {
            	closeCurrentScheme();
            } else if (Menu.DELETE_SCHEME.equals(e.getActionCommand())) {
            	deleteCurrentScheme();
            } else if ( e.getActionCommand().equals( Menu.CLOSE_ALL ) ) {
                while ( Editor.getInstance().getCurrentPackage() != null ) {
                    PropertyBox.setMultiProperty( PropertyBox.PALETTE_FILE, Editor.getInstance().getCurrentPackage().getPath(), false );
                    Editor.getInstance().clearPane();
                }
            } else if ( e.getActionCommand().equals( Menu.RELOAD ) ) {
            	reloadCurrentPackage();
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
                		
                		if( frame.getState() == Frame.ICONIFIED ) {
                			frame.setState( Frame.NORMAL );
                		}
                		
                		frame.toFront();
                		return;
                	}
                	
                	final ProgramRunner runner = new ProgramRunner( canv );
                	
            		ProgramTextEditor programEditor = new ProgramTextEditor( runner.getId(), canv.getTitle() );
            		
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
            } else if ( e.getActionCommand().equals( Menu.SHOW_ALGORITHM ) ) {
            	JCheckBoxMenuItem check = (JCheckBoxMenuItem)e.getSource();
            	RuntimeProperties.showAlgorithm = check.isSelected();
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
            } else if (e.getActionCommand().equals(Menu.CLASSPAINTER)) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if (canvas != null) {
                    canvas.setEnableClassPainter(!canvas.isEnableClassPainter());
                }
            }
        }
    }

	/**
	 * Reloads the current package discarding the current scheme.
	 * The request is ignored if no package is open.
	 */
	private void reloadCurrentPackage() {
		Editor editor = Editor.getInstance();
		VPackage pkg = editor.getCurrentPackage();
		
        if (pkg != null) {
        	File pkgFile = new File(pkg.getPath());
        	if (pkgFile.exists()) {
        		Editor.getInstance().clearPane();
        		Editor.setLastPath(pkgFile.getAbsolutePath());
        		Editor.getInstance().loadPackage(pkgFile);
        	}
        }
	}

	/**
	 * Closes the current scheme and removes the file the scheme was loaded
	 * from. The user is asked for confirmation.
	 */
	private void deleteCurrentScheme() {
		Editor editor = Editor.getInstance();
    	Canvas canvas = editor.getCurrentCanvas();
    	if (canvas != null) {
    		String lastScheme = canvas.vPackage.getLastScheme();
    		if (lastScheme == null) {
    			int rv = JOptionPane.showConfirmDialog(canvas, 
    					"The scheme is not saved. Are you sure you want to "
    					+ "close it?", "Confirm Delete",
    					JOptionPane.YES_NO_OPTION);
    			if (rv == JOptionPane.YES_OPTION)
    				reloadCurrentPackage();
    		} else {
    			int rv = JOptionPane.showConfirmDialog(editor,
    					"Are you sure you want to delete the current scheme"
    					+ " and permanently remove the file\n"
    					+ lastScheme + "?", "Confirm Delete",
    				JOptionPane.YES_NO_OPTION);
    			if (rv == JOptionPane.YES_OPTION) {
    				if (new File(lastScheme).delete())
    					reloadCurrentPackage();
    				else
    					JOptionPane.showMessageDialog(canvas, "Cannot remove"
    							+ " the file " + lastScheme + ".\nPlease check"
    							+ " the permissions.");
    			}
    		}
    	}
	}

	/**
	 * Closes current scheme.
	 */
	private void closeCurrentScheme() {
		Editor editor = Editor.getInstance();
        if (editor.getCurrentPackage() != null) {
            PropertyBox.setMultiProperty(PropertyBox.PALETTE_FILE,
            		editor.getCurrentPackage().getPath(), false);
            editor.clearPane();
        }
	}
}

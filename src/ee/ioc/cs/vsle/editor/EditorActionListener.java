package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.undo.*;

import ee.ioc.cs.vsle.common.gui.AboutDialog;
import ee.ioc.cs.vsle.common.gui.LicenseDialog;
import ee.ioc.cs.vsle.editor.scheme.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.iconeditor.*;
import ee.ioc.cs.vsle.table.gui.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.util.Console;
import ee.ioc.cs.vsle.vclass.*;

public class EditorActionListener implements ActionListener {

    /*
     * For more information about Swing Actions see: "How to Use Actions"
     * http://java.sun.com/docs/books/tutorial/uiswing/misc/action.html
     */

    /**
     * Undo Action. Invokes the undo() method on the active scheme. By default
     * this action is bound to CTRL-z keys.
     */
    static class UndoAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public UndoAction() {
            putValue( Action.NAME, Menu.UNDO );
            putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK ) );
            setEnabled( false );
        }

        @Override
        public void actionPerformed( ActionEvent evt ) {
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                try {
                    canvas.undoManager.undo();
                } catch ( CannotUndoException e ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(),
                            "Undo failed. Please report the steps to reproduce this error.", "Cannot undo",
                            JOptionPane.WARNING_MESSAGE );
                    db.p( e );
                }
                Editor.getInstance().refreshUndoRedo();
                canvas.drawingArea.repaint();
            }
        }
    }

    /**
     * Redo Action. Invokes the redo() method on the active scheme. By default
     * this action is bound to CTRL-y keys.
     */
    static class RedoAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public RedoAction() {
            putValue( Action.NAME, Menu.REDO );
            putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK ) );
            setEnabled( false );
        }

        @Override
        public void actionPerformed( ActionEvent evt ) {
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                try {
                    canvas.undoManager.redo();
                } catch ( CannotRedoException e ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(),
                            "Redo failed. Please report the steps to reproduce this error.", "Cannot redo",
                            JOptionPane.WARNING_MESSAGE );
                    db.p( e );
                }

                Editor.getInstance().refreshUndoRedo();
                canvas.drawingArea.repaint();
            }
        }
    }

    /**
     * Delete Action. Used for removing currently selected objects/connections
     * from the scheme. By default this action is bound to the Delete key.
     */
    static class DeleteAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public DeleteAction() {
            putValue( Action.NAME, Menu.DELETE );
            putValue( Action.MNEMONIC_KEY, Integer.valueOf( KeyEvent.VK_D ) );
            putValue( Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ) );
        }

        @Override
        public void actionPerformed( ActionEvent e ) {
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if ( canvas != null )
                canvas.deleteSelectedObjects();
        }
    }

    static class CloneAction extends AbstractAction {

        public CloneAction() {
            putValue(Action.NAME, Menu.CLONE);
            putValue(Action.MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_C));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                    KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Canvas canvas = Editor.getInstance().getCurrentCanvas();
            if (canvas != null) {
                canvas.cloneObject();
            }
        }
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        // JmenuItem chosen
//        if ( e.getSource().getClass().getName() == "javax.swing.JMenuItem"
//                || e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem" ) {
            
            if ( Menu.NEW_SCHEME.equals( e.getActionCommand() ) ) {
                newScheme();
            } else if ( e.getActionCommand().equals( Menu.SAVE_SCHEME_AS ) ) {
                saveSchemeAs();
            } else if ( Menu.SAVE_SCHEME.equals( e.getActionCommand() ) ) {
                saveScheme();
            } else if ( e.getActionCommand().equals( Menu.LOAD_SCHEME ) ) {
                if ( notifyOnNullPackage(Editor.getInstance().getCurrentPackage()) ) {
                    return;
                }
                JFileChooser fc = new JFileChooser( Editor.getInstance().getCurrentPackage().getPath() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.SYN );
                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( Editor.getInstance() );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File file = fc.getSelectedFile();
                    RuntimeProperties.setLastPath( file.getAbsolutePath() );
                    if ( RuntimeProperties.isLogDebugEnabled() )
                        db.p( "Loading scheme: " + file.getName() );
                    try {
                        if ( Editor.getInstance().getCurrentCanvas().loadScheme( file ) ) {
                            Editor.getInstance().updateWindowTitle();
                        }
                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }
                }

            } else if ( e.getActionCommand().equals( Menu.RELOAD_SCHEME ) ) {
                if ( notifyOnNullPackage(Editor.getInstance().getCurrentPackage()) ) {
                    return;
                } else if ( Editor.getInstance().getCurrentCanvas().getLastScheme() == null ) {
                    JOptionPane.showMessageDialog( Editor.getInstance(), "No scheme has been recently saved or loaded", "Error",
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
                File file = new File( Editor.getInstance().getCurrentCanvas().getLastScheme() );
                if ( file.exists() ) {
                    db.p( "Reloading scheme: " + file.getName() );
                    try {
                        Editor.getInstance().getCurrentCanvas().loadScheme( file );
                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }
                }
            } else if ( e.getActionCommand().equals( Menu.LOAD ) ) {
                JFileChooser fc = new JFileChooser( ( RuntimeProperties.getLastPath() != null && new File( RuntimeProperties
                        .getLastPath() ).exists() ) ? RuntimeProperties.getLastPath() : RuntimeProperties.getWorkingDirectory() );
                CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.XML );

                fc.setFileFilter( synFilter );

                int returnVal = fc.showOpenDialog( Editor.getInstance() );

                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    File pack = fc.getSelectedFile();

                    db.p( "Loading package: " + pack.getName() );
                    Editor.getInstance().openNewCanvasWithPackage( pack );
                    Editor.getInstance().updateWindowTitle();
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE ) ) {
                Editor.getInstance().closeCurrentCanvas();
            } else if ( Menu.DELETE_SCHEME.equals( e.getActionCommand() ) ) {
                deleteCurrentScheme();
            } else if ( Menu.EXPORT_SCHEME.equals( e.getActionCommand() ) ) {
                Canvas canv = Editor.getInstance().getCurrentCanvas();
                if ( canv != null ) {
                    SchemeExporter.exportSchemeSpecification( canv.getScheme(), canv.getSchemeTitle() );
                } else {
                    notifyOnNullPackage(null);
                }
            } else if ( Menu.EXPORT_SCHEME_AS_OBJECT.equals( e.getActionCommand() ) ) {
                Canvas canv = Editor.getInstance().getCurrentCanvas();
                if ( canv != null ) {
                    SchemeExporter.exportAsObject(canv);
                } else {
                    notifyOnNullPackage(null);
                }
            } else if ( e.getActionCommand().equals( Menu.CLOSE_ALL ) ) {
                while ( Editor.getInstance().getCurrentPackage() != null ) {
                    Editor.getInstance().closeCurrentCanvas();
                }
            } else if ( e.getActionCommand().equals( Menu.RELOAD ) ) {
                Canvas canv = Editor.getInstance().getCurrentCanvas();
                if ( canv != null ) {
                    canv.reloadCurrentPackage();
                } else {
                    notifyOnNullPackage(null);
                }
            } else if ( e.getActionCommand().equals( Menu.INFO ) ) {
                String message;
                if ( Editor.getInstance().getCurrentPackage() != null ) {
                    message = Editor.getInstance().getCurrentPackage().getDescription();
                } else {
                    message = "No packages loaded";
                }
                JOptionPane.showMessageDialog( Editor.getInstance(), message );
            } else if ( e.getActionCommand().equals( Menu.PRINT ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().print();
                }
            } else if ( e.getActionCommand().equals( Menu.EXIT ) ) {
                Editor.getInstance().exitApplication();
            } else if ( e.getActionCommand().equals( Menu.GRID ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().setGridVisible(
                            !Editor.getInstance().getCurrentCanvas().isGridVisible() );
                }
            } else if ( e.getActionCommand().equals( Menu.SNAP_TO_GRID ) ) {

                RuntimeProperties.setSnapToGrid( !RuntimeProperties.getSnapToGrid() );
                               
            } else if ( e.getActionCommand().equals( Menu.CONTROL_PANEL ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().setCtrlPanelVisible(
                            !Editor.getInstance().getCurrentCanvas().isCtrlPanelVisible() );
                }
            } else if ( e.getActionCommand().equals( Menu.SHOW_PORTS ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().setDrawPorts( ((JCheckBoxMenuItem)e.getSource()).isSelected() );
                }
            } else if ( e.getActionCommand().equals( Menu.SHOW_NAMES ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().showObjectNames( ((JCheckBoxMenuItem)e.getSource()).isSelected() );
                }
            } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().clearObjects();
                }
            } else if ( e.getActionCommand().equals( Menu.SPECIFICATION ) ) {
                Canvas canv = Editor.getInstance().getCurrentCanvas();
                if ( canv != null ) {

                    JFrame frame = ProgramTextEditor.getFrame( canv.getTitle() );
                    if ( frame != null ) {

                        if ( frame.getState() == Frame.ICONIFIED ) {
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
                    notifyOnNullPackage(null);
                }
            } else if ( e.getActionCommand().equals( Menu.EXTEND_SPEC ) ) {
                new CodeViewer( Editor.getInstance().getCurrentCanvas().getScheme() );
            } else if ( e.getActionCommand().equals( Menu.RUN ) ) {
                Canvas canv = Editor.getInstance().getCurrentCanvas();
                if ( canv != null ) {

                    final ProgramRunner runner = new ProgramRunner( canv );

                    int op = ( RuntimeProperties.isComputeGoal() ? ProgramRunnerEvent.COMPUTE_GOAL : ProgramRunnerEvent.COMPUTE_ALL ) 
                        | ProgramRunnerEvent.RUN_NEW
                        | ( RuntimeProperties.isPropagateValues() ? ProgramRunnerEvent.PROPAGATE : 0 );

                    ProgramRunnerEvent evt = new ProgramRunnerEvent( this, runner.getId(), op );

                    EventSystem.queueEvent( evt );
                } else {
                    notifyOnNullPackage(null);
                }
            } else if ( e.getActionCommand().equals( Menu.PROPAGATE_VALUES ) ) {
                RuntimeProperties.setPropagateValues( checkIfSelected( e.getSource(), false ) );
            } else if ( e.getActionCommand().equals( Menu.COMPUTE_GOAL ) ) {
                RuntimeProperties.setComputeGoal( checkIfSelected( e.getSource(), false ) );
            } else if ( e.getActionCommand().equals( Menu.SCHEME_VALUES ) ) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if ( canvas != null ) {
                    ProgramRunnerEvent event = new ProgramRunnerEvent( this, canvas.getLastProgramRunnerID(), ProgramRunnerEvent.SHOW_ALL_VALUES );
                    EventSystem.queueEvent( event );
                }
            } else if ( e.getActionCommand().equals( Menu.SHOW_ALGORITHM ) ) {
                JCheckBoxMenuItem check = (JCheckBoxMenuItem) e.getSource();
                RuntimeProperties.setShowAlgorithm( check.isSelected() );
            } else if ( e.getActionCommand().equals( Menu.SCHEMEOPTIONS ) ) {
                new SchemeSettingsDialog( Editor.getInstance() );
            }
            /*
             * else if (e.getActionCommand().equals("Planner")) { PlannerEditor
             * plannerEditor = new PlannerEditor(objects, connections);
             * plannerEditor.setSize(450, 260); plannerEditor.setVisible(true);
             *  }
             */
            else if ( e.getActionCommand().equals( Menu.SELECT_ALL ) ) {
                if ( Editor.getInstance().getCurrentCanvas() != null ) {
                    Editor.getInstance().getCurrentCanvas().selectAllObjects();
                }
            }
            /*
             * else if (e.getActionCommand().equals("Run")) {
             * ee.ioc.cs.editor.editor.ResultsWindow resultsWindow= new
             * ee.ioc.cs.editor.editor.ResultsWindow(objects, connections,
             * classes); resultsWindow.setSize(450, 260);
             * resultsWindow.setVisible(true); }
             */
            else if ( e.getActionCommand().equals( Menu.DOCS ) ) {
                String documentationUrl = RuntimeProperties.getSystemDocUrl();

                if ( documentationUrl != null && documentationUrl.trim().length() > 0 ) {
                    SystemUtils.openInBrowser( documentationUrl, Editor.getInstance() );
                } else {
                    Editor.getInstance().showInfoDialog( "Missing information", "No documentation URL defined in properties." );
                }

            } else if ( e.getActionCommand().equals( Menu.SETTINGS ) ) {
                Editor.getInstance().openOptionsDialog();
            } else if ( e.getActionCommand().equals( Menu.FONTS ) ) {
                RuntimeProperties.openFontChooser( Editor.getInstance() );
            }  else if ( e.getActionCommand().equals( Menu.SAVE_SETTINGS ) ) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if ( canvas != null ) {
                    RuntimeProperties.setZoomFactor( canvas.getScale() );
                    RuntimeProperties.setShowGrid( canvas.isGridVisible() );
                    RuntimeProperties.setShowControls( canvas.isCtrlPanelVisible() );
                }
                RuntimeProperties.save();
            } else if ( e.getActionCommand().equals( Menu.VIEW_THREADS ) ) {
                RunningThreadManager.showDialog();
            } else if ( e.getActionCommand().equals( Menu.JAVA_CONSOLE ) ) {
                Console.show();
            } else if ( e.getActionCommand().equals( Menu.EXPERT_TABLE ) ) {
                JDialog ess = new ExpertSystemShellWelcomeDialog();
                ess.setLocationRelativeTo( Editor.getInstance() );
                ess.setVisible( true );
            } else if ( e.getActionCommand().equals( Menu.ABOUT ) ) {
                new AboutDialog( Editor.getInstance() );
            } else if ( e.getActionCommand().equals( Menu.LICENSE ) ) {
                new LicenseDialog( Editor.getInstance() );
            } else if ( e.getActionCommand().equals( Menu.CLASSPAINTER ) ) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if ( canvas != null ) {
                    canvas.setEnableClassPainter( !canvas.isEnableClassPainter() );
                }
            } else if (e.getActionCommand().equals(Menu.SCHEME_FIND)) {
                Editor.getInstance().showSchemeSearchDialog();
            } else if (e.getActionCommand().equals(Palette.CtrlButton.RUNSAME.getActionCmd())) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                long id;
                if ( canvas != null && ( id = canvas.getLastProgramRunnerID() ) > 0 ) {
                    int op = ProgramRunnerEvent.RUN;
                    
                    if( RuntimeProperties.isPropagateValues() )
                        op |= ProgramRunnerEvent.PROPAGATE;
                    
                    EventSystem.queueEvent( new ProgramRunnerEvent( this, id, op ) );
                }
            } else if (e.getActionCommand().equals(Palette.CtrlButton.STOP.getActionCmd())) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                long id;
                if ( canvas != null && ( id = canvas.getLastProgramRunnerID() ) > 0 ) {

                    EventSystem.queueEvent( new ProgramRunnerEvent( this, id, ProgramRunnerEvent.DESTROY ) );
                }
            }
//        }
    }

    /**
     * Clears all elements from the canvas and unlinks any previously loaded scheme
     */
    private void newScheme() {
        Editor ed = Editor.getInstance();
        VPackage pk = ed.getCurrentPackage();
        if ( notifyOnNullPackage(pk) ) {
            return;
        }
        ed.getCurrentCanvas().newScheme();
        ed.updateWindowTitle();        
    }

    /**
     * Saves the current scheme to the file it was loaded from or where it was
     * last saved to. If this scheme has not been saved before the method
     * invokes saveSchemeAs().
     */
    private void saveScheme() {
        Editor editor = Editor.getInstance();
        VPackage pack = editor.getCurrentPackage();

        if ( notifyOnNullPackage(pack) ) {
            return;
        }

        String fileName = editor.getCurrentCanvas().getLastScheme();

        if ( fileName != null ) {
            editor.getCurrentCanvas().saveScheme( new File( fileName ) );
        } else {
            saveSchemeAs();
        }
    }

    /**
     * Saves the scheme to the file the user specifies interactively.
     */
    private void saveSchemeAs() {
        Editor editor = Editor.getInstance();
        VPackage pack = editor.getCurrentPackage();

        if ( notifyOnNullPackage(pack) ) {
            return;
        }

        JFileChooser fc = new JFileChooser( pack.getPath() );
        CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.SYN );
        fc.setFileFilter( synFilter );
        int returnVal = fc.showSaveDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {

            File file = fc.getSelectedFile();

            if ( !file.getAbsolutePath().toLowerCase().endsWith( CustomFileFilter.EXT.SYN.getExtension() ) ) {

                file = new File( file.getAbsolutePath() + "." + CustomFileFilter.EXT.SYN.getExtension() );
            }

            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            if ( RuntimeProperties.isLogInfoEnabled() )
                db.p( "Saving scheme: " + file.getName() );
            editor.getCurrentCanvas().saveScheme( file );
            editor.updateWindowTitle();
        }
    }

    /**
     * Closes the current scheme and removes the file the scheme was loaded
     * from. The user is asked for confirmation.
     */
    private void deleteCurrentScheme() {
        Editor editor = Editor.getInstance();
        Canvas canvas = editor.getCurrentCanvas();
        if ( canvas != null ) {
            String lastScheme = canvas.getLastScheme();
            if ( lastScheme == null ) {
                int rv = JOptionPane.showConfirmDialog( canvas, "The scheme is not saved. Are you sure you want to "
                        + "close it?", "Confirm Delete", JOptionPane.YES_NO_OPTION );
                if ( rv == JOptionPane.YES_OPTION )
                    newScheme();
            } else {
                int rv = JOptionPane.showConfirmDialog( editor, "Are you sure you want to delete the current scheme"
                        + " and permanently remove the file\n" + lastScheme + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION );
                if ( rv == JOptionPane.YES_OPTION ) {
                    if ( new File( lastScheme ).delete() )
                        newScheme();
                    else
                        JOptionPane.showMessageDialog( canvas, "Cannot remove" + " the file " + lastScheme + ".\nPlease check"
                                + " the permissions.", "Error", JOptionPane.ERROR_MESSAGE );
                }
            }
        } else {
            notifyOnNullPackage( null );
        }
    }

    private boolean notifyOnNullPackage( VPackage pk ) {
        if ( pk == null ) {
            JOptionPane.showMessageDialog( Editor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
            return true;
        }
        return false;
    }
    
    private boolean checkIfSelected( Object src, boolean _default ) {
        
        if( src instanceof JCheckBoxMenuItem ) {
            return ((JCheckBoxMenuItem)src).isSelected();
        } else if( src instanceof JToggleButton ) {
            return ((JToggleButton)src).isSelected();
        }
        
        return _default;
    }
}

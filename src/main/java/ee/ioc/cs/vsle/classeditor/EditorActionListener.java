package ee.ioc.cs.vsle.classeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ee.ioc.cs.vsle.common.gui.AboutDialog;
import ee.ioc.cs.vsle.common.gui.LicenseDialog;
import ee.ioc.cs.vsle.editor.CustomFileFilter;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.packageparse.PackageXmlProcessor;
import ee.ioc.cs.vsle.util.SystemUtils;
import ee.ioc.cs.vsle.vclass.Canvas;
import ee.ioc.cs.vsle.vclass.ClassObject;
import ee.ioc.cs.vsle.vclass.VPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorActionListener implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(EditorActionListener.class);

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
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                try {
                    canvas.undoManager.undo();
                } catch ( CannotUndoException e ) {
                    JOptionPane.showMessageDialog( ClassEditor.getInstance(),
                            "Undo failed. Please report the steps to reproduce this error.", "Cannot undo",
                            JOptionPane.WARNING_MESSAGE );
                    logger.error(null, e );
                }
                ClassEditor.getInstance().refreshUndoRedo();
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
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                try {
                    canvas.undoManager.redo();
                } catch ( CannotRedoException e ) {
                    JOptionPane.showMessageDialog( ClassEditor.getInstance(),
                            "Redo failed. Please report the steps to reproduce this error.", "Cannot redo",
                            JOptionPane.WARNING_MESSAGE );
                    logger.error(null, e );
                }

                ClassEditor.getInstance().refreshUndoRedo();
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
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            System.out.println("here DeleteAction actionPerformed");
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
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            if (canvas != null) {
                canvas.cloneObject();
            }
        }
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        System.out.println("EditorActionListener actionPerformed " + e);
        System.out.println( e.getActionCommand());
        
        if ( e.getActionCommand().equals( Menu.PRINT ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().print();
            }
        } else if ( e.getActionCommand().equals( Menu.EXIT ) ) {
            ClassEditor.getInstance().exitApplication();
        } else if ( e.getActionCommand().equals( Menu.GRID ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().setGridVisible(
                        !ClassEditor.getInstance().getCurrentCanvas().isGridVisible() );
            }
        } else if ( e.getActionCommand().equals( Menu.CONTROL_PANEL ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().setCtrlPanelVisible(
                        !ClassEditor.getInstance().getCurrentCanvas().isCtrlPanelVisible() );
            }
        } else if ( e.getActionCommand().equals( Menu.SHOW_PORTS ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().setDrawPorts( ((JCheckBoxMenuItem)e.getSource()).isSelected() );
            }
        } else if ( e.getActionCommand().equals( Menu.SHOW_PORT_OPEN_CLOSE ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().setDrawOpenPorts( ((JCheckBoxMenuItem)e.getSource()).isSelected() );
            }    
        } else if ( e.getActionCommand().equals( Menu.SHOW_NAMES ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().showObjectNames( ((JCheckBoxMenuItem)e.getSource()).isSelected() );
            }
        } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().clearObjects();
            }
        } else if ( e.getActionCommand().equals( Menu.SELECT_ALL ) ) {
            if ( ClassEditor.getInstance().getCurrentCanvas() != null ) {
                ClassEditor.getInstance().getCurrentCanvas().selectAllObjects();
            }
        } else if ( e.getActionCommand().equals( Menu.DOCS ) ) {
            String documentationUrl = RuntimeProperties.getSystemDocUrl();

            if ( documentationUrl != null && documentationUrl.trim().length() > 0 ) {
                SystemUtils.openInBrowser( documentationUrl, ClassEditor.getInstance() );
            } else {
                ClassEditor.getInstance().showInfoDialog( "Missing information", "No documentation URL defined in properties." );
            }

        } else if ( e.getActionCommand().equals( Menu.SETTINGS ) ) {
            ClassEditor.getInstance().openOptionsDialog();
        }  else if ( e.getActionCommand().equals( Menu.SAVE_SETTINGS ) ) {
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                RuntimeProperties.setZoomFactor( canvas.getScale() );
                RuntimeProperties.setShowGrid( canvas.isGridVisible() );
                RuntimeProperties.setShowControls( canvas.isCtrlPanelVisible() );
            }
            RuntimeProperties.save();

        } else if ( e.getActionCommand().equals( Menu.ABOUT ) ) {
            new AboutDialog( ClassEditor.getInstance() );
        } else if ( e.getActionCommand().equals( Menu.LICENSE ) ) {
            new LicenseDialog( ClassEditor.getInstance() );
        } else if ( e.getActionCommand().equals( Menu.CLASSPAINTER ) ) {
            Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
            if ( canvas != null ) {
                canvas.setEnableClassPainter( !canvas.isEnableClassPainter() );
            }
        } else if ( e.getActionCommand().equals( Menu.CLASS_PROPERTIES ) ) {
        	System.out.println(ClassEditor.getInstance().getClassFieldModel());
            new ClassPropertiesDialog( ClassEditor.getInstance().getClassFieldModel(), true );
        } else if ( e.getActionCommand().equals( Menu.VIEWCODE ) ) {
	        if ( ClassObject.className == null ) {
	            JOptionPane.showMessageDialog( ClassEditor.getInstance(), "No class name found", "Error", JOptionPane.ERROR_MESSAGE );
	            return;
	        }	 
	        Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
	    	canvas.openClassCodeViewer( ClassObject.className );
	    } else if ( e.getActionCommand().equals( Menu.EXPORT_TO_PACKAGE ) ) {
	    	ClassEditor.getInstance().exportShapesToPackage(); // append the graphics to a package
	    } else if ( e.getActionCommand().equals( Menu.IMPORT_FROM_PACKAGE ) ) {
	    	ClassEditor.getInstance().loadClass();
	    } else if ( e.getActionCommand().equals( Menu.CREATE_PACKAGE ) ) {
	    	ClassEditor.getInstance().createPackage();
	    } else if ( e.getActionCommand().equals( Menu.SELECT_PACKAGE ) ) {
            JFileChooser fc = new JFileChooser( ( RuntimeProperties.getLastPath() != null && new File( RuntimeProperties
                    .getLastPath() ).exists() ) ? RuntimeProperties.getLastPath() : RuntimeProperties.getWorkingDirectory() );
            CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.XML );
            fc.setFileFilter( synFilter );
            
            int returnVal = fc.showOpenDialog( ClassEditor.getInstance() );
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                File file = fc.getSelectedFile();
                logger.debug( "Loading package: " + file.getName() );
                try {
                    VPackage pkg;
                    if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
                        RuntimeProperties.setLastPath( file.getAbsolutePath() );
                        Canvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
                        ClassEditor classEditor = ClassEditor.getInstance();
                        if (curCanvas != null) {
                        	classEditor.getCurrentCanvas().setPackage(pkg);
                        	classEditor.getCurrentCanvas().setWorkDir(file.getParent()+File.separator);
                        	classEditor.updateWindowTitle();
                        } else {
                        	classEditor.openNewCanvasWithPackage(file);
                        }
                        classEditor.setPackageFile(file);
                    }
                } catch ( Exception exc ) {
                    exc.printStackTrace();
                }
            }
	    } else if ( e.getActionCommand().equals( Menu.DELETE_FROM_PACKAGE ) ) {
	    	ClassEditor.getInstance().deleteClass();
	    }     
    }


//    private boolean notifyOnNullPackage( VPackage pk ) {
//        if ( pk == null ) {
//            JOptionPane.showMessageDialog( ClassEditor.getInstance(), "No package loaded", "Error", JOptionPane.ERROR_MESSAGE );
//            return true;
//        }
//        return false;
//    }
    
}

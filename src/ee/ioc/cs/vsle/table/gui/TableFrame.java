/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import static ee.ioc.cs.vsle.table.gui.TableConstants.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.table.event.*;
import ee.ioc.cs.vsle.table.exception.*;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class TableFrame extends JFrame {

    private static final String MENU_NEW = "New";
    private static final String MENU_OPEN = "Open";
    private static final String MENU_SAVE = "Save";
    private static final String MENU_SAVE_AS = "Save As";
    private static final String MENU_EXIT = "Exit";
    private static final String MENU_H_RENDERER = "Horizontal";
    private static final String MENU_V_RENDERER = "Vertical";
    private static final String MENU_TUTORIAL = "Tutorial";
    private static final String MENU_EDIT_TABLE = "Table Properties...";
    private TableMainPanel lastPanel;
    private File openTableFile;
    
    private ActionListener actionLst;
    private JMenuItem menuItemEditTable;
    private JMenuItem menuItemSaveAs;
    private JMenuItem menuItemSave;
    
    private static int rowHeightH = ROW_HEIGHT_NORM;
    private static int rowHeightV = ROW_HEIGHT_NORM;
    
    /**
     * @throws HeadlessException
     */
    public TableFrame() { 
        
        init();
    }

    /**
     * 
     */
    private void init() {
        
        ToolTipManager.sharedInstance().setInitialDelay( 100 );
        ToolTipManager.sharedInstance().setReshowDelay( 100 );
        ToolTipManager.sharedInstance().setDismissDelay( 8000 );
        
        updateFrameTitle();
        
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setLocationByPlatform( true );
        setContentPane( new JPanel( new BorderLayout() ) );
        
        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
        
        initMenu();
        
        setSize( 400, 300 );
    }
    
    @Override
    protected void processWindowEvent( WindowEvent e ) {
        //TODO maybe the confirmation should be shown only if
        //a table has been modified and not saved?
        if ( e.getID() == WindowEvent.WINDOW_CLOSING 
                && getTable() != null ) {
            if( JOptionPane.YES_OPTION == 
                JOptionPane.showConfirmDialog( 
                        e.getComponent(), "Would you like to close the editor?", 
                        "Confirm close", JOptionPane.YES_NO_OPTION ) )
                dispose();
        } else 
            super.processWindowEvent( e );
    }
    
    /**
     * 
     */
    private void initMenu() {
        
        actionLst = new MyActionListener();
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar( menuBar );
        
        JMenu menu;
        JMenuItem menuItem;
        
        menu = new JMenu( "File" );
        menu.setMnemonic( KeyEvent.VK_F );
        
        menuItem = new JMenuItem( MENU_NEW );
        menu.add( menuItem );
        menuItem.addActionListener( actionLst );
        
        menuItem = new JMenuItem( MENU_OPEN );
        menu.add( menuItem );
        menuItem.addActionListener( actionLst );
        
        menuItemSave = new JMenuItem( MENU_SAVE );
        menu.add( menuItemSave );
        menuItemSave.addActionListener( actionLst );
        
        menuItemSaveAs = new JMenuItem( MENU_SAVE_AS );
        menu.add( menuItemSaveAs );
        menuItemSaveAs.addActionListener( actionLst );
        
        menu.add( new JSeparator() );
        
        menuItem = new JMenuItem( MENU_EXIT );
        menu.add( menuItem );
        menuItem.addActionListener( actionLst );
        
        menuBar.add( menu );
        
        menu = new JMenu( "Edit" );
        menu.setMnemonic( KeyEvent.VK_E );
        
        menuItemEditTable = new JMenuItem( MENU_EDIT_TABLE );
        menu.add( menuItemEditTable );
        menuItemEditTable.addActionListener( actionLst );
        
        menuBar.add( menu );
        
        menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            @Override
            public void popupMenuCanceled( PopupMenuEvent e ) {}

            @Override
            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                boolean tableExists = getTable() != null;
                
                menuItemSave.setEnabled( tableExists );
                menuItemSaveAs.setEnabled( tableExists );
                menuItemEditTable.setEnabled( tableExists );
            }
            
        } );
        
        menu = new JMenu( "View" );
        menu.setMnemonic( KeyEvent.VK_V );
        menuBar.add( menu );
        
        JMenu submenu = new JMenu( "Increase rule height" );
        menu.add( submenu );
        
        menuItem = new JCheckBoxMenuItem( MENU_H_RENDERER, rowHeightH == ROW_HEIGHT_EXTENDED );
        submenu.add( menuItem );
        menuItem.addActionListener( actionLst );
        
        menuItem = new JCheckBoxMenuItem( MENU_V_RENDERER, rowHeightV == ROW_HEIGHT_EXTENDED );
        submenu.add( menuItem );
        menuItem.addActionListener( actionLst );
        
        menu = new JMenu( "Help" );
        menu.setMnemonic( KeyEvent.VK_H );
        menuBar.add( menu );
        
        menuItem = new JMenuItem( MENU_TUTORIAL );
        menu.add( menuItem );
        menuItem.addActionListener( actionLst );
    }
    
    /**
     * @return
     */
    private String getPath() {
        
        if( Editor.getInstance() != null && Editor.getInstance().getCurrentPackage() != null ) {
            return Editor.getInstance().getCurrentPackage().getPath();
        }
        
        String path = RuntimeProperties.getLastPath();
        
        return path != null ? path : RuntimeProperties.getWorkingDirectory();
    }
    
    /**
     * 
     */
    void newTable() {
        
        TablePropertyDialog dialog = new TablePropertyDialog( this );
        dialog.setVisible( true );
        
        if( dialog.isOk() ) {
            setTable( dialog.getTable() );
            openTableFile = null;
        }
    }
    
    /**
     * @param table
     */
    private void setTable( final Table table ) {
        
        if( lastPanel != null ) {
            getContentPane().remove( lastPanel );
            lastPanel.destroy();
        }
        
        if( table != null ) {
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    getContentPane().add( lastPanel = new TableMainPanel( table ), BorderLayout.CENTER );
                    validate();
                    repaint();
                    pack();
                }
            } );
        }
        
        updateFrameTitle();
    }
    
    /**
     * @return
     */
    private Table getTable() {
        
        if( lastPanel != null ) {
            return lastPanel.getTable();
        }
        return null;
    }
    
    /**
     * @param isHorizontal
     * @return
     */
    static int getRowHeight( boolean isHorizontal ) {
        
        return isHorizontal ? rowHeightH : rowHeightV;
    }
    
    @Override
    public void dispose() {
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                setTable( null );                
            }
        } );
        
        super.dispose();
    }
    
    /**
     * 
     */
    void openTable() {
        Pair<Table, File> pair = TableManager.openTable( TableFrame.this, getPath() );
        openTableFile = pair.getSecond();
        setTable( pair.getFirst() );
    }
    
    /**
     * 
     */
    private void updateFrameTitle() {
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                Table table = getTable();
                
                String fileName = openTableFile != null ? " (" + openTableFile.getName() + ")": "";
                
                setTitle( ( table != null ? table.getTableId() + fileName + " - ": "" ) + "Expert Table Visual Editor");
            }
        } );
    }
    
    /**
     * 
     */
    private void editTableProperties() {
        TablePropertyDialog dialog = new TablePropertyDialog( this, getTable() );
        dialog.setVisible( true );
        updateFrameTitle();
    }
    
    /**
     * @param showDialog
     */
    private void saveTable( boolean showDialog ) {
        
        Table table;
        
        if( ( table = getTable() ) == null ) {
            return;
        }
        
        boolean isFileChanged = false;
        
        if( showDialog || openTableFile == null ) {
            
            JFileChooser fc = new JFileChooser( getPath() );
            fc.setDialogType( JFileChooser.SAVE_DIALOG );
            
            fc.setSelectedFile( openTableFile );

            fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.TBL ) );

            if ( fc.showOpenDialog( TableFrame.this ) == JFileChooser.APPROVE_OPTION ) {
                
                File file = fc.getSelectedFile();
                
                if ( !file.getAbsolutePath().toLowerCase().endsWith( CustomFileFilter.EXT.TBL.getExtension() ) ) {

                    file = new File( file.getAbsolutePath() + "." + CustomFileFilter.EXT.TBL.getExtension() );
                }
                
                isFileChanged = !file.equals( openTableFile );
                
                openTableFile = file;
                
            } else {
                return;
            }
        }
        
        if( openTableFile != null ) {
            try {
                new TableXmlProcessor( openTableFile ).save( table, isFileChanged );
            } catch( TableException e ) {
                if( RuntimeProperties.isLogDebugEnabled() ) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
    
    /**
     * @param args
     */
    public static void main( String[] args ) {
        
        RuntimeProperties.setDebugInfo( 1 );
        
        TableFrame fr = new TableFrame();
        fr.setDefaultCloseOperation( EXIT_ON_CLOSE );
        fr.setVisible( true );
    }

    /**
     * @author pavelg
     *
     */
    class MyActionListener implements ActionListener {

        @Override
        public void actionPerformed( ActionEvent e ) {
            if( e.getActionCommand() == MENU_NEW ) {
                
                newTable();
                
            } else if( e.getActionCommand() == MENU_OPEN ) {
                
                openTable();
                
            } else if( e.getActionCommand() == MENU_SAVE ) {
                
                saveTable( false );
                
            } else if( e.getActionCommand() == MENU_SAVE_AS ) {
                
                saveTable( true );
                
            } else if( e.getActionCommand() == MENU_EXIT ) {
                
                dispose();
                return;
                
            } else if( e.getActionCommand() == MENU_EDIT_TABLE ) {
                
                editTableProperties();
                
            } else if( e.getActionCommand() == MENU_H_RENDERER || e.getActionCommand() == MENU_V_RENDERER ) {
                
                int height = ( (JCheckBoxMenuItem) e.getSource()).isSelected() ? ROW_HEIGHT_EXTENDED : ROW_HEIGHT_NORM;
                
                if( e.getActionCommand() == MENU_H_RENDERER ) {
                    rowHeightH = height;
                } else {
                    rowHeightV = height;
                }
                
                TableEvent.dispatchEvent( new TableEvent( this, TableEvent.RENDERER ) );
                
                return;
                
            } else if( e.getActionCommand() == MENU_TUTORIAL ) {
                
                SystemUtils.openInBrowser( URL_TUTORIAL, TableFrame.this );
                return;
            }
        }
    }
}

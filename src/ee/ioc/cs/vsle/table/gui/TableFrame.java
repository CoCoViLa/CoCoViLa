/**
 * 
 */
package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.table.*;

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
    private static final String MENU_EDIT_TABLE = "Table Properties...";
    private TableMainPanel lastPanel;
    private File openTableFile;
    
    private ActionListener actionLst;
    private JMenuItem menuItemEditTable;
    private JMenuItem menuItemSaveAs;
    private JMenuItem menuItemSave;
    
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
    }
    
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
    private void newTable() {
        
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
                public void run() {
                    getContentPane().add( lastPanel = new TableMainPanel( table ), BorderLayout.CENTER );
                    validate();
                    repaint();
                    pack();
                }
            } );
        }
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
    
    @Override
    public void dispose() {
        
        super.dispose();
        
        setTable( null );
    }
    
    private void openTable() {

        JFileChooser fc = new JFileChooser( getPath() );
        fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.TBL ) );
        fc.setDialogType( JFileChooser.OPEN_DIALOG );
        
        int returnVal = fc.showOpenDialog( TableFrame.this );
        
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            
            Map<String, Table> tables;
            
            try {
                tables = new TableXmlProcessor( fc.getSelectedFile() ).parse();
            } catch( TableException e ) {
                if( RuntimeProperties.isLogDebugEnabled() ) {
                    e.printStackTrace();
                }
                return;
            }
            
            openTableFile = fc.getSelectedFile();
            
            final Table table;
            
            if( tables.size() == 1 ) {
                table = tables.values().iterator().next();
                
            } else if( tables.size() > 1 ) {
                
                Object[] opts = tables.keySet().toArray();
                
                int res = JOptionPane.showOptionDialog( TableFrame.this,
                        "Choose table", "Tables",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, opts, opts[0] ); 
                
                if( res != JOptionPane.CLOSED_OPTION ) {
                    table = tables.get( opts[res] );
                } else {
                    return;
                }
            } else {
                return;
            }
            
            setTable( table );
        }
    }
    
    private void updateFrameTitle() {
        Table table = getTable();
        
        String fileName = openTableFile != null ? " (" + openTableFile.getName() + ")": "";
        
        setTitle( ( table != null ? table.getTableId() + fileName + " - ": "" ) + "Expert Table");
    }
    
    /**
     * 
     */
    private void editTableProperties() {
        TablePropertyDialog dialog = new TablePropertyDialog( this, getTable() );
        dialog.setVisible( true );
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
                
            } else if( e.getActionCommand() == MENU_EDIT_TABLE ) {
                
                editTableProperties();
            }
            
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    updateFrameTitle();
                }
            } );
            
        }
    }
}

package ee.ioc.cs.vsle.classeditor;

import static ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel.iNAME;
import static ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel.iTYPE;
import static ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel.iVALUE;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.undo.UndoManager;

import ee.ioc.cs.vsle.classeditor.EditorActionListener.CloneAction;
import ee.ioc.cs.vsle.classeditor.EditorActionListener.DeleteAction;
import ee.ioc.cs.vsle.classeditor.EditorActionListener.RedoAction;
import ee.ioc.cs.vsle.classeditor.EditorActionListener.UndoAction;
import ee.ioc.cs.vsle.editor.ComponentResizer;
import ee.ioc.cs.vsle.editor.CustomFileFilter;
import ee.ioc.cs.vsle.editor.DnDTabbedPane;
import ee.ioc.cs.vsle.editor.Look;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.OptionsDialog;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.editor.SpecGenerator;
import ee.ioc.cs.vsle.editor.XMLSpecGenerator;
import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.Shape;

import ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel;
import ee.ioc.cs.vsle.iconeditor.ClassImport;
import ee.ioc.cs.vsle.iconeditor.ClassPropertiesDialog;
import ee.ioc.cs.vsle.iconeditor.DeleteClassDialog;
import ee.ioc.cs.vsle.iconeditor.IconClass;
import ee.ioc.cs.vsle.packageparse.PackageXmlProcessor;
import ee.ioc.cs.vsle.synthesize.Synthesizer;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.GraphicsExporter;
import ee.ioc.cs.vsle.util.SystemUtils;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.vclass.ClassGraphics;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.PackageClass.ComponentType;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.vclass.VPackage;


public class ClassEditor extends JFrame implements ChangeListener {

    private static final long serialVersionUID = 1L;

    private static ClassEditor s_instance;

    DnDTabbedPane tabbedPane;

    private EditorActionListener aListener;
    
    DeleteAction deleteAction;
    UndoAction undoAction;
    RedoAction redoAction;
    CloneAction cloneAction;
    
    IconPalette iconPalette;

    JMenuBar menuBar;

    public static final String WINDOW_TITLE = "CoCoViLa - New Class Editor";
    
    public static boolean classParamsOk = false;
    public static boolean packageParamsOk = false;    
    
	// Class properties.
    public static String className;
    public static String classDescription;
    private static String classIcon;
    public static ComponentType componentType;
    
    // Package properties
    public static String packageName;
    public static String packageDesc;    
    
    BoundingBox boundingbox;
    
    private static File packageFile;

    private JCheckBoxMenuItem gridCheckBox;
    private JCheckBoxMenuItem showPortCheckBox;
    private JCheckBoxMenuItem showPortOpenCheckBox;
    private JCheckBoxMenuItem showObjectNamesCheckBox;
    private JCheckBoxMenuItem snapToGridCheckBox;
    
    private ClassFieldsTableModel dbrClassFields = new ClassFieldsTableModel();
    
    ArrayList<IconClass> packageClassList = new ArrayList<IconClass>();
    ArrayList<ClassField> fields = new ArrayList<ClassField>();    
    ArrayList<String> packageClassNamesList = new ArrayList<String>();
    
    ChooseClassDialog ccd = new ChooseClassDialog( packageClassNamesList, null );
    DeleteClassDialog dcd = new DeleteClassDialog( packageClassNamesList );
    
    ClassImport ci;    
    ClassImport cig;
    
    /**
     * Class constructor [1].
     */
    private ClassEditor() {
        initialize();
        validate();
    }

    /**
     * Application initializer.
     */
    private void initialize() {
        setLocationByPlatform( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
        tabbedPane = new DnDTabbedPane();
        tabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
        tabbedPane.addChangeListener( this );
        undoAction = new UndoAction();
        redoAction = new RedoAction();
        deleteAction = new DeleteAction();
        cloneAction = new CloneAction();
        makeMenu();
        getContentPane().add( tabbedPane );
        initActions();
    } // initialize

    /**
     * Creates Action objects and initializes Input and Action mappings
     */
    private void initActions() {
        JRootPane rp = getRootPane();

        ActionMap am = rp.getActionMap();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        am.put(DeleteAction.class, deleteAction);
        am.put(CloneAction.class, cloneAction);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                DeleteAction.class );
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK),
                CloneAction.class);
    }

    /**
     * Build menu.
     */
    public void makeMenu() {
        JMenuItem menuItem;

        JMenu menu;
        JMenu submenu;

        menuBar = new JMenuBar();
        setJMenuBar( menuBar );
        menu = new JMenu( Menu.MENU_FILE );
        menu.setMnemonic( KeyEvent.VK_F );

        JMenu exportmenu = new JMenu( Menu.EXPORT_MENU );
        exportmenu.setMnemonic( KeyEvent.VK_E );

        menuItem = new JMenuItem( Menu.EXPORT_TO_PACKAGE, KeyEvent.VK_P );
        menuItem.addActionListener( getActionListener() );
        exportmenu.add( menuItem );

        // Export window graphics
        exportmenu.add(GraphicsExporter.getExportMenu());

        menu.add( exportmenu );

        JMenu importmenu = new JMenu( Menu.IMPORT_MENU );
        importmenu.setMnemonic( KeyEvent.VK_I );

        menuItem = new JMenuItem( Menu.IMPORT_FROM_PACKAGE );
        menuItem.addActionListener( getActionListener() );
        importmenu.add( menuItem );

        menu.add( importmenu );        

        menuItem = new JMenuItem( Menu.DELETE_FROM_PACKAGE, KeyEvent.VK_D );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.CREATE_PACKAGE, KeyEvent.VK_C );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.SELECT_PACKAGE );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        menu.addSeparator();
        menuItem = new JMenuItem( Menu.PRINT, KeyEvent.VK_P );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.EXIT, KeyEvent.VK_X );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuBar.add( menu );
        menu = new JMenu( Menu.MENU_EDIT );
        menu.setMnemonic( KeyEvent.VK_E );

        menu.add( undoAction );
        menu.add( redoAction );
        menu.add( cloneAction );

        menuItem = new JMenuItem( Menu.SELECT_ALL, KeyEvent.VK_A );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLEAR_ALL, KeyEvent.VK_C );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        menu.addSeparator();

        menuItem = new JMenuItem( Menu.CLASS_PROPERTIES, KeyEvent.VK_P );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem ); 
        
        menuItem = new JMenuItem( Menu.VIEWCODE, KeyEvent.VK_V );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );         

        final JCheckBoxMenuItem painterEnabled = new JCheckBoxMenuItem( Menu.CLASSPAINTER, true );
        painterEnabled.addActionListener( getActionListener() );
        menu.add( painterEnabled );

        menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            @Override
            public void popupMenuCanceled( PopupMenuEvent e ) {
                // ignore
            }

            @Override
            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                // ignore
            }

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                Canvas canvas = ClassEditor.getInstance().getCurrentCanvas();
                if ( canvas == null || !canvas.getPackage().hasPainters() ) {
                    painterEnabled.setVisible( false );
                } else {
                    painterEnabled.setVisible( true );
                    painterEnabled.setSelected( canvas.isEnableClassPainter() );
                }
            }

        } );

        menuBar.add( menu );
        
        menu = new JMenu( Menu.MENU_VIEW );
        menu.setMnemonic( KeyEvent.VK_V );
        gridCheckBox = new JCheckBoxMenuItem( Menu.GRID, RuntimeProperties.isShowGrid() );
        gridCheckBox.setMnemonic( 'G' );
        gridCheckBox.addActionListener( getActionListener() );
        menu.add( gridCheckBox );
        
        snapToGridCheckBox = new JCheckBoxMenuItem( Menu.SNAP_TO_GRID, true );
        snapToGridCheckBox.addActionListener( getActionListener() );
        menu.add( snapToGridCheckBox );         
        
        showPortCheckBox = new JCheckBoxMenuItem( Menu.SHOW_PORTS, true );
        showPortCheckBox.addActionListener( getActionListener() );
        menu.add( showPortCheckBox );

        
        showPortOpenCheckBox = new JCheckBoxMenuItem( Menu.SHOW_PORT_OPEN_CLOSE, true );
        showPortOpenCheckBox.addActionListener( getActionListener() );
        menu.add( showPortOpenCheckBox );
        
        
        showObjectNamesCheckBox = new JCheckBoxMenuItem( Menu.SHOW_NAMES, false );
        showObjectNamesCheckBox.addActionListener( getActionListener() );
        
        //sync View with current canvas
        menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                Canvas canvas;
                
                if( ( canvas = getCurrentCanvas() ) == null ) 
                    return;
                
                gridCheckBox.setSelected( canvas.isGridVisible() );
                snapToGridCheckBox.setSelected( RuntimeProperties.getSnapToGrid() );
                showPortCheckBox.setSelected( canvas.isDrawPorts() );
                showPortOpenCheckBox.setSelected( canvas.isDrawOpenPorts() );
                showObjectNamesCheckBox.setSelected( canvas.isShowObjectNames() );
            }

            @Override
            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                // ignore
            }

            @Override
            public void popupMenuCanceled( PopupMenuEvent e ) {
                // ignore
            }

        } );
        
        menuBar.add( menu );
        
        menu = new JMenu( Menu.MENU_OPTIONS );
        menu.setMnemonic( KeyEvent.VK_O );

        menuItem = new JMenuItem( Menu.SETTINGS, KeyEvent.VK_S );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_J, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );


        submenu = new JMenu( Menu.MENU_LAF );
        submenu.setMnemonic( KeyEvent.VK_L );
        Look.getInstance().createMenuItems( submenu );
        menu.add( submenu );
        menuBar.add( menu );
        
        
        menu = new JMenu( Menu.MENU_HELP );
        menu.setMnemonic( KeyEvent.VK_H );
        menuBar.add( menu );
        menuItem = new JMenuItem( Menu.DOCS, KeyEvent.VK_D );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.LICENSE, KeyEvent.VK_L );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.ABOUT, KeyEvent.VK_A );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
    }

    /**
     * Display information dialog to application user.
     * 
     * @param title - information dialog title.
     * @param text - text displayed in the information dialog.
     */
    public void showInfoDialog( String title, String text ) {
        JOptionPane.showMessageDialog( this, text, title, JOptionPane.INFORMATION_MESSAGE );
    }

    /**
     * Overridden so we can exit when window is closed
     * 
     * @param e - Window Event.
     */
    @Override
    protected void processWindowEvent( WindowEvent e ) {
        // super.processWindowEvent(e); // automatic closing disabled,
        // confirmation asked instead.
        if ( e.getID() == WindowEvent.WINDOW_CLOSING ) {
            exitApplication();
        } else {
            super.processWindowEvent( e );
        }
    }

    /**
     * Close application.
     */
    public void exitApplication() {
        int confirmed = JOptionPane.showConfirmDialog( this, "Exit Application?", Menu.EXIT, JOptionPane.OK_CANCEL_OPTION );
        switch ( confirmed ) {
        case JOptionPane.OK_OPTION:

            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {

                    int state = getExtendedState();

                    if ( ( state & MAXIMIZED_BOTH ) == MAXIMIZED_BOTH ) {
                        // need to remember window's size in normal mode...looks
                        // ugly,
                        // any other ideas how to get normal size while staying
                        // maximized?
                        setExtendedState( NORMAL );
                    }

                    RuntimeProperties.setSchemeEditorWindowProps( getBounds(), state );

                    RuntimeProperties.save();

                    System.exit( 0 );
                }
            } );

            break;
        case JOptionPane.CANCEL_OPTION:
            break;
        }
    }


    /**
     * Check if Operating System type is Windows.
     * 
     * @param osType - Operating System type.
     * @return boolean - Operating System belongs to the Windows family or not.
     */
    public static boolean isWin( String osType ) {
        if ( osType != null && osType.startsWith( "Windows" ) ) {
            return true;
        }
        return false;
    }

    private enum OS { WIN, MAC, UNIX };
    
    /**
     * Return operating system type. Uses isWin, isMac, isUnix methods for
     * deciding on Os type and returns always the internally defined Os Type
     * (WIN,MAC or UNIX).
     */
    public static OS getOsType() {
      String os = System.getProperty("os.name");
      if(os != null) {
        os = os.toLowerCase();
        if(os.startsWith("win"))
          return OS.WIN;
        else if(os.startsWith("mac"))
          return OS.MAC;
        else
          return OS.UNIX;
      }
      return null;
    }
    
    /**
     * Package loader.
     * 
     * @param f - package file to be loaded.
     */
    void openNewCanvasWithPackage( File f ) {

        VPackage pkg;
        if((pkg = PackageXmlProcessor.load(f)) != null ) {
            RuntimeProperties.setLastPath( f.getAbsolutePath() );
            Canvas canvas = new Canvas( pkg, f.getParent() + File.separator );
            RuntimeProperties.addOpenPackage( pkg );
            addCanvas(canvas);
            updateWindowTitle();
        }
    } // loadPackage

    private void addCanvas(final Canvas canvas) {
        int count = 0;

        String packageName = canvas.getPackage().getName();

        for (Component canv : tabbedPane.getComponents()) {
            if (canv instanceof Canvas && packageName.equals(((Canvas) canv).getPackage().getName())) {
                count++;
            }
        }

        if (count > 0) {
            packageName = packageName + " (" + (count + 1) + ")";
            canvas.setTitle(packageName);
        }

        tabbedPane.addTab( packageName, null, canvas, canvas.getPackage().getPath() );
        tabbedPane.setSelectedComponent( canvas );
        canvas.setStatusBarText( "Loaded package: " + canvas.getPackage().getPath() );
        
        iconPalette = new IconPalette( canvas.mListener, ClassEditor.getInstance(), canvas );
    }

    /**
     * Main method for module unit-testing.
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) {
      
        if(getOsType() == OS.MAC)
          System.setProperty("apple.laf.useScreenMenuBar", "true");
      
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                createAndInitGUI( args );
            }
        } );
    }

    /**
     * Creates and displays the main application window.
     * 
     * @param args the arguments from the command line
     */
    static void createAndInitGUI( String[] args ) {
        assert SwingUtilities.isEventDispatchThread();

        // Some of the stuff in this method could be run on the main thread,
        // but running it in the EDT seems to not hurt. Creating new Canvas
        // instances combined with PropertyChangeListeners is definitely
        // unsafe in the main thread and has caused real problems.

        checkWebStart( args );
        
        /*
         * Remove security manager, because all permissions
         * are required. This is especially critical when
         * running from the Java Web Start. (Otherwise
         * all classes loaded by our own class loaders
         * will have default sandbox permissions.)
         */
        System.setSecurityManager( null );
        
        String version = System.getProperty( "java.version" );

        System.err.println( "Java Version: " + version );

        if ( version.compareTo( "1.6.0" ) < 0 ) {

            String message = "CoCoViLa requires at least Java 1.6.0 to run!";
            System.err.println( message );
            //for those who start the program w/o the console --
            //try to show this error message in a dialog, not just die silently
            try {
                JOptionPane.showMessageDialog( null, message, "Error", JOptionPane.ERROR_MESSAGE );
            } finally {
                System.exit( 1 );
            }
        }

        String directory = RuntimeProperties.getWorkingDirectory();

        System.err.println( "Working directory: " + directory );

        RuntimeProperties.init();

        Look.getInstance().initDefaultLnF();

        extractPackages();
        
        final ClassEditor window = new ClassEditor();
        s_instance = window;
        
        if ( !RuntimeProperties.isFromWebstart() && args.length > 0 ) {

            if ( args[ 0 ].equals( "-p" ) ) {

                String dir = ( args.length == 3 ) ? directory + args[ 2 ] + File.separator : directory;

                Synthesizer.parseFromCommandLine( dir, args[ 1 ] );

            } else {

                db.p( args[ 0 ] + " read from command line." );

                File file = new File( directory + args[ 0 ] );

                if ( file.exists() ) {
                    window.openNewCanvasWithPackage( file );
                }
            }

        } else {

            for ( String packageFile : RuntimeProperties.getPrevOpenPackages() ) {

                File f = new File( packageFile );

                if ( f.exists() ) {
                    if ( RuntimeProperties.isLogDebugEnabled() )
                        db.p( "Found package file name " + packageFile + " from the configuration file." );
                    window.openNewCanvasWithPackage( f );
                }
            }

        }

        window.setTitle( WINDOW_TITLE );

        //restore window location, size and state
        final String[] bs = RuntimeProperties.getSchemeEditorWindowProps().split( ";" );

        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                int x = bs[ 0 ] != null && bs[ 0 ].length() > 0 ? Integer.parseInt( bs[ 0 ] ) : window.getLocationOnScreen().x;
                int y = bs[ 1 ] != null && bs[ 1 ].length() > 0 ? Integer.parseInt( bs[ 1 ] ) : window.getLocationOnScreen().y;
                int w = bs[ 2 ] != null && bs[ 2 ].length() > 0 ? Integer.parseInt( bs[ 2 ] ) : window.getSize().width;
                int h = bs[ 3 ] != null && bs[ 3 ].length() > 0 ? Integer.parseInt( bs[ 3 ] ) : window.getSize().height;
                int s = bs[ 4 ] != null && bs[ 4 ].length() > 0 ? Integer.parseInt( bs[ 4 ] ) : window.getState();

                window.setBounds( x, y, w, h );

                window.setExtendedState( s );
            }
        } );

        window.setVisible( true );

        /* ******************** Init Factories ******************** */
        SpecGenerator.init();
        XMLSpecGenerator.init();
    }

    public static void checkWebStart(String[] args) {
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[ i ].equals( "-webstart" ) ) {
                RuntimeProperties.setFromWebstart();
                break;
            }
        }
    }
    
    public static void extractPackages()
    {
        //if apps was run for the first time from webstart
        //ask user if he wants to unpack demo packages
        if ( RuntimeProperties.isFromWebstart() && RuntimeProperties.isCleanInstall() ) {
            int res = JOptionPane.showConfirmDialog( null,
                    "Extract demo packages into \""
                    + ( RuntimeProperties.getWorkingDirectory()
                            + "packages" + File.separator ) + "\" ?",
                            "", JOptionPane.YES_NO_OPTION );

            if ( res == JOptionPane.YES_OPTION )
                SystemUtils.unpackPackages();
        }
    }

    public void openOptionsDialog() {
        OptionsDialog o = new OptionsDialog( ClassEditor.this );
        o.setVisible( true );
        repaint();
    }

    public VPackage getCurrentPackage() {
        if ( getCurrentCanvas() != null ) {
            return getCurrentCanvas().getPackage();
        }
        return null;
    }

    public Canvas getCurrentCanvas() {
        return (Canvas) tabbedPane.getSelectedComponent();

    }

    @Override
    public void stateChanged( ChangeEvent e ) {
        refreshUndoRedo();
        Canvas canvas = getCurrentCanvas();
        if ( canvas != null ) {
            gridCheckBox.setSelected( canvas.isGridVisible() );
            snapToGridCheckBox.setSelected( RuntimeProperties.getSnapToGrid());
            canvas.drawingArea.repaint();
            canvas.drawingArea.requestFocusInWindow();
        }

        updateWindowTitle();
    }

    /**
     * Updates the title of the window to reflect current scheme name.
     */
    public void updateWindowTitle() {
        String windowTitle = WINDOW_TITLE;
        
        Canvas canvas = getCurrentCanvas();
        if ( canvas != null ) {
            String packageName = canvas.getPackage().getName();
            String schemeTitle = canvas.getSchemeTitle();
            int idx = tabbedPane.getSelectedIndex();
            
            if( schemeTitle != null ) {
                windowTitle = schemeTitle + " - " + packageName + " - " + WINDOW_TITLE;
                tabbedPane.setTitleAt( idx, schemeTitle );
            } else {
                tabbedPane.setTitleAt( idx, packageName ); 
                windowTitle = packageName + " - " + WINDOW_TITLE;
            }
        }
        
        setTitle( windowTitle );
    }

    public static ClassEditor getInstance() {
        return s_instance;
    }

    /**
     * Updates Undo and Redo actions. When a new scheme tab is selected/opened
     * or an action that modifies the scheme is performed the undo and redo
     * action objects have to be updated to reflect the current state, this
     * includes presentation name and enabled/disabled status.
     */
    public void refreshUndoRedo() {
        Canvas canvas = getCurrentCanvas();
        if ( canvas != null ) {
            UndoManager um = canvas.undoManager;
            undoAction.setEnabled( !canvas.isActionInProgress() && um.canUndo() );
            redoAction.setEnabled( !canvas.isActionInProgress() && um.canRedo() );
            undoAction.putValue( Action.NAME, um.getUndoPresentationName() );
            redoAction.putValue( Action.NAME, um.getRedoPresentationName() );
        } else {
            undoAction.setEnabled( false );
            redoAction.setEnabled( false );
            undoAction.putValue( Action.NAME, Menu.UNDO );
            redoAction.putValue( Action.NAME, Menu.REDO );
        }
    }

    public Canvas newSchemeTab(VPackage pkg, InputStream inputStream) {
        assert SwingUtilities.isEventDispatchThread();

        Canvas c = new Canvas(pkg,
                new File(pkg.getPath()).getParent() + File.separator);
        c.loadScheme(inputStream);
        addCanvas(c);
        return c;
    }

    public Canvas newSchemeTab(VPackage pkg, String pathToScheme) {
        Canvas c;
        try {
            c = newSchemeTab( pkg, new FileInputStream( pathToScheme ) );
            c.setLastScheme( pathToScheme );
            updateWindowTitle();
        } catch ( FileNotFoundException e ) {
            return null;
        }
        return c;
    }
    
    public void closeSchemeTab( Canvas canv ) {
        if ( canv == null )
            return;
        canv.destroy();
        tabbedPane.remove(canv);
        if (tabbedPane.getTabCount() > 0) {
            getCurrentCanvas().drawingArea.grabFocus();
        }
    }

    /**
     * Closes the given tab
     */
    void closeCanvas( Canvas canv ) {
        RuntimeProperties.removeOpenPackage( canv.getPackage().getPath() );
        closeSchemeTab( canv );
    }

    /**
     * Closes current tab
     */
    public void closeCurrentCanvas() {
        closeCanvas( getCurrentCanvas() );
    }

    /**
     * @return the aListener
     */
    EditorActionListener getActionListener() {
        if( aListener == null )
            aListener = new EditorActionListener();
        
        return aListener;
    }
    
    public ClassFieldsTableModel getClassFieldModel() {
        return dbrClassFields;
    }   
    
    void setPackageFile( File packageFile ) {
        ClassEditor.getInstance().packageFile = packageFile;
    }    

    static File getPackageFile() {
        return ClassEditor.getInstance().packageFile;
    }    
    
    public static javax.swing.filechooser.FileFilter getFileFilter( final String... formats ) {
        if ( formats != null && formats.length > 0 ) {
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                @Override
                public String getDescription() {
                    StringBuilder fst = new StringBuilder();
                    StringBuilder snd = new StringBuilder();
                    for ( String format : formats ) {
                        fst.append( format.toUpperCase() ).append( " " );
                        snd.append( "*." ).append( format.toLowerCase() ).append( " " );
                    }
                    return fst + "files (" + snd + ")";
                }

                @Override
                public boolean accept( java.io.File f ) {
                    if( f.isDirectory() ) return true;
                    for ( String format : formats ) {
                        if( f.getName().toLowerCase().endsWith( "." + format.toLowerCase() ) ) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            return filter;
        }
        return null;
    }
    
    /**
     * Saves any input string into a file.
     * 
     * @param content - file content.
     * @param format - file format (also the default file extension).
     */
    public File saveToFile( String content, String format ) {
        try {
            if ( format != null ) {
                format = format.toLowerCase();
            } else {
                throw new Exception( "File format unspecified." );
            }
            JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );

            // [Aulo] 11.02.2004
            // Set custom file filter.
            fc.setFileFilter( getFileFilter( format ) );

            int returnVal = fc.showSaveDialog( null );
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                File file = fc.getSelectedFile();

                // [Aulo] 11.02.2004
                // Check if the file name ends with a required extension. If
                // not,
                // append the default extension to the file name.
                if ( !file.getAbsolutePath().toLowerCase().endsWith( "." + format ) ) {
                    file = new File( file.getAbsolutePath() + "." + format );
                }

                // store the last open directory in system properties.
                RuntimeProperties.setLastPath( file.getAbsolutePath() );
                boolean valid = true;

                // [Aulo] 04.01.2004
                // Check if file with a predefined name already exists.
                // If file exists, confirm file overwrite, otherwise leave
                // file as it is.
                if ( file.exists() ) {
                    if ( JOptionPane.showConfirmDialog( null, "File exists.\nOverwrite file?", "Confirm Save",
                            JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.CANCEL_OPTION ) {
                        valid = false;
                    }
                }
                if ( valid ) {
                    // Save scheme.
                    try {
                        FileOutputStream out = new FileOutputStream( file );
                        out.write( content.getBytes() );
                        out.flush();
                        out.close();
                        JOptionPane.showMessageDialog( null, "Saved to: " + file.getName(), "Saved",
                                JOptionPane.INFORMATION_MESSAGE );
                        return file;

                    } catch ( Exception exc ) {
                        exc.printStackTrace();
                    }

                }
            }
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
        
        return null;
    }    
    
    boolean checkPackage() {
        if( getCurrentPackage() == null ) {
            int result = JOptionPane.showConfirmDialog( this, "Select package and continue?", "Package not selected!", 
                    JOptionPane.YES_NO_OPTION );
            // TODO
//            if( result == JOptionPane.YES_OPTION ) {
//                selectPackage();
//            }
//            if( getPackageFile() == null ) {
//                return false;
//            }
        }
        
        return true;
    }
    
    
    public File selectFile() {
        JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
        fc.setFileFilter( new CustomFileFilter( CustomFileFilter.EXT.XML ) );

        int returnVal = fc.showOpenDialog( null );
        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            File file = fc.getSelectedFile();
            if ( !file.getAbsolutePath().toLowerCase().endsWith( ".xml" ) ) {
                file = new File( file.getAbsolutePath() + ".xml" );
            }
            RuntimeProperties.setLastPath( file.getAbsolutePath() );
            return file;
        }
        return null;
    }
    
    public void deleteClass() {
        File f = selectFile();
        if ( f != null )
            deleteClassFromPackage( f );
    }

    public void deleteClassFromPackage( File file ) {
        try {
            System.out.println("deleteClassFromPackage");
            System.out.println("file " + file);
            System.out.println("packageClassNamesList " + packageClassNamesList);
            System.out.println("packageClassList " + packageClassList);
            
            ci = new ClassImport( file, packageClassNamesList, packageClassList );
            dcd.newJList( packageClassNamesList );
            dcd.setLocationRelativeTo( rootPane );
            dcd.setVisible( true );
            dcd.repaint();
            String selection = dcd.getSelectedValue();
            System.out.println("selection " + selection);
            if ( selection == null )
                return;
            boolean deleteJavaClass = dcd.deleteClass();
            
            VPackage pkg;
            if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
                System.out.println(pkg);
                System.out.println(pkg.getClasses());
                
                System.out.println(pkg.getClass(selection));
                
                PackageClass packageClass = pkg.getClass(selection);
                
                new PackageXmlProcessor( packageFile ).removePackageClass(packageClass);
                if ( deleteJavaClass ) {
                    File javaFile = new File( file.getParent() + File.separator + selection + ".java" );
                    javaFile.delete();
                }
                JOptionPane.showMessageDialog( null, "Deleted " + selection + " from package: " + file.getName(), "Deleted",
                        JOptionPane.INFORMATION_MESSAGE );                
            }
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
       
    }   
    
    public void loadPortGraphicClass(boolean openFlag, IconPort port) {
        File f = selectFile();
        if ( f != null ){
        	
        	cig = new ClassImport( f, packageClassNamesList, packageClassList );
            
            /**
             *  @TODO new method to sort package list
             *  */
            
/*            ArrayList<String> templist = new ArrayList<String>();
            for (String name : packageClassNamesList) {
				if(name)
			}*/
            //= packageClassNamesList
            
            ChooseClassDialog dialog = new ChooseClassDialog( packageClassNamesList, "Select Port Graphic Class" );
            dialog.newJList( packageClassNamesList );
            dialog.setLocationRelativeTo( rootPane );
            dialog.setVisible( true );
            dialog.repaint();
            String selection = dialog.getSelectedValue();
            System.out.println("selection " + selection);
        	
            importPortGraphics(f, selection, openFlag, port.getName());

        }
    }  
    
    public boolean savePortGraphicToXML(){
    	return true;
    }
    
    public void loadClass() {
        File f = selectFile();
        if ( f != null )
        	importClassFromPackage( f );
    }  
    
    
    public void importPortGraphics(File file, String selection, boolean openFlag, String portName){
    	
    	if ( selection == null )
             return;
    	Canvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
    	Port targetPort = curCanvas.getObjectList().getPortById(portName);
    	if(targetPort == null)
    		return;
    	
    	 try {
    		 
             VPackage pkg;
             if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
            	 PackageClass pClass = pkg.getClass(selection);
            	 
            	 curCanvas.mListener.repaintPort(targetPort, pClass.getGraphics(), openFlag);
             }
    	
    	 } catch ( Exception exc ) {
             exc.printStackTrace();
         }    	
    	// curCanvas.repaint();
    }
    
    public void importClassFromPackage( File file ) {
        
        ci = new ClassImport( file, packageClassNamesList, packageClassList );
        ccd.newJList( packageClassNamesList );
        ccd.setLocationRelativeTo( rootPane );
        ccd.setVisible( true );
        ccd.repaint();
        String selection = ccd.getSelectedValue();
        
        System.out.println("selection " + selection);

        Canvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
        /*   Clear pane on new import */
        if(curCanvas != null){
        	curCanvas.clearObjects();
        }
        
       /*for (int i = 0; i < curCanvas.getComponentCount(); i++){
             	 curCanvas.getComponent(i);
    	}*/
        /* Temporary magic numbers */
        int classX = ( curCanvas.drawingArea.getWidth() / 3 );
        int classY = ( curCanvas.drawingArea.getHeight() / 3 );
        
        
        if ( selection == null )
            return;
        
        try {
            VPackage pkg;
            if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
                RuntimeProperties.setLastPath( file.getAbsolutePath() );
                
                ClassEditor classEditor = ClassEditor.getInstance();
                PackageClass pClass = pkg.getClass(selection);
                ClassEditor.className = pClass.getName();
                ClassEditor.classDescription = pClass.getDescription();
                ClassEditor.classIcon = pClass.getIcon();
                ClassEditor.componentType = pClass.getComponentType();
                emptyClassFields();
                fields.clear();
                
                if (curCanvas != null) {
                	classEditor.getCurrentCanvas().setPackage(pkg);
                	classEditor.updateWindowTitle();
                }
                
                if (pClass.getGraphics() != null && pClass.getGraphics().getShapes() != null) {
                	ClassGraphics classGraphics = pClass.getGraphics();
	                BoundingBox box = new BoundingBox( classGraphics.getBoundX(), classGraphics.getBoundY(), 
	                		classGraphics.getBoundWidth(), classGraphics.getBoundHeight() );
	                if (box != null) {
	                	curCanvas.mListener.addShape(box, classX, classY);	
	                	curCanvas.iconPalette.boundingbox.setEnabled( false );
	                }                	
                	
					ArrayList<Shape> shapes = classGraphics.getShapes();
					for (Shape shape : shapes) {
						curCanvas.mListener.addShape(shape, classX, classY);
					}
				}
                if (pClass.getPorts() != null) {
    				ArrayList<Port> ports = pClass.getPorts();
                    for (Port port : ports) {
                    	curCanvas.mListener.drawPort(port, classX, classY);
    				}
                }
                
                if (pClass.getFields() != null) {
	                Collection<ClassField> cFields = pClass.getFields();
	                this.fields = new ArrayList<ClassField>(cFields);
	                for ( int i = 0; i < fields.size(); i++ ) {
	                    String[] row = { ( fields.get( i ) ).getName(), ( fields.get( i ) ).getType(), ( fields.get( i ) ).getValue() };
	                    dbrClassFields.addRow( row );
	                }
	                
	                for (ClassField classField : cFields) {
	                	ClassGraphics classGraphics = classField.getKnownGraphics();
	                	if (classGraphics != null) {
		                	ArrayList<Shape> cShapes = classGraphics.getShapes();
							for (Shape shape : cShapes) {
								curCanvas.mListener.addShape(shape, classX, classY);
							}
	                	}
					}
                }
                curCanvas.drawingArea.repaint();
                /*Port temp = curCanvas.getObjectList().getPortById("1");     //// Test data.
                System.out.println("classEditor port check" + temp.toString()); */
                classEditor.setPackageFile(file);
            }
        } catch ( Exception exc ) {
            exc.printStackTrace();
        }
        
        repaint();
    }    
    
    public void createPackage() {
    	System.out.println("classEditor createPackage()");
        PackagePropertiesDialog p = new PackagePropertiesDialog();
        p.setVisible( true );
        savePackage();
    } // createPackage
    
    // TODO replace with package xml processor
    private void savePackage() {
        StringBuilder sb = new StringBuilder();
        sb.append( "<?xml version=\'1.0\' encoding=\'utf-8\'?>\n" );
        sb.append( "\n" );
        sb.append( "<!DOCTYPE package SYSTEM \"" + RuntimeProperties.PACKAGE_DTD + "\">\n" );
        sb.append( "<package>\n" );
        sb.append( "<name>" + ClassEditor.getInstance().packageName + "</name>\n" );
        sb.append( "<description>" + ClassEditor.getInstance().packageDesc + "</description>\n" );
        sb.append( "</package>" );
        if ( packageParamsOk ) {
            File file = saveToFile( sb.toString(), "xml" );
            setPackageFile(file);
            try {
                VPackage pkg;
                if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
                    RuntimeProperties.setLastPath( file.getAbsolutePath() );
                    Canvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
                    ClassEditor classEditor = ClassEditor.getInstance();
                    if (curCanvas != null) {
                    	classEditor.getCurrentCanvas().setPackage(pkg);
                    	classEditor.updateWindowTitle();
                    } else {
                    	classEditor.openNewCanvasWithPackage(file);
                    }
                }
            } catch ( Exception exc ) {
                exc.printStackTrace();
            }            
        }
    } // savePackage  
    
    public void exportShapesToPackage() {
    	validateClassParamsForPropDialog();
    	/* @TODO fake validation! */
        classParamsOk = true;
        if ( classParamsOk ) {
            if ( getCurrentCanvas().isBBPresent() ) {
                saveToPackage();
            } else {
                JOptionPane.showMessageDialog( null, "Please define a bounding box.", "Bounding box undefined",
                        JOptionPane.INFORMATION_MESSAGE );
            }
        }
    } // exportShapesToPackage 

    /**
     * Validate class params for save
     */
   /** private boolean validateClassParams(){
    	boolean status = true;
    	if(ClassEditor.className == null){
    		JOptionPane.showMessageDialog( null, "Class name required", "Class name required",
                    JOptionPane.INFORMATION_MESSAGE );
    		status=false;
    	}
    	return status;
    }*/
    
    private void validateClassParamsForPropDialog() {
        if ( ClassEditor.className == null || ClassEditor.classDescription == null
                || ClassEditor.getClassIcon() == null
                || ClassEditor.componentType == null
                || ( ClassEditor.className != null && ClassEditor.className.trim().length() == 0 )
                || ( ClassEditor.classDescription != null && ClassEditor.classDescription.trim().length() == 0 )
                || ( ClassEditor.getClassIcon() != null && ClassEditor.getClassIcon().trim().length() == 0 ) ) {
            new ClassPropertiesDialog( dbrClassFields, false );
            //validateClassParamsForPropDialog();
        } 
    } // validateClassParams    
    
    public void saveToPackage() {
    	// TODO create PackageClass and export to package
    	// add shapes
    	// add ports
    	// add BB
    	// add classfields
    	
    	File f = selectFile();
    	
    	Canvas canv = getCurrentCanvas();
        ArrayList<GObj> selectedObjects = canv.getObjectList();
        
        if ( selectedObjects.size() > 0 ) {
            
            PackageClass pc = new PackageClass( ClassEditor.className );
            
            ClassGraphics cg = new ClassGraphics();
            
            ArrayList<Shape> shapes = new ArrayList<Shape>();
            ArrayList<Port> ports = new ArrayList<Port>();
            // TODO fields
            
            for ( GObj obj : selectedObjects ) {
            	
            	shapes.addAll(obj.getShapes());
            	ports.addAll(obj.getPortList());
            	
            	for (Shape shape : obj.getShapes()) {
            		
            		String test = shape.getColor().toString();
            		System.out.println("SHAPE TEST - " + test);
            		shape.setX(obj.getX());
            		shape.setY(obj.getY());
            		System.out.println("ADD SHAPE - " + shape.toText());
					cg.addShape(shape);
				}
            	for (Port port : obj.getPortList()) {            		
                    
                    port.setX(obj.getX());
                    port.setY(obj.getY());
                    pc.addPort(port);
				}            	
            }
            
            System.out.println("cg SHAPES - " + cg.getShapes());
            
            File packageFile = f;
            
            pc.setIcon( ClassEditor.getClassIcon() );
            pc.setDescription( ClassEditor.classDescription );
            
            pc.setComponentType(ClassEditor.componentType);
            
            pc.addGraphics( cg );
            
            
    		for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
    			String fieldName = dbrClassFields.getValueAt( i, iNAME );
    			String fieldType = dbrClassFields.getValueAt( i, iTYPE );
    			String fieldValue = dbrClassFields.getValueAt( i, iVALUE );

    			if ( fieldType != null ) {
    				 ClassField cf = new ClassField(fieldName, fieldType, fieldValue);
    				 pc.addField( cf );
    			}
    		}
            
            //1. Write to package
            new PackageXmlProcessor( packageFile ).addPackageClass( pc );
            
            String _className = ClassEditor.className;
            
            
            /* See if .java file exists */
            File javaFile = new File( packageFile.getParent() + File.separator + _className + ".java" );
            /* See if previews .java file exists */
            File prevJavaFile = new File( packageFile.getParent() + File.separator + _className + ".java" );
            

            /* If file exists show confirmation dialog */
            int overwriteFile = JOptionPane.YES_OPTION;
            if ( javaFile.exists() ) {

                overwriteFile = JOptionPane.showConfirmDialog( null, "Java class already exists. Overwrite?" );
            }

            if ( overwriteFile != JOptionPane.CANCEL_OPTION ) {
                
                if ( overwriteFile == JOptionPane.YES_OPTION ) {
                	String fileText = null;
                	if (prevJavaFile.exists()) {
                		fileText = FileFuncs.getFileContents(prevJavaFile);
                	} else {
                		fileText = "class " + _className + " {";
                		fileText += "\n    /*@ specification " + _className + " {\n";

                		for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
                			String fieldName = dbrClassFields.getValueAt( i, iNAME );
                			String fieldType = dbrClassFields.getValueAt( i, iTYPE );

                			if ( fieldType != null ) {
                				fileText += "    " + fieldType + " " + fieldName + ";\n";
                			}
                		}
                		fileText += "    }@*/\n \n}";
                	}
                	FileFuncs.writeFile( javaFile, fileText );
                }

                JOptionPane.showMessageDialog( null, "Saved to package: " + packageFile.getName(), "Saved",
                        JOptionPane.INFORMATION_MESSAGE );
            }

        } else {
            JOptionPane.showMessageDialog( canv, "Nothing to export!" );
        }    	
    }
    
    
    /**
     * @param classIcon the classIcon to set
     */
    public static void setClassIcon( String classIcon ) {
        ClassEditor.classIcon = classIcon;
    }

    /**
     * @return the classIcon
     */
    public static String getClassIcon() {
        return classIcon;
    }
    
    
    /**
     * Empty the class fields table.
     */
    private void emptyClassFields() {
        if ( dbrClassFields != null )
            dbrClassFields.setRowCount( 0 );
    } // emptyClassFields 
    
    
}

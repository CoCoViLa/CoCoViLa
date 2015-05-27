package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import org.slf4j.*;

import ee.ioc.cs.vsle.editor.EditorActionListener.CloneAction;
import ee.ioc.cs.vsle.editor.EditorActionListener.DeleteAction;
import ee.ioc.cs.vsle.editor.EditorActionListener.RedoAction;
import ee.ioc.cs.vsle.editor.EditorActionListener.UndoAction;
import ee.ioc.cs.vsle.editor.scheme.*;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * <a href="http://vsledit.sourceforge.net">Visual Specification Language Editor</a>
 * main module for combining separate logical units (Built in the IconEditor)
 * into a structured schema.
 * 
 * @author Ando Saabas, Aulo Aasma
 * @version 1.0
 */
public class Editor extends JFrame implements ChangeListener {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger( Editor.class );
    
    private static Editor s_instance;

    DnDTabbedPane tabbedPane;

    private EditorActionListener aListener;
    DeleteAction deleteAction;
    UndoAction undoAction;
    RedoAction redoAction;
    CloneAction cloneAction;

    JMenuBar menuBar;

    public static final String WINDOW_TITLE = "CoCoViLa - Scheme Editor";

    private JCheckBoxMenuItem gridCheckBox;
    private JCheckBoxMenuItem ctrlCheckBox;
    private JCheckBoxMenuItem showPortCheckBox;
    private JCheckBoxMenuItem showObjectNamesCheckBox;
    
    /**
     * Class constructor [1].
     */
    private Editor() {
        initialize();
        validate();
    } // Editor

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
        menuItem = new JMenuItem( Menu.NEW_SCHEME, KeyEvent.VK_N );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.LOAD_SCHEME, KeyEvent.VK_O );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.RELOAD_SCHEME, KeyEvent.VK_R );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.SAVE_SCHEME, KeyEvent.VK_S );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.SAVE_SCHEME_AS );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK ) );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.DELETE_SCHEME, KeyEvent.VK_D );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        submenu = new JMenu( Menu.EXPORT_MENU );
        menu.add(submenu);
        //submenu.setMnemonic( KeyEvent.VK_E );
        
        SchemeExporter.makeSchemeExportMenu(submenu, getActionListener());
        
        // Export window graphics
        submenu.add(GraphicsExporter.getExportMenu());

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
        menu.add(cloneAction);

        menuItem = new JMenuItem(Menu.SCHEME_FIND, KeyEvent.VK_F);
        menuItem.addActionListener(getActionListener());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem( Menu.SELECT_ALL, KeyEvent.VK_A );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLEAR_ALL, KeyEvent.VK_C );
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
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
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
        
        ctrlCheckBox = new JCheckBoxMenuItem( Menu.CONTROL_PANEL, RuntimeProperties.isShowControls() );
        ctrlCheckBox.setMnemonic( 'C' );
        ctrlCheckBox.addActionListener( getActionListener() );
        menu.add( ctrlCheckBox );
        
        showPortCheckBox = new JCheckBoxMenuItem( Menu.SHOW_PORTS, true );
        showPortCheckBox.addActionListener( getActionListener() );
        menu.add( showPortCheckBox );
        
        showObjectNamesCheckBox = new JCheckBoxMenuItem( Menu.SHOW_NAMES, false );
        showObjectNamesCheckBox.addActionListener( getActionListener() );
        menu.add( showObjectNamesCheckBox );
        
        //sync View with current canvas
        menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                Canvas canvas;
                
                if( ( canvas = getCurrentCanvas() ) == null ) 
                    return;
                
                gridCheckBox.setSelected( canvas.isGridVisible() );
                ctrlCheckBox.setSelected( canvas.isCtrlPanelVisible() );
                showPortCheckBox.setSelected( canvas.isDrawPorts() );
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
        
        menu = new JMenu( Menu.MENU_PACKAGE );
        menu.setMnemonic( KeyEvent.VK_P );
        menuItem = new JMenuItem( Menu.LOAD, KeyEvent.VK_L );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.RELOAD, KeyEvent.VK_R );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.INFO, KeyEvent.VK_I );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLOSE, KeyEvent.VK_C );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLOSE_ALL );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menuBar.add( menu );
        menu.add( new JSeparator() );
        final JMenu submenuRecent = new JMenu( Menu.RECENT );
        submenuRecent.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            final JMenuItem empty = new JMenuItem( "Empty" );

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {

                makeRecentSubMenu( submenuRecent );

                if ( submenuRecent.getMenuComponentCount() == 0 ) {

                    submenuRecent.add( empty );
                    empty.setEnabled( false );

                } else {
                    if ( ! ( ( submenuRecent.getMenuComponentCount() == 1 ) && ( submenuRecent.getPopupMenu().getComponentIndex(
                            empty ) >= -1 ) ) ) {
                        submenuRecent.remove( empty );
                    }
                }

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
        menu.add( submenuRecent );
        final JMenu menuScheme = new JMenu( Menu.MENU_SCHEME );
        menuScheme.setMnemonic( KeyEvent.VK_S );
        makeSchemeMenu( menuScheme );

        menuScheme.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {

                makeSchemeMenu( menuScheme );

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

        /*
         * menuItem = new JMenuItem("Planner");
         * menuItem.addActionListener(aListener); menuScheme.add(menuItem);
         * menuItem = new JMenuItem("Plan, compile, run");
         * menuItem.setActionCommand("Run");
         * menuItem.addActionListener(aListener); menuScheme.add(menuItem);
         */
        // menuScheme.setMnemonic(KeyEvent.VK_A);
        menuBar.add( menuScheme );
        menu = new JMenu( Menu.MENU_OPTIONS );
        menu.setMnemonic( KeyEvent.VK_O );

        menuItem = new JMenuItem( Menu.SETTINGS, KeyEvent.VK_S );
        menuItem.addActionListener( getActionListener() );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_J, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.FONTS );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        menuItem = new JMenuItem( Menu.SAVE_SETTINGS );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        submenu = new JMenu( Menu.MENU_LAF );
        submenu.setMnemonic( KeyEvent.VK_L );
        Look.getInstance().createMenuItems( submenu );
        menu.add( submenu );
        menuBar.add( menu );
        
        makeToolsMenu( menuBar );
        
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
     * @param menu
     */
    private void makeRecentSubMenu( JMenu menu ) {

        menu.removeAll();

        for ( final Map.Entry<String, String> entry : RuntimeProperties.getRecentPackages().entrySet() ) {

            JMenuItem menuItem = new JMenuItem( entry.getKey() );
            menuItem.setToolTipText( entry.getValue() );
            
            menuItem.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent e ) {
                    openNewCanvasWithPackage( new File( entry.getValue() ) );
                }
            } );
            menu.add( menuItem );
        }
    }

    /**
     * @param menu
     */
    private void makeSchemeMenu( JMenu menu ) {
        menu.removeAll();

        // Specification...
        JMenuItem menuItem = new JMenuItem( Menu.SPECIFICATION, KeyEvent.VK_S );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        //Extend
        menuItem = new JMenuItem( Menu.EXTEND_SPEC, KeyEvent.VK_E );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        menu.add( new JSeparator() );
        // Run
        menuItem = new JMenuItem( Menu.RUN, KeyEvent.VK_R );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        //Propagate
        menuItem = new JCheckBoxMenuItem( Menu.PROPAGATE_VALUES, RuntimeProperties.isPropagateValues() );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        //Compute goal
        menuItem = new JCheckBoxMenuItem( Menu.COMPUTE_GOAL, RuntimeProperties.isComputeGoal() );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        menu.add( new JSeparator() );

        // Values
        menuItem = new JMenuItem( Menu.SCHEME_VALUES, KeyEvent.VK_V );
        menuItem.addActionListener( getActionListener() );
        boolean enabled = getCurrentCanvas() != null && getCurrentCanvas().getLastProgramRunnerID() != 0;
        menuItem.setEnabled( enabled );
        if(!enabled)
            menuItem.setToolTipText( "Run the scheme first" );
        menu.add( menuItem );
        
        menu.add( new JSeparator() );

        // Options
        menuItem = new JMenuItem( Menu.SCHEMEOPTIONS, KeyEvent.VK_O );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
    }

    /**
     * 
     */
    private void makeToolsMenu( JMenuBar _menuBar ) {
        
        JMenu menu = new JMenu( Menu.MENU_TOOLS );
        menu.setMnemonic( KeyEvent.VK_T );
        _menuBar.add( menu );
        
        JMenuItem menuItem = new JMenuItem( Menu.EXPERT_TABLE );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );

        menuItem = new JCheckBoxMenuItem( Menu.SHOW_ALGORITHM, RuntimeProperties.isShowAlgorithm() );
        menuItem.setToolTipText( "If checked, after planning a window with the synthesized algorithm will be shown" );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        menuItem = new JMenuItem( Menu.VIEW_THREADS );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
        
        menuItem = new JMenuItem( Menu.JAVA_CONSOLE );
        menuItem.addActionListener( getActionListener() );
        menu.add( menuItem );
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

        logger.info( "Java Version: {}", version );

        String directory = RuntimeProperties.getWorkingDirectory();

        logger.info( "Working directory: {}", directory );

        RuntimeProperties.init();

        Look.getInstance().initDefaultLnF();

        final Editor window = new Editor();
        s_instance = window;
        
        if ( !RuntimeProperties.isFromWebstart() && args.length > 0 ) {

            if ( args[ 0 ].equals( "-p" ) ) {

                String dir = ( args.length == 3 ) ? directory + args[ 2 ] + File.separator : directory;

                Synthesizer.parseFromCommandLine( dir, args[ 1 ] );

            } else {

                logger.info( args[ 0 ] + " read from command line." );

                File file = new File( directory + args[ 0 ] );

                if ( file.exists() ) {
                    window.openNewCanvasWithPackage( file );
                }
            }

        } else {

            for ( String packageFile : RuntimeProperties.getPrevOpenPackages() ) {

                File f = new File( packageFile );

                if ( f.exists() ) {
                    logger.debug( "Found package file name {} from the configuration file.", packageFile );
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

    public void openOptionsDialog() {
        OptionsDialog o = new OptionsDialog( Editor.this );
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
            ctrlCheckBox.setSelected( canvas.isCtrlPanelVisible() );
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

    public static Editor getInstance() {
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

    /**
     * Shows the scheme search dialog.
     */
    public void showSchemeSearchDialog() {
        SchemeSearchDialog.getDialog().show();
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

}

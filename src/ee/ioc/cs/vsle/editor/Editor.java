package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import ee.ioc.cs.vsle.editor.EditorActionListener.*;
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

    private static Editor s_instance;

    JTabbedPane tabbedPane;

    EditorActionListener aListener;
    DeleteAction deleteAction;
    UndoAction undoAction;
    RedoAction redoAction;

    JMenuBar menuBar;

    public static final String WINDOW_TITLE = "CoCoViLa - Scheme Editor";

    private JCheckBoxMenuItem gridCheckBox;

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
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener( this );
        undoAction = new UndoAction();
        redoAction = new RedoAction();
        deleteAction = new DeleteAction();
        aListener = new EditorActionListener();
        makeMenu();
        getContentPane().add( tabbedPane );
        getRootPane().getActionMap().put( DeleteAction.class, deleteAction );
        getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 ),
                DeleteAction.class );
    } // initialize

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
        menuItem = new JMenuItem( Menu.SAVE_SCHEME, KeyEvent.VK_S );
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.SAVE_SCHEME_AS );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.LOAD_SCHEME, KeyEvent.VK_O );
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.RELOAD_SCHEME, KeyEvent.VK_R );
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.DELETE_SCHEME, KeyEvent.VK_D );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.PRINT, KeyEvent.VK_P );
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.EXIT, KeyEvent.VK_X );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuBar.add( menu );
        menu = new JMenu( Menu.MENU_EDIT );
        menu.setMnemonic( KeyEvent.VK_E );

        menu.add( undoAction );
        menu.add( redoAction );
        // as it is already defined in the object popup menu and
        // otherwise would require implementing all the object
        // popup items in the current menu as well.
        /*
         * menuItem = new JMenuItem("Clone", KeyEvent.VK_C);
         * menuItem.addActionListener(aListener); menu.add(menuItem);
         */
        menuItem = new JMenuItem( Menu.SELECT_ALL, KeyEvent.VK_A );
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLEAR_ALL, KeyEvent.VK_C );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );

        gridCheckBox = new JCheckBoxMenuItem( Menu.GRID, RuntimeProperties.isShowGrid() );
        gridCheckBox.setMnemonic( 'G' );
        gridCheckBox.addActionListener( aListener );
        menu.add( gridCheckBox );

        final JCheckBoxMenuItem painterEnabled = new JCheckBoxMenuItem( Menu.CLASSPAINTER, true );
        painterEnabled.addActionListener( aListener );
        menu.add( painterEnabled );

        menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            public void popupMenuCanceled( PopupMenuEvent e ) {
                // ignore
            }

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                // ignore
            }

            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                Canvas canvas = Editor.getInstance().getCurrentCanvas();
                if ( canvas == null || !canvas.getCurrentPackage().hasPainters() ) {
                    painterEnabled.setVisible( false );
                } else {
                    painterEnabled.setVisible( true );
                    painterEnabled.setSelected( canvas.isEnableClassPainter() );
                }
            }

        } );

        menuBar.add( menu );
        menu = new JMenu( Menu.MENU_PACKAGE );
        menu.setMnemonic( KeyEvent.VK_P );
        menuItem = new JMenuItem( Menu.LOAD, KeyEvent.VK_L );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.RELOAD, KeyEvent.VK_R );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.INFO, KeyEvent.VK_I );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLOSE, KeyEvent.VK_C );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.CLOSE_ALL );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuBar.add( menu );
        menu.add( new JSeparator() );
        final JMenu submenuRecent = new JMenu( Menu.RECENT );
        submenuRecent.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            final JMenuItem empty = new JMenuItem( "Empty" );

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

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                // ignore
            }

            public void popupMenuCanceled( PopupMenuEvent e ) {
                // ignore
            }

        } );
        menu.add( submenuRecent );
        final JMenu menuScheme = new JMenu( Menu.MENU_SCHEME );
        menuScheme.setMnemonic( KeyEvent.VK_S );

        menuScheme.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {

                makeSchemeMenu( menuScheme );

            }

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                // ignore
            }

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
        menuItem.addActionListener( aListener );
        menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_J, ActionEvent.CTRL_MASK ) );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.SAVE_SETTINGS );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );

        menuItem = new JMenuItem( Menu.VIEW_THREADS );
        menuItem.addActionListener( aListener );
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
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menu.addSeparator();
        menuItem = new JMenuItem( Menu.LICENSE, KeyEvent.VK_L );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menuItem = new JMenuItem( Menu.ABOUT, KeyEvent.VK_A );
        menuItem.addActionListener( aListener );
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

    void makeRecentSubMenu( JMenu menu ) {

        menu.removeAll();

        for ( final String packageName : RuntimeProperties.getRecentPackages().keySet() ) {

            JMenuItem menuItem = new JMenuItem( packageName );

            menuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    loadPackage( new File( RuntimeProperties.getRecentPackages().get( packageName ) ) );
                }
            } );
            menu.add( menuItem );
        }
    }

    void makeSchemeMenu( JMenu menu ) {
        menu.removeAll();

        // Specification...
        JMenuItem menuItem = new JMenuItem( Menu.SPECIFICATION, KeyEvent.VK_S );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
        menu.add( new JSeparator() );
        // Run
        menuItem = new JMenuItem( Menu.RUN, KeyEvent.VK_R );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );

        // Run & Propagate
        menuItem = new JMenuItem( Menu.RUNPROPAGATE, KeyEvent.VK_P );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );

        menu.add( new JSeparator() );

        menuItem = new JCheckBoxMenuItem( Menu.SHOW_ALGORITHM, RuntimeProperties.isShowAlgorithm() );
        menuItem.setToolTipText( "If checked, after planning a window with the synthesized algorithm will be shown" );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );

        // Options
        menuItem = new JMenuItem( Menu.SCHEMEOPTIONS, KeyEvent.VK_O );
        menuItem.addActionListener( aListener );
        menu.add( menuItem );
    }

    /**
     * Upon platform, use OS-specific methods for opening the URL in required
     * browser.
     * 
     * @param url - URL to be opened in a browser. Capable of browsing local
     *                documentation as well if path is given with file://
     */
    public static void openInBrowser( String url ) {
        try {
            // Check if URL is defined, otherwise there is no reason for opening
            // the browser in the first place.
            if ( url != null && url.trim().length() > 0 ) {
                // Get OS type.
                String osType = getOsType();
                // Open URL with OS-specific methods.
                if ( osType != null && osType.equalsIgnoreCase( "Windows" ) ) {
                    Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + url );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
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

    /**
     * Return operating system type. Uses isWin, isMac, isUnix methods for
     * deciding on Os type and returns always the internally defined Os Type
     * (WIN,MAC or UNIX).
     * 
     * @return String - internally defined OS TYPE.
     */
    public static String getOsType() {
        Properties sysProps = System.getProperties();
        try {
            if ( sysProps != null ) {
                String osType = sysProps.getProperty( "os.name" );
                if ( isWin( osType ) ) {
                    return "Windows";
                }
                return "NotWindows";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Package loader.
     * 
     * @param f - package file to be loaded.
     */
    void loadPackage( File f ) {

        if ( f != null ) {
            String path;
            RuntimeProperties.setLastPath( path = f.getAbsolutePath() );
            RuntimeProperties.addOpenPackage( path );

            try {

                Canvas canvas = new Canvas( f );
                String packageName = f.getName().substring( 0, f.getName().indexOf( "." ) );

                int count = 0;

                for ( Component canv : tabbedPane.getComponents() ) {
                    if ( packageName.equals( ( (Canvas) canv ).getCurrentPackage().getName() ) ) {
                        count++;
                    }
                }

                if ( count > 0 ) {
                    packageName = packageName + " (" + ( count + 1 ) + ")";
                    canvas.setTitle( packageName );
                }

                tabbedPane.addTab( packageName, canvas );
                tabbedPane.setSelectedComponent( canvas );
            } catch ( Exception e ) {
                String message = "Unable to load package \"" + f.getAbsolutePath() + "\"";
                db.p( message );
                if ( RuntimeProperties.isLogDebugEnabled() ) {
                    e.printStackTrace( System.out );
                }
                JOptionPane.showMessageDialog( Editor.getInstance(), message, "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
    } // loadPackage

    /**
     * Main method for module unit-testing.
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            // Java 1.5 seems to not support @Override annotations for
            // interface methods. bug_id=5008260
            // @Override
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

        for ( int i = 0; i < args.length; i++ ) {
            if ( args[ i ].equals( "-webstart" ) ) {
                RuntimeProperties.setFromWebstart();

                SystemUtils.unpackPackages();
            }
        }

        String version = System.getProperty( "java.version" );

        System.err.println( "Java Version: " + version );

        if ( version.compareTo( "1.5.0" ) < 0 ) {

            System.err.println( "CoCoViLa requires at least Java 1.5.0 to run!" );
            System.exit( 1 );
        }

        String directory = RuntimeProperties.getWorkingDirectory();

        System.err.println( "Working directory: " + directory );

        RuntimeProperties.init();

        Look.getInstance().initDefaultLnF();

        final Editor window = new Editor();

        if ( !RuntimeProperties.isFromWebstart() && args.length > 0 ) {

            if ( args[ 0 ].equals( "-p" ) ) {

                String dir = ( args.length == 3 ) ? directory + args[ 2 ] + RuntimeProperties.FS : directory;

                Synthesizer.parseFromCommandLine( dir, args[ 1 ] );

            } else {

                db.p( args[ 0 ] + " read from command line." );

                File file = new File( directory + args[ 0 ] );

                if ( file.exists() ) {
                    window.loadPackage( file );
                }
            }

        } else {

            for ( String packageFile : RuntimeProperties.getOpenPackages() ) {

                File f = new File( packageFile );

                if ( f.exists() ) {
                    db.p( "Found package file name " + packageFile + " from the configuration file." );
                    window.loadPackage( f );
                }
            }

        }

        window.setTitle( WINDOW_TITLE );

        //restore window location, size and state
        final String[] bs = RuntimeProperties.getSchemeEditorWindowProps().split( ";" );

        SwingUtilities.invokeLater( new Runnable() {
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

        s_instance = window;

        /* ******************** Init Factories ******************** */
        SpecGenerator.init();
        XMLSpecGenerator.init();
    }

    public void clearPane() {

        Canvas canv = getCurrentCanvas();
        canv.destroy();
        tabbedPane.remove( canv );
        if ( tabbedPane.getTabCount() > 0 ) {
            tabbedPane.setSelectedIndex( 0 );
            getCurrentCanvas().drawingArea.grabFocus();
        }
    }

    public void openOptionsDialog() {
        OptionsDialog o = new OptionsDialog( Editor.this );
        o.setVisible( true );
        repaint();
    }

    public VPackage getCurrentPackage() {
        if ( getCurrentCanvas() != null ) {
            return getCurrentCanvas().getCurrentPackage();
        }
        return null;
    }

    public Canvas getCurrentCanvas() {
        return (Canvas) tabbedPane.getSelectedComponent();

    }

    public void stateChanged( ChangeEvent e ) {
        refreshUndoRedo();
        Canvas canvas = getCurrentCanvas();
        if ( canvas != null ) {
            gridCheckBox.setSelected( canvas.isGridVisible() );
            canvas.drawingArea.repaint();
            canvas.drawingArea.requestFocusInWindow();
        }

        updateWindowTitle();
    }

    /**
     * Updates the title of the window to reflect current scheme name.
     */
    public void updateWindowTitle() {
        String windowTitle = null;

        Canvas canvas = getCurrentCanvas();
        if ( canvas != null )
            windowTitle = canvas.getSchemeTitle();

        if ( windowTitle == null )
            windowTitle = WINDOW_TITLE;
        else
            windowTitle += " - " + WINDOW_TITLE;

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
}

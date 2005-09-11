package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.editor.Menu;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.synthesize.Synthesizer;

import java.io.*;

import java.util.Properties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.*;

/**
 * Visual Specification Language Editor main module
 * for combining separate logical units (Built in the
 * IconEditor) into a structured schema.
 * @author Ando Saabas, Aulo Aasma
 * @link http://vsledit.sourceforge.net
 * @version 1.0
 */
public class Editor extends JFrame implements ChangeListener {

	JTabbedPane tabbedPane = new JTabbedPane();
	EditorActionListener aListener;
	JMenuBar menuBar;

	JPanel infoPanel; // Panel for runtime information, mouse coordinates, selected objects etc.
	public JPanel mainPanel = new JPanel();
	JLabel posInfo; // Mouse position.

	Dimension drawAreaSize = new Dimension(600, 500);
	KeyOps keyListener;
	public static final String WINDOW_TITLE = "COCOVILA - Scheme Editor";

	/**
	 * Class constructor [1].
	 */
	public Editor() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initialize();
		validate();
	} // Editor

	/**
	 * Class constructor [2].
	 * @param fileName - package file name.
	 */
	public Editor(String fileName) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initialize();
		File file = new File(fileName);
		loadPackage(file);
                validate();
	} // Editor

	/**
	 * Application initializer.
	 */
	public void initialize() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
		tabbedPane.addChangeListener(this);
		infoPanel = new JPanel(new GridLayout(1, 2));
		posInfo = new JLabel();
		//keyListener = new KeyOps(this);
		aListener = new EditorActionListener(this);

		mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(areaScrollPane, BorderLayout.CENTER);
		infoPanel.add(posInfo);
		mainPanel.add(infoPanel, BorderLayout.SOUTH);
		posInfo.setText("-");
		makeMenu();
		getContentPane().add(mainPanel);
                getContentPane().add(tabbedPane);
		Look look = new Look();
		look.setGUI(this);
		Look.changeLayout(
			PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
				PropertyBox.DEFAULT_LAYOUT));
	} // initialize

	/**
	 * Check if the grid should be visible or not.
	 * @return boolean - grid visibility from the properties file.
	 */
	public boolean getGridVisibility() {
		String vis = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID);
		if (vis != null) {
			int v = Integer.parseInt(vis);
			if (v < 1) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	} // getGridVisibility


	/**
	 * Build menu.
	 */
	public void makeMenu() {
            JMenuItem menuItem;

        JMenu menu;
        JMenu submenu;

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menu = new JMenu(Menu.MENU_FILE);
		menu.setMnemonic(KeyEvent.VK_F);
		menuItem = new JMenuItem(Menu.SAVE_SCHEME, KeyEvent.VK_S);
		menuItem.addActionListener(aListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.LOAD_SCHEME, KeyEvent.VK_O);
		menuItem.addActionListener(aListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(Menu.PRINT, KeyEvent.VK_P);
		menuItem.addActionListener(aListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(Menu.EXIT, KeyEvent.VK_X);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
		menuBar.add(menu);
		menu = new JMenu(Menu.MENU_EDIT);
		menu.setMnemonic(KeyEvent.VK_E);

		// as it is already defined in the object popup menu and
		// otherwise would require implementing all the object
		// popup items in the current menu as well.
		/*
		 menuItem = new JMenuItem("Clone", KeyEvent.VK_C);
		 menuItem.addActionListener(aListener);
		 menu.add(menuItem);
		 */
		menuItem = new JMenuItem(Menu.SELECT_ALL, KeyEvent.VK_A);
		menuItem.addActionListener(aListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.CLEAR_ALL, KeyEvent.VK_C);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);

		boolean showGrid = false;
		String sShowGrid = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID);
		if (sShowGrid != null) {
			showGrid = Boolean.valueOf(sShowGrid).booleanValue();
		}
		menuItem = new JCheckBoxMenuItem(Menu.GRID, showGrid);
		menuItem.setMnemonic('G');
		menuItem.addActionListener(aListener);
		menu.add(menuItem);

		menuBar.add(menu);
                menu = new JMenu( Menu.MENU_PACKAGE );
                menu.setMnemonic( KeyEvent.VK_P );
                menuItem = new JMenuItem( Menu.LOAD, KeyEvent.VK_L );
                menuItem.addActionListener( aListener );
                menu.add(menuItem);
		menuItem = new JMenuItem(Menu.CLOSE, KeyEvent.VK_C);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.INFO, KeyEvent.VK_I);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
		menuBar.add(menu);
                menu.add( new JSeparator() );
                final JMenu submenuRecent = new JMenu( Menu.RECENT );
                submenuRecent.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

                    final JMenuItem empty = new JMenuItem("Empty");

                    public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {

                        makeRecentSubMenu( submenuRecent );

                        if ( submenuRecent.getMenuComponentCount() == 0 ) {

                            submenuRecent.add( empty );
                            empty.setEnabled( false );

                        } else {
                            if ( !( ( submenuRecent.getMenuComponentCount() == 1 )
                                    &&
                                    ( submenuRecent.getPopupMenu().getComponentIndex( empty ) >= -1 ) ) ) {
                                submenuRecent.remove( empty );
                            }
                        }

                    }

                    public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}

                    public void popupMenuCanceled( PopupMenuEvent e ) {}

                } );
                menu.add(submenuRecent);
                final JMenu menuScheme = new JMenu( Menu.MENU_SCHEME );
		menuScheme.setMnemonic(KeyEvent.VK_S);

                menuScheme.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

                    final JMenuItem empty = new JMenuItem("Empty");

                    public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {

                        makeSchemeMenu( menuScheme );

                    }

                    public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}

                    public void popupMenuCanceled( PopupMenuEvent e ) {}

                } );

		/* menuItem = new JMenuItem("Planner");
		 menuItem.addActionListener(aListener);
		 menuScheme.add(menuItem);
		 menuItem = new JMenuItem("Plan, compile, run");
		 menuItem.setActionCommand("Run");
		 menuItem.addActionListener(aListener);
		 menuScheme.add(menuItem);*/
		// menuScheme.setMnemonic(KeyEvent.VK_A);
		menuBar.add(menuScheme);
		menu = new JMenu(Menu.MENU_OPTIONS);
		menu.setMnemonic(KeyEvent.VK_O);
		submenu = new JMenu(Menu.MENU_LAYOUT);
		submenu.setMnemonic(KeyEvent.VK_L);
		menuItem = new JMenuItem(Look.LOOK_CUSTOM, KeyEvent.VK_C);
		menuItem.addActionListener(aListener);
		submenu.add(menuItem);
		menuItem = new JMenuItem(Look.LOOK_METAL, KeyEvent.VK_M);
		menuItem.addActionListener(aListener);
		submenu.add(menuItem);
		menuItem = new JMenuItem(Look.LOOK_MOTIF, KeyEvent.VK_M);
		menuItem.addActionListener(aListener);
		submenu.add(menuItem);
		menuItem = new JMenuItem(Look.LOOK_WINDOWS, KeyEvent.VK_W);
		menuItem.addActionListener(aListener);
		submenu.add(menuItem);
		menuItem = new JMenuItem(Menu.SETTINGS, KeyEvent.VK_S);
		menuItem.addActionListener(aListener);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_J, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menu.add(submenu);
		menuBar.add(menu);
		menu = new JMenu(Menu.MENU_HELP);
		menu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(menu);
		menuItem = new JMenuItem(Menu.DOCS, KeyEvent.VK_D);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(Menu.LICENSE, KeyEvent.VK_L);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
		menuItem = new JMenuItem(Menu.ABOUT, KeyEvent.VK_A);
		menuItem.addActionListener(aListener);
		menu.add(menuItem);
	}


	/**
	 * Display information dialog to application user.
	 * @param title - information dialog title.
	 * @param text - text displayed in the information dialog.
	 */
	public void showInfoDialog(String title, String text) {
		JOptionPane.showMessageDialog(null, text, title,
			JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Overridden so we can exit when window is closed
	 * @param e - Window Event.
	 */
	protected void processWindowEvent(WindowEvent e) {
		// super.processWindowEvent(e); // automatic closing disabled, confirmation asked instead.
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			exitApplication();
		}
	}

	/**
	 * Close application.
	 */
	public void exitApplication() {
		int confirmed = JOptionPane.showConfirmDialog(null, "Exit Application?",
			Menu.EXIT, JOptionPane.OK_CANCEL_OPTION);
		switch (confirmed) {
			case JOptionPane.OK_OPTION:
				System.exit(0);
				break;
			case JOptionPane.CANCEL_OPTION:
				break;
		}
	}


	/**
	 * Get last file path used for loading or saving schema, package, etc.
	 * from / into a file.
	 * @return String - last used path from system properties.
	 */
	public static String getLastPath() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.LAST_PATH);
	}

	/**
	 * Get system documentation URL value.
	 * @return String - system documentation URL.
	 */
	public static String getSystemDocUrl() {
		return PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.DOCUMENTATION_URL);
	}

	/**
	 * Stores the last path used for loading or saving schema, package, etc.
	 * into system properties.
	 * @param path - last path used for loading or saving schema, package, etc.
	 */
	public static void setLastPath(String path) {
		if (path != null) {
			if (path.indexOf("/") > -1) {
				path = path.substring(0, path.lastIndexOf("/"));
			} else if (path.indexOf("\\") > -1) {
				path = path.substring(0, path.lastIndexOf("\\"));
			}
		}
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.LAST_PATH, path);
	}

        public static void setRecentPackage( String path ) {
            String recentPackages = PropertyBox.getProperty(
                    PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.RECENT_PACKAGES );

            if( recentPackages == null ) {
                recentPackages = "";
            }
            //int index = recentPackages.indexOf( path );
            if ( recentPackages.indexOf( path ) == -1 ) {
                recentPackages = recentPackages + ";" + path;
                PropertyBox.setProperty( PropertyBox.APP_PROPS_FILE_NAME,
                                     PropertyBox.RECENT_PACKAGES, recentPackages );
            }
        }

        void makeRecentSubMenu( JMenu menu ) {
            String recentPackages = PropertyBox.getProperty(
                    PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.RECENT_PACKAGES );
            if( recentPackages == null ) {
                return;
            }
            String[] packages = recentPackages.split( ";" );

            menu.removeAll();

            for ( int i = 0; i < packages.length; i++ ) {
                final File f = new File( packages[ i ] );
                if ( f.exists() ) {
                    String packageName = f.getName().substring( 0, f.getName().indexOf( "." ) );

                    JMenuItem menuItem = new JMenuItem( packageName );

                    menuItem.addActionListener( new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            loadPackage( f );
                        }
                    });
                    menu.add( menuItem );
                }
            }
        }

        void makeSchemeMenu( JMenu menu ) {
            menu.removeAll();

            //Specification...
            JMenuItem menuItem = new JMenuItem( Menu.SPECIFICATION, KeyEvent.VK_S );
            menuItem.addActionListener( aListener );
            menu.add( menuItem );

            if( getCurrentPackage() != null ) {
                menu.add( new JSeparator() );

                final String packageName = getCurrentPackage().name;
                //<package>.meth
                menuItem = new JMenuItem( packageName + ".meth", KeyEvent.VK_M );
                menuItem.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        CodeViewer cv = new CodeViewer( packageName, ".meth" );
                        cv.setSize( 550, 450 );
                        cv.setVisible( true );
                    }
                } );
                menu.add( menuItem );

                //<package>.spec
                menuItem = new JMenuItem( packageName + ".spec", KeyEvent.VK_C );
                menuItem.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        CodeViewer cv = new CodeViewer( packageName, ".spec" );
                        cv.setSize( 550, 450 );
                        cv.setVisible( true );
                    }
                } );

                menu.add( menuItem );
            }
        }
	/**
	 * Upon platform, use OS-specific methods for opening the URL in required browser.
	 * @param url - URL to be opened in a browser. Capable of browsing
	 *              local documentation as well if path is given with file://
	 */
	public static void openInBrowser(String url) {
		try {
			// Check if URL is defined, otherwise there is no reason for opening
			// the browser in the first place.
			if (url != null && url.trim().length() > 0) {
				// Get OS type.
				String osType = getOsType();
				// Open URL with OS-specific methods.
				if (osType != null && osType.equalsIgnoreCase("Windows")) {
					Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if Operating System type is Windows.
	 * @param osType - Operating System type.
	 * @return boolean - Operating System belongs to the Windows family or not.
	 */
	public static boolean isWin(String osType) {
		if (osType != null && osType.startsWith("Windows")) {
			return true;
		}
		return false;
	}

	/**
	 * Return operating system type. Uses isWin, isMac, isUnix
	 * methods for deciding on Os type and returns always the
	 * internally defined Os Type (WIN,MAC or UNIX).
	 * @return String - internally defined OS TYPE.
	 */
	public static String getOsType() {
		Properties sysProps = System.getProperties();
		try {
			if (sysProps != null) {
				String osType = sysProps.getProperty("os.name");
				if (isWin(osType)) {
					return "Windows";
				} else {
					return "NotWindows";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Package loader.
	 * @param f - package file to be loaded.
	 */
	void loadPackage(File f) {
		if (f != null) {
			Canvas canvas = new Canvas(f);
			String packageName = f.getName().substring(0, f.getName().indexOf("."));
			tabbedPane.addTab(packageName, canvas);
			tabbedPane.setSelectedComponent(canvas);
		}
	} // loadPackage


	/**
	 * Returns the window width.
	 * @return - window height.
	 */
	private static final int getWinWidth() {
		return 650;
	}

	/**
	 * Returns the window height.
	 * @return int - window height.
	 */
	private static final int getWinHeight() {
		return 600;
	}


	/**
	 * Main method for module unit-testing.
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		String directory = System.getProperty("user.dir") + System.getProperty("file.separator");
		RuntimeProperties.debugInfo = Integer.parseInt(
			PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
				PropertyBox.DEBUG_INFO));
		RuntimeProperties.gridStep = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP));
		int aa = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));
		if (aa == 0) {
			RuntimeProperties.isAntialiasingOn = false;
		} else {
			RuntimeProperties.isAntialiasingOn = true;
		}

		RuntimeProperties.customLayout = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.CUSTOM_LAYOUT);
        RuntimeProperties.snapToGrid = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SNAP_TO_GRID));

		JFrame window;
		try {
			if (args.length > 0) {
				if (args[0].equals("-p")) {
					if (args.length == 3) {
						RuntimeProperties.packageDir = directory + args[2]
							+ System.getProperty("file.separator");
					} else {
						RuntimeProperties.packageDir = directory;
					}
					Synthesizer synth = new Synthesizer();
					synth.parseFromCommandLine(args[1]);
				} else {
					// Esimeses hoos vaatame, kas moodulite fail on ette antud k�surealt.
					db.p(args[0] + " read from command line.");
					window = new Editor(directory + args[0]);
					window.setTitle(WINDOW_TITLE);
					window.setSize(getWinWidth(), getWinHeight());
					window.setVisible(true);
				}
			} else {
				// Kui k�surealt ei olnud ette antud, v�tame vaikev��rtuse application.properties failist.
				db.p(
					"No module file name was given as the command line argument, reading the application.properties file.");
				String paletteFile = PropertyBox.getProperty(
					PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PALETTE_FILE);
				if (paletteFile != null && paletteFile.trim().length() > 0) {
					// Leidsime vastava kirje.
					db.p(
						"Found module file name " + paletteFile + " from the "
						+ PropertyBox.APP_PROPS_FILE_NAME + ".properties file.");
					window = new Editor(directory + paletteFile);
				} else {
					// application.properties failis polnud vastavat kirjet vaikimisi laetava faili kohta.
					db.p(
						"Module file name was not specified in command line nor in the application.properties file. Starting without.");
					RuntimeProperties.packageDir = directory;
					window = new Editor();
				}
				window.setTitle(WINDOW_TITLE);
				window.setSize(getWinWidth(), getWinHeight());
				window.setVisible(true);
			}
		} catch (Exception e) {
			window = new Editor();
			window.setTitle(WINDOW_TITLE);
			window.setSize(getWinWidth(), getWinHeight());
			window.setVisible(true);
		}
		// log application executions, also making sure that the properties file is
		// available for writing (required by some of current application modules).
		RuntimeProperties.genFileDir = PropertyBox.getProperty(
			PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.LAST_EXECUTED, new java.util.Date().toString());
	}

	public void clearPane() {
		tabbedPane.remove(tabbedPane.getSelectedComponent());
		if (tabbedPane.getTabCount() > 0) {
			tabbedPane.setSelectedIndex(0);
			getCurrentCanvas().drawingArea.grabFocus();
		}

	}

	public void openOptionsDialog() {
		OptionsDialog o = new OptionsDialog();
		o.setVisible(true);
		repaint();
	}

	public VPackage getCurrentPackage() {
            if( getCurrentCanvas() != null ) {
                return getCurrentCanvas().getCurrentPackage();
            }
            return null;
	}

	public Canvas getCurrentCanvas() {
		return (Canvas) tabbedPane.getSelectedComponent();

	}

	public void stateChanged(ChangeEvent e) {
		if (getCurrentCanvas() != null) {
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem) menuBar.getMenu(1).getMenuComponent(2);
			cb.setSelected(getCurrentCanvas().isGridVisible());
			getCurrentCanvas().drawingArea.grabFocus();
			RuntimeProperties.packageDir = getCurrentCanvas().workDir;
		}
	}


}

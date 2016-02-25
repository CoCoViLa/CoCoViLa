package ee.ioc.cs.vsle.classeditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;





import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import ee.ioc.cs.vsle.graphics.Line;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.classeditor.ClassImport;
import ee.ioc.cs.vsle.classeditor.ClassPropertiesDialog;
import ee.ioc.cs.vsle.iconeditor.DeleteClassDialog;
import ee.ioc.cs.vsle.packageparse.PackageXmlProcessor;
import ee.ioc.cs.vsle.synthesize.Synthesizer;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.GraphicsExporter;
import ee.ioc.cs.vsle.util.SystemUtils;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.vclass.ClassGraphics;
import ee.ioc.cs.vsle.vclass.ClassObject;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.vclass.VPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClassEditor extends JFrame implements ChangeListener {

	private static final long serialVersionUID = 1L;
	public static final String MENU_EDIT_PACKAGE = "Edit Package";	
	public static final String MENU_SAVE_CURRENT_PACKAGE = "Save to current Package";	
	
	private static final Logger logger = LoggerFactory.getLogger(ClassEditor.class);

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
	public static ClassObject classObject;

	// Package properties 
	public static String packageName;
	public static String packageDesc;    

	private static File packageFile;

	private JCheckBoxMenuItem gridCheckBox;
	private JCheckBoxMenuItem showPortCheckBox;
	private JCheckBoxMenuItem showPortOpenCheckBox;
	private JCheckBoxMenuItem showAllFields;
	//private JCheckBoxMenuItem showKnownFields;
	private JCheckBoxMenuItem showObjectNamesCheckBox;
	//private JCheckBoxMenuItem snapToGridCheckBox;

	public boolean viewFields = true;
	
	//private ClassFieldTable dbrClassFields = new ClassFieldTable();

	ArrayList<ClassObject> packageClassList = new ArrayList<ClassObject>();  
	ArrayList<String> packageClassNamesList = new ArrayList<String>();
	ArrayList<String> templateNameList = new ArrayList<String>();

	ChooseClassDialog ccd = new ChooseClassDialog( packageClassNamesList, null );
	DeleteClassDialog dcd = new DeleteClassDialog( packageClassNamesList );

	ClassImport ci;    
	ClassImport cig;
	
	private int deltaX = -1;
	private int deltaY = -1;
	

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
		
		JButton newTab = new JButton("Add new Tab");
		  
		newTab.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				   if(getCurrentPackage()!= null){
				   ClassCanvas canvas = new ClassCanvas(getCurrentPackage(), new File(getCurrentPackage().getPath()).getParent() + File.separator);
				   addCanvas(canvas);				
				   } else {

			            JFileChooser fc = new JFileChooser( ( RuntimeProperties.getLastPath() != null && new File( RuntimeProperties
			                    .getLastPath() ).exists() ) ? RuntimeProperties.getLastPath() : RuntimeProperties.getWorkingDirectory() );
			            CustomFileFilter synFilter = new CustomFileFilter( CustomFileFilter.EXT.XML );
			            fc.setFileFilter( synFilter );
			            
			            int returnVal = fc.showOpenDialog( ClassEditor.getInstance() );
			            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
			                File file = fc.getSelectedFile();
			                if ( RuntimeProperties.isLogDebugEnabled() )
			                    logger.info( "Loading package: " + file.getName() );
			                try {
			                    VPackage pkg;
			                    if ( (pkg = PackageXmlProcessor.load(file)) != null ) {
			                        RuntimeProperties.setLastPath( file.getAbsolutePath() );
			                        ClassCanvas curCanvas = getCurrentCanvas();			                      
			                        if (curCanvas != null) {
			                        	 curCanvas.setPackage(pkg);
			                        	 curCanvas.setWorkDir(file.getParent()+File.separator);
			                        	 updateWindowTitle();
			                        } else {
			                        	openNewCanvasWithPackage(file);
			                        }
			                        setPackageFile(file);
			                    }
			                } catch ( Exception exc ) {
			                    exc.printStackTrace();
			                }
			            }
				    
				   }
			}
		});
		
		menuBar.add(newTab);
		
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
		
		menuItem = new JMenuItem(MENU_SAVE_CURRENT_PACKAGE );
		menuItem.addActionListener( getActionListener() );
		menu.add( menuItem );
		
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

		/*menuItem = new JMenuItem( Menu.SELECT_PACKAGE );
		menuItem.addActionListener( getActionListener() );
		menu.add( menuItem );*/

		
		menuItem = new JMenuItem(MENU_EDIT_PACKAGE);
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
				ClassCanvas canvas = ClassEditor.getInstance().getCurrentCanvas();
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

		showPortCheckBox = new JCheckBoxMenuItem( Menu.SHOW_PORTS, true );
		showPortCheckBox.addActionListener( getActionListener() );
		menu.add( showPortCheckBox );


		showPortOpenCheckBox = new JCheckBoxMenuItem( Menu.SHOW_PORT_OPEN_CLOSE, true );
		showPortOpenCheckBox.addActionListener( getActionListener() );
		menu.add( showPortOpenCheckBox );

		showAllFields = new JCheckBoxMenuItem( Menu.SHOW_FIELDS, true );
		showAllFields.addActionListener( getActionListener() );
		menu.add( showAllFields);
		
		/*showKnownFields = new JCheckBoxMenuItem( Menu.SHOW_KNOWN, true );
		showKnownFields.addActionListener( getActionListener() );
		menu.add(showKnownFields);*/
		
		showObjectNamesCheckBox = new JCheckBoxMenuItem( Menu.SHOW_NAMES, false );
		showObjectNamesCheckBox.addActionListener( getActionListener() );

		//sync View with current canvas
		menu.getPopupMenu().addPopupMenuListener( new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
				ClassCanvas canvas;

				if( ( canvas = getCurrentCanvas() ) == null ) 
					return;

				gridCheckBox.setSelected( canvas.isGridVisible() );
			//	snapToGridCheckBox.setSelected( RuntimeProperties.getSnapToGrid() );
				showPortCheckBox.setSelected( canvas.isDrawPorts() );
				showPortOpenCheckBox.setSelected( canvas.isDrawOpenPorts() );
				showAllFields.setSelected(isViewFields());
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
					
				    RuntimeProperties.saveProperty( RuntimeProperties.OPEN_PACKAGES, getOpenPackages() );

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
		if((pkg = PackageXmlProcessor.load(f, false)) != null ) {
			RuntimeProperties.setLastPath( f.getAbsolutePath() );
			ClassCanvas canvas = new ClassCanvas( pkg, f.getParent() + File.separator );
			RuntimeProperties.addOpenPackage( pkg );
			addCanvas(canvas);
			updateWindowTitle();
		}
	} // loadPackage
	
	
	private JPanel createTabTitle(final ClassCanvas canvas, String packageName){
		int count = 0;
		//int countAllCanvas = 0;

		if(packageName == null){
			packageName = canvas.getPackage().getName();
		}
			
		for (Component canv : tabbedPane.getComponents()) {
			if (canv instanceof ClassCanvas){
				//countAllCanvas++;			
				if (((ClassCanvas) canv).getPackage() !=null && packageName.equals(((ClassCanvas) canv).getPackage().getName())) {
				count++;
				}
			}
		}

		if (count > 1) {
			packageName = packageName + " (" + (count) + ")";
			canvas.setTitle(packageName);			
		}				
			    		 	    
		 JButton tabCloseButton = new JButton(FileFuncs.getImageIcon("images/dclose.png", false));
		 tabCloseButton.setSize(15,15);
		 tabCloseButton.setPreferredSize(new Dimension(15,15));
		 tabCloseButton.setAlignmentX(RIGHT_ALIGNMENT);
		 tabCloseButton.setAlignmentY(TOP_ALIGNMENT);
		 tabCloseButton.setBorder(null);
		 tabCloseButton.setToolTipText("Close tab");

		 
		 tabCloseButton.setActionCommand("" + count);
		 
		 ActionListener al;

			 al = new ActionListener() {
			      public void actionPerformed(ActionEvent ae) {
			     //   JButton btn = (JButton) ae.getSource();		       
			        closeSchemeTab(canvas);			       
			      }
			    };			 
			    
		  tabCloseButton.addActionListener(al);
		  JPanel pnl = new JPanel();
	      pnl.setOpaque(false);
	      pnl.add(new JLabel(packageName));
	      pnl.add(tabCloseButton);
	      
	      return pnl;
		
	}

	private void addCanvas(final ClassCanvas canvas) {
		
		  canvas.setStatusBarText( "Loaded package: " + canvas.getPackage().getPath() );
		
	      tabbedPane.addTab( packageName, null, canvas, canvas.getPackage().getPath() );
		  tabbedPane.setSelectedComponent( canvas );
		  
		  JPanel pnl =  createTabTitle(canvas, null);

	      tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, pnl);

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

		final ClassEditor window = new ClassEditor();
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
					logger.debug( "Found package file name " + packageFile + " from the configuration file." );
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

	public ClassCanvas getCurrentCanvas() {
		return (ClassCanvas) tabbedPane.getSelectedComponent();

	}

	@Override
	public void stateChanged( ChangeEvent e ) {
		refreshUndoRedo();
		ClassCanvas canvas = getCurrentCanvas();
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
		 String windowTitle = WINDOW_TITLE;
		 String prevTitle = getTitle();
		 ClassCanvas canvas = getCurrentCanvas();
		 if ( canvas != null ) {
			 String packageName = canvas.getPackage().getName();
			 String schemeTitle = canvas.getSchemeTitle();
			 int idx = tabbedPane.getSelectedIndex();
			 if( schemeTitle != null ) {
				 windowTitle = schemeTitle + " - " + packageName + " - " + WINDOW_TITLE;
				 tabbedPane.setTitleAt( idx, schemeTitle );
			 } else {	
				if(prevTitle.indexOf(packageName) == -1){				
				   tabbedPane.setTabComponentAt(idx, createTabTitle(canvas, packageName));
				}
				// tabbedPane.setTitleAt( idx, packageName ); 
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
		 ClassCanvas canvas = getCurrentCanvas();
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

	 public ClassCanvas newSchemeTab(VPackage pkg, InputStream inputStream) {
		 assert SwingUtilities.isEventDispatchThread();

		 ClassCanvas c = new ClassCanvas(pkg,
				 new File(pkg.getPath()).getParent() + File.separator);
		 c.loadScheme(inputStream);
		 addCanvas(c);
		 return c;
	 }

	 public ClassCanvas newSchemeTab(VPackage pkg, String pathToScheme) {
		 ClassCanvas c;
		 try {
			 c = newSchemeTab( pkg, new FileInputStream( pathToScheme ) );
			 c.setLastScheme( pathToScheme );
			 updateWindowTitle();
		 } catch ( FileNotFoundException e ) {
			 return null;
		 }
		 return c;
	 }

	 public void closeSchemeTab( ClassCanvas canv ) {
		 if ( canv == null )
			 return;
		 if (tabbedPane.getTabCount() > 1) {			 
		 	 RuntimeProperties.removeOpenPackage(canv.getPackage().getPath());
			 canv.destroy();
		 	 tabbedPane.remove(canv);
		 	 getCurrentCanvas().drawingArea.grabFocus();
		 } else {		 
			exitApplication();
		 }
	 }

	 /**
	  * Closes the given tab
	  */
	  void closeCanvas( ClassCanvas canv ) {
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
		 /* @TODO DELETE needed? */
		  
		  /*try {
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
*/
	  }   

	  public void  loadDefaultPortGraphic(boolean openFlag, Port port) {
		  
		  ClassGraphics defaultGraphics;
		  if(openFlag){	  
			  defaultGraphics = Port.DEFAULT_OPEN_GRAPHICS;
			  } else defaultGraphics = Port.DEFAULT_CLOSED_GRAPHICS;
		  GObj targetPort = getCurrentCanvas().getObjectList().getObjectByPortId(port.getName());
		  if(targetPort == null)
			  return;
 
		 getCurrentCanvas().mListener.repaintPort(targetPort, defaultGraphics, openFlag, true);  
	  }
	 
	  
	  public void loadPortGraphicClass(boolean openFlag, Port port) {
		  File f = selectFile();
		  if ( f != null ){

			  cig = new ClassImport( f, packageClassNamesList, packageClassList, templateNameList );
			 
			  PopupCanvas popupCanvas = new PopupCanvas(getCurrentPackage(), f.getParent() + File.separator);

		   	  PortGraphicsDialog dialog = new PortGraphicsDialog( templateNameList, "Select Template", rootPane, popupCanvas, f, openFlag);

			  dialog.newJList( templateNameList );			  
			  dialog.setVisible( true );
			  dialog.repaint();

			  String selection = dialog.getSelectedValue();
			  if (logger.isDebugEnabled()) {
				logger.debug("selection {}", selection);
			  }

			  importPortGraphics(f, selection, openFlag, port.getName());

		  }
	  }  

	  public boolean savePortGraphicToXML(){
		  return true;
	  }

	  public void loadClass() {
		  File f = selectFile(); 		  
		  if ( f != null ){
			  /* load canvas, if it wasn't loaded before */
			  VPackage pkg = PackageXmlProcessor.load(f);
			  if(pkg != null){ 
				  if( ClassEditor.getInstance().getCurrentCanvas() == null ){			   
				  		ClassCanvas canvas = new ClassCanvas(pkg, f.getParent() + File.separator);
				  		addCanvas(canvas);
				  }			  
				  if (logger.isDebugEnabled()) {
		    		logger.debug("Canvas {}", ClassEditor.getInstance().getCurrentCanvas());
		      	   }
				  importClassFromPackage( f, pkg);	
			  } 		  
		  }
	  }  


	  public void importPortGraphics(File file, String selection, boolean openFlag, String portName){

		  if ( selection == null )
			  return;
		  ClassCanvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
		 // Port targetPort = curCanvas.getObjectList().getPortById(portName);
		  GObj portObject = curCanvas.getObjectList().getObjectByPortId(portName);
		  if(portObject == null)
			  return;

		  try {

			  VPackage pkg;
			  if ( (pkg = PackageXmlProcessor.load(file, false)) != null ) {
				  PackageClass pClass = pkg.getClass(selection);
				  
			//	  targetPort.set
				  curCanvas.mListener.repaintPort(portObject, pClass.getGraphics(), openFlag, false);				  
			  }

		  } catch ( Exception exc ) {
			  exc.printStackTrace();
		  }    	
		  // curCanvas.repaint();
	  }

	  public void importClassFromPackage( File file, VPackage pkg) {

		  ci = new ClassImport( file, packageClassNamesList, packageClassList, templateNameList );
		  		  
		  ClassCanvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();		  	  		  
		  PopupCanvas popupCanvas = new PopupCanvas(getCurrentPackage(), file.getParent() + File.separator);

	   	  PortGraphicsDialog dialog = new PortGraphicsDialog( packageClassNamesList, "Import Class", rootPane, popupCanvas, file, true);

		  dialog.newJList( packageClassNamesList );			  
		  dialog.setVisible( true );
		  dialog.repaint();
		  
		 /* ccd.newJList( packageClassNamesList );
		   ccd.setLocationRelativeTo( rootPane );
		   ccd.setListData(packageClassNamesList.toArray());
		   ccd.getRootPane().

		  ccd.setVisible( true );
		  ccd.repaint();*/
		  String selection = dialog.getSelectedValue();
		  
		  if(selection == null){
			  /* nothing selected - abort */
			  return;
		  }

	      if (logger.isDebugEnabled()) {
	    	logger.debug("selection {}", selection);
	      }

		 // ClassCanvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
		  
		  /* Flag to fire extra checks for classProperties and bounding box in case this is NOT the only class on Canvas */		  
		  boolean onlyClass = true;
		  if(curCanvas != null && curCanvas.getObjectList().size() > 0){		  
			  int clearOnImport = JOptionPane.showConfirmDialog( null, "Clear Working Area?" );			
			  if ( clearOnImport == JOptionPane.CANCEL_OPTION ) {
				  return;
			  }
			  if ( clearOnImport == JOptionPane.YES_OPTION ) {
				  /*   Clear pane on new import */
				  curCanvas.clearObjects();
				  onlyClass = true;
			  } else if (clearOnImport == JOptionPane.NO_OPTION){
				  onlyClass = false;
			  }
 
		  }

		  
		  /* Set offset for multiple imports */
		  int classY = 5; /* basic offset*/
		  int classX = 5;
		  if(onlyClass){
			  classY = 40; /* single class position */
			  classX = 40;
		  } else {
		      ObjectList olist = curCanvas.getObjectList();
	            for ( GObj obj : olist) {
	            	if ((obj.getX() + obj.getWidth()) > classX) {
	            		classX = obj.getX() + obj.getWidth();
	            	}	            	
	            }
		  }
		  

	      if (logger.isDebugEnabled()) {
	    	logger.debug("start import {}", classX);
	      }


		  if ( selection == null )
			  return;

		  try {
			  if ( pkg!= null ) {
				  RuntimeProperties.setLastPath( file.getAbsolutePath() );

				  ClassEditor classEditor = ClassEditor.getInstance();
				  PackageClass pClass = pkg.getClass(selection);

				  if(onlyClass && curCanvas != null){
					  curCanvas.setClassObject(new ClassObject( pClass.getName(), pClass.getDescription(),pClass.getIcon(),pClass.getComponentType()));
					  curCanvas.getClassObject().removeClassFieldsGraphics();

					  classEditor.getCurrentCanvas().setPackage(pkg);
					  classEditor.updateWindowTitle();
					  //  fields.clear();  @CheckThis!!!
				  }
				  if (pClass.getGraphics() != null && pClass.getGraphics().getShapes() != null) {
					  ClassGraphics classGraphics = pClass.getGraphics();
					  BoundingBox box = null;
					  /* don't load 2nd boundingBox if !onlyClass */
					  if(onlyClass){							
						  box = new BoundingBox( classGraphics.getBoundX(), classGraphics.getBoundY(), 
								  classGraphics.getBoundWidth(), classGraphics.getBoundHeight() );
					  }
					  if (box != null) {
						  curCanvas.mListener.addShape(box, classX, classY);							
						  // @TODO BB button on palette - check!!!!
						  // curCanvas.iconPalette.boundingbox.setEnabled( false );
					  }                	

					  ArrayList<Shape> shapes = classGraphics.getShapes();
					  for (Shape shape : shapes) {
						  if (shape instanceof Line )
							  curCanvas.mListener.addShape(shape, classX+Math.min(shape.getX(), ((Line) shape).getEndX()), classY+shape.getY());
						  else if (shape instanceof Text ){
							  curCanvas.mListener.addShape(curCanvas.mListener.setTextDimensions((Text) shape), classX, classY);
							  
						  }
						  else curCanvas.mListener.addShape(shape, classX, classY);
					  }
				  }
				  if (pClass.getPorts() != null) {
					  ArrayList<Port> ports = pClass.getPorts();
					  for (Port port : ports) {
						  curCanvas.mListener.drawPort(port, classX, classY);
					  }
				  }

				  if (onlyClass && pClass.getFields() != null) {

					 curCanvas.getClassObject().setClassFields(pClass.getFields());	

					  for (ClassField classField : curCanvas.getClassObject().fields) {
						  ClassGraphics classGraphics = classField.getKnownGraphics();
						  if (classGraphics != null) {
							  graphicsToShapes(curCanvas, classGraphics, classField.getName(), false);							  
						  }
						  classGraphics = classField.getDefaultGraphics();
						  if (classGraphics != null) {
							  graphicsToShapes(curCanvas, classGraphics, classField.getName(), true);							  
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

	  public void graphicsToShapes(ClassCanvas canvas, ClassGraphics classGraphics, String name, boolean fieldDefault){		
		  ArrayList<Shape> cShapes = classGraphics.getShapes();
		  GObj obj = new GObj(); 
		  int x =  2147483647 ;
		  int y =  2147483647 ;
		  for (Shape shape : cShapes) {
		  		shape.setField(true);
		  		shape.updateShapeAsField(shape, name, fieldDefault);
		  		x = Math.min(x, shape.getX());
		  		y = Math.min(y, shape.getY());	  	
		  }
		  for (Shape shape : cShapes) {		
			  shape.setX(shape.getX()-x);
			  shape.setY(shape.getY() - y);
		  }
			  
		  	obj.setWidth(classGraphics.getBoundWidth());
			obj.setHeight(classGraphics.getBoundHeight());
			obj.setX(x);
			obj.setY(y);
			obj.setShapes(cShapes);
			canvas.addObject(obj);
	  }	  
	  
	  public void editPackage() {
		  PackagePropertiesDialog p = new PackagePropertiesDialog(getCurrentPackage().getName(), getCurrentPackage().getDescription());
		  p.setVisible( true );
		 // packageFile;
		  if (getCurrentCanvas().getPackage() != null && packageParamsOk ) {		
			  
			  File packageFile = new File(getCurrentCanvas().getPackage().getPath());			  			  
			  String tmp = FileFuncs.getFileContents(packageFile);
			  
			  int start = tmp.indexOf("<package>\n");
			  int end = tmp.indexOf("</description>");
			  String nonSpecFilePartStart = tmp.substring(0, start);
			  String nonSpecFilePartEnd = tmp.substring(end);
			  String fileText = "<package>\n<name>" + packageName + "</name>\n";
			  fileText += "<description>" + ClassEditor.getInstance().packageDesc  ;
			  fileText = nonSpecFilePartStart + fileText + nonSpecFilePartEnd;
		   
			  
			  try {
				  FileOutputStream out = new FileOutputStream( packageFile );
				  out.write( fileText.getBytes() );
				  out.flush();
				  out.close();
				  JOptionPane.showMessageDialog( null, "Saved to: " + packageFile.getName(), "Saved",
						  JOptionPane.INFORMATION_MESSAGE );
				  
				  getCurrentPackage().setName(packageName); getCurrentPackage().setDescription(packageDesc);
				  updateWindowTitle();
			  } catch ( Exception exc ) {
				  exc.printStackTrace();
			  }
			  
		  }
	  }
	  
	  public void createPackage() {
		  PackagePropertiesDialog p = new PackagePropertiesDialog("","");
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
					  ClassCanvas curCanvas = ClassEditor.getInstance().getCurrentCanvas();
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

	  
	  public void saveShapesToPackage(){
		  saveToPackage(false);
	  }
	  public void exportShapesToPackage() {
		  saveToPackage(true);
		 // commonShapesToPackage(true);
	  }
	  
	  /**
	   *  Validate ClassProperties
		*/
	  private boolean  validateClassObject(){
		  ClassCanvas curCanvas = getCurrentCanvas();
		  ClassPropertiesDialog dialog = null;
		  if (curCanvas.getClassObject() == null) { 
			  dialog = new ClassPropertiesDialog(new ClassFieldTable(), false);
		  } else if (curCanvas.getClassObject() != null|| !curCanvas.getClassObject().validateBasicProperties()){
			  dialog = new ClassPropertiesDialog(curCanvas.getClassObject().getDbrClassFields(), false);
		  }
		  if(dialog == null || dialog.isCancelled()) return false;
		  else return true;
	  }
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

	  public void savePortGraphics(boolean export){
		  
		  File f = selectFile();
		  ClassCanvas canv = getCurrentCanvas();

		  /**
		   * Abort if null file
		   */
		  if(f == null){
			  JOptionPane.showMessageDialog( canv, "Export cancelled!" );
			  return;
		  }    	

		  ArrayList<GObj> selectedObjects = canv.getObjectList();

		  if ( selectedObjects.size() > 0 ) {

			  PackageClass pc = new PackageClass(canv.getClassObject().getClassName()); 
			  
			  ClassGraphics cg = formatShapesForSave(selectedObjects, pc);

			  ArrayList<Shape> shapes = new ArrayList<Shape>();
			  GObj holder = null;
			  
			  for ( GObj obj : selectedObjects ) {
				  
			  /* NO ports for port graphic*/
				  if(obj.getPortList() != null && obj.getPortList().size() > 0){
					  JOptionPane.showMessageDialog( canv, "Port Graphics can't contain port elements!" );
					  return;
				  }
			  
			  shapes.addAll(obj.getShapes());

			   for (Shape shape : obj.getShapes()) {

				   /**
				    *  Check that at least part of shape is inside bounds
				    * */       
				   					  
				    if(!(shape instanceof BoundingBox)){

				    	 if (!obj.isInside(canv.getBoundingBox().getX(), canv.getBoundingBox().getY(), canv.getBoundingBox().getX()+canv.getBoundingBox().getWidth(), canv.getBoundingBox().getX() + canv.getBoundingBox().getHeight())){
				    		 break;
				    	 }
				    	
				    } else {
				    	   holder = obj;							   						   
				    }
				    
				   shape.setX(obj.getX() - canv.getBoundingBox().getX());
				   shape.setY(obj.getY() - canv.getBoundingBox().getY());

				    
			       if (logger.isDebugEnabled()) {
				    	logger.debug("Port Graphics ADD SHAPE - {}", shape.toText());
				   }
				   cg.addShape(shape);
				   if(canv.getBoundingBox() != null){
					   if(canv.getBoundingBox().getHeight() != 0)
						   cg.setBoundHeight(canv.getBoundingBox().getHeight());
					   if(canv.getBoundingBox().getWidth() != 0) 
						   cg.setBoundWidth(canv.getBoundingBox().getWidth());   					   
				   }

				   
				   /* remove info text */
				   if(holder != null && holder.getShapes() != null && holder.getShapes().size() > 1 && holder.getShapes().get(1) instanceof Text){
					   holder.getShapes().remove(1);
				   }
				   
			   	}			  
			   }

			   File packageFile = f;

			   pc.setIcon(canv.getClassObject().getClassIcon() );
			   pc.setDescription(canv.getClassObject().getClassDescription());
			   pc.setComponentType(canv.getClassObject().getComponentType());
			   
			   pc.addGraphics( cg );
			   // toXML
			   new PackageXmlProcessor( packageFile ).addPackageClass( pc );			 
			   
			   /* clear */
			   clearShapesAfterSave(selectedObjects);
		  }
  
	  }
	  
	  public void saveToPackage(boolean export) {
		  
		  /* Bounding box check */
		  if (!getCurrentCanvas().isBBPresent()){
			
			  JOptionPane.showMessageDialog( null, "Please define a bounding box.", "Bounding box undefined",
					  JOptionPane.INFORMATION_MESSAGE );
			  return;
		  }


		  ClassCanvas canv = getCurrentCanvas();
		  File packageFile; 
		  if(canv.getPackage() == null && !export){
			  JOptionPane.showMessageDialog( canv, "No package!" );
			  return;
		  }
		  if(export){
			  File f = selectFile();
			  packageFile = f;
			  /**
			   * Abort if null file
			   */
			 
			  if(f == null){
				  JOptionPane.showMessageDialog( canv, "Export cancelled!" );
				  return;
			  }    	
		  } else {
			     packageFile = new File(canv.getPackage().getPath());
		  }

		  if(!validateClassObject())
			  return;

		  ArrayList<GObj> selectedObjects = canv.getObjectList();
		  
		  	  // template or class 
		  if(getCurrentCanvas().getClassObject().componentType.getXmlName().equals("template")){
			  /* NO ports for port graphic*/ 	
			  for ( GObj obj : selectedObjects ) {
				  if(obj.getPortList() != null && obj.getPortList().size() > 0){
					  JOptionPane.showMessageDialog( canv, "Port Graphics can't contain port elements!" );
					  return;
				  }
			 /* savePortGraphics(export); */
		     } 
		  }
		  if ( selectedObjects.size() > 0 ) {

			  PackageClass pc = new PackageClass(canv.getClassObject().getClassName());

			  ClassGraphics cg = formatShapesForSave(selectedObjects, pc);
			  
			  if(cg == null){ // was cancelled by user
				  clearShapesAfterSave(selectedObjects);
				  return;
			  }

			  ArrayList<ClassField> fields = new ArrayList<ClassField>();
			  
			   //System.out.println(" rx=" + rX + "; ry=" + rY);			 
			
		       if (logger.isDebugEnabled()) {
			    	logger.debug("cg SHAPES - {}", cg.getShapes());
			   }

			   pc.setIcon(canv.getClassObject().getClassIcon() );
			   pc.setDescription(canv.getClassObject().getClassDescription());
			   pc.setComponentType(canv.getClassObject().getComponentType());
			   
			   if(canv.getBoundingBox() != null){
				   if(canv.getBoundingBox().getHeight() != 0)
					   cg.setBoundHeight(canv.getBoundingBox().getHeight());
				   if(canv.getBoundingBox().getWidth() != 0) 
					   cg.setBoundWidth(canv.getBoundingBox().getWidth());   					   
			   }

			   pc.addGraphics( cg );
			   
			   ClassFieldTable dbrClassFields = canv.getClassObject().getDbrClassFields();
			   
			   for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
				   String fieldName = (String)dbrClassFields.getValueAt( i, 0 );
				   String fieldType = (String)dbrClassFields.getValueAt( i, 1 );
				   String fieldValue = (String)dbrClassFields.getValueAt( i, 2 );

				   if ( fieldType != null ) {
					   ClassField cf = new ClassField(fieldName, fieldType, fieldValue, (String)dbrClassFields.getValueAt( i, 3 ),
							   (String)dbrClassFields.getValueAt( i, 4 ), 
							   Boolean.parseBoolean(String.valueOf(dbrClassFields.getValueAt( i, 5 ))), 
							   dbrClassFields.knowns.length>=i+1?dbrClassFields.knowns[i]:null,
							   dbrClassFields.defaults.length>=i+1?dbrClassFields.defaults[i]:null);
					   fields.add(cf);
					   pc.addField( cf );
				   }
			   }

			   //1. Write to package
			   new PackageXmlProcessor( packageFile ).addPackageClass( pc );

			   String _className = canv.getClassObject().className;


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
					   
					   /*   */
					   
					   boolean specExists = false;
					   boolean classExists = false;
					   String nonSpecFilePartStart = "";
					   String nonSpecFilePartEnd = ""; 
							   
					   if (javaFile.exists()) {
						   
						   String tmp = FileFuncs.getFileContents(javaFile);
						   classExists = tmp.indexOf("class") != -1;
						   
						   int start = tmp.indexOf("/*@ spec");
						   int end = tmp.indexOf("  }@*/");
						   specExists = start != -1;
						  
						   if(specExists && classExists){  /* test that file is correctly formatted */
							   nonSpecFilePartStart = tmp.substring(0, start);
							   nonSpecFilePartEnd = tmp.substring(end);
						   }
						   
						 
					   }  
					   
					   			
					   
					   String fileText = "";			   
					   		if (!classExists) {
					   			fileText = "class " + _className + " {";					   							   		
					   			fileText += "\n    /*@ specification " + _className + " {\n";
					   			fileText += this.fieldsToFile(selectedObjects, dbrClassFields);
							    fileText += "    }@*/\n \n}";						  
					   		}
						    else { 						   							  						    	
							   fileText = nonSpecFilePartStart + "\n    /*@ specification " + _className + " {\n" + 
									   this.fieldsToFile(selectedObjects, dbrClassFields) + nonSpecFilePartEnd;
						   }    
					   FileFuncs.writeFile( javaFile, fileText );
				   }

				   JOptionPane.showMessageDialog( null, "Saved to package: " + packageFile.getName(), "Saved",
						   JOptionPane.INFORMATION_MESSAGE );
			   }			   			  

			   /* clear */
			   clearShapesAfterSave(selectedObjects);
			   
		  } else {
			  JOptionPane.showMessageDialog( canv, "Nothing to export!" );
		  }    	
	  }

	  private String fieldsToFile(ArrayList<GObj>selectedObjects, ClassFieldTable dbrClassFields){ // and ports
		  
		  String fileText = "";
			for ( int i = 0; i < dbrClassFields.getRowCount(); i++ ) {
				String fieldName = (String)dbrClassFields.getValueAt( i, 0);
				String fieldType = (String)dbrClassFields.getValueAt( i, 1 );
	    
				if ( fieldType != null) {
					fileText += "    " + fieldType + " " + fieldName + ";\n";
				}
			}	
			for (GObj obj : selectedObjects){
				if(obj.getPortList() != null){				
					for ( int i = 0; i < obj.getPortList().size(); i++ ) {
						String fieldName = obj.getPortList().get(i).getName();
						String fieldType = obj.getPortList().get(i).getType();
	    
						if ( fieldType != null) {
							fileText += "    " + fieldType + " " + fieldName + ";\n";
						}
					}	
				}
			}
			return fileText;
	  }
	  
	  /**
	   *  drop out of bounds shapes. Removed 29.09.
	   * 
	   *  
					    	int xOffset = obj.getX();
					    	int yOffset = obj.getY();
					    	if(shape.getY() < bY){
					    		shape.setY(0);                 			
					    		System.out.println("Height - " + shape.getHeight() + bY + yOffset + "=" + (shape.getHeight() - bY + yOffset));
					    		shape.setHeight(shape.getHeight() - bY + yOffset);      
					    		needConfirm = true;     
					    	} else {
					    		shape.setY(yOffset - bY);
					    	}
					    	if ((yOffset + shape.getHeight()) > (bY+bH)){
					    		needConfirm = true;     
					    		shape.setHeight(bH - shape.getY());
					    	}


					    	if(shape.getX() < bX){
					    		shape.setX(0);                 			
					    		System.out.println("Width - " + shape.getWidth() + bX + xOffset + "=" + (shape.getWidth() - bX + xOffset));
					    		shape.setWidth(shape.getWidth() - bX + obj.getX());            			       		
					    	} else {
					    		shape.setX(xOffset - bX);
					    	}
					    	if ((xOffset + shape.getWidth()) > (bX+bW)){
					    		shape.setWidth(bW - shape.getX());            			
					    	}

							Inside function
								
							if((shape.getX()+ shape.getWidth()) < activeBoundingBox.getX() || (shape.getX()) > (bX + bW)){            			
					    		System.out.println("SHAPE OUF OF Bounds X - " + shape.getX() + " => "+ bX + " - "+ (bX + bW));
					    		needConfirm = true;     
					    		break;
					    	}

					    	if((shape.getY()+ shape.getHeight()) < bY || (shape.getY()) > (bY + bH)){            			
					    		System.out.println("SHAPE OUF OF Bounds Y - " + shape.getY() + " => "+ bY + " - "+ (bY + bH));
					    		needConfirm = true;
					    		break;
					    	}       

	   */

	  /**
	   * Format shapes
	   * @param selectedObjects
	   */
	  private ClassGraphics formatShapesForSave(ArrayList<GObj>selectedObjects, PackageClass pc){
		  
		  /**
		   * Set Bounds            
		  */
		   int bX = 0; int bY = 0; int bW = 0; int bH = 0;   /*Use activeBB 02.10AM*/

		   /**
		    *  Set real bounds, including shapes partially covered by BB
		    */
		   int rX = 10000; int rY = 10000; 	  

		   for ( GObj obj : selectedObjects ) {
			   GObj holder = null;
			   for (Shape sh : obj.getShapes()) {
			       if (logger.isDebugEnabled()) {
				    	logger.debug("SHAPE - {} x={}; y={}", sh.getName(), obj.getX(), obj.getY());
				   }

				   if(obj.getY() < rY){
					   rY = obj.getY();						   
				   }
				   if(obj.getX() < rX){
					   rX = obj.getX();
				   }
				   if (sh instanceof BoundingBox) {
					   bX = obj.getX();
					   bY = obj.getY();
					   bW = sh.getWidth();
					   bH = sh.getHeight();
					   
					   /* remove info text */
					 
						   holder = obj;							   
					   
					   break;
				   }
			   }
			   /* remove info text part 2*/
			   if(holder != null && holder.getShapes() != null && holder.getShapes().size() > 1 && holder.getShapes().get(1) instanceof Text){
				   holder.getShapes().remove(1);
			   }
		   }
		   this.deltaX = rX;
		   this.deltaY = rY;
		   
		   ClassGraphics cg = new  ClassGraphics();
		   ArrayList<Shape> shapes = new ArrayList<Shape>();
		   ArrayList<Port> ports = new ArrayList<Port>();
			  
		 
		   boolean needConfirm = false;  
		   
		   for ( GObj obj : selectedObjects ) {

			   shapes.addAll(obj.getShapes());					 
			   ports.addAll(obj.getPortList());	  

			   for (Shape shape : obj.getShapes()) {
	  
				   /**
				    *  Check that at least part of shape is inside bounds
				    * */       
				   					  
				    if(!(shape instanceof BoundingBox || shape instanceof Text || shape.isField())){
				 		 if (logger.isDebugEnabled()) {
						   	logger.debug("CONFIRM test: x={} - xEnd={}", obj.getX(), (int)(obj.getX() + obj.getWidth()));
						   	logger.debug("CONFIRM test: inside ->{}", obj.isInside(bX, bY, bX+bW, bY + bH));
						 }
				    	 if (!obj.isInside(bX, bY, bX+bW, bY + bH)){  
				    		 
				    		 if(obj.getX() + obj.getWidth() > bX && obj.getX() < bX + bW 
				    				 && obj.getY() + obj.getHeight() > bY && obj.getY() < bY + bH){/* o.x2 > b.x1 && o.x1 < b.x2*/}			    		 
				    		 //else if((obj.getX() > bX && obj.getX()< bX+bW) || (obj.getY() > bY && obj.getY()< bY+bH)){}
				    		 else 	{
				    			
					    		 needConfirm = true;
				    			 break;
				    		 }
				    	 }
				    	
				    }				    
				    /**
				     * Save coords relative to BB /AM 23.10
				     */
				   if (shape instanceof Line){
					   shape.setY(obj.getY() - rY);
					   ((Line) shape).setEndY(((Line) shape).getEndY() + shape.getY() );
					   if(shape.getX() == 0){
						   shape.setX(obj.getX() - rX);						 						   
						   ((Line) shape).setEndX(((Line) shape).getEndX() + shape.getX() );						   
					   } else {
						   shape.setX(obj.getX() - rX + shape.getX());						 						   
						   ((Line) shape).setEndX(((Line) shape).getEndX() - rX + obj.getX());
					   }
				   } else if(shape.isField()){	
						  shape.setX(obj.getX() - rX + shape.getX());
						  shape.setY(obj.getY() - rY + shape.getY());
				   } else {
					   shape.setX(obj.getX() - rX);
					   shape.setY(obj.getY() - rY);
				   }
				    					    
			       if (logger.isDebugEnabled()) {
				    	logger.debug("ADD SHAPE - {}", shape.toText());
				    	logger.debug("Bounds: {} - {};{} - {}", shape.getX(), cg.getBoundX(), shape.getY(), cg.getBoundY());
				   }

				   if(!shape.isField()){
					   cg.addShape(shape);
				   } 
			   }

			   if (pc != null){
				   for (Port port : obj.getPortList()) {            		
					   port.setX(obj.getX() - rX);
					   port.setY(obj.getY() - rY);
					   pc.addPort(port);
			   	   }   
			   }
		   }
		   
		   if(needConfirm){
			   int dropShapes = JOptionPane.showConfirmDialog( null, "Some shapes are out of bounds and will not be exported" );
			   if ( dropShapes != JOptionPane.YES_OPTION ) {
				   return null;
			   }
		   }
		   return cg;
	  }
	  
	  private void clearShapesAfterSave(ArrayList<GObj>selectedObjects){
		  /* CLEAR
		    */
		   ClassGraphics cg = new ClassGraphics();
		   GObj holder = null;
		   for ( GObj obj : selectedObjects ) {
			   for ( Shape s : obj.getShapes() ) {
				   if(s instanceof Line){
					   if(s.getX() < ((Line)s).getEndX()){
						   ((Line) s).setEndX(((Line) s).getEndX() - s.getX() );
						   ((Line) s).setX(0);
					   } else {
						   ((Line) s).setX( s.getX() - ((Line) s).getEndX());
						   ((Line) s).setEndX(0);						   
					   }
					  ((Line) s).setEndY(((Line) s).getEndY() - s.getY() );					  		
					  ((Line) s).setY(0);						   
				    } else if(s instanceof BoundingBox){        				        	
					   holder = obj;
					   	s.setX(0);
						s.setY(0);
			   		} else if(s.isField()){
			   			if(obj.getShapes().size() == 1 || (deltaX == -1 && deltaY == -1)){
			   			// single shape in field obj
			   			  s.setX(0);
						  s.setY(0);
			   			} else{
			   			// multiple shapes in field obj
			   				System.out.println("Clear SHAPE - s.getX() = " + s.getX() +"; obj.getX() = "+ obj.getX() + "; deltaX = "+ deltaX);
			   				s.setX(s.getX() - obj.getX() + deltaX);
			   				s.setY(s.getY() - obj.getY() + deltaY);
			   			}
			   		}
				    else {
			        	s.setX(0);
						s.setY(0);
			        }
			   }
			   for (Port p : obj.getPortList()) {            		
				   p.setX(0);
				   p.setY(0);				   
			   }				   
		   }		
		    if(holder != null ){
		 	   holder.getShapes().add(getCurrentCanvas().drawTextForBoundingBox(holder.getWidth(), 0));
		    }
		   
	  }
	  	  	 
	public String getOpenPackages(){
		String openPackagesString = "";
		
		for (int i = 0; i < tabbedPane.getTabCount(); i++){
			tabbedPane.setSelectedIndex(i);
			ClassCanvas cc  = (ClassCanvas) tabbedPane.getSelectedComponent();
			openPackagesString += cc.getPackage().getPath() + ";";
		}
		return openPackagesString;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	public boolean isViewFields() {
		return viewFields;
	}

	public void setViewFields(boolean viewFields) {
		this.viewFields = viewFields;
		repaint();
	}

	  
	  /**
	   *   activeBoundingBox = null;

		      for (GObj obj : getCurrentCanvas().getObjectList()) {
		            for (Shape shape : obj.getShapes()) {
						if (shape instanceof BoundingBox) {
							activeBoundingBox = new BoundingBox(obj.getX(), obj.getY(), shape.getWidth(), shape.getHeight());
							counterBB++;
						}
					}
		        }
		      
		      if(counterBB>1){	
		    	  
		    	  activeBoundingBox = null;
		    	  for (GObj obj : getCurrentCanvas().getScheme().getSelectedObjects()) {
		    		  if (obj.getShapes().get(0) instanceof BoundingBox) {
		    			  activeBoundingBox = new BoundingBox(obj.getX(), obj.getY(), obj.getShapes().get(0).getWidth(), obj.getShapes().get(0).getHeight()) ;
						}
		    	  }		    	  		    	  
		      } 			  			  
			  
			  if (activeBoundingBox != null ) {
	   */

}

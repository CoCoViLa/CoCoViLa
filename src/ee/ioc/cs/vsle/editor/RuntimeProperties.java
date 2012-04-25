package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

public class RuntimeProperties {

    private final static RuntimeProperties instance = new RuntimeProperties();
    
    public final static String SCHEME_DTD = "scheme.dtd";
    public final static String PACKAGE_DTD = "package.dtd";
    public final static String TABLE_SCHEMA = "table.xsd";
    public final static String SCHEMA_LOC = "http://www.cs.ioc.ee/cocovila/dtd/";
    public final static String PACKAGE_LOCATOR = "package.locator";

    public static final String APP_PROPS_FILE_NAME = "config.xml";
    public static final String GPL_EN_SHORT_LICENSE_FILE_NAME = "resources/gpl_en_short.txt";
    public static final String GPL_EN_LICENSE_FILE_NAME = "resources/gpl_en.txt";
    public static final String GPL_EE_LICENSE_FILE_NAME = "resources/gpl_ee.txt";

    /**
     * Default minimum size for applicaion windows and dialogs
     */
    public static final Dimension WINDOW_MIN_DIM = new Dimension(150, 100);

    /**
     * Property names (keys) found in the configuration file.
     */
    private static final String DOCUMENTATION_URL = "documentation.url";
    private static final String GENERATED_FILES_DIR = "generatedFilesDirectory";
    private static final String OPEN_PACKAGES = "openPackages";
    private static final String DEBUG_INFO = "debugInfo";
    private static final String DEFAULT_LNF = "defaultLayout";
    private static final String LAST_PATH = "last.path";
    private static final String LAST_EXECUTED = "lastExecuted";
    private static final String ANTI_ALIASING = "antiAliasing";
    private static final String SHOW_GRID = "showGrid";
    private static final String SHOW_CONTROLS = "showControls";
    private static final String GRID_STEP = "gridStep";
    private static final String NUDGE_STEP = "nudgeStep";
    private static final String SNAP_TO_GRID = "snapToGrid";
    private static final String RECENT_PACKAGES = "recentPackages";
    // TODO Make sure that classpath entries do not contain semicolons
    // Also, clients of this class should not need to know how the entries are
    // represented internally.
    private static final String COMPILATION_CLASSPATH = "compilationClasspath";
    private static final String ZOOM_LEVEL = "defaultzoom";
    private static final String SYNTAX_HIGHLIGHT = "syntax_highlight";
    private static final String SHOW_ALGORITHM = "show_algorithm";
    private static final String SPEC_RECURSION_PARAMS = "spec_recursion";
    private static final String VERSION = "version";
    private static final String VERSION_UNKNOWN = "@project.version@";
    //x;y;width;height;state
    private static final String SCHEME_EDITOR_WINDOW_PROPS = "schemeEditorWindowProps";
    static final String COMPUTE_GOAL = "computeGoal";
    static final String PROPAGATE_VALUES = "propagateValues";
    private static final String DUMP_GENERATED = "dumpGeneratedFiles";
    private static final String DEFAULT_EDITOR = "defaultEditor";

    private static boolean isCleanInstall = false;
    private static boolean fromWebstart = false;
    private static String workingDirectory = System.getProperty( "user.dir" ) 
            + File.separator;

    private final Properties defaultProperties;
    private final Properties runtimeProperties;

    {

        defaultProperties = new Properties();
        defaultProperties.put( DOCUMENTATION_URL, "http://www.cs.ioc.ee/~cocovila/" );
        defaultProperties.put( GENERATED_FILES_DIR, "generated" );
        defaultProperties.put( COMPILATION_CLASSPATH, "lib/jcommon.jar;lib/jfreechart.jar" );
        defaultProperties.put( OPEN_PACKAGES, "" );
        defaultProperties.put( DEBUG_INFO, Integer.toString( 0 ) );
        defaultProperties.put( DEFAULT_LNF, "javax.swing.plaf.metal.MetalLookAndFeel" );
        defaultProperties.put( ANTI_ALIASING, Boolean.TRUE.toString() );
        defaultProperties.put( SHOW_GRID, Boolean.TRUE.toString() );
        defaultProperties.put( SHOW_CONTROLS, Boolean.FALSE.toString() );
        defaultProperties.put( GRID_STEP, Integer.toString( 15 ) );
        defaultProperties.put( NUDGE_STEP, Integer.toString( 1 ) );
        defaultProperties.put( SNAP_TO_GRID, Boolean.FALSE.toString() );
        defaultProperties.put( RECENT_PACKAGES, "" );
        defaultProperties.put( ZOOM_LEVEL, Float.toString( 1.0f ) );
        defaultProperties.put( SYNTAX_HIGHLIGHT, Boolean.TRUE.toString() );
        defaultProperties.put( SHOW_ALGORITHM, Boolean.FALSE.toString() );
        defaultProperties.put( VERSION, VERSION_UNKNOWN );
        defaultProperties.put( SCHEME_EDITOR_WINDOW_PROPS, ";;650;600;0" );
        defaultProperties.put( SPEC_RECURSION_PARAMS, "false;2" );
        defaultProperties.put( COMPUTE_GOAL, Boolean.FALSE.toString() );
        defaultProperties.put( PROPAGATE_VALUES, Boolean.FALSE.toString() );
        defaultProperties.put( DUMP_GENERATED, Boolean.TRUE.toString() );
        
        //init default fonts
        for( Fonts font : Fonts.values() ) {
            defaultProperties.put( font.getPropertyName(), font.getDefaultFont() );
        }
        
        runtimeProperties = new Properties( defaultProperties );
    }

    private String genFileDir;
    private String compilationClasspath;
    private int debugInfo;
    private int gridStep;
    private int nudgeStep;
    private boolean snapToGrid;
    private boolean showGrid;
    private boolean showControls;
    private boolean isAntialiasingOn;
    private float zoomFactor;
    private boolean isSyntaxHighlightingOn;
    private boolean showAlgorithm;
    private String lnf;
    private boolean recursiveSpecsAllowed = false;
    private int maxRecursiveDeclarationDepth = 2;
    private List<String> openPackages = new ArrayList<String>();
    private List<String> prevOpenPackages = new ArrayList<String>();
    private Map<String, String> recentPackages = new LinkedHashMap<String, String>();
    private boolean computeGoal;
    private boolean propagateValues;
    private boolean dumpGenerated;
    private Map<Fonts, Font> fonts = new Hashtable<Fonts, Font>();
    private String defaultEditor;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        instance.pcs.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        instance.pcs.removePropertyChangeListener(listener);
    }
    
    public static boolean isLogDebugEnabled() {
        return getDebugInfo() >= 1;
    }

    public static boolean isLogInfoEnabled() {
        return getDebugInfo() >= 0;
    }

    public static boolean isFromWebstart() {
        return fromWebstart;
    }

    public static void setFromWebstart() {

        RuntimeProperties.fromWebstart = true;

        workingDirectory = System.getProperty( "user.home" ) + File.separator 
                + "CoCoViLa" + File.separator;

        File file = new File( workingDirectory );

        file.mkdirs();

        System.setProperty( "user.dir", workingDirectory );
        
        instance.defaultProperties.put( GENERATED_FILES_DIR, workingDirectory + "generated" );
        instance.defaultProperties.put( COMPILATION_CLASSPATH, "" );
    }

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    public static void init() {

        readProperties( APP_PROPS_FILE_NAME, instance.runtimeProperties );

        // Initialize debug flag first because other initializations could want to use this flag
        setDebugInfo( Integer.parseInt( instance.runtimeProperties.getProperty( DEBUG_INFO ) ) );

        setGenFileDir( instance.runtimeProperties.getProperty( GENERATED_FILES_DIR ) );
        setDumpGenerated(Boolean.parseBoolean(
                instance.runtimeProperties.getProperty(DUMP_GENERATED)));
        setCompilationClasspath( instance.runtimeProperties.getProperty( COMPILATION_CLASSPATH ) );
        setLnf( instance.runtimeProperties.getProperty( DEFAULT_LNF ) );
        setGridStep( Integer.parseInt( instance.runtimeProperties.getProperty( GRID_STEP ) ) );
        setAntialiasingOn( Boolean.parseBoolean( instance.runtimeProperties.getProperty( ANTI_ALIASING ) ) );
        setSnapToGrid( Boolean.parseBoolean( instance.runtimeProperties.getProperty( SNAP_TO_GRID ) ) );
        setShowGrid( Boolean.parseBoolean( instance.runtimeProperties.getProperty( SHOW_GRID ) ) );
        setShowControls( Boolean.parseBoolean( instance.runtimeProperties.getProperty( SHOW_CONTROLS ) ) );
        setZoomFactor( Float.parseFloat( instance.runtimeProperties.getProperty( ZOOM_LEVEL ) ) );
        setNudgeStep( Integer.parseInt( instance.runtimeProperties.getProperty( NUDGE_STEP ) ) );
        setShowAlgorithm( Boolean.parseBoolean( instance.runtimeProperties.getProperty( SHOW_ALGORITHM ) ) );
        setSyntaxHighlightingOn( Boolean.parseBoolean( instance.runtimeProperties.getProperty( SYNTAX_HIGHLIGHT ) ) );
        setPropagateValues( Boolean.parseBoolean( instance.runtimeProperties.getProperty( PROPAGATE_VALUES ) ) );
        setComputeGoal( Boolean.parseBoolean( instance.runtimeProperties.getProperty( COMPUTE_GOAL ) ) );
        setDefaultEditor(instance.runtimeProperties.getProperty(DEFAULT_EDITOR));

        for( Fonts font : Fonts.values() ) {
            instance.fonts.put( font, Font.decode( instance.runtimeProperties.getProperty( font.getPropertyName() ) ) );
        }
        
        String openPacks = instance.runtimeProperties.getProperty( OPEN_PACKAGES );

        if ( openPacks != null && openPacks.trim().length() > 0 ) {
            String[] pack = openPacks.split( ";" );

            for ( int i = 0; i < pack.length; i++ ) {

                if ( pack[ i ].trim().length() == 0 )
                    continue;

                instance.prevOpenPackages.add( pack[ i ] );
            }
        }

        String recent = instance.runtimeProperties.getProperty( RECENT_PACKAGES );

        if ( recent != null ) {

            String[] packages = recent.split( ";" );

            for ( int i = 0; i < packages.length; i++ ) {

                final File f = new File( packages[ i ] );

                if ( f.exists() ) {

                    String packageName = f.getName().substring( 0, f.getName().indexOf( "." ) );

                    instance.recentPackages.put( packageName, f.getAbsolutePath() );
                }
            }
        }

        String[] specRec = instance.runtimeProperties.getProperty( SPEC_RECURSION_PARAMS ).split( ";" );
        setRecursiveSpecsAllowed( Boolean.parseBoolean( specRec[0] ) );
        setMaxRecursiveDeclarationDepth( Integer.parseInt( specRec[1] ) );

        instance.runtimeProperties.setProperty( LAST_EXECUTED, new java.util.Date().toString() );

    }

    public static void save() {

        instance.runtimeProperties.setProperty( GENERATED_FILES_DIR, instance.genFileDir );
        instance.runtimeProperties.setProperty( COMPILATION_CLASSPATH, instance.compilationClasspath );
        instance.runtimeProperties.setProperty( DEBUG_INFO, Integer.toString( instance.debugInfo ) );
        instance.runtimeProperties.setProperty( GRID_STEP, Integer.toString( instance.gridStep ) );
        instance.runtimeProperties.setProperty( NUDGE_STEP, Integer.toString( instance.nudgeStep ) );
        instance.runtimeProperties.setProperty( SNAP_TO_GRID, Boolean.toString( instance.snapToGrid ) );
        instance.runtimeProperties.setProperty( SHOW_GRID, Boolean.toString( instance.showGrid ) );
        instance.runtimeProperties.setProperty( SHOW_CONTROLS, Boolean.toString( instance.showControls ) );
        instance.runtimeProperties.setProperty( ANTI_ALIASING, Boolean.toString( instance.isAntialiasingOn ) );
        instance.runtimeProperties.setProperty( ZOOM_LEVEL, Double.toString( instance.zoomFactor ) );
        instance.runtimeProperties.setProperty( SYNTAX_HIGHLIGHT, Boolean.toString( instance.isSyntaxHighlightingOn ) );
        instance.runtimeProperties.setProperty( SHOW_ALGORITHM, Boolean.toString( instance.showAlgorithm ) );
        instance.runtimeProperties.setProperty( DEFAULT_LNF, instance.lnf );
        instance.runtimeProperties.setProperty( SPEC_RECURSION_PARAMS, Boolean.toString( instance.recursiveSpecsAllowed ) + ";" + instance.maxRecursiveDeclarationDepth );
        instance.runtimeProperties.setProperty( COMPUTE_GOAL, Boolean.toString( instance.computeGoal ) );
        instance.runtimeProperties.setProperty( PROPAGATE_VALUES, Boolean.toString( instance.propagateValues ) );
        instance.runtimeProperties.setProperty( DUMP_GENERATED, Boolean.toString(instance.dumpGenerated ));

        if (instance.defaultEditor == null) {
            instance.runtimeProperties.remove(DEFAULT_EDITOR);
        } else {
            instance.runtimeProperties.setProperty(DEFAULT_EDITOR, instance.defaultEditor);
        }

        for( Fonts font : Fonts.values() ) {
            instance.runtimeProperties.setProperty( font.getPropertyName(), encodeFont( instance.fonts.get( font ) ) );
        }
        
        String openPackagesString = "";

        for ( String open : instance.openPackages ) {
            openPackagesString += open + ";";
        }

        instance.runtimeProperties.setProperty( OPEN_PACKAGES, openPackagesString );

        String recentPackagesString = "";

        for ( String recent : instance.recentPackages.values() ) {
            recentPackagesString += recent + ";";
        }

        instance.runtimeProperties.setProperty( RECENT_PACKAGES, recentPackagesString );

        writeProperties( APP_PROPS_FILE_NAME, instance.runtimeProperties );
        
        db.p( "Configuration saved" );
    }

    /**
     * Get system documentation URL value.
     * 
     * @return String - system documentation URL.
     */
    public static String getSystemDocUrl() {
        return instance.runtimeProperties.getProperty( DOCUMENTATION_URL );
    }

    /**
     * Stores the last path used for loading or saving schema, package, etc.
     * into system properties.
     * 
     * @param path - last path used for loading or saving schema, package, etc.
     */
    public static void setLastPath( String path ) {
        if ( path != null ) {
            if ( path.indexOf( "/" ) > -1 ) {
                path = path.substring( 0, path.lastIndexOf( "/" ) );
            } else if ( path.indexOf( "\\" ) > -1 ) {
                path = path.substring( 0, path.lastIndexOf( "\\" ) );
            }
        }

        instance.runtimeProperties.setProperty( LAST_PATH, path );
    }

    /**
     * Get last file path used for loading or saving schema, package, etc. from /
     * into a file.
     * 
     * @return String - last used path from system properties.
     */
    public static String getLastPath() {
        return instance.runtimeProperties.getProperty( LAST_PATH );
    }

    /**
     * @param genFileDir the genFileDir to set
     */
    public static void setGenFileDir( String genFileDir ) {
        instance.genFileDir = genFileDir;
    }

    /**
     * @return the genFileDir
     */
    public static String getGenFileDir() {
        return instance.genFileDir;
    }

    /**
     * @param compilationClasspath the compilationClasspath to set
     */
    public static void setCompilationClasspath( String compilationClasspath ) {
        instance.compilationClasspath = (compilationClasspath == null)
            ? ""
            : compilationClasspath;
    }

    /**
     * @return the compilationClasspath
     */
    public static String getCompilationClasspath() {
        return instance.compilationClasspath;
    }

    /**
     * Returns the user set compilation classpath as array.
     * @return the compilationClasspath, can be empty or null
     */
    public static String[] getCompilationClasspaths() {
        if (instance.compilationClasspath != null) {
            return instance.compilationClasspath.split(";");
        }
        return null;
    }

    /**
     * @param debugInfo the debugInfo to set
     */
    public static void setDebugInfo( int debugInfo ) {
        instance.debugInfo = debugInfo;
    }

    /**
     * @return the debugInfo
     */
    public static int getDebugInfo() {
        return instance.debugInfo;
    }

    /**
     * @param gridStep the gridStep to set
     */
    public static void setGridStep( int gridStep ) {

        instance.gridStep = gridStep;
    }

    /**
     * @return the gridStep
     */
    public static int getGridStep() {
        return instance.gridStep;
    }

    /**
     * @param nudgeStep the nudgeStep to set
     */
    public static void setNudgeStep( int nudgeStep ) {
        instance.nudgeStep = nudgeStep;
    }

    /**
     * @return the nudgeStep
     */
    public static int getNudgeStep() {
        return instance.nudgeStep;
    }

    /**
     * @param snapToGrid the snapToGrid to set
     */
    public static void setSnapToGrid( boolean snapToGrid ) {
        instance.snapToGrid = snapToGrid;
    }

    /**
     * @return the snapToGrid
     */
    public static boolean getSnapToGrid() {
        return instance.snapToGrid;
    }

    /**
     * @param showGrid the showGrid to set
     */
    public static void setShowGrid( boolean showGrid ) {
        instance.showGrid = showGrid;
    }

    /**
     * @return the showGrid
     */
    public static boolean isShowGrid() {
        return instance.showGrid;
    }

    public static void setShowControls( boolean showControls ) {
        instance.showControls = showControls;
    }

    public static boolean isShowControls() {
        return instance.showControls;
    }
    
    /**
     * @param isAntialiasingOn the isAntialiasingOn to set
     */
    public static void setAntialiasingOn( boolean isAntialiasingOn ) {
        instance.isAntialiasingOn = isAntialiasingOn;
    }

    /**
     * @return the isAntialiasingOn
     */
    public static boolean isAntialiasingOn() {
        return instance.isAntialiasingOn;
    }

    /**
     * @param zoomFactor the zoomFactor to set
     */
    public static void setZoomFactor( float zoomFactor ) {
        instance.zoomFactor = zoomFactor;
    }

    /**
     * @return the zoomFactor
     */
    public static float getZoomFactor() {
        return instance.zoomFactor;
    }

    /**
     * @param isSyntaxHighlightingOn the isSyntaxHighlightingOn to set
     */
    public static void setSyntaxHighlightingOn( boolean isSyntaxHighlightingOn ) {
        instance.isSyntaxHighlightingOn = isSyntaxHighlightingOn;
    }

    /**
     * @return the isSyntaxHighlightingOn
     */
    public static boolean isSyntaxHighlightingOn() {
        return instance.isSyntaxHighlightingOn;
    }

    /**
     * @param showAlgorithm the showAlgorithm to set
     */
    public static void setShowAlgorithm( boolean showAlgorithm ) {
        instance.showAlgorithm = showAlgorithm;
    }

    /**
     * @return the showAlgorithm
     */
    public static boolean isShowAlgorithm() {
        return instance.showAlgorithm;
    }

    /**
     * @param openPackage the openPackage to set
     */
    public static void addOpenPackage( VPackage pkg ) {
        instance.openPackages.add( pkg.getPath() );
        instance.recentPackages.put( pkg.getName(), pkg.getPath() );
    }

    public static void removeOpenPackage( String package_ ) {
        instance.openPackages.remove( package_ );
    }

    /**
     * @return the openPackages
     */
    public static Collection<String> getPrevOpenPackages() {
        return Collections.unmodifiableCollection( instance.prevOpenPackages );
    }

    /**
     * @return the recentPackages
     */
    public static Map<String, String> getRecentPackages() {
        return Collections.unmodifiableMap( instance.recentPackages );
    }

    /**
     * @param lnf the lnf to set
     */
    public static void setLnf( String lnf ) {
        instance.lnf = lnf;
    }

    /**
     * @return the lnf
     */
    public static String getLnf() {
        return instance.lnf;
    }

    /**
     * @param font the font to set
     */
    public static void setFont( Fonts element, Font font ) {
        instance.fonts.put( element, font );
    }

    /**
     * @return the font
     */
    public static Font getFont( Fonts element ) {
        return instance.fonts.get( element );
    }

    /**
     * Returns the version string of the currently running version of the
     * application.
     * 
     * @return the version string, null if not known
     */
    public static String getApplicationVersion() {
        // The version information is added at build time by ANT so the
        // version number is missing if the application was not compiled
        // by ANT. We assume here that if the user is not running an
        // "official" release then the user knows about CVS and other means
        // to obtain that information. The version number read from the file
        // application.properties distributed with the application should
        // have the form x.y.z-dev in case it is a CVS snapshot release.
        // Real releases should have version numbers without the -dev suffix.

        return instance.runtimeProperties.getProperty( VERSION );
    }

    public static void readProperties( String propFile, Properties props ) {

        String wd = RuntimeProperties.getWorkingDirectory();

        File file = new File( wd + propFile );

        if ( file.exists() ) {

            try {

                FileInputStream in = new FileInputStream( file );

                try {
                    props.loadFromXML( in );    
                    isCleanInstall = false;
                    return;
                } finally {
                    in.close();
                }
            } catch ( Exception e ) {
                db.p( "Error reading configuration properties" );
                e.printStackTrace();
            }
        }
        
        isCleanInstall = true;
    }

    public static void writeProperties( String propFile, Properties props ) {

        String wd = RuntimeProperties.getWorkingDirectory();

        try {

            FileOutputStream out = new FileOutputStream( wd + propFile );

            try {
                props.storeToXML( out, null );
            } finally {
                out.close();
            }
        } catch ( Exception e ) {
            db.p( "Error writing configuration properties" );
            e.printStackTrace();
        }
    }

    public static String getSchemeEditorWindowProps() {
        return instance.runtimeProperties.getProperty( SCHEME_EDITOR_WINDOW_PROPS );
    }

    public static void setSchemeEditorWindowProps( Rectangle bounds, int winState ) {
        instance.runtimeProperties.setProperty( SCHEME_EDITOR_WINDOW_PROPS, 
                bounds.x + ";" + bounds.y + ";" + bounds.width + ";" + bounds.height + ";" + winState );
    }
    
    /**
     * allow recursive specifications, e.g. - 
     * class A {
     * 	int x;
     *  A a;
     *  a.x = x;
     * }
     * @return true when recursive functions are allowed, false otherwise
     */
    public static boolean isRecursiveSpecsAllowed() {
    	return instance.recursiveSpecsAllowed;
    }
    
    public static int getMaxRecursiveDeclarationDepth() {
    	return instance.maxRecursiveDeclarationDepth;
    }

	public static void setRecursiveSpecsAllowed( boolean recursiveSpecsAllowed ) {
		instance.recursiveSpecsAllowed = recursiveSpecsAllowed;
	}

	public static void setMaxRecursiveDeclarationDepth( int maxRecursiveDeclarationDepth ) {
		instance.maxRecursiveDeclarationDepth = maxRecursiveDeclarationDepth;
	}

    /**
     * @return the computeGoal
     */
    public static boolean isComputeGoal() {
        return instance.computeGoal;
    }

    /**
     * @param computeGoal the computeGoal to set
     */
    public static void setComputeGoal( boolean computeGoal ) {
        boolean old = instance.computeGoal;
        instance.computeGoal = computeGoal;
        instance.pcs.firePropertyChange( COMPUTE_GOAL, old, computeGoal );
    }

    /**
     * @return the propagateValues
     */
    public static boolean isPropagateValues() {
        return instance.propagateValues;
    }

    /**
     * @param propagateValues the propagateValues to set
     */
    public static void setPropagateValues( boolean propagateValues ) {
        boolean old = instance.propagateValues;
        instance.propagateValues = propagateValues;
        instance.pcs.firePropertyChange( PROPAGATE_VALUES, old, propagateValues );
    }

    /**
     * This runtime property shows whether the generated files (e.g. source
     * code, compiled classes) should be stored into the filesystem or kept in
     * memory by the framework.
     * @return true when generated files should be dumped into the filesystem,
     * false otherwise.
     */
    public static boolean isDumpGenerated() {
        return instance.dumpGenerated;
    }

    /**
     * Set to true to store the generated files into the filesystem, otherwise
     * the files should be kept in memory only.  When set to true the directory
     * genFileDir is created.
     * @param dumpGenerated true to dump generated files, false otherwise
     */
    public static void setDumpGenerated(boolean dumpGenerated) {
        // create the directory for generated files
        if (dumpGenerated) {
            if( FileFuncs.checkFolderAndCreate( getGenFileDir() ) && isLogDebugEnabled() ) {
                db.p("Created genFileDir " + getGenFileDir() );
            }
        }

        instance.dumpGenerated = dumpGenerated;
    }
    
    private static FontChooser fontChooser;
    
    static void openFontChooser( JFrame parent ) {
        
        if( fontChooser != null ) {
            fontChooser.toFront();
            return;
        }
        
        fontChooser = new FontChooser( parent, new Hashtable<Fonts, Font>( instance.fonts ) ) {
            
            @Override
            public void dispose() {
                fontChooser = null;
                super.dispose();
            }
        };
        
        fontChooser.setActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Map<Fonts, Font> newFonts = fontChooser.getElements();
                
                for ( final Fonts element : newFonts.keySet() ) {
                    final Font newFont = newFonts.get( element );
                    
                    if( !newFont.equals( instance.fonts.get( element ) ) ) {
                        
                        instance.fonts.put( element, newFont );
                        
                        SwingUtilities.invokeLater( new Runnable() {
                            @Override
                            public void run() {
                                FontChangeEvent.dispatchEvent( new FontChangeEvent( this, element, newFont ) );
                            }
                        } );
                    }
                }
            }
        } );
        
        fontChooser.setVisible( true );
    }
    
    static String encodeFont(Font font) {
        String fontString = font.getName() + "-";

        if (font.isBold()) {
            fontString += font.isItalic() ? "bolditalic" : "bold";
        } else {
            fontString += font.isItalic() ? "italic" : "plain";
        }

        fontString += "-" + font.getSize();

        return fontString;
    }

    public static enum Fonts {

        CODE( "text_font", "Java code and specifications", "Courier New-plain-12" ),
        OBJECTS( "object_names_font", "Object names on canvas", "Arial-plain-12" ),
        STATIC( "static_font", "\"S\" for static objects on canvas", "Arial-italic-20" ),
        ALGORITHM( "algorithm_font", "Algorithm visualizer", "Courier New-plain-12" ),
        ERRORS( "error_font", "Error window", "Courier New-plain-12" );
        
        private String propertyName;
        private String description;
        private String defaultFont;
        
        Fonts( String propertyName, String description, String defaultFont ) {
            this.propertyName = propertyName;
            this.description = description;
            this.defaultFont = defaultFont;
        }
        
        String getPropertyName() {
            return propertyName;
        }
        
        String getDescription() {
            return description;
        }
        
        String getDefaultFont() {
            return defaultFont;
        }
        
        @Override
        public String toString() {
            return getDescription();
        }
        
        static Fonts getElementByPropertyName( String name ) {
            for( Fonts element : Fonts.values() ) {
                if( element.getPropertyName().equals( name ) )
                    return element;
            }
            return null;
        }
    }

    /**
     * Returns the default editor command, or null if the editor
     * preference is not set.
     * @return the editor command, or null
     */
    public static String getDefaultEditor() {
        return instance.defaultEditor;
    }

    /**
     * Sets the default editor command. If set to null the built-in
     * editor is used. Leading and trailing spaces are removed, empty strings
     * are considered equal to null.
     * @param editorCmd the new default editor
     */
    public static void setDefaultEditor(String editorCmd) {
        if (editorCmd != null) {
            editorCmd = editorCmd.trim();
            if (editorCmd.length() < 1) {
                editorCmd = null;
            }
        }
        instance.defaultEditor = editorCmd;
    }

    /**
     * @return the isCleanInstall
     */
    public static boolean isCleanInstall() {
        return isCleanInstall;
    }
}

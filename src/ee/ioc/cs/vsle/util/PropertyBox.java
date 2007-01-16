package ee.ioc.cs.vsle.util;

import java.io.*;
import java.net.*;
import java.util.*;

import ee.ioc.cs.vsle.editor.*;

/**
 * Class for holding application properties property key values.
 */
public class PropertyBox {

	public static final String APP_PROPS_FILE_NAME = "application";
	public static final String GPL_EN_SHORT_LICENSE_FILE_NAME = "resources/gpl_en_short.txt";
	public static final String GPL_EN_LICENSE_FILE_NAME = "resources/gpl_en.txt";
	public static final String GPL_EE_LICENSE_FILE_NAME = "resources/gpl_ee.txt";

	/**
	 * Property names (keys) found in the application properties file.
	 */
	public static final String DOCUMENTATION_URL = "documentation.url";
	public static final String GENERATED_FILES_DIR = "generatedFilesDirectory";
	public static final String PALETTE_FILE = "paletteFile";
	public static final String DEBUG_INFO = "debugInfo";
	public static final String DEFAULT_LAYOUT = "defaultLayout";
	public static final String LAST_PATH = "last.path";
	public static final String LAST_EXECUTED = "lastExecuted";
	public static final String ANTI_ALIASING = "antiAliasing";
	public static final String SHOW_GRID = "showGrid";
	public static final String GRID_STEP = "gridStep";
	public static final String PACKAGE_DTD = "packageDtd";
	public static final String NUDGE_STEP = "nudgeStep";
	public static final String SNAP_TO_GRID = "snapToGrid";
    public static final String RECENT_PACKAGES = "recentPackages";
    public static final String COMPILATION_CLASSPATH = "compilationClasspath";
    public static final String ZOOM_LEVEL = "defaultzoom";
    public static final String VERSION = "version";

    private static final String VERSION_UNKNOWN = "@project.version@";

    private static final Properties s_defaultProperties = new Properties();
    
    static {
    	s_defaultProperties.put( DOCUMENTATION_URL, "http://www.cs.ioc.ee/~cocovila/" );
    	String gp = RuntimeProperties.isFromWebstart() 
	    			? RuntimeProperties.getWorkingDirectory() + "generated" 
	    			: "generated";
    	s_defaultProperties.put( GENERATED_FILES_DIR, gp );
    	s_defaultProperties.put( PALETTE_FILE, "" );
    	s_defaultProperties.put( DEBUG_INFO, "0" );
    	s_defaultProperties.put( DEFAULT_LAYOUT, "javax.swing.plaf.metal.MetalLookAndFeel" );
    	s_defaultProperties.put( ANTI_ALIASING, "1" );
    	s_defaultProperties.put( SHOW_GRID, "1" );
    	s_defaultProperties.put( GRID_STEP, "15" );
    	s_defaultProperties.put( PACKAGE_DTD, "package2.dtd" );
    	s_defaultProperties.put( NUDGE_STEP, "1" );
    	s_defaultProperties.put( SNAP_TO_GRID, "0" );
    	s_defaultProperties.put( RECENT_PACKAGES, "" );
    	String cp = RuntimeProperties.isFromWebstart() 
    			    ? "" : "lib/jcommon.jar;lib/jfreechart.jar";
    	s_defaultProperties.put( COMPILATION_CLASSPATH, cp );
    	s_defaultProperties.put(ZOOM_LEVEL, "1.0");
    }
    
	/**
	 * Store application properties.
	 * @param propFile - properties file name (without an extension .properties).
	 * @param propName - property name to be saved.
	 * @param propValue - saved property value.
	 */
	public static void setProperty(String propFile, String propName, String propValue) {
		// Read properties file.
		Properties properties = new Properties();

		String wd = RuntimeProperties.getWorkingDirectory();
		
		try {
			boolean isFromProps = false;
			
			URL url = FileFuncs.getResource( wd + propFile + ".xml", true );
			
			if( url == null ) {
				url = FileFuncs.getResource( wd + propFile + ".properties", true );
				isFromProps = true;
			}
			//System.err.println( "url: " + url );
			if( url == null || !url.getProtocol().equals( "file" ) ) {
				System.err.println( "Writing defaults" );
				properties = (Properties)s_defaultProperties.clone();
				properties.put(propName, propValue);
				properties.storeToXML(new FileOutputStream( wd + propFile + ".xml" ), null);
				return;
			}
			
			//System.err.println( "uri: " + url.getFile() + " propName: " + propName );
			if( isFromProps ) {
				properties.load(new FileInputStream( new File( url.getFile() ) ) );
			} else {
				properties.loadFromXML(new FileInputStream( new File( url.toURI() ) ) );
			}
			
			properties.put(propName, propValue);
			// Write properties file.
			//properties.store(new FileOutputStream(wd + propFile + ".properties"), null);
			new File( wd + propFile + ".properties" ).delete();
			
			properties.storeToXML(new FileOutputStream(wd + propFile + ".xml"), null);
			
		} catch (Exception e) {
			System.err.println( e.getMessage() );
			//e.printStackTrace();
		}
	} // setProperty

	/**
	 * Store property value to the default location.
	 * 
	 * @param propName property name to be saved
	 * @param propValue saved property value
	 */
	public static void setProperty(String propName, String propValue) {
		setProperty(APP_PROPS_FILE_NAME, propName, propValue);
	}
	
	/**
	 * Read application properties.
	 * @param propFile - name of the properties file (without an extension .properties).
	 * @param propName - property name to be read from the properties file.
	 * @return String - read property value.
	 */
	public static String getProperty(String propFile, String propName) {
		
		if (propFile == null || propName == null || (propName.trim().length() == 0))
			return null;
		
		String wd = RuntimeProperties.getWorkingDirectory();
		
		try {
			String fileName = propFile;
			
			Properties props;
			FileInputStream in;
			File file;
			
			{
				if (!propFile.endsWith(".xml")) {
					fileName = propFile + ".xml";
				}
				
				file = new File( wd + fileName );
				
				if( file.exists() ) {
					props = new Properties();
					in = new FileInputStream( wd + fileName );
					props.loadFromXML(in);
					in.close();
					
					String prop = props.getProperty( propName );
					
					if( prop != null ) {
						return prop;
					}
				}
			}
			//if there is no XML, try to use .properties (later delete the following block)
			{
				if (!propFile.endsWith(".properties")) {
					fileName = propFile + ".properties";
				}
				
				file = new File( wd + fileName );
				
				if( file.exists() ) {
					props = new Properties();
					in = new FileInputStream( wd + fileName );
					props.load(in);
					in.close();

					String prop = props.getProperty( propName );
					
					if( prop != null ) {
						return prop;
					}
				}
			}
			
			return s_defaultProperties.getProperty( propName );

		} catch (Exception e) {
			db.p(e);
		}
		return null;
	} // getProperty

	/**
	 * Read property value from the default source.
	 * 
	 * @param propName property name to be read from the default properties file
	 * @return the value of the property <code>propName</code>
	 */
	public static String getProperty(String propName) {
		return getProperty(APP_PROPS_FILE_NAME, propName);
	}

	/**
	 * Add or remove the subvalue <code>value</code> from 
	 * the value of the property <code>propertyName</code>.
	 * 
	 * Internally these properties are stored as a semicolon separated
	 * string. Therefore, in current implementation it is not possible
	 * to store values that contain semicolons.
	 * 
	 * @param propertyName the name of the property to be changed
	 * @param value the subvalue to add or remove
	 * @param add add the value if <code>true</code>, remove otherwise
	 */
	public static void setMultiProperty(String propertyName, String value,
			boolean add) {
		String propertyValue = getProperty(propertyName);
	
		int index = -1; // the index of not found string
		
		if (propertyValue == null)
			propertyValue = "";
		else
			index = propertyValue.indexOf(value);
		
		if (index == -1 && add) {
			propertyValue += ";" + value;
			setProperty(propertyName, propertyValue);
		} else if (index != -1 && !add) {
			// The value of index can be 0 if there is no semicolon
			// at the beginning of the propertyValue for some reason.
			// Although there should always be one, let's make sure
			// that a StringIndexOutOfBounds error will not happen.
			String prefix = "";
			if (index > 0)
				prefix = propertyValue.substring(0, index - 1);

			String suffix = propertyValue.substring(index + value.length(),
								propertyValue.length());
	
			setProperty(propertyName, prefix + suffix);
		}
	}

	/**
	 * Returns the version string of the currently running version of
	 * the application.
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

		String version = null;
		URL url = FileFuncs.getResource(APP_PROPS_FILE_NAME + ".properties",
				false);
		
		if (url != null) {
			Properties props = new Properties();
			try {
				props.load(url.openStream());
				version = (String) props.get(VERSION);
				if (VERSION_UNKNOWN.equals(version))
					version = null;
			} catch (IOException e) {
				db.p(e);
			}
		}
		return version;
	}
}

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
	public static final String GPL_EN_SHORT_LICENSE_FILE_NAME = "gpl_en_short.txt";
	public static final String GPL_EN_LICENSE_FILE_NAME = "gpl_en.txt";
	public static final String GPL_EE_LICENSE_FILE_NAME = "gpl_ee.txt";

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
	public static final String CUSTOM_LAYOUT = "customLayout";
	public static final String PACKAGE_DTD = "packageDtd";
	public static final String NUDGE_STEP = "nudgeStep";
	public static final String SNAP_TO_GRID = "snapToGrid";
    public static final String RECENT_PACKAGES = "recentPackages";
    public static final String COMPILATION_CLASSPATH = "compilationClasspath";
    
    private static final Properties s_defaultProperties = new Properties();
    
    static {
    	s_defaultProperties.put( DOCUMENTATION_URL, "http://vsledit.sourceforge.net" );
    	String gp = RuntimeProperties.isFromWebstart() 
	    			? RuntimeProperties.getWorkingDirectory() + "generated" 
	    			: "../generated";
    	s_defaultProperties.put( GENERATED_FILES_DIR, gp );
    	s_defaultProperties.put( PALETTE_FILE, "" );
    	s_defaultProperties.put( DEBUG_INFO, "0" );
    	s_defaultProperties.put( DEFAULT_LAYOUT, "Custom" );
    	s_defaultProperties.put( ANTI_ALIASING, "1" );
    	s_defaultProperties.put( SHOW_GRID, "1" );
    	s_defaultProperties.put( GRID_STEP, "15" );
    	s_defaultProperties.put( CUSTOM_LAYOUT, "com.incors.plaf.kunststoff.KunststoffLookAndFeel" );
    	s_defaultProperties.put( PACKAGE_DTD, "package2.dtd" );
    	s_defaultProperties.put( NUDGE_STEP, "1" );
    	s_defaultProperties.put( SNAP_TO_GRID, "0" );
    	s_defaultProperties.put( RECENT_PACKAGES, "" );
    	String cp = RuntimeProperties.isFromWebstart() 
    			    ? "" : ";../lib/gnujaxp.jar;../lib/jcommon.jar;../lib/jfreechart.jar";
    	s_defaultProperties.put( COMPILATION_CLASSPATH, cp );
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
				properties.loadFromXML(new FileInputStream( new File( url.getFile() ) ) );
			}
			
			properties.put(propName, propValue);
			// Write properties file.
			properties.store(new FileOutputStream(wd + propFile + ".properties"), null);
			
			properties.storeToXML(new FileOutputStream(wd + propFile + ".xml"), null);
			
		} catch (Exception e) {
			System.err.println( e.getMessage() );
			//e.printStackTrace();
		}
	} // setProperty

	/**
	 * Read application properties.
	 * @param propFile - name of the properties file (without an extension .properties).
	 * @param propName - property name to be read from the properties file.
	 * @return String - read property value.
	 */
	public static String getProperty(String propFile, String propName) {
		
		if( propFile == null || propName == null || ( propName.trim().length() == 0 ) ) { return null; }
		
		String wd = RuntimeProperties.getWorkingDirectory();
		
		try {
			String fileName = propFile;
			
			Properties props;
			FileInputStream in;
			File file;
			
			{
				if ( ( propFile != null ) && !propFile.endsWith(".xml") ) {
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
				if ( propFile != null && !propFile.endsWith(".properties") ) {
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
			e.printStackTrace();
		}
		return null;
	} // getProperty

}

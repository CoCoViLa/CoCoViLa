package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.io.*;

public class RuntimeProperties {
	
	public static final String FS =  System.getProperty( "file.separator" );
	public static final String PS =  System.getProperty( "path.separator" );
	
	private static boolean fromWebstart        = false;
	private static String workingDirectory     = System.getProperty( "user.dir" ) + FS;
	
	public final static String SCHEME_DTD      = "scheme.dtd";
	public final static String PACKAGE_DTD     = "package2.dtd";
	public final static String PACKAGE_LOCATOR = "package.locator";
	// Names of the class field table as well as the dbresult columns.
	public static String[] classDbrFields      = {"FIELD", "TYPE", "VALUE"};
	public static String[] classTblFields      = {"Field Name", "Field Type", "Field Value"};
	
	// Application properties
	public static String genFileDir;
	public static String compilationClasspath;
	public static String customLayout;
	public static int debugInfo;
	public static int gridStep;
	public static int nudgeStep;
	public static int snapToGrid;
	public static boolean isAntialiasingOn;
	public static double zoomFactor;
	public static boolean isSyntaxHighlightingOn = true;
	
	// Class properties.
	public static String className;
	public static String classDescription;
	public static String classIcon;
	public static boolean classIsRelation;
	
	// Package properties
	public static String packageName;
	public static String packageDesc;
	public static String packageDtd;
	
	public static Font font = new Font("Courier New", Font.PLAIN, 12);
	
	public static boolean isLogDebugEnabled() {
		return debugInfo >= 1;
	}
	
	public static boolean isLogInfoEnabled() {
		return debugInfo >= 0;
	}

	public static boolean isFromWebstart() {
		return fromWebstart;
	}

	public static void setFromWebstart() {
		
		RuntimeProperties.fromWebstart = true;
		
		workingDirectory = System.getProperty( "user.home" ) + FS + "CoCoViLa" + FS;
		
		File file = new File( workingDirectory );
		
		file.mkdirs();
		
		System.setProperty( "user.dir", workingDirectory );
	}

	public static String getWorkingDirectory() {
		return workingDirectory;
	}
}


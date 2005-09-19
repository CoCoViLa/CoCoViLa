package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.queryutil.DBResult;

import java.awt.*;

public class RuntimeProperties {

    public final static String SCHEME_DTD = "scheme.dtd";
    public final static String PACKAGE_DTD = "package2.dtd";
	// Names of the class field table as well as the dbresult columns.
	public static String[] classDbrFields = {"FIELD", "TYPE", "VALUE"};
	public static String[] classTblFields = {"Field Name", "Field Type", "Field Value"};

	// Application properties
	public static String packageDir = "";
	public static String genFileDir;
	public static String compilationClasspath;
	public static String customLayout;
	public static int debugInfo;
	public static int gridStep;
	public static int nudgeStep;
	public static int snapToGrid;
	public static boolean isAntialiasingOn;
	public static double zoomFactor;

	// Class properties.
	public static String className;
	public static String classDescription;
	public static String classIcon;
	public static boolean classIsRelation;

	// Package properties
	public static String packageName;
	public static String packageDesc;
	public static String packageDtd;

	// Class fields stored in a DBResult (a virtual representation of a database table).
	// The DBResult is displayed out in a table where all fields of if can be modified.
	public static DBResult dbrClassFields = new DBResult("", classDbrFields);

	public static Font font = new Font("Courier New", Font.PLAIN, 12);
        public static boolean isDebugEnabled() {
            return debugInfo == 1? true: false;
        }
}


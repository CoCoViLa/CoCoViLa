package ee.ioc.cs.vsle.util;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Properties;


/**
 * Class for holding application properties property key values.
 *
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 10.1.2004
 * Time: 15:30:18
 * To change this template use Options | File Templates.
 */
public class PropertyBox {

	public static final String APP_PROPS_FILE_NAME = "application";
	public static String APP_PROPS_FILE_PATH = "";
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

	/**
	 * Store application properties.
	 * @param propFile - properties file name (without an extension .properties).
	 * @param propName - property name to be saved.
	 * @param propValue - saved property value.
	 */
	public static void setProperty(String propFile, String propName, String propValue) {
		// Read properties file.
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(propFile + ".properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		properties.put(propName, propValue);
		// Write properties file.
		try {
			properties.store(new FileOutputStream(propFile + ".properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // setProperty

	/**
	 * Read application properties.
	 * @param propFile - name of the properties file (without an extension .properties).
	 * @param propName - property name to be read from the properties file.
	 * @return String - read property value.
	 */
	public static String getProperty(String propFile, String propName) {
		try {
			if (propFile != null && !propFile.endsWith(".properties")) {
				propFile = propFile + ".properties";
			}
			Properties props = new Properties();
			FileInputStream in = new FileInputStream(propFile);

			props.load(in);
			in.close();

			if (props != null) {
				return props.getProperty(propName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} // getProperty

}

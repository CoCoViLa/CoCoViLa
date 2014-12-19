package ee.ioc.cs.vsle.iconeditor;

import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;

import ee.ioc.cs.vsle.util.*;

public class IconEditorSplash {
	public static void main(String[] args) {
		// Read the image data and display the splash screen.

		// -------------------------------------------------
		Frame splashFrame = null;
		String splashImageUrl = "/images/iesplash.gif";
		URL imageURL = FileFuncs.getResource(splashImageUrl, false );
		if (imageURL != null) {
			splashFrame = SplashWindow.splash(Toolkit.getDefaultToolkit().createImage(imageURL));
		} else {
			System.err.println("Splash image " + splashImageUrl + " not found");
		}

		// Call the main method of the application using Reflection.
		// --------------------------------------------------------
		try {
			Class.forName("ee.ioc.cs.vsle.iconeditor.IconEditor").getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.flush();
			System.exit(10);
		}

		// Dispose the splash screen.
		// -------------------------
		if (splashFrame != null) splashFrame.dispose();
	}
}

package ee.ioc.cs.vsle.iconeditor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

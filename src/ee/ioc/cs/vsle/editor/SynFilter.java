package ee.ioc.cs.vsle.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Created by IntelliJ IDEA.
 * User: Aulo
 * Date: 14.10.2003
 * Time: 9:17:13
 * To change this template use Options | File Templates.
 */
public class SynFilter
	extends FileFilter {
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);

		if (extension != null) {
			if (extension.equals("syn")) {
				return true;
			}
			else {
				return false;
			}
		}

		return false;
	}

	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	// The description of this filter
	public String getDescription() {
		return "Java ee.ioc.cs.editor.synthesize.Synthesizer Schemes";
	}

	public static final String selection = "?selection";
}

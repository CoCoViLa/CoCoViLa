package ee.ioc.cs.vsle.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 */
public class CustomFileFilter extends FileFilter {

	private String description = "";
	private String extension = "";

	public static final String extensionSyn = "syn";
	public static final String extensionXML = "xml";
	public static final String extensionTxt = "txt";

	public static final String descriptionSyn = "Java Synthesizer Schemes (*." + extensionSyn + ")";
	public static final String descriptionXML = "Extensible Markup Language (*." + extensionXML + ")";
	public static final String descriptionTxt = "Text Documents (*." + extensionTxt + ")";

	public CustomFileFilter() {
	}

	public CustomFileFilter(String extension) {
		this.setExtension(extension);
	}

	public CustomFileFilter(String extension, String description) {
		this.setExtension(extension);
		this.setDescription(description);
	}

	public boolean accept(File f) {

		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);

		if (extension != null) {
			if (extension.equalsIgnoreCase(this.getExtension())) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	} // accept

	String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	} // getExtension

	public void setExtension(String s) {
		this.extension = s;
	} // setExtension

	public String getExtension() {
		return this.extension;
	} // getExtension

	public void setDescription(String s) {
		this.description = s;
	} // setDescription

	// The description of this filter
	public String getDescription() {
		return this.description;
	} // getDescription

}
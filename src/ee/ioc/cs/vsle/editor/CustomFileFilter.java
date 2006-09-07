package ee.ioc.cs.vsle.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ee.ioc.cs.vsle.util.FileFuncs;

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

		String extension = FileFuncs.getExtension(f);

		if (extension != null) {
		    return extension.equalsIgnoreCase(this.getExtension());
		}

		return false;
	} // accept

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
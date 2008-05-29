package ee.ioc.cs.vsle.editor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import ee.ioc.cs.vsle.util.FileFuncs;

/**
 */
public class CustomFileFilter extends FileFilter {

	private String description = "";
	private String extension = "";

	public static enum EXT {
		JAVA( "java", "Java Source Code (*.java)"), 
		SYN( "syn", "Java Synthesizer Schemes (*.syn)" ),
		XML( "xml", "Extensible Markup Language (*.xml)" ),
		TXT( "txt", "Text Documents (*.txt)" ),
		TBL( "tbl", "Expert Table (*.tbl)" );
		
		private String description = "";
		private String extension = "";
		
		EXT( String ext, String desc ) {
			extension = ext;
			description = desc;
		}

		public String getDescription() {
			return description;
		}

		public String getExtension() {
			return extension;
		}
		
		
	}
	
	public CustomFileFilter( EXT extEnum ) {
		this( extEnum.getExtension(), extEnum.getDescription() );
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
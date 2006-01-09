package ee.ioc.cs.vsle.util;

import java.io.*;

import ee.ioc.cs.vsle.editor.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 28.03.2005
 * Time: 21:45:37
 * To change this template use Options | File Templates.
 */
public class FileFuncs {
	public static String getFileContents(String fileName) {
		String fileString = new String();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String lineString = new String();

			while ((lineString = in.readLine()) != null) {
				fileString += lineString+"\n";
			}
			in.close();
		} catch (IOException ioe) {
			db.p("Couldn't open file "+ fileName);

		}

		return fileString;
	}

	public static void writeFile(String fileName, String text) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
			out.println(text);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			db.p("Couldn't write to file "+ fileName);
		}
	}

	public static void writeFile( String prog, String mainClassName, String ext, String dir ) {
        try {
        	if( !dir.endsWith( System.getProperty( "file.separator" ) ) ) {
        		dir += System.getProperty( "file.separator" );
        	}
            PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter(
            		dir + System.getProperty( "file.separator" ) +
                    mainClassName + "." + ext ) ) );

            out.println( prog );
            out.close();
        } catch ( Exception e ) {
            db.p( e );
        }
    }
}

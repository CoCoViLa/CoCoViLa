package ee.ioc.cs.vsle.util;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 28.03.2005
 * Time: 21:45:37
 * To change this template use Options | File Templates.
 */
public class FileFuncs {
	public String getFileContents(String fileName) {
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

	public void writeFile(String fileName, String text) {
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

}

package ee.ioc.cs.vsle.util;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.RuntimeProperties;

/**
 * User: Ando
 * Date: 28.03.2005
 * Time: 21:45:37
 */
public class FileFuncs {

    /**
     * Buffer size for file read and write operations.
     */
    private static final int BUFSIZE = 1024;

    /**
     * Interface for different storage types for generated data.
     * The effect of these functions is implementation dependant, for example
     * the files might be kept in memory or written to disk.
     * It could be the case that only files written using writeFile()
     * can be later successfully read.
     */
    public static interface GenStorage {
        public boolean writeFile(String fileName, byte[] data);
        public byte[] getFileContents(String fileName);
        public char[] getCharFileContents(String fileName);
    }

    /**
     * Filesystem backed GenStorage implementation.
     */
    public static class FileSystemStorage implements GenStorage {

        private File path;

        public FileSystemStorage(String defaultPath) {
            if (defaultPath == null) {
                defaultPath = RuntimeProperties.getWorkingDirectory();
            }
            path = new File(defaultPath);
        }

        public byte[] getFileContents(String fileName) {
            File file = new File(fileName);
            if (!file.isAbsolute()) {
                file = new File(path, fileName);
            }
            if (file.isFile() && file.canRead()) {
                try {
                    return getByteStreamContents(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    db.p(e);
                }
            }
            return null;
        }

        public boolean writeFile(String fileName, byte[] data) {
            File file = new File(fileName);
            if (!file.isAbsolute()) {
                file = new File(path, fileName);
            }
            return FileFuncs.writeFile(file, new String(data));
        }

        @Override
        public String toString() {
            return "FileSystemStorage(" + path + ")";
        }

        public char[] getCharFileContents(String fileName) {
            File file = new File(fileName);
            if (!file.isAbsolute()) {
                file = new File(path, fileName);
            }
            if (file.isFile() && file.canRead()) {
                try {
                    return getCharStreamContents(new FileInputStream(file));
                } catch (FileNotFoundException e) {
                    db.p(e);
                }
            }
            return null;
        }
    }

    /**
     * Memory backed GenStorage implementation.
     * This storage knows nothing about filesystems and files. All the
     * data is kept in memory as byte arrays corresponding to the specified
     * names. Only the files written using writeFile() can be later read.
     */
    public static class MemoryStorage implements GenStorage {

        private Map<String, byte[]> fileMap;

        public byte[] getFileContents(String fileName) {
            byte[] data = null;
            if (fileMap != null) {
                data = fileMap.get(fileName);
            }
            return data;
        }

        public boolean writeFile(String fileName, byte[] data) {
            if (fileMap == null) {
                fileMap = new HashMap<String, byte[]>();
            }
            fileMap.put(fileName, data);
            return true;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append("MemoryStorage (");
            s.append(this.hashCode());
            s.append("):");
            if (fileMap != null && fileMap.size() > 0) {
                s.append("\n");
                for (Map.Entry<String, byte[]> v : fileMap.entrySet()) {
                    s.append('\t');
                    s.append(v.getKey());
                    s.append(" (");
                    s.append(v.getValue().length);
                    s.append(")\n");
                }
            } else {
                s.append(" <empty>");
            }
            return s.toString();
        }

        public char[] getCharFileContents(String fileName) {
            byte[] contents = getFileContents(fileName);
            if (contents != null) {
                return new String(contents).toCharArray();
            }
            return null;
        }
    }

    public static String getFileContents(File file) {
        if (RuntimeProperties.isLogDebugEnabled()) {
            db.p("Retrieving " + file);
        }

        String fileString = new String();
        if( file != null && file.exists() && !file.isDirectory() ) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String lineString = new String();

                while ((lineString = in.readLine()) != null) {
                    fileString += lineString+"\n";
                }
                in.close();
            } catch (IOException ioe) {
                db.p("Couldn't open file "+ file.getAbsolutePath());
            }
        }
        return fileString;
    }

	/**
	 * Writes the text to the specified file. The file is created if it
	 * does not exist yet. A newline is appended to the text.
	 * @param file the output file
	 * @param text the text to be written
	 * @return true on success, false on error
	 */
	public static boolean writeFile(File file, String text) {
		boolean status = false;
		if (file != null && !file.isDirectory()) {
			PrintWriter out = null;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				out.println(text);
				out.close();
				out = null;
				status = true;
			} catch (Exception e) {
				db.p(e);
				db.p("Couldn't write to file "+ file.getAbsolutePath());
			} finally {
				if (out != null) {
					out.close();
					out = null;
				}
			}
		}
		return status;
	}

    public static void writeFile( String prog, String mainClassName, String ext, String dir, boolean append ) {
        try {
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            String path = dir + mainClassName + "." + ext;
            File file = new File( path );

            if( !append && file.exists() ) {
                file.delete();
            }

            PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter( path, append ) ) );

            out.println( prog );
            out.close();
        } catch ( Exception e ) {
            db.p( e );
        }
    }
    
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
    
    public static URL getResource( String res, boolean checkFileIfNull ) {
    	
    	URL url = Thread.currentThread().getContextClassLoader().getResource( res );
    	
    	if( url != null || !checkFileIfNull ) {
    		return url;
    	}
    	
    	File file = new File( res );
    	
    	if( file.exists() ) {
    		try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
    	}
    	
    	return null;
    }

    public static ImageIcon getImageIcon( String icon, boolean isAbsolutePath ) {
    	
    	if (!isAbsolutePath) {
    		URL url = getResource( icon, false );
    		
    		if( url != null )
    		{
    			return new ImageIcon( url );
    		}
    	}
    	
    	return new ImageIcon( icon );
    }
    
    
    /**
     * Makes sure that all file separators in path are valid for current OS.
     * If the path is already valid the same string is returned.
     * 
     * @param path a string representing a path name that possibly contains
     * path name separators of a different platform
     * @return a string where foreign path name separators are replaced with
     * the separators of current platform; or the original string if it did not
     * contain any foreign path name separators
     */
    public static String preparePathOS( String path ) {
        return File.separatorChar == '/'
                ? path.replace('\\', '/')
                : path.replace('/', '\\');
    }

    /**
     * Reads everything from a character input stream into a char array.
     * @param charStream Finite character stream
     */
    public static char[] getCharStreamContents(InputStream charStream) {
        InputStreamReader reader = new InputStreamReader(charStream);
        char[] buf = null;
        int readTotal = 0;
        int read = 0;

        try {
            buf = new char[BUFSIZE];

            while (true) {
                if (buf.length == readTotal) {
                    // a linked list of chunks could be more efficient
                    buf = Arrays.copyOf(buf, readTotal + BUFSIZE);
                }
                read = reader.read(buf, readTotal, buf.length - readTotal);
                if (read > -1) {
                    readTotal += read;
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            db.p(e);
            buf = null;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
            try {
                charStream.close();
            } catch (IOException e) {
                // ignore
            }
        }

        if (buf != null && buf.length > readTotal) {
            buf = Arrays.copyOf(buf, readTotal);
        }
        return buf;
    }
    
    /**
    * Copies files using byte streams
    * @param fileIn Input file
    * @param fileOut Output file
    */
    public static void copyImageFile(File fileIn, File fileOut) {
    	try {
    		FileInputStream in = new FileInputStream(fileIn);
    		FileOutputStream out = new FileOutputStream(fileOut);
			
			int b;
			while ((b = in.read()) != -1) {
				out.write((byte)b);
			}
			in.close();
			out.close();
			
		} catch (IOException e) {
			db.p(e);
		} 
    }

    /**
     * Reads everything from a byte input stream into a byte array.
     * @param byteStream Finite byte stream
     * @return the contents of the stream as byte array; null in case of errors
     */
    public static byte[] getByteStreamContents(InputStream byteStream) {
        byte[] buf = null;
        int readTotal = 0;
        int read = 0;

        try {
            buf = new byte[BUFSIZE];

            while (true) {
                if (buf.length == readTotal) {
                    // a linked list of chunks could be more efficient
                    buf = Arrays.copyOf(buf, readTotal + BUFSIZE);
                }
                read = byteStream.read(buf, readTotal, buf.length - readTotal);
                if (read > -1) {
                    readTotal += read;
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            db.p(e);
            buf = null;
        } finally {
            try {
                byteStream.close();
            } catch (IOException e) {
                // ignore
            }
        }

        if (buf != null && buf.length > readTotal) {
            buf = Arrays.copyOf(buf, readTotal);
        }
        return buf;
    }
}

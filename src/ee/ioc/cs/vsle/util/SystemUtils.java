package ee.ioc.cs.vsle.util;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;

public class SystemUtils {
	
	public static void unpackPackages() {

        URL u = Thread.currentThread().getContextClassLoader().getResource(
                RuntimeProperties.PACKAGE_LOCATOR );
        
        String urlString = u.toExternalForm();

        String jarExt = ".jar";

        int jarIndex = urlString.lastIndexOf( jarExt );

        if ( jarIndex < 1 ) {
            return;
        }

        try {

            JarFile jarF;
            String prefix = "jar:file:";

            if ( urlString.startsWith( prefix ) ) {
                
                jarF = new JarFile( new File( urlString.substring( prefix
                        .length(), jarIndex + jarExt.length() ) ) );
            } else {

                jarF = ( (JarURLConnection) u.openConnection() ).getJarFile();
            }

            System.err.println( "Unpacking packages from: " + jarF.getName() );

            // loop through the jar file entries and copy them to files
            for ( Enumeration<JarEntry> e = jarF.entries(); e.hasMoreElements(); ) {
                String entryName = e.nextElement().toString();
                String newFileName = RuntimeProperties.getWorkingDirectory()
                        + "packages" + File.separator + entryName;

                // ignore CVS directories
                if ( entryName.indexOf( "CVS" ) != -1
                        || entryName.indexOf( "META-INF" ) != -1 ) {
                    continue;
                }

                // create missing directories
                if ( entryName.charAt( entryName.length() - 1 ) == '/' ) {
                    File fi = new File( newFileName );
                    fi.mkdirs();
                    continue;
                }

                // if the file already exists, leave it alone
                File fi = new File( newFileName );
                if ( fi.exists() ) {
                    continue;
                }

                if ( RuntimeProperties.isLogDebugEnabled() )
                    db.p( "entryName=" + newFileName );

                // create input stream associated with the jar file entry
                InputStream source = jarF.getInputStream( jarF
                        .getEntry( entryName ) );

                // create output stream associated with the new file
                FileOutputStream target = new FileOutputStream( newFileName );
                int chunkSize = 1024;
                int bytesRead;
                byte[] ba = new byte[chunkSize];

                while ( ( bytesRead = readBlocking( source, ba, 0, chunkSize ) ) > 0 ) {
                    target.write( ba, 0, bytesRead );
                }

                // done copying -- close streams
                source.close();
                target.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }
    }
	
	/**
	 * Returns a path for a jar from a given url.
	 * 
	 * @param url
	 * @return
	 */
	public static String getJarPath( URL url )
	{
	    String urlString = url.getFile();
	    
	    int idx;
	    
	    if((idx = urlString.indexOf( "!/" )) >= 0)
	        urlString = urlString.substring( 0, idx );
	    
	    if(urlString.startsWith("file:"))
            return urlString.substring( 5 );
	    
	    return urlString;
	}
	
	private static final String parsePath( URL url )
	{

		if ( url == null )
		{
			return "";
		}

		String path = url.getPath();

		if ( path.length() <= 5 )
		{
			return "";
		}

		path = path.substring( 5, path.indexOf( "!" ) );

		return parsePath( path );
	}
	
	//Method replaces "%20" symbols by " "
	//JDK 1.4 Bug ID: 4466485
	//http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4466485
	//The expression class.getResource( String name ) gives different results in J2SDK 1.3 and J2SDK 1.4.
	//In the first case it gives C:\Documents and Settings\
	//and in the second case, /C:/Documents%20and%20Settings/
	//Starting from JDK version 1.4 use URI instead of URL.
	//URI uri = new URI("file:" + path);
	//File f = new File(uri);

	/**
	 * Method <code>parsePath</code>
	 *
	 * @param path is of <code>String</code> type
	 *
	 * @return the value of <code>String</code> type
	 */
	private static final String parsePath( String path )
	{

		if ( path == null )
		{
			return "";
		}

		while ( true )
		{
			int pos = path.indexOf( "%20" );

			if ( pos == -1 )
			{
				break;
			}
			path = path.substring( 0, pos ) + " " + path.substring( pos + 3 );
		}

		return path;
	}
	
	/**
	 * Reads exactly len bytes from the input stream
	 * into the byte array. This method reads repeatedly from the
	 * underlying stream until all the bytes are read.
	 * InputStream.read is often documented to block like this, but in actuality it
	 * does not always do so, and returns early with just a few bytes.
	 * readBlockiyng blocks until all the bytes are read,
	 * the end of the stream is detected,
	 * or an exception is thrown. You will always get as many bytes as you
	 * asked for unless you get an eof or other exception.
	 * Unlike readFully, you find out how many bytes you did get.
	 *
	 * @param b the buffer into which the data is read.
	 * @param off the start offset of the data in the array,
	 * not offset into the file!
	 * @param len the number of bytes to read.
	 * @return number of bytes actually read.
	 * @exception IOException if an I/O error occurs.
	 *
	 */
	private static final int readBlocking( InputStream in, byte b[], int off, int len )
			throws IOException
	{

		int totalBytesRead = 0;
		int bytesRead      = 0;

		while ( ( totalBytesRead < len )
				&& ( bytesRead = in.read( b, off + totalBytesRead, len - totalBytesRead ) ) >= 0 )
		{
			totalBytesRead += bytesRead;
		}

		return totalBytesRead;
	}    // end readBlocking

    /**
     * Upon platform, use OS-specific methods for opening the URL in required
     * browser.
     * 
     * @param url - URL to be opened in a browser. Capable of browsing local
     *                documentation as well if path is given with file://
     */
    public static void openInBrowser( String url, Component parent ) {
        try {
            // Check if URL is defined, otherwise there is no reason for opening
            // the browser in the first place.
            if ( url != null && url.trim().length() > 0 ) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            if (RuntimeProperties.isLogDebugEnabled()) {
                db.p(e);
            }

            StringBuilder msg = new StringBuilder();
            msg.append("A browser could not be launched for opening ");
            msg.append("the documentation web page.");

            String exMsg = e.getMessage();
            if (exMsg != null) {
                msg.append('\n');
                msg.append(exMsg);
            }

            msg.append("\nThe documentation can still be found by ");
            msg.append("browsing to the following URL:\n");
            msg.append(url);

            JOptionPane.showMessageDialog(parent,
                    msg.toString(),
                    "Error opening documentation",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

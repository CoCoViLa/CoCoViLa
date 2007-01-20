package ee.ioc.cs.vsle.util;

import java.awt.Frame;
import java.awt.Window;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import ee.ioc.cs.vsle.editor.*;

public class SystemUtils {

	private static ClassLoader cl = SystemUtils.class.getClassLoader();
	
	/**
	 * Method <code>checkDir</code>
	 * If directory s does not exist, create it along with all missing directories
	 * in the path above.
	 */
	public static void createDir( String s )
	{

		try
		{
			File file = new File( s );

			file.mkdirs();
		}
		catch ( SecurityException e )
		{

			//logCategory.logError( "Security violation. ", e );
			System.err.println( "Security violation. " );
			e.printStackTrace();
		}
	}
	
	/**
	 * Method <code>unpackConfigFiles</code>
	 *
	 *
	 */
	public static void unpackPackages()
	{

		// determine the location of the FTOconfig.jar
		URL jarUrl = cl.getResource( RuntimeProperties.PACKAGE_LOCATOR );

		System.out.println( "jarUrl=" + jarUrl );
		
		if( jarUrl == null ) return;
//		;
//		// extract path+filename of the jar file
		String jarFn = parsePath( jarUrl );
//
		if ( jarFn.length() == 0 )
		{
			return;
		}

		
		//      if ( ( jarUrl == null ) || ( jarUrl.toString().length() <= 10 ) )
		//          return;
		try
		{
			System.out.println( " jarFn: " + jarFn );
			// extract path+filename of the jar file
			//          String  jarFn = jarUrl.toString().substring( 10,
			//                              jarUrl.toString().indexOf( "!" ) );
			JarFile jarF = new JarFile( new File( jarFn ) );

			// loop through the jar file entries and copy them to files
			for (Enumeration<JarEntry> e = jarF.entries(); e.hasMoreElements(); )
			{
				String entryName   = e.nextElement().toString();
				String newFileName = RuntimeProperties.getWorkingDirectory() 
										+ "packages" + RuntimeProperties.FS + entryName;

				// ignore CVS directories
				if ( entryName.indexOf( "CVS" ) != -1 )
				{
					continue;
				}

				// ignore META-INF directory
				if ( entryName.indexOf( "META-INF" ) != -1 )
				{
					continue;
				}

				// create missing directories
				if ( entryName.charAt( entryName.length() - 1 ) == '/' )
				{
					File fi = new File( newFileName );

					fi.mkdirs();

					continue;
				}

				// if the file already exists, leave it alone
				File fi = new File( newFileName );

				if ( fi.exists() )
				{
					continue;
				}

				if( RuntimeProperties.isLogDebugEnabled() )
					db.p( "entryName=" + newFileName );

				// create input stream associated with the jar file entry
				InputStream source = jarF.getInputStream( jarF.getEntry( entryName ) );

				// create output stream associated with the new file
				FileOutputStream target    = new FileOutputStream( newFileName );
				int              chunkSize = 1024;
				int              bytesRead;
				byte[]           ba = new byte[ chunkSize ];
				
				while ( ( bytesRead = readBlocking( source, ba, 0, chunkSize ) ) > 0 )
				{
					target.write( ba, 0 /* offset in ba */, bytesRead /* bytes to write */ );
				}

				// done copying -- close streams
				source.close();
				target.close();
			}    // for
		}
		catch ( FileNotFoundException ex1 )
		{
			System.out.println( ex1 );
		}
		catch ( IOException ex2 )
		{
			System.out.println( ex2 );
		}
		catch ( SecurityException ex3 )
		{
			System.out.println( ex3 );
		} 
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
	public static final String parsePath( String path )
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
     * Attempts to find all windows the application has created.
     * Java 1.6 has Window.getWindows() which should be used instead of
     * this method.
     * @return an probably empty array of windows
     */
    public static List<Window> getAllWindows() {
        List<Window> list = new ArrayList<Window>();

        for (Window window : Frame.getFrames())
            addAllOwnedWindows(window, list);

        return list;
    }

    /**
     * Adds recursively all owned windows of the specified window to the list
     * including the specified parent window.
     * @param window parent window
     * @param list accumulator
     */
    private static void addAllOwnedWindows(Window window, List<Window> list) {
        list.add(window);
        for (Window w : window.getOwnedWindows())
            addAllOwnedWindows(w, list);
    }
}

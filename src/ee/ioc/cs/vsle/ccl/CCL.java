package ee.ioc.cs.vsle.ccl;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * When using Eclipse 3.x this import can be resolved as follows:
 * Project -> Properties -> Java Build Path -> Libraries ->
 * -> Add Variable... -> ECLIPSE_HOME -> Extend... ->
 * -> plugins/org.eclipse.jdt.core_3.x.jar (-> OK)^3
 * 
 * A stripped down version of this library consisting of the compiler
 * only is distributed within release archives which are downloadable
 * from the homepage.
 */
import org.eclipse.jdt.internal.compiler.batch.Main;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 * <p>
 * Title: ee.ioc.cs.editor.ccl.CCL - Compiling Class Loader
 * </p>
 * <p>
 * Description: A CompilingClassLoader compiles Java source on-the-fly. It
 * checks for nonexistent .class files, or .class files that are older than
 * their corresponding source code.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Ando Saabas, Pavel Grigorenko
 * @version 1.0
 */
/*
 * This thing really needs some refactoring to make it anywhere even remotely
 * nice, reusable and future proof. It is probably not worth the time right
 * now as Java 1.6 is going to have a nice javax.tools.ToolProvider interface
 * and ejc will most likely adapt it quite soon.
 */
public class CCL extends URLClassLoader {

    private String compileDir;
    private Main batchCompiler;
    
	public CCL() {
		super(createClasspath());
	}
	
	/**
	 * Creates URL array with paths (dirs and jars) required for class loading
	 * @return URL[]
	 */
	private static URL[] createClasspath() {
		
		ArrayList<URL> urls = new ArrayList<URL>();
		
		try {
			urls.add( new File( RuntimeProperties.genFileDir ).toURL() );
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		String[] paths = prepareClasspath( RuntimeProperties.compilationClasspath );
		
		for( int i = 0; i < paths.length; i++ ) {
			File file = new File(paths[i]);
			if(file.exists()) {
				try {
					urls.add( file.toURL() );
					db.p("file.toURL() " + file.toURL() );
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return urls.toArray(new URL[urls.size()]);
	}

	private static String[] prepareClasspath( String path ) {
	
		if( path.indexOf(";") != -1 ) {
			return path.split(";");
			
		} else if( path.indexOf(":") != -1 ) {
			return path.split(":");
		}
		
		return new String[]{ path };
	}
	
	private static String prepareClasspathOS( String path ) {
		String[] paths = prepareClasspath( path );
		String classpath = RuntimeProperties.PS;
		
		for( int i= 0; i < paths.length; i++ ) {
			classpath += paths[i] + RuntimeProperties.PS;
		}
		
		return classpath;
	}
	
	
	/**
	 * Another implementation which uses internal compiler.
	 * 
	 * @param javaFile
	 * @return <code>true</code> on successful compilation
	 * @throws CompileException
	 */
	public boolean compile2(String javaFile) throws CompileException {
	    if (compileDir == null)
            compileDir = RuntimeProperties.genFileDir;
        
		javaFile = compileDir + RuntimeProperties.FS + javaFile + ".java";
		
		db.p("Compiling " + javaFile + "...");
		
		File file = new File(javaFile);
		
		//Check if the file exists.
		if (!file.exists()) {
			throw new CompileException("File " + javaFile + " does not exist.");
		}
				
		StringWriter sw = new StringWriter();		
		PrintWriter pw = new PrintWriter(sw);
		
		String classpath = RuntimeProperties.genFileDir + prepareClasspathOS( RuntimeProperties.compilationClasspath );
		
		/*
		int status = com.sun.tools.javac.Main.compile( 
				new String[]{ "-classpath", classpath, javaFile }, pw );
			
		if (status != 0) {

			throw new CompileException( sw.getBuffer().toString() );

		}
		*/
		if (batchCompiler == null)
			batchCompiler = new Main(pw, pw,false);
		
		if (!batchCompiler.compile(new String[] {"-source", "1.5",
				"-target", "1.5",
				"-classpath", classpath + File.pathSeparator 
				+ System.getProperty("java.class.path"),
				javaFile }))
			throw new CompileException(sw.toString());
		
		db.p("Compilation successful!");
		
		return true;
	}
//	
//	/**
//	 * Spawns a process to compile the java source code file specified in the
//	 * 'javaFile' parameter. Return a true if the compilation worked, false
//	 * otherwise.
//	 * 
//	 * @param javaFile
//	 *            String - name of the Java file to be compiled.
//	 * @return boolean - indicates whether the compilation succeeded or not.
//	 * @throws java.io.IOException -
//	 *             Input/Output exception at reading the file.
//	 * @throws ee.ioc.cs.vsle.ccl.CompileException -
//	 *             Exception at compilation.
//	 */
//	public boolean compile(String javaFile) throws IOException,
//			CompileException {
//		javaFile = RuntimeProperties.genFileDir
//				+ System.getProperty("file.separator") + javaFile + ".java";
//		db.p("ee.ioc.cs.editor.ccl.CCL: Compiling " + javaFile + "...");
//
//		File file = new File(javaFile);
//
//		// Check if the file exists.
//		if (!file.exists()) {
//			throw new CompileException("File " + javaFile + " does not exist.");
//		}
//		
//		String execCMD = "javac -classpath "
//			+ RuntimeProperties.genFileDir
//			+ prepareClasspathOS( RuntimeProperties.compilationClasspath )
//			+ " " + javaFile;
//		
//		Process p = null;
//		
//		try {
//			p = Runtime.getRuntime().exec(execCMD);
//		} catch (IOException ex) {
//			JOptionPane.showMessageDialog(null, ex.getMessage(),
//					"Compilation error", JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
//
//		db.p(execCMD);
//
//		int ret = 1;
//
//		try {
//			// Wait for it to finish running.
//			p.waitFor();
//			ret = p.exitValue();
//
//			// check if an error has occured, in this case throw an exception
//			if (ret != 0) {
//
//				InputStream inpStream = p.getErrorStream();
//				InputStreamReader inpReader = new InputStreamReader(inpStream);
//				BufferedReader err = new BufferedReader(inpReader);
//
//				String errBuff = new String();
//				String line;
//
//				while ((line = err.readLine()) != null) {
//					errBuff += line + "\n";
//				}
//
//				throw new CompileException(errBuff);
//
//			}
//			db.p("Compilation successful!");
//			
//		} catch (InterruptedException ie) {
//			db.p(ie);
//		}
//
//		// Tell whether the compilation worked.
//		return ret == 0;
//	} // compile
//
//	/**
//	 * Perform an automatic compilation of sources as necessary when looking for
//	 * class files. If the source modification is dated/timed later than the
//	 * class files, then a recompilation will be performed. Otherwise the file
//	 * is skipped.
//	 * 
//	 * @param name
//	 *            String - name of the file.
//	 * @param resolve
//	 *            boolean
//	 * @throws java.lang.ClassNotFoundException
//	 * @return Class - a compiled class.
//	 */
//	public Class loadClass(String name, boolean resolve)
//			throws ClassNotFoundException {
//
//		Class c;
//
//		// First, see if we've already dealt with this one
//		c = findLoadedClass(name);
//
//		String fileStub = name.replace('.', '/');
//		
//		String classFilename = RuntimeProperties.genFileDir
//				+ System.getProperty("file.separator") + fileStub + ".class";
//
//		System.err.println( "name " + name + " classFilename " + classFilename );
//		// File classFile = new File( classFilename );
//
//		// First, see if we want to try compiling. We do if (a) there
//		// is source code, and either (b0) there is no object code,
//		// or (b1) there is object code, but it's older than the source
//
//		/*
//		 * if (javaFile.exists() && (!classFile.exists() ||
//		 * javaFile.lastModified() > classFile.lastModified())) { try { // Try
//		 * to compile it. If this doesn't work, then // we must declare failure.
//		 * (It's not good enough to use // and already-existing, but
//		 * out-of-date, classfile) if (!compile( javaFilename ) ||
//		 * !classFile.exists()) { throw new ClassNotFoundException( "Compile
//		 * failed: "+javaFilename ); } } catch( IOException ie ) { // Another
//		 * place where we might come to if we fail to compile. throw new
//		 * ClassNotFoundException( ie.toString() ); } }
//		 */
//
//		// Let's try to load up the raw bytes, assuming they were
//		// properly compiled, or didn't need to be compiled.
//		try {
//
//			// read the bytes
//			byte raw[] = getBytes(classFilename);
//
//			// try to turn them into a class
//			c = defineClass(name, raw, 0, raw.length);
//
//		} catch (IOException ie) { // This is not a failure! If we reach here,
//									// it might
//			// mean that we are dealing with a class in a library,
//			// such as java.lang.Object }
//			// ee.ioc.cs.editor.util.db.p( "defineClass: "+clas );
//		}
//
//		// Maybe the class is in a library -- try loading the normal way.
//		if (c == null) {
//			c = findSystemClass(name);
//		}
//
//		// Resolve the class, if any, but only if the "resolve" flag is set to
//		// true.
//		if (resolve && c != null) {
//			resolveClass(c);
//		}
//
//		// If we still don't have a class, it's an error.
//		if (c == null) {
//			throw new ClassNotFoundException(name);
//		}
//
//		// Otherwise, return the class.
//		return c;
//	} // loadClass
//	
//	/**
//	 * Given a file name, reads the entirety of that file from disk and return
//	 * it as a byte array.
//	 * 
//	 * @param filename
//	 *            String - name of the file to be read.
//	 * @return byte[] - byte stream of the file read.
//	 * @throws java.io.IOException -
//	 *             Input/Output Exception thrown at reading the file.
//	 */
//	private byte[] getBytes(String filename) throws IOException {
//
//		File file = new File(filename);
//
//		// Find out the length of the file.
//		long len = file.length();
//
//		// Create an array that's just the right size for the file's contents.
//		byte raw[] = new byte[(int) len];
//
//		// Open the file for reading.
//		FileInputStream fin = new FileInputStream(file);
//
//		// Read all of it into the array; if we don't get all, then it's an
//		// error.
//		int r = fin.read(raw);
//
//		if (r != len) {
//			throw new IOException("Can't read all, " + r + " != " + len);
//		}
//
//		// Close the file.
//		fin.close();
//
//		return raw;
//	} // getBytes

    public String getCompileDir() {
        return compileDir;
    }

    public void setCompileDir(String compileDir) {
        this.compileDir = compileDir;
    }

    public void addURL(URL url) {
        super.addURL(url);
    }
}

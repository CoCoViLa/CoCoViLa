package ee.ioc.cs.vsle.ccl;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Title: ee.ioc.cs.editor.ccl.CCL - Compiling Class Loader
 * </p>
 * <p>
 * Description: A CompilingClassLoader compiles Java source on-the-fly.
 */
public abstract class CCL extends URLClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(CCL.class);

    public static final String 
            PROGRAM_CONTEXT = "ee.ioc.cs.vsle.api.ProgramContext";

    // May be used by subclasses
    protected INameEnvironment environment;

    private Map<String, Class<?>> classCache;
    private Map<String, ClassFile> resultAccumulator;
    private ArrayList<CompilationResult> problems;

    private Compiler compiler;
    private boolean hasErrors;

    protected CCL(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    protected void putCachedClass(String name, Class<?> c) {
        if (classCache == null) {
            classCache = new HashMap<String, Class<?>>();
        }
        classCache.put(name, c);
    }

    protected Class<?> getCachedClass(String name) {
        Class<?> c = null;
        if (classCache != null) {
            c = classCache.get(name);
        }

        // the class could be compiled but not yet define()d
        if (c == null && resultAccumulator != null) {
            ClassFile cf = resultAccumulator.get(name);
            if (cf != null) {
                c = defineClass(name, cf);
                resultAccumulator.remove(cf);
            }
        }
        return c;
    }

    protected void addProblem(CompilationResult result) {
        if (problems == null) {
            problems = new ArrayList<CompilationResult>();
        }
        problems.add(result);
        if (result.hasErrors()) {
            hasErrors = true;
        }
    }

    public void clearProblems() {
        if (problems != null) {
            problems.clear();
            hasErrors = false;
        }
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void writeProblemLog(Appendable out) throws IOException {
        out.append("----------\n");
        if (problems != null) {
            int errorCount = 0;
            for (CompilationResult r : problems) {
                for (CategorizedProblem p : r.getAllProblems()) {
                    out.append(Integer.toString(++errorCount));
                    out.append(". ");
                    if (p.isError()) {
                        out.append("ERROR in ");
                    } else if (p.isWarning()) {
                        out.append("WARNING in ");
                    } else {
                        out.append("Problem in ");
                    }
                    out.append(new String(p.getOriginatingFileName()));

                    String errorReportSource = 
                        ((DefaultProblem) p).errorReportSource(
                                r.compilationUnit.getContents());

                    out.append(errorReportSource);
                    out.append("\n");
                    out.append(p.getMessage());
                    out.append("\n----------\n");
                }
            }
        }
    }

    protected void accumulateResult(ClassFile classFile) {
        if (resultAccumulator == null) {
            resultAccumulator = new HashMap<String, ClassFile>();
        }

        String className = new String(
                CharOperation.concatWith(classFile.getCompoundName(), '.'));

        resultAccumulator.put(className, classFile);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // Search from our URLs
            Class<?> c = super.findClass(name);
            if (c != null) {
                return c;
            }
        } catch (Exception e) {
            // Class was not found in our URLClassPath
        }

        // When this method is called it is already known the class was
        // not found elsewhere or the class should be defined by this
        // classloader.  Therefore, the only reasonable thing to to
        // is to try to find the source and then compile it.

        try {
            return compile(name, (char[]) null);
        } catch (CompileException e) {
            logger.error(null, e);
        }

        // Failure
        throw new ClassNotFoundException();
    }


    protected Class<?> compile(String className, char[] source) {
        if (source == null) {
            source = findSource(className);
        }

        if (source != null) {
            if (compiler == null) {
                compiler = new Compiler(
                        getNameEnvironment(),
                        getErrorHandlingPolicy(),
                        getCompilerOptions(),
                        getCompileRequestor(),
                        getProblemFactory());
            }

            ICompilationUnit[] units = new CompilationUnit[1];
            units[0] = new CompilationUnit(source, className, null);
            compiler.compile(units);
            if (hasErrors()) {
                StringBuilder sb = new StringBuilder();
                try {
                    writeProblemLog(sb);
                } catch (IOException e) {
                    logger.error(null, e);
                } finally {
                    clearProblems();
                }
                throw new CompileException(sb.toString());
            }
        }
        return getCachedClass(className);
    }

    protected Class<?> defineClass(String name, ClassFile classFile) {
        byte bs[] = classFile.getBytes();
        Class<?> c = defineClass(name, bs, 0, bs.length);
        putCachedClass(name, c);
        return c;
    }

    protected char[] findSource(String className) {
        char[] data = null;

        InputStream is = getResourceAsStream(classToSourceResource(className));

        if (is != null) {
            data = FileFuncs.getCharStreamContents(is);
        }
        return data;
    }

    protected IProblemFactory getProblemFactory() {
        return new DefaultProblemFactory();
    }

    protected ICompilerRequestor getCompileRequestor() {

        return new ICompilerRequestor() {
            public void acceptResult(CompilationResult result) {
                if (result.hasErrors()) {
                    addProblem(result);
                } else {
                    for (ClassFile f : result.getClassFiles()) {
                        accumulateResult(f);
                    }
                }
            }
        };
    }

    protected CompilerOptions getCompilerOptions() {
        Map<String, String> settings = new HashMap<String, String>();

        settings.put(CompilerOptions.OPTION_Compliance,
                CompilerOptions.VERSION_1_7);
        settings.put(CompilerOptions.OPTION_Source,
                CompilerOptions.VERSION_1_7);
        settings.put(CompilerOptions.OPTION_TargetPlatform,
                CompilerOptions.VERSION_1_7);

        return new CompilerOptions(settings);
    }

    protected IErrorHandlingPolicy getErrorHandlingPolicy() {
        return new IErrorHandlingPolicy() {

            @Override
            public boolean proceedOnErrors() {
                return true;
            }

            @Override
            public boolean stopOnFirstError() {
                return false;
            }

            @Override
            public boolean ignoreAllErrors() {
                return false;
            }
        };
    }

    protected String[] getCompilerClassPath() {
        String[] userLibs = RuntimeProperties.getCompilationClasspaths();

        String[] libNames = getJavaClassPath();

        File[] sysLibs = getSystemLibs();

        int req = 0;

        if (userLibs != null) {
            req += userLibs.length;
        }
        if (libNames != null) {
            req += libNames.length;
        }
        if (sysLibs != null) {
            req += sysLibs.length;
        }

        String[] cp = new String[req];

        int j = 0;
        for (int i = 0; userLibs != null && i < userLibs.length; i++) {
            try {
                cp[j++] = new File(userLibs[i]).getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; libNames != null && i < libNames.length; i++) {
            cp[j++] = libNames[i];
        }
        for (int i = 0; sysLibs != null && i < sysLibs.length; i++) {
            try {
                cp[j++] = sysLibs[i].getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cp;
    }

    protected INameEnvironment getNameEnvironment() {
        if (environment == null) {
            environment = new FileSystem(
                    getCompilerClassPath(), new String[] { }, null);
        }
        return environment;
    }

    /**
     * Returns Java class path as string array.
     * @return Java class path, may be null or empty
     */
    public static String[] getJavaClassPath() {
        // The entries should always be separated by File.pathSeparator as
        // we get the string from the system. 
        String jcp = System.getProperty("java.class.path");
        if (jcp != null) {
            return jcp.split(File.pathSeparator);
        }
        return null;
    }

    public static File[] getSystemLibs() {
        File javaHome = new File(System.getProperty("java.home"));
        File libDir = new File(javaHome, "lib");
        File extDir = new File(libDir, "ext");
        File osxDir = new File(javaHome.getParentFile(), "Classes");

        File[][] libsArray = new File[][] {
            getLibraryFiles(libDir),
            getLibraryFiles(osxDir),
            getLibraryFiles(extDir),
        };

        int nLibs = 0;
        for (int i = 0; i < libsArray.length; i++) {
            if (libsArray[i] != null) {
                nLibs += libsArray[i].length;
            }
        }

        File[] libs = new File[nLibs];
        int destPos = 0;
        for (int i = 0; i < libsArray.length; i++) {
            if (libsArray[i] != null) {
                System.arraycopy(libsArray[i], 0, libs, destPos,
                        libsArray[i].length);
                destPos += libsArray[i].length;
            }
        }
        return libs;
    }

    public static File[] getLibraryFiles(File searchDir) {
        return searchDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
        });
    }

    // Convenience methods for manipulating class names consistently

    public static String classToFile(String className) {
        return className.replace('.', File.separatorChar);
    }

    public static String classToSrcFile(String className) {
        return classToFile(className).concat(".java");
    }

    /**
     * Translates a class name into a Java class resource name.
     * Example: org.example.Main -&gt; org/example/Main.class
     * @param className class name
     * @return class resource name
     */
    public static String classToClassResource(String className) {
        return className.replace('.', '/').concat(".class");
    }

    /**
     * Translates a class name into a Java source resource name.
     * Example: org.example.Main -&gt; org/example/Main.java
     * @param className class name
     * @return source resource name
     */
    public static String classToSourceResource(String className) {
        return className.replace('.', '/').concat(".java");
    }

    public static String toClassName(char[][] packageName, char[] typeName) {
        return new String(CharOperation.concatWith(packageName, typeName, '.'));
    }

    public static String toClassName(char[][] compoundName) {
        return new String(CharOperation.concatWith(compoundName, '.'));
    }
}

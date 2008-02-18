package ee.ioc.cs.vsle.ccl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.db;

/**
 * Package classloader.  Loads classes from the package directory and
 * zip and jar archives in the top level package directory.  Source files
 * are compiled on demand when needed.
 */
public class PackageClassLoader extends CCL implements INameEnvironment {

    public PackageClassLoader(File pkgDir) {
        super(createPackageClassPath(pkgDir),
                PackageClassLoader.class.getClassLoader());

        // initialize the environment field inherited from CCL
        initNameEnvironment();
    }

    private void initNameEnvironment() {
        ArrayList<String> fileNames = new ArrayList<String>();
        for (URL u : getURLs()) {
            try {
                fileNames.add(new File(u.toURI()).getAbsolutePath());
            } catch (URISyntaxException e) {
                db.p(e);
            }
        }

        for (String s : getCompilerClassPath()) {
            if (!fileNames.contains(s)) {
                fileNames.add(s);
            }
        }
        environment = new FileSystem(
                fileNames.toArray(new String[fileNames.size()]),
                new String[] { }, null);
    }

    /**
     * Creates a URL array with paths required for package class loading.
     * The returned array contains the URLs of the package directory,
     * all the jar and zip archives found in the package top level directory
     * (the directory is NOT searched recursively) and the compilation
     * classpath set by the user.
     * @return the package classpath
     */
    private static URL[] createPackageClassPath(File packagePath) {
        ArrayList<URL> urls = new ArrayList<URL>();

        // (1) CoCoVila standard libraries
        // CoCoViLa standard libraries should be accessible using the parent
        // classloader, therefore we omit them here.

        // (2) The package directory
        try {
            urls.add(packagePath.toURI().toURL());
        } catch (MalformedURLException e) {
            db.p(e);
        }

        // (3) jar and zip archives from the package top level directory
        File[] pkgLibs = getLibraryFiles(packagePath);
        if (pkgLibs != null) {
            for (File f : pkgLibs) {
                try {
                    urls.add(f.toURI().toURL());
                } catch (MalformedURLException e) {
                    db.p(e);
                }
            }
        }

        // (4) user set classpath
        String[] paths = RuntimeProperties.getCompilationClasspaths();

        for (String path : paths) {
            File file = new File(path);
            if(file.exists()) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    db.p(e);
                }
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

    
    @Override
    protected INameEnvironment getNameEnvironment() {
        return this;
    }

    public void cleanup() {
        if (environment != null) {
            environment.cleanup();
            environment = null;
        }
    }

    public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        NameEnvironmentAnswer rv = environment.findType(compoundTypeName);
        if (rv == null) {
            rv = findSourceAnswer(toClassName(compoundTypeName));
        }
        return rv;
    }

    private NameEnvironmentAnswer findSourceAnswer(String className) {
        String fileName = classToSourceResource(className);
        InputStream is = getResourceAsStream(fileName);
        if (is != null) {
            char[] source = FileFuncs.getCharStreamContents(is);
            if (source != null) {
                return new NameEnvironmentAnswer(
                        new CompilationUnit(source, fileName, null), null);
            }
        }
        return null;
    }

    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        NameEnvironmentAnswer rv = environment.findType(typeName, packageName);
        if (rv == null) {
            rv = findSourceAnswer(toClassName(packageName, typeName)); 
        }
        return rv;
    }

    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        return environment.isPackage(parentPackageName, packageName);
    }
}

package ee.ioc.cs.vsle.ccl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import org.eclipse.jdt.internal.compiler.batch.*;
import org.eclipse.jdt.internal.compiler.env.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.util.FileFuncs.*;

/**
 * ClassLoader for ProgramRunner.
 * This classloader compiles classes generated from package metaclasses
 * on the fly and defines the ProgramContext class for a ProgramRunner instance.
 * Work is delegated to the parent PackageClassLoader whenever possible (except
 * in case of ProgramContext and generated classes).
 */
public class RunnerClassLoader extends CCL {

    private GenStorage storage;
    private PackageClassLoader parent;

    public RunnerClassLoader(GenStorage storage, PackageClassLoader parent) {
        super(new URL[] { }, parent);
        this.storage = storage;
        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> rv = getCachedClass(className);
        if (rv != null) {
            return rv;
        }

        // Make sure the class ProgramContext gets define()d by the right
        // classloader as the static fields must have different values 
        // for different programs.  By default loadClass() asks the parent
        // fist.  This is why overriding findClass() is not enough.
        // The bytes for ProgramContext should be accessible through
        // the system classloader as there should already be a compiled
        // version of this class present in the classpath and we should ignore
        // other (source) versions there might exist.
        if (PROGRAM_CONTEXT.equals(className)) {
            
            String classFile = classToClassFile(className);
            
            InputStream is = getSystemResourceAsStream( classFile );

            if( is == null ) {
                is = getSystemResourceAsStream( classFile.replace( '\\', '/' ) );
            }
            
            if (is != null) {
                byte[] classData = FileFuncs.getByteStreamContents(is);
                if (classData != null) {
                    rv = defineClass(PROGRAM_CONTEXT, classData, 0,
                            classData.length);

                    if (rv != null) {
                        putCachedClass(className, rv);
                        return rv;
                    }
                }
            }
            throw new ClassNotFoundException("ProgramContext not found!");
        }

        // Try to find generated source code and, in case it is found, to
        // compile it before asking the parent.  Otherwise metaclasses
        // in the package directory would get compiled instead of the
        // generated classes.
        if (storage != null) {
            char[] source = storage.getCharFileContents(
                    classToSrcFile(className));

            if (source != null) {
                rv = compile(className, source);
            }
        }

        // If everything failed then delegate to the parent
        return rv != null ? rv : parent.loadClass(className);
    }

    
    @Override
    protected char[] findSource(String className) {
        if (storage != null) {
            return storage.getCharFileContents(classToSrcFile(className));
        }
        return null;
    }

    @Override
    protected INameEnvironment getNameEnvironment() {
        if (environment == null) {
            environment = new GeneratedNameEnvironment(storage,
                    (parent == null ? null : parent.getNameEnvironment()));
        }
        return environment;
    }

    @Override
    public void writeProblemLog(Appendable out) throws IOException {
        if (parent.hasErrors()) {
            out.append("Problems reported from PackageClassLoader (");
            out.append(new Date().toString());
            out.append("):\n");
            parent.writeProblemLog(out);
        }
        if (super.hasErrors()) {
            out.append("Problems reported from RunnerClassLoader (");
            out.append(new Date().toString());
            out.append("):\n");
            super.writeProblemLog(out);
        }
    }

    @Override
    public boolean hasErrors() {
        return parent.hasErrors() || super.hasErrors();
    }

    @Override
    public void clearProblems() {
        parent.clearProblems();
        super.clearProblems();
    }
}


/**
 * This implementation tries to locate resources from its temporary storage
 * that contains the source files generated from metaclasses.  Failed requests
 * are delegated to the parent environment.
 */
class GeneratedNameEnvironment implements INameEnvironment {

    private GenStorage storage;
    private INameEnvironment parentEnv;

    public GeneratedNameEnvironment(GenStorage storage,
            INameEnvironment parentEnv) {
        this.storage = storage;
        this.parentEnv = parentEnv;
    }

    public NameEnvironmentAnswer findType(char[] typeName,
            char[][] packageName) {
        String className = CCL.toClassName(packageName, typeName);
        NameEnvironmentAnswer rv  = findType(className, typeName);
        if (rv == null && parentEnv != null) {
            rv = parentEnv.findType(typeName, packageName);
        }
        return rv;
    }

    public NameEnvironmentAnswer findType(char[][] compoundName) {
        NameEnvironmentAnswer rv = findType(CCL.toClassName(compoundName),
                compoundName[compoundName.length - 1]);

        if (rv == null && parentEnv != null) {
            rv = parentEnv.findType(compoundName);
        }

        return rv;
    }

    /**
     * Returns a compilation unit when the source of the specified class
     * can be 
     * @param qualifiedName the full binary name of the class
     * @param typeName the type name of the class
     * @return a compilation unit or null when no source is found
     */
    private NameEnvironmentAnswer findType(String qualifiedName,
            char[] typeName) {

        String fileName = CCL.classToSrcFile(qualifiedName);
        char[] src = storage.getCharFileContents(fileName);
        if (src != null) {
            return new NameEnvironmentAnswer(
                    new CompilationUnit(src, fileName, null), null);
        }
        return null;
    }


    public boolean isPackage(char[][] compoundName, char[] packageName) {
        // Generated files are in the default pakcage.  When this
        // changes a new check has to be added here.
        return (parentEnv == null 
            ? false 
            : parentEnv.isPackage(compoundName, packageName));
    }

    public void cleanup() {
        storage = null;
        if (parentEnv != null) {
            parentEnv.cleanup();
            parentEnv = null;
        }
    }
}

package ee.ioc.cs.vsle.api;

/**
 * Package interface.
 * It can be accessed by generated programs via Scheme interface
 */
public interface Package {

    /**
     * @return absolute path to package.xml
     */
    public abstract String getPath();

    /**
     * @return the name of a package
     */
    public abstract String getName();

}
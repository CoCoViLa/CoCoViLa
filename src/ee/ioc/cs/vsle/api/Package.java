package ee.ioc.cs.vsle.api;

/**
 *
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
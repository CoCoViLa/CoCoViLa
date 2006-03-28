package ee.ioc.cs.vsle.vclass;

/**
 * Public interface for Scheme Daemons. All daemons will get
 * a separate thread for doing their work.
 * 
 * @author andrex
 */
public interface ISchemeDaemon extends Runnable {

    /**
     * Sets the scheme. This method is guaranteed to be called before
     * the thread is started.
     * 
     * @param scheme The scheme
     */
    public void setScheme(Scheme scheme);

    /**
     * Implementations must take care that the daemons run() method
     * will return very shortly after this method is called. 
     */
    public void stop();
}

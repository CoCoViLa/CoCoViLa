package ee.ioc.cs.vsle.api;

/**
 * The interface of connections between scheme objects that is exposed
 * to the generated programs through the {@code ProgramContext} class.
 * @see ee.ioc.cs.vsle.api.ProgramContext
 * @see ee.ioc.cs.vsle.api.Scheme
 */
public interface Connection {
    public Port getBeginPort();
    public Port getEndPort();
}

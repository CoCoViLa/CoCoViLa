package ee.ioc.cs.vsle.api;

import java.util.List;

/**
 * The interface of ports of scheme objects that is exposed to the
 * generated programs through the {@code ProgramContext} class.
 * @see ee.ioc.cs.vsle.api.ProgramContext
 * @see ee.ioc.cs.vsle.api.Scheme
 */
public interface Port {

    /**
     * Returns the name of the port.
     * @return name of the port
     */
    public String getName();

    /**
     * Returns the type of the port.
     * @return the type of the port
     */
    public String getType();

    /**
     * Returns the list of connections connected to this port.
     * @return the list of connections
     */
    public List<Connection> getConnections();

    /**
     * Returns the object the port belongs to.
     * @return the object the port belongs to
     */
    public SchemeObject getObject();
}

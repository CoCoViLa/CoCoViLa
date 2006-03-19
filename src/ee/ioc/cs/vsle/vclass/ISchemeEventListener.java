package ee.ioc.cs.vsle.vclass;

/**
 * The listener interface for receiving notifications of
 * scheme events.
 *  
 * @author andrex
 */
public interface ISchemeEventListener {
    public void schemeLoaded(SchemeEvent evt);
    public void schemeClosed(SchemeEvent evt);
    public void objectCreated(SchemeEvent evt);
    public void objectDeleted(SchemeEvent evt);
    public void objectClicked(SchemeEvent evt);
}

package ee.ioc.cs.vsle.vclass;

import javax.swing.JPanel;

public class SchemeEvent {

    /**
     * The object that was involved in current event, if any.
     */
    private GObj object;

    /**
     * The scheme
     */
    private Scheme scheme;

    private JPanel window;
    
    /**
     * @return Returns the window.
     */
    public JPanel getWindow() {
        return window;
    }

    /**
     * @param window The window to set.
     */
    public void setWindow(JPanel window) {
        this.window = window;
    }

    /**
     * @return Returns the object.
     */
    public GObj getObject() {
        return object;
    }

    /**
     * @param object The object to set.
     */
    public void setObject(GObj object) {
        this.object = object;
    }

    /**
     * @return Returns the scheme.
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * @param scheme The scheme to set.
     */
    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }
    
}

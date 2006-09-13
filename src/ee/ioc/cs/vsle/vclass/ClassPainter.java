package ee.ioc.cs.vsle.vclass;

import java.awt.Graphics2D;

/**
 * The abstract class <code>ClassPainter</code> provides an extension point
 * to the Scheme Editor where every visual class can customize its default
 * appearance and create additional visual artifacts on the scheme. 
 */
public abstract class ClassPainter implements Cloneable {

    /**
     * Reference to the instance of a visual class this <code>ClassPainter</code>
     * is responsible for.
     */
    protected GObj vclass;
    
    /**
     * The scheme description.
     */
    protected Scheme scheme;
    
    /**
     * This method is called every time the scheme is repainted after
     * drawing all the visual classes and connections.
     * It is possible to draw almost anything on the scheme without
     * any restrictions. However, long computations should be avoided
     * or the user interface will become unresponsive.
     * 
     * @param graphics the graphics object in which the whole scheme is painted
     * @param scale the factor by which the scheme view is currently scaled
     */
    public abstract void paint(Graphics2D graphics, float scale);
    
    /**
     * Sets the instance of a visual class this paitner is associated to.
     * This method is guaranteed to be called with a non-<code>null</code> argument
     * before the first <code>paint()</code> call. Note that the <code>vclass</code>
     * should not be modified here.
     * 
     * @param vclass the instance of a visual class the painter is responsible for
     */
    public void setClass(final GObj vclass) {
        this.vclass = vclass;
    }
    
    /**
     * Sets the scheme description before the first call to <code>paint()</code>
     * method. Note that it is probably not a good idea to modify the scheme directly.
     * 
     * @param scheme the scheme description
     */
    public void setScheme(final Scheme scheme) {
        this.scheme = scheme;
    }
    
    /**
     * Used internally for creating new instances from a prototype.
     */
    public ClassPainter clone() {
        ClassPainter clone = null;
        try {
            clone = (ClassPainter) super.clone();
        } catch (CloneNotSupportedException e) {
            // Object does support clone()
        }
        return clone;
    }
}

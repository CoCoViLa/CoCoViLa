package ee.ioc.cs.vsle.vclass;

import java.awt.Graphics2D;

/**
 * <p>The abstract class {@code ClassPainter} provides an extension point
 * to the Scheme Editor where every visual class can customize its default
 * appearance and create additional visual artifacts on the scheme.</p>
 * <p>Two steps are necessary to make use of this extension. First,
 * the package creator has to implement the painter's code in a Java
 * class that extends the abstract class {@code ClassPainter}.
 * A definition of the abstract method {@link #paint(Graphics2D, float) paint}
 * is needed. The simplest possible example which draws the name of the
 * visual class at the coordinates of the object's top left corner is:<br />
 * <pre>
 * import java.awt.Graphics2D;
 * import ee.ioc.cs.vsle.vclass.ClassPainter;
 * 
 * public class MyClassPainter extends ClassPainter {
 *     public void paint(Graphics2D graphics, float scale) {
 *         graphics.drawString("My name is " + vclass.getName(), 
 *                 vclass.getX(), vclass.getY());
 *     }
 * }
 * </pre>
 * </p>
 *
 * <p>Second, the name of the painter class has to be declared in the package
 * description XML file. For example, a relevant part of the package description might
 * look as follows:
 * <pre>
 * ...
 * &lt;class type="class"&gt;
 * &lt;name&gt;MyClass&lt;/name&gt;
 * ...
 * &lt;graphics&gt;
 * <i>&lt;painter&gt;MyClassPainter&lt;/painter&gt;</i>
 * &lt;/graphics&gt; 
 * ...
 * &lt;/class&gt;
 * ...
 * </pre>
 * In the above example the line in italics specifies that there exists a Java source code
 * file {@code MyClassPainter.java} in the package directory containing a class
 * with the {@code ClassPainter} interface. The source will be automatically (re)compiled
 * when the package is loaded. For each visual class of type {@code MyClass} on the scheme
 * a new instance of {@code MyClassPainter} is created.</p>
 * <p>Please note that although the {@code ClassPainter} has a reference to the
 * scheme description and could modify it directly, the scheme description including the
 * visual class {@code vclass} itself should be considered read-only at present.
 * Unexpected things will happen if this rule is ignored. In future versions there
 * will be another interface for modifying the scheme in a thread safe way.</p>
 * 
 * @see GObj
 * @see Scheme
 */
public abstract class ClassPainter implements Cloneable {

    /**
     * Reference to the instance of a visual class this {@code ClassPainter}
     * is responsible for.
     */
    protected GObj vclass;
    
    /**
     * The scheme description.
     */
    protected Scheme scheme;
    
    /**
     * This method is called every time the scheme is being repainted.
     * It is possible to draw almost anything on the scheme without
     * any restrictions. However, long computations should be avoided
     * or the user interface will become unresponsive.
     *
     * To paint visual class's original graphics,
     * call vclass.drawClassGraphics( graphics, scale );
     * 
     * @param graphics the graphics object in which the whole scheme is painted
     * @param scale the factor by which the scheme view is currently scaled
     */
    public abstract void paint(Graphics2D graphics, float scale);
    
    /**
     * Sets the instance of a visual class this painter is associated to.
     * This method is guaranteed to be called with a non-{@code null} argument
     * before the first {@code paint} call. The referenced {@code vclass}
     * should not be modified here.
     * 
     * @param vclass the instance of a visual class the painter is responsible for
     */
    public void setVClass(final GObj vclass) {
        this.vclass = vclass;
    }
    
    /**
     * Sets the scheme description before the first call to {@code paint}
     * method. It is probably not a good idea to modify the scheme directly.
     * 
     * @param scheme the scheme description
     */
    public void setScheme(final Scheme scheme) {
        this.scheme = scheme;
    }
    
    /**
     * Used internally for creating new instances from a prototype.
     */
    @Override
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

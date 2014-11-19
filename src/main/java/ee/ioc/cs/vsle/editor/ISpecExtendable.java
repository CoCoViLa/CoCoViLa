/**
 * 
 */
package ee.ioc.cs.vsle.editor;

/**
 * @author pavelg
 *
 * Schemes and objects may have extended specifications
 */
public interface ISpecExtendable {

    /**
     * Returns a text of extended specification
     * @return 
     */
    public String getSpecText();
    
    /**
     * Sets the text of extended specification
     * @param spec
     */
    public void setSpecText(String spec);
    
    
    /**
     * A title of scheme/object needed for GUI
     * @return
     */
    public String getTitle();
}

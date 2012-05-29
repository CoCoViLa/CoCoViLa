package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 * 
 * Interface used by ProgramRunner
 */
public interface ISchemeContainer {

    /**
     * Returns a loaded package
     * 
     * @return
     */
    public VPackage getPackage();
    
    /**
     * Registers runner id
     * 
     * @param id
     */
    public void registerRunner( long id );
    
    /**
     * Unregisters runner id
     * 
     * @param id
     */
    public void unregisterRunner( long id );
    
    /**
     * Returns working folder of a package
     * 
     * @return
     */
    String getWorkDir();
    
    /**
     * Returns the reference of the internal object list
     * 
     * @return object list
     */
    ObjectList getObjectList();

    /**
     * Returns a scheme
     * 
     * @return
     */
    Scheme getScheme();
    
    String getSchemeName();
    
    /**
     * Calls repaint
     */
    public void repaint();
}

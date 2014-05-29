/**
 * 
 */
package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class AliasLength extends ClassField {
    
    private Alias alias;
    //class AnnotatedClass is not serializable
    private String annotatedClassName;
    
    public AliasLength(Alias alias, String annotatedClassName) {
        super(alias.getName() + "_LENGTH", TypeUtil.TYPE_INT);
        
        this.annotatedClassName = annotatedClassName;
        this.alias = alias;
    }

    @Override
    public boolean isAliasLength() {
        return true;
    }

    /**
     * @return the alias
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * @return the ac
     */
    public String getAnnotatedClass() {
        return annotatedClassName;
    }

}

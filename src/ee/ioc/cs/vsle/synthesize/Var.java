package ee.ioc.cs.vsle.synthesize;

import java.io.Serializable;
import java.util.*;

import ee.ioc.cs.vsle.vclass.ClassField;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.Var</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author  Ando Saabas
 * @version 1.0
 */
public class Var implements //Cloneable,
        Serializable {

    private Set<Rel> rels = new HashSet<Rel>();
    //the following is for alias only!!!
    private List<Var> vars = new ArrayList<Var>();
    private ClassField field;
    private String object;
    private int varNumber;

    private Var() {

    	varNumber = RelType.varCounter++;
    }
    
    public Var( ClassField cf, String object ) {
    	
    	this();
        
        setField( cf );
        
        this.object = object;
    }


    Set<Rel> getRels() {
        return rels;
    }

    public String getType() {
        return field.getType();
    }
    
    public List<Var> getChildVars() {
    	if( !field.isAlias() ) {
			throw new IllegalStateException( "getChildVars(): Var " + getName() + " is not alias, but " + getType() );
		}
    	return vars;
    }
    
    public void addVar( Var var ) {
    	if( !field.isAlias() ) {
			throw new IllegalStateException( "addVar(): Var " + getName() + " is not alias, but " + getType() );
		}
    	vars.add( var );
    }
    /**
     * <UNCOMMENTED>
     * @param field ClassField
     */
    void setField( ClassField field ) {
        this.field = field;
    } // setField

    public ClassField getField() {
        return field;
    }

    /**
     * <UNCOMMENTED>
     * @param rel Rel
     */
    void addRel( Rel rel ) {
        rels.add( rel );
    } // addRel

    /**
     * <UNCOMMENTED>
     * @return String
     */
    public String getName() {
        return field.getName();
    } // getName

    /**
     * <UNCOMMENTED>
     * @return String
     */
    public String getObject() {
        return object;
    } // getObj

    /**
     * <UNCOMMENTED>
     * @return String
     public
     */
    public String toString() {
        return ( object + "." + field.getName() ).substring( 5 );
    } // toString

    public boolean equals( Object e ) {
        return this.varNumber == ( ( Var ) e ).varNumber;
    }

    public int hashCode() {
        return RelType.VAR_HASH + varNumber;
    }
}

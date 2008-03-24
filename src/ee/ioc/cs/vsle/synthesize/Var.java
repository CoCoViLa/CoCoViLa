package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_THIS;

import java.io.Serializable;
import java.util.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

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

    private Set<Rel> rels = new LinkedHashSet<Rel>();
    //the following is for alias only!!!
    private List<Var> vars = new ArrayList<Var>();
    private ClassField field;
    private int varNumber;
    private Var parent;
    
    private Var() {

    	varNumber = RelType.varCounter++;
    }
    
    public Var( ClassField cf, Var parent ) {
    	
    	this();
        
        setField( cf );
        
        this.parent = parent;
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
    
    public void swapAliasDeclaration( Alias alias ) {
        if( !field.isAlias() || ((Alias)field).isInitialized() || !alias.isInitialized() ) {
            throw new IllegalStateException( "Only uninitialized alias declaration can be swapped with corresponding initialized alias!" );
        }
        field = alias;
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

    public Var getParent() {
    	return parent;
    }
    
    /**
     * <UNCOMMENTED>
     * @return String
     */
    public String getObject() {
    	if( parent != null ) {
    		String obj = parent.getObject();
    		return ( ( obj != null ) ? obj + "." : "" ) + parent.getName();
    	} 
    	return null;
    } // getObj

    public String getDeclaration() {
    	return TypeUtil.getDeclaration( field, "" );
    }
    
    public String getFullName() {
    	String obj = getObject();
    	return ( ( ( obj != null ) ? ( obj + "." ).substring( 5 ) : "" ) + field.getName() );
    }
    
    public String getFullNameForConcat() {
    	String s = getFullName();
        if ( TYPE_THIS.equals( s ) ) {
            return "";
        } else if ( s.startsWith( TYPE_THIS ) ) {
            return s.substring(5) + ".";
        } else {
            return s + ".";
        }
    }
    
    /**
     * PLEASE USE THIS ONLY FOR DEBUGGING!!!
     * @return String
     */
    public String toString() {
    	String obj = getObject();
    	return ( ( ( obj != null ) ? ( obj + "." ) : "" ) + field.getName() );
    } // toString

    public boolean equals( Object e ) {
        return this.varNumber == ( ( Var ) e ).varNumber;
    }

    public int hashCode() {
        return RelType.VAR_HASH + varNumber;
    }
}

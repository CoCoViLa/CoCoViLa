package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.util.TypeUtil.*;

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
public class Var {

    private Set<Rel> rels = new LinkedHashSet<Rel>();
    //the following is for alias only!!!
    private List<Var> vars;
    private ClassField field;
    private int varNumber;
    private Var parent;
    private String parentObject;
    private String fullName;
    private String fullNameForConcat;
    private int hashcode;
    
    public Var( ClassField cf, Var parent ) {
    	
    	this.varNumber = RelType.varCounter++;
        this.field = cf;
        this.parent = parent;

        if(field.isAlias())
            vars = new ArrayList<Var>();
        
        if( parent != null ) {
            String obj = parent.getObject();
            parentObject = ((obj != null) ? obj + "." : "") + parent.getName();
        }

        fullName =  ( parentObject != null
                            ? ( parentObject + "." ).substring( 5 ) : "" ) + field.getName();

        if (TYPE_THIS.equals(fullName)) {
            fullNameForConcat = "";
        } else if (fullName.startsWith(TYPE_THIS)) {
            fullNameForConcat = fullName.substring(5) + ".";
        } else {
            fullNameForConcat = fullName + ".";
        }
        
        hashcode = RelType.VAR_HASH + varNumber;
    }
    
    Set<Rel> getRels() {
        return rels;
    }

    public String getType() {
        return field.getType();
    }
    
    public List<Var> getChildVars() {
        if (!field.isAlias()) {
            throw new IllegalStateException("Var " + getName() + " is not alias, but " + getType());
        }
        return vars;
    }
    
    public void addVar(Var var) {
        if (!field.isAlias()) {
            throw new IllegalStateException("Var " + getName() + " is not alias, but " + getType());
        }
        vars.add(var);
    }
    
    public ClassField getField() {
        return field;
    }

    /**
     * @param rel Rel
     */
    void addRel( Rel rel ) {
        rels.add( rel );
    } // addRel

    /**
     * @return String
     */
    public String getName() {
        return field.getName();
    } // getName

    public Var getParent() {
    	return parent;
    }
    
    /**
     * @return String
     */
    public String getObject() {
        return parentObject;
    } // getObj
    
    public String getFullName() {
        return fullName;
    }
        
    public String getFullNameForConcat() {
        return fullNameForConcat;
    }
    
    public String getDeclaration() {
    	return TypeUtil.getDeclaration( field, "" );
    }

    /**
     * PLEASE USE THIS ONLY FOR DEBUGGING!!!
     * @return String
     */
    @Override
    public String toString() {
    	return getFullName();
    } // toString

    @Override
    public boolean equals( Object e ) {
        return this == e || this.varNumber == ( ( Var ) e ).varNumber;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }
}

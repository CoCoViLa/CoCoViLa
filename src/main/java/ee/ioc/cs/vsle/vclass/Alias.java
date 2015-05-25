package ee.ioc.cs.vsle.vclass;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_OBJECT;

import java.util.ArrayList;
import java.util.Collection;

import ee.ioc.cs.vsle.parser.SpecificationLoader;
import ee.ioc.cs.vsle.synthesize.AliasException;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassList;
import ee.ioc.cs.vsle.synthesize.UnknownVariableException;


public class Alias extends ClassField {

	private static final long	serialVersionUID	= 1L;
	private boolean isWildcard = false;
	private String wildcardVar;
	private boolean isInitialized = false;
	
	/**
	 * Class constructor.
	 * @param name String - alias name.
	 */
        public Alias( String name, String strictType ) {
            super( name );
            vars = new ArrayList<ClassField>();
            
            if( strictType == null || strictType.length() == 0 ) {
            	type = TYPE_OBJECT;
            } else {
            	type = strictType;
            }
            
        } // ee.ioc.cs.editor.vclass.Alias

	/**
	 * Adds a variable to the variables ArrayList.
	 * @param f ClassField - a variable added to the variables ArrayList.
	 * @throws AliasException 
	 */
	public void addVar(ClassField f) throws AliasException {
		if(f.isAny() && !TYPE_OBJECT.equals( this.getVarType() )) {
			f.setAnySpecificType(getVarType());
		}
		else if( !acceptsType( f.getType() ) ) {
			throw new AliasException( "Unable to add " + f.getType() + " " + f.getName() + " to alias " 
					+ this + " because types do not match, required: " + getVarType() + ", given: " + f.getType() );
		}
		
		vars.add(f);
	} // addVar

	public boolean acceptsType( String _type ) {
		return TYPE_OBJECT.equals( this.getVarType() ) || _type.equals( this.getVarType() );
	}
	
	/**
	 * Adds all variables from the varList input parameter to the variables ArrayList.
	 * @param input String[]
	 * @param varList ArrayList - list of added variables.
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException - exception thrown if the variable added is null.
	 * @throws AliasException 
	 */
	@Deprecated
    public void addAll(String[] input, Collection<ClassField> varList,
            ClassList classList) throws UnknownVariableException, AliasException {
        for (int i = 0; i < input.length; i++) {
            if (i > 0 && isWildcard()) {
                throw new AliasException(
                        "Alias structure can only contain one wildcard OR variables that are not wildcards");
            }
            ClassField var = getVar(input[i], varList);

            if (var != null) {
                addVar(var);
            } else if (input[i].startsWith("*.")) {
                // ClassField cf = new ClassField( input[i] );

                if (i != 0 && vars.size() > 0) {
                    throw new AliasException(
                            "Alias structure can only contain one wildcard OR variables that are not wildcards");
                }
                isWildcard = true;
                wildcardVar = input[i].substring(2);
                // addVar(cf);
            } else if (input[i].indexOf(".") >= 1) {
                String[] split = input[i].trim().split("\\.", -1);
                ClassField thisVar = getVar(split[0], varList);

                if (thisVar == null) {
                    throw new UnknownVariableException(split[0]);
                }
                boolean isElementAccess = false;
                String newType = "";
                for (int k = 1; k < split.length; k++) {
                    AnnotatedClass thisType = classList.getType(thisVar.getType() );
                    if (thisType == null) {
                        throw new UnknownVariableException("Unable to find the corresponding class for " + thisVar.getType() 
                                + " while adding " + input[i] + " to " + this.toString() );
                    }
                    ClassField cf = thisType.getFieldByName(split[k]);
                    if (cf != null) {
                        newType = cf.getType();
                    }
                    thisVar = cf;
                    
                    //alias element access, i.e. <alias>.# or <alias>.*
                    if( thisVar.isAlias() && split.length > k+1 ) {
                        //make sure this element does not refer to its own alias
                        if(input[i].startsWith( this.getName() )) {
                            throw new AliasException( input[i] + " cannot be an element of " + getName() );
                        }
                        isElementAccess = true;
                        newType = "";
                        break;
                    }
                }

                ClassField cfNew = new ClassField(input[i], newType);

                if(isElementAccess) {
                    //this is to avoid type checking, it will be done later
                    vars.add( cfNew );
                } else {
                    addVar( cfNew );
                }
            } else {
                throw new UnknownVariableException(input[i]);
            }
        }
    } // addAll
	
	/**
	 * Adds all variables from the varList input parameter to the variables ArrayList.
	 * @param input String[]
	 * @param varList ArrayList - list of added variables.
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException - exception thrown if the variable added is null.
	 * @throws AliasException 
	 */
    public void addAll(String[] input, Collection<ClassField> varList, SpecificationLoader specificationLoader) throws UnknownVariableException, AliasException {
        for (int i = 0; i < input.length; i++) {
            if (i > 0 && isWildcard()) {
                throw new AliasException(
                        "Alias structure can only contain one wildcard OR variables that are not wildcards");
            }
            ClassField var = getVar(input[i], varList);

            if (var != null) {
                addVar(var);
            } else if (input[i].startsWith("*.")) {
                // ClassField cf = new ClassField( input[i] );

                if (i != 0 && vars.size() > 0) {
                    throw new AliasException(
                            "Alias structure can only contain one wildcard OR variables that are not wildcards");
                }
                isWildcard = true;
                wildcardVar = input[i].substring(2);
                // addVar(cf);
            } else if (input[i].indexOf(".") >= 1) {
                String[] split = input[i].trim().split("\\.", -1);
                ClassField thisVar = getVar(split[0], varList);

                if (thisVar == null) {
                    throw new UnknownVariableException(split[0]);
                }
                boolean isElementAccess = false;
                String newType = "";
                for (int k = 1; k < split.length; k++) {
                    AnnotatedClass thisType = specificationLoader.getSpecification(thisVar.getType());
                    if (thisType == null) {
                        throw new UnknownVariableException("Unable to find the corresponding class for " + thisVar.getType() 
                                + " while adding " + input[i] + " to " + this.toString() );
                    }
                    ClassField cf = thisType.getFieldByName(split[k]);
                    if (cf != null) {
                        newType = cf.getType();
                    }
                    thisVar = cf;
                    
                    //alias element access, i.e. <alias>.# or <alias>.*
                    if( thisVar.isAlias() && split.length > k+1 ) {
                        //make sure this element does not refer to its own alias
                        if(input[i].startsWith( this.getName() )) {
                            throw new AliasException( input[i] + " cannot be an element of " + getName() );
                        }
                        isElementAccess = true;
                        newType = "";
                        break;
                    }
                }

                ClassField cfNew = new ClassField(input[i], newType);

                if(isElementAccess) {
                    //this is to avoid type checking, it will be done later
                    vars.add( cfNew );
                } else {
                    addVar( cfNew );
                }
            } else {
                throw new UnknownVariableException(input[i]);
            }
        }
    } // addAll

	/**
	 * Converts the ee.ioc.cs.editor.vclass.Alias into string, returning the alias's name.
	 * NB! FOR DEBUG ONLY
	 * @return String - alias's name.
	 */
	@Override
    public String toString() {
		return "(alias)" + super.toString();
	} // toString

	public boolean equalsByTypes( Alias alias ) {
	    if( vars.size() != alias.vars.size() ) {
	        return false;
	    }
	    
		for (int i = 0; i < vars.size(); i++) {
		    ClassField thisVar = vars.get(i);
		    ClassField otherVar = alias.vars.get(i);
		    
		    if(thisVar.isAny() || otherVar.isAny())
		    	continue;
		    
			if( !thisVar.getType().equals( otherVar.getType() )
			        || ( thisVar.isAlias() ^ otherVar.isAlias() )
			        || thisVar.isAlias() && otherVar.isAlias() && !((Alias)thisVar).equalsByTypes( (Alias)otherVar ) ) {
			    return false;
			}
		}
		return true;
	}
	
	public String getVarType() {
		return type;
	}
	
	@Override
    public String getType() {
		return type + "[]";
	}


	/**
	 * Returns a variable defined by the varName method input parameter from
	 * the variables list given as the method second parameter.
	 * @param varName String - name of the variable to be returned.
	 * @param varList ArrayList - list of variables to be checked through.
	 * @return ClassField - ClassField variable found from the varList by the name "varName".
	 */
    ClassField getVar(String varName, Collection<ClassField> varList) {

        for ( ClassField var : varList ) {
			if (var.getName().equals(varName)) {
				return var;
			}
		}
		return null;
	} // getVar

	public boolean isWildcard() {
		return isWildcard;
	}

	public boolean isEmpty() {
		return vars.isEmpty();
	}

	@Override
    public boolean isAlias() {
		return true;
	} // isAlias
	
	@Override
    public Alias clone() {
      final Alias newAlias = (Alias) super.clone();
      newAlias.vars = new ArrayList<>(vars.size());
      for (ClassField var : vars) {
          newAlias.vars.add(var.clone());
      }
      return newAlias;
	} // clone

	public String getWildcardVar() {
		return wildcardVar;
	}
	
    /**
     * Alias is not initialized if it has been declared, 
     * but no components have been assigned
     * @return
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isInitialized
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
}

package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.synthesize.*;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_ALIAS;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_OBJECT;

import java.util.*;


public class Alias extends ClassField {

	private boolean isWildcard = false;
	private boolean isStrictType = false;
	private boolean isOneTypeVars = true;
	//this means that alias represents Object[] with elements of any type
	private boolean isObjectType = false;
	private String wildcardVar;
	
	/**
	 * Class constructor.
	 * @param name String - alias name.
	 */
        public Alias( String name ) {
            super( name );
            vars = new ArrayList<ClassField>();
            //type = TYPE_ALIAS;
        } // ee.ioc.cs.editor.vclass.Alias

	/**
	 * Adds a variable to the variables ArrayList.
	 * @param f ClassField - a variable added to the variables ArrayList.
	 * @throws AliasException 
	 */
	public void addVar(ClassField f) throws AliasException {
		if( isStrictType && !isWildcard() && !f.getType().equals( type ) ) {
			throw new AliasException( "Unable to add " + f.type + " " + f + " to alias " 
					+ this + " because types do not match: " + type + " vs. " + f.type );
		} else if( ( type != null ) && !isObjectType && (!isStrictType && !f.getType().equals( type ) 
									 /*|| ( f.isAlias() )*/ ) ) {
			isOneTypeVars = false;
			type = TYPE_OBJECT;
		} else if( type == null ){
			type = f.getType();
		}
		vars.add(f);
	} // addVar

	/**
	 * Adds all variables from the varList input parameter to the variables ArrayList.
	 * @param input String[]
	 * @param varList ArrayList - list of added variables.
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException - exception thrown if the variable added is null.
	 * @throws AliasException 
	 */
    public void addAll(String[] input, Collection<ClassField> varList, ClassList classList) throws UnknownVariableException, AliasException {
		for (int i = 0; i < input.length; i++) {
			if( i > 0 && isWildcard() ) {
				throw new AliasException( "Alias structure can only contain one wildcard OR variables that are not wildcards" );
			}
			ClassField var = getVar(input[i], varList);
			
			if (var != null) {
				addVar(var);
			} else if (input[i].indexOf(".") >= 1 && !input[i].startsWith("*.")) {
				String[] split = input[i].trim().split("\\.", -1);
				ClassField thisVar = getVar(split[0], varList);
				if( thisVar == null ) {
					throw new UnknownVariableException(split[0]);
				}
				String newType = "";
				for (int k = 1; k < split.length; k++) {
					ClassField cf = classList.getType(thisVar.type).getFieldByName(split[k]);
					if (cf != null) {
						newType = cf.type;
					}
					thisVar = cf;
				}
				
				ClassField cfNew = new ClassField( input[i], newType);
				
				addVar(cfNew);
			} else if (input[i].startsWith("*.")) {
				//ClassField cf = new ClassField( input[i] );
				
				if( i != 0 && vars.size() > 0 ) {
					throw new AliasException( "Alias structure can only contain one wildcard OR variables that are not wildcards" );
				}
				isWildcard = true;
				wildcardVar = input[i].substring( 2 );
				//addVar(cf);
			} else {
				throw new UnknownVariableException(input[i]);
			}
		}
	} // addAll


	/**
	 * Converts the ee.ioc.cs.editor.vclass.Alias into string, returning the alias's name.
	 * @return String - alias's name.
	 */
	public String toString() {
		return name;
	} // toString

	/**
	 * Returns the type of an alias.
	 * @return String - alias's type.
	 */
	public String getAliasType() {
		String type = TYPE_ALIAS + ":";
		ClassField cf;

		for (int i = 0; i < vars.size(); i++) {
			cf = vars.get(i);
			type += cf.type;
		}
		return type;
	} // getAliasType

	public boolean equalsByTypes( Alias alias ) {
		
		for (int i = 0; i < vars.size(); i++) {
			//TODO if alias contains alias then need additional check
			if( !vars.get(i).getType().equals(alias.vars.get(i).getType()) ) {
				return false;
			}
		}
		return true;
	}
	
	public String getVarType() {
		return type;
	}
	
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
			if (var.name.equals(varName)) {
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

	public void setStrictTypeOfVars(String strictTypeOfVars) {
		if( TYPE_OBJECT.equals( strictTypeOfVars ) ) {
			isObjectType = true;
		} else {
			isStrictType = true;
		}
		this.type = strictTypeOfVars;
	}

	public boolean isStrictType() {
		return isStrictType;
	}

	public boolean isObjectType() {
		return isObjectType;
	}

	public boolean isOneTypeVars() {
		return isOneTypeVars;
	}
	public boolean isAlias() {
		//return TypeUtil.TYPE_ALIAS.equals( type );
		return true;
	} // isAlias
	
	public Alias clone() {
		return (Alias) super.clone();
	} // clone

	public String getWildcardVar() {
		return wildcardVar;
	}
}

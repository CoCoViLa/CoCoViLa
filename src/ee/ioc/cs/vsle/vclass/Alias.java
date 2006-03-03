package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.synthesize.UnknownVariableException;
import ee.ioc.cs.vsle.synthesize.ClassList;
import java.util.ArrayList;


public class Alias extends ClassField {

	private static final String ALIAS = "alias";

	private boolean isWildcard = false;
	/**
	 * Class constructor.
	 * @param name String - alias name.
	 */
        public Alias( String name ) {
            super( name );
            vars = new ArrayList<ClassField>();
            type = ALIAS;
        } // ee.ioc.cs.editor.vclass.Alias

	/**
	 * Adds a variable to the variables ArrayList.
	 * @param f ClassField - a variable added to the variables ArrayList.
	 */
	void addVar(ClassField f) {
		vars.add(f);
	} // addVar

	/**
	 * Adds all variables from the varList input parameter to the variables ArrayList.
	 * @param input String[]
	 * @param varList ArrayList - list of added variables.
	 * @throws ee.ioc.cs.vsle.synthesize.UnknownVariableException - exception thrown if the variable added is null.
	 */
	public void addAll(String[] input, ArrayList<ClassField> varList, ClassList classList) throws UnknownVariableException {
		for (int i = 0; i < input.length; i++) {
			ClassField var = getVar(input[i], varList);

			if (var != null) {
				vars.add(var);
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

				vars.add(cfNew);
			} else if (input[i].startsWith("*.")) {
				ClassField cf = new ClassField( input[i] );

				vars.add(cf);
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
		String type = ALIAS + ":";
		ClassField cf;

		for (int i = 0; i < vars.size(); i++) {
			cf = vars.get(i);
			type += cf.type;
		}
		return type;
	} // getAliasType

	public String getRealType() {
		ClassField cf1 = null, cf2;
                if( vars.size() == 1 ) {
                    cf1 = vars.get(0);
                }
                for ( int i = 1; i < vars.size(); i++ ) {
                    cf1 = vars.get( i - 1 );
                    cf2 = vars.get( i );
                    if ( !cf1.type.equals( cf2.type ) ) {
                        return getAliasType();
                    }
                }
		return cf1.type+"[]";
	}


	/**
	 * Returns a variable defined by the varName method input parameter from
	 * the variables list given as the method second parameter.
	 * @param varName String - name of the variable to be returned.
	 * @param varList ArrayList - list of variables to be checked through.
	 * @return ClassField - ClassField variable found from the varList by the name "varName".
	 */
	ClassField getVar(String varName, ArrayList<ClassField> varList) {
		ClassField var;

		for (int i = 0; i < varList.size(); i++) {
			var = varList.get(i);
			if (var.name.equals(varName)) {
				return var;
			}
		}
		return null;
	} // getVar

	public boolean isWildcard() {
		return isWildcard;
	}

	public void setWildcard(boolean isWildcard) {
		this.isWildcard = isWildcard;
	}

}

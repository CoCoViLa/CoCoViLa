package ee.ioc.cs.vsle.vclass;

import ee.ioc.cs.vsle.synthesize.UnknownVariableException;

import java.util.ArrayList;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.Alias</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Aulo Aasmaa, Ando Saabas
 * @version 1.0
 */
public class Alias
	extends ClassField {

	private static final String ALIAS = "alias";

	/**
	 * Class constructor.
	 * @param name String - alias name.
	 */
	public Alias(String name) {
		vars = new ArrayList();
		this.name = name;
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
	public void addAll(String[] input, ArrayList varList) throws UnknownVariableException {
		for (int i = 0; i < input.length; i++) {
			ClassField var = getVar(input[i], varList);

			if (var != null) {
				vars.add(var);
			}
			else {
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
			cf = (ClassField) vars.get(i);
			type += cf.type;
		}
		return type;
	} // getAliasType

	/**
	 * Returns a variable defined by the varName method input parameter from
	 * the variables list given as the method second parameter.
	 * @param varName String - name of the variable to be returned.
	 * @param varList ArrayList - list of variables to be checked through.
	 * @return ClassField - ClassField variable found from the varList by the name "varName".
	 */
	ClassField getVar(String varName, ArrayList varList) {
		ClassField var;

		for (int i = 0; i < varList.size(); i++) {
			var = (ClassField) varList.get(i);
			if (var.name.equals(varName)) {
				return var;
			}
		}
		return null;
	} // getVar

}

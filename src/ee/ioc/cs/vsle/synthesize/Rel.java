package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.util.db;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

class Rel implements Cloneable, Serializable {

	private List outputs = new ArrayList();

	private List inputs = new ArrayList();

	private List subtasks = new ArrayList();

	private ArrayList exceptions = new ArrayList();

	private List algorithm = new ArrayList();

        //only for subtasks
	private Rel parentRel = null;

        //parent axiom containing this subtask
	private int relNumber = 0;

	private int unknownInputs;

	private int subtaskCounter;

	private String object;

	private String method;

	private int type; /* 1 - equals, 2 - method */

	Rel() {
		relNumber = RelType.relCounter++;
	}

        int getType() {
            return type;
        }

        int getUnknownInputs() {
            return unknownInputs;
        }

        int getSubtaskCounter() {
                return subtaskCounter;
        }

        void setSubtaskCounter( int value ) {
            subtaskCounter = value;
        }

        List getExceptions() {
            return exceptions;
        }

	void setParentRel(Rel rel) {
		if (type != RelType.TYPE_SUBTASK)
			throw new IllegalStateException(
					"Only subtasks can contain parent rels");

		parentRel = rel;
	}

	Rel getParentRel() {
		if (type != RelType.TYPE_SUBTASK)
			throw new IllegalStateException(
					"Only subtasks can contain parent rels");

		return parentRel;
	}

	void addRelToAlgorithm(Rel rel) {
		if (type != RelType.TYPE_SUBTASK)
			throw new IllegalStateException(
					"Only subtasks can contain algorithms");

		algorithm.add(rel);
	}

	List getAlgorithm() {
		if (type != RelType.TYPE_SUBTASK)
			throw new IllegalStateException(
					"Only subtasks can contain algorithms");

		return algorithm;
	}

	List getOutputs() {
		return outputs;
	}

	List getInputs() {
		return inputs;
	}

	List getSubtasks() {
		return subtasks;
	}

	void setUnknownInputs(int f) {
		unknownInputs = f;
	}

	void setSubtaskFlag(int f) {
		subtaskCounter = f;
	}

	void setObj(String s) {
		object = s;
	}

	void setMethod(String m) {
		method = m;
	}

        String getMethod() {
            return method;
        }

	void setType(int t) {
		type = t;
	}

	String getMaxType(List inputs) {
		Var var;

		for (int i = 0; i < inputs.size(); i++) {
			var = (Var) inputs.get(i);
			if (!var.getType().equals("int")) {
				return "double";
			}
		}
		return "int";
	}

        String getOutput() {

            String outputString = "";
            Var var = ( Var ) outputs.get( 0 );

            if ( !var.getType().equals( "void" ) ) {
                if ( var.getField().isAlias() ) {
                    String alias_tmp = CodeGenerator.ALIASTMP + var.getName() + relNumber;

                    if( var.getField().getVars().size() > 0 ) {

                    outputString = ( ( ClassField ) var.getField().getVars().get( 0 ) ).getType()
                         + "[] " + alias_tmp + " ";
                    }

                } else {
                    outputString = var.toString();
                }

            }
            return outputString;

        }

	String getParameters(boolean useBrackets) {
		String params = "";
		if (useBrackets)
			params += "(";
		Var var;
		int j = 0;

		String paramString = "";
		for (int i = 0; i < inputs.size(); i++) {

			var = (Var) inputs.get(i);

			if (!var.getType().equals("void")) {
				if (var.getType().equals("alias")) {
					paramString = CodeGenerator.ALIASTMP + var.getName() + relNumber;
				} else {
					paramString = var.toString();
				}
				if (j == 0) {
					params += paramString;
				} else {
					params += ", " + paramString;
				}
				j++;
			}
		}
		if (useBrackets)
			return params += ")";
		return params;

	}

	String getSubtaskParameters() {
		String params = "(";
		boolean subExist = false;
		for (int i = 0; i < subtasks.size(); i++) {
			if (i == 0) {
				params += RelType.TAG_SUBTASK;
				subExist = true;
			} else {
				params += ", " + RelType.TAG_SUBTASK;
			}
		}
		if (subExist && inputs.size() > 0) {
			params += ", ";
		}
		params += getParameters(false);
		return params += ")";
	}

	void addInput(Var var) {
		inputs.add(var);
	}

	void addOutput(Var var) {
		outputs.add(var);
	}

	void addSubtask(Rel rel) {
		subtasks.add(rel);
	}

	static String getObject(String obj) {
		if (obj.equals("this")) {
			return "";
		} else if (obj.startsWith("this")) {
			return obj.substring(5) + ".";
		} else {
			return obj + ".";
		}
	}

	public String toString() {
		Pattern pattern;
		Matcher matcher;

		if (type == RelType.TYPE_ALIAS) {
			//db.p( "alias inputs " + inputs + " outputs " + outputs );
			return "";
		}
		if (type == RelType.TYPE_JAVAMETHOD) {
			Var op = (Var) outputs.get(0);

			if (op.getType().equals("void")) {
				return (checkAliasInputs() + getObject(object) + method + getParameters(true));
			} else {
				return checkAliasInputs() /*+ outputAliasDeclar()*/
						+ ( getOutput() + " = " + getObject(object)
								+ method + getParameters(true)) +
								";\n" +checkAliasOutputs();
			}
		} else if (type == RelType.TYPE_EQUATION) {
			// if its an array assingment
			if (inputs.size() == 0 && outputs.size() == 1) {
				String assign;
				Var op = (Var) outputs.get(0);

				if (op.getField().isPrimOrStringArray()) {
					String[] split = method.split("=");
					assign = op.getField().getType() + " " + " TEMP"
							+ Integer.toString(RelType.auxVarCounter) + "="
							+ split[1] + ";\n";
					assign += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
							+ getObject(op.getObject()) + op.getName() + " = TEMP"
							+ Integer.toString(RelType.auxVarCounter) + ";\n";
					RelType.auxVarCounter++;
					return assign;

				}
			}

			if (inputs.size() == 1 && outputs.size() == 1) {
				String s1, assigns = "";
				Var ip = (Var) inputs.get(0);
				Var op = (Var) outputs.get(0);

				if (ip.getField().isArray() && op.getField().isAlias()) {

					for (int i = 0; i < ((Var) outputs.get(0)).getField().getVars()
							.size(); i++) {
						s1 = ((ClassField) op.getField().getVars().get(i)).toString();
						assigns += getObject(op.getObject()) + s1 + " = " + ip + "["
								+ Integer.toString(i) + "];\n";
					}
					return assigns;
				}
				if (op.getField().isArray() && ip.getField().isAlias()) {

					assigns += op.getField().getType() + " TEMP"
							+ Integer.toString(relNumber) + " = new "
							+ op.getField().arrayType() + "[" + ip.getField().getVars().size()
							+ "];\n";
					for (int i = 0; i < ip.getField().getVars().size(); i++) {
						s1 = ((ClassField) ip.getField().getVars().get(i)).toString();
						assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
								+ " TEMP" + Integer.toString(relNumber) + "["
								+ Integer.toString(i) + "] = "
								+ getObject(ip.getObject()) + s1 + ";\n";
					}
					assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB + op
							+ " = " + " TEMP" + Integer.toString(relNumber);
					//RelType.auxVarCounter++;
					return assigns;
				}
			}

			Var var;
			String m = new String(method + " ");
			String left = "";
			String left2 = "";
			String right = "";
			ArrayList ajut = new ArrayList();
			for (int i = 0; i < inputs.size(); i++) {
				var = (Var) inputs.get(i);
				pattern = Pattern.compile("([^a-zA-Z_])(([a-zA-Z_0-9]+\\.)*)"
						+ var.getName() + "([^a-zA-Z0-9_])");
				matcher = pattern.matcher(m);
				if (matcher.find()) {
					left = matcher.group(1);
					left2 = matcher.group(2);
					right = matcher.group(4);
				}
				ajut.add(new AjutHack(var.getName(), "#" + Integer.toString(i)));
				m = m.replaceFirst("([^a-zA-Z_]" + left2 + var.getName()
						+ "[^a-zA-Z0-9_])", left + getObject(var.getObject()) + "#"
						+ Integer.toString(i) + right);
			}

			for (int i = 0; i < inputs.size(); i++) {
				AjutHack paar = (AjutHack) ajut.get(i);
				m = m.replaceFirst(paar.repl, paar.var);
			}

			left2 = "";
			var = (Var) outputs.get(0);
			pattern = Pattern.compile("([^a-zA-Z_]?)(([a-zA-Z_0-9]+\\.)*)"
					+ var.getName() + "([^a-zA-Z0-9_])");
			matcher = pattern.matcher(m);
			if (matcher.find()) {
				left = matcher.group(1);
				left2 = matcher.group(2);
				right = matcher.group(4);
			}

			m = m.replaceFirst("([^a-zA-Z_]?" + left2 + var.getName()
					+ "[^a-zA-Z0-9_])", left + getObject(var.getObject()) + var.getName()
					+ right);

			if (((Var) outputs.get(0)).getType().equals("int")
					&& (!getMaxType(inputs).equals("int") || method
							.indexOf(".") >= 0)) {
				m = m.replaceFirst("=", "= (int)(") + ")";

			}
			return m;
		} else if (type == RelType.TYPE_SUBTASK) {
			return (Var) outputs.get(0) + " = " + (Var) inputs.get(0);

		} else if (type == RelType.TYPE_METHOD_WITH_SUBTASK) {
			if (!outputs.isEmpty()) {
				return (checkAliasInputs() /*+ outputAliasDeclar()*/ + getOutput() + " = "
						+ getObject(object) + method + getSubtaskParameters())
                                                + ";\n" +checkAliasOutputs();
			} else {
				return (checkAliasInputs() + getObject(object) + method + getSubtaskParameters());
			}
		} else {
			if (RuntimeProperties.isDebugEnabled())
				db.p(method);
			String s1, s2, assigns = "";
			Var ip = (Var) inputs.get(0);
			Var op = (Var) outputs.get(0);

			if (ip.getField().isArray() && op.getField().isAlias()) {

				for (int i = 0; i < ((Var) outputs.get(0)).getField().getVars().size(); i++) {
					s1 = (String) ((Var) outputs.get(0)).getField().getVars().get(i);
					assigns += "        " + getObject(op.getObject()) + s1 + " = "
							+ ip + "[" + Integer.toString(i) + "];\n";
				}
				return assigns;
			}
			if (op.getField().isArray() && ip.getField().isAlias()) {
				for (int i = 0; i < ip.getField().getVars().size(); i++) {
					s1 = (String) ip.getField().getVars().get(i);
					assigns += CodeGenerator.OT_TAB + op + "["
							+ Integer.toString(i) + "] = "
							+ getObject(ip.getObject()) + s1 + ";\n";
				}
				return assigns;
			}
			if (op.getField().isAlias() && ip.getField().isAlias()) {
				for (int i = 0; i < ip.getField().getVars().size(); i++) {
					s1 = (String) ip.getField().getVars().get(i);
					s2 = (String) op.getField().getVars().get(i);

					assigns += CodeGenerator.OT_TAB + getObject(op.getObject()) + s2
							+ " = " + getObject(ip.getObject()) + s1 + ";\n";
				}
				return assigns;
			}

			return op + " = " + ip;
		}
	}

	/**
	 * @return
	 */
        private String outputAliasDeclar() {
            String declar = "";
            Var output = ( Var ) outputs.get( 0 );
            if ( output.getField().isAlias() ) {
                String alias_tmp = CodeGenerator.ALIASTMP + output.getName() + relNumber;
                declar = ( ( ClassField ) output.getField().getVars().get( 0 ) ).getType()
                         + "[] " + alias_tmp + " = new "
                         + ( ( ClassField ) output.getField().getVars().get( 0 ) ).getType() + "["
                         + output.getField().getVars().size() + "];\n" +
                         CodeGenerator.OT_TAB + CodeGenerator.OT_TAB; ;
            }
            return declar;
        }

	String checkAliasInputs() {
		Var input;
		String assigns = "";
		for (int i = 0; i < inputs.size(); i++) {
			input = (Var) inputs.get(i);
			if (input.getField().isAlias()) {
				if (input.getField().getVars().size() == 0) {
					assigns = "Object " + input.getName() + " = null";
				} else {

					String alias_tmp = CodeGenerator.ALIASTMP + input.getName() + relNumber;
					assigns += ((ClassField) input.getField().getVars().get(0)).getType()
							+ "[] " + alias_tmp + " = new "
							+ ((ClassField) input.getField().getVars().get(0)).getType() + "["
							+ input.getField().getVars().size() + "];\n";
					for (int k = 0; k < input.getField().getVars().size(); k++) {
						String s1 = ((ClassField) input.getField().getVars().get(k))
								.toString();
						assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
								+ alias_tmp + "[" + Integer.toString(k)
								+ "] = " + getObject(object) + s1 + ";\n";
					}
					assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB;
				}
			}
		}
		return assigns;
	}

	String checkAliasOutputs() {
		String assigns = "";
		Var output = ((Var) outputs.get(0));
		if (output.getField().isAlias()) {
			String alias_tmp = CodeGenerator.ALIASTMP + output.getName()
			+ relNumber;
			if (output.getField().getVars().size() == 0) {
				assigns = "Object " + output.getName() + " = null";
			} else {
				for (int k = 0; k < output.getField().getVars().size(); k++) {
					String s1 = ((ClassField) output.getField().getVars().get(k))
							.toString();
					assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
							+  getObject(object) + s1 +"= "
							+ alias_tmp + "[" + Integer.toString(k) + "];\n";
				}
				assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB;
			}
		}

		return assigns;
	}

	public boolean equals(Object e) {
		return this.relNumber == ((Rel) e).relNumber;
	}

	public int hashCode() {
		return RelType.REL_HASH + relNumber;
	}

	public Object clone() {
		try {
			Rel rel = (Rel) super.clone();

			return rel;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	//	public Object clone()
	//	{
	//	    try {
	//            Rel rel = (Rel) super.clone();
	//
	//            rel.outputs = (ArrayList)outputs.clone();
	//            for (int i = 0; i < rel.outputs.size(); i++) {
	//                Var var = (Var)rel.outputs.get(i);
	//                var = (Var)var.clone();
	//                rel.outputs.set(i, var);
	//            }
	//
	//            rel.inputs = (ArrayList)inputs.clone();
	//            for (int i = 0; i < rel.inputs.size(); i++) {
	//                Var var = (Var)rel.inputs.get(i);
	//                var = (Var)var.clone();
	//                rel.inputs.set(i, var);
	//            }
	//
	//            rel.subtasks = (ArrayList)subtasks.clone();
	//            for (int i = 0; i < rel.subtasks.size(); i++) {
	//                Var var = (Var)rel.subtasks.get(i);
	//                var = (Var)var.clone();
	//                rel.subtasks.set(i, var);
	//            }
	//
	//            rel.unknownInputs = rel.inputs.size();
	//    		rel.subtaskFlag = rel.subtasks.size();
	//
	//    		rel.inAlgorithm = false;
	//
	//    		return rel;
	//
	//        } catch (CloneNotSupportedException e) {
	//            return null;
	//        }
	//	}
}

class AjutHack {
	String var;

	String repl;

	public AjutHack(String name, String s) {
		var = name;
		repl = s;
	}
}

package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.util.db;
import java.util.*;
import java.util.regex.*;

class Rel {
	ArrayList outputs = new ArrayList();
	ArrayList inputs = new ArrayList();
	ArrayList subtasks = new ArrayList();
    static int auxVarCounter = 0;

	int flag;
	int subtaskFlag;
	String object;
	String method;
    boolean inAlgorithm = false;

	int type; /* 1 - equals, 2 - method*/

	void setFlag(int f) {
		flag = f;
	}

	void setSubtaskFlag(int f) {
		subtaskFlag = f;
	}

	void setObj(String s) {
		object = s;
	}

	void setMethod(String m) {
		method = m;
	}

	void setType(int t) {
		type = t;
	}

	String getMaxType(ArrayList inputs) {
		Var var;

		for (int i = 0; i < inputs.size(); i++) {
			var = (Var) inputs.get(i);
			if (!var.type.equals("int")) {
				return "double";
			}
		}
		return "int";
	}

	String getParameters() {
		String params = "(";
		Var var;
		int j = 0;

		for (int i = 0; i < inputs.size(); i++) {
			var = (Var) inputs.get(i);
			if (!var.type.equals("void")) {
				if (j == 0) {
					params += var;
				}
				else {
					params += ", " + var;
				}
				j++;
			}
		}
		return params += ")";

	}

	String getSubtaskParameters() {
		String params = "(";
        boolean subExist = false;
		for (int i = 0; i < subtasks.size(); i++) {
			if (i == 0) {
				params += "subtask" + Integer.toString(i);
                subExist = true;
			}
			else {
				params += ", subtask" + Integer.toString(i);
			}
		}
		for (int i = 0; i < inputs.size(); i++) {
			if (i == 0 && !subExist) {
				params += (Var) inputs.get(i);
			}
			else {
				params += ", " + (Var) inputs.get(i);
			}
		}
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

	public String toString() {
		Pattern pattern;
		Matcher matcher;

		if (type == 4) {
			return "";
		}
		if (type == 2) {
			Var op = (Var) outputs.get(0);

			if (op.type.equals("void")) {
				return (object + "." + method + getParameters());
			}
			else {
				return ( (Var) outputs.get(0) + " = " + object + "." + method + getParameters());
			}
		}
		else if (type == 3) {
			// if its an array assingment
			if (inputs.size() == 0 && outputs.size() == 1) {
				String assign;
				Var op = (Var) outputs.get(0);

				if (op.field.isPrimOrStringArray()) {
					String[] split = method.split("=");
                    assign = op.field.type + " " + " TEMP"+Integer.toString(auxVarCounter)+"=" + split[1] + ";\n";
					assign += op.object + "." + op.name + " = TEMP"+Integer.toString(auxVarCounter)+";\n";
					auxVarCounter++;
					return assign;

				}
			}

			if (inputs.size() == 1 && outputs.size() == 1) {
				String s1, assigns = "";
				Var ip = (Var) inputs.get(0);
				Var op = (Var) outputs.get(0);

				if (ip.field.isArray() && op.field.isAlias()) {

					for (int i = 0; i < ( (Var) outputs.get(0)).field.vars.size(); i++) {
						s1 = ( (ClassField) op.field.vars.get(i)).toString();
						assigns += "        " + op.object + "." + s1 + " = " + ip + "[" + Integer.toString(i) + "];\n";
					}
					return assigns;
				}
				if (op.field.isArray() && ip.field.isAlias()) {

					assigns += op.field.type +" TEMP" + Integer.toString(auxVarCounter) + " = new " + op.field.arrayType()+"["+ip.field.vars.size()+"];\n";
					for (int i = 0; i < ip.field.vars.size(); i++) {
						s1 = ( (ClassField) ip.field.vars.get(i)).toString();
						assigns += "        " + " TEMP" + Integer.toString(auxVarCounter) + "[" + Integer.toString(i) + "] = " + ip.object + "." + s1 + ";\n";
					}
					assigns += "        " + op +" = "+" TEMP" + Integer.toString(auxVarCounter) ;
					auxVarCounter++;
					return assigns;
				}
			}

			Var var;
			String m = new String(method + " ");
			String left = "";
			String left2 = "";
			String right = "";

			for (int i = 0; i < inputs.size(); i++) {
				var = (Var) inputs.get(i);
				pattern = Pattern.compile("([^a-zA-Z_])(([a-zA-Z_0-9]+\\.)*)" + var.name + "([^a-zA-Z0-9_])");
				matcher = pattern.matcher(m);
				if (matcher.find()) {
					left = matcher.group(1);
					left2 = matcher.group(2);
					right = matcher.group(4);
				}
				m = m.replaceFirst("([^a-zA-Z_]" + left2 + var.name + "[^a-zA-Z0-9_])", left + var.object + "." + var.name + right);
			}
			left2 = "";
			var = (Var) outputs.get(0);
			pattern = Pattern.compile("([^a-zA-Z_]?)(([a-zA-Z_0-9]+\\.)*)" + var.name + "([^a-zA-Z0-9_])");
			matcher = pattern.matcher(m);
			if (matcher.find()) {
				left = matcher.group(1);
				left2 = matcher.group(2);
				right = matcher.group(4);
			}

			m = m.replaceFirst("([^a-zA-Z_]?" + left2 + var.name + "[^a-zA-Z0-9_])", left + var.object + "." + var.name + right);

			if ( ( (Var) outputs.get(0)).type.equals("int") && (!getMaxType(inputs).equals("int") || method.indexOf(".") >= 0)) {
				m = m.replaceFirst("=", "= (int)(") + ")";

			}
			return m;
		}
		else if (type == 5) {
			return (Var) outputs.get(0) + " = " + (Var) inputs.get(0);

		}
		else if (type == 6) {
			if (!outputs.isEmpty()) {
				return ( (Var) outputs.get(0) + " = " + object + "." + method + getSubtaskParameters());// + getParameters()
			}
			else {
				return (object + "." + method + getSubtaskParameters());// + getParameters()
			}
		}
		else {
			db.p(method);
			String s1, s2, assigns = "";
			Var ip = (Var) inputs.get(0);
			Var op = (Var) outputs.get(0);

			if (ip.field.isArray() && op.field.isAlias()) {

				for (int i = 0; i < ( (Var) outputs.get(0)).field.vars.size(); i++) {
					s1 = (String) ( (Var) outputs.get(0)).field.vars.get(i);
					assigns += "        " + op.object + "." + s1 + " = " + ip + "[" + Integer.toString(i) + "];\n";
				}
				return assigns;
			}
			if (op.field.isArray() && ip.field.isAlias()) {
				for (int i = 0; i < ip.field.vars.size(); i++) {
					s1 = (String) ip.field.vars.get(i);
					assigns += "        " + op + "[" + Integer.toString(i) + "] = " + ip.object + "." + s1 + ";\n";
				}
				return assigns;
			}
			if (op.field.isAlias() && ip.field.isAlias()) {
				for (int i = 0; i < ip.field.vars.size(); i++) {
					s1 = (String) ip.field.vars.get(i);
					s2 = (String) op.field.vars.get(i);

					assigns += "        " + op.object + "." + s2 + " = " + ip.object + "." + s1 + ";\n";
				}
				return assigns;
			}

			return op + " = " + ip;
		}
	}

}

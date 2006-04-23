package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

class Rel implements Serializable {

    private List<Var> outputs = new ArrayList<Var>();

    private List<Var> inputs = new ArrayList<Var>();

    private List<Rel> subtasks = new ArrayList<Rel>();

    private ArrayList<Var> exceptions = new ArrayList<Var>();

    private List<Rel> algorithm = new ArrayList<Rel>();

    // only for subtasks
    private Rel parentRel = null;

    // parent axiom containing this subtask
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

    void setSubtaskCounter(int value) {
        subtaskCounter = value;
    }

    List<Var> getExceptions() {
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

    List<Rel> getAlgorithm() {
        if (type != RelType.TYPE_SUBTASK)
            throw new IllegalStateException(
                    "Only subtasks can contain algorithms");

        return algorithm;
    }

    List<Var> getOutputs() {
        return outputs;
    }

    List<Var> getInputs() {
        return inputs;
    }

    List<Rel> getSubtasks() {
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

    String getMaxType(List<Var> inputs) {
        Var var;

        for (int i = 0; i < inputs.size(); i++) {
            var = inputs.get(i);
            if (!var.getType().equals("int")) {
                return "double";
            }
        }
        return "int";
    }

    String getOutput() {

        String outputString = "";
        Var var = outputs.get(0);

        if (!TypeUtil.TYPE_VOID.equals( var.getType() )) {
            if (var.getField().isAlias()) {
                String alias_tmp = getAliasTmpName(var.getName());

                if (var.getField().getVars().size() > 0) {

                    outputString = ((Alias)var.getField()).getType()
                            + " " + alias_tmp + " ";
                }

            } else {
                outputString = var.toString();
            }

        }
        return outputString;

    }

    private String getAliasTmpName(String varName) {
    	varName = varName.replaceAll( "\\.", "_" );
        return TypeUtil.TYPE_ALIAS + "_" + varName + "_" + relNumber;
    }

    String getParameters(boolean useBrackets) {
        String params = "";
        if (useBrackets)
            params += "(";
        Var var;
        int j = 0;

        String paramString = "";
        for (int i = 0; i < inputs.size(); i++) {

            var = inputs.get(i);

            if (!TypeUtil.TYPE_VOID.equals( var.getType() ) ) {
                if ( var.getField().isAlias() ) {
                    paramString = getAliasTmpName(var.getName());
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
            // db.p( "alias inputs " + inputs + " outputs " + outputs );
            return "";
        }
        if (type == RelType.TYPE_JAVAMETHOD) {
            Var op = outputs.get(0);

            if ( TypeUtil.TYPE_VOID.equals( op.getType() )) {
                return (checkAliasInputs() + getObject(object) + method + getParameters(true));
            }
            return checkAliasInputs() /* + outputAliasDeclar() */
                    + (getOutput() + " = " + getObject(object) + method + getParameters(true))
                    + ";\n" + checkAliasOutputs();
        } else if (type == RelType.TYPE_EQUATION) {
            // if its an array assingment
            if (inputs.size() == 0 && outputs.size() == 1) {
                String assign;
                Var op = outputs.get(0);

                if (op.getField().isPrimOrStringArray()) {
                    String[] split = method.split("=");
                    assign = op.getField().getType() + " " + " TEMP"
                            + Integer.toString(RelType.auxVarCounter) + "="
                            + split[1] + ";\n";
                    assign += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
                            + getObject(op.getObject()) + op.getName()
                            + " = TEMP"
                            + Integer.toString(RelType.auxVarCounter) + ";\n";
                    RelType.auxVarCounter++;
                    return assign;

                }
            }

            if (inputs.size() == 1 && outputs.size() == 1) {
                String s1, assigns = "";
                Var ip = inputs.get(0);
                Var op = outputs.get(0);

                if (ip.getField().isArray() && op.getField().isAlias()) {

                    for (int i = 0; i < outputs.get(0).getField().getVars()
                            .size(); i++) {
                        s1 = op.getField().getVars().get(i).toString();
                        assigns += getObject(op.getObject()) + s1 + " = " + ip
                                + "[" + Integer.toString(i) + "];\n";
                    }
                    return assigns;
                }
                if (op.getField().isArray() && ip.getField().isAlias()) {

                    assigns += op.getField().getType() + " TEMP"
                            + Integer.toString(relNumber) + " = new "
                            + op.getField().arrayType() + "["
                            + ip.getField().getVars().size() + "];\n";
                    for (int i = 0; i < ip.getField().getVars().size(); i++) {
                        s1 = ip.getField().getVars().get(i).toString();
                        assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
                                + " TEMP" + Integer.toString(relNumber) + "["
                                + Integer.toString(i) + "] = "
                                + getObject(ip.getObject()) + s1 + ";\n";
                    }
                    assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB + op
                            + " = " + " TEMP" + Integer.toString(relNumber);
                    // RelType.auxVarCounter++;
                    return assigns;
                }
            }

            Var var;
            String m = new String(method + " ");
            String[] parts = m.split("=");
            String leftside = parts[0]+" ";
            String rightside = parts[1];
            String left = "";
            String left2 = "";
            String right = "";
            ArrayList<AjutHack> ajut = new ArrayList<AjutHack>();
            for (int i = 0; i < inputs.size(); i++) {
                var = inputs.get(i);
                pattern = Pattern.compile("([^a-zA-Z_])(([a-zA-Z_0-9]+\\.)*)?"
                        + var.getName() + "([^a-zA-Z0-9_])");
                matcher = pattern.matcher(rightside);

                if (matcher.find()) {
                    left = matcher.group(1);
                    left2 = matcher.group(2);
                    right = matcher.group(4);

                    ajut.add(new AjutHack(var.getName(), "#"
                            + Integer.toString(i)));
                    rightside = rightside.replaceFirst("([^a-zA-Z_]" + left2
                            + var.getName() + "[^a-zA-Z0-9_])", left
                            + getObject(var.getObject()) + "#"
                            + Integer.toString(i) + right);
                }

            }

            for (int i = 0; i < ajut.size(); i++) {
                AjutHack paar = ajut.get(i);
                rightside = rightside.replaceAll(paar.repl, paar.var);
            }

            left2 = "";
            var = outputs.get(0);
            pattern = Pattern.compile("([^a-zA-Z_]?)(([a-zA-Z_0-9]+\\.)*)"
                    + var.getName() + "([^a-zA-Z0-9_])");
            matcher = pattern.matcher(leftside);
            if (matcher.find()) {
                left = matcher.group(1);
                left2 = matcher.group(2);
                right = matcher.group(4);
            }

            leftside = leftside.replaceFirst("([^a-zA-Z_]?" + left2
                    + var.getName() + "[^a-zA-Z0-9_])", left
                    + getObject(var.getObject()) + var.getName() + right);

            if (outputs.get(0).getType().equals("int")
                    && (!getMaxType(inputs).equals("int") || method
                            .indexOf(".") >= 0)) {
                m = leftside + "= (int)(" + rightside + ")";
                // m = m.replaceFirst("=", "= (int)(") + ")";

            } else
                m = leftside + "=" + rightside;
            return m;
        } else if (type == RelType.TYPE_SUBTASK) {
            if ((outputs.size() == 1) && (inputs.size() == 1)) {
                return outputs.get(0) + " = " + inputs.get(0);
            }
            // this should not be used in code generation
            return inputs + " -> " + outputs;

        } else if (type == RelType.TYPE_METHOD_WITH_SUBTASK) {
            if (!outputs.isEmpty()) {
            	String output = getOutput();
            	String aout = checkAliasOutputs();
                return (checkAliasInputs() /* + outputAliasDeclar() */
                        + ( output.length() > 0 ? output + " = " : "" )  + getObject(object) + method + getSubtaskParameters())
                        + ";\n" + aout;
            }
            return (checkAliasInputs() + getObject(object) + method + getSubtaskParameters());
        } else {
            if (RuntimeProperties.isLogDebugEnabled())
                db.p(method);
            String s1, s2, assigns = "";
            Var ip = inputs.get(0);
            Var op = outputs.get(0);

            if (ip.getField().isArray() && op.getField().isAlias()) {

                for (int i = 0; i < outputs.get(0).getField().getVars().size(); i++) {
                    s1 = outputs.get(0).getField().getVars().get(i).toString();
                    assigns += "        " + getObject(op.getObject()) + s1
                            + " = " + ip + "[" + Integer.toString(i) + "];\n";
                }
                return assigns;
            }
            if (op.getField().isArray() && ip.getField().isAlias()) {
                for (int i = 0; i < ip.getField().getVars().size(); i++) {
                    s1 = ip.getField().getVars().get(i).toString();
                    assigns += CodeGenerator.OT_TAB + op + "["
                            + Integer.toString(i) + "] = "
                            + getObject(ip.getObject()) + s1 + ";\n";
                }
                return assigns;
            }
            if (op.getField().isAlias() && ip.getField().isAlias()) {
                for (int i = 0; i < ip.getField().getVars().size(); i++) {
                    s1 = ip.getField().getVars().get(i).toString();
                    s2 = op.getField().getVars().get(i).toString();

                    assigns += CodeGenerator.OT_TAB + getObject(op.getObject())
                            + s2 + " = " + getObject(ip.getObject()) + s1
                            + ";\n";
                }
                return assigns;
            }

            return op + " = " + ip;
        }
    }

    private String checkAliasInputs() {
        Var input;
        String assigns = "";
        String obj = getObject(object);
        for (int i = 0; i < inputs.size(); i++) {
            input = inputs.get(i);
            if (input.getField().isAlias()) {
            	Alias alias = (Alias)input.getField();
            	
            	String alias_tmp = getAliasTmpName( alias.getName() );
                
                if (alias.getVars().size() == 0) {
                    assigns = alias.getType() + " " + alias_tmp + " = null;\n";
                    assigns += CodeGenerator.getOffset();
                } else {

                    assigns += checkObjectArrayDimension( alias_tmp, 
                    				alias.getType(), 
                    				alias.getVars().size() );
                    String declarations = "";
                    String varList = "";
                    
                    for (int k = 0; k < input.getField().getVars().size(); k++) {
                    	ClassField field = input.getField().getVars().get(k);
                    	String varName;
                		
                		if ( field.isAlias() ) {
                			Alias aliasFromInput = (Alias)field;
                			String aliasTmpFromInput = getAliasTmpName(aliasFromInput.getName());
                	        
                	        declarations += CodeGenerator.getVarsToAlias( aliasFromInput, aliasTmpFromInput, obj );
                	        
                	        varName = aliasTmpFromInput;
                	        
                		} else {
                			varName = field.toString();
                		}
                		
                		varList += CodeGenerator.getOffset() + alias_tmp + "["
                                + Integer.toString(k) + "] = "
                                + obj + varName + ";\n";
                    }
                    assigns += declarations + varList + CodeGenerator.getOffset();
                }
            }
        }
        return assigns;
    }

    private String checkObjectArrayDimension(String name, String type, int size) {
        /*
         * if we have alias as a set of arrays, we should change the declaration
         * as follows: from double[][] tmp = new double[][2]; to double[][] tmp =
         * new double[2][];
         */
        if (type.endsWith("[][]")) {
            return type + " " + name + " = new "
                    + type.substring(0, type.length() - 4) + "[" + size
                    + "][];\n";
        }
        return type + " " + name + " = new " + type.substring(0, type.length() - 2) + "[" + size + "];\n";
    }

    private String checkAliasOutputs() {
        String assigns = "";
        Var output = outputs.get(0);
        if (output.getField().isAlias()) {
        	Alias alias = (Alias)output.getField();
        	
            String alias_tmp = getAliasTmpName( alias.getName() );
            
            if (alias.getVars().size() == 0) {
                assigns = CodeGenerator.getOffset() + alias.getType() + " " + alias_tmp + " = null;\n";
            } else {
            	String obj = getObject(object);
                for (int k = 0; k < alias.getVars().size(); k++) {
                	ClassField varFromAlias = alias.getVars().get(k);
                    
                	String varType = varFromAlias.getType();
            		TypeToken token = TypeToken.getTypeToken( varType );
            		
            		if ( token == TypeToken.TOKEN_OBJECT ) {
            			if( varFromAlias.isAlias() ) {
            				assigns += CodeGenerator.getVarsFromAlias( (Alias)varFromAlias, 
            						CodeGenerator.getAliasTmpName( varFromAlias.getName() ),
            						obj, alias_tmp, k );
            			} else {
            				assigns += CodeGenerator.getOffset() + varFromAlias + " = (" + varType + ")" 
            						+ alias_tmp + "[" + k + "];\n";
            			}
            		} else {
            			assigns += CodeGenerator.getOffset() //+ obj
            					+ varFromAlias + " = ((" + token.getObjType() + ")" 
            					+ alias_tmp + "[" + k + "])." + token.getMethod() + "();\n";
            		}
                    
                }
            }
            assigns += CodeGenerator.getOffset();
        }

        return assigns;
    }

    public boolean equals(Object e) {
        return this.relNumber == ((Rel) e).relNumber;
    }

    public int hashCode() {
        return RelType.REL_HASH + relNumber;
    }

    private class AjutHack {
        String var;

        String repl;

        private AjutHack(String name, String s) {
            var = name;
            repl = s;
        }
    }

}

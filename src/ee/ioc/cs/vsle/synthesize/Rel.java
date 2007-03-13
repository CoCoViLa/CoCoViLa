package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

class Rel implements Serializable {

    private List<Var> outputs = new ArrayList<Var>();
    private List<Var> inputs = new ArrayList<Var>();
    private List<SubtaskRel> subtasks = new ArrayList<SubtaskRel>();
    private ArrayList<Var> exceptions = new ArrayList<Var>();
    private int relID = 0;
    private Var parent;
    private String method;
    //declaration in specification
    private String declaration;
    //see RelType
    private int type;

    Rel( Var parent, String declaration ) {
        relID = RelType.relCounter++;
        this.parent = parent;
        this.declaration = declaration;
    }

    int getType() {
        return type;
    }

    void setType(int t) {
        type = t;
    }
    
    int getUnknownInputCount() {
        return unknownInputs.size();
    }

    void removeUnknownInput( Var var ) {
    	unknownInputs.remove( var );
    }

    Set<Var> unknownInputs = new LinkedHashSet<Var>();
    
    void addInput(Var var) {
        inputs.add( var );
        if( !var.getField().isConstant() ) {
        	unknownInputs.add( var );
        }
    }

    void addInputs( Collection<Var> vars ) {
    	for (Var var : vars) {
			addInput( var );
		}
    }
    
    /**
     * NB! inputs set cannot be managed outside, that is why have to return copy of this list
     * @return inputs copy
     */
    List<Var> getInputs() {
        return new ArrayList<Var>( inputs );
    }
    
    void addOutput(Var var) {
        outputs.add(var);
    }

    void addOutputs( Collection<Var> vars ) {
    	outputs.addAll( vars );
//    	for (Var var : vars) {
//			addOutput(var);
//		}
    }
    
    /**
     * NB! outputs set cannot be managed outside, that is why have to return copy of this list
     * @return outputs copy
     */
    List<Var> getOutputs() {
        return new ArrayList<Var>( outputs );
    }
    
    void addSubtask(SubtaskRel rel) {
        subtasks.add(rel);
    }
    
    boolean isUnknownInputsExist( Collection<Var> vars ) {
    	return vars.containsAll( inputs );
    }
    
    List<Var> getExceptions() {
        return exceptions;
    }

    List<SubtaskRel> getSubtasks() {
        return subtasks;
    }

    void setMethod(String m) {
        method = m;
    }

    String getMethod() {
        return method;
    }

    public boolean equals(Object e) {
        return this.relID == ((Rel) e).relID;
    }

    public int hashCode() {
        return RelType.REL_HASH + relID;
    }
    
    
    /**************** The code below is related to the code generation ************************************/
    
    String getMaxType(List<Var> inputs) {
        Var var;

        for (int i = 0; i < inputs.size(); i++) {
            var = inputs.get(i);
            if (!var.getType().equals(TYPE_INT)) {
                return TYPE_DOUBLE;
            }
        }
        return TYPE_INT;
    }

    private String getOutputString() {

        String outputString = "";
        Var var = outputs.get(0);

        if (!TypeUtil.TYPE_VOID.equals( var.getType() )) {
            if (var.getField().isAlias()) {
                String alias_tmp = getAliasTmpName( var );

                if (var.getChildVars().size() > 0) {

                    outputString = ((Alias)var.getField()).getType()
                            + " " + alias_tmp + " ";
                }

            } else {
                outputString = var.getFullName();
            }

        }
        return outputString;

    }

    private String getAliasTmpName( Var var ) {
    	String varName = var.getFullNameForConcat().replaceAll( "\\.", "_" );
        return TypeUtil.TYPE_ALIAS + "_" + varName + relID;
    }

    String getParametersString(boolean useBrackets) {
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
                    paramString = getAliasTmpName( var );
                } else {
                    paramString = var.getFullName();
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

    String getSubtaskParametersString() {
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
        params += getParametersString(false);
        return params += ")";
    }

    String getParentObjectName() {
    	return parent.getFullName();
    }
    
    public String toString() {
        Pattern pattern;
        Matcher matcher;

        if (type == RelType.TYPE_ALIAS) {
            return "";
        }
        else if (type == RelType.TYPE_EQUATION) {
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
                            + op.getFullName() + " = TEMP" + Integer.toString(RelType.auxVarCounter) + ";\n";
                    RelType.auxVarCounter++;
                    return assign;

                }
            }

            if (inputs.size() == 1 && outputs.size() == 1) {
                String s1, assigns = "";
                Var ip = inputs.get(0);
                Var op = outputs.get(0);

                if (ip.getField().isArray() && op.getField().isAlias()) {

                    for (int i = 0; i < outputs.get(0).getChildVars().size(); i++) {
                        s1 = op.getChildVars().get(i).toString();
                        assigns += s1 + " = " + ip.getFullName() + "[" + Integer.toString(i) + "];\n";
                    }
                    return assigns;
                }
                else if (op.getField().isArray() && ip.getField().isAlias()) {

                    assigns += op.getField().getType() + " TEMP"
                            + Integer.toString(relID) + " = new "
                            + op.getField().arrayType() + "["
                            + ip.getChildVars().size() + "];\n";
                    for (int i = 0; i < ip.getChildVars().size(); i++) {
                        s1 = ip.getChildVars().get(i).toString();
                        assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB
                                + " TEMP" + Integer.toString(relID) + "["
                                + Integer.toString(i) + "] = " + s1 + ";\n";
                    }
                    assigns += CodeGenerator.OT_TAB + CodeGenerator.OT_TAB + op.getFullName()
                            + " = " + " TEMP" + Integer.toString(relID);
                    // RelType.auxVarCounter++;
                    return assigns;
                }
                else if( method == null ) {
                	return op.getFullName() + " = " + ip.getFullName();
                }
            }

            
            Set<String> varNames = new LinkedHashSet<String>();
            for( Var out : getOutputs() ) {
            	varNames.add( out.getFullName() );
            }
            for( Var inps : getInputs() ) {
            	varNames.add( inps.getFullName() );
            }
            
            pattern = Pattern.compile("[^a-zA-Z_]*([a-zA-Z_]{1}[a-zA-Z_0-9\\.]*)");
            matcher = pattern.matcher( method );
            
            boolean methodCallExist = false;
            
            StringBuffer sb = new StringBuffer();
            //take each variable and replace it with the real instance name
            while( matcher.find() ) {
            	
            	String rep = parent.getFullNameForConcat() + matcher.group(1);
            	
            	if( !varNames.contains( rep ) ) {
            		rep = "$1";
            		methodCallExist = true;
            	}
            	
            	matcher.appendReplacement( sb, 
            			method.substring( matcher.start(), matcher.start(1) ) //
            			+ rep // 
            			+ method.substring( matcher.end(1), matcher.end() ) ); //
            }
            
            matcher.appendTail( sb );

            // TODO - add casting to other types as well
            if ( outputs.get(0).getType().equals(TYPE_INT)
                    && ( methodCallExist || !getMaxType(inputs).equals(TYPE_INT) ) ) {
            	
            	String[] eq = sb.toString().split( "=" );
            	return eq[0] + " = (" + TYPE_INT + ") " + eq[1];
            } 
            
            return sb.toString();
            
            /*
             * TODO - cleanup when the time comes (keep this just in case the code above starts to fail).
             * 
            Var var;
            String m = new String(method + " ");
            String[] parts = m.split("=");
            String leftside = parts[0]+" ";
            String rightside = parts[1];
            String left = "";
            String left2 = "";
            String right = "";
            
            //this collection maps tokens to real var names
            Map<String, String> map = new TreeMap<String, String>();
            
            String relParentFullName = parent.getFullNameForConcat();
            
            for (int i = 0; i < inputs.size(); i++) {
                var = inputs.get(i);
                
                String varName = var.getFullName();
                
                if( varName.startsWith( relParentFullName ) )
                {
                	varName = varName.substring( relParentFullName.length() );
                }
                
                pattern = Pattern.compile("([^a-zA-Z_])(([a-zA-Z_0-9]+\\.)*)?" + varName + "([^a-zA-Z0-9_])");
                matcher = pattern.matcher(rightside);

                map.put( "#" + i, var.getName() );
                
                while (matcher.find()) {
                    left = matcher.group(1);
                    left2 = matcher.group(2);
                    right = matcher.group(4);

                    if( left2.trim().equals( "" ) )
                    {
                    	rightside = rightside.replaceFirst("([^a-zA-Z_]" + left2
                    			+ varName + "[^a-zA-Z0-9_])", left
                    			+ var.getParent().getFullNameForConcat() + "#"
                    			+ Integer.toString(i) + right);
                    	
                    	matcher = pattern.matcher(rightside);
                    }
                }

            }

            for ( String key : map.keySet() ) {
            	rightside = rightside.replaceAll( key, map.get(key) );
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
                    + var.getName() + "[^a-zA-Z0-9_])", left + var.getFullName() + right );
                    
            //add casting to other types as well
            if ( outputs.get(0).getType().equals(TYPE_INT)
                    && ( !getMaxType(inputs).equals(TYPE_INT) || method.indexOf(".") >= 0 ) ) {
                m = leftside + "= (" + TYPE_INT + ")(" + rightside + ")";

            } else
                m = leftside + "=" + rightside;
            
            return m;*/
            
        } else if (type == RelType.TYPE_SUBTASK) {

            // this should not be used in code generation
            return inputs + " -> " + outputs;

        } else if ( ( type == RelType.TYPE_JAVAMETHOD )
        		|| ( type == RelType.TYPE_METHOD_WITH_SUBTASK )) {

        	String output = getOutputString();
        	String params = ( type == RelType.TYPE_JAVAMETHOD ) ? getParametersString(true) : getSubtaskParametersString();
        	return ( checkAliasInputs()
        			+ ( output.length() > 0 ? output + " = " : "" ) 
        			+ parent.getFullNameForConcat() + method + params )
        			+ ";\n" + checkAliasOutputs();

        } else {
            if (RuntimeProperties.isLogDebugEnabled())
                db.p(method);
            String s1, s2, assigns = "";
            Var ip = inputs.get(0);
            Var op = outputs.get(0);

            if (ip.getField().isArray() && op.getField().isAlias()) {

                for (int i = 0; i < outputs.get(0).getChildVars().size(); i++) {
                    s1 = outputs.get(0).getChildVars().get(i).toString();
                    assigns += CodeGenerator.OT_TAB + s1 
                            + " = " + ip.getFullName() + "[" + Integer.toString(i) + "];\n";
                }
                return assigns;
            }
            if (op.getField().isArray() && ip.getField().isAlias()) {
                for (int i = 0; i < ip.getChildVars().size(); i++) {
                    s1 = ip.getChildVars().get(i).toString();
                    assigns += CodeGenerator.OT_TAB + op.getFullName() + "["
                            + Integer.toString(i) + "] = " + s1 + ";\n";
                }
                return assigns;
            }
            if (op.getField().isAlias() && ip.getField().isAlias()) {
                for (int i = 0; i < ip.getChildVars().size(); i++) {
                    s1 = ip.getChildVars().get(i).toString();
                    s2 = op.getChildVars().get(i).toString();

                    assigns += CodeGenerator.OT_TAB + s2 + " = " + s1 + ";\n";
                }
                return assigns;
            }

            return op.getFullName() + " = " + ip.getFullName();
        }
    }

    private String checkAliasInputs() {
        Var input;
        String assigns = "";
        for (int i = 0; i < inputs.size(); i++) {
            input = inputs.get(i);
            if (input.getField().isAlias()) {
            	
            	String alias_tmp = getAliasTmpName( input );
                
                if (input.getChildVars().size() == 0) {
                    assigns = input.getType() + " " + alias_tmp + " = null;\n";
                    assigns += CodeGenerator.getOffset();
                } else {

                    assigns += checkObjectArrayDimension( alias_tmp, 
                    		input.getType(), 
                    		input.getChildVars().size() );
                    String declarations = "";
                    String varList = "";
                    
                    for (int k = 0; k < input.getChildVars().size(); k++) {
                    	Var var = input.getChildVars().get(k);
                    	String varName;
                		
                		if ( var.getField().isAlias() ) {
                			String aliasTmpFromInput = getAliasTmpName( var );
                	        
                	        declarations += CodeGenerator.getVarsToAlias( var, aliasTmpFromInput );
                	        
                	        varName = aliasTmpFromInput;
                	        
                		} else {
                			varName = var.getFullName();
                		}
                		
                		varList += CodeGenerator.getOffset() + alias_tmp + "["
                                + Integer.toString(k) + "] = "
                                + varName + ";\n";
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
        	
            String alias_tmp = getAliasTmpName( output );
            
            if (output.getChildVars().size() == 0) {
                assigns = CodeGenerator.getOffset() + output.getType() + " " + alias_tmp + " = null;\n";
            } else {
                for (int k = 0; k < output.getChildVars().size(); k++) {
                	Var varFromAlias = output.getChildVars().get(k);
                    
                	String varType = varFromAlias.getType();
            		TypeToken token = TypeToken.getTypeToken( varType );
            		
            		if ( token == TypeToken.TOKEN_OBJECT ) {
            			if( varFromAlias.getField().isAlias() ) {
            				assigns += CodeGenerator.getVarsFromAlias( varFromAlias, 
            						CodeGenerator.getAliasTmpName( varFromAlias.getName() ),
            						alias_tmp, k );
            			} else {
            				assigns += CodeGenerator.getOffset() + varFromAlias + " = (" + varType + ")" 
            						+ alias_tmp + "[" + k + "];\n";
            			}
            		} else {
            			assigns += CodeGenerator.getOffset()
            					+ varFromAlias.getFullName() + " = ((" + token.getObjType() + ")" 
            					+ alias_tmp + "[" + k + "])." + token.getMethod() + "();\n";
            		}
                    
                }
            }
            assigns += CodeGenerator.getOffset();
        }

        return assigns;
    }

	public String getDeclaration() {
		return declaration;
	}
}

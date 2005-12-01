package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.ClassField;

public class CodeGenerator {

    private static String offset = "";

    public static final String OT_TAB = "        ";

    private static final TypeToken TOKEN_INT = new TypeToken( "int", "Integer", "intValue" );
    private static final TypeToken TOKEN_DOUBLE = new TypeToken( "double", "Double", "doubleValue" );
    private static final TypeToken TOKEN_FLOAT = new TypeToken( "float", "Float", "floatValue" );
    private static final TypeToken TOKEN_CHAR = new TypeToken( "char", "Character", "charValue" );
    private static final TypeToken TOKEN_BYTE = new TypeToken( "byte", "Byte", "byteValue" );
    private static final TypeToken TOKEN_SHORT = new TypeToken( "short", "Short", "shortValue" );
    private static final TypeToken TOKEN_LONG = new TypeToken( "long", "Long", "longValue" );
    private static final TypeToken TOKEN_BOOLEAN = new TypeToken( "boolean", "Boolean", "booleanValue" );
    private static final TypeToken TOKEN_OBJECT = new TypeToken( null, "", "" );

    private final static int OT_NOC = 0;
    private final static int OT_INC = 1;
    private final static int OT_DEC = 2;

    private static int subCount = 0;

    public static final String ALIASTMP = "alias";
    public static int ALIASTMP_NR = 0;

    private static CodeGenerator s_codeGen = null;

    private CodeGenerator() {}

    public static CodeGenerator getInstance() {
        if ( s_codeGen == null ) {
            s_codeGen = new CodeGenerator();
        }
        offset = "";
        subCount = 0;
        ALIASTMP_NR = 0;
        return s_codeGen;
    }

    public String generate( ArrayList algRelList ) {
        StringBuffer alg = new StringBuffer();
        cOT( OT_INC, 2 );

        for ( int i = 0; i < algRelList.size(); i++ ) {
            Rel rel = ( Rel ) algRelList.get( i );

            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( cOT( OT_NOC, 0 ), rel, alg );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                genSubTasks( rel, alg, false );
            }

        }
        return alg.toString();
    }

    private void genSubTasks( Rel rel, StringBuffer alg, boolean isNestedSubtask ) {
        int subNum;
        int start = subCount;

        for ( Rel subtask : rel.getSubtasks() ) {
            subNum = subCount++;

            alg.append( cOT( OT_NOC, 0 ) + "class " + Synthesizer.SUBTASK_INTERFACE_NAME + "_" + subNum
                        + " implements " + Synthesizer.SUBTASK_INTERFACE_NAME + " {\n" );
            alg.append( cOT( OT_INC, 1 ) + "public Object[] run(Object[] in) throws Exception {\n" );

            List<Var> subInputs = subtask.getInputs();
            List<Var> subOutputs = subtask.getOutputs();
            List<Rel> subAlg = subtask.getAlgorithm();
            cOT( OT_INC, 1 );
            alg.append( cOT( OT_NOC, 0 ) + "//Subtask: " + subtask + "\n" );
            // apend subtask inputs to algorithm
            alg.append( getSubtaskInputs( subInputs, cOT( OT_NOC, 0 ) ) );
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = subAlg.get( i );
                if (RuntimeProperties.isDebugEnabled())
					db.p( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs());
                if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                    //recursion
                    genSubTasks( trel, alg, true );

                } else {
                    appendRelToAlg( cOT( OT_NOC, 0 ), trel, alg );
                }

            }
            // apend subtask outputs to algorithm
            alg.append( getSubtaskOutputs( subOutputs, cOT( OT_NOC, 0 ) ) );
            alg.append( cOT( OT_DEC, 1 ) + "}\n"
                        + cOT( OT_DEC, 1 ) + "} //End of subtask: " + subtask + "\n" );

            alg.append( cOT( OT_NOC, 0 ) + "Subtask_" + subNum + " subtask_" + subNum +
                        " = new Subtask_" + subNum + "();\n" );
        }

        appendSubtaskRelToAlg( rel, start, alg, isNestedSubtask );
    }


    private String cOT( int incr, int times ) {
        //0 - no change, 1 - increase, 2 - decrease
        if ( incr == OT_INC ) {
            for ( int i = 0; i < times; i++ ) {
                offset += OT_TAB;
            }
            return offset;
        } else if ( incr == OT_DEC ) {
            for ( int i = 0; i < times; i++ ) {
                offset = offset.substring( OT_TAB.length() );
            }
            return offset;
        } else
            return offset;

    }

    private void appendRelToAlg( String offset, Rel rel, StringBuffer buf ) {
        String s = rel.toString();
        if ( !s.equals( "" ) ) {
            if(rel.getExceptions().size() == 0 ) {
                buf.append( offset + s + ";\n" );
            }
            else {
                buf.append( appendExceptions( rel, s ) );
            }
        }
    }

    private void appendSubtaskRelToAlg( Rel rel, int startInd, StringBuffer buf, boolean isNestedSubtask ) {

        String relString = rel.toString();
        for ( int i = 0; i < rel.getSubtasks().size(); i++ ) {
            relString = relString.replaceFirst( RelType.TAG_SUBTASK, "subtask_" + ( startInd + i ) );
        }
        if(rel.getExceptions().size() == 0 || isNestedSubtask ) {
            buf.append(cOT( OT_NOC, 0 ) + relString + ";\n" );
        }
        else {
            buf.append( appendExceptions( rel, relString ) );
        }
    }

    private String appendExceptions( Rel rel, String s ) {
        StringBuffer buf = new StringBuffer();
        buf.append( cOT( OT_NOC, 0 ) + "try {\n" +
                    cOT( OT_INC, 1 ) + s + ";\n" +
                    cOT( OT_DEC, 1 ) + "}\n" );
        for ( int i = 0; i < rel.getExceptions().size(); i++ ) {
            String excp = rel.getExceptions().get( i ).getName();
            String instanceName = "ex" + i;
            buf.append( cOT( OT_NOC, 0 ) + "catch( " + excp + " " + instanceName + " ) {\n" +
                        cOT( OT_INC, 1 ) + instanceName + ".printStackTrace();\n" +
                        cOT( OT_NOC, 0 ) + "return;\n" +
                        cOT( OT_DEC, 1 ) + "}\n" );
        }

        return buf.toString();
    }

    private String getSubtaskInputs( List<Var> vars, String offset ) {
    	
    	String result = "";
    	
    	for ( int i = 0; i < vars.size(); i++ ) {
    		Var var = vars.get( i );
    		
    		if ( var.getField().isAlias() ) {
    			result += getAliasSubtaskInput( var, offset, i);
    			continue;
    		}
    		
    		String varType = var.getType();
    		TypeToken token = getTypeToken( varType );
    		
    		result += offset;
    		
    		if ( token == TOKEN_OBJECT ) {
    			result += var.toString() + " = (" + varType + ")in[" + i + "];\n";
    		} else {
    			result += var.toString()
    			+ " = ((" + token.getObjType() + ")in[" + i + "])."
    			+ token.getMethod() + "();\n";
    		}
    		
    	}
    	
    	return result;
    }

    private String getSubtaskOutputs( List<Var> vars, String offset ) {
    	
    	String declarations = "";
    	String varList = "";
    	String result = offset + "return new Object[]{ ";
    	
    	for ( int i = 0; i < vars.size(); i++ ) {
    		Var var = vars.get( i );
    		
    		String varName;
    		
    		if ( var.getField().isAlias() ) {
    			Alias alias = (Alias)var.getField();

    	        String aliasTmp = ALIASTMP + "_" + alias.getName() + "_" + ALIASTMP_NR++;
    	        String aliasType = alias.getRealType();
    	        String out = offset + aliasType + " " + aliasTmp + " = new " + aliasType + "{ ";

    	        ClassField field;
    	        for ( int j = 0; j < alias.getVars().size(); j++ ) {
    	        	field = alias.getVars().get( j );
    	            if ( j == 0 ) {
    	                out += Rel.getObject(var.getObject()) + field.getName();
    	            } else {
    	                out += ", " + Rel.getObject(var.getObject()) + field.getName();
    	            }
    	        }
    	        out += " };\n";
    	        
    	        declarations += out;
    	        
    	        varName = aliasTmp;
    	        
    		} else {
    			varName = var.toString();
    		}
    		
    		if( i == 0 ) {
    			varList += varName;
    		} else {
    			varList += ", " + varName;
    		}

    	}
    	
    	return declarations + result + varList + " };\n";
    }

    private TypeToken getTypeToken( String varType ) {
    	
    	TypeToken token;
    	
    	if ( varType.equals( TOKEN_INT.getType() ) ) {
			token = TOKEN_INT;
		} else if ( varType.equals( TOKEN_DOUBLE.getType() ) ) {
			token = TOKEN_DOUBLE;
		} else if ( varType.equals( TOKEN_FLOAT.getType() ) ) {
			token = TOKEN_FLOAT;
		} else if ( varType.equals( TOKEN_CHAR.getType() ) ) {
			token = TOKEN_CHAR;
		} else if ( varType.equals( TOKEN_BYTE.getType() ) ) {
			token = TOKEN_BYTE;
		} else if ( varType.equals( TOKEN_SHORT.getType() ) ) {
			token = TOKEN_SHORT;
		} else if ( varType.equals( TOKEN_LONG.getType() ) ) {
			token = TOKEN_LONG;
		} else if ( varType.equals( TOKEN_BOOLEAN.getType() ) ) {
			token = TOKEN_BOOLEAN;
		} else {
			token = TOKEN_OBJECT;
		}
    	
    	return token;
    }
    
    private String getAliasSubtaskInput( Var input, String offset, int num ) {
        Alias alias = (Alias)input.getField();
        String aliasType = alias.getRealType();
        String aliasTmp = ALIASTMP + "_" + alias.getName() + "_" + ALIASTMP_NR++;
        String out = offset + aliasType + " " + aliasTmp + " = (" + aliasType
                     + ")in[" + num + "];\n";

        ClassField var;

        for ( int i = 0; i < alias.getVars().size(); i++ ) {
            var = alias.getVars().get( i );
            out += offset + Rel.getObject(input.getObject()) + var.getName() + " = " + aliasTmp + "[" + i + "];\n";
        }
        return out;
    }

    private static class TypeToken {

        String m_type;
        String m_objType;
        String m_method;

        private TypeToken( String type, String objType, String method ) {
            m_type = type;
            m_objType = objType;
            m_method = method;
        }

        String getType() {
            return m_type;
        }

        String getObjType() {
            return m_objType;
        }

        String getMethod() {
            return m_method;
        }
    }

	public static String getOffset() {
		return offset;
	}
}

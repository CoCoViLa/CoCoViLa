package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.ClassField;

public class CodeGenerator {

    private static String offset = "";

    public static final String OT_TAB = "    ";

    private final static int OT_NOC = 0;
    private final static int OT_INC = 1;
    private final static int OT_DEC = 2;

    private static int subCount = 0;

    public static int ALIASTMP_NR = 0;

    private static CodeGenerator s_codeGen = null;

    private CodeGenerator() {}

    public static CodeGenerator getInstance() {
        if ( s_codeGen == null ) {
            s_codeGen = new CodeGenerator();
        }
        
        return s_codeGen;
    }

    public static void reset() {
    	offset = "";
        subCount = 0;
        ALIASTMP_NR = 0;
    }
    
    public String generate( ArrayList algRelList, List<Var> assumptions ) {
        StringBuffer alg = new StringBuffer();
        cOT( OT_INC, 2 );

        genAssumptions( alg, assumptions );
        
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

    private void genAssumptions( StringBuffer alg, List<Var> assumptions ) {
    	if( assumptions.isEmpty() ) {
    		return;
    	}
    	
    	String result = "";
    	int i = 0;
    	for (Var var : assumptions) {
    		
    		String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		result += offset;
    		
    		if ( token == TypeToken.TOKEN_OBJECT ) {
    			result += var.toString() + " = (" + varType + ")args[" + i++ + "];\n";
    		} else {
    			result += var.toString()
    			+ " = ((" + token.getObjType() + ")args[" + i++ + "])."
    			+ token.getMethod() + "();\n";
    		}
		}
    	
    	alg.append( result );
    }
    
    private void genSubTasks( Rel rel, StringBuffer alg, boolean isNestedSubtask ) {
        int subNum;
        int start = subCount;

        for ( Rel subtask : rel.getSubtasks() ) {
            subNum = subCount++;

            alg.append( "\n" + cOT( OT_NOC, 0 ) + "class " + Synthesizer.SUBTASK_INTERFACE_NAME + "_" + subNum
                        + " implements " + Synthesizer.SUBTASK_INTERFACE_NAME + " {\n" );
            alg.append( cOT( OT_INC, 1 ) + "public Object[] run(Object[] in) throws Exception {\n" );

            List<Var> subInputs = subtask.getInputs();
            List<Var> subOutputs = subtask.getOutputs();
            List<Rel> subAlg = subtask.getAlgorithm();
            cOT( OT_INC, 1 );
            alg.append( cOT( OT_NOC, 0 ) + "//Subtask: " + subtask + "\n" );
            // apend subtask inputs to algorithm
            alg.append( getSubtaskInputs( subInputs ) );
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = subAlg.get( i );
                if (RuntimeProperties.isLogDebugEnabled())
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
                        " = new Subtask_" + subNum + "();\n\n" );
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
            buf.append(cOT( OT_NOC, 0 ) + relString + "\n" );
        }
        else {
            buf.append( appendExceptions( rel, relString ) + "\n" );
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

    private String getSubtaskInputs( List<Var> vars ) {
    	
    	String result = "";
    	
    	for ( int i = 0; i < vars.size(); i++ ) {
    		Var var = vars.get( i );
    		String object = Rel.getObject(var.getObject());
    		if ( var.getField().isAlias() ) {
    			String aliasTmp = getAliasTmpName(var.getName());
    			result += getVarsFromAlias( (Alias)var.getField(), aliasTmp, object, "in", i);
    			continue;
    		}
    		
    		String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		result += offset;
    		
    		if ( token == TypeToken.TOKEN_OBJECT ) {
    			result += //object + 
    			var.toString() + " = (" + varType + ")in[" + i + "];\n";
    		} else {
    			result += //object + 
    			var.toString()
    			+ " = ((" + token.getObjType() + ")in[" + i + "])."
    			+ token.getMethod() + "();\n";
    		}
    		System.err.println( "si: " + object + "|" + var.toString() );
    	}
    	
    	return result + "\n";
    }

    //getAliasSubtaskInput
    public static String getVarsFromAlias( Alias alias, String aliasTmp, String object, String parentVar, int num ) {
        String aliasType = alias.getType();
        
        String out = offset + aliasType + " " + aliasTmp + " = (" + aliasType
                     + ")" + parentVar + "[" + num + "];\n";

        ClassField var;

        for ( int i = 0; i < alias.getVars().size(); i++ ) {
            var = alias.getVars().get( i );
            //out += offset + object + var.getName() + " = " + aliasTmp + "[" + i + "];\n";
            
            String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		if( var.isAlias() ) {
    			//recursion
    			String tmp = getAliasTmpName(var.getName());
				out += getVarsFromAlias( (Alias)var, tmp, object, aliasTmp, i );
				
			} else if ( token == TypeToken.TOKEN_OBJECT ) {
				
    			out += offset + object
    					+ var + " = (" + varType + ")" + aliasTmp + "[" + i + "];\n";
    		} else {
    			out += offset + object
    					+ var + " = ((" + token.getObjType() + ")" 
    					+ aliasTmp + "[" + i + "])." + token.getMethod() + "();\n";
    		}
    		if( var.toString().equals("Source_6.Source_4.state_c"))
    		{
    			System.err.println( "fa: " );
    		}
    		System.err.println( "fa: " + object + "|" + var );
        }
        return out;
    }
    
    private String getSubtaskOutputs( List<Var> vars, String offset ) {
    	
    	String declarations = "\n";
    	String varList = "";
    	String result = offset + "return new Object[]{ ";
    	
    	for ( int i = 0; i < vars.size(); i++ ) {
    		Var var = vars.get( i );
    		
    		String varName;
    		
    		if ( var.getField().isAlias() ) {
    			Alias alias = (Alias)var.getField();
    			String aliasTmp = getAliasTmpName(alias.getName());
    	        
    	        declarations += getVarsToAlias( alias, aliasTmp, Rel.getObject(var.getObject()) );
    	        
    	        varName = aliasTmp;
    	        
    		} else {
    			varName = var.toString();
    		}
    		System.err.println( "so: " + "|" + varName );
    		if( i == 0 ) {
    			varList += varName;
    		} else {
    			varList += ", " + varName;
    		}

    	}
    	
    	return declarations + result + varList + " };\n";
    }

    //getAliasSubtaskOutput
    public static String getVarsToAlias( Alias alias, String aliasTmp, String object ) {
    	
        String aliasType = alias.getType();
        String before = "";
        String out = offset + aliasType + " " + aliasTmp + " = new " + aliasType + "{ ";

        ClassField field;
        for ( int j = 0; j < alias.getVars().size(); j++ ) {
        	field = alias.getVars().get( j );
        	String varName;
        	if( field.isAlias() ) {
        		varName = getAliasTmpName(alias.getName());
        		before += getVarsToAlias( (Alias)field, varName, object );
        	} else {
        		varName = //object + 
        			field.getName();
        	}
        	System.err.println( "ta: " + object + "|" + field.getName() );
            if ( j == 0 ) {
                out += varName;
            } else {
                out += ", " + varName;
            }
        }
        out += " };\n";
        
        return before + out;
    }
    
	public static String getOffset() {
		return offset;
	}
	
	public static String getAliasTmpName(String varName) {
		varName = varName.replaceAll( "\\.", "_" );
        return TypeUtil.TYPE_ALIAS + "_" + varName + "_" + ALIASTMP_NR++;
    }
}

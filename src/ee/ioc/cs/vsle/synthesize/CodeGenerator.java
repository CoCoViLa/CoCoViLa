package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class CodeGenerator {

    private static String offset = "";

    public static final String OT_TAB = "    ";

    private static enum OFFSET { OT_INC, OT_DEC }

    private int subCount = 0;

    public static int ALIASTMP_NR;

    private ArrayList<Rel> algRelList;
    private Problem problem;
    private String className; 
    
    public CodeGenerator( ArrayList<Rel> algRelList, Problem problem, String className ) {
    	
    	this.algRelList = algRelList;
    	this.problem = problem;
    	this.className = className;
    	
    	ALIASTMP_NR = 0;
    	offset = "";
    }
    
    public String generate() {
    	
    	db.p( "Starting code generation" );
    	
        StringBuffer alg = new StringBuffer();
        cOT( OFFSET.OT_INC, 2 );

        genAssumptions( alg, problem.getAssumptions() );
        
        for ( Rel rel : algRelList ) {

            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( same(), rel, alg );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                genSubTasks( rel, alg, false, className, new HashSet<Var>() );
            }

        }
        
        db.p( "Finished code generation" );
        
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
    			result += var.getFullName() + " = (" + varType + ")args[" + i++ + "];\n";
    		} else {
    			result += var.getFullName()
    			+ " = ((" + token.getObjType() + ")args[" + i++ + "])."
    			+ token.getMethod() + "();\n";
    		}
		}
    	
    	alg.append( result );
    }
    
    private void genSubTasks( Rel rel, StringBuffer alg, boolean isNestedSubtask, String parentClassName, Set<Var> usedVars ) {
        int subNum;
        int start = subCount;

        for ( SubtaskRel subtask : rel.getSubtasks() ) {
            subNum = subCount++;
            
            String sbName = Synthesizer.SUBTASK_INTERFACE_NAME + "_" + subNum;
            
            alg.append( "\n" + same() + "class " + sbName
                        + " implements " + Synthesizer.SUBTASK_INTERFACE_NAME + " {\n\n" );
            
            right() ;
            
            //start generating run()
            alg.append( same() + "public Object[] run(Object[] in) throws Exception {\n" );

            List<Var> subInputs = subtask.getInputs();
            List<Var> subOutputs = subtask.getOutputs();
            
            usedVars.addAll( subInputs );
            usedVars.addAll( subOutputs );
            
            List<Rel> subAlg = subtask.getAlgorithm();
            right() ;
            alg.append( same() + "//Subtask: " + subtask + "\n" );
            // apend subtask inputs to algorithm
            alg.append( getSubtaskInputs( subInputs ) );
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = subAlg.get( i );
                if (RuntimeProperties.isLogDebugEnabled())
					db.p( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs());
                if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                    //recursion
                    genSubTasks( trel, alg, true, sbName, usedVars );

                } else {
                    appendRelToAlg( same(), trel, alg );
                }
                usedVars.addAll( trel.getInputs() );
                usedVars.addAll( trel.getOutputs() );
            }
            // apend subtask outputs to algorithm
            alg.append( getSubtaskOutputs( subOutputs, same() ) );
            //end of run()
            alg.append( left() + "}\n" );
            
            //variable declaration & constructor
            //alg.append( generateFieldDeclaration( parentClassName, sbName, usedVars ) );
            //end of class
            alg.append( left() + "} //End of subtask: " + subtask + "\n" );

            alg.append( same() + "Subtask_" + subNum + " subtask_" + subNum +
                        " = new Subtask_" + subNum + "();\n\n" );
        }

        appendSubtaskRelToAlg( rel, start, alg, isNestedSubtask );
    }

    private String generateFieldDeclaration( String parentClassName, String sbName, Set<Var> usedVars ) {
    	
    	String declOT = same();
    	String consOT = right();
    	StringBuffer bufDecl = new StringBuffer();
    	StringBuffer bufConstr = new StringBuffer();
    	Set<String> topVars = new HashSet<String>();
    	
    	for ( Var var : usedVars ) {
    		
    		boolean allow = !var.getField().isAlias() && !var.getField().isConstant() && !var.getField().isVoid();
    		
    		if( var.getParent().equals( problem.getRootVar() ) ) {
    			if( allow ) {
    				bufDecl.append( declOT + var.getDeclaration() );
    				bufConstr.append( consOT + var.getName() + " = " + parentClassName + "." + TYPE_THIS + "." + var.getName() + ";\n" );
    			}
    		} else {
    			Var parent = var.getParent();

    			while( !parent.getParent().equals( problem.getRootVar() ) ) {
    				parent = parent.getParent();
    			}
    			if( !topVars.contains( parent.getFullName() ) ) {
    				topVars.add( parent.getFullName() );
    				bufDecl.append( declOT + parent.getDeclaration() );
    			}
    			if( allow ) {
    				bufConstr.append( consOT + var.getFullName() + " = " + parentClassName 
    						+ "." + TYPE_THIS + "." + var.getFullName() + ";\n" );
    			}
    		}
    	}
        
        return "\n" + bufDecl.toString() + "\n"
        		+ left() + sbName + "() {\n\n"
        		+ bufConstr.toString()
        		+ same() + "}\n\n";
    }
    
    private String cOT( OFFSET ot, int times ) {
    	
    	switch( ot )
    	{
    	case OT_INC :
    		for ( int i = 0; i < times; i++ ) {
                offset += OT_TAB;
            }
    		break;
    	case OT_DEC :
    		for ( int i = 0; i < times; i++ ) {
                offset = offset.substring( OT_TAB.length() );
            }
    		break;

    	}
    	
    	return offset;
    }

    private String left() {
    	return cOT( OFFSET.OT_DEC, 1 );
    }
    
    private String right() {
    	return cOT( OFFSET.OT_INC, 1 );
    }
    
    private String same() {
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
            buf.append(same() + relString + "\n" );
        }
        else {
            buf.append( appendExceptions( rel, relString ) + "\n" );
        }
    }

    private String appendExceptions( Rel rel, String s ) {
        StringBuffer buf = new StringBuffer();
        buf.append( same() + "try {\n" +
                    right()  + s + ";\n" +
                    left() + "}\n" );
        for ( int i = 0; i < rel.getExceptions().size(); i++ ) {
            String excp = rel.getExceptions().get( i ).getName();
            String instanceName = "ex" + i;
            buf.append( same() + "catch( " + excp + " " + instanceName + " ) {\n" +
                        right()  + instanceName + ".printStackTrace();\n" +
                        same() + "return;\n" +
                        left() + "}\n" );
        }

        return buf.toString();
    }

    private String getSubtaskInputs( List<Var> vars ) {
    	
    	String result = "";
    	
    	for ( int i = 0; i < vars.size(); i++ ) {
    		Var var = vars.get( i );
    		if ( var.getField().isAlias() ) {
    			String aliasTmp = getAliasTmpName(var.getName());
    			result += getVarsFromAlias( var, aliasTmp, "in", i);
    			continue;
    		}
    		
    		String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		result += offset;
    		
    		if ( token == TypeToken.TOKEN_OBJECT ) {
    			result += var.getFullName() + " = (" + varType + ")in[" + i + "];\n";
    		} else {
    			result += var.getFullName() + " = ((" + token.getObjType() + ")in[" + i + "])." + token.getMethod() + "();\n";
    		}
    	}
    	
    	return result + "\n";
    }

    //getAliasSubtaskInput
    public static String getVarsFromAlias( Var aliasVar, String aliasTmp, String parentVar, int num ) {
        String aliasType = aliasVar.getType();
        
        String out = offset + aliasType + " " + aliasTmp + " = (" + aliasType
                     + ")" + parentVar + "[" + num + "];\n";

        Var var;

        for ( int i = 0; i < aliasVar.getChildVars().size(); i++ ) {
            var = aliasVar.getChildVars().get( i );
            
            String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		if( var.getField().isAlias() ) {
    			//recursion
    			String tmp = getAliasTmpName(var.getName());
				out += getVarsFromAlias( var, tmp, aliasTmp, i );
				
			} else if ( token == TypeToken.TOKEN_OBJECT ) {
				
    			out += offset + var.getFullName() + " = (" + varType + ")" + aliasTmp + "[" + i + "];\n";
    		} else {
    			out += offset + var.getFullName() + " = ((" + token.getObjType() + ")" 
    					+ aliasTmp + "[" + i + "])." + token.getMethod() + "();\n";
    		}

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
    			String aliasTmp = getAliasTmpName( var.getName() );
    	        
    	        declarations += getVarsToAlias( var, aliasTmp );
    	        
    	        varName = aliasTmp;
    	        
    		} else {
    			varName = var.getFullName();
    		}
    		if( i == 0 ) {
    			varList += varName;
    		} else {
    			varList += ", " + varName;
    		}

    	}
    	
    	return declarations + result + varList + " };\n";
    }

    //getAliasSubtaskOutput
    public static String getVarsToAlias( Var aliasVar, String aliasTmp ) {
    	
        String aliasType = aliasVar.getType();
        String before = "";
        String out = offset + aliasType + " " + aliasTmp + " = new " + aliasType + "{ ";

        Var var;
        for ( int j = 0; j < aliasVar.getChildVars().size(); j++ ) {
        	var = aliasVar.getChildVars().get( j );
        	String varName;
        	if( var.getField().isAlias() ) {
        		varName = getAliasTmpName(aliasVar.getName());
        		before += getVarsToAlias( var, varName );
        	} else {
        		varName = var.getFullName();
        	}
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
	
	public static String getAliasTmpName( String varName ) {
		varName = varName.replaceAll( "\\.", "_" );
        return TypeUtil.TYPE_ALIAS + "_" + varName + "_" + ALIASTMP_NR++;
    }
}

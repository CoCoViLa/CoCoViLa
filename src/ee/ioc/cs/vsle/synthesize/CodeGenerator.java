package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;
import static ee.ioc.cs.vsle.synthesize.Synthesizer.*;

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
    	long start = System.currentTimeMillis();
    	
        StringBuilder alg = new StringBuilder();
        cOT( OFFSET.OT_INC, 2 );

        genAssumptions( alg, problem.getAssumptions() );
        
        for ( Rel rel : algRelList ) {

            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( same(), rel, alg );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
            	
            	Set<Var> usedVars = new HashSet<Var>();
            	
            	addVarsToSet( rel.getInputs(), usedVars );
            	addVarsToSet( rel.getOutputs(), usedVars );
            	
                genSubTasks( rel, alg, false, className, usedVars, problem );
            }

        }
        
        db.p( "Finished code generation in " + ( System.currentTimeMillis() - start ) + "ms" );
        
        return alg.toString();
    }

    private void genAssumptions( StringBuilder alg, List<Var> assumptions ) {
    	if( assumptions.isEmpty() ) {
    		return;
    	}
    	
    	StringBuilder result = new StringBuilder( "" );
    	int i = 0;
    	for (Var var : assumptions) {
    		
    		String varType = var.getType();
    		TypeToken token = TypeToken.getTypeToken( varType );
    		
    		result.append(offset);

			if (token == TypeToken.TOKEN_OBJECT) {
				result.append(var.getFullName());
				result.append(" = (");
				result.append(varType);
				result.append(")args[");
				result.append(i++);
				result.append("];\n");
			} else {
				result.append(var.getFullName());
				result.append(" = ((");
				result.append(token.getObjType());
				result.append(")args[");
				result.append(i++);
				result.append("]).");
				result.append(token.getMethod());
				result.append("();\n");
			}
		}
    	
    	alg.append( result );
    }
    
    private void genSubTasks( Rel rel, StringBuilder alg, boolean isNestedSubtask, String parentClassName, 
    		Set<Var> usedVars, Problem problem ) {
        int subNum;
        int start = subCount;

        for ( SubtaskRel subtask : rel.getSubtasks() ) {
        	
        	Problem currentProblem = subtask.isIndependent() ? subtask.getContext() : problem;
        	Set<Var> currentUsedVars = subtask.isIndependent() ? new HashSet<Var>() : usedVars;
        	
            subNum = subCount++;
            
            String sbName = ( subtask.isIndependent() ? "Independent" : "" ) + SUBTASK_INTERFACE_NAME + "_" + subNum;
            
            StringBuilder bufSbtClass = new StringBuilder();
            
            bufSbtClass.append( "\n");
            bufSbtClass.append( same() );
            bufSbtClass.append( "class " );
            bufSbtClass.append( sbName );
            bufSbtClass.append( " implements " );
            bufSbtClass.append( SUBTASK_INTERFACE_NAME );
            bufSbtClass.append( " {\n\n" );
            
            right() ;
            
            //start generating run()
            
            StringBuilder bufSbtBody = new StringBuilder();
            
            bufSbtBody.append( same() );
            bufSbtBody.append( getRunMethodSignature() ).append( " {\n" );
            
            List<Var> subInputs = subtask.getInputs();
            List<Var> subOutputs = subtask.getOutputs();
            
            addVarsToSet( subInputs, currentUsedVars );
            addVarsToSet( subOutputs, currentUsedVars );
            
            List<Rel> subAlg = subtask.getAlgorithm();
            right() ;
            bufSbtBody.append( same() );
            bufSbtBody.append( "//Subtask: " );
            bufSbtBody.append( subtask );
            bufSbtBody.append( "\n" );
            // apend subtask inputs to algorithm
            bufSbtBody.append( getSubtaskInputs( subInputs ) );
            for ( int i = 0; i < subAlg.size(); i++ ) {
                Rel trel = subAlg.get( i );
                if (RuntimeProperties.isLogDebugEnabled())
					db.p( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs());
                if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                    //recursion
                    genSubTasks( trel, bufSbtBody, true, sbName, currentUsedVars, currentProblem );

                } else {
                    appendRelToAlg( same(), trel, bufSbtBody );
                }
                addVarsToSet( trel.getInputs(), currentUsedVars );
            	addVarsToSet( trel.getOutputs(), currentUsedVars );
            }
            // apend subtask outputs to algorithm
            bufSbtBody.append( getSubtaskOutputs( subOutputs, same() ) );
            //end of run()
            bufSbtBody.append( left() );
            bufSbtBody.append( "}\n" );
            
            if( !subtask.isIndependent() ) {
            	//variable declaration & constructor
            	bufSbtClass.append( generateFieldDeclaration( parentClassName, sbName, currentUsedVars, currentProblem ) );
            } else {
            	bufSbtClass.append( same() ).append( getDeclaration( subtask.getContextCF(), "private" ) ).append( "\n" );
            }
            
            //append run() after subtasks' constructor
            bufSbtClass.append( bufSbtBody );
            //end of class
            bufSbtClass.append( left() );
            bufSbtClass.append( "} //End of subtask: " );
            bufSbtClass.append( subtask );
            bufSbtClass.append( "\n" );
            
            alg.append(bufSbtClass);
            alg.append( same() );
            alg.append( sbName );
            alg.append( " subtask_" );
            alg.append( subNum );
            alg.append( " = new " );
            alg.append( sbName );
            alg.append( "();\n\n" );
        }

        appendSubtaskRelToAlg( rel, start, alg, isNestedSubtask );
    }

    private static String _this_ = "." + TYPE_THIS + ".";
    
    private String generateFieldDeclaration( String parentClassName, String sbName, Set<Var> usedVars, Problem problem ) {
    	
    	String declOT = same();
    	String consOT = right();
    	StringBuilder bufDecl = new StringBuilder();
    	StringBuilder bufConstr = new StringBuilder();
    	Set<String> topVars = new HashSet<String>();
    	
    	for ( Var var : usedVars ) {
    		
    		boolean allow = !var.getField().isAlias() && !var.getField().isConstant() 
    						&& !var.getField().isVoid() && !var.getField().isStatic();
    		
    		if( var.getParent().equals( problem.getRootVar() ) ) {
    			if( allow ) {
    				bufDecl.append( declOT );
    				bufDecl.append( var.getDeclaration() );
    				
    				String parent = parentClassName.concat( _this_).concat( var.getName() );
    				bufConstr.append( consOT );
    				
    				if( var.getField().isArray() ) {
    					if( var.getField().isPrimitiveArray() ) {
    						bufConstr.append( "if( " ).append( parent ).append( " != null )\n").append( consOT ).append( OT_TAB );
    	    				bufConstr.append( var.getName() );
    	    				bufConstr.append( " = " );
    						bufConstr.append( "(" );
    						bufConstr.append( var.getType() );
    						bufConstr.append( ") " );
    						bufConstr.append( parent );
    						bufConstr.append( ".clone();\n" );
    					} else {
    						bufConstr.append( "try {\n" );
    						bufConstr.append( consOT );
    						bufConstr.append( OT_TAB );
    	    				bufConstr.append( var.getName() );
    	    				bufConstr.append( " = " );
    						bufConstr.append( "DeepCopy.copy( " );
        					bufConstr.append( parent );
        					bufConstr.append( " );\n" );
        					bufConstr.append( consOT );
        					bufConstr.append( "} catch( Exception e ) { e.printStackTrace(); }\n" );
    					}
    				} else {
        				bufConstr.append( var.getName() );
        				bufConstr.append( " = " );
    					bufConstr.append( parent );
    					bufConstr.append( ";\n" );
    				}
    			}
    		} else {
    			Var parent = var.getParent();

    			while( parent.getParent() != null && !parent.getParent().equals( problem.getRootVar() ) ) {
    				parent = parent.getParent();
    			}
    			
    			if( parent.getField().isStatic() ) continue;
    			
    			if( !topVars.contains( parent.getFullName() ) ) {
    				topVars.add( parent.getFullName() );
    				
    				bufDecl.append( declOT );
    				bufDecl.append( parent.getDeclaration() );
    			}
    			if( allow ) {
    				bufConstr.append( consOT );
    				bufConstr.append( var.getFullName() );
    				bufConstr.append( " = " );
    				bufConstr.append( parentClassName );
    				bufConstr.append( _this_ );
    				bufConstr.append( var.getFullName() );
    				bufConstr.append( ";\n" );
    			}
    		}
    	}
        
    	StringBuilder result = new StringBuilder();
    	
    	result.append( "\n" );
    	result.append( bufDecl );
    	result.append( "\n" );
    	result.append( left() );
    	result.append( sbName );
    	result.append( "() {\n\n" );
    	result.append( bufConstr );
    	result.append( same() );
    	result.append( "}\n\n" );
    	
        return result.toString();
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
    
    private void appendRelToAlg( String offset, Rel rel, StringBuilder buf ) {
        String s = rel.toString();
        if ( !s.equals( "" ) ) {
            if(rel.getExceptions().size() == 0 ) {
                buf.append( offset );
                buf.append( s );
                buf.append( ";\n" );
            }
            else {
                buf.append( appendExceptions( rel, s ) );
            }
        }
    }

    private void appendSubtaskRelToAlg(Rel rel, int startInd, StringBuilder buf, boolean isNestedSubtask) {

		String relString = rel.toString();
		for (int i = 0; i < rel.getSubtasks().size(); i++) {
			relString = relString.replaceFirst(RelType.TAG_SUBTASK, "subtask_" + (startInd + i));
		}
		if (rel.getExceptions().size() == 0 || isNestedSubtask) {
			buf.append(same());
			buf.append(relString);
			buf.append("\n");
		} else {
			buf.append(appendExceptions(rel, relString));
			buf.append("\n");
		}
	}

    private String appendExceptions(Rel rel, String s) {
		StringBuilder buf = new StringBuilder();
		buf.append(same());
		buf.append("try {\n");
		buf.append(right());
		buf.append(s);
		buf.append(";\n");
		buf.append(left());
		buf.append("}\n");
		
        int i = 0;
        for ( Var ex : rel.getExceptions() ) {
            String excp = ex.getName();
			String instanceName = "ex" + i;
			buf.append(same());
			buf.append("catch( ");
			buf.append(excp);
			buf.append(" ");
			buf.append(instanceName);
			buf.append(" ) {\n");
			buf.append(right());
			buf.append(instanceName);
			buf.append(".printStackTrace();\n");
			buf.append(same());
			buf.append("return;\n");
			buf.append(left());
			buf.append("}\n");
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
            
            if( var.getField().isVoid() ) continue;
            
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

        int count = 0;
        for ( Var var : aliasVar.getChildVars() ) {
        	
        	if( var.getField().isVoid() ) continue;
        	
        	String varName;
        	if( var.getField().isAlias() ) {
        		varName = getAliasTmpName(aliasVar.getName());
        		before += getVarsToAlias( var, varName );
        	} else {
        		varName = var.getFullName();
        	}
            if ( count++ == 0 ) {
                out += varName;
            } else {
                out += ", " + varName;
            }
        }
        out += " };\n";
        
        return before + out;
    }
    
    /**
	 * This method helps to copy Vars from one collection to another taking into account Aliases, 
	 * i.e. it flattens the hierarchical structure of aliases.
	 * 
	 * @param from
	 * @param to
	 */
	public static void addVarsToSet( Collection<Var> from, Collection<Var> to ) {
		for (Var topvar : from ) {

			to.add(topvar);
			// if output var is alias then all its vars should be copied as well
			if ( topvar.getField().isAlias() ) {
				for ( Var var : topvar.getChildVars() ) {

					to.add(var);
					if( var.getField().isAlias() ) {
						//this is used if we have alias in alias structure
						addVarsToSet( var.getChildVars(), to );//recursion
					}
				}
			}
		}
	}
	
	public static String getOffset() {
		return offset;
	}
	
	public static String getAliasTmpName( String varName ) {
		varName = varName.replaceAll( "\\.", "_" );
        return TypeUtil.TYPE_ALIAS + "_" + varName + "_" + ALIASTMP_NR++;
    }
}

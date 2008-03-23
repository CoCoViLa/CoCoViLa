package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;
import static ee.ioc.cs.vsle.synthesize.Synthesizer.*;

public class CodeGenerator {

    private static String      offset = "";

    public static final String OT_TAB = "    ";

    private static enum OFFSET {
        OT_INC, OT_DEC
    }

    private int                      subCount            = 0;

    public static int                ALIASTMP_NR;

    private List<Rel>                algRelList;
    private Problem                  problem;
    private String                   className;

    private Map<SubtaskRel, IndSubt> independentSubtasks = new HashMap<SubtaskRel, IndSubt>();

    public CodeGenerator( List<Rel> algRelList, Problem problem, String className ) {

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
                appendRelToAlg( rel, alg, false );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {

                Set<Var> usedVars = new HashSet<Var>();

                unfoldVarsToSet( rel.getInputs(), usedVars );
                unfoldVarsToSet( rel.getOutputs(), usedVars );

                genSubTasks( rel, alg, false, className, usedVars, problem );
            }

        }

        db.p( "Finished code generation in " + ( System.currentTimeMillis() - start ) + "ms" );

        return alg.toString();
    }

    public String getIndependentSubtasks() {
        StringBuilder buf = new StringBuilder();

        for ( IndSubt code : independentSubtasks.values() ) {
            buf.append( code.getCode() );
        }

        return buf.toString();
    }

    private void genAssumptions( StringBuilder alg, List<Var> assumptions ) {
        if ( assumptions.isEmpty() ) {
            return;
        }

        StringBuilder result = new StringBuilder( "" );
        int i = 0;
        for ( Var var : assumptions ) {

            String varType = var.getType();
            TypeToken token = TypeToken.getTypeToken( varType );

            result.append( offset );

            if ( token == TypeToken.TOKEN_OBJECT ) {
                result.append( var.getFullName() ).append( " = (" ).append( varType ).append( ")args[" ).append( i++ ).append( "];\n" );
            } else {
                result.append( var.getFullName() ).append( " = ((" ).append( token.getObjType() ).append( ")args[" ).append( i++ ).append( "])." )
                        .append( token.getMethod() ).append( "();\n" );
            }
        }

        alg.append( result );
    }

    private void genSubTasks( Rel rel, StringBuilder alg, boolean isNestedSubtask, String parentClassName, Set<Var> usedVars, Problem problem ) {

        List<String> subInstanceNames = new ArrayList<String>();

        for ( SubtaskRel subtask : rel.getSubtasks() ) {

            int subNum = subCount++;

            String sbName = ( subtask.isIndependent() ? "Independent" : "" ) + SUBTASK_INTERFACE_NAME + "_" + subNum;
            subInstanceNames.add( "subtask_" + subNum );

            if ( !subtask.isIndependent() || ( subtask.isIndependent() && !independentSubtasks.containsKey( subtask ) ) ) {

                String offsetBak = offset;
                if ( subtask.isIndependent() )
                    offset = "";

                Problem currentProblem = subtask.isIndependent() ? subtask.getContext() : problem;
                Set<Var> currentUsedVars = subtask.isIndependent() ? new HashSet<Var>() : usedVars;

                StringBuilder bufSbtClass = new StringBuilder();

                bufSbtClass.append( "\n" ).append( same() ).append( "class " ).append( sbName ).append( " implements " ).append(
                        SUBTASK_INTERFACE_NAME ).append( " {\n\n" );

                right();

                // start generating run()

                StringBuilder bufSbtBody = new StringBuilder();

                bufSbtBody.append( same() ).append( getRunMethodSignature() ).append( " {\n" );

                List<Var> subInputs = subtask.getInputs();
                List<Var> subOutputs = subtask.getOutputs();

                unfoldVarsToSet( subInputs, currentUsedVars );
                unfoldVarsToSet( subOutputs, currentUsedVars );

                List<Rel> subAlg = subtask.getAlgorithm();
                right();
                bufSbtBody.append( same() ).append( "//Subtask: " ).append( subtask ).append( "\n" );
                // apend subtask inputs to algorithm
                bufSbtBody.append( getSubtaskInputs( subInputs ) );
                for ( int i = 0; i < subAlg.size(); i++ ) {
                    Rel trel = subAlg.get( i );
                    if ( RuntimeProperties.isLogDebugEnabled() )
                        db.p( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs() );
                    if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                        // recursion
                        genSubTasks( trel, bufSbtBody, true, sbName, currentUsedVars, currentProblem );

                    } else {
                        appendRelToAlg( trel, bufSbtBody, true );
                    }
                    unfoldVarsToSet( trel.getInputs(), currentUsedVars );
                    unfoldVarsToSet( trel.getOutputs(), currentUsedVars );
                }
                // apend subtask outputs to algorithm
                bufSbtBody.append( getSubtaskOutputs( subOutputs, same() ) );
                // end of run()
                bufSbtBody.append( left() ).append( "}\n" );

                if ( !subtask.isIndependent() ) {
                    // variable declaration & constructor
                    bufSbtClass.append( generateFieldDeclaration( parentClassName, sbName, currentUsedVars, currentProblem ) );
                } else {
                    bufSbtClass.append( same() ).append( getDeclaration( subtask.getContextCF(), "private" ) ).append( "\n" );
                }

                // append run() after subtasks' constructor
                bufSbtClass.append( bufSbtBody );
                // end of class
                bufSbtClass.append( left() ).append( "} //End of subtask: " ).append( subtask ).append( "\n" );

                if ( subtask.isIndependent() ) {
                    independentSubtasks.put( subtask, new IndSubt( sbName, bufSbtClass.toString() ) );
                } else {
                    alg.append( bufSbtClass );
                }

                if ( subtask.isIndependent() )
                    offset = offsetBak;
            } else {
                IndSubt sub = independentSubtasks.get( subtask );
                sbName = sub.getClassName();
            }
            alg.append( same() ).append( sbName ).append( " " ).append( subInstanceNames.get( subInstanceNames.size() - 1 ) ).append( " = new " )
                    .append( sbName ).append( "();\n\n" );
        }

        appendSubtaskRelToAlg( rel, subInstanceNames, alg, isNestedSubtask );
    }

    private static String _this_ = "." + TYPE_THIS + ".";

    private String generateFieldDeclaration( String parentClassName, String sbName, Set<Var> usedVars, Problem problem ) {

        String declOT = same();
        String consOT = right();
        StringBuilder bufDecl = new StringBuilder();
        StringBuilder bufConstr = new StringBuilder();
        Set<String> topVars = new HashSet<String>();

        for ( Var var : usedVars ) {

            boolean allow = !var.getField().isAlias() && !var.getField().isConstant() && !var.getField().isVoid() && !var.getField().isStatic();

            if ( var.getParent().equals( problem.getRootVar() ) ) {
                if ( allow ) {
                    bufDecl.append( declOT );
                    bufDecl.append( var.getDeclaration() );

                    String parent = parentClassName.concat( _this_ ).concat( var.getName() );
                    bufConstr.append( consOT );

                    if ( var.getField().isArray() ) {
                        if ( var.getField().isPrimitiveArray() ) {
                            bufConstr.append( "if( " ).append( parent ).append( " != null )\n" ).append( consOT ).append( OT_TAB );
                            bufConstr.append( var.getName() ).append( " = " ).append( "(" ).append( var.getType() ).append( ") " ).append( parent )
                                    .append( ".clone();\n" );
                        } else {
                            bufConstr.append( "try {\n" ).append( consOT ).append( OT_TAB ).append( var.getName() ).append( " = " ).append(
                                    "DeepCopy.copy( " ).append( parent ).append( " );\n" ).append( consOT ).append(
                                    "} catch( Exception e ) { e.printStackTrace(); }\n" );
                        }
                    } else {
                        bufConstr.append( var.getName() ).append( " = " ).append( parent ).append( ";\n" );
                    }
                }
            } else {
                Var parent = var.getParent();

                while ( parent.getParent() != null && !parent.getParent().equals( problem.getRootVar() ) ) {
                    parent = parent.getParent();
                }

                if ( parent.getField().isStatic() )
                    continue;

                if ( !topVars.contains( parent.getFullName() ) ) {
                    topVars.add( parent.getFullName() );

                    bufDecl.append( declOT ).append( parent.getDeclaration() );
                }
                if ( allow ) {
                    bufConstr.append( consOT ).append( var.getFullName() ).append( " = " ).append( parentClassName ).append( _this_ ).append(
                            var.getFullName() ).append( ";\n" );
                }
            }
        }

        StringBuilder result = new StringBuilder();

        result.append( "\n" ).append( bufDecl ).append( "\n" ).append( left() ).append( sbName ).append( "() {\n\n" ).append( bufConstr ).append(
                same() ).append( "}\n\n" );

        return result.toString();
    }

    private String cOT( OFFSET ot, int times ) {

        switch ( ot ) {
            case OT_INC:
                for ( int i = 0; i < times; i++ ) {
                    offset += OT_TAB;
                }
                break;
            case OT_DEC:
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

    private void appendRelToAlg( Rel rel, StringBuilder buf, boolean isInSubtask ) {
        String s = rel.toString();
        if ( !s.equals( "" ) ) {
            if ( isInSubtask || rel.getExceptions().size() == 0 ) {
                buf.append( same() ).append( s ).append( ";\n" );
            } else {
                buf.append( appendExceptions( rel, s ) );
            }
        }
    }

    private void appendSubtaskRelToAlg( Rel rel, List<String> names, StringBuilder buf, boolean isNestedSubtask ) {

        String relString = rel.toString();
        for ( int i = 0; i < rel.getSubtasks().size(); i++ ) {
            relString = relString.replaceFirst( RelType.TAG_SUBTASK, names.get( i ) );
        }
        if ( isNestedSubtask || rel.getExceptions().size() == 0 ) {
            buf.append( same() ).append( relString ).append( "\n" );
        } else {
            buf.append( appendExceptions( rel, relString ) ).append( "\n" );
        }
    }

    private String appendExceptions( Rel rel, String s ) {
        StringBuilder buf = new StringBuilder();
        buf.append( same() ).append( "try {\n" ).append( right() ).append( s ).append( ";\n" ).append( left() ).append( "}\n" );

        int i = 0;
        for ( Var ex : rel.getExceptions() ) {
            String excp = ex.getName();
            String instanceName = "ex" + i;
            buf.append( same() ).append( "catch( " ).append( excp ).append( " " ).append( instanceName ).append( " ) {\n" ).append( right() ).append(
                    instanceName ).append( ".printStackTrace();\n" ).append( same() ).append( "return;\n" ).append( left() ).append( "}\n" );
        }

        return buf.toString();
    }

    private String getSubtaskInputs( List<Var> vars ) {

        String result = "";

        for ( int i = 0; i < vars.size(); i++ ) {
            Var var = vars.get( i );
            if ( var.getField().isAlias() ) {
                String aliasTmp = getAliasTmpName( var.getName() );
                result += getVarsFromAlias( var, aliasTmp, "in", i );
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

    // getAliasSubtaskInput
    public static String getVarsFromAlias( Var aliasVar, String aliasTmp, String parentVar, int num ) {
        
        if( aliasVar.getChildVars().isEmpty() ) {
            return "";
        }
        
        String aliasType = aliasVar.getType();

        String out = offset + aliasType + " " + aliasTmp + " = (" + aliasType + ")" + parentVar + "[" + num + "];\n";

        Var var;

        for ( int i = 0; i < aliasVar.getChildVars().size(); i++ ) {
            var = aliasVar.getChildVars().get( i );

            if ( var.getField().isVoid() )
                continue;

            String varType = var.getType();
            TypeToken token = TypeToken.getTypeToken( varType );

            if ( var.getField().isAlias() ) {
                // recursion
                String tmp = getAliasTmpName( var.getName() );
                out += getVarsFromAlias( var, tmp, aliasTmp, i );

            } else if ( token == TypeToken.TOKEN_OBJECT ) {

                out += offset + var.getFullName() + " = (" + varType + ")" + aliasTmp + "[" + i + "];\n";
            } else {
                out += offset + var.getFullName() + " = ((" + token.getObjType() + ")" + aliasTmp + "[" + i + "])." + token.getMethod() + "();\n";
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
            if ( i == 0 ) {
                varList += varName;
            } else {
                varList += ", " + varName;
            }

        }

        return declarations + result + varList + " };\n";
    }

    // getAliasSubtaskOutput
    public static String getVarsToAlias( Var aliasVar, String aliasTmp ) {

        String aliasType = aliasVar.getType();
        String before = "";
        
        if( aliasVar.getChildVars().isEmpty() && !((Alias)aliasVar.getField()).isInitialized() ) {
            return offset + aliasType + " " + aliasTmp + " = null;\n";
        }

        String out = offset + aliasType + " " + aliasTmp + " = new " + aliasType + "{ ";

        int count = 0;
        for ( Var var : aliasVar.getChildVars() ) {

            if ( var.getField().isVoid() )
                continue;

            String varName;
            if ( var.getField().isAlias() ) {
                varName = getAliasTmpName( aliasVar.getName() );
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
     * This method helps to copy Vars from one collection to another taking into
     * account Aliases, i.e. it flattens the hierarchical structure of aliases.
     * 
     * @param from
     * @param to
     */
    public static void unfoldVarsToSet( Collection<Var> from, Collection<Var> to ) {
        for ( Var topvar : from ) {

            to.add( topvar );
            // if output var is alias then all its vars should be copied as well
            if ( topvar.getField().isAlias() ) {
                for ( Var var : topvar.getChildVars() ) {

                    to.add( var );
                    if ( var.getField().isAlias() ) {
                        // this is used if we have alias in alias structure
                        unfoldVarsToSet( var.getChildVars(), to );// recursion
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

    private class IndSubt {

        private String className;
        private String code;

        public IndSubt( String className, String code ) {
            this.className = className;
            this.code = code;
        }

        public String getClassName() {
            return className;
        }

        public String getCode() {
            return code;
        }
    }
}

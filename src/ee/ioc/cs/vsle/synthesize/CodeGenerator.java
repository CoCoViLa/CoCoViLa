package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.api.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class CodeGenerator {

    public static final String COMPUTE_ARG_NAME = "cocovilaArgs";
    public static final String SPEC_OBJECT_NAME = "cocovilaSpecObjectName";
    public static final String INDEPENDENT_SUBTASK = "IndependentSubtask";
    public static final String GENERATED_INTERFACE_NAME = "IComputable";
    public static final String SUBTASK_INTERFACE_NAME = "Subtask";
    
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

    private CodeGenerator() {
        
        ALIASTMP_NR = 0;
        offset = "";
    }
    
    public CodeGenerator( List<Rel> algRelList, Problem problem, String className ) {
        
        this();
        this.algRelList = algRelList;
        this.problem = problem;
        this.className = className;
    }

    /**
     * Generates a Java program for a given algorithm
     * 
     * @return
     */
    public String generate() {

        db.p( "Starting code generation" );
        long start = System.currentTimeMillis();

        StringBuilder alg = new StringBuilder();
        cOT( OFFSET.OT_INC, 2 );

        genInputs( alg, problem.getAssumptions(), COMPUTE_ARG_NAME );

        for ( Rel rel : algRelList ) {

            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( rel.toString(), rel.getExceptions(), alg, false );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {

                Set<Var> usedVars = new HashSet<Var>();

                unfoldVarsToSet( rel.getInputs(), usedVars );
                unfoldVarsToSet( rel.getOutputs(), usedVars );

                genRelWithSubtasks( rel, alg, false, className, usedVars, problem );
            }

        }

        db.p( "Finished code generation in " + ( System.currentTimeMillis() - start ) + "ms" );

        return alg.toString();
    }

    /**
     * Returns code of generated independent subtasks
     * 
     * @return
     */
    public String getIndependentSubtasks() {
        StringBuilder buf = new StringBuilder();

        for ( IndSubt code : independentSubtasks.values() ) {
            buf.append( code.getCode() );
        }

        return buf.toString();
    }

    /**
     * Generates code for axiom with subtasks
     * 
     * @param rel
     * @param alg
     * @param isNestedSubtask
     * @param parentClassName
     * @param usedVars
     * @param _problem
     */
    private void genRelWithSubtasks( Rel rel, StringBuilder alg, boolean isNestedSubtask, String parentClassName, Set<Var> usedVars, Problem _problem ) {

        String relString = rel.toString();
        
        for ( SubtaskRel subtask : rel.getSubtasks() ) {

            int subNum = subCount++;
            
            StringBuilder bufSbtClass = new StringBuilder();
            
            String sbClassName;
            if ( !subtask.isIndependent() || ( subtask.isIndependent() && !independentSubtasks.containsKey( subtask ) ) ) {
                
                sbClassName = genSubtask( bufSbtClass, parentClassName, usedVars, _problem,
                        subtask, subNum );
                
                if ( subtask.isIndependent() ) {
                    independentSubtasks.put( subtask, new IndSubt( sbClassName, bufSbtClass.toString() ) );
                } else {
                    alg.append( bufSbtClass );
                }
            }        
            else {
                IndSubt sub = independentSubtasks.get( subtask );
                sbClassName = sub.getClassName();
            }
            
            String sbInstanceName = "subtask_" + subNum;
            
            alg.append( same() ).append( sbClassName ).append( " " ).append(
                    sbInstanceName ).append( " = new " ).append( sbClassName )
                    .append( "();\n\n" );
            
            relString = relString.replaceFirst( RelType.TAG_SUBTASK, sbInstanceName );
        }
        
        appendRelToAlg( relString, rel.getExceptions(), alg, isNestedSubtask );
    }

    /**
     * Generates code for a subtask (both independent/dependent)
     * @param bufSbtInst
     * @param bufSbtClass
     * @param parentClassName
     * @param usedVars
     * @param _problem
     * @param subtask
     * @param subNum
     * @return
     */
    private String genSubtask( StringBuilder bufSbtClass, String parentClassName,
            Set<Var> usedVars, Problem _problem,
            SubtaskRel subtask, int subNum ) {
        
        String sbClassName = ( subtask.isIndependent() ? "Independent" : "" ) + SUBTASK_INTERFACE_NAME + "_" + subNum;

        String offsetBak = offset;
        if ( subtask.isIndependent() )
            offset = "";

        Problem currentProblem = subtask.isIndependent() ? subtask.getContext() : _problem;
        Set<Var> currentUsedVars = subtask.isIndependent() ? new HashSet<Var>() : usedVars;

        bufSbtClass.append( "\n" ).append( same() ).append( "class " ).append( sbClassName ).append( " implements " ).append(
                SUBTASK_INTERFACE_NAME ).append( " {\n\n" );

        right();

        // start generating run()

        StringBuilder bufSbtBody = new StringBuilder();

        String subtaskInputArrayName = "cocovilaSubtaskInput" + subNum;

        bufSbtBody.append( same() ).append( getRunMethodSignature( subtaskInputArrayName ) ).append( " {\n" );

        List<Var> subInputs = subtask.getInputs();
        List<Var> subOutputs = subtask.getOutputs();

        unfoldVarsToSet( subInputs, currentUsedVars );
        unfoldVarsToSet( subOutputs, currentUsedVars );

        List<Rel> subAlg = subtask.getAlgorithm();
        right();
        bufSbtBody.append( same() ).append( "//Subtask: " ).append( subtask ).append( "\n" );
        // apend subtask inputs to algorithm
        genInputs( bufSbtBody, subInputs, subtaskInputArrayName );

        for ( int i = 0; i < subAlg.size(); i++ ) {
            Rel trel = subAlg.get( i );
            if ( RuntimeProperties.isLogDebugEnabled() )
                db.p( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs() );
            if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                // recursion
                genRelWithSubtasks( trel, bufSbtBody, true, sbClassName, currentUsedVars, currentProblem );

            } else {
                appendRelToAlg( trel.toString(), trel.getExceptions(), bufSbtBody, true );
            }
            unfoldVarsToSet( trel.getInputs(), currentUsedVars );
            unfoldVarsToSet( trel.getOutputs(), currentUsedVars );
        }
        // apend subtask outputs to algorithm
        bufSbtBody.append( getSubtaskOutputs( subOutputs) );
        // end of run()
        bufSbtBody.append( left() ).append( "}\n" );

        if ( !subtask.isIndependent() ) {
            // variable declaration & constructor
            bufSbtClass.append( generateFieldDeclaration( parentClassName, sbClassName, currentUsedVars, currentProblem ) );
        } else {
            bufSbtClass.append( same() ).append( getDeclaration( subtask.getContextCF(), "private" ) ).append( "\n" );
        }

        // append run() after subtasks' constructor
        bufSbtClass.append( bufSbtBody );
        // end of class
        bufSbtClass.append( left() ).append( "} //End of subtask: " ).append( subtask ).append( "\n" );

        if ( subtask.isIndependent() )
            offset = offsetBak;
       
        return sbClassName;
    }

    /**
     * Generates code of an independent subtask
     * TODO check nested independent subtasks' code generation
     * 
     * @param subtask
     * @param classCode
     * @return name of the generated class
     */
    static String genIndependentSubtask(SubtaskRel subtask, StringBuilder classCode) {
        
        return new CodeGenerator().genSubtask( classCode, null, null, null, subtask, 0 );
    }
    
    private static String _this_ = "." + TYPE_THIS + ".";

    private String generateFieldDeclaration( String parentClassName, String sbName, Set<Var> usedVars, Problem _problem ) {

        String declOT = same();
        String consOT = right();
        StringBuilder bufDecl = new StringBuilder();
        StringBuilder bufConstr = new StringBuilder();
        Set<String> topVars = new HashSet<String>();

        for ( Var var : usedVars ) {

            ClassField cf = var.getField();
            boolean allow = !cf.isAlias() && !cf.isConstant() 
                                && !cf.isVoid() && !cf.isStatic();

            if ( var.getParent().equals( _problem.getRootVar() ) ) {
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
                                    "} catch( Exception e ) { throw new SubtaskExecutionException(e); }\n" );
                        }
                    } else {
                        bufConstr.append( var.getName() ).append( " = " ).append( parent ).append( ";\n" );
                    }
                }
            } else {
                Var parent = var.getParent();

                while ( parent.getParent() != null && !parent.getParent().equals( _problem.getRootVar() ) ) {
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
            offset = offset.substring( OT_TAB.length() * times );
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

    private void appendRelToAlg( String relString, Collection<Var> exceptions, StringBuilder buf, boolean isInSubtask ) {
        
        if ( !relString.equals( "" ) ) {
            
            String exceptionClassName = isInSubtask ? SubtaskExecutionException.class.getSimpleName()
                    : RunningProgramException.class.getSimpleName();   
            
            if (exceptions.isEmpty()) {
                buf.append( same() ).append( relString );                
            } else {
                buf.append( appendExceptionHandler( relString, exceptionClassName ) );
            }
        }
    }
    
    private String appendExceptionHandler( String s, String exceptionClassName ) {
        StringBuilder buf = new StringBuilder();
        buf.append( same() ).append( "try {\n" ).append( right() ).append( s ).append( left() ).append( "}\n" );

        buf.append( same() ).append( "catch(Exception e) {\n" )
            .append( right() ).append( "throw new " ).append( exceptionClassName ).append( "(e);\n" ).append( left() ).append( "}\n" );

        return buf.toString();
    }
    
    /**
     * Generates code for value extraction from assumptions or subtask input object array
     * 
     * @param alg
     * @param vars
     * @param inputArgName
     */
    private void genInputs( StringBuilder alg, List<Var> vars,
            String inputArgName ) {

        if ( vars.isEmpty() ) {
            return;
        }

        StringBuilder result = new StringBuilder();

        for ( int i = 0; i < vars.size(); i++ ) {
            Var var = vars.get( i );

            if ( var.getField().isAlias() ) {
                String aliasTmp = getAliasTmpName( var.getName() );
                result.append( getVarsFromAlias( var, aliasTmp, inputArgName, i ) );
                continue;
            }

            String varType = var.getType();
            TypeToken token = TypeToken.getTypeToken( varType );

            result.append( offset );

            if ( token == TypeToken.TOKEN_OBJECT
                    || token == TypeToken.TOKEN_STRING ) {
                result.append( var.getFullName() ).append( " = (" ).append(
                        varType ).append( ")" ).append( inputArgName ).append(
                        "[" ).append( i ).append( "];\n" );
            } else {
                result.append( var.getFullName() ).append( " = ((" ).append(
                        token.getObjType() ).append( ")" )
                        .append( inputArgName ).append( "[" ).append( i )
                        .append( "])." ).append( token.getMethod() ).append(
                                "();\n" );
            }
        }

        alg.append( result ).append( "\n" );
    }

    // getAliasSubtaskInput
    public static String getVarsFromAlias( Var aliasVar, String aliasTmp,
            String parentVar, int num ) {

        if ( aliasVar.getChildVars().isEmpty() ) {
            return "";
        }

        String aliasType = aliasVar.getType();

        StringBuilder out = new StringBuilder( offset ).append( aliasType )
                .append( " " ).append( aliasTmp ).append( " = (" ).append(
                        aliasType ).append( ")" ).append( parentVar ).append(
                        "[" ).append( num ).append( "];\n" );

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
                out.append( getVarsFromAlias( var, tmp, aliasTmp, i ) );
            } else if ( token == TypeToken.TOKEN_OBJECT
                    || token == TypeToken.TOKEN_STRING ) {
                out.append( offset ).append( var.getFullName() )
                        .append( " = (" ).append( varType ).append( ")" )
                        .append( aliasTmp ).append( "[" ).append( i ).append(
                                "];\n" );
            } else {
                out.append( offset ).append( var.getFullName() ).append(
                        " = ((" ).append( token.getObjType() ).append( ")" )
                        .append( aliasTmp ).append( "[" ).append( i ).append(
                                "])." ).append( token.getMethod() ).append(
                                "();\n" );
            }

        }
        return out.toString();
    }

    private String getSubtaskOutputs( List<Var> vars ) {

        StringBuilder declarations = new StringBuilder( "\n" );
        StringBuilder varList = new StringBuilder();

        for ( int i = 0; i < vars.size(); i++ ) {
            Var var = vars.get( i );

            String varName;

            if ( var.getField().isAlias() ) {
                String aliasTmp = getAliasTmpName( var.getName() );
                declarations.append( getVarsToAlias( var, aliasTmp ) );
                varName = aliasTmp;
            } else {
                varName = var.getFullName();
            }

            if ( i == 0 ) {
                varList.append( varName );
            } else {
                varList.append( ", " ).append( varName );
            }
        }

        return declarations.append( same() ).append( "return new Object[]{ " )
                .append( varList ).append( " };\n" ).toString();
    }

    // getAliasSubtaskOutput
    public static String getVarsToAlias( Var aliasVar, String aliasTmp ) {

        String aliasType = aliasVar.getType();
        StringBuilder before = new StringBuilder();
        StringBuilder out = new StringBuilder( offset );

        if ( aliasVar.getChildVars().isEmpty()
                && !( (Alias) aliasVar.getField() ).isInitialized() ) {
            return out.append( aliasType ).append( " " ).append( aliasTmp )
                    .append( " = null;\n" ).toString();
        }

        out.append( aliasType ).append( " " ).append( aliasTmp ).append(
                " = new " ).append( aliasType ).append( "{ " );

        int count = 0;
        for ( Var var : aliasVar.getChildVars() ) {

            if ( count++ > 0 ) {
                out.append( ", " );
            }

            if ( var.getField().isVoid() )
                out.append( "/*void:" ).append( var.getField().getName() )
                        .append( "*/null" );
            else if ( var.getField().isAlias() ) {
                String varName = getAliasTmpName( aliasVar.getName() );
                out.append( varName );
                before.append( getVarsToAlias( var, varName ) );
            } else {
                out.append( var.getFullName() );
            }
        }

        return before.append( out ).append( " };\n" ).toString();
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
    
    
    public static String getRunMethodSignature( String argName ) {
        return "public Object[] run(Object[] " + argName + ")";
    }
    
    public static String getComputeMethodSignature( String argName ) {
        return "public void compute( Object... " + argName + " )";
    }
}

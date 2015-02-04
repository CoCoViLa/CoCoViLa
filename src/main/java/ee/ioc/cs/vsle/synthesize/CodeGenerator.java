package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.util.TypeUtil.*;

import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.api.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CodeGenerator.class);

    public static final String COMPUTE_ARG_NAME = "cocovilaArgs";
    public static final String SPEC_OBJECT_NAME = "cocovilaSpecObjectName";
    public static final String INDEPENDENT_SUBTASK = "IndependentSubtask";
    public static final String GENERATED_INTERFACE_NAME = "IComputable";
    public static final String SUBTASK_INTERFACE_NAME = "Subtask";
    public static final String OT_TAB = "    ";
    private static final String _this_ = "." + TYPE_THIS + ".";
    
    private StringBuilder offset = new StringBuilder();

    private int subCount = 0;

    private EvaluationAlgorithm algorithm;
    private Problem problem;
    private String className;
    private RelCodeProducer relProducer = new RelCodeProducer( this );
    
    private Map<SubtaskRel, IndSubt> independentSubtasks = new HashMap<SubtaskRel, IndSubt>();

    private CodeGenerator() {
        offset = new StringBuilder();
    }
    
    public CodeGenerator( EvaluationAlgorithm algorithm, Problem problem, String className ) {
        
        this();
        this.algorithm = algorithm;
        this.problem = problem;
        this.className = className;
    }

    /**
     * Generates a Java program for a given algorithm
     * 
     * @return
     */
    public String generate() {

        logger.debug( "Starting code generation" );
        long start = System.currentTimeMillis();

        StringBuilder alg = new StringBuilder();
        changeOffset( true, 2 );

        genInputs( alg, problem.getAssumptions(), COMPUTE_ARG_NAME );

        for ( PlanningResult res : algorithm ) {

            Rel rel = res.getRel();
            
            if ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {
                appendRelToAlg( relProducer.setRel( rel ) .emit(), rel.getExceptions(), alg, false );
            }

            else if ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {

                Set<Var> usedVars = new HashSet<Var>();

                unfoldVarsToSet( rel.getInputs(), usedVars );
                unfoldVarsToSet( rel.getOutputs(), usedVars );

                genRelWithSubtasks( res, alg, false, className, usedVars, problem );
            }

        }

        logger.info("Code generation time: " + (System.currentTimeMillis() - start) + "ms");

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
     * @param res
     * @param alg
     * @param isNestedSubtask
     * @param parentClassName
     * @param usedVars
     * @param _problem
     */
    private void genRelWithSubtasks( PlanningResult res, StringBuilder alg, boolean isNestedSubtask, String parentClassName, Set<Var> usedVars, Problem _problem ) {

        Rel rel = res.getRel();
        String relString = relProducer.setRel( rel ).emit();
        
        for ( SubtaskRel subtask : rel.getSubtasks() ) {

            int subNum = ++subCount;
            
            StringBuilder bufSbtClass = new StringBuilder();
            
            String sbClassName;
            if ( !subtask.isIndependent() || ( subtask.isIndependent() && !independentSubtasks.containsKey( subtask ) ) ) {
                
                sbClassName = genSubtask( res.getSubtaskAlgorithm( subtask ), bufSbtClass, parentClassName, usedVars, _problem,
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
     * @param bufSbtClass
     * @param parentClassName
     * @param usedVars
     * @param _problem
     * @param subtask
     * @param subNum
     * @return
     */
    private String genSubtask( EvaluationAlgorithm subAlg, StringBuilder bufSbtClass, String parentClassName,
            Set<Var> usedVars, Problem _problem,
            SubtaskRel subtask, int subNum ) {
        
        String sbClassName = ( subtask.isIndependent() ? "Independent" : "" ) + SUBTASK_INTERFACE_NAME + "_" + subNum;

        StringBuilder offsetBak = offset;
        if ( subtask.isIndependent() )
            offset = new StringBuilder();

        Problem currentProblem = subtask.isIndependent() ? subtask.getContext() : _problem;
        Set<Var> currentUsedVars = subtask.isIndependent() ? new HashSet<Var>() : usedVars;

        bufSbtClass.append( "\n" ).append( same() ).append( "class " ).append( sbClassName ).append( " implements " ).append(
                SUBTASK_INTERFACE_NAME ).append( " {\n\n" );

        right();

        // start generating run()

        StringBuilder bufSbtBody = new StringBuilder();

        String subtaskInputArrayName = "cocovilaSubtaskInput" + subNum;

        bufSbtBody.append( same() ).append( getRunMethodSignature( subtaskInputArrayName ) ).append( " {\n" );

        Collection<Var> subInputs = subtask.getInputs();
        Collection<Var> subOutputs = subtask.getOutputs();

        unfoldVarsToSet( subInputs, currentUsedVars );
        unfoldVarsToSet( subOutputs, currentUsedVars );

        right();
        bufSbtBody.append( same() ).append( "//Subtask: " ).append( relProducer.setRel( subtask ).emit() ).append( "\n" );
        // apend subtask inputs to algorithm
        genInputs( bufSbtBody, subInputs, subtaskInputArrayName );

        for ( int i = 0; i < subAlg.size(); i++ ) {
            PlanningResult res = subAlg.get( i );
            Rel trel = res.getRel();
            logger.debug( "rel " + trel + " in " + trel.getInputs() + " out " + trel.getOutputs() );
            if ( trel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
                // recursion
                genRelWithSubtasks( res, bufSbtBody, true, sbClassName, currentUsedVars, currentProblem );

            } else {
                appendRelToAlg( relProducer.setRel( trel ).emit(), trel.getExceptions(), bufSbtBody, true );
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
        bufSbtClass.append( left() ).append( "} //End of subtask: " ).append( relProducer.setRel( subtask ).emit() ).append( "\n" );

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
    static String genIndependentSubtask(SubtaskRel subtask, EvaluationAlgorithm subAlg, StringBuilder classCode) {

        final CodeGenerator codeGenerator = new CodeGenerator();
        final String className = codeGenerator.genSubtask(subAlg, classCode, null, null, null, subtask, 0);
        for(IndSubt subtaskCode : codeGenerator.independentSubtasks.values()) {
            classCode.append(subtaskCode.code);
        }
        return className;
    }
    
    private String generateFieldDeclaration( String parentClassName, String sbName, Set<Var> usedVars, Problem _problem ) {

        String declOT = same().toString();
        String consOT = right().toString();
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
                            bufConstr.append( "try {\n" )
                                     .append( consOT ).append( OT_TAB ).append( "//NB! Classes of objects in the array being copied must implement 'java.io.Serializable' interface\n")
                                     .append( consOT ).append( OT_TAB ).append( var.getName() ).append( " = " )
                                       .append( "DeepCopy.copy( " ).append( parent ).append( " );\n" )
                                     .append( consOT ).append("} catch( Exception e ) { throw new SubtaskExecutionException(e); }\n" );
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

    private StringBuilder changeOffset( boolean increase, int times ) {

        if(increase)
            for ( int i = 0; i < times; i++ ) {
                offset.append( OT_TAB );
            }
        else
            offset.setLength( offset.length() - 4 * times );

        return offset;
    }

    private StringBuilder left() {
        offset.setLength( offset.length()-4 );
        return offset;
    }

    private StringBuilder right() {
        return offset.append( OT_TAB );
    }

    private StringBuilder same() {
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
    private void genInputs( StringBuilder alg, Collection<Var> vars,
            String inputArgName ) {

        if ( vars.isEmpty() ) {
            return;
        }

        StringBuilder result = new StringBuilder();

        int i = 0;
        for ( Var var : vars ) {

            if ( var.getField().isAlias() ) {
                String aliasTmp = getAliasTmpName( var.getName() );
                result.append( getVarsFromAlias( var, aliasTmp, inputArgName, i ) );
                i++;
                continue;
            } else if ( var.getField().isVoid() ) {
                i++;
                continue;
            }

            String varType = var.getType();
            TypeToken token = TypeToken.getTypeToken( varType );

            result.append( offset );

            if ( token == TypeToken.TOKEN_OBJECT
                    || token == TypeToken.TOKEN_STRING ) {
                result.append( var.getFullName() ).append( " = " );
                if(!TYPE_ANY.equals(varType))
                	result.append( "(" ).append( varType ).append( ")" );
                result.append( inputArgName ).append(
                        "[" ).append( i ).append( "];\n" );
            } else {
                result.append( var.getFullName() ).append( " = ((" ).append(
                        token.getObjType() ).append( ")" )
                        .append( inputArgName ).append( "[" ).append( i )
                        .append( "])." ).append( token.getMethod() ).append(
                                "();\n" );
            }
            i++;
        }

        alg.append( result ).append( "\n" );
    }

    // getAliasSubtaskInput
    public String getVarsFromAlias( Var aliasVar, String aliasTmp,
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

    private String getSubtaskOutputs( Collection<Var> vars ) {

        StringBuilder declarations = new StringBuilder( "\n" );
        StringBuilder varList = new StringBuilder();

        int i = 0;
        for ( Var var : vars ) {

            String varName;
            if ( var.getField().isVoid() )
                varName = new StringBuilder( "/*void:" ).append( var.getField().getName() )
                        .append( "*/null" ).toString();
            else if ( var.getField().isAlias() ) {
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
            i++;
        }

        return declarations.append( same() ).append( "return new Object[]{ " )
                .append( varList ).append( " };\n" ).toString();
    }

    // getAliasSubtaskOutput
    public String getVarsToAlias( Var aliasVar, String aliasTmp ) {

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

    public String getOffset() {
        return offset.toString();
    }

    private String getAliasTmpName( String varName ) {
        varName = varName.replaceAll( "\\.", "_" );
        return new StringBuilder( TypeUtil.TYPE_ALIAS ).append( "_" ).append( varName ).append( "_" ).append( RelType.nextTmpVarNr() ).toString();
    }

    private class IndSubt {

        private String _className;
        private String code;

        public IndSubt( String className, String code ) {
            this._className = className;
            this.code = code;
        }

        public String getClassName() {
            return _className;
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
    
    
    /**************** The code below is related to the relation code generation ************************************/
    static class RelCodeProducer
    {
        private static final Pattern PATTERN_VAR_IN_EQUATION = Pattern
                                        .compile( "[^a-zA-Z_]*([a-zA-Z_]{1}[a-zA-Z_0-9\\.]*)" );
        
        private Rel rel;
        private CodeGenerator cg;
        
        RelCodeProducer(Rel rel) {
            this(new CodeGenerator());
            setRel( rel );
        }
        
        private RelCodeProducer(CodeGenerator cg) {
            this.cg = cg;
        }
        
        private RelCodeProducer setRel(Rel rel) {
            this.rel = rel;
            return this;
        }
        
        private String getMaxType(Collection<Var> _inputs) {

            for ( Var var : _inputs ) {
                String type = var.getField().isAny() && var.getField().isAnyTypeBound() 
                                ? var.getField().getAnySpecificType() 
                                : var.getType();
                if (!TYPE_INT.equals(type)) {
                    return TYPE_DOUBLE;
                }
            }
            return TYPE_INT;
        }

        private String getOutputString(Map<String, String> outputSubstitutions) {

            StringBuilder outputString = new StringBuilder();
            Var var = rel.getFirstOutput();

            if ( !TypeUtil.TYPE_VOID.equals( var.getType() ) ) {
                if ( var.getField().isAlias() ) {
                    String alias_tmp = getRelAliasTmpName( var, outputSubstitutions, true );
                    
                    if ( !var.getChildVars().isEmpty() ) {

                        outputString.append(
                                ( (Alias) var.getField() ).getType() ).append(
                                " " ).append( alias_tmp ).append( " " );
                    }

                } else {
                    outputString.append( var.getFullName() );
                }

            }
            return outputString.toString();
        }

        private String getRelAliasTmpName( Var var, Map<String, String> varNameSubstitutions, boolean cache ) {
            String name;
            if( ( name = varNameSubstitutions.get( var.getFullName() ) ) == null ) {
                String varName = var.getFullNameForConcat().replaceAll( "\\.", "_" );
                name = new StringBuilder( TypeUtil.TYPE_ALIAS ).append( "_" )
                        .append( varName ).append( RelType.nextTmpVarNr() ).toString();
                if( cache )
                    varNameSubstitutions.put( var.getFullName(), name );
            }
            return name;
            
        }

        private String getParametersString( boolean useBrackets, Map<String, String> varNameSubstitutions ) {
            
            StringBuilder params = new StringBuilder();
            if ( useBrackets )
                params.append( "(" );

            int j = 0;
            for ( Var var : rel.getInputs() ) {

                if ( !TypeUtil.TYPE_VOID.equals( var.getType() ) ) {
                    if ( j++ > 0 )
                        params.append( ", " );

                    if ( var.getField().isAlias() ) {
                        params.append( getRelAliasTmpName( var, varNameSubstitutions, false ) );
                    } else {
                        params.append( getVarName(var) );
                    }
                }
            }
            if ( useBrackets )
                params.append( ")" );

            return params.toString();
        }

        private String getSubtaskParametersString( Map<String, String> varNameSubstitutions ) {

            StringBuilder params = new StringBuilder( "(" );

            boolean subExist = false;
            for ( int i = 0; i < rel.getSubtaskCount(); i++ ) {
                if ( i == 0 ) {
                    subExist = true;
                } else {
                    params.append( ", " );
                }
                params.append( RelType.TAG_SUBTASK );
            }
            
            String simpleParams = getParametersString( false, varNameSubstitutions );
            
            if ( subExist && simpleParams.length() > 0 ) {
                params.append( ", " );
            }

            return params.append( simpleParams ).append( ")" )
                    .toString();
        }
        
        /* 
         * Emits code for a given relation class
         */
        String emit() {

            assert rel != null : "Relation is null";
            
            if (rel.getType() == RelType.TYPE_ALIAS) {
                return "";
            }
            else if (rel.getType() == RelType.TYPE_EQUATION) {
                return emitEquation();
                
            } else if (rel.getType() == RelType.TYPE_SUBTASK) {
                // this should not be used in code generation
                return rel.getInputs() + " -> " + rel.getOutputs();

            } else if ( ( rel.getType() == RelType.TYPE_JAVAMETHOD )
                    || ( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK )) {

                return emitMethod();

            } else {
                return emitAssignment();
            }
        }

        private String emitAssignment() {

            Var ip = rel.getFirstInput();
            Var op = rel.getFirstOutput();
            StringBuilder assigns = new StringBuilder();
            int i = 0;

            if ( ip.getField().isArray() && op.getField().isAlias() ) {
                for ( Var childVar : op.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append( childVar.getFullName() )
                            .append( " = " ).append( ip.getFullName() ).append(
                                    "[" ).append( Integer.toString( i++ ) )
                            .append( "];\n" );
                }
            } else if ( op.getField().isArray() && ip.getField().isAlias() ) {
                for ( Var childVar : ip.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append(
                            op.getFullName() ).append( "[" ).append(
                            Integer.toString( i++ ) ).append( "] = " ).append(
                            childVar.getFullName() ).append( ";\n" );
                }
            } else if ( op.getField().isAlias() && ip.getField().isAlias() ) {
                for ( Var inpChildVar : ip.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append(
                            op.getChildVars().get( i++ ).getFullName() ).append( " = " )
                            .append( inpChildVar.getFullName() ).append( ";\n" );
                }
            } else {
	            	assigns.append( op.getFullName() ).append( " = " )
	            		.append(getVarName(ip)).append( ";\n" );
	            }
            return assigns.toString();
        }

        private String getVarName(Var var) {
        	String subst = rel.getSubstitutions().get(var.getFullName());
        	if(subst != null) {
        		return subst;
        	}
        	StringBuilder sb = new StringBuilder();
        	if(var.getField().isAny())
        		sb.append(CodeGeneratorUtil.getAnyTypeSubstitution(
        		                                var.getFullName(), 
        		                                var.getField().isAnyTypeBound() ? var.getField().getAnySpecificType() : "Object"));
        	else
        		sb.append(var.getFullName());
        	
        	return sb.toString();
        }
        
        private String emitMethod() {

            Map<String, String> inputSubstitutions = new HashMap<String, String>();
            String aliasInputs = checkAliasInputs( inputSubstitutions );
            
            Map<String, String> outputSubstitutions = new HashMap<String, String>();            
            String output = getOutputString( outputSubstitutions );
            
            String meth, params;

            boolean tableInputsMapping = false;
            if ( Table.TABLE_KEYWORD.equals( rel.getMethod() )
                    || ( tableInputsMapping = Table.TABLE_WITH_INPUT_MAPPING_KEYWORD.equals( rel.getMethod() ) ) ) {

                StringBuilder cast = new StringBuilder();

                if ( !rel.getOutputs().isEmpty() ) {

                    cast.append( "(" );

                    String _type = rel.getFirstOutput().getType();

                    TypeToken token = TypeToken.getTypeToken( _type );

                    if ( token == TypeToken.TOKEN_OBJECT ) {
                        cast.append( _type );
                    } else {
                        cast.append( token.getObjType() );
                    }

                    cast.append( ")" );
                }
                
                meth = cast.append( "ProgramContext.queryTable" ).toString();
                
                if( tableInputsMapping && rel.getInputs().size() > 1 ) {
                    List<Var> list = new ArrayList<Var>(rel.getInputs());
                    StringBuilder inputIds = new StringBuilder( "(new String[] { ");
                    for( int i = 1; i < list.size(); i++ ) {
                        if( i > 1 ) inputIds.append( ", " );
                        inputIds.append( "\"" ).append( list.get( i ).getName() ).append( "\"" );
                    }
                    inputIds.append( " }, " ).append( getParametersString( false, inputSubstitutions ) ).append( ")" );
                    
                    params = inputIds.toString();
                } else {
                    params = getParametersString( true, inputSubstitutions );
                }
            } else {
                meth = rel.getParent().getFullNameForConcat() + rel.getMethod();
                
                params = ( rel.getType() == RelType.TYPE_JAVAMETHOD ) 
                        ? getParametersString( true, inputSubstitutions )
                        : getSubtaskParametersString( inputSubstitutions );
            }
            
            return new StringBuilder( aliasInputs ).append(
                    output.length() > 0 ? output + " = " : "" ).append( meth )
                    .append( params ).append( ";\n" ).append(
                            checkAliasOutputs( outputSubstitutions ) ).toString();
        }

        private String emitEquation() {
            
            StringBuilder result = new StringBuilder();
            // if its an array assingment
            if ( rel.getInputs().size() == 0 && rel.getOutputs().size() == 1 ) {
                Var op = rel.getFirstOutput();

                if ( op.getField().isPrimOrStringArray() ) {
                    String[] split = rel.getMethod().split( "=" );
                    result.append( op.getField().getType() ).append( " " )
                            .append( " TEMP" ).append(
                                    Integer.toString( RelType.tmpVarNr() ) )
                            .append( "=" ).append( split[1] ).append( ";\n" );
                    result.append( CodeGenerator.OT_TAB ).append(
                            CodeGenerator.OT_TAB ).append( op.getFullName() )
                            .append( " = TEMP" ).append(
                                    Integer.toString( RelType.tmpVarNr() ) )
                            .append( ";\n" );
                    RelType.nextTmpVarNr();
                    return result.toString();

                }
            }

            if ( rel.getInputs().size() == 1 && rel.getOutputs().size() == 1 ) {
                Var ip = rel.getFirstInput();
                Var op = rel.getFirstOutput();

                if ( ip.getField().isArray() && op.getField().isAlias() ) {

                    for ( int i = 0; i < rel.getFirstOutput().getChildVars().size(); i++ ) {
                        result.append( op.getChildVars().get( i ).getFullName() ).append(
                                " = " ).append( ip.getFullName() ).append( "[" )
                                .append( i ).append( "];\n" );
                    }
                    return result.toString();
                } else if ( op.getField().isArray() && ip.getField().isAlias() ) {

                    result.append( op.getField().getType() ).append( " TEMP" )
                            .append( rel.getId() ).append( " = new " ).append(
                                    op.getField().arrayType() ).append( "[" )
                            .append( ip.getChildVars().size() ).append( "];\n" );
                    for ( int i = 0; i < ip.getChildVars().size(); i++ ) {
                        result.append( CodeGenerator.OT_TAB ).append(
                                CodeGenerator.OT_TAB ).append( " TEMP" )
                                .append( rel.getId() ).append( "[" ).append( i )
                                .append( "] = " ).append(
                                        ip.getChildVars().get( i ).getFullName() ).append(
                                        ";\n" );
                    }

                    return result.append( CodeGenerator.OT_TAB ).append(
                            CodeGenerator.OT_TAB ).append( op.getFullName() )
                            .append( " = " ).append( " TEMP" ).append( rel.getId() )
                            .append( ";\n" ).toString();
                } else if ( op.getField().isVoid() && ip.getField().isVoid() ) {
                    return "";
                } else if ( rel.getMethod() == null ) {
                    result.append( op.getFullName() ).append( " = " )
                    	.append( getVarName(ip) ).append( ";\n" );
                    return result.toString();
                }
            }

            Set<String> varNames = new LinkedHashSet<String>();
            for ( Var out : rel.getOutputs() ) {
                varNames.add( out.getFullName() );
            }
            for ( Var inps : rel.getInputs() ) {
                varNames.add( inps.getFullName() );
            }

            String method = rel.getMethod();
            Matcher matcher = PATTERN_VAR_IN_EQUATION.matcher( method );

            boolean methodCallExist = false;

            String parentFullName = rel.getParent().getFullNameForConcat();
            StringBuffer sb = new StringBuffer();
            //take each variable and replace it with the real instance name
            while ( matcher.find() ) {

                String varname = matcher.group( 1 );
                String rep = parentFullName + varname;

                if ( !varNames.contains( rep ) ) {
                    if ( ( varname = rel.getSubstitutions().get( varname ) ) != null ) {
                        rep = varname;
                    } else {
                        rep = "$1";
                        methodCallExist = true;
                    }
                } else if ( ( varname = rel.getSubstitutions().get( rep ) ) != null ) {//"any" type
                  rep = varname;
                }

                matcher.appendReplacement( sb, method.substring( matcher
                        .start(), matcher.start( 1 ) ) //
                        + rep // 
                        + method.substring( matcher.end( 1 ), matcher.end() ) ); //
            }

            matcher.appendTail( sb );

            // TODO - add casting to other types as well
            if ( rel.getFirstOutput().getType().equals( TYPE_INT )
                    && ( methodCallExist || !getMaxType( rel.getInputs() ).equals(
                            TYPE_INT ) ) ) {

                String[] eq = sb.toString().split( "=" );
                return result.append( eq[0] ).append( " = (" )
                        .append( TYPE_INT ).append( ") (" ).append( eq[1] )
                        .append( " );\n" ).toString();
            }

            return sb.append( ";\n" ).toString();
        }
        
        private String checkAliasInputs( Map<String, String> varNameSubstitutions ) {
            StringBuilder assigns = new StringBuilder();
            for ( Var input : rel.getInputs() ) {
                if ( input.getField().isAlias() ) {

                    String alias_tmp = getRelAliasTmpName( input, varNameSubstitutions, true );
                    
                    if ( input.getChildVars().isEmpty()
                            && !( (Alias) input.getField() ).isInitialized() ) {
                        //TODO check assigns overwrite (before refactoring, 1.75.2.2)
                        assigns.append( input.getType() ).append( " " ).append(
                                alias_tmp ).append( " = null;\n" ).append(
                                cg.getOffset() );
                    } else {

                        assigns.append( checkObjectArrayDimension( alias_tmp, input
                                .getType(), input.getChildVars().size() ) );
                        
                        StringBuilder declarations = new StringBuilder();
                        StringBuilder varList = new StringBuilder();

                        for ( int k = 0; k < input.getChildVars().size(); k++ ) {
                            Var var = input.getChildVars().get( k );

                            if ( var.getField().isVoid() )
                                continue;

                            String varName;

                            if ( var.getField().isAlias() ) {
                                String aliasTmpFromInput = getRelAliasTmpName( var, varNameSubstitutions, false );

                                declarations.append( cg.getVarsToAlias(
                                        var, aliasTmpFromInput ) );

                                varName = aliasTmpFromInput;

                            } else {
                                varName = getVarName(var);
                            }

                            varList.append( cg.getOffset() ).append(
                                    alias_tmp ).append( "[" ).append(
                                    Integer.toString( k ) ).append( "] = " )
                                    .append( varName ).append( ";\n" );
                        }
                        assigns.append( declarations ).append( varList ).append(
                                cg.getOffset() );
                    }
                }
            }
            return assigns.toString();
        }

        private String checkObjectArrayDimension( String name, String _type, int size ) {

            StringBuilder result = 
                new StringBuilder( _type ).append( " " ).append( name ).append( " = new " );
            
            /*
             * if we have alias as a set of arrays, we should change the declaration
             * as follows: from double[][] tmp = new double[][2]; to double[][] tmp =
             * new double[2][];
             */
            if ( _type.endsWith( "[][]" ) ) {
                return result.append( _type.substring( 0, _type.length() - 4 ) ).append(
                                "[" ).append( size ).append( "][];\n" ).toString();
            }
            return result.append( _type.substring( 0, _type.length() - 2 ) )
                    .append( "[" ).append( size ).append( "];\n" ).toString();
        }

        private String checkAliasOutputs( Map<String, String> outputSubstitutions ) {
            StringBuilder assigns = new StringBuilder();
            Var output = rel.getFirstOutput();
            if ( output.getField().isAlias() ) {

                String alias_tmp = getRelAliasTmpName( output, outputSubstitutions, false );

                for ( int k = 0; k < output.getChildVars().size(); k++ ) {
                    Var varFromAlias = output.getChildVars().get( k );

                    if ( varFromAlias.getField().isVoid() )
                        continue;

                    String varType = varFromAlias.getType();
                    TypeToken token = TypeToken.getTypeToken( varType );

                    if ( token == TypeToken.TOKEN_OBJECT
                            || token == TypeToken.TOKEN_STRING ) {
                        if ( varFromAlias.getField().isAlias() ) {
                            assigns.append( cg.getVarsFromAlias(
                                    varFromAlias, cg.getAliasTmpName( varFromAlias
                                                        .getName() ), alias_tmp, k ) );
                        } else {
                            assigns.append( cg.getOffset() ).append(
                                    varFromAlias.getFullName() ).append( " = (" )
                                    .append( varType ).append( ")" ).append(
                                            alias_tmp ).append( "[" ).append( k )
                                    .append( "];\n" );
                        }
                    } else {
                        assigns.append( cg.getOffset() ).append(
                                varFromAlias.getFullName() ).append( " = ((" )
                                .append( token.getObjType() ).append( ")" ).append(
                                        alias_tmp ).append( "[" ).append( k )
                                .append( "])." ).append( token.getMethod() )
                                .append( "();\n" );
                    }

                }
            }

            return assigns.toString();
        }
    }

    
}

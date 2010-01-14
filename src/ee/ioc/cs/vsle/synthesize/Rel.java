package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

class Rel implements Serializable {

    private Collection<Var> outputs = new LinkedHashSet<Var>();
    private Collection<Var> inputs = new LinkedHashSet<Var>();
    private Collection<SubtaskRel> subtasks = new LinkedHashSet<SubtaskRel>();
    private Collection<Var> exceptions = new LinkedHashSet<Var>();
    //Substitutions appear in the case of alias element access and are used
    //during the code generation, see CodeEmitter.emitEquation()
    //For instance, a.1 is replaced by x in the equation y=a.1
    //EquationSolver produces a method string that is directly used by CodeEmitter. 
    //Variable name a.1 has to be replaced by x in the method string to make it y=x;
    private Map<String, String> substitutions = new LinkedHashMap<String, String>();
    //
    private int relID = 0;
    protected Var parent;
    private String method;
    //declaration in specification
    private String declaration;
    //see RelType
    private int type;
    private Set<Var> unknownInputs = new LinkedHashSet<Var>();
    transient private CodeEmitter emitter;
    
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
        if ( !var.getField().isConstant() ) {
            unknownInputs.remove( var );
        }
    }

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
    
    Collection<Var> getExceptions() {
        return exceptions;
    }

    Collection<SubtaskRel> getSubtasks() {
        return subtasks;
    }

    void setMethod(String m) {
        method = m;
    }

    String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object e) {
        return this.relID == ((Rel) e).relID;
    }

    @Override
    public int hashCode() {
        return RelType.REL_HASH + relID;
    }
    
    Var getFirstInput() {
        return inputs.iterator().next();
    }
    
    Var getFirstOutput() {
        return outputs.iterator().next();
    }

    public String getDeclaration() {
        return declaration;
    }
    
    public String getParentObjectName()
    {
        return parent.getFullName();
    }
    
    @Override
    public String toString()
    {
        return getCodeEmitter().emitCode();
    }
    
    
    //for debug!!!
    String printUnknownInputs() {
        return unknownInputs.toString();
    }
    
    void addSubstitutions( Map<String, String> substs ) {
        if ( substs != null )
            substitutions.putAll( substs );
    }
    
    /**************** The code below is related to the code generation ************************************/
    private CodeEmitter getCodeEmitter()
    {
        if(emitter == null)
            emitter = new CodeEmitter();
        return emitter;
    }
	
	private class CodeEmitter
	{
        private final Pattern PATTERN_VAR_IN_EQUATION = Pattern
                .compile( "[^a-zA-Z_]*([a-zA-Z_]{1}[a-zA-Z_0-9\\.]*)" );
	    
	    private String getMaxType(Collection<Var> _inputs) {

	        for ( Var var : _inputs ) {
	            if (!var.getType().equals(TYPE_INT)) {
	                return TYPE_DOUBLE;
	            }
	        }
	        return TYPE_INT;
	    }

	    private String getOutputString() {

            StringBuilder outputString = new StringBuilder();
            Var var = getFirstOutput();

            if ( !TypeUtil.TYPE_VOID.equals( var.getType() ) ) {
                if ( var.getField().isAlias() ) {
                    String alias_tmp = getAliasTmpName( var );

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

	    private String getAliasTmpName( Var var ) {
	        
            String varName = var.getFullNameForConcat().replaceAll( "\\.", "_" );
            return new StringBuilder( TypeUtil.TYPE_ALIAS ).append( "_" )
                    .append( varName ).append( relID ).toString();
        }

	    private String getParametersString( boolean useBrackets ) {
	        
            StringBuilder params = new StringBuilder();
            if ( useBrackets )
                params.append( "(" );

            int j = 0;
            for ( Var var : inputs ) {

                if ( !TypeUtil.TYPE_VOID.equals( var.getType() ) ) {
                    if ( j++ > 0 )
                        params.append( ", " );

                    if ( var.getField().isAlias() ) {
                        params.append( getAliasTmpName( var ) );
                    } else {
                        params.append( var.getFullName() );
                    }
                }
            }
            if ( useBrackets )
                params.append( ")" );

            return params.toString();
        }

	    private String getSubtaskParametersString() {

            StringBuilder params = new StringBuilder( "(" );

            boolean subExist = false;
            for ( int i = 0; i < subtasks.size(); i++ ) {
                if ( i == 0 ) {
                    subExist = true;
                } else {
                    params.append( ", " );
                }
                params.append( RelType.TAG_SUBTASK );
            }
            if ( subExist && inputs.size() > 0 ) {
                params.append( ", " );
            }

            return params.append( getParametersString( false ) ).append( ")" )
                    .toString();
        }
	    
	    /* 
	     * Emits code for a given relation class
	     */
	    private String emitCode() {

	        if (type == RelType.TYPE_ALIAS) {
	            return "";
	        }
	        else if (type == RelType.TYPE_EQUATION) {
	            return emitEquation();
	            
	        } else if (type == RelType.TYPE_SUBTASK) {
	            // this should not be used in code generation
	            return inputs + " -> " + outputs;

	        } else if ( ( type == RelType.TYPE_JAVAMETHOD )
	                || ( type == RelType.TYPE_METHOD_WITH_SUBTASK )) {

	            return emitMethod();

	        } else {
	            return emitAssignment();
	        }
	    }

        private String emitAssignment() {

            Var ip = getFirstInput();
            Var op = getFirstOutput();
            StringBuilder assigns = new StringBuilder();
            int i = 0;

            if ( ip.getField().isArray() && op.getField().isAlias() ) {
                for ( Var childVar : op.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append( childVar )
                            .append( " = " ).append( ip.getFullName() ).append(
                                    "[" ).append( Integer.toString( i++ ) )
                            .append( "];\n" );
                }
            } else if ( op.getField().isArray() && ip.getField().isAlias() ) {
                for ( Var childVar : ip.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append(
                            op.getFullName() ).append( "[" ).append(
                            Integer.toString( i++ ) ).append( "] = " ).append(
                            childVar ).append( ";\n" );
                }
            } else if ( op.getField().isAlias() && ip.getField().isAlias() ) {
                for ( Var inpChildVar : ip.getChildVars() ) {
                    assigns.append( CodeGenerator.OT_TAB ).append(
                            op.getChildVars().get( i++ ) ).append( " = " )
                            .append( inpChildVar ).append( ";\n" );
                }
            } else
                assigns.append( op.getFullName() ).append( " = " ).append(
                        ip.getFullName() ).append( ";\n" );

            return assigns.toString();
        }

        private String emitMethod() {

            String output = getOutputString();
            String meth;

            if ( Table.TABLE_KEYWORD.equals( method ) ) {

                StringBuilder cast = new StringBuilder();

                if ( !getOutputs().isEmpty() ) {

                    cast.append( "(" );

                    String _type = getFirstOutput().getType();

                    TypeToken token = TypeToken.getTypeToken( _type );

                    if ( token == TypeToken.TOKEN_OBJECT ) {
                        cast.append( _type );
                    } else {
                        cast.append( token.getObjType() );
                    }

                    cast.append( ")" );
                }
                meth = cast.append( "ProgramContext.queryTable" ).toString();
            } else {
                meth = parent.getFullNameForConcat() + method;
            }
            
            String params = ( type == RelType.TYPE_JAVAMETHOD ) ? getParametersString( true )
                    : getSubtaskParametersString();
            
            return new StringBuilder( checkAliasInputs() ).append(
                    output.length() > 0 ? output + " = " : "" ).append( meth )
                    .append( params ).append( ";\n" ).append(
                            checkAliasOutputs() ).toString();
        }

        private String emitEquation() {
            
            StringBuilder result = new StringBuilder();
            // if its an array assingment
            if ( inputs.size() == 0 && outputs.size() == 1 ) {
                Var op = getFirstOutput();

                if ( op.getField().isPrimOrStringArray() ) {
                    String[] split = method.split( "=" );
                    result.append( op.getField().getType() ).append( " " )
                            .append( " TEMP" ).append(
                                    Integer.toString( RelType.auxVarCounter ) )
                            .append( "=" ).append( split[1] ).append( ";\n" );
                    result.append( CodeGenerator.OT_TAB ).append(
                            CodeGenerator.OT_TAB ).append( op.getFullName() )
                            .append( " = TEMP" ).append(
                                    Integer.toString( RelType.auxVarCounter ) )
                            .append( ";\n" );
                    RelType.auxVarCounter++;
                    return result.toString();

                }
            }

            if ( inputs.size() == 1 && outputs.size() == 1 ) {
                Var ip = getFirstInput();
                Var op = getFirstOutput();

                if ( ip.getField().isArray() && op.getField().isAlias() ) {

                    for ( int i = 0; i < getFirstOutput().getChildVars().size(); i++ ) {
                        result.append( op.getChildVars().get( i ) ).append(
                                " = " ).append( ip.getFullName() ).append( "[" )
                                .append( i ).append( "];\n" );
                    }
                    return result.toString();
                } else if ( op.getField().isArray() && ip.getField().isAlias() ) {

                    result.append( op.getField().getType() ).append( " TEMP" )
                            .append( relID ).append( " = new " ).append(
                                    op.getField().arrayType() ).append( "[" )
                            .append( ip.getChildVars().size() ).append( "];\n" );
                    for ( int i = 0; i < ip.getChildVars().size(); i++ ) {
                        result.append( CodeGenerator.OT_TAB ).append(
                                CodeGenerator.OT_TAB ).append( " TEMP" )
                                .append( relID ).append( "[" ).append( i )
                                .append( "] = " ).append(
                                        ip.getChildVars().get( i ) ).append(
                                        ";\n" );
                    }

                    return result.append( CodeGenerator.OT_TAB ).append(
                            CodeGenerator.OT_TAB ).append( op.getFullName() )
                            .append( " = " ).append( " TEMP" ).append( relID )
                            .append( ";\n" ).toString();
                } else if ( method == null ) {
                    return result.append( op.getFullName() ).append( " = " )
                            .append( ip.getFullName() ).append( ";\n" )
                            .toString();
                }
            }

            Set<String> varNames = new LinkedHashSet<String>();
            for ( Var out : getOutputs() ) {
                varNames.add( out.getFullName() );
            }
            for ( Var inps : getInputs() ) {
                varNames.add( inps.getFullName() );
            }

            Matcher matcher = PATTERN_VAR_IN_EQUATION.matcher( method );

            boolean methodCallExist = false;

            String parentFullName = parent.getFullNameForConcat();
            StringBuffer sb = new StringBuffer();
            //take each variable and replace it with the real instance name
            while ( matcher.find() ) {

                String varname = matcher.group( 1 );
                String rep = parentFullName + varname;

                if ( !varNames.contains( rep ) ) {
                    if ( ( varname = substitutions.get( varname ) ) != null ) {
                        rep = varname;
                    } else {
                        rep = "$1";
                        methodCallExist = true;
                    }
                }

                matcher.appendReplacement( sb, method.substring( matcher
                        .start(), matcher.start( 1 ) ) //
                        + rep // 
                        + method.substring( matcher.end( 1 ), matcher.end() ) ); //
            }

            matcher.appendTail( sb );

            // TODO - add casting to other types as well
            if ( getFirstOutput().getType().equals( TYPE_INT )
                    && ( methodCallExist || !getMaxType( inputs ).equals(
                            TYPE_INT ) ) ) {

                String[] eq = sb.toString().split( "=" );
                return result.append( eq[0] ).append( " = (" )
                        .append( TYPE_INT ).append( ") (" ).append( eq[1] )
                        .append( " );\n" ).toString();
            }

            return sb.append( ";\n" ).toString();
        }
	    
	    private String checkAliasInputs() {
	        StringBuilder assigns = new StringBuilder();
	        for ( Var input : inputs ) {
	            if ( input.getField().isAlias() ) {

	                String alias_tmp = getAliasTmpName( input );

	                if ( input.getChildVars().isEmpty()
	                        && !( (Alias) input.getField() ).isInitialized() ) {
	                    //TODO check assigns overwrite (before refactoring, 1.75.2.2)
	                    assigns.append( input.getType() ).append( " " ).append(
	                            alias_tmp ).append( " = null;\n" ).append(
	                            CodeGenerator.getOffset() );
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
	                            String aliasTmpFromInput = getAliasTmpName( var );

	                            declarations.append( CodeGenerator.getVarsToAlias(
	                                    var, aliasTmpFromInput ) );

	                            varName = aliasTmpFromInput;

	                        } else {
	                            varName = var.getFullName();
	                        }

	                        varList.append( CodeGenerator.getOffset() ).append(
	                                alias_tmp ).append( "[" ).append(
	                                Integer.toString( k ) ).append( "] = " )
	                                .append( varName ).append( ";\n" );
	                    }
	                    assigns.append( declarations ).append( varList ).append(
	                            CodeGenerator.getOffset() );
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

	    private String checkAliasOutputs() {
	        StringBuilder assigns = new StringBuilder();
	        Var output = getFirstOutput();
	        if ( output.getField().isAlias() ) {

	            String alias_tmp = getAliasTmpName( output );

	            for ( int k = 0; k < output.getChildVars().size(); k++ ) {
	                Var varFromAlias = output.getChildVars().get( k );

	                if ( varFromAlias.getField().isVoid() )
	                    continue;

	                String varType = varFromAlias.getType();
	                TypeToken token = TypeToken.getTypeToken( varType );

	                if ( token == TypeToken.TOKEN_OBJECT
	                        || token == TypeToken.TOKEN_STRING ) {
	                    if ( varFromAlias.getField().isAlias() ) {
	                        assigns.append( CodeGenerator.getVarsFromAlias(
	                                varFromAlias, CodeGenerator
	                                        .getAliasTmpName( varFromAlias
	                                                .getName() ), alias_tmp, k ) );
	                    } else {
	                        assigns.append( CodeGenerator.getOffset() ).append(
	                                varFromAlias.getFullName() ).append( " = (" )
	                                .append( varType ).append( ")" ).append(
	                                        alias_tmp ).append( "[" ).append( k )
	                                .append( "];\n" );
	                    }
	                } else {
	                    assigns.append( CodeGenerator.getOffset() ).append(
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

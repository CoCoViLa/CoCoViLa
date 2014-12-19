package ee.ioc.cs.vsle.parser;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_ANY;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_DOUBLE;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_INT;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_THIS;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;
import ee.ioc.cs.vsle.equations.EquationSolver.Relation;
import ee.ioc.cs.vsle.parser.SpecificationLoader.SpecificationNotFoundException;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageBaseListener;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasDeclarationContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasDefinitionContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasStructureContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AxiomContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.ClassTypeContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.ConstantVariableContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.EquationContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.ExceptionListContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.GoalContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.MetaInterfaseContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.SpecificationVariableContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.SpecificationVariableDeclaratorContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.StaticVariableContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.SubtaskContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.SubtaskListContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.SuperMetaInterfaceContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.TypeContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableAssignmentContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableDeclarationContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableDeclaratorAssignerContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableDeclaratorInitializerContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableIdentifierContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.VariableInitializerContext;
import ee.ioc.cs.vsle.synthesize.AliasException;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassRelation;
import ee.ioc.cs.vsle.synthesize.CodeGenerator;
import ee.ioc.cs.vsle.synthesize.EquationException;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.synthesize.SpecParseException;
import ee.ioc.cs.vsle.synthesize.SubtaskClassRelation;
import ee.ioc.cs.vsle.synthesize.UnknownVariableException;
import ee.ioc.cs.vsle.table.Table;
import ee.ioc.cs.vsle.util.TypeToken;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.AliasLength;
import ee.ioc.cs.vsle.vclass.ClassField;

public class SpecificatioLanguageListenerImpl extends SpecificationLanguageBaseListener {
	
	private final SpecificationLoader specificationLoader;
	private AnnotatedClass annotatedClass;
	private ClassFieldDeclarator classFieldDeclarator;
	private String specificationName;
	private Alias currentAlias;
	
	public SpecificatioLanguageListenerImpl(SpecificationLoader specificationLoader, String specificationName) {
		this.specificationLoader = specificationLoader;
		this.specificationName = specificationName;
	}

	@Override
	public void enterMetaInterfase(MetaInterfaseContext ctx) {
		if (specificationName == null) {
			specificationName = ctx.IDENTIFIER().getText();
		}
		annotatedClass = new AnnotatedClass(specificationName);
		classFieldDeclarator = new ClassFieldDeclarator();
        ClassField specObjectName = new ClassField( CodeGenerator.SPEC_OBJECT_NAME, "String" );
        annotatedClass.addField( specObjectName );
	}
	
	@Override
	public void enterGoal(GoalContext ctx) {
		List<VariableIdentifierContext> outputVariableContextList = ctx.outputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.outputVariables.variableIdentifier();
		List<VariableIdentifierContext> iputVariableContextList = ctx.inputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.inputVariables.variableIdentifier();
        
		ClassRelation classRelation = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, ctx.getText() );
        
        for (VariableIdentifierContext outputVariableContext : outputVariableContextList) {
        	classRelation.addOutput( outputVariableContext.getText(), annotatedClass.getFields() );
		}
        for (VariableIdentifierContext iputVariableContext : iputVariableContextList) {
        	classRelation.addInput( iputVariableContext.getText(), annotatedClass.getFields() );
		}

        annotatedClass.addClassRelation( classRelation );
	}
	
	@Override
	public void enterSuperMetaInterface(SuperMetaInterfaceContext ctx) {
		for (ClassTypeContext classTypeContext : ctx.classType()) {
			String superSpecificationName = classTypeContext.getText();
				
			AnnotatedClass superClass = specificationLoader.getSpecification(superSpecificationName);
			if ( superClass != null ) {
				annotatedClass.addSuperClass( superClass );
			} else {
				throw new SpecParseException( "Unable to parse superclass " + superSpecificationName + " of " + annotatedClass.getName() );
			}
		}
	}
	
	@Override
	public void enterVariableDeclaration(VariableDeclarationContext ctx) {
		classFieldDeclarator.setType(ctx.type().getText());
	}
	
	@Override
	public void enterStaticVariable(StaticVariableContext ctx) {
		classFieldDeclarator.setStatic(true);
	}
	
	@Override
	public void enterConstantVariable(ConstantVariableContext ctx) {
		classFieldDeclarator.setConstant(true);
	}
	
	public void variableDeclarator(String name, String value, boolean solveEquation) {
		if(classFieldDeclarator.isConstant){
			classFieldDeclarator.addClassField(name, value);
			if(solveEquation)
				solveEquation(name.concat("=").concat(value));
		}else{
			classFieldDeclarator.addClassField(name);
			if(solveEquation)
				solveEquation(name.concat("=").concat(value));
			else
				assignVariable(name, value);
		}
	}
	
	@Override
	public void enterVariableDeclaratorAssigner(VariableDeclaratorAssignerContext ctx) {
		variableDeclarator(ctx.IDENTIFIER().getText(), ctx.variableAssigner().getText(), false);
	}
	
	@Override
	public void enterSpecificationVariable(SpecificationVariableContext ctx) {
		String name = ctx.IDENTIFIER().getText();
		classFieldDeclarator.addClassField(name);
	}
	
	@Override
	public void enterSpecificationVariableDeclarator(SpecificationVariableDeclaratorContext ctx) {
		String fullVariableName = classFieldDeclarator.getName().concat(".").concat(ctx.IDENTIFIER().getText());
		String equation = fullVariableName.concat("=").concat(ctx.expression().getText());
		solveEquation(equation);
	}
	
	@Override
	public void enterVariableDeclaratorInitializer(VariableDeclaratorInitializerContext ctx) {
		String name = ctx.IDENTIFIER().getText();
		VariableInitializerContext variableInitializerContext = ctx.variableInitializer();
		if(variableInitializerContext==null){
			classFieldDeclarator.addClassField(name);
		}else{
			variableDeclarator(name, variableInitializerContext.getText(), true);
		}
	}
	
	@Override
	public void enterVariableAssignment(VariableAssignmentContext ctx) {
		assignVariable(ctx.variableIdentifier().getText(), ctx.variableAssigner().getText());
	}
	
	protected void assignVariable(String variableName, String variableValue){
		String method = variableName.concat("=").concat(variableValue);
        ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, method);
        classRelation.addOutput( variableName, annotatedClass.getFields() );
        classRelation.setMethod( method );
        //TODO: IMPLEMENT ANY
        annotatedClass.addClassRelation( classRelation );
	}
	
	@Override
	public void exitVariableDeclaration(VariableDeclarationContext ctx) {
		classFieldDeclarator.cleanUp();
	}
	
	@Override
	public void enterEquation(EquationContext ctx) {
		solveEquation(ctx.getText());
	}
	
	@Override
	public void enterAxiom(AxiomContext ctx) {
		SubtaskListContext subtaskListContext = ctx.subtaskList();
		List<SubtaskContext> subtaskContextList = subtaskListContext == null ? Collections.<SubtaskContext>emptyList() : subtaskListContext.subtask();
		List<VariableIdentifierContext> outputVariableContextList = ctx.outputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.outputVariables.variableIdentifier();
		List<VariableIdentifierContext> iputVariableContextList = ctx.inputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.inputVariables.variableIdentifier();
		String method = ctx.method.getText();

//        if ( statement.getOutputs().length > 0 ) {
//            if ( statement.getOutputs()[ 0 ].indexOf( "*" ) >= 0 ) {
//                getWildCards( classList, statement.getOutputs()[ 0 ] );
//            }
//        }
		
        ClassRelation classRelation = new ClassRelation( RelType.TYPE_JAVAMETHOD, ctx.getText());

        
//TODO: handle this in lexer
//        if ( statement.getOutputs().length == 0 ) {
//            throw new SpecParseException( "Error in line \n" + lt.getOrigSpecLine() + "\nin class "
//                    + className + ".\nAn axiom can not have an empty output." );
//        }

        for (VariableIdentifierContext oututVariableContext : outputVariableContextList) {
        	classRelation.addOutput(oututVariableContext.getText(), annotatedClass.getFields() );
		}

		classRelation.setMethod(method);

        if( Table.TABLE_KEYWORD.equals( classRelation.getMethod() ) ) {
            classRelation.getExceptions().clear();
            classRelation.getExceptions().add( new ClassField( "java.lang.Exception", "exception" ) );
        }

        for (VariableIdentifierContext inputVariableContext : iputVariableContextList) {
        	String variableName = checkAliasLength(inputVariableContext.getText());
        	classRelation.addInput(variableName, annotatedClass.getFields() );
		}
        
        for (SubtaskContext subtaskContext : subtaskContextList) {
        	String contextName = subtaskContext.context == null ? null : subtaskContext.context.getText();
        	List<VariableIdentifierContext> subtaskInputVariableContextList = subtaskContext.inputVariables.variableIdentifier();
        	List<VariableIdentifierContext> subtaskOutputVariableContextList = subtaskContext.outputVariables.variableIdentifier();
        	
        	Collection<ClassField> varsForSubtask = annotatedClass.getFields();
        	SubtaskClassRelation subtask;
        	
        	// this denotes independant subtask,
        	// have to make sure that this class has
        	// already been parsed
        	if ( contextName != null ) {
        		AnnotatedClass context = specificationLoader.getSpecification(contextName);
        		varsForSubtask = context.getFields();
        		
        		ClassField contextCF = new ClassField( "_" + contextName.toLowerCase(), contextName, true );
        		
        		subtask = SubtaskClassRelation.createIndependentSubtask( subtaskContext.getText(), contextCF );
        	} else {
        		subtask = SubtaskClassRelation.createDependentSubtask( subtaskContext.getText() );
        	}
        	
        	for (VariableIdentifierContext subtaskOutputVariable : subtaskOutputVariableContextList) {
        		subtask.addOutput( subtaskOutputVariable.getText(), varsForSubtask );
			}
        	for (VariableIdentifierContext subtaskInputVariableContext : subtaskInputVariableContextList) {
        		subtask.addInput(subtaskInputVariableContext.getText(), varsForSubtask );
			}
        	
        	classRelation.addSubtask( subtask );
		}
        
        if (!subtaskContextList.isEmpty())
        	classRelation.setType( RelType.TYPE_METHOD_WITH_SUBTASK );

        if ( RuntimeProperties.isLogDebugEnabled() )
            db.p( classRelation );

        if(ctx.exceptionList() != null) {
          for(ClassTypeContext ct : ctx.exceptionList().classType()) {
            classRelation.addException(ct.getText());
          }
        }
        
        annotatedClass.addClassRelation( classRelation );
	}
	
  @Override
	public void enterAliasDeclaration(AliasDeclarationContext ctx) {
		String aliasName = ctx.IDENTIFIER().getText();
		TypeContext typeContext = ctx.type();
		String aliasType = typeContext != null ? typeContext.getText() : null;

		if(annotatedClass.hasField(aliasType))
            throw new SpecParseException( "Variable " + aliasName + " declared more than once in class "
            		+ annotatedClass.getName() + ", line: " + ctx.getText() );
		
		currentAlias = new Alias(aliasName, aliasType);
		annotatedClass.addField(currentAlias);
	}
	
	@Override
	public void enterAliasDefinition(AliasDefinitionContext ctx) {
		String aliasFullName = ctx.variableIdentifier().getText();
		ClassField aliasClassField = annotatedClass.getFieldByName(aliasFullName);
		boolean isLocalAlias = true;
		
		if(aliasClassField == null){//Go deeper 
			List<TerminalNode> identifierList = ctx.variableIdentifier().IDENTIFIER();
			int lastIndex = identifierList.size() - 1;
			int i = 0;
			AnnotatedClass parentClass = annotatedClass;
			String aliasName = "";
			for (TerminalNode terminalNode : identifierList) {
				if (i == lastIndex){
					aliasName = terminalNode.getText();
				}else{
					ClassField parentVar = parentClass.getFieldByName(terminalNode.getText());
					parentClass = specificationLoader.getSpecification(parentVar.getType());
					isLocalAlias = false;
				}
				i++;
			}
			aliasClassField = parentClass.getFieldByName(aliasName);
		}
		
		
		if(aliasClassField == null)
			throw new UnknownVariableException(aliasFullName, ctx.getText());
		if(aliasClassField instanceof Alias){
			Alias alias = (Alias) aliasClassField;
			if(!isLocalAlias){
				alias = new Alias( aliasFullName, alias.getVarType() );
				annotatedClass.addField(alias);
			}
			currentAlias = alias;
		}else
			throw new AliasException("Variable '".concat(aliasFullName).concat("' is not an alias!"));
	}

	@Override
	public void enterAliasStructure(AliasStructureContext ctx) {
		String lineNext = ctx.getParent().getText();
		String[] vars = new String[1];
		if(ctx.variableAlias != null){
			int i = 0;
			List<VariableIdentifierContext> variableIdentifierList = ctx.variableAlias.variableIdentifier();
			vars = new String[variableIdentifierList.size()];
			for(VariableIdentifierContext variableIdentifierContext : variableIdentifierList){
				vars[i] = variableIdentifierContext.getText();
				i++;
			}
		}else{
			vars[0] = ctx.wildcardAliasName.getText();
		}
        
        currentAlias.addAll( vars, annotatedClass.getFields(), specificationLoader);

        ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS, lineNext );

        classRelation.addInputs( vars, annotatedClass.getFields() );
        classRelation.setMethod( TypeUtil.TYPE_ALIAS );
        classRelation.addOutput( currentAlias.getName(), annotatedClass.getFields() );
        annotatedClass.addClassRelation( classRelation );

        if ( RuntimeProperties.isLogDebugEnabled() )
            db.p( classRelation );

        if ( !currentAlias.isWildcard() ) {
            classRelation = new ClassRelation( RelType.TYPE_ALIAS, lineNext );
            classRelation.addOutputs( vars, annotatedClass.getFields() );
            classRelation.setMethod( TypeUtil.TYPE_ALIAS );
            classRelation.addInput( currentAlias.getName(), annotatedClass.getFields() );
            annotatedClass.addClassRelation( classRelation );
            if ( RuntimeProperties.isLogDebugEnabled() )
                db.p( classRelation );
        }

        currentAlias.setInitialized( true );
	}
	
	@Override
	public void exitAliasDeclaration(AliasDeclarationContext ctx) {
//         Alias alias = null;
//         
//
//         ClassField var = getVar( name, annClass.getFields() );//aliasDefinition
//
//         if ( var != null && !var.isAlias() ) {
//         } else if ( var != null && var.isAlias() ) {//aliasDefinition
//             alias = (Alias) var;
//             if ( alias.isInitialized() ) {
//                 throw new SpecParseException( "Alias " + name + " has already been initialized and cannot be overriden, class " + 
//                         className + ", line: " + lt.getOrigSpecLine() );
//             }
//         } else if ( statement.isAssignment() ) {//aliasDefinition
//             // if its an assignment, check if alias has already
//             // been declared
//                 if ( ( name.indexOf( "." ) == -1 ) && !containsVar( annClass.getFields(), name ) ) {
//                     throw new UnknownVariableException( "Alias " + name + " not declared", lt.getOrigSpecLine() );
//
//                 } else if ( name.indexOf( "." ) > -1 ) {
//                     // here we have to dig deeply
//                     int ind = name.indexOf( "." );
//
//                     String parent = name.substring( 0, ind );
//                     String leftFromName = name.substring( ind + 1, name.length() );
//
//                     ClassField parentVar = getVar( parent, annClass.getFields() );
//                     String parentType = parentVar.getType();
//
//                     AnnotatedClass parentClass = classList.getType( parentType );
//
//                     while ( leftFromName.indexOf( "." ) > -1 ) {
//
//                         ind = leftFromName.indexOf( "." );
//                         parent = leftFromName.substring( 0, ind );
//                         leftFromName = leftFromName.substring( ind + 1, leftFromName.length() );
//
//                         parentVar = parentClass.getFieldByName( parent );
//
//                         parentType = parentVar.getType();
//                         parentClass = classList.getType( parentType );
//                     }
//
//                     if ( !parentClass.hasField( leftFromName ) ) {
//                         throw new UnknownVariableException( "Variable " + leftFromName
//                                 + " is not declared in class " + parentClass, lt.getOrigSpecLine() );
//                     }
//
//                     Alias aliasDeclaration = (Alias) parentClass.getFieldByName( leftFromName );
//
//                     if( aliasDeclaration.isInitialized() ) {
//                         throw new SpecParseException( "Alias " + aliasDeclaration.getName() + 
//                                 " has already been initialized and cannot be overriden, class " + 
//                                 className + ", line: " + lt.getOrigSpecLine() );
//                     }
//                     
//                     // if everything is ok, create alias
//                     alias = new Alias( name, aliasDeclaration.getVarType() );
//
//                 }
//         }
	}
	
	protected void solveEquation(String equation) throws EquationException, UnknownVariableException{
        EquationSolver solver = new EquationSolver();
        solver.solve( equation );
        next: for ( Relation rel : solver.getRelations() ) {
            if ( RuntimeProperties.isLogDebugEnabled() )
                db.p( "equation: " + rel );
            String[] pieces = rel.getRel().split( ":" );
            String method = rel.getExp();
            String out = pieces[ 2 ].trim();

            // cannot assign new values for constants
            ClassField tmp = annotatedClass.getFieldByName(checkAliasLength(out));
            if ( tmp != null && ( tmp.isConstant() || tmp.isAliasLength() ) ) {
                db.p( "Ignoring constant as equation output: " + tmp );
                continue;
            }
            // if one variable is used on both sides of "=", we
            // cannot use such relation.
            String[] inputs = pieces[ 1 ].trim().split( " " );
            for ( int j = 0; j < inputs.length; j++ ) {
                if ( inputs[ j ].equals( out ) ) {
                    if ( RuntimeProperties.isLogDebugEnabled() )
                        db.p( " - unable use this equation because variable " + out
                                + " appears on both sides of =" );
                    continue next;
                }
            }

            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, equation );

            classRelation.addOutput( out, annotatedClass.getFields() );

            // checkAliasLength( inputs, annClass.getFields(), className );
            for ( int i = 0; i < inputs.length; i++ ) {
                String initial = inputs[ i ];
                inputs[ i ] = checkAliasLength(inputs[i]);
                String name = inputs[ i ];
                if ( name.startsWith( "*" ) ) {
                    name = inputs[ i ].substring( 1 );
                }
                method = method.replaceAll( "\\$" + initial + "\\$", name );
            }
            method = method.replaceAll( "\\$" + out + "\\$", out );

            //TODO: IMPLEMENT ANY
            checkAnyType(out, inputs);

            if ( !inputs[ 0 ].equals( "" ) ) {
                classRelation.addInputs( inputs, annotatedClass.getFields() );
            }
            classRelation.setMethod( method );
            annotatedClass.addClassRelation( classRelation );
            if ( RuntimeProperties.isLogDebugEnabled() )
                db.p( "Equation: " + classRelation );

        }
	}
	
    public String checkAliasLength(String variableName)
            throws UnknownVariableException {
        // check if inputs contain <alias>.lenth variable
        if ( variableName.endsWith( ".length" ) ) {
            int index = variableName.lastIndexOf( ".length" );
            String aliasName = variableName.substring( 0, index );
            ClassField field = annotatedClass.getFieldByName(aliasName);
            if ( field != null && field.isAlias() ) {
                Alias alias = (Alias) field;
                String aliasLengthName = aliasName + "_LENGTH";
                if (annotatedClass.hasField(aliasLengthName)) {
                    return aliasLengthName;
                }
                int length = alias.getVars().size();
                AliasLength var = new AliasLength( alias, annotatedClass.getName() );
                annotatedClass.addField( var );
                //if value cannot be determined here, it will be defined in ProgramCreator
                if(!alias.isWildcard() && alias.isInitialized() ) {
                    String meth = aliasLengthName + " = " + length;
                    ClassRelation cr = new ClassRelation( RelType.TYPE_EQUATION, meth );
                    cr.addOutput( aliasLengthName, annotatedClass.getFields() );
                    cr.setMethod( meth );
                    annotatedClass.addClassRelation( cr );
                }
                return aliasLengthName;

            }
            throw new UnknownVariableException( "Alias " + aliasName + " not found in " + annotatedClass.getName() );
        }
        return variableName;
    }
    
    private void checkAnyType( String output, String input) throws UnknownVariableException {
        checkAnyType( output, new String[] { input });
    }

    // TODO - implement _any_!!!
    public void checkAnyType( String output, String[] inputs)
            throws UnknownVariableException {
        ClassField out = annotatedClass.getFieldByName(output);

        if ( out == null || !out.getType().equals( TYPE_ANY ) ) {
            return;
        }

        String newType = TYPE_ANY;

        for ( int i = 0; i < inputs.length; i++ ) {
            ClassField in = annotatedClass.getFieldByName(inputs[ i ]);

            if ( in == null ) {
                try {
                    Integer.parseInt( inputs[ i ] );
                    newType = TYPE_INT;
                    continue;
                } catch ( NumberFormatException ex ) {
                }

                try {
                    Double.parseDouble( inputs[ i ] );
                    newType = TYPE_DOUBLE;
                    continue;
                } catch ( NumberFormatException ex ) {
                }

                if ( inputs[ i ] != null && inputs[ i ].trim().equals( "" ) ) {
                    newType = TYPE_DOUBLE;// TODO - tmp
                    continue;
                }

                throw new UnknownVariableException( inputs[ i ] );
            }
            if(in.isAny()) {
            	newType = in.getAnySpecificType();
            	continue;
            }
            else if ( i == 0 ) {
                newType = in.getType();
                continue;
            }
            TypeToken token = TypeToken.getTypeToken( newType );

            TypeToken tokenIn = TypeToken.getTypeToken( in.getType() );

            if ( token != null && tokenIn != null && token.compareTo( tokenIn ) < 0 ) {
                newType = in.getType();
            }
        }

        if(!TYPE_ANY.equals(newType))
        	out.setAnySpecificType( newType );
    }
	
	public AnnotatedClass getAnnotatedClass() {
		return annotatedClass;
	}

	private class ClassFieldDeclarator{
		private String type;
		private String name;
		private boolean isStatic = false;
		private boolean isConstant = false;
		private AnnotatedClass classFieldAnnotatedClass;

		public void addClassField(String name) {
			this.name = name;
			if(isConstant)
				throw new SpecParseException("Field '".concat(name).concat("' was declared as static, but have no value"));
			ClassField classField = new ClassField(name, type, isSpecificationClass());
			classField.setStatic(isStatic);
			annotatedClass.addField(classField);
			//If object of currient type is on scheme
            if ( isSpecificationClass() && TYPE_THIS.equals(annotatedClass.getName())  && specificationLoader.isSchemeObject(name)) {
                String s = name + "." + CodeGenerator.SPEC_OBJECT_NAME;
                String meth = s + " = " + "\"" + name + "\"";

                ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, meth );

                classRelation.addOutput( s, annotatedClass.getFields() );
                classRelation.setMethod( meth );
                annotatedClass.addClassRelation( classRelation );
            }
		}

		public void addClassField(String name, String value){
			this.name = name;
			ClassField classField = new ClassField(name, type, value, isConstant);
			classField.setStatic(isStatic);
			classField.setSchemeObject(isSpecificationClass());
			annotatedClass.addField(classField);
		}
		
		public void cleanUp() {
			type = null;
			classFieldAnnotatedClass = null;
			isStatic = false;
			isConstant = false;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			try{
				classFieldAnnotatedClass = specificationLoader.getSpecification(type);
			}catch(SpecificationNotFoundException e){
				//TODO: check if it is not java class and throw exception if it is so.
			}
			this.type = type;
		}

		public boolean isStatic() {
			return isStatic;
		}

		public void setStatic(boolean isStatic) {
			this.isStatic = isStatic;
		}

		public boolean isConstant() {
			return isConstant;
		}

		public void setConstant(boolean isConstant) {
			this.isConstant = isConstant;
		}

		public boolean isSpecificationClass() {
			return classFieldAnnotatedClass != null;
		}
		
		public String getName(){
			return name;
		}
		
	}
}

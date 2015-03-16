package ee.ioc.cs.vsle.parser;

import static ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.*;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_ANY;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_DOUBLE;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_INT;

import java.util.*;

import ee.ioc.cs.vsle.synthesize.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import ee.ioc.cs.vsle.equations.EquationSolver;
import ee.ioc.cs.vsle.equations.EquationSolver.Relation;
import ee.ioc.cs.vsle.parser.SpecificationLoader.SpecificationNotFoundException;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageBaseListener;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasDeclarationContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasDefinitionContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AliasStructureContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.AxiomContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.ClassOrInterfaceTypeContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.ConstantVariableContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.EquationContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.GoalContext;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.MetaInterfaceContext;
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
import ee.ioc.cs.vsle.table.Table;
import ee.ioc.cs.vsle.util.TypeToken;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.AliasLength;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecificationLanguageListenerImpl extends SpecificationLanguageBaseListener implements ANTLRErrorListener {

  private static final Logger logger = LoggerFactory.getLogger(SpecificationLanguageListenerImpl.class);

	private final SpecificationLoader specificationLoader;
	private AnnotatedClass annotatedClass;
  private List<StatementAnnotation> currentStatementAnnotations;
	private ClassFieldDeclarator classFieldDeclarator;
	private String specificationName;
	private Alias currentAlias;

	public SpecificationLanguageListenerImpl(SpecificationLoader specificationLoader, String specificationName) {
		this.specificationLoader = specificationLoader;
		this.specificationName = specificationName;
	}

	@Override
	public void enterMetaInterface(MetaInterfaceContext ctx) {
		if (specificationName == null) {
			specificationName = ctx.Identifier().getText();
		}
		annotatedClass = new AnnotatedClass(specificationName);
		classFieldDeclarator = new ClassFieldDeclarator();
        ClassField specObjectName = new ClassField( CodeGenerator.SPEC_OBJECT_NAME, "String" );
        annotatedClass.addField(specObjectName);
	}
	
	@Override
	public void enterGoal(GoalContext ctx) {
		List<VariableIdentifierContext> outputVariableContextList = ctx.outputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.outputVariables.variableIdentifier();
		List<VariableIdentifierContext> iputVariableContextList = ctx.inputVariables == null ? Collections.<VariableIdentifierContext>emptyList() : ctx.inputVariables.variableIdentifier();
        
		ClassRelation classRelation = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, ctx.getText() );
    classRelation.setAnnotations(currentStatementAnnotations);
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
		for (ClassOrInterfaceTypeContext classTypeContext : ctx.classOrInterfaceType()) {
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
		classFieldDeclarator.setType(ctx.type());
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
    if (classFieldDeclarator.isConstant){
      classFieldDeclarator.addClassField(name, value);
    }
    else {
      classFieldDeclarator.addClassField(name);
      if (solveEquation) {
        solveEquation(name.concat("=").concat(value));
      }
      else {
        assignVariable(name, value);
      }
    }
  }

	@Override
	public void enterVariableDeclaratorAssigner(VariableDeclaratorAssignerContext ctx) {
		VariableAssignerContext variableAssignerContext = ctx.variableAssigner();
		CreatorContext creatorContext = variableAssignerContext.creator();
		String value;
		if(creatorContext != null) {
			value = "new " + creatorContext.getText();
		}
		else {
			value = variableAssignerContext.getText();
		}
		variableDeclarator(ctx.Identifier().getText(), value, false);
	}
	
	@Override
	public void enterSpecificationVariable(SpecificationVariableContext ctx) {
		String name = ctx.Identifier().getText();
		classFieldDeclarator.addClassField(name);
	}
	
	@Override
	public void enterSpecificationVariableDeclarator(SpecificationVariableDeclaratorContext ctx) {
		String fullVariableName = classFieldDeclarator.getName().concat(".").concat(ctx.Identifier().getText());
		String equation = fullVariableName.concat("=").concat(ctx.expression().getText());
		solveEquation(equation);
	}
	
	@Override
	public void enterVariableDeclaratorInitializer(VariableDeclaratorInitializerContext ctx) {
		String name = ctx.Identifier().getText();
		VariableInitializerContext variableInitializerContext = ctx.variableInitializer();
		if(variableInitializerContext==null){
			classFieldDeclarator.addClassField(name);
		}else{
			variableDeclarator(name, variableInitializerContext.getText(), true);
		}
	}
	
	@Override
	public void enterVariableAssignment(VariableAssignmentContext ctx) {
		assignVariable(ctx.variableIdentifier().getText(), ctx.variableAssigner());
	}

	protected void assignVariable(String variableName, VariableAssignerContext varValueCtx){
		CreatorContext creatorContext = varValueCtx.creator();
		assignVariable(variableName, creatorContext != null ? "new " + creatorContext.getText() : varValueCtx.getText() );
	}

  protected void assignVariable(String variableName, String variableValue){
    String method = variableName.concat(" = ").concat(variableValue);
    ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, method);
    classRelation.setAnnotations(currentStatementAnnotations);
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

//        if ( statement.getOutputs().length > 0 ) {
//            if ( statement.getOutputs()[ 0 ].indexOf( "*" ) >= 0 ) {
//                getWildCards( classList, statement.getOutputs()[ 0 ] );
//            }
//        }
    ClassRelation classRelation = new ClassRelation( RelType.TYPE_JAVAMETHOD, ctx.getText());
    classRelation.setAnnotations(currentStatementAnnotations);
        
//TODO: handle this in lexer
//        if ( statement.getOutputs().length == 0 ) {
//            throw new SpecParseException( "Error in line \n" + lt.getOrigSpecLine() + "\nin class "
//                    + className + ".\nAn axiom can not have an empty output." );
//        }

    for (VariableIdentifierContext oututVariableContext : outputVariableContextList) {
      classRelation.addOutput(oututVariableContext.getText(), annotatedClass.getFields() );
    }

    final AxiomRealizationContext axiomRealizationContext = ctx.axiomRealization();
    if (axiomRealizationContext instanceof MethodContext) {
      String method = ((MethodContext)axiomRealizationContext).Identifier().getText();
      classRelation.setMethod(method);
    }
    else if (axiomRealizationContext instanceof ExpertTableContext) {
      classRelation.setMethod(Table.TABLE_KEYWORD);
      classRelation.getExceptions().clear();
      classRelation.getExceptions().add( new ClassField( "java.lang.Exception", "exception" ) );
    }
    else if (axiomRealizationContext instanceof LambdaContext) {
      throw new IllegalStateException("Lambdas not implemented yet");
    }

    for (VariableIdentifierContext inputVariableContext : iputVariableContextList) {
      String variableName = checkAliasLength(inputVariableContext.getText());
      classRelation.addInput(variableName, annotatedClass.getFields() );
    }

    for (SubtaskContext subtaskContext : subtaskContextList) {
      String contextName = subtaskContext.context == null ? null : subtaskContext.context.getText();
      List<VariableIdentifierContext> subtaskInputVariableContextList =
              subtaskContext.inputVariables != null
                      ? subtaskContext.inputVariables.variableIdentifier()
                      : Collections.<VariableIdentifierContext>emptyList();
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
    logger.debug( classRelation.toString() );

    if(ctx.exceptionList() != null) {
      for(ClassOrInterfaceTypeContext ct : ctx.exceptionList().classOrInterfaceType()) {
        classRelation.addException(ct.getText());
      }
    }

    annotatedClass.addClassRelation( classRelation );
  }

  @Override
  public void enterAliasDeclaration(AliasDeclarationContext ctx) {
    String aliasName = ctx.Identifier().getText();
    TypeContext typeContext = ctx.type();
    String aliasType = typeContext != null ? typeContext.getText() : null;

    currentAlias = new Alias(aliasName, aliasType);

    try {
      annotatedClass.addField(currentAlias);
    }
    catch(SpecParseException e) {
      e.setLine(ctx.getText());
      throw e;
    }
  }
	
	@Override
	public void enterAliasDefinition(AliasDefinitionContext ctx) {
		String aliasFullName = ctx.variableIdentifier().getText();
		ClassField aliasClassField = annotatedClass.getFieldByName(aliasFullName);
		boolean isLocalAlias = true;
		
		if(aliasClassField == null){//Go deeper 
			List<TerminalNode> identifierList = ctx.variableIdentifier().Identifier();
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
			if ( alias.isInitialized() ) {
				throw new SpecParseException( "Alias " + alias.getName() + " has already been initialized and cannot be overriden, line: " + ctx.getText() );
			}
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
    String[] vars;
    if (ctx.variableAlias != null){
      int i = 0;
      List<VariableIdentifierContext> variableIdentifierList = ctx.variableAlias.variableIdentifier();
      vars = new String[variableIdentifierList.size()];
      for (VariableIdentifierContext variableIdentifierContext : variableIdentifierList){
        vars[i] = variableIdentifierContext.getText();
        i++;
      }
    }
    else if (ctx.wildcardAliasName != null) {
      vars = new String[1];
      vars[0] = ctx.wildcardAliasName.getText();
    }
    else {
      vars = new String[0];
    }

    currentAlias.addAll( vars, annotatedClass.getFields(), specificationLoader);

    ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS, lineNext );
    classRelation.setAnnotations(currentStatementAnnotations);
    classRelation.addInputs( vars, annotatedClass.getFields() );
    classRelation.setMethod( TypeUtil.TYPE_ALIAS );
    classRelation.addOutput( currentAlias.getName(), annotatedClass.getFields() );
    annotatedClass.addClassRelation( classRelation );

    logger.debug( classRelation.toString() );

    if ( !currentAlias.isWildcard() ) {
      classRelation = new ClassRelation( RelType.TYPE_ALIAS, lineNext );
      classRelation.setAnnotations(currentStatementAnnotations);
      classRelation.addOutputs( vars, annotatedClass.getFields() );
      classRelation.setMethod( TypeUtil.TYPE_ALIAS );
      classRelation.addInput( currentAlias.getName(), annotatedClass.getFields() );
      annotatedClass.addClassRelation( classRelation );
      logger.debug( classRelation.toString() );
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

  @Override
  public void enterAnnotatedStatement(@NotNull AnnotatedStatementContext asc) {
    final List<AnnotationContext> annotationContextList = asc.annotation();
    if (annotationContextList != null) {
      currentStatementAnnotations = new ArrayList<StatementAnnotation>(annotationContextList.size());
      for (AnnotationContext ctx : annotationContextList) {
        String name = ctx.annotationName().getText();
        final StatementAnnotation annotation = new StatementAnnotation(name);
        currentStatementAnnotations.add(annotation);
        final ElementValueContext elementValueContext = ctx.elementValue();
        if (elementValueContext != null) {
          final String value = elementValueContext.getText();
          annotation.putValue(value);
        }
        final ElementValuePairsContext elementValuePairsContext = ctx.elementValuePairs();
        if (elementValuePairsContext != null) {
          final List<ElementValuePairContext> elementValuePairContexts = elementValuePairsContext.elementValuePair();
          for (ElementValuePairContext pairContext : elementValuePairContexts) {
            final String id = pairContext.Identifier().getText();
            final String value = pairContext.elementValue().getText();
            annotation.putValue(id, value);
          }
        }
      }
    }
  }


  @Override
  public void exitAnnotatedStatement(@NotNull AnnotatedStatementContext ctx) {
    currentStatementAnnotations = null;
  }

  protected void solveEquation(String equation) throws EquationException, UnknownVariableException{
        EquationSolver solver = new EquationSolver();
        solver.solve( equation );
        next: for ( Relation rel : solver.getRelations() ) {
            logger.debug( "equation: " + rel );
            String[] pieces = rel.getRel().split( ":" );
            String method = rel.getExp();
            String out = pieces[ 2 ].trim();

            // cannot assign new values for constants
            ClassField tmp = annotatedClass.getFieldByName(checkAliasLength(out));
            if ( tmp != null && ( tmp.isConstant() || tmp.isAliasLength() ) ) {
            logger.debug( "Ignoring constant as equation output: " + tmp );
                continue;
            }
            // if one variable is used on both sides of "=", we
            // cannot use such relation.
            String[] inputs = pieces[ 1 ].trim().split( " " );
            for ( int j = 0; j < inputs.length; j++ ) {
                if ( inputs[ j ].equals( out ) ) {
                    logger.debug( " - unable use this equation because variable " + out
                                + " appears on both sides of =" );
                    continue next;
                }
            }

            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, equation );
            classRelation.setAnnotations(currentStatementAnnotations);
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
            logger.debug( "Equation: " + classRelation );

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
                    cr.setAnnotations(currentStatementAnnotations);
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

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
//			System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
//			String message = underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
		String errorLine = underlineError(recognizer, (Token)offendingSymbol, line, charPositionInLine);
		msg = errorLine.concat("\n").concat(msg);
		SpecParseException specParseException = new SpecParseException(msg);
		specParseException.setMetaClass(specificationName);
		specParseException.setLine(Integer.toString(line));
		throw specParseException;
	}

	protected String underlineError(Recognizer recognizer, Token offendingToken, int line, int charPositionInLine) {
		StringBuilder sb = new StringBuilder("\n");
		CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
		String input = tokens.getTokenSource().getInputStream().toString();
		String[] lines = input.split("\n");
		String errorLine = lines[line - 1];
		sb.append(errorLine);
		sb.append("\n");
		for (int i = 0; i < charPositionInLine; i++)
			sb.append(" ");
		int start = offendingToken.getStartIndex();
		int stop = offendingToken.getStopIndex();
		if (start >= 0 && stop >= 0) {
			for (int i = start; i <= stop; i++)
				sb.append("^");
		}
		return sb.toString();
	}

	@Override
	public void reportAmbiguity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, @NotNull ATNConfigSet configs) {

	}

	@Override
	public void reportAttemptingFullContext(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, @NotNull ATNConfigSet configs) {

	}

	@Override
	public void reportContextSensitivity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex, int prediction, @NotNull ATNConfigSet configs) {

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
            if ( isSpecificationClass() && annotatedClass.getName().equals(specificationName)  && specificationLoader.isSchemeObject(name)) {
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

		public void setType(TypeContext typeCtx) {
			PrimitiveTypeContext primitiveTypeContext = typeCtx.primitiveType();
			if(primitiveTypeContext != null) {
				//need to take outer context text to capture array braces
				this.type = typeCtx.getText();
				return;
			}

			String type = typeCtx.getText();
			try{
				classFieldAnnotatedClass = specificationLoader.getSpecification(type);
			}
			catch(SpecificationNotFoundException e) {
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

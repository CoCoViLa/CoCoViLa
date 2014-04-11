package ee.ioc.cs.vsle.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;
import ee.ioc.cs.vsle.equations.EquationSolver.Relation;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.AxiomContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.ClassTypeContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.ConstantVariableContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.EquationContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.MetaInterfaseContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.StaticVariableContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.SubtaskContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.SubtaskListContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.SuperMetaInterfaceContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.VariableDeclarationContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.VariableDeclaratorContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.VariableIdentifierContext;
import ee.ioc.cs.vsle.parser.SpecificationParserParser.VariableInitializerContext;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassRelation;
import ee.ioc.cs.vsle.synthesize.EquationException;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.synthesize.SpecParser;
import ee.ioc.cs.vsle.synthesize.SubtaskClassRelation;
import ee.ioc.cs.vsle.synthesize.UnknownVariableException;
import ee.ioc.cs.vsle.table.Table;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.vclass.ClassField;

public class SpecificationParserListenerImpl extends SpecificationParserBaseListener {
	
	private final SpecificationLoader specificationLoader;
	private AnnotatedClass annotatedClass;
	private ClassFieldDeclarator classFieldDeclarator;
	private String specificationName;
	
	public SpecificationParserListenerImpl(SpecificationLoader specificationLoader, String specificationName) {
		this.specificationLoader = specificationLoader;
		this.specificationName = specificationName;
	}

	@Override
	public void enterMetaInterfase(MetaInterfaseContext ctx) {
		if (specificationName == null) {
			specificationName = ctx.IDENTIFIER().getText();
		}
		annotatedClass = new AnnotatedClass(specificationName);
		classFieldDeclarator = new ClassFieldDeclarator(annotatedClass);
	}
	
	@Override
	public void enterSuperMetaInterface(SuperMetaInterfaceContext ctx) {
		for (ClassTypeContext classTypeContext : ctx.classType()) {
			String superSpecificationName = classTypeContext.getText();
				
			AnnotatedClass superClass = specificationLoader.getSpecification(superSpecificationName);
			if ( superClass != null ) {
				annotatedClass.addSuperClass( superClass );
			} else {
//				throw new SpecParseException( "Unable to parse superclass " + superSpecificationName + " of " + annotatedClass.getName() );
				throw new RuntimeException( "Unable to parse superclass " + superSpecificationName + " of " + annotatedClass.getName() );
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
	
	@Override
	public void enterVariableDeclarator(VariableDeclaratorContext ctx) {
		VariableInitializerContext variableInitializer = ctx.variableInitializer();
		if(classFieldDeclarator.isConstant){
			classFieldDeclarator.addClassField(ctx.IDENTIFIER().getText(), variableInitializer.getText());
		}else{
			classFieldDeclarator.addClassField(ctx.IDENTIFIER().getText());
			if(variableInitializer != null){
				try {
					solveEquation(ctx.getText());
				} catch (EquationException e) {
					e.printStackTrace();
				} catch (UnknownVariableException e) {
					e.printStackTrace();
				}
			}
		}
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
//TODO: checkAliasLength
//        checkAliasLength( statement.getInputs(), annClass, className );
        for (VariableIdentifierContext inputVariableContext : iputVariableContextList) {
        	classRelation.addInput(inputVariableContext.getText(), annotatedClass.getFields() );
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
        		AnnotatedClass context = specificationLoader.loadSpecification(contextName);
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

        classRelation.setType( RelType.TYPE_METHOD_WITH_SUBTASK );

        if ( RuntimeProperties.isLogDebugEnabled() )
            db.p( classRelation );

        annotatedClass.addClassRelation( classRelation );
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
            ClassField tmp = SpecParser.getVar( SpecParser.checkAliasLength( out, annotatedClass, annotatedClass.getName() ), annotatedClass.getFields() );
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
                inputs[ i ] = SpecParser.checkAliasLength( inputs[ i ], annotatedClass, annotatedClass.getName() );
                String name = inputs[ i ];
                if ( name.startsWith( "*" ) ) {
                    name = inputs[ i ].substring( 1 );
                }
                method = method.replaceAll( "\\$" + initial + "\\$", name );
            }
            method = method.replaceAll( "\\$" + out + "\\$", out );

            SpecParser.checkAnyType( out, inputs, annotatedClass.getFields() );

            if ( !inputs[ 0 ].equals( "" ) ) {
                classRelation.addInputs( inputs, annotatedClass.getFields() );
            }
            classRelation.setMethod( method );
            annotatedClass.addClassRelation( classRelation );
            if ( RuntimeProperties.isLogDebugEnabled() )
                db.p( "Equation: " + classRelation );

        }
	}
	
	public AnnotatedClass getAnnotatedClass() {
		return annotatedClass;
	}

	private class ClassFieldDeclarator{
		private AnnotatedClass annotatedClass;
		
		private String type;
		private boolean isStatic = false;
		private boolean isConstant = false;

		public ClassFieldDeclarator(AnnotatedClass annotatedClass) {
			this.annotatedClass = annotatedClass;
		}

		public void addClassField(String name) {
			ClassField classField = new ClassField(name, type);
			classField.setStatic(isStatic);
			annotatedClass.addField(classField);
		}

		public void addClassField(String name, String value){
			ClassField classField = new ClassField(name, type, value, isConstant);
			classField.setStatic(isStatic);
			annotatedClass.addField(classField);
		}
		
		public void cleanUp() {
			type = null;
			isStatic = false;
			isConstant = false;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
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
		
		
	}
}

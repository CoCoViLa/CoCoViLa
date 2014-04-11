// Generated from ExampleGrammar.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExampleGrammarParser}.
 */
public interface ExampleGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void enterInnerCreator(@NotNull ExampleGrammarParser.InnerCreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#innerCreator}.
	 * @param ctx the parse tree
	 */
	void exitInnerCreator(@NotNull ExampleGrammarParser.InnerCreatorContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericMethodDeclaration(@NotNull ExampleGrammarParser.GenericMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericMethodDeclaration(@NotNull ExampleGrammarParser.GenericMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(@NotNull ExampleGrammarParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(@NotNull ExampleGrammarParser.ExpressionListContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(@NotNull ExampleGrammarParser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(@NotNull ExampleGrammarParser.TypeDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdate(@NotNull ExampleGrammarParser.ForUpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdate(@NotNull ExampleGrammarParser.ForUpdateContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(@NotNull ExampleGrammarParser.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(@NotNull ExampleGrammarParser.AnnotationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstant(@NotNull ExampleGrammarParser.EnumConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enumConstant}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstant(@NotNull ExampleGrammarParser.EnumConstantContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(@NotNull ExampleGrammarParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(@NotNull ExampleGrammarParser.ImportDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationMethodOrConstantRest(@NotNull ExampleGrammarParser.AnnotationMethodOrConstantRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationMethodOrConstantRest(@NotNull ExampleGrammarParser.AnnotationMethodOrConstantRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstantName(@NotNull ExampleGrammarParser.EnumConstantNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enumConstantName}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstantName(@NotNull ExampleGrammarParser.EnumConstantNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void enterFinallyBlock(@NotNull ExampleGrammarParser.FinallyBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#finallyBlock}.
	 * @param ctx the parse tree
	 */
	void exitFinallyBlock(@NotNull ExampleGrammarParser.FinallyBlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(@NotNull ExampleGrammarParser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(@NotNull ExampleGrammarParser.VariableDeclaratorsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#elementValuePairs}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePairs(@NotNull ExampleGrammarParser.ElementValuePairsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#elementValuePairs}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePairs(@NotNull ExampleGrammarParser.ElementValuePairsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.InterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.InterfaceMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBodyDeclaration(@NotNull ExampleGrammarParser.InterfaceBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBodyDeclaration(@NotNull ExampleGrammarParser.InterfaceBodyDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enumConstants}.
	 * @param ctx the parse tree
	 */
	void enterEnumConstants(@NotNull ExampleGrammarParser.EnumConstantsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enumConstants}.
	 * @param ctx the parse tree
	 */
	void exitEnumConstants(@NotNull ExampleGrammarParser.EnumConstantsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#catchClause}.
	 * @param ctx the parse tree
	 */
	void enterCatchClause(@NotNull ExampleGrammarParser.CatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#catchClause}.
	 * @param ctx the parse tree
	 */
	void exitCatchClause(@NotNull ExampleGrammarParser.CatchClauseContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpression(@NotNull ExampleGrammarParser.ConstantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#constantExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpression(@NotNull ExampleGrammarParser.ConstantExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(@NotNull ExampleGrammarParser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(@NotNull ExampleGrammarParser.EnumDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 */
	void enterExplicitGenericInvocationSuffix(@NotNull ExampleGrammarParser.ExplicitGenericInvocationSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 */
	void exitExplicitGenericInvocationSuffix(@NotNull ExampleGrammarParser.ExplicitGenericInvocationSuffixContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameter(@NotNull ExampleGrammarParser.TypeParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeParameter}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameter(@NotNull ExampleGrammarParser.TypeParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterEnumBodyDeclarations(@NotNull ExampleGrammarParser.EnumBodyDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitEnumBodyDeclarations(@NotNull ExampleGrammarParser.EnumBodyDeclarationsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeBound}.
	 * @param ctx the parse tree
	 */
	void enterTypeBound(@NotNull ExampleGrammarParser.TypeBoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeBound}.
	 * @param ctx the parse tree
	 */
	void exitTypeBound(@NotNull ExampleGrammarParser.TypeBoundContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void enterStatementExpression(@NotNull ExampleGrammarParser.StatementExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#statementExpression}.
	 * @param ctx the parse tree
	 */
	void exitStatementExpression(@NotNull ExampleGrammarParser.StatementExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(@NotNull ExampleGrammarParser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(@NotNull ExampleGrammarParser.VariableInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull ExampleGrammarParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull ExampleGrammarParser.BlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.GenericInterfaceMethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.GenericInterfaceMethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclarationStatement(@NotNull ExampleGrammarParser.LocalVariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclarationStatement(@NotNull ExampleGrammarParser.LocalVariableDeclarationStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void enterSuperSuffix(@NotNull ExampleGrammarParser.SuperSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void exitSuperSuffix(@NotNull ExampleGrammarParser.SuperSuffixContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(@NotNull ExampleGrammarParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(@NotNull ExampleGrammarParser.FieldDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(@NotNull ExampleGrammarParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(@NotNull ExampleGrammarParser.FormalParameterListContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExplicitGenericInvocation(@NotNull ExampleGrammarParser.ExplicitGenericInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExplicitGenericInvocation(@NotNull ExampleGrammarParser.ExplicitGenericInvocationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(@NotNull ExampleGrammarParser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(@NotNull ExampleGrammarParser.ParExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(@NotNull ExampleGrammarParser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(@NotNull ExampleGrammarParser.SwitchLabelContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void enterTypeParameters(@NotNull ExampleGrammarParser.TypeParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeParameters}.
	 * @param ctx the parse tree
	 */
	void exitTypeParameters(@NotNull ExampleGrammarParser.TypeParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(@NotNull ExampleGrammarParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(@NotNull ExampleGrammarParser.QualifiedNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull ExampleGrammarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull ExampleGrammarParser.ClassDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationConstantRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationConstantRest(@NotNull ExampleGrammarParser.AnnotationConstantRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationConstantRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationConstantRest(@NotNull ExampleGrammarParser.AnnotationConstantRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(@NotNull ExampleGrammarParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(@NotNull ExampleGrammarParser.ArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void enterConstructorBody(@NotNull ExampleGrammarParser.ConstructorBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#constructorBody}.
	 * @param ctx the parse tree
	 */
	void exitConstructorBody(@NotNull ExampleGrammarParser.ConstructorBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(@NotNull ExampleGrammarParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(@NotNull ExampleGrammarParser.FormalParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(@NotNull ExampleGrammarParser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(@NotNull ExampleGrammarParser.TypeArgumentContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(@NotNull ExampleGrammarParser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(@NotNull ExampleGrammarParser.ForInitContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(@NotNull ExampleGrammarParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(@NotNull ExampleGrammarParser.VariableDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull ExampleGrammarParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull ExampleGrammarParser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#resources}.
	 * @param ctx the parse tree
	 */
	void enterResources(@NotNull ExampleGrammarParser.ResourcesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#resources}.
	 * @param ctx the parse tree
	 */
	void exitResources(@NotNull ExampleGrammarParser.ResourcesContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(@NotNull ExampleGrammarParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(@NotNull ExampleGrammarParser.FormalParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull ExampleGrammarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull ExampleGrammarParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterElementValueArrayInitializer(@NotNull ExampleGrammarParser.ElementValueArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitElementValueArrayInitializer(@NotNull ExampleGrammarParser.ElementValueArrayInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationName}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationName(@NotNull ExampleGrammarParser.AnnotationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationName}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationName(@NotNull ExampleGrammarParser.AnnotationNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForControl(@NotNull ExampleGrammarParser.EnhancedForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForControl(@NotNull ExampleGrammarParser.EnhancedForControlContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationMethodRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationMethodRest(@NotNull ExampleGrammarParser.AnnotationMethodRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationMethodRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationMethodRest(@NotNull ExampleGrammarParser.AnnotationMethodRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(@NotNull ExampleGrammarParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(@NotNull ExampleGrammarParser.PrimaryContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(@NotNull ExampleGrammarParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(@NotNull ExampleGrammarParser.ClassBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceModifier(@NotNull ExampleGrammarParser.ClassOrInterfaceModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceModifier(@NotNull ExampleGrammarParser.ClassOrInterfaceModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(@NotNull ExampleGrammarParser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(@NotNull ExampleGrammarParser.DefaultValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableModifier(@NotNull ExampleGrammarParser.VariableModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableModifier(@NotNull ExampleGrammarParser.VariableModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstDeclaration(@NotNull ExampleGrammarParser.ConstDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#constDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstDeclaration(@NotNull ExampleGrammarParser.ConstDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#createdName}.
	 * @param ctx the parse tree
	 */
	void enterCreatedName(@NotNull ExampleGrammarParser.CreatedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#createdName}.
	 * @param ctx the parse tree
	 */
	void exitCreatedName(@NotNull ExampleGrammarParser.CreatedNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceDeclaration(@NotNull ExampleGrammarParser.InterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceDeclaration(@NotNull ExampleGrammarParser.InterfaceDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(@NotNull ExampleGrammarParser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(@NotNull ExampleGrammarParser.PackageDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclarator(@NotNull ExampleGrammarParser.ConstantDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#constantDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclarator(@NotNull ExampleGrammarParser.ConstantDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#catchType}.
	 * @param ctx the parse tree
	 */
	void enterCatchType(@NotNull ExampleGrammarParser.CatchTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#catchType}.
	 * @param ctx the parse tree
	 */
	void exitCatchType(@NotNull ExampleGrammarParser.CatchTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void enterTypeArguments(@NotNull ExampleGrammarParser.TypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeArguments}.
	 * @param ctx the parse tree
	 */
	void exitTypeArguments(@NotNull ExampleGrammarParser.TypeArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterClassCreatorRest(@NotNull ExampleGrammarParser.ClassCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitClassCreatorRest(@NotNull ExampleGrammarParser.ClassCreatorRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#modifier}.
	 * @param ctx the parse tree
	 */
	void enterModifier(@NotNull ExampleGrammarParser.ModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#modifier}.
	 * @param ctx the parse tree
	 */
	void exitModifier(@NotNull ExampleGrammarParser.ModifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull ExampleGrammarParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull ExampleGrammarParser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceBody(@NotNull ExampleGrammarParser.InterfaceBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#interfaceBody}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceBody(@NotNull ExampleGrammarParser.InterfaceBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(@NotNull ExampleGrammarParser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(@NotNull ExampleGrammarParser.ClassBodyDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterLastFormalParameter(@NotNull ExampleGrammarParser.LastFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitLastFormalParameter(@NotNull ExampleGrammarParser.LastFormalParameterContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#forControl}.
	 * @param ctx the parse tree
	 */
	void enterForControl(@NotNull ExampleGrammarParser.ForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#forControl}.
	 * @param ctx the parse tree
	 */
	void exitForControl(@NotNull ExampleGrammarParser.ForControlContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(@NotNull ExampleGrammarParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(@NotNull ExampleGrammarParser.TypeListContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocalVariableDeclaration(@NotNull ExampleGrammarParser.LocalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocalVariableDeclaration(@NotNull ExampleGrammarParser.LocalVariableDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(@NotNull ExampleGrammarParser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(@NotNull ExampleGrammarParser.VariableDeclaratorIdContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(@NotNull ExampleGrammarParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(@NotNull ExampleGrammarParser.CompilationUnitContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#elementValue}.
	 * @param ctx the parse tree
	 */
	void enterElementValue(@NotNull ExampleGrammarParser.ElementValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#elementValue}.
	 * @param ctx the parse tree
	 */
	void exitElementValue(@NotNull ExampleGrammarParser.ElementValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceType(@NotNull ExampleGrammarParser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceType(@NotNull ExampleGrammarParser.ClassOrInterfaceTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.TypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.TypeArgumentsOrDiamondContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeElementDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeElementDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(@NotNull ExampleGrammarParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(@NotNull ExampleGrammarParser.BlockStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeBody(@NotNull ExampleGrammarParser.AnnotationTypeBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationTypeBody}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeBody(@NotNull ExampleGrammarParser.AnnotationTypeBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedNameList(@NotNull ExampleGrammarParser.QualifiedNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedNameList(@NotNull ExampleGrammarParser.QualifiedNameListContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(@NotNull ExampleGrammarParser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(@NotNull ExampleGrammarParser.CreatorContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(@NotNull ExampleGrammarParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(@NotNull ExampleGrammarParser.MemberDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(@NotNull ExampleGrammarParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(@NotNull ExampleGrammarParser.MethodDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationTypeElementRest(@NotNull ExampleGrammarParser.AnnotationTypeElementRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationTypeElementRest(@NotNull ExampleGrammarParser.AnnotationTypeElementRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void enterResourceSpecification(@NotNull ExampleGrammarParser.ResourceSpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#resourceSpecification}.
	 * @param ctx the parse tree
	 */
	void exitResourceSpecification(@NotNull ExampleGrammarParser.ResourceSpecificationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(@NotNull ExampleGrammarParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(@NotNull ExampleGrammarParser.ConstructorDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#resource}.
	 * @param ctx the parse tree
	 */
	void enterResource(@NotNull ExampleGrammarParser.ResourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#resource}.
	 * @param ctx the parse tree
	 */
	void exitResource(@NotNull ExampleGrammarParser.ResourceContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void enterElementValuePair(@NotNull ExampleGrammarParser.ElementValuePairContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#elementValuePair}.
	 * @param ctx the parse tree
	 */
	void exitElementValuePair(@NotNull ExampleGrammarParser.ElementValuePairContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#methodBody}.
	 * @param ctx the parse tree
	 */
	void enterMethodBody(@NotNull ExampleGrammarParser.MethodBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#methodBody}.
	 * @param ctx the parse tree
	 */
	void exitMethodBody(@NotNull ExampleGrammarParser.MethodBodyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(@NotNull ExampleGrammarParser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(@NotNull ExampleGrammarParser.ArrayInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void enterNonWildcardTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsOrDiamondContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 */
	void exitNonWildcardTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsOrDiamondContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(@NotNull ExampleGrammarParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(@NotNull ExampleGrammarParser.PrimitiveTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 */
	void enterNonWildcardTypeArguments(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 */
	void exitNonWildcardTypeArguments(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreatorRest(@NotNull ExampleGrammarParser.ArrayCreatorRestContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreatorRest(@NotNull ExampleGrammarParser.ArrayCreatorRestContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInterfaceMemberDeclaration(@NotNull ExampleGrammarParser.InterfaceMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInterfaceMemberDeclaration(@NotNull ExampleGrammarParser.InterfaceMemberDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericConstructorDeclaration(@NotNull ExampleGrammarParser.GenericConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericConstructorDeclaration(@NotNull ExampleGrammarParser.GenericConstructorDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull ExampleGrammarParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull ExampleGrammarParser.LiteralContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExampleGrammarParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(@NotNull ExampleGrammarParser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExampleGrammarParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(@NotNull ExampleGrammarParser.SwitchBlockStatementGroupContext ctx);
}
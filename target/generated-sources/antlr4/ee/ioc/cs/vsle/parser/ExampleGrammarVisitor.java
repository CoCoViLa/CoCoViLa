// Generated from ExampleGrammar.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExampleGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExampleGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#innerCreator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInnerCreator(@NotNull ExampleGrammarParser.InnerCreatorContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#genericMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericMethodDeclaration(@NotNull ExampleGrammarParser.GenericMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(@NotNull ExampleGrammarParser.ExpressionListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(@NotNull ExampleGrammarParser.TypeDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdate(@NotNull ExampleGrammarParser.ForUpdateContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation(@NotNull ExampleGrammarParser.AnnotationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enumConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstant(@NotNull ExampleGrammarParser.EnumConstantContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#importDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDeclaration(@NotNull ExampleGrammarParser.ImportDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationMethodOrConstantRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationMethodOrConstantRest(@NotNull ExampleGrammarParser.AnnotationMethodOrConstantRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enumConstantName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstantName(@NotNull ExampleGrammarParser.EnumConstantNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#finallyBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFinallyBlock(@NotNull ExampleGrammarParser.FinallyBlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#variableDeclarators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarators(@NotNull ExampleGrammarParser.VariableDeclaratorsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#elementValuePairs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValuePairs(@NotNull ExampleGrammarParser.ElementValuePairsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#interfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.InterfaceMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#interfaceBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceBodyDeclaration(@NotNull ExampleGrammarParser.InterfaceBodyDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enumConstants}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumConstants(@NotNull ExampleGrammarParser.EnumConstantsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#catchClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchClause(@NotNull ExampleGrammarParser.CatchClauseContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#constantExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantExpression(@NotNull ExampleGrammarParser.ConstantExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enumDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumDeclaration(@NotNull ExampleGrammarParser.EnumDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocationSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplicitGenericInvocationSuffix(@NotNull ExampleGrammarParser.ExplicitGenericInvocationSuffixContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameter(@NotNull ExampleGrammarParser.TypeParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enumBodyDeclarations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumBodyDeclarations(@NotNull ExampleGrammarParser.EnumBodyDeclarationsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeBound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeBound(@NotNull ExampleGrammarParser.TypeBoundContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#statementExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementExpression(@NotNull ExampleGrammarParser.StatementExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#variableInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInitializer(@NotNull ExampleGrammarParser.VariableInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull ExampleGrammarParser.BlockContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#genericInterfaceMethodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericInterfaceMethodDeclaration(@NotNull ExampleGrammarParser.GenericInterfaceMethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#localVariableDeclarationStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariableDeclarationStatement(@NotNull ExampleGrammarParser.LocalVariableDeclarationStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#superSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperSuffix(@NotNull ExampleGrammarParser.SuperSuffixContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(@NotNull ExampleGrammarParser.FieldDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#formalParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameterList(@NotNull ExampleGrammarParser.FormalParameterListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#explicitGenericInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplicitGenericInvocation(@NotNull ExampleGrammarParser.ExplicitGenericInvocationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#parExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParExpression(@NotNull ExampleGrammarParser.ParExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(@NotNull ExampleGrammarParser.SwitchLabelContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeParameters(@NotNull ExampleGrammarParser.TypeParametersContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(@NotNull ExampleGrammarParser.QualifiedNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(@NotNull ExampleGrammarParser.ClassDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationConstantRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationConstantRest(@NotNull ExampleGrammarParser.AnnotationConstantRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(@NotNull ExampleGrammarParser.ArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#constructorBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorBody(@NotNull ExampleGrammarParser.ConstructorBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(@NotNull ExampleGrammarParser.FormalParametersContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgument(@NotNull ExampleGrammarParser.TypeArgumentContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(@NotNull ExampleGrammarParser.ForInitContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#variableDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarator(@NotNull ExampleGrammarParser.VariableDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationTypeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull ExampleGrammarParser.ExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#resources}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResources(@NotNull ExampleGrammarParser.ResourcesContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#formalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameter(@NotNull ExampleGrammarParser.FormalParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(@NotNull ExampleGrammarParser.TypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#elementValueArrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValueArrayInitializer(@NotNull ExampleGrammarParser.ElementValueArrayInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationName(@NotNull ExampleGrammarParser.AnnotationNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#enhancedForControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnhancedForControl(@NotNull ExampleGrammarParser.EnhancedForControlContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationMethodRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationMethodRest(@NotNull ExampleGrammarParser.AnnotationMethodRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(@NotNull ExampleGrammarParser.PrimaryContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(@NotNull ExampleGrammarParser.ClassBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassOrInterfaceModifier(@NotNull ExampleGrammarParser.ClassOrInterfaceModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#defaultValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultValue(@NotNull ExampleGrammarParser.DefaultValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#variableModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableModifier(@NotNull ExampleGrammarParser.VariableModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#constDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDeclaration(@NotNull ExampleGrammarParser.ConstDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#createdName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreatedName(@NotNull ExampleGrammarParser.CreatedNameContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#interfaceDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceDeclaration(@NotNull ExampleGrammarParser.InterfaceDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#packageDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageDeclaration(@NotNull ExampleGrammarParser.PackageDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#constantDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDeclarator(@NotNull ExampleGrammarParser.ConstantDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#catchType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCatchType(@NotNull ExampleGrammarParser.CatchTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArguments(@NotNull ExampleGrammarParser.TypeArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classCreatorRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassCreatorRest(@NotNull ExampleGrammarParser.ClassCreatorRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#modifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModifier(@NotNull ExampleGrammarParser.ModifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull ExampleGrammarParser.StatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#interfaceBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceBody(@NotNull ExampleGrammarParser.InterfaceBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBodyDeclaration(@NotNull ExampleGrammarParser.ClassBodyDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLastFormalParameter(@NotNull ExampleGrammarParser.LastFormalParameterContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#forControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForControl(@NotNull ExampleGrammarParser.ForControlContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(@NotNull ExampleGrammarParser.TypeListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#localVariableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocalVariableDeclaration(@NotNull ExampleGrammarParser.LocalVariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaratorId(@NotNull ExampleGrammarParser.VariableDeclaratorIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(@NotNull ExampleGrammarParser.CompilationUnitContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#elementValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValue(@NotNull ExampleGrammarParser.ElementValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassOrInterfaceType(@NotNull ExampleGrammarParser.ClassOrInterfaceTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#typeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.TypeArgumentsOrDiamondContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeElementDeclaration(@NotNull ExampleGrammarParser.AnnotationTypeElementDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#blockStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(@NotNull ExampleGrammarParser.BlockStatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationTypeBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeBody(@NotNull ExampleGrammarParser.AnnotationTypeBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedNameList(@NotNull ExampleGrammarParser.QualifiedNameListContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(@NotNull ExampleGrammarParser.CreatorContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(@NotNull ExampleGrammarParser.MemberDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(@NotNull ExampleGrammarParser.MethodDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#annotationTypeElementRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationTypeElementRest(@NotNull ExampleGrammarParser.AnnotationTypeElementRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#resourceSpecification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResourceSpecification(@NotNull ExampleGrammarParser.ResourceSpecificationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDeclaration(@NotNull ExampleGrammarParser.ConstructorDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#resource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResource(@NotNull ExampleGrammarParser.ResourceContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#elementValuePair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElementValuePair(@NotNull ExampleGrammarParser.ElementValuePairContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#methodBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodBody(@NotNull ExampleGrammarParser.MethodBodyContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#arrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayInitializer(@NotNull ExampleGrammarParser.ArrayInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArgumentsOrDiamond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonWildcardTypeArgumentsOrDiamond(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsOrDiamondContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(@NotNull ExampleGrammarParser.PrimitiveTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#nonWildcardTypeArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonWildcardTypeArguments(@NotNull ExampleGrammarParser.NonWildcardTypeArgumentsContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#arrayCreatorRest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayCreatorRest(@NotNull ExampleGrammarParser.ArrayCreatorRestContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#interfaceMemberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterfaceMemberDeclaration(@NotNull ExampleGrammarParser.InterfaceMemberDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#genericConstructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericConstructorDeclaration(@NotNull ExampleGrammarParser.GenericConstructorDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull ExampleGrammarParser.LiteralContext ctx);

	/**
	 * Visit a parse tree produced by {@link ExampleGrammarParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchBlockStatementGroup(@NotNull ExampleGrammarParser.SwitchBlockStatementGroupContext ctx);
}
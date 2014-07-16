// Generated from SpecificationLanguage.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SpecificationLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SpecificationLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull SpecificationLanguageParser.ExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#subtask}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtask(@NotNull SpecificationLanguageParser.SubtaskContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#inArrayVariableAssigner}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInArrayVariableAssigner(@NotNull SpecificationLanguageParser.InArrayVariableAssignerContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#specificationVariableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecificationVariableDeclaration(@NotNull SpecificationLanguageParser.SpecificationVariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInitializer(@NotNull SpecificationLanguageParser.VariableInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#aliasStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasStructure(@NotNull SpecificationLanguageParser.AliasStructureContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(@NotNull SpecificationLanguageParser.TypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(@NotNull SpecificationLanguageParser.VariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#metaInterfase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetaInterfase(@NotNull SpecificationLanguageParser.MetaInterfaseContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#exceptionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExceptionList(@NotNull SpecificationLanguageParser.ExceptionListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#aliasDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasDefinition(@NotNull SpecificationLanguageParser.AliasDefinitionContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#staticVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStaticVariable(@NotNull SpecificationLanguageParser.StaticVariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#equation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquation(@NotNull SpecificationLanguageParser.EquationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableAssigner}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableAssigner(@NotNull SpecificationLanguageParser.VariableAssignerContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableIdentifier(@NotNull SpecificationLanguageParser.VariableIdentifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#specificationVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecificationVariable(@NotNull SpecificationLanguageParser.SpecificationVariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#specificationVariableDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecificationVariableDeclarator(@NotNull SpecificationLanguageParser.SpecificationVariableDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableIdentifierList(@NotNull SpecificationLanguageParser.VariableIdentifierListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#constantVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantVariable(@NotNull SpecificationLanguageParser.ConstantVariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#classType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(@NotNull SpecificationLanguageParser.ClassTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#goal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGoal(@NotNull SpecificationLanguageParser.GoalContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#wildcardAlias}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcardAlias(@NotNull SpecificationLanguageParser.WildcardAliasContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull SpecificationLanguageParser.StatementContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#superMetaInterface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperMetaInterface(@NotNull SpecificationLanguageParser.SuperMetaInterfaceContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(@NotNull SpecificationLanguageParser.TermContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(@NotNull SpecificationLanguageParser.PrimitiveTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaratorInitializer(@NotNull SpecificationLanguageParser.VariableDeclaratorInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#specification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecification(@NotNull SpecificationLanguageParser.SpecificationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasDeclaration(@NotNull SpecificationLanguageParser.AliasDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableAssignment(@NotNull SpecificationLanguageParser.VariableAssignmentContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#subtaskList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtaskList(@NotNull SpecificationLanguageParser.SubtaskListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#axiom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAxiom(@NotNull SpecificationLanguageParser.AxiomContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(@NotNull SpecificationLanguageParser.ArrayContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorAssigner}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaratorAssigner(@NotNull SpecificationLanguageParser.VariableDeclaratorAssignerContext ctx);
}
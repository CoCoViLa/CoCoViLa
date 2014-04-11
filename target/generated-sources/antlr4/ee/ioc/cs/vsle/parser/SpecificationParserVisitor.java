// Generated from SpecificationParser.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SpecificationParserParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SpecificationParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#subtask}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtask(@NotNull SpecificationParserParser.SubtaskContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#variableDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarator(@NotNull SpecificationParserParser.VariableDeclaratorContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull SpecificationParserParser.ExpressionContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#variableInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInitializer(@NotNull SpecificationParserParser.VariableInitializerContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#aliasStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasStructure(@NotNull SpecificationParserParser.AliasStructureContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(@NotNull SpecificationParserParser.TypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#exceptionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExceptionList(@NotNull SpecificationParserParser.ExceptionListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(@NotNull SpecificationParserParser.VariableDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#metaInterfase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMetaInterfase(@NotNull SpecificationParserParser.MetaInterfaseContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#aliasDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasDefinition(@NotNull SpecificationParserParser.AliasDefinitionContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#staticVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStaticVariable(@NotNull SpecificationParserParser.StaticVariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#equation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquation(@NotNull SpecificationParserParser.EquationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#variableIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableIdentifier(@NotNull SpecificationParserParser.VariableIdentifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableIdentifierList(@NotNull SpecificationParserParser.VariableIdentifierListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#constantVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantVariable(@NotNull SpecificationParserParser.ConstantVariableContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#classType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassType(@NotNull SpecificationParserParser.ClassTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#goal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGoal(@NotNull SpecificationParserParser.GoalContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#superMetaInterface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperMetaInterface(@NotNull SpecificationParserParser.SuperMetaInterfaceContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(@NotNull SpecificationParserParser.TermContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(@NotNull SpecificationParserParser.PrimitiveTypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#specification}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecification(@NotNull SpecificationParserParser.SpecificationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAliasDeclaration(@NotNull SpecificationParserParser.AliasDeclarationContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#subtaskList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubtaskList(@NotNull SpecificationParserParser.SubtaskListContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#axiom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAxiom(@NotNull SpecificationParserParser.AxiomContext ctx);

	/**
	 * Visit a parse tree produced by {@link SpecificationParserParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(@NotNull SpecificationParserParser.ArrayContext ctx);
}
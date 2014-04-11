// Generated from SpecificationParser.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SpecificationParserParser}.
 */
public interface SpecificationParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#subtask}.
	 * @param ctx the parse tree
	 */
	void enterSubtask(@NotNull SpecificationParserParser.SubtaskContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#subtask}.
	 * @param ctx the parse tree
	 */
	void exitSubtask(@NotNull SpecificationParserParser.SubtaskContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(@NotNull SpecificationParserParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(@NotNull SpecificationParserParser.VariableDeclaratorContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull SpecificationParserParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull SpecificationParserParser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(@NotNull SpecificationParserParser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(@NotNull SpecificationParserParser.VariableInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#aliasStructure}.
	 * @param ctx the parse tree
	 */
	void enterAliasStructure(@NotNull SpecificationParserParser.AliasStructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#aliasStructure}.
	 * @param ctx the parse tree
	 */
	void exitAliasStructure(@NotNull SpecificationParserParser.AliasStructureContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull SpecificationParserParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull SpecificationParserParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#exceptionList}.
	 * @param ctx the parse tree
	 */
	void enterExceptionList(@NotNull SpecificationParserParser.ExceptionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#exceptionList}.
	 * @param ctx the parse tree
	 */
	void exitExceptionList(@NotNull SpecificationParserParser.ExceptionListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(@NotNull SpecificationParserParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(@NotNull SpecificationParserParser.VariableDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#metaInterfase}.
	 * @param ctx the parse tree
	 */
	void enterMetaInterfase(@NotNull SpecificationParserParser.MetaInterfaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#metaInterfase}.
	 * @param ctx the parse tree
	 */
	void exitMetaInterfase(@NotNull SpecificationParserParser.MetaInterfaseContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#aliasDefinition}.
	 * @param ctx the parse tree
	 */
	void enterAliasDefinition(@NotNull SpecificationParserParser.AliasDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#aliasDefinition}.
	 * @param ctx the parse tree
	 */
	void exitAliasDefinition(@NotNull SpecificationParserParser.AliasDefinitionContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#staticVariable}.
	 * @param ctx the parse tree
	 */
	void enterStaticVariable(@NotNull SpecificationParserParser.StaticVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#staticVariable}.
	 * @param ctx the parse tree
	 */
	void exitStaticVariable(@NotNull SpecificationParserParser.StaticVariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#equation}.
	 * @param ctx the parse tree
	 */
	void enterEquation(@NotNull SpecificationParserParser.EquationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#equation}.
	 * @param ctx the parse tree
	 */
	void exitEquation(@NotNull SpecificationParserParser.EquationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableIdentifier(@NotNull SpecificationParserParser.VariableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableIdentifier(@NotNull SpecificationParserParser.VariableIdentifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 */
	void enterVariableIdentifierList(@NotNull SpecificationParserParser.VariableIdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 */
	void exitVariableIdentifierList(@NotNull SpecificationParserParser.VariableIdentifierListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#constantVariable}.
	 * @param ctx the parse tree
	 */
	void enterConstantVariable(@NotNull SpecificationParserParser.ConstantVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#constantVariable}.
	 * @param ctx the parse tree
	 */
	void exitConstantVariable(@NotNull SpecificationParserParser.ConstantVariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(@NotNull SpecificationParserParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(@NotNull SpecificationParserParser.ClassTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#goal}.
	 * @param ctx the parse tree
	 */
	void enterGoal(@NotNull SpecificationParserParser.GoalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#goal}.
	 * @param ctx the parse tree
	 */
	void exitGoal(@NotNull SpecificationParserParser.GoalContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#superMetaInterface}.
	 * @param ctx the parse tree
	 */
	void enterSuperMetaInterface(@NotNull SpecificationParserParser.SuperMetaInterfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#superMetaInterface}.
	 * @param ctx the parse tree
	 */
	void exitSuperMetaInterface(@NotNull SpecificationParserParser.SuperMetaInterfaceContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull SpecificationParserParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull SpecificationParserParser.TermContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(@NotNull SpecificationParserParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(@NotNull SpecificationParserParser.PrimitiveTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#specification}.
	 * @param ctx the parse tree
	 */
	void enterSpecification(@NotNull SpecificationParserParser.SpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#specification}.
	 * @param ctx the parse tree
	 */
	void exitSpecification(@NotNull SpecificationParserParser.SpecificationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAliasDeclaration(@NotNull SpecificationParserParser.AliasDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAliasDeclaration(@NotNull SpecificationParserParser.AliasDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#subtaskList}.
	 * @param ctx the parse tree
	 */
	void enterSubtaskList(@NotNull SpecificationParserParser.SubtaskListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#subtaskList}.
	 * @param ctx the parse tree
	 */
	void exitSubtaskList(@NotNull SpecificationParserParser.SubtaskListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#axiom}.
	 * @param ctx the parse tree
	 */
	void enterAxiom(@NotNull SpecificationParserParser.AxiomContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#axiom}.
	 * @param ctx the parse tree
	 */
	void exitAxiom(@NotNull SpecificationParserParser.AxiomContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(@NotNull SpecificationParserParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(@NotNull SpecificationParserParser.ArrayContext ctx);
}
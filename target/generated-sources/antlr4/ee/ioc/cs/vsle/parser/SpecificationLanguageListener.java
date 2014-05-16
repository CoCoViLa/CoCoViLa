// Generated from SpecificationLanguage.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SpecificationLanguageParser}.
 */
public interface SpecificationLanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#subtask}.
	 * @param ctx the parse tree
	 */
	void enterSubtask(@NotNull SpecificationLanguageParser.SubtaskContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#subtask}.
	 * @param ctx the parse tree
	 */
	void exitSubtask(@NotNull SpecificationLanguageParser.SubtaskContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull SpecificationLanguageParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull SpecificationLanguageParser.ExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#inArrayVariableAssigner}.
	 * @param ctx the parse tree
	 */
	void enterInArrayVariableAssigner(@NotNull SpecificationLanguageParser.InArrayVariableAssignerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#inArrayVariableAssigner}.
	 * @param ctx the parse tree
	 */
	void exitInArrayVariableAssigner(@NotNull SpecificationLanguageParser.InArrayVariableAssignerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(@NotNull SpecificationLanguageParser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(@NotNull SpecificationLanguageParser.VariableInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#aliasStructure}.
	 * @param ctx the parse tree
	 */
	void enterAliasStructure(@NotNull SpecificationLanguageParser.AliasStructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#aliasStructure}.
	 * @param ctx the parse tree
	 */
	void exitAliasStructure(@NotNull SpecificationLanguageParser.AliasStructureContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull SpecificationLanguageParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull SpecificationLanguageParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(@NotNull SpecificationLanguageParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(@NotNull SpecificationLanguageParser.VariableDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#metaInterfase}.
	 * @param ctx the parse tree
	 */
	void enterMetaInterfase(@NotNull SpecificationLanguageParser.MetaInterfaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#metaInterfase}.
	 * @param ctx the parse tree
	 */
	void exitMetaInterfase(@NotNull SpecificationLanguageParser.MetaInterfaseContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#exceptionList}.
	 * @param ctx the parse tree
	 */
	void enterExceptionList(@NotNull SpecificationLanguageParser.ExceptionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#exceptionList}.
	 * @param ctx the parse tree
	 */
	void exitExceptionList(@NotNull SpecificationLanguageParser.ExceptionListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#aliasDefinition}.
	 * @param ctx the parse tree
	 */
	void enterAliasDefinition(@NotNull SpecificationLanguageParser.AliasDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#aliasDefinition}.
	 * @param ctx the parse tree
	 */
	void exitAliasDefinition(@NotNull SpecificationLanguageParser.AliasDefinitionContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#staticVariable}.
	 * @param ctx the parse tree
	 */
	void enterStaticVariable(@NotNull SpecificationLanguageParser.StaticVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#staticVariable}.
	 * @param ctx the parse tree
	 */
	void exitStaticVariable(@NotNull SpecificationLanguageParser.StaticVariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#equation}.
	 * @param ctx the parse tree
	 */
	void enterEquation(@NotNull SpecificationLanguageParser.EquationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#equation}.
	 * @param ctx the parse tree
	 */
	void exitEquation(@NotNull SpecificationLanguageParser.EquationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableAssigner}.
	 * @param ctx the parse tree
	 */
	void enterVariableAssigner(@NotNull SpecificationLanguageParser.VariableAssignerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableAssigner}.
	 * @param ctx the parse tree
	 */
	void exitVariableAssigner(@NotNull SpecificationLanguageParser.VariableAssignerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableIdentifier(@NotNull SpecificationLanguageParser.VariableIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableIdentifier(@NotNull SpecificationLanguageParser.VariableIdentifierContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 */
	void enterVariableIdentifierList(@NotNull SpecificationLanguageParser.VariableIdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableIdentifierList}.
	 * @param ctx the parse tree
	 */
	void exitVariableIdentifierList(@NotNull SpecificationLanguageParser.VariableIdentifierListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#constantVariable}.
	 * @param ctx the parse tree
	 */
	void enterConstantVariable(@NotNull SpecificationLanguageParser.ConstantVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#constantVariable}.
	 * @param ctx the parse tree
	 */
	void exitConstantVariable(@NotNull SpecificationLanguageParser.ConstantVariableContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#classType}.
	 * @param ctx the parse tree
	 */
	void enterClassType(@NotNull SpecificationLanguageParser.ClassTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#classType}.
	 * @param ctx the parse tree
	 */
	void exitClassType(@NotNull SpecificationLanguageParser.ClassTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#goal}.
	 * @param ctx the parse tree
	 */
	void enterGoal(@NotNull SpecificationLanguageParser.GoalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#goal}.
	 * @param ctx the parse tree
	 */
	void exitGoal(@NotNull SpecificationLanguageParser.GoalContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#superMetaInterface}.
	 * @param ctx the parse tree
	 */
	void enterSuperMetaInterface(@NotNull SpecificationLanguageParser.SuperMetaInterfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#superMetaInterface}.
	 * @param ctx the parse tree
	 */
	void exitSuperMetaInterface(@NotNull SpecificationLanguageParser.SuperMetaInterfaceContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull SpecificationLanguageParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull SpecificationLanguageParser.TermContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(@NotNull SpecificationLanguageParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(@NotNull SpecificationLanguageParser.PrimitiveTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorInitializer(@NotNull SpecificationLanguageParser.VariableDeclaratorInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorInitializer(@NotNull SpecificationLanguageParser.VariableDeclaratorInitializerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#specification}.
	 * @param ctx the parse tree
	 */
	void enterSpecification(@NotNull SpecificationLanguageParser.SpecificationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#specification}.
	 * @param ctx the parse tree
	 */
	void exitSpecification(@NotNull SpecificationLanguageParser.SpecificationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAliasDeclaration(@NotNull SpecificationLanguageParser.AliasDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#aliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAliasDeclaration(@NotNull SpecificationLanguageParser.AliasDeclarationContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableAssignment}.
	 * @param ctx the parse tree
	 */
	void enterVariableAssignment(@NotNull SpecificationLanguageParser.VariableAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableAssignment}.
	 * @param ctx the parse tree
	 */
	void exitVariableAssignment(@NotNull SpecificationLanguageParser.VariableAssignmentContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#subtaskList}.
	 * @param ctx the parse tree
	 */
	void enterSubtaskList(@NotNull SpecificationLanguageParser.SubtaskListContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#subtaskList}.
	 * @param ctx the parse tree
	 */
	void exitSubtaskList(@NotNull SpecificationLanguageParser.SubtaskListContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#axiom}.
	 * @param ctx the parse tree
	 */
	void enterAxiom(@NotNull SpecificationLanguageParser.AxiomContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#axiom}.
	 * @param ctx the parse tree
	 */
	void exitAxiom(@NotNull SpecificationLanguageParser.AxiomContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorAssigner}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorAssigner(@NotNull SpecificationLanguageParser.VariableDeclaratorAssignerContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#variableDeclaratorAssigner}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorAssigner(@NotNull SpecificationLanguageParser.VariableDeclaratorAssignerContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationLanguageParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(@NotNull SpecificationLanguageParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationLanguageParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(@NotNull SpecificationLanguageParser.ArrayContext ctx);
}
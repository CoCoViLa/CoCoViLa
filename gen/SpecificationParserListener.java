// Generated from D:\workspaces\Diplom\CoCoViLa\src\ee\ioc\cs\vsle\parser\SpecificationParser.g4 by ANTLR 4.x
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SpecificationParserParser}.
 */
public interface SpecificationParserListener extends ParseTreeListener {
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
	 * Enter a parse tree produced by {@link SpecificationParserParser#invert}.
	 * @param ctx the parse tree
	 */
	void enterInvert(@NotNull SpecificationParserParser.InvertContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#invert}.
	 * @param ctx the parse tree
	 */
	void exitInvert(@NotNull SpecificationParserParser.InvertContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#unary}.
	 * @param ctx the parse tree
	 */
	void enterUnary(@NotNull SpecificationParserParser.UnaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#unary}.
	 * @param ctx the parse tree
	 */
	void exitUnary(@NotNull SpecificationParserParser.UnaryContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#mult}.
	 * @param ctx the parse tree
	 */
	void enterMult(@NotNull SpecificationParserParser.MultContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#mult}.
	 * @param ctx the parse tree
	 */
	void exitMult(@NotNull SpecificationParserParser.MultContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(@NotNull SpecificationParserParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(@NotNull SpecificationParserParser.DeclarationContext ctx);

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
	 * Enter a parse tree produced by {@link SpecificationParserParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(@NotNull SpecificationParserParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(@NotNull SpecificationParserParser.ParametersContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#add}.
	 * @param ctx the parse tree
	 */
	void enterAdd(@NotNull SpecificationParserParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#add}.
	 * @param ctx the parse tree
	 */
	void exitAdd(@NotNull SpecificationParserParser.AddContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#methodCal}.
	 * @param ctx the parse tree
	 */
	void enterMethodCal(@NotNull SpecificationParserParser.MethodCalContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#methodCal}.
	 * @param ctx the parse tree
	 */
	void exitMethodCal(@NotNull SpecificationParserParser.MethodCalContext ctx);

	/**
	 * Enter a parse tree produced by {@link SpecificationParserParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(@NotNull SpecificationParserParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link SpecificationParserParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(@NotNull SpecificationParserParser.VarDeclarationContext ctx);
}
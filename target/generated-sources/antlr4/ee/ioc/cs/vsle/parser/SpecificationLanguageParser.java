// Generated from SpecificationLanguage.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpecificationLanguageParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__33=1, T__32=2, T__31=3, T__30=4, T__29=5, T__28=6, T__27=7, T__26=8, 
		T__25=9, T__24=10, T__23=11, T__22=12, T__21=13, T__20=14, T__19=15, T__18=16, 
		T__17=17, T__16=18, T__15=19, T__14=20, T__13=21, T__12=22, T__11=23, 
		T__10=24, T__9=25, T__8=26, T__7=27, T__6=28, T__5=29, T__4=30, T__3=31, 
		T__2=32, T__1=33, T__0=34, NUMBER=35, INTEGER=36, STRING=37, IDENTIFIER=38, 
		ALIAS_ELEMENT_REF=39, WS=40, COMMENT=41, BLOCK_COMMENT=42, JAVA_BEFORE_SPEC=43, 
		JAVA_AFTER_SPEC=44;
	public static final String[] tokenNames = {
		"<INVALID>", "'long'", "'short'", "']'", "','", "'['", "'-'", "'*'", "'('", 
		"'int'", "'false'", "'{'", "'double'", "'boolean'", "'}'", "'float'", 
		"'true'", "'specification'", "'static'", "'char'", "'super'", "'->'", 
		"'byte'", "'|-'", "'^'", "'.'", "')'", "'+'", "'='", "';'", "'*.'", "'alias'", 
		"'const'", "'new'", "'/'", "NUMBER", "INTEGER", "STRING", "IDENTIFIER", 
		"ALIAS_ELEMENT_REF", "WS", "COMMENT", "BLOCK_COMMENT", "JAVA_BEFORE_SPEC", 
		"JAVA_AFTER_SPEC"
	};
	public static final int
		RULE_metaInterfase = 0, RULE_superMetaInterface = 1, RULE_specification = 2, 
		RULE_statement = 3, RULE_variableDeclaration = 4, RULE_variableModifier = 5, 
		RULE_variableDeclarator = 6, RULE_specificationVariableDeclaration = 7, 
		RULE_specificationVariableDeclarator = 8, RULE_variableAssignment = 9, 
		RULE_axiom = 10, RULE_subtask = 11, RULE_subtaskList = 12, RULE_exceptionList = 13, 
		RULE_goal = 14, RULE_aliasDeclaration = 15, RULE_aliasStructure = 16, 
		RULE_wildcardAlias = 17, RULE_aliasDefinition = 18, RULE_type = 19, RULE_classType = 20, 
		RULE_primitiveType = 21, RULE_equation = 22, RULE_expression = 23, RULE_term = 24, 
		RULE_array = 25, RULE_inArrayVariableAssigner = 26, RULE_variableAssigner = 27, 
		RULE_variableInitializer = 28, RULE_variableIdentifier = 29, RULE_variableIdentifierList = 30;
	public static final String[] ruleNames = {
		"metaInterfase", "superMetaInterface", "specification", "statement", "variableDeclaration", 
		"variableModifier", "variableDeclarator", "specificationVariableDeclaration", 
		"specificationVariableDeclarator", "variableAssignment", "axiom", "subtask", 
		"subtaskList", "exceptionList", "goal", "aliasDeclaration", "aliasStructure", 
		"wildcardAlias", "aliasDefinition", "type", "classType", "primitiveType", 
		"equation", "expression", "term", "array", "inArrayVariableAssigner", 
		"variableAssigner", "variableInitializer", "variableIdentifier", "variableIdentifierList"
	};

	@Override
	public String getGrammarFileName() { return "SpecificationLanguage.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }


		

	public SpecificationLanguageParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class MetaInterfaseContext extends ParserRuleContext {
		public SuperMetaInterfaceContext superMetaInterface() {
			return getRuleContext(SuperMetaInterfaceContext.class,0);
		}
		public SpecificationContext specification() {
			return getRuleContext(SpecificationContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public MetaInterfaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metaInterfase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterMetaInterfase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitMetaInterfase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitMetaInterfase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MetaInterfaseContext metaInterfase() throws RecognitionException {
		MetaInterfaseContext _localctx = new MetaInterfaseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_metaInterfase);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62); match(17);
			setState(63); match(IDENTIFIER);
			setState(65);
			_la = _input.LA(1);
			if (_la==20) {
				{
				setState(64); superMetaInterface();
				}
			}

			setState(67); match(11);
			setState(68); specification();
			setState(69); match(14);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SuperMetaInterfaceContext extends ParserRuleContext {
		public List<ClassTypeContext> classType() {
			return getRuleContexts(ClassTypeContext.class);
		}
		public ClassTypeContext classType(int i) {
			return getRuleContext(ClassTypeContext.class,i);
		}
		public SuperMetaInterfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_superMetaInterface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSuperMetaInterface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSuperMetaInterface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSuperMetaInterface(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SuperMetaInterfaceContext superMetaInterface() throws RecognitionException {
		SuperMetaInterfaceContext _localctx = new SuperMetaInterfaceContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_superMetaInterface);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71); match(20);
			setState(72); classType();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(73); match(4);
				setState(74); classType();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SpecificationContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public SpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSpecification(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSpecification(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecificationContext specification() throws RecognitionException {
		SpecificationContext _localctx = new SpecificationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 5) | (1L << 6) | (1L << 8) | (1L << 9) | (1L << 12) | (1L << 13) | (1L << 15) | (1L << 18) | (1L << 19) | (1L << 21) | (1L << 22) | (1L << 31) | (1L << 32) | (1L << NUMBER) | (1L << IDENTIFIER))) != 0)) {
				{
				{
				setState(80); statement();
				setState(81); match(29);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public GoalContext goal() {
			return getRuleContext(GoalContext.class,0);
		}
		public AxiomContext axiom() {
			return getRuleContext(AxiomContext.class,0);
		}
		public EquationContext equation() {
			return getRuleContext(EquationContext.class,0);
		}
		public AliasDeclarationContext aliasDeclaration() {
			return getRuleContext(AliasDeclarationContext.class,0);
		}
		public VariableAssignmentContext variableAssignment() {
			return getRuleContext(VariableAssignmentContext.class,0);
		}
		public AliasDefinitionContext aliasDefinition() {
			return getRuleContext(AliasDefinitionContext.class,0);
		}
		public VariableDeclarationContext variableDeclaration() {
			return getRuleContext(VariableDeclarationContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		try {
			setState(95);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(88); variableDeclaration();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(89); variableAssignment();
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(90); axiom();
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(91); goal();
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(92); aliasDeclaration();
				}
				break;

			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(93); aliasDefinition();
				}
				break;

			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(94); equation();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclarationContext extends ParserRuleContext {
		public VariableModifierContext variableModifier() {
			return getRuleContext(VariableModifierContext.class,0);
		}
		public VariableDeclaratorContext variableDeclarator(int i) {
			return getRuleContext(VariableDeclaratorContext.class,i);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<VariableDeclaratorContext> variableDeclarator() {
			return getRuleContexts(VariableDeclaratorContext.class);
		}
		public VariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclarationContext variableDeclaration() throws RecognitionException {
		VariableDeclarationContext _localctx = new VariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			_la = _input.LA(1);
			if (_la==18 || _la==32) {
				{
				setState(97); variableModifier();
				}
			}

			setState(100); type();
			setState(101); variableDeclarator();
			setState(106);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(102); match(4);
				setState(103); variableDeclarator();
				}
				}
				setState(108);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableModifierContext extends ParserRuleContext {
		public VariableModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableModifier; }
	 
		public VariableModifierContext() { }
		public void copyFrom(VariableModifierContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StaticVariableContext extends VariableModifierContext {
		public StaticVariableContext(VariableModifierContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterStaticVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitStaticVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitStaticVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstantVariableContext extends VariableModifierContext {
		public ConstantVariableContext(VariableModifierContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterConstantVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitConstantVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitConstantVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableModifierContext variableModifier() throws RecognitionException {
		VariableModifierContext _localctx = new VariableModifierContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variableModifier);
		try {
			setState(111);
			switch (_input.LA(1)) {
			case 18:
				_localctx = new StaticVariableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(109); match(18);
				}
				break;
			case 32:
				_localctx = new ConstantVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(110); match(32);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclaratorContext extends ParserRuleContext {
		public VariableDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarator; }
	 
		public VariableDeclaratorContext() { }
		public void copyFrom(VariableDeclaratorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SpecificationVariableContext extends VariableDeclaratorContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public SpecificationVariableDeclarationContext specificationVariableDeclaration() {
			return getRuleContext(SpecificationVariableDeclarationContext.class,0);
		}
		public SpecificationVariableContext(VariableDeclaratorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSpecificationVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSpecificationVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSpecificationVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableDeclaratorInitializerContext extends VariableDeclaratorContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public VariableInitializerContext variableInitializer() {
			return getRuleContext(VariableInitializerContext.class,0);
		}
		public VariableDeclaratorInitializerContext(VariableDeclaratorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableDeclaratorInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableDeclaratorInitializer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableDeclaratorInitializer(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableDeclaratorAssignerContext extends VariableDeclaratorContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public VariableAssignerContext variableAssigner() {
			return getRuleContext(VariableAssignerContext.class,0);
		}
		public VariableDeclaratorAssignerContext(VariableDeclaratorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableDeclaratorAssigner(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableDeclaratorAssigner(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableDeclaratorAssigner(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclaratorContext variableDeclarator() throws RecognitionException {
		VariableDeclaratorContext _localctx = new VariableDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_variableDeclarator);
		int _la;
		try {
			setState(128);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				_localctx = new VariableDeclaratorInitializerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(113); match(IDENTIFIER);
				setState(116);
				_la = _input.LA(1);
				if (_la==28) {
					{
					setState(114); match(28);
					setState(115); variableInitializer();
					}
				}

				}
				break;

			case 2:
				_localctx = new VariableDeclaratorAssignerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(118); match(IDENTIFIER);
				{
				setState(119); match(28);
				setState(120); variableAssigner();
				}
				}
				break;

			case 3:
				_localctx = new SpecificationVariableContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(121); match(IDENTIFIER);
				setState(126);
				_la = _input.LA(1);
				if (_la==8) {
					{
					setState(122); match(8);
					setState(123); specificationVariableDeclaration();
					setState(124); match(26);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SpecificationVariableDeclarationContext extends ParserRuleContext {
		public SpecificationVariableDeclaratorContext specificationVariableDeclarator(int i) {
			return getRuleContext(SpecificationVariableDeclaratorContext.class,i);
		}
		public List<SpecificationVariableDeclaratorContext> specificationVariableDeclarator() {
			return getRuleContexts(SpecificationVariableDeclaratorContext.class);
		}
		public SpecificationVariableDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specificationVariableDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSpecificationVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSpecificationVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSpecificationVariableDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecificationVariableDeclarationContext specificationVariableDeclaration() throws RecognitionException {
		SpecificationVariableDeclarationContext _localctx = new SpecificationVariableDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_specificationVariableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130); specificationVariableDeclarator();
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(131); match(4);
				setState(132); specificationVariableDeclarator();
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SpecificationVariableDeclaratorContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SpecificationVariableDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specificationVariableDeclarator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSpecificationVariableDeclarator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSpecificationVariableDeclarator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSpecificationVariableDeclarator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecificationVariableDeclaratorContext specificationVariableDeclarator() throws RecognitionException {
		SpecificationVariableDeclaratorContext _localctx = new SpecificationVariableDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_specificationVariableDeclarator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138); match(IDENTIFIER);
			setState(139); match(28);
			setState(140); expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableAssignmentContext extends ParserRuleContext {
		public VariableIdentifierContext variableIdentifier() {
			return getRuleContext(VariableIdentifierContext.class,0);
		}
		public VariableAssignerContext variableAssigner() {
			return getRuleContext(VariableAssignerContext.class,0);
		}
		public VariableAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableAssignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableAssignmentContext variableAssignment() throws RecognitionException {
		VariableAssignmentContext _localctx = new VariableAssignmentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_variableAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142); variableIdentifier();
			setState(143); match(28);
			setState(144); variableAssigner();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AxiomContext extends ParserRuleContext {
		public VariableIdentifierListContext inputVariables;
		public VariableIdentifierListContext outputVariables;
		public Token method;
		public VariableIdentifierListContext variableIdentifierList(int i) {
			return getRuleContext(VariableIdentifierListContext.class,i);
		}
		public SubtaskListContext subtaskList() {
			return getRuleContext(SubtaskListContext.class,0);
		}
		public ExceptionListContext exceptionList() {
			return getRuleContext(ExceptionListContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public List<VariableIdentifierListContext> variableIdentifierList() {
			return getRuleContexts(VariableIdentifierListContext.class);
		}
		public AxiomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_axiom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterAxiom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitAxiom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitAxiom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AxiomContext axiom() throws RecognitionException {
		AxiomContext _localctx = new AxiomContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_axiom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(146); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				break;

			case 2:
				{
				setState(147); subtaskList();
				}
				break;

			case 3:
				{
				{
				setState(148); subtaskList();
				setState(149); match(4);
				setState(150); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				}
				break;
			}
			setState(154); match(21);
			setState(155); ((AxiomContext)_localctx).outputVariables = variableIdentifierList();
			setState(158);
			_la = _input.LA(1);
			if (_la==4) {
				{
				setState(156); match(4);
				setState(157); exceptionList();
				}
			}

			setState(160); match(11);
			setState(161); ((AxiomContext)_localctx).method = match(IDENTIFIER);
			setState(162); match(14);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubtaskContext extends ParserRuleContext {
		public ClassTypeContext context;
		public VariableIdentifierListContext inputVariables;
		public VariableIdentifierListContext outputVariables;
		public VariableIdentifierListContext variableIdentifierList(int i) {
			return getRuleContext(VariableIdentifierListContext.class,i);
		}
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public List<VariableIdentifierListContext> variableIdentifierList() {
			return getRuleContexts(VariableIdentifierListContext.class);
		}
		public SubtaskContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtask; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSubtask(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSubtask(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSubtask(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubtaskContext subtask() throws RecognitionException {
		SubtaskContext _localctx = new SubtaskContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_subtask);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164); match(5);
			setState(168);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(165); ((SubtaskContext)_localctx).context = classType();
				setState(166); match(23);
				}
				break;
			}
			setState(170); ((SubtaskContext)_localctx).inputVariables = variableIdentifierList();
			setState(171); match(21);
			setState(172); ((SubtaskContext)_localctx).outputVariables = variableIdentifierList();
			setState(173); match(3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubtaskListContext extends ParserRuleContext {
		public SubtaskContext subtask(int i) {
			return getRuleContext(SubtaskContext.class,i);
		}
		public List<SubtaskContext> subtask() {
			return getRuleContexts(SubtaskContext.class);
		}
		public SubtaskListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subtaskList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterSubtaskList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitSubtaskList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitSubtaskList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubtaskListContext subtaskList() throws RecognitionException {
		SubtaskListContext _localctx = new SubtaskListContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_subtaskList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(175); subtask();
			setState(180);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(176); match(4);
					setState(177); subtask();
					}
					} 
				}
				setState(182);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExceptionListContext extends ParserRuleContext {
		public List<ClassTypeContext> classType() {
			return getRuleContexts(ClassTypeContext.class);
		}
		public ClassTypeContext classType(int i) {
			return getRuleContext(ClassTypeContext.class,i);
		}
		public ExceptionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exceptionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterExceptionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitExceptionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitExceptionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExceptionListContext exceptionList() throws RecognitionException {
		ExceptionListContext _localctx = new ExceptionListContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_exceptionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183); match(8);
			setState(184); classType();
			setState(185); match(26);
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(186); match(4);
				setState(187); match(8);
				setState(188); classType();
				setState(189); match(26);
				}
				}
				setState(195);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GoalContext extends ParserRuleContext {
		public VariableIdentifierListContext inputVariables;
		public VariableIdentifierListContext outputVariables;
		public VariableIdentifierListContext variableIdentifierList(int i) {
			return getRuleContext(VariableIdentifierListContext.class,i);
		}
		public List<VariableIdentifierListContext> variableIdentifierList() {
			return getRuleContexts(VariableIdentifierListContext.class);
		}
		public GoalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterGoal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitGoal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitGoal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalContext goal() throws RecognitionException {
		GoalContext _localctx = new GoalContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_goal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(196); ((GoalContext)_localctx).inputVariables = variableIdentifierList();
				}
			}

			setState(199); match(21);
			setState(200); ((GoalContext)_localctx).outputVariables = variableIdentifierList();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AliasDeclarationContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public AliasStructureContext aliasStructure() {
			return getRuleContext(AliasStructureContext.class,0);
		}
		public AliasDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterAliasDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitAliasDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitAliasDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasDeclarationContext aliasDeclaration() throws RecognitionException {
		AliasDeclarationContext _localctx = new AliasDeclarationContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_aliasDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202); match(31);
			setState(207);
			_la = _input.LA(1);
			if (_la==8) {
				{
				setState(203); match(8);
				setState(204); type();
				setState(205); match(26);
				}
			}

			setState(209); match(IDENTIFIER);
			setState(212);
			_la = _input.LA(1);
			if (_la==28) {
				{
				setState(210); match(28);
				setState(211); aliasStructure();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AliasStructureContext extends ParserRuleContext {
		public VariableIdentifierListContext variableAlias;
		public WildcardAliasContext wildcardAliasName;
		public VariableIdentifierListContext variableIdentifierList() {
			return getRuleContext(VariableIdentifierListContext.class,0);
		}
		public WildcardAliasContext wildcardAlias() {
			return getRuleContext(WildcardAliasContext.class,0);
		}
		public AliasStructureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasStructure; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterAliasStructure(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitAliasStructure(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitAliasStructure(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasStructureContext aliasStructure() throws RecognitionException {
		AliasStructureContext _localctx = new AliasStructureContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_aliasStructure);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			_la = _input.LA(1);
			if ( !(_la==5 || _la==8) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(217);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(215); ((AliasStructureContext)_localctx).variableAlias = variableIdentifierList();
				}
				break;
			case 30:
				{
				setState(216); ((AliasStructureContext)_localctx).wildcardAliasName = wildcardAlias();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(219);
			_la = _input.LA(1);
			if ( !(_la==3 || _la==26) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WildcardAliasContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public WildcardAliasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcardAlias; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterWildcardAlias(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitWildcardAlias(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitWildcardAlias(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WildcardAliasContext wildcardAlias() throws RecognitionException {
		WildcardAliasContext _localctx = new WildcardAliasContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_wildcardAlias);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221); match(30);
			setState(222); match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AliasDefinitionContext extends ParserRuleContext {
		public VariableIdentifierContext variableIdentifier() {
			return getRuleContext(VariableIdentifierContext.class,0);
		}
		public AliasStructureContext aliasStructure() {
			return getRuleContext(AliasStructureContext.class,0);
		}
		public AliasDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterAliasDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitAliasDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitAliasDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasDefinitionContext aliasDefinition() throws RecognitionException {
		AliasDefinitionContext _localctx = new AliasDefinitionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_aliasDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224); variableIdentifier();
			setState(225); match(28);
			setState(226); aliasStructure();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public PrimitiveTypeContext primitiveType() {
			return getRuleContext(PrimitiveTypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(228); classType();
				}
				break;
			case 1:
			case 2:
			case 9:
			case 12:
			case 13:
			case 15:
			case 19:
			case 22:
				{
				setState(229); primitiveType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==5) {
				{
				{
				setState(232); match(5);
				setState(233); match(3);
				}
				}
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassTypeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpecificationLanguageParser.IDENTIFIER, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpecificationLanguageParser.IDENTIFIER); }
		public ClassTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterClassType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitClassType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitClassType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassTypeContext classType() throws RecognitionException {
		ClassTypeContext _localctx = new ClassTypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_classType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239); match(IDENTIFIER);
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==25) {
				{
				{
				setState(240); match(25);
				setState(241); match(IDENTIFIER);
				}
				}
				setState(246);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimitiveTypeContext extends ParserRuleContext {
		public PrimitiveTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primitiveType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitPrimitiveType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitPrimitiveType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 9) | (1L << 12) | (1L << 13) | (1L << 15) | (1L << 19) | (1L << 22))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EquationContext extends ParserRuleContext {
		public ExpressionContext left;
		public ExpressionContext right;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public EquationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterEquation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitEquation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitEquation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EquationContext equation() throws RecognitionException {
		EquationContext _localctx = new EquationContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249); ((EquationContext)_localctx).left = expression(0);
			setState(250); match(28);
			setState(251); ((EquationContext)_localctx).right = expression(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public int _p;
		public ExpressionContext left;
		public Token op;
		public ExpressionContext right;
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExpressionContext(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState, _p);
		ExpressionContext _prevctx = _localctx;
		int _startState = 46;
		enterRecursionRule(_localctx, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(254); match(6);
				setState(255); expression(5);
				}
				break;

			case 2:
				{
				setState(256); term();
				}
				break;

			case 3:
				{
				setState(257); match(8);
				setState(258); expression(0);
				setState(259); match(26);
				}
				break;

			case 4:
				{
				setState(261); match(IDENTIFIER);
				setState(262); match(8);
				setState(263); expression(0);
				setState(264); match(26);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(279);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(277);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(268);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(269);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==7 || _la==34) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(270); ((ExpressionContext)_localctx).right = expression(4);
						}
						break;

					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(271);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(272);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==6 || _la==27) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(273); ((ExpressionContext)_localctx).right = expression(3);
						}
						break;

					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(274);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(275); match(24);
						setState(276); expression(2);
						}
						break;
					}
					} 
				}
				setState(281);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public VariableIdentifierContext variableIdentifier() {
			return getRuleContext(VariableIdentifierContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(SpecificationLanguageParser.NUMBER, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_term);
		try {
			setState(284);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(282); match(NUMBER);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(283); variableIdentifier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayContext extends ParserRuleContext {
		public InArrayVariableAssignerContext inArrayVariableAssigner(int i) {
			return getRuleContext(InArrayVariableAssignerContext.class,i);
		}
		public List<InArrayVariableAssignerContext> inArrayVariableAssigner() {
			return getRuleContexts(InArrayVariableAssignerContext.class);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286); match(11);
			setState(295);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 8) | (1L << 10) | (1L << 11) | (1L << 16) | (1L << 33) | (1L << NUMBER) | (1L << STRING) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(287); inArrayVariableAssigner();
				setState(292);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(288); match(4);
					setState(289); inArrayVariableAssigner();
					}
					}
					setState(294);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(297); match(14);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InArrayVariableAssignerContext extends ParserRuleContext {
		public VariableAssignerContext variableAssigner() {
			return getRuleContext(VariableAssignerContext.class,0);
		}
		public VariableInitializerContext variableInitializer() {
			return getRuleContext(VariableInitializerContext.class,0);
		}
		public InArrayVariableAssignerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inArrayVariableAssigner; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterInArrayVariableAssigner(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitInArrayVariableAssigner(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitInArrayVariableAssigner(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InArrayVariableAssignerContext inArrayVariableAssigner() throws RecognitionException {
		InArrayVariableAssignerContext _localctx = new InArrayVariableAssignerContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_inArrayVariableAssigner);
		try {
			setState(301);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(299); variableAssigner();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(300); variableInitializer();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableAssignerContext extends ParserRuleContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ClassTypeContext classType() {
			return getRuleContext(ClassTypeContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public TerminalNode STRING() { return getToken(SpecificationLanguageParser.STRING, 0); }
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public VariableAssignerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableAssigner; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableAssigner(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableAssigner(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableAssigner(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableAssignerContext variableAssigner() throws RecognitionException {
		VariableAssignerContext _localctx = new VariableAssignerContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_variableAssigner);
		int _la;
		try {
			setState(320);
			switch (_input.LA(1)) {
			case 11:
				enterOuterAlt(_localctx, 1);
				{
				setState(303); array();
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 2);
				{
				setState(304); match(33);
				setState(305); classType();
				setState(306); match(8);
				setState(307); expression(0);
				setState(312);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(308); match(4);
					setState(309); expression(0);
					}
					}
					setState(314);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(315); match(26);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(317); match(STRING);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 4);
				{
				setState(318); match(16);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 5);
				{
				setState(319); match(10);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableInitializerContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public VariableInitializerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableInitializer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableInitializer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableInitializer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableInitializerContext variableInitializer() throws RecognitionException {
		VariableInitializerContext _localctx = new VariableInitializerContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_variableInitializer);
		try {
			setState(324);
			switch (_input.LA(1)) {
			case 11:
				enterOuterAlt(_localctx, 1);
				{
				setState(322); array();
				}
				break;
			case 6:
			case 8:
			case NUMBER:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(323); expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableIdentifierContext extends ParserRuleContext {
		public VariableIdentifierContext variableIdentifier() {
			return getRuleContext(VariableIdentifierContext.class,0);
		}
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpecificationLanguageParser.IDENTIFIER, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpecificationLanguageParser.IDENTIFIER); }
		public List<TerminalNode> ALIAS_ELEMENT_REF() { return getTokens(SpecificationLanguageParser.ALIAS_ELEMENT_REF); }
		public TerminalNode ALIAS_ELEMENT_REF(int i) {
			return getToken(SpecificationLanguageParser.ALIAS_ELEMENT_REF, i);
		}
		public VariableIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableIdentifierContext variableIdentifier() throws RecognitionException {
		VariableIdentifierContext _localctx = new VariableIdentifierContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_variableIdentifier);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(326); match(IDENTIFIER);
			setState(334);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(330);
					switch (_input.LA(1)) {
					case 25:
						{
						setState(327); match(25);
						setState(328); match(IDENTIFIER);
						}
						break;
					case 7:
						{
						setState(329); match(7);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(336);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			setState(340);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(337); match(ALIAS_ELEMENT_REF);
					}
					} 
				}
				setState(342);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			setState(345);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(343); match(25);
				setState(344); variableIdentifier();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableIdentifierListContext extends ParserRuleContext {
		public List<VariableIdentifierContext> variableIdentifier() {
			return getRuleContexts(VariableIdentifierContext.class);
		}
		public VariableIdentifierContext variableIdentifier(int i) {
			return getRuleContext(VariableIdentifierContext.class,i);
		}
		public VariableIdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableIdentifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).enterVariableIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationLanguageListener ) ((SpecificationLanguageListener)listener).exitVariableIdentifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationLanguageVisitor ) return ((SpecificationLanguageVisitor<? extends T>)visitor).visitVariableIdentifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableIdentifierListContext variableIdentifierList() throws RecognitionException {
		VariableIdentifierListContext _localctx = new VariableIdentifierListContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_variableIdentifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(347); variableIdentifier();
			setState(352);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(348); match(4);
					setState(349); variableIdentifier();
					}
					} 
				}
				setState(354);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 23: return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 3 >= _localctx._p;

		case 1: return 2 >= _localctx._p;

		case 2: return 1 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3.\u0166\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \3\2"+
		"\3\2\3\2\5\2D\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3N\n\3\f\3\16\3Q\13"+
		"\3\3\4\3\4\3\4\7\4V\n\4\f\4\16\4Y\13\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5"+
		"b\n\5\3\6\5\6e\n\6\3\6\3\6\3\6\3\6\7\6k\n\6\f\6\16\6n\13\6\3\7\3\7\5\7"+
		"r\n\7\3\b\3\b\3\b\5\bw\n\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u0081\n"+
		"\b\5\b\u0083\n\b\3\t\3\t\3\t\7\t\u0088\n\t\f\t\16\t\u008b\13\t\3\n\3\n"+
		"\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u009b\n\f\3\f"+
		"\3\f\3\f\3\f\5\f\u00a1\n\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\5\r\u00ab\n"+
		"\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\7\16\u00b5\n\16\f\16\16\16\u00b8"+
		"\13\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00c2\n\17\f\17\16"+
		"\17\u00c5\13\17\3\20\5\20\u00c8\n\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\5\21\u00d2\n\21\3\21\3\21\3\21\5\21\u00d7\n\21\3\22\3\22\3\22\5"+
		"\22\u00dc\n\22\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25"+
		"\5\25\u00e9\n\25\3\25\3\25\7\25\u00ed\n\25\f\25\16\25\u00f0\13\25\3\26"+
		"\3\26\3\26\7\26\u00f5\n\26\f\26\16\26\u00f8\13\26\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\5\31\u010d\n\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\7\31"+
		"\u0118\n\31\f\31\16\31\u011b\13\31\3\32\3\32\5\32\u011f\n\32\3\33\3\33"+
		"\3\33\3\33\7\33\u0125\n\33\f\33\16\33\u0128\13\33\5\33\u012a\n\33\3\33"+
		"\3\33\3\34\3\34\5\34\u0130\n\34\3\35\3\35\3\35\3\35\3\35\3\35\3\35\7\35"+
		"\u0139\n\35\f\35\16\35\u013c\13\35\3\35\3\35\3\35\3\35\3\35\5\35\u0143"+
		"\n\35\3\36\3\36\5\36\u0147\n\36\3\37\3\37\3\37\3\37\5\37\u014d\n\37\7"+
		"\37\u014f\n\37\f\37\16\37\u0152\13\37\3\37\7\37\u0155\n\37\f\37\16\37"+
		"\u0158\13\37\3\37\3\37\5\37\u015c\n\37\3 \3 \3 \7 \u0161\n \f \16 \u0164"+
		"\13 \3 \2!\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66"+
		"8:<>\2\7\4\2\7\7\n\n\4\2\5\5\34\34\b\2\3\4\13\13\16\17\21\21\25\25\30"+
		"\30\4\2\t\t$$\4\2\b\b\35\35\u0179\2@\3\2\2\2\4I\3\2\2\2\6W\3\2\2\2\ba"+
		"\3\2\2\2\nd\3\2\2\2\fq\3\2\2\2\16\u0082\3\2\2\2\20\u0084\3\2\2\2\22\u008c"+
		"\3\2\2\2\24\u0090\3\2\2\2\26\u009a\3\2\2\2\30\u00a6\3\2\2\2\32\u00b1\3"+
		"\2\2\2\34\u00b9\3\2\2\2\36\u00c7\3\2\2\2 \u00cc\3\2\2\2\"\u00d8\3\2\2"+
		"\2$\u00df\3\2\2\2&\u00e2\3\2\2\2(\u00e8\3\2\2\2*\u00f1\3\2\2\2,\u00f9"+
		"\3\2\2\2.\u00fb\3\2\2\2\60\u010c\3\2\2\2\62\u011e\3\2\2\2\64\u0120\3\2"+
		"\2\2\66\u012f\3\2\2\28\u0142\3\2\2\2:\u0146\3\2\2\2<\u0148\3\2\2\2>\u015d"+
		"\3\2\2\2@A\7\23\2\2AC\7(\2\2BD\5\4\3\2CB\3\2\2\2CD\3\2\2\2DE\3\2\2\2E"+
		"F\7\r\2\2FG\5\6\4\2GH\7\20\2\2H\3\3\2\2\2IJ\7\26\2\2JO\5*\26\2KL\7\6\2"+
		"\2LN\5*\26\2MK\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\5\3\2\2\2QO\3\2"+
		"\2\2RS\5\b\5\2ST\7\37\2\2TV\3\2\2\2UR\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3"+
		"\2\2\2X\7\3\2\2\2YW\3\2\2\2Zb\5\n\6\2[b\5\24\13\2\\b\5\26\f\2]b\5\36\20"+
		"\2^b\5 \21\2_b\5&\24\2`b\5.\30\2aZ\3\2\2\2a[\3\2\2\2a\\\3\2\2\2a]\3\2"+
		"\2\2a^\3\2\2\2a_\3\2\2\2a`\3\2\2\2b\t\3\2\2\2ce\5\f\7\2dc\3\2\2\2de\3"+
		"\2\2\2ef\3\2\2\2fg\5(\25\2gl\5\16\b\2hi\7\6\2\2ik\5\16\b\2jh\3\2\2\2k"+
		"n\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\13\3\2\2\2nl\3\2\2\2or\7\24\2\2pr\7\"\2"+
		"\2qo\3\2\2\2qp\3\2\2\2r\r\3\2\2\2sv\7(\2\2tu\7\36\2\2uw\5:\36\2vt\3\2"+
		"\2\2vw\3\2\2\2w\u0083\3\2\2\2xy\7(\2\2yz\7\36\2\2z\u0083\58\35\2{\u0080"+
		"\7(\2\2|}\7\n\2\2}~\5\20\t\2~\177\7\34\2\2\177\u0081\3\2\2\2\u0080|\3"+
		"\2\2\2\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082s\3\2\2\2\u0082x\3"+
		"\2\2\2\u0082{\3\2\2\2\u0083\17\3\2\2\2\u0084\u0089\5\22\n\2\u0085\u0086"+
		"\7\6\2\2\u0086\u0088\5\22\n\2\u0087\u0085\3\2\2\2\u0088\u008b\3\2\2\2"+
		"\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\21\3\2\2\2\u008b\u0089"+
		"\3\2\2\2\u008c\u008d\7(\2\2\u008d\u008e\7\36\2\2\u008e\u008f\5\60\31\2"+
		"\u008f\23\3\2\2\2\u0090\u0091\5<\37\2\u0091\u0092\7\36\2\2\u0092\u0093"+
		"\58\35\2\u0093\25\3\2\2\2\u0094\u009b\5> \2\u0095\u009b\5\32\16\2\u0096"+
		"\u0097\5\32\16\2\u0097\u0098\7\6\2\2\u0098\u0099\5> \2\u0099\u009b\3\2"+
		"\2\2\u009a\u0094\3\2\2\2\u009a\u0095\3\2\2\2\u009a\u0096\3\2\2\2\u009b"+
		"\u009c\3\2\2\2\u009c\u009d\7\27\2\2\u009d\u00a0\5> \2\u009e\u009f\7\6"+
		"\2\2\u009f\u00a1\5\34\17\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u00a2\3\2\2\2\u00a2\u00a3\7\r\2\2\u00a3\u00a4\7(\2\2\u00a4\u00a5\7\20"+
		"\2\2\u00a5\27\3\2\2\2\u00a6\u00aa\7\7\2\2\u00a7\u00a8\5*\26\2\u00a8\u00a9"+
		"\7\31\2\2\u00a9\u00ab\3\2\2\2\u00aa\u00a7\3\2\2\2\u00aa\u00ab\3\2\2\2"+
		"\u00ab\u00ac\3\2\2\2\u00ac\u00ad\5> \2\u00ad\u00ae\7\27\2\2\u00ae\u00af"+
		"\5> \2\u00af\u00b0\7\5\2\2\u00b0\31\3\2\2\2\u00b1\u00b6\5\30\r\2\u00b2"+
		"\u00b3\7\6\2\2\u00b3\u00b5\5\30\r\2\u00b4\u00b2\3\2\2\2\u00b5\u00b8\3"+
		"\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\33\3\2\2\2\u00b8"+
		"\u00b6\3\2\2\2\u00b9\u00ba\7\n\2\2\u00ba\u00bb\5*\26\2\u00bb\u00c3\7\34"+
		"\2\2\u00bc\u00bd\7\6\2\2\u00bd\u00be\7\n\2\2\u00be\u00bf\5*\26\2\u00bf"+
		"\u00c0\7\34\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bc\3\2\2\2\u00c2\u00c5\3"+
		"\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\35\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c6\u00c8\5> \2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2"+
		"\2\u00c8\u00c9\3\2\2\2\u00c9\u00ca\7\27\2\2\u00ca\u00cb\5> \2\u00cb\37"+
		"\3\2\2\2\u00cc\u00d1\7!\2\2\u00cd\u00ce\7\n\2\2\u00ce\u00cf\5(\25\2\u00cf"+
		"\u00d0\7\34\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00cd\3\2\2\2\u00d1\u00d2\3"+
		"\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d6\7(\2\2\u00d4\u00d5\7\36\2\2\u00d5"+
		"\u00d7\5\"\22\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7!\3\2\2\2"+
		"\u00d8\u00db\t\2\2\2\u00d9\u00dc\5> \2\u00da\u00dc\5$\23\2\u00db\u00d9"+
		"\3\2\2\2\u00db\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00de\t\3\2\2\u00de"+
		"#\3\2\2\2\u00df\u00e0\7 \2\2\u00e0\u00e1\7(\2\2\u00e1%\3\2\2\2\u00e2\u00e3"+
		"\5<\37\2\u00e3\u00e4\7\36\2\2\u00e4\u00e5\5\"\22\2\u00e5\'\3\2\2\2\u00e6"+
		"\u00e9\5*\26\2\u00e7\u00e9\5,\27\2\u00e8\u00e6\3\2\2\2\u00e8\u00e7\3\2"+
		"\2\2\u00e9\u00ee\3\2\2\2\u00ea\u00eb\7\7\2\2\u00eb\u00ed\7\5\2\2\u00ec"+
		"\u00ea\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2"+
		"\2\2\u00ef)\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f6\7(\2\2\u00f2\u00f3"+
		"\7\33\2\2\u00f3\u00f5\7(\2\2\u00f4\u00f2\3\2\2\2\u00f5\u00f8\3\2\2\2\u00f6"+
		"\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7+\3\2\2\2\u00f8\u00f6\3\2\2\2"+
		"\u00f9\u00fa\t\4\2\2\u00fa-\3\2\2\2\u00fb\u00fc\5\60\31\2\u00fc\u00fd"+
		"\7\36\2\2\u00fd\u00fe\5\60\31\2\u00fe/\3\2\2\2\u00ff\u0100\b\31\1\2\u0100"+
		"\u0101\7\b\2\2\u0101\u010d\5\60\31\2\u0102\u010d\5\62\32\2\u0103\u0104"+
		"\7\n\2\2\u0104\u0105\5\60\31\2\u0105\u0106\7\34\2\2\u0106\u010d\3\2\2"+
		"\2\u0107\u0108\7(\2\2\u0108\u0109\7\n\2\2\u0109\u010a\5\60\31\2\u010a"+
		"\u010b\7\34\2\2\u010b\u010d\3\2\2\2\u010c\u00ff\3\2\2\2\u010c\u0102\3"+
		"\2\2\2\u010c\u0103\3\2\2\2\u010c\u0107\3\2\2\2\u010d\u0119\3\2\2\2\u010e"+
		"\u010f\6\31\2\3\u010f\u0110\t\5\2\2\u0110\u0118\5\60\31\2\u0111\u0112"+
		"\6\31\3\3\u0112\u0113\t\6\2\2\u0113\u0118\5\60\31\2\u0114\u0115\6\31\4"+
		"\3\u0115\u0116\7\32\2\2\u0116\u0118\5\60\31\2\u0117\u010e\3\2\2\2\u0117"+
		"\u0111\3\2\2\2\u0117\u0114\3\2\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2"+
		"\2\2\u0119\u011a\3\2\2\2\u011a\61\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u011f"+
		"\7%\2\2\u011d\u011f\5<\37\2\u011e\u011c\3\2\2\2\u011e\u011d\3\2\2\2\u011f"+
		"\63\3\2\2\2\u0120\u0129\7\r\2\2\u0121\u0126\5\66\34\2\u0122\u0123\7\6"+
		"\2\2\u0123\u0125\5\66\34\2\u0124\u0122\3\2\2\2\u0125\u0128\3\2\2\2\u0126"+
		"\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u012a\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0129\u0121\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\u012c\7\20\2\2\u012c\65\3\2\2\2\u012d\u0130\58\35\2\u012e\u0130\5:\36"+
		"\2\u012f\u012d\3\2\2\2\u012f\u012e\3\2\2\2\u0130\67\3\2\2\2\u0131\u0143"+
		"\5\64\33\2\u0132\u0133\7#\2\2\u0133\u0134\5*\26\2\u0134\u0135\7\n\2\2"+
		"\u0135\u013a\5\60\31\2\u0136\u0137\7\6\2\2\u0137\u0139\5\60\31\2\u0138"+
		"\u0136\3\2\2\2\u0139\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2"+
		"\2\2\u013b\u013d\3\2\2\2\u013c\u013a\3\2\2\2\u013d\u013e\7\34\2\2\u013e"+
		"\u0143\3\2\2\2\u013f\u0143\7\'\2\2\u0140\u0143\7\22\2\2\u0141\u0143\7"+
		"\f\2\2\u0142\u0131\3\2\2\2\u0142\u0132\3\2\2\2\u0142\u013f\3\2\2\2\u0142"+
		"\u0140\3\2\2\2\u0142\u0141\3\2\2\2\u01439\3\2\2\2\u0144\u0147\5\64\33"+
		"\2\u0145\u0147\5\60\31\2\u0146\u0144\3\2\2\2\u0146\u0145\3\2\2\2\u0147"+
		";\3\2\2\2\u0148\u0150\7(\2\2\u0149\u014a\7\33\2\2\u014a\u014d\7(\2\2\u014b"+
		"\u014d\7\t\2\2\u014c\u0149\3\2\2\2\u014c\u014b\3\2\2\2\u014d\u014f\3\2"+
		"\2\2\u014e\u014c\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150"+
		"\u0151\3\2\2\2\u0151\u0156\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0155\7)"+
		"\2\2\u0154\u0153\3\2\2\2\u0155\u0158\3\2\2\2\u0156\u0154\3\2\2\2\u0156"+
		"\u0157\3\2\2\2\u0157\u015b\3\2\2\2\u0158\u0156\3\2\2\2\u0159\u015a\7\33"+
		"\2\2\u015a\u015c\5<\37\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c"+
		"=\3\2\2\2\u015d\u0162\5<\37\2\u015e\u015f\7\6\2\2\u015f\u0161\5<\37\2"+
		"\u0160\u015e\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163"+
		"\3\2\2\2\u0163?\3\2\2\2\u0164\u0162\3\2\2\2(COWadlqv\u0080\u0082\u0089"+
		"\u009a\u00a0\u00aa\u00b6\u00c3\u00c7\u00d1\u00d6\u00db\u00e8\u00ee\u00f6"+
		"\u010c\u0117\u0119\u011e\u0126\u0129\u012f\u013a\u0142\u0146\u014c\u0150"+
		"\u0156\u015b\u0162";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
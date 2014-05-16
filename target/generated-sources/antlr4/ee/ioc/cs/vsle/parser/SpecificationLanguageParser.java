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
		RULE_variableDeclaration = 3, RULE_variableModifier = 4, RULE_variableDeclarator = 5, 
		RULE_variableAssignment = 6, RULE_axiom = 7, RULE_subtask = 8, RULE_subtaskList = 9, 
		RULE_exceptionList = 10, RULE_goal = 11, RULE_aliasDeclaration = 12, RULE_aliasStructure = 13, 
		RULE_aliasDefinition = 14, RULE_type = 15, RULE_classType = 16, RULE_primitiveType = 17, 
		RULE_equation = 18, RULE_expression = 19, RULE_term = 20, RULE_array = 21, 
		RULE_inArrayVariableAssigner = 22, RULE_variableAssigner = 23, RULE_variableInitializer = 24, 
		RULE_variableIdentifier = 25, RULE_variableIdentifierList = 26;
	public static final String[] ruleNames = {
		"metaInterfase", "superMetaInterface", "specification", "variableDeclaration", 
		"variableModifier", "variableDeclarator", "variableAssignment", "axiom", 
		"subtask", "subtaskList", "exceptionList", "goal", "aliasDeclaration", 
		"aliasStructure", "aliasDefinition", "type", "classType", "primitiveType", 
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
			setState(54); match(17);
			setState(55); match(IDENTIFIER);
			setState(57);
			_la = _input.LA(1);
			if (_la==20) {
				{
				setState(56); superMetaInterface();
				}
			}

			setState(59); match(11);
			setState(60); specification();
			setState(61); match(14);
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
			setState(63); match(20);
			setState(64); classType();
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(65); match(4);
				setState(66); classType();
				}
				}
				setState(71);
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
		public List<GoalContext> goal() {
			return getRuleContexts(GoalContext.class);
		}
		public VariableDeclarationContext variableDeclaration(int i) {
			return getRuleContext(VariableDeclarationContext.class,i);
		}
		public AliasDeclarationContext aliasDeclaration(int i) {
			return getRuleContext(AliasDeclarationContext.class,i);
		}
		public AxiomContext axiom(int i) {
			return getRuleContext(AxiomContext.class,i);
		}
		public AliasDefinitionContext aliasDefinition(int i) {
			return getRuleContext(AliasDefinitionContext.class,i);
		}
		public List<VariableAssignmentContext> variableAssignment() {
			return getRuleContexts(VariableAssignmentContext.class);
		}
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public VariableAssignmentContext variableAssignment(int i) {
			return getRuleContext(VariableAssignmentContext.class,i);
		}
		public List<AxiomContext> axiom() {
			return getRuleContexts(AxiomContext.class);
		}
		public GoalContext goal(int i) {
			return getRuleContext(GoalContext.class,i);
		}
		public List<EquationContext> equation() {
			return getRuleContexts(EquationContext.class);
		}
		public List<AliasDeclarationContext> aliasDeclaration() {
			return getRuleContexts(AliasDeclarationContext.class);
		}
		public EquationContext equation(int i) {
			return getRuleContext(EquationContext.class,i);
		}
		public List<AliasDefinitionContext> aliasDefinition() {
			return getRuleContexts(AliasDefinitionContext.class);
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
				setState(79);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(72); variableDeclaration();
					}
					break;

				case 2:
					{
					setState(73); variableAssignment();
					}
					break;

				case 3:
					{
					setState(74); axiom();
					}
					break;

				case 4:
					{
					setState(75); goal();
					}
					break;

				case 5:
					{
					setState(76); aliasDeclaration();
					}
					break;

				case 6:
					{
					setState(77); aliasDefinition();
					}
					break;

				case 7:
					{
					setState(78); equation();
					}
					break;
				}
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
		enterRule(_localctx, 6, RULE_variableDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			_la = _input.LA(1);
			if (_la==18 || _la==32) {
				{
				setState(88); variableModifier();
				}
			}

			setState(91); type();
			setState(92); variableDeclarator();
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(93); match(4);
				setState(94); variableDeclarator();
				}
				}
				setState(99);
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
		enterRule(_localctx, 8, RULE_variableModifier);
		try {
			setState(102);
			switch (_input.LA(1)) {
			case 18:
				_localctx = new StaticVariableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(100); match(18);
				}
				break;
			case 32:
				_localctx = new ConstantVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(101); match(32);
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
		enterRule(_localctx, 10, RULE_variableDeclarator);
		int _la;
		try {
			setState(112);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				_localctx = new VariableDeclaratorInitializerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(104); match(IDENTIFIER);
				setState(107);
				_la = _input.LA(1);
				if (_la==28) {
					{
					setState(105); match(28);
					setState(106); variableInitializer();
					}
				}

				}
				break;

			case 2:
				_localctx = new VariableDeclaratorAssignerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(109); match(IDENTIFIER);
				{
				setState(110); match(28);
				setState(111); variableAssigner();
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
		enterRule(_localctx, 12, RULE_variableAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114); variableIdentifier();
			setState(115); match(28);
			setState(116); variableAssigner();
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
		enterRule(_localctx, 14, RULE_axiom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(118); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				break;

			case 2:
				{
				setState(119); subtaskList();
				}
				break;

			case 3:
				{
				{
				setState(120); subtaskList();
				setState(121); match(4);
				setState(122); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				}
				break;
			}
			setState(126); match(21);
			setState(127); ((AxiomContext)_localctx).outputVariables = variableIdentifierList();
			setState(130);
			_la = _input.LA(1);
			if (_la==4) {
				{
				setState(128); match(4);
				setState(129); exceptionList();
				}
			}

			setState(132); match(11);
			setState(133); ((AxiomContext)_localctx).method = match(IDENTIFIER);
			setState(134); match(14);
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
		enterRule(_localctx, 16, RULE_subtask);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136); match(5);
			setState(140);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(137); ((SubtaskContext)_localctx).context = classType();
				setState(138); match(23);
				}
				break;
			}
			setState(142); ((SubtaskContext)_localctx).inputVariables = variableIdentifierList();
			setState(143); match(21);
			setState(144); ((SubtaskContext)_localctx).outputVariables = variableIdentifierList();
			setState(145); match(3);
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
		enterRule(_localctx, 18, RULE_subtaskList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(147); subtask();
			setState(152);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(148); match(4);
					setState(149); subtask();
					}
					} 
				}
				setState(154);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
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
		enterRule(_localctx, 20, RULE_exceptionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155); match(8);
			setState(156); classType();
			setState(157); match(26);
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(158); match(4);
				setState(159); match(8);
				setState(160); classType();
				setState(161); match(26);
				}
				}
				setState(167);
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
		enterRule(_localctx, 22, RULE_goal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(168); ((GoalContext)_localctx).inputVariables = variableIdentifierList();
				}
			}

			setState(171); match(21);
			setState(172); ((GoalContext)_localctx).outputVariables = variableIdentifierList();
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
		enterRule(_localctx, 24, RULE_aliasDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174); match(31);
			setState(179);
			_la = _input.LA(1);
			if (_la==8) {
				{
				setState(175); match(8);
				setState(176); type();
				setState(177); match(26);
				}
			}

			setState(181); match(IDENTIFIER);
			setState(184);
			_la = _input.LA(1);
			if (_la==28) {
				{
				setState(182); match(28);
				setState(183); aliasStructure();
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
		public Token wildcardAlias;
		public TerminalNode IDENTIFIER() { return getToken(SpecificationLanguageParser.IDENTIFIER, 0); }
		public VariableIdentifierListContext variableIdentifierList() {
			return getRuleContext(VariableIdentifierListContext.class,0);
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
		enterRule(_localctx, 26, RULE_aliasStructure);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186); match(8);
			setState(190);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(187); ((AliasStructureContext)_localctx).variableAlias = variableIdentifierList();
				}
				break;
			case 30:
				{
				setState(188); match(30);
				setState(189); ((AliasStructureContext)_localctx).wildcardAlias = match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(192); match(26);
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
		enterRule(_localctx, 28, RULE_aliasDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194); variableIdentifier();
			setState(195); match(28);
			setState(196); aliasStructure();
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
		enterRule(_localctx, 30, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(198); classType();
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
				setState(199); primitiveType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==5) {
				{
				{
				setState(202); match(5);
				setState(203); match(3);
				}
				}
				setState(208);
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
		enterRule(_localctx, 32, RULE_classType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209); match(IDENTIFIER);
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==25) {
				{
				{
				setState(210); match(25);
				setState(211); match(IDENTIFIER);
				}
				}
				setState(216);
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
		enterRule(_localctx, 34, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
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
		enterRule(_localctx, 36, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219); ((EquationContext)_localctx).left = expression(0);
			setState(220); match(28);
			setState(221); ((EquationContext)_localctx).right = expression(0);
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
		int _startState = 38;
		enterRecursionRule(_localctx, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(224); match(6);
				setState(225); expression(5);
				}
				break;

			case 2:
				{
				setState(226); term();
				}
				break;

			case 3:
				{
				setState(227); match(8);
				setState(228); expression(0);
				setState(229); match(26);
				}
				break;

			case 4:
				{
				setState(231); match(IDENTIFIER);
				setState(232); match(8);
				setState(233); expression(0);
				setState(234); match(26);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(249);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(247);
					switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(238);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(239);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==7 || _la==34) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(240); ((ExpressionContext)_localctx).right = expression(4);
						}
						break;

					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(241);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(242);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==6 || _la==27) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(243); ((ExpressionContext)_localctx).right = expression(3);
						}
						break;

					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(244);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(245); match(24);
						setState(246); expression(2);
						}
						break;
					}
					} 
				}
				setState(251);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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
		enterRule(_localctx, 40, RULE_term);
		try {
			setState(254);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(252); match(NUMBER);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(253); variableIdentifier();
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
		enterRule(_localctx, 42, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256); match(11);
			setState(265);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 8) | (1L << 10) | (1L << 11) | (1L << 16) | (1L << 33) | (1L << NUMBER) | (1L << STRING) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(257); inArrayVariableAssigner();
				setState(262);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(258); match(4);
					setState(259); inArrayVariableAssigner();
					}
					}
					setState(264);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(267); match(14);
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
		enterRule(_localctx, 44, RULE_inArrayVariableAssigner);
		try {
			setState(271);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(269); variableAssigner();
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(270); variableInitializer();
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
		enterRule(_localctx, 46, RULE_variableAssigner);
		int _la;
		try {
			setState(290);
			switch (_input.LA(1)) {
			case 11:
				enterOuterAlt(_localctx, 1);
				{
				setState(273); array();
				}
				break;
			case 33:
				enterOuterAlt(_localctx, 2);
				{
				setState(274); match(33);
				setState(275); classType();
				setState(276); match(8);
				setState(277); expression(0);
				setState(282);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(278); match(4);
					setState(279); expression(0);
					}
					}
					setState(284);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(285); match(26);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 3);
				{
				setState(287); match(STRING);
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 4);
				{
				setState(288); match(16);
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 5);
				{
				setState(289); match(10);
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
		enterRule(_localctx, 48, RULE_variableInitializer);
		try {
			setState(294);
			switch (_input.LA(1)) {
			case 11:
				enterOuterAlt(_localctx, 1);
				{
				setState(292); array();
				}
				break;
			case 6:
			case 8:
			case NUMBER:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(293); expression(0);
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
		enterRule(_localctx, 50, RULE_variableIdentifier);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(296); match(IDENTIFIER);
			setState(301);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(297); match(25);
					setState(298); match(IDENTIFIER);
					}
					} 
				}
				setState(303);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
			}
			setState(307);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(304); match(ALIAS_ELEMENT_REF);
					}
					} 
				}
				setState(309);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			setState(312);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(310); match(25);
				setState(311); variableIdentifier();
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
		enterRule(_localctx, 52, RULE_variableIdentifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(314); variableIdentifier();
			setState(319);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(315); match(4);
					setState(316); variableIdentifier();
					}
					} 
				}
				setState(321);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
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
		case 19: return expression_sempred((ExpressionContext)_localctx, predIndex);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3.\u0145\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\5\2<\n\2\3\2\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\7\3F\n\3\f\3\16\3I\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4"+
		"R\n\4\3\4\3\4\7\4V\n\4\f\4\16\4Y\13\4\3\5\5\5\\\n\5\3\5\3\5\3\5\3\5\7"+
		"\5b\n\5\f\5\16\5e\13\5\3\6\3\6\5\6i\n\6\3\7\3\7\3\7\5\7n\n\7\3\7\3\7\3"+
		"\7\5\7s\n\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\5\t\177\n\t\3\t\3"+
		"\t\3\t\3\t\5\t\u0085\n\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\5\n\u008f\n\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13\u0099\n\13\f\13\16\13\u009c\13"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u00a6\n\f\f\f\16\f\u00a9\13\f"+
		"\3\r\5\r\u00ac\n\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\5\16\u00b6\n\16"+
		"\3\16\3\16\3\16\5\16\u00bb\n\16\3\17\3\17\3\17\3\17\5\17\u00c1\n\17\3"+
		"\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\5\21\u00cb\n\21\3\21\3\21\7\21"+
		"\u00cf\n\21\f\21\16\21\u00d2\13\21\3\22\3\22\3\22\7\22\u00d7\n\22\f\22"+
		"\16\22\u00da\13\22\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00ef\n\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\7\25\u00fa\n\25\f\25\16\25\u00fd\13"+
		"\25\3\26\3\26\5\26\u0101\n\26\3\27\3\27\3\27\3\27\7\27\u0107\n\27\f\27"+
		"\16\27\u010a\13\27\5\27\u010c\n\27\3\27\3\27\3\30\3\30\5\30\u0112\n\30"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u011b\n\31\f\31\16\31\u011e\13"+
		"\31\3\31\3\31\3\31\3\31\3\31\5\31\u0125\n\31\3\32\3\32\5\32\u0129\n\32"+
		"\3\33\3\33\3\33\7\33\u012e\n\33\f\33\16\33\u0131\13\33\3\33\7\33\u0134"+
		"\n\33\f\33\16\33\u0137\13\33\3\33\3\33\5\33\u013b\n\33\3\34\3\34\3\34"+
		"\7\34\u0140\n\34\f\34\16\34\u0143\13\34\3\34\2\35\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,.\60\62\64\66\2\5\b\2\3\4\13\13\16\17\21\21"+
		"\25\25\30\30\4\2\t\t$$\4\2\b\b\35\35\u0158\28\3\2\2\2\4A\3\2\2\2\6W\3"+
		"\2\2\2\b[\3\2\2\2\nh\3\2\2\2\fr\3\2\2\2\16t\3\2\2\2\20~\3\2\2\2\22\u008a"+
		"\3\2\2\2\24\u0095\3\2\2\2\26\u009d\3\2\2\2\30\u00ab\3\2\2\2\32\u00b0\3"+
		"\2\2\2\34\u00bc\3\2\2\2\36\u00c4\3\2\2\2 \u00ca\3\2\2\2\"\u00d3\3\2\2"+
		"\2$\u00db\3\2\2\2&\u00dd\3\2\2\2(\u00ee\3\2\2\2*\u0100\3\2\2\2,\u0102"+
		"\3\2\2\2.\u0111\3\2\2\2\60\u0124\3\2\2\2\62\u0128\3\2\2\2\64\u012a\3\2"+
		"\2\2\66\u013c\3\2\2\289\7\23\2\29;\7(\2\2:<\5\4\3\2;:\3\2\2\2;<\3\2\2"+
		"\2<=\3\2\2\2=>\7\r\2\2>?\5\6\4\2?@\7\20\2\2@\3\3\2\2\2AB\7\26\2\2BG\5"+
		"\"\22\2CD\7\6\2\2DF\5\"\22\2EC\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2H"+
		"\5\3\2\2\2IG\3\2\2\2JR\5\b\5\2KR\5\16\b\2LR\5\20\t\2MR\5\30\r\2NR\5\32"+
		"\16\2OR\5\36\20\2PR\5&\24\2QJ\3\2\2\2QK\3\2\2\2QL\3\2\2\2QM\3\2\2\2QN"+
		"\3\2\2\2QO\3\2\2\2QP\3\2\2\2RS\3\2\2\2ST\7\37\2\2TV\3\2\2\2UQ\3\2\2\2"+
		"VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2X\7\3\2\2\2YW\3\2\2\2Z\\\5\n\6\2[Z\3\2\2"+
		"\2[\\\3\2\2\2\\]\3\2\2\2]^\5 \21\2^c\5\f\7\2_`\7\6\2\2`b\5\f\7\2a_\3\2"+
		"\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2d\t\3\2\2\2ec\3\2\2\2fi\7\24\2\2gi\7"+
		"\"\2\2hf\3\2\2\2hg\3\2\2\2i\13\3\2\2\2jm\7(\2\2kl\7\36\2\2ln\5\62\32\2"+
		"mk\3\2\2\2mn\3\2\2\2ns\3\2\2\2op\7(\2\2pq\7\36\2\2qs\5\60\31\2rj\3\2\2"+
		"\2ro\3\2\2\2s\r\3\2\2\2tu\5\64\33\2uv\7\36\2\2vw\5\60\31\2w\17\3\2\2\2"+
		"x\177\5\66\34\2y\177\5\24\13\2z{\5\24\13\2{|\7\6\2\2|}\5\66\34\2}\177"+
		"\3\2\2\2~x\3\2\2\2~y\3\2\2\2~z\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\7"+
		"\27\2\2\u0081\u0084\5\66\34\2\u0082\u0083\7\6\2\2\u0083\u0085\5\26\f\2"+
		"\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087"+
		"\7\r\2\2\u0087\u0088\7(\2\2\u0088\u0089\7\20\2\2\u0089\21\3\2\2\2\u008a"+
		"\u008e\7\7\2\2\u008b\u008c\5\"\22\2\u008c\u008d\7\31\2\2\u008d\u008f\3"+
		"\2\2\2\u008e\u008b\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0090\3\2\2\2\u0090"+
		"\u0091\5\66\34\2\u0091\u0092\7\27\2\2\u0092\u0093\5\66\34\2\u0093\u0094"+
		"\7\5\2\2\u0094\23\3\2\2\2\u0095\u009a\5\22\n\2\u0096\u0097\7\6\2\2\u0097"+
		"\u0099\5\22\n\2\u0098\u0096\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3"+
		"\2\2\2\u009a\u009b\3\2\2\2\u009b\25\3\2\2\2\u009c\u009a\3\2\2\2\u009d"+
		"\u009e\7\n\2\2\u009e\u009f\5\"\22\2\u009f\u00a7\7\34\2\2\u00a0\u00a1\7"+
		"\6\2\2\u00a1\u00a2\7\n\2\2\u00a2\u00a3\5\"\22\2\u00a3\u00a4\7\34\2\2\u00a4"+
		"\u00a6\3\2\2\2\u00a5\u00a0\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2"+
		"\2\2\u00a7\u00a8\3\2\2\2\u00a8\27\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa\u00ac"+
		"\5\66\34\2\u00ab\u00aa\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2\2\2"+
		"\u00ad\u00ae\7\27\2\2\u00ae\u00af\5\66\34\2\u00af\31\3\2\2\2\u00b0\u00b5"+
		"\7!\2\2\u00b1\u00b2\7\n\2\2\u00b2\u00b3\5 \21\2\u00b3\u00b4\7\34\2\2\u00b4"+
		"\u00b6\3\2\2\2\u00b5\u00b1\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b7\3\2"+
		"\2\2\u00b7\u00ba\7(\2\2\u00b8\u00b9\7\36\2\2\u00b9\u00bb\5\34\17\2\u00ba"+
		"\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\33\3\2\2\2\u00bc\u00c0\7\n\2"+
		"\2\u00bd\u00c1\5\66\34\2\u00be\u00bf\7 \2\2\u00bf\u00c1\7(\2\2\u00c0\u00bd"+
		"\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c3\7\34\2\2"+
		"\u00c3\35\3\2\2\2\u00c4\u00c5\5\64\33\2\u00c5\u00c6\7\36\2\2\u00c6\u00c7"+
		"\5\34\17\2\u00c7\37\3\2\2\2\u00c8\u00cb\5\"\22\2\u00c9\u00cb\5$\23\2\u00ca"+
		"\u00c8\3\2\2\2\u00ca\u00c9\3\2\2\2\u00cb\u00d0\3\2\2\2\u00cc\u00cd\7\7"+
		"\2\2\u00cd\u00cf\7\5\2\2\u00ce\u00cc\3\2\2\2\u00cf\u00d2\3\2\2\2\u00d0"+
		"\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1!\3\2\2\2\u00d2\u00d0\3\2\2\2"+
		"\u00d3\u00d8\7(\2\2\u00d4\u00d5\7\33\2\2\u00d5\u00d7\7(\2\2\u00d6\u00d4"+
		"\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9"+
		"#\3\2\2\2\u00da\u00d8\3\2\2\2\u00db\u00dc\t\2\2\2\u00dc%\3\2\2\2\u00dd"+
		"\u00de\5(\25\2\u00de\u00df\7\36\2\2\u00df\u00e0\5(\25\2\u00e0\'\3\2\2"+
		"\2\u00e1\u00e2\b\25\1\2\u00e2\u00e3\7\b\2\2\u00e3\u00ef\5(\25\2\u00e4"+
		"\u00ef\5*\26\2\u00e5\u00e6\7\n\2\2\u00e6\u00e7\5(\25\2\u00e7\u00e8\7\34"+
		"\2\2\u00e8\u00ef\3\2\2\2\u00e9\u00ea\7(\2\2\u00ea\u00eb\7\n\2\2\u00eb"+
		"\u00ec\5(\25\2\u00ec\u00ed\7\34\2\2\u00ed\u00ef\3\2\2\2\u00ee\u00e1\3"+
		"\2\2\2\u00ee\u00e4\3\2\2\2\u00ee\u00e5\3\2\2\2\u00ee\u00e9\3\2\2\2\u00ef"+
		"\u00fb\3\2\2\2\u00f0\u00f1\6\25\2\3\u00f1\u00f2\t\3\2\2\u00f2\u00fa\5"+
		"(\25\2\u00f3\u00f4\6\25\3\3\u00f4\u00f5\t\4\2\2\u00f5\u00fa\5(\25\2\u00f6"+
		"\u00f7\6\25\4\3\u00f7\u00f8\7\32\2\2\u00f8\u00fa\5(\25\2\u00f9\u00f0\3"+
		"\2\2\2\u00f9\u00f3\3\2\2\2\u00f9\u00f6\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb"+
		"\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc)\3\2\2\2\u00fd\u00fb\3\2\2\2"+
		"\u00fe\u0101\7%\2\2\u00ff\u0101\5\64\33\2\u0100\u00fe\3\2\2\2\u0100\u00ff"+
		"\3\2\2\2\u0101+\3\2\2\2\u0102\u010b\7\r\2\2\u0103\u0108\5.\30\2\u0104"+
		"\u0105\7\6\2\2\u0105\u0107\5.\30\2\u0106\u0104\3\2\2\2\u0107\u010a\3\2"+
		"\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010c\3\2\2\2\u010a"+
		"\u0108\3\2\2\2\u010b\u0103\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010d\3\2"+
		"\2\2\u010d\u010e\7\20\2\2\u010e-\3\2\2\2\u010f\u0112\5\60\31\2\u0110\u0112"+
		"\5\62\32\2\u0111\u010f\3\2\2\2\u0111\u0110\3\2\2\2\u0112/\3\2\2\2\u0113"+
		"\u0125\5,\27\2\u0114\u0115\7#\2\2\u0115\u0116\5\"\22\2\u0116\u0117\7\n"+
		"\2\2\u0117\u011c\5(\25\2\u0118\u0119\7\6\2\2\u0119\u011b\5(\25\2\u011a"+
		"\u0118\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2"+
		"\2\2\u011d\u011f\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0120\7\34\2\2\u0120"+
		"\u0125\3\2\2\2\u0121\u0125\7\'\2\2\u0122\u0125\7\22\2\2\u0123\u0125\7"+
		"\f\2\2\u0124\u0113\3\2\2\2\u0124\u0114\3\2\2\2\u0124\u0121\3\2\2\2\u0124"+
		"\u0122\3\2\2\2\u0124\u0123\3\2\2\2\u0125\61\3\2\2\2\u0126\u0129\5,\27"+
		"\2\u0127\u0129\5(\25\2\u0128\u0126\3\2\2\2\u0128\u0127\3\2\2\2\u0129\63"+
		"\3\2\2\2\u012a\u012f\7(\2\2\u012b\u012c\7\33\2\2\u012c\u012e\7(\2\2\u012d"+
		"\u012b\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2"+
		"\2\2\u0130\u0135\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0134\7)\2\2\u0133"+
		"\u0132\3\2\2\2\u0134\u0137\3\2\2\2\u0135\u0133\3\2\2\2\u0135\u0136\3\2"+
		"\2\2\u0136\u013a\3\2\2\2\u0137\u0135\3\2\2\2\u0138\u0139\7\33\2\2\u0139"+
		"\u013b\5\64\33\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\65\3\2"+
		"\2\2\u013c\u0141\5\64\33\2\u013d\u013e\7\6\2\2\u013e\u0140\5\64\33\2\u013f"+
		"\u013d\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u013f\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142\67\3\2\2\2\u0143\u0141\3\2\2\2%;GQW[chmr~\u0084\u008e\u009a"+
		"\u00a7\u00ab\u00b5\u00ba\u00c0\u00ca\u00d0\u00d8\u00ee\u00f9\u00fb\u0100"+
		"\u0108\u010b\u0111\u011c\u0124\u0128\u012f\u0135\u013a\u0141";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
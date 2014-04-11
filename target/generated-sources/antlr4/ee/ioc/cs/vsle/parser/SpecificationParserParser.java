// Generated from SpecificationParser.g4 by ANTLR 4.1
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
public class SpecificationParserParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__30=1, T__29=2, T__28=3, T__27=4, T__26=5, T__25=6, T__24=7, T__23=8, 
		T__22=9, T__21=10, T__20=11, T__19=12, T__18=13, T__17=14, T__16=15, T__15=16, 
		T__14=17, T__13=18, T__12=19, T__11=20, T__10=21, T__9=22, T__8=23, T__7=24, 
		T__6=25, T__5=26, T__4=27, T__3=28, T__2=29, T__1=30, T__0=31, NUMBER=32, 
		INTEGER=33, STRING=34, IDENTIFIER=35, WS=36, COMMENT=37, BLOCK_COMMENT=38, 
		JAVA_BEFORE_SPEC=39, JAVA_AFTER_SPEC=40;
	public static final String[] tokenNames = {
		"<INVALID>", "'long'", "'short'", "']'", "','", "'['", "'-'", "'*'", "'('", 
		"'int'", "'{'", "'double'", "'boolean'", "'}'", "'float'", "'specification'", 
		"'static'", "'char'", "'super'", "'->'", "'byte'", "'|-'", "'^'", "'.'", 
		"')'", "'+'", "'='", "';'", "'*.'", "'alias'", "'const'", "'/'", "NUMBER", 
		"INTEGER", "STRING", "IDENTIFIER", "WS", "COMMENT", "BLOCK_COMMENT", "JAVA_BEFORE_SPEC", 
		"JAVA_AFTER_SPEC"
	};
	public static final int
		RULE_metaInterfase = 0, RULE_superMetaInterface = 1, RULE_specification = 2, 
		RULE_variableDeclaration = 3, RULE_variableModifier = 4, RULE_variableDeclarator = 5, 
		RULE_axiom = 6, RULE_subtask = 7, RULE_subtaskList = 8, RULE_exceptionList = 9, 
		RULE_goal = 10, RULE_aliasDeclaration = 11, RULE_aliasStructure = 12, 
		RULE_aliasDefinition = 13, RULE_type = 14, RULE_classType = 15, RULE_primitiveType = 16, 
		RULE_equation = 17, RULE_expression = 18, RULE_term = 19, RULE_array = 20, 
		RULE_variableInitializer = 21, RULE_variableIdentifier = 22, RULE_variableIdentifierList = 23;
	public static final String[] ruleNames = {
		"metaInterfase", "superMetaInterface", "specification", "variableDeclaration", 
		"variableModifier", "variableDeclarator", "axiom", "subtask", "subtaskList", 
		"exceptionList", "goal", "aliasDeclaration", "aliasStructure", "aliasDefinition", 
		"type", "classType", "primitiveType", "equation", "expression", "term", 
		"array", "variableInitializer", "variableIdentifier", "variableIdentifierList"
	};

	@Override
	public String getGrammarFileName() { return "SpecificationParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }


		

	public SpecificationParserParser(TokenStream input) {
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
		public MetaInterfaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metaInterfase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterMetaInterfase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitMetaInterfase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitMetaInterfase(this);
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
			setState(48); match(15);
			setState(49); match(IDENTIFIER);
			setState(51);
			_la = _input.LA(1);
			if (_la==18) {
				{
				setState(50); superMetaInterface();
				}
			}

			setState(53); match(10);
			setState(54); specification();
			setState(55); match(13);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterSuperMetaInterface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitSuperMetaInterface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitSuperMetaInterface(this);
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
			setState(57); match(18);
			setState(58); classType();
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(59); match(4);
				setState(60); classType();
				}
				}
				setState(65);
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
		public AliasDefinitionContext aliasDefinition(int i) {
			return getRuleContext(AliasDefinitionContext.class,i);
		}
		public AxiomContext axiom(int i) {
			return getRuleContext(AxiomContext.class,i);
		}
		public GoalContext goal(int i) {
			return getRuleContext(GoalContext.class,i);
		}
		public List<AxiomContext> axiom() {
			return getRuleContexts(AxiomContext.class);
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
		public List<VariableDeclarationContext> variableDeclaration() {
			return getRuleContexts(VariableDeclarationContext.class);
		}
		public SpecificationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specification; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterSpecification(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitSpecification(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitSpecification(this);
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
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 5) | (1L << 6) | (1L << 8) | (1L << 9) | (1L << 11) | (1L << 12) | (1L << 14) | (1L << 16) | (1L << 17) | (1L << 19) | (1L << 20) | (1L << 27) | (1L << 29) | (1L << 30) | (1L << NUMBER) | (1L << STRING) | (1L << IDENTIFIER))) != 0)) {
				{
				{
				setState(73);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(66); variableDeclaration();
					}
					break;

				case 2:
					{
					}
					break;

				case 3:
					{
					setState(68); axiom();
					}
					break;

				case 4:
					{
					setState(69); goal();
					}
					break;

				case 5:
					{
					setState(70); aliasDeclaration();
					}
					break;

				case 6:
					{
					setState(71); aliasDefinition();
					}
					break;

				case 7:
					{
					setState(72); equation();
					}
					break;
				}
				setState(75); match(27);
				}
				}
				setState(80);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVariableDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVariableDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitVariableDeclaration(this);
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
			setState(82);
			_la = _input.LA(1);
			if (_la==16 || _la==30) {
				{
				setState(81); variableModifier();
				}
			}

			setState(84); type();
			setState(85); variableDeclarator();
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(86); match(4);
				setState(87); variableDeclarator();
				}
				}
				setState(92);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterStaticVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitStaticVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitStaticVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConstantVariableContext extends VariableModifierContext {
		public ConstantVariableContext(VariableModifierContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterConstantVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitConstantVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitConstantVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableModifierContext variableModifier() throws RecognitionException {
		VariableModifierContext _localctx = new VariableModifierContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variableModifier);
		try {
			setState(95);
			switch (_input.LA(1)) {
			case 16:
				_localctx = new StaticVariableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(93); match(16);
				}
				break;
			case 30:
				_localctx = new ConstantVariableContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(94); match(30);
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
		public VariableInitializerContext variableInitializer() {
			return getRuleContext(VariableInitializerContext.class,0);
		}
		public VariableDeclaratorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclarator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVariableDeclarator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVariableDeclarator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitVariableDeclarator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclaratorContext variableDeclarator() throws RecognitionException {
		VariableDeclaratorContext _localctx = new VariableDeclaratorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_variableDeclarator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97); match(IDENTIFIER);
			setState(100);
			_la = _input.LA(1);
			if (_la==26) {
				{
				setState(98); match(26);
				setState(99); variableInitializer();
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
		public List<VariableIdentifierListContext> variableIdentifierList() {
			return getRuleContexts(VariableIdentifierListContext.class);
		}
		public AxiomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_axiom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterAxiom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitAxiom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitAxiom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AxiomContext axiom() throws RecognitionException {
		AxiomContext _localctx = new AxiomContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_axiom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(102); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				break;

			case 2:
				{
				setState(103); subtaskList();
				}
				break;

			case 3:
				{
				{
				setState(104); subtaskList();
				setState(105); match(4);
				setState(106); ((AxiomContext)_localctx).inputVariables = variableIdentifierList();
				}
				}
				break;
			}
			setState(110); match(19);
			setState(111); ((AxiomContext)_localctx).outputVariables = variableIdentifierList();
			setState(114);
			_la = _input.LA(1);
			if (_la==4) {
				{
				setState(112); match(4);
				setState(113); exceptionList();
				}
			}

			setState(116); match(10);
			setState(117); ((AxiomContext)_localctx).method = match(IDENTIFIER);
			setState(118); match(13);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterSubtask(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitSubtask(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitSubtask(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubtaskContext subtask() throws RecognitionException {
		SubtaskContext _localctx = new SubtaskContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_subtask);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120); match(5);
			setState(124);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(121); ((SubtaskContext)_localctx).context = classType();
				setState(122); match(21);
				}
				break;
			}
			setState(126); ((SubtaskContext)_localctx).inputVariables = variableIdentifierList();
			setState(127); match(19);
			setState(128); ((SubtaskContext)_localctx).outputVariables = variableIdentifierList();
			setState(129); match(3);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterSubtaskList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitSubtaskList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitSubtaskList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SubtaskListContext subtaskList() throws RecognitionException {
		SubtaskListContext _localctx = new SubtaskListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_subtaskList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(131); subtask();
			setState(136);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(132); match(4);
					setState(133); subtask();
					}
					} 
				}
				setState(138);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterExceptionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitExceptionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitExceptionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExceptionListContext exceptionList() throws RecognitionException {
		ExceptionListContext _localctx = new ExceptionListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_exceptionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139); match(8);
			setState(140); classType();
			setState(141); match(24);
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4) {
				{
				{
				setState(142); match(4);
				setState(143); match(8);
				setState(144); classType();
				setState(145); match(24);
				}
				}
				setState(151);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterGoal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitGoal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitGoal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalContext goal() throws RecognitionException {
		GoalContext _localctx = new GoalContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_goal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(152); variableIdentifierList();
				}
			}

			setState(155); match(19);
			setState(156); variableIdentifierList();
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterAliasDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitAliasDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitAliasDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasDeclarationContext aliasDeclaration() throws RecognitionException {
		AliasDeclarationContext _localctx = new AliasDeclarationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_aliasDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158); match(29);
			setState(163);
			_la = _input.LA(1);
			if (_la==8) {
				{
				setState(159); match(8);
				setState(160); type();
				setState(161); match(24);
				}
			}

			setState(165); match(IDENTIFIER);
			setState(168);
			_la = _input.LA(1);
			if (_la==26) {
				{
				setState(166); match(26);
				setState(167); aliasStructure();
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
		public VariableIdentifierListContext variableIdentifierList() {
			return getRuleContext(VariableIdentifierListContext.class,0);
		}
		public AliasStructureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasStructure; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterAliasStructure(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitAliasStructure(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitAliasStructure(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasStructureContext aliasStructure() throws RecognitionException {
		AliasStructureContext _localctx = new AliasStructureContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_aliasStructure);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170); match(8);
			setState(174);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(171); variableIdentifierList();
				}
				break;
			case 28:
				{
				setState(172); match(28);
				setState(173); match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(176); match(24);
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
		public VariableIdentifierListContext variableIdentifierList() {
			return getRuleContext(VariableIdentifierListContext.class,0);
		}
		public AliasDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterAliasDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitAliasDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitAliasDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AliasDefinitionContext aliasDefinition() throws RecognitionException {
		AliasDefinitionContext _localctx = new AliasDefinitionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_aliasDefinition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178); match(IDENTIFIER);
			setState(179); match(26);
			setState(180); match(5);
			setState(181); variableIdentifierList();
			setState(182); match(3);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(184); classType();
				}
				break;
			case 1:
			case 2:
			case 9:
			case 11:
			case 12:
			case 14:
			case 17:
			case 20:
				{
				setState(185); primitiveType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==5) {
				{
				{
				setState(188); match(5);
				setState(189); match(3);
				}
				}
				setState(194);
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
			return getToken(SpecificationParserParser.IDENTIFIER, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpecificationParserParser.IDENTIFIER); }
		public ClassTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterClassType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitClassType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitClassType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassTypeContext classType() throws RecognitionException {
		ClassTypeContext _localctx = new ClassTypeContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_classType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195); match(IDENTIFIER);
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==23) {
				{
				{
				setState(196); match(23);
				setState(197); match(IDENTIFIER);
				}
				}
				setState(202);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterPrimitiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitPrimitiveType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitPrimitiveType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimitiveTypeContext primitiveType() throws RecognitionException {
		PrimitiveTypeContext _localctx = new PrimitiveTypeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_primitiveType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 1) | (1L << 2) | (1L << 9) | (1L << 11) | (1L << 12) | (1L << 14) | (1L << 17) | (1L << 20))) != 0)) ) {
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterEquation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitEquation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitEquation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EquationContext equation() throws RecognitionException {
		EquationContext _localctx = new EquationContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(205); ((EquationContext)_localctx).left = expression(0);
			setState(206); match(26);
			setState(207); ((EquationContext)_localctx).right = expression(0);
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
		public TerminalNode IDENTIFIER() { return getToken(SpecificationParserParser.IDENTIFIER, 0); }
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState, _p);
		ExpressionContext _prevctx = _localctx;
		int _startState = 36;
		enterRecursionRule(_localctx, RULE_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(210); match(6);
				setState(211); expression(5);
				}
				break;

			case 2:
				{
				setState(212); term();
				}
				break;

			case 3:
				{
				setState(213); match(8);
				setState(214); expression(0);
				setState(215); match(24);
				}
				break;

			case 4:
				{
				setState(217); match(IDENTIFIER);
				setState(218); match(8);
				setState(219); expression(0);
				setState(220); match(24);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(235);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(233);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(224);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(225);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==7 || _la==31) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(226); ((ExpressionContext)_localctx).right = expression(4);
						}
						break;

					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						_localctx.left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(227);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(228);
						((ExpressionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==6 || _la==25) ) {
							((ExpressionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						consume();
						setState(229); ((ExpressionContext)_localctx).right = expression(3);
						}
						break;

					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState, _p);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(230);
						if (!(1 >= _localctx._p)) throw new FailedPredicateException(this, "1 >= $_p");
						setState(231); match(22);
						setState(232); expression(2);
						}
						break;
					}
					} 
				}
				setState(237);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
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
		public TerminalNode NUMBER() { return getToken(SpecificationParserParser.NUMBER, 0); }
		public TerminalNode STRING() { return getToken(SpecificationParserParser.STRING, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_term);
		try {
			setState(241);
			switch (_input.LA(1)) {
			case NUMBER:
				enterOuterAlt(_localctx, 1);
				{
				setState(238); match(NUMBER);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(239); match(STRING);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 3);
				{
				setState(240); variableIdentifier();
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
		public VariableInitializerContext variableInitializer(int i) {
			return getRuleContext(VariableInitializerContext.class,i);
		}
		public List<VariableInitializerContext> variableInitializer() {
			return getRuleContexts(VariableInitializerContext.class);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243); match(10);
			setState(252);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 8) | (1L << 10) | (1L << NUMBER) | (1L << STRING) | (1L << IDENTIFIER))) != 0)) {
				{
				setState(244); variableInitializer();
				setState(249);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==4) {
					{
					{
					setState(245); match(4);
					setState(246); variableInitializer();
					}
					}
					setState(251);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(254); match(13);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVariableInitializer(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVariableInitializer(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitVariableInitializer(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableInitializerContext variableInitializer() throws RecognitionException {
		VariableInitializerContext _localctx = new VariableInitializerContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_variableInitializer);
		try {
			setState(258);
			switch (_input.LA(1)) {
			case 10:
				enterOuterAlt(_localctx, 1);
				{
				setState(256); array();
				}
				break;
			case 6:
			case 8:
			case NUMBER:
			case STRING:
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(257); expression(0);
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
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SpecificationParserParser.IDENTIFIER, i);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(SpecificationParserParser.IDENTIFIER); }
		public VariableIdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableIdentifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVariableIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVariableIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitVariableIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableIdentifierContext variableIdentifier() throws RecognitionException {
		VariableIdentifierContext _localctx = new VariableIdentifierContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_variableIdentifier);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(260); match(IDENTIFIER);
			setState(265);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(261); match(23);
					setState(262); match(IDENTIFIER);
					}
					} 
				}
				setState(267);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
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
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVariableIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVariableIdentifierList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SpecificationParserVisitor ) return ((SpecificationParserVisitor<? extends T>)visitor).visitVariableIdentifierList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableIdentifierListContext variableIdentifierList() throws RecognitionException {
		VariableIdentifierListContext _localctx = new VariableIdentifierListContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_variableIdentifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(268); variableIdentifier();
			setState(273);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					{
					{
					setState(269); match(4);
					setState(270); variableIdentifier();
					}
					} 
				}
				setState(275);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
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
		case 18: return expression_sempred((ExpressionContext)_localctx, predIndex);
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3*\u0117\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\5\2\66\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3@\n\3\f\3\16"+
		"\3C\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4L\n\4\3\4\7\4O\n\4\f\4\16\4R\13"+
		"\4\3\5\5\5U\n\5\3\5\3\5\3\5\3\5\7\5[\n\5\f\5\16\5^\13\5\3\6\3\6\5\6b\n"+
		"\6\3\7\3\7\3\7\5\7g\n\7\3\b\3\b\3\b\3\b\3\b\3\b\5\bo\n\b\3\b\3\b\3\b\3"+
		"\b\5\bu\n\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\5\t\177\n\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\7\n\u0089\n\n\f\n\16\n\u008c\13\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\7\13\u0096\n\13\f\13\16\13\u0099\13\13\3\f\5\f"+
		"\u009c\n\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\5\r\u00a6\n\r\3\r\3\r\3\r\5"+
		"\r\u00ab\n\r\3\16\3\16\3\16\3\16\5\16\u00b1\n\16\3\16\3\16\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\20\3\20\5\20\u00bd\n\20\3\20\3\20\7\20\u00c1\n\20"+
		"\f\20\16\20\u00c4\13\20\3\21\3\21\3\21\7\21\u00c9\n\21\f\21\16\21\u00cc"+
		"\13\21\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00e1\n\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\7\24\u00ec\n\24\f\24\16\24\u00ef\13\24\3\25\3\25"+
		"\3\25\5\25\u00f4\n\25\3\26\3\26\3\26\3\26\7\26\u00fa\n\26\f\26\16\26\u00fd"+
		"\13\26\5\26\u00ff\n\26\3\26\3\26\3\27\3\27\5\27\u0105\n\27\3\30\3\30\3"+
		"\30\7\30\u010a\n\30\f\30\16\30\u010d\13\30\3\31\3\31\3\31\7\31\u0112\n"+
		"\31\f\31\16\31\u0115\13\31\3\31\2\32\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\2\5\b\2\3\4\13\13\r\16\20\20\23\23\26\26\4\2\t\t!!"+
		"\4\2\b\b\33\33\u0125\2\62\3\2\2\2\4;\3\2\2\2\6P\3\2\2\2\bT\3\2\2\2\na"+
		"\3\2\2\2\fc\3\2\2\2\16n\3\2\2\2\20z\3\2\2\2\22\u0085\3\2\2\2\24\u008d"+
		"\3\2\2\2\26\u009b\3\2\2\2\30\u00a0\3\2\2\2\32\u00ac\3\2\2\2\34\u00b4\3"+
		"\2\2\2\36\u00bc\3\2\2\2 \u00c5\3\2\2\2\"\u00cd\3\2\2\2$\u00cf\3\2\2\2"+
		"&\u00e0\3\2\2\2(\u00f3\3\2\2\2*\u00f5\3\2\2\2,\u0104\3\2\2\2.\u0106\3"+
		"\2\2\2\60\u010e\3\2\2\2\62\63\7\21\2\2\63\65\7%\2\2\64\66\5\4\3\2\65\64"+
		"\3\2\2\2\65\66\3\2\2\2\66\67\3\2\2\2\678\7\f\2\289\5\6\4\29:\7\17\2\2"+
		":\3\3\2\2\2;<\7\24\2\2<A\5 \21\2=>\7\6\2\2>@\5 \21\2?=\3\2\2\2@C\3\2\2"+
		"\2A?\3\2\2\2AB\3\2\2\2B\5\3\2\2\2CA\3\2\2\2DL\5\b\5\2EL\3\2\2\2FL\5\16"+
		"\b\2GL\5\26\f\2HL\5\30\r\2IL\5\34\17\2JL\5$\23\2KD\3\2\2\2KE\3\2\2\2K"+
		"F\3\2\2\2KG\3\2\2\2KH\3\2\2\2KI\3\2\2\2KJ\3\2\2\2LM\3\2\2\2MO\7\35\2\2"+
		"NK\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\7\3\2\2\2RP\3\2\2\2SU\5\n\6"+
		"\2TS\3\2\2\2TU\3\2\2\2UV\3\2\2\2VW\5\36\20\2W\\\5\f\7\2XY\7\6\2\2Y[\5"+
		"\f\7\2ZX\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]\t\3\2\2\2^\\\3\2\2\2"+
		"_b\7\22\2\2`b\7 \2\2a_\3\2\2\2a`\3\2\2\2b\13\3\2\2\2cf\7%\2\2de\7\34\2"+
		"\2eg\5,\27\2fd\3\2\2\2fg\3\2\2\2g\r\3\2\2\2ho\5\60\31\2io\5\22\n\2jk\5"+
		"\22\n\2kl\7\6\2\2lm\5\60\31\2mo\3\2\2\2nh\3\2\2\2ni\3\2\2\2nj\3\2\2\2"+
		"op\3\2\2\2pq\7\25\2\2qt\5\60\31\2rs\7\6\2\2su\5\24\13\2tr\3\2\2\2tu\3"+
		"\2\2\2uv\3\2\2\2vw\7\f\2\2wx\7%\2\2xy\7\17\2\2y\17\3\2\2\2z~\7\7\2\2{"+
		"|\5 \21\2|}\7\27\2\2}\177\3\2\2\2~{\3\2\2\2~\177\3\2\2\2\177\u0080\3\2"+
		"\2\2\u0080\u0081\5\60\31\2\u0081\u0082\7\25\2\2\u0082\u0083\5\60\31\2"+
		"\u0083\u0084\7\5\2\2\u0084\21\3\2\2\2\u0085\u008a\5\20\t\2\u0086\u0087"+
		"\7\6\2\2\u0087\u0089\5\20\t\2\u0088\u0086\3\2\2\2\u0089\u008c\3\2\2\2"+
		"\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\23\3\2\2\2\u008c\u008a"+
		"\3\2\2\2\u008d\u008e\7\n\2\2\u008e\u008f\5 \21\2\u008f\u0097\7\32\2\2"+
		"\u0090\u0091\7\6\2\2\u0091\u0092\7\n\2\2\u0092\u0093\5 \21\2\u0093\u0094"+
		"\7\32\2\2\u0094\u0096\3\2\2\2\u0095\u0090\3\2\2\2\u0096\u0099\3\2\2\2"+
		"\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\25\3\2\2\2\u0099\u0097"+
		"\3\2\2\2\u009a\u009c\5\60\31\2\u009b\u009a\3\2\2\2\u009b\u009c\3\2\2\2"+
		"\u009c\u009d\3\2\2\2\u009d\u009e\7\25\2\2\u009e\u009f\5\60\31\2\u009f"+
		"\27\3\2\2\2\u00a0\u00a5\7\37\2\2\u00a1\u00a2\7\n\2\2\u00a2\u00a3\5\36"+
		"\20\2\u00a3\u00a4\7\32\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a1\3\2\2\2\u00a5"+
		"\u00a6\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00aa\7%\2\2\u00a8\u00a9\7\34"+
		"\2\2\u00a9\u00ab\5\32\16\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\31\3\2\2\2\u00ac\u00b0\7\n\2\2\u00ad\u00b1\5\60\31\2\u00ae\u00af\7\36"+
		"\2\2\u00af\u00b1\7%\2\2\u00b0\u00ad\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1"+
		"\u00b2\3\2\2\2\u00b2\u00b3\7\32\2\2\u00b3\33\3\2\2\2\u00b4\u00b5\7%\2"+
		"\2\u00b5\u00b6\7\34\2\2\u00b6\u00b7\7\7\2\2\u00b7\u00b8\5\60\31\2\u00b8"+
		"\u00b9\7\5\2\2\u00b9\35\3\2\2\2\u00ba\u00bd\5 \21\2\u00bb\u00bd\5\"\22"+
		"\2\u00bc\u00ba\3\2\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c2\3\2\2\2\u00be\u00bf"+
		"\7\7\2\2\u00bf\u00c1\7\5\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2"+
		"\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\37\3\2\2\2\u00c4\u00c2\3\2\2"+
		"\2\u00c5\u00ca\7%\2\2\u00c6\u00c7\7\31\2\2\u00c7\u00c9\7%\2\2\u00c8\u00c6"+
		"\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"!\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00ce\t\2\2\2\u00ce#\3\2\2\2\u00cf"+
		"\u00d0\5&\24\2\u00d0\u00d1\7\34\2\2\u00d1\u00d2\5&\24\2\u00d2%\3\2\2\2"+
		"\u00d3\u00d4\b\24\1\2\u00d4\u00d5\7\b\2\2\u00d5\u00e1\5&\24\2\u00d6\u00e1"+
		"\5(\25\2\u00d7\u00d8\7\n\2\2\u00d8\u00d9\5&\24\2\u00d9\u00da\7\32\2\2"+
		"\u00da\u00e1\3\2\2\2\u00db\u00dc\7%\2\2\u00dc\u00dd\7\n\2\2\u00dd\u00de"+
		"\5&\24\2\u00de\u00df\7\32\2\2\u00df\u00e1\3\2\2\2\u00e0\u00d3\3\2\2\2"+
		"\u00e0\u00d6\3\2\2\2\u00e0\u00d7\3\2\2\2\u00e0\u00db\3\2\2\2\u00e1\u00ed"+
		"\3\2\2\2\u00e2\u00e3\6\24\2\3\u00e3\u00e4\t\3\2\2\u00e4\u00ec\5&\24\2"+
		"\u00e5\u00e6\6\24\3\3\u00e6\u00e7\t\4\2\2\u00e7\u00ec\5&\24\2\u00e8\u00e9"+
		"\6\24\4\3\u00e9\u00ea\7\30\2\2\u00ea\u00ec\5&\24\2\u00eb\u00e2\3\2\2\2"+
		"\u00eb\u00e5\3\2\2\2\u00eb\u00e8\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb"+
		"\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\'\3\2\2\2\u00ef\u00ed\3\2\2\2\u00f0"+
		"\u00f4\7\"\2\2\u00f1\u00f4\7$\2\2\u00f2\u00f4\5.\30\2\u00f3\u00f0\3\2"+
		"\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f2\3\2\2\2\u00f4)\3\2\2\2\u00f5\u00fe"+
		"\7\f\2\2\u00f6\u00fb\5,\27\2\u00f7\u00f8\7\6\2\2\u00f8\u00fa\5,\27\2\u00f9"+
		"\u00f7\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2"+
		"\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u00f6\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0101\7\17\2\2\u0101+\3\2\2\2"+
		"\u0102\u0105\5*\26\2\u0103\u0105\5&\24\2\u0104\u0102\3\2\2\2\u0104\u0103"+
		"\3\2\2\2\u0105-\3\2\2\2\u0106\u010b\7%\2\2\u0107\u0108\7\31\2\2\u0108"+
		"\u010a\7%\2\2\u0109\u0107\3\2\2\2\u010a\u010d\3\2\2\2\u010b\u0109\3\2"+
		"\2\2\u010b\u010c\3\2\2\2\u010c/\3\2\2\2\u010d\u010b\3\2\2\2\u010e\u0113"+
		"\5.\30\2\u010f\u0110\7\6\2\2\u0110\u0112\5.\30\2\u0111\u010f\3\2\2\2\u0112"+
		"\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114\61\3\2\2"+
		"\2\u0115\u0113\3\2\2\2\37\65AKPT\\afnt~\u008a\u0097\u009b\u00a5\u00aa"+
		"\u00b0\u00bc\u00c2\u00ca\u00e0\u00eb\u00ed\u00f3\u00fb\u00fe\u0104\u010b"+
		"\u0113";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
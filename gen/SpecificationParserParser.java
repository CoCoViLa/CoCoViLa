// Generated from D:\workspaces\Diplom\CoCoViLa\src\ee\ioc\cs\vsle\parser\SpecificationParser.g4 by ANTLR 4.x
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
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, DIGIT=14, INTEGER=15, IDENT=16, WS=17, 
		COMMENT=18;
	public static final String[] tokenNames = {
		"<INVALID>", "'{'", "')'", "','", "'+'", "'-'", "'*'", "'mod'", "'('", 
		"'/'", "'='", "'}'", "';'", "'specification'", "DIGIT", "INTEGER", "IDENT", 
		"WS", "COMMENT"
	};
	public static final int
		RULE_specification = 0, RULE_declaration = 1, RULE_varDeclaration = 2, 
		RULE_term = 3, RULE_methodCal = 4, RULE_parameters = 5, RULE_unary = 6, 
		RULE_invert = 7, RULE_mult = 8, RULE_add = 9, RULE_expression = 10, RULE_equation = 11;
	public static final String[] ruleNames = {
		"specification", "declaration", "varDeclaration", "term", "methodCal", 
		"parameters", "unary", "invert", "mult", "add", "expression", "equation"
	};

	@Override
	public String getGrammarFileName() { return "SpecificationParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


		

	public SpecificationParserParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SpecificationContext extends ParserRuleContext {
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public TerminalNode IDENT() { return getToken(SpecificationParserParser.IDENT, 0); }
		public List<EquationContext> equation() {
			return getRuleContexts(EquationContext.class);
		}
		public EquationContext equation(int i) {
			return getRuleContext(EquationContext.class,i);
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
	}

	public final SpecificationContext specification() throws RecognitionException {
		SpecificationContext _localctx = new SpecificationContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_specification);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(24); match(13);
			setState(25); match(IDENT);
			setState(26); match(1);
			setState(33);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 5) | (1L << 8) | (1L << DIGIT) | (1L << IDENT))) != 0)) {
				{
				setState(31);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(27); declaration();
					}
					break;

				case 2:
					{
					setState(28); equation();
					setState(29); match(12);
					}
					break;
				}
				}
				setState(35);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(36); match(11);
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

	public static class DeclarationContext extends ParserRuleContext {
		public Token IDENT;
		public List<VarDeclarationContext> varDeclaration() {
			return getRuleContexts(VarDeclarationContext.class);
		}
		public VarDeclarationContext varDeclaration(int i) {
			return getRuleContext(VarDeclarationContext.class,i);
		}
		public TerminalNode IDENT() { return getToken(SpecificationParserParser.IDENT, 0); }
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38); ((DeclarationContext)_localctx).IDENT = match(IDENT);
			setState(39); varDeclaration((((DeclarationContext)_localctx).IDENT!=null?((DeclarationContext)_localctx).IDENT.getText():null));
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==3) {
				{
				{
				setState(40); match(3);
				setState(41); varDeclaration((((DeclarationContext)_localctx).IDENT!=null?((DeclarationContext)_localctx).IDENT.getText():null));
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47); match(12);
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

	public static class VarDeclarationContext extends ParserRuleContext {
		public String type;
		public Token IDENT;
		public ExpressionContext e;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(SpecificationParserParser.IDENT, 0); }
		public VarDeclarationContext(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public VarDeclarationContext(ParserRuleContext parent, int invokingState, String type) {
			super(parent, invokingState);
			this.type = type;
		}
		@Override public int getRuleIndex() { return RULE_varDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterVarDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitVarDeclaration(this);
		}
	}

	public final VarDeclarationContext varDeclaration(String type) throws RecognitionException {
		VarDeclarationContext _localctx = new VarDeclarationContext(_ctx, getState(), type);
		enterRule(_localctx, 4, RULE_varDeclaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49); ((VarDeclarationContext)_localctx).IDENT = match(IDENT);
			setState(52);
			_la = _input.LA(1);
			if (_la==10) {
				{
				setState(50); match(10);
				setState(51); ((VarDeclarationContext)_localctx).e = expression();
				}
			}

			System.out.println(_localctx.type + " " + (((VarDeclarationContext)_localctx).IDENT!=null?((VarDeclarationContext)_localctx).IDENT.getText():null) + " = " + (((VarDeclarationContext)_localctx).e!=null?_input.getText(((VarDeclarationContext)_localctx).e.start,((VarDeclarationContext)_localctx).e.stop):null));
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

	public static class TermContext extends ParserRuleContext {
		public TerminalNode DIGIT() { return getToken(SpecificationParserParser.DIGIT, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public MethodCalContext methodCal() {
			return getRuleContext(MethodCalContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(SpecificationParserParser.IDENT, 0); }
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
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_term);
		try {
			setState(64);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(56); match(8);
				setState(57); expression();
				setState(58); match(2);
				}
				break;

			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(60); match(DIGIT);
				}
				break;

			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(61); match(IDENT);
				}
				break;

			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(62); unary();
				}
				break;

			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(63); methodCal();
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

	public static class MethodCalContext extends ParserRuleContext {
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(SpecificationParserParser.IDENT, 0); }
		public MethodCalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodCal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterMethodCal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitMethodCal(this);
		}
	}

	public final MethodCalContext methodCal() throws RecognitionException {
		MethodCalContext _localctx = new MethodCalContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_methodCal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66); match(IDENT);
			setState(67); match(8);
			setState(69);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 5) | (1L << 8) | (1L << DIGIT) | (1L << IDENT))) != 0)) {
				{
				setState(68); parameters();
				}
			}

			setState(71); match(2);
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

	public static class ParametersContext extends ParserRuleContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitParameters(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73); expression();
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==3) {
				{
				{
				setState(74); match(3);
				setState(75); expression();
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

	public static class UnaryContext extends ParserRuleContext {
		public InvertContext invert() {
			return getRuleContext(InvertContext.class,0);
		}
		public UnaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterUnary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitUnary(this);
		}
	}

	public final UnaryContext unary() throws RecognitionException {
		UnaryContext _localctx = new UnaryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_unary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81); invert();
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

	public static class InvertContext extends ParserRuleContext {
		public TermContext term() {
			return getRuleContext(TermContext.class,0);
		}
		public InvertContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_invert; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterInvert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitInvert(this);
		}
	}

	public final InvertContext invert() throws RecognitionException {
		InvertContext _localctx = new InvertContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_invert);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83); match(5);
			setState(84); term();
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

	public static class MultContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public MultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitMult(this);
		}
	}

	public final MultContext mult() throws RecognitionException {
		MultContext _localctx = new MultContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_mult);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86); term();
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 7) | (1L << 9))) != 0)) {
				{
				{
				setState(93);
				switch (_input.LA(1)) {
				case 6:
					{
					setState(87); match(6);
					setState(88); term();
					}
					break;
				case 9:
					{
					setState(89); match(9);
					setState(90); term();
					}
					break;
				case 7:
					{
					setState(91); match(7);
					setState(92); term();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
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

	public static class AddContext extends ParserRuleContext {
		public MultContext mult(int i) {
			return getRuleContext(MultContext.class,i);
		}
		public List<MultContext> mult() {
			return getRuleContexts(MultContext.class);
		}
		public AddContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_add; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).enterAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SpecificationParserListener ) ((SpecificationParserListener)listener).exitAdd(this);
		}
	}

	public final AddContext add() throws RecognitionException {
		AddContext _localctx = new AddContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_add);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100); mult();
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==4 || _la==5) {
				{
				{
				setState(105);
				switch (_input.LA(1)) {
				case 4:
					{
					setState(101); match(4);
					setState(102); mult();
					}
					break;
				case 5:
					{
					setState(103); match(5);
					setState(104); mult();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				}
				setState(111);
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

	public static class ExpressionContext extends ParserRuleContext {
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
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
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112); add();
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
		public ExpressionContext e1;
		public ExpressionContext e2;
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
	}

	public final EquationContext equation() throws RecognitionException {
		EquationContext _localctx = new EquationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_equation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114); ((EquationContext)_localctx).e1 = expression();
			setState(115); match(10);
			setState(116); ((EquationContext)_localctx).e2 = expression();
			System.out.println("EQ:" + (((EquationContext)_localctx).e1!=null?_input.getText(((EquationContext)_localctx).e1.start,((EquationContext)_localctx).e1.stop):null) + " = " + (((EquationContext)_localctx).e2!=null?_input.getText(((EquationContext)_localctx).e2.start,((EquationContext)_localctx).e2.stop):null));
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24z\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\3\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2\"\n\2\f\2\16\2%\13\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\7\3-\n\3\f\3\16\3\60\13\3\3\3\3\3\3\4\3\4\3\4\5"+
		"\4\67\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5C\n\5\3\6\3\6\3\6"+
		"\5\6H\n\6\3\6\3\6\3\7\3\7\3\7\7\7O\n\7\f\7\16\7R\13\7\3\b\3\b\3\t\3\t"+
		"\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n`\n\n\7\nb\n\n\f\n\16\ne\13\n\3\13"+
		"\3\13\3\13\3\13\3\13\5\13l\n\13\7\13n\n\13\f\13\16\13q\13\13\3\f\3\f\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\2\2\16\2\4\6\b\n\f\16\20\22\24\26\30\2\2|\2\32"+
		"\3\2\2\2\4(\3\2\2\2\6\63\3\2\2\2\bB\3\2\2\2\nD\3\2\2\2\fK\3\2\2\2\16S"+
		"\3\2\2\2\20U\3\2\2\2\22X\3\2\2\2\24f\3\2\2\2\26r\3\2\2\2\30t\3\2\2\2\32"+
		"\33\7\17\2\2\33\34\7\22\2\2\34#\7\3\2\2\35\"\5\4\3\2\36\37\5\30\r\2\37"+
		" \7\16\2\2 \"\3\2\2\2!\35\3\2\2\2!\36\3\2\2\2\"%\3\2\2\2#!\3\2\2\2#$\3"+
		"\2\2\2$&\3\2\2\2%#\3\2\2\2&\'\7\r\2\2\'\3\3\2\2\2()\7\22\2\2).\5\6\4\2"+
		"*+\7\5\2\2+-\5\6\4\2,*\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2"+
		"\2\2\60.\3\2\2\2\61\62\7\16\2\2\62\5\3\2\2\2\63\66\7\22\2\2\64\65\7\f"+
		"\2\2\65\67\5\26\f\2\66\64\3\2\2\2\66\67\3\2\2\2\678\3\2\2\289\b\4\1\2"+
		"9\7\3\2\2\2:;\7\n\2\2;<\5\26\f\2<=\7\4\2\2=C\3\2\2\2>C\7\20\2\2?C\7\22"+
		"\2\2@C\5\16\b\2AC\5\n\6\2B:\3\2\2\2B>\3\2\2\2B?\3\2\2\2B@\3\2\2\2BA\3"+
		"\2\2\2C\t\3\2\2\2DE\7\22\2\2EG\7\n\2\2FH\5\f\7\2GF\3\2\2\2GH\3\2\2\2H"+
		"I\3\2\2\2IJ\7\4\2\2J\13\3\2\2\2KP\5\26\f\2LM\7\5\2\2MO\5\26\f\2NL\3\2"+
		"\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\r\3\2\2\2RP\3\2\2\2ST\5\20\t\2T\17"+
		"\3\2\2\2UV\7\7\2\2VW\5\b\5\2W\21\3\2\2\2Xc\5\b\5\2YZ\7\b\2\2Z`\5\b\5\2"+
		"[\\\7\13\2\2\\`\5\b\5\2]^\7\t\2\2^`\5\b\5\2_Y\3\2\2\2_[\3\2\2\2_]\3\2"+
		"\2\2`b\3\2\2\2a_\3\2\2\2be\3\2\2\2ca\3\2\2\2cd\3\2\2\2d\23\3\2\2\2ec\3"+
		"\2\2\2fo\5\22\n\2gh\7\6\2\2hl\5\22\n\2ij\7\7\2\2jl\5\22\n\2kg\3\2\2\2"+
		"ki\3\2\2\2ln\3\2\2\2mk\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2p\25\3\2\2"+
		"\2qo\3\2\2\2rs\5\24\13\2s\27\3\2\2\2tu\5\26\f\2uv\7\f\2\2vw\5\26\f\2w"+
		"x\b\r\1\2x\31\3\2\2\2\r!#.\66BGP_cko";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
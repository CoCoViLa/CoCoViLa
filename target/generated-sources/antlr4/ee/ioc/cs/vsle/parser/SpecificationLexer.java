// Generated from SpecificationLexer.g4 by ANTLR 4.1
package ee.ioc.cs.vsle.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpecificationLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		DIGIT=1, INTEGER=2, IDENT=3, WS=4, COMMENT=5;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"DIGIT", "INTEGER", "IDENT", "WS", "COMMENT"
	};
	public static final String[] ruleNames = {
		"DIGIT", "INTEGER", "IDENT", "LETTER", "WS", "COMMENT"
	};


	public SpecificationLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SpecificationLexer.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 4: WS_action((RuleContext)_localctx, actionIndex); break;

		case 5: COMMENT_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}
	private void COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\7\67\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\5\2\23\n\2\3\3\6"+
		"\3\26\n\3\r\3\16\3\27\3\4\3\4\3\4\7\4\35\n\4\f\4\16\4 \13\4\3\5\3\5\3"+
		"\6\6\6%\n\6\r\6\16\6&\3\6\3\6\3\7\3\7\3\7\3\7\7\7/\n\7\f\7\16\7\62\13"+
		"\7\3\7\3\7\3\7\3\7\3\60\b\3\3\1\5\4\1\7\5\1\t\2\1\13\6\2\r\7\3\3\2\5\4"+
		"\2C\\c|\5\2\13\f\17\17\"\"\4\2\f\f\17\17;\2\3\3\2\2\2\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\3\17\3\2\2\2\5\25\3\2\2\2\7\31\3\2\2"+
		"\2\t!\3\2\2\2\13$\3\2\2\2\r*\3\2\2\2\17\22\5\5\3\2\20\21\7\60\2\2\21\23"+
		"\5\5\3\2\22\20\3\2\2\2\22\23\3\2\2\2\23\4\3\2\2\2\24\26\4\62;\2\25\24"+
		"\3\2\2\2\26\27\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\6\3\2\2\2\31\36"+
		"\5\t\5\2\32\35\5\t\5\2\33\35\4\62;\2\34\32\3\2\2\2\34\33\3\2\2\2\35 \3"+
		"\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2\37\b\3\2\2\2 \36\3\2\2\2!\"\t\2\2\2"+
		"\"\n\3\2\2\2#%\t\3\2\2$#\3\2\2\2%&\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'(\3\2"+
		"\2\2()\b\6\2\2)\f\3\2\2\2*+\7\61\2\2+,\7\61\2\2,\60\3\2\2\2-/\13\2\2\2"+
		".-\3\2\2\2/\62\3\2\2\2\60\61\3\2\2\2\60.\3\2\2\2\61\63\3\2\2\2\62\60\3"+
		"\2\2\2\63\64\t\4\2\2\64\65\3\2\2\2\65\66\b\7\3\2\66\16\3\2\2\2\t\2\22"+
		"\27\34\36&\60";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
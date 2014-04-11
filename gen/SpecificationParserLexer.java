// Generated from D:\workspaces\Diplom\CoCoViLa\src\ee\ioc\cs\vsle\parser\SpecificationParser.g4 by ANTLR 4.x
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpecificationParserLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__12=1, T__11=2, T__10=3, T__9=4, T__8=5, T__7=6, T__6=7, T__5=8, T__4=9, 
		T__3=10, T__2=11, T__1=12, T__0=13, DIGIT=14, INTEGER=15, IDENT=16, WS=17, 
		COMMENT=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'{'", "')'", "','", "'+'", "'-'", "'*'", "'mod'", "'('", "'/'", "'='", 
		"'}'", "';'", "'specification'", "DIGIT", "INTEGER", "IDENT", "WS", "COMMENT"
	};
	public static final String[] ruleNames = {
		"T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", "T__5", "T__4", 
		"T__3", "T__2", "T__1", "T__0", "DIGIT", "INTEGER", "IDENT", "LETTER", 
		"WS", "COMMENT"
	};


		


	public SpecificationParserLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SpecificationParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24y\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\5\17U\n\17\3\20\6\20X\n\20\r\20\16\20Y\3\21\3\21\3\21\7\21_\n\21\f\21"+
		"\16\21b\13\21\3\22\3\22\3\23\6\23g\n\23\r\23\16\23h\3\23\3\23\3\24\3\24"+
		"\3\24\3\24\7\24q\n\24\f\24\16\24t\13\24\3\24\3\24\3\24\3\24\3r\2\25\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\2%\23\'\24\3\2\5\4\2C\\c|\5\2\13\f\17\17\"\"\4\2\f\f\17\17}\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\3)\3\2\2\2\5+\3\2\2\2\7-\3\2\2\2\t/\3\2\2\2\13\61\3\2\2"+
		"\2\r\63\3\2\2\2\17\65\3\2\2\2\219\3\2\2\2\23;\3\2\2\2\25=\3\2\2\2\27?"+
		"\3\2\2\2\31A\3\2\2\2\33C\3\2\2\2\35Q\3\2\2\2\37W\3\2\2\2![\3\2\2\2#c\3"+
		"\2\2\2%f\3\2\2\2\'l\3\2\2\2)*\7}\2\2*\4\3\2\2\2+,\7+\2\2,\6\3\2\2\2-."+
		"\7.\2\2.\b\3\2\2\2/\60\7-\2\2\60\n\3\2\2\2\61\62\7/\2\2\62\f\3\2\2\2\63"+
		"\64\7,\2\2\64\16\3\2\2\2\65\66\7o\2\2\66\67\7q\2\2\678\7f\2\28\20\3\2"+
		"\2\29:\7*\2\2:\22\3\2\2\2;<\7\61\2\2<\24\3\2\2\2=>\7?\2\2>\26\3\2\2\2"+
		"?@\7\177\2\2@\30\3\2\2\2AB\7=\2\2B\32\3\2\2\2CD\7u\2\2DE\7r\2\2EF\7g\2"+
		"\2FG\7e\2\2GH\7k\2\2HI\7h\2\2IJ\7k\2\2JK\7e\2\2KL\7c\2\2LM\7v\2\2MN\7"+
		"k\2\2NO\7q\2\2OP\7p\2\2P\34\3\2\2\2QT\5\37\20\2RS\7\60\2\2SU\5\37\20\2"+
		"TR\3\2\2\2TU\3\2\2\2U\36\3\2\2\2VX\4\62;\2WV\3\2\2\2XY\3\2\2\2YW\3\2\2"+
		"\2YZ\3\2\2\2Z \3\2\2\2[`\5#\22\2\\_\5#\22\2]_\4\62;\2^\\\3\2\2\2^]\3\2"+
		"\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2a\"\3\2\2\2b`\3\2\2\2cd\t\2\2\2d$\3"+
		"\2\2\2eg\t\3\2\2fe\3\2\2\2gh\3\2\2\2hf\3\2\2\2hi\3\2\2\2ij\3\2\2\2jk\b"+
		"\23\2\2k&\3\2\2\2lm\7\61\2\2mn\7\61\2\2nr\3\2\2\2oq\13\2\2\2po\3\2\2\2"+
		"qt\3\2\2\2rs\3\2\2\2rp\3\2\2\2su\3\2\2\2tr\3\2\2\2uv\t\4\2\2vw\3\2\2\2"+
		"wx\b\24\2\2x(\3\2\2\2\t\2TY^`hr\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
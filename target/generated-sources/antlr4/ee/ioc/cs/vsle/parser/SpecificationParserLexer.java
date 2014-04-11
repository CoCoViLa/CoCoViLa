// Generated from SpecificationParser.g4 by ANTLR 4.1
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
public class SpecificationParserLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'long'", "'short'", "']'", "','", "'['", "'-'", "'*'", "'('", "'int'", 
		"'{'", "'double'", "'boolean'", "'}'", "'float'", "'specification'", "'static'", 
		"'char'", "'super'", "'->'", "'byte'", "'|-'", "'^'", "'.'", "')'", "'+'", 
		"'='", "';'", "'*.'", "'alias'", "'const'", "'/'", "NUMBER", "INTEGER", 
		"STRING", "IDENTIFIER", "WS", "COMMENT", "BLOCK_COMMENT", "JAVA_BEFORE_SPEC", 
		"JAVA_AFTER_SPEC"
	};
	public static final String[] ruleNames = {
		"T__30", "T__29", "T__28", "T__27", "T__26", "T__25", "T__24", "T__23", 
		"T__22", "T__21", "T__20", "T__19", "T__18", "T__17", "T__16", "T__15", 
		"T__14", "T__13", "T__12", "T__11", "T__10", "T__9", "T__8", "T__7", "T__6", 
		"T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "NUMBER", "INTEGER", "STRING", 
		"ESC", "IDENTIFIER", "LETTER", "LETTER_OR_DNUMBER", "WS", "COMMENT", "BLOCK_COMMENT", 
		"JAVA_BEFORE_SPEC", "JAVA_AFTER_SPEC"
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
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 38: WS_action((RuleContext)_localctx, actionIndex); break;

		case 39: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 40: BLOCK_COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 41: JAVA_BEFORE_SPEC_action((RuleContext)_localctx, actionIndex); break;

		case 42: JAVA_AFTER_SPEC_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void BLOCK_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2: skip();  break;
		}
	}
	private void JAVA_BEFORE_SPEC_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3: skip();  break;
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
	private void JAVA_AFTER_SPEC_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2*\u0137\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3"+
		"\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3"+
		"\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3"+
		"\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3"+
		"\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3"+
		"\37\3\37\3\37\3 \3 \3!\3!\3!\5!\u00d9\n!\3\"\6\"\u00dc\n\"\r\"\16\"\u00dd"+
		"\3#\3#\3#\7#\u00e3\n#\f#\16#\u00e6\13#\3#\3#\3$\3$\3$\3$\5$\u00ee\n$\3"+
		"%\3%\7%\u00f2\n%\f%\16%\u00f5\13%\3&\3&\3\'\3\'\3(\6(\u00fc\n(\r(\16("+
		"\u00fd\3(\3(\3)\3)\3)\3)\7)\u0106\n)\f)\16)\u0109\13)\3)\5)\u010c\n)\3"+
		")\3)\3)\3)\3*\3*\3*\3*\7*\u0116\n*\f*\16*\u0119\13*\3*\3*\3*\3*\3*\3+"+
		"\7+\u0121\n+\f+\16+\u0124\13+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\7,\u0131"+
		"\n,\f,\16,\u0134\13,\3,\3,\7\u00e4\u0107\u0117\u0122\u0132-\3\3\1\5\4"+
		"\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16"+
		"\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1"+
		"/\31\1\61\32\1\63\33\1\65\34\1\67\35\19\36\1;\37\1= \1?!\1A\"\1C#\1E$"+
		"\1G\2\1I%\1K\2\1M\2\1O&\2Q\'\3S(\4U)\5W*\6\3\2\5\6\2&&C\\aac|\7\2&&\62"+
		";C\\aac|\5\2\13\f\17\17\"\"\u013f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2"+
		"\2\2C\3\2\2\2\2E\3\2\2\2\2I\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2"+
		"U\3\2\2\2\2W\3\2\2\2\3Y\3\2\2\2\5^\3\2\2\2\7d\3\2\2\2\tf\3\2\2\2\13h\3"+
		"\2\2\2\rj\3\2\2\2\17l\3\2\2\2\21n\3\2\2\2\23p\3\2\2\2\25t\3\2\2\2\27v"+
		"\3\2\2\2\31}\3\2\2\2\33\u0085\3\2\2\2\35\u0087\3\2\2\2\37\u008d\3\2\2"+
		"\2!\u009b\3\2\2\2#\u00a2\3\2\2\2%\u00a7\3\2\2\2\'\u00ad\3\2\2\2)\u00b0"+
		"\3\2\2\2+\u00b5\3\2\2\2-\u00b8\3\2\2\2/\u00ba\3\2\2\2\61\u00bc\3\2\2\2"+
		"\63\u00be\3\2\2\2\65\u00c0\3\2\2\2\67\u00c2\3\2\2\29\u00c4\3\2\2\2;\u00c7"+
		"\3\2\2\2=\u00cd\3\2\2\2?\u00d3\3\2\2\2A\u00d5\3\2\2\2C\u00db\3\2\2\2E"+
		"\u00df\3\2\2\2G\u00ed\3\2\2\2I\u00ef\3\2\2\2K\u00f6\3\2\2\2M\u00f8\3\2"+
		"\2\2O\u00fb\3\2\2\2Q\u0101\3\2\2\2S\u0111\3\2\2\2U\u0122\3\2\2\2W\u012b"+
		"\3\2\2\2YZ\7n\2\2Z[\7q\2\2[\\\7p\2\2\\]\7i\2\2]\4\3\2\2\2^_\7u\2\2_`\7"+
		"j\2\2`a\7q\2\2ab\7t\2\2bc\7v\2\2c\6\3\2\2\2de\7_\2\2e\b\3\2\2\2fg\7.\2"+
		"\2g\n\3\2\2\2hi\7]\2\2i\f\3\2\2\2jk\7/\2\2k\16\3\2\2\2lm\7,\2\2m\20\3"+
		"\2\2\2no\7*\2\2o\22\3\2\2\2pq\7k\2\2qr\7p\2\2rs\7v\2\2s\24\3\2\2\2tu\7"+
		"}\2\2u\26\3\2\2\2vw\7f\2\2wx\7q\2\2xy\7w\2\2yz\7d\2\2z{\7n\2\2{|\7g\2"+
		"\2|\30\3\2\2\2}~\7d\2\2~\177\7q\2\2\177\u0080\7q\2\2\u0080\u0081\7n\2"+
		"\2\u0081\u0082\7g\2\2\u0082\u0083\7c\2\2\u0083\u0084\7p\2\2\u0084\32\3"+
		"\2\2\2\u0085\u0086\7\177\2\2\u0086\34\3\2\2\2\u0087\u0088\7h\2\2\u0088"+
		"\u0089\7n\2\2\u0089\u008a\7q\2\2\u008a\u008b\7c\2\2\u008b\u008c\7v\2\2"+
		"\u008c\36\3\2\2\2\u008d\u008e\7u\2\2\u008e\u008f\7r\2\2\u008f\u0090\7"+
		"g\2\2\u0090\u0091\7e\2\2\u0091\u0092\7k\2\2\u0092\u0093\7h\2\2\u0093\u0094"+
		"\7k\2\2\u0094\u0095\7e\2\2\u0095\u0096\7c\2\2\u0096\u0097\7v\2\2\u0097"+
		"\u0098\7k\2\2\u0098\u0099\7q\2\2\u0099\u009a\7p\2\2\u009a \3\2\2\2\u009b"+
		"\u009c\7u\2\2\u009c\u009d\7v\2\2\u009d\u009e\7c\2\2\u009e\u009f\7v\2\2"+
		"\u009f\u00a0\7k\2\2\u00a0\u00a1\7e\2\2\u00a1\"\3\2\2\2\u00a2\u00a3\7e"+
		"\2\2\u00a3\u00a4\7j\2\2\u00a4\u00a5\7c\2\2\u00a5\u00a6\7t\2\2\u00a6$\3"+
		"\2\2\2\u00a7\u00a8\7u\2\2\u00a8\u00a9\7w\2\2\u00a9\u00aa\7r\2\2\u00aa"+
		"\u00ab\7g\2\2\u00ab\u00ac\7t\2\2\u00ac&\3\2\2\2\u00ad\u00ae\7/\2\2\u00ae"+
		"\u00af\7@\2\2\u00af(\3\2\2\2\u00b0\u00b1\7d\2\2\u00b1\u00b2\7{\2\2\u00b2"+
		"\u00b3\7v\2\2\u00b3\u00b4\7g\2\2\u00b4*\3\2\2\2\u00b5\u00b6\7~\2\2\u00b6"+
		"\u00b7\7/\2\2\u00b7,\3\2\2\2\u00b8\u00b9\7`\2\2\u00b9.\3\2\2\2\u00ba\u00bb"+
		"\7\60\2\2\u00bb\60\3\2\2\2\u00bc\u00bd\7+\2\2\u00bd\62\3\2\2\2\u00be\u00bf"+
		"\7-\2\2\u00bf\64\3\2\2\2\u00c0\u00c1\7?\2\2\u00c1\66\3\2\2\2\u00c2\u00c3"+
		"\7=\2\2\u00c38\3\2\2\2\u00c4\u00c5\7,\2\2\u00c5\u00c6\7\60\2\2\u00c6:"+
		"\3\2\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7n\2\2\u00c9\u00ca\7k\2\2\u00ca"+
		"\u00cb\7c\2\2\u00cb\u00cc\7u\2\2\u00cc<\3\2\2\2\u00cd\u00ce\7e\2\2\u00ce"+
		"\u00cf\7q\2\2\u00cf\u00d0\7p\2\2\u00d0\u00d1\7u\2\2\u00d1\u00d2\7v\2\2"+
		"\u00d2>\3\2\2\2\u00d3\u00d4\7\61\2\2\u00d4@\3\2\2\2\u00d5\u00d8\5C\"\2"+
		"\u00d6\u00d7\7\60\2\2\u00d7\u00d9\5C\"\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9"+
		"\3\2\2\2\u00d9B\3\2\2\2\u00da\u00dc\4\62;\2\u00db\u00da\3\2\2\2\u00dc"+
		"\u00dd\3\2\2\2\u00dd\u00db\3\2\2\2\u00dd\u00de\3\2\2\2\u00deD\3\2\2\2"+
		"\u00df\u00e4\7$\2\2\u00e0\u00e3\5G$\2\u00e1\u00e3\13\2\2\2\u00e2\u00e0"+
		"\3\2\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e4"+
		"\u00e2\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00e8\7$"+
		"\2\2\u00e8F\3\2\2\2\u00e9\u00ea\7^\2\2\u00ea\u00ee\7$\2\2\u00eb\u00ec"+
		"\7^\2\2\u00ec\u00ee\7^\2\2\u00ed\u00e9\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee"+
		"H\3\2\2\2\u00ef\u00f3\5K&\2\u00f0\u00f2\5M\'\2\u00f1\u00f0\3\2\2\2\u00f2"+
		"\u00f5\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4J\3\2\2\2"+
		"\u00f5\u00f3\3\2\2\2\u00f6\u00f7\t\2\2\2\u00f7L\3\2\2\2\u00f8\u00f9\t"+
		"\3\2\2\u00f9N\3\2\2\2\u00fa\u00fc\t\4\2\2\u00fb\u00fa\3\2\2\2\u00fc\u00fd"+
		"\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff"+
		"\u0100\b(\2\2\u0100P\3\2\2\2\u0101\u0102\7\61\2\2\u0102\u0103\7\61\2\2"+
		"\u0103\u0107\3\2\2\2\u0104\u0106\13\2\2\2\u0105\u0104\3\2\2\2\u0106\u0109"+
		"\3\2\2\2\u0107\u0108\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u010b\3\2\2\2\u0109"+
		"\u0107\3\2\2\2\u010a\u010c\7\17\2\2\u010b\u010a\3\2\2\2\u010b\u010c\3"+
		"\2\2\2\u010c\u010d\3\2\2\2\u010d\u010e\7\f\2\2\u010e\u010f\3\2\2\2\u010f"+
		"\u0110\b)\3\2\u0110R\3\2\2\2\u0111\u0112\7\61\2\2\u0112\u0113\7,\2\2\u0113"+
		"\u0117\3\2\2\2\u0114\u0116\13\2\2\2\u0115\u0114\3\2\2\2\u0116\u0119\3"+
		"\2\2\2\u0117\u0118\3\2\2\2\u0117\u0115\3\2\2\2\u0118\u011a\3\2\2\2\u0119"+
		"\u0117\3\2\2\2\u011a\u011b\7,\2\2\u011b\u011c\7\61\2\2\u011c\u011d\3\2"+
		"\2\2\u011d\u011e\b*\4\2\u011eT\3\2\2\2\u011f\u0121\13\2\2\2\u0120\u011f"+
		"\3\2\2\2\u0121\u0124\3\2\2\2\u0122\u0123\3\2\2\2\u0122\u0120\3\2\2\2\u0123"+
		"\u0125\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u0126\7\61\2\2\u0126\u0127\7"+
		",\2\2\u0127\u0128\7B\2\2\u0128\u0129\3\2\2\2\u0129\u012a\b+\5\2\u012a"+
		"V\3\2\2\2\u012b\u012c\7B\2\2\u012c\u012d\7,\2\2\u012d\u012e\7\61\2\2\u012e"+
		"\u0132\3\2\2\2\u012f\u0131\13\2\2\2\u0130\u012f\3\2\2\2\u0131\u0134\3"+
		"\2\2\2\u0132\u0133\3\2\2\2\u0132\u0130\3\2\2\2\u0133\u0135\3\2\2\2\u0134"+
		"\u0132\3\2\2\2\u0135\u0136\b,\6\2\u0136X\3\2\2\2\17\2\u00d8\u00dd\u00e2"+
		"\u00e4\u00ed\u00f3\u00fd\u0107\u010b\u0117\u0122\u0132";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
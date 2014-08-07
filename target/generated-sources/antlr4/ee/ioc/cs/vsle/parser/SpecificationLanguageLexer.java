// Generated from SpecificationLanguage.g4 by ANTLR 4.1
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
public class SpecificationLanguageLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'long'", "'short'", "']'", "','", "'['", "'-'", "'*'", "'('", "'int'", 
		"'false'", "'{'", "'double'", "'boolean'", "'}'", "'float'", "'true'", 
		"'specification'", "'static'", "'char'", "'super'", "'->'", "'byte'", 
		"'|-'", "'^'", "'.'", "')'", "'+'", "'='", "';'", "'*.'", "'alias'", "'const'", 
		"'new'", "'/'", "NUMBER", "INTEGER", "STRING", "IDENTIFIER", "ALIAS_ELEMENT_REF", 
		"WS", "COMMENT", "BLOCK_COMMENT", "JAVA_BEFORE_SPEC", "JAVA_AFTER_SPEC"
	};
	public static final String[] ruleNames = {
		"T__33", "T__32", "T__31", "T__30", "T__29", "T__28", "T__27", "T__26", 
		"T__25", "T__24", "T__23", "T__22", "T__21", "T__20", "T__19", "T__18", 
		"T__17", "T__16", "T__15", "T__14", "T__13", "T__12", "T__11", "T__10", 
		"T__9", "T__8", "T__7", "T__6", "T__5", "T__4", "T__3", "T__2", "T__1", 
		"T__0", "NUMBER", "INTEGER", "STRING", "ESC", "IDENTIFIER", "LETTER", 
		"LETTER_OR_DNUMBER", "ALIAS_ELEMENT_REF", "WS", "COMMENT", "BLOCK_COMMENT", 
		"JAVA_BEFORE_SPEC", "JAVA_AFTER_SPEC"
	};


		


	public SpecificationLanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SpecificationLanguage.g4"; }

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
		case 42: WS_action((RuleContext)_localctx, actionIndex); break;

		case 43: COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 44: BLOCK_COMMENT_action((RuleContext)_localctx, actionIndex); break;

		case 45: JAVA_BEFORE_SPEC_action((RuleContext)_localctx, actionIndex); break;

		case 46: JAVA_AFTER_SPEC_action((RuleContext)_localctx, actionIndex); break;
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2.\u015f\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34"+
		"\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3!\3!\3!\3"+
		"!\3!\3!\3\"\3\"\3\"\3\"\3#\3#\3$\3$\3$\5$\u00f0\n$\3$\3$\5$\u00f4\n$\3"+
		"$\6$\u00f7\n$\r$\16$\u00f8\5$\u00fb\n$\3%\6%\u00fe\n%\r%\16%\u00ff\3&"+
		"\3&\3&\7&\u0105\n&\f&\16&\u0108\13&\3&\3&\3\'\3\'\3\'\3\'\5\'\u0110\n"+
		"\'\3(\3(\7(\u0114\n(\f(\16(\u0117\13(\3)\3)\3*\3*\3+\3+\6+\u011f\n+\r"+
		"+\16+\u0120\3,\6,\u0124\n,\r,\16,\u0125\3,\3,\3-\3-\3-\3-\7-\u012e\n-"+
		"\f-\16-\u0131\13-\3-\5-\u0134\n-\3-\3-\3-\3-\3.\3.\3.\3.\7.\u013e\n.\f"+
		".\16.\u0141\13.\3.\3.\3.\3.\3.\3/\7/\u0149\n/\f/\16/\u014c\13/\3/\3/\3"+
		"/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60\7\60\u0159\n\60\f\60\16\60\u015c\13"+
		"\60\3\60\3\60\7\u0106\u012f\u013f\u014a\u015a\61\3\3\1\5\4\1\7\5\1\t\6"+
		"\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35"+
		"\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1"+
		"\63\33\1\65\34\1\67\35\19\36\1;\37\1= \1?!\1A\"\1C#\1E$\1G%\1I&\1K\'\1"+
		"M\2\1O(\1Q\2\1S\2\1U)\1W*\2Y+\3[,\4]-\5_.\6\3\2\7\4\2GGgg\4\2--//\6\2"+
		"&&C\\aac|\7\2&&\62;C\\aac|\5\2\13\f\17\17\"\"\u016b\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'"+
		"\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63"+
		"\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2"+
		"?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3"+
		"\2\2\2\2O\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2"+
		"\2\2_\3\2\2\2\3a\3\2\2\2\5f\3\2\2\2\7l\3\2\2\2\tn\3\2\2\2\13p\3\2\2\2"+
		"\rr\3\2\2\2\17t\3\2\2\2\21v\3\2\2\2\23x\3\2\2\2\25|\3\2\2\2\27\u0082\3"+
		"\2\2\2\31\u0084\3\2\2\2\33\u008b\3\2\2\2\35\u0093\3\2\2\2\37\u0095\3\2"+
		"\2\2!\u009b\3\2\2\2#\u00a0\3\2\2\2%\u00ae\3\2\2\2\'\u00b5\3\2\2\2)\u00ba"+
		"\3\2\2\2+\u00c0\3\2\2\2-\u00c3\3\2\2\2/\u00c8\3\2\2\2\61\u00cb\3\2\2\2"+
		"\63\u00cd\3\2\2\2\65\u00cf\3\2\2\2\67\u00d1\3\2\2\29\u00d3\3\2\2\2;\u00d5"+
		"\3\2\2\2=\u00d7\3\2\2\2?\u00da\3\2\2\2A\u00e0\3\2\2\2C\u00e6\3\2\2\2E"+
		"\u00ea\3\2\2\2G\u00ec\3\2\2\2I\u00fd\3\2\2\2K\u0101\3\2\2\2M\u010f\3\2"+
		"\2\2O\u0111\3\2\2\2Q\u0118\3\2\2\2S\u011a\3\2\2\2U\u011c\3\2\2\2W\u0123"+
		"\3\2\2\2Y\u0129\3\2\2\2[\u0139\3\2\2\2]\u014a\3\2\2\2_\u0153\3\2\2\2a"+
		"b\7n\2\2bc\7q\2\2cd\7p\2\2de\7i\2\2e\4\3\2\2\2fg\7u\2\2gh\7j\2\2hi\7q"+
		"\2\2ij\7t\2\2jk\7v\2\2k\6\3\2\2\2lm\7_\2\2m\b\3\2\2\2no\7.\2\2o\n\3\2"+
		"\2\2pq\7]\2\2q\f\3\2\2\2rs\7/\2\2s\16\3\2\2\2tu\7,\2\2u\20\3\2\2\2vw\7"+
		"*\2\2w\22\3\2\2\2xy\7k\2\2yz\7p\2\2z{\7v\2\2{\24\3\2\2\2|}\7h\2\2}~\7"+
		"c\2\2~\177\7n\2\2\177\u0080\7u\2\2\u0080\u0081\7g\2\2\u0081\26\3\2\2\2"+
		"\u0082\u0083\7}\2\2\u0083\30\3\2\2\2\u0084\u0085\7f\2\2\u0085\u0086\7"+
		"q\2\2\u0086\u0087\7w\2\2\u0087\u0088\7d\2\2\u0088\u0089\7n\2\2\u0089\u008a"+
		"\7g\2\2\u008a\32\3\2\2\2\u008b\u008c\7d\2\2\u008c\u008d\7q\2\2\u008d\u008e"+
		"\7q\2\2\u008e\u008f\7n\2\2\u008f\u0090\7g\2\2\u0090\u0091\7c\2\2\u0091"+
		"\u0092\7p\2\2\u0092\34\3\2\2\2\u0093\u0094\7\177\2\2\u0094\36\3\2\2\2"+
		"\u0095\u0096\7h\2\2\u0096\u0097\7n\2\2\u0097\u0098\7q\2\2\u0098\u0099"+
		"\7c\2\2\u0099\u009a\7v\2\2\u009a \3\2\2\2\u009b\u009c\7v\2\2\u009c\u009d"+
		"\7t\2\2\u009d\u009e\7w\2\2\u009e\u009f\7g\2\2\u009f\"\3\2\2\2\u00a0\u00a1"+
		"\7u\2\2\u00a1\u00a2\7r\2\2\u00a2\u00a3\7g\2\2\u00a3\u00a4\7e\2\2\u00a4"+
		"\u00a5\7k\2\2\u00a5\u00a6\7h\2\2\u00a6\u00a7\7k\2\2\u00a7\u00a8\7e\2\2"+
		"\u00a8\u00a9\7c\2\2\u00a9\u00aa\7v\2\2\u00aa\u00ab\7k\2\2\u00ab\u00ac"+
		"\7q\2\2\u00ac\u00ad\7p\2\2\u00ad$\3\2\2\2\u00ae\u00af\7u\2\2\u00af\u00b0"+
		"\7v\2\2\u00b0\u00b1\7c\2\2\u00b1\u00b2\7v\2\2\u00b2\u00b3\7k\2\2\u00b3"+
		"\u00b4\7e\2\2\u00b4&\3\2\2\2\u00b5\u00b6\7e\2\2\u00b6\u00b7\7j\2\2\u00b7"+
		"\u00b8\7c\2\2\u00b8\u00b9\7t\2\2\u00b9(\3\2\2\2\u00ba\u00bb\7u\2\2\u00bb"+
		"\u00bc\7w\2\2\u00bc\u00bd\7r\2\2\u00bd\u00be\7g\2\2\u00be\u00bf\7t\2\2"+
		"\u00bf*\3\2\2\2\u00c0\u00c1\7/\2\2\u00c1\u00c2\7@\2\2\u00c2,\3\2\2\2\u00c3"+
		"\u00c4\7d\2\2\u00c4\u00c5\7{\2\2\u00c5\u00c6\7v\2\2\u00c6\u00c7\7g\2\2"+
		"\u00c7.\3\2\2\2\u00c8\u00c9\7~\2\2\u00c9\u00ca\7/\2\2\u00ca\60\3\2\2\2"+
		"\u00cb\u00cc\7`\2\2\u00cc\62\3\2\2\2\u00cd\u00ce\7\60\2\2\u00ce\64\3\2"+
		"\2\2\u00cf\u00d0\7+\2\2\u00d0\66\3\2\2\2\u00d1\u00d2\7-\2\2\u00d28\3\2"+
		"\2\2\u00d3\u00d4\7?\2\2\u00d4:\3\2\2\2\u00d5\u00d6\7=\2\2\u00d6<\3\2\2"+
		"\2\u00d7\u00d8\7,\2\2\u00d8\u00d9\7\60\2\2\u00d9>\3\2\2\2\u00da\u00db"+
		"\7c\2\2\u00db\u00dc\7n\2\2\u00dc\u00dd\7k\2\2\u00dd\u00de\7c\2\2\u00de"+
		"\u00df\7u\2\2\u00df@\3\2\2\2\u00e0\u00e1\7e\2\2\u00e1\u00e2\7q\2\2\u00e2"+
		"\u00e3\7p\2\2\u00e3\u00e4\7u\2\2\u00e4\u00e5\7v\2\2\u00e5B\3\2\2\2\u00e6"+
		"\u00e7\7p\2\2\u00e7\u00e8\7g\2\2\u00e8\u00e9\7y\2\2\u00e9D\3\2\2\2\u00ea"+
		"\u00eb\7\61\2\2\u00ebF\3\2\2\2\u00ec\u00ef\5I%\2\u00ed\u00ee\7\60\2\2"+
		"\u00ee\u00f0\5I%\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00fa"+
		"\3\2\2\2\u00f1\u00f3\t\2\2\2\u00f2\u00f4\t\3\2\2\u00f3\u00f2\3\2\2\2\u00f3"+
		"\u00f4\3\2\2\2\u00f4\u00f6\3\2\2\2\u00f5\u00f7\5I%\2\u00f6\u00f5\3\2\2"+
		"\2\u00f7\u00f8\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb"+
		"\3\2\2\2\u00fa\u00f1\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fbH\3\2\2\2\u00fc"+
		"\u00fe\4\62;\2\u00fd\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u00fd\3\2"+
		"\2\2\u00ff\u0100\3\2\2\2\u0100J\3\2\2\2\u0101\u0106\7$\2\2\u0102\u0105"+
		"\5M\'\2\u0103\u0105\13\2\2\2\u0104\u0102\3\2\2\2\u0104\u0103\3\2\2\2\u0105"+
		"\u0108\3\2\2\2\u0106\u0107\3\2\2\2\u0106\u0104\3\2\2\2\u0107\u0109\3\2"+
		"\2\2\u0108\u0106\3\2\2\2\u0109\u010a\7$\2\2\u010aL\3\2\2\2\u010b\u010c"+
		"\7^\2\2\u010c\u0110\7$\2\2\u010d\u010e\7^\2\2\u010e\u0110\7^\2\2\u010f"+
		"\u010b\3\2\2\2\u010f\u010d\3\2\2\2\u0110N\3\2\2\2\u0111\u0115\5Q)\2\u0112"+
		"\u0114\5S*\2\u0113\u0112\3\2\2\2\u0114\u0117\3\2\2\2\u0115\u0113\3\2\2"+
		"\2\u0115\u0116\3\2\2\2\u0116P\3\2\2\2\u0117\u0115\3\2\2\2\u0118\u0119"+
		"\t\4\2\2\u0119R\3\2\2\2\u011a\u011b\t\5\2\2\u011bT\3\2\2\2\u011c\u011e"+
		"\7\60\2\2\u011d\u011f\5G$\2\u011e\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120"+
		"\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121V\3\2\2\2\u0122\u0124\t\6\2\2"+
		"\u0123\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0126"+
		"\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128\b,\2\2\u0128X\3\2\2\2\u0129\u012a"+
		"\7\61\2\2\u012a\u012b\7\61\2\2\u012b\u012f\3\2\2\2\u012c\u012e\13\2\2"+
		"\2\u012d\u012c\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u0130\3\2\2\2\u012f\u012d"+
		"\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0134\7\17\2\2"+
		"\u0133\u0132\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136"+
		"\7\f\2\2\u0136\u0137\3\2\2\2\u0137\u0138\b-\3\2\u0138Z\3\2\2\2\u0139\u013a"+
		"\7\61\2\2\u013a\u013b\7,\2\2\u013b\u013f\3\2\2\2\u013c\u013e\13\2\2\2"+
		"\u013d\u013c\3\2\2\2\u013e\u0141\3\2\2\2\u013f\u0140\3\2\2\2\u013f\u013d"+
		"\3\2\2\2\u0140\u0142\3\2\2\2\u0141\u013f\3\2\2\2\u0142\u0143\7,\2\2\u0143"+
		"\u0144\7\61\2\2\u0144\u0145\3\2\2\2\u0145\u0146\b.\4\2\u0146\\\3\2\2\2"+
		"\u0147\u0149\13\2\2\2\u0148\u0147\3\2\2\2\u0149\u014c\3\2\2\2\u014a\u014b"+
		"\3\2\2\2\u014a\u0148\3\2\2\2\u014b\u014d\3\2\2\2\u014c\u014a\3\2\2\2\u014d"+
		"\u014e\7\61\2\2\u014e\u014f\7,\2\2\u014f\u0150\7B\2\2\u0150\u0151\3\2"+
		"\2\2\u0151\u0152\b/\5\2\u0152^\3\2\2\2\u0153\u0154\7B\2\2\u0154\u0155"+
		"\7,\2\2\u0155\u0156\7\61\2\2\u0156\u015a\3\2\2\2\u0157\u0159\13\2\2\2"+
		"\u0158\u0157\3\2\2\2\u0159\u015c\3\2\2\2\u015a\u015b\3\2\2\2\u015a\u0158"+
		"\3\2\2\2\u015b\u015d\3\2\2\2\u015c\u015a\3\2\2\2\u015d\u015e\b\60\6\2"+
		"\u015e`\3\2\2\2\23\2\u00ef\u00f3\u00f8\u00fa\u00ff\u0104\u0106\u010f\u0115"+
		"\u0120\u0125\u012f\u0133\u013f\u014a\u015a";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
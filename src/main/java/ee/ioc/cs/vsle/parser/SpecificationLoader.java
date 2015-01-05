package ee.ioc.cs.vsle.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageLexer;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser;
import ee.ioc.cs.vsle.parser.generated.SpecificationLanguageParser.MetaInterfaceContext;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.SpecParseException;


public class SpecificationLoader {
	private Map<String, AnnotatedClass> specificationByName;
	private Set<String> schemeObjectSet;
	private AntlrSpecificationSourceProvider sourceProvider;

	public SpecificationLoader(final String basePath, Set<String> schemeObjects) {
		this(new AntlrSpecificationSourceProvider() {
			@Override
			public CharStream getSource(String specificationName) {
				try {
					return new ANTLRFileStream(basePath.concat(specificationName).concat(".java"));
				} catch (IOException e) {
					throw new SpecificationNotFoundException("Unable to find specification ".concat(specificationName));
			}
			}
		}, schemeObjects);
	}

	public SpecificationLoader(AntlrSpecificationSourceProvider sourceProvider, Set<String> schemeObjects) {
		assert sourceProvider != null;

		this.sourceProvider = sourceProvider;
		this.schemeObjectSet = schemeObjects;
		specificationByName = new HashMap<String, AnnotatedClass>();
	}
	//TODO: Recursive specifications...
	public AnnotatedClass getSpecification(String specificationName) throws SpecificationNotFoundException{
		if(!specificationByName.containsKey(specificationName)){
			loadSpecification(sourceProvider.getSource(specificationName), specificationName);
		}
		
		AnnotatedClass annotatedClass = specificationByName.get(specificationName);
		return annotatedClass;
	}
	
	public AnnotatedClass loadSpecification(String specificationCode, String specificationName) {
		AnnotatedClass annotatedClass = loadSpecification(new ANTLRInputStream(specificationCode), specificationName);
		return annotatedClass;
	}
	
	protected AnnotatedClass loadSpecification(CharStream input, String specificationName) {
		SpecificationLanguageLexer lexer = new SpecificationLanguageLexer(input);
		TokenStream token = new CommonTokenStream(lexer);
		SpecificationLanguageParser parser = new SpecificationLanguageParser(token);
		parser.removeErrorListeners(); // remove ConsoleErrorListener
		parser.addErrorListener(new UnderlineListener()); // add ours
		
		SpecificationLanguageListenerImpl specificationLanguageListener = new SpecificationLanguageListenerImpl(this, specificationName);
		MetaInterfaceContext metaInterfase = parser.metaInterface();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(specificationLanguageListener, metaInterfase);
		
		AnnotatedClass annotatedClass = specificationLanguageListener.getAnnotatedClass();
		specificationByName.put(annotatedClass.getName(), annotatedClass);
		return annotatedClass;
	}
	
	public Collection<AnnotatedClass> getLoadedSpecifications(){
		return specificationByName.values();
	}
	
	public boolean isSchemeObject(String objectName) {
		return schemeObjectSet.contains(objectName);
	}
	
	public static class SpecificationNotFoundException extends SpecParseException{

		public SpecificationNotFoundException(String message) {
			super(message);
		}
		
	}
	
	public static class UnderlineListener extends BaseErrorListener {
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
			System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
//			String message = underlineError(recognizer, (Token) offendingSymbol, line, charPositionInLine);
			String errorLine = underlineError(recognizer, (Token)offendingSymbol, line, charPositionInLine);
			msg = errorLine.concat("\n").concat(msg);
			SpecParseException specParseException = new SpecParseException(msg);
			specParseException.setLine(Integer.toString(line));
			throw specParseException;
		}

		protected String underlineError(Recognizer recognizer, Token offendingToken, int line, int charPositionInLine) {
			StringBuilder sb = new StringBuilder("\n");
			CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
			String input = tokens.getTokenSource().getInputStream().toString();
			String[] lines = input.split("\n");
			String errorLine = lines[line - 1];
			sb.append(errorLine);
			sb.append("\n");
			for (int i = 0; i < charPositionInLine; i++)
				sb.append(" ");
			int start = offendingToken.getStartIndex();
			int stop = offendingToken.getStopIndex();
			if (start >= 0 && stop >= 0) {
				for (int i = start; i <= stop; i++)
					sb.append("^");
			}
			return sb.toString();
		}
		
	}
}

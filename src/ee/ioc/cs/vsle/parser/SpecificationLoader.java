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
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ee.ioc.cs.vsle.parser.SpecificationParserParser.MetaInterfaseContext;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.SpecParseException;


public class SpecificationLoader {
	private Map<String, AnnotatedClass> specificationByName;
	private String basePath;
	private Set<String> schemeObjectSet;
	
	public SpecificationLoader(String basePath, Set<String> schemeObjects) {
		this.basePath = basePath;
		this.schemeObjectSet = schemeObjects;
		specificationByName = new HashMap<>();
	}
	//TODO: Recursive specifications...
	public AnnotatedClass getSpecification(String specificationName) throws SpecificationNotFoundException{
		if(!specificationByName.containsKey(specificationName)){
			try {
				loadSpecification(new ANTLRFileStream(basePath.concat(specificationName).concat(".java")), specificationName);
			} catch (IOException e) {
				throw new SpecificationNotFoundException("Unable to find specification ".concat(specificationName));
			}
		}
		
		AnnotatedClass annotatedClass = specificationByName.get(specificationName);
		return annotatedClass;
	}
	
	public AnnotatedClass loadSpecification(String specificationCode, String specificationName) {
		AnnotatedClass annotatedClass = loadSpecification(new ANTLRInputStream(specificationCode), specificationName);
		return annotatedClass;
	}
	
	protected AnnotatedClass loadSpecification(CharStream input, String specificationName) {
		SpecificationParserLexer lexer = new SpecificationParserLexer(input);
		TokenStream token = new CommonTokenStream(lexer);
		SpecificationParserParser parser = new SpecificationParserParser(token);
		parser.removeErrorListeners(); // remove ConsoleErrorListener
		parser.addErrorListener(new UnderlineListener()); // add ours
		
		SpecificationParserListenerImpl secificationListener = new SpecificationParserListenerImpl(this, specificationName);
		MetaInterfaseContext metaInterfase = parser.metaInterfase();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(secificationListener, metaInterfase);
		
		AnnotatedClass annotatedClass = secificationListener.getAnnotatedClass();
		specificationByName.put(annotatedClass.getName(), annotatedClass);
		return annotatedClass;
	}
	
	public Collection<AnnotatedClass> getLoaddedSpecificationList(){
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
			throw new SpecParseException("\n" + msg);
		}

//		protected String underlineError(Recognizer recognizer, Token offendingToken, int line, int charPositionInLine) {
//			StringBuilder sb = new StringBuilder("\n");
//			CommonTokenStream tokens = (CommonTokenStream) recognizer.getInputStream();
//			String input = tokens.getTokenSource().getInputStream().toString();
//			String[] lines = input.split("\n");
//			String errorLine = lines[line - 1];
//			sb.append(errorLine);
//			sb.append("\n");
//			for (int i = 0; i < charPositionInLine; i++)
//				sb.append(" ");
//			int start = offendingToken.getStartIndex();
//			int stop = offendingToken.getStopIndex();
//			if (start >= 0 && stop >= 0) {
//				for (int i = start; i <= stop; i++)
//					sb.append("^");
//			}
//			return sb.toString();
//		}
		
	}
}

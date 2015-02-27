package ee.ioc.cs.vsle.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ee.ioc.cs.vsle.synthesize.ClassList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpecificationLoader {

	private static final Logger logger = LoggerFactory.getLogger(SpecificationLoader.class);

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
	
	public AnnotatedClass loadSpecification(String specificationCode) {
		return loadSpecification(specificationCode, null);
	}

	public AnnotatedClass loadSpecification(String specificationCode, String specificationName) {
		AnnotatedClass annotatedClass = loadSpecification(new ANTLRInputStream(specificationCode), specificationName);
		return annotatedClass;
	}
	
	protected AnnotatedClass loadSpecification(CharStream input, String specificationName) {
		logger.trace("Load specification '{}'", specificationName);
		if(!input.toString().contains("/*@")) {
			throw new SpecificationNotFoundException(specificationName);
		}
		SpecificationLanguageLexer lexer = new SpecificationLanguageLexer(input);
		TokenStream token = new CommonTokenStream(lexer);
		SpecificationLanguageParser parser = new SpecificationLanguageParser(token);
		parser.removeErrorListeners(); // remove ConsoleErrorListener
		SpecificationLanguageListenerImpl specificationLanguageListener = new SpecificationLanguageListenerImpl(this, specificationName);
		parser.addErrorListener(specificationLanguageListener);

		MetaInterfaceContext metaInterface = parser.metaInterface();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(specificationLanguageListener, metaInterface);
		
		AnnotatedClass annotatedClass = specificationLanguageListener.getAnnotatedClass();
		specificationByName.put(annotatedClass.getName(), annotatedClass);
		return annotatedClass;
	}
	
	public ClassList getLoadedSpecifications(){
		return new ClassList(specificationByName.values());
	}
	
	public boolean isSchemeObject(String objectName) {
		return schemeObjectSet != null && schemeObjectSet.contains(objectName);
	}
	
	public static class SpecificationNotFoundException extends SpecParseException{

		public SpecificationNotFoundException(String message) {
			super(message);
		}
		
	}

	public void reset() {
		specificationByName.clear();
	}
	
}

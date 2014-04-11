package ee.ioc.cs.vsle.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ee.ioc.cs.vsle.parser.SpecificationParserParser.MetaInterfaseContext;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;


public class SpecificationLoader {
	private Map<String, AnnotatedClass> specificationByName;
	private String basePath;
	
	public SpecificationLoader(String basePath) {
		this.basePath = basePath;
		specificationByName = new HashMap<>();
	}
	//TODO: Recursive specifications...
	public AnnotatedClass getSpecification(String specificationName) {
		if(!specificationByName.containsKey(specificationName)){
			try {
				loadSpecification(new ANTLRFileStream(basePath.concat(specificationName).concat(".java")), specificationName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		AnnotatedClass annotatedClass = specificationByName.get(specificationName);
		return annotatedClass;
	}
	
	public AnnotatedClass loadSpecification(String specificationCode) {
		AnnotatedClass annotatedClass = loadSpecification(new ANTLRInputStream(specificationCode), "this");
		return annotatedClass;
	}
	
	protected AnnotatedClass loadSpecification(CharStream input, String specificationName) {
		SpecificationParserLexer lexer = new SpecificationParserLexer(input);
		TokenStream token = new CommonTokenStream(lexer);
		SpecificationParserParser parser = new SpecificationParserParser(token);
		
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
}

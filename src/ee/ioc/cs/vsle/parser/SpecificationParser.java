package ee.ioc.cs.vsle.parser;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassList;
import ee.ioc.cs.vsle.synthesize.EquationException;
import ee.ioc.cs.vsle.synthesize.SpecParseException;
import ee.ioc.cs.vsle.util.db;

public class SpecificationParser {
	//ClassList classes = parseSpecificationImpl( refineSpec( fullSpec ), TYPE_THIS, schemeObjects, path, new LinkedHashSet<String>() );
    public static ClassList parseSpecification( String fullSpec, String mainClassName, Set<String> schemeObjects, String path )
    		throws IOException, SpecParseException, EquationException {

        long start = System.currentTimeMillis();
        
		SpecificationLoader specificationLoader = new SpecificationLoader(path);
		specificationLoader.loadSpecification(fullSpec);
        
		ClassList classes = new ClassList();
		Collection<AnnotatedClass> loaddedSpecificationList = specificationLoader.getLoaddedSpecificationList();
		classes.addAll(loaddedSpecificationList);
		
        if ( RuntimeProperties.isLogInfoEnabled() )
            db.p( "Specification parsed in: " + ( System.currentTimeMillis() - start ) + "ms." );
        
        return classes;
    }
}

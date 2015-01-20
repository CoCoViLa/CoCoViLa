package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassList;
import ee.ioc.cs.vsle.synthesize.SpecParser;

import java.util.Set;

/**
 * @author Pavel Grigorenko
 */
public class SpecParserUtil {

  public static ParsedSpecificationContext parseSpec(String spec, String workingDir, Set<String> schemeObjects) {
    String mainClassName = null;
    ClassList classList = null;
    switch (RuntimeProperties.getSpecParserKind()) {
      case REGEXP: {
        mainClassName = SpecParser.getClassName(spec);
        classList = new SpecParser(workingDir).parseSpecification( spec, mainClassName, schemeObjects );
        break;
      }
      case ANTLR: {
        SpecificationLoader specificationLoader = new SpecificationLoader(workingDir, schemeObjects);
        AnnotatedClass _this = specificationLoader.loadSpecification(spec);
        mainClassName = _this.getName();
        classList = new ClassList(specificationLoader.getLoadedSpecifications());
        break;
      }
    }
    return new ParsedSpecificationContext(spec, classList, mainClassName);
  }
}

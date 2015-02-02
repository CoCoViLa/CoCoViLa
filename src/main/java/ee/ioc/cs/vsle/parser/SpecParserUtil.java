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

  public static ParsedSpecificationContext parseFromFile(String className, String workingDir) {
    final String mainClassName = className;
    final ClassList classList;

    switch (RuntimeProperties.getSpecParserKind()) {
      case REGEXP: {
        classList = new ClassList();
        SpecParser.parseSpecClass(className, workingDir, classList);
        break;
      }
      case ANTLR: {
        SpecificationLoader specificationLoader = new SpecificationLoader(workingDir, null);
        specificationLoader.getSpecification(mainClassName);
        classList = new ClassList(specificationLoader.getLoadedSpecifications());
        break;
      }
      default:
        throw new IllegalStateException("Undefined specification language parser");
    }

    return new ParsedSpecificationContext(null, classList, mainClassName);
  }

  public static ParsedSpecificationContext parseFromString(String spec, String workingDir) {
    return parseFromString(spec, workingDir, null);
  }

  public static ParsedSpecificationContext parseFromString(String spec, String workingDir, Set<String> schemeObjects) {
    String mainClassName;
    ClassList classList;
    switch (RuntimeProperties.getSpecParserKind()) {
      case REGEXP: {
        mainClassName = SpecParser.getClassName(spec);
        classList = new SpecParser(workingDir).parseSpecification(spec, mainClassName, schemeObjects);
        break;
      }
      case ANTLR: {
        SpecificationLoader specificationLoader = new SpecificationLoader(workingDir, schemeObjects);
        AnnotatedClass _this = specificationLoader.loadSpecification(spec);
        mainClassName = _this.getName();
        classList = new ClassList(specificationLoader.getLoadedSpecifications());
        break;
      }
      default:
        throw new IllegalStateException("Undefined specification language parser");
    }
    return new ParsedSpecificationContext(spec, classList, mainClassName);
  }
}

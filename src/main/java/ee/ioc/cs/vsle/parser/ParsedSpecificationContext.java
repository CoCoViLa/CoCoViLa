package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.ClassList;

/**
 * @author Pavel Grigorenko
 */
public class ParsedSpecificationContext {
  public final String fullRootSpec;
  public final ClassList classList;
  public final String mainClassName;

  public ParsedSpecificationContext(String fullRootSpec, ClassList classList, String mainClassName) {
    this.fullRootSpec = fullRootSpec;
    this.classList = classList;
    this.mainClassName = mainClassName;
  }
}

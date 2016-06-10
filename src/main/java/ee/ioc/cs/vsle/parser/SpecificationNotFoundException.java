package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.SpecParseException;

public class SpecificationNotFoundException  extends SpecParseException {

  public SpecificationNotFoundException(String message) {
    super(message);
  }

}
